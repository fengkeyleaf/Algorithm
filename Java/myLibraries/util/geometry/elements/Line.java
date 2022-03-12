package myLibraries.util.geometry.elements;

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

import myLibraries.lang.LinearTwoUnknowns;
import myLibraries.lang.MyMath;
import myLibraries.util.geometry.BoundingBox;
import myLibraries.util.geometry.Lines;
import myLibraries.util.geometry.Triangles;
import myLibraries.util.geometry.Vectors;

import java.util.Arrays;

/**
 * Data structure of Line
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Line implements UpdateCoordinatesShape {
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

    // only used for the output of lineCycleIntersect
    public Line( Vector startPoint, Vector endPoint, Circle circle ) {
        ID = IDStatic++;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    protected static
    Vector[] sortedByX( Vector p1, Vector p2 ) {
        Vector[] ps = new Vector[] { p1, p2 };
        Arrays.sort( ps, Vectors::sortByX );
        return ps;
    }

    public double getVerticalX() {
        return verticalX;
    }

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
     * get Vector of Line
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

    private Vector[] sortPointsByX( Vector startPoint, Vector endPoint ) {
        Vector[] points = new Vector[ 2 ];
        points[ 0 ] = startPoint;
        points[ 1 ] = endPoint;
        Arrays.sort( points, Vectors::sortByX );
        return points;
    }

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

    /**
     * @param boundary non-negative
     * */

    protected Vector generatePoint( int quadrant, double boundary ) {
        assert !MyMath.isNegative( boundary );

        switch ( quadrant ) {
            case 1:
                return processRayOrLine( boundary, boundary );
            case 2:
                return processRayOrLine( -boundary, boundary );
            case 3:
                return processRayOrLine( -boundary, -boundary );
            case 4:
                return processRayOrLine( boundary, -boundary );
            default:
                assert false;
        }

        assert false;
        return null;
    }

    protected Vector generatePoint( int quadrant, double minX, double maxX, double minY, double maxY ) {
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

    public Segment getSegment( double minX, double maxX, double minY, double maxY ) {
        Line reverse = new Line( endPoint, startPoint );
        Vector vectorLineLeft = getVector();
        Vector vectorLineRight = reverse.getVector();

        Vector[] points = new Vector[ 2 ];
        points[ 0 ] = generatePoint( MyMath.quadrant( vectorLineLeft ), minX, maxX, minY, maxY );
        points[ 1 ] = generatePoint( MyMath.quadrant( vectorLineRight ), minX, maxX, minY, maxY );
        Arrays.sort( points, Vectors::sortByX );

        return new Segment( points[ 0 ], points[ 1 ] );
    }

    public Segment getSegment( BoundingBox box ) {
        double minX = box.left.origin.x;
        double maxX = box.top.origin.x;
        double minY = box.bottom.origin.y;
        double maxY = box.top.origin.y;

        if ( startPoint != null && endPoint != null )
            return getSegment( minX, maxX, minY, maxY );

        if ( isVertical )
            return new Line( new Vector( verticalX,minY ), new Vector( verticalX, maxY ) ).getSegment( box );

        return new Line( new Vector( minX, updateY( minX ) ), new Vector( maxX, updateY( maxX ) ) ).getSegment( box );
    }

    public double updateY( double x ) {
        assert equation.b != 0 : this;
        return ( equation.c - x * equation.a ) / equation.b;
    }

    private double updateX( double y ) {
        assert equation.a != 0 : this;
        return ( equation.c - y * equation.b ) / equation.a;
    }

    /**
     * update Y And X, for line
     *
     * @param isUpdatingByX     update x and y based which, x or y?
     *                          true -> x; false -> y
     */

    @Override
    public void updateYAndX( Vector target, Vector update, boolean isUpdatingByX ) {
        // vertical line
        if ( isVertical ) {
            // given x == the x of the line?
            if ( MyMath.isEqual( update.x, verticalX ) ) {
                // yes, update Y
//                assert target.x == verticalX : target + " " + verticalX;
                target.y = update.y;
            }
            return;
        }
        // Horizontal line
        else if ( isHorizontal ) {
            // given y == the y of the line?
            if ( MyMath.isEqual( update.y, horizontalY ) ) {
                // yes, update x
                assert target.y == horizontalY;
                target.x = update.x;
            }
            return;
        }

        // neither vertical nor horizontal,
        // update y based on given x
        if ( isUpdatingByX ) target.setXAndY( update.x, updateY( update.x ) );
        else target.setXAndY( updateX( update.y ), update.y );
    }

    /**
     * update the target point's x and y
     * based on the given x, using this line to calculate
     *
     * */

    @Override
    public void updateYAndX( Vector target, double x ) {
        // if this line is vertical, no need to update.
        // only case where we are supposed to update target's x is
        // that given x is on this vertical line,
        // but in this case, x is the same as before
        if ( isVertical ) return;
        // if this line is horizontal
        // only update x, y is the same as before
        else if ( isHorizontal ) {
            target.x = x;
            return;
        }

        target.setXAndY( x, updateY( x ) );
    }

    /**
     * is the give point on this line?
     * */

    public boolean isOnThisLine( Vector vector ) {
        if ( isVertical ) return MyMath.isEqual( vector.x, verticalX );
        else if ( isHorizontal ) return MyMath.isEqual( vector.y, horizontalY );

        return MyMath.isEqual( updateY( vector.x ), vector.y );
    }

    // TODO: 1/6/2022 may put this part of code in class Segment
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
    // intersection of line and line
    //-------------------------------------------------------

    /**
     * get the common endPoint, intersection as well,
     * if two lines on the same line intersect at one of the endpoints
     */

    private Vector getOnlyCommonEndPoint( Line l ) {
        if ( this.startPoint.equalsXAndY( l.endPoint ) ) return this.startPoint;

        return this.endPoint;
    }

    /**
     * if the two lines Overlap But Have Common EndPoint,
     * but note that may only have one common endPoint.
     */

    private boolean isOverlapButHavingCommonEndPoint( Line l ) {
        return Vectors.sortByX( l.startPoint, l.endPoint ) > 0 &&
                this.startPoint.equalsXAndY( l.endPoint ) ||
                this.endPoint.equalsXAndY( l.startPoint );
    }

    /**
     * toLeft test to check if two segments intersect
     */

    private boolean ifSegmentsIntersect( Line s, double res1, double res2 ) {
        // parallel cases:
        // case 1: overlap or on the same line.
        if ( MyMath.isEqualZero( res1 ) &&
                MyMath.isEqualZero( res2 ) )
            return isOverlapButHavingCommonEndPoint( s );
        // case 2: parallel on the right side.
        if ( res1 < 0 && res2 < 0 )
            return false;
        // case 3: parallel on the left side.
        if ( res1 > 0 && res2 > 0 )
            return false;

        // intersecting cases: either intersect at
        // a common point other than endpoints,
        // or at one of the endpoints.
        return true;
    }

    /**
     * segment1 and segment2 intersects?
     */

    private boolean ifSegmentsIntersect( Line s ) {
        if ( s == null ) return false;

        // to left test based on line1.
        double res1 = Triangles.areaTwo( this.endPoint, this.startPoint, s.endPoint );
        double res2 = Triangles.areaTwo( this.endPoint, this.startPoint, s.startPoint );
        // to left test based on line2.
        double res3 = Triangles.areaTwo( s.endPoint, s.startPoint, this.endPoint );
        double res4 = Triangles.areaTwo( s.endPoint, s.startPoint, this.startPoint );

        // have intersection if and only if
        // two endpoints of one line are
        // at the opposite side of the other line.
        boolean finalRes1 = ifSegmentsIntersect( s, res1, res2 );
        boolean finalRes2 = ifSegmentsIntersect( s, res3, res4 );
        return finalRes1 && finalRes2;
    }

    /**
     * get segment intersection point with vector, if exists
     * Only difference from line intersection is that
     * there is always an intersection between two lines
     * as long as they're not parallel.
     * But not ture for segment intersection.
     */

    public Vector segmentIntersect( Line l ) {
        // have intersection between two segments?
        if ( !ifSegmentsIntersect( l ) ) return null;

        // yes, go get it.
        return lineIntersectCommon( l );
    }

    /**
     * this line and l intersects?
     */

    private boolean ifLinesIntersect( Line l ) {
        if ( l == null ||
                ( isVertical && l.isVertical ) ) return false;

        return Lines.compareBySlope( this, l ) != 0;
    }

    /**
     * get line intersection point with vector, if exists.
     * Only difference from segment intersection is that
     * there is always an intersection between two lines
     * as long as they're not parallel.
     * But not ture for segment intersection.
     */

    // TODO: 2/11/2022 extract an abstract method intersect for both segmentIntersect and lineIntersect
    public Vector lineIntersect( Line l ) {
        // lines created from standard format.
        if ( startPoint == null || l.startPoint == null )
            return equation.intersect( l.equation );

        // lines created from points.
        // have intersection between two lines?
        if ( !ifLinesIntersect( l ) )
            return null;

        // yes, go get it.
        return lineIntersectCommon( l );
    }

    /**
     * get line intersection point with vector, if exists
     *
     * Reference resource:
     * @see <a href=https://blog.csdn.net/qq_40998706/article/details/87482435>portal gate</a>
     */

    private Vector lineIntersectCommon( Line line2 ) {
        // yes, but the intersection point is one of the endpoints.
        if ( isOverlapButHavingCommonEndPoint( line2 ) )
            return getOnlyCommonEndPoint( line2 );

        // yes, normal intersection point.
        Vector base = line2.getVector();
        double d1 = Math.abs( base.cross( startPoint.subtract( line2.startPoint ) ) );
        double d2 = Math.abs( base.cross( endPoint.subtract( line2.startPoint ) ) );
        assert !MyMath.isEqualZero( d1 + d2 );
        double t = d1 / ( d1 + d2 );
        Vector intersection = getVector().multiply( t );

        // the following commented-out code is correct as well,
        // but with less computational accuracy because of ( 1 / t ),
        // one additional division compared to the method above.
//        double t = ( d1 + d2 ) / d1;
//        Vector intersection = line1.endPoint.subtract(
//        line1.startPoint ).multiply( 1 / t );

        return startPoint.add( intersection );
    }

    @Override
    public String toString() {
        if ( isVertical )
            return "Vertical( " + startPoint + "<->" + endPoint + " ): " + verticalX + " | eq: " + equation;
        else if ( isHorizontal )
            return "Horizontal( " + startPoint + "<->" + endPoint + "): " + horizontalY + " | eq: " + equation;

        return startPoint + "<->" + endPoint + " | eq: " + equation;
    }

    private static
    void testLine1() {
        int index = 0;
        Vector point1 = new Vector( -1, -1, index++ );
        Vector point2 = new Vector( -1, 1, index++ );
        Vector point3 = new Vector( 1, 1, index++ );
        Vector point4 = new Vector( 1, -1, index++ );
        Line line1 = new Line( point1, point3 );
        Line line2 = new Line( point2, point4 );
        System.out.println( line1.updateY( 1 ) );
        Vector point5 = new Vector( 2, 5, index++ );
        Vector point6 = new Vector( 8, 4, index++ );
        Vector point7 = new Vector( 6, 6, index++ );
        Line line3 = new Line( point5, point6 );
        Line line4 = new Line( point3, point7 );
        double x = 4.5714283;
        x = 4.571428571428571;
//        System.out.println( GeometricIntersection.linesIntersect( line1, line3 ) );
//        System.out.println( GeometricIntersection.linesIntersect( line3, line1 ) );
        System.out.println( line3.segmentIntersect( line4 ) );
        System.out.println( line3.updateY( x ) );
        System.out.println( line4.updateY( x ) );
        System.out.println( Double.compare( line4.updateY( x ), line3.updateY( x ) ) );
        System.out.println( MyMath.isEqual( line4.updateY( x ), line3.updateY( x ) ));
    }

    private static
    void testLine2() {
        int index = 0;
        Vector point1 = new Vector( -6, 6, index++ );
        Vector point2 = new Vector( 6, -6, index++ );
        Line line1 = new Line( point1, point2 );
        System.out.println( line1.updateY( -3.5355339059327378 ) );

        double num = Math.pow( 10, 5 );
        double res = 74 * Math.pow( num, 2 ) - 86 * num + 25;
        System.out.println( res );
        System.out.println( Math.sqrt( res ) );
        System.out.println( Math.ceil( Math.sqrt( res ) ) );
    }

    private static
    void testLine3() {
//        System.out.println( Lines.getBisector( new Vector( -1, 0 ), new Vector( 1 , 0 ) ) ); // 0.0y + 1.0x = 0.0
//        System.out.println( Lines.getBisector( new Vector( 0, 1 ), new Vector( 0 , -1 ) ) ); // 1.0y + 0.0x = 0.0
//        System.out.println( Lines.getBisector( new Vector( 1, 1 ), new Vector( -1 , -1 ) ) ); // 1.0y + 1.0x = 0.0

//        System.out.println( new Line( -13, -11, 15, -11 ).isOnThisSegment( new Vector( -5.285714285714285, -11.000000000000007 ) ) );
//        System.out.println( new Line( -13, -11, 15, -11 ).isOnThisLine( new Vector( -5.285714285714285, -11.000000000000007 ) ) );

        System.out.println( new Line( -3, -1, 3, 2 ).isOnThisSegment( new Vector( -1, 0 ) ) );
    }

    public static
    void main( String[] args ) {
        testLine3();
    }
}
