package myLibraries.util.geometry.elements.cycle;

/*
 * Cycle.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.line.Line;
import myLibraries.util.geometry.elements.point.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data structure of Cycle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Cycle {
    public final Vector center;
    public final double radius;
    private final double[] xRange = new double[ 2 ];
    private final double[] yRange = new double[ 2 ];

    /**
     * constructs to create an instance of Cycle
     * */

    public Cycle( Vector center, double radius ) {
        if ( MyMath.doubleCompare( radius, 0 ) < 0 ) {
            System.err.println( "radius cannot be negative!" );
            System.exit( 1 );
        }

        this.center = center;
        this.radius = radius;

        xRange[ 0 ] = center.x - radius;
        xRange[ 1 ] = center.x + radius;
        yRange[ 0 ] = center.y + radius;
        yRange[ 1 ] = center.y - radius;
    }

    /**
     * sort by radius and then center
     * */

    public static
    int sort( Cycle cycle1, Cycle cycle2 ) {
        if ( cycle1.center.equalsXAndY( cycle2.center ) )
            return MyMath.doubleCompare( cycle1.radius, cycle2.radius );

        return Vector.sortByX( cycle1.center, cycle2.center );
    }

    /**
     * remove duplicate cycles
     * */

    public static
    List<Cycle> mergeOverlappingCycles( List<Cycle> cycles ) {
        List<Cycle> nonOverlappingCycles = new ArrayList<>();
        if ( cycles.isEmpty() ) return nonOverlappingCycles;
        cycles.sort( Cycle::sort );

        nonOverlappingCycles.add( cycles.get( 0 ) );
        for ( int i = 0; i < cycles.size() - 1; i++ ) {
            if ( Cycle.sort( cycles.get( i ), cycles.get( i + 1 ) ) != 0 )
                nonOverlappingCycles.add( cycles.get( i + 1 ) );
        }

        return nonOverlappingCycles;
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

    public boolean belongX( double x ) {
        if ( x > xRange[ 1 ] || x < xRange[ 0 ] )
            return false;

        return true;
    }

    public boolean belongY( double y ) {
        if ( y > yRange[ 0 ] || y < yRange[ 1 ] )
            return false;

        return true;
    }

    /**
     * get the four monotone arcs of this cycle
     * */

    public Arc[] getFourQuarters() {
        // clockwise
        Vector point1 = new Vector( center.x - radius, center.y, -1 );
        Vector point2 = new Vector( center.x, center.y + radius, -1 );
        Vector point3 = new Vector( center.x + radius, center.y, -1 );
        Vector point4 = new Vector( center.x, center.y - radius, -1 );

        int index = 0;
        Arc[] arcs = new Arc[ 4 ];
        arcs[ index++ ] = new Arc( this, center.x, center.x - radius, center.y + radius, center.y, point1, point2 );
        arcs[ index++ ] = new Arc( this, center.x + radius, center.x, center.y + radius, center.y, point2, point3 );
        arcs[ index++ ] = new Arc( this, center.x, center.x - radius, center.y, center.y - radius, point1, point4 );
        arcs[ index++ ] = new Arc( this, center.x + radius, center.x, center.y, center.y - radius, point4, point3 );

        return arcs;
    }

    /**
     * intersects with the line?
     * */

    public boolean ifIntersectsLine( Line line ) {
        return MyMath.doubleCompare( line.distance( center ), radius ) <= 0;
    }

    /**
     * move this cycle's center to the origin first,
     * and then compute y
     * */

    private double updateYRelativeToOrigin( double x ) {
        double intermediate = Math.abs( radius * radius - x * x );
        assert MyMath.doubleCompare( intermediate, 0 ) >= 0;
        // radius * radius - x * x. may be extremely small negative number
        return Math.sqrt( Math.abs( intermediate ) );
    }

    /**
     * calculate y with the given x,
     * using this cycle
     * */

    protected double[] updateY( double x ) {
        double y = updateYRelativeToOrigin( x - center.x );
        return new double[] { y + center.y, -y + center.y };
    }

    @Override
    public String toString() {
        return "C:" + center + "Rad:" + radius;
    }

    public static
    void main( String[] args ) {
        int ID = 0;
        Cycle cycle1 = new Cycle( Vector.origin, 1 );
        System.out.println( Arrays.toString( cycle1.updateY(0.5 ) ) );
        System.out.println( Arrays.toString( cycle1.updateY(-0.5 ) ) );
        Vector vector1 = new Vector( 0, 1, ID++ );
        Cycle cycle2 = new Cycle( vector1, 1 );
        System.out.println( Arrays.toString( cycle2.updateY(0.5 ) ) ) ;
        System.out.println( Arrays.toString( cycle2.updateY(-0.5 ) ) ) ;
    }
}
