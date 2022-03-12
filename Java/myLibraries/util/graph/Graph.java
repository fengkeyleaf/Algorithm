package myLibraries.util.graph;

/*
 * Graph.java
 *
 * JDK: 14
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 standard data structure$
 *     $1.1 standard data structure with generic$
 */

import myLibraries.util.graph.elements.Edge;
import myLibraries.util.Node;
import myLibraries.util.graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of a Graph
 *
 * @author       Xiaoyu Tongyang
 */

// TODO: 10/30/2021 class tree can be merged into this class, Graph;
//  i.e. graph represents n-array tree.
public class Graph<E extends Node> {
    public final List<E> vertices = new ArrayList<>();
    public List<Edge> edges;

    // array to indicate if a vertex has been visited before
    protected boolean[] explored;
    // array to store vertices by their finishing time
    public final List<Vertex> finishes = new ArrayList<>();

    /**
     * add a vertex into this graph
     * */

    public void add( E vertex ) {
       vertices.add( vertex );
    }

    public int size() {
       return vertices.size();
    }

    public boolean isEmpty() {
       return vertices.isEmpty();
    }

    /**
     * get a vertex in this graph with its ID
     * note that the ID has to start at 1, not 0
     * */

    public E getVertexByID( int ID ) {
       return vertices.get( ID - 1 );
    }

    public E getVertexByIndex( int index ) {
        return vertices.get( index );
    }

    public E setVertex( int index, E vertex ) {
        return vertices.set( index, vertex );
    }

    /**
     * clear this graph
     * */

    public void clear() {
       vertices.clear();
    }

    /**
     * sort Vertex By Finish Time
     *
     * time complexity: O(n + m)
     * */

    public void sortVertexByFinishTime( Vertex aVertex ) {
        explored[ aVertex.ID ] = true;

        for ( Vertex neighbour : aVertex.neighbours ) {
            if ( !explored[ neighbour.ID ] ) {
                sortVertexByFinishTime( neighbour );
            }
        }

        finishes.add( aVertex );
    }

    /**
     * DFS to find all reachable vertices and count their number
     * */

    public int DFS( Vertex vertex, boolean[] visited ) {
        // base case
        // visited before?
        if ( visited[ vertex.ID ] ) return 0;
        visited[ vertex.ID ] = true;

        // recursion procedure
        int totalVertices = 0;
        for ( Vertex neighbour : vertex.neighbours )
            totalVertices += DFS( neighbour, visited );

        return totalVertices + 1;
    }

    public void addAll( List<E> vertices ) {
        this.vertices.addAll( vertices );
    }

    public String toString() {
        StringBuilder text = new StringBuilder();
        for ( E vertex : vertices )
            text.append( vertex ).append( "\n" );
        return text.toString();
    }
}
