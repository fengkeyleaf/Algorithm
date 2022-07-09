package com.fengkeyleaf.util.geom;

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

/**
 * Data structure of Voronoi vertex
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class VoronoiVertex extends Vertex {
    public final Circle circle;

    VoronoiVertex( double x, double y, Circle circle ) {
        super( x, y );
        this.circle = circle;
    }

    VoronoiVertex( Circle circle ) {
        super( circle.center );
        this.circle = circle;
    }
}
