package com.fengkeyleaf.io.pkts;

/*
 * UDP.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/4/2022$
 */

import com.fengkeyleaf.lang.MyMath;

import java.util.Arrays;

/**
 * Data structure of UDP, User Datagram Protocol
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://en.wikipedia.org/wiki/User_Datagram_Protocol
public record UDP (
        long srcPort, // 16b
        long desPort, // 16b
        long len, // 16b
        String checksum // 16b
) {

    public static
    UDP getUDP( byte[] B ) {
        byte[] fixedPart = Arrays.copyOfRange( B, 0, 8 );
        String bits = IPv4.getBits( fixedPart );

        return new UDP(
                MyMath.binaryToDecimal( bits.substring( 0, 16 ) ), // srcPort
                MyMath.binaryToDecimal( bits.substring( 16, 32 ) ), // desPort
                MyMath.binaryToDecimal( bits.substring( 32, 48 ) ), // len
                MyMath.binaryToHex( bits.substring( 48, 64 ) ) // checksum
        );
    }

    @Override
    public String toString() {
        return "Source Port = " + srcPort +
               "\nDestination Port = " + desPort +
               "\nLength = " + len +
               "\nChecksum = 0x" + checksum;
    }
}
