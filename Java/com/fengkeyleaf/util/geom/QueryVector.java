package com.fengkeyleaf.util.geom;

/*
 * QueryVector.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/18/2022$
 */

import java.util.Comparator;

/**
 * This class gives space tree, like range tree, the ability to query infinite,
 * with the aid of conceptual coordinate.
 * It aims for giving range tree the ability to query with infinite points,
 * and also, more importantly, for combining interval tree and range tree.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

class QueryVector {
    static final QueryVector POS_INF = new QueryVector( null, null, true, null, true );
    static final QueryVector NEG_INF = new QueryVector( null, null, false, null, false );
    // not null -> real point; null -> conceptual point.
    final Vector p;
    // Conceptual coordinates:
    // true -> POS_INFINITE; false -> NEG_INFINITE
    final Double x; // real x-coor
    final boolean cx; // conceptual x-coor
    final Double y; // real y-coor
    final boolean cy; // conceptual y-coor

    QueryVector( Vector p, Double x, boolean cx,
                 Double y, boolean cy ) {

        this.p = p;
        this.x = x;
        this.cx = cx;
        this.y = y;
        this.cy = cy;
    }

    QueryVector( Vector p ) {
        this( p, null, true, null, true );
    }

    // there are two types of comapre().
    // one ( static ) is used for comparing two QueryVectors.
    // the other ( instance ) is used for comparing QueryVector and Vector.

    static
    int compare( QueryVector q1, QueryVector q2,
                 Comparator<Vector> c, boolean isX ) {

        // both have conceptual coors
        if ( q1.p == null && q2.p == null ) {
            int res = isX ? compareX( q1, q2 ) : compareY( q1, q2 );
            return res == 0 ? ( !isX ? compareX( q1, q2 ) : compareY( q1, q2 ) ) : res;
        }
        // only q1 has conceptual coor.
        else if ( q1.p == null )
            return q1.compare( q2.p, c, isX );
        // only q2 has conceptual coor.
        else if ( q2.p == null )
            return -q2.compare( q1.p, c, isX );

        // no conceptual coors in this case.
        int res = c.compare( q1.p, q2.p );
        return res == 0 ? ( !isX ? Vectors.sortByX( q1.p, q2.p ) : Vectors.sortByY( q1.p, q2.p ) ) : res;
    }

    static
    int compareX( QueryVector q1, QueryVector q2 ) {
        // comparing with conceptual x-coor.
        if ( q1.x == null && q2.x == null )
            return q1.cx == q2.cx ? 0 : ( q1.cx ? 1 : -1 );
        else if ( q1.x == null )
            return q1.cx ? 1 : -1;
        else if ( q2.x == null )
            return q2.cx ? -1 : 1;

        // comparing with real x-coor.
        return Double.compare( q1.x, q2.x );
    }

    static
    int compareY( QueryVector q1, QueryVector q2 ) {
        // comparing with conceptual y-coor.
        if ( q1.y == null && q2.y == null )
            return q1.cy == q2.cy ? 0 : ( q1.cy ? 1 : -1 );
        else if ( q1.y == null )
            return q1.cy ? 1 : -1;
        else if ( q2.y == null )
            return q2.cy ? -1 : 1;

        // comparing with real y-coor.
        return Double.compare( q1.y, q2.y );
    }

    /**
     * compare real point with conceptual point.
     */

    int compare( Vector p, Comparator<Vector> c, boolean isX ) {
        if ( this.p != null ) return c.compare( this.p, p );

        // compare x-coor first
        if ( isX ) {
            int res = compareX( p.x );
            // compare real y-coor, if x-coors are the same.
            return res == 0 ? compareY( p.y ) : res;
        }

        // compare y-coor first
        int res = compareY( p.y );
        // compare real x-coor, if y-coors are the same.
        return res == 0 ? compareX( p.x ) : res;
    }

    /**
     * compare real x-coor with conceptual x-coor.
     */

    int compareX( double x ) {
        // compare real point, if it exists.
        if ( p != null ) return Double.compare( p.x, x );

        // compare conceptual x-coor.
        if ( this.x == null ) return cx ? 1 : -1;

        // compare real x-coor.
        return Double.compare( this.x, x );
    }

    /**
     * compare real y-coor with conceptual y-coor.
     */

    int compareY( double y ) {
        if ( p != null ) return Double.compare( p.y, y );

        if ( this.y == null ) return cy ? 1 : -1;

        return Double.compare( this.y, y );
    }

    /**
     * get real point of this conceptual point for visualization.
     */

    Vector getVector( BoundingBox b, boolean ixMax ) {
        if ( p != null ) return p;

        if ( ixMax )
            return new Vector( x == null ? b.maxX : x, y == null ? b.maxY : y );

        return new Vector( x == null ? b.minX : x, y == null ? b.minY : y );
    }

    @Override
    public String toString() {
        if ( p != null ) return p.toString();

        return ( x == null ? ( cx ? "INF" : "-INF" ) : x ) + ", " + ( y == null ? ( cy ? "INF" : "-INF" ) : y );
    }
}
