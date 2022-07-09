package com.fengkeyleaf.util.graph;

/*
 * Vertex.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $0.0$
 */

import com.fengkeyleaf.util.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * data structure of a Vertex in graph
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Vertex extends Node {
    protected static int staticID = 0;
    // adjacent list
    public final List<Vertex> neighbours;
    // the leader of the group
    public Vertex leader = this;
    // the group this vertex belonging to
    public UnionFind group;
    // array to check if an edge has been added before
    public boolean[] addEdge;

    /**
     * constructs to create an instance of Vertex
     * */

    public Vertex() {
        super( staticID++ );
         neighbours = new ArrayList<>();
    }

    public Vertex( int ID ) {
        super( ID );
        neighbours = new ArrayList<>();
    }

    public Vertex( int ID, Vertex predecessor ) {
        super( ID, predecessor );
        neighbours = new ArrayList<>();
    }

    protected Vertex( int ID, int capacity ) {
        super( ID );
        neighbours = new ArrayList<>( capacity );
    }

    /**
     * add an edge to this vertex
     * */

    public void add( Vertex neighbour ) {
        neighbours.add( neighbour );
    }

    /**
     * check if a given edge has been added before
     * */

    public boolean alreadyHasThisNeighbour( Vertex neighbour ) {
        return neighbours.contains( neighbour );
    }

    /**
     * the size of the neighbours of this vertex
     * */

    public int size() {
        return neighbours.size();
    }

    /**
     * this vertex has any neighbours?
     * */

    public boolean hasNoNeighbours() {
        return neighbours.isEmpty();
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null || getClass() != obj.getClass() ) return false;
        Vertex vertex = ( Vertex ) obj;
        return ID == vertex.ID;
    }


    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append( ID ).append(": [ ");
        for ( Vertex n : neighbours )
            text.append( n.ID ).append( " " );

        return text.append("]").toString();
    }
}