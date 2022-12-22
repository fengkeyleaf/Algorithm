package com.fengkeyleaf.util.geom;

/*
 * RangeTree.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 basic operations on 5/1/2022$
 *     $1.1 query infinite on 5/10/2022$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.lang.MySorts;
import com.fengkeyleaf.util.MyCollections;
import com.fengkeyleaf.util.tree.AbstractTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Data structure of range tree, only support 2d for now.
 * Time complexity: O( logn * logn + k ),
 * where n is # of points and k is # of reported ones.
 *
 * Note that this range tree has the ability
 * to query positive infinite or negative infinite.
 *
 * Theorem 5.8
 * Let P be a set of n points in the plane.
 * A range tree for P uses O(nlogn) storage and
 * can be constructed in O(nlogn) time.
 * By querying this range tree one can report the points in P
 * that lie in a rectangular query range in O( (logn) ^ 2 + k) time,
 * where k is the number of reported points.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class RangeTree extends AbstractTree<Vector> {
    RangeNode root;
    // Is this tree built on x-coor?
    final boolean isX;
    final Comparator<Vector> c;
    // point set for visualization and checking.
    List<Vector> P;

    // this constructor will be called on
    // to build top level tree ( built on x-coor ),
    // and bottom level tree( built on y-ccor ).
    RangeTree( List<Vector> P, boolean isX ) {
        if ( this.isX = isX ) c = Vectors::sortByX;
        else c = Vectors::sortByY;

        // build range tree.
        root = P == null || P.isEmpty() ? null : build( this.P = P );

        // P has only one point,
        // free up the space for sorted list based on y-coor.
        if ( root != null ) root.L = null;

        assert isBST() : isX + " | " + P;
        assert !isX || root == null || isTopLevel( root );
    }

    /**
     * build a 2D range tree with x-coor as the top level.
     *
     * @param P A set of points P.
     *          Duplicate points allowed, they will be removed in the process.
     * */

    public RangeTree( List<Vector> P ) {
        // presort points by x-coor and remove duplicates.
        // And this is the first sorted list we maintain
        // during the construction process.
        this( init( P ), true );
    }

    static
    List<Vector> init( List<Vector> P ) {
        // presort points by x-coor and remove duplicates.
        // And this is the first sorted list we maintain
        // during the construction process.
        return MyCollections.removeDuplicates( P, Vectors::sortByX );
    }

    /**
     * range tree node.
     * */

    static class RangeNode {
        final Vector p;
        RangeNode left;
        RangeNode right;
        // associated structure
        RangeTree assoc;
        // sorted list for the merge process.
        // this is the key to build the range tree bottom-up.
        // And this is the second sorted list we maintain
        // during the construction process.
        List<Vector> L;
        // associated structure with fractional cascading.
        List<LayerNode> A;

        RangeNode( Vector p ) {
            this( p, null, null );
        }

        RangeNode( Vector p, RangeNode l, RangeNode r ) {
            this.p = p;
            left = l;
            right = r;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }

        /**
         * initialize the associated structure for this node.
         * */

        RangeNode initAssoc( boolean isX ) {
            if ( !isX ) return this;

            // associated structure for an internal node.
            if ( left != null && right != null )
                return initAssoc( left.L, right.L );

            // associated structure for a leaf node.
            assert isLeaf();
            assert L == null;
            L = new ArrayList<>( 1 );
            L.add( p );
            assoc = new RangeTree( L, false );

            return this;
        }

        RangeNode initAssoc( List<Vector> L, List<Vector> R ) {
            // merge-sort like process to get a presorted list by y-coor.
            this.L = MySorts.merge( L, R, Vectors::sortByY );
            // free up space for the merged lists sorted by y-coor.
            left.L = null;
            right.L = null;

            // build the bottom-level range tree.
            assoc = new RangeTree( this.L, false );
            return this;
        }

        /**
         * does the searching area contain this leaf node?
         * Note that no need to consider degenerate case,
         * because this is a kind of brute force with only one query point.
         *
         * @param R orthogonal searching area.
         * */

        boolean isContained( QueryVector[] R ) {
            assert isLeaf();

            return R[ 0 ].compareX( p.x ) <= 0 && R[ 1 ].compareX( p.x ) >= 0 &&
                    R[ 0 ].compareY( p.y ) <= 0 && R[ 1 ].compareY( p.y ) >= 0;
        }

        // brute force checking.
        boolean isContained( List<Vector> R ) {
            assert isLeaf();

            return R.get( 0 ).x <= p.x && p.x <= R.get( 1 ).x &&
                    R.get( 0 ).y <= p.y && p.y <= R.get( 1 ).y;
        }

        @Override
        public String toString() {
            return p.toString();
        }

    }

    /**
     * layered range tree node.
     * */

    static class LayerNode {
        final Vector p;
        // left pointer pointing to the left fractional cascading structure.
        int left;
        // right pointer pointing to the left fractional cascading structure.
        int right;
        // left fractional cascading structure.
        List<LayerNode> L;
        // right fractional cascading structure.
        List<LayerNode> R;

        LayerNode( Vector p ) {
            this( p, -1, -1, null, null );
        }

        LayerNode( Vector p, int l, int r,
                   List<LayerNode> L, List<LayerNode> R ) {

            this.p = p;
            left = l;
            right = r;
            this.L = L;
            this.R = R;
        }

        @Override
        public String toString() {
            return p.toString();
        }
    }

    /**
     * build the range tree.
     *
     * Reference resource:
     * @see <a href="http://www.cs.uu.nl/geobook/">Computational Geometry: Algorithms and Applications(Third Edition)</a>
     *
     * @param P A set of points P
     * */

    // Algorithm BUILD2DRANGETREE as it is described will not result in the
    // optimal construction time of O(nlogn). To obtain this we have to be a bit
    // careful. Constructing a binary search tree on an unsorted set of n keys takes
    // O(nlogn) time. This means that constructing the associated structure in line 1
    // would take O(nlogn) time. But we can do better if the points in Py are presorted
    // on y-coordinate; then the binary search tree can be constructed bottom-up in
    // linear time. During the construction algorithm we therefore maintain the set of
    // points in two lists, one sorted on x-coordinate and one sorted on y-coordinate.
    // This way the time we spend at a node in the main tree T is linear in the size of
    // its canonical subset. This implies that the total construction time is the same as
    // the amount of storage, namely O(nlogn). Since the presorting takes O(nlogn)
    // time as well, the total construction time is again O(nlogn).

    // Algorithm BUILD2DRANGETREE(P)
    // Input. A set P of points in the plane.
    // Output. The root of a 2-dimensional range tree.
    private RangeNode build( List<Vector> P ) {
        // Construct the associated structure as a bottom-up process.
        // So do it at the end of the recursion.
        // 1. Construct the associated structure:
        // Build a binary search tree T_assoc
        // on the set Py of y-coordinates of the points in P.
        // Store at the leaves of T_assoc
        // not just the y-coordinate of the points in Py,
        // but the points themselves.

        // 2. if P contains only one point
        // 3. then Create a leaf n storing this point,
        // and make T_assoc the associated structure of n.
        if ( P.size() == 1 )
            return new RangeNode( P.get( 0 ) ).initAssoc( isX );

        // 4. else Split P into two subsets;
        // one subset PLeft contains the points with x-coordinate
        // less than or equal to xmid, the median x-coordinate,
        // and the other subset PRight contains the points with x-coordinate larger than xmid.
        int mid = P.size() % 2 == 0 ? P.size() / 2 - 1 : P.size() / 2;
        List<Vector> PLeft = P.subList( 0, mid + 1 );
        List<Vector> PRight = P.subList( mid + 1, P.size() );

        // 5. vLeft <- BUILD2DRANGETREE(PLeft)
        RangeNode vLeft = build( PLeft );
        // 6. vRight <- BUILD2DRANGETREE(PRight)
        RangeNode vRight = build( PRight );

        // 7. Create a node n storing xmid,
        // make nLeft the left child of n, make nRight the right child of n,
        // and make T_assoc the associated structure of n.
        // 8. return n
        return new RangeNode( P.get( mid ), vLeft, vRight ).initAssoc( isX );
    }

    /**
     * search to find which points lying in the orthogonal region, R.
     *
     * @param R orthogonal searching area, [x:x'] x [y:y'].
     *          It is not degenerated to a line or a point.
     *          [ ( x1, y1 ), ( x2, y2 ) ] = [ bottomLeft, topRight ].
     * */

    public List<Vector> query2D( List<Vector> R ) {
        return query2D( vectorToString( R ) );
    }

    static
    boolean isInvalidR( QueryVector[] R, Comparator<Vector> c, boolean isX ) {
        return QueryVector.compare( R[ 0 ], R[ 1 ], c, isX ) >= 0;
    }

    static
    String[] vectorToString( List<Vector> R ) {
        assert R.size() == 2;

        String[] RStr = new String[ 4 ];
        RStr[ 0 ] = String.valueOf( R.get( 0 ).x );
        RStr[ 1 ] = String.valueOf( R.get( 0 ).y );
        RStr[ 2 ] = String.valueOf( R.get( 1 ).x );
        RStr[ 3 ] = String.valueOf( R.get( 1 ).y );

        return RStr;
    }

    /**
     * search which points lying in the orthogonal region, R.
     * Note that this method has the ability to query infinite.
     *
     * @param R orthogonal searching area in the form of string,
     *          [x:x'] x [y:y']. It is not degenerated to a line or a point.
     *          [ ( x1, y1 ), ( x2, y2 ) ] = [ bottomLeft, topRight ].
     * */

    public List<Vector> query2D( String[] R ) {
        assert R.length == 4;
        return query2D( getQueryR( R ) );
    }

    static
    QueryVector[] getQueryR( String[] R ) {
        QueryVector[] queryR = new QueryVector[ 2 ];
        queryR[ 0 ] = paraphraseNumber( R[ 0 ], R[ 1 ] );
        queryR[ 1 ] = paraphraseNumber( R[ 2 ], R[ 3 ] );

        return queryR;
    }

    static
    QueryVector[] getQueryR( List<Vector> R ) {
        return getQueryR( vectorToString( R ) );
    }

    static final String NEG_INF = "-INF";
    static final String[] POS_INFS = new String[] { "INF", "+INF" };

    /**
     * numbers in the form of string => QueryNode
     * */

    static
    QueryVector paraphraseNumber( String xStr, String yStr ) {
        boolean isPosInfX = true;
        Double x = null;
        if ( xStr.equals( NEG_INF ) )
            isPosInfX = false;
        else if ( !( xStr.equals( POS_INFS[ 0 ] ) || xStr.equals( POS_INFS[ 1 ] ) ) )
            x = Double.parseDouble( xStr );

        boolean isPosInfY = true;
        Double y = null;
        if ( yStr.equals( NEG_INF ) )
            isPosInfY = false;
        else if ( !( yStr.equals( POS_INFS[ 0 ] ) || yStr.equals( POS_INFS[ 1 ] ) ) )
            y = Double.parseDouble( yStr );

        // real point.
        if ( x != null && y != null )
            return new QueryVector( new Vector( x, y ) );

        // conceptual point.
        return new QueryVector( null, x, isPosInfX, y, isPosInfY );
    }

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm 2DRANGEQUERY(T, [x : x'] x [y : y'])
    // Input. A 2-dimensional range tree T and a range [x : x'] x [y : y'].
    // Output. All points in T that lie in the range.
    List<Vector> query2D( QueryVector[] R ) {
        if ( isInvalidR( R, c, isX ) )
            throw new IllegalArgumentException( "Illegal orthogonal searching areas" );

        // 1. vSplit <- FINDSPLITNODE(T, x, x')
        RangeNode vSplit = findSplitNode( R );
        List<Vector> res = new ArrayList<>();
        if ( vSplit == null ) return res;

        // 2. if vsplit is a leaf
        // 3. then Check if the point stored at nSplit must be reported.
        if ( vSplit.isLeaf() ) {
            report( vSplit, R, res );
            return check2D( R, res );
        }

        // 4. else (* Follow the path to x and
        // call 1DRANGEQUERY on the subtrees right of the path. * )
        // 5. v <- lc(vSplit)
        RangeNode v = vSplit.left;
        // 6. while v is not a leaf
        while ( !v.isLeaf() ) {
            // 7. do if x <= xv
            if ( R[ 0 ].compare( v.p, c, isX ) <= 0 ) {
                // 8. then 1DRANGEQUERY(Tassoc(rc(n)), [y : y'])
                res.addAll( v.right.assoc.query1D( R ) );
                // 9. v <- lc(v)
                v = v.left;
                continue;
            }

            // 10. else v <- rc(v)
            v = v.right;
        }
        // 11. Check if the point stored at v must be reported.
        report( v, R, res );

        // 12. Similarly, follow the path from rc(vSplit) to x',
        // call 1DRANGEQUERY with the range [y : y']
        // on the associated structures of subtrees left of the path,
        // and check if the point stored at the leaf where the path ends must be reported.
        // 12.1. v <- rc(vSplit)
        v = vSplit.right;
        // 12.2. while v is not a leaf
        while ( !v.isLeaf() ) {
            // 12.3. do if xv < x'
            if ( R[ 1 ].compare( v.p, c, isX ) > 0 ) {
                // 12.4. then 1DRANGEQUERY(Tassoc(lc(n)), [y : y'])
                res.addAll( v.left.assoc.query1D( R ) );
                // 12.5. v <- rc(v)
                v = v.right;
                continue;
            }

            // 12.6 else v <- lc(v)
            v = v.left;
        }
        // 12.7 Check if the point stored at v must be reported.
        report( v, R, res );

        return check2D( R, res );
    }

    /**
     * report points ( if they're in leaf nodes ) lying in the searching region.
     *
     * @param R searching region.
     * @param res result list.
     * */

    void report( RangeNode n, QueryVector[] R, List<Vector> res ) {
        // n may not necessarily be a leaf node in Range Tree.
        if ( n.isLeaf() && n.isContained( R ) )
            res.add( n.p );
    }

    RangeNode findSplitNode( List<Vector> R ) {
        return findSplitNode( getQueryR( vectorToString( R ) ) );
    }

    /**
     * find Split Node.
     * Note that this method has the ability to query infinite.
     * */

    // Reference resource: http://www.cs.uu.nl/geobook/
    // FINDSPLITNODE(T,x,x')
    // Input. A tree T and two values x and x' with x <= x'.
    // Output. The node ν where the paths to x and x' split,
    // or the leaf where both paths end.
    RangeNode findSplitNode( QueryVector[] R ) {
        if ( root == null ) return null;

        // 1. ν <- root(T)
        RangeNode v = root;

        // 2. while ν is not a leaf and (x' <= xν or x > xν )
        while ( isNotSplit( v, R ) ) {
            // 3. do if x' <= xν
            // Note that we already update the intersection point
            // after executing isNotSplit(). ( SegmentRangeTree limited )
            if ( R[ 1 ].compare( v.p, c, isX ) <= 0 )
                // 4. then ν <- lc(ν)
                v = v.left;
                // 5. else ν <- rc(ν)
            else v = v.right;
        }

        // 6. return ν
        return v;
    }

    boolean isNotSplit( RangeNode v, QueryVector[] R ) {
        return !v.isLeaf() &&
                    ( isOutOfRangeLeft( v, R ) ||
                        isOutOfRangeRight( v, R ) );
    }

    boolean isOutOfRangeLeft( RangeNode v, QueryVector[] R ) {
        return R[ 1 ].compare( v.p, c, isX ) <= 0;
    }

    boolean isOutOfRangeRight( RangeNode v, QueryVector[] R ) {
        return R[ 0 ].compare( v.p, c, isX ) > 0;
    }

    List<Vector> query1D( List<Vector> R ) {
        return query1D( getQueryR( vectorToString( R ) ) );
    }

    /**
     * query 1D.
     * Note that this method has the ability to query infinite.
     * */

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm 1DRANGEQUERY(T, [x : x'])
    // Input. A binary search tree T and a range [x : x'].
    // Output. All points stored in T that lie in the range.
    List<Vector> query1D( QueryVector[] R ) {
        // 1. vSplit <- FINDSPLITNODE(T,x,x')
        RangeNode vSplit = findSplitNode( R );
        List<Vector> res = new ArrayList<>();
        if ( vSplit == null ) return null;

        // 2. if vSplit is a leaf
        // 3. then Check if the point stored at vSplit must be reported.
        if ( vSplit.isLeaf() ) {
            report( vSplit, R, res );
            assert check1D( R, res );
            return res;
        }

        // 4. else (* Follow the path to x and report the points in subtrees right of the path. *)
        // 5. v <- lc(vSplit)
        RangeNode v = vSplit.left;
        // 6. while v is not a leaf
        while ( !v.isLeaf() ) {
            // 7. do if x <= vx
            if ( !isOutOfRangeRight( v, R ) ) {
                // 8. then REPORTSUBTREE(rc(v))
                report( v.right, res );
                // 9. v <- lc(v)
                v = v.left;
                continue;
            }

            // 10. else v <- rc(v)
            v = v.right;
        }
        // 11. Check if the point stored at the leaf v must be reported.
        report( v, R, res );

        // 12. Similarly, follow the path to x',
        // report the points in subtrees left of the path,
        // and check if the point stored at the leaf
        // where the path ends must be reported.
        // 12.1. v <- lc(vSplit)
        v = vSplit.right;
        // 12.2. while v is not a leaf
        while ( !v.isLeaf() ) {
            // 12.3. do if vx < x'
            if ( !isOutOfRangeLeft( v, R ) ) {
                // 12.4. then REPORTSUBTREE(lc(v))
                report( v.left, res );
                // 12.5. v <- rc(v)
                v = v.right;
                continue;
            }

            // 12.6. else v <- lc(v)
            v = v.left;
        }
        // 12.7. Check if the point stored at the leaf v must be reported.
        report( v, R, res );

        assert check1D( R, res );
        return res;
    }

    /**
     * report all leaf nodes starting at the node, {@code n}.
     * */

    // report subtree
    void report( RangeNode n, List<Vector> res ) {
        if ( n.isLeaf() ) {
            res.add( n.p );
            return;
        }

        report( n.left, res );
        report( n.right, res );
    }

    @Override
    public int size() {
        System.err.println( "size() is unavailable in range tree" );
        System.exit( 1 );
        return -1;
    }

    //-------------------------------------------------------
    // Check integrity of range tree data structure.
    //-------------------------------------------------------

    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since order is strict
    private boolean isBST() {
        return isBST( root, null, null );
    }

    // Reference resource: https://algs4.cs.princeton.edu/home/
    // is the tree rooted at x a BST with all keys strictly between min and max
    // (if min or max is null, treat as empty constraint)
    // Credit: Bob Dondero's elegant solution
    private boolean isBST( RangeNode x, Vector min, Vector max ) {
        if ( x == null ) return true;

        if ( min != null && c.compare( x.p, min ) < 0 ) {
            System.err.println( "Min: " + x + " | " + min );
            return false;
        }

        if ( max != null && c.compare( x.p, max ) > 0 ) {
            System.err.println( "Max: " + x + " | " + max );
            return false;
        }

        // make sure that free up the space for the merge process.
        assert x.L == null;

        return isBST( x.left, min, x.p ) && // check left subtree.
                    isBST( x.right, x.p, max ); // check right subtree.
    }

    private boolean isTopLevel( RangeNode n ) {
        if ( n.isLeaf() ) return true;

        // every structure should have associated structure,
        // except for the bottom level.
        assert n.assoc != null;

        isTopLevel( n.left );
        isTopLevel( n.right );

        return true;
    }

    /**
     * check 1D query operation.
     * */

    boolean check1D( QueryVector[] R, List<Vector> res ) {
        List<Vector> diff = MyCollections.compare( bruteForce( P, R ), res, c );

        if ( !diff.isEmpty() ) {
            System.out.println( KdTree.compare( res, diff ) );
            assert false : bruteForce( P, R ) + " | " + res + " || " + Arrays.toString( R ) + " | " + P;
        }

        return true;
    }

    List<Vector> bruteForce( List<Vector> P, QueryVector[] R ) {
        List<Vector> res = new ArrayList<>();
        P.forEach( p -> {
            if ( new RangeNode( p ).isContained( R ) )
                res.add( p );
        } );

        return res;
    }

    /**
     * Brute force to find points lying in the 1D range.
     * Time complexity: O( n ), where n is # of points.
     *
     * By the way, Brute force to find points lying in the orthogonal region.
     * which is in {@link KdTree}.
     * */

    // not tested.
    static
    List<Vector> bruteForce( List<Vector> P, List<Vector> R, Comparator<Vector> c ) {
        List<Vector> res = new ArrayList<>();
        P.forEach( p -> {
            if ( c.compare( R.get( 0 ), p ) <= 0 &&
                    c.compare( p, R.get( 1 ) ) <= 0 )
                res.add( p );
        } );

        return res;
    }

    /**
     * check 2D query operation.
     * */

    protected List<Vector> check2D( QueryVector[] R, List<Vector> res ) {
        assert visualization( R, res );
        assert check2D( P, R, res );

        return res;
    }

    private boolean check2D( List<Vector> P, QueryVector[] R, List<Vector> res ) {
        List<Vector> diff = MyCollections.compare( bruteForce( P, R ), res, c );

        if ( !diff.isEmpty() ) {
            System.out.println( KdTree.compare( res, diff ) );
            assert false : Arrays.toString( R );
        }

        return true;
    }

    /**
     * Visualize 2D query operation.
     * */

    private boolean visualization( QueryVector[] RStr, List<Vector> res ) {
        BoundingBox b = BoundingBox.getBox( P, BoundingBox.OFFSET );

        List<Vector> R = new ArrayList<>( 2 );
        R.add( RStr[ 0 ].getVector( b, false ) );
        R.add( RStr[ 1 ].getVector( b, true ) );

        int size = Math.abs( ( int ) MyMath.findMaxMinInAbs( b.maxX, b.maxY, b.minX, b.minY )[ 1 ] ) * 2;
        DrawingProgram drawer = new DrawingProgram( "Visualized debugger for Range Tree" , size, size );

        b = new BoundingBox( R.get( 0 ), R.get( 1 ) );
        List<Line> L = new ArrayList<>( 4 );
        L.add( b.top.getSegment() );
        L.add( b.left.getSegment() );
        L.add( b.bottom.getSegment() );
        L.add( b.right.getSegment() );

        drawer.drawLines( DrawingProgram.INTERSECTION_COLOR, L );
        drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, P );
        drawer.drawPoints(  DrawingProgram.INTERSECTION_COLOR, res );

        drawer.initialize();
        return true;
    }
}
