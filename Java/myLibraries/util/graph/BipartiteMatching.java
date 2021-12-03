package myLibraries.util.graph;

/*
 * BipartiteMatching.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.graph.elements.InternetFlowVertex;
import myLibraries.util.graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

import static myLibraries.util.graph.elements.InternetFlowVertex.Type.*;

/**
 * Data structure of a BipartiteMatching.
 *
 * @author       Xiaoyu Tongyang
 */

public class BipartiteMatching extends InternetFlow {
    // vertex IDs for left set elements
    private int[] setLeftIDs;
    // vertex IDs for right set elements
    private int[] setRightIDs;

    /**
     * add s and t into a Bipartite Matching graph
     */

    public InternetFlowVertex buildInternetFlow( int[] setLeftIDs, int[] setRightIDs,
                                                 InternetFlowVertex[] vertices ) {
        // add s and t
        InternetFlowVertex start = new InternetFlowVertex( vertices.length, START );
        InternetFlowVertex end = new InternetFlowVertex( vertices.length + 1, END );
        add( start );
        add( end );

        // connect s to vertices in left set
        for ( int ID : setLeftIDs ) {
            if ( ID < 0 ) continue;
            assert getVertexByID( ID + 1 ).ID == vertices[ ID ].ID;
            connectVertices( start, vertices[ ID ] );
        }

        // connect vertices in left set to t
        for ( int ID : setRightIDs ) {
            if ( ID < 0 ) continue;
            assert getVertexByID( ID + 1 ).ID == vertices[ ID ].ID;
            connectVertices( vertices[ ID ], end );
        }

        this.setLeftIDs = setLeftIDs;
        this.setRightIDs = setRightIDs;

        // return s
        return start;
    }

    /**
     * connect vertex1 to vertex2 and add capacity
     */

    private void connectVertices( InternetFlowVertex vertex1,
                                  InternetFlowVertex vertex2 ) {
        vertex1.add( vertex2 );
        if ( vertex1.forwardsDistances == null )
            vertex1.forwardsDistances = new int[ vertices.size() ];
        vertex1.addForwardDistance( vertex2.ID, 1 );

        vertex2.addBackwardNeighbour( vertex1 );
        if ( vertex2.backwardsDistances == null )
            vertex2.backwardsDistances = new int[ vertices.size() ];
        vertex2.addBackwardDistance( vertex1.ID, 0 );
    }

    /**
     * get all matching
     */

    public List<List<InternetFlowVertex>> getAllMatching() {
        final List<List<InternetFlowVertex>> paths = new ArrayList<>( maxFlow );

        // not include start and end
        for ( int leftID : setLeftIDs ) {
            InternetFlowVertex vertex = getVertexByIndex( leftID );
            assert vertex.childOrCloth;

            for ( Vertex neighbour : vertex.neighbours ) {
                // edges with 0 capacity are the ones that used to build a match
                if ( vertex.forwardsDistances[ neighbour.ID ] == 0 ) {
                    List<InternetFlowVertex> path = new ArrayList<>();
                    path.add( vertex );
                    path.add( ( InternetFlowVertex ) neighbour );
                    paths.add( path );
                }
            }
        }

        // max matching must equal max flow
        assert paths.size() == maxFlow;
        return paths;
    }
}
