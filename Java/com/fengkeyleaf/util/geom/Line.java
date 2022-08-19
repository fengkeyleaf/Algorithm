package com.fengkeyleaf.util.geom;

/*
 * Line.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.lang.LinearTwoUnknowns;
import com.fengkeyleaf.lang.MyMath;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Data structure of Line
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Line
        implements Intersection {
    private static int IDStatic = 0;
    public final int ID;
    public final Vector startPoint;
    public final Vector endPoint;

    // B * y + A * x = C
    public LinearTwoUnknowns equation;

    public boolean isVertical;
    protected double verticalX;
    public boolean isHorizontal;
    protected double horizontalY;

    // k = dy / dx ( dx != 0 )
    public double dx;
    public double dy;
    public double interceptX;

    /**
     * Constructs to create an instance of Line
     *
     * left endPoint( startPoint ) <-> right endPoint( endPoint ) in Geometric Intersection
     * */

    public Line( Vector startPoint, Vector endPoint, int ID ) {
        assert startPoint != null : endPoint;
        assert endPoint != null : startPoint;

        this.ID = ID;

        assert !startPoint.equals( endPoint ) : startPoint;
        this.startPoint = startPoint;
        this.endPoint = endPoint;

        isVertical = MyMath.isEqual( startPoint.x - endPoint.x, 0 );
        verticalX = isVertical ? startPoint.x : 0;
        isHorizontal = MyMath.isEqual( startPoint.y - endPoint.y, 0 );
        horizontalY = isHorizontal ? startPoint.y : 0;
        assert !( isVertical & isHorizontal ) : startPoint + " " + endPoint;

        this.dx = startPoint.x - endPoint.x;
        this.dy = startPoint.y - endPoint.y;

        equation = new LinearTwoUnknowns( startPoint, endPoint );

        // no X intercept if this line is vertical
        interceptX = equation.c / equation.b;
    }

    public Line( Vector startPoint, Vector endPoint ) {
        this( startPoint, endPoint, IDStatic++ );
    }

    public Line( double x1, double y1, double x2, double y2 ) {
        this( new Vector( x1, y1 ), new Vector( x2, y2 ) );
    }

    public Line( double a, double b, double c ) {
        this( new LinearTwoUnknowns( a, b, c ) );
    }

    protected Line( Vector[] vectors ) {
        this( vectors[ 0 ], vectors[ 1 ] );
    }

    // also dx and dy not supported in this constructor.
    // but can get slope of this line by the equation.
    public Line( LinearTwoUnknowns equation ) {
        ID = IDStatic++;

        startPoint = endPoint = null;
        // strict mode, meaning that this line is vertical or horizontal
        // if and only if a exactly equals 1 and b equals 0,
        // or b exactly 1 and a equals 0.
        // no MyMath.doubleCompare allowed.
        isVertical = equation.a == 1 && equation.b == 0;
        verticalX = isVertical ? equation.c : 0;
        isHorizontal = equation.b == 1 && equation.a == 0;
        horizontalY = isHorizontal ? equation.c : 0;
        assert !( isVertical & isHorizontal ) : startPoint + " " + endPoint;

        this.equation = equation;

        // no X intercept if this line is vertical
        interceptX = equation.c / equation.b;
    }

    protected static
    Vector[] presort( Vector p1, Vector p2, Comparator<Vector> c ) {
        Vector[] ps = new Vector[] { p1, p2 };
        Arrays.sort( ps, c );
        return ps;
    }

    public double getVerticalX() {
        return verticalX;
    }

    public double getHorizontalY() {
        return horizontalY;
    }

    /**
     * get a line perpendicular to this line,
     * passing the given point, {@code mid}.
     * */

    public Line getVertical( Vector mid ) {
        if ( isHorizontal )
            return new Line( 1, 0, mid.x );
        else if ( isVertical )
            return new Line( 0, 1, mid.y );

        double slope = -dx / dy;
        double interceptY = mid.y - slope * mid.x;
        return new Line( -slope, 1, interceptY );
    }

    /**
     * get Vector of Line, endPoint - startPoint.
     * */

    public Vector getVector() {
        return endPoint.subtract( startPoint );
    }

    /**
     * get projecting point of the point
     * */

    public Vector project( Vector point ) {
        Vector base = getVector();
        double ratio = point.subtract( startPoint ).dot( base ) / base.lengthSq();
        return startPoint.add( base.multiply( ratio ) );
    }

    /**
     * get the linear distance from the point
     * */

    public double distance( Vector point ) {
        Vector vector = getVector();
        double area = vector.cross( point.subtract( startPoint ) );
        assert !MyMath.isEqualZero( vector.length() );
        return Math.abs( area / vector.length() );
    }

    /**
     * this line is parallel to the given line, {@code l}?
     * */

    public boolean isParallel( Line l ) {
        // one of lines without slope.
        return isVertical && l.isVertical ||
                // both have slope.
                Lines.compareBySlope( this, l ) == 0;
    }

    //-------------------------------------------------------
    // line to segment.
    //-------------------------------------------------------

    /**
     * regard ray and line as segment
     * */

    public Vector processRayOrLine( double x, double y ) {
        Vector vector = new Vector( 0, 0, -1 );
        if ( isVertical )
            vector.setXAndY( verticalX, y );
        else if ( isHorizontal )
            vector.setXAndY( x, horizontalY );
        else
            vector.setXAndY( x, updateY( x ) );

        return vector;
    }

    protected Vector generatePoint( int quadrant, double minX,
                                    double maxX, double minY, double maxY ) {

        switch ( quadrant ) {
            case 1:
                return processRayOrLine( maxX, maxY );
            case 2:
                return processRayOrLine( minX, maxY );
            case 3:
                return processRayOrLine( minX, minY );
            case 4:
                return processRayOrLine( maxX, minY );
            default:
                assert false;
        }

        assert false;
        return null;
    }

    Segment getSegment( double minX, double maxX,
                        double minY, double maxY,
                        Comparator<Vector> c ) {

        Line reverse = new Line( endPoint, startPoint );
        Vector vectorLineLeft = getVector();
        Vector vectorLineRight = reverse.getVector();

        Vector[] points = new Vector[ 2 ];
        points[ 0 ] = generatePoint( MyMath.quadrant( vectorLineLeft ), minX, maxX, minY, maxY );
        points[ 1 ] = generatePoint( MyMath.quadrant( vectorLineRight ), minX, maxX, minY, maxY );
        Arrays.sort( points, c );

        return new Segment( points[ 0 ], points[ 1 ], c );
    }

    public Segment getSegment( BoundingBox box ) {
        return getSegment( box, Vectors::sortByX );
    }

    // TODO: 6/19/2022 not support the line created from parameter.
    public Segment getSegment( BoundingBox box,
                               Comparator<Vector> c ) {

        double minX = box.left.origin.x;
        double maxX = box.top.origin.x;
        double minY = box.bottom.origin.y;
        double maxY = box.top.origin.y;

        if ( startPoint != null && endPoint != null )
            return getSegment( minX, maxX, minY, maxY, c );

        if ( isVertical )
            return new Line( new Vector( verticalX, minY ), new Vector( verticalX, maxY ) ).getSegment( box, c );

        return new Line( new Vector( minX, updateY( minX ) ), new Vector( maxX, updateY( maxX ) ) ).getSegment( box, c );
    }

    //-------------------------------------------------------
    // update
    //-------------------------------------------------------

    public double updateY( double x ) {
        assert equation.b != 0 : this;
        assert !isVertical;
        return ( equation.c - x * equation.a ) / equation.b;
    }

    public double updateX( double y ) {
        assert equation.a != 0 : this;
        assert !isHorizontal;
        return ( equation.c - y * equation.b ) / equation.a;
    }

    //-------------------------------------------------------
    // on this line or this segment.
    //-------------------------------------------------------

    /**
     * is the give point on this line?
     * */

    public boolean isOnThisLine( Vector vector ) {
        if ( isVertical ) return MyMath.isEqual( vector.x, verticalX );
        else if ( isHorizontal ) return MyMath.isEqual( vector.y, horizontalY );

        return MyMath.isEqual( updateY( vector.x ), vector.y );
    }

    public boolean outOfRangeX( double x ) {
        double[] xCoordinates = new double[ 2 ];
        xCoordinates[ 0 ] = startPoint.x;
        xCoordinates[ 1 ] = endPoint.x;
        Arrays.sort( xCoordinates );

        return MyMath.doubleCompare( x, xCoordinates[ 0 ] ) < 0 ||
                MyMath.doubleCompare( x, xCoordinates[ 1 ] ) > 0;
    }

    public boolean outOfRangeY( double y ) {
        double[] yCoordinates = new double[ 2 ];
        yCoordinates[ 0 ] = startPoint.y;
        yCoordinates[ 1 ] = endPoint.y;
        Arrays.sort( yCoordinates );

        return MyMath.doubleCompare( y, yCoordinates[ 0 ] ) < 0 ||
                MyMath.doubleCompare( y, yCoordinates[ 1 ] ) > 0;
    }

    /**
     * the point is out of the range of x and y of this segment?
     *  but of a line, this method is useless
     * */

    public boolean outOfRange( Vector vector ) {
        return outOfRangeX( vector.x ) || outOfRangeY( vector.y );
    }

    /**
     * does the give point on this Segment?
     * */

    public boolean isOnThisSegment( Vector vector ) {
        if ( outOfRange( vector ) ) return false;

        return isOnThisLine( vector );
    }

    //-------------------------------------------------------
    // intersection.
    //-------------------------------------------------------

    @Override
    public Vector[] intersect( Intersection s ) {
        if ( s instanceof Segment ) {
            Vector i = GeometricIntersection.lineSegment( this, ( Segment ) s );
            return i == null ? new Vector[] {} : new Vector[] { i };
        }
        else if ( s instanceof Line ){
            Vector i = GeometricIntersection.lines( this, ( Line ) s );
            return i == null ? new Vector[] {} : new Vector[] { i };
        }

        assert s instanceof Circle;
        return GeometricIntersection.lineCircle( this, ( Circle ) s );
    }

    //-------------------------------------------------------
    // Duality
    //-------------------------------------------------------
    // Reference resource: http://www.cs.uu.nl/geobook/

    /**
     * Transform this line in the primary plane into the dual plane, line to point.
     *
     * l  :  y = mx + b
     * l* := ( m, −b )
     *
     * @return a point representing this line in the dual plane.
     * @throws IllegalArgumentException - Try getting a vertical line in the primary plane.
     */

    public Vector toDuality() {
        if ( isVertical )
            throw new IllegalArgumentException( "No vertical line transformation " +
                    "in the primary plane to the dual plane." );

        // note that the formula for line:
        // B * y + A * x = C,
        // so, m = -A / B, b = C / B
        assert MyMath.isEqualZero( equation.b );
        return new Vector( -equation.a / equation.b, -equation.c / equation.b );
    }

    //-------------------------------------------------------
    // equals & toString.
    //-------------------------------------------------------

    String toString( boolean showEqu ) {
        if ( isVertical )
            return "Vertical( " + startPoint + "<->" + endPoint + " ): " + verticalX + ( showEqu ? " | eq: " +  equation : "" );
        else if ( isHorizontal )
            return "Horizontal( " + startPoint + "<->" + endPoint + "): " + horizontalY + ( showEqu ? " | eq: " +  equation : "" );

        return startPoint + "<->" + endPoint + ( showEqu ? " | eq: " +  equation : "" );
    }

    @Override
    public String toString() {
        return toString( false );
    }
}
