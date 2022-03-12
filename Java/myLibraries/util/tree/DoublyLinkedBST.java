package myLibraries.util.tree;

/*
 * DoublyLinkedBST.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/27/2022$
 */

import myLibraries.util.MyLinkedList;
import myLibraries.util.tree.elements.MapTreeNode;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Data structure of Doubly Linked BST
 * This BST has thw following two special features compared to normal BST:
 * 1) every child has a pointer pointing to its parent. i.e. parent <-> child, not parent -> child.
 * Strictly speaking, note that this BST is not a tree anymore, but a connected graph.
 * 2) this BST maintains an ordered linked list in order to get predecessor and successor in O(1).
 * So the order of the list is the same as that of this BST.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public class DoublyLinkedBST<K, V> extends BinarySearchTree<K, V>
        implements DoublyLinkedTree<K, V>{

    /**
     * constructs to create an instance of DoublyLinkedRBT
     * */

    public DoublyLinkedBST( Comparator<K> comparator ) {
        super( comparator );
    }

    public DoublyLinkedBST() {
        this( null );
    }

    /**
     * put key -> val into this BST
     * */

    public void put( K key, V val ) {
        if ( isNull( key, val ) ) return;

        root = put( null, root, key, val, true );
        assert check();
    }

    private MapTreeNode<K, V> put( MapTreeNode<K, V> parent, MapTreeNode<K, V> root,
                                   K key, V val, boolean isLeft ) {
        // base case, attach the new node to this position
        if ( root == null ) {
            MapTreeNode<K, V> node = new MapTreeNode<K, V>( ID++, key, val );
            // add this node to the linked list
            if ( isLeft ) linkedList.addBefore( parent == null ? null : parent.node, node.node );
            else linkedList.addAfter( parent.node, node.node );
            return node;
        }

        int res = compareKeys( root, key );
        // doubly-connected node, root <-> child.
        // note that we have a connected graph, instead of a tree

        // the node should be attached in the left subtree
        if ( res > 0 ) {
            root.left =  put( root, root.left, key, val, true );
            root.left.parent = root;
        }
        // the node should be attached in the right subtree
        else if ( res < 0 ) {
            root.right = put( root, root.right, key, val, false );
            root.right.parent = root;
        }
        // added before, update value
        else root.val = val;

        return updateSize( root );
    }

    //-------------------------------------------------------
    // delete methods
    //-------------------------------------------------------

    /**
     * delete the minimum key -> value in this BST
     * */

    public void deleteMin() {
        // remove the first node in the linked list.
        linkedList.poll();
        deletedMinNode = null;

        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( isEmpty() ) return;

        root = deleteMin( root );
        assert check();
    }

    private MapTreeNode<K, V> deleteMin( MapTreeNode<K, V> root ) {
        // base case, this node is the least one in the tree
        // attach its right subtree to its father
        if ( root.left == null ) {
            deletedMinNode = root;
            return root.right;
        }

        // otherwise, look into the left subtree
        root.left = deleteMin( root.left );
        // restore doubly-connected node, root <-> child.
        if ( root.left != null ) root.left.parent = root;
        return updateSize( root );
    }

    /**
     * delete the maximum key -> value in this BST
     * */

    public void deleteMax() {
        // remove the last node in the linked list.
        linkedList.pollLast();
        deletedMaxNode = null;

        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( isEmpty() ) return;

        root = deleteMax( root );
        assert check();
    }

    private MapTreeNode<K, V> deleteMax( MapTreeNode<K, V> root ) {
        // base case, this node is the greatest one in the tree
        // attach its left subtree to its father
        if ( root.right == null ) {
            deletedMaxNode = root;
            return root.left;
        }

        // otherwise, look into the right subtree
        root.right = deleteMin( root.right );
        // restore doubly-connected node, root <-> child.
        if ( root.right != null ) root.right.parent = root;
        return updateSize( root );
    }

    /**
     * delete the key -> value in this BST
     * */

    public void delete( K key ) {
        deletedNode = null;
        root = delete( root, key );

        // remove the node in the linked list.
        if ( deletedNode != null ) linkedList.remove( deletedNode.node );
        assert check();

    }

    private MapTreeNode<K, V> delete( MapTreeNode<K, V> root, K key ) {
        // base case, not found the key
        if ( root == null ) return null;

        int res = compareKeys( root, key );
        // the key may be in the left subtree
        if ( res > 0 ) {
            root.left = delete( root.left, key );
            // restore doubly-connected node, root <-> child.
            if ( root.left != null ) root.left.parent = root;
        }
        // the key may be in the right subtree
        else if ( res < 0 ) {
            root.right = delete( root.right, key );
            // restore doubly-connected node, root <-> child.
            if ( root.right != null ) root.right.parent = root;
        }
        // found the key
        else {
            // case 1 or 2, have only one child,
            // either left child or right child
            // just make this child attach to its grandfather
            if ( root.right == null ) {
                deletedNode = root;
                return root.left;
            }
            if ( root.left == null ) {
                deletedNode = root;
                return root.right;
            }

            // case 3, have two children,
            // replace the value of this node with that of its successor,
            // the one that greater than the node
            // but is the minimum among all the successors of the node
            // and delete the successor,
            // i.e deleteMin( theNode.right )
            MapTreeNode<K, V> temp = deletedNode = root;
            root = min( temp.right );
            root.right = deleteMin( temp.right );
            // restore doubly-connected node, root <-> child.
            if ( root.right != null ) root.right.parent = root;
            root.left = temp.left;
            // restore doubly-connected node, root <-> child.
            if ( root.left != null ) root.left.parent = root;
        }

        return updateSize( root );
    }

    //-------------------------------------------------------
    // Check integrity of BST data structure.
    //-------------------------------------------------------

    protected boolean check() {
        boolean isBST = super.isBST();
        boolean isDoublyConnected = isDoublyConnected();
        boolean isLinked = isLinked();

        if ( !isBST )            System.err.println( "Not in symmetric order" );
        if ( !isDoublyConnected )       System.err.println( "Not doublyConnected" );
        if ( !isLinked )       System.err.println( "Not Linked" );

        return isBST && isDoublyConnected && isLinked;
    }
}
