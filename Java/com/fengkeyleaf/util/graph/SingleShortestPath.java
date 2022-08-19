package com.fengkeyleaf.util.graph;

/*
 * SingleShortestPath.java
 *
 * JDK: 14
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 added Bellman Ford and Dijkstra's on 4/20/2021$
 *     $1.0 added BFS and funnel on 7/14/2021$
 */

import com.fengkeyleaf.util.Node;
import com.fengkeyleaf.util.geom.HalfEdge;
import com.fengkeyleaf.util.geom.Triangles;
import com.fengkeyleaf.util.geom.Vector;
import com.fengkeyleaf.util.tree.MyPriorityQueue;

import java.util.*;

/**
 * Algorithms related to single-resource shortest path problems
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class SingleShortestPath {

    //-------------------------------------------------------
    // Funnel algorithm.
    //-------------------------------------------------------

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
                assert current.twin.origin.mappingID == -1;
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
     */

    // Reference resource: http://digestingduck.blogspot.com/2010/03/simple-stupid-funnel-algorithm.html
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

        // return the corners we've gone though,
        // including the start point,
        // and the end point.
        System.out.println( visitedVertices );
        return visitedVertices;
    }

    //-------------------------------------------------------
    // BFS
    //-------------------------------------------------------

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

    //-------------------------------------------------------
    // Bellman Ford's
    //-------------------------------------------------------

    /**
     * update shortest path
     */

    private static
    boolean updatePathWithOneEdge( ShortestVertex start, ShortestVertex destination,
                                   long distance, boolean[] ifReachable,
                                   long[] shortestDistances, boolean[] ifUpdatedThisRound ) {
        long cumulativeShortest = shortestDistances[ start.ID ] + distance;
        assert cumulativeShortest >= 0; // long overflow

        // never reached the destination before
        if ( !ifReachable[ destination.ID ] && !destination.ifReachable ) {
            destination.currentShortestDistance = cumulativeShortest;
            destination.parent = start;
            destination.ifReachable = true;
            ifUpdatedThisRound[ destination.ID ] = true;
            return true;
        }

        // the destination is now reachable
        // Has it been updated before?
        // Yes, compare cumulativeShortest with updated information;
        // no, compare it with current information
        long previousShortest = ifUpdatedThisRound[ destination.ID ] ?
                destination.currentShortestDistance : shortestDistances[ destination.ID ];

        // update the shortest path?
        if ( previousShortest > cumulativeShortest ) {
            destination.currentShortestDistance = cumulativeShortest;
            destination.parent = start;
            ifUpdatedThisRound[ destination.ID ] = true;
            return true;
        }

        return false;
    }

    /**
     * Do constricted Bellman Ford's
     */

    private static
    void constrictedBellmanFord( Graph<ShortestVertex> aGraph,
                                 int edgeLimit, boolean[] ifReachable,
                                 ShortestVertex[] predecessors, long[] shortestDistances,
                                 boolean[] ifUpdatedThisRound ) {
        boolean ifContinue = true; // false, no changes and we can stop
        for ( int i = 0; i < edgeLimit && ifContinue; i++ ) {
            boolean ifContinueThisRound = false;

            for ( Edge edge : aGraph.edges ) {
                ShortestVertex startVertex = ( ShortestVertex ) edge.startVertex;
                ShortestVertex endVertex = ( ShortestVertex ) edge.endVertex;

                // go from startVertex to endVertex
                if ( ifReachable[ startVertex.ID ] &&
                        updatePathWithOneEdge( startVertex, endVertex,
                                edge.distance, ifReachable, shortestDistances, ifUpdatedThisRound ) )
                    ifContinueThisRound = true;

                // go from the opposite direction, endVertex to startVertex
                if ( ifReachable[ endVertex.ID ] &&
                        updatePathWithOneEdge( endVertex, startVertex,
                                edge.distance, ifReachable, shortestDistances, ifUpdatedThisRound ) )
                    ifContinueThisRound = true;
            }

            ifContinue = ifContinueThisRound;
            Arrays.fill( ifUpdatedThisRound, false );

            // update current information after one iteration
            aGraph.vertices.forEach( vertex -> {
                int ID = vertex.ID;
                ifReachable[ ID ] = vertex.ifReachable;
                predecessors[ ID ] = ( ShortestVertex ) vertex.parent;
                shortestDistances[ ID ] = vertex.currentShortestDistance;
            } );
        }
    }

    /**
     * Bellman Ford's that is forced to make
     * only one edge of progress at a given step.
     *
     * The idea of implementing the restriction is
     * to put current information and updated information into two separated parts
     * i.e. we'll not use updated information until we go into next iteration
     * in order to make several progresses in one iteration
     *
     * note that for this current implementation,
     * vertex's ID has to start from 0,
     * and the given graph is undirected
     */

    public static
    void constrictedBellmanFord( Graph<ShortestVertex> aGraph,
                                 int edgeLimit, ShortestVertex start ) {
        // Have the same ID in the graph
        assert start.ID == aGraph.vertices.get( start.ID ).ID;

        int numberOfVertices = aGraph.size();
        // edgelimit greater than ( n - 1 ) is redundant
        // since the shortest path has at most ( n - 1 ) edges
        if ( edgeLimit > numberOfVertices - 1 )
            edgeLimit = numberOfVertices - 1;

        // the following three arrays represent current information
        // so the information stored in the ShortestVertex class represents updated information
        final boolean[] ifReachable = new boolean[ numberOfVertices ];
        ifReachable[ start.ID ] = true;
        final ShortestVertex[] predecessors = new ShortestVertex[ numberOfVertices ];
        predecessors[ start.ID ] = null; // could be removed
        final long[] shortestDistances = new long[ numberOfVertices ];
        Arrays.fill( shortestDistances, Long.MAX_VALUE );
        shortestDistances[ start.ID ] = 0;

        // the current information and updated information for the start vertex are the same
        start.currentShortestDistance = 0;
        start.ifReachable = true;

        // array to indicate if a certain vertex has been updated in a iteration or not
        final boolean[] ifUpdatedThisRound = new boolean[ aGraph.size() ];

        constrictedBellmanFord( aGraph, edgeLimit,
                ifReachable, predecessors, shortestDistances, ifUpdatedThisRound );
    }

    //-------------------------------------------------------
    // Dijkstra's
    //-------------------------------------------------------

    /**
     * Dijkstra's
     *
     * @deprecated plase use {@link SingleShortestPath#dijkstra(Vertex, Graph)}
     */

    @Deprecated
    public static
    List<Edge> allShortestPath( ShortestVertex s, ShortestVertex d,
                                Graph<ShortestVertex> g ) {
        System.err.println( "Wrong method, not use this" );
        System.exit( 1 );
        return null;
    }

    /**
     * Run Dijkstra's to compute all shortest paths starting at the starting vertex s.
     *
     * Note that all weights in the graph g, including s, must be non-negative.
     * For negative weighted single-resource shortest path, use BellmanFord's.
     *
     * To retrieve the minimum weight from s to a node in the graph g, just use {@link Vertex#shortestWeight}.
     * To get the shortest path from s to a node in the graph g, just use {@link Node#getPath()}
     *
     * @param s starting vertex.
     * @param g Graph g containing vertices to calculate
     *          the shortest weighted path starting at the starting vertex.
     */

    public static<V extends Vertex>
    void dijkstra( V s, Graph<V> g ) {
        if ( !g.vertices.contains( s ) )
            throw new IllegalArgumentException( "Starting vertex is not in the graph." );

        // 1. Let H = V – {s};
        final Comparator<V> c = ( v1, v2 ) -> -Long.compare( v1.shortestWeight, v2.shortestWeight );

        // 2. For every vertex v do
        g.forEach( v -> {
            // 3. dist[v] = ∞, parent[v] = null
            v.shortestWeight = Long.MAX_VALUE;
            v.parent = null;
        } );
        // 4. dist[s] = 0, parent[s] = none
        s.shortestWeight = 0;
        // 5. Update (s)

        MyPriorityQueue<V> Q = new MyPriorityQueue<>( c );
        g.forEach( Q::add );
        // 6. For i = 1 to n - 1 do
        while ( !Q.isEmpty() ) {
            // 7. u = extract vertex from H of smallest weight
            V v = Q.poll();

            // 8. Update(u)
            update( v );

            // rearrange elements in the queue after updating.
            MyPriorityQueue<V> q = new MyPriorityQueue<>( c );
            Q.forEach( q::add );
            Q = q;
        }

        // 9. Return dist[]
        // This information is stored in each vertex.
    }

    private static<V extends Vertex>
    void update( V v ) {
        // 1. For every neighbor n of v (such that n in H)
        for ( int i = 0; i < v.neighbours.size(); i++ ) {
            Vertex n = v.neighbours.get( i );

            assert v.weights.get( i ) >= 0;
            // 2. If dist[n] > dist[v] + w(n,v) then
            if ( n.shortestWeight > v.shortestWeight + v.weights.get( i ) ) {
                // 3. dist[n] = dist[v] + w(n,v)
                n.shortestWeight = v.shortestWeight + v.weights.get( i );
                // 4. parent[n] = v
                n.parent = v;
            }
        }
    }
}
