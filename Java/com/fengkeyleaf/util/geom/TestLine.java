package com.fengkeyleaf.util.geom;

/*
 * TestLine.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 6/29/2022$
 */

import com.fengkeyleaf.lang.MyMath;

import java.util.Arrays;

/**
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class TestLine {
    static
    void testLine1() {
        int index = 0;
        Vector point1 = new Vector( -1, -1, index++ );
        Vector point2 = new Vector( -1, 1, index++ );
        Vector point3 = new Vector( 1, 1, index++ );
        Vector point4 = new Vector( 1, -1, index++ );
        Line line1 = new Line( point1, point3 );
        Line line2 = new Line( point2, point4 );
        System.out.println( line1.updateY( 1 ) );
        Vector point5 = new Vector( 2, 5, index++ );
        Vector point6 = new Vector( 8, 4, index++ );
        Vector point7 = new Vector( 6, 6, index++ );
        Line line3 = new Line( point5, point6 );
        Line line4 = new Line( point3, point7 );
        double x = 4.5714283;
        x = 4.571428571428571;
//        System.out.println( GeometricIntersection.linesIntersect( line1, line3 ) );
//        System.out.println( GeometricIntersection.linesIntersect( line3, line1 ) );
        System.out.println( Arrays.toString( line3.intersect( line4 ) ) );
        System.out.println( line3.updateY( x ) );
        System.out.println( line4.updateY( x ) );
        System.out.println( Double.compare( line4.updateY( x ), line3.updateY( x ) ) );
        System.out.println( MyMath.isEqual( line4.updateY( x ), line3.updateY( x ) ));
    }

    static
    void testLine2() {
        int index = 0;
        Vector point1 = new Vector( -6, 6, index++ );
        Vector point2 = new Vector( 6, -6, index++ );
        Line line1 = new Line( point1, point2 );
        System.out.println( line1.updateY( -3.5355339059327378 ) );

        double num = Math.pow( 10, 5 );
        double res = 74 * Math.pow( num, 2 ) - 86 * num + 25;
        System.out.println( res );
        System.out.println( Math.sqrt( res ) );
        System.out.println( Math.ceil( Math.sqrt( res ) ) );
    }

    private static
    void testLine3() {
//        System.out.println( Lines.getBisector( new Vector( -1, 0 ), new Vector( 1 , 0 ) ) ); // 0.0y + 1.0x = 0.0
//        System.out.println( Lines.getBisector( new Vector( 0, 1 ), new Vector( 0 , -1 ) ) ); // 1.0y + 0.0x = 0.0
//        System.out.println( Lines.getBisector( new Vector( 1, 1 ), new Vector( -1 , -1 ) ) ); // 1.0y + 1.0x = 0.0

//        System.out.println( new Line( -13, -11, 15, -11 ).isOnThisSegment( new Vector( -5.285714285714285, -11.000000000000007 ) ) );
//        System.out.println( new Line( -13, -11, 15, -11 ).isOnThisLine( new Vector( -5.285714285714285, -11.000000000000007 ) ) );

//        System.out.println( new Line( -3, -1, 3, 2 ).isOnThisSegment( new Vector( -1, 0 ) ) );

//        System.out.println( new Line( -9, -5, 5, 7 ).isOnThisSegment( new Vector( -4.84375, -1.4375 ) ) );

//        System.out.println( Arrays.toString( new Line( -1, -1, 1, 1 ).intersect( new Line( 1, 1, 2, 2 ) ) ) );
//        System.out.println( Arrays.toString( new Segment( -1, -1, 1, 1 ).intersect( new Segment( 1, 1, 2, 2 ) ) ) );
//        System.out.println( Arrays.toString( new Segment( -1, -1, 1, 1 ).intersect( new Segment( 0, 0, 2, 2 ) ) ) );
//        System.out.println( Arrays.toString( new Segment( -1, -1, 1, 1 ).intersect( new Segment( -1, -1, 1, 1 ) ) ) );

//        System.out.println( new Line( 416667.0, -5000000.0, 416668.0, 5000000.0 ).isOnThisLine( new Vector( 416667.4166661166, -833338.8333322331 ) ) );
//        System.out.println( new Line( 416667.0, -5000000.0, 416668.0, 5000000.0 ).updateY( 416667.4166661166 ) );
//        System.out.println( Arrays.toString( new Line( 416667.0, -5000000.0, 416668.0, 5000000.0 ).intersect( new Line( -2500002, 5000000, 2499998, -5000000 ) ) ) );

        System.out.println( Arrays.toString( new Line( 416666.8333333333, 5000000.0, -416666.5,-5000000.0 ).intersect( new Line( -2500002,5000000, 2499998,-5000000 ) ) ) );
    }

    static
    void testBisector() {
        // Vertical: 0.0 | 0.0y + 1.0x = 0.0
//        System.out.println( Lines.getBisector( new Vector( -1, 0 ), new Vector( 1, 0 ) ) );
        // null<->null | 1.0y + -1.0x = 0.0
//        System.out.println( Lines.getBisector( new Vector( -1, 1 ), new Vector( 1, -1 ) ) );
        // null<->null | eq:1.0y + -2.3333333333333335x = 1.3333333333333333
//        System.out.println( Lines.getBisector( new Vector( -3, 4 ), new Vector( 4, 1 ) ) );
        // null<->null | eq: 1.0y + -0.3333333333333333x = 1.6666666666666665
//        System.out.println( Lines.getBisector( new Vector( -3, 4 ), new Vector( -1, -2 ) ) );
        // null<->null | eq: 1.0y + 1.6666666666666667x = 2.0
//        System.out.println( Lines.getBisector( new Vector( 4, 1 ), new Vector( -1, -2 ) ) );
    }

    public static
    void main( String[] args ) {
        testLine3();
    }
}
