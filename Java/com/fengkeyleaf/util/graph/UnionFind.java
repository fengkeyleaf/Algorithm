package com.fengkeyleaf.util.graph;

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

/**
 * data structure of Union Find
 *
 * Note that this implementation with arrayList is
 * less efficient than the one with LinkedList,
 * since I consider union find as a type of graph that
 * also has the ability to access a vertex with its ID in O(1)
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class UnionFind extends Graph<Vertex> {
    public UnionFind( Vertex v ) {
        add( v );
        v.leader = v;
        v.group = this;
    }

    /**
     * add all vertices of a UnionFind(aUnionFind2) into the other(aUnionFind1)
     * */

    static
    void doUnion( UnionFind u1, UnionFind u2 ) {
        assert !u1.isEmpty();
        assert u1.size() >= u2.size();

        if ( u1 == u2 ) return;

        final Vertex leader = u1.vertices.get( 0 ).leader;
        // reset the leader and the group
        u2.vertices.forEach( v -> {
            v.leader = leader;
            v.group = u1;
        } );

        // merge. Less efficient for this operation with ArrayList
        u1.vertices.addAll( u2.vertices );
    }

    /**
     * determine which UnionFind to be merged
     * we will merge the smaller one
     * */

    static
    UnionFind union( UnionFind u1, UnionFind u2 ) {
        if ( u1.size() < u2.size() ) {
            doUnion( u2, u1 );
            return u2;
        }

        doUnion( u1, u2 );
        return u1;
    }

    /**
     * overlord with Vertex:
     * public static UnionFind union
     * */

    public static
    UnionFind union( Vertex v1, Vertex v2 ) {
        return union( v1.group, v2.group );
    }

    public static
    UnionFind union( Edge edge ) {
        return union( edge.startVertex, edge.endVertex );
    }

    /**
     * return the union leader if merge the give edge
     * */

    public static
    Vertex getLeader( Edge edge ) {
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
    boolean isSameUnion( Vertex v1, Vertex v2 ) {
        assert ( v1.leader != null && v2.leader != null );
        // have the same leader?
        return  v1.leader == v2.leader;
    }
}
