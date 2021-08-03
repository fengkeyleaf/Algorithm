package myLibraries.util.geometry.elements.point;

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
    protected static int IDStatic = 0;

    public double x;
    public double y;
    // x relative to LTL, lowest then left
    public double xRelativeToLTL;
    // y relative to LTL
    public double yRelativeToLTL;

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

    /**
     * coordinates relative to LTL
     * */

    public void relativeToLTL( double offsetX, double offsetY ) {
        xRelativeToLTL = x - offsetX;
        yRelativeToLTL = y - offsetY;
    }

    /**
     * set X And Y
     * */

    public void setXAndY( double x, double y ) {
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
     * vector's norm, but without radical
     * */

    public double normWithoutRadical() {
        return x * x + y * y;
    }

    /**
     * vector's norm
     * */

    public double norm() {
        return Math.sqrt( normWithoutRadical() );
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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        return equalsXAndY( ( Vector ) o );
    }

    protected String toStringNormal() {
        return ID + "->" + x + "|" + y;
    }

    private String toStringNormalWithoutID() {
        return x + "|" + y;
    }

    private String toStringRelativeToLTL() {
        return ID + "->" + xRelativeToLTL + "|" + yRelativeToLTL;
    }

    private String toStringBoth() {
        return ID + "-> " + x + "`" + y + "|" + xRelativeToLTL + "`" + yRelativeToLTL;
    }

    @Override
    public String toString() {
        return toStringNormalWithoutID();
    }
}
