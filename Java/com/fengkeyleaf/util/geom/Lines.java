package com.fengkeyleaf.util.geom;

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

import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.lang.MyMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class consists exclusively of static methods that
 * related to Line
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
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
        if ( graphics == null || points == null || colors == null ) return;
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
     * remove duplicate lines in the line set, {@code I}.
     * */

    // Time complexity: O(nlogn)
    public static
    List<Line> removeDuplicates( List<Line> I, Comparator<Vector> c ) {
        I.sort( ( l1, l2 ) -> c.compare( l1.startPoint, l2.startPoint ) );
        List<Line> res = new ArrayList<>();

        for ( int i = 0; i < I.size() - 1; i++ ) {
            Line l1 = I.get( i );
            Line l2 = I.get( i + 1 );

            // duplicates are adjacent and they must have the same two endpoints.
            if ( !( l1.startPoint.equals( l2.startPoint ) &&
                    l1.endPoint.equals( l2.endPoint ) ) )
                res.add( l1 );
        }
        if ( !I.isEmpty() ) res.add( I.get( I.size() - 1 ) );

        return res;
    }

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
     * And have two vertical lines, it will return 0, meaning both lines are parallel.
     * Notice: we assume startpoint < endpoint.
     *
     * @param l1 first line to be compared with slope, vertical line allowed.
     * @param l2 second line to be compared with slope, vertical line allowed.
     * @return zero, both lines have the same slope, meaning they're parallel;
     *         positive, l1 has large slope compared to l2.
     *         negative, l2 has large slope compared to l1.
     */

    public static
    int compareBySlope( Line l1, Line l2 ) {
        // both vertical lines, assume they have the same slope.
        if ( l1.isVertical && l2.isVertical ) return 0;
        // one of the lines is vertical,
        // it has the largest slope.
        else if ( l1.isVertical ) return 1;
        else if ( l2.isVertical ) return -1;

        // non-vertical lines, compare by dy and dx.
        return compareBySlope( l1.dx, l1.dy, l2.dx, l2.dy );
    }

    static
    int compareBySlope( double dx1, double dy1,
                        double dx2, double dy2 ) {

        double res = dy1 * dx2 - dx1 * dy2;
        if ( MyMath.isEqualZero( res ) ) return 0;
        else if ( res > 0 ) return 1;

        return -1;
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
            for ( int j = 1; j < lineSet.size(); j++ ) {
                Line line = lineSet.get( j );

                // not overlapping
                if ( Vectors.sortByX( min, line.endPoint ) >= 0 ) {
                    lines.add( new Segment( min, max ) );
                    min = line.startPoint;
                    max = line.endPoint;
                }
                // overlapping
                else {
                    max = Vectors.max( max, line.endPoint, Vectors::sortByX );
                }
            }

            lines.add( new Segment( min, max ) );
        }

        return lines;
    }

    private static
    List<List<Line>> collectParallelSegments( List<Line> lines ) {
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
    List<List<Line>> collectOverlappingSegments( List<List<Line>> parallelLines ) {
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
     * merge overlapping segments into one line,
     * except for those with only one common endpoint
     *
     * @deprecated it will merge segments with endpoints that coincide,
     *             and useless for geometric intersection
     *             since we will not report overlapping segments.
     * */

    @Deprecated
    public static
    List<Line> mergeOverlappingSegments( List<Line> lines ) {
        // sort by slope;
        lines.sort( Lines::compareBySlope );
        // collect lines with the same slope;
        List<List<Line>> parallelLines = collectParallelSegments( lines );
        // sort each line set by area2 to identify lines on the same line;
        for ( List<Line> paraLines : parallelLines )
            paraLines.sort( Lines::collectLinesOnTheSameLine );
        // collect lines on the same line;
        List<List<Line>> overlappingLines = collectOverlappingSegments( parallelLines );
        // sort each line set by left endpoint; (left endpoint <= right endpoint)
        for ( List<Line> overLines : overlappingLines )
            overLines.sort( ( l1, l2 ) -> Vectors.sortByX( l1.startPoint, l2.startPoint ) );
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
}
