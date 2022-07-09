package com.fengkeyleaf.util.geom;

/*
 * ConvexHull.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 grahamScan and brute force ( static convex hull ) support on 9/9/2021$
 *     $1.0 dynamic convex hull on 5/6/2022$
 */

import com.fengkeyleaf.util.tree.DoublyLinkedRBT;
import com.fengkeyleaf.util.tree.MapTreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class provides static Convex Hull by brute force,
 * and also dynamic convex hull using BBST.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class ConvexHull {
    // BBST to sort points by x-coor,
    // and thus give us the ability to construct dynamic convex hull.
    private final DoublyLinkedRBT<Vector, Vector> t =
            new DoublyLinkedRBT<>( Vectors::sortByX );
    // current convex hull in the form of point set.
    // They are in counter-clockwise order.
    List<Vector> C;

    /**
     * find convex hull of given points with brute force ( static )
     * time complexity: O( n ^ 3 ) overall
     *
     *
     * @param P A set P of points in the plane. Duplicate points allowed,
     *         they will be ignored during the process of graham scan.
     * @return A list containing the vertices of CH(P) in counter-clockwise order
     */

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm SLOWCONVEXHULL(P)
    // Input. A set P of points in the plane.
    // Output. A list L containing the vertices of CH(P) in clockwise order.
    public static
    List<Vector> slowConvexHull( List<Vector> P ) {
        if ( P.size() < 3 ) return null;

        // E <- 0. -> O( 1 )
        List<Vector> extremePoints = new ArrayList<>();
        // for all ordered pairs (p;q) 2 PxP with p not equal to q -> O( n ^ 3 );
        for ( int i = 0; i < P.size(); i++ ) {
            Vector p = P.get( i );

            for ( int j = 0; j < P.size(); j++ ) {
                if ( i == j ) continue;

                Vector q = P.get( j );
                // do valid <- true
                boolean isValid = true;
                // for all points r 2 P not equal to p or q
                for ( int k = 0; k < P.size(); k++ ) {
                    if ( k == i || k == j ) continue;

                    Vector r = P.get( k );
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

        // From the set E of edges construct
        // a list L of vertices of CH(P),
        // sorted in clockwise order. -> O( nlogn )
        return new ConvexHull().grahamScan( extremePoints );
    }

    /**
     * find convex hull of given points with graham scan in O( nlogn ) time,
     * and this method provides dynamic convex hull,
     * adding a point and querying a point in this convex hull.
     *
     * Theorem 1.1
     * The convex hull of a set of n points in the plane can be computed in O(nlogn) time.
     *
     * @param P A set P of points in the plane. Duplicate points allowed,
     *         they will be ignored during the process of inserting them into the R-B tree.
     * @return A list containing the vertices of CH(P) in counter-clockwise order
     */

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm CONVEXHULL(P)
    // Input. A set P of points in the plane.
    // Output. A list containing the vertices of CH(P) in clockwise order,
    // but actually in counter-clockwise order in the implementation.
    public List<Vector> grahamScan( List<Vector> P ) {
        if ( P.size() < 3 ) return null;

        // Sort the points by x-coordinate,
        // resulting in a sequence p1...pn. -> O( nlogn );
        P.forEach( p -> t.put( p, p ) );

        // scan points to build the convex hull.
        return C = grahamScan();
    }

    private List<Vector> grahamScan() {
        if ( t.size() < 3 ) return null;

        // Put the points p1 and p2 in a list L_upper,
        // with p1 as the first point. -> O( 1 );
        Iterator<MapTreeNode<Vector, Vector>> iter = t.iterator();
        LinkedList<Vector> upper = new LinkedList<>();
        upper.push( iter.next().key );
        upper.push( iter.next().key );

        // for i <- 3 to n -> O( n );
        while ( iter.hasNext() )
            grahamScan( upper, iter.next().key );

        // Put the points pn and pn-1 in a list L_lower,
        // with pn as the first point.
        iter = t.descendingIterator();
        LinkedList<Vector> lower = new LinkedList<>();
        lower.push( iter.next().key );
        lower.push( iter.next().key );

        // for i <- n-2 downTo 1 -> O( n );
        while ( iter.hasNext() )
            grahamScan( lower, iter.next().key );

        // Remove the first and the last point from L_lower
        // to avoid duplication of the points
        // where the upper and lower hull meet.
        // Append L_lower to L_upper, and call the resulting list L. -> O( n );
        // LinkedList::pop = LinkedList::removeFirst.
        // LinkedList::push = LinkedList::addFirst.
        lower.pop();
        while ( lower.size() > 1 )
            upper.add( lower.pop() );

        // no convex nulls, if having less than 3 points
        return upper.size() < 3 ? null : upper;
    }

    /**
     * @param t element at the top of the stack, {@code L}
     * */

    private void grahamScan( LinkedList<Vector> L, Vector t ) {
        // do Append pi to L_upper or L_lower

        // while L_upper or L_lower contains more than two points and
        // the last three points in L_upper or L_lower do not make a right turn
        Vector m = L.pop();
        while ( !L.isEmpty() &&
                Triangles.toLeft( L.peek(), m, t ) ) {
            // do Delete the middle of the last three points from L_upper or L_lower.
            m = L.pop();
        }
        L.push( m );
        L.push( t );
    }

    /**
     * Add one point into this convex hull,
     * and construct a new one.
     * Time complexity:
     * add p into the BBST and construct a new convex hull,
     * thus O(logn + n) = O(n) overall.
     *
     * @return A list containing the vertices of CH(P) in counter-clockwise order,
     *          after adding the point, {@code p}
     * */

    public List<Vector> add( Vector p ) {
        t.put( p, p );
        return C = grahamScan();
    }

    /**
     * Query a given point, {@code p}, to see
     * if it is in this convex hull
     * Time complexity: O(n).
     * */

    // TODO: 6/1/2022 can do in O( logn )?
    public boolean contains( Vector p ) {
        if ( C.size() < 3 ) return false;

        for ( int i = 0; i < C.size(); i += 2 ) {
            if ( !Triangles.toLeft( C.get( i ), C.get( i + 1 ), p ) ) {
                return false;
            }
        }

        return Triangles.toLeft( C.get( C.size() - 1 ), C.get( 0 ), p );
    }

    public static
    Face OnWhichFace( List<Face> polygons, Vector point ) {
        for ( Face polygon : polygons ) {
            if ( polygon.isOnConvexHull( point ) )
                return polygon;
        }

        return null;
    }
}
