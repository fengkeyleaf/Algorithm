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

import AnimatorZero from "../../assignmentZero/JavaScript/AnimatorZero.js";
import KeyFraming from "../../../myLibraries/animation/KeyFraming.js";
import Animator from "../../../myLibraries/animation/Animator.js";
import Data from "./Data.js";

/**
 * Assignment 1:
 * Write a simplified key framing system that will translate
 * and rotate a single object based on a set of key frames.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

export default class AnimatorOne extends AnimatorZero {
    constructor() {
        super();
        // initializing keyframe - assign #1
        this.allKeyframes = null;
        this.startIndex = 0;
        this.currentKeyframe = null;

        this.endIndex = 1;
        this.nextKeyframe = null;

        this.preTime = null;
        this.nextTime = null;
    }

    printInfo() {
        console.log( Animator.animator );
        console.log( Animator.animator.group1 );
        console.log( "position", Animator.animator.group1.position );
        console.log( "rotation", Animator.animator.group1.rotation );
        console.log( "scale", Animator.animator.group1.scale );
    }

    initKeyFrames() {
        const frames = Data.initData();
        this.allKeyframes = frames;
        this.currentKeyframe = this.allKeyframes[ this.startIndex ];
        this.nextKeyframe =this.allKeyframes[ this.endIndex ];

        this.preTime = this.currentKeyframe.time;
        this.nextTime = this.nextKeyframe.time;
    }

    // called by window.onload
    static run() {
        // assignment one
        let animator = new AnimatorOne();
        Animator.animator = animator;
        animator.init();
        animator.initKeyFrames();

        // add the canvas to the HTML
        document.body.appendChild( animator.renderer.domElement );

        // Assignment #1
        AnimatorOne.animate();
    }

    static animate() {
        let current = new Date() - Animator.animator.initializingDate;

        if ( current >= Animator.animator.nextTime ) {
            Animator.animator.update();
            if ( Animator.animator.endIndex >= Animator.animator.allKeyframes.length - 1 ) {
                console.log( "stop here" );
                Animator.animator.printInfo();
                return;
            }
        }

        requestAnimationFrame( AnimatorOne.animate );
        Animator.animator.renderer.render( Animator.animator.scene, Animator.animator.camera );

        // For each frame
        // Get time, t
        // Convert t to u
        let u = KeyFraming.mapTtoU( current, Animator.animator.preTime, Animator.animator.nextTime );
        // Perform interpolation, for u value
        // Translation
        let po = KeyFraming.LinearInterpolation( u, Animator.animator.currentKeyframe.position, Animator.animator.nextKeyframe.position );
        Animator.animator.mesh1.position.set( po.x, po.y, po.z );

        // Orientation
        // 1) As rotation assumes normalized axis, quaternions should
        // be normalized to be used for rotation.
        // 2) Allows for concatenation of rotations via quaternion
        // multiplication (with a caveat…e.g. must multiply in reverse order ).
        let newQuat = KeyFraming.slerp( u, Animator.animator.currentKeyframe.quaternion, Animator.animator.nextKeyframe.quaternion );
        Animator.animator.mesh1.quaternion.set( newQuat.x, newQuat.y, newQuat.z, newQuat.w );
        // Construct transformation matrix
        // Apply to object coordinates
        // Render
    }

    /**
     * update next key frame
     */

    update() {
        this.startIndex = this.endIndex++;
        this.currentKeyframe = this.allKeyframes[ this.startIndex ];
        this.nextKeyframe = this.allKeyframes[ this.endIndex ];
        this.preTime = this.currentKeyframe.time;
        console.assert( this.nextKeyframe, this.endIndex );
        this.nextTime = this.nextKeyframe.time;
        this.printInfo();
    }
}