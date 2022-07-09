package com.fengkeyleaf.util.geom;

/*
 * GraphVertex.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 6/9/2022$
 */

import com.fengkeyleaf.util.graph.Vertex;

/**
 * Alias for the Graph Vertex, {@link Vertex}
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

class GraphVertex extends Vertex {

    //----------------------------------------------------------
    // MapOverlay Part.
    //----------------------------------------------------------

    // half-edge attached to this graph vertex.
    final HalfEdge e;
    // face attached to this graph vertex.
    Face f;

    GraphVertex() {
        e = null;
    }

    GraphVertex( HalfEdge e ) {
        this.e = e;
    }

    /**
     * add face for the cycle of this graph vertex.
     */

    void addFace() {
        f = new Face( e );
        f.resetIncidentFace();
    }

    /**
     * build links between boundary cycles.
     */

    void setFace() {
        // no bounded face.
        if ( neighbours.isEmpty() ) return;

        assert neighbours.size() == 1;
        // find the bounded face which is the top level of the graph vertex link chain.
        GraphVertex v = findBoundary( ( GraphVertex ) neighbours.get( 0 ) );
        // link to the infinite face.
        if ( v.e == null ) {
            v.f.addInnerComponent( e );
            return;
        }

        // link to the outer boundary cycle.
        v.e.incidentFace.addInnerComponent( e );
        // e cannot be null.
        e.resetIncidentFace( v.e.incidentFace );
        e.twin.addMasters( v.e.master );
        assert isRightFace( v.e );
    }

    static
    GraphVertex findBoundary( GraphVertex v ) {
        while ( !v.neighbours.isEmpty() ) {
            assert v.neighbours.size() == 1;
            v = ( GraphVertex ) v.neighbours.get( 0 );
        }

        return v;
    }

    boolean isRightFace( HalfEdge e ) {
        HalfEdge t = this.e;

        do {
            // inner face by this graph vertex must be contained by its outer boundary face.
            assert e.incidentFace.isOnPolygon( t.origin ) : t.origin + " | " + t.walkAroundEdge() + "\n" + e.incidentFace.walkAroundEdge();
            t = t.next;
        } while ( t != this.e );

        return true;
    }

    @Override
    public String toString() {
        return e == null ? "[]" : "[ " + e + " ]";
    }
}
