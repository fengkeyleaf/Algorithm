package coding.leetcode.medium;

/*
 * NetworkDelayTime.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/30/2022$
 */

import com.fengkeyleaf.util.graph.Graph;
import com.fengkeyleaf.util.graph.SingleShortestPath;
import com.fengkeyleaf.util.graph.Vertex;

import java.util.TreeMap;

/**
 * <a href="https://leetcode.com/problems/network-delay-time/">743. Network Delay Time</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public class NetworkDelayTime {

    final TreeMap<Integer, Vertex> m = new TreeMap<>();

    public int networkDelayTime( int[][] times, int n, int k ) {
        return findShortest( getGraph( times, n ), getVertex( k ) );
    }

    private int findShortest( Graph<Vertex> g, Vertex s ) {
        SingleShortestPath.dijkstra( s, g );

        long max = 0;
        for ( Vertex v : g ) {
            if ( v != s && v.parent == null )
                return -1;

            max = Math.max( max, v.getShortestWeight() );
        }

        return ( int ) max;
    }

    private Graph<Vertex> getGraph( int[][] times, int n ) {
        for ( int[] t : times ) {
            Vertex v1 = getVertex( t[ 0 ] );
            Vertex v2 = getVertex( t[ 1 ] );

            v1.add( v2, t[ 2 ] );
        }

        for ( int i = 1; i <= n; i++ ) {
            if ( m.containsKey( i ) ) continue;

            m.put( i, new Vertex() );
        }

        return new Graph<>( m.values() );
    }

    private Vertex getVertex( int i ) {
        if ( m.containsKey( i ) )
            return m.get( i );

        Vertex v = new Vertex();
        m.put( i, v );
        return v;
    }

    public static
    void main( String[] args ) {
//        System.out.println( new NetworkDelayTime().networkDelayTime( new int[][] { { 2, 1, 1 }, { 2, 3, 1 }, { 3, 4, 1 } }, 4, 2 ) );
//        System.out.println( new NetworkDelayTime().networkDelayTime( new int[][] { { 2, 1, 1 }, { 2, 3, 1 }, { 3, 4, 1 } }, 4, 2 ) );
        System.out.println( new NetworkDelayTime().networkDelayTime( new int[][] { { 1, 2, 1 }, { 2, 3, 7 }, { 1, 3, 4 }, { 2, 1, 2 } }, 4, 1 ) );
    }
}
