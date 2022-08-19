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

import java.util.List;

/**
 * Data structure of an Edge
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Edge implements Comparable<Edge> {
    public int mappingID = -1;
    // distance/weight between
    // the starting vertex and the ending vertex
    long distance;
    final Vertex startVertex;
    final Vertex endVertex;

    /**
     * constructs to create an instance of Edge
     * */

    Edge( long d, Vertex s, Vertex e ) {
        this.distance = d;
        this.startVertex = s;
        this.endVertex = e;
    }

    Edge( Vertex s, Vertex e ) {
        this.startVertex = s;
        this.endVertex = e;
    }

    public static<E extends Edge>
    void resetMappingID( List<E> E ) {
        E.forEach( e -> e.mappingID = -1 );
    }

    //-------------------------------------------------------
    // union find operations
    //-------------------------------------------------------

    boolean formCycle() {
        return startVertex.isSameUnion( endVertex );
    }

    /**
     * return the union leader if merge the give edge
     * */

    Vertex getLeader() {
        Vertex leaderStart = startVertex.leader;
        Vertex leaderEnd = endVertex.leader;
        if ( leaderStart.group.size() <
                leaderEnd.group.size() )
            return leaderEnd;

        return leaderStart;
    }

    UnionFind union( Edge edge ) {
        return edge.startVertex.union( edge.endVertex );
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
                ": [ " + startVertex +
                ", " + endVertex +
                " ]";
    }
}
