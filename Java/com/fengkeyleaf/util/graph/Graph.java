package com.fengkeyleaf.util.graph;

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

import com.fengkeyleaf.util.Node;

import java.util.*;

/**
 * Data structure of a Graph
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// TODO: 10/30/2021 class tree can be merged into this class, Graph;
//  i.e. graph represents n-array tree.
public class Graph<V extends Vertex> implements Iterable<V> {
    final List<V> vertices;
    List<Edge> edges;

    public Graph() { vertices = new ArrayList<>(); }

    public Graph( int capacity ) {
        vertices = new ArrayList<>( capacity );
    }

    public Graph( Collection<V> c ) {
        this( c.size() );
        vertices.addAll( c );
    }

    // TODO: 7/28/2022 get rid of this one.
    // array to indicate if a vertex has been visited before
    protected boolean[] explored;
    // array to store vertices by their finishing time
    final List<Vertex> finishes = new ArrayList<>();

    /**
     * add a vertex into this graph
     * */

    public void add( V vertex ) {
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

    public V getVertexByID( int ID ) {
       return vertices.get( ID - 1 );
    }

    public V getVertexByIndex( int index ) {
        return vertices.get( index );
    }

    public V setVertex( int index, V vertex ) {
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

    void sortVertexByFinishTime( Vertex v ) {
        assert v.mappingID == -1;
        v.mappingID = 0;

        for ( Vertex n : v.neighbours ) {
            if ( n.mappingID < 0 ) {
                sortVertexByFinishTime( n );
            }
        }

        finishes.add( v );
    }

    /**
     * DFS to find all reachable vertices and count their number
     *
     * @deprecated move this one into {@link Vertex#countNumber()}
     * */

    @Deprecated
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

    //-------------------------------------------------------
    // Bipartite
    //-------------------------------------------------------

    /**
     * Is this graph a bipartite graph?
     */

    public boolean isBipartite() {
        if( isEmpty() ) return false;

        // BFS starting at each vertex in this graph,
        // and label every level.
        vertices.forEach( this::isBipartite );

        for ( Vertex v : vertices ) {
            for ( Vertex n : v.neighbours ) {
                // v and it neighbours cannot be the same level
                // to be a bipartite graph.
                if ( n.mappingID == v.mappingID )
                    return false;
            }
        }

        return true;
    }

    private void isBipartite( Vertex s ) {
        if ( s.mappingID > -1 ) return;

        LinkedList<Vertex> q = new LinkedList<>();
        q.add( s );
        q.getLast().mappingID = 0;
        int id = 1; // level id.
        int preSize = 1, curSize = 0;

        // BFS procedure
        while ( !q.isEmpty() ) {
            Vertex v = q.poll();

            // traverse neighbors.
            for ( Vertex n : v.neighbours ) {
                if ( n.mappingID > -1 ) continue;

                assert n.mappingID == -1 : v;
                n.mappingID = id; // label it with level id.

                q.add( n );
                curSize++;
            }

            // reach next level.
            if ( --preSize <= 0 ) {
                id++;
                assert id > -1 : "Integer overflow";
                preSize = curSize;
                curSize = 0;
            }
        }
    }

    //-------------------------------------------------------
    // tarjan
    //-------------------------------------------------------

    /**
     * Run tarjan's algorithm to find bridges and articulation points.
     *
     * Note that this graph must be an undirected graph.
     */

    // bridges
    List<List<Vertex>> B;
    // articulations
    List<Vertex> A;

    // TODO: 8/6/2022 need to check if the graph is connected or not?
    // reference resources:
    // https://en.wikipedia.org/wiki/Biconnected_component
    // https://www.bilibili.com/video/BV1Q7411e7bM?p=2&share_source=copy_web&vd_source=98f92a0e5bc605c2c2ec72eaef324a99
    public void tarjan() {
        // reset parent.
        vertices.forEach( v -> v.parent = null );
        // set indexing.
        Node.setMappingID( vertices, 0 );

        B = new ArrayList<>();
        A = new ArrayList<>();
        boolean[] visited = new boolean[ vertices.size() ];
        int[] dfn = new int[ vertices.size() ];
        int[] low = new int[ vertices.size() ];
        int d = 1;

        for ( V v : this )
            d = tarjan( v, d, visited, dfn, low, B, A );
    }

    // GetArticulationPoints(i, d)
    private static<V extends Vertex>
    int tarjan( V v, int d, boolean[] visited,
                int[] dfn, int[] low,
                List<List<Vertex>> B, List<Vertex> A ) {

        // i = v.mappingID.
        int i = v.mappingID;
        // visited[i] := true
        visited[ i ] = true;
        // depth[i] := d
        dfn[ i ] = d;
        // low[i] := d
        low[ i ] = d;
        // childCount := 0
        int c = 0;
        // isArticulation := false
        boolean isArticulation = false;

        // for each ni in adj[i] do
        for ( Vertex n : v.neighbours ) {
            int ni = n.mappingID;

            // if not visited[ni] then
            if ( !visited[ ni ] ) {
                // parent[ni] := i
                n.parent = v;
                // GetArticulationPoints(ni, d + 1)
                assert d + 1 > 0 : "Integer overflow";
                d = tarjan( n, d + 1, visited, dfn, low, B, A );
                // childCount := childCount + 1
                c++;
                assert c > 0 : "Integer overflow";
                // if low[ni] ≥ depth[i] then
                if ( low[ ni ] >= dfn[ i ] )
                    // isArticulation := true
                    isArticulation = true;
                // low[i] := Min (low[i], low[ni])
                low[ i ] = Math.min( low[ i ], low[ ni ] );
                continue;
            }

            // else if ni ≠ parent[i] then
            if ( n != v.parent )
                // low[i] := Min (low[i], depth[ni])
                low[ i ] = Math.min( low[ i ], dfn[ ni ] );
        }

        // if ( parent[i] ≠ null and isArticulation ) or
        // ( parent[i] = null and childCount > 1 ) then
        if ( v.parent != null && isArticulation ||
                v.parent == null && c > 1 )
            // Output i as articulation point
            A.add( v );

        // find bridges with one end as i(v).
        v.neighbours.forEach( n -> {
            // if low[ni] ≥ depth[i] then
            if ( low[ n.mappingID ] > dfn[ i ] ) {
                // Output edge i -> ni as a bridge.
                List<Vertex> L = new ArrayList<>( 2 );
                L.add( v );
                L.add( n );
                B.add( L );
            }
        } );

        return d;
    }

    public List<List<Vertex>> getBridges() {
        return B;
    }

    public List<Vertex> getArticulations() {
        return A;
    }

    @Override
    public Iterator<V> iterator() {
        return vertices.iterator();
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder( "[ " );
        for ( int i = 0; i < vertices.size(); i++ ) {
            text.append( vertices.get( i ) );
            if ( i != vertices.size() - 1 )
                text.append( ", " );
        }
        return text.append( " ]\n" ).toString();
    }
}
