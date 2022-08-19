package coding.leetcode.medium;

/*
 * IsGraphBipartite.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/28/2022$
 */

import com.fengkeyleaf.util.graph.Graph;
import com.fengkeyleaf.util.graph.Vertex;

import java.util.TreeMap;

/**
 * <a href="https://leetcode.com/problems/is-graph-bipartite/">785. Is Graph Bipartite?</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public final class IsGraphBipartite {
    TreeMap<Integer, Vertex> m = new TreeMap<>();

    public boolean isBipartite( int[][] graph ) {
        return getGraph( graph ).isBipartite();
    }

    private Graph<Vertex> getGraph( int[][] graph ) {
        for ( int i = 0; i < graph.length; i++ ) {
            Vertex v1 = getVertex( i );

            for ( int n : graph[ i ] ) {
                Vertex v2 = getVertex( n );
                v1.add( v2 );
            }
        }

        return new Graph<>( m.values() );
    }

    private Vertex getVertex( int n ) {
        if ( m.containsKey( n ) )
            return m.get( n );

        Vertex v = new Vertex();
        m.put( n, v );
        return v;
    }

    public static
    void main( String[] args ) {
        System.out.println( new IsGraphBipartite().isBipartite( new int[][] { { 1, 2, 3 }, { 0, 2 }, { 0, 1, 3 }, { 0, 2 } } ) );
        System.out.println( new IsGraphBipartite().isBipartite( new int[][] { { 1, 3 }, { 0, 2 }, { 1, 3 }, { 0, 2 } } ) );
        System.out.println( new IsGraphBipartite().isBipartite( new int[][] { {}, { 2, 3 }, { 1, 3 }, { 0, 2 } } ) );
    }
}
