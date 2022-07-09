package com.fengkeyleaf.util.geom;

/*
 * Ray.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/6/2022$
 */

import com.fengkeyleaf.lang.MyMath;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Data structure of Ray.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Ray extends Line {

    public Ray( Vector startPoint, Vector rayPoint ) {
        super( startPoint, rayPoint );
    }

    public Ray( double x1, double y1, double x2, double y2 ) {
        this( new Vector( x1, y1 ), new Vector( x2, y2 ) );
    }

    protected Ray( Vector[] vectors ) {
        super( vectors );
    }

    Segment getSegment( double minX, double maxX,
                        double minY, double maxY,
                        Comparator<Vector> c ) {

        Vector[] points = new Vector[ 2 ];
        points[ 0 ] = startPoint;
        Vector vectorRay = getVector();
        points[ 1 ] = generatePoint( MyMath.quadrant( vectorRay ), minX, maxX, minY, maxY );
        Arrays.sort( points, c );

        return new Segment( points[ 0 ], points[ 1 ], c );
    }

    public Segment getSegment( BoundingBox box ) {
        return getSegment( box.left.origin.x, box.top.origin.x,
                box.bottom.origin.y, box.top.origin.y, Vectors::sortByX );
    }

    public Segment getSegment( BoundingBox box,
                               Comparator<Vector> c ) {

        return getSegment( box.left.origin.x, box.top.origin.x,
                box.bottom.origin.y, box.top.origin.y, c );
    }
}
