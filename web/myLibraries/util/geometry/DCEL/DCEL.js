"use strict"

/*
 * DCEL.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Triangles from "../tools/Triangles.js";
import HalfEdge from "./HalfEdge.js";
import Vertex from "./Vertex.js";
import Face from "./Face.js";
import CompareElement from "../../CompareElement.js";

/**
 * This class consists exclusively of static methods
 * that related to DCEL, Doubly-Connected Edge List
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/DCEL/DCEL.java>DCEL</a>
 *
 * The source code in java is from my own github:
 * @see <a href=></a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class DCEL {

    /**
     * find the first ClockWise Edge
     * with two vertices destination and origin
     *
     * for this one, we will find the edge with maximum clockwise angle,
     * i.e. minimum counter-clockwise angle
     *
     * @param {Vertex} destination
     * @param {Vertex} origin
     * */

    static firstClockWiseEdge( destination, origin ) {
        let edge = destination.incidentEdge;
        let first = null;

        do {
            if ( first == null ) {
                first = edge;
                edge = edge.twin.next;
                continue;
            }

            // found smaller angle in clock wise ordering
            if ( Triangles.clockWiseAngleCompareTo( destination, origin,
                first.next.origin, edge.next.origin ) > 0 )
                first = edge;

            edge = edge.twin.next;
        } while ( edge !== destination.incidentEdge );

        return first;
    }

    /**
     * find the first CounterClockWise Edge
     * with two vertices origin and destination
     *
     * for this one, we will find the edge with minimum clockwise angle
     *
     * @param {Vertex} origin
     * @param {Vertex} destination
     * */

    static firstCounterClockWiseEdge( origin, destination ) {
        let edge = origin.incidentEdge.twin;
        let first = null;

        do {
            if ( first == null ) {
                first = edge;
                edge = edge.next.twin;
                continue;
            }

            // found smaller angle in counter-clock wise ordering
            if ( Triangles.clockWiseAngleCompareTo( origin, destination,
                first.origin, edge.origin ) < 0 )
                first = edge;

            edge = edge.next.twin;
        } while ( edge !== origin.incidentEdge.twin );

        return first;
    }

    /**
     * reset IncidentEdge
     *
     * @param {HalfEdge} start
     * @param {Face} face
     * */

    static resetIncidentFace( start, face ) {
        let edge = start;
        do {
            edge.incidentFace = face;
            edge = edge.next;
        } while ( edge !== start );
    }

    /**
     * walk around all halfEdges, starting at innerComponent
     *
     * @param {HalfEdge} outComponent
     * */

    static walkAroundEdge( outComponent ) {
        let edges = [];
        let edge = outComponent;
        do {
            edges.push( edge );
            console.assert( edge.incidentFace === outComponent.incidentFace, outComponent.origin );
            edge = edge.next;
        } while ( edge !== outComponent );

        return edges;
    }

    /**
     * walk around all halfEdges, starting at face
     * and get visited halfEdges
     *
     * @param {Face} face
     * */

    static walkAroundEdgeFace( face ) {
        return this.walkAroundEdge( face.outComponent );
    }

    static walkAroundVertex( outComponent ) {
        let vertices = [];
        let edge = outComponent;
        do {
            vertices.push( edge.origin );
            console.assert( edge.incidentFace === outComponent.incidentFace, "Error" );
            edge = edge.next;
        } while ( edge !== outComponent );

        return vertices;
    }

    /**
     * walk around all halfEdges, starting at face
     * and get visited vertices
     *
     * @param {Face} face
     * */

    static walkAroundVertexFace( face ) {
        return this.walkAroundVertex( face.outComponent );
    }

    /**
     * get all incident edges of the vertex
     *
     * @param {Vertex} vertex
     * */

    static allIncidentEdges( vertex ) {
        const edges = [];
        let edge = vertex.incidentEdge;
        do {
            console.assert( !CompareElement.isNullOrUndefined( edge ), vertex );
            edges.push( edge );
            edges.push( edge.twin );
            edge = edge.twin.next;
        } while ( edge !== vertex.incidentEdge );

        return edges;
    }

    static testCaseOne() {
        let ID = -1;
        let len = 6;
        let vertices = [];
        for ( let i = 0; i < len; i++ )
            vertices.push( new Vertex( ID, ID++ ) );

        let face1 = new Face();
        let face2 = new Face();
        let counterClockWiseEdges = [];
        let clockWiseEdges = [];

        for ( let i = 0; i < len; i++ ) {
            let edge1 = new HalfEdge( { origin: vertices[ i ], incidentFace: face1 } );
            vertices[ i ].incidentEdge = edge1;
            counterClockWiseEdges.push( edge1 );
            let edge2 = new HalfEdge( { origin: vertices[ ( i + 1 ) % len ], incidentFace: face2 } );
            clockWiseEdges.push( edge2 );
            edge1.twin = edge2;
            edge2.twin = edge1;

            if ( i > 0 ) {
//                counterClockWiseEdges.get( i ).prev = counterClockWiseEdges.get( i - 1 );
//                counterClockWiseEdges.get( i - 1 ).next = counterClockWiseEdges.get( i );
                HalfEdge.connect( counterClockWiseEdges[ i ], counterClockWiseEdges[ i - 1 ] );
//                clockWiseEdges.get( i - 1 ).prev = clockWiseEdges.get( i );
//                clockWiseEdges.get( i ).next = clockWiseEdges.get( i - 1 );
                HalfEdge.connect( clockWiseEdges[ i - 1 ], clockWiseEdges[ i ] );
            }
        }
//        counterClockWiseEdges.get( 0 ).prev = counterClockWiseEdges.get( counterClockWiseEdges.size() - 1  );
//        counterClockWiseEdges.get( counterClockWiseEdges.size() - 1  ).next = counterClockWiseEdges.get( 0 );
        HalfEdge.connect( counterClockWiseEdges[ 0 ], counterClockWiseEdges[ counterClockWiseEdges.length - 1 ] );
//        clockWiseEdges.get( clockWiseEdges.size() - 1  ).prev = clockWiseEdges.get( 0 );
//        clockWiseEdges.get( 0 ).next = clockWiseEdges.get( clockWiseEdges.size() - 1  );
        HalfEdge.connect( clockWiseEdges[ clockWiseEdges.length - 1 ], clockWiseEdges[ 0 ] );

        face1.outComponent = counterClockWiseEdges[ 0 ];
        face2.innerComponents.push( clockWiseEdges[ 0 ] );

        console.log( this.walkAroundEdgeFace( face1 ) );

        vertices.forEach( vertex => console.log( this.allIncidentEdges( vertex ) ) );
    }
}

// DCEL.testCaseOne();
