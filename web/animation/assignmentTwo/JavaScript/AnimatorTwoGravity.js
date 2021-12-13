"use strict"

/*
 * AnimatorTwoGravity.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 Basic operations on 12/6/2021$
 */

import Animator from "../../../myLibraries/animation/Animator.js";
import Ball from "./Ball.js";
import Cushion from "./Cushion.js";
import Dynamics from "../../../myLibraries/animation/Dynamics.js";
import MyMath from "../../../myLibraries/lang/MyMath.js";
import { OrbitControls } from "../../../myLibraries/externals/jsm/controls/OrbitControls.js";

/**
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class AnimatorTwoGravity extends Animator {

    constructor() {
        super();

        this.us = 0.01;
        // this.us = 0.38 // sliding fraction, Dry wood on wood
        // this.us = 0.78 // sliding fraction, Dry steel on steel
        // this.e = 0.2; // 0 <= e <= 1
        this.e = 0.8; // 0 <= e <= 1
        // this.impulseStriking = new THREE.Vector3( -400, 0, 20 ); // kg * m / s ^ 2
        this.impulseStriking = new THREE.Vector3( 50, 0, 0 ); // kg * m / s ^ 2
        // this.impulseStriking = new THREE.Vector3( -30, -40, 0 ); // kg * m / s ^ 2
        this.isInit = true;
        this.ballsCollided = null;

        this.outOfTable = new THREE.Vector3( -400, 0, -240 );
    }

    #initBallsDynamics() {
        let mass = 0.23; // kg
        // balls
        this.cue = new Ball( this.mesh1, mass, this.#radius, "cue" );
        this.cue.setPosition( 0, 100, 0 );
        this.ball1 = new Ball( this.mesh2, mass, this.#radius, "ballRed" );
        this.ball1.setPosition( -50, 0, 0 );
        this.ball2 = new Ball( this.mesh3, mass, this.#radius, "ballGreen" );
        this.ball2.setPosition( -140, 0, 16 );
        this.ball3 = new Ball( this.mesh4, mass, this.#radius, "ballYellow" );
        this.ball3.setPosition( -140, 0, -16 );
        this.ball4 = new Ball( this.mesh5, mass, this.#radius, "ballOrange" );
        this.ball4.setPosition( -160, 0, 0 );

        this.balls = [];
        this.balls.push( this.cue, this.ball1, this.ball2, this.ball3, this.ball4 );
    }

    #initCushionsDynamics() {
        let mass = 10; // kg
        // cushions
        this.cushion = new Cushion( this.meshPlane, 10, Dynamics.xAxis.clone().negate() , "cushion" );
        this.westCushion = new Cushion( this.westCushionMesh, mass, Dynamics.xAxis.clone(), "westCushion" );
        this.eastCushion = new Cushion( this.eastCushionMesh, mass, Dynamics.xAxis.clone().negate(), "eastCushion" );
        this.northCushion = new Cushion( this.northCushionMesh, mass, Dynamics.zAxis.clone(), "eastCushion" );
        this.southCushion = new Cushion( this.southCushionMesh, mass, Dynamics.zAxis.clone().negate(), "eastCushion" );
    }

    #initCollision() {
        this.objects = [];
        this.objects.push( this.cue );
        // this.objects.push( this.ball1 );
        // this.objects.push( this.ball2 );
        // this.objects.push( this.ball3 );
        // this.objects.push( this.ball4 );
        // this.objects.push( this.cushion );
        // this.objects.push( this.westCushion );
        // this.objects.push( this.eastCushion );
        // this.objects.push( this.northCushion );
        // this.objects.push( this.southCushion );

        this.collidables = [];
        this.collidables.push( this.cue );
        // this.collidables.push( this.ball1 );
        // this.collidables.push( this.ball2 );
        // this.collidables.push( this.ball3 );
        // this.collidables.push( this.ball4 );
        this.collidables.push( this.cushion );
        // this.collidables.push( this.westCushion );
        // this.collidables.push( this.eastCushion );
        // this.collidables.push( this.northCushion );
        // this.collidables.push( this.southCushion );

        this.ballsCollided = [];
    }

    #initDynamics() {
        this.#initBallsDynamics();
        this.#initCushionsDynamics();
        this.#initCollision();
    }

    // called by window.onload
    static run() {
        let animator = new AnimatorTwoGravity();
        Animator.animator = animator;
        animator.initTwo();
        animator.#initDynamics();
        console.log( animator.objects );

        // add the canvas to the HTML
        document.body.appendChild( animator.renderer.domElement );

        animator.renderer.render( animator.scene, animator.camera );
        // record last time we call the render function
        setTimeout( function () {
            animator.initializingDate = new Date().getTime() / 1000; // s
            animator.startingDate = animator.initializingDate;
            AnimatorTwoGravity.renderTwo();
        }, 1000 );
    }

    // render functions
    static renderTwo() {
        let animator = Animator.animator;
        let objects = animator.objects;

        // t + Dt = new Date() = t( t )
        let current = new Date().getTime() / 1000; // s
        // Dt = t + Dt - t
        let dt = current - animator.initializingDate; // s
        console.log( dt );

        // stop animating after 20s
        // console.log( current - animator.initializingDate );
        if ( current - animator.startingDate >= 8 ) {
            console.log( animator.mesh1.position );
            return;
        }

        let tempDt = dt;
        // Step 1: Calculate Forces, F( t )
        if ( animator.isInit ) {
            objects[ 0 ].fs.add( animator.impulseStriking );
            tempDt = dt;
            dt = 1;
            // animator.isInit = false;
        }
        objects.forEach( b => {
            // b.addSlidingFric( animator.us, Dynamics.G );
            b.addGravityForce( Dynamics.G );
        } );

        // Step 2: update position ( integrate velocity )
        objects.forEach( b => b.updatePos( dt ) );

        // Render the scene
        animator.renderer.render( animator.scene, animator.camera );

        // Perform collision detection / response
        dt = Dynamics.detectCollision( current, dt );

        // console.log(animator.f);
        // update Momentum ( integrate force / acceleration )
        objects.forEach( b => {
            // b.addSlidingFric( animator.us, Dynamics.G );
            // b.addGravityForce( Dynamics.G );
            b.updateM( dt );
            b.impulses = [];
            b.fs.set( 0, 0, 0 );
        } );

        // Step 3: Calculate velocities
        objects.forEach( b => {
            b.calV();
            b.isStationary();
            // console.log( b.name+" v", b.v );
        } );

        // update t to t + Dt
        if ( animator.isInit ) {
            dt = tempDt;
            animator.isInit = false;
        }
        animator.initializingDate += dt;

        // Go to step 1
        requestAnimationFrame( AnimatorTwoGravity.renderTwo );
        // balls.forEach( b=> console.log( b.group.position));
        console.log( "\n" );
    }

    #radius = 10;
    #shapes = 20;

    #initBalls() {
        // create a ball with len 60
        let sphere1 = new THREE.SphereGeometry( this.#radius, this.#shapes, this.#shapes );
        let sphere2 = new THREE.SphereGeometry( this.#radius, this.#shapes, this.#shapes );
        let sphere3 = new THREE.SphereGeometry( this.#radius, this.#shapes, this.#shapes );
        let sphere4 = new THREE.SphereGeometry( this.#radius, this.#shapes, this.#shapes );

        // set up material
        let material1 = new THREE.MeshLambertMaterial( {
            color: 0x0000ff // blue
        } );
        let material2 = new THREE.MeshLambertMaterial( {
            color: 0xCC0033 // red
        } );
        let material3 = new THREE.MeshLambertMaterial( {
            color: 0x336666 // ink green
        } );
        let material4 = new THREE.MeshLambertMaterial( {
            color: 0xFFFF00 // Yellow
        } );
        let material5 = new THREE.MeshLambertMaterial( {
            color: 0xFFCC33 // orange
        } );

        // nest the cube in the mesh
        this.mesh1 = new THREE.Mesh( sphere1, material1 );
        this.mesh2 = new THREE.Mesh( sphere2, material2 );
        this.mesh3 = new THREE.Mesh( sphere3, material3 );
        this.mesh4 = new THREE.Mesh( sphere4, material4 );
        this.mesh5 = new THREE.Mesh( sphere4, material5 );

        this.scene.add( this.mesh1 );
        // this.scene.add( this.mesh2 );
        // this.scene.add( this.mesh3 );
        // this.scene.add( this.mesh4 );
        // this.scene.add( this.mesh5 );
    }

    #rightAngleInRadian = MyMath.radians( 90 );

    #initCushions() {
        const width = 700;
        const height = 500;

        let plane = new THREE.PlaneGeometry( width, height );
        let westCushion = new THREE.PlaneGeometry( this.#radius * 2, height );
        let eastCushion = new THREE.PlaneGeometry( this.#radius * 2, height );
        let northCushion = new THREE.PlaneGeometry( width, this.#radius * 2 );
        let southCushion = new THREE.PlaneGeometry( width, this.#radius * 2 );

        // #006633
        const materialPlane = new THREE.MeshBasicMaterial( { color: 0x009966, side: THREE.DoubleSide } );
        // const materialPlane = new THREE.MeshBasicMaterial( { color: 0x009966  } );
        const materialCushion = new THREE.MeshBasicMaterial( { color: 0x006633, side: THREE.DoubleSide } );

        this.meshPlane = new THREE.Mesh( plane, materialPlane );
        // this.meshPlane.add( new THREE.AxesHelper( 250 ) );
        this.meshPlane.translateX( 100 );
        this.meshPlane.rotateOnAxis( Dynamics.xAxis, this.#rightAngleInRadian );
        this.meshPlane.rotateOnAxis( Dynamics.yAxis, this.#rightAngleInRadian );
        // this.meshPlane.rotateOnAxis( Dynamics.yAxis, MyMath.radians( 20 ) );

        this.westCushionMesh = new THREE.Mesh( westCushion, materialCushion );
        this.westCushionMesh.translateX( -width / 2 );
        this.westCushionMesh.rotateOnAxis( Dynamics.xAxis, this.#rightAngleInRadian );
        this.westCushionMesh.rotateOnAxis( Dynamics.yAxis, this.#rightAngleInRadian );

        this.eastCushionMesh = new THREE.Mesh( eastCushion, materialCushion );
        this.eastCushionMesh.translateX( width / 2 );
        this.eastCushionMesh.rotateOnAxis( Dynamics.xAxis, this.#rightAngleInRadian );
        this.eastCushionMesh.rotateOnAxis( Dynamics.yAxis, this.#rightAngleInRadian );

        this.northCushionMesh = new THREE.Mesh( northCushion, materialCushion );
        this.northCushionMesh.translateZ( -height / 2 );

        this.southCushionMesh = new THREE.Mesh( southCushion, materialCushion );
        this.southCushionMesh.translateZ( height / 2 );

        this.scene.add( this.meshPlane );
        // this.scene.add( this.westCushionMesh );
        // this.scene.add( this.eastCushionMesh );
        // this.scene.add( this.northCushionMesh );
        // this.scene.add( this.southCushionMesh );
    }

    #initObjects() {
        this.#initBalls();
        this.#initCushions();
    }

    initTwo() {
        // Setting up the basics, scene, lights and material, for three.js:
        // http://www.webgl3d.cn/Three.js/
        // assign #2
        this.mesh2 = null;

        // set up our scene
        this.scene = new THREE.Scene();

        this.#initObjects();

        // set up point light
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
        // const S = 190; // factor to control the size of showing area
        const S = 260; // factor to control the size of showing area

        // set up Orthographic Camera
        this.camera = new THREE.OrthographicCamera( -S * K, S * K, S, -S, 1, 1000 );
        // this.camera.position.set( 200, 200, 200 );
        // this.camera.position.set( 60, 200, 120 );
        this.camera.position.set( 0, 20, 200 ); // looking from Z
        // this.camera.position.set( 0, 200, 0 ); // looking from Y
        this.camera.lookAt( this.scene.position );

        // set up renderer
        this.renderer = new THREE.WebGLRenderer();
        this.renderer.setSize( WIDTH, HEIGHT );
        this.renderer.setClearColor( 0xb9d3ff, 1 );

        // set up axis helpers
        let axisHelper = new THREE.AxesHelper( 250 );
        this.scene.add( axisHelper );

        this.controls = new OrbitControls( this.camera, this.renderer.domElement );
        this.controls.addEventListener( 'change', Animator.render );
    }
}