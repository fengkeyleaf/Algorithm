package myLibraries.util.geometry.tools;

/*
 * Polygons.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.DCEL.HalfEdge;
import myLibraries.util.geometry.DCEL.Vertex;
import myLibraries.util.geometry.elements.point.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to Polygon
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Polygons {

    /**
     * remove Points On The Same Line
     */

    public static
    List<Vertex> removePointsOnTheSameLine( List<Vertex> vertices ) {
        List<Vertex> filtered = new ArrayList<>();
        filtered.add( vertices.get( 0 ) );

        // for i-th vertex, check its predecessor and successor
        // to see if they are on the same line.
        // the reason why we can do this is that
        // the medium point is the one that is redundant,
        // if three consecutive points are on the same line.
        for ( int i = 1; i < vertices.size() - 1; i++ ) {
            double res = Triangles.areaTwo( vertices.get( i - 1 ), vertices.get( i ), vertices.get( i + 1 ) );
            // if yes, ignore this vertex;
            // if no, keep this vertex
            if ( !MyMath.isEqualZero( res ) )
                filtered.add( vertices.get( i ) );
        }

        filtered.add( vertices.get( vertices.size() - 1 ) );
        return filtered;
    }

    /**
     * the given point lies "inside" which face?
     * */

    public static
    Face inWhichFace( List<Face> polygons, Vector point ) {
        for ( Face polygon : polygons ) {
            if ( isInsideThisPolygon( polygon, point ) )
                return polygon;
        }

        return null;
    }

    /**
     * the given point lies "on" which face?
     * */

    public static
    Face OnWhichFace( List<Face> polygons, Vector point ) {
        for ( Face polygon : polygons ) {
            if ( isOnThisPolygon( polygon, point ) )
                return polygon;
        }

        return null;
    }

    /**
     * is the point inside This Polygon?
     * */

    public static
    boolean isInsideThisPolygon( Face polygon, Vector point ) {
        if ( polygon.outComponent == null ) return false;

        HalfEdge edge = polygon.outComponent;
        do {
            assert edge.next != null : edge;
            if ( !Triangles.toLeftRigorously( edge.origin, edge.next.origin, point ) )
                return false;

            assert edge.incidentFace == polygon.outComponent.incidentFace;
            assert edge.next != null : edge;
            edge = edge.next;
        } while ( edge != polygon.outComponent );

        return true;
    }

    /**
     * is the point On This Polygon?
     * */

    public static
    boolean isOnThisPolygon( Face polygon, Vector point ) {
        if ( polygon.outComponent == null ) return false;

        HalfEdge edge = polygon.outComponent;
        do {
            if ( !Triangles.toLeft( edge.origin, edge.next.origin, point ) )
                return false;

            assert edge.incidentFace == polygon.outComponent.incidentFace;
            edge = edge.next;
        } while ( edge != polygon.outComponent );

        return true;
    }

    /**
     * get the DCEL for the polygon
     * representing by counter-clock-wise vertices
     *
     * @return  [faceOutside(infinite face), faceInner][
     * */

    // TODO: 7/14/2021 not support complex polygons
    public static
    Face[] getDCEL( List<Vertex> vertices ) {
        if ( vertices == null || vertices.size() < 3 ) return null;
        int len = vertices.size();

        // two faces, one inside the polygon, the other outside of it.
        Face faceInner = new Face();
        Face faceOutside = new Face();
        // two edge list, one going counter-clock wise, the other going the opposite.
        final List<HalfEdge> counterClockWiseEdges = new ArrayList<>();
        final List<HalfEdge> clockWiseEdges = new ArrayList<>();

        // from the first vertex to the last one, i-th vertex:
        for ( int i = 0; i < len; i++ ) {
            // create twin edges
            HalfEdge edge1 = new HalfEdge( vertices.get( i ), faceInner );
            vertices.get( i ).incidentEdge = edge1;
            counterClockWiseEdges.add( edge1 );
            HalfEdge edge2 = new HalfEdge( vertices.get( ( i + 1 ) % len ), faceOutside );
            clockWiseEdges.add( edge2 );
            HalfEdge.setTwins( edge1, edge2 );

            // add next and prev for the two halfEdges, i and i - 1, separately.
            if ( i > 0 ) {
                HalfEdge.connect( counterClockWiseEdges.get( i ),
                        counterClockWiseEdges.get( i - 1 ) );
                HalfEdge.connect( clockWiseEdges.get( i - 1 ),
                        clockWiseEdges.get( i ) );
            }
        }

        // add next and prev for the first and last halfEdges.
        HalfEdge.connect( counterClockWiseEdges.get( 0 ),
                counterClockWiseEdges.get( counterClockWiseEdges.size() - 1 ) );
        HalfEdge.connect( clockWiseEdges.get( clockWiseEdges.size() - 1 ),
                clockWiseEdges.get( 0 ) );
        // set outComponent and innerComponent.
        faceInner.outComponent = counterClockWiseEdges.get( 0 );
        faceOutside.innerComponents.add( clockWiseEdges.get( 0 ) );

        // And also determine Vertex type for each vertex.
        MonotonePolygons.getVertexType( faceInner );

        // faceOutside = infinite face
        return new Face[] { faceOutside, faceInner };
    }

    /**
     * is the point Inside Polygon?
     * only used for partitioning monotone polygons
     *
     * @param base    the vertex of the polygon
     * @param prev    the previous one of the base
     * @param next    the next one of the base
     * */

    static
    boolean isInsidePolygon( Vector point, Vector base,
                             Vector prev, Vector next ) {
        return Triangles.toLeftRigorously( prev, base, point ) &&
                Triangles.toLeftRigorously( base, next, point );
    }
}
