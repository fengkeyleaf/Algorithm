package com.fengkeyleaf.util.graph;

/*
 * DAG.java
 *
 * JDK: 14
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data structure of directed acyclic graph, DAG
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class DAG<E extends Vertex> extends Graph<E> {

    /**
     * get Topological Ordering of this graph.
     * time complexity: O(n + m)
     *
     * @return            array with vertices sorted
     *                    by their decreasing finishing time
     *                    if this graph is a DAG;
     *                    an empty list if this graph is not a DAG;
     * */

    public List<Vertex> getTopologicalOrdering() {
        explored = new boolean[ vertices.size() + 1 ];

        for ( int i = vertices.size() - 1; i >= 0; i-- ) {
            E aVertex = vertices.get( i );

            if ( !explored[ aVertex.ID ] ) {
                sortVertexByFinishTime( aVertex );
            }
        }

        Collections.reverse( finishes );
        return checkProcessing() ? finishes : new ArrayList<>();
    }

    /**
     * check to see if this graph is a DAG
     * we will see an empty graph as not DAG
     *
     * time complexity: O(n + m)
     * */

    public boolean checkProcessing() {
        // map each vertex to a new index, with decreasing finishing time
        int[] finishingOrder = new int[ finishes.size() + 1 ];
        for ( int i = 0; i < finishes.size(); i++ )
            finishingOrder[ finishes.get( i ).ID ] = i;

        // i-th vertex can only reach the latter ones,
        // (i + 1)th, (i + 2)th, and so on
        for ( Vertex vertex : finishes )
            for ( Vertex neighbour : vertex.neighbours )
                if ( finishingOrder[ neighbour.ID ] < finishingOrder[ vertex.ID ] )
                    return false;

        // handle an empty graph
        // return isEmpty() ? false : true;
        return !isEmpty();
    }

    /**
     * Not necessary
     *
     * determines if it is possible to
     * add just a single edge to the graph
     * such that it becomes strongly connected
     *
     * Note that the input graph must be guaranteed to be a DAG,
     * or an empty graph
     *
     * time complexity: O(n + m)
     * */

    public boolean OneEdgeProperty() {
        assert checkProcessing() || isEmpty();

        for ( int i = 0; i < finishes.size() - 1; i++ ) {
            Vertex next = finishes.get( i + 1 );
            boolean canReachNext = false;

            // Check if i-th node can reach (i+1)th node
            // in topological ordering
            for ( Vertex neighbour : finishes.get( i ).neighbours ) {
                if ( neighbour.equals( next ) ) {
                    canReachNext = true;
                    break;
                }
            }

            if ( !canReachNext ) return false;
        }

        // handle an empty graph
        // return isEmpty() ? false : true;
        return !isEmpty();
    }

    /**
     * Add one edge connecting
     * the last vertex and the first one
     * */

    public void addOneEdge() {
        if ( finishes.isEmpty() ) return;

        UnionFindVertex first = ( UnionFindVertex ) finishes.get( 0 );
        UnionFindVertex last = ( UnionFindVertex ) finishes.get( finishes.size() - 1 );
        // add original edge
        last.add( first );
        // add reversed edge
        first.addReverse( last );
    }
}
