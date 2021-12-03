package myLibraries.util.geometry.tools;

/*
 * ConvexHull.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 grahamScan and brute force support on 9/9/2021$
 *
 * JDK: 15
 */

import myLibraries.util.geometry.elements.point.Vector;

import java.util.*;
import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to Convex Hull
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class ConvexHull {

    /**
     * find convex hull of given points with brute force
     *
     * time complexity: O( n ^ 3 ) overall
     *
     * Reference resource:
     * @see <a href=http://www.cs.uu.nl/geobook/>Computational Geometry: Algorithms and Applications(Third Edition)</a>
     */

    // Algorithm SLOWCONVEXHULL(P)
    // Input. A set P of points in the plane.
    // Output. A list L containing the vertices of CH(P) in clockwise order.
    public static
    List<Vector> slowConvexHull( List<Vector> points ) {
        if ( points.size() < 3 ) return null;

        // E <- 0. -> O( 1 )
        List<Vector> extremePoints = new ArrayList<>();
        // for all ordered pairs (p;q) 2 PxP with p not equal to q -> O( n ^ 3 );
        for ( int i = 0; i < points.size(); i++ ) {
            Vector p = points.get( i );

            for ( int j = 0; j < points.size(); j++ ) {
                if ( i == j ) continue;

                Vector q = points.get( j );
                // do valid <- true
                boolean isValid = true;
                // for all points r 2 P not equal to p or q
                for ( int k = 0; k < points.size(); k++ ) {
                    if ( k == i || k == j ) continue;

                    Vector r = points.get( k );
                    // do if r lies to the left of the directed line from p to q
                    if ( Triangles.toLeftRigorously( p, q, r ) ) {
                        // then valid <- false.
                        isValid = false;
                        break;
                    }
                }

                // if valid then Add the directed edge pq to E.
                if ( isValid ) {
                    extremePoints.add( p );
                    extremePoints.add( q );
                }
            }
        }

//        System.out.println( extremePoints );
        // From the set E of edges construct
        // a list L of vertices of CH(P),
        // sorted in clockwise order. -> O( nlogn )
        return grahamScan( extremePoints );
    }

    private static
    void grahamScan( List<Vector> points, List<Vector> list, int i ) {
        // do Append pi to L_upper or L_lower
        list.add( points.get( i ) );

        // while L_upper contains more than two points and
        // the last three points in L_upper or L_lower do not make a right turn
        while ( list.size() > 2 &&
                Triangles.toLeft( list.get( list.size() - 3 ),
                        list.get( list.size() - 2 ), list.get( list.size() - 1 ) ) ) {
            // do Delete the middle of the last three points from L_upper or L_lower.
            list.set( list.size() - 2, list.get( list.size() - 1 ) );
            list.remove( list.size() - 1 );
        }
    }

    /**
     * find convex hull of given points with graham scan
     *
     * time complexity: O( nlogn ) overall
     *
     * Reference resource:
     * @see <a href=http://www.cs.uu.nl/geobook/>Computational Geometry: Algorithms and Applications(Third Edition)</a>
     */

    // Algorithm CONVEXHULL(P)
    // Input. A set P of points in the plane.
    // Output. A list containing the vertices of CH(P) in clockwise order
    public static
    List<Vector> grahamScan( List<Vector> points ) {
        if ( points.size() < 3 ) return null;

        // Sort the points by x-coordinate,
        // resulting in a sequence p1...pn. -> O( nlogn );
        points.sort( Vectors::sortByX );

        // Put the points p1 and p2 in a list L_upper,
        // with p1 as the first point. -> O( 1 );
        List<Vector> upper = new ArrayList<>( 2 );
        upper.add( points.get( 0 ) );
        upper.add( points.get( 1 ) );

        // for i 3 to n -> O( n );
        for ( int i = 2; i < points.size(); i++ ) {
            grahamScan( points, upper, i );
        }

//        System.out.println( upper );
        // Put the points pn and pn􀀀1 in a list L_lower,
        // with pn as the first point.
        List<Vector> lower = new ArrayList<>( 2 );
        lower.add( points.get( points.size() - 1 ) );
        lower.add( points.get( points.size() - 2 ) );

        // for i n-2 downTo 1 -> O( n );
        for ( int i = points.size() - 3; i >= 0; i-- ) {
            grahamScan( points, lower, i );
        }

//        System.out.println( lower );
        // Remove the first and the last point from L_lower
        // to avoid duplication of the points
        // where the upper and lower hull meet.
        // Append L_lower to L_upper, and call the resulting list L. -> O( n );
        for ( int i = 1; i < lower.size() - 1; i++ )
            upper.add( lower.get( i ) );

        // no convex nulls, if having less than 3 points
        return upper.size() < 3 ? null : upper;
    }

    public static
    void main( String[] args ) {
        int ID = 0;
        List<Vector> point2Ds = new ArrayList<>();
        point2Ds.add( new Vector( 7, 9, ID++ ) );
        point2Ds.add( new Vector( -8, -1, ID++ ) );
        point2Ds.add( new Vector( -3, -1, ID++ ) );
        point2Ds.add( new Vector( 1, 4, ID++ ) );
        point2Ds.add( new Vector( -3, 9, ID++ ) );
        point2Ds.add( new Vector( 6, -4, ID++ ) );
        point2Ds.add( new Vector( 7, 5, ID++ ) );
        point2Ds.add( new Vector( 6, 6, ID++ ) );
        point2Ds.add( new Vector( -6, 10, ID++ ) );
        point2Ds.add( new Vector( -0, 8, ID++ ) );

        point2Ds.clear();
        Vector point1 = new Vector( 2, 1, ID++ );
        Vector point2 = new Vector( 2, 2, ID++ );
        Vector point3 = new Vector( 0, 4, ID++ );
        Vector point4 = new Vector( -2, 1, ID++ );
        Vector point5 = new Vector( -2, 2, ID++ );
        Vector point6 = new Vector( -4, 0, ID++ );
        Vector point7 = new Vector( -2, -1, ID++ );
        Vector point8 = new Vector( -2, -2, ID++ );
        Vector point9 = new Vector( 0, -4, ID++ );
        Vector point10 = new Vector( 2, -1, ID++ );
        Vector point11 = new Vector( 2, -2, ID++ );

        Vector point12 = new Vector( -2, -2, ID++ );
        Vector point13 = new Vector( 2, -2, ID++ );
        Vector point14 = new Vector( 2, 1, ID++ );
        Vector point15 = new Vector( 1, 1, ID++ );
        Vector point16 = new Vector( -2, 2, ID++ );
        Vector point17 = new Vector( -4, 1, ID++ );

//        point2Ds.add( point1 );
//        point2Ds.add( point2 );
//        point2Ds.add( point3 );
//        point2Ds.add( point4 );
//        point2Ds.add( point5 );
//        point2Ds.add( point6 );
//        point2Ds.add( point7 );
//        point2Ds.add( point8 );
//        point2Ds.add( point9 );
//        point2Ds.add( point10 );
//        point2Ds.add( point11 );
//
//        System.out.println( point2Ds );
//        point2Ds.sort( Point2DComparator::sortByPolar );
//        System.out.println( point2Ds );
//
//        point2Ds.clear();
//        point2Ds.add( point12 );
//        point2Ds.add( point13 );
//        point2Ds.add( point14 );
//        point2Ds.add( point15 );
//        point2Ds.add( point16 );
//        point2Ds.add( point17 );
//        System.out.println( point2Ds );
//
//        Vector LTL = findLTL( point2Ds );
//        System.out.println( LTL );
//        System.out.println( LTL.x + " " + LTL.y );
//
//        point2Ds.forEach( point2D -> point2D.relativeToLTL( LTL.x, LTL.y ) );
//        point2Ds.sort( Point2DComparator::sortByToLeft );
//        System.out.println( point2Ds );

        Vector point18 = new Vector( 2, -2, ID++ );
//        point18.xRelativeToLTL = 2;
//        point18.yRelativeToLTL = -2;
        Vector point19 = new Vector( 0, 2, ID++ );
//        point19.xRelativeToLTL = 0;
//        point19.yRelativeToLTL = 2;
        Vector point20 = new Vector( -2, 2, ID++ );
//        point20.xRelativeToLTL = -2;
//        point20.yRelativeToLTL = 2;

//        System.out.println( Triangles.areaTwoByLTL( point18, point19, point20 ) );
        System.out.println( Triangles.areaTwo( point18, point19, point20 ) );
    }
}
