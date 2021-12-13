"use strict"

/*
 * AnimatorFourGravity.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 Basic operations on 12/6/2021$
 */

import { OrbitControls } from "../../../myLibraries/externals/jsm/controls/OrbitControls.js";
import ParticleSystem from "../../../myLibraries/animation/ParticleSystem.js";
import AnimatorOne from "../../assignmentOne/JavaScript/AnimatorOne.js";
import Animator from "../../../myLibraries/animation/Animator.js";
import Dynamics from "../../../myLibraries/animation/Dynamics.js";

/**
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class AnimatorFourGravity extends AnimatorOne {

    constructor( paras ) {
        super();
        Animator.animator = this;

        this.texture;
        this.meanV = paras.meanV ? paras.meanV : 0.01;
        this.maxV = paras.maxV ? paras.maxV : 0.1;
        this.radiusCone = paras.radiusCone ? paras.radiusCone : 0.5;
        this.MAX_PARTICLES = paras.MAX_PARTICLES ? paras.MAX_PARTICLES : 300;
        this.generateRate = paras.generateRate ? paras.generateRate : 90;
        this.meanLifetime = paras.meanLifetime ? paras.meanLifetime : 20;
        this.maxLifetime = paras.maxLifetime ? paras.maxLifetime : 60;
        this.psi = paras.psi ? paras.psi : 25; // degrees

        this.particleSystem = null;
    }

    // called by window.onload
    // and change parameters for the particle system
    static run() {
        new AnimatorFourGravity( {
            meanV: 20,
            maxV: 20,
            radiusCone: 0.3,
            MAX_PARTICLES: 230,
            generateRate: 25,
            meanLifetime: 800,
            maxLifetime: 60,
            psi: 25 // psi, emission angle, side Angle
        } ).init();
        Animator.render();

        let file = "textures/spark1.png";
        // file = "textures/disc.png";
        Animator.animator.texture = new THREE.TextureLoader().load( file, function () {
            Animator.animator.initParticleSystem();
            Animator.animator.initializingDate = new Date().getTime() / 1000;
            Animator.animator.startingDate = Animator.animator.initializingDate;
            AnimatorFourGravity.animate();
        } );
    }

    initParticleSystem() {
        this.particleSystem = new ParticleSystem( {
            MAX_PARTICLES: this.MAX_PARTICLES,
            generateRate: this.generateRate,
            texture: this.texture,
            scene: this.scene,
            meanV: this.meanV,
            maxV: this.maxV,
            radius: this.radiusCone,
            meanLifetime: this.meanLifetime,
            maxLifetime: this.maxLifetime,
            psi: this.psi,
            isAppliedGravity: true
        } );
        console.log( this.particleSystem );
        this.particleSystem.setup();

        this.objects = this.particleSystem.particles;

        this.collidables = [];
        this.ballsCollided = [];
    }

    init() {
        // set up our scene
        this.scene = new THREE.Scene();
        this.scene.background = new THREE.Color( 0xeeeeee );

        let point = new THREE.PointLight( 0xffffff );
        point.position.set( 400, 200, 300 );
        this.scene.add( point );

        // setup ambient light
        let ambient = new THREE.AmbientLight( 0x444444 );
        this.scene.add( ambient );

        // set up the canvas covering the whole DOM
        const WIDTH = window.innerWidth;
        const HEIGHT = window.innerHeight;
        const K = WIDTH / HEIGHT; // ratio of window
        const S = 24; // factor to control the size of showing area

        this.camera = new THREE.OrthographicCamera( -S * K, S * K, S, -S, 1, 1000 );
        this.camera.position.set( 200, 100, 500 );
        this.camera.lookAt( this.scene.position );

        // set up axis helpers
        let axisHelper = new THREE.AxesHelper( 250 );
        this.scene.add( axisHelper );

        this.renderer = new THREE.WebGLRenderer();
        this.renderer.setSize( WIDTH, HEIGHT );
        this.renderer.setClearColor( 0xb9d3ff, 1 );
        document.body.appendChild( this.renderer.domElement );

        this.controls = new OrbitControls( this.camera, this.renderer.domElement );
        this.controls.addEventListener( 'change', Animator.render );
    }

    static animate() {
        const animator = Animator.animator;
        let current = new Date().getTime() / 1000;
        let dt = current - animator.initializingDate;

        console.log(dt);
        if ( current - Animator.animator.startingDate >= 10 ) {
            console.log( "stop here" );
            return;
        }

        animator.particleSystem.generate( Dynamics.origin.clone(), Dynamics.yAxis.clone() );
        animator.particleSystem.update( current, dt );

        animator.initializingDate += dt;
        requestAnimationFrame( AnimatorFourGravity.animate );
        Animator.render();
    }
}