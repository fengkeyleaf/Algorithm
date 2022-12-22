package com.fengkeyleaf.util;

/*
 * TestArrays.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/16/2022$
 */

import java.util.Arrays;

/**
 * Test Arrays
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

final class TestArrays {
    private static
    void testConcat() {
        byte[] b1 = new byte[] { 1, 2, 3, 4 };
        byte[] b2 = new byte[] { 5, 6, 7, 8 };

        System.out.println( Arrays.toString( MyArrays.concatAll( b1, b2 ) ) );
    }

    public static
    void main( String[] args ) {
        testConcat();
    }
}
