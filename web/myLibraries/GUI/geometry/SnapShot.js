"use strict"

/*
 * SnapShot.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Main from "../../../finalProject/JavaScript/Main.js";
import Drawer from "./Drawer.js";
import Lines from "../../util/geometry/tools/Lines.js";
import MonotoneVertex from "../../util/geometry/DCEL/MonotoneVertex.js";
import Program from "../Program.js";
import Circle from "../../util/geometry/elements/cycle/Circle.js";
import Vertex from "../../util/geometry/DCEL/Vertex.js";

/**
 * data structure of SnapShot
 * This class is the key class to implement step-by-step animation
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

export default class SnapShot {
    static IDStatic = 0;
    static animationTime = 500;

    constructor() {
        this.polygons = {
            points: null,
            colors: null,
            drawingType: Program.LINE_LOOP
        };

        this.diagonals = {
            points: [],
            colors: [],
            drawingType: [] // Program.LINE_LOOP only
        };

        this.sweepLines = {
            points: null,
            colors: null,
            drawingType: Program.LINE_LOOP,
            isNewMonotone: false
        };

        this.stackPoints = {
            points: [],
            colors: [],
            drawingType: [], // Program.LINE_LOOP only
            isEndOfThisMonotone: false
        };

        this.currentVertex = {
            points: null,
            colors: null,
            drawingType: Program.TRIANGLE_FAN
        };

        this.pseudocode = {
            main: null,
            sub: null,
            indicesMain: [],
            indicesSub: []
        };

        this.monotone = {
            points: null,
            colors: null,
            drawingType: Program.LINE_LOOP
        };

        this.isLast = false;

        this.ID = SnapShot.IDStatic++;
    }

    /**
     * @param {SnapShot} last
     */

    static getLastSnapshot( last ) {
        let final = new SnapShot();

        final.polygons = last.polygons;
        final.diagonals = last.diagonals;
        final.pseudocode = last.pseudocode;
        final.isLast = true;

        return final;
    }

    /**
     * @param {Float32Array} polygons
     * @param {Float32Array} colors
     */

    addPolygons( polygons, colors ) {
        console.assert( !this.polygons.points );
        console.assert( !this.polygons.colors );
        this.polygons.points = polygons;
        this.polygons.colors = colors;
    }

    /**
     * @param {Float32Array} diagonals
     * @param {Float32Array} colors
     */

    addDiagonals( diagonals, colors ) {
        console.assert( diagonals !== null, diagonals );
        console.assert( colors !== null, colors );
        this.diagonals.points.push( diagonals );
        this.diagonals.colors.push( colors );
        this.diagonals.drawingType.push( Program.LINE_LOOP );
    }

    /**
     * @param {Float32Array} sweepLines
     * @param {Float32Array} colors
     */

    addSweep( sweepLines, colors ) {
        console.assert( !this.sweepLines.points );
        console.assert( !this.sweepLines.colors );
        this.sweepLines.points = sweepLines;
        this.sweepLines.colors = colors;
    }

    addSweepAtTop() {
        this.addSweep( SnapShot.getSweepAtTopPoints(), new Float32Array( [].concat( Drawer.DeepSkyBlue, Drawer.DeepSkyBlue ) ) );
    }

    static getSweepAtTopPoints() {
        return new Float32Array( [ -1, 1.1, 1, 1.1 ] );
    }

    /**
     * @param {Boolean} val
     */

    setIsANewMono( val ) {
        this.sweepLines.isNewMonotone = val;
    }

    /**
     * @param {Float32Array} vertex
     * @param {Float32Array} colors
     */

    __addStack( vertex, colors ) {
        console.assert( vertex !== null );
        console.assert( colors !== null );
        this.stackPoints.points.push( vertex );
        this.stackPoints.colors.push( colors );
        this.stackPoints.drawingType.push( Program.TRIANGLE_FAN );
    }

    /**
     * @param {[HalfEdge]} vertices
     */

    addStack( vertices ) {
        // console.log( vertices );
        vertices.forEach( edge => {
            let vertex = edge.origin;
            let normalizedVertex = new Vertex( vertex.x / Main.main.originalWidth, vertex.y / Main.main.originalHeight );

            // get drawing points of the circle
            let { points, colors } = new Circle( {
                center: normalizedVertex,
                radius: 0.02,
                color: vertex.isLeftChainVertex ? MonotoneVertex.LEFT_CHAIN_VERTEX_COLOR : MonotoneVertex.RIGHT_CHAIN_VERTEX_COLOR
            } ).getPoints();
            this.__addStack( new Float32Array( points ), new Float32Array( colors ) );
        } );

        return this;
    }

    /**
     * @param {Float32Array} vertex
     * @param {Float32Array} colors
     */

    addCurrentVertex( vertex, colors ) {
        console.assert( vertex != null );
        console.assert( colors != null );
        this.currentVertex.points = vertex;
        this.currentVertex.colors = colors;
    }

    /**
     * @param {Float32Array} vertex
     * @param {Float32Array} colors
     */

    addMonotone( vertex, colors ) {
        console.assert( vertex != null );
        console.assert( colors != null );
        this.monotone.points = vertex;
        this.monotone.colors = colors;
    }

    /**
     * @param {Number} i
     */

    addMainPseIndices( ...i ) {
        this.pseudocode.indicesMain = this.pseudocode.indicesMain.concat( i );
    }

    /**
     * @param {Number} i
     */

    addSubPseIndices( ...i ) {
        this.pseudocode.indicesSub = this.pseudocode.indicesSub.concat( i );
    }

    /**
     * @param {HTMLElement} element
     */

    setMainPse( element ) {
        this.pseudocode.main = element;
    }

    /**
     * @param {HTMLElement} element
     */

    setSubPse( element ) {
        this.pseudocode.sub = element;
    }

    __hidden() {
        if ( this.pseudocode.main != null )
            this.pseudocode.main.style.display = "none";

        if ( this.pseudocode.sub != null )
            this.pseudocode.sub.style.display = "none";

        if ( !this.pseudocode.indicesMain.isEmpty() )
            SnapShot.__leavePse( this.pseudocode.main.children[ this.pseudocode.indicesMain.getLast() ] );

        if ( !this.pseudocode.indicesSub.isEmpty() )
            SnapShot.__leavePse( this.pseudocode.sub.children[ this.pseudocode.indicesSub.getLast() ] );
    }

    /**
     * @param {HTMLElement} element
     */

    static __selectPse( element ) {
        element.style.backgroundColor = "#AAAAAA";
        element.style.color = "#FFFFFF";
    }

    /**
     * @param {HTMLElement} element
     */

    static __leavePse( element ) {
        element.style.backgroundColor = "#FFFFFF";
        element.style.color = "#000000";
    }

    /**
     * @param {Number} i
     * @param {[HTMLElement]} elements
     */

    static __select( i, elements ) {
        if ( i > 0 ) SnapShot.__leavePse( elements[ i - 1 ] );
        console.assert( elements[ i ], i );
        SnapShot.__selectPse( elements[ i ] );

        return i + 1 < elements.length;
    }

    /**
     * animation pseudocode
     */

    highlightPse() {
        // console.log( this );

        let main = Main.main;

        // hide previous
        if ( main.preSnapshot != null )
            main.preSnapshot.__hidden();

        this.pseudocode.main.style.display = "block";

        // which main code to be highlighted
        let elements = [];
        this.pseudocode.indicesMain.forEach( i => elements.push( this.pseudocode.main.children[ i ] ) );

        // which sub code to be highlighted
        if ( this.pseudocode.sub != null ) {
            this.pseudocode.sub.style.display = "block";
            console.assert( !this.pseudocode.indicesSub.isEmpty() );
            this.pseudocode.indicesSub.forEach( i => elements.push( this.pseudocode.sub.children[ i ] ) );
        }

        SnapShot.selectAnimation( elements );
    }

    /**
     * @param {[HTMLElement]} elements
     */

    static selectAnimation = function ( elements ) {
        if ( elements.isEmpty() ) return;

        let i = 0;
        let time = ( SnapShot.animationTime * 4 ) / elements.length;
        time = time < 200 ? 200 : time; // min: 200 ms
        time = time > 400 ? 400 : time // max: 400 ms

        // closure
        function start() {
            return new Promise( function ( resolve, reject ) {
                resolve = next;
                reject = console.log;
                SnapShot.__select( i++, elements ) ? setTimeout( resolve, time ) : reject( "select done" );
            } );
        }

        function next() {
            return new Promise( function ( resolve, reject ) {
                resolve = start;
                reject = console.log;
                SnapShot.__select( i++, elements ) ? setTimeout( resolve, time ) : reject( "select done" );
            } );
        }

        start().then( null, null );
    }

    /**
     * push all previous diagonal data
     *
     * @param {Number} len
     * @param {Stack} snapshotsCurrent
     */

    static #draw( len, snapshotsCurrent ) {

        for ( let i = 0; i < len; i++ ) {
            let snapshot = snapshotsCurrent.array[ i ];
            // console.log( snapshot );
            console.assert( snapshot.diagonals.points.length === snapshot.diagonals.colors.length );
            for ( let j = 0; j < snapshot.diagonals.points.length; j++ ) {
                Main.main.pushData( snapshot.diagonals.points[ j ], snapshot.diagonals.colors[ j ], snapshot.diagonals.drawingType[ j ] );
            }
        }
    }

    // polygons -> pre dia -> pre vertices in stack -> sweep -> current vertex -> current dia
    draw() {
        // console.assert( this.current.length === this.currentAnimations.length );
        // console.log( this );

        // reset drawing data
        Main.main.resetDrawingData();

        // push original polygon's drawing data
        Main.main.pushData( this.polygons.points, this.polygons.colors, this.polygons.drawingType );

        // animate pseudocode
        this.highlightPse();

        console.assert( this.polygons.points );
        // draw the original polygon
        if ( Main.main.preSnapshot === null ) {
            console.assert( this.diagonals.points.isEmpty() && this.diagonals.colors.isEmpty() );
            Main.main.draw();
            return;
        }

        // draw last step
        if ( this.isLast ) {
            // first push monotone's diagonals
            SnapShot.#draw( Main.main.snapshotsCurrent.array.length, Main.main.snapshotsCurrent );

            // then push triangulation's diagonals
            SnapShot.#draw( Main.main.snapshotsCurrentTri.array.length, Main.main.snapshotsCurrentTri );

            Main.main.draw();
            return;
        }

        // draw the monotone polygon being triangulating
        if ( this.monotone.points != null ) {
            Main.main.pushData( this.monotone.points, this.monotone.colors, this.monotone.drawingType );
        }

        // draw previous stack status
        console.assert( Main.main.preSnapshot.stackPoints.points.length === Main.main.preSnapshot.stackPoints.colors.length );
        for ( let i = 0; i < this.stackPoints.points.length; i++ ) {
            Main.main.pushData( this.stackPoints.points[ i ], this.stackPoints.colors[ i ], this.stackPoints.drawingType[ i ] );
        }

        // draw previous diagonals
        // note that we need to ignore duplicate animation.
        // i.e. avoid re-drawing this snapshot twice
        // first push monotone's diagonals
        let len = Main.main.snapshotsCurrentTri.array.isEmpty() ? Main.main.snapshotsCurrent.array.length - 1 : Main.main.snapshotsCurrent.array.length;
        SnapShot.#draw( len, Main.main.snapshotsCurrent );

        // then push triangulation's diagonals
        len = Main.main.snapshotsCurrentTri.array.length - 1;
        SnapShot.#draw( len, Main.main.snapshotsCurrentTri );

        // draw previous vertices int the stack
        console.assert( this.sweepLines.points );
        console.assert( Main.main.preSnapshot !== null );

        // sweep line points and colors
        let sweepLines = this.sweepLines.isNewMonotone ? [ SnapShot.getSweepAtTopPoints(), Main.main.currentSnapshot.sweepLines.points ] : [ Main.main.preSnapshot.sweepLines.points, Main.main.currentSnapshot.sweepLines.points ];
        let sweepLinesColors = [ Main.main.preSnapshot.sweepLines.colors, Main.main.currentSnapshot.sweepLines.colors ];

        // add initializing status of sweep line to drawing data
        for ( let i = 0; i < sweepLines.length; i += 2 ) {
            let linePre = sweepLines[ i ];
            let startPre = new THREE.Vector3( linePre[ 0 ], linePre[ 1 ], 0 );
            let endPre = new THREE.Vector3( linePre[ 2 ], linePre[ 3 ], 0 );

            let points = new Float32Array( [ startPre.x, startPre.y, endPre.x, endPre.y ] );
            Main.main.pushData( points, sweepLinesColors[ i + 1 ], this.sweepLines.drawingType );
        }

        // reset initializing time
        Main.main.initializingDate = new Date();

        // draw current vertex
        let animateCurrentVertex = SnapShot.animateSweep( sweepLines, sweepLinesColors ).then( () => {
            Main.main.pushData( Main.main.currentSnapshot.currentVertex.points, Main.main.currentSnapshot.currentVertex.colors, Main.main.currentSnapshot.currentVertex.drawingType );
            Main.main.drawer.draw( Main.main.allDrawingPoints, Main.main.allDrawingColors, Main.main.allDrawingTypes );
        } );

        // animate diagonals
        let animateCurrentStack = animateCurrentVertex.then( SnapShot.animateDiagonals );
    }

    static animateDiagonals() {
        let diagonals = Main.main.currentSnapshot.diagonals;
        // Main.main.drawer.drawLines( Main.main.allDrawingPoints, Main.main.allDrawingColors );
        // animate current diagonal
        if ( diagonals.points.isEmpty() ) {
            console.assert( diagonals.colors.isEmpty() );
            // console.log( "no dia " );
            return;
        }

        // add initializing status of these diagonals to drawing data
        console.assert( diagonals.points.length === diagonals.colors.length );
        for ( let i = 0; i < diagonals.points.length; i++ ) {
            let diagonal = diagonals.points[ i ];
            let start = new THREE.Vector3( diagonal[ 0 ], diagonal[ 1 ], 0 );
            let middle = new THREE.Vector3( diagonal[ 0 ], diagonal[ 1 ], 0 );

            Main.main.pushData( new Float32Array( [ start.x, start.y, middle.x, middle.y ] ), diagonals.colors[ i ], 2 );
        }

        // reset initializing time
        Main.main.initializingDate = new Date();
        Lines.animateByPoint( diagonals.points, diagonals.colors );
    }

    // animate sweep line
    static animateSweep( sweepLines, sweepLinesColors ) {
        return new Promise( function ( resolve, reject ) {
            // give enough time for the current animation to finish,
            // or otherwise will cause double animations for the next one
            Lines.animateByLine( sweepLines, sweepLinesColors );
            setTimeout( resolve, SnapShot.animationTime + 50 );
        } );
    }

    /**
     * @param {Vertex} vertex
     */

    static addSnapshot( vertex ) {
        let snapshot = new SnapShot();
        snapshot.addPolygons( Main.main.drawer.polygonsPoints, Main.main.drawer.polygonsColors );
        snapshot.addSweep( new Float32Array( [ -1, vertex.y / Main.main.originalHeight, 1, vertex.y / Main.main.originalHeight ] ), new Float32Array( [].concat( Drawer.DeepSkyBlue, Drawer.DeepSkyBlue ) ) );

        let normalizedVertex = new Vertex( vertex.x / Main.main.originalWidth, vertex.y / Main.main.originalHeight );
        // get drawing points of the circle
        let { points, colors } = new Circle( {
            center: normalizedVertex,
            radius: 0.02,
            color: Vertex.NORMAL_COLOR
        } ).getPoints();
        snapshot.addCurrentVertex( new Float32Array( points ), new Float32Array( colors ) );

        Main.main.snapshots.push( snapshot );
        return snapshot;
    }
}