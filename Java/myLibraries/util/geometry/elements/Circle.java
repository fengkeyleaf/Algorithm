package myLibraries.util.geometry.elements;

/*
 * Circle.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.lang.QuadraticTwoUnknown;

import java.util.Arrays;
import java.util.Objects;

/**
 * Data structure of Circle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Circle {
    public final Vector center;
    public final double radius;
    private final double[] xRange = new double[ 2 ];
    private final double[] yRange = new double[ 2 ];

    private final QuadraticTwoUnknown equation;

    /**
     * constructs to create an instance of Circle with center and radius
     * */

    public Circle( Vector center, double radius ) {
        if ( MyMath.doubleCompare( radius, 0 ) < 0 ) {
            System.err.println( "radius cannot be negative!" );
            System.exit( 1 );
        }

        this.center = center;
        this.radius = radius;

        xRange[ 0 ] = center.x - radius;
        xRange[ 1 ] = center.x + radius;
        yRange[ 0 ] = center.y + radius;
        yRange[ 1 ] = center.y - radius;

        // TODO: 1/2/2022 implement
        equation = null;
    }

    /**
     * constructs to create an instance of Circle with equation.
     * x² + y² + Dx + Ey + F = 0, ( D² + E² - 4F > 0 ).
     * ( X + D/2 )²+( Y + E/2 )² = ( D² + E² - 4F ) / 4.
     *
     * reference resource:
     * https://baike.baidu.com/item/%E5%9C%86%E7%9A%84%E6%A0%87%E5%87%86%E6%96%B9%E7%A8%8B/3723940?fr=aladdin
     * */

    public Circle( double d, double e, double f ) {
        equation = new QuadraticTwoUnknown( d, e, f );
        center = new Vector( -d / 2, -e / 2 );

        assert MyMath.isPositive( d * d + e * e - 4 * f );
        radius = Math.sqrt( ( d * d + e * e - 4 * f ) / 4 );
    }

    public Vector getLowest() {
        return new Vector( 0, -1 ).multiply( radius ).add( center );
    }

    /**
     * get the upper left point of the rectangle bounding this circle.
     * Mainly for drawing purpose.
     * @see <a href=https://docs.oracle.com/en/java/javase/16/docs/api/java.desktop/java/awt/Graphics.html#drawOval(int,int,int,int)>drawOval(int x, int y, int width, int height)</a>
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

    public boolean belongX( double x ) {
        if ( x > xRange[ 1 ] || x < xRange[ 0 ] )
            return false;

        return true;
    }

    public boolean belongY( double y ) {
        if ( y > yRange[ 0 ] || y < yRange[ 1 ] )
            return false;

        return true;
    }

    /**
     * get the four monotone arcs of this cycle
     * */

    public Arc[] getFourQuarters() {
        // clockwise
        Vector point1 = new Vector( center.x - radius, center.y, -1 );
        Vector point2 = new Vector( center.x, center.y + radius, -1 );
        Vector point3 = new Vector( center.x + radius, center.y, -1 );
        Vector point4 = new Vector( center.x, center.y - radius, -1 );

        int index = 0;
        Arc[] arcs = new Arc[ 4 ];
        arcs[ index++ ] = new Arc( this, center.x, center.x - radius, center.y + radius, center.y, point1, point2 );
        arcs[ index++ ] = new Arc( this, center.x + radius, center.x, center.y + radius, center.y, point2, point3 );
        arcs[ index++ ] = new Arc( this, center.x, center.x - radius, center.y, center.y - radius, point1, point4 );
        arcs[ index++ ] = new Arc( this, center.x + radius, center.x, center.y, center.y - radius, point4, point3 );

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

    private double updateYRelativeToOrigin( double x ) {
        double intermediate = Math.abs( radius * radius - x * x );
        assert MyMath.doubleCompare( intermediate, 0 ) >= 0;
        // radius * radius - x * x. may be extremely small negative number
        return Math.sqrt( Math.abs( intermediate ) );
    }

    /**
     * calculate y with the given x,
     * using this cycle
     * */

    protected double[] updateY( double x ) {
        double y = updateYRelativeToOrigin( x - center.x );
        return new double[] { y + center.y, -y + center.y };
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
        return "C: " + center + " | Rad: " + radius + " | equ: " + equation;
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
