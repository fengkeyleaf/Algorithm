package coding.leetcode.medium;

/*
 * EvaluateDivision.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/28/2022$
 */

import com.fengkeyleaf.util.Node;
import com.fengkeyleaf.util.graph.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * <a href="https://leetcode.com/problems/evaluate-division/">399. Evaluate Division</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public final class EvaluateDivision {

    class GraphVertex extends Vertex {

        final List<Double> W = new ArrayList<>();

        void add( GraphVertex v, double w ) {
            assert neighbours.size() == W.size();
            neighbours.add( v );
            W.add( w );
        }

        public double getValue( int i ) {
            return W.get( i );
        }
    }

    final TreeMap<String, GraphVertex> m = new TreeMap<>();
    final List<Double> res = new ArrayList<>();

    public double[] calcEquation( List<List<String>> equations,
                                  double[] values, List<List<String>> queries ) {

        buildGraph( equations, values );
        return query( queries );
    }

    private double[] query( List<List<String>> Q ) {
        Q.forEach( q -> {
            GraphVertex s = getQueryVertex( q.get( 0 ) );
            GraphVertex e = getQueryVertex( q.get( 1 ) );

            res.add( findRes( s, e ) );
        } );

        double[] R = new double[ res.size() ];
        int idx = 0;
        for ( Double n : res ) {
            R[ idx++ ] = n;
        }

        return R;
    }

    private double findRes( GraphVertex s, GraphVertex e ) {
        if ( s == null || e == null ) return -1;
        if ( s == e ) return 1;

        List<GraphVertex> V = new ArrayList<>();
        double res = dfs( s, e, V );
        Node.resetMappingID( V );
        return res;
    }

    private double dfs( GraphVertex s, GraphVertex e, List<GraphVertex> V ) {
        if ( s.mappingID > -1 ) return -1;

        assert s.mappingID == -1;
        s.mappingID = 0;
        V.add( s );

        double res = 1;
        for ( int i = 0; i < s.getNeighbours().size(); i++ ) {
            GraphVertex n = ( GraphVertex ) s.getNeighbours().get( i );
            if ( n == e ) return s.getValue( i );

            res = dfs( n, e, V );
            if ( res >= 0 ) return res * s.getValue( i );
        }

        return -1;
    }

    private GraphVertex getQueryVertex( String s ) {
        if ( m.containsKey( s ) )
            return m.get( s );

        return null;
    }

    private void buildGraph( List<List<String>> E, double[] V ) {
        for ( int i = 0; i < E.size(); i++ ) {
            GraphVertex v1 = getVertex( E.get( i ).get( 0 ) );
            GraphVertex v2 = getVertex( E.get( i ).get( 1 ) );

            v1.add( v2, V[ i ] );
            v2.add( v1, 1 / V[ i ] );
        }
    }

    private GraphVertex getVertex( String s ) {
        if ( m.containsKey( s ) )
            return m.get( s );

        GraphVertex v = new GraphVertex();
        m.put( s, v );
        return v;
    }

    static
    void test1() {
        List<List<String>> E = new ArrayList<>();
        E.add( List.of( new String[] { "a", "b" } ) );
        E.add( List.of( new String[] { "b", "c" } ) );

        double[] values = new double[] { 2.0,3.0 };
        List<List<String>> queries = new ArrayList<>();
        queries.add( List.of( new String[] { "a","c" } ) );
        queries.add( List.of( new String[] { "b","a" } ) );
        queries.add( List.of( new String[] { "a","e" } ) );
        queries.add( List.of( new String[] { "a","a" } ) );
        queries.add( List.of( new String[] { "x","x" } ) );

        System.out.println( Arrays.toString( new EvaluateDivision().calcEquation( E, values, queries ) ) );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
