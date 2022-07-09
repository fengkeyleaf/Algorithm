package com.fengkeyleaf.util.geom;

/*
 * IntervalTree.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/7/2022$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of basic interval tree, only support 2d for now.
 * Time complexity: O( logn + k ),
 * where n is # of points and k is # of reported ones.
 * ( also with the aid of bucket tree to deal with some axis-parallel intervals )
 *
 * Note that this interval tree don't combine other data structures,
 * like range tree, priority queue.
 *
 * Theorem 10.4
 * An interval tree for a set I of n intervals uses O(n) storage and
 * can be built in O(nlogn) time.
 * Using the interval tree we can report all intervals
 * that contain a query point in O(logn+k) time,
 * where k is the number of reported intervals.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class IntervalTree extends WindowingQueryTree
        implements OrthogonalWindowingQuery.StabbingQuery {

    private static final String title = "Visualized debugger for Interval Tree";
    IntervalNode root;
    // bucket tree to deal with vertical or horizontal segments separately.
    final BucketTree<Double, Line> T = new BucketTree<>();

    /**
     * build a basic interval tree with the ability
     * to do stabbing query with axis-parallel segments.
     *
     * @param I     axis-parallel segment set.
     *              And the line's left endpoint should be less than its right endpoint.
     *              To guarantee that, use {@link Segment} to create a line.
     *              Allowed duplicates, which will be removed in the process.
     *              But prohibit overlapping segments, except for overlapping only one endpoint.
     *              e.g. [(x1,y):(x2,y)] and [(x2,y):(x3,y)] are allowed,
     *              but [(x1,y):(x2,y)] and [(x3,y):(x4,y)] aren't, where x1 <= x3 < x2.
     * @param isX   true, query vertically; false, query horizontally.
     * */

    public IntervalTree( List<Line> I, boolean isX ) {
        super( isX );

        if ( I == null || I.isEmpty() ) return;

        root = build( preprocess( I ) );
        assert check( root );
    }

    /**
     * remove duplicates, get point set for line endpoints.
     * */

    protected List<LineNode> preprocess( List<Line> I ) {
        // remove duplicate lines and also auto-wire this.I for checking.
        I = Lines.removeDuplicates( I, c );
        assert ( this.I = I ) != null;

        // separate special interval
        I = separate( I );

        // generate endpoints.
        List<LineNode> L = new ArrayList<>( I.size() * 2 );
        I.forEach( i -> {
            LineNode n1 = new LineNode( i.startPoint, true, i );
            LineNode n2 = new LineNode( i.endPoint, false, i );
            // mark two endpoints from the same line.
            n1.setTwin( n2 );
            L.add( n1 );
            L.add( n2 );

        } );
        // presorting by x-coor or y-coor.
        L.sort( ( l1, l2 ) -> c.compare( l1.p, l2.p ) );

        return L;
    }

    /**
     * separate special intervals from others.
     * i.e. handle verticals separately when querying vertically.
     * */

    List<Line> separate( List<Line> I ) {
        List<Line> lines = new ArrayList<>();

        I.forEach( i -> {
            if ( isX ? i.isVertical : i.isHorizontal )
                T.add( isX ? i.startPoint.x : i.startPoint.y, i );
            else lines.add( i );
        } );

        return lines;
    }

    /**
     * interval tree node.
     * */

    class IntervalNode {
        final Vector p;
        IntervalNode left;
        IntervalNode right;
        // left list storing segments sorted by their left endpoints.
        // i-th's endpoint <= (i+1)-th's endpoint.
        private List<LineNode> L;
        // right list storing segments sorted by their right endpoints.
        // i-th's endpoint >= (i+1)-th's endpoint.
        private List<LineNode> R;
        // segment lists for constructing children.
        // They'll be freed up after the construction.
        List<LineNode> subL;
        List<LineNode> subR;

        // this constructor is for the subclasses.
        IntervalNode( Vector p ) {
            this.p = p;
        }

        IntervalNode( List<LineNode> L ) {
            assert L.size() % 2 == 0;
            int mid = L.size() / 2 - 1;
            p = L.get( mid ).p;

            // initialize the two lists.
            // O( n + n_mid * log( n_mid ) )
            initL( L, mid );
            initR( L, mid );
        }

        /**
         * @param L presorting by x-coor or y-coor in increasing order.
         * */

        // initialize the left list.
        private void initL( List<LineNode> L, int mid ) {
            this.L = new ArrayList<>();
            subL = new ArrayList<>();

            // endpoints <= mid,
            // note that there may be overlapping endpoints.
            for ( int i = 0; i <= mid; i++ ) {
                // left interval that doesn't contain mid.
                if ( c.compare( L.get( i ).l.endPoint, p ) < 0 ) {
                    subL.add( L.get( i ) );
                    continue;
                }

                add( L.get( i ) );
            }
        }

        void add( LineNode n ) {
            // interval that contains mid.
            // avoid re-add segments with an overlapping endpoint.
            if ( n.isNotAdded() ) {
                // And notice that elements in L is presorted by x-coor,
                // so right endpoints is also in the increasing order of x-ccor.
                L.add( n );
                n.isNotAdded = false;
            }
        }

        /**
         * @param L presorting by x-coor or y-coor in increasing order.
         * */

        // initialize the right list.
        private void initR( List<LineNode> L, int mid ) {
            subR = new ArrayList<>();

            // mid <= endpoints
            // note that there may be overlapping endpoints.
            for ( int i = mid + 1; i < L.size(); i++ ) {
                // right interval that doesn't contain mid.
                if ( c.compare( p, L.get( i ).l.startPoint ) < 0 ) {
                    subR.add( L.get( i ) );
                    continue;
                }

                add( L.get( i ) );
            }

            // init R with L and sort by endpoint.
            R = new ArrayList<>( this.L );
            R.sort( ( p1, p2 ) -> c.compare( p2.l.endPoint, p1.l.endPoint ) );
            // reset status.
            R.forEach( v -> v.isNotAdded = true );
        }

        // free up space for the two sub lists.
        IntervalNode cleanUp() {
            subL = null;
            subR = null;
            return this;
        }

        // -----------> checking process.

        void check() {
            if ( L != null ) checkL();
            if ( R != null ) checkR();
        }

        void checkL() {
            for ( int i = 0; i < L.size() - 1; i++ ) {
                assert c.compare( L.get( i ).l.startPoint, L.get( i + 1 ).l.startPoint ) <= 0 : L.get( i ) + " | " + L.get( i + 1 ) + " | " + p;
            }
            L.forEach( p -> {
                assert p.l.startPoint == this.p || p.l.endPoint == this.p || check( p.l, this.p ) : p + " | " + this.p;
            } );
        }

        void checkR() {
            for ( int i = 0; i < R.size() - 1; i++ ) {
                assert c.compare( R.get( i ).l.endPoint, R.get( i + 1 ).l.endPoint ) >= 0 : R.get( i ) + " | " + R.get( i + 1 ) + " | " + p;
            }
            R.forEach( p -> {
                assert p.l.startPoint == this.p || p.l.endPoint == this.p || check( p.l, this.p );
            } );
        }

        boolean check( Line l, Vector p ) {
            return c.compare( l.startPoint, p ) <= 0 && c.compare( p, l.endPoint ) <= 0;
        }
    }

    // Finding the median of a set of points takes linear time.
    // Actually, it is better to compute the median
    // by presorting the set of points, as in Chapter 5.
    // It is easy to maintain these presorted sets through the recursive calls.

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm CONSTRUCTINTERVALTREE(I)
    // Input. A set I of intervals on the real line.
    // Output. The root of an interval tree for I.
    IntervalNode build( List<LineNode> I ) {
        // 1. if I = empty
        // 2. then return an empty leaf
        if ( I.isEmpty() ) return null;

        // 3. else Create a node ν.
        // Compute xMid, the median of the set of interval endpoints, and store xMid with ν.
        // 4. Compute IMid and construct two sorted lists for IMid:
        // a list LLeft(ν) sorted on left endpoint and a list LRight(ν) sorted on right endpoint.
        // Store these two lists at ν.
        IntervalNode v = new IntervalNode( I );
        // 5. lc(ν) <- CONSTRUCTINTERVALTREE(ILeft)
        v.left = build( v.subL );
        // 6. rc(ν) <- CONSTRUCTINTERVALTREE(IRight)
        v.right = build( v.subR );

        // 7. return ν
        return v.cleanUp();
    }

    /**
     * stabbing query with the point, {@code q}.
     *
     * @param q query point.
     * */

    @Override
    public List<Line> query( Vector q ) {
        List<Line> res = new ArrayList<>();
        // query the bucket tree to include special intervals,
        // in O( logn + k ) time.
        query( q, res );

        if ( root == null ) return check( q, res, title );

        // store LineNode instead of Line,
        // to avoid to add a line twice.
        List<LineNode> P = new ArrayList<>();

        // With composite number to construct the tree,
        // querying the vertical line q.x is equivalent to
        // querying point (x, +INF) and point (x, -INF).
        // Vice versa for horizontal query line.
        QueryVector[] V = getQueryVector( q, isX );
        // query point, (x, +INF) or (+INF, y)
        query( root, V[ 0 ], P );
        // query point, (x, -INF) or (-INF, y)
        query( root, V[ 1 ], P );

        // get resulting line set and reset status.
        // To do this, need store result lines as LineNode.
        P.forEach( p -> {
            res.add( p.l );
            p.isNotAdded = true;
        } );

        return check( q, res, title );
    }

    /**
     * get query ranges:
     * [ (x, +INF) x (x, -INF) ]
     * [ (+INF, y) x (-INF, y) ]
     * */

    static
    QueryVector[] getQueryVector( Vector q, boolean isX ) {
        QueryVector[] V = new QueryVector[ 2 ];
        if ( isX ) {
            V[ 0 ] = new QueryVector( null, q.x, true, null, true ); // (x, +INF)
            V[ 1 ] = new QueryVector( null, q.x, true, null, false ); // (x, -INF)
            return V;
        }

        V[ 0 ] = new QueryVector( null, null, true, q.y, true ); // (+INF, y)
        V[ 1 ] = new QueryVector( null, null, false, q.y, true ); // (-INF, y)
        return V;
    }

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm QUERYINTERVALTREE(ν,qx)
    // Input. The root ν of an interval tree and a query point qx.
    // Output. All intervals that contain qx.
    void query( IntervalNode n, QueryVector q, List<LineNode> res ) {
        if ( n == null ) return;

        // 1. if ν is not a leaf
        // 2. then if qx < xMid(ν)
        if ( q.compare( n.p, c, isX ) < 0 ) {
            // 3. then Walk along the list Lleft(ν),
            // starting at the interval with the leftmost endpoint,
            // reporting all the intervals that contain qx.
            // Stop as soon as an interval does not contain qx.
            report( n.L, q, res );
            // 4. QUERYINTERVALTREE(lc(ν),qx)
            query( n.left, q, res );
            return;
        }

        // 5. else Walk along the list Lright(ν),
        // starting at the interval with the rightmost endpoint,
        // reporting all the intervals that contain qx.
        // Stop as soon as an interval does not contain qx.
        report( n.R, q, res );
        // 6. QUERYINTERVALTREE(rc(ν),qx)
        query( n.right, q, res );
    }

    /**
     * Does the line, {@code l}, belonging to the line set, {@code L},
     * contain the vertical query line, representing as a point, {@code qx}?
     * Note that no need to consider degenerate case,
     * because this is a kind of brute force with only one query point.
     *
     * @param P line set.
     * @param q vertical query line.
     * @param res reported line set.
     * */

    void report( List<LineNode> P, QueryVector q, List<LineNode> res ) {
        P.forEach( p -> {
            if ( p.isNotAdded() && ( isX && containsX( p.l, q.x ) ||
                    !isX && containsY( p.l, q.y ) ) ) {
                res.add( p );
                p.isNotAdded = false;
            }
        } );
    }

    /**
     * bucket tree query.
     * */

    void query( Vector q, List<Line> res ) {
        List<Line> I = T.get( isX ? q.x : q.y );
        if ( I != null ) res.addAll( I );
    }

    @Override
    public int size() {
        System.err.println( "size() is unavailable in interval tree" );
        System.exit( 1 );
        return -1;
    }

    //-------------------------------------------------------
    // Check integrity of interval tree data structure.
    //-------------------------------------------------------

    boolean check( IntervalNode n ) {
        if ( n == null ) return true;

        n.check();
        assert n.left == null || c.compare( n.left.p, n.p ) < 0;
        assert n.right == null || c.compare( n.p, n.right.p ) < 0;

        return check( n.left ) && check( n.right );
    }

}
