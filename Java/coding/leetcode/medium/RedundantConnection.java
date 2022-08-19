package coding.leetcode.medium;

/*
 * RedundantConnection.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/30/2022$
 */

import com.fengkeyleaf.util.graph.UnionFind;
import com.fengkeyleaf.util.graph.Vertex;

import java.util.Arrays;
import java.util.TreeMap;

/**
 * <a href="https://leetcode.com/problems/redundant-connection/">684. Redundant Connection</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public class RedundantConnection {

    final TreeMap<Integer, Vertex> m = new TreeMap<>();

    public int[] findRedundantConnection( int[][] edges ) {
        for ( int[] e : edges ) {
            Vertex v1 = getVertex( e[ 0 ] );
            Vertex v2 = getVertex( e[ 1 ] );

            if ( !union( v1, v2 ) ) return e;
        }

        assert false;
        return null;
    }

    private static
    boolean union( Vertex v1, Vertex v2 ) {
        if ( v1.isSameUnion( v2 ) ) return false;

        v1.union( v2 );
        return true;
    }

    private Vertex getVertex( int i ) {
        if ( m.containsKey( i ) )
            return m.get( i );

        Vertex v = new Vertex();
        new UnionFind( v );
        m.put( i, v );
        return v;
    }

    public static
    void main( String[] args ) {
        System.out.println( Arrays.toString( new RedundantConnection().findRedundantConnection( new int[][] { { 1, 2 }, { 1, 3 }, { 2, 3 } } ) ) );
        System.out.println( Arrays.toString( new RedundantConnection().findRedundantConnection( new int[][] { { 1, 2 }, { 2, 3 }, { 3, 4 }, { 1, 4 }, { 1, 5 } } ) ) );
    }
}
