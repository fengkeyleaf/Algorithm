package coding.leetcode.hard;

/*
 * ReconstructItinerary.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/2/2022$
 */

import com.fengkeyleaf.util.graph.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * <a href="https://leetcode.com/problems/reconstruct-itinerary/">332. Reconstruct Itinerary</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// http://www.graph-magics.com/articles/euler.php
public final class ReconstructItinerary {

    static class Departure extends Vertex {
        final String city;

        Departure( String city ) {
            this.city = city;
        }

        @Override
        public String toString() {
            return city;
        }
    }
    static final String s = "JFK";
    final TreeMap<String, Departure> m = new TreeMap<>();

    public List<String> findItinerary( List<List<String>> tickets ) {
        getGraph( tickets );
        return m.get( s ).eulerianpath().stream().map( v -> ( ( Departure ) v ).city ).toList();
    }

    final TreeMap<String, List<String>> map = new TreeMap<>();

    private void getGraph( List<List<String>> tickets ) {
        tickets.forEach( t -> {
            if ( map.containsKey( t.get( 0 ) ) ) {
                map.get( t.get( 0 ) ).add( t.get( 1 ) );
                return;
            }

            List<String> n = new ArrayList<>();
            n.add( t.get( 1 ) );
            map.put( t.get( 0 ), n );
        } );

        map.values().forEach( v -> v.sort( String::compareTo ) );

        map.keySet().forEach( k -> {
            Departure d1 = getDeparture( k );

            map.get( k ).forEach( n -> {
                Departure d2 = getDeparture( n );
                Vertex.addDirected( d1, d2 );
            } );
        } );
    }

    private Departure getDeparture( String c ) {
        if ( m.containsKey( c ) )
            return m.get( c );

        Departure p = new Departure( c );
        m.put( c, p );
        return p;
    }

    static
    void test1() {
        List<List<String>> T = new ArrayList<>();
        List<String> L = new ArrayList<>( 2 );
        L.add( "MUC" );
        L.add( "LHR" );
        T.add( L );

        L = new ArrayList<>( 2 );
        L.add( "JFK" );
        L.add( "MUC" );
        T.add( L );

        L = new ArrayList<>( 2 );
        L.add( "SFO" );
        L.add( "SJC" );
        T.add( L );

        L = new ArrayList<>( 2 );
        L.add( "LHR" );
        L.add( "SFO" );
        T.add( L );

        System.out.println( new ReconstructItinerary().findItinerary( T ) );
    }

    static
    void test2() {
        List<List<String>> T = new ArrayList<>();
        List<String> L = new ArrayList<>( 2 );
        L.add( "JFK" );
        L.add( "SFO" );
        T.add( L );

        L = new ArrayList<>( 2 );
        L.add( "JFK" );
        L.add( "ATL" );
        T.add( L );

        L = new ArrayList<>( 2 );
        L.add( "SFO" );
        L.add( "ATL" );
        T.add( L );

        L = new ArrayList<>( 2 );
        L.add( "ATL" );
        L.add( "JFK" );
        T.add( L );

        L = new ArrayList<>( 2 );
        L.add( "ATL" );
        L.add( "SFO" );
        T.add( L );

        System.out.println( new ReconstructItinerary().findItinerary( T ) );
    }

    public static
    void main( String[] args ) {
//        test1();
        test2();
    }
}
