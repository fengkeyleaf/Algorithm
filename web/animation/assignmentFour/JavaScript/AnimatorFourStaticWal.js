"use strict"

/*
 * AnimatorFourStaticWal.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 Basic operations on 12/6/2021$
 */

import ParticleSystem from "../../../myLibraries/animation/ParticleSystem.js";
import Animator from "../../../myLibraries/animation/Animator.js";
import Dynamics from "../../../myLibraries/animation/Dynamics.js";
import AnimatorFour from "./AnimatorFour.js";
import MyMath from "../../../myLibraries/lang/MyMath.js";
import Cushion from "../../assignmentTwo/JavaScript/Cushion.js";
import { OrbitControls } from "../../../myLibraries/externals/jsm/controls/OrbitControls.js";

/**
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */


export default class AnimatorFourStaticWal extends AnimatorFour {
    #curves = null;

    constructor( paras ) {
        super( paras );
        Animator.animator = this;

        this.e = 0.8;

        this.objects = null;
        this.ballsCollided = [];
    }

    // called by window.onload
    // and change parameters for the particle system
    static run() {
        new AnimatorFourStaticWal( {
            meanV: 20,
            maxV: 10,
            radiusCone: 0.3,
            MAX_PARTICLES: 500,
            generateRate: 10,
            meanLifetime: 1200,
            maxLifetime: 80,
            psi: 25 // psi, emission angle, side Angle
        } ).init();
        Animator.animator.#initCushions();
        Animator.render();

        let file = "textures/spark1.png";
        // file = "textures/disc.png";
        Animator.animator.texture = new THREE.TextureLoader().load( file, function () {
            Animator.animator.initParticleSystem();
            console.log( Animator.animator.objects );
            console.log( Animator.animator.collidables );
            Animator.animator.initializingDate = new Date().getTime() / 1000;
            Animator.animator.startingDate = Animator.animator.initializingDate;
            AnimatorFourStaticWal.animate();
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
        }  );
        this.particleSystem.setup();

        this.objects = this.particleSystem.particles;

        this.collidables = [ this.cushion ];
        // this.collidables = [];
    }

    #initCushions() {
        const width = 30;
        const height = 20;

        let plane = new THREE.PlaneGeometry( width, height );
        const materialPlane = new THREE.MeshBasicMaterial( { color: 0x009966, side: THREE.DoubleSide } );

        this.meshPlane = new THREE.Mesh( plane, materialPlane );
        this.meshPlane.position.set( 10, -10, 0 );
        this.meshPlane.position.set( 4, 0, 0 );
        this.meshPlane.rotateOnAxis( Dynamics.yAxis, MyMath.radians( 90 ) );

        this.scene.add( this.meshPlane );

        let mass = 10; // kg
        // cushions
        this.cushion = new Cushion( this.meshPlane, mass, Dynamics.xAxis.clone().negate(), "cushion" );
    }

    static animate() {
        const animator = Animator.animator;
        let current = new Date().getTime() / 1000;
        let dt = current - animator.initializingDate;

        console.log(dt);
        if ( current - Animator.animator.startingDate >= 5 ) {
            console.log( "stop here" );
            return;
        }

        let center = Dynamics.origin.clone().add( new THREE.Vector3( 0, 0, 0 ) );
        animator.particleSystem.generate( center, Dynamics.xAxis.clone().sub( center ) );
        dt = animator.particleSystem.update( current, dt );

        animator.initializingDate += dt;
        requestAnimationFrame( AnimatorFourStaticWal.animate );
        Animator.render();
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
        const S = 15; // factor to control the size of showing area

        this.camera = new THREE.OrthographicCamera( -S * K, S * K, S, -S, 1, 1000 );
        this.camera.position.set( -200, 100, 500 );
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
}