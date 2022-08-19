package coding.leetcode.hard;

/*
 * CriticalConnectionsInANetwork.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/5/2022$
 */

import com.fengkeyleaf.util.graph.Graph;
import com.fengkeyleaf.util.graph.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * <a href="https://leetcode.com/problems/critical-connections-in-a-network/">1192. Critical Connections in a Network</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://www.bilibili.com/video/BV1Q7411e7bM?p=2&share_source=copy_web&vd_source=98f92a0e5bc605c2c2ec72eaef324a99
// https://zhuanlan.zhihu.com/p/101923309
// https://www.cnblogs.com/yanyiming10243247/p/9294160.html
// https://cp-algorithms.com/graph/bridge-searching.html
// https://en.wikipedia.org/wiki/Biconnected_component
public final class CriticalConnectionsInANetwork {

    static class Server extends Vertex {
        final int id;

        Server( int id ) {
            this.id = id;
        }

        @Override
        public String toString() {
            return String.valueOf( id );
        }
    }

    public List<List<Integer>> criticalConnections( int n, List<List<Integer>> connections ) {
        return getGraph( n, connections ).getBridges().stream().map( L -> {
            List<Integer> N = new ArrayList<>( 2 );
            N.add( ( ( Server ) L.get( 0 ) ).id );
            N.add( ( ( Server ) L.get( 1 ) ).id );
            return N;
        } ).toList();
    }

    final TreeMap<Integer, Server> m = new TreeMap<>();

    private Graph<Server> getGraph( int n, List<List<Integer>> connections ) {
        connections.forEach( c -> {
            Server s1 = getServer( c.get( 0 ) );
            Server s2 = getServer( c.get( 1 ) );
            Vertex.addUndirected( s1, s2 );
        } );

        for ( int i = 0; i < n; i++ ) {
            if ( !m.containsKey( i ) )
                m.put( i, new Server( i ) );
        }

        Graph<Server> g = new Graph<>( m.values() );
        System.out.print( g );
        g.tarjan();
        System.out.println( g.getArticulations() );
        return g;
    }

    private Server getServer( int i ) {
        if ( m.containsKey( i ) )
            return m.get( i );

        Server s = new Server( i );
        m.put( i, s );
        return s;
    }

    static
    void test1() {
        List<List<Integer>> C = new ArrayList<>();
        List<Integer> L = new ArrayList<>( 2 );
        L.add( 0 );
        L.add( 1 );
        C.add( L );

        L = new ArrayList<>( 2 );
        L.add( 1 );
        L.add( 2 );
        C.add( L );

        L = new ArrayList<>( 2 );
        L.add( 2 );
        L.add( 0 );
        C.add( L );

        L = new ArrayList<>( 2 );
        L.add( 1 );
        L.add( 3 );
        C.add( L );

        System.out.println( new CriticalConnectionsInANetwork().criticalConnections( 4, C ) );
    }

    static
    void test2() {
        List<List<Integer>> C = new ArrayList<>();
        List<Integer> L = new ArrayList<>( 2 );
        L.add( 0 );
        L.add( 1 );
        C.add( L );

        L = new ArrayList<>( 2 );
        L.add( 0 );
        L.add( 2 );
        C.add( L );

        L = new ArrayList<>( 2 );
        L.add( 0 );
        L.add( 3 );
        C.add( L );

        L = new ArrayList<>( 2 );
        L.add( 0 );
        L.add( 4 );
        C.add( L );

        L = new ArrayList<>( 2 );
        L.add( 1 );
        L.add( 5 );
        C.add( L );

        L = new ArrayList<>( 2 );
        L.add( 5 );
        L.add( 6 );
        C.add( L );

        L = new ArrayList<>( 2 );
        L.add( 4 );
        L.add( 6 );
        C.add( L );

        L = new ArrayList<>( 2 );
        L.add( 4 );
        L.add( 7 );
        C.add( L );

        L = new ArrayList<>( 2 );
        L.add( 6 );
        L.add( 7 );
        C.add( L );

        System.out.println( new CriticalConnectionsInANetwork().criticalConnections( 8, C ) );
    }

    public static
    void main( String[] args ) {
//        test1();
        test2();
    }
}
