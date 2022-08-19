package coding.leetcode;

/*
 * Node.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/26/2022$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

// Definition for a Node.
public class Node {
    public int val;
    public List<Node> neighbors;

    public Node() {
        val = 0;
        neighbors = new ArrayList<Node>();
    }

    public Node( int _val ) {
        val = _val;
        neighbors = new ArrayList<Node>();
    }

    public Node( int _val, ArrayList<Node> _neighbors ) {
        val = _val;
        neighbors = _neighbors;
    }

    public Node( Node n ) {
        val = n.val;
        neighbors = new ArrayList<>( n.neighbors.size() + 1 );
    }
}
