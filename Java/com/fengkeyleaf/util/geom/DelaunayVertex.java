package com.fengkeyleaf.util.geom;

/*
 * DelaunayVertex.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 4/3/2022$
 */

import com.fengkeyleaf.util.MyCollections;
import com.fengkeyleaf.util.graph.Vertex;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * vertex structure of Point location structure D for Delaunay triangulation.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

class DelaunayVertex extends Vertex {
    private static int IDStatic = 0;
    boolean isInside;
    Face triangle;
    HalfEdge edge;
    final List<Vertex> children = neighbours;
    List<Vector> vertices;

    // fix counter.
    private int count;

    /**
     * constructs to create an instance of DelaunayVertex
     * */

    DelaunayVertex( Face f ) {
        // a triangle can be split into 2 ~ 3 sub-triangles.
        super( IDStatic++, 3 );

        // Delaunay Vertex <=> face of triangle.
        triangle = f;
        ( ( DelaunayFace ) f ).vertex = this;
    }

    /**
     * fix the status of this vertex, mainly the triangle.
     * */

    DelaunayVertex fix() {
        // every triangle to be fixed only once.
        // one for adding new points or flip edge,
        assert ++count < 2 : count + " | " + this;

        // fix the status of the current triangle that this vertex holds.
        vertices = new ArrayList<>( 4 );
        vertices.addAll( triangle.walkAroundVertex() );
        vertices.add( vertices.get( 0 ) );
        assert vertices.size() == 4;
        triangle = null;

        return this;
    }

    /**
     * add a new triangle( as a child node ) to this vertex.
     * */

    DelaunayVertex add( Face f ) {
        assert f != null;
        DelaunayVertex v = new DelaunayVertex( f );
        children.add( v );
        return v;
    }

    /**
     * triangle of this vertex containing pr?
     * */

    DelaunayVertex contains( Vector pr ) {
        // impossible to query p0, p-1 and p-2.
        assert pr.mappingID > 0;

        // including inside and lying on the circumcircle.
        if ( isOnTriangle( pr ) ) {
            edge = onEdge( pr );
            isInside = edge == null;
            return this;
        }

        return null;
    }

    /**
     * Does pr lie inside this triangle or on the circumcircle of it?
     * */

    private boolean isOnTriangle( Vector pr ) {
        assert children.isEmpty() || count == 1;
        // fixed before, use vertex array to do the check.
        if ( triangle == null ) return isOnTriangleList( pr );

        // no, use face to do so.
        assert triangle.outComponent != null;
        HalfEdge e = triangle.outComponent;
        do {
            assert e.next != null : e;
            // TODO: 4/5/2022 precession issue?
            if ( areaTwo( e.origin, e.next.origin, pr ) < 0 )
                return false;

            assert e.incidentFace == triangle.outComponent.incidentFace;
            assert e.next != null : e;
            e = e.next;
        } while ( e != triangle.outComponent );

        return true;
    }

    private boolean isOnTriangleList( Vector pr ) {
        for ( int i = 0; i < 3; i++ )
            if ( areaTwo( vertices.get( i ), vertices.get( i + 1 ), pr ) < 0 )
                return false;

        return true;
    }

    /**
     * special areaTwo for Delaunay, i.e. include p-1 and p-2.
     * */

    double areaTwo( Vector pi, Vector pj, Vector pr ) {
        // piPj is p-1P-2, pr is always on the left of it.
        if ( Math.max( pi.mappingID, pj.mappingID ) < 0 )
            return 1;
        // all non-negative indices, use normal areaTwo.
        if ( Math.min( pi.mappingID, pj.mappingID ) >= 0 )
            return Triangles.areaTwo( pi, pj, pr );

        // find which point, p, has negative index.
        // sorting costs O(1).
        List<Vector> points = MyCollections.sort( Comparator.comparingInt( v -> v.mappingID ), pi, pj );
        Vector p = points.get( 0 );
        pj = points.get( 1 );
        assert pj.mappingID >= 0;

        // with p-1, pj -> p-1: pr > pj.
        // p-1 -> pj, pj > pr.
        if ( p.mappingID == -1 )
            return p == pi ? Vectors.sortByY( pj, pr ) : Vectors.sortByY( pr, pj );

        // with p-2, p-2 -> pj: pr > pj.
        // pj -> p-2: pj > pr
        assert p.mappingID == -2 : points + " | " + pr;
        return p == pi ? Vectors.sortByY( pr, pj ) : Vectors.sortByY( pj, pr );
    }

    /**
     * Does pr lie on the circumcircle of the triangle, or to say, on an edge?
     * */

    private HalfEdge onEdge( Vector pr ) {
        if ( triangle == null ) return null;

        assert triangle.outComponent != null;
        assert vertices == null;

        HalfEdge e = triangle.outComponent;
        do {
            assert e.next != null : e;
            // TODO: 4/5/2022 precession issue?
            if ( areaTwo( e.origin, e.next.origin, pr ) == 0 )
                return e;

            assert e.incidentFace == triangle.outComponent.incidentFace;
            assert e.next != null : e;
            e = e.next;
        } while ( e != triangle.outComponent );

        return null;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder( "Tri: [ " );
        if ( triangle == null ) {
            for ( int i = 0; i < 3; i++ )
                text.append( vertices.get( i ).mappingID ).append( ", " );
        }
        else {
            assert vertices == null : vertices;
            triangle.walkAroundEdge().forEach( e -> text.append( e.origin.mappingID ).append( ", " ) );
        }
        return text.append( " ]" ).toString();
    }
}
