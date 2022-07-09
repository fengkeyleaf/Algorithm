package com.fengkeyleaf.util.geom;

/*
 * Face.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 basic operations on 6/4/2021$
 *     $1.1 boolean operations on 7/2/2022$
 */

import com.fengkeyleaf.util.graph.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Data structure of Face of DCEL
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Face {
    private static int IDStatic = 0;
    public final int ID;
    public HalfEdge outComponent;
    public final List<HalfEdge> innerComponents = new ArrayList<>();
    Face master;
    Face servant;
    // Data field for MapOverlay.
    // parent faces
    List<Face> parents;
    // the Graph G
    Graph<GraphVertex> G;
    // the leader face( infinite face ) containing this face.
    Face leader;

    // TODO: 4/18/2022 those two can be combined to mappingID as well?
    public int IDOfDualVertex;
    boolean isVisited;

    /**
     * constructs to create an instance of Vertex
     * */

    public Face() { ID = IDStatic++; }

    public Face( HalfEdge outComponent ) {
        this.outComponent = outComponent;
        ID = IDStatic++;
    }

    /**
     * copy constructor.
     *
     * @param f face to be copied.
     */

    Face ( Face f ) {
        ID = IDStatic++;

        f.servant = this;
        master = f;

        if ( f.outComponent != null )
            outComponent = f.outComponent.servant == null ? new HalfEdge( f.outComponent ) : f.outComponent.servant;

        f.innerComponents.forEach( e -> innerComponents.add( e.servant == null ? new HalfEdge( e ) : e.servant ) );
    }

    static<T extends Face>
    List<T> reset( List<T> l ) {
        l.forEach( f -> f.isVisited = false );
        return l;
    }

    /**
     * Get all faces contained in the outer face.
     * */

    // TODO: 6/22/2022 something may go wrong with the concept of innerComponents()
    static
    List<Face> getInners( List<Vertex> vertices, Face outer ) {
        outer.isVisited = true;

        List<Face> l = new ArrayList<>();
        vertices.forEach( v -> getInners( v, outer, l ) );

        return reset( l );
    }

    private static
    void getInners( Vertex v, Face outer, List<Face> l ) {
        v.allOutGoingEdges().forEach( e -> {
            if ( e.incidentFace != outer && !e.incidentFace.isVisited ) {
                e.incidentFace.isVisited = true;
                l.add( e.incidentFace );
            }
        } );
    }

    /**
     * get all inner faces contained in this face.
     *
     * @return all inner faces contained in this face.
     */

    public List<Face> getInners() {
        List<Face> faces = new ArrayList<>( innerComponents.size() + 1 );

        innerComponents.forEach( e ->
                e.getInners().forEach( edge -> faces.add( edge.incidentFace ) )
        );

        return faces;
    }

    public static
    void resetIDStatic() {
        IDStatic = 0;
    }

    /**
     * walk around all halfEdges, starting at face
     * and get visited halfEdges
     * */

    public List<HalfEdge> walkAroundEdge() {
        return outComponent.walkAroundEdge();
    }

    /**
     * walk around all halfEdges, starting at face
     * and get visited vertices
     *
     * walkAroundVertexFace() in JavaScript Version
     * */

    public List<Vertex> walkAroundVertex() {
        return outComponent.walkAroundVertex();
    }

    /**
     * reset incidentFace of all half-edges inside the face to it
     * */

    void resetIncidentFace() {
        if ( outComponent == null ) return;

        outComponent.resetIncidentFace( this );
    }

    public void addInnerComponent( HalfEdge halfEdge ) {
        innerComponents.add( halfEdge );
    }

    void addParents( Collection<Face> P ) {
        if ( P.isEmpty() ) return;

        assert parents == null : this + " | " + P;
        parents = new ArrayList<>( P.size() );
        parents.addAll( P );
    }

    /**
     * Is the point inside this convex hull?
     * but excluding the boundary.
     *
     * @param  p point to be tested to see if it's inside the convex hull, {@code c}.
     * @return true, p is inside c; false, not inside.
     */

    public boolean isInsideConvexHull( Vector p ) {
        if ( outComponent == null ) return false;

        HalfEdge e = outComponent;
        do {
            assert e.next != null : e;
            if ( !Triangles.toLeftRigorously( e.origin, e.next.origin, p ) )
                return false;

            assert e.incidentFace == outComponent.incidentFace;
            assert e.next != null : e;
            e = e.next;
        } while ( e != outComponent );

        return true;
    }

    /**
     * Is the point on this convex hull?
     * including the boundary.
     *
     * @param  p point to be tested to see if it's on the convex hull, {@code c}.
     * @return true, p lies on c; false, not on c.
     */

    public boolean isOnConvexHull( Vector p ) {
        if ( outComponent == null ) return false;

        HalfEdge e = outComponent;
        do {
            if ( !Triangles.toLeft( e.origin, e.next.origin, p ) )
                return false;

            assert e.incidentFace == outComponent.incidentFace;
            e = e.next;
        } while ( e != outComponent );

        return true;
    }

    /**
     * is the point inside This Polygon?
     *
     * @deprecated not full test
     * */

    // TODO: 6/30/2022 not full test
    @Deprecated
    public boolean isInsidePolygon( Vector p ) {
        List<List<Face>> T = Triangulation.triangulate( MonotonePolygons.makeMonotone( copy() ) );
        for ( List<Face> F : T )
            for ( Face f : F )
                if ( !f.isInsideConvexHull( p ) )
                    return false;

        return true;
    }

    /**
     * copy this face, but this only copies the outer boundary cycle,
     * also incident to the infinite face. No masters generated.
     *
     * @return copy of this face.
     */

    Face copy() {
        List<Vertex> P = walkAroundVertex();
        List<Vertex> V = new ArrayList<>( P.size() );
        P.forEach( v -> V.add( new Vertex( ( Vector ) v ) ) );

        return Polygons.getDCEL( V )[ 1 ];
    }

    /**
     * is the point On This Polygon?
     * */

    public boolean isOnPolygon( Vector p ) {
        List<List<Face>> T = Triangulation.triangulate( MonotonePolygons.makeMonotone( copy() ) );
        for ( List<Face> F : T )
            for ( Face f : F )
                if ( !f.isOnConvexHull( p ) )
                    return false;

        return true;
    }

    @Override
    public String toString() {
        return outComponent != null ? outComponent.toString() : innerComponents.toString();
    }
}
