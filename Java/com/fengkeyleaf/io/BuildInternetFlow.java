package com.fengkeyleaf.io;

/*
 * BuildInternetFlow.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.util.graph.InternetFlow;
import com.fengkeyleaf.util.graph.InternetFlowVertex;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * class to build an internet flow with input data
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class BuildInternetFlow implements ProcessingFile {
    private InternetFlowVertex[] vertices;
    public final InternetFlow aGraph = new InternetFlow();

    /**
     * read input data from input resource and output results
     * */

    public BuildInternetFlow( String fileName ) {
        ReadFromStdOrFile.readFromFile( fileName, this );
    }

    /**
     * read n, k, t, m
     * */

    private void readInfo( String info ) {
        String[] number = info.split( ReadFromStdOrFile.PATTERN_WHITE_CHARACTER );
        vertices = new InternetFlowVertex[ Integer.parseInt( number[ 0 ] ) ];
    }

    /**
     * process input data
     * */

    public void processingFile( Scanner sc ) {
        int initializingCount = 0;

        while ( sc.hasNext() ) {
            String content = sc.nextLine();
            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            // read info, #'s vertex, #'s edges, etc.
            if ( initializingCount++ < 1 &&
                    Pattern.matches( ReadFromStdOrFile.PATTERN_LENGTH, content ) ) {
                readInfo( content );
                continue;
            }

            String[] numbers = content.split( ReadFromStdOrFile.PATTERN_WHITE_CHARACTER );
            int vertexID1 = Integer.parseInt( numbers[ 0 ] );
            int vertexID2 = Integer.parseInt( numbers[ 1 ] );
            int weight = Integer.parseInt( numbers[ 2 ] );
            buildingGraph( vertexID1, vertexID2, weight );
        }

        for ( InternetFlowVertex vertex : vertices )
            aGraph.add( vertex );

//        System.out.println( aGraph );
    }

    private InternetFlowVertex.Type getType( int ID, int n ) {
        if ( ID == 0 ) return InternetFlowVertex.Type.START;
        else if ( ID == n - 1 ) return InternetFlowVertex.Type.END;

        return InternetFlowVertex.Type.INTERMEDIATE;
    }

    /**
     * building the undirected graph
     * Note that ID starts from 0, not 1 for this problem
     * */

    public void buildingGraph( int ID1, int ID2, int weight ) {
        // created these vertices before?
        if ( vertices[ ID1 ] == null )
            vertices[ ID1 ] = new InternetFlowVertex( ID1, getType( ID1, vertices.length ) );

        if ( vertices[ ID2 ] == null )
            vertices[ ID2 ] = new InternetFlowVertex( ID2, getType( ID2, vertices.length )  );

        InternetFlowVertex vertex1 =  vertices[ ID1 ];
        InternetFlowVertex vertex2 =  vertices[ ID2 ];
        // add forward edge
        vertex1.add( vertex2 );
        // add forward weight
        if ( vertex1.forwardsDistances == null )
            vertex1.forwardsDistances = new int[ vertices.length ];
        vertex1.addForwardDistance( ID2, weight );

        // add backward edge and forward weight
        if ( vertex2.backwardsDistances == null )
            vertex2.backwardsDistances = new int[ vertices.length ];

        vertex2.addBackwardNeighbour( vertex1 );
        vertex2.addBackwardDistance( ID1, 0 );
    }
}
