package myLibraries.util.tree.elements;

/*
 * RBTreeNode.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/5/2022$
 */

/**
 * Data structure of Red Black Tree Node
 *
 * This class was originally inner class, but I made it not,
 * since we want to get access to this node.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * */

public class RBTreeNode<K, V> extends MapTreeNode<K, V> {

    public boolean color;

    /**
     * constructs to create an instance of Node
     * */

    public RBTreeNode( int ID, K key,
                       V val, boolean color ) {
        this( ID, null, null, null, key, val, color );
    }

    public RBTreeNode( int ID, RBTreeNode<K, V> parent,
                       K key, V val, boolean color ) {
        this( ID, parent, null, null, key, val, color );
    }

    public RBTreeNode( int ID, RBTreeNode<K, V> parent,
                       RBTreeNode<K, V> left, RBTreeNode<K, V> right,
                       K key, V val, boolean color ) {
        super( ID, parent, left, right, key, val );
        this.color = color;
    }

    public RBTreeNode( int ID, RBTreeNode<K, V> left,
                       RBTreeNode<K, V> right, K key, V val, boolean color ) {
        this( ID, null, left, right, key, val, color );
    }

    public RBTreeNode( RBTreeNode<K, V> node ) {
        this( node.ID, node.key, node.val, node.color );
    }

    private String toStringWithoutColor() {
        return "{" + key + "->" + val + "}";
    }

    private String toStringNormal() {
        return color + ":{" + key + "->" + val + "}";
    }

    @Override
    public String toString() {
        return toStringWithoutValue();
    }
}