package com.fengkeyleaf.util.geom;

/*
 * MonotonePolygons.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 */

import com.fengkeyleaf.lang.MyMath;

import java.util.ArrayList;
import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to Monotone Polygons
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class MonotonePolygons {

    private static
    boolean isCorner( HalfEdge edge ) {
        // topmost
        if ( MyMath.doubleCompare( edge.prev.origin.y, edge.origin.y ) < 0 &&
                MyMath.doubleCompare( edge.origin.y, edge.next.origin.y ) > 0 )
            return true;

        // bottommost
        if ( MyMath.doubleCompare( edge.prev.origin.y, edge.origin.y ) > 0 &&
                MyMath.doubleCompare( edge.origin.y, edge.next.origin.y ) < 0 )
            return true;

        return false;
    }

    /**
     * the passed in polygon is monotone?
     *
     * @deprecated not full tested
     */

    // TODO: 6/30/2022 not full tested
    @Deprecated
    static
    boolean isMonotonePolygon( Face face ) {
        int cornerCount = 2;
        HalfEdge edge = face.outComponent;
        do {
            if ( isCorner( edge ) ) cornerCount--;
            // the # of turn a monotone polygon has
            // is at most two,
            // i.e. they are at topmost and bottommost
            if ( cornerCount < 0 ) return false;
            assert edge.incidentFace == face;
            edge = edge.next;
        } while ( edge != face.outComponent );

        return true;
    }

    private static final float SHIFT = 0.01f;

    /**
     * handle Horizontal Vertices
     * in partitioning a simple polygon into monotone subpolygons
     *
     * @deprecated may have precision issue
     */

    // TODO: 2/23/2022 may have precision issue
    @Deprecated
    private static
    Vertex handleHorizontalVertices( Vertex pointToBeShifted, Vertex base ) {
        Vertex upShiftedPoint = new Vertex(
                pointToBeShifted.x, pointToBeShifted.y + SHIFT );
        Vertex downShiftedPoint = new Vertex(
                pointToBeShifted.x, pointToBeShifted.y - SHIFT );

        // make the horizontal line tilted a bit upwards
        if ( Triangles.toLeftRigorously( base, pointToBeShifted, upShiftedPoint ) ) {
            assert !Triangles.toLeftRigorously( base, pointToBeShifted, downShiftedPoint );
            return upShiftedPoint;
        }

        // make the horizontal line tilted a bit downwards
        assert !Triangles.toLeftRigorously( base, pointToBeShifted, upShiftedPoint );
        assert Triangles.toLeftRigorously( base, pointToBeShifted, downShiftedPoint );
        return downShiftedPoint;
    }

    /**
     * to left test is the key
     * to determine which type of vertex is
     *
     * @deprecated may have precision issue
     */

    @Deprecated
    private static
    void getVertexType( HalfEdge edge ) {
        Vertex base = edge.origin;
        Vertex next = edge.next.origin;
        Vertex prev = edge.prev.origin;

        if ( MyMath.isEqualZero( base.y - next.y ) )
            next = handleHorizontalVertices( next, base );
        else if ( MyMath.isEqualZero( base.y - prev.y ) )
            base = handleHorizontalVertices( base, prev );

        // if its two neighbors lie below it
        if ( base.isBelow( next ) &&
                base.isBelow( prev ) ) {
            base = edge.origin;
            // 	if interior angle at v is less than π:
            if ( !Triangles.toLeft( prev, base, next ) )
                base.vertexType = Vertex.VertexType.SPLIT;
            else
                base.vertexType = Vertex.VertexType.START;

            return;
        }
        // else if two neighbors lie above it
        else if ( base.isAbove( next ) &&
                base.isAbove( prev ) ) {
            base = edge.origin;
            // if less than π:
            if ( !Triangles.toLeft( prev, base, next ) )
                base.vertexType = Vertex.VertexType.MERGE;
            else
                base.vertexType = Vertex.VertexType.END;

            return;
        }

        // regular vertex
        assert Triangles.toLeftRigorously( prev, base, next );
        // left regular vertex,
        // if the interior of P lies to the right of vi
        if ( base.isAbove( prev ) && base.isBelow( next ) ) {
            base = edge.origin;
            base.vertexType = Vertex.VertexType.REGULAR_LEFT;
        }
        // right regular vertex,
        // if the interior of P lies to the left of vi
        else {
            assert base.isAbove( next ) && base.isBelow( prev );
            base = edge.origin;
            base.vertexType = Vertex.VertexType.REGULAR_RIGHT;
        }

        // TODO: 2/23/2022 may have precision issue
        // regular vertex
//        Vector rightShiftedPoint = new Vector( base.x + SHIFT, base.y );
//        Vector leftShiftedPoint = new Vector( base.x - SHIFT, base.y );
//        // left regular vertex,
//        // if the interior of P lies to the right of vi
//        if ( isInsidePolygon( rightShiftedPoint, base, prev, next ) ) {
//            assert !isInsidePolygon( leftShiftedPoint, base, prev, next ) : base;
//            base = edge.origin;
//            base.vertexType = Vertex.VertexType.REGULAR_LEFT;
//        }
//        // right regular vertex,
//        // if the interior of P lies to the left of vi
//        else {
//            assert isInsidePolygon( leftShiftedPoint, base, prev, next ) : leftShiftedPoint + " | " + rightShiftedPoint + " | " + prev + " | " + base + " | " +next;
//            base = edge.origin;
//            base.vertexType = Vertex.VertexType.REGULAR_RIGHT;
//        }
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

    /**
     * get the five Vertex Types:
     * start, end, split, merge, regular(left or right)
     *
     * getVertexTypeEntry() in javascript version
     */

    static
    void getVertexType( Face face ) {
        if ( face == null ) return;

        HalfEdge edge = face.outComponent;
        do {
            getVertexType( edge );
            edge = edge.next;
        } while ( edge != face.outComponent );
    }

    /**
     * handle Regular Vertex
     */

    private static
    void handleRegularVertex( Vertex vertex,
                              StatusRBTree statusRBTree, List<Face> faces ) {
        // if the interior of P lies to the right of vi
        if ( vertex.vertexType == Vertex.VertexType.REGULAR_LEFT ) {
            handleEndVertex( vertex, statusRBTree, faces );
            handleStartVertex( vertex, statusRBTree );
            return;
        }

        // else the interior of P lies to the left of vi
        handleMergeVertexCommonPart( vertex, statusRBTree, faces );
    }

    /**
     * common part of code for handling merge vertex,
     * i.e. we could reuse it for other cases
     */

    private static
    void handleMergeVertexCommonPart( Vertex vertex,
                                      StatusRBTree statusRBTree, List<Face> faces ) {
        // Search in T to find the edge e j directly left of vi.
        EventEdge left = ( EventEdge ) statusRBTree.lowerVal( vertex );
        // if helper(ej) is a merge vertex
        if ( left.vertex.vertexType == Vertex.VertexType.MERGE ) {
            assert left.vertex.incidentEdge.incidentFace == vertex.incidentEdge.incidentFace;
            // 	then Insert the diagonal connecting vi to helper(e j) in D.
            faces.add( left.vertex.connect( vertex ) );
        }

        // helper(e j)<-vi
        left.vertex = vertex;
    }

    /**
     * handle Merge Vertex
     */

    private static
    void handleMergeVertex( Vertex vertex, StatusRBTree statusRBTree,
                            List<Face> faces ) {

        handleEndVertex( vertex, statusRBTree, faces );
        handleMergeVertexCommonPart( vertex, statusRBTree, faces );
    }

    /**
     * handle Split Vertex
     */

    private static
    void handleSplitVertex( Vertex vertex, StatusRBTree statusRBTree,
                            List<Face> faces ) {

        // Search in T to find the edge e j directly left of vi.
        EventEdge left = ( EventEdge ) statusRBTree.lowerVal( vertex );
        assert left != null : vertex;
        // Insert the diagonal connecting vi to helper(ej) in D.
        faces.add( left.vertex.connect( vertex ) );
        // helper(e j)<-vi
        left.vertex = vertex;
        // Insert ei in T and set helper(ei) to vi.
        handleStartVertex( vertex, statusRBTree );
    }

    /**
     * handle End Vertex
     */

    private static
    void handleEndVertex( Vertex vertex, StatusRBTree statusRBTree,
                          List<Face> faces ) {

        // Delete ei-1 from T.
        // vertex must be on the line of ei-1, as the lower endpoint
        EventEdge prevEvent = ( EventEdge ) statusRBTree.deleteAndGetVal( vertex );
        // if helper(ei-1) is a merge vertex
        // prevEvent != null && // orthogonal vertex may have null as prevEvent
        assert prevEvent != null : vertex;
        if ( prevEvent.vertex.vertexType == Vertex.VertexType.MERGE ) {
            assert prevEvent.vertex.incidentEdge.incidentFace == vertex.incidentEdge.incidentFace : prevEvent.vertex + " " + vertex;
            // 	then Insert the diagonal connecting vi to helper(ei-1) in D.
            faces.add( prevEvent.vertex.connect( vertex ) );
        }
    }

    /**
     * handle Start Vertex
     */

    private static
    void handleStartVertex( Vertex vertex, StatusRBTree statusRBTree ) {
        // Insert ei in T and set helper(ei) to vi.
        assert vertex.incidentEdge.origin == vertex;
        Segment s = new Segment( vertex, vertex.incidentEdge.next.origin );
        EventEdge event = new EventEdge( s, vertex, vertex.ID );
        statusRBTree.put( event );
    }

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Input. A simple polygon P stored in a doubly-connected edge list D.
    // Output. A partitioning of P into monotone subpolygons, stored in D.
    static
    List<Face> makeMonotone( List<Vertex> vertices ) {
        List<Face> faces = new ArrayList<>();
        // Construct a priority queue Q on the vertices of P,
        // using their y-coordinates as priority.
        // If two points have the same y-coordinate,
        // the one with smaller x-coordinate has higher priority.
        List<Vertex> priorityQueue = new ArrayList<>( vertices );
        priorityQueue.sort( Vectors::sortByY );

        // Initialize an empty binary search tree T.
        StatusRBTree statusRBTree = new StatusRBTree( Vectors::sortByX );

        // while Q is not empty
        for ( int i = priorityQueue.size() - 1; i >= 0; i-- ) {
            // 	do Remove the vertex vi with the highest priority from Q.
            Vertex vertex = priorityQueue.get( i );
            // 	Call the appropriate procedure to handle the vertex, depending on its type.
            switch ( vertex.vertexType ) {
                case START -> handleStartVertex( vertex, statusRBTree );
                case SPLIT -> handleSplitVertex( vertex, statusRBTree, faces );
                case END -> handleEndVertex( vertex, statusRBTree, faces );
                case MERGE -> handleMergeVertex( vertex, statusRBTree, faces );
                case REGULAR_LEFT, REGULAR_RIGHT -> handleRegularVertex( vertex, statusRBTree, faces );
                default -> { assert false; }
            }
        }

        return faces;
    }

    /**
     * partitioning a simple polygon into monotone subpolygons
     *
     * @param    f A simple polygon P.
     * @return   [ monotone polygons for the input polygon P ]
     */

    // TODO: 7/14/2021 not support complex polygons
    public static
    List<Face> makeMonotone( Face f ) {
        // And also determine Vertex type for each vertex.
        getVertexType( f );
        // partition monotone polygon.
        return makeMonotone( f.walkAroundVertex() );
    }
}
