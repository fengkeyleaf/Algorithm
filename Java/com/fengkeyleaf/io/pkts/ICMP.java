package com.fengkeyleaf.io.pkts;

/*
 * ICMP.java
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
 * Data structure of ICMP, Internet Control Message Protocol.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://en.wikipedia.org/wiki/Internet_Control_Message_Protocol
// https://blog.csdn.net/baidu_37964071/article/details/80514340
public record ICMP (
        long type, // 1B, 8b
        long code, // 1B, 8b
        String checksum // 2B, 16b
) {

    public static
    ICMP getICMP( byte[] B ) {
        byte[] fixedPart = Arrays.copyOfRange( B, 0, 4 );
        String bits = IPv4.getBits( fixedPart );

        return new ICMP(
                MyMath.binaryToDecimal( bits.substring( 0, 8 ) ),
                MyMath.binaryToDecimal( bits.substring( 8, 16 ) ),
                MyMath.binaryToHex( bits.substring( 16, 32 ) )
        );
    }

    @Override
    public String toString() {
        return "Type = " + type + "\nCode = " + code + "\nChecksum = 0x" + checksum;
    }
}
