package myLibraries.util.geometry.DCEL;

/*
 * HalfEdge.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 * JDK: 15
 */

import myLibraries.util.geometry.elements.line.Line;
import myLibraries.util.geometry.tools.Vectors;

import java.util.List;

/**
 * Data structure of halfEdge for DCEL
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class HalfEdge {
    private static int IDStatic = 0;
    public final int ID;
    public Vertex origin;
    public HalfEdge twin;
    public Face incidentFace;
    public HalfEdge next;
    public HalfEdge prev;

    /**
     * Constructs to create an instance of HalfEdge
     * */

    public HalfEdge( Vertex origin, Face incidentFace ) {
        this( origin, incidentFace, IDStatic++ );
    }

    public HalfEdge( Vertex origin, Face incidentFace, int ID ) {
        this.origin = origin;
        this.incidentFace = incidentFace;
        this.ID = ID;
    }

    public HalfEdge( Vertex origin ) {
        this( origin, null );
    }

    /**
     * sort by Y
     * */

    public static
    int sort( HalfEdge edge1, HalfEdge edge2 ) {
        return Vectors.sortByY( edge1.origin, edge2.origin );
    }

    /**
     * connect two half edges
     *
     * @param first      next to the second halfEdge
     * @param second     previous to the second halfEdge
     * */

    public static
    void connect( HalfEdge first, HalfEdge second ) {
        first.prev = second;
        second.next = first;
    }

    /**
     * set twins for two twin half edges
     * */

    public static
    void setTwins( HalfEdge first, HalfEdge second ) {
        first.twin = second;
        second.twin = first;
    }

    /**
     * left is already connected to right?
     * */

    public static
    boolean isAlreadyConnected( Vertex left, Vertex right ) {
        if ( left.incidentEdge == null ||
                right.incidentEdge == null ) return false;

        List<HalfEdge> incidentEdges = DCEL.allIncidentEdges( left );

        for ( HalfEdge edge : incidentEdges ) {
            if ( edge.origin.equals( right ) ) {
                assert edge.twin.origin.equals( left );
                return true;
            }
        }

        return false;
    }

    public static
    void connect( Vertex left, Vertex right, HalfEdge newer, HalfEdge older, Face another ) {
        // connect newer with left as its destination to
        // its first counter-clockwise edge with left as its origin
        connect( DCEL.firstClockWiseEdge( left, right ), newer );
        // connect older with left as its origin to
        // its first clockwise edge with left as its destination
        connect( older, DCEL.firstCounterClockWiseEdge( left, right ) );

        // connect newer with right as its destination to
        // its first counter-clockwise edge with right as its origin
        connect( DCEL.firstClockWiseEdge( right, left ), older );
        // connect older with right as its origin to
        // its first clockwise edge with right as its destination
        connect( newer, DCEL.firstCounterClockWiseEdge( right, left ) );

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
     * connect a split or merge vertex to a helper,
     * and add an internal diagonal.
     * */

    public static
    void connectHelper( Vertex left, Vertex right, List<Face> faces ) {
        if ( isAlreadyConnected( left, right ) ) return;

        // create two new halfEdges,
        // newer and older,
        // connecting left and right
        HalfEdge newer = new HalfEdge( right );
        HalfEdge older = new HalfEdge( left );
        setTwins( newer, older );
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
        if ( isAlreadyConnected( origin, target ) ) return;

        // create two new halfEdges,
        // outDegree and inDegree,
        // connecting target and origin
        HalfEdge outDegree = new HalfEdge( origin );
        assert origin.incidentEdge == null;
        origin.incidentEdge = outDegree;
        HalfEdge inDegree = new HalfEdge( target );
        setTwins( outDegree, inDegree );
        connect( outDegree, inDegree );

        HalfEdge clockWise = DCEL.firstClockWiseEdge( target, origin );

        // connect outDegree to the first clockwise edge left with target as its origin vertex
        connect( clockWise, outDegree );
        // connect to inDegree the first counter-clockwise edge left with origin as its origin vertex
        HalfEdge counterClockWise = DCEL.firstCounterClockWiseEdge( target, origin );
        connect( inDegree, counterClockWise );

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
     * @param edge edge which incidentFace is inner one, also returned value
     * @param split newly initialized vertex
     * */

    public static
    void split( HalfEdge edge, Vertex split ) {
        if ( isAlreadyConnected( split, edge.origin ) ||
                isAlreadyConnected( split, edge.next.origin ) ) return;

        assert new Line( edge.origin, edge.next.origin ).isOnThisSegment( split ) : edge.origin + " " + edge.next.origin;

        HalfEdge twin = edge.twin;
        HalfEdge originalNext = edge.next;
        HalfEdge originalTwinNext = twin.next;
        HalfEdge newlyNext = new HalfEdge( split );
        HalfEdge newlyTwinNext = new HalfEdge( split );
        assert split.incidentEdge == null;
        split.incidentEdge = newlyNext;

        newlyNext.incidentFace = edge.incidentFace;
        connect( newlyNext, edge );
        connect( originalNext, newlyNext );

        newlyTwinNext.incidentFace = edge.twin.incidentFace;
        connect( newlyTwinNext, twin );
        connect( originalTwinNext, newlyTwinNext );

        setTwins( edge, newlyTwinNext );
        setTwins( twin, newlyNext );

        assert DCEL.walkAroundEdge( edge ) != null;
        assert DCEL.walkAroundEdge( twin ) != null;
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

        connect( twin.next, edge.prev );
        connect( edge.next, twin.prev );

        // TODO: 10/19/2021 redirect Trapezoids

        Face face = twin.next.incidentFace;
        face.outComponent = edge.next;
        DCEL.resetIncidentFace( edge.next, face );

        assert DCEL.walkAroundEdge( edge.next ) != null;
        // f[0] remained one; f[1] deleted one
        return faces;
    }

    /**
     * are both vertices of the two halfEdges
     * on the same monotone chain?
     * */

    public boolean isOnTheDifferentChain( HalfEdge edge ) {
        return ( ( MonotoneVertex ) origin ).isOnTheDifferentChain( ( MonotoneVertex ) edge.origin );
    }

    private String toStringNormal() {
        return "[ origin:" + origin + "|" + "twin:" + twin + " ]";
    }

    private String toStringPointer() {
        return origin + " -> " + twin.origin;
    }

    @Override
    public String toString() {
        return toStringPointer();
    }
}
