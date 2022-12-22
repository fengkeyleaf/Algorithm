package CGTsinghua.PA_4.problem_2;

/*
 * Main.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/8/2022$
 */

import CGTsinghua.MainCG;
import com.fengkeyleaf.io.ProcessingFile;
import com.fengkeyleaf.io.ReadFromStdOrFile;
import com.fengkeyleaf.util.geom.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * <a href="https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1652">CG2017 PA4-2 Orthogonal Windowing Query</a><br>
 *
 * Three ways to solve it:
 * 1) interval tree.
 * 2) priority search tree.
 * 3) segment tree.
 * Time complexity: O((logn)^2+k),
 * where n is # of points and k is # of reported ones.
 *
 * By the way, segment tree is overqualified for this problem,
 * since input intervals are all axis-parallel.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class Main extends MainCG
        implements ProcessingFile {

    private List<Line> L;
    private List<List<Vector>> boxes;
    private boolean isX;
    enum Type {
        INTERVAL_TREE,
        INTERVAL_RANGE_TREE,
        INTERVAL_RANGE_TREE_ORTHOGONAL,
        PRIORITY_SEARCH_TREE,
        SEGMENT_TREE,
        SEGMENT_ORTHOGONAL
    }

    Main( int fileName, boolean isX ) {
        this.isX = isX;
        ReadFromStdOrFile.readFromFile( getFilePathCG( 4, 2, fileName ), this );
    }

    Main( int fileName ) {
        this( fileName, true );
    }

    @Override
    public void processingFile( Scanner sc ) {
        boolean initializeLength = true;
        int index = 0;
        int numberOfLines = 0;
        String[] numbers = null;
        String content = null;

        while ( sc.hasNext() ) {
            content = sc.nextLine();
            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            if ( initializeLength &&
                    Pattern.matches( ReadFromStdOrFile.PATTERN_LENGTH, content ) ) {
                numbers = content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS );
                numberOfLines = Integer.parseInt( numbers[ 0 ] );
                L = new ArrayList<>( numberOfLines );
                boxes = new ArrayList<>( Integer.parseInt( numbers[ 1 ] ) );
                initializeLength = false;
                continue;
            }

            numbers = content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS );
            assert numbers.length == 4 : Arrays.toString( numbers );

            int x1 = Integer.parseInt( numbers[ 0 ] );
            int y1 = Integer.parseInt( numbers[ 1 ] );
            Vector v1 = new Vector( x1, y1 );
            int x2 = Integer.parseInt( numbers[ 2 ] );
            int y2 = Integer.parseInt( numbers[ 3 ] );
            Vector v2 = new Vector( x2, y2 );

            if ( index++ < numberOfLines ) {
                // make sure startPoint < endpoint.
                L.add( new Segment( v1, v2, isX ? Vectors::sortByX : Vectors::sortByY ) );
                continue;
            }

            List<Vector> l = new ArrayList<>( 2 );
            l.add( v1 );
            l.add( v2 );
            boxes.add( l );
        }
    }

    void doTheAlgorithm( Type t ) {
        switch ( t ) {
            case INTERVAL_TREE -> {
                IntervalTree tree = new IntervalTree( L, isX );

                boxes.forEach( b -> {
                    List<Line> res = null;
                    res = tree.query( b.get( 0 ) );
                    if ( res != null ) res.forEach( System.out::println );

                    System.out.println( "----------------->" );
                    res = tree.query( b.get( 1 ) );
                    if ( res != null ) res.forEach( System.out::println );

                    System.out.println();
                } );
            }
            case INTERVAL_RANGE_TREE -> {
                IntervalRangeTree tree = new IntervalRangeTree( L, isX );
//                tree.isOnlyReportingCrossing = true;

                boxes.forEach( b -> {
                    List<Line> res = tree.query( b );

                    if ( res != null )
                        res.forEach( System.out::println );
                } );
            }
            case INTERVAL_RANGE_TREE_ORTHOGONAL -> {
                OrthogonalWindowingQuery searcher = new OrthogonalWindowingQuery( L, OrthogonalWindowingQuery.INTERVAL_TREE );

                boxes.forEach( b -> {
                    List<Line> res = searcher.query( b );

                    if ( res != null )
                        System.out.println( res.size() );
                } );
            }
            case PRIORITY_SEARCH_TREE -> {
                OrthogonalWindowingQuery searcher = new OrthogonalWindowingQuery( L, OrthogonalWindowingQuery.PRIORITY_SEARCH_TREE );

                boxes.forEach( b -> {
                    List<Line> res = searcher.query( b );

                    if ( res != null )
                        System.out.println( res.size() );
                } );
            }
            case SEGMENT_TREE -> {
                SegmentTree tree = new SegmentTree( L, isX );

                boxes.forEach( b -> {
                    tree.query( b.get( 0 ) );
                    tree.query( b.get( 1 ) );
                } );
            }
            case SEGMENT_ORTHOGONAL -> {
                OrthogonalWindowingQuery searcher = new OrthogonalWindowingQuery( L, OrthogonalWindowingQuery.SEGMENT_TREE );

                boxes.forEach( b -> {
                    List<Line> res = searcher.query( b );

                    if ( res != null )
                        System.out.println( res.size() );
                } );
            }
            default -> { assert false; }
        }
    }

    static
    void testInterval() {
//        new Main( 1, true ).doTheAlgorithm( Type.INTERVAL_TREE );
//        new Main( 1, false ).doTheAlgorithm( Type.INTERVAL_TREE );
//        new Main( 2, true ).doTheAlgorithm( Type.INTERVAL_TREE );
//        new Main( 2, false ).doTheAlgorithm( Type.INTERVAL_TREE );
//        new Main( 3, true ).doTheAlgorithm( Type.INTERVAL_TREE );
//        new Main( 3, false ).doTheAlgorithm( Type.INTERVAL_TREE );
//        new Main( 4, true ).doTheAlgorithm( Type.INTERVAL_TREE ); // degenerate case
//        new Main( 4, false ).doTheAlgorithm( Type.INTERVAL_TREE ); // degenerate case
//        new Main( 5, true ).doTheAlgorithm( Type.INTERVAL_TREE ); // degenerate case
//        new Main( 5, false ).doTheAlgorithm( Type.INTERVAL_TREE ); // degenerate case
//        new Main( 6, true ).doTheAlgorithm( Type.INTERVAL_TREE ); // provided case
//        new Main( 6, false ).doTheAlgorithm( Type.INTERVAL_TREE ); // provided case
        new Main( 7, true ).doTheAlgorithm( Type.INTERVAL_TREE ); // real circuits.
//        new Main( 7, false ).doTheAlgorithm( Type.INTERVAL_TREE ); // real circuits
//        new Main( 11, true ).doTheAlgorithm( Type.INTERVAL_TREE ); // regular quad
//        new Main( 11, false ).doTheAlgorithm( Type.INTERVAL_TREE ); // regular quad
//        new Main( 12, true ).doTheAlgorithm( Type.INTERVAL_TREE ); // regular quad
//        new Main( 12, false ).doTheAlgorithm( Type.INTERVAL_TREE ); // regular quad
//        new Main( 13, true ).doTheAlgorithm( Type.INTERVAL_TREE ); // all verticals on the same line, and so as horizontals
//        new Main( 13, false ).doTheAlgorithm( Type.INTERVAL_TREE ); // all verticals on the same line, and so as horizontals
    }

    static
    void testIntervalRange() {
//        new Main( 1, true ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE );
//        new Main( 1, false ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE );
//        new Main( 2, true ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE );
//        new Main( 2, false ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE );
//        new Main( 3, true ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE );
//        new Main( 3, false ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE );
        new Main( 4, true ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // degenerate case
        new Main( 4, false ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // degenerate case
//        new Main( 5, true ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // degenerate case
//        new Main( 5, false ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // degenerate case
//        new Main( 6, true ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // provided case
//        new Main( 6, false ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // provided case
//        new Main( 7, true ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // real circuits.
//        new Main( 7, false ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // real circuits
//        new Main( 11, true ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // regular quad
//        new Main( 11, false ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // regular quad
//        new Main( 12, true ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // regular quad
//        new Main( 12, false ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // regular quad
//        new Main( 13, true ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // all verticals on the same line, and so as horizontals
//        new Main( 13, false ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE ); // all verticals on the same line, and so as horizontals
    }

    static
    void testIntervalRangeOrthogonal() {
//        new Main( 1 ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE_ORTHOGONAL );
//        new Main( 2 ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE_ORTHOGONAL );
//        new Main( 3 ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE_ORTHOGONAL );
//        new Main( 4 ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE_ORTHOGONAL ); // degenerate case
//        new Main( 5 ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE_ORTHOGONAL ); // degenerate case
//        new Main( 6 ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE_ORTHOGONAL ); // provided case
        new Main( 7 ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE_ORTHOGONAL ); // real circuits.
//        new Main( 11 ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE_ORTHOGONAL ); // regular quad
//        new Main( 12 ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE_ORTHOGONAL ); // regular quad
//        new Main( 13 ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE_ORTHOGONAL ); // all verticals on the same line, and so as horizontals, with intersecting intervals.
//        new Main( 14 ).doTheAlgorithm( Type.INTERVAL_RANGE_TREE_ORTHOGONAL ); // all verticals on the same line, and so as horizontals, without intersecting intervals.
    }

    static
    void testPriority() {
//        new Main( 1 ).doTheAlgorithm( Type.PRIORITY_SEARCH_TREE );
//        new Main( 2 ).doTheAlgorithm( Type.PRIORITY_SEARCH_TREE );
//        new Main( 3 ).doTheAlgorithm( Type.PRIORITY_SEARCH_TREE );
//        new Main( 4 ).doTheAlgorithm( Type.PRIORITY_SEARCH_TREE ); // degenerate case
        new Main( 5 ).doTheAlgorithm( Type.PRIORITY_SEARCH_TREE ); // degenerate case
//        new Main( 6 ).doTheAlgorithm( Type.PRIORITY_SEARCH_TREE ); // provided case
        new Main( 7 ).doTheAlgorithm( Type.PRIORITY_SEARCH_TREE ); // real circuits.
//        new Main( 11 ).doTheAlgorithm( Type.PRIORITY_SEARCH_TREE ); // regular quad
//        new Main( 12 ).doTheAlgorithm( Type.PRIORITY_SEARCH_TREE ); // regular quad
//        new Main( 13 ).doTheAlgorithm( Type.PRIORITY_SEARCH_TREE ); // all verticals on the same line, and so as horizontals, with intersecting intervals.
//        new Main( 14 ).doTheAlgorithm( Type.PRIORITY_SEARCH_TREE ); // all verticals on the same line, and so as horizontals, without intersecting intervals.
    }

    static
    void testSegment() {
//        new Main( 1, true ).doTheAlgorithm( Type.SEGMENT_TREE );
//        new Main( 1, false ).doTheAlgorithm( Type.SEGMENT_TREE );
//        new Main( 2, true ).doTheAlgorithm( Type.SEGMENT_TREE );
//        new Main( 2, false ).doTheAlgorithm( Type.SEGMENT_TREE ); //
//        new Main( 3, true ).doTheAlgorithm( Type.SEGMENT_TREE );
//        new Main( 3, false ).doTheAlgorithm( Type.SEGMENT_TREE ); //
//        new Main( 4, true ).doTheAlgorithm( Type.SEGMENT_TREE ); // degenerate case
//        new Main( 4, false ).doTheAlgorithm( Type.SEGMENT_TREE ); // degenerate case
        new Main( 5, true ).doTheAlgorithm( Type.SEGMENT_TREE ); // degenerate case
        new Main( 5, false ).doTheAlgorithm( Type.SEGMENT_TREE ); // degenerate case
        new Main( 6, true ).doTheAlgorithm( Type.SEGMENT_TREE ); // provided case
//        new Main( 6, false ).doTheAlgorithm( Type.SEGMENT_TREE ); // provided case
//        new Main( 7, true ).doTheAlgorithm( Type.SEGMENT_TREE ); // real circuits.
//        new Main( 7, false ).doTheAlgorithm( Type.SEGMENT_TREE ); // real circuits.
//        new Main( 8, true ).doTheAlgorithm( Type.SEGMENT_TREE ); // arbitrary oriented segments
//        new Main( 8, false ).doTheAlgorithm( Type.SEGMENT_TREE ); // arbitrary oriented segments
//        new Main( 9, true ).doTheAlgorithm( Type.SEGMENT_TREE ); //
//        new Main( 9, false ).doTheAlgorithm( Type.SEGMENT_TREE ); //
        new Main( 10, true ).doTheAlgorithm( Type.SEGMENT_TREE ); // regular octagon
//        new Main( 10, false ).doTheAlgorithm( Type.SEGMENT_TREE ); // regular octagon
//        new Main( 11, true ).doTheAlgorithm( Type.SEGMENT_TREE ); // regular quad
//        new Main( 11, false ).doTheAlgorithm( Type.SEGMENT_TREE ); // regular quad
//        new Main( 12, true ).doTheAlgorithm( Type.SEGMENT_TREE ); // regular quad
//        new Main( 12, false ).doTheAlgorithm( Type.SEGMENT_TREE ); // regular quad
        // all verticals on the same line, and so as horizontals, with intersecting intervals.
//        new Main( 13, true ).doTheAlgorithm( Type.SEGMENT_TREE );
//        new Main( 13, false ).doTheAlgorithm( Type.SEGMENT_TREE );
        // all verticals on the same line, and so as horizontals, without intersecting intervals.
//        new Main( 14, true ).doTheAlgorithm( Type.SEGMENT_TREE );
//        new Main( 14, false ).doTheAlgorithm( Type.SEGMENT_TREE );
    }

    static
    void testSegmentOrthogonal() {
//        new Main( 1 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL );
//        new Main( 2 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL );
//        new Main( 3 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL );
//        new Main( 4 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL ); // degenerate case
//        new Main( 5 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL ); // degenerate ca/se
//        new Main( 6 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL ); // provided case
//        new Main( 7 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL ); // real circuits.
//        new Main( 8 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL ); // arbitrary oriented segments
        new Main( 9 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL ); // arbitrary oriented segments
//        new Main( 10 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL ); // regular octagon
//        new Main( 11 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL ); // regular quad
//        new Main( 12 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL ); // regular quad
//        new Main( 14 ).doTheAlgorithm( Type.SEGMENT_ORTHOGONAL ); // all verticals on the same line, and so as horizontals, without intersecting intervals.
    }

    // TODO: 8/29/2022 Shear transformation to determine a crossing segment.
    // TODO: 8/29/2022 Segments by sortByX when querying vertically, sortyByY when querying horizontally.
    public static
    void main( String[] args ) {
//        testInterval();
//        testIntervalRange();
//        testIntervalRangeOrthogonal();
//        testPriority();

//        testSegment();
        testSegmentOrthogonal();
    }
}
