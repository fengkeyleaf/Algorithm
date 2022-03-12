package myLibraries.util.geometry.DCEL;

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

import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.Vector;
import myLibraries.util.geometry.Triangles;
import myLibraries.util.geometry.Vectors;

import java.util.List;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class HalfEdges {

    // TODO: 2/11/2022 refine code for connect(), too many of them.

    /**
     * connect vertex left and vertex, edge.origin,
     * but we first need to know about which half-edge is the newer one?
     * Based on the visualization below.
     *
     * @param p1 point inside the face of either edge.incidentFace or edge.twin.incidentFace
     * @param p2 point inside the face of edge.twin.incidentFace or either edge.incidentFace
     * */

    public static
    void connect( Vertex left, HalfEdge edge, Vector p1, Vector p2 ) {
        // edge.origin => right, edge => newer
        if ( Triangles.toLeft( edge.origin, left, p1 ) ) {
            HalfEdges.connect( left, edge.origin, edge );
            return;
        }

        // edge.origin => right, edge.twin => newer
        assert Triangles.toLeft( edge.origin, left, p2 );
        HalfEdges.connect( left, edge.origin, edge.twin );
    }

    /**
     * connect vertex left and vertex right. Connect ray for Voronoi.
     *
     *  clockN = DCEL.firstClockWiseEdge( left, right );
     *  c-clockO = DCEL.firstCounterClockWiseEdge( left, right );
     *
     *  Visualization:
     *
     *      clockN           c-clockO
     * <-------------- l <---------------
     *                ^|
     *                ||
     *            new || older
     *                ||
     *                |v
     * --------------> r --------------->
     *
     * @param left must have at least one in-coming edge and one out-coming edge
     * @param right must have at least one in-coming edge and one out-coming edge
     * @param newer may have no origin vertex, its twin is the older
     * */

    private static
    void connect( Vertex left, Vertex right, HalfEdge newer ) {
        HalfEdge older = newer.twin;

        connectLeft( left, right, newer, newer.twin );
        if ( newer.origin == null ) newer.origin = right;
        if ( older.origin == null ) older.origin = left;

        assert newer.origin == right;
        assert older.origin == left : older + " " + newer + " " + left;
    }

    /**
     * connect vertex left and vertex right,
     * but we first need to know about which half-edge is the newer one?
     * Based on the visualization below.
     *
     * @param p1 point inside the face of either edge.incidentFace or edge.twin.incidentFace
     * @param p2 point inside the face of edge.twin.incidentFace or either edge.incidentFace
     * */

    public static
    void connect( Vertex left, Vertex right,
                  HalfEdge edge, Vector p1, Vector p2 ) {
        // edge => newer, edge.twin => older
        if ( Triangles.toLeft( right, left, p1 ) ) {
            HalfEdges.connect( left, right, edge, edge.twin );
            return;
        }

        // edge.twin => newer, edge => older
        assert Triangles.toLeft( right, left, p2 );
        HalfEdges.connect( left, right, edge.twin, edge );
    }

    /**
     * connect line for Voronoi.
     *
     *  clockN = DCEL.firstClockWiseEdge( left, right );
     *  c-clockO = DCEL.firstCounterClockWiseEdge( left, right );
     *  clockO = DCEL.firstClockWiseEdge( right, left );
     *  c-clockN = DCEL.firstCounterClockWiseEdge( right, left );
     *
     *  Visualization:
     *
     *      clockN           c-clockO
     * <-------------- l <---------------
     *                ^|
     *                ||
     *            new || older
     *                ||
     *                |v
     * --------------> r --------------->
     *     c-clockN         clockO
     *
     * @param left must have at least one in-coming edge and one out-coming edge
     * @param right must have at least one in-coming edge and one out-coming edge
     * @param newer may have no origin vertex, its twin is the older
     * @param older may have no origin vertex, its twin is the newer
     * */

    public static
    void connect( Vertex left, Vertex right, HalfEdge newer, HalfEdge older ) {
        connectLeft( left, right, newer, older );
        if ( newer.origin == null ) newer.origin = right;

        connectRight( left, right, newer, older );
        if ( older.origin == null ) older.origin = left;

        assert newer.origin == right;
        assert older.origin == left : older + " " + newer + " " + left;

        assert newer.incidentFace != older.incidentFace;
        // set the newer's incident edge to a new one.
        DCEL.resetIncidentFace( newer, newer.incidentFace );
        // set the older's incident edge to the old one.
        DCEL.resetIncidentFace( older, older.incidentFace );
        assert older.incidentFace != newer.incidentFace : older + " " + newer;
    }

    /**
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
     *  another   new || older
     *                ||
     *                |v
     * --------------> r --------------->
     *  c-clockN             clockO
     *
     * @param left origin has been assigned
     * @param right origin has been assigned
     * */

    public static
    void connect( Vertex left, Vertex right, HalfEdge newer, HalfEdge older, Face another ) {
        connectLeft( left, right, newer, older );
        connectRight( left, right, newer, older );

        assert newer.origin == right;
        assert older.origin == left : older + " " + newer + " " + left;

        // set the newer's incident edge to a new one.
        DCEL.resetIncidentFace( newer, another );
        // set the older's incident edge to the old one.
        assert older.next.incidentFace == older.prev.incidentFace;
        older.incidentFace = older.next.incidentFace;
        assert DCEL.walkAroundEdge( older ) != null;
        // set the older face's outComponent
        older.incidentFace.outComponent = older;
        assert older.incidentFace != newer.incidentFace : older + " " + newer;
    }

    /**
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
     *             new || older
     *                 ||
     *                 |v
     *                 r
     *
     * */

    private static
    void connectLeft( Vertex left, Vertex right, HalfEdge newer, HalfEdge older ) {
        // connect newer with left as its destination to
        // its first counter-clockwise edge with left as its origin
        DCEL.firstClockWiseEdge( left, right ).connect( newer );
        // connect older with left as its origin to
        // its first clockwise edge with left as its destination
        older.connect( DCEL.firstCounterClockWiseEdge( left, right ) );
    }

    /**
     *  clockO = DCEL.firstClockWiseEdge( right, left );
     *  c-clockN = DCEL.firstCounterClockWiseEdge( right, left );
     *
     *  Visualization:
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
     * */

    private static
    void connectRight( Vertex left, Vertex right, HalfEdge newer, HalfEdge older ) {
        // connect older with right as its destination to
        // its first counter-clockwise edge with right as its origin
        DCEL.firstClockWiseEdge( right, left ).connect( older );
        // connect newer with right as its origin to
        // its first clockwise edge with right as its destination
        newer.connect( DCEL.firstCounterClockWiseEdge( right, left ) );
    }

    /**
     * connect a split or merge vertex to a helper,
     * and add an internal diagonal.
     * */

    public static
    void connectHelper( Vertex left, Vertex right, List<Face> faces ) {
        if ( left.isAlreadyConnected( right ) ) return;

        // create two new halfEdges,
        // newer and older,
        // connecting left and right
        HalfEdge newer = new HalfEdge( right );
        HalfEdge older = new HalfEdge( left );
        newer.setTwins( older );
        Face another = new Face( newer );
        // add the newly created face
        faces.add( another );

        connect( left, right, newer, older, another );
    }

    /**
     * add the edge with target and origin as its endpoints.
     * i.e. also connect origin to target
     *
     * @param origin newly initialized vertex
     * */

    public static
    void addEdge( Vertex target, Vertex origin ) {
        if ( origin.isAlreadyConnected( target ) ) return;

        // create two new halfEdges,
        // outDegree and inDegree,
        // connecting target and origin
        HalfEdge outDegree = new HalfEdge( origin );
        assert origin.incidentEdge == null;
        origin.incidentEdge = outDegree;
        HalfEdge inDegree = new HalfEdge( target );
        outDegree.setTwins( inDegree );
        outDegree.connect( inDegree );

        HalfEdge clockWise = DCEL.firstClockWiseEdge( target, origin );

        // connect outDegree to the first clockwise edge left with target as its origin vertex
        clockWise.connect( outDegree );
        // connect to inDegree the first counter-clockwise edge left with origin as its origin vertex
        HalfEdge counterClockWise = DCEL.firstCounterClockWiseEdge( target, origin );
        inDegree.connect( counterClockWise );

        assert clockWise.incidentFace == counterClockWise.incidentFace;
        outDegree.incidentFace = inDegree.incidentFace = clockWise.incidentFace;
        assert DCEL.walkAroundEdge( outDegree ) != null;
    }

    /**
     * split the edge into two parts,
     * which means the split point must be on the edge.
     *
     * Note that given edge must have next() and prev(),
     * they should be null. This is true for its twin edge as well.
     *
     * And after splitting, the edge's origin doesn't change and
     * its next' origin is the split vertex. e.g.
     *
     *            edge
     * <-------------------------- o
     *
     * after splitting:
     *
     *   newlyNext        edge
     * <----------- s ------------ o
     *
     * And if split point is already a vertex of one of the half-edges,
     * then return either of them. Otherwise, return the new split vertex.
     *
     * Further, it's recommended that to check
     * integrity of the two faces that the edge and its twin points to,
     * but this is not required in some cases, like Voronoi Diagram.
     * assert DCEL.walkAroundEdge( edge ) != null;
     * assert DCEL.walkAroundEdge( twin ) != null;
     *
     * @param edge edge which incidentFace is inner one, also returned value
     * @param split newly initialized vertex
     * @return Vertex of one of the half-edges if split point is already a vertex of one of them,
     *         Otherwise, return the new split vertex.
     * */

    public static
    Vertex split( HalfEdge edge, Vertex split ) {
        assert edge.next.origin == edge.twin.origin;

        if ( split.equals( edge.origin ) ||
                split.isAlreadyConnected( edge.origin ) ) return edge.origin;
        if ( split.equals( edge.next.origin ) ||
                split.isAlreadyConnected( edge.next.origin ) ) return edge.next.origin;

        assert new Line( edge.origin, edge.next.origin ).isOnThisSegment( split ) : edge.origin;

        HalfEdge twin = edge.twin;
        HalfEdge originalNext = edge.next;
        HalfEdge originalTwinNext = twin.next;
        HalfEdge newlyNext = new HalfEdge( split );
        HalfEdge newlyTwinNext = new HalfEdge( split );
        assert split.incidentEdge == null;
        split.incidentEdge = newlyNext;

        newlyNext.incidentFace = edge.incidentFace;
        newlyNext.connect( edge );
        originalNext.connect( newlyNext );

        newlyTwinNext.incidentFace = edge.twin.incidentFace;
        newlyTwinNext.connect( twin );
        originalTwinNext.connect( newlyTwinNext );

        edge.setTwins( newlyTwinNext );
        twin.setTwins( newlyNext );

        return split;
    }

    // TODO: 1/8/2022 two split() can be merged into this one
    public static
    Vertex split( HalfEdge edge, Vector split ) {
        return split( edge, new Vertex( split ) );
    }

    /**
     * delete the give edge from its DCEL
     *
     * Note that given edge must have next() and prev(),
     * they should be null. This is true for its twin edge as well.
     * */

    public static
    Face[] deleteEdge( HalfEdge edge ) {
        HalfEdge twin = edge.twin;
        Vertex origin = edge.origin;
        Vertex twinOrigin = twin.origin;
        Face[] faces = new Face[] { edge.twin.incidentFace, edge.incidentFace};

        if ( origin.incidentEdge == edge ) {
            assert twin.next.origin == origin;
            origin.incidentEdge = twin.next;
        }

        if ( twinOrigin.incidentEdge == twin ) {
            assert edge.next.origin == twinOrigin;
            twinOrigin.incidentEdge = edge.next;
        }

        twin.next.connect( edge.prev );
        edge.next.connect( twin.prev );

        Face face = twin.next.incidentFace;
        face.outComponent = edge.next;
        DCEL.resetIncidentFace( edge.next, face );

        assert DCEL.walkAroundEdge( edge.next ) != null;
        // f[0] remained one; f[1] deleted one
        return faces;
    }

    /**
     * sort by Y
     * */

    public static
    int sort( HalfEdge edge1, HalfEdge edge2 ) {
        return Vectors.sortByY( edge1.origin, edge2.origin );
    }
}
