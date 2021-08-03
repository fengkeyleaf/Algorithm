package myLibraries.util.geometry.tools;

/*
 * Cycles.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.Determinant;
import myLibraries.util.geometry.elements.point.Vector;

/**
 * This class consists exclusively of static methods
 * that related to Cycle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Cycles {

    /**
     * determine if p lies in the circumcircle of triangle(a, b, c)
     *
     * Reference resource:
     * https://www.cs.cmu.edu/~quake/robust.html
     * */

    public static
    double inCircle( Vector a, Vector b, Vector c, Vector p ) {
        // a, b and c are on the same line, throw exception
        if ( MyMath.isEqualZero( Triangles.areaTwo( a, b, c ) ) )
            throw new ArithmeticException( "not a triangle in inCircleTest()" );

        final double DIFF_X_A_P = a.x - p.x;
        final double DIFF_Y_A_P = a.y - p.y;
        final double DIFF_X_B_P = b.x - p.x;
        final double DIFF_Y_B_P = b.y - p.y;
        final double DIFF_X_C_P = c.x - p.x;
        final double DIFF_Y_C_P = c.y - p.y;
        double[][] matrix = {
                { DIFF_X_A_P, DIFF_Y_A_P, DIFF_X_A_P * DIFF_X_A_P + DIFF_Y_A_P * DIFF_Y_A_P },
                { DIFF_X_B_P, DIFF_Y_B_P, DIFF_X_B_P * DIFF_X_B_P + DIFF_Y_B_P * DIFF_Y_B_P },
                { DIFF_X_C_P, DIFF_Y_C_P, DIFF_X_C_P * DIFF_X_C_P + DIFF_Y_C_P * DIFF_Y_C_P }
        };

        return new Determinant( matrix ).getResult();
    }

    public static
    void main( String[] args ) {
        Vector point1 = new Vector( -1, 0 );
        Vector point2 = new Vector( 1, 0 );
        Vector point3 = new Vector( 0, 1 );
        Vector point4 = new Vector( 2, 1 );
        Vector point5 = new Vector( 0, -1 );

        System.out.println( inCircle( point1, point2, point3, point1 ) ); // 0
        System.out.println( inCircle( point1, point2, point3, Vector.origin ) ); // 2
        System.out.println( inCircle( point1, point2, point3, point4 ) ); // -8
        System.out.println( inCircle( point1, point2, point3, point5 ) ); // 0

        System.out.println();
        System.out.println( inCircle( point1, Vector.origin, point3, point5 ) ); // -2
        System.out.println( inCircle( point1, Vector.origin, point2, point5 ) ); // error
    }
}
