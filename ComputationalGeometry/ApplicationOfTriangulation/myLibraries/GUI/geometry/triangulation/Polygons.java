package myLibraries.GUI.geometry.triangulation;

/*
 * Polygons.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.DCEL.*;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.Triangles;
import myLibraries.util.graph.Graph;
import myLibraries.util.graph.elements.DualVertex;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class consists exclusively of static methods that
 * related to drawing polygon
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Polygons {

    static
    void drawPoint( Graphics graphics, Vector point,
                    int WHITE_AREA, int[] mins, int ratio, Color color ) {
        final int RADIUS = 6;
        final int CYCLE_OFFSET = RADIUS / 2;
        Vector shiftedPoint = Drawer.getShiftedPoint( point, WHITE_AREA, mins, ratio );
        int x2 = ( int ) shiftedPoint.x;
        int y2 = ( int ) shiftedPoint.y;

        graphics.setColor( color );
        graphics.fillOval( x2 - CYCLE_OFFSET, y2 - CYCLE_OFFSET, RADIUS, RADIUS );
    }

    static
    void drawPath( Graphics graphics, List<Integer> points, Color color ) {
        assert points.size() % 2 == 0 : points;
        graphics.setColor( color );
        for ( int i = 0; i < points.size() - 2; i += 2 ) {
            graphics.drawLine( points.get( i ), points.get( i + 1 ),
                    points.get( i + 2 ), points.get( i + 3 ) );
        }
    }

    static
    void drawDualGraph( Graph<DualVertex> graph, Graphics graphics,
                        int WHITE_AREA, int[] mins, int ratio ) {
        List<List<Integer>> points = new ArrayList<>();

        for ( DualVertex vertex : graph.vertices ) {
            points.add( new ArrayList<>() );

            List<Vertex> vertices = DCEL.walkAroundVertex( vertex.face );
            assert vertices.size() == 3;
            List<Vector> vectors = new ArrayList<>( vertices );
            vectors = Drawer.getShiftedPoint( vectors, WHITE_AREA, mins, ratio );
            Vector gravity = Triangles.getCenterOfGravity( vectors.get( 0 ), vectors.get( 1 ), vectors.get( 2 ) );
            int x1 = ( int ) gravity.x;
            int y1 = ( int ) gravity.y;
            graphics.fillOval( x1 - Drawer.CYCLE_OFFSET, y1 - Drawer.CYCLE_OFFSET, Drawer.RADIUS, Drawer.RADIUS );

            for ( myLibraries.util.graph.elements.Vertex neighbour : vertex.neighbours ) {
                vertices = DCEL.walkAroundVertex( ( ( DualVertex ) neighbour ).face );
                assert vertices.size() == 3;
                vectors = new ArrayList<>( vertices );
                vectors = Drawer.getShiftedPoint( vectors, WHITE_AREA, mins, ratio );
                gravity = Triangles.getCenterOfGravity( vectors.get( 0 ), vectors.get( 1 ), vectors.get( 2 ) );
                int x2 = ( int ) gravity.x;
                int y2 = ( int ) gravity.y;
                graphics.fillOval( x2 - Drawer.CYCLE_OFFSET, y2 - Drawer.CYCLE_OFFSET, Drawer.RADIUS, Drawer.RADIUS );
                graphics.drawLine( x1, y1, x2, y2 );
            }
        }
    }

    static
    List<Integer> drawCorners( List<Vector> corners,
                               int WHITE_AREA, int[] mins, int ratio ) {
        List<Integer> points = new ArrayList<>();
        for ( Vector corner : corners ) {
            Vector shifted = Drawer.getShiftedPoint( corner, WHITE_AREA, mins, ratio );
            points.add( ( int ) shifted.x );
            points.add( ( int ) shifted.y );
        }

        return points;
    }

    static
    void drawPolygon( Face face, Graphics graphics,
                           int WHITE_AREA, int[] mins, int ratio, Color color ) {
        graphics.setColor( color );
        List<HalfEdge> edges = DCEL.walkAroundEdge( face );
        for ( HalfEdge edge : edges ) {
            int x1 = ( ( int ) edge.origin.x + mins[ 0 ] ) * ratio + WHITE_AREA;
            int y1 = ( ( int ) edge.origin.y + mins[ 1 ] ) * ratio + WHITE_AREA;
            int x2 = ( ( int ) edge.next.origin.x + mins[ 0 ] ) * ratio + WHITE_AREA;
            int y2 = ( ( int ) edge.next.origin.y + mins[ 1 ] ) * ratio + WHITE_AREA;
            graphics.drawLine( x1, y1, x2, y2 );
        }
    }

    private static
    void drawInternalDiagonals( HalfEdge edge1, HalfEdge edge2, Graphics graphics,
                                int WHITE_AREA, int[] mins, int ratio ) {
        graphics.setColor( Color.ORANGE );
        List<Vector> vectors = new ArrayList<>( 2 );
        vectors.add( edge1.origin );
        vectors.add( edge2.origin );

        vectors = Drawer.getShiftedPoint( vectors, WHITE_AREA, mins, ratio );
        Vector vector1 = vectors.get( 0 );
        Vector vector2 = vectors.get( 1 );
        graphics.drawLine( ( int ) vector1.x, ( int ) vector1.y, ( int ) vector2.x, ( int ) vector2.y );
    }

    static
    void drawInternalDiagonals( DualVertex end, Graphics graphics,
                                        int WHITE_AREA, int[] mins, int ratio ) {
        List<List<Integer>> points = new ArrayList<>();

        while ( end != null ) {
            if ( end.shortestNeighbourEdge != null )
                drawInternalDiagonals( end.shortestNeighbourEdge, end.shortestNeighbourEdge.twin,
                        graphics, WHITE_AREA, mins, ratio );

            end = ( DualVertex ) end.parent;
        }
    }

    static
    void drawShortestPathWithTriangles( DualVertex end, Graphics graphics,
                                       int WHITE_AREA, int[] mins, int ratio ) {
        List<List<Integer>> points = new ArrayList<>();

        final int RADIUS = 6;
        final int CYCLE_OFFSET = RADIUS / 2;

        Vector prev = null;
        while ( end != null ) {
            List<Vertex> vertices = DCEL.walkAroundVertex( end.face );

            assert vertices.size() == 3;
            List<Vector> vectors = new ArrayList<>( vertices );
            vectors = Drawer.getShiftedPoint( vectors, WHITE_AREA, mins, ratio );
            Vector gravity = Triangles.getCenterOfGravity( vectors.get( 0 ), vectors.get( 1 ), vectors.get( 2 ) );
            int x1 = ( int ) gravity.x;
            int y1 = ( int ) gravity.y;
            graphics.setColor( Color.BLUE );
            graphics.fillOval( x1 - CYCLE_OFFSET, y1 - CYCLE_OFFSET, RADIUS, RADIUS );

            if ( prev == null ) prev = gravity;
            else {
                graphics.setColor( Color.BLUE );
                graphics.drawLine( x1, y1, ( int ) prev.x, ( int ) prev.y );
                prev = gravity;
            }

            end = ( DualVertex ) end.parent;
        }
    }

    static
    void storePoints( List<Integer> points, double x, double y ) {
        points.add( ( int ) x );
        points.add( ( int ) y );
    }

    static
    void storePoints( List<Integer> points, int x1, int y1, int x2, int y2 ) {
       points.add( x1 );
       points.add( y1 );
       points.add( x2 );
       points.add( y2 );
    }

    static
    void drawPolygon( Graphics graphics, List<Integer> points, Color color ) {
        assert points.size() % 4 == 0 : points;
        graphics.setColor( color );
        for ( int i = 0; i < points.size(); i += 4 ) {
            graphics.drawLine( points.get( i ), points.get( i + 1 ),
                    points.get( i + 2 ), points.get( i + 3 ) );
        }
    }

    private static
    List<Integer> drawPolygon( int WHITE_AREA,
                      List<HalfEdge> edges, int[] mins, int ratio ) {
        List<Integer> points = new ArrayList<>();
        for ( HalfEdge edge : edges ) {
            int x1 = ( ( int ) edge.origin.x + mins[ 0 ] ) * ratio + WHITE_AREA;
            int y1 = ( ( int ) edge.origin.y + mins[ 1 ] ) * ratio + WHITE_AREA;
            int x2 = ( ( int ) edge.next.origin.x + mins[ 0 ] ) * ratio + WHITE_AREA;
            int y2 = ( ( int ) edge.next.origin.y + mins[ 1 ] ) * ratio + WHITE_AREA;
            storePoints( points, x1, y1, x2, y2 );
        }

        return points;
    }

    static
    List<Integer> drawPolygon( List<Face> faces,
               int WHITE_AREA, int[] mins, int ratio ) {
        List<Integer> points = new ArrayList<>();
        for ( Face face : faces ) {
            if ( face.outComponent == null ) continue;

            List<HalfEdge> edges = DCEL.walkAroundEdge( face );
            points.addAll( drawPolygon( WHITE_AREA, edges, mins, ratio ) );
        }

        return points;
    }

    public static
    void main( String[] args ) {
        // 6
        // 5 0
        // 4 5
        // 1 3
        // -3 4
        // -2 1
        // 1 -1
        int xOffset = 4;
        int yOffset = 1;
        List<Vertex> vertices = new ArrayList<>();
        vertices.add( new MonotoneVertex( 5, 0 ) );
        vertices.add( new MonotoneVertex( 4, 5 ) );
        vertices.add( new MonotoneVertex( 1, 3 ) );
        vertices.add( new MonotoneVertex( -4, 4 ) );
        vertices.add( new MonotoneVertex( -2, 1 ) );
        vertices.add( new MonotoneVertex( 1, -1 ) );

        /*for ( Vertex vertex : vertices ) {
            vertex.x += xOffset;
//            vertex.x *= 5;
            vertex.y += yOffset;
//            vertex.y *= 5;
        }

        int xMax = 9;
        int yMax = 6;

        int whiteArea = 50;
        int ratio = Math.min( ( CANVAS_WIDTH - whiteArea * 2 ) / xMax, ( CANVAS_HEIGHT - whiteArea * 2 ) / yMax );
        for ( Vertex vertex : vertices ) {
            vertex.x *= ratio - 1;
            vertex.y *= ratio - 1;
        }

        for ( Vertex vertex : vertices ) {
            vertex.x += whiteArea;
            vertex.y += whiteArea;
        }*/

        Face[] faces = myLibraries.util.geometry.tools.Polygons.getDCEL( vertices );
//        draw( Arrays.asList( faces ), vertices  );
    }
}
