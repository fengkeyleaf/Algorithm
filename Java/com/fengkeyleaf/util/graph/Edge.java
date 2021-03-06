package com.fengkeyleaf.util.graph;

/*
 * Edge.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $0.0$
 */

/**
 * Data structure of an Edge
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Edge implements Comparable<Edge> {
    // distance/weight between
    public final long distance;
    // the starting vertex and the ending vertex
    public final Vertex startVertex;
    public final Vertex endVertex;

    /**
     * constructs to create an instance of Edge
     * */

    public Edge( long distance,
                 Vertex startVertex, Vertex endVertex ) {
        this.distance = distance;
        this.startVertex = startVertex;
        this.endVertex = endVertex;
    }

    public boolean formCycle() {
        return UnionFind.isSameUnion( startVertex, endVertex );
    }

    /**
     * sort edge by distance in ascending order
     * */

    @Override
    public int compareTo( Edge aEdge ) {
        return Long.compare( distance, aEdge.distance );
    }

    @Override
    public String toString() {
        return  distance +
                ": [ " + startVertex.ID +
                ", " + endVertex.ID +
                " ]";
    }
}
