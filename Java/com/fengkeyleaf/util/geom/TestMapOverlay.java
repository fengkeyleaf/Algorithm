package com.fengkeyleaf.util.geom;

/*
 * TestMapOverlay.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 6/7/2022$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to test algorithms related to MapOverlay.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class TestMapOverlay {
    private static final String title = "Map Overlay";

    // https://www.geogebra.org/calculator/f66yncgx
    static
    void testCopy1() {
        DrawingProgram drawer = new DrawingProgram( title, 10, 10 );
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( 0, 0 );
        Vertex v2 = new Vertex( 1, -1 );
        Vertex v3 = new Vertex( 3, 1 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );

        Face f = Polygons.getDCEL( vertices )[ 0 ];
        Face copy = new Face( f );

        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, copy );

        drawer.initialize();
    }

    // https://www.geogebra.org/calculator/eq3dq7rk
    static
    void testCopy2() {
        int size = 20;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -6, -1 );
        Vertex v2 = new Vertex( 1, -6 );
        Vertex v3 = new Vertex( 2, 5 );
        Vertex v4 = new Vertex( 0, 1 );
        Vertex v5 = new Vertex( -3, 0 );
        Vertex v6 = new Vertex( 0, -3 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );

        Face f1 = Polygons.getDCEL( vertices )[ 1 ];

        vertices.clear();

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );

        Face f2 = Polygons.getDCEL( vertices )[ 1 ];
        f1.outComponent.incidentFace.innerComponents.add( f2.outComponent.twin );
        f2.outComponent.twin.resetIncidentFace( f1 );

//        drawer.drawPolyAll( f1.outComponent.twin.incidentFace, DrawingProgram.NORMAL_POLYGON_COLOR );

        Face copy = new Face( f1.outComponent.twin.incidentFace );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, copy );

        drawer.initialize();
    }

    // https://www.geogebra.org/calculator/pmc547xy
    static
    void testCopy3() {
        int size = 20;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( 0, 3 );
        Vertex v2 = new Vertex( -6, 1 );
        Vertex v3 = new Vertex( -2, -4 );
        Vertex v6 = new Vertex( 5, 0 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );

        Face f1 = Polygons.getDCEL( vertices )[ 1 ];

        vertices.clear();
        vertices.add( v1 );
        vertices.add( v3 );
        vertices.add( v6 );

        Face f2 = Polygons.getDCEL( vertices )[ 1 ];
        f1.outComponent.twin.incidentFace.innerComponents.add( f2.outComponent.twin );
        f2.outComponent.twin.resetIncidentFace( f1.outComponent.twin.incidentFace );

//        drawer.drawPolyAll( f1.outComponent.twin.incidentFace, DrawingProgram.NORMAL_POLYGON_COLOR );

        Face copy = new Face( f1.outComponent.twin.incidentFace );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, copy );

        drawer.initialize();
    }

    // https://www.geogebra.org/calculator/sxwmhbzg
    static
    Face[] testMapOverlay1() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 1 );
        Vertex v2 = new Vertex( 2, -4 );
        Vertex v3 = new Vertex( 3, 5 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -2, 0 );
        Vertex v5 = new Vertex( 1, -1 );
        Vertex v6 = new Vertex( 0, 2 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 20;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();

        MapOverlay.compute( f1, f2 );
//        MapOverlay.compute( f1, null );
//        MapOverlay.compute( null, f2 );
//        MapOverlay.compute( null, null );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/uvj43syj
    static
    Face[] testMapOverlay2() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( 0, -2 );
        Vertex v2 = new Vertex( 1, 5 );
        Vertex v3 = new Vertex( -6, 2 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( 8, -5 );
        Vertex v5 = new Vertex( 4, 5 );
        Vertex v6 = new Vertex( -5, -3 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 20;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();

//        MapOverlay.compute( f1, f2 );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/t5qr8def
    static
    Face[] testMapOverlay3() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( 2, 1 );
        Vertex v2 = new Vertex( -3, 6 );
        Vertex v3 = new Vertex( -7, 2 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( 9, -4 );
        Vertex v5 = new Vertex( 9, 5 );
        Vertex v6 = new Vertex( 4, 3 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 20;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );
//        drawer.initialize();
//        MapOverlay.compute( f1, f2 );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/dn5zuzxq
    static
    Face[] testMapOverlay4() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 1 );
        Vertex v2 = new Vertex( 2, -4 );
        Vertex v3 = new Vertex( 3, 5 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -2, 0 );
        Vertex v5 = new Vertex( 1, -1 );
        Vertex v6 = new Vertex( 0, 2 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v7 = new Vertex( 8, -3 );
        Vertex v8 = new Vertex( 15, 3 );
        Vertex v9 = new Vertex( 6, 1 );

        vertices.add( v7 );
        vertices.add( v8 );
        vertices.add( v9 );
        Face f3 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f3 );

//        drawer.initialize();
//        Face s1 = MapOverlay.compute( f1, f2 );
//        MapOverlay.compute( s1, f3 );

        return new Face[] { MapOverlay.compute( f1, f2 ), f3 };
    }

    // https://www.geogebra.org/calculator/ba6w6g2h
    static
    Face[] testMapOverlay5() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -4, -4 );
        Vertex v2 = new Vertex( -2, 4 );
        Vertex v3 = new Vertex( -8, -2 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( 4, 2 );
        Vertex v5 = new Vertex( 12, -4 );
        Vertex v6 = new Vertex( 10, 6 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v7 = new Vertex( 14, -6 );
        Vertex v8 = new Vertex( 16, 8 );
        Vertex v9 = new Vertex( -8, 6 );
        Vertex v10 = new Vertex( -11, -7 );

        vertices.add( v7 );
        vertices.add( v8 );
        vertices.add( v9 );
        vertices.add( v10 );
        Face f3 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f3 );

//        drawer.initialize();
//        testMapOverlay( f1, f2, f3 );
//        testMapOverlay( f1, f3, f2 );
//        testMapOverlay( f2, f1, f3 );
//        testMapOverlay( f2, f3, f1 );
//        testMapOverlay( f3, f2, f1 );
//        testMapOverlay( f3, f1, f2 );

        return new Face[] { MapOverlay.compute( f1, f2 ), f3 };
    }

    static
    void testMapOverlay( Face ...F ) {
        Face t = null;
        for ( Face face : F ) {
            if ( t == null ) {
                t = face;
                continue;
            }

            t = MapOverlay.compute( t, face );
        }
    }

    // https://www.geogebra.org/calculator/qd6pq92n
    static
    Face[] testMapOverlay6() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -4, 3 );
        Vertex v2 = new Vertex( 0, 2 );
        Vertex v3 = new Vertex( 0, 7 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -5, -2 );
        Vertex v5 = new Vertex( 2, -6 );
        Vertex v6 = new Vertex( 4, 1 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v7 = new Vertex( -7, -8 );
        Vertex v8 = new Vertex( 6, -8 );
        Vertex v9 = new Vertex( 6, 8 );
        Vertex v10 = new Vertex( -7, 8 );

        vertices.add( v7 );
        vertices.add( v8 );
        vertices.add( v9 );
        vertices.add( v10 );
        Face f3 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f3 );

//        drawer.initialize();
//        testMapOverlay( f1, f2, f3 );
//        testMapOverlay( f1, f3, f2 );
//        testMapOverlay( f2, f1, f3 );
//        testMapOverlay( f2, f3, f1 );
//        testMapOverlay( f3, f2, f1 );
//        testMapOverlay( f3, f1, f2 );

        return new Face[] { MapOverlay.compute( f1, f2 ), f3 };
    }

    // https://www.geogebra.org/calculator/f58xwbh8
    static
    Face[] testMapOverlay7() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 3 );
        Vertex v2 = new Vertex( 1, -6 );
        Vertex v3 = new Vertex( 4, 7 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -3, 3 );
        Vertex v5 = new Vertex( 1, -2 );
        Vertex v6 = new Vertex( 2, 3 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();
//        testMapOverlay( f1, f2 );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/vnyzzymm
    static
    Face[] testMapOverlay8() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -6, 2 );
        Vertex v2 = new Vertex( 0, -2 );
        Vertex v3 = new Vertex( 5, 6 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -7, -3 );
        Vertex v5 = new Vertex( 14, 0 );
        Vertex v6 = new Vertex( -1, 2 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

        drawer.initialize();
//        testMapOverlay( f1, f2 );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/uxxdu4rt
    static
    Face[] testMapOverlay9() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 3 );
        Vertex v2 = new Vertex( -3, -3 );
        Vertex v3 = new Vertex( 5, 5 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( 0, 4 );
        Vertex v5 = new Vertex( 9, 0 );
        Vertex v6 = new Vertex( 10, 6 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();
//        testMapOverlay( f1, f2 );
//        testMapOverlay( f2, f1 );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/uprqpagd
    static
    Face[] testMapOverlay10() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -25, 3 );
        Vertex v2 = new Vertex( -19, -3 );
        Vertex v3 = new Vertex( -12, 8 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 56;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();
//        testMapOverlay( f1, f2 );
//        testMapOverlay( f2, f1 );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/xeqw4pfu
    static
    Face[] testMapOverlay11() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 3 );
        Vertex v2 = new Vertex( 4, -9 );
        Vertex v3 = new Vertex( 7, 6 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -5, 3 );
        Vertex v5 = new Vertex( -2, -1 );
        Vertex v6 = new Vertex( -1, 4 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();
//        testMapOverlay( f1, f2 );
//        testMapOverlay( f2, f1 );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/yg3xrpcw
    static
    Face[] testMapOverlay12() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 3 );
        Vertex v2 = new Vertex( 5, -3 );
        Vertex v3 = new Vertex( 7, 6 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -7, -4 );

        vertices.add( v1 );
        vertices.add( v4 );
        vertices.add( v2 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();
//        testMapOverlay( f1, f2 );
//        testMapOverlay( f2, f1 );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/euuhuqzd
    static
    Face[] testMapOverlay13() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 0 );
        Vertex v2 = new Vertex( 0, -3 );
        Vertex v3 = new Vertex( 1, 5 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -6, -9 );
        Vertex v5 = new Vertex( -3, -10 );

        vertices.add( v2 );
        vertices.add( v4 );
        vertices.add( v5 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 26;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();
//        testMapOverlay( f1, f2 );
//        testMapOverlay( f2, f1 );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/ttjr3jtc
    static
    Face[] testMapOverlay14() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 3 );
        Vertex v2 = new Vertex( 1, -2 );
        Vertex v3 = new Vertex( 6, 6 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -6, -4 );
        Vertex v5 = new Vertex( -4, -8 );
        Vertex v6 = new Vertex( 15, 2 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();
//        testMapOverlay( f1, f2 );
//        testMapOverlay( f2, f1 );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/yg3xrpcw
    static
    Face[] testMapOverlay15() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 3 );
        Vertex v2 = new Vertex( 5, -3 );
        Vertex v3 = new Vertex( 7, 6 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -7, -4 );

        vertices.add( v1 );
        vertices.add( v4 );
        vertices.add( v2 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v5 = new Vertex( -11, -2 );
        Vertex v6 = new Vertex( -9, -10 );
        Vertex v7 = new Vertex( 3, -5 );

        vertices.add( v5 );
        vertices.add( v6 );
        vertices.add( v7 );
        Face f3 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f3 );

        drawer.initialize();
//        testMapOverlay( f2, f1, f3 );

        return new Face[] { MapOverlay.compute( f1, f2 ), f3 };
    }

    // https://www.geogebra.org/calculator/qry46zke
    static
    Face[] testMapOverlay16() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -7, 3 );
        Vertex v2 = new Vertex( -2, -8 );
        Vertex v3 = new Vertex( 7, 8 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -4, 0 );
        Vertex v5 = new Vertex( -1, -5 );
        Vertex v6 = new Vertex( 1, 5 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v7 = new Vertex( -9, -5 );
        Vertex v8 = new Vertex( -2, -10 );
        Vertex v9 = new Vertex( 7, 1 );

        vertices.add( v7 );
        vertices.add( v8 );
        vertices.add( v9 );
        Face f3 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f3 );

//        drawer.initialize();
//        testMapOverlay( f2, f3, f1 );

        return new Face[] { MapOverlay.compute( f1, f2 ), f3 };
    }

    // https://www.geogebra.org/calculator/e5kytzmd
    static
    Face[] testMapOverlay17() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -2, 8 );
        Vertex v2 = new Vertex( -13, -4 );
        Vertex v3 = new Vertex( 3, -2 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -7, 0 );
        Vertex v5 = new Vertex( -4, -2 );
        Vertex v6 = new Vertex( -3, 4 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v7 = new Vertex( 5, 7 );
        Vertex v8 = new Vertex( -9, -5 );
        Vertex v9 = new Vertex( 9, -7 );

        vertices.add( v7 );
        vertices.add( v8 );
        vertices.add( v9 );
        Face f3 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v10 = new Vertex( 3, 3 );
        Vertex v11 = new Vertex( 1, -5 );
        Vertex v12 = new Vertex( 7, -6 );

        vertices.add( v10 );
        vertices.add( v11 );
        vertices.add( v12 );
        Face f4 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 34;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f3 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f4 );

        drawer.initialize();
        Face s1 = MapOverlay.compute( f1, f2 );
        Face s2 = MapOverlay.compute( f3, f4 );
//        MapOverlay.compute( f2, S2 );
//        MapOverlay.compute( s1, s2 );

        return new Face[] { s1, s2 };
    }

    // https://www.geogebra.org/calculator/ttjr3jtc
    static
    Face[] testMapOverlay18() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 3 );
        Vertex v2 = new Vertex( 1, -2 );
        Vertex v3 = new Vertex( 6, 6 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -6, -4 );
        Vertex v5 = new Vertex( -4, -8 );
        Vertex v6 = new Vertex( 15, 2 );

        vertices.add( v4 );
        vertices.add( v5 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v7 = new Vertex( -8, 0 );
        Vertex v8 = new Vertex( 0, -10 );
        Vertex v9 = new Vertex( 19, -6 );

        vertices.add( v7 );
        vertices.add( v8 );
        vertices.add( v9 );
        Face f3 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 40;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );
        drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f3 );

        drawer.initialize();
//        testMapOverlay( f1, f3 );
        Face s1 = MapOverlay.compute( f1, f3 );
//        MapOverlay.compute( s1, f2 );

        return new Face[] { s1, f2 };
    }

    static
    Face[] testMapOverlay19() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 0 );
        Vertex v2 = new Vertex( 5, 0 );
        Vertex v3 = new Vertex( 5, 5 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v4 = new Vertex( -5, 5 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v4 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 40;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();
//        MapOverlay.compute( f1, f2 );

        return new Face[] { f1, f2 };
    }

    // https://www.geogebra.org/calculator/bkpbcy6u
    static
    Face[] testMapOverlay20() {
        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( -5, 4 );
        v1 = new Vertex( -20, 18 );
        Vertex v2 = new Vertex( -5, -4 );
        v2 = new Vertex( -20, -20 );
        Vertex v3 = new Vertex( 3, -4 );
        v3 = new Vertex( 20 ,-20 );
        Vertex v4 = new Vertex( 3, 1 );
        v4 = new Vertex( 20, -2 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        vertices.add( v4 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v5 = new Vertex( -5, 2 );
        v5 = new Vertex( -20, 9 );
        Vertex v6 = new Vertex( 3, 3 );
        v6 = new Vertex( 20, 2 );

        vertices.add( v5 );
        vertices.add( v2 );
        vertices.add( v3 );
        vertices.add( v6 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 60;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();
//        MapOverlay.compute( f1, f2 );

        return new Face[] { f1, f2 };
    }

    static
    Face[] testMapOverlay21() {
        // [5000000.0|5000000.0, 416666.8333333333|5000000.0, -416666.5|-5000000.0, 5000000.0|-5000000.0]
        // [ 5000000,5000000 | 416667,5000000 | 416667,-5000000 | 5000000,-5000000 ]
        // [5000000.0|5000000.0, -2500002.0|5000000.0, 2499998.0|-5000000.0, 5000000.0|-5000000.0]
        // [ 5000000,5000000, -2500002,5000000, 2499998,-5000000 | 5000000,-5000000 ]

        List<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex( 5000000,5000000 );
//        v1 = new Vertex( 5, 5 );
        Vertex v2 = new Vertex( 416667,5000000 );
        v2 = new Vertex( 416666.8333333333, 5000000.0 );
//        v2 = new Vertex( -3, 5 );
        Vertex v3 = new Vertex( 416666,-5000000 );
        v3 = new Vertex( -416666.5,-5000000.0 );
//        v3 = new Vertex( -2, -5 );
        Vertex v4 = new Vertex( 5000000,-5000000 );
//        v4 = new Vertex( 5, -5 );

        vertices.add( v1 );
        vertices.add( v2 );
        vertices.add( v3 );
        vertices.add( v4 );
        Face f1 = Polygons.getDCEL( vertices )[ 0 ];

        vertices.clear();
        Vertex v5 = new Vertex( -2500002,5000000 );
//        v5 = new Vertex( -5, 5 );
        Vertex v6 = new Vertex( 2499998,-5000000 );
//        v6 = new Vertex( 0, -5 );

        vertices.add( v1 );
        vertices.add( v5 );
        vertices.add( v6 );
        vertices.add( v4 );
        Face f2 = Polygons.getDCEL( vertices )[ 0 ];

        int size = 20000000;
//        size = 20;
        DrawingProgram drawer = new DrawingProgram( title, size, size );
        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, f1 );
        drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, f2 );

//        drawer.initialize();
        MapOverlay.compute( f1, f2 );

        return new Face[] { f1, f2 };
    }

    static
    void testCopy() {
        testCopy1();
//        testCopy2();
//        testCopy3();
    }

    static
    void testMapOverlay() {
//        testMapOverlay1();
//        testMapOverlay2();
//        testMapOverlay3();
//        testMapOverlay4();
//        testMapOverlay5();
//        testMapOverlay6();
//        testMapOverlay7(); // degenerate case, the number of half-edge directly left to a vertex, are two.
//        testMapOverlay8(); // degenerate case
//        testMapOverlay9(); // degenerate case
//        testMapOverlay10(); // degenerate case, two identical subdivisions.
//        testMapOverlay11(); // degenerate case, S2 is contained in S1.
//        testMapOverlay12(); // degenerate case, two overlapping edges with different direction.
//        testMapOverlay13(); // degenerate case, self-intersecting vertex.
//        testMapOverlay14(); // degenerate case, one vertex kisses a half-edge, almost no intersection area
//        testMapOverlay15(); // degenerate case, two overlapping edges with different direction.

        // complex test cases
//         testMapOverlay16();
//         testMapOverlay17();
//         testMapOverlay18(); // including degenerate case from test 14.

        // test cases from Half-plane intersection.
//        testMapOverlay19();
//        testMapOverlay20();

        // test cases from fruit ninja
        testMapOverlay21();
    }

    public static
    void main( String[] args ) {
//        testCopy();
        testMapOverlay();
    }
}
