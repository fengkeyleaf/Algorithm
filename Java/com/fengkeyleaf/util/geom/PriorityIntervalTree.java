package com.fengkeyleaf.util.geom;

/*
 * PrioritySearchTree.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/14/2022$
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Interval tree combined with priority search tree
 * to have the ability to do orthogonal windowing query with axis-parallel segments.
 *
 * Theorem 10.9
 * A priority search tree for a set P of n points in the plane uses O(n) storage
 * and can be built in O(nlogn) time.
 * Using the priority search tree
 * we can report all points in a query range of the form (−∞ : qx]×[qy : qy']
 * in O(logn+k) time, where k is the number of reported points.
 *
 * So combining Theorem 10.4, we can report segments intersecting
 * an axis-parallel rectangular query window in O((logn)^2+k) time.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class PriorityIntervalTree extends IntervalRangeTree {

    // the following two are used by priority search tree as the outer layer's comparator.
    // i.e. if priority interval tree is built on horizontal intervals( x-coor ),
    // and then priority search tree will be built on y-coor and then x-coor.
    // outer layer's isX, which is the same as the one in class. RangeTree.
    boolean isXMain;
    // outer layer's c, which is the same as the one in class RangeTree.
    Comparator<com.fengkeyleaf.util.geom.Vector> cMain;

    /**
     * build an interval tree enhanced with priority search tree
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
     */

    public PriorityIntervalTree( List<Line> I, boolean isX ) {
        super( null, isX );

        isXMain = isX;
        cMain = c;
        if ( I == null || I.isEmpty() ) return;

        // preprocess() will function as it is in IntervalRangeTree.
        root = build( preprocess( I ) );
        assert check( root );
    }

    /**
     * class to combine priority search tree with interval tree.
     * */

    class PriorityNode extends IntervalRangeNode {
        PrioritySearchTree L;
        PrioritySearchTree R;

        PriorityNode( List<LineNode> P ) {
            super( P.get( P.size() / 2 - 1 ) );
            assert P.size() % 2 == 0;

            List<com.fengkeyleaf.util.geom.Vector> L = new ArrayList<>();
            List<com.fengkeyleaf.util.geom.Vector> R = new ArrayList<>();
            initL( P, L, R );
            initR( P, L, R );

            this.L = new PrioritySearchTree( preprocessEndpoint( L ) );
            this.R = new PrioritySearchTree( preprocessEndpoint( R ) );
        }

        /**
         * Priority Search Tree designed for orthogonal windowing query.
         * */

        class PrioritySearchTree extends IntervalLayeredRangeTree {

            // minimum x-coor or y-coor point among the input interval set.
            final RangeNode pMin;

            // It’s straightforward to derive a recursive O(nlogn) algorithm
            // for building a priority search tree.
            // Interestingly, priority search trees can even be built in linear time,
            // if the points are already sorted on y-coordinate.
            // The idea is to construct the tree bottom-up instead of top-down,
            // in the same way heaps are normally constructed

            /**
             * @param I  interval set, presorted in x-coor or y-coor,
             *           if the outer interval tree is built on y-coor or x-coor.
             * */

            PrioritySearchTree( List<com.fengkeyleaf.util.geom.Vector> I ) {
                // c = opposite to cMain, isX = opposite to isMain.
                super( new ArrayList<>(), !isXMain );

                assert ( P = I ) != null;
                pMin = new RangeNode( preprocess( I ) );
                // mid = ( r - l ) / 2 + l,
                // where l is always 1 and r is xL.size() - 1.
                int mid = I.size() < 2 ? 0 : ( I.size() - 2 ) / 2 + 1;
                root = new RangeNode( I.get( mid ) );

                root.left = initL( I, mid ); // init below
                root.right = initR( I, mid + 1 ); // init above

                assert root.left == null || c.compare( root.p, root.left.p ) >= 0 : root.p + " | " + root.left.p;
                assert root.right == null || c.compare( root.p, root.right.p ) <= 0;
                assert check( root.left );
                assert check( root.right );
            }

            // A formal definition of a priority search tree
            // for a set P of points is as follows.
            // If P = null then the priority search tree is an empty leaf.
            // Otherwise, Let yMid be the median of the y-coordinates of the remaining points.
            // Let
            // P_below := {p ∈ P\{pMin} : py <= yMid},
            // P_above := {p ∈ P\{pMin} : py > yMid}.
            // The priority search tree consists of a root node ν
            // where the point p(ν) := pMin and
            // the value y(ν) := yMid are stored.
            // Furthermore, the left subtree of ν is a priority search tree for the set P_below,
            // the right subtree of ν is a priority search tree for the set P_above.

            com.fengkeyleaf.util.geom.Vector preprocess( List<com.fengkeyleaf.util.geom.Vector> xL ) {
                com.fengkeyleaf.util.geom.Vector pMin = xL.get( 0 );
                xL.sort( ( v1, v2 ) -> {
                    // point with minimum x-coor or y-coor is considered always the smallest.
                    if ( v1 == pMin ) return -1;
                    if ( v2 == pMin ) return 1;

                    return c.compare( v1, v2 );
                } );

                assert check( pMin, xL );
                return pMin;
            }

            boolean check( com.fengkeyleaf.util.geom.Vector pMin, List<com.fengkeyleaf.util.geom.Vector> xL ) {
                for ( int i = 1; i < xL.size(); i++ ) {
                    // be careful with overlapping endpoints.
                    assert cMain.compare( pMin, xL.get( i ) ) <= 0 : pMin + " | " + xL.get( i );
                }

                return true;
            }

            // init below
            private RangeNode initL( List<com.fengkeyleaf.util.geom.Vector> I, int mid ) {
                if ( I.size() < 2 ) return null;

                int idx = 1;
                LinkedList<RangeNode> Q = new LinkedList<>();
                RangeNode n = new RangeNode( I.get( idx++ ) );
                Q.add( n );

                // build the heap tree directly from an array.
                // i.e. use a queue to build the tree based on its layer level.
                while ( !Q.isEmpty() ) {
                    RangeNode v = Q.poll();

                    // should include mid in the left heap.
                    if ( idx > mid ) break;
                    v.left = new RangeNode( I.get( idx++ ) );
                    Q.add( v.left );
                    assert c.compare( v.p, v.left.p ) <= 0;

                    if ( idx > mid ) break;
                    v.right = new RangeNode( I.get( idx++ ) );
                    Q.add( v.right );
                    assert c.compare( v.p, v.right.p ) <= 0;
                }

                return n;
            }

            /**
             * @param mid index just 1 greater than the real median index of the list, {@code I}
             * */

            // init above
            private RangeNode initR( List<com.fengkeyleaf.util.geom.Vector> I, int mid ) {
                if ( I.size() < 3 ) return null;

                LinkedList<RangeNode> Q = new LinkedList<>();
                RangeNode n = new RangeNode( I.get( mid++ ) );
                Q.add( n );

                // build the heap tree directly from an array.
                // i.e. use a queue to build the tree based on its layer level.
                while ( !Q.isEmpty() ) {
                    RangeNode v = Q.poll();

                    if ( mid >= I.size() ) break;
                    v.left = new RangeNode( I.get( mid++ ) );
                    Q.add( v.left );
                    assert c.compare( v.p, v.left.p ) <= 0;

                    if ( mid >= I.size() ) break;
                    v.right = new RangeNode( I.get( mid++ ) );
                    Q.add( v.right );
                    assert c.compare( v.p, v.right.p ) <= 0;
                }

                return n;
            }

            // Reference resource: http://www.cs.uu.nl/geobook/
            // REPORTINSUBTREE(ν,qx)
            // Input. The root ν of a subtree of a priority search tree and a value qx.
            // Output. All points in the subtree with x-coordinate at most qx.
            void reportSubTree( RangeNode n, QueryVector[] R,
                                List<com.fengkeyleaf.util.geom.Vector> res ) {

                // 1. if ν is not a leaf and qx <= (p(ν))x <= qx'
                if ( n != null &&
                        R[ 0 ].compare( n.p, cMain, isXMain ) >= 0 &&
                        R[ 1 ].compare( n.p, cMain, isXMain ) <= 0 ) {

                    // 2. then Report p(ν).
                    reportDuplicate( ( LineNode ) n.p, res );

                    // 3. REPORTINSUBTREE(lc(ν),qx)
                    reportSubTree( n.left, R, res );
                    // 4. REPORTINSUBTREE(rc(ν),qx)
                    reportSubTree( n.right, R, res );
                }
            }

            /**
             * query in this priority search tree with the region, {@code R}.
             * This method is very similar to query2D() in {@link RangeTree}.
             * */

            // Reference resource: http://www.cs.uu.nl/geobook/
            // Algorithm QUERYPRIOSEARCHTREE(T, (−∞ : qx]×[qy : qy'])
            // Input. A priority search tree and a range, unbounded to the left.
            // Output. All points lying in the range.
            @Override
            List<com.fengkeyleaf.util.geom.Vector> query( QueryVector[] R ) {
                // 1. Search with qy and qy' in T.
                // Let νSplit be the node where the two search paths split.
                RangeNode vSplit = findSplitNode( R );
                List<com.fengkeyleaf.util.geom.Vector> res = new ArrayList<>();
                if ( vSplit == null ) return res;

                // 2. for each node ν on the search path of qy or qy'
                // 3. do if p(ν) ∈ (-INF : qx]x[qy : qy'] then report p(ν).
                report( pMin, R, res );
                report( vSplit, R, res );

                // 4. for each node ν on the path of qy in the left subtree of νSplit
                RangeNode v = vSplit.left;
                while ( v != null ) {
                    report( v, R, res );

                    // 5. do if the search path goes left at ν
                    // 6. then REPORTINSUBTREE(rc(ν),qx)
                    if ( R[ 0 ].compare( v.p, c, isX ) <= 0 ) {
                        reportSubTree( v, R, res );
                        v = v.left;
                        continue;
                    }

                    v = v.right;
                }
                report( v, R, res );

                // 7. for each node ν on the path of qy' in the right subtree of νSplit
                v = vSplit.right;
                while ( v != null ) {
                    report( v, R, res );

                    // 8. do if the search path goes right at ν
                    // 9. then REPORTINSUBTREE(lc(ν),qx)
                    if ( R[ 1 ].compare( v.p, c, isX ) > 0 ) {
                        reportSubTree( v, R, res );
                        v = v.right;
                        continue;
                    }

                    v = v.left;
                }
                report( v, R, res );

                return check2D( R, res );
            }

            @Override
            boolean isNotSplit( RangeNode v, QueryVector[] R ) {
                // note that for the priority search tree,
                // some node may have single child.
                // so cannot use !v.isLeaf() to verify.
                return v != null &&
                          ( isOutOfRangeLeft( v, R ) ||
                               isOutOfRangeRight( v, R ) );
            }

            @Override
            void report( RangeNode n, QueryVector[] R, List<com.fengkeyleaf.util.geom.Vector> res ) {
                if ( n == null ) return;

                // n lying inside R
                if ( new RangeTree.RangeNode( n.p ).isContained( R ) )
                    reportDuplicate( ( LineNode ) n.p, res );
            }

            //----------------------------------------------------------
            // Check integrity of priority search tree data structure.
            //----------------------------------------------------------

            boolean check( RangeNode n ) {
                if ( n == null ) return true;

                // first element < i-th element, compared by x-coor.
                // be careful with overlapping endpoints.
                assert cMain.compare( pMin.p, n.p ) <= 0 : pMin.p + " | " + n.p;

                // P_below := {p ∈ P\{pMin} : py <= yMid},
                assert n.left == null || c.compare( n.p, n.left.p ) <= 0 : n.p + " | " + n.left.p;
                // P_above := {p ∈ P\{pMin} : py > yMid}.
                // note that there are intervals with one overlapping endpoint.
                assert n.right == null || c.compare( n.p, n.right.p ) <= 0;

                return check( n.left ) && check( n.right );
            }
        }
    }

    /**
     * @param I  interval set, presorted in x-coor or y-coor,
     * */

    @Override
    PriorityNode build( List<LineNode> I ) {
        // 1. if I = empty
        // 2. then return an empty leaf
        if ( I.isEmpty() ) return null;

        // 3. else Create a node ν.
        // Compute xMid, the median of the set of interval endpoints, and store xMid with ν.
        // 4. Compute IMid and construct two sorted lists for IMid:
        // a list LLeft(ν) sorted on left endpoint and a list LRight(ν) sorted on right endpoint.
        // Store these two lists at ν.
        PriorityNode v = new PriorityNode( I );
        // 5. lc(ν) <- CONSTRUCTINTERVALTREE(ILeft)
        v.left = build( v.subL );
        // 6. rc(ν) <- CONSTRUCTINTERVALTREE(IRight)
        v.right = build( v.subR );

        // 7. return ν
        return ( PriorityNode ) v.cleanUp();
    }

    /**
     * query associated structure ( priority search tree ).
     *
     * @param res result line set.
     * @param n node at which we need search its associated structures.
     * @param R orthogonal searching area.
     * @param isPosInf query positive or negative infinite.
     * */

    @Override
    void report( List<LineNode> res, IntervalRangeNode n,
                 List<com.fengkeyleaf.util.geom.Vector> R, boolean isPosInf ) {
        String[] RStr = getOrthogonalArea( R, isPosInf );
        // query in priority search tree.
        PriorityNode v = ( PriorityNode ) n;
        List<Vector> subRes = isPosInf ? v.R.query( RStr ) : v.L.query( RStr );
        subRes.forEach( p -> res.add( ( ( LineNode ) p ) ) );

        assert checkDuplicate( res );
    }
}
