package com.fengkeyleaf.util.geom;

/*
 * StatusRBTree.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.util.tree.MapTreeNode;
import com.fengkeyleaf.util.tree.RBTreeNode;
import com.fengkeyleaf.util.tree.RedBlackTree;

import java.util.Comparator;

/**
 * status BBST for Bentley Ottmann's algorithm,
 * partitioning monotone subpolygon
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// TODO: 1/13/2022 introduce check() for BBST
// TODO: 3/12/2022 status trees and event queues can be merged into one?
class StatusRBTree
        extends RedBlackTree<Vector, Event> {
    boolean isVerticalSweepLine = true;

    /**
     * constructs to create an instance of StatusRBTree
     * */

    StatusRBTree( Comparator<Vector> comparator ) {
        super( comparator );
    }

    StatusRBTree() {
        this( null );
    }

    /**
     * put key -> val into this R-B tree
     * */

    void put( Event key ) {
        if ( isNull( key, key ) ) return;

        RBTreeNode<Vector, Event> root = put( ( RBTreeNode<Vector, Event> ) this.root, key );
        root.color = BLACK;
        this.root = root;
    }

    private RBTreeNode<Vector, Event> put( RBTreeNode<Vector, Event> root, Event key ) {
        // base case, attach the new node to this position
        if ( root == null ) return new RBTreeNode<>( ID++, key, key, RED );
        // TODO: 7/12/2021 non-dynamic in put() for triangulation, correct?
        // update y based on give x,
        // so that we can compare y of two shapes
        assert root.val == root.key : root.key + " " + root.val;
        root.val.update( key, isVerticalSweepLine );

        int res = compareKeys( root, key );
        // the node should be attached in the left subtree
        if ( res > 0 ) root.left = put( ( RBTreeNode<Vector, Event> ) root.left, key );
        // the node should be attached in the right subtree
        else if ( res < 0 ) root.right = put( ( RBTreeNode<Vector, Event> ) root.right, key );
        // added before, update value
        else updateSameKey( root, key );

        // update size and restore this R-B tree
        return balance( root );
    }

    /**
     * store shapes with the same point into the node,
     * they are usually overlapping segments.
     * */

    private void updateSameKey( RBTreeNode<Vector, Event> root, Event val ) {
        if ( val.shape != null )
            root.val.shapes.add( val.shape );
    }

    /**
     * Returns the least element in this set
     * strictly greater than the given element,
     * or null if there is no such element.
     *
     * get the key's Predecessor in this BST
     * */

    public RBTreeNode<Vector, Event> lower( Vector key ) {
        return lower( ( RBTreeNode<Vector, Event> ) root, key );
    }

    private RBTreeNode<Vector, Event> lower( RBTreeNode<Vector, Event> root, Vector key ) {
        // not found
        if ( root == null ) return null;
        // update y based on give x,
        // so that we can compare y of two shapes
        assert root.val == root.key : root.val + "   " + root.key;
        root.val.update( key, isVerticalSweepLine );

        int res = compareKeys( root, key );
        // not return even if found the equal,
        // less key must be in the left subtree
        // this is the major difference from floor()
        if ( res >= 0 ) return lower( ( RBTreeNode<Vector, Event> ) root.left, key );
        // less key may be this root's,
        // but there may be greater one then the root's key
        // in the right subtree
        RBTreeNode<Vector, Event> node = lower( ( RBTreeNode<Vector, Event> ) root.right, key );
        return node == null ? root : node;
    }

    public Event lowerVal( Vector key ) {
        RBTreeNode<Vector, Event> res = lower( ( RBTreeNode<Vector, Event> ) root, key );
        return res == null ? null : res.val;
    }

    /**
     * Returns the greatest element in this set
     * strictly less than the given element,
     * or null if there is no such element.
     *
     * get the key's Successor in this BST
     * */

    public RBTreeNode<Vector, Event> higher( Vector key ) {
        return higher( ( RBTreeNode<Vector, Event> ) root, key );
    }

    private RBTreeNode<Vector, Event> higher( RBTreeNode<Vector, Event> root, Vector key ) {
        // not found
        if ( root == null ) return null;
        // update y based on give x,
        // so that we can compare y of two shapes
        assert root.val == root.key;
        root.val.update( key, isVerticalSweepLine );

        int res = compareKeys( root, key );
        // not return even if found the equal,
        // greater key must be in the right subtree
        // this is the major difference from ceiling()
        if ( res <= 0 ) return higher( ( RBTreeNode<Vector, Event> ) root.right, key );
        // greater key may be this root's,
        // but there may be less key then the root's key
        // in the left subtree
        RBTreeNode<Vector, Event> node = higher( ( RBTreeNode<Vector, Event> ) root.left, key );
        return node == null ? root : node;
    }

    public Event higherVal( Vector key ) {
        RBTreeNode<Vector, Event> res = higher( ( RBTreeNode<Vector, Event> ) root, key );
        return res == null ? null : res.val;
    }

    /**
     * delete the key -> value in this R-B tree
     * */

    public void delete( Vector key ) {
        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( isEmpty() ) return;

        // delete the node and update the root
        updateRootForDelete( delete( ( RBTreeNode<Vector, Event> ) this.root, key ) );
    }

    // Note that this recursive method is a bit unique from usual ones,
    // since the base case for this method is not at the beginning of code
    private RBTreeNode<Vector, Event> delete( RBTreeNode<Vector, Event> root, Vector key ) {
        // update y based on give x,
        // so that we can compare y of two shapes
        assert root.val == root.key;
        root.val.update( key, isVerticalSweepLine );

        // the key may be in the left subtree.
        if ( compareKeys( root, key ) > 0 ) {
            // this part of code is very similar to
            // deleteMin( RedBlackTreeNode root ).

            // base 3, not found the key in this R-B tree
            // and this corner case,
            // where you delete a minimum key that is not in the R-B tree,
            // is missed by the textbook
            if ( root.left == null ) return root;

            if ( !isRed( root.left ) &&
                    !isRed( root.left.left ) )
                root = moveRedLeft( root );

            root.left = delete( ( RBTreeNode<Vector, Event> ) root.left, key );
        }
        // the key may be in the right subtree,
        // or found the key to delete.
        else {
            RBTreeNode<Vector, Event> original = root;
            // this part of code is very similar to
            // deleteMax( RedBlackTreeNode root ).
            // starts at here ---->
            if ( isRed( root.left ) )
                root = rotateRight( root );

            // base case 2 and also case 1, found the key and the node
            // associated with the key is either a 3-node or 4-node
            // just delete it
            // ---------->
            // Updated: the node to delete will be rotated to right,
            // if isRed( root.left ) == true,
            // and this guarantee root.right == null is false
            if ( compareKeys( root, key ) == 0 &&
                    root.right == null ) {
                deletedNode = root;
                return null;
            }

            // base 3, not found the key in this R-B tree
            // and this corner case,
            // where you delete a maximum key that is not in the R-B tree,
            // is missed by the textbook
            if ( root.right == null ) return root;


            if ( !isRed( root.right ) &&
                    !isRed( root.right.left ) ) {
                root = moveRedRight( root );
            }
            // ----> ends here

            // case 2, found the key but
            // the node associated with the key is a 2-node
            // replace it with its successor and
            // delete the successor with deleteMin( root ).
            // ---------->
            // Updated: delete the root iff original == root,
            // i.e. the node to delete isn't rotated to right
            if ( original == root &&
                    compareKeys( root, key ) == 0 ) {
                deletedNode = new MapTreeNode<>( root );
                // replace the node with its successor
                root.replace( min( root.right ) );
                // delete the successor
                root.right = deleteMin( ( RBTreeNode<Vector, Event> ) root.right );
            }
            // the key may be in the right subtree,
            else root.right = delete( ( RBTreeNode<Vector, Event> ) root.right, key );
        }

        // update size and restore this R-B tree
        return balance( root );
    }

    public Event deleteAndGetVal( Vector key ) {
        deletedNode = null;
        delete( key );
        return deletedNode == null ? null : deletedNode.val;
    }
}
