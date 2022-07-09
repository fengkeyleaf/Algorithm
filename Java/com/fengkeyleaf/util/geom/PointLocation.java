package com.fengkeyleaf.util.geom;

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

import com.fengkeyleaf.io.MyWriter;
import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.util.MyCollections;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class consists exclusively of static methods
 * that related to Point Location.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class PointLocation {

    /**
     * get the bounding box R with given bottomLeft and topRight points
     * */

    static
    SearchVertex getBoundingBox( BoundingBox b ) {
        // Using InterLine so as to have the ability to combine Point Location and Voronoi Diagrams.
        Line top = new Segment( b.topLeft, b.topRight );
        Line bottom = new Segment( b.bottomLeft, b.bottomRight );
        Trapezoid R = new Trapezoid( b.bottomLeft, b.topRight, top, bottom );

        SearchVertex vertex = new SearchVertex( SearchVertex.NodeType.TRAPEZOID );
        vertex.trapezoid = R;
        R.vertex = vertex;
        return vertex;
    }

    /**
     * build trapezoidal Map and search structure.
     * And allow degenerate cases, where there are points with the same x-coors.
     * Note that this method won't return a bounding box containing all query points inside.
     * To do so, use another overloaded one, {@linkplain  PointLocation#trapezoidalMap(List, List)}}
     *
     * @param S  segment set on which the trapezoidal map is based.
     * @return   The trapezoidal map T(S) and a search structure D for T(S) in a bounding box
     *           containing all segment inside.
     * */

    public static
    BoundingBox trapezoidalMap( List<Line> S ) {
        return trapezoidalMap( S, new ArrayList<>() );
    }

    /**
     * build trapezoidal Map and search structure.
     * And allow degenerate cases, where there are points with the same x-coors.
     *
     * Corollary 6.4
     * Let S be a planar subdivision with n edges.
     * In O(nlogn) expected time one can construct a data structure
     * that uses O(n) expected storage,
     * such that for any query point q,
     * the expected time for a point location query is O(logn).
     *
     * @param S  segment set on which the trapezoidal map is based.
     *           Note that every segment should have the following property: left endpoint < right endpoint.
     * @param Q  query point set to initialize a bounding box
     *           including all segments and query points as well.
     *           ( if it's not nil )
     * @return   The trapezoidal map T(S) and a search structure D for T(S) in a bounding box
     *           containing all segments and query points inside.
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

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm TRAPEZOIDALMAP(S)
    // Input. A set S of n non-crossing line segments.
    // Output. The trapezoidal map T(S) and a search structure D for T(S) in a bounding box.
    public static
    BoundingBox trapezoidalMap( List<Line> S, List<Vector> Q ) {
        if ( S == null ) return null;

        List<Vector> points = new ArrayList<>( S.size() * 2 + 1 );
        // no input data, add one additional point to visualize.
        if ( S.isEmpty() ) points.add( Vector.origin );
        S.forEach( l -> {
            points.add( l.startPoint );
            points.add( l.endPoint );
        } );
        if ( Q != null ) points.addAll( Q );

        // 1. Determine a bounding box R that contains all segments of S,
        // and initialize the trapezoidal map structure T and search structure D for it.
        // get the bounding box R and compute the map.
        BoundingBox b = BoundingBox.getBoundingBox( points, BoundingBox.OFFSET );

        // 2. Compute a random permutation s1, s2, . . . , sn of the elements of S.
        MyCollections.randomPermutation( S );
        assert writeToFile( S );

        // cannot not have null for the bounding box.
        b.SS = trapezoidalMap( S, PointLocation.getBoundingBox( b ) );
        return b;
    }

    static
    boolean writeToFile( List<Line> S ) {
        StringBuilder t = new StringBuilder();
        S.forEach( s -> {
            t.append( s.startPoint.x ).append( " " ).append( s.startPoint.y ).append( " " ).append( s.endPoint.x ).append( " " ).append( s.endPoint.y ).append( "\n" );
        } );
        MyWriter.writeToFile( "src/CGTsinghua/PA_3/problem_2/input", t.toString() );

        return true;
    }

    private static
    SearchStructure trapezoidalMap( List<Line> lines, SearchVertex b ) {
        // D <- initialize;
        // put T into D;
        SearchStructure SS = new SearchStructure( b );

        // 3. for i <- 1 to n
        for ( int i = 0; i < lines.size(); i++ ) {
            Line l = lines.get( i );
            assert Vectors.sortByX( l.startPoint, l.endPoint ) <= 0;
            // 4. do Find the set D0, D1, ... , Dk of trapezoids in T properly intersected by si.
            // Ds <- D0, D1, ... , Dk;
            List<SearchVertex> Ds = followSegment( SS, l );

            // 5. Remove D0, D1, ... , Dk from T and replace them
            // by the new trapezoids that appear because of the insertion of si.
            // updateTrapezoidalMap(Ds, si);
            // 6. Remove the leaves for D0, D1, ... , Dk from D,
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
        // 1. Let p and q be the left and right endpoint of si.
        Vector p = l.startPoint;
        Vector q = l.endPoint;
        assert Vectors.sortByX( p, q ) <= 0;

        List<SearchVertex> Ds = new ArrayList<>();
        // 2. Search with p in the search structure D to find D0.
        assert SS.get( l ) != null : l;
        Trapezoid D0 = SS.get( l ).trapezoid;
        Ds.add( D0.vertex );

        // 3. j <- 0;
        // 4. while q lies to the right of rightP(Dj).
        // D0.rightP.isRight( q ) -- no shear transformation.
        // imagine that we applied shear transformation to the map.
        while ( Vectors.sortByX( D0.rightP, q ) < 0 ) {
            double res = Triangles.areaTwo( p, q, D0.rightP );
            // if rightP(Dj) lies on si
            if ( MyMath.isEqualZero( res ) ) {
                D0 = followSegment( D0, q );
            }
            // 5. do if rightP(Dj) lies above si
            else if ( MyMath.isPositive( res ) ) {
                // 6. then Let Dj+1 be the lower right neighbor of Dj.
                D0 = D0.lowerRightNeighbor;
            }
            else {
                // 7. else Let Dj+1 be the upper right neighbor of Dj.
                D0 = D0.upperRightNeighbor;
            }
            // 8. j <- j+1
            assert D0 != null : l;
            Ds.add( D0.vertex );
        }

        // 9. return D0,D1, ... , Dj
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
        // there are two cases where both right neighbours are null:
        // 1) the rightmost trapezoid whose top line and bottom line are from the bounding box.
        // 2) endPoints of the top and the bottom are the same, i.e. the trapezoid is closed to the right.
        // but we won't encounter both cases in followSegment().
        else assert false;

        return D0;
    }

}
