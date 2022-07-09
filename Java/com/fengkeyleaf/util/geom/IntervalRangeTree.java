package com.fengkeyleaf.util.geom;

/*
 * IntervalRangeTree.java
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
 * Interval tree combined with range tree
 * to have the ability to do orthogonal windowing query with axis-parallel segments.
 *
 * Corollary 10.6
 * Let S be a set of n axis-parallel segments in the plane.
 * The segments intersecting an axis-parallel rectangular query window can be reported
 * in O((logn)^2+k) time with a data structure
 * that uses O(nlogn) storage,
 * where k is the number of reported segments.
 * The structure can be built in O(nlogn) time.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class IntervalRangeTree extends IntervalTree
        implements OrthogonalWindowingQuery.WindowingQuery {

    private static final String title = "Visualized debugger for Interval Range Tree";
    IntervalRangeNode root;

    /**
     * build an interval tree enhanced with range tree
     * to have the ability to do orthogonal windowing query.
     *
     * @param I   axis-parallel interval( segment ) set.
     *            And the line's left endpoint should be less than its right endpoint.
     *            To guarantee that, use {@link Segment} to create a line.
     *            Allow duplicates, which will be removed in the process.
     *            And also allow intersecting intervals.
     *            But prohibit overlapping segments, except for overlapping only one endpoint.
     *            e.g. [(x1,y):(x2,y)] and [(x2,y):(x3,y)] are allowed,
     *            but [(x1,y):(x2,y)] and [(x3,y):(x4,y)] aren't, where x1 <= x3 < x2.
     * @param isX true, build on horizontal segments; false, build on vertical segments.
     * */

    public IntervalRangeTree( List<Line> I, boolean isX ) {
        super( null, isX );

        if ( I == null || I.isEmpty() ) return;

        // preporcess() will remove special intervals and build a bucket tree.
        // but we will ignore it when querying.
        // i.e. ignore vertical intervals when querying vertically,
        // them will be handled correctly when querying horizontally.
        root = build( preprocess( I ) );
        assert check( root );
    }

    // Used to build an Interval layered range tree.
    IntervalRangeTree() {
        super( null, true );
    }

    /**
     * class to combine interval tree and range tree.
     * */

    class IntervalRangeNode extends IntervalNode {
        IntervalLayeredRangeTree L;
        IntervalLayeredRangeTree R;

        IntervalRangeNode( Vector p ) {
            super( p );
        }

        IntervalRangeNode( List<LineNode> P ) {
            super( P.get( P.size() / 2 - 1 ) );
            assert P.size() % 2 == 0;

            List<Vector> L = new ArrayList<>();
            List<Vector> R = new ArrayList<>();
            initL( P, L, R );
            initR( P, L, R );

            this.L = new IntervalLayeredRangeTree( preprocessEndpoint( L ) );
            this.R = new IntervalLayeredRangeTree( preprocessEndpoint( R ) );
        }



        // initialize the left range tree.
        void initL( List<LineNode> P, List<Vector> L, List<Vector> R ) {
            subL = new ArrayList<>();
            int mid = P.size() / 2 - 1;

            // endpoints <= mid,
            // note that there may be overlapping endpoints.
            for ( int i = 0; i <= mid; i++ ) {
                // interval that doesn't contain mid.
                if ( c.compare( P.get( i ).l.endPoint, p ) < 0 ) {
                    subL.add( P.get( i ) );
                    continue;
                }

                // interval that contains mid.
                // And notice that elements in L is presorted by x-coor,
                // so left endpoints is also in the increasing order of x-ccor.
                add( P.get( i ), L, R );
            }
        }

        // add endpoint to L or R according their type,
        // left endpoint or right endpoint.
        void add( LineNode n, List<Vector> L, List<Vector> R ) {
            if ( n.isLeft ) L.add( n );
            else R.add( n );
        }

        // initialize the right range tree.
        void initR( List<LineNode> P, List<Vector> L, List<Vector> R ) {
            subR = new ArrayList<>();
            int mid = P.size() / 2 - 1;

            // mid <= endpoints
            // note that there may be overlapping endpoints.
            for ( int i = mid + 1; i < P.size(); i++ ) {
                // interval that doesn't contain mid.
                if ( c.compare( p, P.get( i ).l.startPoint ) < 0 ) {
                    subR.add( P.get( i ) );
                    continue;
                }

                add( P.get( i ), L, R );
            }

            // at this point, we're done with initializing endpoint set
            // for both L and R ( in the form of range tree ).
            // because the passed-in endpoint set is already sorted in ascending order,
            // we just iterate from left to right and done.
        }
    }

    @Override
    IntervalRangeNode build( List<LineNode> I ) {
        // 1. if I = empty
        // 2. then return an empty leaf
        if ( I.isEmpty() ) return null;

        // 3. else Create a node ν.
        // Compute xMid, the median of the set of interval endpoints, and store xMid with ν.
        // 4. Compute IMid and construct two sorted lists for IMid:
        // a list LLeft(ν) sorted on left endpoint and a list LRight(ν) sorted on right endpoint.
        // Store these two lists at ν.
        IntervalRangeNode v = new IntervalRangeNode( I );
        // 5. lc(ν) <- CONSTRUCTINTERVALTREE(ILeft)
        v.left = build( v.subL );
        // 6. rc(ν) <- CONSTRUCTINTERVALTREE(IRight)
        v.right = build( v.subR );

        // 7. return ν
        return ( IntervalRangeNode ) v.cleanUp();
    }

    /**
     * Orthogonal windowing query
     * with interval tree combining range tree.
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
        QueryVector[] V = getQueryVector( R.get( 0 ), isX );
        // query point, (x, +INF) or (+INF, y)
        query( root, R, V[ 0 ], P );
        // query point, (x, -INF) or (-INF, y)
        query( root, R, V[ 1 ], P );

        // get resulting line set and reset status.
        P.forEach( p -> {
            res.add( p.l );
            p.isNotAdded = true;
        } );

        return check( R, res , title );
    }

    void query( IntervalRangeNode n, List<Vector> R,
                QueryVector q, List<LineNode> res ) {

        if ( n == null ) return;

        // 1. if ν is not a leaf
        // 2. then if qx < xMid(ν)
        if ( q.compare( n.p, c, isX ) < 0 ) {
            // 3. then Walk along the list Lleft(ν),
            // starting at the interval with the leftmost endpoint,
            // reporting all the intervals that contain qx.
            // Stop as soon as an interval does not contain qx.
            report( res, n, R, false );
            // 4. QUERYINTERVALTREE(lc(ν),qx)
            query( ( IntervalRangeNode ) n.left, R, q, res );
            return;
        }

        // 5. else Walk along the list Lright(ν),
        // starting at the interval with the rightmost endpoint,
        // reporting all the intervals that contain qx.
        // Stop as soon as an interval does not contain qx.
        report( res, n, R, true );
        // 6. QUERYINTERVALTREE(rc(ν),qx)
        query( ( IntervalRangeNode ) n.right, R, q, res );
    }

    /**
     * query associated structure ( range tree ).
     *
     * @param res result line set.
     * @param n node at which we need search its associated structures.
     * @param R orthogonal searching area.
     * @param isPosInf query positive or negative infinite.
     * */

    void report( List<LineNode> res, IntervalRangeNode n,
                 List<Vector> R, boolean isPosInf ) {
        String[] RStr = getOrthogonalArea( R, isPosInf );
        // query in layered range tree.
        List<Vector> subRes = isPosInf ? n.R.query( RStr ) : n.L.query( RStr );
        subRes.forEach( v -> res.add( ( ( LineNode ) v ) ) );

        assert checkDuplicate( res );
    }

    static
    boolean checkDuplicate( List<LineNode> res ) {
        // check for duplicate.
        List<Line> I = new ArrayList<>( res.size() );
        res.forEach( p -> I.add( p.l ) );
        assert check( I );

        return true;
    }

    /**
     * Get Orthogonal searching area for associated structure, namely segment tree.
     * We need to convert real vectors into string vectors
     * to be able to query infinite.
     * */

    String[] getOrthogonalArea( List<Vector> R, boolean isPosInf ) {
        if ( isX )
            return getOrthogonalAreaHorizontal( R, isPosInf );

        return getOrthogonalAreaVertical( R, isPosInf );
    }

    static
    String[] getOrthogonalAreaHorizontal( List<Vector> R, boolean isPosInf ) {
        String[] RStr = RangeTree.vectorToString( R );
        if ( isPosInf ) {
            RStr[ 2 ] = RangeTree.POS_INFS[ 0 ];
            return RStr;
        }

        RStr[ 0 ] = RangeTree.NEG_INF;
        RStr[ 2 ] = String.valueOf( R.get( 0 ).x );
        return RStr;
    }

    static
    String[] getOrthogonalAreaVertical( List<Vector> R, boolean isPosInf ) {
        String[] RStr = RangeTree.vectorToString( R );
        if ( isPosInf ) {
            RStr[ 3 ] = RangeTree.POS_INFS[ 0 ];
            return RStr;
        }

        RStr[ 1 ] = RangeTree.NEG_INF;
        RStr[ 3 ] = String.valueOf( R.get( 0 ).y );
        return RStr;
    }
}
