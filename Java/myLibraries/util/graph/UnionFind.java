package myLibraries.util.graph;

/*
 * UnionFind.java
 *
 * JDK: 14
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 standard data structure$
 *     $1.1 standard data structure with generic$
 */

import myLibraries.util.graph.elements.Edge;
import myLibraries.util.graph.elements.Vertex;

/**
 * data structure of Union Find
 *
 * Note that this implementation with arrayList is
 * less efficient than the one with LinkedList,
 * since I consider union find as a type of graph that
 * also has the ability to access a vertex with its ID in O(1)
 *
 * @author       Xiaoyu Tongyang
 */

public class UnionFind extends Graph<Vertex> {
    public UnionFind( Vertex aVertex ) {
        add( aVertex );
        aVertex.leader = aVertex;
        aVertex.group = this;
    }

    /**
     * add all vertices of a UnionFind(aUnionFind2) into the other(aUnionFind1)
     * */

    public static
    void doUnion( UnionFind aUnionFind1, UnionFind aUnionFind2 ) {
        assert !aUnionFind1.isEmpty();
        assert aUnionFind1.size() >= aUnionFind2.size();

        if ( aUnionFind1 == aUnionFind2 ) return;

        final Vertex leader = aUnionFind1.vertices.get( 0 ).leader;
        // reset the leader and the group
        aUnionFind2.vertices.forEach( vertex -> {
            vertex.leader = leader;
            vertex.group = aUnionFind1;
        } );

        // merge. Less efficient for this operation with ArrayList
        aUnionFind1.vertices.addAll( aUnionFind2.vertices );
    }

    /**
     * determine which UnionFind to be merged
     * we will merge the smaller one
     * */

    public static UnionFind union(
            UnionFind aUnionFind1, UnionFind aUnionFind2 ) {
        if ( aUnionFind1.size() < aUnionFind2.size() ) {
            doUnion( aUnionFind2, aUnionFind1 );
            return aUnionFind2;
        }

        doUnion( aUnionFind1, aUnionFind2 );
        return aUnionFind1;
    }

    /**
     * overlord with Vertex:
     * public static UnionFind union
     * */

    public static
    UnionFind union( Vertex vertex1, Vertex vertex2 ) {
        return union( vertex1.group, vertex2.group );
    }

    /**
     * overlord with Edge:
     * public static UnionFind union
     * */

    public static
    UnionFind union( Edge edge ) {
        return union( edge.startVertex, edge.endVertex );
    }

    /**
     * return the union leader if merge the give edge
     * */

    public static
    Vertex getUnionLeader( Edge edge ) {
        Vertex leaderStart = edge.startVertex.leader;
        Vertex leaderEnd = edge.endVertex.leader;
        if ( leaderStart.group.size() <
                leaderEnd.group.size() )
            return leaderEnd;

        return leaderStart;
    }

    /**
     * determine whether two vertex are in the same group
     * */

    public static
    boolean findSameUnion( Vertex aVertex1, Vertex aVertex2 ) {
        assert ( aVertex1.leader != null && aVertex2.leader != null );
        // have the same leader?
        return  aVertex1.leader == aVertex2.leader;
    }
}
