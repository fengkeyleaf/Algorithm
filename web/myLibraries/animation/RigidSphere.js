"use strict"

/*
 * RigidSphere.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 Basic operations on 12/9/2021$
 */

import CollidingObject from "./CollidingObject.js";
import MyMath from "../lang/MyMath.js";

/**
 * data structure of a RigidSphere
 * Common class for Ball, Particle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class RigidSphere extends CollidingObject {
    static typeID = CollidingObject.Types.RigidSphere;

    /**
     * @param {Mesh} mesh
     * @param {Number} mass kg
     * @param {Number} radius
     * @param {String} name
     *
     */

    constructor( mesh, mass, radius, name ) {
        super( mesh, mass, name, RigidSphere.typeID );
        this.radius = radius;
    }

    /**
     * @param {CollidingObject} b2 collided
     * @param {Number} e
     */

    collidingImpulse( b2, e ) {
        console.assert( MyMath.doubleCompare( e , 0 ) >= 0 && MyMath.doubleCompare( e, 1 ) <= 0 );

        // TODO: 12/4/2021 this implementation is correct?
        // let v1 = this.v.clone().dot( this.lineOfAction( b2 ) );
        // let v2 = b2.v.clone().dot( b2.lineOfAction( this ) );
        let v1 = b2.lineOfAction( this ).dot( this.v );
        let v2 = b2.lineOfAction( this ).dot( b2.v );
        console.log( v1, v2, v2 - v1 );
        let j = ( v2 - v1 ) * ( this.m * b2.m / ( this.m + b2.m ) ) * ( 1 + e );
        console.assert( j < 1000, ( v2 - v1 ), ( this.m * b2.m / ( this.m + b2.m ) ) * ( 1 + e ) )
        return Math.abs( j );
    }


    /**
     * @param {{}} object
     */

    isColliding( object ) {
        if ( object.typeID === CollidingObject.Types.RigidSphere ) {
            let distance = this.mesh.position.clone().sub( object.mesh.position ).length();
            return MyMath.doubleCompare( this.radius + object.radius, distance ) > 0;
        }

        console.assert( object.typeID === CollidingObject.Types.Cushion );
        let p0 = object.mesh.position.clone();

        // TODO: 12/4/2021 this implementation is correct?
        let diff = this.mesh.position.clone().sub( p0 );
        let d = object.n.clone().dot( diff );
        return MyMath.doubleCompare( d, this.radius ) < 0;
    }

    /**
     * @param {Ball} object
     * @param {Cushion} object
     */

    lineOfAction( object ) {
        if ( object.typeID === CollidingObject.Types.RigidSphere )
            return this.mesh.position.clone().sub( object.mesh.position ).normalize();

        console.assert( object.typeID === CollidingObject.Types.Cushion );
        return object.n.clone();
    }
}