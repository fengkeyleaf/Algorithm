package myLibraries.util.geometry;

/*
 * Lines.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 basic operations on 7/21/2021$
 *     $1.1 added drawing methods on 11/28/2021$
 */

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class consists exclusively of static methods that
 * related to Line
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Lines {

    //-------------------------------------------------------
    // drawing part
    //-------------------------------------------------------

    /**
     * draw normalized lines.
     *
     * @param points assume it's been flipped y
     * */

    public static
    void drawLines( Graphics graphics, List<Integer> points, Color color ) {
        if ( points == null ) return;

        assert points.size() % 2 == 0 : points;
        graphics.setColor( color );
        for ( int i = 0; i < points.size(); i += 4 ) {
            graphics.drawLine( points.get( i ), points.get( i + 1 ),
                    points.get( i + 2 ), points.get( i + 3 ) );
        }
    }

    public static
    void drawLines( Graphics graphics, List<List<Integer>> points, List<Color> colors ) {
        assert points.size() == colors.size();

        for ( int i = 0; i < points.size(); i++ ) {
            drawLines( graphics, points.get( i ), colors.get( i ) );
        }
    }

    /**
     * draw a path with normalized points.
     *
     * @param points assume it's been flipped y
     * */

    public static
    void drawPath( Graphics graphics, List<Integer> points, Color color ) {
        assert points.size() % 2 == 0 : points;
        graphics.setColor( color );
        for ( int i = 0; i < points.size() - 2; i += 2 ) {
            graphics.drawLine( points.get( i ), points.get( i + 1 ),
                    points.get( i + 2 ), points.get( i + 3 ) );
        }
    }

    public static
    List<Integer> getDrawingPoints( List<Line> lines,
                                    int originWidth, int originHeight,
                                    int windowWidth, int windowHeight ) {
        final List<Integer> points = new ArrayList<>();
        lines.forEach( l -> {
            int x1 = Vectors.normalize( l.startPoint.x, originWidth, windowWidth );
            int y1 = Vectors.normalize( -l.startPoint.y, originHeight, windowHeight );
            int x2 = Vectors.normalize( l.endPoint.x, originWidth, windowWidth );
            int y2 = Vectors.normalize( -l.endPoint.y, originHeight, windowHeight );
            DrawingProgram.storePoints( points, x1, y1, x2, y2 );
        } );

        return points;
    }

    //-------------------------------------------------------
    // computational part
    //-------------------------------------------------------

    /**
     * @param p1 start point of the vector
     * @param p2 end point of the vector
     */

    public static
    Line getBisector( Vector p1, Vector p2 ) {
        if ( p1.equals( p2 ) ) return null;

        return new Line( p1, p2 ).getVertical( p1.getMid( p2 ) );
    }

    /**
     * if a, b and c are on the same line
     */

    public static
    boolean isOnTheSameLine( Vector a, Vector b, Vector c ) {
        return MyMath.isEqualZero( Triangles.areaTwo( a, b, c ) );
    }

    /**
     * Compare lines by slope.
     * Slope of vertical lines is considered as being infinite large.
     * And have two vertical lines,
     * one of which with larger y-coor of its endpoint is large.
     * Notice: we assume startpoint < endpoint.
     *
     * And allowing vertical lines is attempted to do the comparison in Point Location.
     */

    public static
    int compareBySlope( Line l1, Line l2 ) {
        // vertical lines, compare by endpoint's y-coor.
        if ( l1.isVertical && l2.isVertical ) {
            Vector end1 = l1.startPoint.y >= l1.endPoint.y ? l1.startPoint : l1.endPoint;
            Vector end2 = l2.startPoint.y >= l2.endPoint.y ? l2.startPoint : l2.endPoint;
            return Vectors.sortByY( end1, end2 );
        }
        else if ( l1.isVertical )
            return 1;
        else if ( l2.isVertical )
            return -1;

        // non-vertical lines, compare by dy and dx.
        double res = l1.dy * l2.dx - l1.dx * l2.dy;
        if ( MyMath.isEqualZero( res ) ) return 0;
        else if ( res > 0 ) return 1;

        return -1;
    }

    /**
     * compare by EndPoint
     */

    static
    int compareByEndPoint( Line l1, Line l2 ) {
        return Vectors.sortByX( l1.endPoint, l2.endPoint );
    }

    /**
     * compare by StartPoint
     */

    static
    int compareByStartPoint( Line l1, Line l2 ) {
        return Vectors.sortByX( l1.startPoint, l2.startPoint );
    }

    private static
    List<Line> merge(List<List<Line>> overlappingLines ) {
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
            for ( int j = 1; j < lineSet.size(); j++ ) {
                Line line = lineSet.get( j );

                // not overlapping
                if ( Vectors.sortByX( min, line.endPoint ) >= 0 ) {
                    lines.add( new Line( min, max ) );
                    min = line.startPoint;
                    max = line.endPoint;
                }
                // overlapping
                else {
                    max = Vectors.max( max, line.endPoint, Vectors::sortByX );
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

                if ( Lines.collectLinesOnTheSameLine( line1, line2 ) != 0 ) {
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
        lines.sort( Lines::compareBySlope );
        // collect lines with the same slope;
        List<List<Line>> parallelLines = collectParallelLines( lines );
        // sort each line set by area2 to identify lines on the same line;
        for ( List<Line> paraLines : parallelLines )
            paraLines.sort( Lines::collectLinesOnTheSameLine );
        // collect lines on the same line;
        List<List<Line>> overlappingLines = collectOverlappingLines( parallelLines );
        // sort each line set by left endpoint; (left endpoint <= right endpoint)
        for ( List<Line> overLines : overlappingLines )
            overLines.sort( Lines::compareByStartPoint );
        // greedy to merge overlapping lines,
        // excluding ones only with one common endpoint;
        return merge( overlappingLines );
    }

    static
    int collectLinesOnTheSameLine( Line line1, Line line2 ) {
        // to left test based on line1.
        double res1 = Triangles.areaTwo( line1.endPoint, line1.startPoint, line2.endPoint );
        double res2 = Triangles.areaTwo( line1.endPoint, line1.startPoint, line2.startPoint );

        // to left test based on line2.
        double res3 = Triangles.areaTwo( line2.endPoint, line2.startPoint, line1.endPoint );
        double res4 = Triangles.areaTwo( line2.endPoint, line2.startPoint, line1.startPoint );
        assert MyMath.isSameSign( res1, res2 ) && MyMath.isSameSign( res3, res4 );
        return ( int ) res1;
//        assert ( MyMath.isEqualZero( res1 ) && MyMath.isEqualZero( res2 ) ) == ( MyMath.isEqualZero( res3 ) && MyMath.isEqualZero( res4 ) );
//        return MyMath.isEqualZero( res1 ) && MyMath.isEqualZero( res2 ) ? 0 : 1;
    }

    static
    void testBisector() {
        // Vertical: 0.0 | 0.0y + 1.0x = 0.0
//        System.out.println( Lines.getBisector( new Vector( -1, 0 ), new Vector( 1, 0 ) ) );
        // null<->null | 1.0y + -1.0x = 0.0
//        System.out.println( Lines.getBisector( new Vector( -1, 1 ), new Vector( 1, -1 ) ) );
        // null<->null | eq:1.0y + -2.3333333333333335x = 1.3333333333333333
//        System.out.println( Lines.getBisector( new Vector( -3, 4 ), new Vector( 4, 1 ) ) );
        // null<->null | eq: 1.0y + -0.3333333333333333x = 1.6666666666666665
//        System.out.println( Lines.getBisector( new Vector( -3, 4 ), new Vector( -1, -2 ) ) );
        // null<->null | eq: 1.0y + 1.6666666666666667x = 2.0
//        System.out.println( Lines.getBisector( new Vector( 4, 1 ), new Vector( -1, -2 ) ) );

    }

    public static
    void main( String[] args ) {
        testBisector();
    }
}
