package myLibraries.util.geometry;

/*
 * EventRBTree.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 */

import myLibraries.util.geometry.elements.Event;
import myLibraries.util.tree.RedBlackTree;
import myLibraries.util.tree.elements.RBTreeNode;

import java.util.Comparator;

/**
 * Event queue for Bentley Ottmann's algorithm
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

// TODO: 1/13/2022 introduce check() for BBST
public final class EventRBTree
        extends RedBlackTree<Event, Event> {

    /**
     * constructs to create an instance of EventRBTree
     * */

    public EventRBTree( Comparator<Event> comparator ) {
        super( comparator );
    }

    public EventRBTree() {
        this( null );
    }

    /**
     * put key -> val( key ) into this R-B tree
     * */

    public void put( Event key ) {
        if ( isNull( key, key ) ) return;

        RBTreeNode<Event, Event> root = put( ( RBTreeNode<Event, Event> ) this.root, key );
        root.color = BLACK;
        this.root = root;
        assert check();
    }

    private RBTreeNode<Event, Event> put( RBTreeNode<Event, Event> root, Event key ) {
        // base case, attach the new node to this position
        if ( root == null ) return new RBTreeNode<>( ID++, key, key, RED );
        assert root.val == root.key;

        int res = compareKeys( root, key );
        // the node should be attached in the left subtree
        if ( res > 0 ) {
            root.left = put( ( RBTreeNode<Event, Event> ) root.left, key );
            root.left.parent = root;
        }
        // the node should be attached in the right subtree
        else if ( res < 0 ) {
            root.right = put( ( RBTreeNode<Event, Event> ) root.right, key );
            root.right.parent = root;
        }
        // added before, update value
        else updateSameKey( root, key );

        // update size and restore this R-B tree
        return balance( root );
    }

    /**
     * maybe several lines or arcs( Geometric Intersection ) start at the same point,
     * or sites have the same Y coordinate( Voronoi Diagrams )
     * */

    private void updateSameKey( RBTreeNode<Event, Event> root, Event val ) {
        // Geometric Intersection : shapes starting at this event point( left endpoint )
        // duplicate intersection eventPoint won't be
        // added multiple times, since this kind of eventPoint's shape is null
        // and thus won't update the root

        // Voronoi Diagrams: sites with the same Y coordinate
        if ( val.shape != null )
            root.val.shapes.add( val.shape );
    }
}
