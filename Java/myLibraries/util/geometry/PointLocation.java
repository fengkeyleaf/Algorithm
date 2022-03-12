package myLibraries.util.geometry;

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

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.InterLine;
import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.Vector;

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

    static
    SearchVertex getBoundingBox( BoundingBox b ) {
        // Using InterLine so as to have the ability to combine Point Location and Voronoi Diagrams.
        Line top = new InterLine( b.topLeft, b.topRight, null );
        Line bottom = new InterLine( b.bottomLeft, b.bottomRight, null );
        Trapezoid R = new Trapezoid( b.bottomLeft, b.topRight, top, bottom );

        SearchVertex vertex = new SearchVertex( SearchVertex.NodeType.TRAPEZOID );
        vertex.trapezoid = R;
        R.vertex = vertex;
        return vertex;
    }

    /**
     * build trapezoidal Map and search structure.
     * And allow degenerate cases, where there are points with the same x-coors.
     *
     * Reference resource:
     * @see <a href=http://www.cs.uu.nl/geobook/>Computational Geometry: Algorithms and Applications(Third Edition)</a>
     * */

    // Here I'll briefly describe the idea behind the method to handle degenerate cases,
    // where there are points with the same x-coors.
    // According to the textbook, we need to apply shear transformation to the map.
    // i.e. we need compute a sheared point, ( x + εy, y ).
    // But this will lead to precision issue.
    // Fortunately, there is no need to actually compute the sheared points.
    // We just need original x and y coordinates so that
    // we can imagine that we already apply the shear transformation to the map.
    // So how can we do this with ( x, y ), instead of ( x + εy, y )?

    // Let's dig into the description from the textbook for now.
    // In face, we never actually compute the coordinates of the endpoints of vertical extensions,
    // for instance. All it does is to apply two types of elementary operations to the input points.

    // The first operation takes two distinct points p and q and decides whether q lies
    // to the left, to the right, or on the vertical line through p.
    // The second operation takes one of the input segments,
    // specified by its two endpoints p1 and p2,
    // and tests whether a third point q lies above, below, or on this segment.
    // This second operation is only applied when we already know that
    // a vertical line through q intersects the segment.
    // All the points p, q, p1, and p2 are endpoints of segments in the input set S.

    // So what does the first operation mean?
    // It means that we can actually "shear" a point just by sorting it with y-coors.
    // that is, the point with larger y-coor is considered as being
    // right to the one with smaller y-coor.
    // As for the second operation, we only need to take advantage of to left test to do the work.
    // No need to consider that many cases. Very convenient.

    // So with those methods, we'll introduce some "zero-area" trapezoid to the map.
    // Those trapezoids have overlapping tops, bottoms, leftPs and rightPs,
    // so their area is zero.

    // At this point, we're done with discussing the degenerate cases with the same x-coors.
    // But how about an endpoint lying on a segment?
    // This is not difficult to deal with.
    // You can imagine those segments moving a little downwards or upwards to trim walls.
    // Which direction to trim is up to you.
    // As for my implementation, when we have P point of sj lying on si,
    // we trim upper wall when Q point of si lying above sj,
    // and trim lower wall when Q point of si lying below sj.
    // And vice versa when we have Q point of sj lying on si,

    // Algorithm TRAPEZOIDALMAP(S)
    // Input. A set S of n non-crossing line segments.
    // Output. The trapezoidal map T(S) and a search structure D for T(S) in a bounding box.
    public static
    BoundingBox trapezoidalMap( List<Line> lines ) {
        if ( lines == null ) return null;

        List<Vector> points = new ArrayList<>( lines.size() * 2 + 1 );
        // no input data, add one additional point to visualize.
        if ( lines.isEmpty() ) points.add( Vector.origin );
        lines.forEach( l -> {
            points.add( l.startPoint );
            points.add( l.endPoint );
        } );

        // get the bounding box R and compute the map.
        BoundingBox b = BoundingBox.getBoundingBox( points, BoundingBox.OFFSET );
        // cannot not have null for the bounding box.
        b.SS = trapezoidalMap( lines, PointLocation.getBoundingBox( b ) );
        return b;
    }

    private static
    SearchStructure trapezoidalMap( List<Line> lines, SearchVertex b ) {
        // D <- initialize;
        // put T into D;
        SearchStructure SS = new SearchStructure( b );

        // for i <- 1 to n
        for ( int i = 0; i < lines.size(); i++ ) {
            Line l = lines.get( i );
            assert Vectors.sortByX( l.startPoint, l.endPoint ) <= 0;
            // do Find the set D0, D1, ... , Dk of trapezoids in T properly intersected by si.
            // Ds <- D0, D1, ... , Dk;
            List<SearchVertex> Ds = followSegment( SS, l );

            // Remove D0, D1, ... , Dk from T and replace them
            // by the new trapezoids that appear because of the insertion of si.
            // updateTrapezoidalMap(Ds, si);
            // Remove the leaves for D0, D1, ... , Dk from D,
            // and create leaves for the new trapezoids.
            // Link the new leaves to the existing inner nodes
            // by adding some new inner nodes, as explained below.
            if ( i == 0 ) SS.setRoot( update( Ds, l, true ) );
            else update( Ds, l, false );
        }

        return SS;
    }

    /**
     * update trapezoidal Map and search structure
     * */

    static
    SearchVertex update( List<SearchVertex> Ds, Line l, boolean isFirst ) {
        // Let p and q be the left and right endpoint of si.
        Vector p = l.startPoint;
        Vector q = l.endPoint;
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
                    res = TrapezoidalMap.handleP( di, l, null );
                }
                // one more trapezoids to be partitioned
                else {
                    res = TrapezoidalMap.handleP( di, l, deletes );
                }

                // replace the root when added the first segment
                if ( isFirst ) return res;
            }
            // if i == Ds.length - 1
            else if ( i == Ds.size() - 1 ) {
                // partition Q
                TrapezoidalMap.handleQ( di, l, deletes );
            }
            else {
                // partition S
                TrapezoidalMap.handleS( di, l , deletes );
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
    List<SearchVertex> followSegment( SearchStructure SS, Line l ) {
        // Let p and q be the left and right endpoint of si.
        Vector p = l.startPoint;
        Vector q = l.endPoint;
        assert Vectors.sortByX( p, q ) <= 0;

        List<SearchVertex> Ds = new ArrayList<>();
        // Search with p in the search structure D to find D0.
        assert SS.get( l ) != null : l;
        Trapezoid D0 = SS.get( l ).trapezoid;
        Ds.add( D0.vertex );

        // j <- 0;
        // while q lies to the right of rightP(Dj).
        // D0.rightP.isRight( q ) -- no shear transformation.
        // imagine that we applied shear transformation to the map.
        while ( Vectors.sortByX( D0.rightP, q ) < 0 ) {
            double res = Triangles.areaTwo( p, q, D0.rightP );
            // if rightP(Dj) lies on si
            if ( MyMath.isEqualZero( res ) ) {
                D0 = followSegment( D0, q );
            }
            // do if rightP(Dj) lies above si
            else if ( MyMath.isPositive( res ) ) {
                // then Let Dj+1 be the lower right neighbor of Dj.
                D0 = D0.lowerRightNeighbor;
            }
            else {
                // else Let Dj+1 be the upper right neighbor of Dj.
                D0 = D0.upperRightNeighbor;
            }
            // j <- j+1
            assert D0 != null : l;
            Ds.add( D0.vertex );
        }

        // return D0,D1, ... , Dj
        return Ds;
    }

    private static
    Trapezoid followSegment( Trapezoid D0, Vector q ) {
        Trapezoid upperRight = D0.upperRightNeighbor;
        Trapezoid lowerRight = D0.lowerRightNeighbor;

        // rightP(Dj) is P node of sj
        if ( upperRight != null && lowerRight != null ) {
            // endpoint of si ( q ) lies above sj
            if ( D0.rightP.isAbove( q ) )
                D0 = D0.upperRightNeighbor;
            else
                D0 = D0.lowerRightNeighbor;
        }
        // rightP(Dj) is Q node of sj
        else if ( upperRight != null )
            D0 = D0.upperRightNeighbor;
        else if ( lowerRight != null )
            D0 = D0.lowerRightNeighbor;
        // impossible both are null.
        else assert false;

        return D0;
    }

}
