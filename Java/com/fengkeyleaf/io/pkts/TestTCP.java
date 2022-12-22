package com.fengkeyleaf.io.pkts;

/*
 * TestTCP.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/13/2022$
 */

/**
 * Test TCP
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

//            source port            |             Destination port
//                            Sequence number
//                      Acknowledgment number (if ACK set)
// Data offset | Reserved | NS, CWR, ECE, URG, ACK, PSH, RST, SYN, FIN | Window Size
//             Checksum              |         Urgent pointer (if URG set)
//    Options (if data offset > 5. Padded at the end with "0" bits if necessary.)
//

public final class TestTCP {

    public static final TCP t1 = new TCP(
            3293, 443,
            1238177539, 1593465588, 5,
            "000", "000010000",
            528, 0, "0", null
    );
    public static final TCP t2 = new TCP(
                49219, 443,
                        2112741785, 1445843972, 5,
                        "000", "000010000",
                        4095, 0, "0", null
    );
    public static final TCP t3 = new TCP(
            49371, 3293,
            2040707015, 1044374827, 5,
            "000", "000010000",
            4095, 0, "0", null
    );
    public static final TCP t4 = new TCP(
            443, 49371,
            1044374827, 2000707015, 5,
            "000", "000011000",
            8, 0, "0", null
    );;
    public static final TCP t5 = new TCP(
            443, 49371,
            1044374827, 2000707015, 5,
            "000", "101011010",
            8, 0, "0", null
    );;
    public static final TCP t6 = new TCP(
            3293, 443,
            1238177539, 1593465588, 5,
            "000", "000010000",
            528, 0, "0", new byte[ 0 ]
    );;
//    public static final TCP t7;

    private static
    void test1() {
        t1.getBytes();
    }

    private static
    void test2() {
        t2.getBytes();

        t3.getBytes();

        t4.getBytes();

        t5.getBytes();
    }

    private static
    void test3() {
        System.out.println( t6.getBytes().length );
        System.out.println( TCP.getTCP( t6.getBytes() ).ackNum() );
    }

    // -enableassertions
    public static
    void main( String[] args ) {
//        test1();
//        test2();
//        test3();

//        System.out.println( Math.pow( 2, 16 ) );
//        System.out.println( 1 << 16 ); // 65536
//        System.out.println( ~( 1 << 16 ) ); // -65537
//        System.out.println( ~( 1 << 16 ) + ( 1 << 16 ) ); // -1
//        System.out.println( ~( ~( 1 << 16 ) + ( 1 << 16 ) ) ); // 0

        byte i = ( byte ) 1;
        System.out.println( ( int ) i << 8 );
        System.out.println( ( ( int ) i ) << 8 );
    }
}
