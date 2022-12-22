package com.fengkeyleaf.util.graph;

/*
 * Vertex.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $0.0$
 */

import com.fengkeyleaf.util.Node;

import java.util.*;

/**
 * data structure of a Vertex in graph
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Vertex extends Node {
    protected static int staticID = 0;
    // adjacent list
    protected final List<Vertex> neighbours;
    // weights from this vertex to its neighbors.
    protected final List<Long> weights;
    // the smallest weight(distance)
    // going from the source vertex to this vertex
    long shortestWeight = Long.MAX_VALUE;
    // the leader of the group
    Vertex leader = this;
    // the group this vertex belongs to
    UnionFind group;
    // adjacent list in the form of edges,
    // mainly for finding a eulerian path.
    Queue<Edge> edges;
    // array to check if an edge has been added before
    boolean[] addEdge;

    /**
     * constructs to create an instance of Vertex
     * */

    public Vertex() {
        super( staticID++ );
         neighbours = new ArrayList<>();
         weights = new ArrayList<>();
    }

    public Vertex( int ID ) {
        super( ID );
        neighbours = new ArrayList<>();
        weights = new ArrayList<>();
    }

    public Vertex( int ID, Vertex predecessor ) {
        super( ID, predecessor );
        neighbours = new ArrayList<>();
        weights = new ArrayList<>();
    }

    protected Vertex( int ID, int capacity ) {
        super( ID );
        neighbours = new ArrayList<>( capacity );
        weights = new ArrayList<>( capacity );
    }

    //-------------------------------------------------------
    // add operations
    //-------------------------------------------------------

    /**
     * add an edge to this vertex
     * */

    public void add( Vertex neighbour ) {
        neighbours.add( neighbour );
    }

    public void add( Vertex n, long w ) {
        add( n );
        weights.add( w );
    }

    /**
     * Add an undirected edge from n1 <-> n2.
     * Note that this method will do the following four things:
     * 1) add n2 to n1's adjacent list;
     * 2) add n1 to n2's adjacent list;
     * 3) add a directed edge ( n1 -> n2 ) to n1.
     * 4) add a directed edge ( n2 -> n1 ) to n2.
     *
     * Applications: Eulerian Path and Circuit for undirected graph.
     */

    // n1 <-> n2
    public static
    void addUndirected( Vertex n1, Vertex n2 ) {
        if ( n1.edges == null ) n1.edges = new LinkedList<>();
        if ( n2.edges == null ) n2.edges = new LinkedList<>();

        Edge e = new Edge( n1, n2 );
        n1.add( n2 );
        n2.add( n1 );
        n1.edges.add( e );
        n2.edges.add( e );
    }

    /**
     * Add a directed edge from n1 -> n2.
     * Note that this method will do the following two things:
     * 1) add n2 to n1's adjacent list;
     * 2) add a directed edge ( n1 -> n2 ) to n1.
     *
     * Applications: Eulerian Path and Circuit for directed graph.
     */

    // n1 -> n2
    public static
    void addDirected( Vertex n1, Vertex n2 ) {
        if ( n1.edges == null ) n1.edges = new LinkedList<>();
        if ( n2.edges == null ) n2.edges = new LinkedList<>();

        Edge e = new Edge( n1, n2 );
        n1.add( n2 );
        n1.edges.add( e );
    }

    //-------------------------------------------------------
    // get & contains operations
    //-------------------------------------------------------

    /**
     * check if a given edge has been added before
     * */

    public boolean alreadyHasThisNeighbour( Vertex neighbour ) {
        return neighbours.contains( neighbour );
    }

    public List<Vertex> getNeighbours() {
        return neighbours;
    }

    public long getShortestWeight() {
        return shortestWeight;
    }

    /**
     * the size of the neighbours of this vertex
     * */

    public int size() {
        return neighbours.size();
    }

    /**
     * this vertex has any neighbours?
     * */

    public boolean hasNoNeighbours() {
        return neighbours.isEmpty();
    }

    /**
     * DFS to find all reachable vertices starting this vertex,
     * and count their number.
     * */

    public int countNumber() {
        List<Vertex> V = new ArrayList<>();
        int c = countNumber( this, V );
        Node.resetMappingID( V );
        return c;
    }

    public int countNumber( Vertex v, List<Vertex> V ) {
        // base case
        // visited before?
        if ( v.mappingID > -1 ) return 0;

        assert v.mappingID == -1;
        v.mappingID = 0;
        V.add( v );

        // recursion procedure
        int s = 0;
        for ( Vertex n : v.neighbours )
            s += countNumber( n, V );

        return s + 1;
    }

    //-------------------------------------------------------
    // union find operations
    //-------------------------------------------------------

    /**
     * determine whether two vertex are in the same group
     * */

    public boolean isSameUnion( Vertex v ) {
        assert leader != null && v.leader != null;
        // have the same leader?
        return  leader == v.leader;
    }

    /**
     * merge two union finds into one.
     *
     * @return reference with larger number of vertices will be returned,
     *         along with the vertices from the other union find.
     * */

    public UnionFind union( Vertex v2 ) {
        return group.union( v2.group );
    }

    //-------------------------------------------------------
    // Eulerian Path and Circuit
    //-------------------------------------------------------

    /**
     * Get Eulerian Path and Circuit in a directed or undirect graph.
     * Note that the given graph must contains a Eulerian Path and Circuit.
     *
     * @return Eulerian Path and Circuit in a list, from start vertex to end vertex.
     */

    // TODO: 8/5/2022 determine if there is a Eulerian Path and Circuit in this graph.
    // reference resource: http://www.graph-magics.com/articles/euler.php
    public List<Vertex> eulerianpath() {
        // 1. Undirected: Start with an empty stack and an empty circuit (eulerian path).
        //  - If all vertices have even degree - choose any of them.
        //  - If there are exactly 2 vertices having an odd degree - choose one of them.
        //  - Otherwise no euler circuit or path exists.
        // 1. Directed: Start with an empty stack and an empty circuit (eulerian path).
        //  - If all vertices have even degree - choose any of them.
        //  - If there are exactly 2 vertices having an odd degree - choose one of them.
        //  - Otherwise no euler circuit or path exists.
        Stack<Vertex> s = new Stack<>();
        List<Vertex> p = new ArrayList<>();
        Vertex c = this;

        // 2. Repeat step 2 until the current vertex has no more neighbors
        // and the stack is empty.
        while ( isGoOn( c, s ) ) {
            // 3. If current vertex has no neighbors -
            if ( c.edges.isEmpty() ) {
                // add it to circuit, remove the last vertex from the stack and set it as the current one.
                p.add( c );
                c = s.pop();
                continue;
            }

            // 4. Otherwise (in case it has neighbors)
            // add the vertex to the stack,
            s.push( c );
            // take any of its neighbors,
            // remove the edge between selected neighbor and that vertex,
            // and set that neighbor as the current vertex.
            Edge e = c.edges.poll();
            if ( e == null ) continue;

            e.mappingID = 0;
            c = e.startVertex == c ? e.endVertex : e.startVertex;
            assert c != s.peek();
        }

        // 5. Don't forget to add the start vertex to the path.
        assert c == this;
        p.add( c );

        // 6. Note that obtained circuit will be in reverse order
        // from end vertex to start vertex.
        Collections.reverse( p );
        // free up memory space.
        p.forEach( v -> v.edges = null );
        return p;
    }

    private static
    boolean isGoOn( Vertex c, Stack<Vertex> s ) {
        Queue<Edge> E = c.edges;
        while ( !E.isEmpty() && E.peek().mappingID > -1 )
            E.poll();

        return !E.isEmpty() || !s.isEmpty();
    }

    //-------------------------------------------------------
    // equals and toString.
    //-------------------------------------------------------

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null || getClass() != obj.getClass() ) return false;
        Vertex vertex = ( Vertex ) obj;
        return ID == vertex.ID;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append( ID ).append( ": [ " );
        for ( Vertex n : neighbours )
            text.append( n.ID ).append( " " );

        return text.append( "]" ).toString();
    }
}