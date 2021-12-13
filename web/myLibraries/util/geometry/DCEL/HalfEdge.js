"use strict"

/*
 * HalfEdge.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Vectors from "../tools/Vectors.js";
import DCEL from "./DCEL.js";
import Face from "./Face.js";
import CompareElement from "../../CompareElement.js";
import Drawer from "../../../GUI/geometry/Drawer.js";
import Main from "../../../../finalProject/JavaScript/Main.js";

/**
 * Data structure of halfEdge for DCEL
 *
 * The source code in java is from my own github:
 * @see <a href=>https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/DCEL/HalfEdge.java</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class HalfEdge {
    static IDStatic = 0;

    /**
     * Constructs to create an instance of HalfEdge
     * */

    constructor( paras ) {
        this.origin = CompareElement.chooseWhich( paras.origin, null );
        this.incidentFace = CompareElement.chooseWhich( paras.incidentFace, null );
        this.ID = CompareElement.chooseWhich( paras.ID, HalfEdge.IDStatic-- );
        this.twin = null;
        this.next = null;
        this.prev = null;
    }


    /**
     * sort by Y
     *
     * @param {HalfEdge} edge1
     * @param {HalfEdge} edge2
     * */

    static sort( edge1, edge2 ) {
        return Vectors.sortByY( edge1.origin, edge2.origin );
    }

    /**
     * connect two half edges
     *
     * @param {HalfEdge} first      next to the second halfEdge
     * @param {HalfEdge} second     previous to the second halfEdge
     * */

    static connect( first, second ) {
        first.prev = second;
        second.next = first;
    }

    /**
     * set twins for two twin half edges
     *
     * @param {HalfEdge} first
     * @param {HalfEdge} second
     * */

    static setTwins( first, second ) {
        first.twin = second;
        second.twin = first;
    }

    /**
     * left is already connected to right?
     *
     * @param {Vertex} left
     * @param {Vertex} right
     * */

    static isAlreadyConnected( left, right ) {
        const incidentEdges = DCEL.allIncidentEdges( left );
        for ( let edge of incidentEdges ) {
            if ( edge.origin.equals( right ) ) {
                console.assert( edge.twin.origin.equals( left ) );
                return true;
            }
        }

        return false;
    }

    /**
     * connect a split or merge vertex to a helper,
     * and add an internal diagonal.
     *
     * @param {Vertex} left
     * @param {Vertex} right
     * @param {[Face]} faces
     * */

    static connectHelper( left, right, faces ) {
        if ( this.isAlreadyConnected( left, right ) ) return;
        let { points, colors } = Drawer.drawLines( left, right );
        Main.main.snapshots.getLast().addDiagonals( new Float32Array( points ), new Float32Array( colors ) );
        // Drawer.addDrawingPoints( left, right );

        // create two new halfEdges,
        // newer and older,
        // connecting left and right
        let newer = new HalfEdge( { origin: right } );
        let older = new HalfEdge( { origin: left } );
        this.setTwins( newer, older );
        let another = new Face( newer );
        // add the newly created face
        faces.push( another );

        // connect newer with left as its destination and
        // its first counter-clockwise edge with left as its origin
        this.connect( DCEL.firstClockWiseEdge( left, right ), newer );
        // connect older with left as its origin and
        // its first clockwise edge with left as its destination
        this.connect( older, DCEL.firstCounterClockWiseEdge( left, right ) );

        // connect newer with right as its destination and
        // its first counter-clockwise edge with right as its origin
        this.connect( DCEL.firstClockWiseEdge( right, left ), older );
        // connect older with right as its origin and
        // its first clockwise edge with right as its destination
        this.connect( newer, DCEL.firstCounterClockWiseEdge( right, left ) );

        // set the newer's incident edge to a new one.
        DCEL.resetIncidentFace( newer, another );
        // set the older's incident edge to the old one.
        console.assert( older.next.incidentFace === older.prev.incidentFace );
        older.incidentFace = older.next.incidentFace;
        console.assert( DCEL.walkAroundEdge( older ) != null );
        // set the older face's outComponent
        older.incidentFace.outComponent = older;
        // console.log( older );
        // console.log( newer.toString() );
        console.assert( older.incidentFace !== newer.incidentFace, older + " " + newer.toString() );
    }

    /**
     * are both vertices of the two halfEdges
     * on the same monotone chain?
     *
     * @param {HalfEdge} edge
     * */

    isOnTheDifferentChain( edge ) {
        return this.origin.isOnTheDifferentChain( edge.origin );
    }

    toStringNormal() {
        return "[ origin:" + this.origin + "|" + "twin:" + this.twin + " ]";
    }

    toString() {
        return this.origin.toString();
    }
}
