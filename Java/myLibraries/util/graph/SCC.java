package myLibraries.util.graph;

/*
 * SCC.java
 *
 * JDK: 14
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.graph.elements.InternetFlowVertex;
import myLibraries.util.graph.elements.UnionFindVertex;
import myLibraries.util.graph.elements.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of a strongly connected component, SCC
 *
 * @author       Xiaoyu Tongyang
 */

public class SCC extends Graph<UnionFindVertex> {
    // SCCs with their leading vertices
    private final List<UnionFindVertex> SCCs = new ArrayList<>();

    /**
     * make sure that we iterate vertices by their finishes time
     *
     * time complexity: O(n)
     * */

    private void changeIteratedVertices() {
        vertices.clear();
        for ( Vertex vertex : finishes )
            vertices.add( ( UnionFindVertex ) vertex);
    }

    /**
     * find all SCCs with two DFSs on the original graph
     * and on the reversed one
     *
     * time complexity: O(n + m)
     *
     * @param goForward true, DFS on the original graph;
     *                  false, on the reversed one
     * */

    public void findSCCs( boolean goForward ) {
        explored = new boolean[ vertices.size() + 1 ];
        if ( !goForward ) changeIteratedVertices();

        for ( int i = vertices.size() - 1; i >= 0; i-- ) {
            UnionFindVertex aVertex = vertices.get( i );

            // skip those visited vertices
            if ( !explored[ aVertex.ID ] ) {
                if ( goForward ) sortVertexByFinishTime( aVertex );
                else {
                    SCCs.add( aVertex );
                    separateEachSCC( aVertex, aVertex );
                }
            }
        }
    }

    /**
     * separate each SCC with DFS on the reversed graph
     *
     * time complexity: O(n + m)
     * */

    public void separateEachSCC(
            UnionFindVertex aVertex, UnionFindVertex leader ) {
        explored[ aVertex.ID ] = true;

        for ( UnionFindVertex neighbour : aVertex.reversedNeighbours ) {
            if ( !explored[ neighbour.ID ] ) {
                separateEachSCC( neighbour, leader );
            }
        }

        // combine the vertex of the same group into one with Union-find
        // note that this union operation is O(1),
        // because each time we merge at most one vertex
        // and also every vertex is merged only once,
        // so the total cost for this union operation is O(n)
        // which is different from the union operation for MST
        UnionFind.doUnion( leader.group, aVertex.group );
    }

    /**
     * create a vertex for DAG
     * */

    public UnionFindVertex createVertex( int ID, int totalVertices ) {
        UnionFindVertex vertex = new UnionFindVertex( ID );
        vertex.addEdge = new boolean[ totalVertices ];
        // avoid adding an edge back to itself
        vertex.addEdge[ vertex.ID ] = true;
        return vertex;
    }

    /**
     * create a vertex and its edges
     * */

    public void buildingGraph( int ID1, int ID2,
                               UnionFindVertex[] vertices ) {
        assert vertices != null;
        // used to index vertices array
        ID1--;
        ID2--;

        // created these vertices before?
        if ( vertices[ ID1 ] == null )
            vertices[ ID1 ] = createVertex(
                    ID1 + 1, vertices.length + 1 );

        if ( vertices[ ID2 ] == null )
            vertices[ ID2 ] = createVertex(
                    ID2 + 1, vertices.length + 1 );

        // add edges
        UnionFindVertex vertex1 = vertices[ ID1 ];
        UnionFindVertex vertex2 = vertices[ ID2 ];
        // add original edge
        // avoid adding repeated edge
        if ( !vertex1.addEdge[ vertex2.ID ] ) {
            vertex1.addEdge[ vertex2.ID ] = true;
            vertex1.add( vertex2 );
            // add reversed edge
            vertex2.addReverse( vertex1 );
        }
    }

    /**
     * build a DAG from this SCC
     * */

    public DAG<UnionFindVertex> buildingDAG() {
        DAG<UnionFindVertex> aDAG = new DAG<>();

        // map each leader vertex to a new ID
        int[] newIDs = new int[ vertices.size() + 1 ];
        for ( int i = 0; i < SCCs.size(); i++ )
            newIDs[ SCCs.get( i ).ID ] = i + 1;

        // build the DAG
        UnionFindVertex[] vertices = new UnionFindVertex[ SCCs.size() ];
        for ( Vertex vertex1 : SCCs ) {
            boolean isolated = true;

            // add a edge if one SCC can reach another one
            for ( Vertex member : vertex1.group.vertices ) {
                for ( Vertex neighbour : member.neighbours ) {
                    // use Union-find to see if two vertex belongs to two different SCCs
                    if ( !UnionFind.findSameUnion( vertex1, neighbour ) ) {
                        isolated = false;
                        buildingGraph( newIDs[ vertex1.ID ],
                                newIDs[ neighbour.leader.ID ], vertices);
                    }
                }
            }

            // special case
            // add this SCC into the graph when it is completely isolated,
            // i.e. having neither in-coming nor out-coming edges
            if ( isolated &&
                    vertices[ newIDs[ vertex1.ID ] - 1 ] == null )
                vertices[ newIDs[ vertex1.ID ] - 1 ] = new UnionFindVertex( newIDs[ vertex1.ID ] );
        }

        // add all vertices into the DAG
        for ( UnionFindVertex vertex : vertices )
            aDAG.add( vertex );

        return aDAG;
    }
}
