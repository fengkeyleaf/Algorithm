package myLibraries.util.geometry.tools;

/*
 * Lines.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.line.Line;
import myLibraries.util.geometry.elements.point.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * This class consists exclusively of static methods that
 * related to Line
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Lines {

    /**
     * compare by slope
     */

    public static
    int compareBySlope(Line l1, Line l2 ) {
        double res = l1.dy * l2.dx - l1.dx * l2.dy;
        if ( MyMath.isEqualZero( res ) ) return 0;
        else if ( res > 0 ) return 1;

        return -1;
    }

    /**
     * compare by EndPoint
     */

    public static
    int compareByEndPoint( Line l1, Line l2 ) {
        return Vectors.sortByX( l1.endPoint, l2.endPoint );
    }

    /**
     * compare by StartPoint
     */

    public static
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
            for ( int j = 0; j < lineSet.size() - 1; j++ ) {
                Line line1 = lineSet.get( j );
                Line line2 = lineSet.get( j + 1 );

                // not overlapping
                if ( Vectors.sortByX( line1.endPoint, line2.startPoint ) <= 0 ) {
                    lines.add( new Line( min, max ) );
                    min = line2.startPoint;
                    max = line2.endPoint;
                }
                // overlapping
                else {
                    max = Vectors.max( max, line2.endPoint, Vectors::sortByX );
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
        lines.sort( Lines::compareBySlope );
        // collect lines with the same slope;
        List<List<Line>> parallelLines = collectParallelLines( lines );
        // sort each line set by area2 to identify lines on the same line;
        for ( List<Line> paraLines : parallelLines )
            paraLines.sort( GeometricIntersection::collectLinesOnTheSameLine );
        // collect lines on the same line;
        List<List<Line>> overlappingLines = collectOverlappingLines( parallelLines );
        // sort each line set by left endpoint; (left endpoint <= right endpoint)
        for ( List<Line> overLines : overlappingLines )
            overLines.sort( Lines::compareByStartPoint );
        // greedy to merge overlapping lines,
        // excluding ones only with one common endpoint;
        return merge( overlappingLines );
    }
}
