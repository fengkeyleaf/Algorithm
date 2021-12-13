"use strict"

/*
 * AnimatorFour.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 Basic operations on 12/5/2021$
 */

import { OrbitControls } from "../../../myLibraries/externals/jsm/controls/OrbitControls.js";
import ParticleSystem from "../../../myLibraries/animation/ParticleSystem.js";
import KeyFraming from "../../../myLibraries/animation/KeyFraming.js";
import AnimatorOne from "../../assignmentOne/JavaScript/AnimatorOne.js";
import Curves from "./Curves.js";
import Animator from "../../../myLibraries/animation/Animator.js";

/**
 * Assignment #4a - particle system
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class AnimatorFour extends AnimatorOne {
    #curves = null;

    constructor( paras ) {
        super();
        Animator.animator = this;

        this.texture;
        this.meanV = paras.meanV ? paras.meanV : 0.01;
        this.maxV = paras.maxV ? paras.maxV : 0.1;
        this.radiusCone = paras.radiusCone ? paras.radiusCone : 0.5;
        this.keyframeRate = paras.keyframeRate ? paras.keyframeRate : 100;
        this.MAX_PARTICLES = paras.MAX_PARTICLES ? paras.MAX_PARTICLES : 300;
        this.generateRate = paras.generateRate ? paras.generateRate : 90;
        this.meanLifetime = paras.meanLifetime ? paras.meanLifetime : 20;
        this.maxLifetime = paras.maxLifetime ? paras.maxLifetime : 60;
        this.psi = paras.psi ? paras.psi : 25; // degrees

        this.particleSystem = null;
        this.lastPos = null;

        this.#curves = new Curves( this );
    }

    // called by window.onload
    // and change parameters for the particle system
    static run() {
        new AnimatorFour( {
            meanV: 0.01,
            maxV: 0.1,
            radiusCone: 0.5,
            keyframeRate: 100,
            MAX_PARTICLES: 300,
            generateRate: 90,
            meanLifetime: 20,
            maxLifetime: 60,
            psi: 25 // psi, emission angle, side Angle
        } ).init();
        Animator.animator.initKeyFrames();
        Animator.render();

        let file = "textures/spark1.png";
        // file = "textures/disc.png";
        Animator.animator.texture = new THREE.TextureLoader().load( file, function () {
            Animator.animator.initParticleSystem();
            Animator.animator.initializingDate = new Date();
            AnimatorFour.animate();
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
            psi: this.psi
        } );
        this.particleSystem.setup();

        this.objects = this.particleSystem.particles;
        this.objects = [];

        this.collidables = [];
        this.ballsCollided = [];
    }

    initKeyFrames() {
        this.allKeyframes = [];
        this.#curves.init();
        this.currentKeyframe = this.allKeyframes[ this.startIndex ];
        this.nextKeyframe =this.allKeyframes[ this.endIndex ];

        this.preTime = this.currentKeyframe.time;
        this.nextTime = this.nextKeyframe.time;
    }

    printInfo() {}

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
        const S = 30; // factor to control the size of showing area

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
        let current = new Date() - animator.initializingDate;

        if ( current >= animator.nextTime ) {
            if ( animator.endIndex >= animator.allKeyframes.length - 1 ) {
                animator.particleSystem.generate( animator.allKeyframes.getLast().position, animator.lastPos.sub( animator.allKeyframes.getLast().position ).clone() );
                animator.particleSystem.update( current / 1000, current / 1000 );
                console.log( "stop here" );
                return;
            } else {
                animator.update();
            }
        }

        let u = KeyFraming.mapTtoU( current, animator.preTime, animator.nextTime );
        let pos = KeyFraming.LinearInterpolation( u, animator.currentKeyframe.position, animator.nextKeyframe.position );

        if ( animator.lastPos != null ) {
            animator.particleSystem.generate( pos, animator.lastPos.sub( pos ).clone() );
            animator.particleSystem.update( current / 1000, current / 1000 );
            Animator.render();
        }

        animator.lastPos = pos;
        requestAnimationFrame( AnimatorFour.animate );
    }
}