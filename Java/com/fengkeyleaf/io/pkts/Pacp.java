package com.fengkeyleaf.io.pkts;

/*
 * Pacp.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/2/2022$
 */

import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.util.function.BExp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data structure of pacp file.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/FileInputStream.html
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/InputStream.html
public class Pacp {

    private static final int GLOBAL_HEADER_LEN = 24;
    private static final int PACKET_HEADER_LEN = 16;
    final PacpGlobalHeader h;
    final List<Packet> P = new ArrayList<>();
    // # of pkts will be analyzed,
    // -1 means analyze all pkts.
    public int filteringNum = -1;

    public Pacp( String f ) {
        try ( InputStream i = new FileInputStream( f ) ) {
            int n = 0;
            byte[] B = new byte[ GLOBAL_HEADER_LEN ];

            n = i.read( B );
            assert n > 0;

            boolean isReverse = isReverse( B );
            h = PacpGlobalHeader.getHeader( B, isReverse );
            B = new byte[ PACKET_HEADER_LEN ];
            while ( i.available() > 0 ) {
                n = i.read( B );
                assert n > 0;
                P.add( readPacketData(
                        PacketHeader.getHeader( B, isReverse ), i
                ) );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    private static final String NORMAL_ORDER = "a1b2c3d4";
    private static final String REVERSE_ORDER = "d4c3b2a1";

    private static
    boolean isReverse( byte[] B ) {
        assert B.length == 24;
        StringBuilder magicStr = new StringBuilder();
        for ( String s : MyMath.bytesToHex(
                Arrays.copyOfRange( B, 0, 4 )
        ) ) {
            magicStr.append( s );
        }

        assert magicStr.toString().equals( NORMAL_ORDER ) ||
                magicStr.toString().equals( REVERSE_ORDER );
        return !magicStr.toString().equals( NORMAL_ORDER );
    }

    private Packet readPacketData( PacketHeader h, InputStream i )
            throws IOException {

        byte[] B = new byte[ h.getCapLen() ];
        int n = i.read( B );
        assert n > 0 : n;

        return new Packet( h, PacketData.getData( B ) );
    }

    //-------------------------------------------------------
    // Get Operations.
    //-------------------------------------------------------

    public PacpGlobalHeader getGlobalHeader() {
        return h;
    }

    public List<Packet> getPackets() {
        return P;
    }

    //-------------------------------------------------------
    // filtering Operations.
    //-------------------------------------------------------

    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/Predicate.html
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/stream/Stream.html#collect(java.util.stream.Collector)
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/Consumer.html
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/stream/Stream.html#reduce(T,java.util.function.BinaryOperator)
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Optional.html
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/BinaryOperator.html
    // https://blog.csdn.net/jiadajing267/article/details/107302625
    // https://blog.csdn.net/dalinsi/article/details/78093130

    /**
     * Filter pkts in this pacp file based on the boolean expression.
     *
     * @param e boolean expression to filter the pkts.
     * */

    // TODO: 9/5/2022 stop filtering after analyzing limited number of pkts.
    public List<Packet> filter( BExp<Packet> e ) {
        // filtering process.
        List<Packet> P = this.P.stream().filter( p -> {
            e.assemble( p );
            return e.evaluate();
        } ).toList();

        // only return the required number of pkts.
        if ( filteringNum < 0 ) return P;
        return P.subList( 0, Math.min( filteringNum, P.size() ) );
    }

    /**
     * Return all pkts in this pacp file.
     */

    public void resetFilteringNum() {
        filteringNum = -1;
    }

    //-------------------------------------------------------
    // toString
    //-------------------------------------------------------

    static
    void print( String f ) {
        try ( InputStream i = new FileInputStream( f ) ) {
            byte[] B = new byte[ 8 ];
            while ( i.available() > 0 ) {
                i.read( B );
                for ( String b : MyMath.bytesToHex( B ) ) {
                    System.out.print( b + " " );
                }
                System.out.println();
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }
}
