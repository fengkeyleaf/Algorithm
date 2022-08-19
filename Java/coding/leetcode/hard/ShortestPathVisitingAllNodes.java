package coding.leetcode.hard;

/*
 * ShortestPathVisitingAllNodes.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/31/2022$
 */

import com.fengkeyleaf.util.Bitmask;
import com.fengkeyleaf.util.graph.Graph;
import com.fengkeyleaf.util.graph.Vertex;

import java.util.*;

/**
 * <a href="https://leetcode.com/problems/shortest-path-visiting-all-nodes/">847. Shortest Path Visiting All Nodes</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public final class ShortestPathVisitingAllNodes {

    private int end;

    class Status implements Iterable<Vertex> {
        int s;
        Vertex cur;
        int cost;

        Status( Vertex v ) {
            cur = v;
            s = Bitmask.add( s, v.mappingID );
        }

        Status( int bitMask, Vertex n, int cost ) {
            s = bitMask;
            cur = n;
            this.cost = cost;
        }

        boolean isVisitedAll() {
            return Bitmask.containsAll( s, end );
        }

        int add( int n ) {
            return Bitmask.add( s, n );
        }

        @Override
        public Iterator<Vertex> iterator() {
            return cur.getNeighbours().iterator();
        }

        @Override
        public String toString() {
            return Integer.toBinaryString( s ) + " | " + cur.mappingID + " | " + cost;
        }
    }

    // TODO: 8/2/2022 time limit exceeded, due to this condition?
    final TreeSet<Status> s = new TreeSet<>( ( s1, s2 ) -> {
//        if ( s1.s == s2.s && s1.cur == s2.cur && s1.cost == s2.cost )
        if ( s1.s == s2.s && s1.cur == s2.cur )
            return 0;

        return 1;
    } );

    // https://leetcode.com/problems/shortest-path-visiting-all-nodes/discuss/135809/Fast-BFS-Solution-(46ms)-Clear-Detailed-Explanation-Included
    // https://leetcode.com/problems/shortest-path-visiting-all-nodes/discuss/549233/Breadth-First-Search(BFS)with-intuitive-approach-Thinking-process-or-13-ms
    public int shortestPathLength( int[][] graph ) {
        getEndBit( graph.length );

        Queue<Status> q = new LinkedList<>();
        getGraph( graph ).forEach( v -> {
            Status s = new Status( v );
            q.add( s );
            this.s.add( s );
        } );

        while ( !q.isEmpty() ) {
            Status s = q.poll();

            if ( s.isVisitedAll() ) return s.cost;

            for ( Vertex n : s ) {
                int bitMask = s.add( n.mappingID );
                Status next = new Status( bitMask, n, s.cost + 1 );
                if ( this.s.contains( next ) ) continue;

                q.add( next );
                this.s.add( next );
            }
        }

        assert false;
        return -1;
    }

    private void getEndBit( int n ) {
        int m = 1;
        while ( n-- > 0 ) {
            end = Bitmask.add( end, m );
            m <<= 1;
        }
    }

    final TreeMap<Integer, Vertex> m = new TreeMap<>();

    private Graph<Vertex> getGraph( int[][] graph ) {
        for ( int i = 0; i < graph.length; i++ ) {
            Vertex v1 = getVertex( i );

            for ( int j = 0; j < graph[ i ].length; j++ ) {
                Vertex v2 = getVertex( graph[ i ][ j ] );

                v1.add( v2 );
            }
        }

        Graph<Vertex> g = new Graph<>( m.values() );
        return g;
    }

    private Vertex getVertex( int i ) {
        if ( m.containsKey( i ) )
            return m.get( i );

        Vertex v = new Vertex();
        v.mappingID = getBitMask( i );
        m.put( i, v );
        return v;
    }

    private int getBitMask( int i ) {
        i++;
        int n = 1;
        while ( --i > 0 )
            n <<= 1;

        return n;
    }

    public static
    void main( String[] args ) {
//        System.out.println( new ShortestPathVisitingAllNodes().shortestPathLength( new int[][] { { 1 }, { 0 } } ) );
//        System.out.println( new ShortestPathVisitingAllNodes().shortestPathLength( new int[][] { { 1, 2, 3 }, { 0 }, { 0 }, { 0 } } ) );
//        System.out.println( new ShortestPathVisitingAllNodes().shortestPathLength( new int[][] { { 1 }, { 0, 2, 4 }, { 1, 3, 4 }, { 2 }, { 1, 2 } } ) );
        System.out.println( new ShortestPathVisitingAllNodes().shortestPathLength( new int[][] { { 2, 3, 4 ,5 }, { 2, 3, 4 }, { 0, 1 }, { 0, 1 }, { 0, 6, 1, 7, 9 }, { 0 }, { 4 }, { 4, 8 }, { 7 }, { 4, 10 }, { 9 } } ) );
//        System.out.println( new ShortestPathVisitingAllNodes().shortestPathLength( new int[][] { { 2, 3, 4 ,5 }, { 2, 3, 4 }, { 0, 1 }, { 0, 1 }, { 0, 6, 1, 7, 8 }, { 0 }, { 7, 4 }, { 6 }, { 4, 9 }, { 8 } } ) );
    }
}
