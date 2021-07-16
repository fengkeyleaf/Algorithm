package myLibraries.util.geometry.tools;

/*
 * GeometricIntersection.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.*;
import myLibraries.util.geometry.elements.line.InterLine;
import myLibraries.util.geometry.elements.point.EventPoint2D;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.elements.cycle.Arc;
import myLibraries.util.geometry.elements.cycle.Cycle;
import myLibraries.util.geometry.elements.line.Line;

import java.util.*;

/**
 * Provide algorithms related to Geometric Intersection
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class GeometricIntersection {
    public static int ID = -3;

    /**
     * get the intersection point of line and arc with vector, if exists
     */

    public static Line lineArcIntersect(Line  line, Arc arc ) {
        if ( line == null || arc == null ) return null;

        // get the intersection points of the cycle of the arc and the line
        Line segment = lineCycleIntersect( line, arc );
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
     * get the intersection point of line and cycle with vector, if exists
     *
     * Reference resource:
     * https://blog.csdn.net/qq_40998706/article/details/87521165
     */

    public static
    Line lineCycleIntersect( Line line, Cycle cycle ) {
        // have intersectin point?
        if ( line == null || cycle == null
                || !cycle.ifIntersectsLine( line ) ) return null;

        // get the project point
        Vector projectPoint = line.project( cycle.center );
        Vector lineVector = line.getVector();
        // get e of the line
        Vector e = lineVector.division( lineVector.norm() );
        double sideLength = cycle.radius * cycle.radius -
                projectPoint.subtract( cycle.center ).normWithoutRadical();
        double ratio = Math.sqrt( Math.abs( sideLength ) );
        Vector distance = e.multiply( ratio );
        return new Line( projectPoint.add( distance ), projectPoint.subtract( distance ), cycle );
    }

    public static
    int collectLinesOnTheSameLine( Line line1, Line line2 ) {
        // to left test based on line1.
        double res1 = Triangles.areaTwo( line1.endPoint, line1.startPoint, line2.endPoint );
        double res2 = Triangles.areaTwo( line1.endPoint, line1.startPoint, line2.startPoint );

        // to left test based on line2.
        double res3 = Triangles.areaTwo( line2.endPoint, line2.startPoint, line1.endPoint );
        double res4 = Triangles.areaTwo( line2.endPoint, line2.startPoint, line1.startPoint );
        assert ( MyMath.isEqualZero( res1 ) && MyMath.isEqualZero( res2 ) ) == ( MyMath.isEqualZero( res3 ) && MyMath.isEqualZero( res4 ) );
        return MyMath.isEqualZero( res1 ) && MyMath.isEqualZero( res2 ) ? 0 : 1;
    }

    /**
     * get the common endPoint, intersection as well,
     * if two lines on the same line intersect at one of the endpoints
     */

    private static
    Vector getOnlyCommonEndPoint( Line line1, Line line2 ) {
        if ( line1.startPoint.equalsXAndY( line2.endPoint ) ) return line1.startPoint;

        return line1.endPoint;
    }

    /**
     * if the two lines Overlap But Have Common EndPoint,
     * but note that may only have one common endPoint.
     */

    private static
    boolean isOverlapButHavingCommonEndPoint( Line line1, Line line2 ) {
        return Vectors.sortByX( line2.startPoint, line2.endPoint ) > 0 &&
                line1.startPoint.equalsXAndY( line2.endPoint ) ||
                line1.endPoint.equalsXAndY( line2.startPoint );
    }

    /**
     * toLeft test to check if two lines intersect
     */

    private static
    boolean ifLinesIntersect( Line line1, Line line2,
                              double res1, double res2 ) {
        // parallel cases:
        // case 1: overlap or on the same line.
        if ( MyMath.isEqualZero( res1 ) &&
                MyMath.isEqualZero( res2 ) )
            return isOverlapButHavingCommonEndPoint( line1, line2 );
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
     * line1 and line2 intersects?
     */

    public static
    boolean ifLinesIntersect( Line line1, Line line2 ) {
        if ( line1 == null || line2 == null ) return false;

        // to left test based on line1.
        double res1 = Triangles.areaTwo( line1.endPoint, line1.startPoint, line2.endPoint );
        double res2 = Triangles.areaTwo( line1.endPoint, line1.startPoint, line2.startPoint );
        // to left test based on line2.
        double res3 = Triangles.areaTwo( line2.endPoint, line2.startPoint, line1.endPoint );
        double res4 = Triangles.areaTwo( line2.endPoint, line2.startPoint, line1.startPoint );

        // have intersection if and only if
        // two endpoints of one line are
        // at the opposite side of the other line.
        boolean finalRes1 = ifLinesIntersect( line1, line2, res1, res2 );
        boolean finalRes2 = ifLinesIntersect( line1, line2, res3, res4 );
        return finalRes1 && finalRes2;
    }

    /**
     * get line intersection point with vector, if exists
     *
     * Reference resource:
     * https://blog.csdn.net/qq_40998706/article/details/87482435
     */

    public static
    Vector lineIntersect( Line line1, Line line2 ) {
        // have intersection?
        if ( !ifLinesIntersect( line1, line2 ) ) return null;
        // yes, but the intersection point is one of the endpoints.
        if ( isOverlapButHavingCommonEndPoint( line1, line2 ) )
            return getOnlyCommonEndPoint( line1, line2 );

        // yes, normal intersection point.
        Vector base = line2.getVector();
        double d1 = Math.abs( Vectors.cross(
                base, line1.startPoint.subtract( line2.startPoint ) ) );
        double d2 = Math.abs( Vectors.cross(
                base, line1.endPoint.subtract( line2.startPoint ) ) );
        assert !MyMath.isEqualZero( d1 + d2 );
        double t = d1 / ( d1 + d2 );
        Vector intersection = line1.getVector().multiply( t );

        // the following commented-out code is correct as well,
        // but with less computational accuracy because of ( 1 / t ),
        // one additional division compared to the method above.
//        double t = ( d1 + d2 ) / d1;
//        Vector intersection = line1.endPoint.subtract(
//        line1.startPoint ).multiply( 1 / t );

        return line1.startPoint.add( intersection );
    }

    private static
    void findNewEvent( Vector intersection,
                       EventRBTree eventQueue, EventPoint2D eventPoint ) {
        // if sl and sr intersect below the sweep line,
        // or on it and to the right of the current event point
        // p, and the intersection is not yet present as an event in Q
        // then Insert the intersection point as an event into Q.
        if ( intersection != null &&
                intersection.x >= eventPoint.x ) {
            EventPoint2D intersectionEvent = new EventPoint2D( intersection,
                    null, EventPoint2D.EventType.OTHER );
            eventQueue.put( intersectionEvent );
        }
    }

    /**
     * find New Event
     * */

    private static
    void findNewEvent( EventPoint2D lineLeft, EventPoint2D lineRight,
                      EventRBTree eventQueue, EventPoint2D eventPoint ) {
        if ( lineLeft == null || lineRight == null ) return;
        // find possible intersections
        Vector[] intersections = lineLeft.shape.findIntersection( lineRight.shape );
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
        // Delete the segments in L(p)[C(p) from T.
        while ( ( point = ( EventPoint2D ) statusRBTree.deleteAndGetVal( eventPoint ) ) != null ) {
            // Let L(p) denote the
            // subset of segments found whose lower endpoint is p,
            if ( point.shape.isSameEndPoint( eventPoint ) )
                rights.add( point );
            // and let C(p) denote the subset of
            // segments found that contain p in their interior.
            else {
                assert point.shape.ifOnThisShape( eventPoint );
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

    private static
    double reinsertLeftsAndInteriors( StatusRBTree statusRBTree, List<EventPoint2D> statuses,
                                      double updatedX, List<EventPoint2D> verticals ) {
        double maxY = Double.MIN_VALUE;
        boolean isOnlyVertical = true;
        for ( EventPoint2D status : statuses ) {
//            System.out.println( status );
            // vertical lines
            if ( status.shape.getShapeType() == IntersectionShape.ShapeType.LINE &&
                    ( (InterLine ) status.shape ).isVertical ) {
                    verticals.add( status );
                    continue;
            }
            isOnlyVertical = false;
            // non-vertical lines or cycles
            status.updateY( updatedX );
            maxY = Math.max( maxY, status.y );
//            statusRBTree.put( status );
            statusRBTree.put( status, status );
        }

        // if statues only have one vertical line,
        // should return maxY = y of left endpoint of the line
        if ( !verticals.isEmpty() &&
                isOnlyVertical ) {
            assert verticals.size() == 1;
            maxY = verticals.get( 0 ).y;
        }
        return maxY;
    }

    /**
     * reinsert Lefts And Interiors into statusRBTree
     * */

    // (Deleting and re-inserting the segments of C(p) reverses their order.)
    private static
    void reinsertLeftsAndInteriors( EventPoint2D eventPoint, StatusRBTree statusRBTree,
                                    List<EventPoint2D> lefts, List<EventPoint2D> interiors ) {
        final double OFFSET = 0.05;
        // just below the sweeping line,
        // so only smaller value to offset x coordinate
        final double updatedX = eventPoint.x + OFFSET;
        final List<EventPoint2D> verticals = new ArrayList<>();

        // Insert the segments inU(p)[C(p) into T.
        // The order of the segments in T should correspond
        // the order in which they are intersected
        // by a sweep line just below p.
        double maxYLefts = reinsertLeftsAndInteriors( statusRBTree, lefts, updatedX, verticals );
        double maxYInteriors = reinsertLeftsAndInteriors( statusRBTree, interiors, updatedX, verticals );
        double maxY = Math.max( maxYLefts, maxYInteriors );

        // If there is a horizontal segment,
        // it comes last among all segments containing p.
        // nothing to do with cycles here
        for ( EventPoint2D status : verticals ) {
            // vertical lines are a little bit higher than
            // all shapes in lefts and interiors
            // at updatedX
            status.y = maxY + OFFSET;
            assert status.y <= status.shape.getEndPointY();
//            statusRBTree.put( status );
            statusRBTree.put( status, status );
        }
    }

    /**
     * detect New Event
     * */

    private static
    void detectNewEvent( EventPoint2D predecessor, EventPoint2D successor,
                         List<EventPoint2D> lefts, List<EventPoint2D> interiors,
                         EventRBTree eventQueue, EventPoint2D eventPoint ) {
        int totalNumber = lefts.size() + interiors.size();
        List<EventPoint2D> leftsAndInteriors = new ArrayList<>( totalNumber + 1 );
        leftsAndInteriors.addAll( lefts );
        leftsAndInteriors.addAll( interiors );

        // if U(p)[C(p) = /0
        // then Let sl and sr be the left and right neighbors of p in T.
        if ( leftsAndInteriors.isEmpty() ) {
            // FINDNEWEVENT(sl ; sr; p)
            findNewEvent( predecessor, successor, eventQueue, eventPoint );
            return;
        }

        // TODO: 6/18/2021 need the following one?
        leftsAndInteriors.sort( Vectors::sortByY );
        // else Let s0 be the leftmost segment of U(p)[C(p) in T.
        // Let sl be the left neighbor of s0 in T.
        // FINDNEWEVENT(sl ; s0; p)
        EventPoint2D lowest = leftsAndInteriors.get( 0 );
        findNewEvent( predecessor, lowest, eventQueue, eventPoint );
        // Let s00 be the rightmost segment of U(p)[C(p) in T.
        // Let sr be the right neighbor of s00 in T.
        // FINDNEWEVENT(s0; sr; p)
        EventPoint2D highest = leftsAndInteriors.get( leftsAndInteriors.size() - 1 );
        findNewEvent( successor, highest, eventQueue, eventPoint );
    }

    /**
     * handle Event Point
     * */

    private static
    void handleEventPoint( EventPoint2D eventPoint,
                           StatusRBTree statusRBTree,
                           List<EventPoint2D> intersections,
                           EventRBTree eventQueue) {
        // find sl and sr
        EventPoint2D predecessor = ( EventPoint2D ) statusRBTree.lowerVal( eventPoint );
        EventPoint2D successor = ( EventPoint2D ) statusRBTree.higherVal( eventPoint );

        // Let U(p) be the set of segments whose upper endpoint is p;
        // these segments are stored with
        // the event point p. (For horizontal segments,
        // the upper endpoint is by definition the left endpoint.)
        List<EventPoint2D> lefts = new ArrayList<>();
        for ( IntersectionShape shape : eventPoint.shapes )
            lefts.add( new EventPoint2D( eventPoint.x, eventPoint.y, shape, EventPoint2D.EventType.LEFT ) );

        // Find all segments stored in T that contain p;
        // they are adjacent in T.
        List<EventPoint2D> rights = new ArrayList<>();
        List<EventPoint2D> interiors = new ArrayList<>();
        findRightsAndInteriors( eventPoint, statusRBTree, rights, interiors );

        // if L(p)[U(p)[C(p) contains more than one segment
        // then Report p as an intersection, together with L(p), U(p), and C(p).
        if ( lefts.size() + rights.size() + interiors.size() > 1 )
            reportIntersection( eventPoint, lefts, rights, interiors, intersections );

        reinsertLeftsAndInteriors( eventPoint, statusRBTree, lefts, interiors );

        detectNewEvent( predecessor, successor, lefts, interiors, eventQueue, eventPoint);
    }

    /**
     * add initialized events into the event queue
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
     * http://www.cs.uu.nl/geobook/
     * */

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
            handleEventPoint( eventQueue.deleteMinAndGetVal(),
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
                Vector intersection = lineIntersect( line1, line2 );
                if ( intersection != null ) intersections.add( intersection );
            }
        }
    }

    /**
     * brute Force Lines and cycles Intersection
     * */

    public static
    TreeSet<Vector> bruteForceLineCycleIntersection( List<Line> lines, List<Cycle> cycles ) {
        TreeSet<Vector> intersections = new TreeSet<>( Vectors::sortByX );
        // line intersections
        bruteForceLinesIntersection( lines, intersections );

        // line and cycle intersections
        for ( Cycle cycle : cycles ) {
            for ( Line line : lines ) {
                Line intersection = lineCycleIntersect( line, cycle );
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

    public static
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

    public static
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
        System.out.println( lineIntersect( line1, line2 ) ); // 0,0
        Line line3 = new Line( vector6, vector5 );
        System.out.println( lineIntersect( line1, line3 ) ); // 0,0
        System.out.println( lineIntersect( line2, line3 ) ); // 0,0
        Vector vector7 = new Vector( 0, 3, ID++ );
        Vector vector8 = new Vector( 3, 0, ID++ );
        Vector vector9 = new Vector( 3, 3, ID++ );
        Line line4 = new Line( vector7, vector8 );
        Line line5 = new Line( vector9, vector3 );
        System.out.println( lineIntersect( line4, line5 ) ); // 1.7142857,1.2857143
        System.out.println( lineIntersect( line5, line4 ) ); // 1.7142857,1.2857143
        Vector vector10 = new Vector( 4, 3, ID++ );
        Line line6 = new Line( vector10, vector5 );
        System.out.println( lineIntersect( line3, line6 ) ); // 1.0,1.0
        System.out.println( lineIntersect( line6, line6 ) ); // null
        Vector vector11 = new Vector( 4, 2, ID++ );
        Line line7 = new Line( vector11, vector1 );
        System.out.println( lineIntersect( line7, line6 ) ); // null
        System.out.println( lineIntersect( line6, line6 ) ); // null
        Vector vector12 = new Vector( 1, -1, ID++ );
        Vector vector13 = new Vector( 1, 1, ID++ );
        Vector vector14 = new Vector( -1, 2, ID++ );
        Vector vector15 = new Vector( 2, 2, ID++ );
        Vector vector16 = new Vector( -1, 2, ID++ );
        Line line8 = new Line( vector12, vector13 );
        Line line9 = new Line( vector14, vector13 );
        Line line10 = new Line( vector15, vector13 );
        Line line11 = new Line( vector16, vector13 );
        System.out.println( lineIntersect( line8, line9 ) ); // 1.0,1.0
        System.out.println( lineIntersect( line10, line9 ) ); // 1.0,1.0
        System.out.println( lineIntersect( line10, line11 ) ); // 1.0,1.0
        System.out.println( lineIntersect( line8, line11 ) ); // 1.0,1.0
    }

    public static
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

        Cycle cycle1 = new Cycle( Vector.origin, radius );
//        System.out.println( lineCycleIntersect( line1, cycle1 ) ); // -11->-1.4142135|0.0 -12->-1.4142135|0.0
//        System.out.println( lineCycleIntersect( line2, cycle1 ) ); // -23->-0.99999994|-0.99999994|-24->0.99999994|0.99999994
//        System.out.println( lineCycleIntersect( line3, cycle1 ) ); // -35->0.0|1.4142135	-36->0.0|1.4142135
//        System.out.println( lineCycleIntersect( line4, cycle1 ) ); // -11->-1.0002441|0.99975586	-12->-0.99975586|1.0002441

        Cycle cycle2 = new Cycle( Vector.origin, 2 );
        Line line5 = new Line( vector8, vector6 );
        System.out.println( lineCycleIntersect( line5, cycle2 ) );
    }

    public static
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
        Cycle cycle = new Cycle( Vector.origin, radius );

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

    public static
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

    public static
    void testRayLine() {
        int index = 0;
        Vector point1 = new Vector( 8, 3, index++ );
        Vector point2 = new Vector( 5, 4, index++ );
        Vector point3 = new Vector( 1, 1, index++ );
        Vector point4 = new Vector( -1, -1, index++ );
        Line line1 = new Line( point1, point2 );
        Line line2 = new Line( point3, point4 );
        System.out.println( GeometricIntersection.lineIntersect( line1, line2 ) );
    }

    public static
    void main( String[] args ) {
//        testSegmentIntersection();
        testSegmentCycleIntersection();
//        testSegmentArcIntersection();
//        testOthers();
//        testRayLine();
    }
}
;
