"use strict"

/*
 * StatusRBTree.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import RedBlackTree from "../../tree/RedBlackTree.js";
import RedBlackTreeNode from "../../tree/elements/RedBlackTreeNode.js";
import MapTreeNode from "../../tree/elements/MapTreeNode.js";
import BinarySearchTree from "../../tree/BinarySearchTree.js";

/**
 * status BBST for Bentley Ottmann's algorithm,
 * partitioning monotone subpolygon
 *
 * The source code in java is from my own github:
 * @see <a href=StatusRBTree>https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/tools/StatusRBTree.java</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class StatusRBTree
    extends RedBlackTree {

    /**
     * constructs to create an instance of StatusRBTree
     * */

    constructor( comparator ) {
        super( comparator );
    }

    /**
     * put key -> val into this R-B tree
     * */

    put( key ) {
        let root = this.putRecur( this.root, key );
        root.color = RedBlackTreeNode.Color.BLACK;
        this.root = root;
    }

    // this part of code is very similar to
    // put( MapTreeNode<K, V> root, K key, V val ).
    putRecur( root, key ) {
        // base case, attach the new node to this position
        if ( root == null ) return new RedBlackTreeNode( {
            ID: BinarySearchTree.ID++,
            key: key,
            val: key,
            color: RedBlackTreeNode.Color.RED
        } );
        // TODO: 7/12/2021 non-dynamic in put(), correct?
        // update y based on give x,
        // so that we can compare y of two shapes
        console.assert( root.val === root.key );
//        root.val.updateYAndX( key );

        let res = this.compareKeys( root, key );
        // the node should be attached in the left subtree
        if ( res > 0 ) root.left = this.putRecur( root.left, key );
        // the node should be attached in the right subtree
        else if ( res < 0 ) root.right = this.putRecur( root.right, key );
        // added before, update value
        else root.val = key;

        // update size and restore this R-B tree
        return this.balance( root );
    }

    /**
     * Returns the least element in this set
     * strictly greater than the given element,
     * or null if there is no such element.
     *
     * get the key's Predecessor in this BST
     * */

    lower( key ) {
        return this.lowerRecur( this.root, key );
    }

    lowerRecur( root, key ) {
        // not found
        if ( root == null ) return null;
        // update y based on give x,
        // so that we can compare y of two shapes
        console.assert( root.val === root.key, root.val + "   " + root.key );
        root.val.updateYAndX( key );

        let res = this.compareKeys( root, key );
        // not return even if found the equal,
        // less key must be in the left subtree
        // this is the major difference from floor()
        if ( res >= 0 ) return this.lowerRecur( root.left, key );
        // less key may be this root's,
        // but there may be greater one then the root's key
        // in the right subtree
        let node = this.lowerRecur( root.right, key );
        return node == null ? root : node;
    }

    lowerVal( key ) {
        let res = this.lowerRecur( this.root, key );
        return res == null ? null : res.val;
    }

    /**
     * Returns the greatest element in this set
     * strictly less than the given element,
     * or null if there is no such element.
     *
     * get the key's Successor in this BST
     * */

    higher( key ) {
        return this.higherRecur( this.root, key );
    }

    higherRecur( root, key ) {
        // not found
        if ( root == null ) return null;
        // update y based on give x,
        // so that we can compare y of two shapes
        console.assert( root.val === root.key );
        root.val.updateYAndX( key );

        let res = this.compareKeys( root, key );
        // not return even if found the equal,
        // greater key must be in the right subtree
        // this is the major difference from ceiling()
        if ( res <= 0 ) return this.higherRecur( root.right, key );
        // greater key may be this root's,
        // but there may be less key then the root's key
        // in the left subtree
        let node = this.higherRecur( root.left, key );
        return node == null ? root : node;
    }

    higherVal( key ) {
        let res = this.higherRecur( this.root, key );
        return res == null ? null : res.val;
    }

    /**
     * delete the key -> value in this R-B tree
     * */

    delete( key ) {
        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( this.isEmpty() ) return;

        // delete the node and update the root
        this.updateRootForDelete( this.deleteRecur( this.root, key ) );
    }

    // Note that this recursive method is a bit unique from usual ones,
    // since the base case for this method is not at the beginning of code
    deleteRecur( root, key ) {
        // update y based on give x,
        // so that we can compare y of two shapes
        console.assert( root.val === root.key );
        root.val.updateYAndX( key );

        // the key may be in the left subtree.
        if ( this.compareKeys( root, key ) > 0 ) {
            // this part of code is very similar to
            // deleteMin( RedBlackTreeNode root ).

            // base 3, not found the key in this R-B tree
            // and this corner case,
            // where you delete a minimum key that is not in the R-B tree,
            // is missed by the textbook
            if ( root.left == null ) return root;

            if ( !this.isRed( root.left ) &&
                !this.isRed( root.left.left ) )
                root = this.moveRedLeft( root );

            root.left = this.deleteRecur( root.left, key );
        }
            // the key may be in the right subtree,
        // or found the key to delete.
        else {
            let original = root;
            // this part of code is very similar to
            // deleteMax( RedBlackTreeNode root ).
            // starts at here ---->
            if ( this.isRed( root.left ) )
                root = this.rotateRight( root );

            // base case 2 and also case 1, found the key and the node
            // associated with the key is either a 3-node or 4-node
            // just delete it
            // ---------->
            // Updated: the node to delete will be rotated to right,
            // if isRed( root.left ) == true,
            // and this guarantee root.right == null is false
            if ( this.compareKeys( root, key ) === 0 &&
                root.right == null ) {
                this.deletedNode = root;
                return null;
            }

            // base 3, not found the key in this R-B tree
            // and this corner case,
            // where you delete a maximum key that is not in the R-B tree,
            // is missed by the textbook
            if ( root.right == null ) return root;


            if ( !this.isRed( root.right ) &&
                !this.isRed( root.right.left ) ) {
                root = this.moveRedRight( root );
            }
            // ----> ends here

            // case 2, found the key but
            // the node associated with the key is a 2-node
            // replace it with its successor and
            // delete the successor with deleteMin( root ).
            // ---------->
            // Updated: delete the root iff original == root,
            // i.e. the node to delete isn't rotated to right
            if ( original === root &&
                this.compareKeys( root, key ) === 0 ) {
                this.deletedNode = new MapTreeNode( { ID: root.ID, key: root.key, val: root.val } );
                // replace the node with its successor
                root.replace( this.minRecur( root.right ) );
                // delete the successor
                root.right = this.deleteMinRecur( root.right );
            }
            // the key may be in the right subtree,
            else root.right = this.deleteRecur( root.right, key );
        }

        // update size and restore this R-B tree
        return this.balance( root );
    }

    deleteAndGetVal( key ) {
        this.deletedNode = null;
        this.delete( key );
        return this.deletedNode == null ? null : this.deletedNode.val;
    }
}
