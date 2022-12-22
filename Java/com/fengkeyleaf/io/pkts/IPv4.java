package com.fengkeyleaf.io.pkts;

/*
 * IPv4.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/3/2022$
 */

import com.fengkeyleaf.lang.MyMath;

import java.util.Arrays;

/**
 * Data structure of IPv4, Internet Protocol version 4.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// http://c.biancheng.net/view/6411.html
// https://en.wikipedia.org/wiki/IPv4
public record IPv4 (
        int version, // 4b
        int IHL, // 4b
        String TOS, // 8b
        int totalLen, // 16b
        String identification, // 16b
        String flags, // 3b
        long fragmentationOffset, // 13b
        long TTL, // 8b
        int protocolNum, // 8b
        String headerChecksum, // 16b
        int[] srcIPAddr, // 32b
        int[] desIPAddr, // 32b
        byte[] options,
        // protocol layers
        Protocol protocol // container to store protocol layers.
) {

    public static final short ICMP = 1;
    public static final short TCP = 6;
    public static final short UDP = 17;

    // options = totalLen - IHL
    public static
    IPv4 getIPv4( byte[] B ) {
        byte[] fixedPart = Arrays.copyOfRange( B, 0, 20 );
        String bits = getBits( fixedPart );

        int IHL = ( int ) MyMath.binaryToDecimal( bits.substring( 4, 8 ) );
        int totalLen = ( int ) MyMath.binaryToDecimal( bits.substring( 16, 32 ) );
        int protocolNum = ( int ) MyMath.binaryToDecimal( bits.substring( 72, 80 ) );

        assert bits.length() == 160 : bits.length();
        return new IPv4 (
                ( int ) MyMath.binaryToDecimal( bits.substring( 0, 4 ) ), // version
                IHL,
                MyMath.binaryToHex( bits.substring( 8, 16 ) ), // TOS
                totalLen,
                MyMath.binaryToHex( bits.substring( 32, 48 ) ), // identification
                bits.substring( 48, 51 ), // flags
                MyMath.binaryToDecimal( bits.substring( 51, 64 ) ), // fragmentationOffset
                MyMath.binaryToDecimal( bits.substring( 64, 72 ) ), // TTL
                protocolNum,
                MyMath.binaryToHex( bits.substring( 80, 96 ) ), // headerChecksum
                getIP( MyMath.binaryToHex( bits.substring( 96, 128 ) ) ), // srcIPAddr
                getIP( MyMath.binaryToHex( bits.substring( 128, 160 ) ) ), // desIPAddr
                Arrays.copyOfRange( B, 20, B.length ), // options
                // protocol layers
                Protocol.getProtocol(
                        protocolNum, Arrays.copyOfRange( B, IHL * 4, totalLen )
                )
        );
    }

    static
    int[] getIP( String hex ) {
        if ( hex.length() < 8 )
            hex = String.format( "%8s", hex ).replace( " ", "0" );

        assert hex.length() == 8;
        int[] ip = new int[ 4 ];
        ip[ 0 ] = MyMath.hexToDecimal( hex.substring( 0, 2 ) );
        ip[ 1 ] = MyMath.hexToDecimal( hex.substring( 2, 4 ) );
        ip[ 2 ] = MyMath.hexToDecimal( hex.substring( 4, 6 ) );
        ip[ 3 ] = MyMath.hexToDecimal( hex.substring( 6, 8 ) );

        return ip;
    }

    private static
    String getProtocolType( int n ) {
        return switch ( n ) {
            case ICMP -> "ICMP";
            case TCP -> "TCP";
            case UDP -> "UDP";
            default -> null;
        };
    }

    public int getHeaderLen() {
        return IHL * 4;
    }

    public String getSrcIP() {
        return getIP( srcIPAddr );
    }

    private static
    String getIP( int[] ip ) {
        StringBuilder b = new StringBuilder();
        for ( int i = 0; i < ip.length; i++ ) {
            b.append( ip[ i ] );
            if ( i != ip.length - 1 ) b.append( "." );
        }

        return b.toString();
    }

    public String getDesIP() {
        return getIP( desIPAddr );
    }

    static
    String getBits( byte[] B ) {
        StringBuilder b = new StringBuilder();
        for ( String s : MyMath.bytesToBinary( B ) ) {
            b.append( s );
        }

        return b.toString();
    }

    @Override
    public String toString() {
        return "Version = " + version +
               "\nHeader length = " + getHeaderLen() + " bytes" +
               "\nType of service = " + TOS +
               "\nTotal Length = " + totalLen + " bytes" +
               "\nIdentification = " + identification +
               "\nFlags = " + flags +
               "\nFragmentation Offset = " + fragmentationOffset + " bytes" +
               "\nTime to live = " + TTL +
               "\nProtocol = " + protocolNum + "(" + getProtocolType( protocolNum ) + ")" +
               "\nHeader Checksum = 0x" + headerChecksum +
               "\nSource address = " + getSrcIP() +
               "\nDestination Address = " + getDesIP() +
               "\nOptions: " + ( options.length == 0 ? "No" : "Yes" );
    }
}
