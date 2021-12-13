"use strict"

/*
 * Animator.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

/**
 * Assignment 0 - animation framework:
 * Create the framework and testbed for the animation
 * techniques to be explored during the semester.
 *
 * Assignment 1 - keyframing:
 * Write a simplified key framing system that will translate
 * and rotate a single object based on a set of key frames.
 *
 * Assignment 2 - billiards, or collision system:
 * Write a system that will simulate a single shot of a billiards game.
 *
 * Assignment 3 - motion capture:
 * Read, interpret and apply motion capture data.
 *
 * Assignment #4a - particle system
 * To implement a simple particle system
 * Simulate the tail of a moving comet
 *
 * Assignment #4b - Behavioral motion (not implemented yet)
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

export default class Animator {
    static animator = null;

    constructor() {
        // time
        this.initializingDate = null;
        this.startingDate = null;

        // webgl
        this.scene = null;
        this.renderer = null;
        this.camera = null;
        this.group1 = null;
        this.mesh1 = null;
        this.controls = null;
    }

    static render() {
        Animator.animator.renderer.render( Animator.animator.scene, Animator.animator.camera );
    }
}