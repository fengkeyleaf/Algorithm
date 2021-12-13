"use strict"

/*
 * Polygons.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Lines from "./Lines.js";
import Triangles from "./Triangles.js";
import Face from "../DCEL/Face.js";
import HalfEdge from "../DCEL/HalfEdge.js";
import MonotonePolygons from "./MonotonePolygons.js";

/**
 * This class consists exclusively of static methods
 * that related to Polygon
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Polygons {

    /**
     * remove Points On The Same Line
     */

    static removePointsOnTheSameLine( vertices ) {
        let filtered = [];
        filtered.push( vertices[ 0 ] );

        // for i-th vertex, check its predecessor and successor
        // to see if they are on the same line.
        // the reason why we can do this is that
        // the medium point is the one that is redundant,
        // if three consecutive points are on the same line.
        for ( let i = 1; i < vertices.length - 1; i++ ) {
            // if yes, ignore this vertex;
            // if no, keep this vertex
            if ( !Lines.isOnTheSameLine( vertices[ i - 1 ], vertices[ i ], vertices[ i + 1 ] ) )
                filtered.push( vertices[ i ] );
        }

        filtered.push( vertices[ vertices.length - 1 ] );
        return filtered;
    }

    /**
     * the given point lies "inside" which face?
     * */

    static inWhichFace( polygons, point ) {
        for ( let polygon in polygons ) {
            if ( this.isInsideThisPolygon( polygon, point ) )
                return polygon;
        }

        return null;
    }

    /**
     * the given point lies "on" which face?
     * */

    static OnWhichFace( polygons, point ) {
        for ( let polygon in polygons ) {
            if ( this.isOnThisPolygon( polygon, point ) )
                return polygon;
        }

        return null;
    }

    /**
     * is the point inside This Polygon?
     * */

    static isInsideThisPolygon( polygon, point ) {
        if ( polygon.outComponent == null ) return false;

        let edge = polygon.outComponent;
        do {
            console.assert( edge.next != null, edge );
            if ( !Triangles.toLeftRigorously( edge.origin, edge.next.origin, point ) )
                return false;

            console.assert( edge.incidentFace === polygon.outComponent.incidentFace );
            console.assert( edge.next != null, edge );
            edge = edge.next;
        } while ( edge !== polygon.outComponent );

        return true;
    }

    /**
     * is the point On This Polygon?
     * */

    static isOnThisPolygon( polygon, point ) {
        if ( polygon.outComponent == null ) return false;

        let edge = polygon.outComponent;
        do {
            if ( !Triangles.toLeft( edge.origin, edge.next.origin, point ) )
                return false;

            console.assert( edge.incidentFace === polygon.outComponent.incidentFace );
            edge = edge.next;
        } while ( edge !== polygon.outComponent );

        return true;
    }

    /**
     * get the DCEL for the polygon
     * representing by counter-clock-wise vertices
     *
     * @return  [faceOutside(infinite face), faceInner][
     * */

// TODO: 7/14/2021 not support complex polygons
    static getDCEL( vertices ) {
        if ( vertices == null || vertices.length < 3 ) return null;
        let len = vertices.length;

        // two faces, one inside the polygon, the other outside of it.
        let faceInner = new Face();
        let faceOutside = new Face();
        // two edge list, one going counter-clock wise, the other going the opposite.
        const counterClockWiseEdges = [];
        const clockWiseEdges = [];

        // from the first vertex to the last one, i-th vertex:
        for ( let i = 0; i < len; i++ ) {
            // create twin edges
            let edge1 = new HalfEdge( { origin: vertices[ i ], incidentFace: faceInner } );
            vertices[ i ].incidentEdge = edge1;
            counterClockWiseEdges.push( edge1 );
            let edge2 = new HalfEdge( { origin: vertices[ ( i + 1 ) % len ], incidentFace: faceOutside } );
            clockWiseEdges.push( edge2 );
            HalfEdge.setTwins( edge1, edge2 );

            // add next and prev for the two halfEdges, i and i - 1, separately.
            if ( i > 0 ) {
                HalfEdge.connect( counterClockWiseEdges[ i ],
                    counterClockWiseEdges[ i - 1 ] );
                HalfEdge.connect( clockWiseEdges[ i - 1 ],
                    clockWiseEdges[ i ] );
            }
        }

        // add next and prev for the first and last halfEdges.
        HalfEdge.connect( counterClockWiseEdges[ 0 ],
            counterClockWiseEdges[ counterClockWiseEdges.length - 1 ] );
        HalfEdge.connect( clockWiseEdges[ clockWiseEdges.length - 1 ],
            clockWiseEdges[ 0 ] );
        // set outComponent and innerComponent.
        faceInner.outComponent = counterClockWiseEdges[ 0 ];
        faceOutside.innerComponents.push( clockWiseEdges[ 0 ] );

        // faceOutside = infinite face
        return [ faceOutside, faceInner ];
    }

    /**
     * is the point Inside Polygon?
     * only used for partitioning monotone polygons
     *
     * @param base    the vertex of the polygon
     * @param prev    the previous one of the base
     * @param next    the next one of the base
     * */

    static isInsidePolygon( point, base,
                            prev, next ) {
        return Triangles.toLeftRigorously( prev, base, point ) &&
            Triangles.toLeftRigorously( base, next, point );
    }
}
