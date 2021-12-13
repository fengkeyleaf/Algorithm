"use strict"

/*
 * Particle.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import MyMath from "../lang/MyMath.js";
import Dynamics from "./Dynamics.js";
import RigidSphere from "./RigidSphere.js";

/**
 * data structure of Particle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Particle extends RigidSphere {
    static IDStatic = 0;

    /**
     * @param {TextureLoader} texture
     * @param {Number} color
     * @param {Scene} scene
     */

    constructor( texture, color, scene ) {
        super( null, 0.01, 0.01, "sprite" + Particle.IDStatic++ );

        this.material = new THREE.SpriteMaterial( {
            color: color,
            map: texture,
            transparent: true
        } );
        this.mesh = new THREE.Sprite( this.material );
        scene.add( this.mesh );

        this.alive = true;
    }

    updateOpacity() {
        this.material.opacity = this.alive ? this.remeanedLifeTime / this.lifeTime : 0;
    }

    updateLifetime() {
        this.alive = --this.remeanedLifeTime > 0;
    }

    /**
     * initialize a particle
     *
     * @param {Vector3} center
     * @param {Vector3} direction
     * @param {Number} radius
     * @param {Number} speed
     * @param {Number} psi, emission angle, side Angle
     * @param {Vector3} scale
     * @param {Number} lifeTime
     */

    init( center, direction,
          radius, speed,
          psi, scale, lifeTime ) {
        const k = radius / 0.5;
        // get position
        let x = ( Math.random() - 0.5 ) * k;
        let y = ( Math.random() - 0.5 ) * k;
        let z = ( Math.random() - 0.5 ) * k;
        let pos = new THREE.Vector3( x, y, z ).add( center );
        // pos= new THREE.Vector3();
        // console.log( pos, pos.clone().sub( center ).length() );

        this.reset();
        // get velocity
        let unitDirection = Particle.randomUnitVector3InCone( direction, psi );
        // hit it with a rotation matrix to get it aligned with the direction of the cone
        let rotation = new THREE.Matrix4().lookAt( center, direction.clone().add( center ), Dynamics.zAxis.clone() );
        let directionV = unitDirection.clone().transformDirection( rotation );
        // get Momentum for this particle
        let m = speed * this.m;
        this.M = directionV.clone().negate().multiplyScalar( m );

        // set position and scale
        this.mesh.scale.set( scale.x, scale.y, 1 ); // set y scale only
        this.mesh.position.set( pos.x, pos.y, pos.z );

        // set life time
        this.lifeTime = lifeTime;
        this.remeanedLifeTime = lifeTime;
        this.alive = true;
    }

    /**
     * get random Unit Vector3 bounded by the Cone.
     * This is defined by the direction of the cone and its side angle, a.k.a., emission angle
     *
     * Reference resource:
     * Generating uniform unit random vectors in Rn, UMONS, Belgium, Andersen Ang
     *
     * @param {Vector3} direction  direction of the cone
     * @param {Number} sideAngle  in degrees
     */

    static randomUnitVector3InCone( direction, sideAngle ) {
        let theta = Math.random() * 360;

        // side angle of the cone is sideAngle degrees
        let cosPhi = Math.cos( MyMath.radians( sideAngle ) );
        let z = MyMath.randomInRange( cosPhi, 1 );

        console.assert( MyMath.doubleCompare( 1 - z * z, 0 ) >= 0, 1 - z * z );
        let sinPhi = Math.sqrt( 1 - z * z );
        let x = sinPhi * Math.cos( MyMath.radians( theta ) );
        let y = sinPhi * Math.sin( MyMath.radians( theta ) );

        let directionV = new THREE.Vector3( x, y, z );
        // console.log( MyMath.degrees( direction.angleTo( directionV ) ), direction.length() );
        return directionV;
    }
}