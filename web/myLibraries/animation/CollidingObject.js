"use strict"

/*
 * CollidingObject.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 10/11/2021$
 */

import Dynamics from "./Dynamics.js";
import MyMath from "../lang/MyMath.js";

/**
 * This class has the ability to move and collide
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class CollidingObject {
    static IDStatic = 0;
    static Types = {
        RigidSphere: 0,
        Cushion: 1,
    }

    /**
     * @param {Mesh} mesh
     * @param {Number} mass kg
     * @param {String} name
     * @param {Number} typeID
     */

    constructor( mesh, mass, name, typeID ) {
        this.mesh = mesh; // shape
        this.m = mass; // kg

        this.fs = new THREE.Vector3( 0, 0, 0 ); // forces, kg * m / s ^ 2
        this.v = new THREE.Vector3( 0, 0, 0 ); // velocity, m / s
        this.M = new THREE.Vector3( 0, 0, 0 ); // Momentum, kg * m / s
        this.impulses = [];

        this.typeID = typeID;
        this.isStatic = false;
        this.name = name;
        this.ID = CollidingObject.IDStatic++;
    }

    printV() {
        console.log( this.name + " v: ", this.v );
    }

    printM() {
        console.log( this.name + " M: ", this.M );
    }

    printImpulses() {
        console.log( this.name + " impulses: ", this.impulses );
    }

    printPos() {
        console.log( this.name + " pos: ", this.mesh.position );
    }

    reset() {
        this.fs = new THREE.Vector3( 0, 0, 0 ); // forces, kg * m / s ^ 2
        this.v = new THREE.Vector3( 0, 0, 0 ); // velocity, m / s
        this.M = new THREE.Vector3( 0, 0, 0 ); // Momentum, kg * m / s
        this.impulses = [];
    }

    /**
     * update position ( integrate velocity )
     *
     * @param {Number} dt
     */

    updatePos( dt ) {
        if ( this.isStatic ) return;
        // s( t + Dt ) = s( t ) + v( t ) * Dt
        dt = dt === 0 ? 1 : dt;
        this.mesh.position.add( this.v.clone().multiplyScalar( dt ) );
        // this.printPos();
    }

    /**
     * update Momentum ( integrate force / acceleration )
     *
     * @param {Number} dt
     */

    updateM( dt ) {
        if ( this.isStatic ) return;
        // debugger
        // M( t + Dt ) = M( t ) + F( t ) * Dt + impulse( impulse_init + impulse_coll + impulse_fric )
        // console.log( this.name + " f", f );
        dt = dt === 0 ? 1 : dt;
        console.assert( dt, dt );
        let F = this.fs.clone().multiplyScalar( dt );
        // this.M.add( f );
        // this.impulses.forEach( i => this.M.add( i ) );
        // TODO: 12/6/2021 precision issue will cause objects more slightly downwards
        //  when falling from top to a table, and there is only gravity force applied
        console.log( this.name + " F", F );
        console.log( this.impulses );
        this.impulses.forEach( i => F.add( i ) );
        console.log( this.name + " F", F );
        // console.log( this.name + " ims", this.impulses );
        this.M.add( F );
        // console.assert( this.M.y === 0, this.M.y );
        this.printM();
    }

    /**
     * Step 3
     * Calculate velocities
     */

    calV() {
        if ( this.isStatic ) return;
        // v(t +Dt) = M( t + Dt ) / m
        let M = this.M.clone().divideScalar( this.m );
        // console.assert( M.y === 0, M.y );
        this.v.set( M.x, M.y, M.z );
        this.printV();
    }

    /**
     * @param {Number} x
     * @param {Number} y
     * @param {Number} z
     */

    setPosition( x, y, z ) {
        this.mesh.position.set( x, y, z );
    }

    /**
     * @param {Number} j
     * @param {CollidingObject} ball
     */

    addCollidingImpulse( j, ball ) {
        if ( this.isStatic ) return;

        let impulse = this.lineOfAction( ball ).multiplyScalar( j );
        console.log( this.lineOfAction( ball ), j )
        this.impulses.push( impulse );
    }

    /**
     * @param {Number} us
     * @param {Number} g
     */

    addSlidingFric( us, g ) {
        if ( this.isStatic ) return;

        let slidingFric = us * this.m * g;
        let sFricVec = this.getVelocityDirection().multiplyScalar( slidingFric ).negate();
        this.impulses.push( sFricVec );
    }

    /**
     * @param {Number} g
     */

    addGravityForce( g ) {
        if ( this.isStatic ) return;

        let gravity = this.m * g;
        let gravityVec = Dynamics.yAxis.clone().negate().multiplyScalar( gravity );
        this.impulses.push( gravityVec );
    }

    getVelocityDirection() {
        return this.v.clone().normalize();
    }

    isStationary() {
        if ( MyMath.isEqualZero( this.v.length() ) )
            this.isStatic = true;
    }

    lineOfAction( object ) {
        console.assert( false );
        return null;
    }

    isColliding( object ) {
        console.assert( false );
        return false;
    }
}