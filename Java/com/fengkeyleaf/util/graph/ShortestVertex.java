package com.fengkeyleaf.util.graph;

/*
 * ShortestVertex.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of Shortest Vertex
 * used for Dijkstra's and Bellman Ford's algorithm
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class ShortestVertex extends Vertex
        implements Comparable<ShortestVertex> {
    // distances
    public final List<Integer> distances = new ArrayList<>();

    // the smallest distance
    // going from the source vertex to this vertex
    public long currentShortestDistance = Long.MAX_VALUE;
    public boolean ifReachable;

    // these variables are irrelevant to hw_6
    public int numberOfParent = 0;
    public int indexAtHeap = -1;

    /**
     * constructs to create an instance of ShortestVertex
     * */

    public ShortestVertex( int ID ) {
        super( ID );
    }

    public ShortestVertex( int ID, Vertex predecessor ) {
        super( ID, predecessor );
    }

    public ShortestVertex( int ID, long currentShortestDistance ) {
        this( ID, null, currentShortestDistance );
    }

    public ShortestVertex( int ID, Vertex predecessor, long currentShortestDistance ) {
        super( ID, predecessor );
        this.currentShortestDistance = currentShortestDistance;
    }

    public void addDistance( int distance ) {
        distances.add( distance );
    }

    @Override
    public int compareTo( ShortestVertex vertex ) {
        return Long.compare( currentShortestDistance,
                vertex.currentShortestDistance );
    }

    public String myToString() {
        assert neighbours.size() == distances.size();

        StringBuilder text = new StringBuilder();
        text.append( ID ).append(": [ ");
        for ( int i = 0; i < neighbours.size(); i++ )
            text.append( neighbours.get( i ).ID ).append( "|" )
                    .append( distances.get( i ) ).append( ", " );

        return text.append("]").toString();
    }

    @Override
    public String toString() {
        return ID + ": [ " + currentShortestDistance + "|" + ( parent == null ? "" : parent.ID ) + " ]";
    }
}
