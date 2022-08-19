package coding.leetcode.medium;

/*
 * CloneGraph.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/26/2022$
 */

import coding.leetcode.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * <a href="https://leetcode.com/problems/clone-graph/">133. Clone Graph</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public final class CloneGraph {

    LinkedList<Node> qOri = new LinkedList<>();
    LinkedList<Node> qCop = new LinkedList<>();
    TreeMap<Integer, Node> m = new TreeMap<>();

    // FIXME: 7/26/2022 must return a copy of the nodes, don't know why
    public Node cloneGraph( Node node ) {
        if ( node == null ) return null;

        Node f = copy( node );
        qCop.add( f );
        qOri.add( node );
        node.val = -1;
        while ( !qOri.isEmpty() ) {
            Node o = qOri.poll();
            Node c = qCop.poll();

            o.neighbors.forEach( v -> {
                if ( v.val < 1 ) return;

                Node t = copy( v );
                c.neighbors.add( t );
                t.neighbors.add( c );
                qOri.add( v );
                qCop.add( t );
            } );

            o.val = -1;
        }

        return f;
    }

    private Node copy( Node n ) {
        if ( m.containsKey( n.val ) )
            return m.get( n.val );

        Node t = new Node();
        t.val = n.val;
        t.neighbors = new ArrayList<>( n.neighbors.size() + 1 );
        m.put( t.val, t );
        return t;
    }

    static
    void test1() {
        Node n1 = new Node( 1 );
        Node n2 = new Node( 2 );
        Node n3 = new Node( 3 );
        Node n4 = new Node( 4 );

        n1.neighbors.add( n2 );
        n1.neighbors.add( n4 );
        n2.neighbors.add( n1 );
        n2.neighbors.add( n3 );
        n4.neighbors.add( n1 );
        n4.neighbors.add( n3 );
        n3.neighbors.add( n4 );
        n3.neighbors.add( n2 );

        new CloneGraph().cloneGraph( n1 );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
