package myLibraries.util.graph.elements;

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

import myLibraries.util.Node;
import myLibraries.util.graph.UnionFind;

import java.util.ArrayList;
import java.util.List;

/**
 * data structure of a Vertex in graph
 *
 * @author       Xiaoyu Tongyang
 */

public class Vertex extends Node {
    // adjacent list
    public final List<Vertex> neighbours = new ArrayList<>();
    // the leader of the group
    public Vertex leader = this;
    // the group this vertex belonging to
    public UnionFind group;
    // array to check if an edge has been added before
    public boolean[] addEdge;

    /**
     * constructs to create an instance of Vertex
     * */

    public Vertex( int ID ) {
        super( ID );
    }

    public Vertex( int ID, Vertex predecessor ) {
        super( ID, predecessor );
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
        for ( Vertex neighbour : neighbours )
            text.append( neighbour.ID ).append( " " );

        return text.append("]").toString();
    }
}