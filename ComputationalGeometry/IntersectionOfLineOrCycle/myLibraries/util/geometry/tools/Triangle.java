package myLibraries.util.geometry.tools;

/*
 * Triangle.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.Vector;

/**
 * Data structure of Triangle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Triangle {

    /**
     * comparator to find LTL
     * */

    public static
    int LTL( Vector point1, Vector point2 ) {
        if ( point1.y == point2.y )
            return Float.compare( point1.x, point2.x );

        return Float.compare( point1.y, point2.y );
    }

    /**
     * to left test, including area2 == 0
     * */

    public static
    boolean toLeft( Vector point1, Vector point2, Vector point3 ) {
        float res = areaTwo( point1, point2, point3 );
        return res >= 0 || MyMath.equalFloats( res, 0 );
    }

    public static
    boolean toLeftRigorously( Vector point1, Vector point2, Vector point3 ) {
        return areaTwo( point1, point2, point3 ) > 0;
    }

    /**
     * to left test by LTL, not including area2 == 0
     * */

    public static
    boolean toLeftByLTLRigorously( Vector point1, Vector point2, Vector point3 ) {
        return areaTwoByLTL( point1, point2, point3 ) > 0;
    }

    /**
     * to left test by LTL
     * */

    public static Turn toLeftByLTL( Vector point1, Vector point2, Vector point3 ) {
        float res = areaTwoByLTL( point1, point2, point3 );
        if ( res == 0 ) return Turn.NONE;
        else if ( res > 0 ) return Turn.LEFT_TURN;

        return Turn.RIGHT_RIGHT;
    }

    /**
     * compute area2
     *
     * point1 -> p, point2 -> q, point3 -> s
     * */

    public static
    float areaTwo( Vector point1, Vector point2, Vector point3 ) {
        return point1.x * point2.y - point1.y * point2.x +
                point2.x * point3.y - point2.y * point3.x +
                point3.x * point1.y - point3.y * point1.x;
    }

    /**
     * compute area2 by LTL
     *
     * point1 -> p, point2 -> q, point3 -> s
     * */

    public static
    float areaTwoByLTL( Vector point1, Vector point2, Vector point3 ) {
        return point1.xRelativeToLTL * point2.yRelativeToLTL -
                point1.yRelativeToLTL * point2.xRelativeToLTL +
                point2.xRelativeToLTL * point3.yRelativeToLTL -
                point2.yRelativeToLTL * point3.xRelativeToLTL +
                point3.xRelativeToLTL * point1.yRelativeToLTL -
                point3.yRelativeToLTL * point1.xRelativeToLTL;
    }

    /**
     * sort by polar angle
     * */

    public static
    int sortByPolar( Vector point1, Vector point2 ) {
        int res = MyMath.quadrant( point1 ) - MyMath.quadrant( point2 );
        if ( res == 0 ) {
            float area = areaTwo( Vector.origin, point1, point2 );
            if ( area == 0 ) return 0;
            else if ( area > 0 ) return -1;
            return 1;
        }
        return res > 0 ? 1 : -1;
    }

    /**
     * sort by polar angle, but the polar is LTL
     * */

    public static
    int sortByToLeft( Vector point1, Vector point2 ) {
        float res = MyMath.quadrant( point1.xRelativeToLTL, point1.yRelativeToLTL ) -
                MyMath.quadrant( point2.xRelativeToLTL, point2.yRelativeToLTL );
        if ( res == 0 ) {
            float area = areaTwoByLTL( Vector.origin, point1, point2 );
            if ( area == 0 ) return 0;
            else if ( area > 0 ) return -1;
            return 1;
        }
        return res > 0 ? 1 : -1;
    }

}
