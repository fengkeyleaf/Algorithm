package com.fengkeyleaf.util.geom;

/*
 * Vector.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 * JDK: 15
 */

import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.util.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of Vector, aka, 2D point
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Vector extends Node {
    public static final Vector origin = new Vector( 0, 0, -1 );
    protected static int minsID = -1;
    protected static int IDStatic = 0;

    public double x;
    public double y;

    /**
     * Constructs to create an instance of Vector
     * */

    public Vector( double x, double y, int ID ) {
        super( ID );
        this.x = x;
        this.y = y;
    }

    public Vector( double x, double y ) {
        this( x, y, IDStatic++ );
    }

    public Vector( Vector v ) {
        this( v.x, v.y );
    }

    /**
     * set X And Y
     * */

    public void setXAndY( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public void setXAndY( Vector vector ) {
        this.x = vector.x;
        this.y = vector.y;
    }

    /**
     * vector addition
     * */

    public Vector add( Vector vector ) {
        return new Vector( x + vector.x, y + vector.y, minsID-- );
    }

    /**
     * vector subtract
     * */

    public Vector subtract( Vector vector ) {
        return new Vector( x - vector.x, y - vector.y, minsID-- );
    }

    /**
     * vector multiplication
     * */

    public Vector multiply( double ratio ) {
        return new Vector( ratio * x , ratio * y, minsID-- );
    }

    /**
     * vector division
     * */

    public Vector division( double ratio ) {
        return new Vector( x / ratio , y / ratio, minsID-- );
    }

    /**
     * vector's norm, but without radical.
     * Computes the square of the Euclidean length (straight-line length).
     * If you are comparing the lengths of vectors,
     * you should compare the length squared instead as it is slightly more efficient to calculate.
     * */

    // https://threejs.org/docs/index.html?q=Vector#api/en/math/Vector2.lengthSq
    public double lengthSq() {
        return x * x + y * y;
    }

    /**
     * vector's norm, Computes the Euclidean length (straight-line length)
     * */

    // https://threejs.org/docs/index.html?q=Vector#api/en/math/Vector2.length
    public double length() {
        return Math.sqrt( lengthSq() );
    }

    /**
     * Returns the angle between this vector and vector v in radians.
     * */

    // https://threejs.org/docs/index.html?q=Vector#api/en/math/Vector3.angleTo
    public double angleTo( Vector v ) {
        double res = dot( v ) / ( length() * v.length() );
        assert MyMath.doubleCompare( res, 1 ) <= 0 && MyMath.doubleCompare( res, -1 ) >= 0;
        return Math.acos( res );
    }

    /**
     * Calculate the dot product of this vector and v.
     * */

    // https://threejs.org/docs/index.html?q=Vector#api/en/math/Vector2.dot
    public double dot( Vector v ) {
        return x * v.x + y * v.y;
    }

    /**
     * Calculates the cross product of this vector and v.
     * Note that a 'cross-product' in 2D is not well-defined.
     * This function computes a geometric cross-product often used in 2D graphics
     * */

    // https://threejs.org/docs/index.html?q=Vector#api/en/math/Vector2.cross
    public double cross( Vector v ) {
        return x * v.y - y * v.x;
    }

    public Vector getEndPoint( Vector startPoint ) {
        return new Vector( x + startPoint.x, y + startPoint.y );
    }

    public Vector getMid( Vector endPoint ) {
        return endPoint.subtract( this ).multiply( 0.5 ).getEndPoint( this );
    }

    /**
     * is the point below this point?
     * including on the same line
     * */

    public boolean isBelow( Vector v ) {
        return MyMath.doubleCompare( y, v.y ) >= 0;
    }

    /**
     * is the point above this point?
     * including on the same line
     * */

    public boolean isAbove( Vector v ) {
        return MyMath.doubleCompare( y, v.y ) <= 0;
    }

    /**
     * is the point left to this point?
     * */

    public boolean isLeft( Vector v ) {
        return MyMath.doubleCompare( x, v.x ) > 0;
    }

    /**
     * is the point right to this point?
     * */

    public boolean isRight( Vector v ) {
        return MyMath.doubleCompare( x, v.x ) < 0;
    }

    private boolean equalsXAndY( double x, double y ) {
        return MyMath.isEqualZero( x - this.x ) &&
                MyMath.isEqualZero( y - this.y );
    }

    /**
     * Are x and y of this vector and the vector same?
     * */

    public boolean equalsXAndY( Vector vector ) {
        if ( vector == null ) return false;
        return equalsXAndY( vector.x, vector.y );
    }

    boolean isSameDirection( Vector p1, Vector p2 ) {
        if ( !MyMath.isEqualZero( Triangles.areaTwo( this, p1, p2 ) ) )
            return false;

        List<Vector> P = new ArrayList<>( 2 );
        P.add( p1 );
        P.add( p2 );

        List<List<Vector>> V = Vectors.sortByAngle( this, P );
        for ( List<Vector> L : V )
            if ( L.size() == 2 )
                return true;

        return false;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        return equalsXAndY( ( Vector ) o );
    }

    protected String toStringNormal() {
        return ID + "(" + x + ", " + y + ")";
    }

    protected String toStringNormalWithoutID() {
        return x + "|" + y;
    }

    protected String toStringNormalWithMappingID() {
        return mappingID + "(" + x + ", " + y + ")";
    }

    @Override
    public String toString() {
        return toStringNormal();
    }
}
