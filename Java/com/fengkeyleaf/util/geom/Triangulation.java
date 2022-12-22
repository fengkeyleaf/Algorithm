package com.fengkeyleaf.util.geom;

/*
 * Triangulation.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 6/30/2022$
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * This class consists exclusively of static methods
 * that related to Triangulation.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class Triangulation {

    /**
     * triangulate monotone polygons
     *
     * @return newly partitioned faces (triangles) by adding internal diagonals
     */

    // Reference resource: http://www.cs.uu.nl/geobook/
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
        List<HalfEdge> sortedEdges = monotonePolygon.walkAroundEdge();
        sortedEdges.sort( ( e1, e2 ) -> Vectors.sortByY( e1.origin, e2.origin ) );
        int len = sortedEdges.size();
        // Initialize an empty stack S, and push u1 and u2 onto it.
        Stack<HalfEdge> stack = new Stack<>();
        stack.push( sortedEdges.get( len - 1 ) );
        stack.push( sortedEdges.get( len - 2 ) );

        // for j<-3 to n-1
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
                    faces.add( edge.origin.connect( stack.pop().origin ) );
                }
                stack.pop();

                // Push uj-1 and uj onto S.
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
                    if ( edge.origin.isLeftChainVertex ==
                            Vertex.LEFT_CHAIN_VERTEX )
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
                        faces.add( edge.origin.connect( ( prev = stack.pop() ).origin ) );
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
            faces.add( edge.origin.connect( stack.pop().origin ) );
        }

        return faces;
    }

    /**
     * determine which chain a vertex is on
     *
     */

    private static
    void findLeftAndRightChainVertices( HalfEdge topmost,
                                        HalfEdge bottommost ) {
        HalfEdge edge = topmost;
        // vertices on the left chain, including the topmost
        do {
            edge.origin.isLeftChainVertex =
                    Vertex.LEFT_CHAIN_VERTEX;
            edge = edge.next;
        } while ( edge != bottommost );

        // vertices on the right chain, including the bottommost
        do {
            edge.origin.isLeftChainVertex =
                    Vertex.RIGHT_CHAIN_VERTEX;
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
     *
     * @param monotonePolygons monotone polygons required.
     * @return [ [ triangles for each input monotone polygon ] ]
     */

    public static
    List<List<Face>> triangulate( List<Face> monotonePolygons ) {
        List<List<Face>> triangles = new ArrayList<>();
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
            triangles.add( triangulationMonotonePolygon( monotonePolygon ) );
        }

        return triangles;
    }

    public static
    List<List<Face>> triangulate( Face... faces ) {
        return triangulate( Arrays.asList( faces ) );
    }
}
