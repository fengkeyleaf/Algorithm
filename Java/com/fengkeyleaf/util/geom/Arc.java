package com.fengkeyleaf.util.geom;

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
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Arc extends Circle
        implements IntersectionShape {

    protected final double[] xRange = new double[ 2 ];
    protected final double[] yRange = new double[ 2 ];
    final Vector startPoint;
    final Vector endPoint;
    final Circle c;

    /**
     * Constructs to create an instance of Arc
     *
     * x's range: left to right;
     * y's range: up to down;
     * */

    public Arc( Circle c,
                double xRangeMax, double xRangeMin,
                double yRangeMax, double yRangeMin,
                Vector startPoint, Vector endPoint ) {

        super( c.center, c.radius );
        this.c = c;
        xRange[ 0 ] = xRangeMin;
        xRange[ 1 ] = xRangeMax;
        yRange[ 0 ] = yRangeMax;
        yRange[ 1 ] = yRangeMin;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Arc( Circle c ) {
        this( c, 0, 0, 0, 0, null, null );
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

    // TODO: 6/19/2022 precision issue?
    boolean belongY( double y ) {
        if ( y > yRange[ 0 ] || y < yRange[ 1 ] )
            return false;

        return true;
    }

    boolean belongX( double x ) {
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

    private double whichUpdatedX( double[] xCoordinates ) {
        if ( belongX( xCoordinates[ 0 ] ) ) {
            return xCoordinates[ 0 ];
        }

        return xCoordinates[ 1 ];
    }

    /**
     * update the target point's x and y
     * based on the given update point, using this arc to calculate
     *
     * Interface definition
     *
     * @param isUpdatingByX     update x and y based which, x or y?
     *                          true -> x; false -> y
     * */

    public void update( Vector target, Vector update,
                        boolean isUpdatingByX ) {

        if ( isUpdatingByX ) {
            if ( belongX( update.x ) ) {
                double[] yCoordinates = updateY( update.x );
                target.setXAndY( update.x, whichUpdatedY( yCoordinates ) );
            }

            return;
        }

        if ( belongY( update.y ) ) {
            double[] xCoordinates = updateX( update.y );
            target.setXAndY( whichUpdatedX( xCoordinates ), update.y );
        }
    }

    /**
     * update the target point's x and y
     * based on the given x, using this arc to calculate
     * */

    public void update( Vector target, double coor,
                        boolean isUpdatingByX ) {

        update( target, new Vector( coor, coor ), isUpdatingByX );
    }

    /**
     * Generate eventPoint for this cycle.
     * Note that this preprocess will create new monotone arcs for the input circle.
     * And the four arcs have been associated to the original circle,
     * which is different from {@link Segment#preprocess(boolean)}.
     *
     * @param isXMonotone     true, preprocess the input shape as monotone relative to x-axis;
     *                        false, monotone relative to y-axis.
     * */

    @Override
    public EventPoint2D[] preprocess( boolean isXMonotone ) {
        Arc[] arcs = getFourMonotoneQuarters( isXMonotone );
        EventPoint2D[] eventPoint2DS = new EventPoint2D[ 8 ];
        int index = 0;

        for ( Arc arc : arcs ) {
            EventPoint2D left = new EventPoint2D( arc.startPoint.x,
                    arc.startPoint.y, arc, true );
            EventPoint2D right = new EventPoint2D( arc.endPoint.x,
                    arc.endPoint.y, null, false );
            eventPoint2DS[ index++ ] = left;
            eventPoint2DS[ index++ ] = right;
        }

        return eventPoint2DS;
    }

    /**
     * does the give point on this arc?
     * */

    @Override
    public boolean ifOnThisShape( Vector vector ) {
        return belong( vector );
    }

    /**
     * this shape has the endPoint?
     * */

    @Override
    public boolean isSameEndPoint( Vector endPoint ) {
        return this.endPoint.equalsXAndY( endPoint );
    }

    /**
     * get Y of EndPoint
     * */

    @Override
    public Vector getEndPoint() {
        return endPoint;
    }


    @Override
    public Vector[] intersect( Intersection s ) {
        if ( s instanceof Line )
            return GeometricIntersection.lineArc( ( Line ) s, this );

        // TODO: 5/30/2022 other intersections
        return new Vector[] {};
    }

    @Override
    public String toString() {
        return "L:" + startPoint + "|R: " + endPoint + "|C:" + center;
    }

    static
    void testUpdateX() {
        int ID = 0;
        Vector temp = new Vector( 0, 0, ID++ );
        Circle circle1 = new Circle( Vector.origin, 1 );
        Arc[] arcs = circle1.getFourMonotoneQuarters( true );
        for ( Arc arc : arcs ) {
            arc.update( temp, 0.5, true );
            System.out.println( temp );
            temp.setXAndY( 0, 0 );
        }

        System.out.println();
        for ( Arc arc : arcs ) {
            arc.update( temp, -0.5, true );
            System.out.println( temp );
            temp.setXAndY( 0, 0 );
        }

        System.out.println();
        Vector vector1 = new Vector( 0, 1, ID++ );
        Circle circle2 = new Circle( vector1, 1 );
        arcs = circle2.getFourMonotoneQuarters( true );
        for ( Arc arc : arcs ) {
            arc.update( temp, 0.5, true );
            System.out.println( temp );
            temp.setXAndY( 0, 0 );
        }

        System.out.println();
        for ( Arc arc : arcs ) {
            arc.update( temp, -0.5, true );
            System.out.println( temp );
            temp.setXAndY( 0, 0 );
        }
    }

    public static
    void main( String[] args ) {
        testUpdateX();
    }
}
