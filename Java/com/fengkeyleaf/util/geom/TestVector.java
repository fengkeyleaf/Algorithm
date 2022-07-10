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
        List<com.fengkeyleaf.util.geom.Vector> points = new ArrayList<>();
        points.add( com.fengkeyleaf.util.geom.Vector.origin );
        points.add( new com.fengkeyleaf.util.geom.Vector( 1, 1 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( 0, 1 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( 1, 0 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( -1, 1 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( -1, 1 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( 1, -1 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( -1, -1 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( -1, 0 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( 0, -1 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( -2, 2 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( 2, -2 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( 2, 2 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( -4, -4 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( 0, 2 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( 0, -3 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( -2, 0 ) );
        points.add( new com.fengkeyleaf.util.geom.Vector( 2, 0 ) );

        points.forEach( p -> {
            p.x += 10;
            p.y += 10;
        } );

        System.out.println( Vectors.sortByAngleClockWise( com.fengkeyleaf.util.geom.Vector.origin, points ) );

        System.out.println( Vectors.firstClockWise( com.fengkeyleaf.util.geom.Vector.origin, new com.fengkeyleaf.util.geom.Vector( 0, 1 ), points ) ); // (0, 2)
        System.out.println( Vectors.firstCounterClockWise( com.fengkeyleaf.util.geom.Vector.origin, new Vector( 0, 1 ), points ) ); // (0, 2), (-1, 1)
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
        Vector v1 = new Vector( 0, 1 );
        Vector v2 = new Vector( 1, 100000000 );
        Vector v3 = new Vector( 2, 190000000 );
        Vector v4 = new Vector( -1, 1 );
        System.out.println( v1.angleTo( v2 ) );
        System.out.println( v1.angleTo( v3 ) );
        System.out.println( MyMath.isEqualZero( v1.angleTo( v3 ) ) );


        Vertex ve1 = new Vertex( v4 );
        Vertex ve2 = new Vertex( v1 );

        Vertex ve3 = new Vertex( Vector.origin );
        Vertex ve4 = new Vertex( v2 );
        Vertex ve5 = new Vertex( v3 );

        HalfEdge e1 = new HalfEdge( ve1 );
        HalfEdge e2 = new HalfEdge( ve2 );
        e1.setTwins( e2 );

        HalfEdge e3 = new HalfEdge( ve3 );
        HalfEdge e4 = new HalfEdge( ve4 );
        e3.setTwins( e4 );

        HalfEdge e5 = new HalfEdge( ve3 );
        HalfEdge e6 = new HalfEdge( ve5 );
        e5.setTwins( e6 );

        e1.connect( e2 );
        e2.connect( e1 );

        e6.connect( e5 );
        e5.connect( e4 );
        e4.connect( e3 );
        e3.connect( e6 );

        ve1.incidentEdge = e1;
        ve2.incidentEdge = e2;

        ve3.incidentEdge = e3;
        ve3.incidentEdge = e5;

        ve4.incidentEdge = e4;
        ve5.incidentEdge = e6;

        Face f = new Face( e1 );
        e1.incidentFace = f;
        e2.incidentFace = f;
        e3.incidentFace = f;
        e4.incidentFace = f;
        e5.incidentFace = f;
        e6.incidentFace = f;

        HalfEdge e7 = new HalfEdge( ve3 );
        HalfEdge e8 = new HalfEdge( ve2 );
        e7.setTwins( e8 );

        e7.incidentFace = f;
        e8.incidentFace = f;

        System.out.println();
//        System.out.println( ve2.firstClockWiseEdge( ve1 ) );
//        HalfEdges.connectBoth( ve2, ve3, e7 );
        System.out.println( ve3.allIncidentEdges() );
        // [2(0.0, 0.0) -> 4(2.0, 1.9E8), 4(2.0, 1.9E8) -> 2(0.0, 0.0), 2(0.0, 0.0) -> 3(1.0, 1.0E8), 3(1.0, 1.0E8) -> 2(0.0, 0.0)]

        List<HalfEdge> E = new ArrayList<>();
        E.add( e3 );
        E.add( e4 );
        E.add( e5 );
        E.add( e6 );
        E.add( e7 );
        E.add( e8 );

        ve3.connect( E );
        System.out.println( ve3.allIncidentEdges() );
        // [2(0.0, 0.0) -> 3(1.0, 1.0E8), 3(1.0, 1.0E8) -> 2(0.0, 0.0), 2(0.0, 0.0) -> 4(2.0, 1.9E8), 4(2.0, 1.9E8) -> 2(0.0, 0.0), 2(0.0, 0.0) -> 1(0.0, 1.0), 1(0.0, 1.0) -> 2(0.0, 0.0)]
    }

    public static
    void main( String[] args ) {
        testAngle();
    }
}
