package myLibraries.util.geometry.elements;

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

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.BoundingBox;
import myLibraries.util.geometry.Vectors;

import java.util.Arrays;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public class Ray extends Line {

    public Ray( Vector startPoint, Vector rayPoint ) {
        super( startPoint, rayPoint );
    }

    protected Ray( Vector[] vectors ) {
        super( vectors );
    }

    public Segment getSegment( double minX, double maxX, double minY, double maxY ) {
        Vector[] points = new Vector[ 2 ];
        points[ 0 ] = startPoint;
        Vector vectorRay = getVector();
        points[ 1 ] = generatePoint( MyMath.quadrant( vectorRay ), minX, maxX, minY, maxY );
        Arrays.sort( points, Vectors::sortByX );

        return new Segment( points[ 0 ], points[ 1 ] );
    }

    public Segment getSegment( BoundingBox box ) {
        return getSegment( box.left.origin.x, box.top.origin.x, box.bottom.origin.y, box.top.origin.y );
    }
}
