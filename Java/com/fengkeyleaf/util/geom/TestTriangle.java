package com.fengkeyleaf.util.geom;

/*
 * TestTriangle.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 6/29/2022$
 */

/**
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class TestTriangle {

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
    void testGetRadian() {
        System.out.println( Math.toDegrees( Triangles.getRadian( Vector.origin, point1, point9 ) ) ); // 0
        System.out.println( Math.toDegrees( Triangles.getRadian( Vector.origin, point1, point3 ) ) ); // 45
        System.out.println( Math.toDegrees( Triangles.getRadian( Vector.origin, point1, point2 ) ) ); // 90

        System.out.println( Math.toDegrees( Triangles.getRadian( Vector.origin, point1, point6 ) ) ); // 135
        System.out.println( Math.toDegrees( Triangles.getRadian( Vector.origin, point1, point7 ) ) ); // 180
        System.out.println( Math.toDegrees( Triangles.getRadian( Vector.origin, point1, point5 ) ) ); // 225
        System.out.println( Math.toDegrees( Triangles.getRadian( Vector.origin, point1, point8 ) ) ); // 270
        System.out.println( Math.toDegrees( Triangles.getRadian( Vector.origin, point1, point4 ) ) ); // 315
    }

    public static
    void testClockWiseAngleCompareTo() {
        // same side, left
        System.out.println( Triangles.clockWiseAngleCompareTo( Vector.origin, point1, point3, point2 ) ); // 1
        System.out.println( Triangles.clockWiseAngleCompareTo( Vector.origin, point1, point6, point3 ) ); // -1
        System.out.println( Triangles.clockWiseAngleCompareTo( Vector.origin, point1, point6, point7 ) ); // 1

        // same side, right
        System.out.println( Triangles.clockWiseAngleCompareTo( Vector.origin, point1, point4, point5 ) ); // -1
        System.out.println( Triangles.clockWiseAngleCompareTo( Vector.origin, point1, point5, point4 ) ); // 1
        System.out.println( Triangles.clockWiseAngleCompareTo( Vector.origin, point1, point7, point4 ) ); // 1

        // opposite side
        System.out.println( Triangles.clockWiseAngleCompareTo( Vector.origin, point1, point3, point5 ) ); // 1
        System.out.println( Triangles.clockWiseAngleCompareTo( Vector.origin, point1, point4, point6 ) ); // -1

        System.out.println( Triangles.clockWiseAngleCompareTo( Vector.origin, point1, point1, point9 ) ); // 0
        System.out.println( Triangles.clockWiseAngleCompareTo( Vector.origin, point1, point1, point7 ) ); // -1
        System.out.println( Triangles.clockWiseAngleCompareTo( Vector.origin, point1, point7, point7 ) ); // 0
    }

    private static
    void testSpecial() {
        Vector p1 = new Vector( 0, 3 );
        Vector p2 = new Vector( -1, -3 );
//        System.out.println( areaTwo( p1, p1, p2 ) );
//
//        System.out.println( areaTwo( new Vector( -1, -3 ), new Vector( 1, 2 ), Vector.origin ) );
//        System.out.println( new Line( new Vector( -1, -3 ), new Vector( 1, 2 ) ).isOnThisSegment( Vector.origin ) );

//        p1 = new Vector( -3.49247897E8, 7.60020688E8 );
//        p2 = new Vector( 4.10773733E8, 7.60020688E8 );
        Vector p3 = new Vector( 4.10773733E8, 7.60020688E8 );
//        System.out.println( Triangles.areaTwo( p1, p2, p3 ) );

//        p1 = new Vector( -3.49247897, 7.60020688 );
//        p2 = new Vector( 4.10773733, 7.60020688 );
//        p3 = new Vector( 4.10773733, 7.60020688 );
//        System.out.println( Triangles.areaTwo( p1, p2, p3 ) );

//        p1 = new Vector( -9, -5 );
//        p2 = new Vector( 5, 7 );
//        p3 = new Vector( -4.84375, -1.4375 );
//        System.out.println( Triangles.toLeft( p1, p2, p3 ) );
//
//        p1 = new Vector( 5, 7 );
//        p2 = new Vector( 9, -7 );
//        System.out.println( Triangles.toLeft( p1, p2, p3 ) );
//
//        p1 = new Vector( 9, -7 );
//        p2 = new Vector( -9, -5 );
//        System.out.println( Triangles.toLeft( p1, p2, p3 ) );

        p1 = new Vector( -4.0, -2.0 );
        p2 = new Vector( -4.84375, -1.4375 );
        p3 = new Vector( 1.0, -5.0 );
        System.out.println( Triangles.toLeft( p1, p2, p3 ) );
    }

    public static
    void main( String[] args ) {
//        testClockWiseAngleCompareTo();
        testSpecial();
    }
}
