package com.fengkeyleaf.util.geom;

/*
 * SearchStructure.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 10/19/2021$
 */

import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.util.graph.DAG;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of Search Structure, SS, tree-like DGA, for point location.
 * Left child of pi is the left trapezoid.
 * Right child of qi is the right Trapezoid.
 * Left child of si is the top trapezoid.
 * Right child of si is the bottom trapezoid.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class SearchStructure extends DAG<SearchVertex> {
    private SearchVertex root = null;
    public Trapezoid boundingBox;
    private final Face face;
    private final List<SearchVertex> searchPath = new ArrayList<>();
    private final List<SearchVertex> xNodePs = new ArrayList<>();
    private final List<SearchVertex> xNodeQs = new ArrayList<>();
    private final List<SearchVertex> yNodes = new ArrayList<>();
    private final List<SearchVertex> leaves = new ArrayList<>();

    SearchStructure( SearchVertex R ) {
        root = R;

        try {
            boundingBox = ( Trapezoid ) R.trapezoid.clone();
        } catch ( CloneNotSupportedException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        face = boundingBox.getDCEL();
    }

    void setRoot( SearchVertex root ) {
        this.root = root;
    }

    public static
    String getSearchPathString( List<SearchVertex> searchPath ) {
        StringBuilder path = new StringBuilder();
        searchPath.forEach( vertex -> {
            path.append( vertex.name ).append( " " );
        } );

        return path.toString();
    }

    /**
     * set Trapezoids' leafIDs in pre-traversal ordering
     * */

    public void setLeafIDs() {
        List<SearchVertex> list = getAllLeafNodes();

        int ID = 1;
        for ( SearchVertex vertex : list ) {
            if ( vertex.trapezoid.leafID < 0 ) {
                vertex.trapezoid.leafID = ID++;
                vertex.name = "T" + vertex.trapezoid.leafID;
            }
        }
    }

    public List<SearchVertex> getAllXNodePs() {
        getAllNodes();
        return new ArrayList<>( xNodePs );
    }

    public List<SearchVertex> getAllXNodeQs() {
        getAllNodes();
        return new ArrayList<>( xNodeQs );
    }

    public List<SearchVertex> getAllYNodes() {
        getAllNodes();
        return new ArrayList<>( yNodes );
    }

    public List<SearchVertex> getAllLeafNodes() {
        getAllNodes();
        return new ArrayList<>( leaves );
    }

    private void resetNodeLists() {
        xNodePs.clear();
        xNodeQs.clear();
        yNodes.clear();
        leaves.clear();
    }

    private void getAllNodes() {
        resetNodeLists();
        getAllNodes( root );
    }

    private void getAllNodes( SearchVertex root ) {
        switch ( root.type ) {
            case X_POINT_P -> xNodePs.add( root );
            case X_POINT_Q -> xNodeQs.add( root );
            case SEGMENT -> yNodes.add( root );
            case TRAPEZOID -> {
                assert root.isLeaf() : root;
                assert root.trapezoid.check();
                leaves.add( root );
                return;
            }
            default -> {
                assert false;
            }
        }

        getAllNodes( root.left );
        getAllNodes( root.right );
    }

    /**
     * query operation with traversal path
     * */

    public List<SearchVertex> getPath( Vector p ) {
        if ( p == null ) return null;
        searchPath.clear();

        get( p );
        return new ArrayList<>( searchPath );
    }

    /**
     * query operation with a point.
     * Notice that if there are no input segments,
     * there will be 10 * 10 bounding box, without any segments inside it, to be queried.
     *
     * @param p query point.
     * @return  a {@link SearchVertex} containing the querying result.
     *          You can get the shape on which the query point is falling,
     *          based on its type. X_POINT_P and X_POINT_Q means that p happens to lie on an endpoint.
     *          SEGMENT means that p happens to lie on a segment.
     *          TRAPEZOID means that p falls inside a trapezoid.
     * */

    public SearchVertex get( Vector p ) {
        if ( p == null ) return null;

        // query point( line.startPoint ) lies in the range of the bounding box?
        if ( !face.isInsideConvexHull( p ) )
            return null;

        return get( root, p );
    }

    private SearchVertex get( SearchVertex root, Vector p ) {
        assert root != null;
        searchPath.add( root );

        switch ( root.type ) {
            case X_POINT_Q, X_POINT_P -> {
                // root.point.isLeft( p ) -- no shear transformation.
                // imagine that we applied shear transformation to the map.
                int res = Vectors.sortByX( root.point, p );
                // query point lies on a endpoint.
                if ( res == 0 ) return root;
                // query point lies to the left.
                else if ( res > 0 )
                    return get( root.left, p );

                // whenever a query point lies on the vertical line of an x-node,
                // we decide that it lies to the right.
                return get( root.right, p );
            }
            case SEGMENT -> {
                double res = Triangles.areaTwo( root.line.startPoint, root.line.endPoint, p );

                // query point lies on a segment.
                if ( MyMath.isEqualZero( res ) )
                    return root;
                // query point lies on the left side of a segment.
                else if ( MyMath.isPositive( res ) )
                    return get( root.left, p );

                // query point lies on the right side of a segment.
                return get( root.right, p );
            }
            // base case
            case TRAPEZOID -> {
                return root;
            }
            default -> { assert false; }
        }

        assert false;
        return null;
    }

    /**
     * query operation with l, actually query its startPoint, left EndPoint.
     * This method is used for build the trapezoidal map.
     * */

    SearchVertex get( Line l ) {
        if ( l == null ) return null;
        if ( root.left == null && root.right == null ) return root;

        // line.startPoint should lie in the range of the bounding box
        assert face.isInsideConvexHull( l.startPoint );
        assert Vectors.sortByX( l.startPoint, l.endPoint ) <= 0;
        SearchVertex res = get( root, l );
        assert res.type == SearchVertex.NodeType.TRAPEZOID;
        return res;
    }

    private SearchVertex get( SearchVertex root, Line line ) {
        Vector p = line.startPoint;

        assert root != null;
        switch ( root.type ) {
            case X_POINT_Q, X_POINT_P -> {
                // root.point.isLeft( p ) -- no shear transformation.
                // imagine that we applied shear transformation to the map.
                if ( Vectors.sortByX( root.point, p ) > 0 )
                    return get( root.left, line );

                // whenever p lies on the vertical line of an x-node,
                // we decide that it lies to the right.
                return get( root.right, line );
            }
            case SEGMENT -> {
                double res = Triangles.areaTwo( root.line.startPoint, root.line.endPoint, p );

                // whenever p lies on a segment s of a y-node
                // (this can only happen if si shares its left endpoint, p, with s)
                // we compare the slopes of s and si; if the slope of si is larger,
                // we decide that p lies above s, otherwise we decide that it is below s.
                if ( MyMath.isEqualZero( res ) ) {
                    if ( compareBySlope( line, root.line ) > 0 )
                        return get( root.left, line );
                    else return get( root.right, line );
                }
                else if ( MyMath.isPositive( res ) )
                    return get( root.left, line );

                return get( root.right, line );
            }
            // base case
            case TRAPEZOID -> {
                return root;
            }
            default -> {
                assert false;
            }
        }

        assert false;
        return null;
    }

    /**
     * Compare lines by slope.
     * Slope of vertical lines is considered as being infinite large.
     * And have two vertical lines,
     * one of which with larger y-coor of its endpoint is large.
     * Notice: we assume startpoint < endpoint.
     *
     * And allowing vertical lines is attempted to do the comparison in Point Location.
     */

    private static
    int compareBySlope( Line l1, Line l2 ) {
        // vertical lines, compare by endpoint's y-coor.
        if ( l1.isVertical && l2.isVertical ) {
            Vector end1 = l1.startPoint.y >= l1.endPoint.y ? l1.startPoint : l1.endPoint;
            Vector end2 = l2.startPoint.y >= l2.endPoint.y ? l2.startPoint : l2.endPoint;
            return Vectors.sortByY( end1, end2 );
        }
        else if ( l1.isVertical )
            return 1;
        else if ( l2.isVertical )
            return -1;

        // non-vertical lines, compare by dy and dx.
        return Lines.compareBySlope( l1.dx, l1.dy, l2.dx, l2.dy );
    }
}
