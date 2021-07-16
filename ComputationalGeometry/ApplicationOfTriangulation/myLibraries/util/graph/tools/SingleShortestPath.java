package myLibraries.util.graph.tools;

/*
 * SingleShortestPath.java
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 added Bellman Ford and Dijkstra's on 4/20/2021$
 *     $1.0 added BFS and funnel on 7/14/2021$
 */

import myLibraries.util.geometry.DCEL.HalfEdge;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.Triangles;
import myLibraries.util.graph.Graph;
import myLibraries.util.graph.elements.*;

import java.util.*;

/**
 * Algorithms related to
 * single-resource shortest path problems
 *
 * @author       Xiaoyu Tongyang
 */

public final class SingleShortestPath {
    private static final boolean LEFT_POINT = true;
    private static final boolean RIGHT_POINT = false;

    private static
    void addPoint( List<Vector> points, List<Boolean> leftOrRight,
                   int ID, HalfEdge edge, boolean LEFT_POINT ) {
        edge.origin.mappingID = ID;
        points.add( edge.origin );
        leftOrRight.add( LEFT_POINT );
    }

    private static
    void addPoint( List<Vector> points, List<Boolean> leftOrRight,
                   int ID, Vector point, boolean LEFT_POINT ) {
        point.mappingID = ID;
        points.add( point );
        leftOrRight.add( LEFT_POINT );
    }

    /**
     * go through portal edges' points
     */

    private static
    int getLeftAndRightPoints( DualVertex endTriangle, List<Vector> points,
                               List<Boolean> leftOrRight, int ID ) {
        // identify the first left and right point
        HalfEdge current = endTriangle.shortestNeighbourEdge;
        addPoint( points, leftOrRight, ID++, current, LEFT_POINT );
        addPoint( points, leftOrRight, ID++, current.twin, RIGHT_POINT );

        endTriangle = ( DualVertex ) endTriangle.parent;

        // step through the shortest path formed by triangles
        while ( endTriangle.shortestNeighbourEdge != null ) {
            current = endTriangle.shortestNeighbourEdge;

            // first see this vertex
            // the face that is is a left or right point is
            // based on the opposite point connected by the portal edge
            // i.e. reverse the direction of the opposite point
            if ( current.origin.mappingID == -1 ) {
                assert current.twin.origin.mappingID > -1;
                addPoint( points, leftOrRight, ID++, current, !leftOrRight.get( current.twin.origin.mappingID ) );
            }
            else {
                // this is a special case,
                // where several portal edges have one common vertex
                assert current.origin.mappingID > -1;
                addPoint( points, leftOrRight, ID++, current.twin, !leftOrRight.get( current.origin.mappingID ) );
            }

            endTriangle = ( DualVertex ) endTriangle.parent;
        }

        return ID;
    }

    /**
     * get Left And Right Points for funnel algorithm
     */

    // funnel algorithm
    private static
    List<Boolean> getLeftAndRightPoints( DualVertex endTriangle, Vector startPoint,
                                         Vector endPoint, List<Vector> points ) {
        List<Boolean> leftOrRight = new ArrayList<>();
        if ( endTriangle == null ) return leftOrRight;

        int ID = 0;
        // add start point
        addPoint( points, leftOrRight, ID++, startPoint, LEFT_POINT );
        addPoint( points, leftOrRight, ID++, startPoint, RIGHT_POINT );

        if ( endTriangle.shortestNeighbourEdge != null )
            ID = getLeftAndRightPoints( endTriangle, points, leftOrRight, ID );

        // add end point
        addPoint( points, leftOrRight, ID++, endPoint, LEFT_POINT );
        addPoint( points, leftOrRight, ID++, endPoint, RIGHT_POINT );

        return leftOrRight;
    }

    /**
     * add a distinct corner to the path
     */

    private static
    void addCorner( List<Vector> visitedVertices, Vector apex ) {
        if ( !visitedVertices.get( visitedVertices.size() - 1 ).equals( apex ) )
            visitedVertices.add( apex );
    }

    /**
     * the left or right point is the apex itself?
     */

    private static
    boolean isEqualToApex( Vector apex, Vector left, Vector right ) {
        return apex.equals( left ) || apex.equals( right );
    }

    /**
     * Funnel Algorithm
     *
     * Reference resource:
     * http://digestingduck.blogspot.com/2010/03/simple-stupid-funnel-algorithm.html
     */

    // TODO: 7/14/2021 not support complex polygons
    public static
    List<Vector> Funnel( DualVertex startTriangle,
                         Vector startPoint, Vector endPoint ) {

        List<Vector> visitedVertices = new LinkedList<>();
        if ( startTriangle == null ) return visitedVertices;

        // add start point
        visitedVertices.add( startPoint );
        
        // get "left" and "right" points,
        // presented as a boolean array
        // from the shortest path in a dual graph
        List<Vector> points = new ArrayList<>();
        List<Boolean> leftOrRight = getLeftAndRightPoints( startTriangle, startPoint, endPoint, points );

        assert points.size() == leftOrRight.size();
        // initialize the first funnel,
        // with the apex and two points
        // associated with the internal diagonal of endTriangle
        int left = 0;
        int right = 1;
        Vector apex = startPoint;
        // while endTriangle is not null,
        // i.e. we have internal diagonals to step through
        for ( int i = 2; i < points.size(); i++ ) {
            int mappingID = i == points.size() - 2 ?
                    points.get( i ).mappingID - 1 : points.get( i ).mappingID;
            // do if the new funnel, to say,
            // the one formed with the apex and the current internal diagonal,
            // is smaller than or equal to the previous one,
            // and move it to the current diagonal.

            // more precisely speaking,
            // if current point is left point,
            // and it is on the right side of left boundary of the funnel,
            // left point ---> starts
            if ( leftOrRight.get( mappingID ) &&
                    Triangles.areaTwo( apex, points.get( left ), points.get( i ) ) <= 0 ) {
                // as well as on the left side of the right boundary,
                // or on the left boundary
                // then set left boundary of the funnel to this point
                if ( apex.equals( points.get( right ) ) ||
                        Triangles.areaTwo( apex, points.get( right ), points.get( i ) ) > 0 ) {
                    left = i;
                }
                // else if current point is "left" point,
                // and it is on the right side of left boundary of the funnel,
                // but on the right side of the right boundary,
                // meaning the left boundary crossing the right boundary,
                // add the apex to the list,
                // and then set the apex to the left point
                else {
                    visitedVertices.add( apex = points.get( right ) );
                    i = left = right;
                }
            }
            // left point ---> ends

            // similar steps when current point is "right" point.
            // but in this case,
            // we flip directions for the following steps.
            // right point ---> starts
            if ( !leftOrRight.get( mappingID ) &&
                    Triangles.areaTwo( apex, points.get( right ), points.get( i ) ) >= 0 ) {

                if ( apex.equals( points.get( left ) ) ||
                        Triangles.areaTwo( apex, points.get( left ), points.get( i ) ) < 0 ) {
                    right = i;
                }
                else {
                    visitedVertices.add( apex = points.get( left ) );
                    i = right = left;
                }
            }
            // right point ---> ends
        }

        // add end point
        addCorner( visitedVertices, endPoint );

        // reset mapping ID to -1
        Node.resetMappingID( points );

        // return the corners we've gong though,
        // including the start point,
        // but not the end point.
//        System.out.println( visitedVertices );
        return visitedVertices;
    }

    /**
     * shortest path in a dual graph
     * with the use of BFS
     */

    public static
    void BFS( int sizeOfGraph, DualVertex start, DualVertex end ) {
        if ( sizeOfGraph <= 0 || start == null || end == null ) return;

        start.parent = null;
        start.shortestNeighbourEdge = null;

        // initialize a queue
        // and enqueue the start vertex
        LinkedList<DualVertex> queue = new LinkedList<>();
        queue.addLast( start );
        // and a boolean array to indicate
        // whether a vertex has been explored before
        boolean[] isVisited = new boolean[ sizeOfGraph ];
        isVisited[ start.ID ] = true;

        int count = 1;
        // while the queue is not empty
        while ( !queue.isEmpty() ) {
            int countTemp = 0;
            for ( int i = 0; i < count; i++ ) {
                // do, of the vertices already in the queue,
                // enqueue all their neighbours that haven't benn visited,
                // set their parent node to the dequeued vertex,
                // and mark them as visited,
                // and count the number of newly added vertices
                DualVertex current = queue.removeFirst();
                for ( int j = 0; j < current.neighbours.size(); j++ ) {
                    DualVertex neighbour =  ( DualVertex ) current.neighbours.get( j );
                    if ( isVisited[ neighbour.ID ] ) continue;
                    isVisited[ neighbour.ID ] = true;

                    neighbour.parent = current;
                    neighbour.shortestNeighbourEdge = current.neighbourEdges.get( j );
                    assert neighbour.shortestNeighbourEdge.origin.mappingID == -1;
                    assert neighbour.shortestNeighbourEdge.twin.origin.mappingID == -1;
                    queue.addLast( neighbour );
                    countTemp++;

                    // if the vertex to be enqueued is the end vertex
                    // break from the while loop
                    if ( neighbour.equals( end ) )
                        return;
                }
            }

            // update the number of vertex
            // we'll dequeue next time
            count = countTemp;
        }
    }

    /**
     * shortest path with the use of BFS
     */

    public static
    void BFS( int sizeOfGraph, Vertex start, Vertex end ) {
        if ( sizeOfGraph <= 0 || start == null || end == null ) return;

        start.parent = null;

        // initialize a queue
        // and enqueue the start vertex
        LinkedList<Vertex> queue = new LinkedList<>();
        queue.addLast( start );
        // and a boolean array to indicate
        // whether a vertex has been explored before
        boolean[] isVisited = new boolean[ sizeOfGraph ];
        isVisited[ start.ID ] = true;

        int count = 1;
        // while the queue is not empty
        while ( !queue.isEmpty() ) {
            int countTemp = 0;
            for ( int i = 0; i < count; i++ ) {
                // do, of the vertices already in the queue,
                // enqueue all their neighbours that haven't benn visited,
                // set their parent node to the dequeued vertex,
                // and mark them as visited,
                // and count the number of newly added vertices
                Vertex current = queue.removeFirst();
                for ( Vertex neighbour : current.neighbours ) {
                    if ( isVisited[ neighbour.ID ] ) continue;
                    isVisited[ neighbour.ID ] = true;

                    neighbour.parent = current;
                    queue.addLast( neighbour );
                    countTemp++;

                    // if the vertex to be enqueued is the end vertex
                    // break from the while loop
                    if ( neighbour.equals( end ) )
                        return;
                }
            }

            // update the number of vertex
            // we'll dequeue next time
            count = countTemp;
        }
    }
}
