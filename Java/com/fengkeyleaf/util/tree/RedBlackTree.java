package com.fengkeyleaf.util.tree;

/*
 * RedBlackTree.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import java.util.Comparator;

/**
 * Data structure of Red Black Tree
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

// Reference resource: https://algs4.cs.princeton.edu/home/
public class RedBlackTree<K, V> extends BinarySearchTree<K, V> {

    /**
     * node colors
     * */

    protected static final boolean RED   = true;
    protected static final boolean BLACK = false;

    /**
     * constructs to create an instance of RedBlackTree
     * */

    public RedBlackTree( Comparator<K> comparator ) {
        super( comparator );
    }

    public RedBlackTree() {
        this( null );
    }


    //-------------------------------------------------------
    // balancing operations
    //-------------------------------------------------------

    /**
     * a node is red?
     * */

    protected boolean isRed( MapTreeNode<K, V> node ) {
        return node != null && ( ( RBTreeNode<K, V> ) node ).color;
    }

    /**
     * common part for both rotateLeft and rotateRight
     * */

    protected void rotate( RBTreeNode<K, V>  root, RBTreeNode<K, V>  temp ) {
        temp.color = root.color;
        root.color = RED;
        temp.numberOfChildren = root.numberOfChildren;
        updateSize( root );
    }

    /**
     * rotate Left
     * */

    protected RBTreeNode<K, V>  rotateLeft( RBTreeNode<K, V>  root ) {
        RBTreeNode<K, V>  temp = ( RBTreeNode<K, V>  ) root.right;
        root.right = temp.left;
        temp.left = root;
        rotate( root, temp );
        return temp;
    }

    /**
     * rotate Right
     * */

    protected RBTreeNode<K, V>  rotateRight( RBTreeNode<K, V>  root ) {
        RBTreeNode<K, V>  temp = ( RBTreeNode<K, V>  ) root.left;
        root.left = temp.right;
        temp.right = root;
        rotate( root, temp );
        return temp;
    }

    /**
     * flip Colors
     * */

    protected void flipColors( RBTreeNode<K, V>  root ) {
        assert root.left != null && root.right != null;
        root.color = !root.color;
        ( ( RBTreeNode<K, V>  ) root.left ).color = !( ( RBTreeNode<K, V>  ) root.left ).color;
        ( ( RBTreeNode<K, V>  ) root.right ).color = !( ( RBTreeNode<K, V>  ) root.right ).color;
    }

    /**
     * Balance Case One:
     * root's left child is RED and
     * the right child is BLACK
     * */

    protected boolean ifBalanceCaseOne( RBTreeNode<K, V>  root ) {
        return isRed( root.right ) && !isRed( root.left );
    }

    /**
     * Balance Case Two:
     * root's left child is RED
     * and the left child's left child is RED
     * */

    protected boolean ifBalanceCaseTwo( RBTreeNode<K, V>  root ) {
        return isRed( root.left ) && isRed( root.left.left );
    }

    /**
     * Balance Case Three:
     * root's left child is BLACK and
     * the right child is RED
     * */

    protected boolean ifBalanceCaseThree( RBTreeNode<K, V>  root ) {
        return isRed( root.left ) && isRed( root.right );
    }

    /**
     * balance this R-B tree
     * */

    protected RBTreeNode<K, V>  balance( RBTreeNode<K, V>  root ) {
        if ( ifBalanceCaseOne( root ) ) root = rotateLeft( root );
        if ( ifBalanceCaseTwo( root ) ) root = rotateRight( root );
        if ( ifBalanceCaseThree( root ) ) flipColors( root );

        return ( RBTreeNode<K, V>  ) updateSize( root );
    }

    //-------------------------------------------------------
    // put
    //-------------------------------------------------------

    /**
     * put key -> val into this R-B tree
     * */

    public void put( K key, V val ) {
        if ( isNull( key, val ) ) return;

        root = put( this.root, key, val );
        ( ( RBTreeNode<K, V> ) root ).color = BLACK;
        assert check();
    }

    // this part of code is very similar to
    // put( MapTreeNode<K, V> root, K key, V val ).
    private RBTreeNode<K, V>  put( MapTreeNode<K, V> root, K key, V val ) {
        // base case, attach the new node to this position
        if ( root == null ) return new RBTreeNode<K, V> ( ID++, key, val, RED );

        int res = compareKeys( root, key );
        // the node should be attached in the left subtree
        if ( res > 0 ) root.left = put( root.left, key, val );
        // the node should be attached in the right subtree
        else if ( res < 0 ) root.right = put( root.right, key, val );
        // added before, update value
        else root.val = val;

        // update size and restore this R-B tree
        return balance( ( RBTreeNode<K, V>  ) root );
    }

    //-------------------------------------------------------
    // delete
    //-------------------------------------------------------

    /**
     * update the root after deleting
     * */

    protected void updateRootForDelete( RBTreeNode<K, V>  root ) {
        this.root = root;
        // !isEmpty() is equivalent to root != null
        if ( !isEmpty() ) root.color = RED;
    }

    /**
     * delete the minimum key -> value in this R-B tree,
     * and get the value
     * */

    public V deleteMinAndGetVal() {
        deletedMinNode = null;
        deleteMin();
        return deletedMinNode == null ? null : deletedMinNode.val;
    }

    /**
     * delete the minimum key -> value in this R-B tree
     * */

    public void deleteMin() {
        // the root is null, i.e. the tree is empty,
        // which is missed by the textbook
        if ( isEmpty() ) return;

        // the following commented out code is from the textbook,
        // but from my point of view, they're redundant.
        if ( !isRed( root.left ) &&
                 !isRed( root.right ) )
             ( ( RBTreeNode<K, V> ) root ).color = BLACK;

        // delete the node and update the root
        updateRootForDelete( deleteMin( ( RBTreeNode<K, V> ) this.root ) );
        assert check();
    }

    protected RBTreeNode<K, V>  deleteMin( RBTreeNode<K, V>  root ) {
        // base case, this node is the least one in the tree
        // and it's also a leaf node in this R-B tree,
        // so just return null, instead of return root.right,
        // which is different from deleteMin() for BST.
        if ( root.left == null ) {
            deletedMinNode = root;
            assert root.right == null;
            return null;
        }

        // guarantee that every node
        // we're traveling along left subtree
        // is either 3-node or 4-node.
        // !isRed( root.left.left ) is to
        // differentiate case 4 and case 7
        if ( !isRed( root.left ) &&
                !isRed( root.left.left ) )
            root = moveRedLeft( root );

        // otherwise, look into the left subtree
        root.left = deleteMin( ( RBTreeNode<K, V> ) root.left );
        return balance( root );
    }

    protected RBTreeNode<K, V>  moveRedLeft( RBTreeNode<K, V>  root ) {
        flipColors( root );

        if ( isRed( root.right.left ) ) {
            // handle case 6
            root.right = rotateRight( ( RBTreeNode<K, V>  ) root.right );
            root = rotateLeft( root );
            flipColors( root );
        }

        return root;
    }

    /**
     * delete the maximum key -> value in this R-B tree,
     * and get the value
     * */

    public V deleteMaxAndGetVal() {
        deletedMaxNode = null;
        deleteMax();
        return deletedMaxNode == null ? null : deletedMaxNode.val;
    }


    /**
     * delete the maximum key -> value in this R-B tree
     * */

    // TODO: 5/29/2021 return the deleted max val in O(1)
    public void deleteMax() {
        // the root is null, i.e. the tree is empty,
        // which is missed by the textbook
        if ( isEmpty() ) return;

        // the following commented out code is from the textbook,
        // but from my point of view, they're redundant
         if ( !isRed( root.left ) &&
                 !isRed( root.right ) )
             ( ( RBTreeNode<K, V> ) root ).color = RED;

        // delete the node and update the root
        updateRootForDelete( deleteMax( ( RBTreeNode<K, V> ) this.root ) );
        assert check();
    }

    private RBTreeNode<K, V>  deleteMax( RBTreeNode<K, V>  root ) {
        // handle case 2
        if ( isRed( root.left ) )
            root = rotateRight( root );

        // base case, this node is the greatest one in the tree
        // and it's also a leaf node in this R-B tree,
        // so just return null, instead of return root.left,
        // which is different from deleteMax() for BST
        if ( root.right == null ) {
            deletedMaxNode = root;
            assert root.left == null;
            return null;
        }

        // guarantee that every node
        // we're traveling along right subtree
        // is either 3-node or 4-node.
        // differentiate case 4 and case 5,
        // and handle case 5 more efficiently
        if ( !isRed( root.right ) &&
                !isRed( root.right.left ) )
            root = moveRedRight( root );

        // otherwise, look into the right subtree
        root.right = deleteMax( ( RBTreeNode<K, V>  ) root.right );
        return balance( root );
    }

    protected RBTreeNode<K, V>  moveRedRight( RBTreeNode<K, V>  root ) {
        flipColors( root );

        // handle case 4 more efficiently,
        // since at this point, there is an extra red node on the left,
        // we could move it to the right part of the tree
        // but different from the code in the textbook,
        // which is: !isRed( root.left.left )
        if ( isRed( root.left.left ) ) {
            root = rotateRight( root );
            flipColors( root );
        }

        return root;
    }

    /**
     * delete the key -> value in this R-B tree,
     * and get the value
     * */

    public V deleteAndGetVal( K key ) {
        deletedNode = null;
        delete( key );
        return deletedNode == null ? null : deletedNode.val;
    }

    /**
     * delete the key -> value in this R-B tree
     * */

    public void delete( K key ) {
        deletedNode = null;

        // the root is null, i.e. the tree is empty,
        // which is missed by the textbook
        if ( key == null || isEmpty() ) return;

        // the following commented out code is from the textbook,
        // but from my point of view, they're redundant
        if ( !isRed( root.left ) &&
                 !isRed(  root.right ) )
             ( ( RBTreeNode<K, V> ) root ).color  = RED;

        // delete the node and update the root
        updateRootForDelete( delete( ( RBTreeNode<K, V> ) this.root, key ) );
        assert check();
    }

    // Note that this recursive method is a bit unique from usual ones,
    // since the base case for this method is not at the beginning of code
    private RBTreeNode<K, V>  delete( RBTreeNode<K, V>  root, K key ) {
        // why not use the commented-out code to compare keys?
        // because if tree was rotated, the root has changed,
        // i.e. not the previous one we computed for the variable, res,
        // so we need to recompute their comparing order.
//        int res = compareKeys( root, key );

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

            root.left = delete( ( RBTreeNode<K, V>  ) root.left, key );
        }
        // the key may be in the right subtree,
        // or found the key to delete.
        else {
            // this part of code is very similar to
            // deleteMax( RedBlackTreeNode root ).
            // starts at here ---->
            if ( isRed( root.left ) )
                root = rotateRight( root );

            // base case 2 and also case 1, found the key and the node
            // associated with the key is either a 3-node or 4-node
            // just delete it
            if ( compareKeys( root, key ) == 0 &&
                    root.right == null ) {
                assert deletedNode == null;
                deletedNode = root;
                return null;
            }

            // base 3, not found the key in this R-B tree
            // and this corner case,
            // where you delete a maximum key that is not in the R-B tree,
            // is missed by the textbook
            if ( root.right == null ) return root;

            if ( !isRed( root.right ) &&
                    !isRed( root.right.left ) )
                root = moveRedRight( root );
            // ----> ends here

            // case 2, found the key but
            // the node associated with the key is a 2-node
            // replace it with its successor and
            // delete the successor with deleteMin( root ).
            if ( compareKeys( root, key ) == 0 ) {
                assert deletedNode == null;
                deletedNode = new MapTreeNode<>( root.ID, root.key, root.val );
                // replace the node with its successor
                root.replace( min( root.right ) );
                // delete the successor
                root.right = deleteMin( ( RBTreeNode<K, V>  ) root.right );

                // the following commented-out code is from the textbook,
                // which is a little bit complex, not as simple as possible
//                root.val = get( root.right, min( root.right ).key );
//                root.key = min( root.right ).key;
//                root.right = deleteMin( ( RedBlackTreeNode ) root.right );
            }
            // the key may be in the right subtree,
            else root.right = delete( ( RBTreeNode<K, V>  ) root.right, key );
        }

        // update size and restore this R-B tree
        return balance( root );
    }

    //-------------------------------------------------------
    // toString methods
    //-------------------------------------------------------

    /**
     * inorderPrint
     * */

    private void inorderPrint( RBTreeNode<K, V>  root ) {
        if ( root == null ) return;

        inorderPrint( ( RBTreeNode<K, V>  ) root.left );
        System.out.print( root + " " );
        inorderPrint( ( RBTreeNode<K, V>  ) root.right );
    }

    /**
     * print this R-B tree in inorder
     * */

    public void inorderPrint() {
        inorderPrint( ( RBTreeNode<K, V> ) root );
        System.out.println();
    }

    //-------------------------------------------------------
    // Check integrity of red-black tree data structure.
    //-------------------------------------------------------

    protected boolean check() {
        boolean isBST = super.check();
        boolean is23 = is23();
        boolean isBalanced = isBalanced();

        if ( !is23 )             System.err.println( "Not a 2-3 tree" );
        if ( !isBalanced )       System.err.println( "Not balanced" );

        return isBST && is23 && isBalanced;
    }

    // Does the tree have no red right links, and at most one (left)
    // red links in a row on any path?
    private boolean is23() { return is23( root ); }
    private boolean is23( MapTreeNode<K, V> x ) {
        if ( x == null ) return true;
        if ( isRed( x.right ) ) return false;
        if ( x != root && isRed( x ) && isRed( x.left ) ) return false;
        return is23( x.left ) && is23( x.right );
    }

    // do all paths from root to leaf have same number of black edges?
    private boolean isBalanced() {
        int black = 0;     // number of black links on path from root to min
        MapTreeNode<K, V> x = root;
        while ( x != null ) {
            if ( !isRed( x ) ) black++;
            x = x.left;
        }
        return isBalanced( root, black );
    }

    // does every path from the root to a leaf have the given number of black links?
    private boolean isBalanced( MapTreeNode<K, V> x, int black ) {
        if ( x == null ) return black == 0;
        if ( !isRed( x ) ) black--;
        return isBalanced( x.left, black ) && isBalanced( x.right, black );
    }
}
