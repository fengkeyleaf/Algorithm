package myLibraries.util.geometry.tools;

/*
 * PointLocation.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 10/21/2021$
 */

import myLibraries.util.geometry.elements.Trapezoid;
import myLibraries.util.geometry.elements.line.Line;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.graph.SearchStructure;
import myLibraries.util.graph.elements.SearchVertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class consists exclusively of static methods
 * that related to Point Location
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class PointLocation {

    /**
     * get the bounding box R with given bottomLeft and topRight points
     * */

    public static
    SearchVertex getBoundingBox( Vector bottomLeft, Vector topRight ) {
        Vector topLeft = new Vector( bottomLeft.x, topRight.y );
        Vector bottomRight = new Vector( topRight.x, bottomLeft.y );

        Line top = new Line( topLeft, topRight );
        Line bottom = new Line( bottomLeft, bottomRight );
        Trapezoid R = new Trapezoid( bottomLeft, topRight, top, bottom );

        SearchVertex vertex = new SearchVertex( SearchVertex.NodeType.TRAPEZOID );
        vertex.trapezoid = R;
        R.vertex = vertex;
        return vertex;
    }

    /**
     * build trapezoidal Map and search structure
     *
     * Reference resource:
     * @see <a href=http://www.cs.uu.nl/geobook/>Computational Geometry: Algorithms and Applications(Third Edition)</a>
     * */

    // Algorithm TRAPEZOIDALMAP(S)
    // Input. A set S of n non-crossing line segments.
    // Output. The trapezoidal map T(S) and a search structure D for T(S) in a bounding box.
    // TODO: 10/23/2021 Need to deal with following two degenerate cases with dual number:
    //  firstly, no two distinct endpoints have the same x-coordinate.
    //  Secondly, we assumed that a query point never lies on the vertical line of an x-node on its search path,
    //  nor on the segment of a y-node.
    public static
    SearchStructure trapezoidalMap( List<Line> lines, SearchVertex box ) {
        // D <- initialize;
        // put T into D;
        SearchStructure SS = new SearchStructure( box );

        // for i <- 1 to n
        for ( int i = 0; i < lines.size(); i++ ) {
            Line line = lines.get( i );
            assert Vectors.sortByX( line.startPoint, line.endPoint ) <= 0;
            // do Find the set D0, D1, ... , Dk of trapezoids in T properly intersected by si.
            // Ds <- D0, D1, ... , Dk;
            List<SearchVertex> Ds = followSegment( SS, line );

            // Remove D0, D1, ... , Dk from T and replace them
            // by the new trapezoids that appear because of the insertion of si.
            // updateTrapezoidalMap(Ds, si);
            // Remove the leaves for D0, D1, ... , Dk from D,
            // and create leaves for the new trapezoids.
            // Link the new leaves to the existing inner nodes
            // by adding some new inner nodes, as explained below.
            if ( i == 0 ) SS.setRoot( update( Ds, line, true ) );
            else update( Ds, line, false );
        }

        return SS;
    }

    /**
     * update trapezoidal Map and search structure
     * */

    static
    SearchVertex update( List<SearchVertex> Ds, Line line, boolean isFirst ) {
        // Let p and q be the left and right endpoint of si.
        Vector p = line.startPoint;
        Vector q = line.endPoint;
        assert Vectors.sortByX( p, q ) <= 0;

        // de <- initialize a list
        Stack<SearchVertex> deletes = new Stack<>();
        // for i <- 0 to Ds.length - 1
        for ( int i = 0; i < Ds.size(); i++ ) {
            SearchVertex di = Ds.get( i );

            // do if i == 0, partition P
            if ( i == 0 ) {
                SearchVertex res = null;
                // only one trapezoid to be partitioned
                if ( Ds.size() == 1 ) {
                    res = TrapezoidalMap.handleP( di, line, null );
                }
                // one more trapezoids to be partitioned
                else {
                    res = TrapezoidalMap.handleP( di, line, deletes );
                }

                // replace the root when added the first segment
                if ( isFirst ) return res;
            }
            // if i == Ds.length - 1
            else if ( i == Ds.size() - 1 ) {
                // partition Q
                TrapezoidalMap.handleQ( di, line, deletes );
            }
            else {
                // partition S
                TrapezoidalMap.handleS( di, line , deletes );
            }
        }

        return null;
    }

    /**
     * find leaf nodes to be replaced when adding a new segment
     * */

    // Algorithm FOLLOWSEGMENT(T, D, si)
    // Input. A trapezoidal map T, a search structure D for T, and a new segment si.
    // Output. The sequence D0; ... ;Dk of trapezoids intersected by si.
    static
    List<SearchVertex> followSegment( SearchStructure search, Line line ) {
        // Let p and q be the left and right endpoint of si.
        Vector p = line.startPoint;
        Vector q = line.endPoint;
        assert Vectors.sortByX( p, q ) <= 0;

        List<SearchVertex> Ds = new ArrayList<>();
        // Search with p in the search structure D to find D0.
        Trapezoid D0 = search.get( line ).trapezoid;
        Ds.add( D0.vertex );

        // j <- 0;
        // while q lies to the right of rightP(Dj)
        while ( Vectors.isRight( D0.rightP, q ) ) {
            // do if rightP(Dj) lies above si
            if ( Triangles.toLeftRigorously( p, q, D0.rightP ) ) {
                // then Let Dj+1 be the lower right neighbor of Dj.
                D0 = D0.lowerRightNeighbor;
            }
            else {
                // else Let Dj+1 be the upper right neighbor of Dj.
                D0 = D0.upperRightNeighbor;
            }
            // j <- j+1
            Ds.add( D0.vertex );
        }

        // return D0,D1, ... , Dj
        return Ds;
    }

}
