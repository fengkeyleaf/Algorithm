package com.fengkeyleaf.util.geom;

/*
 * Circle.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.lang.QuadraticTwoUnknown;

import java.util.Arrays;
import java.util.Objects;

/**
 * Data structure of Circle
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Circle implements Intersection {
    private static int IDStatic = 0;
    private final int ID;
    public final Vector center;
    public final double radius;
    public final Vector[] extremes;
    private final double[] xRange = new double[ 2 ];
    private final double[] yRange = new double[ 2 ];

    private final QuadraticTwoUnknown equation;

    /**
     * constructs to create an instance of Circle with center and radius
     * */

    public Circle( Vector center, double radius ) {
        if ( MyMath.doubleCompare( radius, 0 ) < 0 ) {
            new Exception( "radius cannot be negative!" ).printStackTrace();
            System.exit( 1 );
        }

        ID = IDStatic++;
        this.center = center;
        this.radius = radius;

        xRange[ 0 ] = center.x - radius;
        xRange[ 1 ] = center.x + radius;
        yRange[ 0 ] = center.y + radius;
        yRange[ 1 ] = center.y - radius;

        // clockwise
        extremes  = new Vector[ 4 ];
        // left
        extremes[ 0 ] = new Vector( center.x - radius, center.y, -1 );
        // top
        extremes[ 1 ] = new Vector( center.x, center.y + radius, -1 );
        // right
        extremes[ 2 ] = new Vector( center.x + radius, center.y, -1 );
        // bottom
        extremes[ 3 ] = new Vector( center.x, center.y - radius, -1 );

        // TODO: 1/2/2022 implement
        equation = null;
    }

    /**
     * constructs to create an instance of Circle with equation.
     * x² + y² + Dx + Ey + F = 0, ( D² + E² - 4F > 0 ).
     * ( X + D/2 )²+( Y + E/2 )² = ( D² + E² - 4F ) / 4.
     *
     * */

    public Circle( double d, double e, double f ) {
        ID = IDStatic++;
        equation = new QuadraticTwoUnknown( d, e, f );
        center = new Vector( -d / 2, -e / 2 );

        assert MyMath.isPositive( d * d + e * e - 4 * f );
        radius = Math.sqrt( ( d * d + e * e - 4 * f ) / 4 );

        // TODO: 1/2/2022 implement
        extremes = null;
    }

    public Vector getLowest() {
        return new Vector( 0, -1 ).multiply( radius ).add( center );
    }

    /**
     * get the upper left point of the rectangle bounding this circle.
     * Mainly for drawing purpose.
     * @see <a href="https://docs.oracle.com/en/java/javase/16/docs/api/java.desktop/java/awt/Graphics.html#drawOval(int,int,int,int)">drawOval(int x, int y, int width, int height)</a>
     * */

    public Vector getUpperLeft() {
        return new Vector( center.x - radius, center.y + radius );
    }

    /**
     * the point lies on this arc?
     *
     * Note that the point must be the intersection points
     * with the cycle and a line,
     * i.e. not an arbitrary point
     * */

    public boolean belong( Vector point ) {
        return belongX( point.x ) && belongY( point.y );
    }

    boolean belongX( double x ) {
        if ( x > xRange[ 1 ] || x < xRange[ 0 ] )
            return false;

        return true;
    }

    boolean belongY( double y ) {
        if ( y > yRange[ 0 ] || y < yRange[ 1 ] )
            return false;

        return true;
    }

    /**
     * get the four monotone arcs of this cycle
     *
     * -------------------------------------->      construction direction
     *  0, top left    <- ( ) -> 1, top right                  |
     *  2, bottom left <- ( ) -> 3, bottom right               |
     *                                                         v
     *
     * @param isXMonotone     true, preprocess the input shape as monotone relative to x-axis;
     *                        false, monotone relative to y-axis.
     * */

    // TODO: 6/21/2022 isXMonotone is necessary to Arc?
    Arc[] getFourMonotoneQuarters( boolean isXMonotone ) {
        Arc[] arcs = new Arc[ 4 ];
        // top left
        arcs[ 0 ] = new Arc( this, center.x, center.x - radius,
                center.y + radius, center.y, extremes[ 0 ], extremes[ 1 ] );
        // top right
        arcs[ 1 ] = new Arc( this, center.x + radius, center.x,
                center.y + radius, center.y, extremes[ isXMonotone ? 1 : 2 ], extremes[ isXMonotone ? 2 : 1 ] );
        // bottom left
        arcs[ 2 ] = new Arc( this, center.x, center.x - radius,
                center.y, center.y - radius, extremes[ isXMonotone ? 0 : 3 ], extremes[ isXMonotone ? 3 : 0 ] );
        // bottom right
        arcs[ 3 ] = new Arc( this, center.x + radius, center.x,
                center.y, center.y - radius, extremes[ 3 ], extremes[ 2 ] );

        return arcs;
    }

    /**
     * intersects with the line?
     * */

    public boolean ifIntersectsLine( Line line ) {
        return MyMath.doubleCompare( line.distance( center ), radius ) <= 0;
    }

    /**
     * move this cycle's center to the origin first,
     * and then compute y
     * */

    private double updateRelativeToOrigin( double coor ) {
        // calculate x * x: r * r - y * y
        // calculate y * y: r * r - x * x
        double intermediate = Math.abs( radius * radius - coor * coor );
        assert MyMath.doubleCompare( intermediate, 0 ) >= 0;
        // radius * radius - x * x. may be extremely small negative number
        return Math.sqrt( Math.abs( intermediate ) );
    }

    /**
     * calculate y with the given x,
     * using this cycle
     * */

    protected double[] updateY( double x ) {
        double y = updateRelativeToOrigin( x - center.x );
        // check number overflow
        assert y <= 0 && -y >= 0 || y >= 0 && -y <= 0;
        return new double[] { y + center.y, -y + center.y };
    }

    protected double[] updateX( double y ) {
        double x = updateRelativeToOrigin( y - center.y );
        // check number overflow
        assert x <= 0 && -x >= 0 || x >= 0 && -x <= 0;
        return new double[] { x + center.x, -x + center.x };
    }

    @Override
    public Vector[] intersect( Intersection s ) {
        if ( s instanceof Segment )
            return GeometricIntersection.segmentCircle( ( Segment ) s, this );
        else if ( s instanceof Line )
            return GeometricIntersection.lineCircle( ( Line ) s, this );

        // TODO: 5/30/2022 other intersections
        return new Vector[] {};
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Circle circle = ( Circle ) o;
        return Double.compare( circle.radius, radius ) == 0 && center.equals( circle.center );
    }

    @Override
    public int hashCode() {
        return Objects.hash( center, radius );
    }

    @Override
    public String toString() {
        return "C" + ID + ": " + center + " | Rad: " + radius + " | equ: " + equation;
    }

    public static
    void main( String[] args ) {
        int ID = 0;
        Circle circle1 = new Circle( Vector.origin, 1 );
        System.out.println( Arrays.toString( circle1.updateY(0.5 ) ) );
        System.out.println( Arrays.toString( circle1.updateY(-0.5 ) ) );
        Vector vector1 = new Vector( 0, 1, ID++ );
        Circle circle2 = new Circle( vector1, 1 );
        System.out.println( Arrays.toString( circle2.updateY(0.5 ) ) ) ;
        System.out.println( Arrays.toString( circle2.updateY(-0.5 ) ) ) ;
    }
}
