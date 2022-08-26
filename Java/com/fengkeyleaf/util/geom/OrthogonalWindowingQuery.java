package com.fengkeyleaf.util.geom;

/*
 * OrthogonalWindowingQuery.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/11/2022$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Class to do Orthogonal Windowing Query.
 * It provides three methods to do so.
 * 1) interval tree combining layered range tree ( fractional cascading ).
 * 2) interval tree combining priority search tree.
 * 3) segment tree combining range tree.
 *
 * And by default, it provides segment tree with the ability
 * to query with arbitrary oriented segments, but no intersecting ones.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class OrthogonalWindowingQuery {
    private static final String title = "Visualized debugger for Orthogonal windowing query";
    // querying structure type
    enum Type {
        INTERVAL_TREE,
        PRIORITY_SEARCH_TREE,
        SEGMENT_TREE
    }
    public static final Type INTERVAL_TREE = Type.INTERVAL_TREE;
    public static final Type PRIORITY_SEARCH_TREE = Type.PRIORITY_SEARCH_TREE;
    public static final Type SEGMENT_TREE = Type.SEGMENT_TREE;
    Type t;
    // layered Range Tree dealing with intervals with endpoints lying inside R.
    IntervalRangeTree.IntervalLayeredRangeTree layeredRangeTree;
    // two windowing query trees,
    // interval tree ( combining Range tree or Priority search tree ),
    // or segment tree ( combining Range tree ),
    // to deal with intervals crossing R entirely.
    // left vertical query segment.
    WindowingQuery left;
    // bottom horizontal query segment.
    WindowingQuery bottom;
    // input interval set for checking.
    List<Line> I;

    interface StabbingQuery {
        List<Line> query( Vector q );
    }

    interface WindowingQuery {
        List<Line> query( List<Vector> R );
    }

    /**
     * constructs to provide a segment tree.
     *
     * @param I    arbitrary oriented segment set.
     *             And the line's left endpoint should be less than its right endpoint.
     *             To guarantee that, use {@link Segment} to create a line.
     *             Allowed duplicates, which will be removed in the process.
     *             But prohibit intersecting segments, except for overlapping endpoint,
     *             or endpoint lying on a segment.
     * */

    public OrthogonalWindowingQuery( List<Line> I ) {
        this( I, SEGMENT_TREE );
    }

    /**
     * constructs to provide an orthogonal windowing query tree,
     * based on the querying structure type provided.
     *
     * @param I    arbitrary oriented segment set.
     *             And the line's left endpoint should be less than its right endpoint.
     *             To guarantee that, use {@link Segment} to create a line.
     *             Allowed duplicates, which will be removed in the process.
     *             But prohibit intersecting segments, except for overlapping endpoint,
     *             or endpoint lying on a segment.
     * @param t    querying structure type, either interval tree, priority search tree, or segment tree.
     * */

    public OrthogonalWindowingQuery( List<Line> I, Type t ) {
        // auto-wiring interval set for checking process.
        assert I == null || ( this.I = Lines.removeDuplicates( I, Vectors::sortByX ) ) != null;

        layeredRangeTree = new WindowingQueryTree().getIntervalLayeredRangeTree( I );
        switch ( this.t = t ) {
            case INTERVAL_TREE -> {
                IntervalRangeTree tree = new IntervalRangeTree( I, true );
                tree.isOnlyReportingCrossing = true;
                left = tree;

                tree = new IntervalRangeTree( I, false );
                tree.isOnlyReportingCrossing = true;
                bottom = tree;
            }
            case PRIORITY_SEARCH_TREE -> {
                PriorityIntervalTree tree = new PriorityIntervalTree( I, true );
                tree.isOnlyReportingCrossing = true;
                left = tree;

                tree =  new PriorityIntervalTree( I, false );
                tree.isOnlyReportingCrossing = true;
                bottom = tree;
            }
            case SEGMENT_TREE -> {
                SegmentRangeTree tree = new SegmentRangeTree( I, true );
                tree.isOnlyReportingCrossing = true;
                left = tree;

                tree = new SegmentRangeTree( I, false );
                tree.isOnlyReportingCrossing = true;
                bottom = tree;
            }
            default -> { assert false; }
        }
    }

    /**
     * search to find which intervals lying in the orthogonal region, R.
     *
     * @param R orthogonal searching area, [x:x'] x [y:y'].
     *          It is not degenerated to a line or a point.
     *          [ ( x1, y1 ), ( x2, y2 ) ] = [ bottomLeft, topRight ].
     * */

    public List<Line> query( List<Vector> R ) {
        if ( layeredRangeTree == null || left == null ||
                bottom == null || R == null )
            return null;

        // List<Line> res = new ArrayList<>();
        // query horizontal intervals crossing R.
        List<Line> res = left.query( R );
        // query vertical intervals crossing R.
        res.addAll( bottom.query( R ) );
        // query intervals with at least one endpoint lying inside R.
        layeredRangeTree.query( R ).forEach( v -> res.add( ( ( LineNode ) v ).l ) );

        return check( R, res );
    }

    //-------------------------------------------------------
    // Check integrity of OrthogonalWindowingQuery.
    //-------------------------------------------------------

    // check the querying result
    List<Line> check( List<Vector> R, List<Line> res ) {
        assert WindowingQueryTree.visualization( I, R, res, title );
        assert WindowingQueryTree.check( res );

        return res;
    }
}
