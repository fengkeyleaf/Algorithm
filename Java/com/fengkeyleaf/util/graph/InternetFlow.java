package com.fengkeyleaf.util.graph;

/*
 * InternetFlow.java
 *
 * JDK: 14
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.io.BuildInternetFlow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Data structure of a Internet Flow.
 * We use 0 to stand for edges used up all capacity
 * since the weight of an edge cannot be negative in internet flow
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class InternetFlow extends Graph<InternetFlowVertex> {
    public int maxFlow;
    // paths s -> t with flows
    // but in the current implementation,
    // paths using backwards edges haven't been restored
    // so the stored paths are mainly for testing purpose
    private final List<LinkedList<InternetFlowVertex>> paths = new ArrayList<>();
    private final List<Integer> distances = new ArrayList<>();

    // for testing
    private boolean flag = true;

    // TODO: 4/25/2021 the recursion processes for forward and backward
    //  can be combined into one method, but maybe no time to set this up
    //  since have an AI lab assignment to work on
    private int recursionProcess( InternetFlowVertex vertex, boolean[] ifVisited,
                                  int bottleneck, boolean ifForward,
                                  List<Vertex> forwardEdges, List<Vertex> backwardEdges ) {
        return 0;
    }

    /**
     * DFS find path s -> t with backwards or without backwards edges
     * */

    private int DFS( InternetFlowVertex vertex, boolean[] ifVisited,
                     int bottleneck, boolean ifForward ) {
        int ID = vertex.ID;
//        System.out.println( ID );

        // base case, we've reached t
        if ( vertex.type == InternetFlowVertex.Type.END ) {
            assert vertex.neighbours.isEmpty(); // having no out-coming edges
            assert bottleneck > 0;
            ifVisited[ ID ] = true;
            LinkedList<InternetFlowVertex> path = new LinkedList<>();
            path.addFirst( vertex );
            paths.add( path );
            distances.add( bottleneck );
            return bottleneck;
        }

        // or revisited a vertex
        if ( ifVisited[ ID ] ) return 0;
        ifVisited[ ID ] = true;

        // DFS without backward edges
        for ( int i = 0; i < vertex.neighbours.size(); i++ ) {
            // cannot be s
            assert ( ( InternetFlowVertex ) vertex.neighbours.get( i ) ).type != InternetFlowVertex.Type.START;

            InternetFlowVertex neighbour = ( InternetFlowVertex ) vertex.neighbours.get( i );
            int currentCapacity = vertex.forwardsDistances[ neighbour.ID ];
            // flow cannot be negative
            assert currentCapacity >= 0;
            // skip edges with no capacity anymore
            if ( currentCapacity == 0 ) continue;

            int res = DFS( neighbour, ifVisited, Math.min( bottleneck, currentCapacity ), ifForward );
            // flow cannot be negative
            assert res >= 0;
            // we've reached t when returned values are greater than 0
            if ( res > 0 ) {
                // flow cannot be negative
                assert vertex.forwardsDistances[ neighbour.ID ] - res >= 0;
                // update flow of forwards and backwards edge
                vertex.forwardsDistances[ neighbour.ID ] -= res;
                neighbour.backwardsDistances[ vertex.ID ] += res;

                // add path
                paths.get( paths.size() - 1 ).addFirst( vertex );
                return res;
            }
        }

        // cannot use backwards edges for the first time
        if ( ifForward ) return 0;

        // for testing purpose
        if ( flag ) {
            paths.add( new LinkedList<>() );
            distances.add( -1 );
            flag = false;
        }

        // DFS with backward edges
        for ( int i = 0; i < vertex.backwardsNeighbours.size(); i++ ) {

            InternetFlowVertex backwardNeighbour = vertex.backwardsNeighbours.get( i );
            int currentCapacity = vertex.backwardsDistances[ backwardNeighbour.ID ];
            // flow cannot be negative
            assert currentCapacity >= 0;
            // skip edges with no capacity anymore
            if ( currentCapacity == 0 ) continue;

            int res = DFS( backwardNeighbour, ifVisited, Math.min( bottleneck, currentCapacity ), false );
            assert res >= 0;
            // we've reached t when returned values are greater than 0
            if ( res > 0 ) {
                assert vertex.backwardsDistances[ backwardNeighbour.ID ] - res >= 0;
                // update flow of forwards and backwards edge
                vertex.backwardsDistances[ backwardNeighbour.ID ] -= res;
                backwardNeighbour.forwardsDistances[ vertex.ID ] += res;

                // add path
                paths.get( paths.size() - 1 ).addFirst( vertex );
                return res;
            }
        }

        // cannot find a path s -> t
        return 0;
    }

    /**
     * start searching paths s -> t
     * */

    // TODO: 12/3/2021 use BFS to implement Internet flow
    private int startSearching( InternetFlowVertex start, boolean ifForward ) {
        int sum = 0;
        // false, no paths s -> t existed in the graph
        boolean ifContinue = true;
        final boolean[] ifVisited = new boolean[ vertices.size() ];
        ifVisited[ start.ID ] = true;

        while ( ifContinue ) {
            ifContinue = false;

            // iterate all edges of s
            for ( int i = 0; i < start.neighbours.size(); i++ ) {

                InternetFlowVertex neighbour = ( InternetFlowVertex ) start.neighbours.get( i );
                int currentCapacity = start.forwardsDistances[ neighbour.ID ];
                // flow cannot be negative
                assert currentCapacity >= 0;
                // skip edges with no capacity anymore
                if ( currentCapacity == 0 ) continue;

                int res = DFS( neighbour, ifVisited, currentCapacity, ifForward );
                sum += res;
                // we've reached t when returned values are greater than 0
                if ( res > 0 ) {
                    assert start.forwardsDistances[ neighbour.ID ] - res >= 0;
                    // update flow of forwards and backwards edge
                    start.forwardsDistances[ neighbour.ID ] -= res;
                    neighbour.backwardsDistances[ start.ID ] += res;

                    // add path
                    paths.get( paths.size() - 1 ).addFirst( start );
                }

                // stop at this point?
                ifContinue |= res > 0;
                // reset all vertices' visit status
                Arrays.fill( ifVisited, false );
                // no need to visit s again
                ifVisited[ start.ID ] = true;
            }
        }

        return sum;
    }

    /**
     * find the max flow
     * */

    public int findMaxFlow( InternetFlowVertex start ) {
        maxFlow = 0;

        // DFS without backwards edges
        maxFlow += startSearching( start,true );
        // DFS with backwards edges
        maxFlow += startSearching( start,false );

        return maxFlow;
    }

    /**
     * methods below aim at testing
     * */

    public static
    void addVertices( InternetFlow aGraph1, InternetFlow aGraph2 ) {
        for ( InternetFlowVertex vertex: aGraph2.vertices )
            aGraph1.add( vertex );
    }

    public static
    void printAllPaths( InternetFlow aGraph) {
        StringBuilder text = new StringBuilder();

        for ( int i = 0; i < aGraph.paths.size(); i++ ) {
            text.append( i ).append( ": " ).append( aGraph.distances.get( i ) ).append( " [ ");
            for ( InternetFlowVertex vertex : aGraph.paths.get( i ) ) {
                text.append( vertex.ID ).append( " -> " );
            }
            text.append( " ]\n" );
        }

        System.out.println( text );
    }

    public static
    void main( String[] args ) {
//        InternetFlow aGraph1 = new BuildInternetFlow( "src/hw_" + 6 + "/Q" + 2 + "/" + "1_1" ).aGraph;
//        System.out.println( aGraph1.findMaxFlow( aGraph1.getVertexByID( 1 ) ) ); // 13
//        printAllPaths( aGraph1 );

        InternetFlow aGraph2 = new BuildInternetFlow( "src/hw_" + 6 + "/Q" + 2 + "/" + "1_2" ).aGraph;
        System.out.println( aGraph2.findMaxFlow( aGraph2.getVertexByID( 1 ) ) ); // 21
        printAllPaths( aGraph2 );

        InternetFlow aGraph3 = new BuildInternetFlow( "src/hw_" + 6 + "/Q" + 2 + "/" + "1_3" ).aGraph;
        System.out.println( aGraph3.findMaxFlow( aGraph3.getVertexByID( 1 ) ) ); // 4
        printAllPaths( aGraph3 );
    }
}
