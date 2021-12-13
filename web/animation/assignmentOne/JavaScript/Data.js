"use strict"

/*
 * Data.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 Basic operations on 12/5/2021$
 */

import MyMath from "../../../myLibraries/lang/MyMath.js";

/**
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Data {
    static #KEYFRAMES = [
        // 0.0  0.0 0.0 0.0 1.0 1.0 -1.0 0.0
        {
            time: 0.0 * 1000,
            position: new THREE.Vector3( 0.0, 0.0, 0.0 ),
            quaternion: new THREE.Quaternion().setFromAxisAngle( new THREE.Vector3( 1, 1, -1 ), MyMath.radians( 0 ) ),
            degrees: 0
        },
        // 1.0  4.0 0.0 0.0 1.0 1.0 -1.0 30.0
        {
            time: 1.0 * 1000,
            position: new THREE.Vector3( 4.0, 0.0, 0.0 ),
            quaternion: new THREE.Quaternion().setFromAxisAngle( new THREE.Vector3( 1, 1, -1 ), MyMath.radians( 30 ) ),
            degrees: 30
        },
        // 2.0  8.0 0.0 0.0 1.0 1.0 -1.0 90.0
        {
            time: 2.0 * 1000,
            position: new THREE.Vector3( 8.0, 0.0, 0.0 ),
            quaternion: new THREE.Quaternion().setFromAxisAngle( new THREE.Vector3( 1, 1, -1 ), MyMath.radians( 90 ) ),
            degrees: 90
        },
        // 3.0  12.0 12.0 12.0 1.0 1.0 -1.0 180.0
        {
            time: 3.0 * 1000,
            position: new THREE.Vector3( 12.0, 12.0, 12.0 ),
            quaternion: new THREE.Quaternion().setFromAxisAngle( new THREE.Vector3( 1, 1, -1 ), MyMath.radians( 180 ) ),
            degrees: 180
        },
        // 4.0  12.0 18.0 18.0 1.0 1.0 -1.0 270.0
        {
            time: 4.0 * 1000,
            position: new THREE.Vector3( 12, 18, 18 ),
            quaternion: new THREE.Quaternion().setFromAxisAngle( new THREE.Vector3( 1, 1, -1 ), MyMath.radians( 270 ) ),
            degrees: 270
        },
        // 5.0  18.0 18.0 18.0 0.0 1.0 0.0 90.0
        {
            time: 5.0 * 1000,
            position: new THREE.Vector3( 18, 18, 18 ),
            quaternion: new THREE.Quaternion().setFromAxisAngle( new THREE.Vector3( 0, 1, 0 ), MyMath.radians( 90 ) ),
            degrees: 90
        },
        // 6.0  18.0 18.0 18.0 0.0 0.0 1.0 90.0
        {
            time: 6.0 * 1000,
            position: new THREE.Vector3( 18, 18, 18 ),
            quaternion: new THREE.Quaternion().setFromAxisAngle( new THREE.Vector3( 0, 0, 1 ), MyMath.radians( 90 ) ),
            degrees: 90
        },
        // 7.0  25.0 12.0 12.0 1.0 0.0 0.0 0.0
        {
            time: 7.0 * 1000,
            position: new THREE.Vector3( 25, 12, 12 ),
            quaternion: new THREE.Quaternion().setFromAxisAngle( new THREE.Vector3( 1, 0, 0 ), MyMath.radians( 0 ) ),
            degrees: 0
        },
        // 8.0  25.0 0.0 18.0 1.0 0.0 0.0 0.0
        {
            time: 8.0 * 1000,
            position: new THREE.Vector3( 25, 0, 18 ),
            quaternion: new THREE.Quaternion().setFromAxisAngle( new THREE.Vector3( 1, 0, 0 ), MyMath.radians( 0 ) ),
            degrees: 0
        },
        // 9.0 25.0 1.0 18.0 1.0 0.0 0.0 0.0
        {
            time: 9.0 * 1000,
            position: new THREE.Vector3( 25, 1, 18 ),
            quaternion: new THREE.Quaternion().setFromAxisAngle( new THREE.Vector3( 1, 0, 0 ), MyMath.radians( 0 ) ),
            degrees: 0
        }
    ];

    static initData() {
        console.log( Data.#KEYFRAMES );
        Data.#KEYFRAMES.forEach( datum => {
            datum.quaternion.normalize();
        } );
        Data.#KEYFRAMES.forEach( k => console.log( k.quaternion ) );

        return Data.#KEYFRAMES;
    }
}