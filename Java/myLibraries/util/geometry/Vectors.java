package myLibraries.util.geometry;

/*
 * Vectors.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/21/2021$
 *     $1.1 added drawing methods on 11/28/2021$
 */

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class consists exclusively of static methods that
 * related to Vector
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
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
    List<Integer> getDrawingPointsWithID( List<Vector> points,
                                          int originWidth, int originHeight,
                                          int windowWidth, int windowHeight ) {
        List<Integer> vertexPoints = new ArrayList<>();
        for ( Vector point : points ) {
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
    List<Integer> getDrawingPoints( List<Vector> points,
                                    int originWidth, int originHeight,
                                    int windowWidth, int windowHeight ) {
        List<Integer> vertexPoints = new ArrayList<>();
        for ( Vector point : points ) {
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
    void drawPoint( Graphics graphics, Vector point,
                    int originWidth, int originHeight,
                    int windowWidth, int windowHeight,
                    Color color ) {
        final int RADIUS = 5;
        final int CYCLE_OFFSET = RADIUS / 2;
        Vector normalized = normalize( point, originWidth, originHeight, windowWidth, windowHeight );

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
    void drawPoints( Graphics graphics, List<Vector> points,
                     int originWidth, int originHeight,
                     int windowWidth, int windowHeight,
                     Color color ) {
        points.forEach( v -> drawPoint( graphics, v, originWidth, originHeight, windowWidth, windowHeight, color ) );
    }

    public static
    void drawPoints( Graphics graphics, List<List<Vector>> points,
                     int originWidth, int originHeight,
                     int windowWidth, int windowHeight,
                     List<Color> colors ) {
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

    public static
    Vector normalize( Vector point,
                      int originWidth, int originHeight,
                      int windowWidth, int windowHeight ) {
        Vector p = new Vector( normalize( point.x, originWidth, windowWidth ),
                                normalize( point.y, originHeight, windowHeight ) );
        return p;
    }

    public static
    List<Vector> normalize( List<Vector> points,
                            int originWidth, int originHeight,
                            int windowWidth, int windowHeight ) {
        List<Vector> ps = new ArrayList<>( points.size() + 1 );
        points.forEach( p -> ps.add( normalize( p, originWidth, originHeight, windowWidth, windowHeight ) ) );
        return ps;
    }

    /**
     * reverse Y coordinates for each point to draw shapes in Java GUI
     * in the terms of normal X-Y coordinates
     */

    public static
    List<Vector> reversedY( List<Vector> points ) {
        List<Vector> reversedY = new ArrayList<>( points.size() + 1 );
        points.forEach( vertex -> reversedY.add( new Vector( vertex.x, -vertex.y, vertex.ID ) ) );
        return reversedY;
    }

    public static
    Vector reversedY( Vector point ) {
        return new Vector( point.x, -point.y, point.ID );
    }

    // -------------------------------------------
    // computational part ------------------------------
    // -------------------------------------------

    /**
     * get the max vector among two
     * */

    public static
    Vector max( Vector vector1, Vector vector2,
                Comparator<Vector> comparator ) {
        int res = comparator.compare( vector1, vector2 );
        if ( res > 0 ) return vector1;
        else if ( res < 0 ) return vector2;

        return vector1;
    }

    /**
     * compare two vectors
     * */

    public static
    int compare( Vector q1, Vector q2,
                 Comparator<? super Vector> c ) {

        return c.compare( q1, q2 );
    }

    /**
     * sort By Y, increasing order
     * */

    public static
    int sortByY( Vector q1, Vector q2 ) {
        if ( MyMath.isEqual( q1.y, q2.y ) )
            return MyMath.doubleCompare( q1.x, q2.x );

        return MyMath.doubleCompare( q1.y, q2.y );
    }

    /**
     * sort By Y, decreasing order
     * */

    public static
    int sortByYDecreasing( Vector point1, Vector point2 ) {
        if ( MyMath.isEqual( point1.y, point2.y ) )
            return -MyMath.doubleCompare( point1.x, point2.x );

        return -MyMath.doubleCompare( point1.y, point2.y );
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
    int sortByX( Vector q1, Vector q2 ) {
        if ( MyMath.isEqual( q1.x, q2.x ) )
            return MyMath.doubleCompare( q1.y, q2.y );

        return MyMath.doubleCompare( q1.x, q2.x );
    }


    /**
     * find LTL, lowest then left
     */

    public static
    Vector findLTL( List<Vector> points ) {
        Vector LTL = new Vector( Integer.MAX_VALUE,
                Integer.MAX_VALUE, -2 );

        for ( int i = 0; i < points.size(); i++ ) {
            Vector point = points.get( i );
            if ( Vectors.compare( point, LTL, Triangles::LTL ) < 0 )
                LTL = point;
        }

        return LTL;
    }
}
