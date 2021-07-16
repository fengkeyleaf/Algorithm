package myLibraries.util.geometry.tools;

/*
 * Vectors.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.point.Vector;

import java.util.Comparator;

/**
 * This class consists exclusively of static methods that
 * related to Vector
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Vectors {

    /**
     * get the max vector among two
     * */

    public static
    Vector max( Vector vector1, Vector vector2,
                Comparator<Vector> comparator ) {
        int res = comparator.compare( vector1, vector2 );
        if ( res > 0 ) return vector1;
        else if ( res < 0 ) return vector2;

        return vector1;
    }

    /**
     * compare two vectors
     * */

    public static
    int compare( Vector point1, Vector point2,
                 Comparator<? super Vector> comparator ) {
        return comparator.compare( point1, point2 );
    }

    /**
     * dot multiplication, vector * vector
     * */

    public static
    double dot( Vector vector1, Vector vector2 ) {
        return vector1.x * vector2.x + vector1.y * vector2.y;
    }

    /**
     * cross multiplication,  vector x vector
     * */

    public static
    double cross( Vector vector1, Vector vector2 ) {
        return vector1.x * vector2.y - vector1.y * vector2.x;
    }

    /**
     * sort By Y, increasing order
     * */

    public static
    int sortByY( Vector point1, Vector point2 ) {
        if ( MyMath.isEqualFloats( point1.y, point2.y ) )
            return MyMath.doubleCompare( point1.x, point2.x );

        return MyMath.doubleCompare( point1.y, point2.y );
    }

    /**
     * sort By Y, decreasing order
     * */

    public static
    int sortByYDecreasing( Vector point1, Vector point2 ) {
        if ( MyMath.isEqualFloats( point1.y, point2.y ) )
            return -MyMath.doubleCompare( point1.x, point2.x );

        return -MyMath.doubleCompare( point1.y, point2.y );
    }

    /**
     * sort By X
     * */

    public static
    int sortByX( Vector point1, Vector point2 ) {
        if ( MyMath.isEqualFloats( point1.x, point2.x ) )
            return MyMath.doubleCompare( point1.y, point2.y );

        return MyMath.doubleCompare( point1.x, point2.x );
    }

    /**
     * is the point below the base?
     * including on the same line
     * */

    public static
    boolean isBelow( Vector base, Vector vector ) {
        return MyMath.doubleCompare( base.y, vector.y ) >= 0;
    }

    /**
     * is the point above the base?
     * including on the same line
     * */

    public static
    boolean isAbove( Vector base, Vector vector ) {
        return MyMath.doubleCompare( base.y, vector.y ) <= 0;
    }

    /**
     * is the point left the base?
     * */

    public static
    boolean isLeft( Vector base, Vector vector ) {
        return MyMath.doubleCompare( base.x, vector.x ) > 0;
    }

    /**
     * is the point right the base?
     * */

    public static
    boolean isRight( Vector base, Vector vector ) {
        return MyMath.doubleCompare( base.x, vector.x ) < 0;
    }
}
