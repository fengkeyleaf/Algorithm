"use strict"

/*
 * KeyFraming.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 mapTtoU(), LinearInterpolation(), slerp() on 9/21/2021$
 */

import MyMath from "../lang/MyMath.js";

/**
 * This class consists exclusively of static methods
 * that related to KeyFraming
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class KeyFraming {

    /**
     * @param {CubicBezierCurve3} curve
     * @param {Number} divisions
     * @param {Number} startTime, ms
     * @param {Number} rate
     */

    static getKeyFramesFromBezierCurve( curve, divisions, startTime, rate ) {
        const points = curve.getPoints( divisions );
        let frames = [];
        points.forEach( v => {
            frames.push( {
                time: startTime,
                position: v
            } );
            startTime += rate;
        } );

        return frames;
    }

    /**
     * @param {Array} currents
     * @param {Array} nexts
     * @param {Number} u
     */

    static interpolatingMultiples( currents, nexts, u ) {
        console.assert( MyMath.doubleCompare( u, 1 ) <= 0 && MyMath.doubleCompare( u, 0 ) >= 0, u );
        let res = [];
        console.assert( currents.length === nexts.length, currents, nexts );
        for ( let i = 0; i < currents.length; i++ ) {
            let po = KeyFraming.LinearInterpolation( u, currents[ i ].position, nexts[ i ].position );
            // let q = KeyFraming.slerp( u, currents[ i ], nexts[ i ] );
            let q = new THREE.Quaternion();
            q.slerpQuaternions( currents[ i ].quaternion, currents[ i ].quaternion, u );
            res.push( {
                name: currents[ i ].name,
                position: po,
                quaternion: q
            } );
        }

        return res;
    }

    /**
     * @param {Number} currentT
     * @param {Number} initT
     * @param {Number} EndT
     */

    static mapTtoU( currentT, initT, EndT ) {
        console.assert( !MyMath.isNegative( currentT ), currentT );
        console.assert( !MyMath.isNegative( initT ), initT );
        console.assert( !MyMath.isNegative( EndT ), EndT );
        console.assert( MyMath.doubleCompare( currentT, EndT ) <= 0 && MyMath.doubleCompare( currentT, initT ) >= 0, currentT );
        return ( currentT - initT ) / ( EndT - initT );
    }

    /**
     * @param {Number} u
     * @param {Number} P0
     * @param {Number} P1
     */

    static __LinearInterpolation( u, P0, P1 ) {
        console.assert( MyMath.doubleCompare( u, 1 ) <= 0 && MyMath.doubleCompare( u, 0 ) >= 0, u );
        return ( 1 - u ) * P0 + u * P1;
    }

    /**
     * @param {Number} u
     * @param {Vector3} currentKeyframe
     * @param {Vector3} nextKeyframe
     */

    static LinearInterpolation( u, currentKeyframe, nextKeyframe ) {
        let x = KeyFraming.__LinearInterpolation( u, currentKeyframe.x, nextKeyframe.x );
        let y = KeyFraming.__LinearInterpolation( u, currentKeyframe.y, nextKeyframe.y );
        let z = KeyFraming.__LinearInterpolation( u, currentKeyframe.z, nextKeyframe.z );

        return new THREE.Vector3( x, y, z );
    }

    /**
     * Spherical Linear Interpolation (Slerp)
     *
     * @param {Quaternion} q1
     * @param {Quaternion} q2
     * @param {Number} u
     */

    // TODO: 11/2/2021 cannot handle cases where res < 0 or res > 1, where res = q1.dot( q2 )
    static __slerp( q1, q2, u ) {
        console.assert( q1, q1 );
        console.assert( q2, q2 );
        const res = q1.dot( q2 );
        if ( MyMath.equalsQuaternion( q1, q2 ) ) {
            console.assert( MyMath.equalsQuaternion( q1, q2 ) );
            return q1;
        }
        console.assert( !MyMath.equalsQuaternion( q1, q2 ) );
        console.assert( MyMath.doubleCompare( res, 1 ) <= 0 && MyMath.doubleCompare( res, -1 ) >= 0, res, q1, q2 );
        const THETA = Math.acos( res ); // in radians
        console.assert( THETA, THETA, q1, q2 );
        const SIN_THETA = Math.sin( THETA );
        console.assert( SIN_THETA, SIN_THETA );
        const RATIO1 = Math.sin( ( 1 - u ) * THETA ) / SIN_THETA;
        console.assert( RATIO1, RATIO1 );
        const RATIO2 = Math.sin( u * THETA ) / SIN_THETA;
        console.assert( RATIO1, RATIO1 );

        q1 = MyMath.multipleConstQuaternion( q1, RATIO1 );
        q2 = MyMath.multipleConstQuaternion( q2, RATIO2 );
        return MyMath.addQuaternion( q1, q2 );
    }

    /**
     * Spherical Linear Interpolation (Slerp)
     *
     * @param {Number} u
     * @param {Quaternion} currentQuaternion
     * @param {Quaternion} nextQuaternion
     */

    static slerp( u, currentQuaternion, nextQuaternion ) {
        console.assert( MyMath.doubleCompare( u, 1 ) <= 0 && MyMath.doubleCompare( u, 0 ) >= 0, u );
        return KeyFraming.__slerp( currentQuaternion.clone(), nextQuaternion.clone(), u );
    }
}