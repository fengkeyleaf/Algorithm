package com.fengkeyleaf.util.geom;

/*
 * TestVector.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/8/2022$
 */

import com.fengkeyleaf.lang.MyMath;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class TestVector {

    private static
    void testSortByAngle() {
        List<Vector> points = new ArrayList<>();
        points.add( Vector.origin );
        points.add( new Vector( 1, 1 ) );
        points.add( new Vector( 0, 1 ) );
        points.add( new Vector( 1, 0 ) );
        points.add( new Vector( -1, 1 ) );
        points.add( new Vector( -1, 1 ) );
        points.add( new Vector( 1, -1 ) );
        points.add( new Vector( -1, -1 ) );
        points.add( new Vector( -1, 0 ) );
        points.add( new Vector( 0, -1 ) );
        points.add( new Vector( -2, 2 ) );
        points.add( new Vector( 2, -2 ) );
        points.add( new Vector( 2, 2 ) );
        points.add( new Vector( -4, -4 ) );
        points.add( new Vector( 0, 2 ) );
        points.add( new Vector( 0, -3 ) );
        points.add( new Vector( -2, 0 ) );
        points.add( new Vector( 2, 0 ) );

        points.forEach( p -> {
            p.x += 10;
            p.y += 10;
        } );

        System.out.println( Vectors.sortByAngleClockWise( Vector.origin, points ) );

        System.out.println( Vectors.firstClockWise( Vector.origin, new Vector( 0, 1 ), points ) ); // (0, 2)
        System.out.println( Vectors.firstCounterClockWise( Vector.origin, new Vector( 0, 1 ), points ) ); // (0, 2), (-1, 1)
    }

    static
    void test1() {
        //        System.out.println( new Line( -2,-1.6666666666666667, -28.2828568570857,47.1380947618095 ).getVector().lengthSq() );
//        System.out.println( new Line( -2.0081616392799093,-1.669387213093303, -28.282893361053,47.13815560175498 ).getVector().lengthSq() );
//        System.out.println( new Vector( -2,-1.6666666666666667 ).subtract( new Vector( -28.2828568570857,47.1380947618095 ) ).lengthSq() );
//        System.out.println( new Vector( -2.0081616392799093,-1.669387213093303 ).subtract( new Vector( -28.282893361053,47.13815560175498 ) ).lengthSq() );

//        System.out.println( new Vector( 0, 1 ).angleTo( new Vector( 0, -1 ) ) ); // 3.141592653589793
//        System.out.println( new Vector( 0, -1 ).angleTo( new Vector( 0, -1 ) ) ); // 0.0
//        System.out.println( new Vector( 0, 1 ).angleTo( new Vector( 1, 1 ) ) ); // 0.7853981633974484

        System.out.println( new Vector( 0, 1 ).dot( new Vector( 0, -1 ) ) ); // -1
        System.out.println( new Vector( 0, -1 ).dot( new Vector( 0, -1 ) ) ); // 1
        System.out.println( new Vector( 0, 1 ).dot( new Vector( 1, 1 ) ) ); // 1
    }

    static
    void testAngle() {
        Vertex v1 = new Vertex( -1, 1 );
        Vertex v2 = new Vertex( 0, 1 );
        Vertex v3 = new Vertex( Vector.origin );
        Vertex v4 = new Vertex( 1, 1 );
        int coor = 100000000; // 10 ^ 8
        Vertex v5 = new Vertex( coor, coor - 1 );
        System.out.println( v2.angleTo( v4 ) );
        System.out.println( v2.angleTo( v5 ) );
        System.out.println( MyMath.isEqual( v2.angleTo( v4 ), v2.angleTo( v5 ) ) );

        HalfEdge e1 = new HalfEdge( v1 );
        HalfEdge e2 = new HalfEdge( v2 );
        e1.setTwins( e2 );

        HalfEdge e3 = new HalfEdge( v3 );
        HalfEdge e4 = new HalfEdge( v4 );
        e3.setTwins( e4 );

        HalfEdge e5 = new HalfEdge( v3 );
        HalfEdge e6 = new HalfEdge( v5 );
        e5.setTwins( e6 );

        e1.connect( e2 );
        e2.connect( e1 );

        e6.connect( e5 );
        e5.connect( e4 );
        e4.connect( e3 );
        e3.connect( e6 );

        v1.incidentEdge = e1;
        v2.incidentEdge = e2;

        v3.incidentEdge = e3;
        v3.incidentEdge = e5;

        v4.incidentEdge = e4;
        v5.incidentEdge = e6;

        Face f = new Face( e1 );
        e1.incidentFace = f;
        e2.incidentFace = f;
        e3.incidentFace = f;
        e4.incidentFace = f;
        e5.incidentFace = f;
        e6.incidentFace = f;

        HalfEdge e7 = new HalfEdge( v3 );
        HalfEdge e8 = new HalfEdge( v2 );
        e7.setTwins( e8 );

        e7.incidentFace = f;
        e8.incidentFace = f;

        System.out.println();
        System.out.println( v3.firstClockWiseEdge( v2 ) ); // res: (0.0, 0.0) -> 4(1.0E8, 9.9999999E7), but should be 2(0.0, 0.0) -> 3(1.0, 1.0)
        HalfEdges.connectBoth( v2, v3, e7 );
        System.out.println( v3.allIncidentEdges() );
        // [2(0.0, 0.0) -> 4(1.0E8, 9.9999999E7), 4(1.0E8, 9.9999999E7) -> 2(0.0, 0.0), 2(0.0, 0.0) -> 1(0.0, 1.0), 1(0.0, 1.0) -> 2(0.0, 0.0)]

        List<HalfEdge> E = new ArrayList<>();
        E.add( e7 );
        E.add( e5 );
        E.add( e6 );
        E.add( e3 );
        E.add( e8 );
        E.add( e4 );

        E = HalfEdges.sortInClockWise( E, v3 );
//        v3.connect( E );
        System.out.println( v3.allIncidentEdges() );
        // [2(0.0, 0.0) -> 1(0.0, 1.0), 1(0.0, 1.0) -> 2(0.0, 0.0), 2(0.0, 0.0) -> 3(1.0, 1.0), 3(1.0, 1.0) -> 2(0.0, 0.0), 2(0.0, 0.0) -> 4(1.0E8, 9.9999999E7), 4(1.0E8, 9.9999999E7) -> 2(0.0, 0.0)]
    }

    public static
    void main( String[] args ) {
        testAngle();
    }
}
