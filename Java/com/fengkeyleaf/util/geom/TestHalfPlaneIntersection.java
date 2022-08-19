package com.fengkeyleaf.util.geom;

/*
 * TestHalfPlaneIntersection.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/15/2022$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.lang.LinearTwoUnknowns;

import java.util.ArrayList;
import java.util.List;

/**
 * Test HalfPlaneIntersection
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

final class TestHalfPlaneIntersection {
    private static final String TITLE_HALF_PLANE = "Half plane to subdivision";

    static
    void testHalfPlane() {
        HalfPlane h = new HalfPlane( -1, 1, 0, HalfPlane.GREATER_EQ );
        System.out.println( h.contains( new Vector( 1, 2 ) ) ); // F
        System.out.println( h.contains( new Vector( 1, 1 ) ) ); // T
        System.out.println( h.contains( new Vector( 1, 0 ) ) ); // T
        System.out.println( h.contains( new Vector( 1, -1 ) ) ); // T

        System.out.println();
        h = new HalfPlane( 1, 1, 0, HalfPlane.GREATER_EQ );
        System.out.println( h.contains( new Vector( 1, 2 ) ) ); // F
        System.out.println( h.contains( new Vector( 1, 1 ) ) ); // F
        System.out.println( h.contains( new Vector( 1, 0 ) ) ); // F
        System.out.println( h.contains( new Vector( 1, -1 ) ) ); // T

        System.out.println();
        System.out.println( h.contains( new Vector( -1, 2 ) ) ); // F
        System.out.println( h.contains( new Vector( -1, 1 ) ) ); // F
        System.out.println( h.contains( new Vector( -1, 0 ) ) ); // F
        System.out.println( h.contains( new Vector( -1, -1 ) ) ); // T
    }

    static
    void testHalfPlaneToFace() {
        int size = 10;
        BoundingBox b = BoundingBox.getBox( Vector.origin, size, size );

        size = 25;
        DrawingProgram drawer = new DrawingProgram( TITLE_HALF_PLANE, size, size );
//        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, new HalfPlane( -1, 1, 0, HalfPlane.GREATER_EQ ).getSubdivision( b ) );
//        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, new HalfPlane( 1, 1, 0, HalfPlane.GREATER_EQ ).getSubdivision( b ) );
//        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, new HalfPlane( -1, 1, 1, HalfPlane.GREATER_EQ ).getSubdivision( b ) );
//        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, new HalfPlane( 1, 1, -1, HalfPlane.GREATER_EQ ).getSubdivision( b ) );
//        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, new HalfPlane( 1, 1, 1, HalfPlane.GREATER_EQ ).getSubdivision( b ) );
        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, new HalfPlane( 1, 0, 2, HalfPlane.GREATER_EQ ).getSubdivision( b ) );

        drawer.initialize();
    }

    static
    void testHalfPlaneIntersection1() {
        int size = 20;
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( -1, 1, 0, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 1, 1, 0, HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/kpgbzay9
    static
    void testHalfPlaneIntersection2() {
        int size = 20;
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( -1, 1, 0, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 1, -1, 2, HalfPlane.GREATER_EQ  ) );

        H.add( new HalfPlane( -1, 1, 2, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 1, -1, 4, HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/hbrud29s
    static
    void testHalfPlaneIntersection3() {
        int size = 20;
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( -1, 1, 3, HalfPlane.GREATER_EQ ) );

        LinearTwoUnknowns eq2 = new HalfPlane( new Line( -4, -1, 2, -6 ).equation, HalfPlane.GREATER_EQ ).eq;
        HalfPlane h2 = new HalfPlane( -eq2.a, -eq2.b, -eq2.c, HalfPlane.GREATER_EQ );
//        System.out.println( h2.contains( new Vector( 2, 5 ) ) );
        H.add( h2 );

        H.add( new HalfPlane( 1, 0, 2, HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/tk6pfxqv
    static
    void testHalfPlaneIntersection4() {
        int size = 20;
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( -1, 1, 0, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 1, 1, 0, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 0, -1, 0, HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/hbrud29s
    static
    void testHalfPlaneIntersection5() {
        int size = 20;
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( 1, -1, -3, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( new Line( -4, -1, 2, -6 ).equation, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( -1, 0, -2, HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/hnkskeza
    static
    void testHalfPlaneIntersection6() {
        int size = 60;
        List<HalfPlane> H = new ArrayList<>();
//        System.out.println( new HalfPlane( -9, 1, 41, HalfPlane.GREATER_EQ ).contains( new Vector( -3, 4 ) ) );
        H.add( new HalfPlane( -9, 1, 41, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( new Line( 4, -2, 7, 3 ).equation, HalfPlane.GREATER_EQ ).contains( new Vector( -3, 4 ) ) );
        LinearTwoUnknowns eq = new Line( 4, -2, 7, 3 ).equation;
//        System.out.println( new HalfPlane( new LinearTwoUnknowns( -eq.a, -eq.b, -eq.c ), HalfPlane.GREATER_EQ ).contains( new Vector( -3, 4 ) ) );
        H.add( new HalfPlane( new LinearTwoUnknowns( -eq.a, -eq.b, -eq.c ), HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( -4, -1, 8, HalfPlane.GREATER_EQ ).contains( new Vector( 3, 7 ) ) );
        H.add( new HalfPlane( -4, -1, 8, HalfPlane.GREATER_EQ ) );

        H.add( new HalfPlane( 0, -1, 2, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( -0.5, 1, 5.5, HalfPlane.GREATER_EQ ).contains( new Vector( 4, -2 ) ) );
        H.add( new HalfPlane( -0.5, 1, 5.5, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( 1, 1, 12, HalfPlane.GREATER_EQ ).contains( new Vector( 4, -2 ) ) );
        H.add(  new HalfPlane( 1, 1, 12, HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/q348aary
    static
    void testHalfPlaneIntersection7() {
        int size = 40;
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( -1, 0, 3, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( new Line( 4, -2, 7, 3 ).equation, HalfPlane.GREATER_EQ ).contains( new Vector( -3, 4 ) ) );
        LinearTwoUnknowns eq = new Line( -6, 1, -3, -4 ).equation;
//        System.out.println( new HalfPlane( new LinearTwoUnknowns( -eq.a, -eq.b, -eq.c ), HalfPlane.GREATER_EQ ).contains( new Vector( -3, -1 ) ) );
        H.add( new HalfPlane( new LinearTwoUnknowns( -eq.a, -eq.b, -eq.c ), HalfPlane.GREATER_EQ ) );

        eq = new Line( -5, -1, 1, 6 ).equation;
//        System.out.println( new HalfPlane( new LinearTwoUnknowns( eq.a, eq.b, eq.c ), HalfPlane.GREATER_EQ ).contains( new Vector( -3, -1 ) ) );
        H.add( new HalfPlane( eq, HalfPlane.GREATER_EQ ) );

        eq = new Line( -2, 6, 4, 5 ).equation;
//        System.out.println( new HalfPlane( new LinearTwoUnknowns( eq.a, eq.b, eq.c ), HalfPlane.GREATER_EQ ).contains( new Vector( -3, -1 ) ) );
        H.add( new HalfPlane( eq, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( 0.5, 1, 8, HalfPlane.GREATER_EQ ).contains( new Vector( -3, -1 ) ) );
        H.add( new HalfPlane( 0.5, 1, 8, HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/xa3687yb
    static
    void testHalfPlaneIntersection8() {
        int size = 40;
        List<HalfPlane> H = new ArrayList<>();
//        System.out.println( new HalfPlane( -6, 1, 22, HalfPlane.GREATER_EQ ).contains( new Vector( 3, 3 ) ) );
        H.add( new HalfPlane( -6, 1, 22, HalfPlane.GREATER_EQ ) );

        LinearTwoUnknowns eq = new Line( 2, 5, 8, 1 ).equation;
//        System.out.println( new HalfPlane( new LinearTwoUnknowns( eq.a, eq.b, eq.c ), HalfPlane.GREATER_EQ ).contains( new Vector( 3, 3 ) ) );
        H.add( new HalfPlane( eq, HalfPlane.GREATER_EQ ) );

        eq = new Line( -3, 2, 3, 3 ).equation;
//        System.out.println( new HalfPlane( new LinearTwoUnknowns( eq.a, eq.b, eq.c ), HalfPlane.GREATER_EQ ).contains( new Vector( -3, -1 ) ) );
        H.add( new HalfPlane( eq, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( new LinearTwoUnknowns( eq.a, eq.b, eq.c ), HalfPlane.GREATER_EQ ).contains( new Vector( -3, -1 ) ) );
        H.add( new HalfPlane( new LinearTwoUnknowns( -eq.a, -eq.b, -eq.c ), HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    static
    void testHalfPlaneIntersection9() {
        int size = 40;
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( -6, 1, 22, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( -6, 1, 22, HalfPlane.GREATER_EQ ) );

        LinearTwoUnknowns eq = new Line( 2, 5, 8, 1 ).equation;
//        System.out.println( new HalfPlane( new LinearTwoUnknowns( eq.a, eq.b, eq.c ), HalfPlane.GREATER_EQ ).contains( new Vector( 3, 3 ) ) );
        H.add( new HalfPlane( eq, HalfPlane.GREATER_EQ ) );
        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/tk6pfxqv
    static
    void testHalfPlaneIntersection10() {
        int size = 20;
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( -1, 1, 0, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 1, 1, 0, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 0, -1, 0, HalfPlane.GREATER_EQ ) );

//        H.add( new HalfPlane( -3, 1, -9, HalfPlane.GREATER_EQ ) ); // exclude the point.
//        H.add( new HalfPlane( 3, -1, 9, HalfPlane.GREATER_EQ ) ); // include the point.
        H.add( new HalfPlane( 1, 0, 0, HalfPlane.GREATER_EQ ) ); // lies on the point.

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/xa3687yb
    static
    void testHalfPlaneIntersection11() {
        int size = 40;
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( -6, 1, 22, HalfPlane.GREATER_EQ ) );

        LinearTwoUnknowns eq = new Line( 2, 5, 8, 1 ).equation;
        H.add( new HalfPlane( eq, HalfPlane.GREATER_EQ ) );

        eq = new Line( -3, 2, 3, 3 ).equation;
        H.add( new HalfPlane( eq, HalfPlane.GREATER_EQ ) );

        H.add( new HalfPlane( new LinearTwoUnknowns( -eq.a, -eq.b, -eq.c ), HalfPlane.GREATER_EQ ) );

        // exclude the segment.
        eq = new Line( -4, 4, 2, 8 ).equation;
//        System.out.println( new HalfPlane( -eq.a, -eq.b, -eq.c, HalfPlane.GREATER_EQ ).contains( new Vector( 0, 7 ) ) );
//        H.add( new HalfPlane( -eq.a, -eq.b, -eq.c, HalfPlane.GREATER_EQ ) );

        // include the segmetn.
//        H.add( new HalfPlane( eq, HalfPlane.GREATER_EQ ) );

        // intercept the segment.
//        System.out.println( new HalfPlane( 0.4, 1, 3, HalfPlane.GREATER_EQ ).contains( new Vector( -3, 2 ) ) );
        H.add( new HalfPlane( 0.4, 1, 3, HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/kujbreuh
    static
    void testHalfPlaneIntersection12() {
        int size = 40;
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( -1, 1, 0, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 1, 1, 0, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 0, -1, 0, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( -0.6, 1, 3.8, HalfPlane.GREATER_EQ ).contains( new Vector( -4, 1 ) ) );
//        H.add( new HalfPlane( -0.6, 1, 3.8, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 0.6, -1, -3.8, HalfPlane.GREATER_EQ ) );

        LinearTwoUnknowns eq = new Line( -4, 1, -1, -3 ).equation;
//        System.out.println( new HalfPlane( -eq.a, -eq.b, -eq.c, HalfPlane.GREATER_EQ ).contains( new Vector( -3, 2 ) ) );
//        H.add( new HalfPlane( -eq.a, -eq.b, -eq.c, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( eq.a, eq.b, eq.c, HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/hbrud29s
    static
    void testHalfPlaneIntersection13() {
        int size = 60;
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( -1, 1, 3, HalfPlane.GREATER_EQ ) );

        LinearTwoUnknowns eq2 = new HalfPlane( new Line( -4, -1, 2, -6 ).equation, HalfPlane.GREATER_EQ ).eq;
        HalfPlane h2 = new HalfPlane( -eq2.a, -eq2.b, -eq2.c, HalfPlane.GREATER_EQ );
//        System.out.println( h2.contains( new Vector( 2, 5 ) ) );
        H.add( h2 );

        H.add( new HalfPlane( 1, 0, 2, HalfPlane.GREATER_EQ ) );

        // include the face.
//        System.out.println( new HalfPlane( 2, -1, 16, HalfPlane.GREATER_EQ ).contains( new Vector( 0, 0 ) ) );
//        H.add( new HalfPlane( 2, -1, 16, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( 2, -1, 16, HalfPlane.GREATER_EQ ).contains( new Vector( 0, 0 ) ) );
//        H.add( new HalfPlane( 3, 1, 24, HalfPlane.GREATER_EQ ) );

        // exclude the face.
//        H.add( new HalfPlane( -2, 1, -16, HalfPlane.GREATER_EQ ) );
//        H.add( new HalfPlane( -3, -1, -24, HalfPlane.GREATER_EQ ) );

        // a point.
//        H.add( new HalfPlane( 1, 0, 12, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( -0.25, -1, -5.5, HalfPlane.GREATER_EQ ).contains( new Vector( 6, 6 ) ) );
//        H.add( new HalfPlane( -0.25, -1, -5.5, HalfPlane.GREATER_EQ ) );

        // intersection.
//        System.out.println( new HalfPlane( 0.5, -1, 4, HalfPlane.GREATER_EQ ).contains( new Vector( 0, 0 ) ) );
        H.add( new HalfPlane( 0.5, -1, 4, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( 0.25, 1, 3.5, HalfPlane.GREATER_EQ ).contains( new Vector( 0, 0 ) ) );
        H.add( new HalfPlane( 0.25, 1, 3.5, HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // https://www.geogebra.org/calculator/j8bw7dgw
    static
    void testHalfPlaneIntersection14() {
        int size = 160;
        List<HalfPlane> H = new ArrayList<>();
//        H.add( new HalfPlane( -0.25, 1, -2.5, HalfPlane.GREATER_EQ ) );
//        H.add( new HalfPlane( 0.25, -1, 2.5, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( 1.5, -1, 7.5, HalfPlane.GREATER_EQ ).contains( Vector.origin ) );
//        H.add( new HalfPlane( 1.5, -1, 7.5, HalfPlane.GREATER_EQ ) );

        LinearTwoUnknowns eq = new Line( -6, -2, -3, -4 ).equation;
//        System.out.println( new HalfPlane( -eq.a, -eq.b, -eq.c, HalfPlane.GREATER_EQ ).contains( Vector.origin ) );
//        H.add( new HalfPlane( -eq.a, -eq.b, -eq.c, HalfPlane.GREATER_EQ ) );

        // empty
        H.add( new HalfPlane( -1, 0, 6, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( -1, 1, 6, HalfPlane.GREATER_EQ ).contains( Vector.origin ) );
        H.add( new HalfPlane( -1, 1, 6, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 1, -1, -6, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( 0.5, 1, 5.5, HalfPlane.GREATER_EQ ).contains( Vector.origin ) );
        H.add( new HalfPlane( 0.5, 1, 5.5, HalfPlane.GREATER_EQ ) );

        // end point
//        H.add( new HalfPlane( 1, 0, -6, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( -0.75, -1, 4.5, HalfPlane.GREATER_EQ ).contains( Vector.origin ) );
//        H.add( new HalfPlane( 0.5, 1, 5.5, HalfPlane.GREATER_EQ ) );

//        H.add( new HalfPlane( 1, 0, -2, HalfPlane.GREATER_EQ ) );

        // intersection point.
//        H.add( new HalfPlane( 1, 0, -2, HalfPlane.GREATER_EQ ) );
//        H.add( new HalfPlane( 0, 1, 4, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( 1, 1, 0, HalfPlane.GREATER_EQ ).contains( new Vector( -4, 2 ) ) );
//        H.add( new HalfPlane( 1, 1, 0, HalfPlane.GREATER_EQ ) );

        // segment
//        System.out.println( new HalfPlane( -3, -1, 28, HalfPlane.GREATER_EQ ).contains( Vector.origin ) );
        H.add( new HalfPlane( -3, -1, 28, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 3, 1, -28, HalfPlane.GREATER_EQ ) );

//        System.out.println( new HalfPlane( 1, 1, -2, HalfPlane.GREATER_EQ ).contains( new Vector( -8, -4 ) ) );
        H.add( new HalfPlane( 1, 1, -2, HalfPlane.GREATER_EQ ) );

        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    // [2.0 x + 1.0 y <= 2.0, -2.0 x + -1.0 y <= 4.0, -2.0 x + 1.0 y <= 6.0, 2.0 x + -1.0 y <= 1.0, -12.0 x + 1.0 y <= -2.0, 12.0 x + -1.0 y <= 8.0]
    static
    void testHalfPlaneIntersection15() {
        List<HalfPlane> H = new ArrayList<>();
        H.add( new HalfPlane( 2, 1, 2, HalfPlane.GREATER_EQ ) );

        H.add( new HalfPlane( -2, -1, 4, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( -2, 1, 6, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( 2, -1, 1, HalfPlane.GREATER_EQ ) );
        H.add( new HalfPlane( -12, 1, -2, HalfPlane.GREATER_EQ ) );

        H.add( new HalfPlane( 12, -1, 8, HalfPlane.GREATER_EQ ) );

        int size = 10000000;
        new HalfPlaneIntersection( size, size, Vector.origin ).intersect( H );
    }

    public static
    void main( String[] args ) {
//        testHalfPlane();
//        testHalfPlaneToFace();

//        testHalfPlaneIntersection1(); // simple unbounded convex region.
//        testHalfPlaneIntersection2(); // parallel unbounded convex region.
//        testHalfPlaneIntersection3(); // simple closed convex region, triangle.
//        testHalfPlaneIntersection4(); // a point.
//        testHalfPlaneIntersection5(); // empty.
//        testHalfPlaneIntersection6(); // complex closed convex region.
//        testHalfPlaneIntersection7(); // complex unbounded convex region.
//        testHalfPlaneIntersection8(); // segment
//        testHalfPlaneIntersection9(); // duplicate
//        testHalfPlaneIntersection10(); // point + half-plane
//        testHalfPlaneIntersection11(); // segment + half-plane
//        testHalfPlaneIntersection12(); // unbounded + point.
//        testHalfPlaneIntersection13(); // unbounded + face.
//        testHalfPlaneIntersection14(); // segment + segment
        testHalfPlaneIntersection15(); // fruit ninja test case 2
    }
}
