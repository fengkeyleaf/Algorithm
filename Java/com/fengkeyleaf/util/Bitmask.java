package com.fengkeyleaf.util;

/*
 * Bitmask.java
 *
 * JDK: 14
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 */

/**
 * Data structure of a general Bitmask
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class Bitmask {

    /**
     * Add a mask to a status
     *
     * for example:
     * a status: 0011
     * add mask: 1000
     * the status after adding: 1011
     *
     *   0011
     * | 1000
     * ------
     *   1011
     */

    public static
    int add( int status, int mask ) {
        return status | mask;
    }

    /**
     * Remove a mask from a status
     *
     * for example:
     * a status: 0011
     * add city: 0001
     * the status after removing: 0001
     *
     *   0011
     * & 0001
     * ------
     *   0001
     */

    public static
    int remove( int status, int mask ) {
        return status & ~mask;
    }

    /**
     * Check if a status contains a mask
     *
     * for example:
     * a set: 0101, maks: 0100
     *
     *   0101
     * & 0100
     * ------
     *   0100 > 0
     *
     * -> true
     *
     * a set: 0101, maks: 0010
     *
     *   0101
     * & 0010
     * ------
     *   0000 == 0
     *
     * -> false
     */

    public static
    boolean contains( int status, int mask ) {
        return ( status & mask ) != 0;
    }

    /**
     * Determine whether a status is equal to another
     *
     * for example:
     * status 1: 0011, status 2: 0011
     *
     *   0011
     * ^ 0011
     * ------
     *   0000 == 0
     *
     * -> true
     *
     * status 1: 0010, status 2: 0011
     *
     *   0010
     * ^ 0011
     * ------
     *   0001 > 0
     *
     * -> false
     */

    public static
    boolean containsAll( int s1, int s2 ) {
        return ( s1 ^ s2 ) == 0;
    }
}
