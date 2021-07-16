package myLibraries.util.graph.tools;

/*
 * Graphs.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.DCEL.DCEL;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.DCEL.HalfEdge;
import myLibraries.util.graph.Graph;
import myLibraries.util.graph.elements.DualVertex;
import myLibraries.util.graph.elements.Vertex;

import java.util.Arrays;
import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to Graph
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Graphs {

    /**
     * building Dual graph
     * */

    private static
    void buildingDualGraph( int ID1, Face face1, HalfEdge neighbourEdge,
                            int ID2, Face face2, DualVertex[] vertices ) {
        assert vertices != null;

        // created these vertices before?
        if ( vertices[ ID1 ] == null ) {
            vertices[ ID1 ] = new DualVertex( ID1, face1 );
            face1.IDOfDualVertex = ID1;
        }

        if ( vertices[ ID2 ] == null ) {
            vertices[ ID2 ] = new DualVertex( ID2, face2 );
            face2.IDOfDualVertex = ID2;
        }

        // add edges
        DualVertex vertex1 = vertices[ ID1 ];
        DualVertex vertex2 = vertices[ ID2 ];
        vertex1.add( vertex2 );
        vertex1.add( neighbourEdge );
    }

    /**
     * building Dual graph
     * */

    public static
    Graph<DualVertex> getDualGraph( List<Face> faces, Face infinite ) {
        Graph<DualVertex> graph = new Graph<>();
        if ( faces == null || faces.size() < 2 ) return graph;

        assert infinite != null;
        // map face's ID to its vertex's ID,
        // except that infinite's ID is -1
        int[] IDs = new int[ faces.size() ];
        int ID = 0;
        for ( Face value : faces ) {
            if ( value == infinite ) IDs[ value.ID ] = -1;
            else IDs[ value.ID ] = ID++;
        }

        DualVertex[] vertices = new DualVertex[ faces.size() ];
        // for each face, except for the infinite one
        for ( Face face : faces ) {
            if ( face == infinite ) continue;

            // create a graph vertex for it,
            // and connect it to all faces around it,
            // also excluding the infinite one
            List<HalfEdge> edges = DCEL.walkAroundEdge( face );
            for ( HalfEdge edge : edges ) {
                if ( edge.twin.incidentFace != infinite ) {
                    buildingDualGraph( IDs[ face.ID ], face, edge,
                            IDs[ edge.twin.incidentFace.ID ], edge.twin.incidentFace,
                            vertices );
                }
            }

            if ( faces.size() == 2 )
                vertices[ IDs[ face.ID ] ] = new DualVertex( IDs[ face.ID ], face );
        }

        Arrays.asList( vertices ).forEach( v -> {
           if ( v != null ) graph.add( v );
        } );
        return graph;
    }
}
