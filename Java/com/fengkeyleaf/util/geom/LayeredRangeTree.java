package com.fengkeyleaf.util.geom;

/*
 * LayeredRangeTree.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/3/2022$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of layered range tree with fractional cascading,
 * only support 2d for now.
 * Time complexity: O(logn + k),
 * where n is # of points and k is # of reported ones.
 *
 * Note that this class also has the ability to query like a normal range tree.
 * @see RangeTree
 *
 * Theorem 5.11
 * Let P be a set of n points in d-dimensional space, with d >= 2.
 * A layered range tree for P uses O( n ( logn ) ^ ( d−1 ) ) storage
 * and it can be constructed in ( n ( logn ) ^ ( d−1 ) ) time.
 * With this range tree one can report the points in P
 * that lie in a rectangular query range in O(( logn ) ^ ( d−1 ) n + k) time,
 * where k is the number of reported points
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class LayeredRangeTree extends RangeTree {

    // this constructor will not remove duplicate points.
    LayeredRangeTree( List<com.fengkeyleaf.util.geom.Vector> P, boolean isX ) {
        // build the normal range tree.
        super( init( P ), isX );
        // build associated fractional cascading structure.
        if ( root != null ) build( root );
    }

    /**
     * build a 2D layered range tree.
     *
     * @param P A set of points P.
     *          Duplicate points allowed, they will be removed in the process.
     * */

    public LayeredRangeTree( List<com.fengkeyleaf.util.geom.Vector> P ) {
        // build the normal range tree.
        super( P );
        // build associated fractional cascading structure.
        if ( root != null ) build( root );
    }

    List<LayerNode> build( RangeNode n ) {
        if ( n.isLeaf() ) {
            List<LayerNode> A = new ArrayList<>();
            A.add( new LayerNode( n.p ) );
            return A;
        }

        // merge-sort like process to build fractional cascading structure.
        return n.A = merge( build( n.left ), build( n.right ) );
    }

    /**
     * merge-sort like process to build fractional cascading.
     * */

    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ListIterator.html
    private static
    List<LayerNode> merge( List<LayerNode> L, List<LayerNode> R ) {
        List<LayerNode> A = new ArrayList<>( L.size() + R.size() );

        // merging step
        int idxL = 0, idxR = 0;
        while ( idxL < L.size() && idxR < R.size() ) {
            LayerNode l = L.get( idxL );
            LayerNode r = R.get( idxR );

            // element in the right list are less
            if ( Vectors.sortByY( l.p, r.p ) >= 0 )
                A.add( new LayerNode( r.p, idxL, idxR++, L, R ) );
            // element in the left list are less
            else
                A.add( new LayerNode( l.p, idxL++, idxR, L, R ) );
        }

        // concatenate remained elements in either left list or right list
        if ( idxL >= L.size() ) {
            while ( idxR < R.size() )
                A.add( new LayerNode( R.get( idxR ).p, -1, idxR++, null, R ) );
        }
        else {
            while ( idxL < L.size() )
                A.add( new LayerNode( L.get( idxL ).p, idxL++, -1, L, null ) );
        }

        assert check( A );
        return A;
    }

    public List<com.fengkeyleaf.util.geom.Vector> query( List<com.fengkeyleaf.util.geom.Vector> R ) {
        return query( vectorToString( R ) );
    }

    /**
     * search which points lying in the orthogonal region, R.
     * This method will take advantage of fractional cascading.
     * Note that this method has the ability to query infinite.
     *
     * @param R orthogonal searching area in the form of string,
     *         [x:x'] x [y:y']. It is not degenerated to a line or a point.
     *         [ ( x1, y1 ), ( x2, y2 ) ] = [ bottomLeft, topRight ].
     * */

    public List<com.fengkeyleaf.util.geom.Vector> query( String[] R ) {
        assert R.length == 4;
        return query( getQueryR( R ) );
    }

    // Reference resource: http://www.cs.uu.nl/geobook/
    List<com.fengkeyleaf.util.geom.Vector> query( QueryVector[] R ) {
        if ( isInvalidR( R, c, isX ) )
            throw new IllegalArgumentException( "Illegal orthogonal searching areas" );

        // 1. vSplit <- FINDSPLITNODE(T, x, x')
        RangeNode vSplit = findSplitNode( R );
        List<com.fengkeyleaf.util.geom.Vector> res = new ArrayList<>();
        if ( vSplit == null ) return check2D( R, res );

        // 2. if vsplit is a leaf
        // 3. then Check if the point stored at nSplit must be reported.
        if ( vSplit.isLeaf() ) {
            report( vSplit, R, res );
            return check2D( R, res );
        }

        // find the node, whose y-coor is equal to or greater than y,
        // in the associated factional cascading structure.
        int idxSplit = ceilingBound( vSplit.A, R[ 0 ] );
        // no points satisfying the requirement for y-coor.
        if ( idxSplit < 0 ) return check2D( R, res );

        // While we search further with x and x' in the main tree,
        // we keep track of the entry in the associated arrays
        // whose y-coordinate is the smallest one larger than or equal to y.
        assert idxSplit >= 0 && idxSplit < vSplit.A.size() : vSplit;
        LayerNode n = vSplit.A.get( idxSplit );

        // 4. else (* Follow the path to x and
        // call 1DRANGEQUERY on the subtrees right of the path. * )
        // 5. v <- lc(vSplit)
        RangeNode v = vSplit.left;
        if ( n.L != null ) n = n.L.get( n.left );
        // 6. while v is not a leaf
        while ( vSplit.A.get( idxSplit ).L != null &&
                !v.isLeaf() ) {
            // 7. do if x <= xv
            if ( R[ 0 ].compare( v.p, c, isX ) <= 0 ) {
                // 8. then report the points stored in A(rc(v))
                // whose y-coordinate is in the range [y : y']
                report( n.R, n.right, R, res );
                // 9. v <- lc(v)
                v = v.left;

                // left pointer is null,
                // meaning rest of nodes in the right subtree
                // don't lie in the range [y:y'].
                // no need to continue searching.
                if ( n.L == null ) break;
                n = n.L.get( n.left );
                continue;
            }

            // 10. else v <- rc(v)
            v = v.right;

            // right pointer is null,
            // meaning rest of nodes in the left subtree
            // don't lie in the range [y:y'].
            // no need to continue searching.
            if ( n.R == null ) break;
            n = n.R.get( n.right );
        }
        // 11. Check if the point stored at v must be reported.
        report( v, R, res );

        n = vSplit.A.get( idxSplit );
        // 12. Similarly, follow the path from rc(vSplit) to x',
        // call 1DRANGEQUERY with the range [y : y']
        // on the associated structures of subtrees left of the path,
        // and check if the point stored at the leaf where the path ends must be reported.
        // 12.1. v <- rc(vSplit)
        v = vSplit.right;
        if ( n.R != null ) n = n.R.get( n.right );
        // 12.2. while v is not a leaf
        while ( vSplit.A.get( idxSplit ).R != null &&
                !v.isLeaf() ) {
            // 12.3. do if xv < x'
            if ( R[ 1 ].compare( v.p, c, isX ) > 0 ) {
                // 12.4. then report the points stored in A(lc(v))
                // whose y-coordinate is in the range [y : y']
                report( n.L, n.left, R, res );
                // 12.5. v <- rc(v)
                v = v.right;

                // no need to look into right subtree.
                if ( n.R == null ) break;
                n = n.R.get( n.right );
                continue;
            }

            // 12.6 else v <- lc(v)
            v = v.left;

            // no need to look into left subtree.
            if ( n.L == null ) break;
            n = n.L.get( n.left );
        }
        // 12.7 Check if the point stored at v must be reported.
        report( v, R, res );

        return check2D( R, res );
    }


    /**
     * Find the first point that is equal or greater than
     * the pont of querying area, {@code n}, in this list. {@code A}.
     * */

    static
    int ceilingBound( List<LayerNode> A, QueryVector n ) {
        int l = 0, r = A.size() - 1;

        while ( l <= r ) {
            int mid = ( r - l ) / 2 + l;

            int res = n.compare( A.get( mid ).p, Vectors::sortByY, false );
            // found duplicate
            if ( res == 0 )
                return mid;
            // look for lower
            else if ( res < 0 )
                r = mid - 1;
            // look for higher
            else
                l = mid + 1;
        }

        return l == A.size() ? -1 : l;
    }

    /**
     * report points lying in the searching region.
     *
     * @param i stating index.
     * @param R searching region.
     * */

    void report( List<LayerNode> L, int i,
                 QueryVector[] R, List<Vector> res ) {
        // no nodes satisfying the searching area requirement
        if ( L == null ) return;
        assert !L.isEmpty();

        // otherwise, walk through the array to report points
        // until we reach a node lying outside the range [y:y'].
        for ( ; i < L.size(); i++ ) {
            if ( R[ 1 ].compare( L.get( i ).p, Vectors::sortByY, false ) >= 0 )
                res.add( L.get( i ).p );
            else break;
        }
    }

    //-------------------------------------------------------
    // Check integrity of layered range tree data structure.
    //-------------------------------------------------------

    // We mainly check the structure of fractional cascading.
    private static
    boolean check( List<LayerNode> A ) {
        // check current level.
        for ( int i = 0; i < A.size() - 1; i++ )
            assert Vectors.sortByY( A.get( i ).p, A.get( i + 1 ).p ) <= 0;

        // check the relationship between current level and its bottom level.
        A.forEach( n -> {
            assert check( n );
        } );

        return true;
    }

    private static
    boolean check( LayerNode n ) {
        return ( n.L == null || // no left part.
                    ( n.left == 0 || // no previous element.
                            // otherwise, the next must be equal or larger than the previous one.
                            Vectors.sortByY( n.L.get( n.left - 1 ).p, n.L.get( n.left ).p ) <= 0 ) )
                // similar to the right part.
                && ( n.R == null ||
                      ( n.right == n.R.size() - 1 ||
                            Vectors.sortByY( n.R.get( n.right ).p, n.R.get( n.right + 1 ).p ) <= 0 ) );
    }
}
