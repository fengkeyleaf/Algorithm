package com.fengkeyleaf.io.pkts;
/*
 * PacpGlobalHeader.java
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

public record PacpGlobalHeader(
        // 24B in total.
        byte[] magic, // 4B
        byte[] major, // 2B
        byte[] minor, // 2B
        byte[] thisZone, // 4B
        byte[] sigFigs, // 4B
        byte[] snapLen, // 4B
        byte[] linkType // 4B
) {

    public static
    PacpGlobalHeader getHeader( byte[] B, boolean isReverse ) {
        assert B.length == 24;
        byte[] magic = Arrays.copyOfRange( B, 0, 4 );
        byte[] major = isReverse ? MyArrays.reverse( Arrays.copyOfRange( B, 4, 6 ) ) :
                Arrays.copyOfRange( B, 4, 6 );
        byte[] minor = isReverse ? MyArrays.reverse( Arrays.copyOfRange( B, 6, 8 ) ) :
                Arrays.copyOfRange( B, 6, 8 );
        byte[] thisZone = isReverse ? MyArrays.reverse( Arrays.copyOfRange( B, 8, 12 ) ) :
                Arrays.copyOfRange( B, 8, 12 );
        byte[] sigFigs = isReverse ? MyArrays.reverse( Arrays.copyOfRange( B, 12, 16 ) ) :
                Arrays.copyOfRange( B, 12, 16 );
        byte[] snapLen = isReverse ? MyArrays.reverse( Arrays.copyOfRange( B, 16, 20 ) ) :
                Arrays.copyOfRange( B, 16, 20 );
        byte[] linkType = isReverse ? MyArrays.reverse( Arrays.copyOfRange( B, 20, 24 ) ) :
                Arrays.copyOfRange( B, 20, 24 );

        return new PacpGlobalHeader(
                magic,   // magic
                major,   // major
                minor,   // minor
                thisZone,  // thisZone
                sigFigs, // sigFigs
                snapLen, // snapLen
                linkType // linkType
        );
    }

    @Override
    public String toString() {
        return "Magic = " + Arrays.toString( MyMath.bytesToHex( magic ) ) +
                "\nMajor = " + Arrays.toString( MyMath.bytesToHex( major ) ) +
                "\nMinor = " + Arrays.toString( MyMath.bytesToHex( minor ) ) +
                "\nThisZone = " + Arrays.toString( MyMath.bytesToHex( thisZone ) ) +
                "\nSigFigs = "  + Arrays.toString( MyMath.bytesToHex( sigFigs ) ) +
                "\nSnapLen = " + Arrays.toString( MyMath.bytesToHex( snapLen ) ) +
                "\nLinkType = " + Arrays.toString( MyMath.bytesToHex( linkType ) );
    }
}
