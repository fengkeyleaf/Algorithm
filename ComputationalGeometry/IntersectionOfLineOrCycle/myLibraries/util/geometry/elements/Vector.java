package myLibraries.util.geometry.elements;

/*
 * Vector.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.graph.elements.Node;

/**
 * Data structure of Vector, aka, 2D point
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Vector extends Node {
    public static final Vector origin = new Vector( 0, 0, -1 );
    protected static int minsID = -1;

    public float x;
    public float y;
    // x relative to LTL, lowest then left
    public float xRelativeToLTL;
    // y relative to LTL
    public float yRelativeToLTL;

    /**
     * Constructs to create an instance of Vector
     * */

    public Vector(float x, float y, int ID ) {
        super( ID );
        this.x = x;
        this.y = y;
    }

    /**
     * sort By Y
     * */

    public static
    int sortByY( Vector point1, Vector point2 ) {
        if ( MyMath.equalFloats( point1.y, point2.y ) )
            return Float.compare( point1.x, point2.x );

        return Float.compare( point1.y, point2.y );
    }

    /**
     * sort By X
     * */

    public static
    int sortByX( Vector point1, Vector point2 ) {
        if ( MyMath.equalFloats( point1.x, point2.x ) )
            return Float.compare( point1.y, point2.y );

        return Float.compare( point1.x, point2.x );
    }

    /**
     * dot multiplication, vector * vector
     * */

    public static float dot( Vector vector1, Vector vector2 ) {
        return vector1.x * vector2.x + vector1.y * vector2.y;
    }

    /**
     * cross multiplication,  vector x vector
     * */

    public static float cross( Vector vector1, Vector vector2 ) {
        return vector1.x * vector2.y - vector1.y * vector2.x;
    }

    /**
     * coordinates relative to LTL
     * */

    public void relativeToLTL( float offsetX, float offsetY ) {
        xRelativeToLTL = x - offsetX;
        yRelativeToLTL = y - offsetY;
    }

    /**
     * set X And Y
     * */

    public void setXAndY( float x, float y ) {
        this.x = x;
        this.y = y;
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

    public Vector multiply( float ratio ) {
        return new Vector( ratio * x , ratio * y, minsID-- );
    }

    /**
     * vector division
     * */

    public Vector division( float ratio ) {
        return new Vector( x / ratio , y / ratio, minsID-- );
    }

    /**
     * vector's norm, but without radical
     * */

    public float normWithoutRadical() {
        return x * x + y * y;
    }

    /**
     * vector's norm
     * */

    public float norm() {
        return ( float ) Math.sqrt( normWithoutRadical() );
    }

    private String toStringNormal() {
        return ID + "->" + x + "|" + y;
    }

    private String toStringRelativeToLTL() {
        return ID + "->" + xRelativeToLTL + "|" + yRelativeToLTL;
    }

    private String toStringBoth() {
        return ID + "-> " + x + "`" + y + "|" + xRelativeToLTL + "`" + yRelativeToLTL;
    }

    @Override
    public String toString() {
        return toStringNormal();
    }
}
