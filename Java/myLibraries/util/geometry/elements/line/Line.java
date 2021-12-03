package myLibraries.util.geometry.elements.line;

/*
 * Line.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.UpdateCoordinatesShape;
import myLibraries.util.geometry.elements.circle.Circle;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.GeometricIntersection;
import myLibraries.util.geometry.tools.Vectors;

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

    private double A;
    private double B;
    private double C;

    public boolean isVertical;
    private double verticalX;
    public boolean isHorizontal;
    private double horizontalY;

    public double dx;
    public double dy;

    /**
     * Constructs to create an instance of Line
     * */

    public Line( Vector startPoint, Vector endPoint ) {
        ID = IDStatic++;

        this.startPoint = startPoint;
        this.endPoint = endPoint;

        isVertical = isVertical();
        verticalX = isVertical ? startPoint.x : 0;
        isHorizontal = isHorizontal();
        horizontalY = isHorizontal ? startPoint.y : 0;
        assert !( isVertical & isHorizontal ) : startPoint + " " + endPoint;

        this.dx = startPoint.x - endPoint.x;
        this.dy = startPoint.y - endPoint.y;

        getLineStandardFunction();
    }

    // Used for the output of lineCycleIntersect
    public Line( Vector startPoint, Vector endPoint, Circle circle ) {
        ID = IDStatic++;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public static
    Line getLineSortedByX( Vector p1, Vector p2 ) {
        Vector[] ps = new Vector[] { p1, p2 };
        Arrays.sort( ps, Vectors::sortByX );
        return new Line( ps[ 0 ], ps[ 1 ] );
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
        double ratio = Vectors.dot(
                point.subtract( startPoint ), base )
                / base.normWithoutRadical();
        return startPoint.add( base.multiply( ratio ) );
    }

    /**
     * get the linear distance from the point
     * */

    public double distance( Vector point ) {
        Vector vector = getVector();
        double area = Vectors.cross( vector, point.subtract( startPoint ) );
        assert !MyMath.isEqualZero( vector.norm() );
        return Math.abs( area / vector.norm() );
    }

    /**
     * B * y + A * x = C
     * */

    public void getLineStandardFunction() {
        A = startPoint.y - endPoint.y;
        B = endPoint.x - startPoint.x;
        C = B * startPoint.y + A * startPoint.x;
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

    public double updateY( double x ) {
        assert B != 0 : this;
        return ( C - x * A ) / B;
    }

    private double updateX( double y ) {
        assert A != 0 : this;
        return ( C - y * B ) / A;
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
                assert target.x == verticalX;
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
     * updateYAndXByX() in javaScript version
     * */

    @Override
    public void updateYAndX( Vector target, double x ) {
        if ( isVertical ) return;
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

    public boolean outOfRangeX( double x ) {
        double[] xCoordinates = new double[ 2 ];
        xCoordinates[ 0 ] = startPoint.x;
        xCoordinates[ 1 ] = endPoint.x;
        Arrays.sort( xCoordinates );

        return x < xCoordinates[ 0 ] || x > xCoordinates[ 1 ];
    }

    public boolean outOfRangeY( double y ) {
        double[] yCoordinates = new double[ 2 ];
        yCoordinates[ 0 ] = startPoint.y;
        yCoordinates[ 1 ] = endPoint.y;
        Arrays.sort( yCoordinates );

        return y < yCoordinates[ 0 ] || y > yCoordinates[ 1 ];
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

    private boolean isVertical() {
        return MyMath.isEqual( startPoint.x - endPoint.x, 0 );
    }

    private boolean isHorizontal() {
        return MyMath.isEqual( startPoint.y - endPoint.y, 0 );
    }

    @Override
    public String toString() {
        return startPoint + "<->" + endPoint;
    }

    private void testLine1() {
        int index = 0;
        Vector point1 = new Vector( -1, -1, index++ );
        Vector point2 = new Vector( -1, 1, index++ );
        Vector point3 = new Vector( 1, 1, index++ );
        Vector point4 = new Vector( 1, -1, index++ );
        Line line1 = new Line( point1, point3 );
        line1.getLineStandardFunction();
        Line line2 = new Line( point2, point4 );
        line2.getLineStandardFunction();
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
        System.out.println( GeometricIntersection.lineIntersect( line3, line4 ) );
        System.out.println( line3.updateY( x ) );
        System.out.println( line4.updateY( x ) );
        System.out.println( Double.compare( line4.updateY( x ), line3.updateY( x ) ) );
        System.out.println( MyMath.isEqual( line4.updateY( x ), line3.updateY( x ) ));
    }

    public static
    void main( String[] args ) {
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
}
