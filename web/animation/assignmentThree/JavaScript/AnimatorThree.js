"use strict"

/*
 * AnimatorThree.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Animator from "../../../myLibraries/animation/Animator.js";
import MoCop from "../../../myLibraries/animation/MoCop.js";
import { OrbitControls } from "../../../myLibraries/externals/jsm/controls/OrbitControls.js";
import AnimatorOne from "../../assignmentOne/JavaScript/AnimatorOne.js";
import { BVHLoader } from '../../../myLibraries/externals/jsm/loaders/BVHLoader.js';
import Slider from "../../../myLibraries/GUI/frame/Slider.js";
import MyMath from "../../../myLibraries/lang/MyMath.js";

/**
 * Assignment 3 - motion capture:
 * Read, interpret and apply motion capture data.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

export default class AnimatorThree extends AnimatorOne {
    constructor() {
        super();
        Animator.animator = this;

        this.skeletonHelper = null;
        this.root = null;

        this.isAnimating = true;
        this.progessSlider = new Slider( "progressSlider", '.progress-bar', '.progress-thumb', AnimatorThree.setProgress );
        this.speedSlider = new Slider( "speedSlider", '.progress-bar', '.progress-thumb', AnimatorThree.setSpeed );
        this.speedSlider.setProgress( 6 );

        this.frameRate = 0;
        this.fps = 60;
        this.fpsInterval = 1000 / this.fps;
    }

    static run() {
        new AnimatorThree().init();

        // Frame Time:	0.00833333
        let file = "./doc/pirouette.bvh";
        // file = "./doc/pirouette - only two keyframes.bvh";
        // file = "./doc/Arms.bvh";
        // file = "./doc/Ambient.bvh";
        // file = "./doc/Example1 - basic.bvh";
        // file = "./doc/Example2 - very simple.bvh"
        // file = "./doc/Example3 - one leg with rotation.bvh"
        // file = "./doc/Example5 - full body with only offset.bvh"
        new BVHLoader().load( file, function ( result ) {
            let animator = Animator.animator;
            console.log( result );
            animator.skeletonHelper = new THREE.SkeletonHelper( result.skeleton.bones[ 0 ] );

            animator.#setFrameRate( result.clip.tracks );
            animator.allKeyframes = MoCop.getKeyFrames( result.clip );

            console.assert( animator.allKeyframes.length === result.clip.tracks[ 0 ].values.length / 3, result.clip.tracks.length / 2, animator.allKeyframes.length );
            console.log( animator.allKeyframes, animator.frameRate );
            animator.root = MoCop.setupShapes( result.skeleton.bones[ 0 ], animator.scene );
            console.log( animator.root );

            animator.allKeyframes.forEach( k => k.reverse() );

            animator.currentKeyframe = animator.allKeyframes[ animator.startIndex ];
            animator.nextKeyframe = animator.allKeyframes[ animator.endIndex ];
            animator.preTime = animator.currentKeyframe[ 0 ].time;
            animator.nextTime = animator.nextKeyframe[ 0 ].time;

            animator.initializingDate = Date.now(); // then
            AnimatorThree.animate();
        } );
    }

    init() {
        // set up our scene
        this.scene = new THREE.Scene();
        this.scene.background = new THREE.Color( 0xeeeeee );
        this.scene.autoUpdate = false;
        this.scene.add( new THREE.GridHelper( 400, 10 ) );

        let point = new THREE.PointLight( 0xffffff );
        point.position.set( 400, 200, 300 );
        this.scene.add( point );
        point.updateMatrixWorld( true );

        // setup ambient light
        let ambient = new THREE.AmbientLight( 0x444444 );
        this.scene.add( ambient );
        ambient.updateMatrixWorld( true );

        // set up the canvas covering the whole DOM
        const WIDTH = window.innerWidth;
        const HEIGHT = window.innerHeight;
        const K = WIDTH / HEIGHT; // ratio of window
        const S = 140; // factor to control the size of showing area

        this.camera = new THREE.OrthographicCamera( -S * K, S * K, S, -S, 1, 1000 );
        this.camera.position.set( 200, 300, 500 );
        this.camera.lookAt( this.scene.position );
        this.camera.updateMatrixWorld( true );

        // set up axis helpers
        let axisHelper = new THREE.AxesHelper( 250 );
        this.scene.add( axisHelper );

        this.renderer = new THREE.WebGLRenderer();
        this.renderer.setSize( WIDTH, HEIGHT );
        this.renderer.setClearColor( 0xb9d3ff, 1 );
        document.body.appendChild( this.renderer.domElement );

        this.controls = new OrbitControls( this.camera, this.renderer.domElement );
        this.controls.addEventListener( 'change', Animator.render );

        Animator.render();
    }

    // https://stackoverflow.com/questions/19764018/controlling-fps-with-requestanimationframe
    static animate( timestamp ) {
        let animator = Animator.animator;

        let copy = [];
        if ( animator.endIndex >= animator.allKeyframes.length - 1 ) {
            // render the last frame
            animator.currentKeyframe.forEach( k => copy.push( k ) );
            MoCop.orientBone( animator.scene, animator.root, copy );
            Animator.render();

            animator.progessSlider.setProgress( 100 );
            animator.isAnimating = false;
            console.log( "stop here" );
            return;
        } else {
            // render next frame
            console.log( "next keyframe" );
            animator.update();
            animator.currentKeyframe.forEach( k => copy.push( k ) );
            MoCop.orientBone( animator.scene, animator.root, copy );
        }

        setTimeout( AnimatorThree.animate, animator.fpsInterval );
        Animator.render();
    }

    update() {
        this.startIndex = this.endIndex++;
        this.#update();
    }

    #update() {
        this.currentKeyframe = this.allKeyframes[ this.startIndex ];
        console.assert( this.currentKeyframe, this.startIndex );
        this.nextKeyframe = this.endIndex >= this.allKeyframes.length ? null : this.allKeyframes[ this.endIndex ];
        this.preTime = this.currentKeyframe[ 0 ].time;
        // console.assert( this.nextKeyframe, this.endIndex );
        this.nextTime = this.nextKeyframe ? this.nextKeyframe[ 0 ].time : null;
        // printInfo();

        this.progessSlider.setProgress( this.startIndex / ( this.allKeyframes.length - 1 ) * 100 );
    }

    printInfo() {
        MoCop.groups.forEach( g => {
            let temp = new THREE.Vector3();
            g.joint.getWorldPosition( temp );
            console.log( g.name, temp );
            console.log( g.joint.rotation );
        } );
    }

    #setFrameRate( tracks ) {
        this.frameRate = tracks[ 0 ].times[ 1 ];
    }

    static setProgress() {
        let animator = Animator.animator;
        // set key frames based on progress slider
        console.assert( MyMath.doubleCompare( animator.progessSlider.per, 0 ) >= 0 && MyMath.doubleCompare( animator.progessSlider.per, 100 ) <= 0 );
        animator.startIndex = Math.floor( animator.progessSlider.per / 100 * ( animator.allKeyframes.length - 1 ) );
        animator.startIndex = animator.startIndex >= animator.allKeyframes.length ? animator.allKeyframes.length - 1 : animator.startIndex;
        animator.endIndex = animator.startIndex + 1;
        animator.#update();

        // re-animate when done with previous animation
        if ( !animator.isAnimating ) {
            animator.isAnimating = true;
            AnimatorThree.animate();
        }
    }

    static setSpeed() {
        let animator = Animator.animator;
        let changeRate = animator.speedSlider.getProgress();
        // range: 1 fps ~ 1000 fps
        animator.fps = 1000 * changeRate / 100;
        animator.fpsInterval = 1000 / animator.fps;
        console.log( animator.fpsInterval );
    }
}