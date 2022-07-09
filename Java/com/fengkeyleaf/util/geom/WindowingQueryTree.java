package com.fengkeyleaf.util.geom;

/*
 * WindowingQueryTree.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/25/2022$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.util.tree.AbstractTree;
import com.fengkeyleaf.util.tree.MapTreeNode;
import com.fengkeyleaf.util.tree.RedBlackTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * class to include common data field and methods for orthogonal windowing query.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

class WindowingQueryTree extends AbstractTree<Line> {
    // build on x-coor or y-coor
    // true: build on x-coor and query horizontal segments.
    // false: build on y-coor and query vertical segments.
    final boolean isX;
    final Comparator<Vector> c;
    // only report intervals that cross R?
    public boolean isOnlyReportingCrossing;
    // orthogonal searching region, R,
    // which is used to report intervals crossing R.
    QueryVector[] ROrigin;
    // Segment set for visualization and checking.
    List<Line> I;
    // check
    List<LineNode> resP;

    WindowingQueryTree() {
        this( true );
    }

    WindowingQueryTree( boolean isX ) {
        if ( this.isX = isX ) c = Vectors::sortByX;
        else c = Vectors::sortByY;
    }

    /**
     * bucket tree to store vertical or horizontal segments ( special segments ) separately,
     * when querying vertically or horizontally.
     * It's not guaranteed that we can find all special segments with normal query operation,
     * so just handle them separately.
     * */

    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/TreeMap.html
    static class BucketTree<K, V> extends RedBlackTree<K, List<V>> {

        BucketTree( Comparator<K> comparator ) {
            super( comparator );
        }

        BucketTree() {
            this( null );
        }

        /**
         * put the value into the bucket associated with the key.
         *
         * @param k key
         * @param v value to be put into the bucket associated with the key.
         * */

        void add( K k, V v ) {
            if ( k == null || v == null )
                throw new IllegalArgumentException( "key or value is null" );

            List<V> T = get( k );

            // not found the bucket.
            if ( T == null ) {
                T = new ArrayList<>();
                T.add( v );
                put( k, T );
                return;
            }

            // find the bucket.
            T.add( v );
        }
    }

    /**
     * Get an interval layered range tree to report
     * intervals with one or both endpoints lying in R.
     * */

    IntervalLayeredRangeTree getIntervalLayeredRangeTree( List<Line> I ) {
        List<Vector> P = new ArrayList<>( I.size() * 2 + 1 );

        Lines.removeDuplicates( I, c ).forEach( i -> {
            LineNode n1 = new LineNode( i.startPoint, true, i );
            LineNode n2 = new LineNode( i.endPoint, false, i );
            // mark two endpoints from the same line.
            n1.setTwin( n2 );
            P.add( n1 );
            P.add( n2 );
        } );

        IntervalLayeredRangeTree T = new IntervalLayeredRangeTree( preprocessEndpoint( P ) );
        T.isUsedForReportingCrossing = false;
        return T;
    }

    List<Vector> preprocessEndpoint( List<Vector> P ) {
        BucketTree<Vector, LineNode> B = new BucketTree<>( c );
        P.forEach( p -> B.add( p, ( LineNode ) p ) );

        List<Vector> list = new ArrayList<>( P.size() + 1 );
        for ( MapTreeNode<Vector, List<LineNode>> n : B )
            list.add( ( ( LineNode ) n.key ).addAll( n.val ) );

        return list;
    }

    /**
     * Layered Range tree designed for orthogonal windowing query.
     * */

    class IntervalLayeredRangeTree extends LayeredRangeTree {
        // is this interval range tree used for only reporting intervals crossing R?
        boolean isUsedForReportingCrossing = true;

        // should keep duplicate points.
        IntervalLayeredRangeTree( List<Vector> P ) {
            super( P, true );
        }

        IntervalLayeredRangeTree( List<Vector> P, boolean isX ) {
            super( P, isX );
        }

        @Override
        public List<Vector> query( String[] R ) {
            return reset( query( getQueryR( R ) ) );
        }

        /**
         * report points ( if they're in leaf nodes ) lying in the searching region.
         * Will avoid added intervals and not report intervals with endpoints inside {@code R}.
         *
         * @param R searching region.
         * @param res result list.
         * */

        @Override
        void report( RangeNode n, QueryVector[] R, List<Vector> res ) {
            // n may not necessarily be a leaf node in Layered Range Tree.
            if ( n.isLeaf() &&
                    n.isContained( R ) )
                reportDuplicate( ( LineNode ) n.p, res );
        }

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
                    v.isNotAdded = false;
                }
            } );
        }

        /**
         * report points lying in the searching region.
         * Will avoid added intervals and not report intervals with endpoints inside {@code R}.
         *
         * @param i stating index.
         * @param R searching region.
         * */

        @Override
        void report( List<LayerNode> L, int i,
                     QueryVector[] R, List<Vector> res ) {
            // no nodes satisfying the searching area requirement
            if ( L == null ) return;
            assert !L.isEmpty();

            // otherwise, walk through the array to report points
            // until we reach a node lying outside the range [y:y'].
            for ( ; i < L.size(); i++ ) {
                LineNode n = ( LineNode ) L.get( i ).p;

                if ( R[ 1 ].compare( n, Vectors::sortByY, false ) >= 0 )
                    reportDuplicate( n, res );
                else break;
            }
        }

        /**
         * mark the interval hasn't been added before.
         * */

        List<Vector> reset( List<Vector> res ) {
            // only report intervals crossing R, no need to reset the status.
            if ( isUsedForReportingCrossing ) return res;

            // reset the status of reported nodes.
            res.forEach( v -> ( ( LineNode ) v ).isNotAdded = true );
            return res;
        }

        //---------------------------------------------------------------
        // Check integrity of interval layered range tree data structure.
        //---------------------------------------------------------------

        @Override
        protected List<Vector> check2D( QueryVector[] R, List<Vector> res ) {
            if ( P == null ) return res;

            assert visualization( R, res );
            assert check2D( P, R, res );

            return res;
        }

        private boolean check2D( List<Vector> P, QueryVector[] R, List<Vector> res ) {
            List<Vector> bruteForce = bruteForce( P, R );
            assert compare( res, bruteForce, "M: " ) && compare( bruteForce, res, "L: " ) : Arrays.toString( R ) + " || " + bruteForce + " || " + res;
            return true;
        }

        @Override
        List<Vector> bruteForce( List<Vector> P, QueryVector[] R ) {
            List<Vector> res = new ArrayList<>();
            List<Vector> more = new ArrayList<>();
            for ( Vector p : P ) {
                LineNode n = ( LineNode ) p;

                // check all endpoints.
                n.L.forEach( v -> {
                    // line added by the algorithm,
                    // we need to check it should be added to the result set.
                    if ( !v.isNotAdded() && new RangeNode( v ).isContained( R ) &&
                            isReportCrossing( v, ROrigin ) ) {
                        // report intervals inside R.
                        // cannot use List::contains,
                        // because it will regard two vectors with the same x-coor and y-coor as the same vector.
                        // but in this case, we wanna to compare pointer only.
                        if ( resP == null &&
                                !contains( res, v ) && !contains( res, v.twin ) )
                            res.add( v );
                        // report intervals crossing R.
                        // notice that in this case, we will query twice, -INF and +INF.
                        else if ( resP != null &&
                                !contains( resP, v ) && !contains( resP, v.twin ) )
                            res.add( v );
                    }

                    // line not added by the algorithm,
                    // we need to check it should be added to the result set.
                    if ( v.isNotAdded() && new RangeNode( v ).isContained( R ) &&
                            isReportCrossing( v, ROrigin ) ) {
                        more.add( v );
                        v.isNotAdded = false;
                    }
                } );

            }

            // reset status of nodes added by brute force checking.
            more.forEach( p -> ( ( LineNode ) p ).isNotAdded = true );
            res.addAll( more );

            return res;
        }

        static<E>
        boolean contains( List<E> L, E e ) {
            for ( E ele : L )
                if ( ele == e ) return true;

            return false;
        }

        static
        boolean compare( List<Vector> res, List<Vector> brute, String error ) {
            boolean isValid = true;

            for ( Vector r : res ) {
                LineNode n1 = ( LineNode ) r;
                boolean isNotReport = true;
                for ( Vector b : brute ) {
                    LineNode n2 = ( LineNode ) b;

                    if ( n1.twin == n2 || n1 == n2 ) {
                        isNotReport = false;
                        break;
                    }
                }

                if ( isNotReport ) {
                    System.err.println( error + n1 );
                    isValid = false;
                }
            }

            return isValid;
        }

        /**
         * Visualize 2D query operation.
         * */

        private boolean visualization( QueryVector[] RStr, List<Vector> res ) {
            BoundingBox b = BoundingBox.getBoundingBox( P, BoundingBox.OFFSET );

            List<Vector> R = new ArrayList<>( 2 );
            R.add( RStr[ 0 ].getVector( b, false ) );
            R.add( RStr[ 1 ].getVector( b, true ) );

            int size = Math.abs( ( int ) MyMath.findMaxMinInAbs( b.maxX, b.maxY, b.minX, b.minY )[ 1 ] ) * 2;
            DrawingProgram drawer = new DrawingProgram( "Visualized debugger for Interval Layered Range Tree" , size, size );

            b = new BoundingBox( R.get( 0 ), R.get( 1 ) );
            List<Line> L = new ArrayList<>( 4 );
            L.add( b.top.getSegment() );
            L.add( b.left.getSegment() );
            L.add( b.bottom.getSegment() );
            L.add( b.right.getSegment() );

            drawer.drawLines( DrawingProgram.INTERSECTION_COLOR, L );
            drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, P );
            drawer.drawPoints( DrawingProgram.INTERSECTION_COLOR, res );

            L.clear();
            P.forEach( p -> {
                LineNode n = ( LineNode ) p;

                L.add( n.l );
            } );
            drawer.drawLines( DrawingProgram.NORMAL_POLYGON_COLOR, L );

            drawer.initialize();
            return true;
        }
    }

    boolean isReportCrossing( LineNode n, QueryVector[] R ) {
        return isReportCrossing( n.l, R );
    }

    /**
     * report the interval {@code l} with endpoints inside {@code R}?
     * */

    boolean isReportCrossing( Line l, QueryVector[] R ) {
        if ( isOnlyReportingCrossing ) {
            return R[ 0 ].compare( l.startPoint, c, isX ) > 0 &&
                    R[ 1 ].compare( l.endPoint, c, isX ) < 0;
        }

        return true;
    }

    static
    boolean containsX( Line l, double x ) {
        return l.startPoint.x <= x && x <= l.endPoint.x;
    }

    static
    boolean containsY( Line l, double y ) {
        return l.startPoint.y <= y && y <= l.endPoint.y;
    }

    @Override
    public int size() {
        System.err.println( "size() is unavailable in Windowing Query tree" );
        System.exit( 1 );
        return -1;
    }

    //-------------------------------------------------------
    // Checking methods.
    //-------------------------------------------------------

    // ------------------------------------------------------>
    // stabbing query ---------------------------------------

    List<Line> check( Vector q, List<Line> res, String title ) {
        if ( I == null ) return res;

        assert visualization( q, res, title );
        assert check( q, res );
        assert check( res );
        return res;
    }

    // check duplicate
    static
    boolean check( List<Line> I ) {
        if ( I == null ) return true;

        for ( int i = 0; i < I.size(); i++ ) {
            for ( int j = 0; j < I.size(); j++ ) {
                if ( i == j ) continue;

                if ( I.get( i ) == I.get( j ) ) {
                    System.err.println( "Duplicate: " + I.get( i ) );
                    return false;
                }
            }
        }

        return true;
    }

    boolean check( Vector q, List<Line> res ) {
        // remove the query line after the visualization.
        res.remove( res.size() - 1 );

        List<Line> bruteForce = new ArrayList<>();
        I.forEach( i -> {
            if ( isX && containsX( i, q.x ) ||
                    !isX && containsY( i, q.y ) ) bruteForce.add( i );
        } );

        List<Line> diff = compare( bruteForce, res );
        if ( !diff.isEmpty() ) {
            diff.forEach( System.err::println );
            assert false;
        }

        return true;
    }

    static
    List<Line> compare( List<Line> bruteForce, List<Line> res ) {
        List<Line> diff = new ArrayList<>();

        compare( bruteForce, res, diff );
        compare( res, bruteForce, diff );
        return diff;
    }

    static
    void compare( List<Line> brute, List<Line> res, List<Line> diff ) {
        for ( Line l1 : brute ) {
            boolean isDIff = true;

            for ( Line l2 : res ) {
                if ( l1 == l2 ) {
                    isDIff = false;
                    break;
                }
            }

            if ( isDIff ) diff.add( l1 );
        }
    }

    boolean visualization( Vector q, List<Line> res, String title ) {

        List<Vector> P = new ArrayList<>( I.size() * 2 );
        I.forEach( i -> {
            P.add( i.startPoint );
            P.add( i.endPoint );
        } );
        BoundingBox b = BoundingBox.getBoundingBox( P, BoundingBox.OFFSET );

        int size = Math.abs( ( int ) MyMath.findMaxMinInAbs( b.maxX, b.maxY, b.minX, b.minY )[ 1 ] ) * 2;
        DrawingProgram drawer = new DrawingProgram( title, size, size );

        List<Line> L = new ArrayList<>( 4 );
        L.add( b.top.getSegment() );
        L.add( b.left.getSegment() );
        L.add( b.bottom.getSegment() );
        L.add( b.right.getSegment() );

        drawer.drawLines( DrawingProgram.NORMAL_POLYGON_COLOR, L );
        drawer.drawLines( DrawingProgram.NORMAL_POLYGON_COLOR, I );
        drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, P );

        List<Vector> resPoints = new ArrayList<>( res.size() * 2 + 1 );
        res.forEach( l -> {
            resPoints.add( l.startPoint );
            resPoints.add( l.endPoint );
        } );
        res.add( isX ? new Line( new Vector( q.x, b.minY ), new Vector( q.x, b.maxY ) ) :
                new Line( new Vector( b.minX, q.y ), new Vector( b.maxX, q.y ) ) );

        drawer.drawLines( DrawingProgram.INTERSECTION_COLOR, res );
        drawer.drawPoints( DrawingProgram.INTERSECTION_COLOR, resPoints );

        drawer.initialize();
        return true;
    }

    // ------------------------------------------------------>
    // windowing query ---------------------------------------

    List<Line> check( List<Vector> R, List<Line> res, String title ) {
        assert visualization( I, R, res, title );
        assert check( I, R, res );

        return res;
    }

    boolean check( List<Line> I, List<Vector> R, List<Line> res ) {
        List<Line> diff = compare( bruteForce( I, R ), res );

        if ( !diff.isEmpty() ) {
            diff.forEach( System.err::println );
            assert false : res + " | " + diff;
        }

        return true;
    }


    private List<Line> bruteForce( List<Line> I, List<Vector> R ) {
        List<Line> res = new ArrayList<>();
        if ( I == null ) return res;

        Line l = new Line( R.get( 0 ), new Vector( R.get( 0 ).x, R.get( 1 ).y ) );
        Line b = new Line( R.get( 0 ), new Vector( R.get( 1 ).x, R.get( 0 ).y ) );
        I.forEach( i -> {
            if ( isX && l.intersect( i )[ 0 ] != null && isReportCrossing( i, RangeTree.getQueryR( R ) ) )
                res.add( i );

            if ( !isX && b.intersect( i )[ 0 ] != null && isReportCrossing( i, RangeTree.getQueryR( R ) ) )
                res.add( i );
        } );

        return res;
    }

    static
    boolean visualization( List<Line> I, List<Vector> R,
                           List<Line> res, String title ) {

        if ( I == null || I.isEmpty() ) return true;

        List<Vector> P = new ArrayList<>( I.size() * 2 );
        I.forEach( i -> {
            P.add( i.startPoint );
            P.add( i.endPoint );
        } );
        BoundingBox b = BoundingBox.getBoundingBox( P, BoundingBox.OFFSET );

        int size = Math.abs( ( int ) MyMath.findMaxMinInAbs( b.maxX, b.maxY, b.minX, b.minY )[ 1 ] ) * 2;
        DrawingProgram drawer = new DrawingProgram( title , size, size );

        b = new BoundingBox( R.get( 0 ), R.get( 1 ) );
        List<Line> L = new ArrayList<>( 4 );
        L.add( b.top.getSegment() );
        L.add( b.left.getSegment() );
        L.add( b.bottom.getSegment() );
        L.add( b.right.getSegment() );

        drawer.drawLines( DrawingProgram.INTERSECTION_COLOR, L );
        drawer.drawLines( DrawingProgram.NORMAL_POLYGON_COLOR, I );
        drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, P );
        drawer.drawLines( DrawingProgram.INTERSECTION_COLOR, res );

        P.clear();
        res.forEach( l -> {
            P.add( l.startPoint );
            P.add( l.endPoint );
        } );
        drawer.drawPoints( DrawingProgram.INTERSECTION_COLOR, P );

        drawer.initialize();
        return true;
    }
}
