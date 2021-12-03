package myLibraries.util.graph;

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

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.elements.Trapezoid;
import myLibraries.util.geometry.elements.line.Line;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.Lines;
import myLibraries.util.geometry.tools.Polygons;
import myLibraries.util.geometry.tools.Triangles;
import myLibraries.util.geometry.tools.Vectors;
import myLibraries.util.graph.elements.SearchVertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of Search Structure, SS, tree-like DGA, for point location.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
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

    public SearchStructure( SearchVertex R ) {
        root = R;

        try {
            boundingBox = ( Trapezoid ) R.trapezoid.clone();
        } catch ( CloneNotSupportedException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        face = boundingBox.getDCEL();
    }

    public void setRoot( SearchVertex root ) {
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
     * query operation with point
     * */

    public SearchVertex get( Vector p ) {
        if ( p == null ) return null;

        Vector q = new Vector( p.x, p.y + 1 );
        return get( new Line( p, q ) );
    }

    /**
     * query operation with line, actually query its startPoint, left EndPoint
     * */

    public SearchVertex get( Line line ) {
        if ( line == null ) return null;
        if ( root.left == null && root.right == null ) return root;

        // query point( line.startPoint ) lies in the range of the bounding box?
        if ( !Polygons.isInsideThisPolygon( face, line.startPoint ) )
            return null;

        assert Vectors.sortByX( line.startPoint, line.endPoint ) <= 0;
        SearchVertex res = get( root, line );
        assert res.type == SearchVertex.NodeType.TRAPEZOID;
        return res;
    }

    private SearchVertex get( SearchVertex root, Line line ) {
        Vector p = line.startPoint;

        assert root != null;
        searchPath.add( root );
        switch ( root.type ) {
            case X_POINT_Q, X_POINT_P -> {
                if ( Vectors.isLeft( root.point, p ) )
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
                    if ( Lines.compareBySlope( line, root.line ) > 0 )
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
}
