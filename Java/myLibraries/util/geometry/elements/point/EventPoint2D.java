package myLibraries.util.geometry.elements.point;

/*
 * EventPoint2D.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 * JDK: 16
 */

import myLibraries.util.geometry.elements.IntersectionShape;
import myLibraries.util.geometry.elements.line.InterLine;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Data structure of Event point for Bentley Ottmann's
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class EventPoint2D extends Event {
    public final EventType type;
    // shapes starting at this event point(left endpoint)
    public final List<IntersectionShape> shapes = new ArrayList<>( 1 );
    // shape associated with this eventPoint
    public final IntersectionShape shape;

    public enum EventType {
        LEFT, OTHER
    }

    /**
     * constructs to create an instance of Matrix
     * */

    public EventPoint2D( double x, double y,
                         IntersectionShape shape, EventType eventType ) {
        super( x, y, minsID-- );
        this.type = eventType;
        this.shape = shape;
    }

    public EventPoint2D( Vector point, IntersectionShape shape, EventType eventType ) {
        this( point.x, point.y, shape, eventType );
    }

    /**
     * add all shapes intersection at this point into a list
     * */

    public void reportIntersection( List<EventPoint2D> lines ) {
        for ( EventPoint2D event : lines )
            this.shapes.add( event.shape );
    }

    /**
     * update Y And X with the shape in this eventPoint
     * */

    @Override
    public void updateYAndX( Vector update ) {
        shape.updateYAndX(this, update, true );
    }

    /**
     * update Y with given X with the shape in this eventPoint
     * */

    @Override
    public void updateY( double x ) {
        shape.updateYAndX( this, x );
    }

    @Override
    public String toString() {
        return x + "/" + y + ": {" + shape + "}";
    }

    public static
    void main( String[] args ) {
        int ID = 0;
        Vector point1 = new Vector( -1, -1, ID++ );
        Vector point2 = new Vector( -1, 1, ID++ );
        Vector point3 = new Vector( 1, 1, ID++ );
        Vector point4 = new Vector( 1, -1, ID++ );
        IntersectionShape line1 = new InterLine( point1, point4 );
        IntersectionShape line2 = new InterLine( point2, point4 );
        EventPoint2D eventPoint1 = new EventPoint2D(1, 0, line1, EventType.OTHER );
        EventPoint2D eventPoint2 = new EventPoint2D(1, 1, line1, EventType.OTHER );
        TreeMap<Double, EventPoint2D> events = new TreeMap<>();
        events.put( eventPoint1.x, eventPoint1 );
        System.out.println( events );
        System.out.println( events );
    }
}
