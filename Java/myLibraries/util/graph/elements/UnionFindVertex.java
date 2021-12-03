package myLibraries.util.graph.elements;

/*
 * UnionFindVertex.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.graph.UnionFind;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of a vertex with Union-find
 * and reversed edges
 *
 * @author       Xiaoyu Tongyang
 */

public class UnionFindVertex extends Vertex {
    // backwards edges and distances
    public final List<UnionFindVertex> reversedNeighbours = new ArrayList<>();

    /**
     * constructs to create an instance of UnionFindVertex
     * */

    public UnionFindVertex( int ID ) {
        this( ID, null );
    }

    public UnionFindVertex( int ID, UnionFindVertex predecessor ) {
        super( ID, predecessor );
        group = new UnionFind( this );
    }

    /**
     * add a reversed edge
     * */

    public void addReverse( UnionFindVertex neighbour ) {
        reversedNeighbours.add( neighbour );
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append( ID ).append(": [ ");
        for ( Vertex neighbour : neighbours )
            text.append( neighbour.ID ).append( " " );

        text.append( " ] | [ " );
        for ( Vertex neighbour : reversedNeighbours )
            text.append( neighbour.ID ).append( " " );

        return text.append("]").toString();
    }
}
