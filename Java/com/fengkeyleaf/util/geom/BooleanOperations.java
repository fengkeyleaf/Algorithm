package com.fengkeyleaf.util.geom;

/*
 * BooleanOperations.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/3/2022$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Performing the Boolean operations
 * union, intersection, and difference on two polygons P1 and P2.
 *
 * Corollary 2.7
 * Let P1 be a polygon with n1 vertices and P2 a polygon with n2 vertices,
 * and let n := n1+n2. Then P1 ∩ P2, P1 ∪ P2, and P1 \ P2 can each be
 * computed in O(nlogn+klogn) time, where k is the complexity of the output.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// Reference resource: http://www.cs.uu.nl/geobook/
// To perform the Boolean operation we regard the polygons as planar maps
// whose bounded faces are labeled P1 and P2, respectively.
// We compute the overlay of these maps,
// and we extract the faces in the overlay whose
// labels correspond to the particular Boolean operation we want to perform.
// If we want to compute the intersection P1 ∩ P2,
// we extract the faces in the overlay that are labeled with P1 and P2.
// If we want to compute the union P1 ∪ P2,
// we extract the faces in the overlay that are labeled with P1 or P2.
// And if we want to compute the difference P1 \ P2,
// we extract the faces in the overlay that are labeled with P1 and not with P2.

public final class BooleanOperations {

    static final String intersectionStr = "Boolean Operations: intersection";
    static final String unionStr = "Boolean Operations: union";
    static final String differenceStr = "Boolean Operations: difference";

    /**
     * compute the intersection of two subdivisions, P1 ∩ P2.
     *
     * @return [ included faces ]
     */

    public static
    List<Face> intersection( Face s1, Face s2 ) {
        List<Face> F = new ArrayList<>();

        MapOverlay.compute( s1, s2 ).G.forEach( v -> {
            // exclude cycles with no parents.
            if ( v.f.parents == null ) return;

            // extract the faces in the overlay that are labeled with P1 and P2.
            if ( contains( v, s1 ) && contains( v, s2 ) )
                F.add( v.f );
        } );

//        System.out.println( F );
        return Checker.check( s1, s2, F, intersectionStr );
    }

    static
    boolean contains( GraphVertex v, Face s ) {
        assert v.f.parents != null;

        for ( Face p : v.f.parents )
            if ( p.leader == s ) return true;

        return false;
    }

    /**
     * compute the union of two subdivisions, P1 ∪ P2,
     *
     * @return [ included faces ]
     */

    public static
    List<Face> union( Face s1, Face s2 ) {
        List<Face> F = new ArrayList<>();

        MapOverlay.compute( s1, s2 ).G.forEach( v -> {
            if ( v.f.parents == null ) return;

            // extract the faces in the overlay that are labeled with P1 or P2.
            if ( contains( v, s1 ) || contains( v, s2 ) )
                F.add( v.f );
        } );

//        System.out.println( F );
        return Checker.check( s1, s2, F, unionStr );
    }

    /**
     * compute the difference of two subdivisions, P1 \ P2.
     *
     * @return [ included faces ]
     */

    public static
    List<Face> difference( Face s1, Face s2 ) {
        List<Face> F = new ArrayList<>();

        MapOverlay.compute( s1, s2 ).G.forEach( v -> {
            if ( v.f.parents == null ) return;

            // extract the faces in the overlay that are labeled with P1 and not with P2.
            if ( contains( v, s1 ) && !contains( v, s2 ) )
                F.add( v.f );
        } );

//        System.out.println( F );
        return Checker.check( s1, s2, F, differenceStr );
    }

    //----------------------------------------------------------
    // Class Checker
    //----------------------------------------------------------

    static class Checker {
        static
        List<Face> check( Face s1, Face s2,
                          List<Face> F, String title ) {

            assert visualization( s1, s2, F, title );
            return F;
        }

        private static final Color COVER_COLOR = new Color( 221, 221, 221, 160 );

        static
        boolean visualization( Face s1, Face s2,
                               List<Face> F, String title ) {

            MapOverlay.Checker c = new MapOverlay.Checker( s1, s2 );
            DrawingProgram drawer = new DrawingProgram( title, c.size, c.size );

            TreeMap<Integer, Face> t = new TreeMap<>();
            F.forEach( f -> {
                assert !t.containsKey( f.ID );
                t.put( f.ID, f );
            } );

            F.forEach( f -> {
                drawer.fillPoly( COVER_COLOR, f );
                f.innerComponents.forEach( e -> {
                    if ( !t.containsKey( e.twin.incidentFace.ID ) )
                        drawer.fillPoly( Color.white, e.twin.incidentFace );
                } );
            } );
            drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, s1 );
            drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, s2 );

            drawer.initialize();
            return true;
        }
    }
}
