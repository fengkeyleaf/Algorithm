package com.fengkeyleaf.net;

/*
 * TestChecksum.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/21/2022$
 */

import com.fengkeyleaf.io.pkts.IPv4;
import com.fengkeyleaf.io.pkts.TCP;
import com.fengkeyleaf.io.pkts.TestTCP;
import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.net.MySocket;
import com.fengkeyleaf.util.MyArrays;
import com.fengkeyleaf.util.MyBitSet;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Test Checksum
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// http://www.metools.info/encoding/ecod129.html
final class TestChecksum {

    private static
    void test1() {
        byte[] B = new byte[] { 1 };
//        System.out.println( Arrays.toString( MyMath.bytesToHex( B ) ) );
//        System.out.println( MySocket.getChecksum( B ) == ~256 );

        B = new byte[] { 1, 2 };
//        System.out.println( MySocket.getChecksum( B ) == ~258 );

        B = new byte[] { 1, 2, 3 };
//        System.out.println( MySocket.getChecksum( B ) );
//        System.out.println( MySocket.getChecksum( B ) == ~1026 );

        B = new byte[] { 1, 2, 3, 4 };
//        System.out.println( MySocket.getChecksum( B ) == ~1030 );

        B = new byte[] { ( -128 + 64 + 32 + 4 + 2 ), 102, ( -128 + 64 + 16 + 4 + 1 ), 85 };
//        System.out.println( ( ( byte ) ( -128 + 64 + 32 + 4 + 2 ) ) & 0xff );
//        System.out.println( MySocket.getChecksum( B ) );
//        System.out.println( ~48060 );
//        System.out.println( CheckSum.getChecksum( B ) == ~48060 );
    }

    private static
    void test2() throws SocketException, UnknownHostException {
        BitSet b = new BitSet( 32 );
        // Fixed 8 bits are all zeros, skip.
        // protocol
        MyBitSet.setBits( b, 8, Integer.toBinaryString( 1 ) );
        // segment length
        MyBitSet.setBits( b, 16, Integer.toBinaryString( 2 ) );
//        System.out.println( b );
//        System.out.println( Arrays.toString( MyArrays.reverse( MyBitSet.toByteArray( b, 4 ) ) ) );

//        System.out.println( 0 & 0xff );
        TCP t = TestTCP.t1;
        int c = MySocket.getChecksum( MySocket.getChecksumBytes( t, InetAddress.getByName( "127.0.0.1" ), InetAddress.getByName( "127.0.0.2" ) ) );
        TCP p = new TCP( t.srcPort(), t.desPort(), t.seqNum(), t.ackNum(), t.headerLen(), t.reserved(), t.flags(), t.windowSize(), c, t.urgentPtr(), t.data() );
        MySocket r = new MySocket(  "127.0.0.2", 1235, "127.0.0.1", 1234, 0, 0, 0, null );
        System.out.println( r.corrupts( p ) );

        t = TestTCP.t2;
        c = MySocket.getChecksum( MySocket.getChecksumBytes( t, InetAddress.getByName( "127.0.0.1" ), InetAddress.getByName( "127.0.0.2" ) ) );
        p = new TCP( t.srcPort(), t.desPort(), t.seqNum(), t.ackNum(), t.headerLen(), t.reserved(), t.flags(), t.windowSize(), c, t.urgentPtr(), t.data() );
        System.out.println( r.corrupts( p ) );

        t = TestTCP.t3;
        c = MySocket.getChecksum( MySocket.getChecksumBytes( t, InetAddress.getByName( "127.0.0.1" ), InetAddress.getByName( "127.0.0.2" ) ) );
        p = new TCP( t.srcPort(), t.desPort(), t.seqNum(), t.ackNum(), t.headerLen(), t.reserved(), t.flags(), t.windowSize(), c, t.urgentPtr(), t.data() );
        System.out.println( r.corrupts( p ) );

        t = TestTCP.t4;
        c = MySocket.getChecksum( MySocket.getChecksumBytes( t, InetAddress.getByName( "127.0.0.1" ), InetAddress.getByName( "127.0.0.2" ) ) );
        p = new TCP( t.srcPort(), t.desPort(), t.seqNum(), t.ackNum(), t.headerLen(), t.reserved(), t.flags(), t.windowSize(), c, t.urgentPtr(), t.data() );
        System.out.println( r.corrupts( p ) );

        t = TestTCP.t5;
        c = MySocket.getChecksum( MySocket.getChecksumBytes( t, InetAddress.getByName( "127.0.0.1" ), InetAddress.getByName( "127.0.0.2" ) ) );
        p = new TCP( t.srcPort(), t.desPort(), t.seqNum(), t.ackNum(), t.headerLen(), t.reserved(), t.flags(), t.windowSize(), c, t.urgentPtr(), t.data() );
        System.out.println( r.corrupts( p ) );

        t = TestTCP.t6;
        c = MySocket.getChecksum( MySocket.getChecksumBytes( t, InetAddress.getByName( "127.0.0.1" ), InetAddress.getByName( "127.0.0.2" ) ) );
        p = new TCP( t.srcPort(), t.desPort(), t.seqNum(), t.ackNum(), t.headerLen(), t.reserved(), t.flags(), t.windowSize(), c, t.urgentPtr(), t.data() );
        System.out.println( r.corrupts( p ) );
    }

    public static
    void main( String[] args ) throws SocketException, UnknownHostException {
//        test1();
        test2();
    }
}
