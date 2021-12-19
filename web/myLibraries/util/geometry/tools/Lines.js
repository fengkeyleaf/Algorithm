"use strict"

/*
 * Lines.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import MyMath from "../../../lang/MyMath.js";
import Triangles from "./Triangles.js";
import Vectors from "./Vectors.js";
import Main from "../../../../finalProject/JavaScript/Main.js";
import KeyFraming from "../../../animation/KeyFraming.js";
import Line from "../elements/line/Line.js";
import SnapShot from "../../../GUI/geometry/SnapShot.js";

/**
 * This class consists exclusively of static methods that
 * related to Line
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/tools/Lines.java>Lines</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Lines {

    /**
     * if a, b and c are on the same line
     */

    static isOnTheSameLine( a, b, c ) {
        return MyMath.isEqualZero( Triangles.areaTwo( a, b, c ) );
    }

    /**
     * compare by slope
     */

    static compareBySlope( l1, l2 ) {
        let res = l1.dy * l2.dx - l1.dx * l2.dy;
        if ( MyMath.isEqualZero( res ) ) return 0;
        else if ( res > 0 ) return 1;

        return -1;
    }

    /**
     * compare by EndPoint
     */

    static compareByEndPoint( l1, l2 ) {
        return Vectors.sortByX( l1.endPoint, l2.endPoint );
    }

    /**
     * compare by StartPoint
     */

    static compareByStartPoint( l1, l2 ) {
        return Vectors.sortByX( l1.startPoint, l2.startPoint );
    }

    /**
     * @param {[Float32Array]} lines
     * @param {[Float32Array]} colors
     */

    // TODO: 9/29/2021 para: drawingTypes
    static animateByLine( lines, colors ) {
        console.assert( lines.length % 2 === 0 );
        console.assert( lines.length === 2 );
        console.assert( lines.length === colors.length );
        let current = new Date() - Main.main.initializingDate;

        // draw the final status
        if ( current >= SnapShot.animationTime ) {
            // pop previous statuses
            Main.pop( lines.length, 2 );

            // add current statues
            for ( let i = 0; i < lines.length; i += 2 ) {
                let linePre = lines[ i ];
                let lineCurr = lines[ i + 1 ];

                // initializing line
                Main.main.pushData( new Float32Array( [ lineCurr[ 0 ], lineCurr[ 1 ], lineCurr[ 2 ], lineCurr[ 3 ] ] ), colors[ i + 1 ], 2 );
            }

            Main.main.draw();
            return true;
        }

        requestAnimationFrame( () => Lines.animateByLine( lines, colors ) );
        Main.main.draw();

        // pop previous statuses
        Main.pop( lines.length, 2 );

        // add current statues
        for ( let i = 0; i < lines.length; i += 2 ) {
            // get keyframe
            let linePre = lines[ i ];
            let startPre = new THREE.Vector3( linePre[ 0 ], linePre[ 1 ], 0 );
            let endPre = new THREE.Vector3( linePre[ 2 ], linePre[ 3 ], 0 );

            let lineCurr = lines[ i + 1 ];
            let startCurr = new THREE.Vector3( lineCurr[ 0 ], lineCurr[ 1 ], 0 );
            let endCurr = new THREE.Vector3( lineCurr[ 2 ], lineCurr[ 3 ], 0 );

            let middleStart = new THREE.Vector3( 0, 0, 0 );
            let middleEnd = new THREE.Vector3( 0, 0, 0 );

            // interpolate
            let u = KeyFraming.mapTtoU( current, 0, SnapShot.animationTime );
            middleStart = KeyFraming.LinearInterpolation( u, startPre, startCurr );
            middleEnd = KeyFraming.LinearInterpolation( u, endPre, endCurr );

            // push data
            Main.main.pushData( new Float32Array( [ middleStart.x, middleStart.y, middleEnd.x, middleEnd.y ] ), colors[ i + 1 ], 2 );
        }

        return false;
    }

    /**
     * @param {[Float32Array]} lines
     * @param {[Float32Array]} colors
     */

    // TODO: 9/29/2021 para: drawingTypes
    static animateByPoint( lines, colors ) {
        // console.log( lines, colors );
        console.assert( lines.length === colors.length );
        let current = new Date() - Main.main.initializingDate;

        // draw the final status
        if ( current >= SnapShot.animationTime ) {
            // pop previous statuses
            Main.pop( lines.length, 1 );

            // add current statues
            for ( let i = 0; i < lines.length; i++ ) {
                let line = lines[ i ];
                let start = new THREE.Vector3( line[ 0 ], line[ 1 ], 0 );
                let end = new THREE.Vector3( line[ 2 ], line[ 3 ], 0 );
                // initializing line
                Main.main.pushData( new Float32Array( [ start.x, start.y, end.x, end.y ] ), colors[ i ], 2 );
            }

            Main.main.draw();
            return;
        }

        requestAnimationFrame( function () {
            Lines.animateByPoint( lines, colors );
        } );
        Main.main.draw();

        // pop previous statuses
        Main.pop( lines.length, 1 );

        // add current statues
        for ( let i = 0; i < lines.length; i++ ) {
            // get keyframe
            let line = lines[ i ];
            let start = new THREE.Vector3( line[ 0 ], line[ 1 ], 0 );
            let middle = new THREE.Vector3( line[ 0 ], line[ 1 ], 0 );
            let end = new THREE.Vector3( line[ 2 ], line[ 3 ], 0 );

            // interpolate
            let u = KeyFraming.mapTtoU( current, 0, SnapShot.animationTime );
            middle = KeyFraming.LinearInterpolation( u, start, end );

            // push data
            Main.main.pushData( new Float32Array( [ start.x, start.y, middle.x, middle.y ] ), colors[ i ], 2 );
        }

        return false;
    }
}
