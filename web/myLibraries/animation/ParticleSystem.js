"use strict"

/*
 * ParticleSystem.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Particle from "./Particle.js";
import Dynamics from "./Dynamics.js";

/**
 * data structure of ParticleSystem
 *
 * Reference material of setting up particle system:
 * https://soulwire.github.io/sketch.js/examples/particles.html
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class ParticleSystem {
    static color = 0xff00ff;
    static fourth = new THREE.Vector3( 0.4, 0.4, 1 );

    /**
     * @param {Number} MAX_PARTICLES
     * @param {Number} generateRate
     * @param {TextureLoader} texture
     * @param {Scene} scene
     * @param {Number} meanSpeed
     * @param {Number} maxSpeed
     * @param {Number} radius bounded sphere for the position where particles are generated
     * @param {Number} meanLifetime
     * @param {Number} maxLifetime
     * @param {Number} psi emission angle, side Angle
     */

    constructor( paras ) {
        this.MAX_PARTICLES = paras.MAX_PARTICLES;
        this.texture = paras.texture;
        this.scene = paras.scene;
        this.meanV = paras.meanV;
        this.maxV =paras.maxV;
        this.radius = paras.radius;
        this.generateRate = paras.generateRate;

        this.meanLifetime = paras.meanLifetime;
        this.maxLifetime = paras.maxLifetime;
        this.psi = paras.psi; // emission angle, side Angle
        this.isAppliedGravity = paras.isAppliedGravity ? true : false;

        this.particles = [];
        this.pool = [];
    }

    /**
     * generate particles at the rate
     *
     * @param {Vector3} center
     * @param {Vector3} normal
     */

    generate( center, normal ) {

        for ( let i = 0; i < this.generateRate; i++ ) {
            this.spawn( center, normal );
        }
    }

    /**
     * initialize some particles at the origin
     */

    setup() {
        this.generate( Dynamics.origin, Dynamics.origin );
    }

    /**
     * set up particles either from the pool or a new one
     *
     * @param {Vector3} center
     * @param {Vector3} normal
     */

    spawn( center, normal,  ) {

        if ( this.particles.length >= this.MAX_PARTICLES )
            this.pool.push( this.particles.shift() );

        let lifeTime = this.meanLifetime + Math.random() * this.maxLifetime;
        let particle = this.pool.length ? this.pool.shift() : new Particle( this.texture, ParticleSystem.color, this.scene );
        particle.init( center, normal, this.radius, this.getSpeed(), this.psi, ParticleSystem.fourth, lifeTime );

        this.particles.push( particle );
    }

    /**
     * update particles
     *
     * @param {Number} current, seconds
     * @param {Number} dt, seconds
     */

    update( current, dt ) {
        // remove dead particles
        for ( let i = this.particles.length - 1; i >= 0; i-- ) {
            let particle = this.particles[ i ];
            if ( !particle.alive ) {
                let e = this.particles.splice( i, 1 );
                this.pool.push( e[ 0 ] );
            }
        }

        // add forces and update position
        for ( let i = this.particles.length - 1; i >= 0; i-- ) {
            let particle = this.particles[ i ];
            console.assert( particle.alive );

            if ( this.isAppliedGravity ) particle.addGravityForce( Dynamics.G );
            particle.updatePos( dt );
        }

        // detect collision
        dt = Dynamics.detectCollision( current, dt );

        // update Momentum and velocity
        for ( let i = this.particles.length - 1; i >= 0; i-- ) {
            let particle = this.particles[ i ];
            console.assert( particle.alive );

            particle.updateM( dt );
            particle.impulses = [];
            particle.fs.set( 0, 0, 0 );

            particle.calV();

            particle.updateLifetime();
            particle.updateOpacity();
        }

        return dt;
    }

    /**
     * get random speed in the range of [ meanSpeed, maxSpeed )
     */

    getSpeed() {
        return this.meanV + Math.random() * this.maxV;
    }
}