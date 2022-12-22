package com.fengkeyleaf.io.pkts;

/*
 * Test.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/2/2022$
 */

import java.util.Arrays;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

final class TestPacp {

    static final String FILE_NAME_1 = "src/CSCI651/proj1/icmp.pcap";
    static final String FILE_NAME_2 = "src/CSCI651/proj1/tcp.pcap";
    static final String FILE_NAME_3 = "src/CSCI651/proj1/udp.pcap";

    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Arrays.html#copyOfRange(boolean%5B%5D,int,int)
    static
    void testArrays() {
        int[] N = new int[] { 1, 2, 3, 4, 5, 6 };
//        System.out.println( Arrays.toString( Arrays.copyOfRange( N, 0, 3 ) ) );
//        System.out.println( Arrays.toString( Arrays.copyOfRange( N, 1, 4 ) ) );
        System.out.println( Arrays.toString( Arrays.copyOfRange( N, 6, 6 ) ) );

//        System.out.println( Integer.parseInt( "2a", 16 ) ); // 42
//        System.out.println( Integer.parseInt( "3f2", 16 ) ); // 1010

        String s = "1";
//        System.out.println( Arrays.toString( MyMath.bytesToBinary( s.getBytes() ) ) );
        System.out.println( Integer.toBinaryString(( byte ) 1 & 0xff ) );
        System.out.println( String.format( "%8s", Integer.toBinaryString(( byte ) 1 & 0xff ) ).replace( " ", "0"  ) );
    }

    static
    void testString() {
        String a = "hi";
        String b = "h";
        System.out.println( a == ( b + "i" ) );
        System.out.println( a == ( "h" + "i" ) );
    }

    static
    void testPacp1() {
        test( new Pacp( FILE_NAME_1 ) );
    }

    static
    void test( Pacp p ) {
        System.out.println( p.getGlobalHeader() );

        System.out.println();
//        System.out.println( p.P.get( 0 ).header() );
//        System.out.println( p.P.get( 0 ).header().getCapLen() );
//
//        System.out.println();
//        System.out.println( p.P.get( 0 ).data().getDesMac() );
//        System.out.println( p.P.get( 0 ).data().getSrcMac() );
        System.out.println( p.P.get( 0 ).data() );

        System.out.println();
//        System.out.println();
//        System.out.println( p.P.get( 0 ).data().iPv4().version() );
//        System.out.println( p.P.get( 0 ).data().iPv4().IHL() );
//        System.out.println( p.P.get( 0 ).data().iPv4().totalLen() );
//        System.out.println( p.P.get( 0 ).data().iPv4().protocolNum() );
//        System.out.println( Arrays.toString( p.P.get( 0 ).data().iPv4().desIPAddr() ) );
//        System.out.println( Arrays.toString( p.P.get( 0 ).data().iPv4().srcIPAddr() ) );
        System.out.println( p.getPackets().get( 0 ).data().iPv4() );

        System.out.println();
        System.out.println( p.P.get( 0 ).data().iPv4().protocol() );
    }

    static
    void testPacp2() {
        test( new Pacp( FILE_NAME_2 ) );
    }

    static
    void testPacp3() {
        test( new Pacp( FILE_NAME_3 ) );
    }

    static
    void testHost() {
        Pacp p = new Pacp( FILE_NAME_3 );
        System.out.println( p.getPackets().get( 0 ).containsHost( "10.230.205.207" ) );
        System.out.println( p.getPackets().get( 0 ).containsHost( "183.47.102.80" ) );
        System.out.println( p.getPackets().get( 0 ).containsHost( "183.00.102.80" ) );
    }

    static
    void testPort() {
        Pacp p2 = new Pacp( FILE_NAME_2 );
        System.out.println( p2.getPackets().get( 0 ).data().iPv4().protocol().tcp().srcPort() );
        System.out.println( p2.getPackets().get( 0 ).data().iPv4().protocol().tcp().desPort() );
        System.out.println( p2.getPackets().get( 0 ).containsPort( 3293 ) );
        System.out.println( p2.getPackets().get( 0 ).containsPort( 443 ) );
        System.out.println( p2.getPackets().get( 0 ).containsPort( 64075 ) );

        System.out.println();
        Pacp p3 = new Pacp( FILE_NAME_3 );
        System.out.println( p3.getPackets().get( 0 ).data().iPv4().protocol().udp().srcPort() );
        System.out.println( p3.getPackets().get( 0 ).data().iPv4().protocol().udp().desPort() );
        System.out.println( p3.getPackets().get( 0 ).containsPort( 8000 ) );
        System.out.println( p3.getPackets().get( 0 ).containsPort( 64074 ) );
        System.out.println( p3.getPackets().get( 0 ).containsPort( 64075 ) );
    }

    static
    void testProtocol() {
        Pacp p1 = new Pacp( FILE_NAME_1 );
        Pacp p2 = new Pacp( FILE_NAME_2 );
        Pacp p3 = new Pacp( FILE_NAME_3 );

        System.out.println( p1.getPackets().get( 0 ).isICMP() );
        System.out.println( p1.getPackets().get( 0 ).isUDP() );
        System.out.println( p1.getPackets().get( 0 ).isTCP() );

        System.out.println();
        System.out.println( p2.getPackets().get( 0 ).isICMP() );
        System.out.println( p2.getPackets().get( 0 ).isUDP() );
        System.out.println( p2.getPackets().get( 0 ).isTCP() );

        System.out.println();
        System.out.println( p3.getPackets().get( 0 ).isICMP() );
        System.out.println( p3.getPackets().get( 0 ).isUDP() );
        System.out.println( p3.getPackets().get( 0 ).isTCP() );
    }

    static
    void testNet() {
        Pacp p1 = new Pacp( FILE_NAME_1 );
        System.out.println( p1.getPackets().get( 0 ).containsNet( "10.230.205.207" ) );
        System.out.println( p1.getPackets().get( 0 ).containsNet( "10.230.205" ) );
        System.out.println( p1.getPackets().get( 0 ).containsNet( "10.230" ) );
        System.out.println( p1.getPackets().get( 0 ).containsNet( "10" ) );
        System.out.println( p1.getPackets().get( 0 ).containsNet( "11" ) );

        System.out.println();
        System.out.println( p1.getPackets().get( 0 ).containsNet( "183.232.231.173" ) );
        System.out.println( p1.getPackets().get( 0 ).containsNet( "183.232.231" ) );
        System.out.println( p1.getPackets().get( 0 ).containsNet( "183.232" ) );
        System.out.println( p1.getPackets().get( 0 ).containsNet( "183" ) );
        System.out.println( p1.getPackets().get( 0 ).containsNet( "184" ) );

        System.out.println( "--------------->" );
        Pacp p2 = new Pacp( FILE_NAME_2 );
        System.out.println( p2.getPackets().get( 0 ).containsNet( "10.230.205.207" ) );
        System.out.println( p2.getPackets().get( 0 ).containsNet( "10.230.205" ) );
        System.out.println( p2.getPackets().get( 0 ).containsNet( "10.230" ) );
        System.out.println( p2.getPackets().get( 0 ).containsNet( "10" ) );
        System.out.println( p2.getPackets().get( 0 ).containsNet( "11" ) );

        System.out.println();
        System.out.println( p2.getPackets().get( 0 ).containsNet( "182.61.200.109" ) );
        System.out.println( p2.getPackets().get( 0 ).containsNet( "182.61.200" ) );
        System.out.println( p2.getPackets().get( 0 ).containsNet( "182.61" ) );
        System.out.println( p2.getPackets().get( 0 ).containsNet( "182" ) );
        System.out.println( p2.getPackets().get( 0 ).containsNet( "184" ) );

        System.out.println( "--------------->" );
        Pacp p3 = new Pacp( FILE_NAME_3 );
        System.out.println( p3.getPackets().get( 0 ).containsNet( "10.230.205.207" ) );
        System.out.println( p3.getPackets().get( 0 ).containsNet( "10.230.205" ) );
        System.out.println( p3.getPackets().get( 0 ).containsNet( "10.230" ) );
        System.out.println( p3.getPackets().get( 0 ).containsNet( "10" ) );
        System.out.println( p3.getPackets().get( 0 ).containsNet( "11" ) );

        System.out.println();
        System.out.println( p3.getPackets().get( 0 ).containsNet( "183.47.102.80" ) );
        System.out.println( p3.getPackets().get( 0 ).containsNet( "183.47.102" ) );
        System.out.println( p3.getPackets().get( 0 ).containsNet( "183.47" ) );
        System.out.println( p3.getPackets().get( 0 ).containsNet( "183" ) );
        System.out.println( p3.getPackets().get( 0 ).containsNet( "184" ) );
    }

    public static
    void main( String[] args ) {
        testArrays();

//        testPacp1();
//        testPacp2();
//        testPacp3();

//        testHost();
//        testPort();
//        testProtocol();
//        testNet();
    }
}
