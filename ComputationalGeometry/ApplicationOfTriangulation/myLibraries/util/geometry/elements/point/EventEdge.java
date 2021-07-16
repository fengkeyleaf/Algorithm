package myLibraries.util.geometry.elements.point;

/*
 * EventEdge.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.DCEL.MonotoneVertex;
import myLibraries.util.geometry.elements.UpdateCoordinatesShape;
import myLibraries.util.geometry.elements.line.Line;

/**
 * Data structure of Event edge for partitioning monotone subpolygon
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class EventEdge extends Event {
    private static int IDStatic = 0;
    // helper for split vertex, but maybe a merge vertex
    public MonotoneVertex vertex;
    // line
    public final UpdateCoordinatesShape shape;

    /**
     * Constructs to create an instance of EventEdge
     * */

    public EventEdge( double x, double y,
                     UpdateCoordinatesShape shape,
                      MonotoneVertex vertex ) {
        super( x, y, IDStatic++ );
        this.shape = shape;
        this.vertex = vertex;
    }

    public EventEdge( UpdateCoordinatesShape shape,
                      MonotoneVertex vertex ) {
        this( shape, vertex, IDStatic++ );
    }

    public EventEdge( UpdateCoordinatesShape shape,
                      MonotoneVertex vertex, int ID ) {
        super( vertex.x, vertex.y, ID );
        this.shape = shape;
        this.vertex = vertex;
    }

    /**
     * update Y And X with the shape in this EventEdge
     *
     * */

    @Override
    public void updateYAndX( Vector update ) {
        if ( ( ( Line ) shape ).outOfRangeX( update.x ) ) return;
        shape.updateYAndX(this, update, false );
    }

    /**
     * update Y with given X with the shape in this EventEdge
     * */

    @Override
    public void updateY( double x ) {
        if ( ( ( Line ) shape ).outOfRangeX( x ) ) return;
        shape.updateYAndX( this, x );
    }

    @Override
    public String toString() {
        return toStringNormal() + "(h:" + vertex + ")";
    }
}
