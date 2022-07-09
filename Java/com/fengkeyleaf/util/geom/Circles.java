package com.fengkeyleaf.util.geom;

/*
 * Circles.java
 *
 * Version:
 *     $1.2$
 *
 * Revisions:
 *     $1.0 inCircle(), mergeOverlappingCycles() on 08/03/2021$
 *     $1.1 getCircleByThreePoints() on 01/01/2022$
 *     $1.2 draw() on 01/19/2022$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.util.Determinant;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to Circle
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class Circles {

    //-------------------------------------------------------
    // drawing part
    //-------------------------------------------------------

    /**
     * draw circles, not normalized. Assume already flipped Y
     *
     * @param points assume it's been flipped y
     * */

    public static
    void draw( Graphics graphics, List<Integer> points, Color color ) {
        assert points.size() % 3 == 0 : points;

        graphics.setColor( color );
        for ( int i = 0; i < points.size(); i += 3 ) {
            graphics.drawOval( points.get( i ), points.get( i + 1 ), points.get( i + 2 ), points.get( i + 2 ) );
        }
    }

    public static
    void draw( Graphics graphics, List<List<Integer>> points, List<Color> colors ) {
        assert points.size() == colors.size();

        for ( int i = 0; i < points.size(); i++ ) {
            draw( graphics, points.get( i ), colors.get( i ) );
        }
    }

    /**
     * @return [ x-coor, y-coor, diameter ]
     * */

    public static
    List<Integer> getDrawingPoints( List<Circle> circles,
                                    int originWidth, int originHeight,
                                    int windowWidth, int windowHeight ) {
        final List<Integer> points = new ArrayList<>();
        circles.forEach( c -> {
            Vector upperLeft = c.getUpperLeft();
            int x = Vectors.normalize( upperLeft.x, originWidth, windowWidth );
            int y = Vectors.normalize( -upperLeft.y, originHeight, windowHeight );
            DrawingProgram.storePoints( points, x, y, ( int ) ( c.radius * 2 * windowWidth / originWidth ) );
        } );

        return points;
    }

    //-------------------------------------------------------
    // computational part
    //-------------------------------------------------------

    /**
     * determine a circle with given three points.
     *
     * <a href="https://www.zhihu.com/question/326059238/answer/715869809">Reference resource</a>
     * */

    public static
    Circle getCircleByThreePoints( Vector a, Vector b, Vector c ) {
        if ( Lines.isOnTheSameLine( a, b, c ) )
            return null;

        double A = c.x - a.x;
        double B = c.y - a.y;
        double C = b.y - a.y;
        double D = b.x - a.x;
        double gamma = - ( A * ( c.x - b.x ) + B * ( c.y - b.y ) ) / ( A * C - D * B );

        double E = -b.x - a.x + gamma * C;
        double F = -b.y - a.y - gamma * D;
        double G = a.x * b.x + a.y * b.y - C * gamma * a.x + D * gamma * a.y;

        return new Circle( E, F, G );
    }

    /**
     * determine if p lies in the circumcircle of triangle(a, b, c)
     *
     * Note that point a, b and c may not necessarily be
     * in counter-clock wise order.
     * We'll guarantee that a, b and c are in counter-clock wise order
     * with the help of duality.
     *
     * Reference resource:
     * @see <a href=https://www.cs.cmu.edu/~quake/robust.html>Adaptive Precision Floating-Point</a>
     * @see <a href=https://www.cs.umd.edu/class/spring2020/cmsc754/Lects/lect13-delaun-alg.pdf>Delaunay Triangulations: Incremental Construction</a>
     *
     * @return positive, P lies in the circumcircle of (a, b, c);
     *         zero, P lies on the circumcircle of (a, b, c);
     *         negative, P lies outside the circumcircle of (a, b, c)
     * */

    public static
    double inCircle( Vector a, Vector b, Vector c, Vector p ) {
        // a, b and c are on the same line, throw exception
        if ( Lines.isOnTheSameLine( a, b, c ) )
            throw new ArithmeticException( "not a triangle in inCircle():\n " + a + " | " + b + " | " + c + " | " + p );

        // find the lowest then left point among a, b and c
        Vector LTL = Vectors.findLTL( a, b, c );
        // sort other two points by angle with LTL as P
        List<Vector> points = Vectors.sortByAngleCounterClockWise( LTL, a, b, c );
        assert points.size() == 2;

        // special case, where one of the points other than LTL has
        // the same x-coor with LTL's.
        if ( inCircle( LTL, points.get( 0 ), points.get( 1 ) ) )
            Collections.reverse( points );

        // guarantee that a, b and c are in counter-clock wise order.
        a = LTL;
        b = points.get( 0 );
        c = points.get( 1 );

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

    private static
    boolean inCircle( Vector LTL, Vector q1, Vector q2 ) {
        return  LTL.isRight( q2 ) &&
                ( MyMath.isEqual( LTL.x, q1.x ) || LTL.isLeft( q1 ) );
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

    // https://www.geogebra.org/calculator/crcy9nkn
    static
    void testInCircle() {
        Vector p1 = new Vector( -1, 0 );
        Vector p2 = new Vector( 1, 0 );
        Vector p3 = new Vector( 0, 1 );
        Vector p4 = new Vector( 2, 1 );
        Vector p5 = new Vector( 0, -1 );
        Vector p6 = new Vector( 3, 3 );

        List<Vector> P = new ArrayList<>( 6 );
        P.add( p1 );
        P.add( p2 );
        P.add( p3 );
        P.add( p4 );
        P.add( p5 );
        P.add( p6 );
        P.forEach( p -> {
            p.x++;
            p.y++;
        } );

//        System.out.println( inCircle( p1, p2, p3, p2 ) ); // 0, 0
//        System.out.println( inCircle( p1, p2, p3, Vector.origin ) ); // 2, -2
//        System.out.println( inCircle( p1, p2, p3, p4 ) ); // -8, -8
//        System.out.println( inCircle( p1, p2, p3, p5 ) ); // 0, 0
//
//        System.out.println();
//        System.out.println( inCircle( p1, p3, p2, p6 ) ); // -34, -34
//        System.out.println( inCircle( p2, p3, p1, p6 ) ); // -34, -34
//
//        System.out.println();
////        System.out.println( getCircleByThreePoints( p1, Vector.origin, p3 ) );
//        System.out.println( inCircle( p1, Vector.origin, p3, p5 ) ); // -2, 2
//        System.out.println( inCircle( p1, Vector.origin, p2, p5 ) ); // error, 2

        System.out.println();
        System.out.println( inCircle( p1, p2, p5, p3 ) ); // ?, 0
        System.out.println( inCircle( p2, p1, p5, Vector.origin ) ); // ?, < 0
        System.out.println( inCircle( p2, p5, p1, new Vector( 1, 1 ) ) ); // ?, > 0
        System.out.println( inCircle( p2, p5, p1, p6 ) ); // ?, < 0
    }

    // visualization: https://www.geogebra.org/calculator
    // https://www.geogebra.org/calculator/zjdjtnnj
    static
    void testThreePoints() {
        Vector p1 = new Vector( -3, 4 );
        Vector p2 = new Vector( 4, 1 );
        Vector p3 = new Vector( -1, -2 );

        // C: 0.16666666666666669|1.7222222222222223 | Rad: 3.900775484787102 |
        // equ: x² + y² -0.33333333333333337 x + -3.4444444444444446 y + -12.222222222222221 = 0
        Circle circle = Circles.getCircleByThreePoints( p1, p2, p3 );
//        System.out.println( circle );

//        p1 = new Vector( 1, 0 );
//        p2 = new Vector( 0, 1 );
//        p3 = new Vector( -1, 0 );
//
        p1 = new Vector( 1, 4 );
        p2 = new Vector( -4, 1 );
        p3 = new Vector( -2, -5 );

        // C: 0.5|-0.833333333333333 | Rad: 4.85912657903775 |
        // equ: x² + y² -1.0 x + 1.666666666666666 y + -22.666666666666664 = 0
        circle = Circles.getCircleByThreePoints( p1, p2, p3 );
//        System.out.println( circle );

        p1 = new Vector( -3, 4 );
        p2 = new Vector( 4, 1 );
        p3 = new Vector( -1, -2 );

        // C: 0.16666666666666669|1.7222222222222223 | Rad: 3.900775484787102 |
        // equ: x² + y² -0.33333333333333337 x + -3.4444444444444446 y + -12.222222222222221 = 0
        circle = Circles.getCircleByThreePoints( p1, p2, p3 );
//        System.out.println( circle );

        p1 = new Vector( -4, 4 );
        p2 = new Vector( 4, 4 );
        p3 = new Vector( -4, -4 );

        // C: -0.0|-0.0 | Rad: 5.656854249492381 | equ: x² + y² + 0.0 x + 0.0 y + -32.0 = 0
        circle = Circles.getCircleByThreePoints( p1, p2, p3 );
//        System.out.println( circle );

        p1 = new Vector( -10, 10 );
        p2 = new Vector( 10, 10 );
        p3 = new Vector( -10, -10 );

        // C: -0.0|-0.0 | Rad: 11.313708498984761 | equ: x² + y² + 0.0 x + 0.0 y + -128.0 = 0
        circle = Circles.getCircleByThreePoints( p1, p2, p3 );
//        System.out.println( circle );

        p1 = new Vector( 27, 7 );
        p2 = new Vector( 28, 8 );
        p3 = new Vector( 32, 25 );

        // C: 14.961538461538462|20.03846153846154 | Rad: 17.746155513320588 |
        // equ: x² + y² + -29.923076923076923 x + -40.07692307692308 y + 310.46153846153845 = 0
        circle = Circles.getCircleByThreePoints( p1, p2, p3 );
//        System.out.println( circle );

        p1 = new Vector( 0, 1 );
        p2 = new Vector( 2, 1 );
        p3 = new Vector( 1, 0 );

        // C6: 1.0|1.0 | Rad: 1.0 | equ: x² + y² + -2.0 x + -2.0 y + 1.0 = 0
        circle = Circles.getCircleByThreePoints( p1, p2, p3 );
//        System.out.println( circle );

        p1 = new Vector( 0, 1 );
        p2 = new Vector( 2, 1 );
        p3 = new Vector( 1, 0 );

        // C6: 1.0|1.0 | Rad: 1.0 | equ: x² + y² + -2.0 x + -2.0 y + 1.0 = 0
        circle = Circles.getCircleByThreePoints( p1, p2, p3 );
        System.out.println( circle );
    }

    public static
    void main( String[] args ) {
//        testInCircle();
        testThreePoints();
    }
}
