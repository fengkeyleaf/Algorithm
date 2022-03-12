package myLibraries.util.tree;

/*
 * DoublyLinkedRBT.java
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
import myLibraries.util.tree.elements.RBTreeNode;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * Data structure of Doubly Linked red black tree.
 * This BST has thw following two special features inherited from {@link DoublyLinkedBST}.
 * 1) every child has a pointer pointing to its parent. i.e. parent <-> child, not parent -> child.
 * Strictly speaking, note that this BST is not a tree anymore, but a connected graph.
 * 2) this R-B tree maintains an ordered linked list in order to get predecessor and successor in O(1).
 * So the order of the list is the same as that of this BST.
 *
 * And this R-B tree also has two special features compared to normal R-B tree,
 * 1) Allow to insert a node at the left or right position of another node.
 * And the tree self-balances after inserting.
 * 2) Allow to delete a node directly in the tree,
 * and the tree self-balances when and after deleting.
 *
 * Enabling those two features means
 * no key-comparison required, but doubly-connected nodes required.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public class DoublyLinkedRBT<K, V> extends RedBlackTree<K, V>
        implements DoublyLinkedTree<K, V> {

    protected MapTreeNode<K, V> deletedLinkedNode;

    /**
     * constructs to create an instance of DoublyLinkedRBT
     * */

    public DoublyLinkedRBT( Comparator<K> comparator ) {
        super( comparator );
    }

    public DoublyLinkedRBT() {
        this( null );
    }

    /**
     * common part for both rotateLeft and rotateRight
     * */

    protected void rotate( RBTreeNode<K, V>  root, RBTreeNode<K, V>  temp ) {
        temp.parent = root.parent;
        root.parent = temp;

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
        if ( temp.left != null ) temp.left.parent = root;

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
        if ( temp.right != null ) temp.right.parent = root;

        temp.right = root;
        rotate( root, temp );
        return temp;
    }

    /**
     * put key -> val into this R-B tree
     * */

    public void put( K key, V val ) {
        if ( isNull( key, val ) ) return;

        root = put( null, this.root, key, val, true );
        ( ( RBTreeNode<K, V> ) root ).color = BLACK;
        assert check();
    }

    // this part of code is very similar to
    // put( MapTreeNode<K, V> root, K key, V val ).
    private RBTreeNode<K, V>  put( MapTreeNode<K, V> parent, MapTreeNode<K, V> root,
                                   K key, V val, boolean isLeft ) {
        // base case, attach the new node to this position
        if ( root == null ) {
            RBTreeNode<K, V> node = new RBTreeNode<K, V> ( ID++, key, val, RED );
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
            root.left = put( root, root.left, key, val, true );
            root.left.parent = root;
        }
        // the node should be attached in the right subtree
        else if ( res < 0 ) {
            root.right = put( root, root.right, key, val, false );
            root.right.parent = root;
        }
        // added before, update value
        else root.val = val;

        // update size and restore this R-B tree
        return balance( ( RBTreeNode<K, V>  ) root );
    }

    /**
     * put key -> val into this R-B tree at one of children of the given node.
     * Assume that the inserting position is guarantee to be right.
     *
     * @param keyRoot the key associated with the node where the new node is inserted
     * @param isLeft true, append to left child; otherwise, append to right child
     * */

    public void put( K key, V val, K keyRoot, boolean isLeft ) {
        if ( isNull( key, val ) ) return;

        RBTreeNode<K, V>  node = new RBTreeNode<K, V> ( ID++, key, val, RED );
        if ( root == null ) {
            root = node;
            linkedList.addBefore( null, node.node );
            ( ( RBTreeNode<K, V> ) root ).color = BLACK;
            return;
        }

        put( ( RBTreeNode<K, V>  ) getNode( keyRoot ), node, isLeft );
    }

    protected void put( RBTreeNode<K, V>  root, RBTreeNode<K, V>  node, boolean isLeft ) {
        // doubly-connected node, root <-> child.
        // note that we have a connected graph, instead of a tree
        if ( isLeft ) {
            assert root.left == null;
            root.left = node;
            linkedList.addBefore( root.node, node.node );
        }
        else {
            assert root.right == null;
            root.right = node;
            linkedList.addAfter( root.node, node.node );
        }
        node.parent = root;

        // self-balancing process
        // update size and restore this R-B tree
        // all the way to the root node
        do {
            // root may change to another node after balancing,
            // we need to keep a copy of it
            RBTreeNode<K, V>  origin = root;
            root = balance( root );
            // only RedBlackTreeNode used here, so ignore the warning
            @SuppressWarnings( "unchecked" )
            RBTreeNode<K, V>  parent = ( RBTreeNode<K, V>  ) root.parent;
            // restore root after balancing
            if ( parent == null ) this.root = root;
                // restore parent's child after balancing
            else {
                if ( parent.left == origin ) parent.left = root;
                else {
                    assert parent.right == origin;
                    parent.right = root;
                }
            }

            root = parent;
        } while( root != null );

        ( ( RBTreeNode<K, V> ) this.root ).color = BLACK;

        if ( !check() )
            throw new IllegalArgumentException( "Inserted node is not at the right position" );
    }

    /**
     * delete the minimum key -> value in this R-B tree
     * */

    public void deleteMin() {
        // remove the first node in the linked list.
        linkedList.poll();
        deletedMinNode = null;

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
        // restore doubly-connected node, root <-> child.
        if ( root.left != null ) root.left.parent = root;
        return balance( root );
    }

    /**
     * delete the maximum key -> value in this R-B tree
     * */

    // TODO: 5/29/2021 return the deleted max val in O(1)
    public void deleteMax() {
        // remove the last node in the linked list.
        linkedList.pollLast();
        deletedMaxNode = null;

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
        // restore doubly-connected node, root <-> child.
        if ( root.right != null ) root.right.parent = root;
        return balance( root );
    }

    /**
     * delete the key -> value in this R-B tree
     * */

    public void delete( K key ) {
        deletedMinNode = deletedLinkedNode = deletedNode = null;

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

        if ( deletedLinkedNode != null ) linkedList.remove( deletedLinkedNode.node );
        assert check();
    }

    // Note that this recursive method is a bit unique from usual ones,
    // since the base case for this method is not at the beginning of code
    private RBTreeNode<K, V>  delete( RBTreeNode<K, V>  root, K key ) {
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
            // restore doubly-connected node, root <-> child.
            if ( root.left != null ) root.left.parent = root;
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
                deletedLinkedNode = deletedNode = root;
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
            // the node associated with the key is a 2-node,
            // replace it with its successor and
            // delete the successor with deleteMin( root ).
            if ( compareKeys( root, key ) == 0 ) {
                assert deletedNode == null;
                deletedNode = new MapTreeNode<>( root.ID, root.key, root.val );
                // replace the node with its successor
                root.replace( min( root.right ) );
                // delete the successor
                root.right = deleteMin( ( RBTreeNode<K, V>  ) root.right );

                // the linked node we need remove is the one from min( root.right )
                deletedLinkedNode = deletedMinNode;
            }
            // the key may be in the right subtree,
            else root.right = delete( ( RBTreeNode<K, V>  ) root.right, key );
            // restore doubly-connected node, root <-> child.
            if ( root.right != null ) root.right.parent = root;
        }

        // update size and restore this R-B tree
        return balance( root );
    }

    /**
     * delete the given node in this R-B tree.
     * Doubly-connected nodes required.
     * And allow not existing node to be deleted in this tree.
     * check() enabled.
     * */

    public void delete( RBTreeNode<K, V>  node ) {
        assert node == null || linkedList.contains( node.node ) : node;

        deleteCommon( node );
        assert check();
    }

    public V deleteNodeAndGetVal( K key ) {
        delete( ( RBTreeNode<K, V> ) getNode( key ) );
        return deletedNode == null ? null : deletedNode.val;
    }

    @SuppressWarnings( "unchecked" )
    protected void deleteCommon( RBTreeNode<K, V>  node ) {
        deletedMinNode = deletedLinkedNode = deletedNode = null;
        // the root is null, i.e. the tree is empty,
        if ( node == null || isEmpty() ) return;

        // https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/util/LinkedList.html
        LinkedList<RBTreeNode<K, V>> path = new LinkedList<>();

        // get the path from the node all the way to the root node
        do {
            path.addFirst( node );
            // only RedBlackTreeNode used here, so ignore warning
            node = ( RBTreeNode<K, V>  ) node.parent;
        } while ( node != null );

        // re-get the node to be deleted
        node = path.getLast();
        // remove the root node
        path.poll();

        // if both children of root are black, set root to red
        if ( !isRed( root.left ) &&
                !isRed(  root.right ) )
            ( ( RBTreeNode<K, V> ) root ).color = RED;

        // delete the node and update the root
        updateRootForDelete( delete( ( RBTreeNode<K, V> ) root, path, node ) );

        linkedList.remove( deletedLinkedNode.node );
    }

    protected RBTreeNode<K, V>  moveRedLeft( RBTreeNode<K, V>  root,
                                             LinkedList<RBTreeNode<K, V> > path ) {
        flipColors( root );

        if ( isRed( root.right.left ) ) {
            // handle case 6
            root.right = rotateRight( ( RBTreeNode<K, V>  ) root.right );

            // left rotate, meaning we need visit root's left child once ( if it's not null )
            // before reaching the node we want to delete
            if ( root.left != null ) path.addFirst( ( RBTreeNode<K, V>  ) root.left );
            root = rotateLeft( root );

            flipColors( root );
        }

        return root;
    }

    protected RBTreeNode<K, V>  moveRedRight( RBTreeNode<K, V>  root,
                                              LinkedList<RBTreeNode<K, V> > path ) {
        flipColors( root );

        // handle case 4 more efficiently,
        // since at this point, there is an extra red node on the left,
        // we could move it to the right part of the tree
        // but different from the code in the textbook,
        // which is: !isRed( root.left.left )
        if ( isRed( root.left.left ) ) {
            // right rotate, meaning we need visit root's right child once ( if it's not null )
            // before reaching the node we want to delete
            if ( root.right != null ) path.addFirst( ( RBTreeNode<K, V>  ) root.right );
            root = rotateRight( root );

            flipColors( root );
        }

        return root;
    }

    /**
     * @param root current node
     * @param path path to the node to be deleted.( excluding the root node )
     *             For example, we inserted S -> E -> A -> R one by one,
     *             and want to delete R and the path will be S -> R in this R-B tree
     * @param node node to be deleted
     * */

    // note that with this method,
    // there must be a node to be deleted in this R-B tree
    private RBTreeNode<K, V>  delete( RBTreeNode<K, V>  root,
                                      LinkedList<RBTreeNode<K, V> > path,
                                      RBTreeNode<K, V>  node ) {
        // the node to be deleted may be in the left subtree.
        // and the path must have nodes as we step into the left subtree
        if ( !path.isEmpty() &&
                root.left == path.poll() ) {
            // this part of code is very similar to
            // deleteMin( RedBlackTreeNode root )
            if ( !isRed( root.left ) &&
                    !isRed( root.left.left ) )
                root = moveRedLeft( root, path );

            root.left = delete( ( RBTreeNode<K, V>  ) root.left, path, node );
            // restore doubly-connected node, root <-> child.
            if ( root.left != null ) root.left.parent = root;
        }
        // the node to be deleted may be in the right subtree,
        // or found the node to delete where the path is supposed to be empty.
        // but we don't take rotation into consideration.
        // if so happens, we should guarantee current node is the one we want to delete,
        // otherwise, we need step into right subtree.
        else {
            // this part of code is very similar to
            // deleteMax( RedBlackTreeNode root ).
            // starts at here ---->
            if ( isRed( root.left ) ) {
                // left rotate, meaning we need visit root's left child once ( if it's not null )
                // before reaching the node we want to delete
                if ( root.right != null ) path.addFirst( ( RBTreeNode<K, V>  ) root.right );
                root = rotateRight( root );
            }

            // base case 2 and also case 1, found the key and the node
            // associated with the key is either a 3-node or 4-node
            // just delete it
            if ( path.isEmpty() && root == node &&
                    root.right == null ) {
                assert deletedNode == null;
                deletedLinkedNode = deletedNode = root;
                return null;
            }

            assert root.right != null : root;
            if ( !isRed( root.right ) &&
                    !isRed( root.right.left ) )
                root = moveRedRight( root, path );
            // ----> ends here

            // case 2, found the key but
            // the node associated with the key is a 2-node
            // replace it with its successor and
            // delete the successor with deleteMin( root ).
            if ( path.isEmpty() && root == node ) {
                assert deletedNode == null;
                deletedNode = new MapTreeNode<>( root.ID, root.key, root.val );
                // replace the node with its successor
                root.replace( min( root.right ) );
                // delete the successor
                root.right = deleteMin( ( RBTreeNode<K, V>  ) root.right );

                deletedLinkedNode = deletedMinNode;
            }
            // the key may be in the right subtree,
            else root.right = delete( ( RBTreeNode<K, V>  ) root.right, path, node );
            // restore doubly-connected node, root <-> child.
            if ( root.right != null ) root.right.parent = root;
        }

        // update size and restore this R-B tree
        return balance( root );
    }

    //-------------------------------------------------------
    // Check integrity of R-B tree data structure.
    //-------------------------------------------------------

    protected boolean check() {
        boolean isBBST = super.check();
        boolean isDoublyConnected = isDoublyConnected();
        boolean isLinked = isLinked();

        if ( !isDoublyConnected )       System.err.println( "Not doublyConnected" );
        if ( !isLinked )       System.err.println( "Not Linked" );

        return isBBST && isDoublyConnected && isLinked;
    }
}
