package myLibraries.util.geometry.DCEL;

/*
 * HalfEdge.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

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

    private static
    boolean isAlreadyConnected( Vertex left, Vertex right ) {
        List<HalfEdge> incidentEdges = DCEL.allIncidentEdges( left );
        for ( HalfEdge edge : incidentEdges ) {
            if ( edge.origin.equals( right ) ) {
                assert edge.twin.origin.equals( left );
                return true;
            }
        }

        return false;
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

        // connect newer with left as its destination and
        // its first counter-clockwise edge with left as its origin
        connect( DCEL.firstClockWiseEdge( left, right ), newer );
        // connect older with left as its origin and
        // its first clockwise edge with left as its destination
        connect( older, DCEL.firstCounterClockWiseEdge( left, right ) );

        // connect newer with right as its destination and
        // its first counter-clockwise edge with right as its origin
        connect( DCEL.firstClockWiseEdge( right, left ), older );
        // connect older with right as its origin and
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
     * are both vertices of the two halfEdges
     * on the same monotone chain?
     * */

    public boolean isOnTheDifferentChain( HalfEdge edge ) {
        return ( ( MonotoneVertex ) origin ).isOnTheDifferentChain( ( MonotoneVertex ) edge.origin );
    }

    private String toStringNormal() {
        return "[ origin:" + origin + "|" + "twin:" + twin + " ]";
    }

    @Override
    public String toString() {
        return String.valueOf( origin );
    }
}
