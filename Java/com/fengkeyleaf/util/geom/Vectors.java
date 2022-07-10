package com.fengkeyleaf.util.geom;

/*
 * Vectors.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.2$
 *
 * Revisions:
 *     $1.0 basic operations on 7/21/2021$
 *     $1.1 added drawing methods on 11/28/2021$
 *     $1.2 added clockWiseAngleCompareTo(), counterClockWiseAngleCompareTo() on 3/25/2021$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.lang.MyMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class consists exclusively of static methods that
 * related to Vector
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class Vectors {

    // -------------------------------------------
    // drawing part ------------------------------
    // -------------------------------------------

    /**
     * drawing data for drawing point with its ID ( draw with string )
     *
     * @param points assume it's been flipped y
     * */

    public static
    List<Integer> getDrawingPointsWithID( List<com.fengkeyleaf.util.geom.Vector> points,
                                          int originWidth, int originHeight,
                                          int windowWidth, int windowHeight ) {
        List<Integer> vertexPoints = new ArrayList<>();
        for ( com.fengkeyleaf.util.geom.Vector point : points ) {
            int x = normalize( point.x, originWidth, windowWidth );
            int y = normalize( point.y, originHeight, windowHeight );
            DrawingProgram.storePoints( vertexPoints, point.ID, x, y );
        }

        return vertexPoints;
    }

    /**
     * get drawing data for points, including its x-coor and y-coor
     *
     * @param points assume it's been flipped y
     * @return [(x,y)*], like [ x1, y1, x2, y2, ..., ]
     * */

    public static
    List<Integer> getDrawingPoints( List<com.fengkeyleaf.util.geom.Vector> points,
                                    int originWidth, int originHeight,
                                    int windowWidth, int windowHeight ) {
        List<Integer> vertexPoints = new ArrayList<>();
        for ( com.fengkeyleaf.util.geom.Vector point : points ) {
            int x = normalize( point.x, originWidth, windowWidth );
            int y = normalize( point.y, originHeight, windowHeight );
            DrawingProgram.storePoints( vertexPoints, x, y );
        }

        return vertexPoints;
    }

    /**
     * draw Points With ID, but this one is to draw string for a point:
     * v + ID, like v0
     *
     * point data format: ( ID, x, y )
     *
     * @param vertexPoints assume it's been flipped y
     * */

    public static
    void drawPointsWithID( Graphics graphics, List<Integer> vertexPoints ) {
        assert vertexPoints.size() % 3 == 0;
        graphics.setColor( Color.BLACK );
        graphics.setFont( new Font( "Times", Font.PLAIN, 14 ) );
        for ( int i = 0; i < vertexPoints.size(); i += 3 ) {
            graphics.drawString( "v" + vertexPoints.get( i ), vertexPoints.get( i + 1 ), vertexPoints.get( i + 2 ) );
        }
    }

    /**
     * draw a point, not normalized. Assume already flipped Y
     *
     * @param point assume it's been flipped y
     * */

    public static
    void drawPoint( Graphics graphics, com.fengkeyleaf.util.geom.Vector point,
                    int originWidth, int originHeight,
                    int windowWidth, int windowHeight,
                    Color color ) {
        final int RADIUS = 5;
        final int CYCLE_OFFSET = RADIUS / 2;
        com.fengkeyleaf.util.geom.Vector normalized = normalize( point, originWidth, originHeight, windowWidth, windowHeight );

        graphics.setColor( color );
        graphics.fillOval( ( int ) normalized.x - CYCLE_OFFSET,
                            ( int ) normalized.y - CYCLE_OFFSET, RADIUS, RADIUS );
    }

    /**
     * draw points, not normalized. Assume already flipped Y
     *
     * @param points assume it's been flipped y
     * */

    public static
    void drawPoints( Graphics graphics, List<com.fengkeyleaf.util.geom.Vector> points,
                     int originWidth, int originHeight,
                     int windowWidth, int windowHeight,
                     Color color ) {
        points.forEach( v -> drawPoint( graphics, v, originWidth, originHeight, windowWidth, windowHeight, color ) );
    }

    public static
    void drawPoints( Graphics graphics, List<List<com.fengkeyleaf.util.geom.Vector>> points,
                     int originWidth, int originHeight,
                     int windowWidth, int windowHeight,
                     List<Color> colors ) {

        if ( graphics == null || points == null || colors == null ) return;

        for ( int i = 0; i < points.size(); i++ ) {
            Vectors.drawPoints( graphics, points.get( i ),
                                    originWidth, originHeight,
                                        windowWidth, windowHeight, colors.get( i ) );
        }
    }

    /**
     * draw a normalized point.
     *
     * @param y assume it's been flipped y
     * */

    public static
    void drawPoint( Graphics graphics,
                    int x, int y, Color color ) {
        final int RADIUS = DrawingProgram.RADIUS;
        final int CYCLE_OFFSET = DrawingProgram.CYCLE_OFFSET;
        graphics.setColor( color );
        graphics.fillOval( x - CYCLE_OFFSET, y - CYCLE_OFFSET, RADIUS, RADIUS );
    }

    /**
     * normalize point
     *
     * @param coor assume it's been flipped y, if it's y-coor
     * */

    public static
    int normalize( double coor, int origin, int window ) {
        return ( int ) ( coor * window / origin + window / 2 );
    }

    public static com.fengkeyleaf.util.geom.Vector normalize( com.fengkeyleaf.util.geom.Vector point,
                                                              int originWidth, int originHeight,
                                                              int windowWidth, int windowHeight ) {
        com.fengkeyleaf.util.geom.Vector p = new com.fengkeyleaf.util.geom.Vector( normalize( point.x, originWidth, windowWidth ),
                                normalize( point.y, originHeight, windowHeight ) );
        return p;
    }

    public static
    List<com.fengkeyleaf.util.geom.Vector> normalize( List<com.fengkeyleaf.util.geom.Vector> points,
                                                      int originWidth, int originHeight,
                                                      int windowWidth, int windowHeight ) {
        List<com.fengkeyleaf.util.geom.Vector> ps = new ArrayList<>( points.size() + 1 );
        points.forEach( p -> ps.add( normalize( p, originWidth, originHeight, windowWidth, windowHeight ) ) );
        return ps;
    }

    /**
     * reverse Y coordinates for each point to draw shapes in Java GUI
     * in the terms of normal X-Y coordinates
     */

    public static
    List<com.fengkeyleaf.util.geom.Vector> reversedY( List<com.fengkeyleaf.util.geom.Vector> points ) {
        List<com.fengkeyleaf.util.geom.Vector> reversedY = new ArrayList<>( points.size() + 1 );
        points.forEach( vertex -> reversedY.add( new com.fengkeyleaf.util.geom.Vector( vertex.x, -vertex.y, vertex.ID ) ) );
        return reversedY;
    }

    public static com.fengkeyleaf.util.geom.Vector reversedY( com.fengkeyleaf.util.geom.Vector point ) {
        return new com.fengkeyleaf.util.geom.Vector( point.x, -point.y, point.ID );
    }

    // -------------------------------------------
    // computational part ------------------------------
    // -------------------------------------------

    /**
     * sort the points in angular orderings ( clock wise ) about P as the center point.
     * We take advantage of duality to do the sorting,
     * thus can avoid precision issue.
     *
     * The resulting is an angular sequence
     * starting with the angle 0 degree and proceeding up to +360 degrees:
     *
     *                    0 degree
     *                    ^
     *                    |
     *                    |
     *                    |
     *                    |
     *                    |
     * <----------------- P ------------------> 90 degrees
     *                    |
     *                    |
     *                    |
     *                    |
     *                    |
     *                    V
     *                    180 degrees
     *
     * And note that points closer to P are considered smaller
     * when they have the same angular ordering. e.g.
     *
     *                    ^
     *                    |
     *                    |
     *                    p3
     *                    |
     *                    |
     *                    p2
     *                    |
     *                    P
     *
     * p2 < p3 when they have the same angular order, 0 degree.
     *
     * @param P       center point.
     * @param points  point set to be sorted. Allow duplicate points.
     *                Further, ignore P if the point set contains it.
     * @return the points in angular orderings ( clock wise ),
     *         without P if it is given as input point set.
     * */

    public static<E extends com.fengkeyleaf.util.geom.Vector>
    List<E> sortByAngleClockWise( E P, List<E> points ) {
        List<List<E>> fourParts = sortByAngle( P, points );

        sortWithDuality( P, fourParts.get( 3 ), true );
        sortWithDuality( P, fourParts.get( 1 ), false );

        List<E> res = new ArrayList<>( points.size() + 1 );
        res.addAll( fourParts.get( 0 ) ); // verticalUp, 0
        res.addAll( fourParts.get( 1 ) ); // right, 1
        res.addAll( fourParts.get( 2 ) ); // verticalDown, 2
        res.addAll( fourParts.get( 3 ) ); // left, 3

        return res;
    }

    static<E extends com.fengkeyleaf.util.geom.Vector>
    List<List<E>> sortByAngle( E P, List<E> points ) {
        List<List<E>> res = new ArrayList<>( 4 );
        res.add( new ArrayList<>() ); // verticalUp, 0
        res.add( new ArrayList<>() ); // right, 1
        res.add( new ArrayList<>() ); // verticalDown, 2
        res.add( new ArrayList<>() ); // left, 3

        // separate the point set into four parts:
        // verticalUp: p.x == P.x, p.y > P.y
        // right: p.x > P.x
        // verticalDown: p.x == P.x, p.y < P.y
        // left, p.x < P.x
        points.forEach( p -> {
            if ( P.equals( p ) ) return;

            if ( MyMath.isEqual( P.x, p.x ) ) {
                if ( MyMath.isGreater( P.y, p.y ) )
                    res.get( 0 ).add( p );
                else if ( MyMath.isLess( P.y, p.y ) )
                    res.get( 2 ).add( p );
                else assert false;
            } else {
                if ( MyMath.isGreater( P.x, p.x ) )
                    res.get( 1 ).add( p );
                else res.get( 3 ).add( p );
            }
        } );

        // points closer to P are considered smaller
        // when they have the same angular ordering.
        res.get( 0 ).sort( Vectors::sortByY );
        res.get( 2 ).sort( ( p1, p2 ) -> MyMath.doubleCompare( p2.y, p1.y ) );

        return res;
    }

    private static<E extends com.fengkeyleaf.util.geom.Vector>
    void sortWithDuality( E P, List<E> points, boolean isLeft ) {
        points.sort( ( p1, p2 ) -> {
            // clock wise order sorted by duality.
            int res = Lines.compareBySlope( P.x - p1.x, P.y - p1.y,
                    P.x - p2.x, P.y - p2.y );

            // points closer to P are considered smaller
            // when they have the same angular ordering.
            return res == 0 ? ( isLeft ? Vectors.sortByX( p2, p1 ) : Vectors.sortByX( p1, p2 ) )
                                : -res;
        }  );
    }

    /**
     * sort the points in angular orderings ( clock wise ) about P as the center point,
     * and with the axis defined by P and Q as 0 degree.
     * We take advantage of duality to do the sorting,
     * thus can avoid precision issue.
     *
     * The resulting is an angular sequence
     * starting with the angle 0 degree and proceeding up to +360 degrees,
     * assume Q is above P and they are on the same vertical line:
     *
     *                    Q (0 degree)
     *                    ^
     *                    |
     *                    |
     *                    |
     *                    |
     *                    |
     * <----------------- P ------------------> 90 degrees
     *                    |
     *                    |
     *                    |
     *                    |
     *                    |
     *                    V
     *                    180 degrees
     *
     * And note that points closer to P are considered smaller
     * when they have the same angular ordering. e.g.
     *
     *                    ^
     *                    |
     *                    |
     *                    p3
     *                    |
     *                    |
     *                    p2
     *                    |
     *                    P
     *
     * p2 < p3 when they have the same angular order, 0 degree.
     *
     * @param P center point.
     * @param Q axis point.
     * @param points  point set to be sorted. Allow duplicate points.
     *                Further, ignore P if the point set contains it.
     *                Also, it must contain Q.
     * @return the points in angular orderings ( clock wise )
     *         with the axis defined by P and Q as 0 degree.
     *         Further, they don't include P if it is given as input point set.
     * */

    public static<E extends com.fengkeyleaf.util.geom.Vector>
    List<E> sortByAngleClockWise( E P, E Q, List<E> points ) {
        if ( !points.contains( Q ) ) return null;

        return select( Q, sortByAngleClockWise( P, points ) );
    }

    /**
     * rearrange points in the angular order with the axis PQ as 0 degree.
     * */

    private static<E extends com.fengkeyleaf.util.geom.Vector>
    List<E> select( E Q, List<E> sorted ) {
        // find the position at which Q is:
        // q1, q2, ..., Q, q3, q4, ..., q5
        int index = -1;
        for ( int i = 0; i < sorted.size(); i++ )
            if ( sorted.get( i ).equals( Q ) )
                index = i;

        List<E> res = new ArrayList<>( sorted.size() + 1 );
        // ( q1, q2, ..., ) ( Q, q3, q4, ..., q5 )
        // ( Q, q3, q4, ..., q5 )
        for ( int i = index; i < sorted.size(); i++ )
            res.add( sorted.get( i ) );

        // ( Q, q3, q4, ..., q5 ) + ( q1, q2, ..., )
        for ( int i = 0; i < index; i++ )
            res.add( sorted.get( i ) );

        return res;
    }

    /**
     * find the first clock wise point about Q,
     * with P as the center point.
     * */

    public static<E extends com.fengkeyleaf.util.geom.Vector>
    E firstClockWise( E P, E Q, List<E> points ) {
        if ( P == null || Q == null || points == null ) return null;

        List<E> sorted = sortByAngleClockWise( P, Q, points );
        if ( sorted == null ) return null;

        // skip duplicates
        for ( E vector : sorted )
            if ( !Q.equals( vector ) )
                return vector;

        assert false;
        return null;
    }

    /**
     * sort the points in angular orderings ( counter-clock wise ) about P as the center point.
     * We take advantage of duality to do the sorting,
     * thus can avoid precision issue.
     *
     * The resulting is an angular sequence
     * starting with the angle 0 degree and proceeding up to +360 degrees:
     *
     *                               0 degree
     *                               ^
     *                               |
     *                               |
     *                               |
     *                               |
     *                               |
     * 90 degrees <----------------- P ------------------>
     *                               |
     *                               |
     *                               |
     *                               |
     *                               |
     *                               V
     *                               180 degrees
     *
     * And note that points closer to P are considered smaller
     * when they have the same angular ordering. e.g.
     *
     *                    ^
     *                    |
     *                    |
     *                    p3
     *                    |
     *                    |
     *                    p2
     *                    |
     *                    P
     *
     * p2 < p3 when they have the same angular order, 0 degree.
     *
     * @param P center point.
     * @param points  point set to be sorted. Allow duplicate points.
     *                Further, ignore P if the point set contains it.
     * @return the points in angular orderings ( clock wise ),
     *         without P if it is given as input point set.
     * */

    public static<E extends com.fengkeyleaf.util.geom.Vector>
    List<E> sortByAngleCounterClockWise( E P, List<E> points ) {
        List<List<E>> fourParts = sortByAngle( P, points );

        sortWithDualityCounter( P, fourParts.get( 3 ), true );
        sortWithDualityCounter( P, fourParts.get( 1 ), false );

        List<E> res = new ArrayList<>( points.size() + 1 );
        res.addAll( fourParts.get( 0 ) ); // verticalUp, 0
        res.addAll( fourParts.get( 3 ) ); // left, 3
        res.addAll( fourParts.get( 2 ) ); // right, 1
        res.addAll( fourParts.get( 1 ) ); // verticalDown, 2

        return res;
    }

    static<E extends com.fengkeyleaf.util.geom.Vector>
    List<E> sortByAngleCounterClockWise( E P, E... points ) {
        return sortByAngleCounterClockWise( P, new ArrayList<>( Arrays.asList( points ) ) );
    }

    private static<E extends com.fengkeyleaf.util.geom.Vector>
    void sortWithDualityCounter( E P, List<E> points, boolean isLeft ) {
        points.sort( ( p1, p2 ) -> {
            // counter-clock wise order sorted by duality.
            int res = -Lines.compareBySlope( P.x - p1.x, P.y - p1.y,
                    P.x - p2.x, P.y - p2.y );

            // points closer to P are considered smaller
            // when they have the same angular ordering.
            return res == 0 ? ( isLeft ? Vectors.sortByX( p2, p1 ) : Vectors.sortByX( p1, p2 ) )
                    : -res;
        }  );
    }

    /**
     * sort the points in angular orderings ( counter-clock wise ) about P as the center point,
     * and with the axis defined by P and Q as 0 degree.
     * We take advantage of duality to do the sorting,
     * thus can avoid precision issue.
     *
     * The resulting is an angular sequence
     * starting with the angle 0 degree and proceeding up to +360 degrees,
     * assume Q is above P and they are on the same vertical line:
     *
     *                    Q (0 degree)
     *                    ^
     *                    |
     *                    |
     *                    |
     *                    |
     *                    |
     * <----------------- P ------------------> 90 degrees
     *                    |
     *                    |
     *                    |
     *                    |
     *                    |
     *                    V
     *                    180 degrees
     *
     * And note that points closer to P are considered smaller
     * when they have the same angular ordering. e.g.
     *
     *                    ^
     *                    |
     *                    |
     *                    p3
     *                    |
     *                    |
     *                    p2
     *                    |
     *                    P
     *
     * p2 < p3 when they have the same angular order, 0 degree.
     *
     * @param P center point.
     * @param Q axis point.
     * @param points  point set to be sorted. Allow duplicate points.
     *                Further, ignore P if the point set contains it.
     *                Also, it must contain Q.
     * @return the points in angular orderings ( counter-clock wise )
     *         with the axis defined by P and Q as 0 degree.
     *         Further, they don't include P if it is given as input point set.
     * */

    public static<E extends com.fengkeyleaf.util.geom.Vector>
    List<E> sortByAngleCounterClockWise( E P, E Q, List<E> points ) {
        if ( !points.contains( Q ) ) return null;

        return select( Q, sortByAngleCounterClockWise( P, points ) );
    }

    /**
     * find the first counter-clock wise point about Q,
     * with P as the center point.
     * */

    public static<E extends com.fengkeyleaf.util.geom.Vector>
    E firstCounterClockWise( E P, E Q, List<E> points ) {
        if ( P == null || Q == null || points == null ) return null;

        List<E> sorted = sortByAngleCounterClockWise( P, Q, points );
        if ( sorted == null ) return null;

        // skip duplicates
        for ( E vector : sorted )
            if ( !Q.equals( vector ) )
                return vector;

        assert false;
        return null;
    }

    /**
     * get the max vector among two
     * */

    public static com.fengkeyleaf.util.geom.Vector max( com.fengkeyleaf.util.geom.Vector vector1, com.fengkeyleaf.util.geom.Vector vector2,
                                                        Comparator<com.fengkeyleaf.util.geom.Vector> comparator ) {
        int res = comparator.compare( vector1, vector2 );
        if ( res > 0 ) return vector1;
        else if ( res < 0 ) return vector2;

        return vector1;
    }

    /**
     * sort By Y, increasing order
     * */

    public static
    int sortByY( com.fengkeyleaf.util.geom.Vector p1, com.fengkeyleaf.util.geom.Vector p2 ) {
        if ( MyMath.isEqual( p1.y, p2.y ) )
            return MyMath.doubleCompare( p1.x, p2.x );

        return MyMath.doubleCompare( p1.y, p2.y );
    }

    /**
     * sort By X.
     * X-coors are in ascending order,
     * so are y-coors when x-coors are the same.
     *
     * Visualization:
     *
     * ----------------------------> x becomes larger.
     *              ^
     *              |
     *              |
     *              | y becomes larger,
     *              | when x-coors are the same.
     *              |
     *              |
     *
     * */

    public static
    int sortByX( com.fengkeyleaf.util.geom.Vector p1, com.fengkeyleaf.util.geom.Vector p2 ) {
        if ( MyMath.isEqual( p1.x, p2.x ) )
            return MyMath.doubleCompare( p1.y, p2.y );

        return MyMath.doubleCompare( p1.x, p2.x );
    }

    /**
     * find LTL, lowest then left
     */

    static<E extends com.fengkeyleaf.util.geom.Vector>
    E findLTL( List<E> P ) {
        if ( P == null || P.isEmpty() ) return null;

        P.sort( Vectors::sortByY );
        return P.get( 0 );
    }

    static<E extends com.fengkeyleaf.util.geom.Vector>
    E findLTL( E... P ) {
        return findLTL( Arrays.asList( P ) );
    }

    /**
     * Are all points are the same line?
     */

    public static<E extends Vector>
    boolean isAllOnSameLine( List<E> P ) {
        if ( P == null || P.size() < 3 ) return false;

        for ( int i = 0; i < P.size() - 2; i++ ) {
            if ( !MyMath.isEqualZero(
                    Triangles.areaTwo( P.get( i ), P.get( i + 1 ), P.get( i + 2 ) )
                  ) ) {
                return false;
            }
        }

        return true;
    }
}
