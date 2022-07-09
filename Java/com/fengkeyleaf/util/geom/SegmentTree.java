package com.fengkeyleaf.util.geom;

/*
 * SegmentTree.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/18/2022$
 */

import com.fengkeyleaf.util.tree.DoublyLinkedRBT;
import com.fengkeyleaf.util.tree.MapTreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Data structure of basic segment tree, only support 2d for now.
 * Time complexity: O( logn + k ),
 * where n is # of points and k is # of reported ones.
 * ( also with the aid of interval tree to deal with some axis-parallel intervals )
 *
 * Note that this segment tree don't support orthogonal windowing query.
 * It can do stabbing query with arbitrary oriented segments, but no intersecting ones.
 * while interval tree only can handle axis-parallel segments.
 *
 * Theorem 10.12
 * A segment tree for a set I of n intervals uses O(nlogn) storage
 * and can be built in O(nlogn) time.
 * Using the segment tree we can report all intervals
 * that contain a query point in O(logn+k) time,
 * where k is the number of reported intervals.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class SegmentTree extends WindowingQueryTree
        implements OrthogonalWindowingQuery.StabbingQuery {

    private static final String title = "Visualized debugger for Segment Tree";
    // intersecting type.
    enum Type {
        // Int(ν) ⊆ [x : x']
        CONTAINED,
        // Int(v) ∩ [x : x'] != null
        INTERSECT,
        // Int(v) ∩ [x : x'] == null
        OUTSIDE
    }
    SegmentNode root;
    // interval tree to handle axis-parallel interval.
    IntervalTree T;

    /**
     * build a basic Segment tree combining range tree with the ability
     * to do orthogonal windowing query with arbitrary oriented segments.
     *
     * @param I    arbitrary oriented segment set.
     *             And the line's left endpoint should be less than its right endpoint.
     *             To guarantee that, use {@link Segment} to create a line.
     *             Allowed duplicates, which will be removed in the process.
     *             But prohibit intersecting segments, except for overlapping endpoint,
     *             or endpoint lying on a segment.
     * @param isX  true, query vertically; false, query horizontally.
     * */

    public SegmentTree( List<Line> I, boolean isX ) {
        super( isX );

        if ( I == null || I.isEmpty() ) return;

        // build segment tree and interval tree.
        root = build( preprocess( I = init( I ) ) );
        // insert segments into the tree.
        insertSegment( I );
        assert check();
    }

    void insertSegment( List<Line> I ) {
        I.forEach( i -> insertSegment( root, new Interval( i ) ) );
    }

    List<Line> init( List<Line> I ) {
        // remove duplicate lines and also auto-wire this.I for checking.
        I = Lines.removeDuplicates( I, c );
        assert ( this.I = I ) != null;

        // separate special interval
        return separate( I );
    }

    /**
     * @param I startPoint < endPoint
     * */

    List<Interval> preprocess( List<Line> I ) {
        if ( I.isEmpty() ) return new ArrayList<>();

        // presort endpoints, by x-coor or y-coor.
        // of overlapping endpoints, only one of them will be kept.
        DoublyLinkedRBT<Vector, Vector> T = new DoublyLinkedRBT<>( c );
        I.forEach( i -> {
            T.put( i.startPoint, i.startPoint );
            T.put( i.endPoint, i.endPoint );
        } );

        // generate elementary intervals, presorted by x-coor or y-coor.
        List<Interval> L = new ArrayList<>( I.size() * 2 + 2 );

        // negative infinite interval.
        L.add( new Interval( false, T.iterator().next().key ) );
        Iterator<MapTreeNode<Vector, Vector>> i = T.iterator();

        Vector pre = null;
        while ( i.hasNext() ) {
            Vector cur = i.next().key;
            // line-elementary interval
            if ( pre != null )
                L.add( new Interval( pre, cur ) );

            // endpoint-elementary interval
            L.add( new Interval( pre = cur ) );
        }

        // positive infinite interval.
        L.add( new Interval( true, pre ) );
        return L;
    }

    /**
     * separate special intervals from others.
     * i.e. handle verticals separately when querying vertically.
     * */

    List<Line> separate( List<Line> I ) {
        List<Line> lines = new ArrayList<>();
        List<Line> S = new ArrayList<>();

        I.forEach( i -> {
            if ( isX ? i.isVertical : i.isHorizontal )
                S.add( i );
            else lines.add( i );
        } );

        T = new IntervalTree( S, isX );
        return lines;
    }

    /**
     * class to present an elementary interval
     * */

    class Interval {
        QueryVector l;
        boolean isOpenL;
        QueryVector r;
        boolean isOpenR;
        // line in the form of LineNode.
        LineNode line;

        // infinite elementary interval
        Interval( boolean isPosInf, Vector p ) {
            assert p != null;

            l = QueryVector.NEG_INF;
            r = QueryVector.POS_INF;

            // see infinite as closed interval to make intersecting check easy.
            // -INF: (-INF, i) conceptually; [-INF, i) in implementation.
            // +INF: (i, +INF) conceptually; (i, +INF] in implementation.
            if ( isPosInf ) {
                l = new QueryVector( p );
                isOpenL = true;
                return;
            }

            r = new QueryVector( p );
            isOpenR = true;
        }

        // endpoint-elementary interval
        Interval( Vector p ) {
            assert p != null;

            l = r = new QueryVector( p );
            isOpenL = isOpenR = false;
        }

        // line interval
        Interval( Line l ) {
            this.l = new QueryVector( l.startPoint );
            r = new QueryVector( l.endPoint );

            isOpenL = isOpenR = false;
            line = new LineNode( l.startPoint, l );
        }

        // line-elementary interval
        Interval( Vector l, Vector r ) {
            assert l != null && r != null;

            this.l = new QueryVector( l );
            this.r = new QueryVector( r );
            isOpenL = isOpenR = true;
        }

        Interval() {}

        /**
         * this interval contains, intersects
         * or lies outside another interval?
         *
         * @param i r <= l
         * */

        Type contains( Interval i ) {
            if ( isOutSide( i ) )
                return Type.OUTSIDE;
            else if ( contains( i.l, i.isOpenL ) &&
                    contains( i.r, i.isOpenR ) )
                return Type.CONTAINED;

            return Type.INTERSECT;
        }

        boolean isOutSide( Interval i ) {
            int resL = QueryVector.compare( l, i.r, c, isX );
            int resR = QueryVector.compare( r, i.l, c, isX );

            return ( resL == 0 ? // have the same point of l and i.r
                    // this interval is open or other one is open,
                    // other interval lies outside.
                    // otherwise, lying outside only if i.r goes before l.
                    isOpenL || i.isOpenL : resL > 0 ) ||
                    ( resR == 0 ? isOpenR || i.isOpenR : resR < 0 );
        }

        boolean contains( QueryVector q, boolean isOpen ) {
            int resL = QueryVector.compare( l, q, c, isX );
            int resR = QueryVector.compare( r, q, c, isX );

            return ( resL == 0 ? !( isOpenL && !isOpen ) : resL < 0 ) &&
                    ( resR == 0 ? !( isOpenR && !isOpen ) : resR > 0 );
        }

        Type contains( Line l ) {
            return contains( new Interval( l ) );
        }

        /**
         * the union of two consecutive intervals.
         *
         * @param i this.r <= i.l
         * */

        Interval union( Interval i ) {
            Interval interval =  new Interval();
            interval.l = l;
            interval.isOpenL = isOpenL;
            interval.r = i.r;
            interval.isOpenR = i.isOpenR;

            return interval;
        }

        @Override
        public String toString() {
            return ( isOpenL ? "(" : "[" ) + l + ", " + r + ( isOpenR ? ")" : "]" );
        }

    }

    /**
     * tree node to build segment tree.
     * */

    class SegmentNode {
        final Interval i;
        SegmentNode left;
        SegmentNode right;
        // LineNode set to store segment.
        List<Vector> P;

        SegmentNode( List<Interval> I ) {
              i = I.get( 0 ).union( I.get( I.size() - 1 ) );
        }

        SegmentNode( List<Interval> I, SegmentNode l, SegmentNode r ) {
            this( I );
            left = l;
            right = r;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }

        /**
         * add segment into the interval ( i ) of this node
         * if i is contained by the segment.
         * */

        void add( LineNode p ) {
            if ( P == null ) P = new ArrayList<>();

            P.add( p );
        }

        @Override
        public String toString() {
            return i.toString();
        }
    }

    // To construct a segment tree we proceed as follows.
    // First we sort the endpoints of the intervals in I in O(nlogn) time.
    // This gives us the elementary intervals.
    // We then construct a balanced binary tree on the elementary intervals,
    // and we determine for each node ν of the tree the interval Int(ν) it represents.
    // This can be done bottom-up in linear time.
    // It remains to compute the canonical subsets for the nodes.
    // To this end we insert the intervals one by one into the segment tree.
    // An interval is inserted into T
    // by calling the following procedure INSERTSEGMENTTREE with ν = root(T).

    /**
     * @param I presorted by x-coor or y-coor.
     *          And no special intervals.
     *          i.g. no vertical segments when querying vertically.
     * */

    SegmentNode build( List<Interval> I ) {
        // all input intervals are special ones.
        // they will be handled by interval tree.
        if ( I.isEmpty() ) return null;
        // real base case.
        else if ( I.size() == 1 ) return new SegmentNode( I );

        // mid = ( r - l ) / 2 + l;
        int mid = ( I.size() - 1 ) / 2;
        List<Interval> L = I.subList( 0, mid + 1 );
        List<Interval> R = I.subList( mid + 1, I.size() );

        return new SegmentNode( I, build( L ), build( R ) );
    }

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm INSERTSEGMENTTREE(ν, [x : x'])
    // Input. The root of a (subtree of a) segment tree and an interval.
    // Output. The interval will be stored in the subtree.
    void insertSegment( SegmentNode n, Interval i ) {
        // 1. if Int(ν) ⊆ [x : x']
        if ( i.contains( n.i ) == Type.CONTAINED ) {
            // 2. then store [x : x'] at ν
            n.add( i.line );
            return;
        }

        // 3. else if Int(lc(ν)) ∩ [x : x'] != null
        // 4. then INSERTSEGMENTTREE(lc(ν), [x : x'])
        if ( n.left.i.contains( i ) != Type.OUTSIDE )
            insertSegment( n.left, i );

        // 5. if Int(rc(ν)) ∩ [x : x'] != null
        // 6. then INSERTSEGMENTTREE(rc(ν), [x : x'])
        if ( n.right.i.contains( i ) != Type.OUTSIDE )
            insertSegment( n.right, i );
    }

    /**
     * stabbing query with the point, {@code q}.
     *
     * @param q query point.
     * */

    @Override
    public List<Line> query( Vector q ) {
        List<Line> res = new ArrayList<>();
        // query the segment tree to include special intervals,
        // in O( logn + k ) time.
        if ( T != null )
            res.addAll( T.query( q ) );

        if ( root == null ) return check( q, res, title );

        // store LineNode instead of Line,
        // to avoid to add a line twice.
        List<LineNode> P = new ArrayList<>();

        // With composite number to construct the tree,
        // querying the vertical line q.x is equivalent to
        // querying point (x, +INF) and point (x, -INF).
        // Vice versa for horizontal query line.
        QueryVector[] V = IntervalTree.getQueryVector( q, isX );
        // query point, (x, +INF) or (+INF, y)
        query( root, V[ 0 ], P );
        // query point, (x, -INF) or (-INF, y)
        query( root, V[ 1 ], P );

        // get resulting line set and reset status.
        P.forEach( p -> {
            res.add( p.l );
            p.isNotAdded = true;
        } );

        return check( q, res, title );
    }

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm QUERYSEGMENTTREE(ν,qx)
    // Input. The root of a (subtree of a) segment tree and a query point qx.
    // Output. All intervals in the tree containing qx.
    void query( SegmentNode n, QueryVector q, List<LineNode> res ) {
        // 1. Report all the intervals in I(ν).
        report( n, res );

        // 2. if ν is not a leaf
        if ( !n.isLeaf() ) {
            // 3. then if qx ∈ Int(lc(ν))
            if ( n.left.i.contains( q, false ) )
                // 4. then QUERYSEGMENTTREE(lc(ν),qx)
                query( n.left, q, res );
            else
                // 5. else QUERYSEGMENTTREE(rc(ν),qx)
                query( n.right, q, res );
        }
    }

    void report( SegmentNode n, List<LineNode> res ) {
        if ( n.P == null ) return;

        n.P.forEach( p -> {
            LineNode v = ( LineNode ) p;

            if ( v.isNotAdded() ) {
                res.add( v );
                v.isNotAdded = false;
            }
        } );
    }

    @Override
    public int size() {
        System.err.println( "size() is unavailable in segment tree" );
        System.exit( 1 );
        return -1;
    }

    //-------------------------------------------------------
    // Check integrity of segment tree data structure.
    //-------------------------------------------------------

    boolean check() {
        isContained( root );
        isRightSeg( root );

        return true;
    }

    void isContained( SegmentNode x ) {
        if ( x == null ) return;

        assert x.left == null || x.i.contains( x.left.i ) == Type.CONTAINED : x.i + " | " + x.left.i;
        assert x.right == null || x.i.contains( x.right.i ) == Type.CONTAINED : x.i + " | " + x.right.i;

        isContained( x.left ); // check left subtree.
        isContained( x.right ); // check right subtree.
    }

    void isRightSeg( SegmentNode n ) {
        if ( n == null ) return;

        assert n.left == null || isRightSeg( n, n.left );
        assert n.right == null || isRightSeg( n, n.right );

        isRightSeg( n.left );
        isRightSeg( n.right );
    }

    boolean isRightSeg( SegmentNode p, SegmentNode c ) {
        if ( p.P == null ) return true;

        p.P.forEach( i -> {
            assert c.i.contains( ( ( LineNode ) i ).l ) != Type.CONTAINED : c.i + " | " + i;
        } );

        return true;
    }
}
