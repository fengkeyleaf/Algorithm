package myLibraries.util.geometry;

/*
 * MonotonePolygons.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 * JDK: 16
 */

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.DCEL.*;
import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.EventEdge;
import myLibraries.util.geometry.elements.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class consists exclusively of static methods
 * that related to MonotonePolygons
 *
 * @author Xiaoyu Tongyang, or call me sora for short
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
     */

    // TODO: 7/8/2021 not full tested
    public static
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
     */

    // TODO: 2/23/2022 may have precision issue
    private static
    MonotoneVertex handleHorizontalVertices( Vertex pointToBeShifted, Vertex base ) {
        MonotoneVertex upShiftedPoint = new MonotoneVertex(
                pointToBeShifted.x, pointToBeShifted.y + SHIFT );
        MonotoneVertex downShiftedPoint = new MonotoneVertex(
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
     */

    private static
    void getVertexType( HalfEdge edge ) {
        MonotoneVertex base = ( MonotoneVertex ) edge.origin;
        Vertex next = edge.next.origin;
        Vertex prev = edge.prev.origin;

        if ( MyMath.isEqualZero( base.y - next.y ) )
            next = handleHorizontalVertices( next, base );
        else if ( MyMath.isEqualZero( base.y - prev.y ) )
            base = handleHorizontalVertices( base, prev );

        // if its two neighbors lie below it
        if ( base.isBelow( next ) &&
                base.isBelow( prev ) ) {
            base = ( MonotoneVertex ) edge.origin;
            // 	if interior angle at v is less than π:
            if ( base.isSplitOrMergeVertex() )
                base.vertexType = MonotoneVertex.VertexType.SPLIT;
            else
                base.vertexType = MonotoneVertex.VertexType.START;

            return;
        }
        // else if two neighbors lie above it
        else if ( base.isAbove( next ) &&
                base.isAbove( prev ) ) {
            base = ( MonotoneVertex ) edge.origin;
            // 	if less than π:
            if ( base.isSplitOrMergeVertex() )
                base.vertexType = MonotoneVertex.VertexType.MERGE;
            else
                base.vertexType = MonotoneVertex.VertexType.END;

            return;
        }

        // TODO: 2/23/2022 may have precision issue
        // regular vertex
        Vector rightShiftedPoint = new Vector( base.x + SHIFT, base.y );
        Vector leftShiftedPoint = new Vector( base.x - SHIFT, base.y );
        // left regular vertex,
        // if the interior of P lies to the right of vi
        if ( Polygons.isInsidePolygon( rightShiftedPoint, base, prev, next ) ) {
            assert !Polygons.isInsidePolygon( leftShiftedPoint, base, prev, next ) : base;
            base = ( MonotoneVertex ) edge.origin;
            base.vertexType = MonotoneVertex.VertexType.REGULAR_LEFT;
        }
        // right regular vertex,
        // if the interior of P lies to the left of vi
        else {
            assert Polygons.isInsidePolygon( leftShiftedPoint, base, prev, next ) : base;
            base = ( MonotoneVertex ) edge.origin;
            base.vertexType = MonotoneVertex.VertexType.REGULAR_RIGHT;
        }
    }

    /**
     * get the five Vertex Types:
     * start, end, split, merge, regular(left or right)
     *
     * getVertexTypeEntry() in javascript version
     */

    public static
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
    void handleRegularVertex( MonotoneVertex vertex,
                              StatusRBTree statusRBTree, List<Face> faces ) {
        // if the interior of P lies to the right of vi
        if ( vertex.vertexType == MonotoneVertex.VertexType.REGULAR_LEFT ) {
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
    void handleMergeVertexCommonPart( MonotoneVertex vertex,
                                      StatusRBTree statusRBTree, List<Face> faces ) {
        // Search in T to find the edge e j directly left of vi.
        EventEdge left = ( EventEdge ) statusRBTree.lowerVal( vertex );
        // if helper(ej) is a merge vertex
        if ( left.vertex.vertexType == MonotoneVertex.VertexType.MERGE ) {
            assert left.vertex.incidentEdge.incidentFace == vertex.incidentEdge.incidentFace;
            // 	then Insert the diagonal connecting vi to helper(e j) in D.
            HalfEdges.connectHelper( left.vertex, vertex, faces );
        }

        // helper(e j)<-vi
        left.vertex = vertex;
    }

    /**
     * handle Merge Vertex
     */

    private static
    void handleMergeVertex( MonotoneVertex vertex,
                            StatusRBTree statusRBTree, List<Face> faces ) {
        handleEndVertex( vertex, statusRBTree, faces );
        handleMergeVertexCommonPart( vertex, statusRBTree, faces );
    }

    /**
     * handle Split Vertex
     */

    private static
    void handleSplitVertex( MonotoneVertex vertex,
                            StatusRBTree statusRBTree, List<Face> faces ) {
        // Search in T to find the edge e j directly left of vi.
        EventEdge left = ( EventEdge ) statusRBTree.lowerVal( vertex );
        assert left != null : vertex;
        // Insert the diagonal connecting vi to helper(ej) in D.
        HalfEdges.connectHelper( left.vertex, vertex, faces );
        // helper(e j)<-vi
        left.vertex = vertex;
        // Insert ei in T and set helper(ei) to vi.
        handleStartVertex( vertex, statusRBTree );
    }

    /**
     * handle End Vertex
     */

    private static
    void handleEndVertex( MonotoneVertex vertex,
                          StatusRBTree statusRBTree, List<Face> faces ) {
        // Delete ei−1 from T.
        // vertex must be on the line of ei-1, as the lower endpoint
        EventEdge prevEvent = ( EventEdge ) statusRBTree.deleteAndGetVal( vertex );
        // if helper(ei−1) is a merge vertex
        // prevEvent != null && // orthogonal vertex may have null as prevEvent
        if ( prevEvent.vertex.vertexType == MonotoneVertex.VertexType.MERGE ) {
            assert prevEvent.vertex.incidentEdge.incidentFace == vertex.incidentEdge.incidentFace : prevEvent.vertex + " " + vertex;
            // 	then Insert the diagonal connecting vi to helper(ei−1) in D.
            HalfEdges.connectHelper( prevEvent.vertex, vertex, faces );
        }
    }

    /**
     * handle Start Vertex
     */

    private static
    void handleStartVertex( MonotoneVertex vertex, StatusRBTree statusRBTree ) {
        // Insert ei in T and set helper(ei) to vi.
        assert vertex.incidentEdge.origin == vertex;
        Line line = new Line( vertex, vertex.incidentEdge.next.origin );
        EventEdge event = new EventEdge( line, vertex, vertex.ID );
        statusRBTree.put( event );
    }

    /**
     * partitioning a simple polygon into monotone subpolygons
     *
     * Reference resource:
     * @see <a href=http://www.cs.uu.nl/geobook/>Computational Geometry: Algorithms and Applications(Third Edition)</a>
     *
     * @return newly partitioned faces (Monotone polygons) by adding internal diagonals
     */

    // TODO: 7/14/2021 not support complex polygons
    // Input. A simple polygon P stored in a doubly-connected edge list D.
    // Output. A partitioning of P into monotone subpolygons, stored in D.
    public static
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
            MonotoneVertex vertex = ( MonotoneVertex ) priorityQueue.get( i );
            // 	Call the appropriate procedure to handle the vertex, depending on its type.
            switch ( vertex.vertexType ) {
                case START:
                    handleStartVertex( vertex, statusRBTree );
                    break;
                case SPLIT:
                    handleSplitVertex( vertex, statusRBTree, faces );
                    break;
                case END:
                    handleEndVertex( vertex, statusRBTree, faces );
                    break;
                case MERGE:
                    handleMergeVertex( vertex, statusRBTree, faces );
                    break;
                case REGULAR_LEFT:
                case REGULAR_RIGHT:
                    handleRegularVertex( vertex, statusRBTree, faces );
                    break;
                default:
                    assert false;
            }
        }

        return faces;
    }

    /**
     * triangulating monotone polygons
     *
     * Reference resource:
     * @see <a href=http://www.cs.uu.nl/geobook/>portal gate</a>
     *
     * @return newly partitioned faces (triangles) by adding internal diagonals
     */

    // Input. A strictly y-monotone polygon P stored in a doubly-connected edge list D.
    // Output. A triangulation of P stored in the doubly-connected edge list D.
    private static
    List<Face> triangulationMonotonePolygon( Face monotonePolygon ) {
        final List<Face> faces = new ArrayList<>();
        if ( monotonePolygon == null ) return faces;

        // Merge the vertices on the left chain and the vertices on the right chain of P
        // into one sequence, sorted on decreasing y-coordinate. If two vertices have
        // the same y-coordinate, then the leftmost one comes first. Let u1......un
        // denote the sorted sequence.
        List<HalfEdge> sortedEdges = DCEL.walkAroundEdge( monotonePolygon );
        sortedEdges.sort( HalfEdges::sort );
        int len = sortedEdges.size();
        // Initialize an empty stack S, and push u1 and u2 onto it.
        Stack<HalfEdge> stack = new Stack<>();
        stack.push( sortedEdges.get( len - 1 ) );
        stack.push( sortedEdges.get( len - 2 ) );

        // for j<-3 to n−1
        HalfEdge edge = null;
        for ( int i = len - 3; i > 0; i-- ) {
            edge = sortedEdges.get( i );
            // 	do if uj and the vertex on top of S are on different chains
            if ( edge.isOnTheDifferentChain( stack.peek() ) ) {
                // then Pop all vertices from S.
                // Insert into D a diagonal from uj to each popped vertex,
                // except the last one.
                HalfEdge prev = stack.peek();
                while ( stack.size() > 1 ) {
                    HalfEdges.connectHelper( edge.origin, stack.pop().origin, faces );
                }
                stack.pop();

                // Push uj−1 and uj onto S.
                stack.push( prev );
                stack.push( edge );
            } else {
                // else Pop one vertex from S.
                HalfEdge prev = stack.pop();

                // Pop the other vertices from S as long as the diagonals from
                // uj to them are inside P. Insert these diagonals into D.
                while ( !stack.isEmpty() ) {
                    boolean isInside = false;
                    // if uj is on the left chain,
                    // the counter-clock-wise ordering of vertices is
                    // from stack.peek() to prev, to uj.
                    if ( ( ( MonotoneVertex ) edge.origin ).isLeftChainVertex ==
                            MonotoneVertex.LEFT_CHAIN_VERTEX )
                        isInside = Triangles.toLeftRigorously( stack.peek().origin,
                                prev.origin, edge.origin );
                    // if uj is on the right chain,
                    // the counter-clock-wise ordering of vertices is
                    // from uj to prev, to stack.peek().
                    else
                        isInside = Triangles.toLeftRigorously( edge.origin,
                                prev.origin, stack.peek().origin );

                    // the diagonal to be added is inside the polygon?
                    if ( isInside )
                        // yes, add it to D.
                        HalfEdges.connectHelper( edge.origin,
                                ( prev = stack.pop() ).origin, faces );
                    // no, check the next vertex
                    else break;
                }
                // Push the last vertex that has been popped back onto S.
                stack.push( prev );
                // Push uj onto S.
                stack.push( edge );
            }
        }

        // Add diagonals from un to all stack vertices
        // except the first and the last one.
        edge = sortedEdges.get( 0 );
        stack.pop();
        while ( stack.size() > 1 ) {
            HalfEdges.connectHelper( edge.origin,
                    stack.pop().origin, faces );
        }

        return faces;
    }

    /**
     * determine which chain a vertex is on
     *
     * findLeftAndRightChainVerticesChain() in javascript version
     */

    private static
    void findLeftAndRightChainVertices( HalfEdge topmost,
                                        HalfEdge bottommost ) {
        HalfEdge edge = topmost;
        // vertices on the left chain, including the topmost
        do {
            ( ( MonotoneVertex ) edge.origin ).isLeftChainVertex =
                    MonotoneVertex.LEFT_CHAIN_VERTEX;
            edge = edge.next;
        } while ( edge != bottommost );

        // vertices on the right chain, including the bottommost
        do {
            ( ( MonotoneVertex ) edge.origin ).isLeftChainVertex =
                    MonotoneVertex.RIGHT_CHAIN_VERTEX;
            edge = edge.next;
        } while ( edge != topmost );
    }

    /**
     * determine which chain a vertex is on,
     * first we need to find the topmost and bottommost
     *
     * topMost -> L; bottom -> R
     */

    private static
    void findLeftAndRightChainVertices( Face monotonePolygon ) {
        HalfEdge topmost = null;
        HalfEdge bottommost = null;
        double maxY = Integer.MIN_VALUE;
        double minY = Integer.MAX_VALUE;

        assert monotonePolygon.innerComponents.isEmpty();
        HalfEdge edge = monotonePolygon.outComponent;

        // find the topmost and bottommost,
        // by visiting all vertices of the polygon
        do {
            Vertex vertex = edge.origin;
            if ( vertex.y > maxY ) {
                maxY = vertex.y;
                topmost = edge;
            }

            if ( vertex.y < minY ) {
                minY = vertex.y;
                bottommost = edge;
            }

            edge = edge.next;
        } while ( edge != monotonePolygon.outComponent );

        assert topmost != null;
        assert bottommost != null;
        assert topmost != bottommost;
        findLeftAndRightChainVertices( topmost, bottommost );
    }

    /**
     * triangulate each monotone polygon
     */

    public static
    List<Face> preprocessMonotonePolygon( List<Face> monotonePolygons ) {
        List<Face> triangles = new ArrayList<>();
        if ( monotonePolygons == null ||
                monotonePolygons.isEmpty() ) return triangles;

        // for each face, also a monotone polygon,
        // walk around to get all its vertices
        for ( Face monotonePolygon : monotonePolygons ) {
            // skip the infinite face
            if ( !monotonePolygon.innerComponents.isEmpty() ) continue;
            // and determine on which chain it is
            findLeftAndRightChainVertices( monotonePolygon );
            // and triangulate the polygon
            triangles.addAll( triangulationMonotonePolygon( monotonePolygon ) );
        }

        return triangles;
    }
}
