package myLibraries.util.tree;

/*
 * BinarySearchTree.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.CompareElement;
import myLibraries.util.tree.elements.MapTreeNode;

import java.util.Comparator;

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
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class BinarySearchTree<K, V> extends AbstractTree<K> {
    protected MapTreeNode<K, V> root;
    // comparator to compare key, K
    protected final Comparator<K> comparator;
    // returned tree nodes
    protected MapTreeNode<K, V> getNode;
    protected MapTreeNode<K, V> deletedNode;
    protected MapTreeNode<K, V> deletedMinNode;

    /**
     * constructs to create an instance of BinarySearchTree
     * */

    public BinarySearchTree( Comparator<K> comparator ) {
        this.comparator = comparator;
    }

    public BinarySearchTree() {
        this( null );
    }

    /**
     * inorderPrint
     * */

    private static<K, V>
    void inorderPrint( MapTreeNode<K, V> root ) {
        if ( root == null ) return;
        inorderPrint( root.left );
        System.out.print( root + " " );
        inorderPrint( root.right );
    }

    /**
     * print this BST in inorder
     * */

    public void inorderPrint() {
        inorderPrint( root );
        System.out.println();
    }

    /**
     * size of this BST
     * */

    @Override
    public int size() {
        return size( root );
    }

    /**
     * count how many children this root has
     *
     * sizeRecur() in javascript version
     * */

    protected int size( MapTreeNode<K, V> root ) {
        if ( root == null ) return 0;
        return root.numberOfChildren;
    }

    /**
     * update the number of children
     * that the root has, including itself.
     * */

    protected MapTreeNode<K, V> updateSize( MapTreeNode<K, V> root ) {
        root.numberOfChildren = size( root.left ) + size( root.right ) + 1;
        return root;
    }

    /**
     * compare keys using Comparable<K> or Comparator<K>
     * */

    protected int compareKeys( MapTreeNode<K, V> root, K key ) {
        return CompareElement.compare( comparator, root.key, key );
    }

    /**
     * get the value associated with the key
     * */
    
    public V get( K key ) {
        getNode = null;
        return get( root, key );
    }

    // getRecur() in javascript version
    // TODO: 5/29/2021 cache,
    //  store the most frequently pull-outed key in a variable
    protected V get( MapTreeNode<K, V> root, K key ) {
        // base case, not found the key
        if ( root == null ) return null;

        int res = compareKeys( root, key );
        // the key may be in the left subtree
        if ( res > 0 ) return get( root.left, key );
        // the key may be in the right subtree
        else if ( res < 0 ) return get( root.right, key );

        // found the key, return the value and the node
        getNode = root;
        return root.val;
    }

    /**
     * put key -> val into this BST
     * */

    public void put( K key, V val ) {
        root = put( root, key, val );
    }

    // putRecur() in javascript version
    private MapTreeNode<K, V> put( MapTreeNode<K, V> root, K key, V val ) {
        // base case, attach the new node to this position
        if ( root == null ) return new MapTreeNode<K, V>( ID++, key, val );

        int res = compareKeys( root, key );
        // the node should be attached in the left subtree
        if ( res > 0 ) root.left =  put( root.left, key, val );
        // the node should be attached in the right subtree
        else if ( res < 0 ) root.right = put( root.right, key, val );
        // added before, update value
        else root.val = val;

        return updateSize( root );
    }

    /**
     * get the minimum key -> value in this BST
     * */

    public K min() {
        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        return isEmpty() ? null : min( root ).key;
    }

    // minRecur in javascript version
    protected MapTreeNode<K, V> min( MapTreeNode<K, V> root ) {
        if ( root.left == null ) return root;
        return min( root.left );
    }

    /**
     * Returns the least element in this set
     * strictly greater than the given element,
     * or null if there is no such element.
     *
     * get the key's Predecessor in this BST
     * */

    public MapTreeNode<K, V> lower( K key ) {
        return lower( root, key );
    }

    private MapTreeNode<K, V> lower( MapTreeNode<K, V> root, K key ) {
        // not found
        if ( root == null ) return null;

        int res = compareKeys( root, key );
        // not return even if found the equal,
        // less key must be in the left subtree
        // this is the major difference from floor()
        if ( res >= 0 ) return lower( root.left, key );
        // less key may be this root's,
        // but there may be greater one then the root's key
        // in the right subtree
        MapTreeNode<K, V> node = lower( root.right, key );
        return node == null ? root : node;
    }

    public K lowerKey( K key ) {
        MapTreeNode<K, V> res = lower( root, key );
        return res == null ? null : res.key;
    }

    public V lowerVal( K key ) {
        MapTreeNode<K, V> res = lower( root, key );
        return res == null ? null : res.val;
    }

    /**
     * Returns the greatest element in this set
     * less than or equal to the given element,
     * or null if there is no such element.
     *
     * get the key's Predecessor or its val in this BST
     * */

    public MapTreeNode<K, V> floor( K key ) {
        return floor( root, key );
    }

    private MapTreeNode<K, V> floor( MapTreeNode<K, V> root, K key ) {
        // not found
        if ( root == null ) return null;

        int res = compareKeys( root, key );
        // found equal
        if ( res == 0 ) return root;
        // less key must be in the left subtree
        if ( res > 0 ) return floor( root.left, key );
        // less key may be this root's,
        // but there may be greater one then the root's key
        // in the right subtree
        MapTreeNode<K, V> node = floor( root.right, key );
        return node == null ? root : node;
    }

    public K floorKey( K key ) {
        MapTreeNode<K, V> res = floor( root, key );
        return res == null ? null : res.key;
    }

    public V floorVal( K key ) {
        MapTreeNode<K, V> res = floor( root, key );
        return res == null ? null : res.val;
    }

    /**
     * get the maximum key -> value in this BST
     * */

    public K max() {
        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        return isEmpty() ? null : max( root ).key;
    }

    protected MapTreeNode<K, V> max( MapTreeNode<K, V> root ) {
        if ( root.right == null ) return root;
        return min( root.right );
    }

    /**
     * Returns the greatest element in this set
     * strictly less than the given element,
     * or null if there is no such element.
     *
     * get the key's Successor in this BST
     * */

    public MapTreeNode<K, V> higher( K key ) {
        return higher( root, key );
    }

    private MapTreeNode<K, V> higher( MapTreeNode<K, V> root, K key ) {
        // not found
        if ( root == null ) return null;

        int res = compareKeys( root, key );
        // not return even if found the equal,
        // greater key must be in the right subtree
        // this is the major difference from ceiling()
        if ( res <= 0 ) return higher( root.right, key );
        // greater key may be this root's,
        // but there may be less key then the root's key
        // in the left subtree
        MapTreeNode<K, V> node = higher( root.left, key );
        return node == null ? root : node;
    }

    public K higherKey( K key ) {
        MapTreeNode<K, V> res = higher( root, key );
        return res == null ? null : res.key;
    }

    public V higherVal( K key ) {
        MapTreeNode<K, V> res = higher( root, key );
        return res == null ? null : res.val;
    }

    /**
     * Returns the least element in this set
     * greater than or equal to the given element,
     * or null if there is no such element.
     *
     * get the key's Successor or its val in this BST
     * */

    public MapTreeNode<K, V> ceiling( K key ) {
        return ceiling( root, key );
    }

    private MapTreeNode<K, V> ceiling( MapTreeNode<K, V> root, K key ) {
        // not found
        if ( root == null ) return null;

        int res = compareKeys( root, key );
        // found equal
        if ( res == 0 ) return root;
        // greater key must be in the right subtree
        if ( res < 0 ) return ceiling( root.right, key );
        // greater key may be this root's,
        // but there may be less key then the root's key
        // in the left subtree
        MapTreeNode<K, V> node = ceiling( root.left, key );
        return node == null ? root : node;
    }

    public K ceilingKey( K key ) {
        MapTreeNode<K, V> res = ceiling( root, key );
        return res == null ? null : res.key;
    }

    public V ceilingVal( K key ) {
        MapTreeNode<K, V> res = ceiling( root, key );
        return res == null ? null : res.val;
    }

    /**
     * delete the minimum key -> value in this BST
     * */

    // TODO: 5/29/2021 return the deleted min val in O(1)
    public void deleteMin() {
        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( isEmpty() ) return;

        root = deleteMin( root );
    }

    private MapTreeNode<K, V> deleteMin( MapTreeNode<K, V> root ) {
        // base case, this node is the least one in the tree
        // attach its right subtree to its father
        if ( root.left == null ) return root.right;

        // otherwise, look into the left subtree
        root.left = deleteMin( root.left );
        return updateSize( root );
    }

    /**
     * delete the maximum key -> value in this BST
     * */

    // TODO: 5/29/2021 return the deleted max val in O(1)
    public void deleteMax() {
        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( isEmpty() ) return;

        root = deleteMax( root );
    }

    private MapTreeNode<K, V> deleteMax( MapTreeNode<K, V> root ) {
        // base case, this node is the greatest one in the tree
        // attach its left subtree to its father
        if ( root.right == null ) return root.left;

        // otherwise, look into the right subtree
        root.right = deleteMin( root.right );
        return updateSize( root );
    }

    /**
     * delete the key -> value in this BST
     * */

    // TODO: 5/29/2021 return the deleted val in O(1)
    public void delete( K key ) {
        root = delete( root, key );
    }

    private MapTreeNode<K, V> delete( MapTreeNode<K, V> root, K key ) {
        // base case, not found the key
        if ( root == null ) return null;

        int res = compareKeys( root, key );
        // the key may be in the left subtree
        if ( res > 0 ) root.left = delete( root.left, key );
        // the key may be in the right subtree
        else if ( res < 0 ) root.right = delete( root.right, key );
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
            MapTreeNode<K, V> temp = root;
            root = min( temp.right );
            root.right = deleteMin( temp.right );
            root.left = temp.left;
        }

        return updateSize( root );
    }

    private void inorder( MapTreeNode<K, V> root, StringBuilder text ) {
        if ( root == null ) return;
        inorder( root.left, text );
        text.append( root ).append( ", " );
        inorder( root.right, text );
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder( "[" );
        inorder( root, text );
        return text.append( "]" ).toString();
    }
}