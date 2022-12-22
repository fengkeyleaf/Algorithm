package com.fengkeyleaf.util.geom;

/*
 * SegmentRangeTree.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/23/2022$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Segment tree combined with range tree
 * to have the ability to do orthogonal windowing query
 * with arbitrary oriented segments.
 *
 * Corollary 10.14
 * Let S be a set of n segments in the plane with disjoint interiors.
 * The segments intersecting an axis-parallel rectangular query window can be reported
 * in O((logn)^2+k) time with a data structure that uses O(nlogn) storage,
 * where k is the number of reported segments.
 * The structure can be built in O(nlogn) time
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class SegmentRangeTree extends SegmentTree
        implements OrthogonalWindowingQuery.WindowingQuery {

    private static final String title = "Visualized debugger for Segment Range Tree";

    /**
     * build a basic SegmentTree tree with the ability
     * to do stabbing query with arbitrary oriented segments.
     *
     * @param I   arbitrary oriented segment set.
     *            ( axis-parallel intervals will be handled by interval tree instead.
     *            And they are allowed to intersect with each other. )
     *            And the line's left endpoint should be less than its right endpoint.
     *            To guarantee that, use {@link Segment} to create a line.
     *            Allowed duplicates, which will be removed in the process.
     *            But prohibit intersecting segments, except for overlapping endpoint.
     */

    public SegmentRangeTree( List<Line> I, boolean isX ) {
        super( I, isX );
    }

    @Override
    void insertSegment( List<Line> I ) {
        super.insertSegment( I );
        insertSegment( ( SegmentRangeNode ) root );
    }

    /**
     * build associated structure ( range tree supporting 1D range query ) with intervals.
     * */

    void insertSegment( SegmentRangeNode n ) {
        if ( n == null ) return;

        if ( n.P != null ) {
            // may have duplicate intersection points.
            // i.g. overlapping endpoint or endpoint lying on a segment.
            List<Vector> P = new WindowingQueryTree( !isX ).preprocessEndpoint( n.P );
            // build the range tree to do 1D orthogonal range search.
            n.T = new SlabRangeTree( P, !isX );
            // free up space.
            n.P = null;
        }

        insertSegment( ( SegmentRangeNode ) n.left );
        insertSegment( ( SegmentRangeNode ) n.right );
    }

    /**
     * Segment Range Node with range tree as associated structure.
     * */

    class SegmentRangeNode extends SegmentNode {
        SlabRangeTree T;

        SegmentRangeNode( List<Interval> I ) {
            super( I );
        }

        SegmentRangeNode( List<Interval> I, SegmentNode l, SegmentNode r ) {
            super( I, l, r );
        }

        @Override
        void add( LineNode p ) {
            if ( P == null ) P = new ArrayList<>();

            // get the intersection point between the segment and
            // the line passing through one end of the interval presenting this node.
            // And we will ues intersection point as the information to sort segments.
            double x = 0, y = 0;
            if ( isX ) {
                // sort lines by y-coor.
                x = i.l.p.x;
                y = p.l.updateY( x );
            }
            else {
                // sort lines by x-coor.
                y = i.l.p.y;
                x = p.l.updateX( y );
            }

            LineNode n = new LineNode( new Vector( x, y ), p.l );
            // LineNodes with the same line all have the same twin node containing the line.
            // in this way, avoid repeatedly reporting the segment.
            // and we call p as "The Leader" to inform
            // if we should add the line of it or not.
            n.twin = p;
            P.add( n );
        }
    }

    /***
     * a subclass of range tree to support 1D range query
     * for orthogonal windowing query.
     * The main purpose of this class to update the intersection point
     * when checking to see if a segment intersecting the query line.
     */

    class SlabRangeTree extends IntervalLayeredRangeTree {

       SlabRangeTree( List<Vector> P, boolean isX ) {
           super( P, isX );
       }

       @Override
       boolean isOutOfRangeLeft( RangeNode v, QueryVector[] R ) {
           update( v, R );
           return super.isOutOfRangeLeft( v, R );
       }

       @Override
       boolean isOutOfRangeRight( RangeNode v, QueryVector[] R ) {
           update( v, R );
           return super.isOutOfRangeRight( v, R );
       }

       @Override
       void report( RangeNode n, QueryVector[] R, List<Vector> res ) {
           update( n, R );
           super.report( n, R, res );
       }

        @Override
        void reportDuplicate( LineNode n, List<Vector> res ) {
            n.L.forEach( v -> {
                // skip added line.
                // and n hasn't been added before,
                // note that we're dealing with intervals, not single point.
                if ( v.isNotAdded() &&
                        // ignore intervals with endpoints inside R, if necessary.
                        // i.e. only report crossing intervals?
                        isReportCrossing( v, ROrigin ) ) {

                    res.add( v );
                    // twin is the leader node,
                    // mark it as reported.
                    v.twin.isNotAdded = false;
                }
            } );
        }

        /**
         * update the intersection point based on the querying line.
         * */

        void update( RangeNode v, QueryVector[] R ) {
           LineNode n = ( LineNode ) v.p;

           // query vertically, use y-coor to update x-coor.
           if ( isX )
               n.setXAndY( n.l.updateX( R[ 0 ].p.y ), R[ 0 ].p.y );
           // query horizontally, use x-coor to update y-coor.
           else
               n.setXAndY( R[ 0 ].p.x, n.l.updateY( R[ 0 ].p.x ) );
       }

       // report subtree, overrides it to avoid repeatedly reporting the interval.
        @Override
        void report( RangeNode n, List<Vector> res ) {
            if ( n.isLeaf() ) {
                reportDuplicate( ( LineNode ) n.p, res );
                return;
            }

            report( n.left, res );
            report( n.right, res );
        }
    }

    /**
     * @param I presorted by x-coor or y-coor.
     *          And no special intervals.
     *          i.g. no vertical segments when querying vertically.
     * */

    @Override
    SegmentRangeNode build( List<Interval> I ) {
        // all input intervals are special ones.
        // they will be handled by interval tree.
        if ( I.isEmpty() ) return null;
        // real base case.
        else if ( I.size() == 1 ) return new SegmentRangeNode( I );

        // mid = ( r - l ) / 2 + l;
        int mid = ( I.size() - 1 ) / 2;
        List<Interval> L = I.subList( 0, mid + 1 );
        List<Interval> R = I.subList( mid + 1, I.size() );

        return new SegmentRangeNode( I, build( L ), build( R ) );
    }

    /**
     * Orthogonal windowing query
     * with interval tree combining segment tree.
     *
     * @param R orthogonal searching area, [x:x'] x [y:y'].
     *          It is not degenerated to a line or a point.
     *          [ ( x1, y1 ), ( x2, y2 ) ] = [ bottomLeft, topRight ].
     * */

    @Override
    public List<Line> query( List<Vector> R ) {
        List<Line> res = new ArrayList<>();
        if ( root == null ) return check( R, res, title );

        ROrigin = RangeTree.getQueryR( R );
        if ( RangeTree.isInvalidR( ROrigin, c, isX ) )
            throw new IllegalArgumentException( "Illegal orthogonal searching areas" );

        // store LineNode instead of Line,
        // to avoid to add a line twice.
        List<LineNode> P = new ArrayList<>();
        // auto wire result endpont set for checking process.
        assert ( resP = P ) != null;

        // With composite number to construct the tree,
        // querying the vertical line q.x is equivalent to
        // querying point (x, +INF) and point (x, -INF).
        // Vice versa for horizontal query line.
        QueryVector[] V = IntervalTree.getQueryVector( R.get( 0 ), isX );
        // query point, (x, +INF) or (+INF, y)
        query( root, V[ 0 ], P );
        // query poi (x, -INF) or (-INF, y)
        query( root, V[ 1 ], P );

        // get resulting line set and reset status.
        P.forEach( p -> {
            res.add( p.l );
            // Be careful, twin is the leader.
            p.twin.isNotAdded = true;
        } );

        return check( R, res, title );
    }

    @Override
    void report( SegmentNode n, List<LineNode> res ) {
        if ( ( ( SegmentRangeNode ) n ).T == null ) return;

        // 1D range query in range tree.
        List<Vector> subRes = ( ( SegmentRangeNode ) n ).T.query1D( ROrigin );
        subRes.forEach( p -> res.add( ( ( LineNode ) p ) ) );

        assert IntervalRangeTree.checkDuplicate( res );
    }
}
