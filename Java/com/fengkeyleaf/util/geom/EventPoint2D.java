package com.fengkeyleaf.util.geom;

/*
 * EventPoint2D.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Data structure of Event point for Bentley Ottmann's, Geometric Intersection.
 * With this class, intersection will be reported and also shapes involving the intersection.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class EventPoint2D extends Event {
    final boolean isLeftEvent;
    final List<IntersectionShape> L = new ArrayList<>();
    final List<IntersectionShape> I = new ArrayList<>();
    final List<IntersectionShape> R = new ArrayList<>();
    HalfEdge l;

    /**
     * constructs to create an instance of Matrix
     * */

    EventPoint2D( double x, double y,
                  IntersectionShape shape, boolean eventType ) {

        super( x, y, minsID-- );
        isLeftEvent = eventType;
        if ( ( this.shape = shape ) != null ) shapes.add( shape );
    }

    EventPoint2D( Vector point, IntersectionShape shape, boolean eventType ) {
        this( point.x, point.y, shape, eventType );
    }

    /**
     * reset x-coordinate of this event point to
     * the original x-coordinate of the vertical line that represents the event point
     * */

    void resetVerticalX() {
        assert ( ( Segment ) shape ).isVertical;
        x = ( ( Segment ) shape ).getVerticalX();
    }

    void resetVerticalY() {
        assert ( ( Segment ) shape ).isHorizontal : shape;
        y = ( ( Segment ) shape ).getHorizontalY();
    }

    /**
     * add all shapes intersection at this point into a list
     * */

    void reportIntersection( List<EventPoint2D> lines ) {
        for ( EventPoint2D event : lines )
            this.shapes.add( event.shape );
    }

    /**
     * ignore intersection with overlapping segments.
     * */

    boolean isRightIntersection() {
        // when the number of intersecting shapes are more than 2,
        // they couldn't be all arcs come from the same circle.
        if ( shapes.size() > 2 ) return isAllLineIntersection();

        assert shapes.size() == 2;
        IntersectionShape s1 = shapes.get( 0 );
        IntersectionShape s2 = shapes.get( 1 );
        // two shapes are no the same type,
        return !s1.getClass().isInstance( s2 ) || isRightIntersection( s1, s2 );
    }

    // Just brute force, so there are too many overlapping segments,
    // the overall time cost is bad......
    boolean isAllLineIntersection() {
        // cannot have two identical ars here.
        // overlapping circles will be removed during the pre-process.
        List<Line> L = new ArrayList<>();

        for ( IntersectionShape s1 : shapes ) {
            for ( IntersectionShape s2 : shapes ) {
                if ( s1 == s2 ) continue;
                if ( !s1.getClass().isInstance( s2 ) ) return true;

                if ( s1 instanceof Line ) L .add( ( Line ) s1 );
            }
        }

        for ( Line l1 : L ) {
            for ( Line l2 : L ) {
                if ( l1 == l2 ) continue;

                if ( isNotOverlappingSegment( l1, l2 ) ) return true;
            }
        }

        return false;
    }

    boolean isNotOverlappingSegment( Line l1, Line l2 ) {
        // and they aren't parallel,
        return !l1.isParallel( l2 ) ||
                // they don't have the same startPoint and endPoint.
                l1.startPoint.equals( l2.endPoint ) || l1.endPoint.equals( l2.startPoint );
    }

    boolean isRightIntersection( IntersectionShape s1, IntersectionShape s2 ) {
        // or they are the same type, but both are Line,
        if ( s1 instanceof Line l1 && s2 instanceof Line l2 )
            return isNotOverlappingSegment( l1, l2 );

        // or they cannot come from the same circle.
        assert s1 instanceof Arc && s2 instanceof Arc;
        return ( ( Arc ) s1 ).c != ( ( Arc ) s2 ).c;
    }

    /**
     * update Y And X with the shape in this eventPoint
     * */

    @Override
    public void update( Vector update, boolean isUpdatingByX ) {
        shape.update(this, update, isUpdatingByX );
    }

    /**
     * update Y with given X with the shape in this eventPoint
     * */

    @Override
    public void update( double coor, boolean isUpdatingByX ) {
        shape.update( this, coor, isUpdatingByX );
    }

    @Override
    public String toString() {
        return x + "|" + y + ": {" + shape + "}";
    }

    public static
    void main( String[] args ) {
        int ID = 0;
        Vector point1 = new Vector( -1, -1, ID++ );
        Vector point2 = new Vector( -1, 1, ID++ );
        Vector point3 = new Vector( 1, 1, ID++ );
        Vector point4 = new Vector( 1, -1, ID++ );
        IntersectionShape line1 = new Segment( point1, point4 );
        IntersectionShape line2 = new Segment( point2, point4 );
        EventPoint2D eventPoint1 = new EventPoint2D(1, 0, line1, false );
        EventPoint2D eventPoint2 = new EventPoint2D(1, 1, line1, false );
        TreeMap<Double, EventPoint2D> events = new TreeMap<>();
        events.put( eventPoint1.x, eventPoint1 );
        System.out.println( events );
        System.out.println( events );
    }
}
