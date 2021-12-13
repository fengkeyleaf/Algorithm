"use strict"

/*
 * BinarySearchTree.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import MapTreeNode from "./elements/MapTreeNode.js";
import CompareElement from "../CompareElement.js";

/**
 * Data structure of Binary Search Tree
 * with mapping tree node
 *
 * Note that in order to avoid errors, either a Comparator<K> is provided
 * or the key, K, implements Comparable<K>
 *
 * Reference resource: https://algs4.cs.princeton.edu/home/
 * or Algorithms 4th Edition in Chinese
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/tree/BinarySearchTree.java>BinarySearchTree</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class BinarySearchTree {
    static ID = 0;

    /**
     * constructs to create an instance of BinarySearchTree
     * */

    constructor( comparator ) {
        // comparator to compare key, K
        this.comparator = comparator || null;
        this.root = null;
        // returned tree nodes
        this.getNode = null;
        this.deletedNode = null;
        this.deletedMinNode = null;

    }

    /**
     * inorderPrint
     * */

    static inorderPrint( root ) {
        if ( root == null ) return;
        this.inorderPrint( root.left );
        console.log( root + " " );
        this.inorderPrint( root.right );
    }

    /**
     * print this BST in inorder
     * */

    inorderPrintStart() {
        BinarySearchTree.inorderPrint( this.root );
        console.log( "\n" );
    }

    /**
     * size of this BST
     * */

    size() {
        return this.sizeRecur( this.root );
    }

    /**
     * count how many children this root has
     * */

    sizeRecur( root ) {
        if ( root == null ) return 0;
        return root.numberOfChildren;
    }

    /**
     * is this tree empty?
     * */

    isEmpty() {
        return this.size() === 0;
    }

    /**
     * update the number of children
     * that the root has, including itself.
     * */

    updateSize( root ) {
        root.numberOfChildren = this.sizeRecur( root.left ) + this.sizeRecur( root.right ) + 1;
        return root;
    }

    /**
     * compare keys using Comparable<K> or Comparator<K>
     * */

    compareKeys( root, key ) {
        return CompareElement.compare( this.comparator, root.key, key );
    }

    /**
     * get the value associated with the key
     * */

    get( key ) {
        this.getNode = null;
        return this.getRecur( this.root, key );
    }

    // TODO: 5/29/2021 cache,
    //  store the most frequently pull-outed key in a variable
    getRecur( root, key ) {
        // base case, not found the key
        if ( root == null ) return null;

        let res = this.compareKeys( root, key );
        // the key may be in the left subtree
        if ( res > 0 ) return this.getRecur( root.left, key );
        // the key may be in the right subtree
        else if ( res < 0 ) return this.getRecur( root.right, key );

        // found the key, return the value and the node
        this.getNode = root;
        return root.val;
    }

    /**
     * put key -> val into this BST
     * */

    put( key, val ) {
        this.root = this.putRecur( this.root, key, val );
    }

    putRecur( root, key, val ) {
        // base case, attach the new node to this position
        if ( root == null ) return new MapTreeNode( { ID: BinarySearchTree.ID++, key: key, val: val } );

        let res = this.compareKeys( root, key );
        // the node should be attached in the left subtree
        if ( res > 0 ) root.left = this.putRecur( root.left, key, val );
        // the node should be attached in the right subtree
        else if ( res < 0 ) root.right = this.putRecur( root.right, key, val );
        // added before, update value
        else root.val = val;

        return this.updateSize( root );
    }

    /**
     * get the minimum key -> value in this BST
     * */

    min() {
        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        return this.isEmpty() ? null : this.minRecur( this.root ).key;
    }

    minRecur( root ) {
        if ( root.left == null ) return root;
        return this.minRecur( root.left );
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

    lowerKey( key ) {
        let res = this.lowerRecur( this.root, key );
        return res == null ? null : res.key;
    }

    lowerVal( key ) {
        let res = this.lowerRecur( this.root, key );
        return res == null ? null : res.val;
    }

    /**
     * Returns the greatest element in this set
     * less than or equal to the given element,
     * or null if there is no such element.
     *
     * get the key's Predecessor or its val in this BST
     * */

    floor( key ) {
        return this.floorRecur( this.root, key );
    }

    floorRecur( root, key ) {
        // not found
        if ( root == null ) return null;

        let res = this.compareKeys( root, key );
        // found equal
        if ( res === 0 ) return root;
        // less key must be in the left subtree
        if ( res > 0 ) return this.floorRecur( root.left, key );
        // less key may be this root's,
        // but there may be greater one then the root's key
        // in the right subtree
        let node = this.floorRecur( root.right, key );
        return node == null ? root : node;
    }

    floorKey( key ) {
        let res = this.floorRecur( this.root, key );
        return res == null ? null : res.key;
    }

    floorVal( key ) {
        let res = this.floorRecur( this.root, key );
        return res == null ? null : res.val;
    }

    /**
     * get the maximum key -> value in this BST
     * */

    max() {
        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        return this.isEmpty() ? null : this.maxRecur( this.root ).key;
    }

    maxRecur( root ) {
        if ( root.right == null ) return root;
        return this.minRecur( root.right );
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

    higherKey( key ) {
        let res = this.higherRecur( this.root, key );
        return res == null ? null : res.key;
    }

    higherVal( key ) {
        let res = this.higherRecur( this.root, key );
        return res == null ? null : res.val;
    }

    /**
     * Returns the least element in this set
     * greater than or equal to the given element,
     * or null if there is no such element.
     *
     * get the key's Successor or its val in this BST
     * */

    ceiling( key ) {
        return this.ceilingRecur( this.root, key );
    }

    ceilingRecur( root, key ) {
        // not found
        if ( root == null ) return null;

        let res = this.compareKeys( root, key );
        // found equal
        if ( res === 0 ) return root;
        // greater key must be in the right subtree
        if ( res < 0 ) return this.ceilingRecur( root.right, key );
        // greater key may be this root's,
        // but there may be less key then the root's key
        // in the left subtree
        let node = this.ceilingRecur( root.left, key );
        return node == null ? root : node;
    }

    ceilingKey( key ) {
        let res = this.ceilingRecur( this.root, key );
        return res == null ? null : res.key;
    }

    ceilingVal( key ) {
        let res = this.ceilingRecur( this.root, key );
        return res == null ? null : res.val;
    }

    /**
     * delete the minimum key -> value in this BST
     * */

    // TODO: 5/29/2021 return the deleted min val in O(1)
    deleteMin() {
        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( this.isEmpty() ) return;

        this.root = this.deleteMinRecur( this.root );
    }

    deleteMinRecur( root ) {
        // base case, this node is the least one in the tree
        // attach its right subtree to its father
        if ( root.left == null ) return root.right;

        // otherwise, look into the left subtree
        root.left = this.deleteMinRecur( root.left );
        return this.updateSize( root );
    }

    /**
     * delete the maximum key -> value in this BST
     * */

    // TODO: 5/29/2021 return the deleted max val in O(1)
    deleteMax() {
        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( this.isEmpty() ) return;

        this.root = this.deleteMaxRecur( this.root );
    }

    deleteMaxRecur( root ) {
        // base case, this node is the greatest one in the tree
        // attach its left subtree to its father
        if ( root.right == null ) return root.left;

        // otherwise, look into the right subtree
        root.right = this.deleteMinRecur( root.right );
        return this.updateSize( root );
    }

    /**
     * delete the key -> value in this BST
     * */

    // TODO: 5/29/2021 return the deleted val in O(1)
    delete( key ) {
        this.root = this.deleteRecur( this.root, key );
    }

    deleteRecur( root, key ) {
        // base case, not found the key
        if ( root == null ) return null;

        let res = this.compareKeys( root, key );
        // the key may be in the left subtree
        if ( res > 0 ) root.left = this.deleteRecur( root.left, key );
        // the key may be in the right subtree
        else if ( res < 0 ) root.right = this.deleteRecur( root.right, key );
        // found the key
        else {
            // case 1 or 2, have only one child,
            // either left child or right child
            // just make this child attach to its grandfather
            if ( root.right == null ) return root.left;
            if ( root.left == null ) return root.right;

            // case 3, have two children,
            // replace the value of this node with that of its successor,
            // the one that greater than the node
            // but is the minimum among all the successors of the node
            // and delete the successor,
            // i.e deleteMin( theNode.right )
            let temp = root;
            root = this.minRecur( temp.right );
            root.right = this.deleteMinRecur( temp.right );
            root.left = temp.left;
        }

        return this.updateSize( root );
    }

    inorder( root, text ) {
        if ( root == null ) return;
        this.inorder( root.left, text );
        text.append( root ).append( ", " );
        this.inorder( root.right, text );
    }

    toString() {
        let text = "[";
        this.inorder( this.root, text );
        return text.append( "]" ).toString();
    }
}