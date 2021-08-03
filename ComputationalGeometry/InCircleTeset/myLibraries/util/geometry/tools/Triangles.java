package myLibraries.util.geometry.tools;

/*
 * Triangles.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.point.Vector;

import java.util.List;

/**
 * This class consists exclusively of static methods that related to triangle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Triangles {

    /**
     * compute area2
     *
     * point1 -> p, point2 -> q, point3 -> s
     * */

    public static
    double areaTwo( Vector point1, Vector point2, Vector point3 ) {
        return point1.x * point2.y - point1.y * point2.x +
                point2.x * point3.y - point2.y * point3.x +
                point3.x * point1.y - point3.y * point1.x;
    }

    private static
    void testOther() {
        System.out.println( Math.cos( 60 * Math.PI / 180 ) );
        System.out.println( Math.acos( 0.5 ) );
        System.out.println( Math.acos( 0.5 ) * 180 / Math.PI );
        System.out.println( Math.acos( -0.5 ) );
        System.out.println( Math.acos( -0.5 ) * 180 / Math.PI );
    }

    private static final Vector point1 = new Vector( 1, 0 );
    private static final Vector point2 = new Vector( 0, 1 );
    private static final Vector point3 = new Vector( 1, 1 );
    private static final Vector point4 = new Vector( 1, -1 );
    private static final Vector point5 = new Vector( -1, -1 );
    private static final Vector point6 = new Vector( -1, 1 );
    private static final Vector point7 = new Vector( -1, 0 );
    private static final Vector point8 = new Vector( 0, -1 );
    private static final Vector point9 = new Vector( 2, 0 );

    private static
    void testSpecial() {
        Vector point1 = new Vector( 0, 3 );
        Vector point2 = new Vector( -1, -3 );
        System.out.println( areaTwo( point1, point1, point2 ) );
    }

    public static
    void main( String[] args ) {
//        testClockWiseAngleCompareTo();
        testSpecial();
    }
}
