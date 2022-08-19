package com.fengkeyleaf.util.geom;

/*
 * HalfPlane.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/1/2022$
 */

import com.fengkeyleaf.lang.LinearTwoUnknowns;
import com.fengkeyleaf.lang.MyMath;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Data structure of 2D half-plane:
 * aix + biy <= ci, or aix + biy >= ci,
 * aix + biy < ci, or aix + biy > ci.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public class HalfPlane {
    // n <= c
    public static final Comparator<Double> GREATER_EQ = ( n, c ) -> MyMath.isLessEq( c, n ) ? 1 : -1;
    // n < c
    static final Comparator<Double> GREATER = ( n, c ) -> MyMath.isLess( c, n ) ? 1 : -1;
    // n >= c
    static final Comparator<Double> LESS_EQ = ( n, c ) -> MyMath.isGreaterEq( c, n ) ? 1 : -1;
    // n > c
    static final Comparator<Double> LESS = ( n, c ) -> MyMath.isGreater( c, n ) ? 1 : -1;
    final Comparator<Double> cp;
    public final LinearTwoUnknowns eq;

    public HalfPlane( LinearTwoUnknowns e, Comparator<Double> cp ) {
        this.cp = cp;
        this.eq = e;
    }

    public HalfPlane( double a, double b, double c,
               Comparator<Double> cp ) {

        if ( cp != GREATER_EQ )
            throw new IllegalArgumentException( "Other inequalities are not supported now." );

        this.cp = cp;
        eq = new LinearTwoUnknowns( a, b, c );
    }

    /**
     * Give point is contained by the area bounded by this half-plane?
     */

    public boolean contains( Vector v ) {
        return cp.compare( eq.a * v.x + eq.b * v.y, eq.c ) > 0;
    }

    /**
     * Get a subdivision ( Face ) representing this half-plane.
     * i.e. transform this half-plane ( infinite area ) to a face ( finite are ).
     *
     * @param b bounding box that must have two intersection points with this half-plane.
     * @return a subdivision( Face ) representing this half-plane.
     */

    Face getSubdivision( BoundingBox b ) {
        // P has constant number of points, P.size < 7;
        List<Vertex> P = new ArrayList<>();
        // note that box edges in the edge list is in the counter-clock wise order,
        // such that we have points also in counter-clock wise to build the subdivision.
        b.edges.forEach( e -> {
            // no duplicate and contained by this half-plane.
            if ( !P.contains( e.origin ) && contains( e.origin ) )
                P.add( new Vertex( e.origin.x, e.origin.y ) );

            // I has up to 1 point in it.
            Vector[] I = e.getSegment().intersect( new Line( eq ) );
            for ( Vector i : I ) {
                if ( !P.contains( new Vertex( i ) ) )
                    P.add( new Vertex( i ) );
            }
        } );

        assert check( P );
        return Polygons.getDCEL( P )[ 0 ];
    }

    private static
    boolean check( List<Vertex> P ) {
        assert P.size() < 7 : P.size();
        assert P.size() > 2;

        // guarantee no duplicates.
        for ( int i = 0; i < P.size(); i++ ) {
            for ( int j = 0; j < P.size(); j++ ) {
                assert i == j || !P.get( i ).equals( P.get( j ) ) : P.get( i );
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return eq.a + " x + " + eq.b + " y <= " + eq.c;
    }
}
