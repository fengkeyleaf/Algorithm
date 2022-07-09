package com.fengkeyleaf.util.geom;

/*
 * TestPointLoction.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 10/18/2021$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test DCEL data structure
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class TestHalfEdge {
    private static
    void testAddEdges() {
        Vector p1 = new Vector( 1, 1 );
        Vector p2 = new Vector( -1, 1 );
        Vector p3 = new Vector( -1, -1 );
        Vector p4 = new Vector( 1, -1 );
        Vector p5 = new Vector( 2, -2 );

        List<Vector> points = new ArrayList<>();
        points.add( p1 );
        points.add( p2 );
        points.add( p3 );
        points.add( p4 );

        List<Vertex> vertices = new ArrayList<>();
        points.forEach( p -> vertices.add( new Vertex( p ) ) );
        Face[] faces = Polygons.getDCEL( vertices );

        Vertex target = vertices.get( 0 );
        Vertex origin = new Vertex( Vector.origin );
//        HalfEdges.addEdge( target, origin );

        target = vertices.get(3);
        origin = new Vertex( p5 );
//        HalfEdges.addEdge( target, origin );

        points.add( Vector.origin );
        points.add( p5 );
        int size = 6;
        DrawingProgram drawer = new DrawingProgram( "", size, size );
//        drawer.draw( points, faces );

        drawer.initialize();
    }

    private static
    void testSplit() {
        Vector p1 = new Vector( 1, 1 );
        Vector p2 = new Vector( -1, 1 );
        Vector p3 = new Vector( -1, -1 );
        Vector p4 = new Vector( 1, -1 );
        Vector p5 = new Vector( 2, -2 );
        Vector p6 = new Vector( 0, 1 );

        List<Vector> points = new ArrayList<>();
        points.add( p1 );
        points.add( p2 );
        points.add( p3 );
        points.add( p4 );

        List<Vertex> vertices = new ArrayList<>();
        points.forEach( p -> vertices.add( new Vertex( p ) ) );
        Face[] faces = Polygons.getDCEL( vertices );

        Vertex split = new Vertex( p6 );
        List<HalfEdge> edges = vertices.get( 0 ).allIncidentEdges();
        HalfEdge edge = null;
        for ( HalfEdge e :
                edges ) {
            if ( e.origin.equals( vertices.get( 0 ) ) && e.next.origin.equals( vertices.get( 1 ) ) )
                edge = e;
        }
        edge.split( split );
        assert edge.walkAroundEdge() != null;
        assert edge.twin.walkAroundEdge() != null;

        Vertex target = split;
        Vertex origin = new Vertex( Vector.origin );
//        HalfEdges.addEdge( target, origin );

        points.add( Vector.origin );
        points.add( p6 );
        int size = 6;
        DrawingProgram drawer = new DrawingProgram( "", size, size );
//        drawer.draw( points, faces );

        drawer.initialize();
    }

    private static
    void testDeleteEdges() {
        Vector p1 = new Vertex( 1, 1 );
        Vector p2 = new Vertex( -1, 1 );
        Vector p3 = new Vertex( -1, -1 );
        Vector p4 = new Vertex( 1, -1 );
        Vector p5 = new Vertex( 2, -2 );
        Vector p6 = new Vertex( 0, 1 );
        Vector p7 = new Vertex( 0, -1 );

        List<Vector> points = new ArrayList<>();
        points.add( p1 );
        points.add( p2 );
        points.add( p3 );
        points.add( p4 );

        List<Vertex> vertices = new ArrayList<>();
        points.forEach( p -> vertices.add( new Vertex( p ) ) );
        Face[] faces = Polygons.getDCEL( vertices );

        List<HalfEdge> edges =  faces[ 1 ].walkAroundEdge();
        System.out.println( edges.get( 0 ).twin );
        edges.get( 0 ).twin.delete();
        System.out.println( edges.get( 1 ) );
        edges.get( 1 ).twin.delete();
        System.out.println( edges.get( 2 ).twin );
        edges.get( 2 ).twin.delete();
        System.out.println( edges.get( 3 ) );
        edges.get( 3 ).twin.delete();

        int size = 6;
        DrawingProgram drawer = new DrawingProgram( "", size, size );
        drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, points );
        Arrays.asList( faces ).forEach( f -> drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f ) );

        List<HalfEdge> drawingEdges = edges.get( 3 ).walkAroundEdge();
        List<Line> lines = new ArrayList<>();
        drawingEdges.forEach( e -> lines.add( e.getSegment() ) );
        drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, points );

        drawer.initialize();
    }

    public static
    void main( String[] args ) {
//        testAddEdges();
//        testSplit();
        testDeleteEdges();
    }
}
