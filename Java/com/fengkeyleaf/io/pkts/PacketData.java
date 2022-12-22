package com.fengkeyleaf.io.pkts;

/*
 * PacketData.java
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

import java.util.Arrays;

/**
 * Data structure of packet data.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public record PacketData (
        String[] desMac,  // 6B
        String[] srcMac,  // 6B
        String type, // 2B
        byte[] data,  // 64 ~ 1500B
        IPv4 iPv4
) {

    // ipv4
    static final String IPV4 = "0800";

    public static
    PacketData getData( byte[] B ) {
        String t = getHex( MyMath.bytesToHex( Arrays.copyOfRange( B, 12, 14 ) ) );

        return new PacketData(
                MyMath.bytesToHex( Arrays.copyOfRange( B, 0, 6 ) ),
                MyMath.bytesToHex( Arrays.copyOfRange( B, 6, 12 ) ),
                t,
                Arrays.copyOfRange( B, 14, B.length ),
                isIPv4( t ) ? IPv4.getIPv4( Arrays.copyOfRange( B, 14, B.length ) ) : null
        );
    }

    static
    boolean isIPv4( String type ) {
        return type.equals( IPV4 );
    }

    static
    String getHex( String[] hex ) {
        StringBuilder b = new StringBuilder();

        for ( String h : hex ) {
            b.append( h );
        }

        return b.toString();
    }

    public String getDesMac() {
        return getMac( desMac );
    }

    public String getSrcMac() {
        return getMac( srcMac );
    }

    static
    String getMac( String[] BStr ) {
        StringBuilder t = new StringBuilder();

        for ( int i = 0; i < BStr.length; i++ ) {
            t.append( BStr[ i ] );
            if ( i != BStr.length - 1 )
                t.append( ":" );
        }

        return t.toString();
    }

    @Override
    public String toString() {
        return "Destination Mac = " + getDesMac() +
                "\nSource Mac = " + getSrcMac() +
                "\nType = 0x" + type;
    }
}
