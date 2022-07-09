package com.fengkeyleaf.util.tree;

/*
 * MapTreeNode.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.util.DoublyLinkedNode;
import com.fengkeyleaf.util.Node;

/**
 * Data structure of tree node with mapping, key -> value
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class MapTreeNode<K, V> extends Node {
    protected static int IDStatic = 0;
    public MapTreeNode<K, V> left;
    public MapTreeNode<K, V> right;
    public K key;
    public V val;
    // including this node itself
    public int numberOfChildren = 1;

    public final DoublyLinkedNode<MapTreeNode<K, V>> node;

    /**
     * constructs to create an instance of Node
     * */

    public MapTreeNode( int ID, K key, V val ) {
        this( ID, null, null, null, key, val );
    }

    public MapTreeNode( K key, V val ) {
        this( IDStatic++, null, null, null, key, val );
    }

    public MapTreeNode( int ID, MapTreeNode<K, V> parent,
                        K key, V val ) {
        this( ID, parent, null, null, key, val );
    }

    public MapTreeNode( int ID, MapTreeNode<K, V> left,
                        MapTreeNode<K, V> right, K key, V val ) {
        this( ID, null, left, right, key, val );
    }

    public MapTreeNode( int ID, MapTreeNode<K, V> parent,
                        MapTreeNode<K, V> left, MapTreeNode<K, V> right,
                        K key, V val ) {
        super( ID, parent );
        this.key = key;
        this.val = val;
        this.left = left;
        this.right = right;
        node = new DoublyLinkedNode<>( this );
    }

    public MapTreeNode( MapTreeNode<K, V> node ) {
        this( node.ID, node.key, node.val );
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeft() {
        // only MapTreeNode used in BST, so ignore the warning.
        return !isRoot() && ( ( MapTreeNode<K, V> ) parent ).left == this;
    }

    public boolean isRight() {
        // only MapTreeNode used in BST, so ignore the warning.
        return !isRoot() && ( ( MapTreeNode<K, V> ) parent ).right == this;
    }

    public static <K, V>
    void swap( MapTreeNode<K, V> node1, MapTreeNode<K, V> node2 ) {
        MapTreeNode<K, V> temp = new MapTreeNode<>( -1, node1.key, node1.val );
        node1.replace( node2 );
        node2.replace( temp );
    }

    /**
     * replace anther node at this node
     * */

    public void replace( MapTreeNode<K, V> node ) {
        this.val = node.val;
        this.key = node.key;
    }

    private String normalToString() {
        return ID + ":{" + key + "->" + val + "}";
    }

    private String toStringWithoutID() {
        return "{" + key + "->" + val + "}";
    }

    protected String toStringWithoutKey() {
        return String.valueOf( val );
    }

    protected String toStringWithoutValue() {
        return String.valueOf( key );
    }

    @Override
    public String toString() {
        return toStringWithoutValue();
    }
}
