package com.fengkeyleaf.util.geom;

/*
 * EventEdge.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

/**
 * Data structure of Event edge for partitioning monotone subpolygon
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

class EventEdge extends Event {
    private static int IDStatic = 0;
    // helper for split vertex, but maybe a merge vertex
    Vertex vertex;

    /**
     * Constructs to create an instance of EventEdge
     * */

    EventEdge( double x, double y,
               IntersectionShape shape,
               Vertex vertex ) {
        super( x, y, IDStatic++ );
        // line
        this.shape = shape;
        this.vertex = vertex;
    }

    EventEdge( double x, double y,
               IntersectionShape shape,
               Vertex vertex, int ID ) {
        super( x, y, ID );
        // line
        this.shape = shape;
        this.vertex = vertex;
    }

    EventEdge( IntersectionShape shape,
               Vertex vertex ) {
        this( shape, vertex, IDStatic++ );
    }

    EventEdge( IntersectionShape shape,
               Vertex vertex, int ID ) {
        this( vertex.x, vertex.y, shape, vertex, ID );
    }

    /**
     * update Y And X with the shape in this EventEdge
     *
     * */

    @Override
    public void update( Vector update, boolean isUpdatingByX ) {
        if ( ( ( Line ) shape ).outOfRangeX( update.x ) ) return;
        shape.update( this, update, false );
    }

    /**
     * update Y with given X with the shape in this EventEdge
     * */

    @Override
    public void update( double coor, boolean isUpdatingByX ) {
        if ( ( ( Line ) shape ).outOfRangeX( coor ) ) return;
        shape.update( this, coor, isUpdatingByX );
    }

    @Override
    public String toString() {
        return toStringNormal() + "(h:" + vertex + ")";
    }
}
