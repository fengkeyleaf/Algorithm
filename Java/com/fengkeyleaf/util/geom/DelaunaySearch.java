package com.fengkeyleaf.util.geom;

/*
 * DelaunaySearch.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 4/3/2022$
 */

import com.fengkeyleaf.util.graph.DAG;
import com.fengkeyleaf.util.graph.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Point location structure D for Delaunay triangulation.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

class DelaunaySearch extends DAG<DelaunayVertex> {
    // triangle p0P-1P-2
    final DelaunayVertex root;
    // infinite face containing triangle p0P-1P02
    final Face outer;

    DelaunaySearch( Face[] faces ) {
        outer = faces[ 0 ];
        root = new DelaunayVertex( faces[ 1 ] );
    }

    /**
     * Find a triangle piPjPk ∈ T containing pr.
     * */

    DelaunayVertex get( Vector pr ) {
        if ( pr == null ) return null;

        return get( root, pr );
    }

    DelaunayVertex get( DelaunayVertex root, Vector pr ) {
        // base case 1, where this triangle doesn't contain pr.
        // look into its brothers or parent.
        if ( root.contains( pr ) == null ) return null;

        // recursive process.
        // this triangle contains pr,
        // but need to look into its children, if it has.
        for ( Vertex c : root.children ) {
            DelaunayVertex v = ( DelaunayVertex ) c;
            if ( ( v = get( v, pr ) ) != null ) return v;
        }

        // otherwise, pr only lies on this triangle.
        return root;
    }

    /**
     * get all real( non-conceptual ) triangles from this point location structure D.
     *
     * Deprecated: too slow to traverse all leaf nodes in practice.
     * */

    @Deprecated
    List<Face> getTriangles( boolean isAll ) {
        List<Face> triangles = new ArrayList<>();
        getTriangles( root, triangles, isAll );
        return triangles;
    }

    private void getTriangles( DelaunayVertex r,
                               List<Face> triangles, boolean isAll ) {

        // base case
        if ( isAdded( r, isAll ) ) {
            r.isInside = true;
            assert r.triangle != null : r;
            triangles.add( r.triangle );
            return;
        }

        // recursive process
        r.children.forEach( c -> getTriangles( ( DelaunayVertex ) c, triangles, isAll ) );
    }

    /**
     * should add this triangle?
     * */

    private boolean isAdded( DelaunayVertex r, boolean isAll ) {
        // not visited before and leaf node,
        return !r.isInside && r.children.isEmpty() &&
                // and non-conceptual triangle.
                ( isAll || isReal( r ) );
    }

    /**
     * is this triangle not conceptual?
     * */

    private boolean isReal( DelaunayVertex r ) {
        // fixed before, use vertex array to do the check.
        if ( r.triangle == null ) return isReal( r.vertices );

        // no, use face to do so.
        assert r.triangle.outComponent != null : r;
        List<HalfEdge> edges = r.triangle.walkAroundEdge();
        assert edges.size() == 3;
        for ( HalfEdge e : edges )
            if ( e.origin.mappingID < 0 ) return false;

        return true;
    }

    private boolean isReal( List<Vector> vertices ) {
        for ( int i = 0; i < 3; i++ )
            if ( vertices.get( i ).mappingID < 0 ) return false;

        return true;
    }

    /**
     * reset all nodes to the status of unvisited.
     *
     * Deprecated: too slow to traverse all leaf nodes in practice.
     * */

    @Deprecated
    boolean reset() {
        reset( root );
        return true;
    }

    private void reset( DelaunayVertex r ) {
        r.isInside = false;

        r.children.forEach( c -> reset( ( DelaunayVertex ) c ) );
    }

    /**
     * fix all leaf nodes with all conceptual triangles.
     *
     * Deprecated: too slow to traverse all leaf nodes in practice.
     * */

    @Deprecated
    void fix() {
        fix( root );
        reset();
    }

    private void fix( DelaunayVertex r ) {
        // base case
        // not visited before and leaf node,
        if ( !r.isInside && r.children.isEmpty() &&
                // and conceptual triangle.
                !isReal( r ) ) {
            r.fix();
            r.isInside = true;
        }

        // recursive process.
        r.children.forEach( c -> fix( ( DelaunayVertex ) c ) );
    }
}
