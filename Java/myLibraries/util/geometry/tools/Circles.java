package myLibraries.util.geometry.tools;

/*
 * Circles.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.Determinant;
import myLibraries.util.geometry.elements.circle.Circle;
import myLibraries.util.geometry.elements.point.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to Circle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Circles {

    /**
     * determine if p lies in the circumcircle of triangle(a, b, c)
     *
     * Note that point a, b and c must be in counter-clock wise order
     *
     * Reference resource:
     * @see <a href=https://www.cs.cmu.edu/~quake/robust.html>Adaptive Precision Floating-Point</a>
     * @see <a href=https://www.cs.umd.edu/class/spring2020/cmsc754/Lects/lect13-delaun-alg.pdf>Delaunay Triangulations: Incremental Construction</a>
     *
     * */

    public static
    double inCircle( Vector a, Vector b, Vector c, Vector p ) {
        // a, b and c are on the same line, throw exception
        if ( Lines.isOnTheSameLine( a, b, c ) )
            throw new ArithmeticException( "not a triangle in inCircle()" );

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

    /**
     * sort by radius and then center
     * */

    public static
    int sort( Circle circle1, Circle circle2 ) {
        if ( circle1.center.equalsXAndY( circle2.center ) )
            return MyMath.doubleCompare( circle1.radius, circle2.radius );

        return Vectors.sortByX( circle1.center, circle2.center );
    }

    /**
     * remove duplicate circles
     * */

    public static
    List<Circle> mergeOverlappingCycles( List<Circle> circles ) {
        List<Circle> nonOverlappingCircles = new ArrayList<>();
        if ( circles.isEmpty() ) return nonOverlappingCircles;
        circles.sort( Circles::sort );

        nonOverlappingCircles.add( circles.get( 0 ) );
        for ( int i = 0; i < circles.size() - 1; i++ ) {
            if ( Circles.sort( circles.get( i ), circles.get( i + 1 ) ) != 0 )
                nonOverlappingCircles.add( circles.get( i + 1 ) );
        }

        return nonOverlappingCircles;
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
