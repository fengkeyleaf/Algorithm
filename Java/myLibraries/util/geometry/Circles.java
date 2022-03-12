package myLibraries.util.geometry;

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

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.lang.MyMath;
import myLibraries.util.Determinant;
import myLibraries.util.geometry.elements.Circle;
import myLibraries.util.geometry.elements.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to Circle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
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
     * determine a circle with given three points
     *
     * Reference resource:
     * https://www.zhihu.com/question/326059238/answer/715869809
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
     * Note that point a, b and c must be in counter-clock wise order
     *
     * Reference resource:
     * @see <a href=https://www.cs.cmu.edu/~quake/robust.html>Adaptive Precision Floating-Point</a>
     * @see <a href=https://www.cs.umd.edu/class/spring2020/cmsc754/Lects/lect13-delaun-alg.pdf>Delaunay Triangulations: Incremental Construction</a>
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

    static
    void testInCircle() {
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
        System.out.println( circle );
    }

    public static
    void main( String[] args ) {
//        testInCircle();
        testThreePoints();
    }
}
