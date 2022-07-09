package com.fengkeyleaf.util.geom;

/*
 * Triangles.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.lang.MyMath;

import java.util.List;

/**
 * This class consists exclusively of static methods that related to triangle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class Triangles {

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
        return B.subtract( A ).angleTo( C.subtract( A ) );
    }

    /**
     * compare two angles at different side of the base line,
     * we just need to use the results of toLeft test to do so.
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
     *
     * @deprecated   Be careful with precision issue which can lead to unexpected error
     *               since this method use vector dot production to compute the angle between two vectors.
     * */

    @Deprecated
    static
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
     *
     * @deprecated not full tested, also not consider corner cases, like three points on the same line
     * */

    // TODO: 7/8/2021 not full tested, also not consider corner cases, like three points on the same line
    @Deprecated
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
     * to left test, including area2 == 0.
     * i.e. allow point is left to the line formed from base1 and base2,
     * or they three are on the same line.
     * */

    public static
    boolean toLeft( Vector base1, Vector base2, Vector point ) {
        return MyMath.doubleCompare( 0, areaTwo( base1, base2, point ) ) <= 0;
    }

    /**
     * to left test, excluding area2 == 0,
     * i.e. only allow that point is left to the line formed from base1 and base2,
     * but prohibit that they three are on the same line.
     * */

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
}
