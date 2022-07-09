package com.fengkeyleaf.util.geom;

/*
 * KdTree.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 4/23/2022$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.util.MyCollections;
import com.fengkeyleaf.util.Node;
import com.fengkeyleaf.util.tree.AbstractTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data structure of kd-tree, only support 2d for now.
 * Time complexity: O(√n +k）, where n is # of points and k is # of reported ones.
 *
 * Note that with doubly-linked nodes, this kd-tree is no longer a tree,
 * but actually a connected graph.
 *
 * Theorem 5.5
 * A kd-tree for a set P of n points in the plane uses O(n) storage
 * and can be built in O(nlogn) time.
 * A rectangular range query on the kd-tree takes O( √n+k ) time,
 * where k is the number of reported points.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class KdTree extends AbstractTree<Vector> {
    final KdNode root;
    enum Type {
        // region(lc(ν)) or region(rc(ν)) is fully contained in R
        CONTAINED,
        // region(lc(ν)) or region(rc(ν)) intersects R
        INTERSECT,
        // region(lc(ν)) or region(rc(ν)) is outside R
        OUTSIDE
    }
    // visualization program.
    DrawingProgram drawer;
    // point set for visualization and checking.
    List<Vector> P;

    /**
     * init a kd-tree with the given point set, {@code P}.
     *
     * @param P A set of points P.
     *          Duplicate points allowed, they will be removed in the process.
     * */

    public KdTree( List<Vector> P ) {
        assert checkID( P );

        this.P = P = MyCollections.removeDuplicates( P, Vectors::sortByX );
        // build the tree
        root = P == null || P.isEmpty() ? null : build( P, 0 );
        // init region for each node.
        if ( root != null ) initR( root );

        assert check();
    }

    private boolean checkID( List<Vector> P ) {
        if ( P == null ) return true;

        P.forEach( v -> {
            assert v.mappingID == -1;
        } );

        return true;
    }

    private static class KdNode extends Node {
        private static int IDStatic = 0;

        enum Type {
            // leaf node
            LEAF,
            // horizontal separating line
            HORIZONTAL,
            // vertical separating line
            VERTICAL,
            // following two are conceptual nodes.
            // This idea was inspired by
            // the way of handling p-1 and p-2 in Delaunay Triangulation.
            // node with positive infinite x or y coordinate.
            POS_INFINITE,
            // node with negative infinite x or y coordinate.
            NEG_INFINITE
        }
        // conceptual points, infPos and infNeg
        static KdNode infPos = new KdNode( null, Type.POS_INFINITE );
        static KdNode infNeg = new KdNode( null, Type.NEG_INFINITE );
        final Vector p;
        final Type type;
        KdNode left;
        KdNode right;
        // the region in which this node is lying.
        // [ ( x1, y1 ), ( x2, y2 ) ] = [ bottomLeft, topRight ]
        KdNode[] R;

        KdNode( Vector p, Type t ) {
            super( IDStatic++ );

            this.p = p;
            this.type = t;
        }

        KdNode( Vector p, boolean isEven, KdNode l, KdNode r ) {
            super( IDStatic++ );

            this.p = p;
            this.type = isEven ? KdNode.Type.VERTICAL : KdNode.Type.HORIZONTAL;
            // doubly-linked:  parent <-> child
            left = l;
            l.parent = this;
            right = r;
            r.parent = this;
        }

        /**
         * init the region in which this node lying.
         * Note that we also initialize the region for the leaf node.
         * */

        private void initR() {
            R = new KdNode[]{ infNeg, infNeg, infPos, infPos };

            // The region is bounded by this node's parent,
            // its grandparent and its grand-grandparent.
            KdNode l_1 = parent == null ? null : ( KdNode ) parent;
            KdNode l_2 = l_1 == null ? null : ( KdNode ) l_1.parent;
            KdNode l_3 = l_2 == null ? null : ( KdNode ) l_2.parent;
            boolean isHorizontal = isHorizontal( l_1 );

            // set the region based on the type of this node.
            if ( isHorizontal ) {
                setAs( l_1, this );
                setBs( l_2, l_1 );
                if ( isDiffSide( this, l_1, l_2, l_3 ) )
                    setAs( l_3, l_2 );
            }
            else {
                setBs( l_1, this );
                setAs( l_2, l_1 );
                if ( isDiffSide( this, l_1, l_2, l_3 ) )
                    setBs( l_3, l_2 );
            }
        }

        /**
         * Is this node a horizontal or vertical one?
         * the type of the leaf node is depended on its parent.
         * I.e. a leaf node is "vertical" if and only if its parent is "horizontal".
         * */

        private boolean isHorizontal( KdNode p ) {
            // a leaf node is considered vertical, but without parent,
            // is the root node with no children.
            if ( p == null ) return false;

            if ( type == Type.LEAF ) return p.type == Type.VERTICAL;

            return type == Type.HORIZONTAL;
        }

        /**
         * Do this node's{@code n} grandparent{@code l_1} and
         * its grand-grandparent {@code l_3} have different type of children.
         *
         * @param l_1 n's parent.
         * @param l_2 n's grandparent.
         * @param l_3 n's grand-grandparent.
         * */

        private boolean isDiffSide( KdNode n, KdNode l_1,
                                    KdNode l_2, KdNode l_3 ) {

            if ( l_3 == null ) return false;

            return !( l_1.left == n && l_3.left == l_2 || l_1.right == n && l_3.right == l_2 );
        }

        private void setAs( KdNode p, KdNode n ) {
            if ( p == null ) return;

            if ( p.left == n ) {
                R[ 2 ] = p;
                return;
            }

            assert p.right == n;
            R[ 0 ] = p;
        }

        private void setBs( KdNode p, KdNode n ) {
            if ( p == null ) return;

            if ( p.left == n ) {
                R[ 3 ] = p;
                return;
            }

            assert p.right == n;
            R[ 1 ] = p;
        }

        /**
         * get a segment for this separating line.
         * */

        Line getSegment( BoundingBox b ) {
            return switch ( type ) {
                case HORIZONTAL -> getHorizontal( b );
                case VERTICAL -> getVertical( b );
                default -> null;
            };
        }

        private Line getHorizontal( BoundingBox b ) {
            double xl = R[ 0 ].type == Type.NEG_INFINITE ? b.minX : R[ 0 ].p.x;
            double xr = R[ 2 ].type == Type.POS_INFINITE ? b.maxX : R[ 2 ].p.x;
            return new Line( new Vector( xl, p.y ), new Vector( xr, p.y ) );
        }

        private Line getVertical( BoundingBox b ) {
            double yb = R[ 1 ].type == Type.NEG_INFINITE ? b.minY : R[ 1 ].p.y;
            double yu = R[ 3 ].type == Type.POS_INFINITE ? b.maxY : R[ 3 ].p.y;
            return new Line( new Vector( p.x, yb ), new Vector( p.x, yu ) );
        }

        /**
         * does the searching area contain this leaf node?
         * Note that no need to consider degenerate case,
         * because this is a kind of brute force with only one query point.
         *
         * @param R orthogonal searching area.
         * @param res reported point set.
         * */

        // TODO: 5/18/2022 could combine QueryNode
        void contains( List<Vector> R, List<Vector> res ) {
            assert type == Type.LEAF;

            if ( R.get( 0 ).x <= p.x && p.x <= R.get( 1 ).x &&
                        R.get( 0 ).y <= p.y && p.y <= R.get( 1 ).y )
                res.add( p );
        }

        /**
         * does the searching area {@code R} contain, intersect or is outside
         * the region of this node.
         *
         * @param R orthogonal searching area.
         * */

        KdTree.Type contains( List<Vector> R ) {
            assert this.R[ 0 ].type != Type.NEG_INFINITE || this.R[ 1 ].type != Type.POS_INFINITE;

            Vector l = R.get( 0 );
            Vector r = R.get( 1 );

            // We only identify two cases: outside and full contained,
            // because there are too many cases with intersect.
            // And as we know, there are three generate cases
            // for the geometric relations between two rectangles, A and B.
            // Outside, intersect and contained.
            // But there are two kinds of fully contained,
            // A contains B, or B contains A.
            // And the latter is regarded as a kind of intersect,
            // if we assume A is the orthogonal searching are,
            // and B is the region of a tree node.
            if ( isOutsideX( l, r ) || isOutsideY( l, r ) )
                return KdTree.Type.OUTSIDE;
            else if ( containsX( l, r ) && containsY( l, r ) )
                return KdTree.Type.CONTAINED;

            return KdTree.Type.INTERSECT;
        }

        /**
         * Note that this must hold:
         * l.x < r.x
         *
         * @param l bottom left of the orthogonal searching area.
         * @param r top right of the orthogonal searching area.
         * */

        private boolean isOutsideX( Vector l, Vector r ) {
            // the region of this node has positive infinite x-coor,
            // it's impossible to be case of outside.
            return ( !( R[ 2 ].type == Type.POS_INFINITE ) &&
                    // No positive infinite x-coor, just compare x-coors,
                    // but compare y-coors when they have the same x-coors.
                    // In this way, we title a vertical line a little bit
                    // to make no two points lying on the same vertical line, conceptually.
                    // This idea was inspired by the way of handling degenerate cases in Point Location.
                    Vectors.sortByX( R[ 2 ].p, l ) < 0 ) ||
                        // similar idea when handling with left part of x-coors.
                        ( !( R[ 0 ].type == Type.NEG_INFINITE ) &&
                            Vectors.sortByX( r, R[ 0 ].p ) <= 0 );
        }

        private boolean isOutsideY( Vector l, Vector r ) {
            return ( !( R[ 3 ].type == Type.POS_INFINITE ) && Vectors.sortByY( R[ 3 ].p, l ) < 0 ) ||
                    ( !( R[ 1 ].type == Type.NEG_INFINITE ) && Vectors.sortByY( r, R[ 1 ].p ) <= 0 );
        }

        private boolean containsX( Vector l, Vector r ) {
            return ( !( R[ 0 ].type == Type.NEG_INFINITE ) && Vectors.sortByX( l, R[ 0 ].p ) <= 0 ) &&
                    ( !( R[ 2 ].type == Type.POS_INFINITE ) && Vectors.sortByX( R[ 2 ].p, r ) <= 0 );
        }

        private boolean containsY( Vector l, Vector r ) {
            return ( !( R[ 1 ].type == Type.NEG_INFINITE ) && Vectors.sortByY( l, R[ 1 ].p ) <= 0 ) &&
                    ( !( R[ 3 ].type == Type.POS_INFINITE ) && Vectors.sortByY( R[ 3 ].p, r ) <= 0 );
        }

        @Override
        public String toString() {
            return switch ( type ) {
                case LEAF -> p.toString();
                case HORIZONTAL -> "H: " + p;
                case VERTICAL -> "V: " + p;
                case POS_INFINITE -> "+Inf";
                case NEG_INFINITE -> "-Inf";
            };
        }
    }

    /**
     * build the kd-tree.
     *
     * Reference resource:
     * @see <a href="http://www.cs.uu.nl/geobook/">Computational Geometry: Algorithms and Applications(Third Edition)</a>
     *
     * @param P A set of points P
     * @param d current depth d
     * */

    // Algorithm BUILDKDTREE( P, depth )
    // Input. A set of points P and the current depth d.
    // Output. The root of a kd-tree storing P.
    KdNode build( List<Vector> P, int d ) {
        assert !P.isEmpty();

        // 1.1 if P contains only one point
        // 2. then return a leaf storing this point
        if ( P.size() == 1 ) return new KdNode( P.get( 0 ), KdNode.Type.LEAF );

        Vector m = null;
        boolean isEven = false;
        List<Vector> p1 = null;
        List<Vector> p2 = null;
        int mid = P.size() % 2 == 0 ? P.size() / 2 - 1 : P.size() / 2;
        // 3. else if depth is even
        if ( d % 2 == 0 ) {
            // 4. then Split P into two subsets with a vertical line l
            // through the median x-coordinate of the points in P.
            m = MyCollections.kSelect( P, mid, Vectors::sortByX );

            // Let P1 be the set of points to the left of l or on l,
            // and let P2 be the set of points to the right of l.
            List<List<Vector>> l = MyCollections.split( P, m, Vectors::sortByX );
            List<List<Vector>> res = split( l );

            p1 = res.get( 0 );
            p2 = res.get( 1 );

            isEven = true;
        }
        else {
            // 5. else Split P into two subsets with a horizontal line l
            // through the median y-coordinate of the points in P.
            m = MyCollections.kSelect( P, mid, Vectors::sortByY );

            // Let P1 be the set of points below l or on l,
            // and let P2 be the set of points above l.
            List<List<Vector>> l = MyCollections.split( P, m, Vectors::sortByY );
            assert l != null : P;
            List<List<Vector>> res = split( l );

            p1 = res.get( 0 );
            p2 = res.get( 1 );
        }

        // Record how many times the median point has been chosen as a separating line.
        // i.e. a point can only be selected as separating line twice,
        // one for vertical, one for horizontal.
        // Notice that mappingID starts at -1 as default.
        assert ++m.mappingID < 2;

        // 6. vLeft <- BUILDKDTREE( P1, depth + 1 )
        // 7. vRight <- BUILDKDTREE( P2, depth + 1 )
        // 8. Create a node v storing l,
        // make vLeft the left child of n, and make vRight the right child of n.
        // 9. return v
        return new KdNode( m, isEven,
                build( p1, d + 1 ), build( p2, d + 1 ) );
    }

    private List<List<Vector>> split( List<List<Vector>> l ) {
        assert l.size() == 3;

        List<List<Vector>> res = new ArrayList<>( 2 );
        List<Vector> p1 = new ArrayList<>( l.get( 0 ) );
        p1.addAll( l.get( 1 ) );

        res.add( p1 );
        res.add( l.get( 2 ) );

        return res;
    }

    private void initR( KdNode n ) {
        if ( n == null ) return;

        n.initR();

        initR( n.left );
        initR( n.right );
    }

    /**
     * search which points lying in the orthogonal region, R.
     *
     * @param R orthogonal searching area, [x:x'] x [y:y']. It is not degenerated to a line or a point.
     *          [ ( x1, y1 ), ( x2, y2 ) ] = [ bottomLeft, topRight ].
     * */

    // TODO: 5/1/2022 handle when R is degenerated to a line or a point.
    public List<Vector> query( List<Vector> R ) {
        if ( root == null ) return null;

        List<Vector> res = new ArrayList<>();
        query( root, R, res );

        return check( R, res );
    }

    /**
     * search which points lying in the orthogonal region, R.
     *
     * Reference resource:
     * @see <a href="http://www.cs.uu.nl/geobook/">Computational Geometry: Algorithms and Applications(Third Edition)</a>
     * */

    // Algorithm SEARCHKDTREE(v,R)
    // Input. The root of (a subtree of) a kd-tree, and a range R.
    // Output. All points at leaves below v that lie in the range.
    void query( KdNode n, List<Vector> R, List<Vector> res ) {
        // 1. if n is a leaf
        if ( n.type == KdNode.Type.LEAF ) {
            // 2. then Report the point stored at n if it lies in R.
            n.contains( R, res );
            return;
        }

        // 3. else if region(lc(n)) is fully contained in R
        Type t = n.left.contains( R );
        assert t != null;
        if ( t == Type.CONTAINED )
            // 4. then REPORTSUBTREE( lc( n ) )
            report( n.left, res );
        // 5. else if region( lc( n ) ) intersects R
        else if ( t == Type.INTERSECT ) {
            // 6. then SEARCHKDTREE( lc( n ), R )
            query( n.left, R, res );
        }

        t = n.right.contains( R );
        assert t != null;
        // 7. if region( rc( n ) ) is fully contained in R
        if ( t == Type.CONTAINED )
            // 8. then REPORTSUBTREE( rc( n ) )
            report( n.right, res );
        // 9. else if region( rc( n ) ) intersects R
        else if ( t == Type.INTERSECT ) {
            // 10. then SEARCHKDTREE( rc( n ), R )
            query( n.right, R, res );
        }
    }

    /**
     * report all leaf nodes starting at the node, {@code n}.
     * */

    private void report( KdNode n, List<Vector> res ) {
        if ( n.type == KdNode.Type.LEAF ) {
            res.add( n.p );
            return;
        }

        report( n.left, res );
        report( n.right, res );
    }

    @Override
    public int size() {
        System.err.println( "size() is unavailable in kd-tree" );
        System.exit( 1 );
        return -1;
    }

    //-------------------------------------------------------
    // Check integrity of kd-tree data structure.
    //-------------------------------------------------------

    // note that this checking doesn't guarantee the correctness of this kd-tree,
    // but make it more correct than the one without any checking.
    private boolean check() {
        if ( root == null ) return true;

        isRightPos( root );
        isInRegion( root );
        visualization();

        Node.resetMappingID( P );
        return true;
    }

    // n be the right position?
    private void isRightPos( KdNode n ) {
        if ( n == null ) return;

        assert n.type != KdNode.Type.HORIZONTAL || // n is horizontal line.
                // n.left is a leaf node, no check to the left side.
                // n.left must lie below or on n, if not.
                Vectors.sortByY( n.p, n.left.p ) >= 0
                        // n.right is a leaf node, no check to the right side.
                        // n.right must lie to the left of n or on n, if not.
                        && Vectors.sortByY( n.p, n.right.p ) < 0 : n.p + " | " + n.left.p + " | " + n.right.p;
        // similar to the following one. Poor readability though.
        assert n.type != KdNode.Type.VERTICAL ||
                 Vectors.sortByX( n.p, n.left.p ) >= 0  &&
                        Vectors.sortByX( n.p, n.right.p ) < 0 : n.p + " | " + n.left.p + " | " + n.right.p;

        isRightPos( n.left );
        isRightPos( n.left );
    }

    // n lying in the region defined by its parents?
    private void isInRegion( KdNode n ) {
        if ( n == null ) return;

        isInRegion( n, n.R );

        isInRegion( n.left );
        isInRegion( n.right );
    }

    private void isInRegion( KdNode n, KdNode[] R ) {
        // no precession issue here.
        // degenerate case will cause R[ 0 ].p.x == n.p.x, or R[ 1 ].p.y == n.p.y
        assert R[ 0 ].type == KdNode.Type.NEG_INFINITE || R[ 0 ].p.x <= n.p.x : n + " | " + Arrays.toString( R );
        assert R[ 2 ].type == KdNode.Type.POS_INFINITE || n.p.x <= R[ 2 ].p.x;
        assert R[ 1 ].type == KdNode.Type.NEG_INFINITE || R[ 1 ].p.y <= n.p.y : n + " | " + Arrays.toString( R );
        assert R[ 3 ].type == KdNode.Type.POS_INFINITE || n.p.y <= R[ 3 ].p.y;
    }

    private void visualization() {
        BoundingBox b = BoundingBox.getBoundingBox( P, BoundingBox.OFFSET );

        // drawing data for the kd-tree.
        List<Line> L = new ArrayList<>( P.size() );
        getSegments( root, b, L );

        L.add( b.top.getSegment() );
        L.add( b.left.getSegment() );
        L.add( b.bottom.getSegment() );
        L.add( b.right.getSegment() );

        int size = Math.abs( ( int ) MyMath.findMaxMinInAbs( b.maxX, b.maxY, b.minX, b.minY )[ 1 ] ) * 2;
        drawer = new DrawingProgram( "Visualized debugger for Kd-tree", size, size );
        drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, P  );
        drawer.drawLines( DrawingProgram.NORMAL_POLYGON_COLOR, L );

        drawer.initialize();
    }

    private void getSegments( KdNode n, BoundingBox b, List<Line> L ) {
        if ( n.type == KdNode.Type.LEAF ) return;

        L.add( n.getSegment( b ) );
        assert L.get( L.size() - 1 ) != null;

        getSegments( n.left, b, L );
        getSegments( n.right, b, L );
    }

    private List<Vector> check( List<Vector> R, List<Vector> res ) {

        assert visualization( R, res );

        List<Vector> diff = MyCollections.compare( bruteForce( P, R ), res, Vectors::sortByY );

        if ( !diff.isEmpty() ) {
            System.out.println( compare( res, diff ) );
            assert false;
        }

        return res;
    }

    /**
     * compare result from the algorithm with the one from brute force.
     * */

    static
    String compare( List<Vector> res, List<Vector> diff ) {
        StringBuilder text = new StringBuilder( "Diff: " );
        // assume P has no duplicates.
        // M -> points shouldn't be in the result list, but it is.
        // L -> points should be in the result list, but it isn't.
        diff.forEach( v -> text.append( res.contains( v ) ? "M(" : "L(" ).append( v ).append( "), " ) );

        return text.toString();
    }

    // not support visualization with multiple searching.
    private boolean visualization( List<Vector> R, List<Vector> res ) {
        BoundingBox b = new BoundingBox( R.get( 0 ), R.get( 1 ) );
        List<Line> L = new ArrayList<>( 4 );
        L.add( b.top.getSegment() );
        L.add( b.left.getSegment() );
        L.add( b.bottom.getSegment() );
        L.add( b.right.getSegment() );

        drawer.drawLines( DrawingProgram.INTERSECTION_COLOR, L  );
        drawer.drawPoints( DrawingProgram.INTERSECTION_COLOR, res );

        drawer.resetCanvas();
        drawer.draw();
        return true;
    }

    /**
     * Brute force to find points lying in the orthogonal region.
     * Time complexity: O( n ), where n is # of points.
     *
     * By the way, Brute force to find points lying in the 1D range.
     * which is in {@link RangeTree}.
     * */

    static
    List<Vector> bruteForce( List<Vector> P, List<Vector> R ) {
        List<Vector> res = new ArrayList<>();
        P.forEach( p -> new KdNode( p, KdNode.Type.LEAF ).contains( R, res ) );

        return res;
    }
}
