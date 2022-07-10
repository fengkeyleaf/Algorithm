package com.fengkeyleaf.util.geom;

/*
 * HalfEdges.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/7/2022$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to connecting two vertices in DCEL.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

final class HalfEdges {

    /**
     * connect vertex left and vertex, edge.origin,
     * but we first need to know about which half-edge is the newer one?
     * Based on the visualization below.
     *
     * Visualization:
     *
     *   clockN             c-clockO
     * <-------------- l <---------------
     *                ^|
     *                ||
     *  p1          e || e.twin      p2
     *                ||
     *                |v
     * --------------> r --------------->
     *  c-clockN             clockO
     *
     * r := edge.origin
     *
     * @param p1 point inside the face of either edge.incidentFace or edge.twin.incidentFace
     * @param p2 point inside the face of edge.twin.incidentFace or either edge.incidentFace
     * @deprecated   Be careful with precision issue which can lead to unexpected error
     *               since this method use vector dot production to compute the angle between two vectors.
     * */

    // TODO: 4/7/2022 Voronoi limited
    @Deprecated
    static
    void connect( Vertex left, HalfEdge edge,
                  Vector p1, Vector p2 ) {

        // edge.origin => right, edge => newer
        if ( Triangles.toLeft( edge.origin, left, p1 ) ) {
            HalfEdges.connectSingle( left, edge.origin, edge );
            return;
        }

        // edge.origin => right, edge.twin => newer
        assert Triangles.toLeft( edge.origin, left, p2 );
        HalfEdges.connectSingle( left, edge.origin, edge.twin );
    }

    //----------------------------------------------------------------------------------

    /**
     * connect vertex left and vertex right,
     * but we first need to know about which half-edge is the newer one?
     * Based on the visualization below.
     *
     * Visualization:
     *
     *   clockN             c-clockO
     * <-------------- l <---------------
     *                ^|
     *                ||
     *  p1          e || e.twin      p2
     *                ||
     *                |v
     * --------------> r --------------->
     *  c-clockN             clockO
     *
     * @param p1 point inside the face of either edge.incidentFace or edge.twin.incidentFace
     * @param p2 point inside the face of edge.twin.incidentFace or either edge.incidentFace
     * @deprecated   Be careful with precision issue which can lead to unexpected error
     *               since this method use vector dot production to compute the angle between two vectors.
     * */

    // TODO: 4/7/2022 Voronoi limited
    @Deprecated
    static
    void connect( Vertex left, Vertex right,
                  HalfEdge edge, Vector p1, Vector p2 ) {

        // edge => newer, edge.twin => older
        if ( Triangles.toLeft( right, left, p1 ) ) {
            HalfEdges.connectBoth( left, right, edge );
            return;
        }

        // edge.twin => newer, edge => older
        assert Triangles.toLeft( right, left, p2 );
        HalfEdges.connectBoth( left, right, edge.twin );
    }

    /**
     * re-connect half-edges from both sides, see the visualization below.
     *
     * This method also has the following two features:
     * 1) Reset incidentFace for the half-edges involved.
     * 2) Set the origin of the half-edges, if they don't have one.
     *
     *  clockN = DCEL.firstClockWiseEdge( left, right );
     *  c-clockO = DCEL.firstCounterClockWiseEdge( left, right );
     *  clockO = DCEL.firstClockWiseEdge( right, left );
     *  c-clockN = DCEL.firstCounterClockWiseEdge( right, left );
     *
     *  Visualization:
     *
     *   clockN             c-clockO
     * <-------------- l <---------------
     *                ^|
     *                ||
     *  another     e || e.twin
     *                ||
     *                |v
     * --------------> r --------------->
     *  c-clockN             clockO
     *
     * @param left origin has been assigned
     * @param right origin has been assigned
     * @deprecated   Be careful with precision issue which can lead to unexpected error
     *               since this method use vector dot production to compute the angle between two vectors.
     * */

    @Deprecated
    static
    void connectBoth( Vertex left, Vertex right, HalfEdge e ) {
        connectSingle( left, right, e );
        if ( e.origin == null ) e.origin = right;
        connectSingle( right, left, e.twin );
        if ( e.twin.origin == null ) e.twin.origin = left;

        assert e.origin == right;
        assert e.twin.origin == left : e.twin + " " + e + " " + left;

        assert ( e.incidentFace == null || e.twin.incidentFace == null ) || e.incidentFace != e.twin.incidentFace;
        // set the newer's incident edge to a new one.
        e.resetIncidentFace( e.incidentFace );
        assert e.walkAroundEdge() != null;

        // set the older's incident edge to the old one.
        assert ( e.twin.next.incidentFace == null || e.twin.prev.incidentFace == null ) || e.twin.next.incidentFace == e.twin.prev.incidentFace;
        e.twin.resetIncidentFace( e.twin.next.incidentFace );
        assert e.twin.walkAroundEdge() != null;
        // set the older face's outComponent
        e.twin.incidentFace.outComponent = e.twin;
        assert e.twin.incidentFace != e.incidentFace : e.twin + " " + e + " | " + e.twin.incidentFace;
    }

    /**
     * re-connect half-edges from one side, see the visualization below.
     *
     * This method also has the following feature:
     * 1) Set the origin of the half-edges, if they don't have one.
     *
     * Note that this method will not reset incidentFace for the half-edges involved,
     * which is different from {@link HalfEdges#connectBoth(Vertex, Vertex, HalfEdge)}
     *
     *  clockN = DCEL.firstClockWiseEdge( left, right );
     *  c-clockO = DCEL.firstCounterClockWiseEdge( left, right );
     *
     *  Visualization:
     *
     *   clockN             c-clockO
     * <--------------- l <---------------
     *             ^   ^|    ^
     *             |   ||    |
     *             --> || <--
     *                 ||
     *               e || e.twin
     *                 ||
     *                 |v
     *                 r
     *
     * Or:
     *
     *                 l
     *                ^|
     *                ||
     *            new || older
     *                ||
     *            --> || <--
     *            |   ||   |
     *            V   |v   V
     * --------------> r --------------->
     *  c-clockN             clockO
     *
     * @deprecated   Be careful with precision issue which can lead to unexpected error
     *               since this method use vector dot production to compute the angle between two vectors.
     * */

    @Deprecated
    static
    void connectSingle( Vertex left, Vertex right, HalfEdge e ) {
        // connect newer with left as its destination to
        // its first counter-clockwise edge with left as its origin
        left.firstClockWiseEdge( right ).connect( e );
        // connect older with left as its origin to
        // its first clockwise edge with left as its destination
        e.twin.connect( left.firstCounterClockWiseEdge( right ) );

        if ( e.origin == null ) e.origin = right;
        if ( e.twin.origin == null ) e.twin.origin = left;

        assert e.origin == right : e.origin + " | " + right;
        assert e.twin.origin == left : e.twin + " " + e + " " + left;
    }

    /**
     * Sort half-edges in clock wise order with the point i as the center.
     *
     * @param    i center point.
     * @return   half-edges sorted in clock wise order.
     */

    static
    List<HalfEdge> sortInClockWise( List<HalfEdge> E, Vector i ) {
        if ( E == null || i == null || E.isEmpty() ) return null;

        // attach half-edge to vertex, and then sort vertex.
        List<HalfEdge> edges = new ArrayList<>( E.size() );
        Vectors.sortByAngleClockWise( i, edgesToVectors( E, i ) ).forEach(
                p -> edges.add( ( ( Vertex ) p ).incidentEdge )
        );
        return edges;
    }

    private static
    List<Vector> edgesToVectors( List<HalfEdge> E, Vector i ) {
        // attaching process.
        List<Vector> P = new ArrayList<>( E.size() );
        E.forEach( e -> {
            Vertex v = null;
            if ( e.origin.equalsXAndY( i ) )
                v = new Vertex( ( Vector ) e.twin.origin );
            else v = new Vertex( ( Vector ) e.origin );

            v.incidentEdge = e;
            P.add( v );
        } );

        return P;
    }
}
