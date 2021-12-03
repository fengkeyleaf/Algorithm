package myLibraries.util;

/*
 * Bitmask.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 * JDK: 15
 */

/**
 * Data structure of a general Bitmask
 *
 * @author Xiaoyu Tongyang or call me sora for short
 */

public final class Bitmask {

    /**
     * add a mask to a status
     * for example:
     * a status: 0011
     * add mask: 1000
     * the status after adding: 1011
     * <p>
     * 0011
     * | 1000
     * ------
     * 1011
     */

    public static
    int add( int status, int mask ) {
        return status | mask;
    }

    /**
     * remove a mask from a status
     * for example:
     * a status: 0011
     * add city: 0001
     * the status after removing: 0001
     * <p>
     * 0011
     * & 0001
     * ------
     * 0001
     */

    public static
    int remove( int status, int mask ) {
        return status & ~mask;
    }

    /**
     * check if a status contains a mask
     * for example:
     * a set: 0101, maks: 0100
     * <p>
     * 0101
     * & 0100
     * ------
     * 0100 > 0
     * <p>
     * -> true
     * <p>
     * a set: 0101, maks: 0010
     * <p>
     * 0101
     * & 0010
     * ------
     * 0000 == 0
     * <p>
     * -> false
     */

    public static
    boolean contain( int status, int mask ) {
        return ( status & mask ) != 0;
    }

    /**
     * determine whether a status is equal to another
     * for example:
     * status 1: 0011, status 2: 0011
     * <p>
     * 0011
     * ^ 0011
     * ------
     * 0000 == 0
     * <p>
     * -> true
     * <p>
     * <p>
     * status 1: 0010, status 2: 0011
     * <p>
     * 0010
     * ^ 0011
     * ------
     * 0001 > 0
     * <p>
     * -> false
     */

    public static
    boolean containAll( int status1, int status2 ) {
        return ( status1 ^ status2 ) == 0;
    }
}
