package myLibraries.util.geometry.elements;

/*
 * Event.java
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

/**
 * abstract event class for Bentley Ottmann's algorithm,
 * partitioning monotone subpolygon
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

// TODO: 12/30/2021 reconstruct this class, abstract or not
public abstract class Event extends Vector {
    // shape associated with this eventPoint
    public UpdateCoordinatesShape shape;
    // Geometric Intersection : shapes starting at this event point( left endpoint )
    // Voronoi Diagrams: sites with the same Y coordinate
    public final List<UpdateCoordinatesShape> shapes = new ArrayList<>( 1 );

    /**
     * Constructs to create an instance of Event
     * */

    public Event( double x, double y, int ID ) {
        super( x, y, ID );
    }

    /**
     * update Y And X with the shape in this event
     *
     * Interface definition
     *
     * */

    public abstract void updateYAndX( Vector update );

    /**
     * update Y with given X with the shape in this event
     *
     * Interface definition
     * */

    public abstract void updateY( double x );
}
