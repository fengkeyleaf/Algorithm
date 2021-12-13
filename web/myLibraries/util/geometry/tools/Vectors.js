"use strict"

/*
 * Vectors.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import MyMath from "../../../lang/MyMath.js";

/**
 * This class consists exclusively of static methods that
 * related to Vector
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/tools/Vectors.java>Vectors</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Vectors {

    /**
     * get the max vector among two
     * */

    static max( vector1, vector2, comparator ) {
        let res = comparator.compare( vector1, vector2 );
        if ( res > 0 ) return vector1;
        else if ( res < 0 ) return vector2;

        return vector1;
    }

    /**
     * compare two vectors
     * */

    static compare( point1, point2, comparator ) {
        return comparator.compare( point1, point2 );
    }

    /**
     * dot multiplication, vector * vector
     * */

    static dot( vector1, vector2 ) {
        return vector1.x * vector2.x + vector1.y * vector2.y;
    }

    /**
     * cross multiplication,  vector x vector
     * */

    static cross( vector1, vector2 ) {
        return vector1.x * vector2.y - vector1.y * vector2.x;
    }

    /**
     * sort By Y, increasing order
     * */

    static sortByY( point1, point2 ) {
        if ( MyMath.isEqual( point1.y, point2.y ) )
            return MyMath.doubleCompare( point1.x, point2.x );

        return MyMath.doubleCompare( point1.y, point2.y );
    }

    /**
     * sort By Y, decreasing order
     * */

    static sortByYDecreasing( point1, point2 ) {
        if ( MyMath.isEqual( point1.y, point2.y ) )
            return -MyMath.doubleCompare( point1.x, point2.x );

        return -MyMath.doubleCompare( point1.y, point2.y );
    }

    /**
     * sort By X
     * */

    static sortByX( point1, point2 ) {
        if ( MyMath.isEqual( point1.x, point2.x ) )
            return MyMath.doubleCompare( point1.y, point2.y );

        return MyMath.doubleCompare( point1.x, point2.x );
    }

    /**
     * is the point below the base?
     * including on the same line
     * */

    static isBelow( base, vector ) {
        return MyMath.doubleCompare( base.y, vector.y ) >= 0;
    }

    /**
     * is the point above the base?
     * including on the same line
     * */

    static isAbove( base, vector ) {
        return MyMath.doubleCompare( base.y, vector.y ) <= 0;
    }

    /**
     * is the point left the base?
     * */

    static isLeft( base, vector ) {
        return MyMath.doubleCompare( base.x, vector.x ) > 0;
    }

    /**
     * is the point right the base?
     * */

    static isRight( base, vector ) {
        return MyMath.doubleCompare( base.x, vector.x ) < 0;
    }
}