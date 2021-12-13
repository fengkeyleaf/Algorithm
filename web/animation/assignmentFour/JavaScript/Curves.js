"use strict"

/*
 * Curves.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 Basic operations on 12/5/2021$
 */

import KeyFraming from "../../../myLibraries/animation/KeyFraming.js";

/**
 * class to generate three paths that the comet goes along
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Curves {
    #animator = null;

    constructor( animator ) {
        this.#animator = animator;
    }

    init() {
        this.initPathFirst();
        this.initPathSecond();
        this.initPathThird();
    }

    initPathFirst() {
        const curve = new THREE.CubicBezierCurve3(
            new THREE.Vector3( -50, 0, 0 ),
            new THREE.Vector3( -42, -20, 0 ),
            new THREE.Vector3( 18, -20, 0 ),
            new THREE.Vector3( 10, 20, 0 )
        );

        const points = curve.getPoints( 50 );
        // console.log( points);
        // console.log( KeyFraming.getKeyFramesFromBezierCurve( curve, 50, 100 ) );
        let res = KeyFraming.getKeyFramesFromBezierCurve( curve, 50, 0, this.#animator.keyframeRate );
        this.#animator.allKeyframes = this.#animator.allKeyframes.concat( res );
        // allKeyframes.push( res );

        const lineMaterial = new THREE.LineBasicMaterial( { color: 0xff0000 } );

        const geometry = new THREE.BufferGeometry().setFromPoints( points );

        // Create the final object to add to the scene
        const curveObject = new THREE.Line( geometry, lineMaterial );

        this.#animator.scene.add( curveObject );
    }

    initPathSecond() {
        const curve = new THREE.CubicBezierCurve3(
            new THREE.Vector3( 10, 20, 0 ),
            new THREE.Vector3( 8, 25, 0 ),
            new THREE.Vector3( 2, 25, 0 ),
            new THREE.Vector3( 0, 20, 0 ),
        );

        const points = curve.getPoints( 50 );
        // console.log( points);
        // console.log( KeyFraming.getKeyFramesFromBezierCurve( curve, 50, 100 ) );
        let res = KeyFraming.getKeyFramesFromBezierCurve( curve, 20, this.#animator.allKeyframes.getLast().time, this.#animator.keyframeRate );
        // let res = KeyFraming.getKeyFramesFromBezierCurve( curve, 20, 0, keyframeRate );
        res.shift();
        this.#animator.allKeyframes = this.#animator.allKeyframes.concat( res );
        // allKeyframes.push( res );

        const lineMaterial = new THREE.LineBasicMaterial( { color: 0xff0000 } );

        const geometry = new THREE.BufferGeometry().setFromPoints( points );

        // Create the final object to add to the scene
        const curveObject = new THREE.Line( geometry, lineMaterial );

        this.#animator.scene.add( curveObject );
    }

    initPathThird() {
        const curve = new THREE.CubicBezierCurve3(
            new THREE.Vector3( 0, 20, 0 ),
            new THREE.Vector3( -8, 0, 0 ),
            new THREE.Vector3( 16, 0, 0 ),
            new THREE.Vector3( 50, 0, 0 ),
        );

        const points = curve.getPoints( 50 );
        // console.log( points);
        // console.log( KeyFraming.getKeyFramesFromBezierCurve( curve, 50, 100 ) );
        // let res = KeyFraming.getKeyFramesFromBezierCurve( curve, 50, 0, keyframeRate );
        let res = KeyFraming.getKeyFramesFromBezierCurve( curve, 50, this.#animator.allKeyframes.getLast().time, this.#animator.keyframeRate );
        res.shift();
        this.#animator.allKeyframes = this.#animator.allKeyframes.concat( res );
        // allKeyframes.push( res );

        const lineMaterial = new THREE.LineBasicMaterial( { color: 0xff0000 } );

        const geometry = new THREE.BufferGeometry().setFromPoints( points );

        // Create the final object to add to the scene
        const curveObject = new THREE.Line( geometry, lineMaterial );

        this.#animator.scene.add( curveObject );
    }
}