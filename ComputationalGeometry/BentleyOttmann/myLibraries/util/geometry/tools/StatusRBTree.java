package myLibraries.util.geometry.tools;

/*
 * StatusRBTree.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.elements.point.EventPoint2D;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.tree.RedBlackTree;
import myLibraries.util.tree.elements.MapTreeNode;

import java.util.Comparator;

/**
 * status BBST for Bentley Ottmann's algorithm
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class StatusRBTree
        extends RedBlackTree<Vector, EventPoint2D> {

    /**
     * constructs to create an instance of StatusRBTree
     * */

    public StatusRBTree( Comparator<Vector> comparator ) {
        super( comparator );
    }

    public StatusRBTree() {
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

    // this part of code is very similar to
    // put( MapTreeNode<K, V> root, K key, V val ).
    private RedBlackTreeNode put( RedBlackTreeNode root, EventPoint2D key ) {
        // base case, attach the new node to this position
        if ( root == null ) return new RedBlackTreeNode( ID++, key, key, RedBlackTree.Color.RED );
        // update y based on give x,
        // so that we can compare y of two shapes
        assert root.val == root.key;
        root.val.updateYAndX( key );

        int res = compareKeys( root, key );
        // the node should be attached in the left subtree
        if ( res > 0 ) root.left = put( ( RedBlackTreeNode ) root.left, key );
            // the node should be attached in the right subtree
        else if ( res < 0 ) root.right = put( ( RedBlackTreeNode ) root.right, key );
            // added before, update value
        else root.val = key;

        // update size and restore this R-B tree
        return balance( root );
    }

    /**
     * Returns the least element in this set
     * strictly greater than the given element,
     * or null if there is no such element.
     *
     * get the key's Predecessor in this BST
     * */

    public RedBlackTreeNode lower( Vector key ) {
        return lower( ( RedBlackTreeNode ) root, key );
    }

    private RedBlackTreeNode lower( RedBlackTreeNode root, Vector key ) {
        // not found
        if ( root == null ) return null;
        // update y based on give x,
        // so that we can compare y of two shapes
        assert root.val == root.key : root.val + "   " + root.key;
        root.val.updateYAndX( key );

        int res = compareKeys( root, key );
        // not return even if found the equal,
        // less key must be in the left subtree
        // this is the major difference from floor()
        if ( res >= 0 ) return lower( ( RedBlackTreeNode ) root.left, key );
        // less key may be this root's,
        // but there may be greater one then the root's key
        // in the right subtree
        RedBlackTreeNode node = lower( ( RedBlackTreeNode ) root.right, key );
        return node == null ? root : node;
    }

    public EventPoint2D lowerVal( Vector key ) {
        RedBlackTreeNode res = lower( ( RedBlackTreeNode ) root, key );
        return res == null ? null : res.val;
    }

    /**
     * Returns the greatest element in this set
     * strictly less than the given element,
     * or null if there is no such element.
     *
     * get the key's Successor in this BST
     * */

    public RedBlackTreeNode higher( Vector key ) {
        return higher( ( RedBlackTreeNode ) root, key );
    }

    private RedBlackTreeNode higher( RedBlackTreeNode root, Vector key ) {
        // not found
        if ( root == null ) return null;
        // update y based on give x,
        // so that we can compare y of two shapes
        assert root.val == root.key;
        root.val.updateYAndX( key );

        int res = compareKeys( root, key );
        // not return even if found the equal,
        // greater key must be in the right subtree
        // this is the major difference from ceiling()
        if ( res <= 0 ) return higher( ( RedBlackTreeNode ) root.right, key );
        // greater key may be this root's,
        // but there may be less key then the root's key
        // in the left subtree
        RedBlackTreeNode node = higher( ( RedBlackTreeNode ) root.left, key );
        return node == null ? root : node;
    }

    public EventPoint2D higherVal( Vector key ) {
        RedBlackTreeNode res = higher( ( RedBlackTreeNode) root, key );
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
        updateRootForDelete( delete( ( RedBlackTreeNode ) this.root, key ) );
    }

    // Note that this recursive method is a bit unique from usual ones,
    // since the base case for this method is not at the beginning of code
    private RedBlackTreeNode delete( RedBlackTreeNode root, Vector key ) {
        // update y based on give x,
        // so that we can compare y of two shapes
        assert root.val == root.key;
        root.val.updateYAndX( key );

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

            root.left = delete( ( RedBlackTreeNode ) root.left, key );
        }
        // the key may be in the right subtree,
        // or found the key to delete.
        else {
            RedBlackTreeNode original = root;
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
                root.right = deleteMin( ( RedBlackTreeNode ) root.right );
            }
            // the key may be in the right subtree,
            else root.right = delete( ( RedBlackTreeNode ) root.right, key );
        }

        // update size and restore this R-B tree
        return balance( root );
    }

    public EventPoint2D deleteAndGetVal( Vector key ) {
        deletedNode = null;
        delete( key );
        return deletedNode == null ? null : deletedNode.val;
    }
}
