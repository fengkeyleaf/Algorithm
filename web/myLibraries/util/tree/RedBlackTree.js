"use strict"

/*
 * RedBlackTree.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import BinarySearchTree from "./BinarySearchTree.js";
import RedBlackTreeNode from "./elements/RedBlackTreeNode.js";
import MapTreeNode from "./elements/MapTreeNode.js";

/**
 * Data structure of Red Black Tree
 * with mapping tree node
 *
 * Note that in order to avoid errors, either a Comparator<K> is provided
 * or the key, K, implements Comparable<K>
 *
 * Reference resource: https://algs4.cs.princeton.edu/home/
 * or Algorithms 4th Edition in Chinese
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/tree/RedBlackTree.java>RedBlackTree</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class RedBlackTree extends BinarySearchTree {

    /**
     * constructs to create an instance of RedBlackTree
     * */

    constructor( comparator ) {
        super( comparator );
    }


    /**
     * a node is red?
     * */

    isRed( node ) {
        return node != null && node.color === RedBlackTreeNode.Color.RED;
    }

    /**
     * common part for both rotateLeft and rotateRight
     * */

    rotate( root, temp ) {
        temp.color = root.color;
        root.color = RedBlackTreeNode.Color.RED;
        temp.numberOfChildren = root.numberOfChildren;
        this.updateSize( root );
    }

    /**
     * rotate Left
     * */

    rotateLeft( root ) {
        let temp = root.right;
        root.right = temp.left;
        temp.left = root;
        this.rotate( root, temp );
        return temp;
    }

    /**
     * rotate Right
     * */

    rotateRight( root ) {
        let temp = root.left;
        root.left = temp.right;
        temp.right = root;
        this.rotate( root, temp );
        return temp;
    }

    /**
     * flip Colors
     *
     * @param reverse   true - set root's color to BLACK,
     *                  children RED ( up-down combing ),
     *                  false - set root's color to RED,
     *                  children BLACK ( bottom-up restoring )
     * */

    flipColors( root, reverse ) {
        console.assert( root.left != null && root.right != null );
        root.color = reverse ? RedBlackTreeNode.Color.BLACK : RedBlackTreeNode.Color.RED;
        root.left.color = reverse ? RedBlackTreeNode.Color.RED : RedBlackTreeNode.Color.BLACK;
        root.right.color = reverse ? RedBlackTreeNode.Color.RED : RedBlackTreeNode.Color.BLACK;
    }

    /**
     * Balance Case One:
     * root's left child is RED and
     * the right child is BLACK
     * */

    ifBalanceCaseOne( root ) {
        return this.isRed( root.right ) && !this.isRed( root.left );
    }

    /**
     * Balance Case Two:
     * root's left child is RED
     * and the left child's left child is RED
     * */

    ifBalanceCaseTwo( root ) {
        return this.isRed( root.left ) && this.isRed( root.left.left );
    }

    /**
     * Balance Case Three:
     * root's left child is BLACK and
     * the right child is RED
     * */

    ifBalanceCaseThree( root ) {
        return this.isRed( root.left ) && this.isRed( root.right );
    }

    /**
     * balance this R-B tree
     * */

    balance( root ) {
        if ( this.ifBalanceCaseOne( root ) ) root = this.rotateLeft( root );
        if ( this.ifBalanceCaseTwo( root ) ) root = this.rotateRight( root );
        if ( this.ifBalanceCaseThree( root ) ) this.flipColors( root, false );

        return this.updateSize( root );
    }

    /**
     * put key -> val into this R-B tree
     * */

    put( key, val ) {
        let root = this.putRecur( this.root, key, val );
        root.color = RedBlackTreeNode.Color.BLACK;
        this.root = root;
    }

    // this part of code is very similar to
    // put( MapTreeNode<K, V> root, K key, V val ).
    putRecur( root, key, val ) {
        // base case, attach the new node to this position
        if ( root == null ) return new RedBlackTreeNode( {
            ID: BinarySearchTree.ID++,
            key: key,
            val: val,
            color: RedBlackTreeNode.Color.RED
        } );

        let res = this.compareKeys( root, key );
        // the node should be attached in the left subtree
        if ( res > 0 ) root.left = this.putRecur( root.left, key, val );
        // the node should be attached in the right subtree
        else if ( res < 0 ) root.right = this.putRecur( root.right, key, val );
        // added before, update value
        else root.val = val;

        // update size and restore this R-B tree
        return this.balance( root );
    }

    /**
     * update the root after deleting
     * */

    updateRootForDelete( root ) {
        this.root = root;
        // !isEmpty() is equivalent to root != null
        if ( !this.isEmpty() ) root.color = RedBlackTreeNode.Color.BLACK;
    }

    /**
     * delete the minimum key -> value in this R-B tree
     * */

    deleteMin() {
        // the following commented out code is from the textbook,
        // but from my point of view, they're redundant.
        if ( !this.isRed( this.root.left ) &&
            !this.isRed( this.root.right ) )
            this.root.color = RedBlackTreeNode.Color.RED;

        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( this.isEmpty() ) return;

        // delete the node and update the root
        this.updateRootForDelete( this.deleteMinRecur( this.root ) );
    }

    deleteMinRecur( root ) {
        // base case, this node is the least one in the tree
        // and it's also a leaf node in this R-B tree,
        // so just return null, instead of return root.right,
        // which is different from deleteMin() for BST.
        if ( root.left == null ) {
            this.deletedMinNode = root;
            console.assert( root.right == null );
            return null;
        }

        // guarantee that every node
        // we're traveling along left subtree
        // is either 3-node or 4-node.
        // !isRed( root.left.left ) is to
        // differentiate case 4 and case 7
        if ( !this.isRed( root.left ) &&
            !this.isRed( root.left.left ) )
            root = this.moveRedLeft( root );

        // otherwise, look into the left subtree
        root.left = this.deleteMinRecur( root.left );
        return this.balance( root );
    }

    /**
     * delete the minimum key -> value in this R-B tree,
     * and get the value
     * */

    deleteMinAndGetVal() {
        this.deletedMinNode = null;
        this.deleteMin();
        return this.deletedMinNode == null ? null : this.deletedMinNode.val;
    }

    moveRedLeft( root ) {
        this.flipColors( root, true );

        if ( this.isRed( root.right.left ) ) {
            // handle case 6
            root.right = this.rotateRight( root.right );
            root = this.rotateLeft( root );
            this.flipColors( root, false ); // RedBlackTree.flipColors(RedBlackTree.java:229) nullPointerException
        }

        return root;
    }

    /**
     * delete the maximum key -> value in this R-B tree
     * */

// TODO: 5/29/2021 return the deleted max val in O(1)
    deleteMax() {
        // the following commented out code is from the textbook,
        // but from my point of view, they're redundant
        if ( !this.isRed( this.root.left ) &&
            !this.isRed( this.root.right ) )
            this.root.color = RedBlackTreeNode.Color.RED;

        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( this.isEmpty() ) return;

        // delete the node and update the root
        this.updateRootForDelete( this.deleteMaxRecur( this.root ) );
    }

    deleteMaxRecur( root ) {
        // handle case 2
        if ( this.isRed( root.left ) )
            root = this.rotateRight( root );

        // base case, this node is the greatest one in the tree
        // and it's also a leaf node in this R-B tree,
        // so just return null, instead of return root.left,
        // which is different from deleteMax() for BST
        if ( root.right == null ) {
            console.assert( root.left == null );
            return null;
        }

        // guarantee that every node
        // we're traveling along right subtree
        // is either 3-node or 4-node.
        // differentiate case 4 and case 5,
        // and handle case 5 more efficiently
        if ( !this.isRed( root.right ) &&
            !this.isRed( root.right.left ) )
            root = this.moveRedRight( root );

        // otherwise, look into the right subtree
        root.right = this.deleteMaxRecur( root.right );
        return this.balance( root );
    }

    moveRedRight( root ) {
        this.flipColors( root, true );

        // handle case 4 more efficiently,
        // since at this point, there is an extra red node on the left,
        // we could move it to the right part of the tree
        // but different from the code in the textbook,
        // which is: !isRed( root.left.left )
        if ( this.isRed( root.left.left ) ) {
            root = this.rotateRight( root );
            this.flipColors( root, false );
        }

        return root;
    }

    /**
     * delete the key -> value in this R-B tree
     * */

    delete( key ) {
        // the following commented out code is from the textbook,
        // but from my point of view, they're redundant
        if ( !this.isRed( this.root.left ) &&
            !this.isRed( this.root.right ) )
            this.root.color = RedBlackTreeNode.Color.RED;

        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( this.isEmpty() ) return;

        // delete the node and update the root
        this.updateRootForDelete( this.deleteRecur( this.root, key ) );
    }

// Note that this recursive method is a bit unique from usual ones,
// since the base case for this method is not at the beginning of code
    deleteRecur( root, key ) {
        // why not use the commented-out code to compare keys?
        // because if tree was rotated, the root has changed,
        // i.e. not the previous one we computed for the variable, res,
        // so we need to recompute their comparing order.
//        int res = compareKeys( root, key );

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
            // this part of code is very similar to
            // deleteMax( RedBlackTreeNode root ).
            // starts at here ---->
            if ( this.isRed( root.left ) )
                root = this.rotateRight( root );

            // base case 2 and also case 1, found the key and the node
            // associated with the key is either a 3-node or 4-node
            // just delete it
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
                !this.isRed( root.right.left ) )
                root = this.moveRedRight( root );
            // ----> ends here

            // case 2, found the key but
            // the node associated with the key is a 2-node
            // replace it with its successor and
            // delete the successor with deleteMin( root ).
            if ( this.compareKeys( root, key ) === 0 ) {
                this.deletedNode = new MapTreeNode( { ID: root.ID, key: root.key, val: root.val } );
                // replace the node with its successor
                root.replace( this.minRecur( root.right ) );
                // delete the successor
                root.right = this.deleteMinRecur( root.right );

                // the following commented-out code is from the textbook,
                // which is a little bit complex, not as simple as possible
//                root.val = get( root.right, min( root.right ).key );
//                root.key = min( root.right ).key;
//                root.right = deleteMin( ( RedBlackTreeNode ) root.right );
            }
            // the key may be in the right subtree,
            else root.right = this.deleteRecur( root.right, key );
        }

        // update size and restore this R-B tree
        return this.balance( root );
    }

    /**
     * delete the key -> value in this R-B tree,
     * and get the value
     * */

    deleteAndGetVal( key ) {
        this.deletedNode = null;
        this.delete( key );
        return this.deletedNode == null ? null : this.deletedNode.val;
    }
}
