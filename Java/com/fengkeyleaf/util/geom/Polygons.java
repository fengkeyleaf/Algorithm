package com.fengkeyleaf.util.geom;

/*
 * Polygons.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 basic operations on 7/21/2021$
 *     $1.1 added drawing methods on 11/28/2021$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to Polygon
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class Polygons {

    // -------------------------------------------
    // drawing part ------------------------------
    // -------------------------------------------

    /**
     * draw polygons with reversed Y coordinates
     * */

    private static
    List<Integer> addPoints( List<HalfEdge> edges,
                             int originWidth, int originHeight,
                             int windowWidth, int windowHeight ) {
        List<Integer> points = new ArrayList<>();
        for ( HalfEdge edge : edges ) {
            int x1 = Vectors.normalize( edge.origin.x, originWidth, windowWidth );
            int y1 = Vectors.normalize( -edge.origin.y, originHeight, windowHeight );
            int x2 = Vectors.normalize( edge.next.origin.x, originWidth, windowWidth );
            int y2 = Vectors.normalize( -edge.next.origin.y, originHeight, windowHeight );
            DrawingProgram.storePoints( points, x1, y1, x2, y2 );
        }

        return points;
    }

    public static
    List<Integer> getDrawingPoints( List<Face> faces,
                                    int originWidth, int originHeight,
                                    int windowWidth, int windowHeight ) {
        List<Integer> points = new ArrayList<>();
        for ( Face face : faces ) {
            if ( face.outComponent == null ) continue;

            List<HalfEdge> edges = face.walkAroundEdge();
            points.addAll( addPoints( edges, originWidth, originHeight, windowWidth, windowHeight ) );
        }

        return points;
    }

    public static
    List<Integer> getDrawingPoints( Face face,
                                    int originWidth, int originHeight,
                                    int windowWidth, int windowHeight ) {
        if ( face == null ) return new ArrayList<>();

        List<Face> faces = new ArrayList<>( 1 );
        faces.add( face );
        return getDrawingPoints( faces, originWidth, originHeight, windowWidth, windowHeight );
    }

    public static
    List<Color> getColors( int numbers, Color color ) {
        List<Color> colors = new ArrayList<>();
        if ( numbers <= 0 ) return null;
        for ( int i = 0; i < numbers; i++ ) {
            colors.add( color );
        }

        return colors;
    }

    // https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/java/awt/Graphics.html
    public static
    void drawPolygons( Graphics graphics, List<List<Integer>> points,
                       List<Boolean> types, List<Color> colors ) {

        if ( graphics == null || points == null || types == null || colors == null ) return;

        assert points.size() == types.size() && types.size() == colors.size();
        for ( int i = 0; i < points.size(); i++ ) {
            List<Integer> P = points.get( i );
            int[] xPoints = new int[ P.size() / 2 ];
            int[] yPoints = new int[ P.size() / 2 ];

            int idx = 0;
            for ( int j = 0; j < P.size() - 1; j += 2 ) {
                xPoints[ idx ] = P.get( j );
                yPoints[ idx++ ] = P.get( j + 1 );
            }

            graphics.setColor( colors.get( i ) );
            if ( types.get( i ) )
                graphics.drawPolygon( xPoints, yPoints, xPoints.length );
            else
                graphics.fillPolygon( xPoints, yPoints, xPoints.length );
        }
    }

    public static
    void drawPolygons( Graphics graphics, List<List<Integer>> points, List<Color> colors ) {
        if ( graphics == null || points == null || colors == null ) return;

        // TODO: 11/28/2021 reconstruct drawPolygons() and drawPolygon(), make them consistent
//        assert points.size() == colors.size();
        for ( int i = 0; i < points.size(); i++ ) {
            Lines.drawLines( graphics, points.get( i ), colors.get( i ) );
        }
    }

    public static
    void drawPolygon( Graphics graphics, Face face,
                      int originWidth, int originHeight,
                      int windowWidth, int windowHeight, Color color ) {
        List<List<Integer>> points = new ArrayList<>();
        points.add( getDrawingPoints( face, originWidth, originHeight, windowWidth, windowHeight ) );
        List<Color> colors = getColors( points.size(), color );
        drawPolygons( graphics, points, colors );
    }


    // -------------------------------------------
    // computational part ------------------------
    // -------------------------------------------

    /**
     * remove Points On The Same Line
     */

    public static
    List<Vertex> removePointsOnTheSameLine( List<Vertex> vertices ) {
        List<Vertex> filtered = new ArrayList<>();
        if ( vertices.isEmpty() ) return filtered;

        filtered.add( vertices.get( 0 ) );

        // for i-th vertex, check its predecessor and successor
        // to see if they are on the same line.
        // the reason why we can do this is that
        // the medium point is the one that is redundant,
        // if three consecutive points are on the same line.
        for ( int i = 1; i < vertices.size() - 1; i++ ) {
            // if yes, ignore this vertex;
            // if no, keep this vertex
            if ( !Lines.isOnTheSameLine( vertices.get( i - 1 ), vertices.get( i ), vertices.get( i + 1 ) ) )
                filtered.add( vertices.get( i ) );
        }

        filtered.add( vertices.get( vertices.size() - 1 ) );
        return filtered;
    }

    /**
     * the given point lies "inside" which face?
     *
     * */

    public static
    Face inWhichFace( List<Face> polygons, Vector point ) {
        for ( Face polygon : polygons ) {
            if ( polygon.isInsidePolygon( point ) )
                return polygon;
        }

        return null;
    }

    /**
     * the given point lies "on" which face?
     *
     * */

    public static
    Face OnWhichFace( List<Face> polygons, Vector point ) {
        for ( Face polygon : polygons ) {
            if ( polygon.isOnPolygon( point ) )
                return polygon;
        }

        return null;
    }

    /**
     * get the DCEL for the polygon
     * representing by counter-clock-wise vertices.
     * And this method guarantee that the half-edge inner face attaches to
     * is the one connecting first vertex and the second in the given vertex list
     *
     * @param vertices vertices in counter-clock-wise order.
     * @return  [faceOutside(infinite face), faceInner]
     * */

    // TODO: 7/14/2021 not support complex polygons
    public static
    Face[] getDCEL( List<Vertex> vertices ) {
        Face[] faces = null;
        try {
            faces = getDCEL( vertices, Face.class.getConstructor() );
        } catch ( NoSuchMethodException | SecurityException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        return faces;
    }

    public static<T extends Face>
    Face[] getDCEL( List<Vertex> vertices, Constructor<T> c ) {
        if ( vertices == null || vertices.size() < 3 ) return null;

        // two faces, one inside the polygon, the other outside of it.
        Face faceInner = null;
        Face faceOutside = null;
        try {
            faceInner = c.newInstance();
            faceOutside = c.newInstance();
        } catch ( IllegalAccessException | InvocationTargetException | InstantiationException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        return getDECL( vertices, faceInner, faceOutside );
    }


    private static
    Face[] getDECL( List<Vertex> vertices,
                    Face faceInner, Face faceOutside ) {

        int len = vertices.size();

        // two edge list, one going counter-clock wise, the other going the opposite.
        final List<HalfEdge> counterClockWiseEdges = new ArrayList<>();
        final List<HalfEdge> clockWiseEdges = new ArrayList<>();

        // from the first vertex to the last one, i-th vertex:
        for ( int i = 0; i < len; i++ ) {
            // create twin edges
            HalfEdge edge1 = new HalfEdge( vertices.get( i ), faceInner );
            vertices.get( i ).incidentEdge = edge1;
            counterClockWiseEdges.add( edge1 );
            HalfEdge edge2 = new HalfEdge( vertices.get( ( i + 1 ) % len ), faceOutside );
            clockWiseEdges.add( edge2 );
            edge1.setTwins( edge2 );

            // add next and prev for the two halfEdges, i and i - 1, separately.
            if ( i > 0 ) {
                counterClockWiseEdges.get( i ).connect( counterClockWiseEdges.get( i - 1 ) );
                clockWiseEdges.get( i - 1 ).connect( clockWiseEdges.get( i ) );
            }
        }

        // add next and prev for the first and last halfEdges.
        counterClockWiseEdges.get( 0 ).connect( counterClockWiseEdges.get( counterClockWiseEdges.size() - 1 ) );
        clockWiseEdges.get( clockWiseEdges.size() - 1 ).connect( clockWiseEdges.get( 0 ) );
        // set outComponent and innerComponent.
        faceInner.outComponent = counterClockWiseEdges.get( 0 );
        faceOutside.innerComponents.add( clockWiseEdges.get( 0 ) );

        // faceOutside = infinite face
        return new Face[] { faceOutside, faceInner };
    }
}
