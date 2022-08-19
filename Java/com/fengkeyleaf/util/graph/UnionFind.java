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

// TODO: 7/30/2022 use LinkedList;
public class UnionFind extends Graph<Vertex> {

    /**
     * Constructs to create a union find.
     *
     * This constructor hss the following two properties:
     * 1) leader of this union find will be the vertex v.
     * 2) leader of the vertex will be itself.
     *
     * @param v vertex assigned as the leader for this union find.
     */

    public UnionFind( Vertex v ) {
        add( v );
        v.leader = v;
        v.group = this;
    }

    /**
     * add all vertices of a UnionFind(aUnionFind2) into the other(aUnionFind1)
     * */

    void doUnion( UnionFind u ) {
        assert !isEmpty();
        assert size() >= u.size();

        if ( this == u ) return;

        final Vertex leader = vertices.get( 0 ).leader;
        // reset the leader and the group
        u.vertices.forEach( v -> {
            v.leader = leader;
            v.group = this;
        } );

        // merge. Less efficient for this operation with ArrayList
        vertices.addAll( u.vertices );
    }

    /**
     * determine which UnionFind to be merged
     * we will merge the smaller one
     * */

    UnionFind union( UnionFind u ) {
        if ( size() < u.size() ) {
            u.doUnion( this );
            return u;
        }

        doUnion( u );
        return this;
    }

    /**
     * overlord with Vertex:
     * public static UnionFind union
     *
     * @deprecated move into {@link Vertex#union(Vertex)}
     * */

    @Deprecated
    public static
    UnionFind union( Vertex v1, Vertex v2 ) {
        return v1.group.union( v2.group );
    }

    /**
     * @deprecated move into {@link Edge#union(Edge)} )}
     * */

    @Deprecated
    public static
    UnionFind union( Edge edge ) {
        return union( edge.startVertex, edge.endVertex );
    }

    /**
     * return the union leader if merge the give edge
     *
     * @deprecated move into {@link Edge#getLeader()}
     * */

    @Deprecated
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
     *
     * @deprecated move into {@link Vertex#isSameUnion(Vertex)}
     * */

    @Deprecated
    public static
    boolean isSameUnion( Vertex v1, Vertex v2 ) {
        assert ( v1.leader != null && v2.leader != null );
        // have the same leader?
        return  v1.leader == v2.leader;
    }
}
