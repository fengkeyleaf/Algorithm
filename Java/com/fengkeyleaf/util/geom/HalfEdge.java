package com.fengkeyleaf.util.geom;

/*
 * HalfEdge.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 6/4/2021$
 *
 */

import com.fengkeyleaf.util.Node;

import java.util.*;

/**
 * Data structure of DCEL half-edge.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class HalfEdge {
    private static int IDStatic = 0;
    public final int ID;
    public Vertex origin;
    public HalfEdge twin;
    public Face incidentFace;
    public HalfEdge next;
    public HalfEdge prev;
    HalfEdge master;
    HalfEdge[] masters;
    HalfEdge servant;
    GraphVertex v;

    // Have visited this half-edge?
    // Give halfEdge the ability to traverse like a directed graph.
    // Mapping ID, functions the same as the one in class Node
    public int mappingID = -1;

    /**
     * Constructs to create an instance of HalfEdge
     * */

    HalfEdge( Face incidentFace ) {
        this( null, incidentFace, IDStatic++ );
    }

    HalfEdge( Vertex origin, Face incidentFace ) {
        this( origin, incidentFace, IDStatic++ );
    }

    HalfEdge( Vertex origin, Face incidentFace, int ID ) {
        this.origin = origin;
        this.incidentFace = incidentFace;
        this.ID = ID;
    }

    HalfEdge( Vertex origin ) {
        this( origin, null );
    }

    /**
     * copy constructor.
     *
     * @param e half-edge to be copied.
     */

    HalfEdge( HalfEdge e ) {
        ID = IDStatic++;

        e.servant = this;
        master = e;

        // copy origin
        origin = e.origin.servant == null ? new Vertex( e.origin ) : e.origin.servant;
        // copy next and prev
        next = e.next.servant == null ? new HalfEdge( e.next ) : e.next.servant;
        prev = e.prev.servant == null ? new HalfEdge( e.prev ) : e.prev.servant;
        // copy incidentFace
        incidentFace = e.incidentFace.servant == null ? new Face( e.incidentFace ) : e.incidentFace.servant;
        // copy twin, already connected twin with the following step,
        // so no need to use connect( twin ).
        twin = e.twin.servant == null ? new HalfEdge( e.twin ) : e.twin.servant;
    }

    Ray getRay( Vector rayPoint ) {
        return new Ray( origin, rayPoint );
    }

    Segment getSegment() {
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
     * Clean up all data field of this half-edge.
     * Note that its twin will not be reset.
     * In order to reset both two, use the following code:
     *
     * edge.twin.reset();
     * edge.reset();
     * */

    // TODO: 6/29/2022 remove the one from incidentFace.innerComponents(), set origin's incidentEdge.
    public void reset() {
        if ( origin != null && origin.incidentEdge == this )
            origin.incidentEdge = null;
        origin = null;

        twin = null;

        if ( incidentFace != null && incidentFace.outComponent == this )
            incidentFace.outComponent = null;
        incidentFace = null;

        if ( next != null && next.prev == this )
            next.prev = null;
        if ( prev != null && prev.next == this )
            prev.next = null;
        next = prev = null;

        if ( master != null && master.servant == this )
            master.servant = null;
        if ( servant != null )
            servant.master = null;
        master = servant = null;
        masters = null;

        v = null;
    }

    /**
     * Walk around all halfEdges connected to this one,
     * and get vertices incident to them.
     * */

    public List<Vertex> walkAroundVertex() {
        final List<Vertex> vertices = new ArrayList<>();
        HalfEdge e = this;

        do {
            vertices.add( e.origin );
            assert e.incidentFace == incidentFace;
            e = e.next;
        } while ( e != this );

        return vertices;
    }

    /**
     * Walk around and get all halfEdges connected to this one.
     * */

    public List<HalfEdge> walkAroundEdge() {
        final List<HalfEdge> edges = new ArrayList<>();
        HalfEdge e = this;

        do {
            edges.add( e );
            assert e.twin != null : this;
            assert e.incidentFace == incidentFace : e + " " + this;
            e = e.next;
        } while ( e != this );

        return edges;
    }

    /**
     * reset incidentFace of all half-edges starting from the start edge to the face
     * */

    public void resetIncidentFace( Face f ) {
        HalfEdge e = this;
        do {
            e.incidentFace = f;
            assert e.next != null : e;
            e = e.next;
        } while ( e != this );
    }

    /**
     * Split the edge into two parts,
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
     * Note that edge which incidentFace is inner one, also returned value
     *
     * @param split newly initialized vertex
     * @return Vertex of one of the half-edges if split point is already a vertex of one of them,
     *         Otherwise, return the new split vertex.
     * */

    public Vertex split( Vertex split ) {
        assert next.origin == twin.origin : this + " | " + next + " | " + twin;

        if ( split.equals( origin ) ||
                split.isAlreadyConnected( origin ) ) return origin;
        if ( split.equals( next.origin ) ||
                split.isAlreadyConnected( next.origin ) ) return next.origin;

        assert getSegment().isOnThisSegment( split ) : origin;

        // cannot remove this line,
        // since this.twin will not be the origin one at the very beginning.
        HalfEdge twin = this.twin;
        HalfEdge originalNext = next;
        HalfEdge originalTwinNext = twin.next;
        HalfEdge newlyNext = new HalfEdge( split );
        HalfEdge newlyTwinNext = new HalfEdge( split );
        // set split's incidentEdge
        if ( split.incidentEdge == null )
            split.incidentEdge = newlyNext;

        // set master
        assert masters == null;
        newlyNext.master = master;
        newlyTwinNext.master = twin.master;

        // connect half-edges
        newlyNext.incidentFace = incidentFace;
        newlyNext.connect( this );
        originalNext.connect( newlyNext );

        newlyTwinNext.incidentFace = twin.incidentFace;
        newlyTwinNext.connect( twin );
        originalTwinNext.connect( newlyTwinNext );

        setTwins( newlyTwinNext );
        twin.setTwins( newlyNext );

        return split;
    }

    public Vertex split( Vector split ) {
        return split( new Vertex( split ) );
    }

    /**
     * Delete this edge from its DCEL.
     *
     * Note that given edge must have next() and prev(),
     * they shouldn't be null. This is true for its twin edge as well.
     *
     * @return [remained face, deleted face]
     * */

    // TODO: 4/3/2022 not prefect.
    public Face[] delete() {
        Face[] faces = new Face[] { twin.incidentFace, incidentFace };
        Vertex twinOrigin = twin.origin;

        if ( next == twin && twin.prev == this ) {
            assert twin.origin.allIncidentEdges().size() == 2;
            twinOrigin.incidentEdge = null;
        }

        if ( prev == twin && twin.next == this ) {
            assert origin.allIncidentEdges().size() == 2;
            origin.incidentEdge = null;
        }

        if ( origin.incidentEdge == this ) {
            assert twin.next.origin == origin;
            origin.incidentEdge = twin.next;
        }

        if ( twinOrigin.incidentEdge == twin ) {
            assert next.origin == twinOrigin;
            twinOrigin.incidentEdge = next;
        }

        twin.next.connect( prev );
        next.connect( twin.prev );

        // ignore when remained face is the infinite one.
        if ( faces[ 0 ].outComponent != null )
            faces[ 0 ].outComponent = next;
        if ( faces[ 0 ] != faces[ 1 ] )
            next.resetIncidentFace( faces[ 0 ] );

        incidentFace.outComponent = null;
        twin.reset();
        reset();

        assert faces[ 0 ].outComponent == null || faces[ 0 ].walkAroundEdge() != null;
        // f[0] remained one; f[1] deleted one
        return faces;
    }

    /**
     * are both vertices of the two halfEdges
     * on the same monotone chain?
     * */

    boolean isOnTheDifferentChain( HalfEdge edge ) {
        return origin.isOnTheDifferentChain( edge.origin );
    }

    void addMasters( HalfEdge m1, HalfEdge m2 ) {
        assert masters == null;
        assert master != null && m1 != null && m2 != null;
        masters = new HalfEdge[] { m1, m2 };
    }

    void addMasters( HalfEdge m ) {
        assert masters == null;
        assert master != null && m != null;
        masters = new HalfEdge[] { master, m };
    }

    List<HalfEdge> getMasters() {
        List<HalfEdge> E = new ArrayList<>( masters != null ? 2 : 1 );
        if ( masters != null )
            E.addAll( Arrays.asList( masters ) );
        else {
            assert master != null : this;
            E.add( master );
        }

        return E;
    }

    /**
     * Get all inner faces bounded by this half-edge, but not including holes.
     *
     * @return hale-edges corresponding to each face inside the outer face ( they're not holes ).
     */

    public Collection<HalfEdge> getInners() {
        List<HalfEdge> E = new ArrayList<>();

        // mark the outer boundary as visited.
        List<HalfEdge> edges = walkAroundEdge();
        edges.forEach( e -> {
            assert e.mappingID == -1 : e.mappingID + " | " + e;
            e.mappingID = 0;
            E.add( e );
        } );

        // TODO: 6/22/2022 Type of the filter could be List<> for efficiency improvement in production environment.
        TreeMap<Integer, HalfEdge> filter = new TreeMap<>();
        // find inner faces bounded by the cycle of this half-edge, but they're not holes.
        edges.forEach( e -> getInners( e.twin, filter, E ) );

        resetMappingID( E );
        return filter.values();
    }

    private static
    void getInners( HalfEdge edge,
                    TreeMap<Integer, HalfEdge> filter,
                    List<HalfEdge> E ) {

        if ( edge.mappingID >= 0 ) return;

        // add inner face of e.
        assert !filter.containsKey( edge.incidentFace.ID ) : edge;
        filter.put( edge.incidentFace.ID, edge );

        List<HalfEdge> edges = edge.walkAroundEdge();
        // mark the cycle of e as visited.
        edges.forEach( e -> {
            assert e.mappingID == -1;
            e.mappingID = 0;
            E.add( e );
        } );

        // check to see if there are neighbouring faces adjacent to the cycle.
        edges.forEach( e -> getInners( e.twin, filter, E ) );

        // find inner faces bounded by the cycle of this half-edge, and also they are holes.
        edge.incidentFace.innerComponents.forEach( e ->
                e.getInners().forEach( holeEdge -> {
                    assert !filter.containsKey( holeEdge.incidentFace.ID );
                    filter.put( holeEdge.incidentFace.ID, holeEdge );
                } )
        );
    }

    /**
     * Reset mapping ID to -1.
     * This method is similar to the one in {@link Node#resetMappingID(List)}
     */

    public static
    void resetMappingID( List<HalfEdge> E ) {
        E.forEach( e -> e.mappingID = -1 );
    }

    @Override
    public String toString() {
        return origin + " -> " + ( twin == null ? null : twin.origin );
    }
}
