package myLibraries.util.geometry;

/*
 * TestPointLoction.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 10/21/2021$
 */

import myLibraries.GUI.geometry.DCELProgram;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.DCEL.Vertex;
import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class TestPointLoction {
    static Vector p1 = new Vertex( 10, 10 );
    static Vector p2 = new Vertex( -10, 10 );
    static Vector p3 = new Vertex( -10, -10 );
    static Vector p4 = new Vertex( 10, -10 );
    static Vector p5 = new Vertex( 2, -2 );
    static Vector p6 = new Vertex( 0, 1 );
    static Vector p7 = new Vertex( 0, -1 );


    private static
    void testConnectVerticesV() {
        List<Vector> points = new ArrayList<>();
//        points.add( p1 );
//        points.add( p2 );
//        points.add( p3 );
//        points.add( p4 );

        SearchVertex R = null;
//        SearchVertex R = PointLocation.getBoundingBox( p3, p1 );
        List<Face> faces = new ArrayList<>();
//        faces.add( R.trapezoid );

        int size = 40;
        DCELProgram drawer = new DCELProgram( size, size );

        SearchStructure SS = new SearchStructure( R );

        // add line (-2,-2) <-> (2, 2)
        Vector p = new Vector( -2, -2 );
        Vector q = new Vector( 2, 2 );
        points.add( p );
        points.add( q );
        Line line = new Line( p, q );
        Stack<SearchVertex> de = new Stack<>();

        SearchVertex res = TrapezoidalMap.handleP( SS.get( line ), line, null );
        SearchStructure SS2 = new SearchStructure( res );
        TrapezoidalMap.drawTrapezoidalMap( SS2, drawer, points );

        // add line (0,-2) <-> (4, -3)
//        p = new Vector( 0, -2 );
//        q = new Vector( 4, -3 );
        p = new Vector( 1, -1 );
        q = new Vector( 7, 2 );
        points.add( p );
        points.add( q );
        line = new Line( p, q );
        List<SearchVertex> Ds = PointLocation.followSegment( SS2, line );
        TrapezoidalMap.drawDs( Ds, drawer );

        PointLocation.update( Ds, line, false );
        drawer.resetData();
        TrapezoidalMap.drawTrapezoidalMap( SS2, drawer, points );

//        p = new Vector( 1, -1 );
//        q = new Vector( 7, 2 );
//        p = new Vector( 0, -2 );
//        q = new Vector( 4, -3 );
//        p = new Vector( 0, -2 );
//        q = new Vector( 8, -3 );
        p = new Vector( -3, 2 );
        q = new Vector( 8, 5 );
        points.add( p );
        points.add( q );
        line = new Line( p, q );
        Ds = PointLocation.followSegment( SS2, line );
        TrapezoidalMap.drawDs( Ds, drawer );

        PointLocation.update( Ds, line, false );
        drawer.resetData();
        TrapezoidalMap.drawTrapezoidalMap( SS2, drawer, points );

        p = new Vector( -5, -7 );
        q = new Vector( 9, -3 );
        points.add( p );
        points.add( q );
        line = new Line( p, q );
//        Ds = PointLocation.followSegment( SS2, line );
//        drawDs( Ds, drawer );

//        faces.clear();
//        Vector queryPoint = new Vector( -4, 0 );
//        SearchVertex queryResult = SS2.get( queryPoint );
//        faces.add( queryResult.trapezoid );
//        drawer.addPoly( faces, DrawingProgram.INTERSECTION_COLOR );
//        points.clear();
//        points.add( queryPoint );
//        drawer.addVertices( points, DrawingProgram.INTERSECTION_COLOR );
        drawer.initialize();
    }

    public static
    void main( String[] args ) {
        testConnectVerticesV();
    }
}
