"use strict"

export default class KeyFrames {
    static MOVE_ALONG_X_AXIS = [
        {
            time: 0.0 * 1000,
            position: new THREE.Vector3( 0.0, 0.0, 0.0 ),
        },
        {
            time: 1.0 * 1000,
            position: new THREE.Vector3( 25.0, 0.0, 0.0 ),
        },
        {
            time: 2.0 * 1000,
            position: new THREE.Vector3( 50.0, 0.0, 0.0 ),
        },
        {
            time: 3.0 * 1000,
            position: new THREE.Vector3( 100.0, 0.0, 0.0 ),
        },
        {
            time: 4.0 * 1000,
            position: new THREE.Vector3( 150.0, 0.0, 0.0 ),
        }
    ];

    static MOVE_ALONG_Y_AXIS = [
        {
            time: 0.0 * 1000,
            position: new THREE.Vector3( 0.0, 0.0, 0.0 ),
        },
        {
            time: 1.0 * 1000,
            position: new THREE.Vector3( 0.0, 25.0, 0.0 ),
        },
        {
            time: 2.0 * 1000,
            position: new THREE.Vector3( 0.0, 50.0, 0.0 ),
        },
        {
            time: 3.0 * 1000,
            position: new THREE.Vector3( 0, 100.0, 0.0 ),
        },
        {
            time: 4.0 * 1000,
            position: new THREE.Vector3( 0.0, 150.0, 0.0 ),
        }
    ];

    static MOVE_ALONG_Z_AXIS = [
        {
            time: 0.0 * 1000,
            position: new THREE.Vector3( 0.0, 0.0, 0.0 ),
        },
        {
            time: 1.0 * 1000,
            position: new THREE.Vector3( 0.0, 0.0, 25.0 ),
        },
        {
            time: 2.0 * 1000,
            position: new THREE.Vector3( 0.0, 0.0, 50.0 ),
        },
        {
            time: 3.0 * 1000,
            position: new THREE.Vector3( 0, 0.0, 100.0 ),
        },
        {
            time: 4.0 * 1000,
            position: new THREE.Vector3( 0.0, 0.0, 150.0 ),
        }
    ];

    static MOVE_ALONG_XY_AXIS = [
        {
            time: 0.0 * 1000,
            position: new THREE.Vector3( 0.0, 0.0, 0.0 ),
        },
        {
            time: 1.0 * 1000,
            position: new THREE.Vector3( -25.0, -25.0, 0.0 ),
        },
        {
            time: 2.0 * 1000,
            position: new THREE.Vector3( -50.0, -50.0, 0.0 ),
        },
        {
            time: 3.0 * 1000,
            position: new THREE.Vector3( -100, -100.0, 0.0 ),
        },
        {
            time: 4.0 * 1000,
            position: new THREE.Vector3(- 150.0, -150.0, 0.0 ),
        }
    ];
}
