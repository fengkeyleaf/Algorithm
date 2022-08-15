package com.fengkeyleaf.util.geom;

/*
 * GeometricIntersection.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 line and line, line and circle, and Bentley Ottmann on 07/18/2021$
 *     $1.0 parabola and parabola on 12/31/2021$
 */

import com.fengkeyleaf.lang.MyMath;

import java.beans.ConstructorProperties;
import java.util.*;
import java.util.function.Predicate;

/**
 * This class consists exclusively of static methods
 * that related to Geometric Intersection
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class GeometricIntersection {

    /**
     * brute force to find intersection.
     * */

    public static<E extends Intersection>
    List<Vector> bruteForce( List<E> S ) {
        TreeSet<Vector> I = new TreeSet<>( Vectors::sortByX );

        for ( int i = 0; i < S.size(); i++ ) {

            for ( int j = 0; j < S.size(); j++ ) {
                if ( i == j ) continue;

                Vector[] intersections = S.get( i ).intersect( S.get( j ) );
                if ( intersections == null ) continue;

                for ( Vector p : intersections ) {
                    if ( p != null ) I.add( p );
                }
            }
        }

        return new ArrayList<>( I );
    }

    //-------------------------------------------------------
    // line and line
    //-------------------------------------------------------

    /**
     * Get line intersection point with vector, if exists.
     * Only difference from segment intersection, {@link GeometricIntersection#segments(Line, Line)}, is that
     * there is always an intersection between two lines
     * as long as they're not parallel.
     * But not ture for segment intersection,
     * so we'll use line equation to compute.
     *
     * Note that overlapping lines are considered as no intersections.
     */

    public static Vector lines( Line l1, Line l2 ) {
        return l1.equation.intersect( l2.equation );
    }

    /**
     * if the two lines Overlap But Have Common EndPoint,
     * but note that may only have one common endPoint.
     */

    private static
    boolean isOverlapButHavingCommonEndPoint( Line l1, Line l2 ) {
        return Vectors.sortByX( l2.startPoint, l2.endPoint ) > 0 &&
                l1.startPoint.equalsXAndY( l2.endPoint ) ||
                l1.endPoint.equalsXAndY( l2.startPoint );
    }

    /**
     * get the common endPoint, intersection as well,
     * if two lines on the same line intersect at one of the endpoints
     */

    private static Vector getOnlyCommonEndPoint( Line l1, Line l2 ) {
        if ( l1.startPoint.equalsXAndY( l2.endPoint ) )
            return l1.startPoint;

        return l1.endPoint;
    }

    /**
     * get line intersection point with vector, if exists
     */

    // Reference resource: https://blog.csdn.net/qq_40998706/article/details/87482435
    private static Vector lineIntersectCommon( Line l1, Line l2 ) {
        // yes, but the intersection point is one of the endpoints.
        if ( isOverlapButHavingCommonEndPoint( l1, l2 ) )
            return getOnlyCommonEndPoint( l1, l2 );

        // yes, normal intersection point.
        Vector base = l2.getVector();
        double d1 = Math.abs( base.cross( l1.startPoint.subtract( l2.startPoint ) ) );
        double d2 = Math.abs( base.cross( l1.endPoint.subtract( l2.startPoint ) ) );
        assert !MyMath.isEqualZero( d1 + d2 );
        double t = d1 / ( d1 + d2 );
        Vector intersection = l1.getVector().multiply( t );

        // the following commented-out code is correct as well,
        // but with less computational accuracy because of ( 1 / t ),
        // one additional division compared to the method above.
//        double t = ( d1 + d2 ) / d1;
//        Vector intersection = l1.endPoint.subtract(
//        l1.startPoint ).multiply( 1 / t );

        return l1.startPoint.add( intersection );
    }

    //-------------------------------------------------------
    // segment and segment
    //-------------------------------------------------------


    /**
     * toLeft test to check if two segments intersect
     */

    private static
    boolean ifSegmentsIntersect( Line l1, Line l2,
                                 double res1, double res2 ) {

        // parallel cases:
        // case 1: overlap or on the same line.
        if ( MyMath.isEqualZero( res1 ) &&
                MyMath.isEqualZero( res2 ) )
            return isOverlapButHavingCommonEndPoint( l1, l2 );
        // case 2: parallel on the right side.
        if ( res1 < 0 && res2 < 0 )
            return false;
        // case 3: parallel on the left side.
        if ( res1 > 0 && res2 > 0 )
            return false;

        // intersecting cases: either intersect at
        // a common point other than endpoints,
        // or at one of the endpoints.
        return true;
    }

    /**
     * segment1 and segment2 intersects?
     */

    private static
    boolean ifSegmentsIntersect( Line l1, Line l2 ) {
        if ( l1 == null || l2 == null ) return false;

        // to left test based on line1.
        double res1 = Triangles.areaTwo( l1.endPoint, l1.startPoint, l2.endPoint );
        double res2 = Triangles.areaTwo( l1.endPoint, l1.startPoint, l2.startPoint );
        // to left test based on line2.
        double res3 = Triangles.areaTwo( l2.endPoint, l2.startPoint, l1.endPoint );
        double res4 = Triangles.areaTwo( l2.endPoint, l2.startPoint, l1.startPoint );

        // have intersection if and only if
        // two endpoints of one line are
        // at the opposite side of the other line.
        boolean finalRes1 = ifSegmentsIntersect( l1, l2, res1, res2 );
        boolean finalRes2 = ifSegmentsIntersect( l1, l2, res3, res4 );
        return finalRes1 && finalRes2;
    }

    /**
     * get segment intersection point with vector, if exists
     * Only difference from line intersection is that
     * there is always an intersection between two lines
     * as long as they're not parallel.
     * But not ture for segment intersection.
     *
     * Note that overlapping segments are considered as no intersections,
     * except that ones with only one endpoint intersection.
     */

    public static
    Vector segments( Line l1, Line l2 ) {
        // have intersection between two segments?
        if ( !ifSegmentsIntersect( l1, l2 ) ) return null;

        // yes, go get it.
        return lineIntersectCommon( l1, l2 );
    }

    //-------------------------------------------------------
    // line and segment
    //-------------------------------------------------------

    public static
    Vector lineSegment( Line l, Line s ) {
        // consider the segment as a line,
        // find the intersection between it and l.
        Vector i = l.equation.intersect( s.equation );
        // no intersection.
        if ( i == null ) return null;

        // have intersection, but is it on the segment?
        return s.isOnThisSegment( i ) ? i : null;
    }

    //-------------------------------------------------------
    // line and arc
    //-------------------------------------------------------

    /**
     * get the intersection point of line and arc with vector, if exists
     */

    public static
    Vector[] lineArc( Line line, Arc arc ) {
        if ( line == null || arc == null ) return null;

        // get the intersection points of the cycle of the arc and the line
        Vector[] segment = lineCircle( line, arc );
        if ( segment == null ) return null;

        Vector intersection1 = null;
        Vector intersection2 = null;
        // those points lay on the arc?
        // note that there are at most two intersection points, not only one
        if ( arc.belong( segment[ 0 ] ) )
            intersection1 = segment[ 0 ];
        if ( arc.belong( segment[ 1 ] ) )
            intersection2 = segment[ 1 ];

        return new Vector[] { intersection1, intersection2 };
    }

    //-------------------------------------------------------
    // segment and circle
    //-------------------------------------------------------

    public static
    Vector[] segmentCircle( Segment s, Circle c ) {
        Vector[] I = lineCircle( s, c );
        if ( I == null ) return null;

        if ( I[ 0 ] != null &&
                !s.isOnThisSegment( I[ 0 ] ) ) {
            I[ 0 ] = null;
        }

        if ( I[ 1 ] != null &&
                !s.isOnThisSegment( I[ 1 ] ) ) {
            I[ 1 ] = null;
        }

        return I;
    }

    //-------------------------------------------------------
    // line and circle
    //-------------------------------------------------------

    /**
     * get the intersection point of line and circle with vector, if exists
     *
     */

    // Reference resource: https://blog.csdn.net/qq_40998706/article/details/87521165
    public static
    Vector[] lineCircle( Line line, Circle circle ) {
        // have intersecting point?
        if ( line == null || circle == null
                || !circle.ifIntersectsLine( line ) ) return null;

        // get the project point
        Vector projectPoint = line.project( circle.center );
        Vector lineVector = line.getVector();
        // get e of the line
        Vector e = lineVector.division( lineVector.length() );
        double sideLength = circle.radius * circle.radius -
                projectPoint.subtract( circle.center ).lengthSq();
        double ratio = Math.sqrt( Math.abs( sideLength ) );
        Vector distance = e.multiply( ratio );
        return new Vector[] { projectPoint.add( distance ), projectPoint.subtract( distance ) };
    }

    //-------------------------------------------------------
    // Bentley Ottmann's algorithm, line segment intersection
    //-------------------------------------------------------

    // data field ------------------------------------------>
    // current event point.
    EventPoint2D e;
    // event queue.
    EventRBTree eventQueue;
    // status tree.
    StatusRBTree statusRBTree;
    // intersection set.
    List<Vector> intersections;
    // condition to determine which intersections will be kept.
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/Predicate.html
    Predicate<EventPoint2D> p = EventPoint2D::isRightIntersection;
    // vertically or horizontally sweeping.
    boolean isVerticalSweepLine = true;

    @ConstructorProperties( { "Conditions", "isVerticalSweepLine" } )
    public GeometricIntersection( Predicate<EventPoint2D> p,
                                  boolean isVerticalSweepLine ) {

        this.p = p;
        this.isVerticalSweepLine = isVerticalSweepLine;
    }

    public GeometricIntersection( Predicate<EventPoint2D> p ) {
        this.p = p;
    }

    public GeometricIntersection( boolean isVerticalSweepLine ) {
        this.isVerticalSweepLine = isVerticalSweepLine;
    }

    public GeometricIntersection() {}

    public void setConditions( Predicate<EventPoint2D> p ) {
        this.p = p;
    }

    public void setVerticalSweepLine( boolean isVerticalSweepLine ) {
        this.isVerticalSweepLine = isVerticalSweepLine;
    }

    private void findNewEvent( Vector i ) {
        // if sl and sr intersect right to the sweep line,
        // or on it and above of the current event point p,
        // and the intersection is not yet present as an event in Q
        // then Insert the intersection point as an event into Q.
        if ( i != null &&
                // here, imagine that we apply shear transformation to
                // vertical segments when sweeping along the x-aix,
                // or to horizontal segments when sweeping along the y-axis.
                ( isVerticalSweepLine ? Vectors.sortByX( i, e ) >= 0 :
                        Vectors.sortByY( i, e ) >= 0 ) ) {

            EventPoint2D intersectionEvent = new EventPoint2D( i, null, false );
            eventQueue.put( intersectionEvent );
        }
    }

    /**
     * find New Event
     * */

    private void findNewEvent( Event l, Event r ) {

        if ( l == null || r == null ) return;

        // 2-layer-for loop to deal with overlapping segments correctly.
        // i.g. two overlapping segments with the same left endpoint( startPoint )
        for ( IntersectionShape sl : l.shapes ) {
            for ( IntersectionShape sr : r.shapes ) {
                // find possible intersections
                Vector[] intersections = sl.intersect( sr );
                if ( intersections == null || intersections.length == 0 ) return;
                assert intersections.length < 3;

                // line intersection, 1 intersection at most
                findNewEvent( intersections[ 0 ] );

                if ( intersections.length == 1 ) return;

                // line and cycle intersection, 2 intersection at most
                findNewEvent( intersections[ 1 ] );
            }
        }
    }

    /**
     * report intersection point.
     *
     * @param   L shapes intersecting at p with p as its left endpoint.
     * @param   R shapes intersecting at p with p as its right endpoint.
     * @param   I shapes intersecting at p and containing p.
     * @return  intersection point with all intersecting shapes involved.
     */

    EventPoint2D reportIntersection( List<EventPoint2D> L,
                                     List<EventPoint2D> R, List<EventPoint2D> I ) {

        EventPoint2D i = new EventPoint2D( e.x, e.y,
                null, false );
        // add intersecting shapes
        i.reportIntersection( L );
        L.forEach( v -> i.L.add( v.shape ) );
        i.reportIntersection( R );
        R.forEach( v -> i.R.add( v.shape ) );
        i.reportIntersection( I );
        I.forEach( v -> i.I.add( v.shape ) );
        // add intersection when it's legal.
        if ( this.p.test( i ) )
            intersections.add( i );

        return i;
    }

    /**
     * reinsert Lefts And Interiors into statusRBTree
     * we should consider vertical lines as special case
     * */

    // Double.MAX_VALUE, positive
    // https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/lang/Double.html#MAX_VALUE
    // A constant holding the largest positive finite value of type double, (2-2-52)·21023.
    // It is equal to the hexadecimal floating-point literal 0x1.fffffffffffffP+1023 and also equal to Double.longBitsToDouble(0x7fefffffffffffffL).
    // Double.MIN_VALUE, positive
    // https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/lang/Double.html#MIN_VALUE
    // A constant holding the smallest positive nonzero value of type double, 2-1074.
    // It is equal to the hexadecimal floating-point literal 0x0.0000000000001P-1022 and also equal to Double.longBitsToDouble(0x1L).
    private double reinsertLeftsAndInteriors( List<EventPoint2D> statuses,
                                              double updatedCoor,
                                              List<EventPoint2D> specicals ) {

        double maxCoor = -Double.MAX_VALUE;
        // we only have special segments?
        boolean isOnlySpecial = true;
        for ( EventPoint2D status : statuses ) {
            // dealing with special lines
            if ( status.shape instanceof Line &&
                    // vertical sweep line, so vertical segments are special
                    ( isVerticalSweepLine && ( ( Segment ) status.shape ).isVertical ||
                            // horizontal sweep line, so horizontal segments are special
                            !isVerticalSweepLine && ( ( Segment ) status.shape ).isHorizontal ) ) {

                    specicals.add( status );
                    continue;
            }
            isOnlySpecial = false;

            // non-vertical lines or arcs ( circle )
            status.update( updatedCoor, isVerticalSweepLine );
            maxCoor = Math.max( maxCoor, isVerticalSweepLine ? status.y : status.x );
            statusRBTree.put( status );
        }

        // if statues only have one vertical line,
        // should return maxY = y of left endpoint of this vertical line
        if ( !specicals.isEmpty() && isOnlySpecial ) {
            maxCoor = Math.max( maxCoor, isVerticalSweepLine ? e.y : e.x );
        }

        return maxCoor;
    }

    /**
     * reinsert Lefts And Interiors into statusRBTree
     * */

    // (Deleting and re-inserting the segments of C(p) reverses their order.)
    private List<EventPoint2D> reinsertLeftsAndInteriors( List<EventPoint2D> L,
                                                          List<EventPoint2D> I ) {

        // Choice of the offset is very tricky,
        // because if two lines are very close to each other,
        // the program will think those two intersect
        // when updating them in the status tree, but they don't in reality.
        // See test case 35, we will have error if the offset is MyMath.EPSILON * 10,
        // but get correct answer if it is MyMath.EPSILON * 100
        // Also see test case 1_8, you will find segment very close to an arc,
        // where it is tangent to the circle of the arc.
        final double OFFSET = 0.001f;
        // just right to the sweeping line,
        // so only smaller value to offset x coordinate
        final double updatedCoor = ( isVerticalSweepLine ? e.x : e.y ) + OFFSET;
        final List<EventPoint2D> specials = new ArrayList<>();

        // Insert the segments in L(p) ∪ C(p) into T.
        // The order of the segments in T should correspond
        // the order in which they are intersected
        // by a sweep line just right to p.
        double maxCoorLeft = reinsertLeftsAndInteriors( L, updatedCoor, specials );
        double maxCoorInterior = reinsertLeftsAndInteriors( I, updatedCoor, specials );

        // insert all vertical lines in my implementation,
        // while insert all horizontal lines in the textbook's context.
        for ( EventPoint2D status : specials ) {
            // If there is a vertical segment,
            // it comes last among all segments containing p.
            // nothing to do with cycles here

            // vertical lines are a little higher than
            // all shapes in lefts and interiors at updatedX
            // and we need to use updatedX to update segments in those trees,
            // not x-coordinate at current event point.
            // you can imagine we use an imaginary sweep line just right to the real one to re-insert
            // segments with current event point as its left endpoint,
            // or containing current event point.
            // ( Be careful with this, and we'll reset x-coordinates of verticals to its original )
            if ( isVerticalSweepLine )
                status.setXAndY( updatedCoor, Math.max( maxCoorLeft, maxCoorInterior ) + OFFSET );
            else
                status.setXAndY( Math.max( maxCoorLeft, maxCoorInterior ) + OFFSET, updatedCoor );
            assert !isVerticalSweepLine || MyMath.doubleCompare( status.y, status.shape.getEndPoint().y ) <= 0 : status.y + " " + status.shape;
            assert isVerticalSweepLine || MyMath.doubleCompare( status.x, status.shape.getEndPoint().x ) <= 0 : status.x + " " + status.shape;
            statusRBTree.put( status );
        }

        return specials;
    }

    /**
     * detect New Event
     * */

    private void detectNewEvent( List<EventPoint2D> L,
                                 List<EventPoint2D> I ) {

        List<EventPoint2D> leftsAndInteriors = new ArrayList<>( L.size() + I.size() + 1 );
        leftsAndInteriors.addAll( L );
        leftsAndInteriors.addAll( I );

        // if L(p) ∪ C(p) = null
        // then Let sl and sr be the left and right neighbors of p in T.
        if ( leftsAndInteriors.isEmpty() ) {
            // FINDNEWEVENT(sl, sr, p)
            findNewEvent( statusRBTree.lowerVal( e ), statusRBTree.higherVal( e ) );
            return;
        }

        // TODO: 6/18/2021 need the following one?
        //  maybe it could be done in O(n) or O(1)
        leftsAndInteriors.sort( isVerticalSweepLine ? Vectors::sortByY : Vectors::sortByX );
        // else Let s' be the lowest segment of U(p) ∪ C(p) in T.
        // Let sl be the lower neighbor of s' in T.
        // FINDNEWEVENT(sl, s', p)
        EventPoint2D lowest = leftsAndInteriors.get( 0 );
        findNewEvent( statusRBTree.lowerVal( lowest ), lowest );
        // Let s'' be the highest segment of U(p) ∪ C(p) in T.
        // Let sr be the higher neighbor of s'' in T.
        // FINDNEWEVENT(s'', sr, p)
        EventPoint2D highest = leftsAndInteriors.get( leftsAndInteriors.size() - 1 );
        findNewEvent( statusRBTree.higherVal( highest ), highest );
    }

    /**
     * handle Event Point
     * */

    private void handleEventPoint() {

        // Let L(p) be the set of segments whose leftmost endpoint is p;
        // these segments are stored with the event point p.
        // (For verticals segments,
        // the leftmost endpoint is by definition the lowest endpoint.)
        List<EventPoint2D> L = new ArrayList<>();
        for ( IntersectionShape shape : e.shapes )
            L.add( new EventPoint2D( e.x, e.y, shape, true ) );

        // Find all segments stored in T that contain p;
        // they are adjacent in T.
        List<EventPoint2D> R = new ArrayList<>();
        List<EventPoint2D> I = new ArrayList<>();
        findRightsAndInteriors( R, I );

        // if L(p) ∪ R(p) ∪ C(p) contains more than one segment
        // then Report p as an intersection, together with L(p), R(p), and C(p).
        if ( L.size() + R.size() + I.size() > 1 )
            reportIntersection( L, R, I );

        List<EventPoint2D> specials = reinsertLeftsAndInteriors( L, I );

        detectNewEvent( L, I );

        // reset x-coordinates of verticals to its original
        specials.forEach( isVerticalSweepLine ? EventPoint2D::resetVerticalX : EventPoint2D::resetVerticalY );
    }

    /**
     * find Rights And Interiors
     * */

    private void findRightsAndInteriors( List<EventPoint2D> rights,
                                         List<EventPoint2D> interiors ) {

        EventPoint2D point = null;
        // Delete the segments in L(p) ∪ C(p) from T.
        while ( ( point = ( EventPoint2D ) statusRBTree.deleteAndGetVal( e ) ) != null ) {

            for ( IntersectionShape s : point.shapes ) {
                // Let R(p) denote the subset of segments found
                // whose rightmost endpoint is p,
                if ( s.isSameEndPoint( e ) )
                    rights.add( new EventPoint2D( point, s, false ) );
                // and let C(p) denote the subset of segments found
                // that contain p in their interior.
                else {
                    assert s.ifOnThisShape( e ) : point + " " + e;
                    interiors.add( new EventPoint2D( point, s, false ) );
                }
            }
        }
    }

    /**
     * Get the set of intersection points among the shapes in S. ( Bentley Ottmann )
     * Shapes to be reported intersection must implement {@link IntersectionShape}.
     *
     * @return intersection point set, {@link EventPoint2D}, with shapes involving it.
     *         But they have been cast to {@link Vector}.
     * */

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm FINDINTERSECTIONS(S)
    // Input. A set S of line segments in the plane.
    // Output. The set of intersection points among the segments in S,
    // with for each intersection point the segments that contain it.
    public List<Vector> findIntersection( List<IntersectionShape> S ) {
        intersections = new ArrayList<>();
        if ( S == null ) return intersections;

        // Initialize an empty event queue Q.
        eventQueue = new EventRBTree( isVerticalSweepLine ? Vectors::sortByX : Vectors::sortByY );
        eventQueue.isVerticalSweepLine = isVerticalSweepLine;
        // Next, insert the segment endpoints into Q;
        // when an upper endpoint is inserted,
        // the corresponding segment should be stored with it.
        eventQueue.addEvents( S );
        // Initialize an empty status structure T.
        statusRBTree = new StatusRBTree( isVerticalSweepLine ? Vectors::sortByY : Vectors::sortByX );
        statusRBTree.isVerticalSweepLine = isVerticalSweepLine;
        // while Q is not empty
        while ( !eventQueue.isEmpty() ) {
            // do Determine the next event point p in Q and delete it.
            e = ( EventPoint2D ) eventQueue.deleteMinAndGetVal();
            // HANDLEEVENTPOINT(p)
            handleEventPoint();
        }

        return intersections;
    }
}
