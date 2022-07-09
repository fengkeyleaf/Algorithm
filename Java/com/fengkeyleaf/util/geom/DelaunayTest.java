package com.fengkeyleaf.util.geom;

/*
 * DelaunayTest.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 4/13/2022$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to test algorithms related to Delaunay Triangulation.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class DelaunayTest {
    // test case 5
    static
    void test1() {
        Vertex p0 = new Vertex( 2, 1 );
        Vertex pMin1 = new Vertex( 10, -10 );
        pMin1.mappingID = -1; // may be redundant
        // Next, we choose p−2 to lie on the line l−2 sufficiently far to
        // the left that p−2 lies outside every circle defined by
        // three non-collinear points of P∪{p−1},
        // and such that the counterclockwise ordering of the points of P∪{p−1} around p−2 is
        // identical to their (lexicographic) ordering.
        Vertex pMin2 = new Vertex( -10, 10 );
        pMin2.mappingID = -2;

        // 3.1 Initialize T as the triangulation consisting of the single triangle p0P−1P−2.
        List<Vertex> vertices = new ArrayList<>( 3 );
        // counter-clock wise order. p-1 -> p0 -> p-2
        vertices.add( pMin1 );
        vertices.add( p0 );
        vertices.add( pMin2 );
        // triangle p0P-1P02 -> faces[ 1 ]
        Face[] faces = null;
        try {
            faces = Polygons.getDCEL( vertices, DelaunayFace.class.getDeclaredConstructor() );
        } catch ( NoSuchMethodException | SecurityException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        Face f = faces[ 1 ];
        List<HalfEdge> edges = f.walkAroundEdge();
        assert edges.size() == 3;

        HalfEdge e1 = edges.get( 0 );
        HalfEdge e2 = edges.get( 1 );
        HalfEdge e3 = edges.get( 2 );

        Vertex pr = new Vertex( 4, -2 );
        // 9.1 first connect the first vertex and pr, no new face generated.
        HalfEdge e4 = pr.getEdges( e1.origin );
        f.outComponent = e4;
        e1.connect( e4 );
        e4.twin.connect( e3 );

        // 9.2 connect other two vertices, and two new faces generated.
        HalfEdge e5 = pr.getEdges( e2.origin );
        e4.connect( e5.twin );
        e2.connect( e5 );
        e5.twin.connect( e1 );

        HalfEdge e6 = pr.getEdges( e3.origin );
        e5.connect( e6.twin );
        e6.twin.connect( e2 );

        e6.connect( e4.twin );
        e3.connect( e6 );

        List<Vector> points = new ArrayList<>( vertices.size() );
        points.addAll( vertices );
        BoundingBox b = BoundingBox.getBoundingBox( points, 10 );
        DrawingProgram drawer = new DrawingProgram( "", b.width, b.height );
        Arrays.asList( faces ).forEach( face -> drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, face ) );

        drawer.initialize();
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
