package myLibraries.GUI.geometry.triangulation;

/*
 * Drawer.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/28/2021$
 */

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.util.geometry.DCEL.DCEL;
import myLibraries.util.geometry.DCEL.HalfEdge;
import myLibraries.util.geometry.DCEL.Vertex;
import myLibraries.util.geometry.elements.Vector;
import myLibraries.util.geometry.Triangles;
import myLibraries.util.geometry.Vectors;
import myLibraries.util.graph.elements.DualVertex;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to draw The Shortest Path With Triangles and internal Diagonals
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class Drawer {

    static
    void drawShortestPathWithTriangles( DualVertex end, Graphics graphics,
                                        int originWidth, int originHeight,
                                        int windowWidth, int windowHeight ) {
        List<List<Integer>> points = new ArrayList<>();

        final int RADIUS = DrawingProgram.RADIUS;
        final int CYCLE_OFFSET = DrawingProgram.CYCLE_OFFSET;

        Vector prev = null;
        while ( end != null ) {
            List<Vertex> vertices = DCEL.walkAroundVertex( end.face );

            assert vertices.size() == 3;
            List<Vector> vectors = new ArrayList<>( vertices );
            vectors = Vectors.normalize( Vectors.reversedY( vectors ), originWidth, originHeight, windowWidth, windowHeight );
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

    private static
    void drawInternalDiagonals( HalfEdge edge1, HalfEdge edge2, Graphics graphics,
                                int originWidth, int originHeight,
                                int windowWidth, int windowHeight ) {
        graphics.setColor( Color.ORANGE );
        List<Vector> vectors = new ArrayList<>( 2 );
        vectors.add( edge1.origin );
        vectors.add( edge2.origin );

        vectors = Vectors.normalize( Vectors.reversedY( vectors ), originWidth, originHeight, windowWidth, windowHeight );
        Vector vector1 = vectors.get( 0 );
        Vector vector2 = vectors.get( 1 );
        graphics.drawLine( ( int ) vector1.x, ( int ) vector1.y, ( int ) vector2.x, ( int ) vector2.y );
    }

    static
    void drawInternalDiagonals( DualVertex end, Graphics graphics,
                                int originWidth, int originHeight,
                                int windowWidth, int windowHeight ) {
        List<List<Integer>> points = new ArrayList<>();

        while ( end != null ) {
            if ( end.shortestNeighbourEdge != null )
                drawInternalDiagonals( end.shortestNeighbourEdge, end.shortestNeighbourEdge.twin,
                        graphics, originWidth, originHeight, windowWidth, windowHeight );

            end = ( DualVertex ) end.parent;
        }
    }

}
