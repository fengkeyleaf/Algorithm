package myLibraries.util.geometry.DCEL;

/*
 * VoronoiVertex.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/20/2022$
 */

import myLibraries.util.geometry.elements.Circle;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public class VoronoiVertex extends Vertex {
    public final Circle circle;

    public VoronoiVertex( double x, double y, Circle circle ) {
        super( x, y );
        this.circle = circle;
    }

    public VoronoiVertex( Circle circle ) {
        super( circle.center );
        this.circle = circle;
    }
}
