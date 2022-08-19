package com.fengkeyleaf.util.geom;

/*
 * TestIntersection.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/16/2022$
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Test Intersection
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

final class TestIntersection {

    static
    void testSort() {
        int ID = 0;
        Vector point1 = new Vector( 0, 0, ID++ );
        Vector point2 = new Vector( 1, 0, ID++ );
        Vector point3 = new Vector( 1, 1, ID++ );
        Vector point4 = new Vector( 0, 1, ID++ );
        Vector point5 = new Vector( -1, 1, ID++ );
        Vector point6 = new Vector( -1, 0, ID++ );
        Vector point7 = new Vector( -1, -1, ID++ );
        Vector point8 = new Vector( 0, -1, ID++ );
        Vector point9 = new Vector( 1, -1, ID++ );
        Vector point10 = new Vector( 0, 0, ID++ );

//        List<Vector> points = new ArrayList<>();
//        points.add( point1 );
//        points.add( point2 );
//        points.add( point3 );
//        points.add( point4 );
//        points.add( point5 );
//
//        System.out.println( points );
//        points.sort( Vector::sortByX );
//        System.out.println( points );
//
//        System.out.println( points );
//        points.sort( Vector::sortByY );
//        System.out.println( points );

//        System.out.println( Integer.MAX_VALUE );
//        System.out.println( Long.MAX_VALUE );
//        System.out.println( Float.MAX_VALUE );
//        System.out.println( Double.MAX_VALUE );
//
//        System.out.println( Long.MAX_VALUE - Float.MAX_VALUE );
//        System.out.println( Long.MIN_VALUE - Float.MIN_VALUE );
    }

    static
    void testSegmentIntersection() {
        int ID = 0;
        Vector vector1 = new Vector( 1, 0, ID++ );
        Vector vector2 = new Vector( -1, 0, ID++ );
        Vector vector3 = new Vector( 0, -1, ID++ );
        Vector vector4 = new Vector( 0, 1, ID++ );
        Vector vector5 = new Vector( 1, 1, ID++ );
        Vector vector6 = new Vector( -1, -1, ID++ );

        Line line1 = new Line( vector1, vector2 );
        Line line2 = new Line( vector3, vector4 );
        System.out.println( Arrays.toString( line1.intersect( line2 ) ) ); // 0,0

        Line line3 = new Line( vector6, vector5 );
        System.out.println( Arrays.toString( line1.intersect( line3 ) ) ); // 0,0
        System.out.println( Arrays.toString( line2.intersect( line3 ) ) ); // 0,0

        Vector vector7 = new Vector( 0, 3, ID++ );
        Vector vector8 = new Vector( 3, 0, ID++ );
        Vector vector9 = new Vector( 3, 3, ID++ );

        Line line4 = new Line( vector7, vector8 );
        Line line5 = new Line( vector9, vector3 );
        System.out.println( Arrays.toString( line4.intersect( line5 ) ) ); // 1.7142857,1.2857143
        System.out.println( Arrays.toString( line5.intersect( line4 ) ) ); // 1.7142857,1.2857143

        Vector vector10 = new Vector( 4, 3, ID++ );
        Line line6 = new Line( vector10, vector5 );

        System.out.println( Arrays.toString( line3.intersect( line6 ) ) ); // 1.0,1.0
        System.out.println( Arrays.toString( line6.intersect( line6 ) ) ); // null

        Vector vector11 = new Vector( 4, 2, ID++ );
        Line line7 = new Line( vector11, vector1 );
        System.out.println( Arrays.toString( line7.intersect( line6 ) ) ); // null
        System.out.println( Arrays.toString( line6.intersect( line6 ) ) ); // null

        Vector vector12 = new Vector( 1, -1, ID++ );
        Vector vector13 = new Vector( 1, 1, ID++ );
        Vector vector14 = new Vector( -1, 2, ID++ );
        Vector vector15 = new Vector( 2, 2, ID++ );
        Vector vector16 = new Vector( -1, 2, ID++ );

        Line line8 = new Line( vector12, vector13 );
        Line line9 = new Line( vector14, vector13 );
        Line line10 = new Line( vector15, vector13 );
        Line line11 = new Line( vector16, vector13 );

        System.out.println( Arrays.toString( line8.intersect( line9 ) ) ); // 1.0,1.0
        System.out.println( Arrays.toString( line10.intersect( line9 ) ) ); // 1.0,1.0
        System.out.println( Arrays.toString( line10.intersect( line11 ) ) ); // 1.0,1.0
        System.out.println( Arrays.toString( line8.intersect( line11 ) ) ); // 1.0,1.0
    }

    static
    void testSegmentCycleIntersection() {
        int ID = 0;
        final double radius = Math.sqrt( 2 );
        Vector vector1 = new Vector( -radius, 0, ID++ );
        Vector vector2 = new Vector( radius, 0, ID++ );
        Vector vector3 = new Vector( -radius, -1, ID++ );
        Vector vector4 = new Vector( 0, radius, ID++ );
        Vector vector5 = new Vector( 1, radius, ID++ );
        Vector vector6 = new Vector( 1, 1, ID++ );
        Vector vector7 = new Vector( -1, -1, ID++ );
        Vector vector8 = new Vector( -1, 1, ID++ );
        Vector vector9 = new Vector( 0, 2, ID++ );
        Line line1 = new Line( vector1, vector3 );
        Line line2 = new Line( vector6, vector7 );
        Line line3 = new Line( vector4, vector5 );
        Line line4 = new Line( vector9, vector8 );

        Circle cycle1 = new Circle( Vector.origin, radius );
//        System.out.println( lineCycleIntersect( line1, cycle1 ) ); // -11->-1.4142135|0.0 -12->-1.4142135|0.0
//        System.out.println( lineCycleIntersect( line2, cycle1 ) ); // -23->-0.99999994|-0.99999994|-24->0.99999994|0.99999994
//        System.out.println( lineCycleIntersect( line3, cycle1 ) ); // -35->0.0|1.4142135	-36->0.0|1.4142135
//        System.out.println( lineCycleIntersect( line4, cycle1 ) ); // -11->-1.0002441|0.99975586	-12->-0.99975586|1.0002441

        Circle cycle2 = new Circle( Vector.origin, 2 );
        Line line5 = new Line( vector8, vector6 );
        System.out.println( Arrays.toString( GeometricIntersection.lineCircle( line5, cycle2 ) ) );
    }

    static
    void testSegmentArcIntersection() {
        int ID = 0;
        double radius = Math.sqrt( 2 );
        Vector vector1 = new Vector( -radius, 0, ID++ );
        Vector vector2 = new Vector( 0, 0, ID++ );
        Vector vector3 = new Vector( -radius, -1, ID++ );
        Vector vector4 = new Vector( 0, radius, ID++ );
        Vector vector5 = new Vector( 1, radius, ID++ );
        Vector vector6 = new Vector( 1, 1, ID++ );
        Vector vector7 = new Vector( -1, -1, ID++ );
        Vector vector8 = new Vector( -1, 1, ID++ );
        Vector vector9 = new Vector( 0, 2, ID++ );
        Line line1 = new Line( vector1, vector3 );
        Line line2 = new Line( vector6, vector7 );
        Line line3 = new Line( vector4, vector5 );
        Line line4 = new Line( vector9, vector8 );
        Line line5 = new Line( vector2, vector8 );
        Circle cycle = new Circle( Vector.origin, radius );

        int index = 0;
        Line[] lines = new Line[ 5 ];
        lines[ index++ ] = line1;
        lines[ index++ ] = line2;
        lines[ index++ ] = line3;
        lines[ index++ ] = line4;
        lines[ index++ ] = line5;

        Arc[] arcs = cycle.getFourMonotoneQuarters( true );

        for ( int i = 0; i < lines.length; i++ ) {
            System.out.println( "lines(" + ( i + 1 ) + "): " + lines[ i ] );
            for ( int j = 0; j < arcs.length; j++ ) {
                System.out.println( "arc" + ( j + 1 ) + ": " + Arrays.toString( GeometricIntersection.lineArc( lines[ i ], arcs[ j ] ) ) );
            }
            System.out.println();
        }

//        for ( int j = 0; j < arcs.length; j++ ) {
//            System.out.println( "arc" + ( j + 1 ) + ": " + lineArcIntersect( lines[ 3 ], arcs[ j ] ) );
//        }
//        System.out.println( lineArcIntersect( lines[ 3 ], arcs[ 0 ] ) );
    }

    static
    void testOthers() {
        int index = 0;
        PriorityQueue<EventPoint2D> events = new PriorityQueue<>( Vectors::sortByX );
        Vector point1 = new Vector( -1, -1, index++ );
        Vector point2 = new Vector( -1, 1, index++ );
        Vector point3 = new Vector( 1, 1, index++ );
        Vector point4 = new Vector( 1, -1, index++ );
        Line line1 = new Line( point1, point3 );
        Line line2 = new Line( point2, point4 );
        List<Line> lines = new ArrayList<>();
        lines.add( line1 );
        lines.add( line2 );
//        List<EventPoint2D> list = generateEvents( lines );
//        events.addAll( list );
//        System.out.println( events );
//        while ( !events.isEmpty() ) {
//            System.out.println( events.poll() );
//        }
    }

    static
    void testRayLine() {
        int index = 0;
        Vector point1 = new Vector( 8, 3, index++ );
        Vector point2 = new Vector( 5, 4, index++ );
        Vector point3 = new Vector( 1, 1, index++ );
        Vector point4 = new Vector( -1, -1, index++ );
        Line line1 = new Line( point1, point2 );
        Line line2 = new Line( point3, point4 );
        System.out.println( Arrays.toString( line1.intersect( line2 ) ) );
    }

    static
    void testLineIntersection() {
        Line line1 = new Line( 2, 2, 6, 6 );
        Line line2 = new Line( 1, 8, 2, 6 );
        System.out.println( GeometricIntersection.segments( line1, line2 ) );
        System.out.println( GeometricIntersection.lines( line1, line2 ) );

        line1 = new Line( 3, 1 , 2 );
        line2 = new Line( 4, 2, 0 );
        System.out.println( Arrays.toString( line1.intersect( line2 ) ) );

        line1 = new Line( -2.3333333333333335, 1 , 1.3333333333333333 );
        line2 = new Line( -0.3333333333333333, 1, 1.6666666666666665 );
        System.out.println( Arrays.toString( line1.intersect( line2 ) ) );
    }

    public static
    void main( String[] args ) {
//        testSegmentIntersection();
//        testSegmentCycleIntersection();
//        testSegmentArcIntersection();
//        testOthers();
//        testRayLine();
        testLineIntersection();
    }
}
