package com.fengkeyleaf.net;

/*
 * RIP1.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/29/2022$
 */

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.TreeMap;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// Linux:
// cd CSCI651/src
// javac ./com/fengkeyleaf/net/*.java

// Windows:
// cd C:\Users\fengk\OneDrive\documents\computerScience\Computational Geometry\programming_assignments\src
// javac .\com\fengkeyleaf\net\*.java

// java -enableassertions com.fengkeyleaf.net.RIP1
final class RIP1 extends RIP {
    static final String LOCAL_HOST = "127.0.0.1";

    RIP1() throws SocketException, UnknownHostException {
        super( LOCAL_HOST );

        buildTable( t, new String[] {
                LOCAL_HOST + " " + 0 + " " + LOCAL_HOST,
                RIP2.LOCAL_HOST + " " + 15 + " " + RIP2.LOCAL_HOST,
                RIP3.LOCAL_HOST + " " + 4 + " " + RIP3.LOCAL_HOST
        } );
        buildTableOrigin( originT, new String[] {
                LOCAL_HOST + " " + 0,
                RIP2.LOCAL_HOST + " " + 15,
                RIP3.LOCAL_HOST + " " + 4
        } );

        run();
    }

    static
    void buildTable( TreeMap<String, Data> t, String[] T ) {
        long time = System.currentTimeMillis();

        for ( String d : T ) {
            String[] C = d.split( " " );
            assert !t.containsKey( C[ 0 ] );
            // dst cost nextHub\n
//            t.put( C[ 0 ], new Data( Integer.parseInt( C[ 1 ] ), C[ 2 ], time ) );
        }
    }

    static
    void buildTableOrigin( TreeMap<String, Integer> t, String[] T ) {
        for ( String d : T ) {
            String[] C = d.split( " " );
            assert !t.containsKey( C[ 0 ] );
            // dst cost nextHub\n
            t.put( C[ 0 ], Integer.parseInt( C[ 1 ] ) );
        }
    }

    static
    void testTime() {
        Date d = new Date();
        long t = d.getTime();
        t = System.currentTimeMillis();
        try {
            Thread.sleep( 5000 );
        } catch ( InterruptedException e ) {
            throw new RuntimeException( e );
        }

        System.out.println( d.getTime() - t );
        System.out.println( System.currentTimeMillis() - t );
    }

    public static
    void main( String[] args )
            throws SocketException, UnknownHostException {
//        new RIP1();
//        System.out.println( 1 & 2 );
//        System.out.println( 1 | 2 );
//        System.out.println( Integer.toBinaryString( 1 ) );

        String mask = "255.255.255.0";
        System.out.println( RIP.initSubnet( "129.21.30.37", mask ) ); // 129.21.30.0\26
        System.out.println( RIP.initSubnet( "129.21.37.49", mask ) ); // 129.21.37.0\26
        System.out.println( RIP.initSubnet( "129.21.34.80", mask ) ); // 129.21.34.0\25
        System.out.println( RIP.initSubnet( "129.21.22.196", mask ) ); // 129.21.22.0\24
    }
}
