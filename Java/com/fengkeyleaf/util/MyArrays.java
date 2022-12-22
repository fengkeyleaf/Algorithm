package com.fengkeyleaf.util;

/*
 * MyArrays.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Provide methods related to the Arrays Class
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class MyArrays {

    /**
     * Concatenate arrays to one array.
     *
     * @param A arrays to be concatenated.
     * @return Concatenated array.
     */

    // TODO: 11/13/2022 Implement generic
    // Reference resource: https://www.cjavapy.com/article/290/
    public static
    byte[] concatAll( byte[]... A ) {
        // Compute the total length of all arrays.
        int t = 0;
        for ( byte[] a : A )
            t += a.length;

        // Copy the first array into R.
        byte[] R = Arrays.copyOf( A[ 0 ], t );
        int offset = A[ 0 ].length;
        // Concatenate others.
        for ( int i = 1; i < A.length; i++ ) {
            byte[] a = A[ i ];
            System.arraycopy( a, 0, R, offset, a.length );
            offset += a.length;
        }

        return R;
    }

    /**
     * Reverse a byte array.
     *
     * @return new copied reversed byte array.
     */

    public static
    byte[] reverse( byte[] a ) {
        List<Byte> L = new ArrayList<>( a.length );
        for ( byte b : a )
            L.add( b );
        Collections.reverse( L );

        for ( int i = 0; i < L.size(); i++ ) {
            a[ i ] = L.get( i );
        }

        return a;
    }

    /**
     * given indices are out of boundary?
     */

    public static
    boolean isOutOfIndex( int i, int size ) {
        return isOutOfIndex( i, i, size );
    }

    public static
    boolean isOutOfIndex( int l, int r, int size ) {
        return l < 0 || r >= size;
    }

    /**
     * medium index
     */

    public static
    int mid( int left, int right ) {
        return ( right - left ) / 2 + left;
    }

    /**
     * print 2D array
     */

    public static
    String print2DArrays( int[][] arrays, boolean upsideDownRow ) {
        StringBuilder text = new StringBuilder();
        if ( upsideDownRow )
            for ( int i = arrays.length - 1; i >= 0; i-- )
                text.append( Arrays.toString( arrays[ i ] ) ).append( "\n" );
        else
            for ( int[] array : arrays )
                text.append( Arrays.toString( array ) ).append( "\n" );

        return text.toString();
    }

    public static
    String print2DArrays( float[][] arrays, boolean upsideDownRow ) {
        StringBuilder text = new StringBuilder();
        if ( upsideDownRow )
            for ( int i = arrays.length - 1; i >= 0; i-- )
                text.append( Arrays.toString( arrays[ i ] ) ).append( "\n" );
        else
            for ( float[] array : arrays )
                text.append( Arrays.toString( array ) ).append( "\n" );

        return text.toString();
    }

    public static
    String print2DArrays( double[][] arrays, boolean upsideDownRow ) {
        StringBuilder text = new StringBuilder();
        if ( upsideDownRow )
            for ( int i = arrays.length - 1; i >= 0; i-- )
                text.append( Arrays.toString( arrays[ i ] ) ).append( "\n" );
        else
            for ( double[] array : arrays )
                text.append( Arrays.toString( array ) ).append( "\n" );

        return text.toString();
    }
}
