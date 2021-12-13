"use strict"

/*
 * AnimatorZero.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Animator from "../../../myLibraries/animation/Animator.js";
import MyMath from "../../../myLibraries/lang/MyMath.js";

/**
 * Assignment 0:
 * Create the framework and testbed for the animation
 * techniques to be explored during the semester.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

export default class AnimatorZero extends Animator {

    // called by window.onload
    static run() {
        let animator = new AnimatorZero();
        Animator.animator = animator;
        animator.init();

        // add the canvas to the HTML
        document.body.appendChild( animator.renderer.domElement );

        // start rendering
        // Assignment #0
        AnimatorZero.animate();
    }

    static animate() {
        let current = new Date();

        // stop animating after 20s
        if ( current - Animator.animator.initializingDate >= 20 * 1000 ) {
            console.log( Animator.animator.group.position );
            return;
        }

        let t = ( current - Animator.animator.initializingDate ) / 1000;
        Animator.animator.initializingDate = current;

        requestAnimationFrame( AnimatorZero.animate );
        Animator.render();

        // make sure translation first and then rotation by nesting
        // more info, see:
        // https://stackoverflow.com/questions/15292504/combine-rotation-and-translation-with-three-js
        // in short, we cannot use:
        // mesh.translateOnAxis( axis, 5 * t );
        // mesh.rotateY( 1.8 * t );
        // no mater how you define
        // the order of translation and rotation in this way.
        // three.js will apply rotation first and then translate,
        // not the opposite order.
        Animator.animator.group1.translateOnAxis( new THREE.Vector3( 1, 1, 0 ), 5 * t );
        Animator.animator.mesh1.rotateY( MyMath.radians( 18 ) * t );
    }

    init() {
        // Setting up the basics, scene, lights and material, for three.js:
        // http://www.webgl3d.cn/Three.js/

        // set up our scene
        this.scene = new THREE.Scene();

        // create a cube with len 60
        const LEN = 10;
        let cube = new THREE.BoxGeometry( LEN, LEN, LEN );

        // set up material
        let material = new THREE.MeshLambertMaterial( {
            color: 0x0000ff
        } );

        // nest the cube in the mesh,
        // then nest the mesh in the parent group
        this.mesh1 = new THREE.Mesh( cube, material );

        this.offset = new THREE.Vector3( 0, 20, 0 );
        const points = [];
        points.push( this.mesh1.position.clone() );
        points.push( this.mesh1.position.clone().add( this.offset ) );

        const geometry = new THREE.BufferGeometry().setFromPoints( points );
        this.line = new THREE.Line( geometry, material );
        console.log( this.line );

        // this.mesh2 = new THREE.Mesh( line, material );
        this.group1 = new THREE.Group();
        this.group1.add( this.mesh1 );
        // this.group1.add( this.line );
        // this.group1.add( this.mesh2 );
        this.scene.add( this.line );
        this.scene.add( this.group1 );

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
        const S = 60; // factor to control the size of showing area

        // set up Orthographic Camera
        this.camera = new THREE.OrthographicCamera( -S * K, S * K, S, -S, 1, 1000 );
        this.camera.position.set( 200, 300, 200 );
        this.camera.lookAt( this.scene.position );

        // set up renderer
        this.renderer = new THREE.WebGLRenderer();
        this.renderer.setSize( WIDTH, HEIGHT );
        this.renderer.setClearColor( 0xb9d3ff, 1 );

        // set up axis helpers
        let axisHelper = new THREE.AxesHelper( 250 );
        this.scene.add( axisHelper );

        // record last time we call the render function
        this.initializingDate = new Date();
    }

}