package myLibraries.util.geometry.DCEL;

/*
 * HalfEdge.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 */

import myLibraries.util.geometry.elements.Ray;
import myLibraries.util.geometry.elements.Segment;
import myLibraries.util.geometry.elements.Vector;

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

    // Have visited this half-edge?
    // Give halfEdge the ability to traverse like a directed graph.
    public boolean isVisited;

    /**
     * Constructs to create an instance of HalfEdge
     * */

    public HalfEdge( Face incidentFace ) {
        this( null, incidentFace, IDStatic++ );
    }

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

    public Ray getRay( Vector rayPoint ) {
        return new Ray( origin, rayPoint );
    }

    public Segment getSegment() {
        return new Segment( origin, twin.origin );
    }

    /**
     * connect two half edges, this.pre <=> second.next
     *
     * @param second     previous to the second halfEdge
     * */

    public void connect( HalfEdge second ) {
        prev = second;
        second.next = this;
    }

    /**
     * set twins for two twin half edges, this <=> second
     * */

    public void setTwins(  HalfEdge second ) {
        twin = second;
        second.twin = this;
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
