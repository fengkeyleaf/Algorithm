"use strict"

/*
 * Pocket.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 Basic operations on 12/5/2021$
 */

import CollidingObject from "../../../myLibraries/animation/CollidingObject.js";
import MyMath from "../../../myLibraries/lang/MyMath.js";
import Animator from "../../../myLibraries/animation/Animator.js";

/**
 * Data structure of Pocket for Billiards game
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Pocket extends CollidingObject {
    static typeID = CollidingObject.Types.Pocket;

    /**
     * @param {Mesh} mesh
     * @param {String} name
     * @param {Number} radius
     */

    constructor( mesh, name, radius ) {
        super( mesh, 10, name, Pocket.typeID );
        this.radius = radius;
    }

    /**
     * is the ball falling into this pocket
     *
     * @param {Ball} ball
     */

    isInPocket( ball ) {
        let posPocket = new THREE.Vector3( this.mesh.position.x, 0, this.mesh.position.z );
        let posBall = new THREE.Vector3( ball.mesh.position.x, 0, ball.mesh.position.z );
        let distance = posPocket.clone().sub( posBall ).length();
        console.assert( this.radius >= ball.radius );
        return MyMath.doubleCompare( distance, this.radius - ball.radius + ball.radius / 2 ) <= 0;
    }

    /**
     *
     * @param {[Ball]} balls
     * @param {[Pocket]} pockets
     * @param {Vector3} pos
     */

    static isRemoved( balls, pockets, pos ) {
        for ( const ball of balls ) {
            pockets.forEach( p => {
                if ( p.isInPocket( ball ) ) {
                    ball.removeFromTable( pos );
                    Animator.animator.outOfTable.add( new THREE.Vector3( 0, 0, 16 + ball.radius ) );
                    return;
                }
            } );
        }
    }
}