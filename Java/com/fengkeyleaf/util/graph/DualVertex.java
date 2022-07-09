package com.fengkeyleaf.util.graph;

/*
 * DualVertex.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.util.geom.Face;
import com.fengkeyleaf.util.geom.HalfEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of Dual Vertex for dual graph
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class DualVertex extends Vertex {
    // outer boundary halfEdges adjacent to neighbour polygons
    public final List<HalfEdge> neighbourEdges = new ArrayList<>();
    // face this Dual vertex has
    public final Face face;
    // outer boundary halfEdge adjacent to the neighbour polygon,
    // when finding shortest path in a dual graph
    public HalfEdge shortestNeighbourEdge;

    /**
     * constructs to create an instance of Vertex
     * */

    public DualVertex( int ID, Face face ) {
        this( ID, null, face );
    }

    public DualVertex( int ID, Vertex predecessor, Face face ) {
        super( ID, predecessor );
        this.face = face;
    }

    public void add( HalfEdge neighbour ) {
        neighbourEdges.add( neighbour );
    }
}
