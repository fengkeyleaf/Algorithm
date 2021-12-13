"use strict"

/*
 * Triangles.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Vector from "../elements/point/Vector.js";
import Vectors from "../tools/Vectors.js";
import MyMath from "../../../lang/MyMath.js";

/**
 * This class consists exclusively of static methods that related to triangle
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/tools/Triangles.java>Triangles</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Triangles {

    static getCenterOfGravityFromList( points ) {
        console.assert( points.size() === 3 );
        return this.getCenterOfGravity( points[ 0 ], points[ 1 ], points[ 2 ] );
    }

    /**
     * get Center Of Gravity of a triangle
     * */

    static getCenterOfGravity( A, B, C ) {
        console.assert( !MyMath.isEqualZero( this.areaTwo( A, B, C ) ) );

        return new Vector( ( A.x + B.x + C.x ) / 3, ( A.y + B.y + C.y ) / 3 );
    }

    /**
     * get radian formed by point A, B and C, A as the center of angle
     * */

    static getRadian( A, B, C ) {
        const AB = B.subtract( A );
        const AC = C.subtract( A );

        let res = Vectors.dot( AB, AC ) / ( AB.norm() * AC.norm() );
        // check to avoid NaN
        console.assert( MyMath.doubleCompare( res, 1 ) <= 0, res + " " + Vectors.dot( AB, AC ) + " " + AB.norm() * AC.norm() );
        console.assert( MyMath.doubleCompare( res, -1 ) >= 0, res );

        return Math.acos( res );
    }

    /**
     * compare two angles at different side of the base line,
     * we just need to use the results of toLest test to do so.
     * */

    static clockWiseAngleCompareToToLeft( toLeftC, toLeftD ) {
        // angle with C is greater than
        // that with D in clockwise order,
        // IFF,
        // C is on the right,
        // and D is on the left or on the same line
        // on which A and B are
        return ( MyMath.doubleCompare( toLeftC, 0 ) < 0 &&
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

    static clockWiseAngleCompareTo( A, B, C, D ) {
        let toLeftC = this.areaTwo( B, A, C );
        let toLeftD = this.areaTwo( B, A, D );

        // compare the result of toLeft test first
        // if the two points are on the same side
        // then compare radians.
        if ( MyMath.isSameSign( toLeftC, toLeftD ) ) {
            // if on the left side,
            if ( MyMath.doubleCompare( toLeftC, 0 ) >= 0 ) {
                console.assert( MyMath.doubleCompare( toLeftD, 0 ) >= 0 );
                // the greater the radian is, the bigger the angle is
                return MyMath.doubleCompare( this.getRadian( A, B, C ), this.getRadian( A, B, D ) );
            }
            // if on the right side,
            console.assert( MyMath.doubleCompare( toLeftD, 0 ) < 0 );
            // the greater the radian is, the smaller the angle is
            return -MyMath.doubleCompare( this.getRadian( A, B, C ), this.getRadian( A, B, D ) );
        }

        // else the angle on the left side
        // is smaller than the one on the right side
        if ( this.clockWiseAngleCompareToToLeft( toLeftC, toLeftD ) )
            return 1;
        if ( this.clockWiseAngleCompareToToLeft( toLeftD, toLeftC ) )
            return -1;

        return 0;
    }


    /**
     * to left test, including area2 == 0
     * */

    static toLeft( point1, point2, point3 ) {
        let res = this.areaTwo( point1, point2, point3 );
        return res >= 0 || MyMath.isEqual( res, 0 );
    }

    static toLeftRigorously( point1, point2, point3 ) {
        return this.areaTwo( point1, point2, point3 ) > 0;
    }

    /**
     * compute area2
     *
     * point1 -> p, point2 -> q, point3 -> s
     * */

    static areaTwo( point1, point2, point3 ) {
        return point1.x * point2.y - point1.y * point2.x +
            point2.x * point3.y - point2.y * point3.x +
            point3.x * point1.y - point3.y * point1.x;
    }
}
