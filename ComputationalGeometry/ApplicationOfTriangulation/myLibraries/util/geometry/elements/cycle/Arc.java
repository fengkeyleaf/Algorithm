package myLibraries.util.geometry.elements.cycle;

/*
 * Arc.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.elements.point.Vector;

/**
 *  Data structure of Arc
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Arc extends Cycle {
    protected final double[] xRange = new double[ 2 ];
    protected final double[] yRange = new double[ 2 ];
    public final Vector startPoint;
    public final Vector endPoint;

    /**
     * Constructs to create an instance of Arc
     *
     * x's range: left to right;
     * y's range: up to down;
     * */

    public Arc( Vector center, double radius,
                double xRangeMax, double xRangeMin,
                double yRangeMax, double yRangeMin,
                Vector startPoint, Vector endPoint ) {
        super( center, radius );
        xRange[ 0 ] = xRangeMin;
        xRange[ 1 ] = xRangeMax;
        yRange[ 0 ] = yRangeMax;
        yRange[ 1 ] = yRangeMin;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Arc( Cycle cycle, double xRangeMax, double xRangeMin,
                double yRangeMax, double yRangeMin,
                Vector startPoint, Vector endPoint ) {
        this( cycle.center, cycle.radius,
                xRangeMax, xRangeMin,
                yRangeMax, yRangeMin,
                startPoint, endPoint );
    }

    /**
     * the point lays on this arc?
     *
     * Note that the point must be the intersection points
     * with the cycle and a line,
     * i.e. not an arbitrary point
     * */

    public boolean belong( Vector point ) {
        return belongX( point.x ) && belongY( point.y );
    }

    public boolean belongY( double y ) {
        if ( y > yRange[ 0 ] || y < yRange[ 1 ] )
            return false;

        return true;
    }

    public boolean belongX( double x ) {
        if ( x > xRange[ 1 ] || x < xRange[ 0 ] )
            return false;

        return true;
    }

    // an x will have two y for a cycle
    private double whichUpdatedY( double[] yCoordinates ) {
        if ( belongY( yCoordinates[ 0 ] ) ) {
            return yCoordinates[ 0 ];
        }

        return yCoordinates[ 1 ];
    }

    /**
     * update the target point's x and y
     * based on the given x, using this line to calculate
     *
     * Interface definition
     *
     * @param isUpdatingByX     update x and y based which, x or y?
     *                          true -> x; false -> y
     * */

    // TODO: 6/30/2021 only support updating with given x
    public void updateYAndX( Vector target, Vector update, boolean isUpdatingByX ) {
        if ( !belong( update ) ) return;
        double[] yCoordinates = updateY( update.x );
        target.setXAndY( update.x, whichUpdatedY( yCoordinates ) );
    }

    /**
     * update the target point's x and y
     * based on the given x, using this arc to calculate
     * */

    public void updateYAndX( Vector target, double x ) {
        if ( !belongX( x ) ) return;
        double[] yCoordinates = updateY( x );
        target.setXAndY( x, whichUpdatedY( yCoordinates ) );
    }

    @Override
    public String toString() {
        return "L:" + startPoint + "|R: " + endPoint + "|C:" + center;
    }
}
