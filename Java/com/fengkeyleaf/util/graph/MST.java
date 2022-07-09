package com.fengkeyleaf.util.graph;

/*
 * MST.java
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
 * Class of Minimum spanning tree, MST.
 * This class provides algorithms related to MST.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class MST {

    /**
     * Can have choices given two edges with identical weight
     * */

    public static
    boolean hasChoices( Edge edge1, Edge edge2 ) {
        // case 1: one must be included and the other must be excluded
        // and case 3: both must be excluded
        // i.e. either edge1 or edge2 will form a cycle in both cases
        if ( edge1.formCycle() || edge2.formCycle() )
            return false;

        // then we only need to consider case 2 and case 4
        Vertex original1 = edge1.startVertex.leader;
        Vertex original2 = edge1.endVertex.leader;

        // merge the first edge and get the leader
        // not actually do the merging process
        Vertex unionLeader = UnionFind.getLeader( edge1 );
        edge1.startVertex.leader = unionLeader;
        edge1.endVertex.leader = unionLeader;

        // case 4, if and only if we need to exclude the second edge,
        // after merging the first one;
        // otherwise, it's case 1;
        boolean canMergeSecond = edge2.formCycle();

        // recover the original leaders
        edge1.startVertex.leader = original1;
        edge1.endVertex.leader = original2;

        return canMergeSecond;
    }

    /**
     * the number of minimum spanning trees that exist for the given graph,
     * and it is guaranteed that
     * there are never more than two edges with identical weight
     *
     * Use Kruskal's algorithm in this implementation
     * */

    public static
    int howManyMSTWithTwo( List<Edge> edges ) {
        if ( edges.isEmpty() ) return 0;

        // sort the edges in ascending weight order
        Collections.sort( edges );

        int countChoices = 0;
        int minWeight = 0;
        List<Edge> chosenEdges = new ArrayList<>();
        for ( int i = 0; i < edges.size(); i++ ) {
            Edge edge = edges.get( i );
            // check to see if we have choices,
            // given there are two edges with identical weight
            // Must check first and then choose an arbitrary legal edge
            if ( i < edges.size() - 1 &&
                    edge.distance == edges.get( i + 1 ).distance )
                countChoices += hasChoices( edge, edges.get( i + 1 ) ) ? 1 : 0;

            // build the MST
            if ( !edge.formCycle() ) {
                minWeight += edge.distance;
                UnionFind.union( edge );
                chosenEdges.add( edge );
            }
        }

//        System.out.println( minWeight );
//        System.out.println( chosenEdges );
//        System.out.println( chosenEdges.size() );
        return ( int ) Math.pow( 2, countChoices );
    }
}
