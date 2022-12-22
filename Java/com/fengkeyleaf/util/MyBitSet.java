package com.fengkeyleaf.util;

/*
 * MyBitSet.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/21/2022$
 */

import java.util.Arrays;
import java.util.BitSet;

/**
 * My BitSet
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public class MyBitSet {

    /**
     * Returns a new byte array containing all the bits in this bit set.
     * But this methods will pad with zero if resulted array's length < give length limited, l.
     *
     * @param b bitset to be processed.
     * @param l byte array length limit.
     * @return a byte array containing a little-endian representation
     *         of all the bits in this bit set
     */

    public static
    byte[] toByteArray( BitSet b, int l ) {
        byte[] B = b.toByteArray();
        if ( B.length > l )
            throw new IllegalArgumentException( "Max length(" + l + ") < byte array's length(" + B.length + ")" );

        return Arrays.copyOf( B, l );
    }

    /**
     * Set bits in the BitSet B, starting from j, with the bits in b.
     *
     * @param B Bitset to operate.
     * @param j Starting index.
     * @param b Bit string to be set into B.
     */

    public static
    void setBits( BitSet B, int j, String b ) {
        for ( int i = b.length() - 1; i >= 0; i-- ) {
            if ( b.charAt( i ) == '1' ) B.set( j );
            j++;
        }
    }
}
