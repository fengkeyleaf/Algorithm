package myLibraries.util.geometry.elements;

/*
 * Arc.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */
 
/**
 *  Data structure of Arc
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Arc {
    public final Cycle cycle;
    public final float[] xRange = new float[ 2 ];
    public final float[] yRange = new float[ 2 ];

    /**
     * Constructs to create an instance of Arc
     *
     * x's range: left to right;
     * y's range: up to down;
     *
     * */

    public Arc( Cycle cycle,
                float startX, float endX,
                float startY, float endY ) {
        this.cycle = cycle;
        xRange[ 0 ] = startX;
        xRange[ 1 ] = endX;
        yRange[ 0 ] = startY;
        yRange[ 1 ] = endY;
    }

    /**
     * the point lays on this arc?
     *
     * Note that the point must be the intersection points
     * with the cycle and a line,
     * i.e. not an arbitrary point
     * */

    public boolean belong( Vector point ) {
        if ( point.x > xRange[ 1 ] || point.x < xRange[ 0 ]
                || point.y > yRange[ 0 ] || point.y < yRange[ 1 ] )
            return false;

        return true;
    }
}
