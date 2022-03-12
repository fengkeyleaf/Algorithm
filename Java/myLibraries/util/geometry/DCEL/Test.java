package myLibraries.util.geometry.DCEL;

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

import myLibraries.GUI.geometry.DCELProgram;
import myLibraries.util.geometry.elements.Vector;
import myLibraries.util.geometry.Polygons;

import java.util.ArrayList;
import java.util.List;

/**
 * Test DCEL data structure
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

final class Test {
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
        HalfEdges.addEdge( target, origin );

        target = vertices.get(3);
        origin = new Vertex( p5 );
        HalfEdges.addEdge( target, origin );

        points.add( Vector.origin );
        points.add( p5 );
        int size = 6;
        DCELProgram drawer = new DCELProgram( size, size );
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
        List<HalfEdge> edges = DCEL.allIncidentEdges( vertices.get( 0 ) );
        HalfEdge edge = null;
        for ( HalfEdge e :
                edges ) {
            if ( e.origin.equals( vertices.get( 0 ) ) && e.next.origin.equals( vertices.get( 1 ) ) )
                edge = e;
        }
        HalfEdges.split( edge, split );
        assert DCEL.walkAroundEdge( edge ) != null;
        assert DCEL.walkAroundEdge( edge.twin ) != null;

        Vertex target = split;
        Vertex origin = new Vertex( Vector.origin );
        HalfEdges.addEdge( target, origin );

        points.add( Vector.origin );
        points.add( p6 );
        int size = 6;
        DCELProgram drawer = new DCELProgram( size, size );
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

        Vertex split = new Vertex( p6 );
        vertices.add( split );
        List<HalfEdge> edges = DCEL.allIncidentEdges( vertices.get( 0 ) );
        HalfEdge edge = null;
        for ( HalfEdge e :
                edges ) {
            if ( e.origin.equals( vertices.get( 0 ) ) && e.next.origin.equals( vertices.get( 1 ) ) )
                edge = e;
        }
        HalfEdges.split( edge, split );
        assert DCEL.walkAroundEdge( edge ) != null;
        assert DCEL.walkAroundEdge( edge.twin ) != null;

        Vertex target = split;
        Vertex origin = new Vertex( Vector.origin );
        vertices.add( origin );
        HalfEdges.addEdge( target, origin );

        points.add( Vector.origin );
        points.add( p6 );

        split = new Vertex( p7 );
        vertices.add( split );
        edges = DCEL.allIncidentEdges( vertices.get(3) );
        for ( HalfEdge e :
                edges ) {
            if ( e.origin.equals( vertices.get( 3 ) ) && e.next.origin.equals( vertices.get( 2 ) ) )
                edge = e;
        }
        HalfEdges.split( edge, split );
        assert DCEL.walkAroundEdge( edge ) != null;
        assert DCEL.walkAroundEdge( edge.twin ) != null;

        HalfEdges.connectHelper( vertices.get( vertices.size() - 2 ), split, new ArrayList<>() );
//        points.add( origin );
        points.add( p7 );

        edges = DCEL.allIncidentEdges( vertices.get( vertices.size() - 2 ) );
        for ( HalfEdge e :
                edges ) {
            if ( e.origin.equals( vertices.get( vertices.size() - 2 ) ) && e.twin.origin.equals( split ) )
                edge = e;
        }
        Face[] face = HalfEdges.deleteEdge( edge );
        faces[ 1 ] = face[ 1 ];

        HalfEdges.connectHelper( vertices.get( vertices.size() - 2 ), vertices.get( 3 ), new ArrayList<>() );

        edges = DCEL.allIncidentEdges( vertices.get( vertices.size() - 2 ) );
        for ( HalfEdge e :
                edges ) {
            if ( e.origin.equals( vertices.get( vertices.size() - 2 ) ) && e.twin.origin.equals( vertices.get( 4 ) ) )
                edge = e;
        }
        face = HalfEdges.deleteEdge( edge );
        faces[ 1 ] = face[ 1 ];

        int size = 6;
        DCELProgram drawer = new DCELProgram( size, size );
//        drawer.draw( points, faces );

        drawer.initialize();
    }

    public static
    void main( String[] args ) {
//        testAddEdges();
        testSplit();
        testDeleteEdges();
    }
}
