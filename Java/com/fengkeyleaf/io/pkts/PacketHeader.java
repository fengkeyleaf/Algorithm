package com.fengkeyleaf.io.pkts;
/*
 * PacketHeader.java
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
import com.fengkeyleaf.util.MyArrays;

import java.util.Arrays;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public record PacketHeader(
        // 16B in total.
        byte[] timeStampSec, // 4B
        byte[] timeStampMic, // 4B
        byte[] capLen, // 4B
        byte[] len // 4B
) {

    public static
    PacketHeader getHeader( byte[] B, boolean isReverse ) {
        assert B.length == 16;
        byte[] timeStampSec = isReverse ? MyArrays.reverse( Arrays.copyOfRange( B, 0, 4 ) ) :
                Arrays.copyOfRange( B, 0, 4 );
        byte[] timeStampMic = isReverse ? MyArrays.reverse( Arrays.copyOfRange( B, 4, 8 ) ) :
                Arrays.copyOfRange( B, 4, 8 );
        byte[] capLen = isReverse ? MyArrays.reverse( Arrays.copyOfRange( B, 8, 12 ) ) :
                Arrays.copyOfRange( B, 8, 12 );
        byte[] len = isReverse ? MyArrays.reverse( Arrays.copyOfRange( B, 12, 16 ) ) :
                Arrays.copyOfRange( B, 12, 16 );

        return new PacketHeader( timeStampSec, timeStampMic, capLen, len );
    }

    public int getCapLen() {
        return MyMath.hexToDecimal( bytesToString( capLen ) );
    }

    static
    String bytesToString( byte[] B ) {
        StringBuilder t = new StringBuilder();
        for ( String s : MyMath.bytesToHex( B ) ) {
            t.append( s );
        }

        return t.toString();
    }

    @Override
    public String toString() {
        return "TimeStampSec = " + Arrays.toString( MyMath.bytesToHex( timeStampSec ) ) +
                "\nTimeStampMic = " + Arrays.toString( MyMath.bytesToHex( timeStampMic ) ) +
                "\nCaplen = " + Arrays.toString( MyMath.bytesToHex( capLen ) ) +
                "\nLen = " + Arrays.toString( MyMath.bytesToHex( len ) );
    }
}
