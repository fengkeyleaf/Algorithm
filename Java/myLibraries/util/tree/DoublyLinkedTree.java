package myLibraries.util.tree;

/*
 * DoublyLinkedTree.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/28/2022$
 */

import myLibraries.util.tree.elements.MapTreeNode;

/**
 * common methods that doubly-linked tree holds.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public interface DoublyLinkedTree<K, V> {

    /**
     * find node's predecessor. Assume that the node is already in this BST.
     * Note that this method doesn't compare keys to find the predecessor,
     * so is different from lower();.
     *
     * Ordered linked list is the key to implementing this method.
     * */

    default MapTreeNode<K, V> predecessor( MapTreeNode<K, V> node ) {
        if ( node == null ) return null;

        return node.node.getPrev() == null ? null : node.node.getPrev().getData();
    }

    /**
     * find node's successor. Assume that the node is already in this BST.
     * Note that this method doesn't compare keys to find the successor,
     * so is different from higher();.
     *
     * Ordered linked list is the key to implementing this method.
     * */

    default MapTreeNode<K, V> successor( MapTreeNode<K, V> node ) {
        if ( node == null ) return null;

        return node.node.getNext() == null ? null : node.node.getNext().getData();
    }
}
