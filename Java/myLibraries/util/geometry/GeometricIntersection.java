package myLibraries.util.geometry;

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

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.*;
import myLibraries.util.geometry.elements.InterLine;
import myLibraries.util.geometry.elements.Event;
import myLibraries.util.geometry.elements.EventPoint2D;
import myLibraries.util.geometry.elements.Vector;
import myLibraries.util.geometry.elements.Arc;
import myLibraries.util.geometry.elements.Circle;
import myLibraries.util.geometry.elements.Line;

import java.util.*;

/**
 * This class consists exclusively of static methods
 * that related to Geometric Intersection
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class GeometricIntersection {

    // TODO: 1/31/2022 put intersection methods in class Line, Circle, Parabola

    //-------------------------------------------------------
    // line and circle
    //-------------------------------------------------------

    /**
     * get the intersection point of line and arc with vector, if exists
     */

    public static Line lineArcIntersect( Line line, Arc arc ) {
        if ( line == null || arc == null ) return null;

        // get the intersection points of the cycle of the arc and the line
        Line segment = lineCircleIntersect( line, arc );
        if ( segment == null ) return null;

        Vector intersection1 = null;
        Vector intersection2 = null;
        // those points lay on the arc?
        // note that there are at most two intersection points, not only one
        if ( arc.belong( segment.startPoint ) )
            intersection1 = segment.startPoint;
        if ( arc.belong( segment.endPoint ) )
            intersection2 = segment.endPoint;

        return new Line( intersection1, intersection2, arc );
    }

    /**
     * get the intersection point of line and circle with vector, if exists
     *
     * Reference resource:
     * @see <a href=https://blog.csdn.net/qq_40998706/article/details/87521165>portal gate</a>
     */

    public static
    Line lineCircleIntersect( Line line, Circle circle ) {
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
        return new Line( projectPoint.add( distance ), projectPoint.subtract( distance ), circle );
    }

    //-------------------------------------------------------
    // Bentley Ottmann
    //-------------------------------------------------------

    private static
    void findNewEvent( Vector intersection,
                       EventRBTree eventQueue, EventPoint2D eventPoint ) {
        // if sl and sr intersect right to the sweep line,
        // or on it and above of the current event point p,
        // and the intersection is not yet present as an event in Q
        // then Insert the intersection point as an event into Q.
        if ( intersection != null &&
                MyMath.doubleCompare( intersection.x, eventPoint.x ) >= 0 ) {
            EventPoint2D intersectionEvent = new EventPoint2D( intersection,
                    null, EventPoint2D.EventType.OTHER );
            eventQueue.put( intersectionEvent );
        }
    }

    /**
     * find New Event
     * */

    private static
    void findNewEvent( Event lineLeft, Event lineRight,
                       EventPoint2D eventPoint, EventRBTree eventQueue ) {
        if ( lineLeft == null || lineRight == null ) return;
        // find possible intersections
        Vector[] intersections = ( ( IntersectionShape ) lineLeft.shape ).findIntersection( ( IntersectionShape ) lineRight.shape );
        if ( intersections == null ) return;
        assert intersections.length < 3;

        // line intersection, 1 intersection at most
        findNewEvent( intersections[ 0 ], eventQueue, eventPoint );

        if ( intersections.length == 1 ) return;

        // line and cycle intersection, 2 intersection at most
        findNewEvent( intersections[ 1 ], eventQueue, eventPoint );
    }

    /**
     * find Rights And Interiors
     * */

    private static
    void findRightsAndInteriors( EventPoint2D eventPoint, StatusRBTree statusRBTree,
                                 List<EventPoint2D> rights, List<EventPoint2D> interiors ) {
        EventPoint2D point = null;
        // Delete the segments in L(p) ∪ C(p) from T.
        while ( ( point = ( EventPoint2D ) statusRBTree.deleteAndGetVal( eventPoint ) ) != null ) {
            // Let R(p) denote the subset of segments found
            // whose rightmost endpoint is p,
            if ( ( ( IntersectionShape ) point.shape ).isSameEndPoint( eventPoint ) )
                rights.add( point );
            // and let C(p) denote the subset of segments found
            // that contain p in their interior.
            else {
                assert ( ( IntersectionShape ) point.shape ).ifOnThisShape( eventPoint ) : point + " " + eventPoint;
                interiors.add( point );
            }
        }
    }

    /**
     * report Intersection, where it is and which shapes intersect
     * */

    private static
    void reportIntersection( EventPoint2D eventPoint, List<EventPoint2D> lefts,
                             List<EventPoint2D> rights, List<EventPoint2D> interiors,
                             List<EventPoint2D> intersections ) {
        // cycles cannot have intersection for this problem
        if ( IntersectionShape.isAllTheCycles( lefts ) &
                IntersectionShape.isAllTheCycles( rights ) &
                IntersectionShape.isAllTheCycles( interiors ) ) return;

        EventPoint2D intersection = new EventPoint2D( eventPoint.x, eventPoint.y,
                null, EventPoint2D.EventType.OTHER );
        // add intersecting shapes
        intersection.reportIntersection( lefts );
        intersection.reportIntersection( rights );
        intersection.reportIntersection( interiors );
        // add intersection
        intersections.add( intersection );
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
    private static
    double reinsertLeftsAndInteriors( StatusRBTree statusRBTree, List<EventPoint2D> statuses,
                                      double updatedX, List<EventPoint2D> verticals ) {

        double maxY = -Double.MAX_VALUE;
        boolean isOnlyVertical = true;
        for ( EventPoint2D status : statuses ) {
            // vertical lines
            if ( ( ( IntersectionShape ) status.shape ).getShapeType() == IntersectionShape.ShapeType.LINE &&
                    ( (InterLine ) status.shape ).isVertical ) {
                    verticals.add( status );
                    continue;
            }
            isOnlyVertical = false;

            // non-vertical lines or cycles
            status.updateY( updatedX );
            maxY = Math.max( maxY, status.y );
            statusRBTree.put( status );
        }

        // if statues only have one vertical line,
        // should return maxY = y of left endpoint of this vertical line
        if ( !verticals.isEmpty() &&
                isOnlyVertical ) {
            assert verticals.size() == 1;
            InterLine line = ( ( InterLine ) verticals.get( 0 ).shape );
            assert line.isVertical;
            maxY = line.startPoint.y;
        }

        return maxY;
    }

    /**
     * reinsert Lefts And Interiors into statusRBTree
     * */

    // (Deleting and re-inserting the segments of C(p) reverses their order.)
    private static
    List<EventPoint2D> reinsertLeftsAndInteriors( EventPoint2D eventPoint, StatusRBTree statusRBTree,
                                    List<EventPoint2D> lefts, List<EventPoint2D> interiors ) {
        // TODO: 9/19/2021 Choice of the offset is very tricky,
        //  because if two lines are very close to each other,
        //  the program will think those two intersect
        //  when updating them in the status tree, but they don't in reality.
        //  See test case 35, we will have error if the offset is MyMath.EPSILON * 10,
        //  but get correct answer if it is MyMath.EPSILON * 100
        final double OFFSET = MyMath.EPSILON * 100;
        // just right to the sweeping line,
        // so only smaller value to offset x coordinate
        final double updatedX = eventPoint.x + OFFSET;
        final List<EventPoint2D> verticals = new ArrayList<>();

        // Insert the segments in L(p) ∪ C(p) into T.
        // The order of the segments in T should correspond
        // the order in which they are intersected
        // by a sweep line just right to p.
        double maxYLefts = reinsertLeftsAndInteriors( statusRBTree, lefts, updatedX, verticals );
        double maxYInteriors = reinsertLeftsAndInteriors( statusRBTree, interiors, updatedX, verticals );

        // insert all vertical lines in my implementation,
        // while insert all horizontal lines in the textbook's context
        for ( EventPoint2D status : verticals ) {
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
            status.setXAndY( updatedX, Math.max( maxYLefts, maxYInteriors ) + OFFSET );
            assert MyMath.doubleCompare( status.y, ( ( IntersectionShape ) status.shape ).getEndPointY() ) <= 0 : status.y + " " + status.shape;
            statusRBTree.put( status );
        }

        return verticals;
    }

    /**
     * detect New Event
     * */

    private static
    void detectNewEvent( List<EventPoint2D> lefts, List<EventPoint2D> interiors,
                         EventRBTree eventQueue, StatusRBTree statusRBTree, EventPoint2D eventPoint ) {
        int totalNumber = lefts.size() + interiors.size();
        List<EventPoint2D> leftsAndInteriors = new ArrayList<>( totalNumber + 1 );
        leftsAndInteriors.addAll( lefts );
        leftsAndInteriors.addAll( interiors );

        // if L(p) ∪ C(p) = null
        // then Let sl and sr be the left and right neighbors of p in T.
        if ( leftsAndInteriors.isEmpty() ) {
            // FINDNEWEVENT(sl, sr, p)
            findNewEvent( statusRBTree.lowerVal( eventPoint ), statusRBTree.higherVal( eventPoint ),
                    eventPoint, eventQueue );
            return;
        }

        // TODO: 6/18/2021 need the following one?
        //  maybe it could be done in O(n) or O(1)
        leftsAndInteriors.sort( Vectors::sortByY );
        // else Let s' be the lowest segment of U(p) ∪ C(p) in T.
        // Let sl be the lower neighbor of s' in T.
        // FINDNEWEVENT(sl, s', p)
        EventPoint2D lowest = leftsAndInteriors.get( 0 );
        findNewEvent( statusRBTree.lowerVal( lowest ), lowest, eventPoint, eventQueue );
        // Let s'' be the highest segment of U(p) ∪ C(p) in T.
        // Let sr be the higher neighbor of s'' in T.
        // FINDNEWEVENT(s'', sr, p)
        EventPoint2D highest = leftsAndInteriors.get( leftsAndInteriors.size() - 1 );
        findNewEvent( statusRBTree.higherVal( highest ), highest, eventPoint, eventQueue );
    }

    /**
     * handle Event Point
     * */

    private static
    void handleEventPoint( EventPoint2D eventPoint,
                           StatusRBTree statusRBTree,
                           List<EventPoint2D> intersections,
                           EventRBTree eventQueue) {
        // Let L(p) be the set of segments whose leftmost endpoint is p;
        // these segments are stored with
        // the event point p. (For verticals segments,
        // the leftmost endpoint is by definition the lowest endpoint.)
        List<EventPoint2D> lefts = new ArrayList<>();
        for ( UpdateCoordinatesShape shape : eventPoint.shapes )
            lefts.add( new EventPoint2D( eventPoint.x, eventPoint.y, ( IntersectionShape ) shape, EventPoint2D.EventType.LEFT ) );

        // Find all segments stored in T that contain p; they are adjacent in T.
        List<EventPoint2D> rights = new ArrayList<>();
        List<EventPoint2D> interiors = new ArrayList<>();
        findRightsAndInteriors( eventPoint, statusRBTree, rights, interiors );

        // if L(p) ∪ R(p) ∪ C(p) contains more than one segment
        // then Report p as an intersection, together with L(p), R(p), and C(p).
        if ( lefts.size() + rights.size() + interiors.size() > 1 )
            reportIntersection( eventPoint, lefts, rights, interiors, intersections );

        List<EventPoint2D> verticals = reinsertLeftsAndInteriors( eventPoint, statusRBTree, lefts, interiors );

        detectNewEvent( lefts, interiors, eventQueue, statusRBTree, eventPoint );

        // reset x-coordinates of verticals to its original
        verticals.forEach( EventPoint2D::resetVerticalX );
    }

    /**
     * add initialized events into the event queue
     *
     * Circle: be processed as four monotone arcs, lower and upper endpoints for each arc
     * Segments: lower and upper endpoints for each segment
     * */

    private static
    void addEvents( EventRBTree eventQueue,
                    List<IntersectionShape> shapes ) {
        for ( IntersectionShape shape : shapes ) {
            EventPoint2D[] events = shape.preprocess();
            for ( EventPoint2D event : events )
                eventQueue.put( event );
        }
    }

    /**
     * Bentley Ottmann
     *
     * Reference resource:
     * @see <a href=http://www.cs.uu.nl/geobook/>Computational Geometry: Algorithms and Applications</a>
     * */

    // Algorithm FINDINTERSECTIONS(S)
    // Input. A set S of line segments in the plane.
    // Output. The set of intersection points among the segments in S,
    // with for each intersection point the segments that contain it.
    public static
    List<EventPoint2D> findIntersection( List<IntersectionShape> shapes ) {
        List<EventPoint2D> intersections = new ArrayList<>();
        if ( shapes == null ) return intersections;

        // Initialize an empty event queue Q.
        EventRBTree eventQueue = new EventRBTree( Vectors::sortByX );
        // Next, insert the segment endpoints into Q;
        // when an upper endpoint is inserted,
        // the corresponding segment should be stored with it.
        addEvents( eventQueue, shapes );
        // Initialize an empty status structure T.
        StatusRBTree statusRBTree = new StatusRBTree( Vectors::sortByY );
        // while Q is not empty
        while ( !eventQueue.isEmpty() ) {
            // do Determine the next event point p in Q and delete it.
            // HANDLEEVENTPOINT(p)
            handleEventPoint( ( EventPoint2D ) eventQueue.deleteMinAndGetVal(),
                    statusRBTree, intersections, eventQueue );
        }

        return intersections;
    }

    /**
     * brute Force Lines Intersection
     * */

    public static
    TreeSet<Vector> bruteForceLinesIntersection( List<Line> lines ) {
        TreeSet<Vector> intersections = new TreeSet<>( Vectors::sortByX );
        bruteForceLinesIntersection( lines, intersections );

        return intersections;
    }

    /**
     * brute Force Lines Intersection
     * */

    public static
    void bruteForceLinesIntersection( List<Line> lines, TreeSet<Vector> intersections ) {
        for ( int i = 0; i < lines.size(); i++ ) {
            Line line1 = lines.get( i );
            for ( int j = i + 1; j < lines.size(); j++ ) {
                Line line2 = lines.get( j );
                Vector intersection = line1.segmentIntersect( line2 );
                if ( intersection != null ) intersections.add( intersection );
            }
        }
    }

    /**
     * brute Force Lines and cycles Intersection
     * */

    public static
    TreeSet<Vector> bruteForceLineCycleIntersection( List<Line> lines, List<Circle> cycles ) {
        TreeSet<Vector> intersections = new TreeSet<>( Vectors::sortByX );
        // line intersections
        bruteForceLinesIntersection( lines, intersections );

        // line and cycle intersections
        for ( Circle cycle : cycles ) {
            for ( Line line : lines ) {
                Line intersection = lineCircleIntersect( line, cycle );
                if ( intersection == null ) continue;

                if ( intersection.startPoint != null &&
                        line.isOnThisSegment( intersection.startPoint ) ) {
                    intersections.add( intersection.startPoint );
                }

                if ( intersection.endPoint != null &&
                        line.isOnThisSegment( intersection.endPoint ) ) {
                    intersections.add( intersection.endPoint );
                }
            }
        }

        return intersections;
    }

    static
    void testSort() {
        int ID = 0;
        Vector point1 = new Vector( 0, 0, ID++ );
        Vector point2 = new Vector( 1, 0, ID++ );
        Vector point3 = new Vector( 1, 1, ID++ );
        Vector point4 = new Vector( 0, 1, ID++ );
        Vector point5 = new Vector( -1, 1, ID++ );
        Vector point6 = new Vector( -1, 0, ID++ );
        Vector point7 = new Vector( -1, -1, ID++ );
        Vector point8 = new Vector( 0, -1, ID++ );
        Vector point9 = new Vector( 1, -1, ID++ );
        Vector point10 = new Vector( 0, 0, ID++ );

//        List<Vector> points = new ArrayList<>();
//        points.add( point1 );
//        points.add( point2 );
//        points.add( point3 );
//        points.add( point4 );
//        points.add( point5 );
//
//        System.out.println( points );
//        points.sort( Vector::sortByX );
//        System.out.println( points );
//
//        System.out.println( points );
//        points.sort( Vector::sortByY );
//        System.out.println( points );

//        System.out.println( Integer.MAX_VALUE );
//        System.out.println( Long.MAX_VALUE );
//        System.out.println( Float.MAX_VALUE );
//        System.out.println( Double.MAX_VALUE );
//
//        System.out.println( Long.MAX_VALUE - Float.MAX_VALUE );
//        System.out.println( Long.MIN_VALUE - Float.MIN_VALUE );
    }

    static
    void testSegmentIntersection() {
        int ID = 0;
        Vector vector1 = new Vector( 1, 0, ID++ );
        Vector vector2 = new Vector( -1, 0, ID++ );
        Vector vector3 = new Vector( 0, -1, ID++ );
        Vector vector4 = new Vector( 0, 1, ID++ );
        Vector vector5 = new Vector( 1, 1, ID++ );
        Vector vector6 = new Vector( -1, -1, ID++ );

        Line line1 = new Line( vector1, vector2 );
        Line line2 = new Line( vector3, vector4 );
        System.out.println( line1.segmentIntersect( line2 ) ); // 0,0

        Line line3 = new Line( vector6, vector5 );
        System.out.println( line1.segmentIntersect( line3 ) ); // 0,0
        System.out.println( line2.segmentIntersect( line3 ) ); // 0,0

        Vector vector7 = new Vector( 0, 3, ID++ );
        Vector vector8 = new Vector( 3, 0, ID++ );
        Vector vector9 = new Vector( 3, 3, ID++ );

        Line line4 = new Line( vector7, vector8 );
        Line line5 = new Line( vector9, vector3 );
        System.out.println( line4.segmentIntersect( line5 ) ); // 1.7142857,1.2857143
        System.out.println( line5.segmentIntersect( line4 ) ); // 1.7142857,1.2857143

        Vector vector10 = new Vector( 4, 3, ID++ );
        Line line6 = new Line( vector10, vector5 );

        System.out.println( line3.segmentIntersect( line6 ) ); // 1.0,1.0
        System.out.println( line6.segmentIntersect( line6 ) ); // null

        Vector vector11 = new Vector( 4, 2, ID++ );
        Line line7 = new Line( vector11, vector1 );
        System.out.println( line7.segmentIntersect( line6 ) ); // null
        System.out.println( line6.segmentIntersect( line6 ) ); // null

        Vector vector12 = new Vector( 1, -1, ID++ );
        Vector vector13 = new Vector( 1, 1, ID++ );
        Vector vector14 = new Vector( -1, 2, ID++ );
        Vector vector15 = new Vector( 2, 2, ID++ );
        Vector vector16 = new Vector( -1, 2, ID++ );

        Line line8 = new Line( vector12, vector13 );
        Line line9 = new Line( vector14, vector13 );
        Line line10 = new Line( vector15, vector13 );
        Line line11 = new Line( vector16, vector13 );

        System.out.println( line8.segmentIntersect( line9 ) ); // 1.0,1.0
        System.out.println( line10.segmentIntersect( line9 ) ); // 1.0,1.0
        System.out.println( line10.segmentIntersect( line11 ) ); // 1.0,1.0
        System.out.println( line8.segmentIntersect( line11 ) ); // 1.0,1.0
    }

    static
    void testSegmentCycleIntersection() {
        int ID = 0;
        final double radius = Math.sqrt( 2 );
        Vector vector1 = new Vector( -radius, 0, ID++ );
        Vector vector2 = new Vector( radius, 0, ID++ );
        Vector vector3 = new Vector( -radius, -1, ID++ );
        Vector vector4 = new Vector( 0, radius, ID++ );
        Vector vector5 = new Vector( 1, radius, ID++ );
        Vector vector6 = new Vector( 1, 1, ID++ );
        Vector vector7 = new Vector( -1, -1, ID++ );
        Vector vector8 = new Vector( -1, 1, ID++ );
        Vector vector9 = new Vector( 0, 2, ID++ );
        Line line1 = new Line( vector1, vector3 );
        Line line2 = new Line( vector6, vector7 );
        Line line3 = new Line( vector4, vector5 );
        Line line4 = new Line( vector9, vector8 );

        Circle cycle1 = new Circle( Vector.origin, radius );
//        System.out.println( lineCycleIntersect( line1, cycle1 ) ); // -11->-1.4142135|0.0 -12->-1.4142135|0.0
//        System.out.println( lineCycleIntersect( line2, cycle1 ) ); // -23->-0.99999994|-0.99999994|-24->0.99999994|0.99999994
//        System.out.println( lineCycleIntersect( line3, cycle1 ) ); // -35->0.0|1.4142135	-36->0.0|1.4142135
//        System.out.println( lineCycleIntersect( line4, cycle1 ) ); // -11->-1.0002441|0.99975586	-12->-0.99975586|1.0002441

        Circle cycle2 = new Circle( Vector.origin, 2 );
        Line line5 = new Line( vector8, vector6 );
        System.out.println( lineCircleIntersect( line5, cycle2 ) );
    }

    static
    void testSegmentArcIntersection() {
        int ID = 0;
        double radius = Math.sqrt( 2 );
        Vector vector1 = new Vector( -radius, 0, ID++ );
        Vector vector2 = new Vector( 0, 0, ID++ );
        Vector vector3 = new Vector( -radius, -1, ID++ );
        Vector vector4 = new Vector( 0, radius, ID++ );
        Vector vector5 = new Vector( 1, radius, ID++ );
        Vector vector6 = new Vector( 1, 1, ID++ );
        Vector vector7 = new Vector( -1, -1, ID++ );
        Vector vector8 = new Vector( -1, 1, ID++ );
        Vector vector9 = new Vector( 0, 2, ID++ );
        Line line1 = new Line( vector1, vector3 );
        Line line2 = new Line( vector6, vector7 );
        Line line3 = new Line( vector4, vector5 );
        Line line4 = new Line( vector9, vector8 );
        Line line5 = new Line( vector2, vector8 );
        Circle cycle = new Circle( Vector.origin, radius );

        int index = 0;
        Line[] lines = new Line[ 5 ];
        lines[ index++ ] = line1;
        lines[ index++ ] = line2;
        lines[ index++ ] = line3;
        lines[ index++ ] = line4;
        lines[ index++ ] = line5;

        Arc[] arcs = cycle.getFourQuarters();

        for ( int i = 0; i < lines.length; i++ ) {
            System.out.println( "lines(" + ( i + 1 ) + "): " + lines[ i ] );
            for ( int j = 0; j < arcs.length; j++ ) {
                System.out.println( "arc" + ( j + 1 ) + ": " + lineArcIntersect( lines[ i ], arcs[ j ] ) );
            }
            System.out.println();
        }

//        for ( int j = 0; j < arcs.length; j++ ) {
//            System.out.println( "arc" + ( j + 1 ) + ": " + lineArcIntersect( lines[ 3 ], arcs[ j ] ) );
//        }
//        System.out.println( lineArcIntersect( lines[ 3 ], arcs[ 0 ] ) );
    }

    static
    void testOthers() {
        int index = 0;
        PriorityQueue<EventPoint2D> events = new PriorityQueue<>( Vectors::sortByX );
        Vector point1 = new Vector( -1, -1, index++ );
        Vector point2 = new Vector( -1, 1, index++ );
        Vector point3 = new Vector( 1, 1, index++ );
        Vector point4 = new Vector( 1, -1, index++ );
        Line line1 = new Line( point1, point3 );
        Line line2 = new Line( point2, point4 );
        List<Line> lines = new ArrayList<>();
        lines.add( line1 );
        lines.add( line2 );
//        List<EventPoint2D> list = generateEvents( lines );
//        events.addAll( list );
//        System.out.println( events );
//        while ( !events.isEmpty() ) {
//            System.out.println( events.poll() );
//        }
    }

    static
    void testRayLine() {
        int index = 0;
        Vector point1 = new Vector( 8, 3, index++ );
        Vector point2 = new Vector( 5, 4, index++ );
        Vector point3 = new Vector( 1, 1, index++ );
        Vector point4 = new Vector( -1, -1, index++ );
        Line line1 = new Line( point1, point2 );
        Line line2 = new Line( point3, point4 );
        System.out.println( line1.segmentIntersect( line2 ) );
    }

    static
    void testLineIntersection() {
        Line line1 = new Line( 2, 2, 6, 6 );
        Line line2 = new Line( 1, 8, 2, 6 );
//        System.out.println( GeometricIntersection.segmentIntersect( line1, line2 ) );
//        System.out.println( GeometricIntersection.lineIntersect( line1, line2 ) );

        line1 = new Line( 3, 1 , 2 );
        line2 = new Line( 4, 2, 0 );
        System.out.println( line1.lineIntersect( line2 ) );

        line1 = new Line( -2.3333333333333335, 1 , 1.3333333333333333 );
        line2 = new Line( -0.3333333333333333, 1, 1.6666666666666665 );
        System.out.println( line1.lineIntersect( line2 ) );
    }

    public static
    void main( String[] args ) {
//        testSegmentIntersection();
//        testSegmentCycleIntersection();
//        testSegmentArcIntersection();
//        testOthers();
//        testRayLine();
        testLineIntersection();
    }
}
;
