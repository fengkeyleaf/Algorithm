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
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.elements.cycle.Cycle;
import myLibraries.util.geometry.tools.GeometricIntersection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data structure of Line
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Line {
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
        this.startPoint = startPoint;
        this.endPoint = endPoint;

        isVertical = isVertical();
        verticalX = isVertical ? startPoint.x : 0;
        isHorizontal = isHorizontal();
        horizontalY = isHorizontal ? startPoint.y : 0;
        assert !( isVertical & isHorizontal );

        this.dx = startPoint.x - endPoint.x;
        this.dy = startPoint.y - endPoint.y;

        getLineStandardFunction();
    }

    // Used for the output of lineCycleIntersect
    public Line( Vector startPoint, Vector endPoint, Cycle cycle ) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    /**
     * compare by slope
     */

    public static
    int compareBySlope( Line l1, Line l2 ) {
        double res = l1.dy * l2.dx - l1.dx * l2.dy;
        if ( MyMath.equalZero( res ) ) return 0;
        else if ( res > 0 ) return 1;

        return -1;
    }

    /**
     * compare by EndPoint
     */

    public static
    int compareByEndPoint( Line l1, Line l2 ) {
        return Vector.sortByX( l1.endPoint, l2.endPoint );
    }

    /**
     * compare by StartPoint
     */

    public static
    int compareByStartPoint( Line l1, Line l2 ) {
        return Vector.sortByX( l1.startPoint, l2.startPoint );
    }

    private static
    List<Line> merge( List<List<Line>> overlappingLines ) {
        List<Line> lines = new ArrayList<>();
        for ( int i = 0; i < overlappingLines.size(); i++ ) {
            List<Line> lineSet = overlappingLines.get( i );
            if ( lineSet.isEmpty() ) continue;

            // find the min startPoint,
            // and the max endPoint for overlapping lines.
            // note that lines that
            // have only one common endpoint
            // will be merged
            Vector min = lineSet.get( 0 ).startPoint;
            Vector max = lineSet.get( 0 ).endPoint;
            for ( int j = 0; j < lineSet.size() - 1; j++ ) {
                Line line1 = lineSet.get( j );
                Line line2 = lineSet.get( j + 1 );

                // not overlapping
                if ( Vector.sortByX( line1.endPoint, line2.startPoint ) <= 0 ) {
                    lines.add( new Line( min, max ) );
                    min = line2.startPoint;
                    max = line2.endPoint;
                }
                // overlapping
                else {
                    max = Vector.max( max, line2.endPoint, Vector::sortByX );
                }
            }

            lines.add( new Line( min, max ) );
        }

        return lines;
    }

    private static
    List<List<Line>> collectParallelLines( List<Line> lines ) {
        List<List<Line>> parallelLines = new ArrayList<>();
        if ( lines.isEmpty() ) return parallelLines;
        parallelLines.add( new ArrayList<>()) ;
        parallelLines.get( 0 ).add( lines.get( 0 ) );

        for ( int i = 0; i < lines.size() - 1; i++ ) {
            Line line1 = lines.get( i );
            Line line2 = lines.get( i + 1 );

            if ( compareBySlope(line1, line2) != 0 ) {
                parallelLines.add( new ArrayList<>() );
            }
            // don't miss the last one
            parallelLines.get( parallelLines.size() - 1 ).add( line2 );
        }

        return parallelLines;
    }

    private static
    List<List<Line>> collectOverlappingLines( List<List<Line>> parallelLines ) {
        List<List<Line>> overlappingLines = new ArrayList<>();
        for ( int i = 0; i < parallelLines.size(); i++ ) {
            List<Line> lines = parallelLines.get( i );
            if ( lines.isEmpty() ) continue;
            overlappingLines.add( new ArrayList<>() ) ;
            overlappingLines.get( overlappingLines.size() - 1 ).add( lines.get( 0 ) );

            for ( int j = 0; j < lines.size() - 1; j++ ) {
                Line line1 = lines.get( j );
                Line line2 = lines.get( j + 1 );

                if ( GeometricIntersection.collectLinesOnTheSameLine(line1, line2) != 0 ) {
                    overlappingLines.add( new ArrayList<>() );
                }
                // don't miss the last one
                overlappingLines.get( overlappingLines.size() - 1 ).add( line2 );
            }
        }

        return overlappingLines;
    }

    /**
     * merge overlapping lines into one line,
     * except for those with only one common endpoint
     * */

    public static
    List<Line> mergeOverlappingLines( List<Line> lines ) {
        // sort by slope;
        lines.sort( Line::compareBySlope );
        // collect lines with the same slope;
        List<List<Line>> parallelLines = collectParallelLines( lines );
        // sort each line set by area2 to identify lines on the same line;
        for ( List<Line> paraLines : parallelLines )
            paraLines.sort( GeometricIntersection::collectLinesOnTheSameLine );
        // collect lines on the same line;
        List<List<Line>> overlappingLines = collectOverlappingLines( parallelLines );
        // sort each line set by left endpoint; (left endpoint <= right endpoint)
        for ( List<Line> overLines : overlappingLines )
            overLines.sort( Line::compareByStartPoint );
        // greedy to merge overlapping lines,
        // excluding ones only with one common endpoint;
        return merge( overlappingLines );
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
        double ratio = Vector.dot(
                point.subtract( startPoint ), base )
                / base.normWithoutRadical();
        return startPoint.add( base.multiply( ratio ) );
    }

    /**
     * get the linear distance from the point
     * */

    public double distance( Vector point ) {
        Vector vector = getVector();
        double area = Vector.cross( vector, point.subtract( startPoint ) );
        assert !MyMath.equalZero( vector.norm() );
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
        Arrays.sort( points, Vector::sortByX );
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

    /**
     * update Y And X, for line
     */

    public void updateYAndX( Vector target, Vector update ) {
        // vertical line
        if ( isVertical ) {
            // given x == the x of the line?
            if ( MyMath.equalFloats( update.x, verticalX ) ) {
                // yes, update Y
                assert target.x == verticalX;
                target.y = update.y;
            }
            return;
        }
        // Horizontal line
        else if ( isHorizontal ) {
            // given y == the y of the line?
            if ( MyMath.equalFloats( update.y, horizontalY ) ) {
                // yes, update x
                assert target.y == horizontalY;
                target.x = update.x;
            }
            return;
        }

        // neither vertical nor horizontal,
        // update y based on given x
        target.setXAndY( update.x, updateY( update.x ) );
    }

    /**
     * does the give point on this line?
     * */

    public boolean isOnThisLine( Vector vector ) {
        if ( isVertical ) return MyMath.equalFloats( vector.x, verticalX );
        else if ( isHorizontal ) return MyMath.equalFloats( vector.y, horizontalY );

        return MyMath.equalFloats( updateY( vector.x ), vector.y );
    }

    /**
     * the point is out of the range of x and y of this segment?
     *  but of a line, this method is useless
     * */

    private boolean outOfRange( Vector vector ) {
        double[] xCoordinates = new double[ 2 ];
        xCoordinates[ 0 ] = startPoint.x;
        xCoordinates[ 1 ] = endPoint.x;
        Arrays.sort( xCoordinates );

        double[] yCoordinates = new double[ 2 ];
        yCoordinates[ 0 ] = startPoint.y;
        yCoordinates[ 1 ] = endPoint.y;
        Arrays.sort( yCoordinates );

        return vector.x < xCoordinates[ 0 ] || vector.x > xCoordinates[ 1 ] ||
                vector.y < yCoordinates[ 0 ] || vector.y > yCoordinates[ 1 ];
    }

    /**
     * does the give point on this Segment?
     * */

    public boolean isOnThisSegment( Vector vector ) {
        if ( outOfRange( vector ) ) return false;

        return isOnThisLine( vector );
    }

    private boolean isVertical() {
        return MyMath.equalFloats( startPoint.x - endPoint.x, 0 );
    }

    private boolean isHorizontal() {
        return MyMath.equalFloats( startPoint.y - endPoint.y, 0 );
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
        System.out.println( MyMath.equalFloats( line4.updateY( x ), line3.updateY( x ) ));
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
