package com.fengkeyleaf.util.geom;

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
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

abstract class Event extends Vector {
    // shape associated with this eventPoint
    // Geometric Intersection: Segment, Arc.
    // Triangulation: edge.
    public IntersectionShape shape;
    // Geometric Intersection: shapes starting at this event point( left endpoint )
    public final List<IntersectionShape> shapes = new ArrayList<>( 1 );

    /**
     * Constructs to create an instance of Event
     * */

    public Event( double x, double y, int ID ) {
        super( x, y, ID );
    }

    /**
     * update this event point based on given updating point.
     * This method is just an outer-layer function definition
     * with regard to the one in {@link IntersectionShape#update(Vector, Vector, boolean)}.
     * i.e. we will implement the code in the class implementing {@link IntersectionShape}.
     *
     * Interface definition
     *
     * @param update            updating point
     * @param isUpdatingByX     update x and y based on which, x or y?
     *                          true(vertical sweep line) -> x;
     *                          false(horizontal sweep line) -> y
     * */

    abstract void update( Vector update, boolean isUpdatingByX );

    /**
     * update this event point based on given updating coor.
     * This method is just an outer-layer function definition
     * with regard to the one in {@link IntersectionShape#update(Vector, double, boolean)}.
     * i.e. we will implement the code in the class implementing {@link IntersectionShape}.
     *
     * Interface definition
     *
     * @param coor updating coordinate, x-coor or y-coor
     * @param isUpdatingByX     update x and y based on which, x or y?
     *                          true(vertical sweep line) -> x;
     *                          false(horizontal sweep line) -> y
     * */

    abstract void update( double coor, boolean isUpdatingByX );
}
