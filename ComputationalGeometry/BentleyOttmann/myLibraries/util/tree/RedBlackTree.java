package myLibraries.util.tree;

/*
 * RedBlackTree.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.tree.elements.MapTreeNode;

import java.util.Comparator;

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
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class RedBlackTree<K, V> extends BinarySearchTree<K, V> {

    /**
     * constructs to create an instance of RedBlackTree
     * */

    public RedBlackTree( Comparator<K> comparator ) {
        super( comparator );
    }

    public RedBlackTree() {
        this( null );
    }

    /**
     * node color
     * */

    public enum Color {
        RED, BLACK
    }

    /**
     * Data structure of Red Black Tree Node
     *
     * Inner class
     * */

    protected class RedBlackTreeNode extends MapTreeNode<K, V> {
        public Color color;

        /**
         * constructs to create an instance of Node
         * */

        public RedBlackTreeNode( int ID, K key,
                                 V val, Color color ) {
            this( ID, null, null, null, key, val, color );
        }

        public RedBlackTreeNode( int ID, RedBlackTreeNode parent,
                            K key, V val, Color color ) {
            this( ID, parent, null, null, key, val, color );
        }

        public RedBlackTreeNode( int ID, RedBlackTreeNode parent,
                                 RedBlackTreeNode left, RedBlackTreeNode right,
                            K key, V val, Color color ) {
            super( ID, parent, left, right, key, val );
            this.color = color;
        }

        public RedBlackTreeNode( int ID, RedBlackTreeNode left,
                                 RedBlackTreeNode right, K key, V val, Color color ) {
            this( ID, null, left, right, key, val, color );
        }

        public RedBlackTreeNode( RedBlackTreeNode node ) {
            this( node.ID, node.key, node.val, node.color );
        }

        private String toStringWithoutColor() {
            return "{" + key + "->" + val + "}";
        }

        private String toStringNormal() {
            return color + ":{" + key + "->" + val + "}";
        }

        private String toStringWithoutKey() {
            return String.valueOf( val );
        }

        @Override
        public String toString() {
            return toStringWithoutKey();
        }
    }

    /**
     * check a node has valid structure
     * */

    private boolean checkInvalidStructure( RedBlackTreeNode root ) {
        RedBlackTreeNode left = ( RedBlackTreeNode ) root.left;
        RedBlackTreeNode right = ( RedBlackTreeNode ) root.right;

        boolean ifInvalid = false;
        // if left is BLACK, right must be non-null
        if ( left != null && left.color == RedBlackTree.Color.BLACK )
            if ( right == null )
                ifInvalid = true;

        // if right is BLACK, left must be non-null
        if ( right != null && right.color == RedBlackTree.Color.BLACK )
            if ( left == null )
                ifInvalid = true;

        return ifInvalid;
    }

    /**
     * Use inorder to do the job
     * */

    private void checkValidTreeStructure( RedBlackTreeNode root ) {
        if ( root == null ) return;
        assert !checkInvalidStructure( root ) : root.left + " <- " +root + " -> " + root.right;

        checkValidTreeStructure( ( RedBlackTreeNode ) root.left );
        checkValidTreeStructure( ( RedBlackTreeNode ) root.right );
    }

    /**
     * check Valid Tree Structure
     * */

    public void checkValidTreeStructure() {
        checkValidTreeStructure( ( RedBlackTreeNode ) root );
    }

    /**
     * inorderPrint
     * */

    private void inorderPrint( RedBlackTreeNode root ) {
        if ( root == null ) return;
        assert !checkInvalidStructure( root );

        inorderPrint( ( RedBlackTreeNode ) root.left );
        System.out.print( root + " " );
        inorderPrint( ( RedBlackTreeNode ) root.right );
    }

    /**
     * print this BST in inorder
     * */

    public void inorderPrint() {
        inorderPrint( ( RedBlackTreeNode ) root );
        System.out.println();
    }

    /**
     * a node is red?
     * */

    protected boolean isRed( MapTreeNode<K, V> node ) {
        return node != null &&
                ( ( RedBlackTreeNode ) node ).color == RedBlackTree.Color.RED;
    }

    /**
     * common part for both rotateLeft and rotateRight
     * */

    protected void rotate( RedBlackTreeNode root, RedBlackTreeNode temp ) {
        temp.color = root.color;
        root.color = RedBlackTree.Color.RED;
        temp.numberOfChildren = root.numberOfChildren;
        updateSize( root );
    }

    /**
     * rotate Left
     * */

    protected RedBlackTreeNode rotateLeft( RedBlackTreeNode root ) {
        RedBlackTreeNode temp = ( RedBlackTreeNode ) root.right;
        root.right = temp.left;
        temp.left = root;
        rotate( root, temp );
        return temp;
    }

    /**
     * rotate Right
     * */

    protected RedBlackTreeNode rotateRight( RedBlackTreeNode root ) {
        RedBlackTreeNode temp = ( RedBlackTreeNode ) root.left;
        root.left = temp.right;
        temp.right = root;
        rotate( root, temp );
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

    protected void flipColors( RedBlackTreeNode root, boolean reverse ) {
        assert root.left != null && root.right != null;
        root.color = reverse ? RedBlackTree.Color.BLACK : RedBlackTree.Color.RED;
        ( ( RedBlackTreeNode ) root.left ).color = reverse ? RedBlackTree.Color.RED : RedBlackTree.Color.BLACK;
        ( ( RedBlackTreeNode ) root.right ).color = reverse ? RedBlackTree.Color.RED : RedBlackTree.Color.BLACK;
    }

    /**
     * Balance Case One:
     * root's left child is RED and
     * the right child is BLACK
     * */

    protected boolean ifBalanceCaseOne( RedBlackTreeNode root ) {
        return isRed( root.right ) && !isRed( root.left );
    }

    /**
     * Balance Case Two:
     * root's left child is RED
     * and the left child's left child is RED
     * */

    protected boolean ifBalanceCaseTwo( RedBlackTreeNode root ) {
        return isRed( root.left ) && isRed( root.left.left );
    }

    /**
     * Balance Case Three:
     * root's left child is BLACK and
     * the right child is RED
     * */

    protected boolean ifBalanceCaseThree( RedBlackTreeNode root ) {
        return isRed( root.left ) && isRed( root.right );
    }

    /**
     * balance this R-B tree
     * */

    protected RedBlackTreeNode balance( RedBlackTreeNode root ) {
        if ( ifBalanceCaseOne( root ) ) root = rotateLeft( root );
        if ( ifBalanceCaseTwo( root ) ) root = rotateRight( root );
        if ( ifBalanceCaseThree( root ) ) flipColors( root, false );

        return ( RedBlackTreeNode ) updateSize( root );
    }

    /**
     * put key -> val into this R-B tree
     * */

    public void put( K key, V val ) {
        RedBlackTreeNode root = put( ( RedBlackTreeNode ) this.root, key, val );
        root.color = RedBlackTree.Color.BLACK;
        this.root = root;
    }

    // this part of code is very similar to
    // put( MapTreeNode<K, V> root, K key, V val ).
    private RedBlackTreeNode put( RedBlackTreeNode root, K key, V val ) {
        // base case, attach the new node to this position
        if ( root == null ) return new RedBlackTreeNode( ID++, key, val, RedBlackTree.Color.RED );

        int res = compareKeys( root, key );
        // the node should be attached in the left subtree
        if ( res > 0 ) root.left = put( ( RedBlackTreeNode ) root.left, key, val );
        // the node should be attached in the right subtree
        else if ( res < 0 ) root.right = put( ( RedBlackTreeNode ) root.right, key, val );
        // added before, update value
        else root.val = val;

        // update size and restore this R-B tree
        return balance( root );
    }

    /**
     * update the root after deleting
     * */

    protected void updateRootForDelete( RedBlackTreeNode root ) {
        this.root = root;
        // !isEmpty() is equivalent to root != null
        if ( !isEmpty() ) root.color = RedBlackTree.Color.BLACK;
    }

    /**
     * delete the minimum key -> value in this R-B tree
     * */

    public void deleteMin() {
        // the following commented out code is from the textbook,
        // but from my point of view, they're redundant.
         if ( !isRed( root.left ) &&
                 !isRed( root.right ) )
             ( ( RedBlackTreeNode ) root ).color = RedBlackTree.Color.RED;

        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( isEmpty() ) return;

        // delete the node and update the root
        updateRootForDelete( deleteMin( ( RedBlackTreeNode ) this.root ) );
    }

    protected RedBlackTreeNode deleteMin( RedBlackTreeNode root ) {
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
        root.left = deleteMin( ( RedBlackTreeNode ) root.left );
        return balance( root );
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

    protected RedBlackTreeNode moveRedLeft( RedBlackTreeNode root ) {
        flipColors( root, true );

        if ( isRed( root.right.left ) ) {
            // handle case 6
            root.right = rotateRight( ( RedBlackTreeNode ) root.right );
            root = rotateLeft( root );
            flipColors( root, false ); // RedBlackTree.flipColors(RedBlackTree.java:229) nullPointerException
        }

        return root;
    }

    /**
     * delete the maximum key -> value in this R-B tree
     * */

    public void deleteMax() {
        // the following commented out code is from the textbook,
        // but from my point of view, they're redundant
         if ( !isRed( root.left ) &&
                 !isRed( root.right ) )
             ( ( RedBlackTreeNode ) root ).color = RedBlackTree.Color.RED;

        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( isEmpty() ) return;

        // delete the node and update the root
        updateRootForDelete( deleteMax( ( RedBlackTreeNode ) this.root ) );
    }

    private RedBlackTreeNode deleteMax( RedBlackTreeNode root ) {
        // handle case 2
        if ( isRed( root.left ) )
            root = rotateRight( root );

        // base case, this node is the greatest one in the tree
        // and it's also a leaf node in this R-B tree,
        // so just return null, instead of return root.left,
        // which is different from deleteMax() for BST
        if ( root.right == null ) {
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
        root.right = deleteMax( ( RedBlackTreeNode ) root.right );
        return balance( root );
    }

    protected RedBlackTreeNode moveRedRight( RedBlackTreeNode root ) {
        flipColors( root, true );

        // handle case 4 more efficiently,
        // since at this point, there is an extra red node on the left,
        // we could move it to the right part of the tree
        // but different from the code in the textbook,
        // which is: !isRed( root.left.left )
        if ( isRed( root.left.left ) ) {
            root = rotateRight( root );
            flipColors( root, false );
        }

        return root;
    }

    /**
     * delete the key -> value in this R-B tree
     * */

    public void delete( K key ) {
        // the following commented out code is from the textbook,
        // but from my point of view, they're redundant
         if ( !isRed( root.left ) &&
                 !isRed(  root.right ) )
             ( ( RedBlackTreeNode ) root ).color = RedBlackTree.Color.RED;

        // the root is null, i.e the tree is empty,
        // which is missed by the textbook
        if ( isEmpty() ) return;

        // delete the node and update the root
        updateRootForDelete( delete( ( RedBlackTreeNode ) this.root, key ) );
    }

    // Note that this recursive method is a bit unique from usual ones,
    // since the base case for this method is not at the beginning of code
    private RedBlackTreeNode delete( RedBlackTreeNode root, K key ) {
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

            root.left = delete( ( RedBlackTreeNode ) root.left, key );
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
                deletedNode = new MapTreeNode<>( root.ID, root.key, root.val );
                // replace the node with its successor
                root.replace( min( root.right ) );
                // delete the successor
                root.right = deleteMin( ( RedBlackTreeNode ) root.right );

                // the following commented-out code is from the textbook,
                // which is a little bit complex, not as simple as possible
//                root.val = get( root.right, min( root.right ).key );
//                root.key = min( root.right ).key;
//                root.right = deleteMin( ( RedBlackTreeNode ) root.right );
            }
            // the key may be in the right subtree,
            else root.right = delete( ( RedBlackTreeNode ) root.right, key );
        }

        // update size and restore this R-B tree
        return balance( root );
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
}
