package myLibraries.util.geometry.tools;

/*
 * EventRBTree.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.elements.point.EventPoint2D;
import myLibraries.util.tree.RedBlackTree;

import java.util.Comparator;

/**
 * Event queue for Bentley Ottmann's algorithm
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class EventRBTree
        extends RedBlackTree<EventPoint2D, EventPoint2D> {

    /**
     * constructs to create an instance of EventRBTree
     * */

    public EventRBTree( Comparator<EventPoint2D> comparator ) {
        super( comparator );
    }

    public EventRBTree() {
        this( null );
    }

    /**
     * put key -> val into this R-B tree
     * */

    public void put( EventPoint2D key ) {
        RedBlackTreeNode root = put( ( RedBlackTreeNode ) this.root, key );
        root.color = RedBlackTree.Color.BLACK;
        this.root = root;
    }

    private RedBlackTreeNode put( RedBlackTreeNode root, EventPoint2D key ) {
        // base case, attach the new node to this position
        if ( root == null ) return new RedBlackTreeNode( ID++, key, key, RedBlackTree.Color.RED );
        assert root.val == root.key;

        int res = compareKeys( root, key );
        // the node should be attached in the left subtree
        if ( res > 0 ) root.left = put( ( RedBlackTreeNode ) root.left, key );
        // the node should be attached in the right subtree
        else if ( res < 0 ) root.right = put( ( RedBlackTreeNode ) root.right, key );
        // added before, update value
        else updateSameKey( root, key );

        // update size and restore this R-B tree
        return balance( root );
    }

    /**
     * maybe several lines or arcs start at the same point
     * */

    private void updateSameKey( RedBlackTreeNode root, EventPoint2D val ) {
        if ( val.shape != null ) root.val.shapes.add( val.shape );
    }
}
