package myLibraries.lang;
/*
 * MyMath.java
 *
 * Version:
 *     $1.5$
 *
 * Revisions:
 *     $1.0 convert Number To OtherBase and Arrangement, etc.$
 *     $1.1 added minAmongAl()l on 3/23/2021$
 *     $1.2 added entropy() and gain() for AI on 4/8/2021$
 *     $1.3 add equalFloats() on 4/13/2021$
 *     $1.4 add quadrant() on 5/14/2021$
 *     $1.5 add doubleCompare() and isSameSign() on 7/8/2021$
 */


import myLibraries.util.geometry.elements.point.Vector;

/**
 * Math tool box
 *
 * @author       Xiaoyu Tongyang or call me sora for short
 */

public final class MyMath {
    public static final double EPSILON = 0.00000001; // 1e-8

    /**
     * determine if the floating number equals zero
     * */

    public static
    boolean isEqualZero(double num ) {
        return Math.abs( num - 0 ) <= EPSILON;
    }

    /**
     * determine if two floating numbers are equal or not
     * */

    public static
    boolean isEqualFloats(double num1, double num2 ) {
        return Math.abs( num1 - num2 ) <= EPSILON;
    }
}
