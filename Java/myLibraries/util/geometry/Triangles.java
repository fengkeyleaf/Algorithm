package myLibraries.util.geometry;

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
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.DCEL.HalfEdge;
import myLibraries.util.geometry.elements.Vector;

import java.util.List;

/**
 * This class consists exclusively of static methods that related to triangle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Triangles {

    /**
     * getCenterOfGravityFromList() in JavaScript Version
     * */

    public static
    Vector getCenterOfGravity( List<Vector> points ) {
        assert points.size() == 3;
        return getCenterOfGravity( points.get( 0 ), points.get( 1 ), points.get( 2 ) );
    }

    /**
     * get Center Of Gravity of a triangle
     * */

    public static
    Vector getCenterOfGravity( Vector A, Vector B, Vector C ) {
        assert !MyMath.isEqualZero( areaTwo( A, B, C ) );

        return new Vector( ( A.x + B.x + C.x ) / 3, ( A.y + B.y + C.y ) / 3 );
    }

    /**
     * get radian formed by point A, B and C, A as the center of angle
     * */

    public static
    double getRadian( Vector A, Vector B, Vector C ) {
        Vector AB = B.subtract( A );
        Vector AC = C.subtract( A );

        double res = AB.dot( AC ) / ( AB.length() * AC.length() );
        // check to avoid NaN
        assert MyMath.doubleCompare( res, 1 ) <= 0 : res + " " + AB.dot( AC ) + " " + AB.length() * AC.length();
        assert MyMath.doubleCompare( res, -1 ) >= 0 : res;

        return Math.acos( res );
    }

    /**
     * compare two angles at different side of the base line,
     * we just need to use the results of toLeft test to do so.
     *
     * clockWiseAngleCompareToToLeft() in JavaScript Version
     * */

    private static
    boolean clockWiseAngleCompareTo( double toLeftC, double toLeftD ) {
        return  // angle with C is greater than
                // that with D in clockwise order,
                // IFF,
                // C is on the right,
                // and D is on the left or on the same line
                // on which A and B are
                ( MyMath.doubleCompare( toLeftC, 0 ) < 0 &&
                MyMath.doubleCompare( toLeftD, 0 ) >= 0 )
                || // or
                // C is on the same line on which A and B are
                // and D is on the left
                ( MyMath.isEqualZero( toLeftC ) &&
                        MyMath.doubleCompare( toLeftD, 0 ) > 0 );
    }

    /**
     * compare the two clockwise angles with line AB as base,
     * forming the angles with point C or D, separately
     * */

    public static
    int clockWiseAngleCompareTo( Vector A, Vector B, Vector C, Vector D ) {
        double toLeftC = areaTwo( B, A, C );
        double toLeftD = areaTwo( B, A, D );

        // compare the result of toLeft test first
        // if the two points are on the same side
        // then compare radians.
        if ( MyMath.isSameSign( toLeftC, toLeftD ) ) {
            // if on the left side,
            if ( MyMath.doubleCompare( toLeftC, 0 ) >= 0 ) {
                assert MyMath.doubleCompare( toLeftD, 0 ) >= 0;
                // the greater the radian is, the bigger the angle is
                return MyMath.doubleCompare( getRadian( A, B, C ), getRadian( A, B, D ) );
            }
            // if on the right side,
            assert MyMath.doubleCompare( toLeftD, 0 ) < 0;
            // the greater the radian is, the smaller the angle is
            return -MyMath.doubleCompare( getRadian( A, B, C ), getRadian( A, B, D ) );
        }

        // else the angle on the left side
        // is smaller than the one on the right side
        if ( clockWiseAngleCompareTo( toLeftC, toLeftD ) )
            return 1;
        if ( clockWiseAngleCompareTo( toLeftD, toLeftC ) )
            return -1;

        return 0;
    }

    /**
     * is the passed-in polygon a triangle?
     * */

    // TODO: 7/8/2021 not full tested, also not consider corner cases, like three points on the same line
    public static
    boolean isTriangle( Face face ) {
        int vertexCount = 0;
        HalfEdge edge = face.outComponent;
        do {
            vertexCount++;
            assert edge.incidentFace == face;
            edge = edge.next;
        } while ( edge != face.outComponent );

        boolean res = vertexCount == 3;
        assert !( res & !MonotonePolygons.isMonotonePolygon( face ) ) : res + " " + MonotonePolygons.isMonotonePolygon( face );
        return res;
    }

    /**
     * comparator to find LTL
     * */

    public static
    int LTL( Vector point1, Vector point2 ) {
        if ( point1.y == point2.y )
            return Double.compare( point1.x, point2.x );

        return Double.compare( point1.y, point2.y );
    }

    /**
     * to left test, including area2 == 0
     * */

    public static
    boolean toLeft( Vector base1, Vector base2, Vector point ) {
        return MyMath.doubleCompare( 0, areaTwo( base1, base2, point ) ) <= 0;
    }

    public static
    boolean toLeftRigorously( Vector base1, Vector base2, Vector point ) {
        return MyMath.doubleCompare( 0, areaTwo( base1, base2, point ) ) < 0;
    }


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
    void testGetRadian() {
        System.out.println( Math.toDegrees( getRadian( Vector.origin, point1, point9 ) ) ); // 0
        System.out.println( Math.toDegrees( getRadian( Vector.origin, point1, point3 ) ) ); // 45
        System.out.println( Math.toDegrees( getRadian( Vector.origin, point1, point2 ) ) ); // 90

        System.out.println( Math.toDegrees( getRadian( Vector.origin, point1, point6 ) ) ); // 135
        System.out.println( Math.toDegrees( getRadian( Vector.origin, point1, point7 ) ) ); // 180
        System.out.println( Math.toDegrees( getRadian( Vector.origin, point1, point5 ) ) ); // 225
        System.out.println( Math.toDegrees( getRadian( Vector.origin, point1, point8 ) ) ); // 270
        System.out.println( Math.toDegrees( getRadian( Vector.origin, point1, point4 ) ) ); // 315
    }

    public static
    void testClockWiseAngleCompareTo() {
        // same side, left
        System.out.println( clockWiseAngleCompareTo( Vector.origin, point1, point3, point2 ) ); // 1
        System.out.println( clockWiseAngleCompareTo( Vector.origin, point1, point6, point3 ) ); // -1
        System.out.println( clockWiseAngleCompareTo( Vector.origin, point1, point6, point7 ) ); // 1

        // same side, right
        System.out.println( clockWiseAngleCompareTo( Vector.origin, point1, point4, point5 ) ); // -1
        System.out.println( clockWiseAngleCompareTo( Vector.origin, point1, point5, point4 ) ); // 1
        System.out.println( clockWiseAngleCompareTo( Vector.origin, point1, point7, point4 ) ); // 1

        // opposite side
        System.out.println( clockWiseAngleCompareTo( Vector.origin, point1, point3, point5 ) ); // 1
        System.out.println( clockWiseAngleCompareTo( Vector.origin, point1, point4, point6 ) ); // -1

        System.out.println( clockWiseAngleCompareTo( Vector.origin, point1, point1, point9 ) ); // 0
        System.out.println( clockWiseAngleCompareTo( Vector.origin, point1, point1, point7 ) ); // -1
        System.out.println( clockWiseAngleCompareTo( Vector.origin, point1, point7, point7 ) ); // 0
    }

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
