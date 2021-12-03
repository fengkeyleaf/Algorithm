package myLibraries.util.geometry.elements.point;

/*
 * Event.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

/**
 * abstract event class for Bentley Ottmann's algorithm,
 * partitioning monotone subpolygon
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public abstract class Event extends Vector {

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
