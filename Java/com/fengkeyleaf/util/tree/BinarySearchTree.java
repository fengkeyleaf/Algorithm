package com.fengkeyleaf.util.tree;

/*
 * BinarySearchTree.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.util.CompareElement;
import com.fengkeyleaf.util.MyLinkedList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Data structure of Binary Search Tree
 * with mapping tree node
 *
 * Note that in order to avoid errors, either a Comparator<K> is provided
 * or the key, K, implements Comparable<K>
 *
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// TODO: 12/30/2021 implement rank(), select() and keys()
// Reference resource: https://algs4.cs.princeton.edu/home/
public class BinarySearchTree<K, V> extends AbstractTree<K>
        implements Iterable<MapTreeNode<K, V>> {

    protected MapTreeNode<K, V> root;
    // comparator to compare key, K
    protected final Comparator<K> comparator;
    // returned tree nodes
    protected MapTreeNode<K, V> getNode;
    protected MapTreeNode<K, V> deletedNode;
    protected MapTreeNode<K, V> deletedMinNode;
    protected MapTreeNode<K, V> deletedMaxNode;

    // linked list storing tree nodes in the order of this BST
    // doubly-linked tree needs.
    protected final MyLinkedList<MapTreeNode<K, V>> linkedList = new MyLinkedList<>();

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

    //-------------------------------------------------------
    // get method
    //-------------------------------------------------------

    /**
     * get the node associated with the key
     * */

    public MapTreeNode<K, V> getNode( K key ) {
        get( key );
        return getNode;
    }

    /**
     * get the value associated with the key
     * */
    
    public V get( K key ) {
        if ( key == null )
            throw new IllegalArgumentException( "key in get() is null" );

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

    protected boolean isNull( K key, V val ) {
        if ( key == null ) throw new IllegalArgumentException( "first argument to put() is null" );
        if ( val == null ) {
            delete( key );
            return true;
        }

        return false;
    }

    //-------------------------------------------------------
    // put method
    //-------------------------------------------------------

    /**
     * put key -> val into this BST
     * */

    public void put( K key, V val ) {
        if ( isNull( key, val ) ) return;

        root = put( root, key, val );
        assert check();
    }

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

    //-------------------------------------------------------
    // ordering query methods
    //-------------------------------------------------------

    /**
     * get the minimum key -> value in this BST
     * */

    public K min() {
        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        return isEmpty() ? null : min( root ).key;
    }

    // minRecur in javascript version
    public MapTreeNode<K, V> min( MapTreeNode<K, V> root ) {
        if ( root == null ) return null;

        if ( root.left == null ) return root;
        return min( root.left );
    }

    /**
     * Returns the greatest element in this set
     * less than or equal to the given element,
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

    public MapTreeNode<K, V> max( MapTreeNode<K, V> root ) {
        if ( root == null ) return null;

        if ( root.right == null ) return root;
        return max( root.right );
    }

    /**
     * Returns the least element in this set
     * greater than or equal to the given element,
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

    //-------------------------------------------------------
    // delete methods
    //-------------------------------------------------------

    /**
     * delete the minimum key -> value in this BST
     * */

    public void deleteMin() {
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
        return updateSize( root );
    }

    /**
     * delete the maximum key -> value in this BST
     * */

    public void deleteMax() {
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
        return updateSize( root );
    }

    /**
     * delete the key -> value in this BST
     * */

    public void delete( K key ) {
        deletedNode = null;
        root = delete( root, key );
        assert check();
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
            deletedNode = new MapTreeNode<>( root.key, root.val );
            MapTreeNode<K, V> temp = root;
            root = min( temp.right );
            root.right = deleteMin( temp.right );
            root.left = temp.left;
        }

        return updateSize( root );
    }

    //-------------------------------------------------------
    // toString methods
    //-------------------------------------------------------

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

    //-------------------------------------------------------
    // Iterable & traversal
    //-------------------------------------------------------

    @Override
    public Iterator<MapTreeNode<K, V>> iterator() {
        final LinkedList<MapTreeNode<K, V>> queue = new LinkedList<>();
        inorderTraversal( root, queue );
        return queue.listIterator();
    }

    private void inorderTraversal( MapTreeNode<K, V> root,
                                   LinkedList<MapTreeNode<K, V>> queue ) {
        if ( root == null ) return;

        inorderTraversal( root.left, queue );
        queue.add( root );
        inorderTraversal( root.right, queue );
    }

    /**
     * inorder traversal with O(1) space, Morris Traversal.
     * But we could take advantage of parent pointer in {@link DoublyLinkedBST} or {@link DoublyLinkedRBT}.
     * */

    // reference resource: https://www.cnblogs.com/AnnieKim/archive/2013/06/15/morristraversal.html
    LinkedList<MapTreeNode<K, V>> morrisInOrderTraversal() {
        LinkedList<MapTreeNode<K, V>> L = new LinkedList<>();
        MapTreeNode<K, V> cur = root;

        // traversal process.
        while ( cur != null ) {
            if ( cur.left == null ) {
                L.add( cur );
                cur = cur.right;
                continue;
            }

            MapTreeNode<K, V> p = max( cur.left, cur );
            // backup the node we'll return to.
            if ( p.right == null ) {
                p.right = cur;
                cur = cur.left;
                continue;
            }

            // recover the tree structure.
            if ( p.right == cur ) {
                p.right = null;
                L.add( cur );
                cur = cur.right;
            }
        }

        return L;
    }

    private MapTreeNode<K, V> max( MapTreeNode<K, V> left,
                                   MapTreeNode<K, V> cur ) {

        while ( left.right != null && left.right != cur )
            left = left.right;

        return left;
    }

    //-------------------------------------------------------
    // Check integrity of BST data structure.
    //-------------------------------------------------------

    protected boolean check() {
        boolean isBST = isBST();
        boolean isSizeConsistent = isSizeConsistent();

        if ( !isBST )            System.err.println( "Not in symmetric order" );
        if ( !isSizeConsistent ) System.err.println( "Subtree counts not consistent" );
//        if (!isRankConsistent()) System.err.println( "Ranks not consistent" );

//        return isBST() && isSizeConsistent() && isRankConsistent();
        return isBST && isSizeConsistent;
    }

    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since order is strict
    protected boolean isBST() {
        return isBST( root, null, null );
    }

    // is the tree rooted at x a BST with all keys strictly between min and max
    // (if min or max is null, treat as empty constraint)
    // Credit: Bob Dondero's elegant solution
    private boolean isBST( MapTreeNode<K, V> x, K min, K max ) {
        if ( x == null ) return true;

        if ( min != null && compareKeys( x, min ) <= 0 ) {
            System.err.println( x );
            return false;
        }
        if ( max != null && compareKeys( x, max ) >= 0 ) {
            System.err.println( x );
            return false;
        }

        return isBST( x.left, min, x.key ) &&
                isBST( x.right, x.key, max );
    }

    // are the size fields correct?
    protected boolean isSizeConsistent() { return isSizeConsistent( root ); }
    private boolean isSizeConsistent( MapTreeNode<K, V> x ) {
        if ( x == null ) return true;
        if ( x.numberOfChildren != size( x.left ) + size( x.right ) + 1 ) return false;
        return isSizeConsistent( x.left) && isSizeConsistent( x.right );
    }

//    // check that ranks are consistent
//    private boolean isRankConsistent() {
//        for (int i = 0; i < size(); i++)
//            if (i != rank(select(i))) return false;
//        for (Key key : keys())
//            if (key.compareTo(select(rank(key))) != 0) return false;
//        return true;
//    }

    // doubly-linked tree needs.
    protected boolean isDoublyConnected() {
        return isDoublyConnected( root );
    }

    private boolean isDoublyConnected( MapTreeNode<K, V> root ) {
        if ( root == null ) return true;

        if ( root.left != null && root.left.parent != root ) {
            System.err.println( root + " -> left: " + root.left + "( parent: " + root.left.parent + " )" );
            return false;
        }
        if ( root.right != null && root.right.parent != root ) {
            System.err.println( root + " -> right: " + root.right + "( parent: " + root.right.parent + " )" );
            return false;
        }

        return isDoublyConnected( root.left ) && isDoublyConnected( root.right );
    }

    // doubly-linked tree needs.
    protected boolean isLinked() {
        Iterator<MapTreeNode<K, V>> treeQueue = iterator();
        Iterator<MapTreeNode<K, V>> list = linkedList.iterator();

        // next() - Throws:
        // NoSuchElementException - if the iteration has no more elements
        while ( treeQueue.hasNext() ) {
            MapTreeNode<K, V> treeNode = treeQueue.next();
            MapTreeNode<K, V> listNode = list.next();
            if ( treeNode != listNode ) {
                System.err.println( treeNode + " " + listNode );
                System.err.println( this );
                System.err.println( linkedList );
                return false;
            }
        }

        // no more elements for both iterators.
        // treeQueue is so for sure, but list is not.
        return !list.hasNext();
    }
}