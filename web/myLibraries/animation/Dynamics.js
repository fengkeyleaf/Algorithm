"use strict"

/*
 * Dynamics.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 isColliding(), binarySearchCollision on 10/11/2021$
 */

import MyMath from "../lang/MyMath.js";
import Animator from "./Animator.js";

`/**
 * This class consists exclusively of static methods
 * that related to Dynamics
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */`

export default class Dynamics {
    static G = 9.80665; // N/kg

    static origin = new THREE.Vector3( 0, 0, 0 );
    static xAxis = new THREE.Vector3( 1, 0, 0 );
    static yAxis = new THREE.Vector3( 0, 1, 0 );
    static zAxis = new THREE.Vector3( 0, 0, 1 );

    /**
     * detect collision and trackback to the point where the first collision happens
     *
     * @param {Number} current
     * @param {Number} dt
     */

    static detectCollision( current, dt ) {
        let animator = Animator.animator;
        // console.log( "detect:" + dt )

        // Perform collision detection / response
        // Test all pairs of objects, if collision detected,
        console.assert( !animator.objects.isEmpty() );
        // if ( false && Dynamics.isColliding( animator ) ) {
        // if ( Dynamics.isColliding( animator, true ) ) {
        if ( Dynamics.__isColliding( animator.objects, animator.collidables, animator.ballsCollided, true ) ) {
            // debugger
            console.log( "\ncoll" );
            // Binary search to find the point of collision
            // let timeOfCollision = Dynamics.binarySearchCollision( animator, animator.initializingDate, current, false );
            let timeOfCollision = Dynamics.__binarySearchCollision( animator.objects, animator.collidables, animator.ballsCollided, animator.initializingDate, current, false );
            console.assert( timeOfCollision, timeOfCollision );
            // backup object to p oint of collision. t'
            // Dt = t' - t
            dt = timeOfCollision - animator.initializingDate;

            // Calculate Forces with newer Dt
            // update position with newer Dt
            console.assert( MyMath.isPositive( dt ) || MyMath.isEqualZero( dt ) );
            console.assert( !animator.ballsCollided.isEmpty(), animator.ballsCollided );
            // Calculate impulse_coll ( realized as change to momentum )
            animator.ballsCollided.forEach( pairs => {
                let ball1 = pairs[ 0 ];
                let ball2 = pairs[ 1 ];
                let j = ball1.collidingImpulse( ball2, animator.e );
                console.log( "j", j );
                ball1.addCollidingImpulse( j, ball2 );
                ball2.addCollidingImpulse( j, ball1 );
                console.log( ball1.name + " co_im", ball1.impulses );
                console.log( ball2.name + " co_im", ball2.impulses );
            } );

            console.log( dt );
        }

        console.log( "\n" );
        return dt;
    }

    /**
     * @param {[[CollidingObject]]} ballsCollided
     * @param {[CollidingObject]} pair
     */

    static #push( ballsCollided, pair ) {
        for ( let i = 0; i < ballsCollided.length; i++ ) {
            let p = ballsCollided[ i ];
            if ( ( p[ 0 ] === pair[ 0 ] && p[ 1 ] === pair[ 1 ] || p[ 1 ] === pair[ 0 ] && p[ 0 ] === pair[ 1 ] ) )
                return;
        }

        ballsCollided.push( pair );
    }

    /**
     * @param {[CollidingObject]} checks
     * @param {[CollidingObject]} collidables
     * @param {[[CollidingObject]]} ballsCollided
     * @param {Boolean} isUpdate    true, add collied objects into an array
     */

    static __isColliding( checks, collidables,
                          ballsCollided, isUpdate ) {
        if ( isUpdate ) ballsCollided.clear();

        let res = false;
        console.assert( !checks.isEmpty() );
        // brute force to find collision
        for ( let i = 0; i < checks.length; i++ ) {
            let ball1 = checks[ i ];
            if ( ball1.isStatic ) continue;

            for ( let j = 0; j < collidables.length; j++ ) {
                let ball2 = collidables[ j ];
                if ( ball1 === ball2 ) continue;

                if ( ball1.isColliding( ball2 ) ) {
                    if ( isUpdate ) Dynamics.#push( ballsCollided, [ ball1, ball2 ] );
                    res = true;
                }
            }
        }

        return res;
    }

    static tolerance = 0.0001;

    /**
     * @param {[CollidingObject]} objects
     * @param {Number} dt
     * @param {Boolean} isLeft
     */

    static #backtrack( objects, dt, isLeft ) {
        objects.forEach( b => {
            if ( !isLeft ) b.v.negate();
            b.updatePos( dt );
            if ( !isLeft ) b.v.negate();
        } );
    }

    /**
     * @param {[CollidingObject]} checks
     * @param {[CollidingObject]} collidables
     * @param {[[CollidingObject]]} ballsCollided
     * @param {Number} t1
     * @param {Number} t2
     * @param {Boolean} isLeft
     */

    static __binarySearchCollision( checks, collidables, ballsCollided
        , t1, t2, isLeft ) {
        // No collision at time t, Penetration at time t + Dt
        // Test point at time t’ between t and t + Dt iteratively.
        // t' = ( t + Dt ) / 2
        let tMid = ( t2 - t1 ) / 2 + t1;
        console.assert( tMid, tMid, t1, t2 );
        // Dt = t' - t
        let dt = isLeft ? tMid - t1 : ( t2 - t1 ) - ( tMid - t1 );
        // update all objects' position with Dt
        Dynamics.#backtrack( checks, dt, isLeft );
        Dynamics.#backtrack( collidables, dt, isLeft );

        let isColliding = Dynamics.__isColliding( checks, collidables, ballsCollided, false );
        // Iterate until a predefined tolerance(tol) is achieved
        if ( MyMath.doubleCompare( t2 - t1, Dynamics.tolerance ) <= 0 )
            return tMid;

        // Test all pairs of objects, If No Collision, test at time between t’ and t + Dt
        if ( !isColliding ) return Dynamics.__binarySearchCollision( checks, collidables, ballsCollided, tMid, t2, true );
        // Else test at time between t and t’
        else return Dynamics.__binarySearchCollision( checks, collidables, ballsCollided, t1, tMid, false );
    }
}