package myLibraries.util.geometry;

/*
 * EventRBTreeVoronoi.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/5/2022$
 */

import myLibraries.util.geometry.DCEL.HalfEdge;
import myLibraries.util.geometry.DCEL.VoronoiVertex;
import myLibraries.util.geometry.elements.EventSite;
import myLibraries.util.tree.DoublyLinkedRBT;
import myLibraries.util.tree.elements.MapTreeNode;
import myLibraries.util.tree.elements.RBTreeNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 */

// TODO: 2/15/2022 combine status trees
public class EventRBTreeVoronoi extends DoublyLinkedRBT<EventSite, EventSite> {

    double minSweepY = Double.MAX_VALUE;
    // Voronoi vertices.
    public final List<VoronoiVertex> vertices = new ArrayList<>();

    /**
     * constructs to create an instance of EventRBTree
     * */

    public EventRBTreeVoronoi( Comparator<EventSite> c ) {
        super( c );
    }

    public EventRBTreeVoronoi() {
        this( null );
    }

    public void addVertex( VoronoiVertex v ) {
        vertices.add( v );
    }

    /**
     * put key -> val( key ) into this R-B tree
     * */

    public void put( EventSite key ) {
        if ( isNull( key, key ) ) return;

        minSweepY = Math.min( key.y, minSweepY );

        VorRBTNode root = put( null, ( VorRBTNode ) this.root, key, true );
        root.color = BLACK;
        this.root = root;
        assert check();
    }

    private VorRBTNode put( VorRBTNode parent, VorRBTNode root,
                            EventSite key, boolean isLeft ) {
        // base case, attach the new node to this position
        if ( root == null ) {
            VorRBTNode node = new VorRBTNode( key );
            // add this node to the linked list
            if ( isLeft ) linkedList.addBefore( parent == null ? null : parent.node, node.node );
            else linkedList.addAfter( parent.node, node.node );

            // EventSite <-> R-B tree node
            key.node = node;
            return node;
        }

        int res = compareKeys( root, key );
        // the node should be attached in the left subtree
        if ( res > 0 ) {
            root.left = put( root, ( VorRBTNode ) root.left, key, true );
            root.left.parent = root;
        }
        // the node should be attached in the right subtree
        else if ( res < 0 ) {
            root.right = put( root, ( VorRBTNode ) root.right, key, false );
            root.right.parent = root;
        }
        // else res == 0, ignore the input,
        // meaning that the first one will be kept
        // when handling several circle events with the same value.
        // or the first one will be put in this queue( R-B tree )
        // with duplicate sites.
        // Also, duplicate circle events more than one will not be kept,
        // so remove their circle event relationship.
        else key.deleteCircleEvent( this );

        // update size and restore this R-B tree
        return ( VorRBTNode ) balance( root );
    }

    /**
     * delete the given node in this R-B tree.
     * Doubly-connected nodes required.
     * And allow not existing node to be deleted in this tree.
     * check() enabled.
     * */

    // TODO: 2/9/2022 too many duplicate code with DoublyLinkedRBT.java, Have ways to refine it?
    public void delete( VorRBTNode node ) {
        assert node == null || linkedList.contains( node.node ) : node;

        deleteCommon( node );
        assert check();
    }

    @SuppressWarnings( "unchecked" )
    protected void deleteCommon( VorRBTNode node ) {
        deletedMinNode = deletedLinkedNode = deletedNode = null;
        // the root is null, i.e. the tree is empty,
        if ( node == null || isEmpty() ) return;

        // https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/util/LinkedList.html
        LinkedList<RBTreeNode<EventSite, EventSite>> path = new LinkedList<>();

        // get the path from the node all the way to the root node
        do {
            path.addFirst( node );
            // only RedBlackTreeNode used here, so ignore warning
            node = ( VorRBTNode ) node.parent;
        } while ( node != null );

        // re-get the node to be deleted
        node = ( VorRBTNode ) path.getLast();
        // remove the root node
        path.poll();

        // if both children of root are black, set root to red
        if ( !isRed( root.left ) &&
                !isRed(  root.right ) )
            ( ( VorRBTNode ) root ).color = RED;

        // delete the node and update the root
        updateRootForDelete( delete( ( VorRBTNode ) root, path, node ) );

        linkedList.remove( deletedLinkedNode.node );
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
    private VorRBTNode delete( VorRBTNode root, LinkedList<RBTreeNode<EventSite, EventSite>> path,
                               VorRBTNode  node ) {

        // the node to be deleted may be in the left subtree.
        // and the path must have nodes as we step into the left subtree
        if ( !path.isEmpty() &&
                root.left == path.poll() ) {
            // this part of code is very similar to
            // deleteMin( RedBlackTreeNode root )
            if ( !isRed( root.left ) &&
                    !isRed( root.left.left ) )
                root = ( VorRBTNode ) moveRedLeft( root, path );

            root.left = delete( ( VorRBTNode ) root.left, path, node );
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
                if ( root.right != null ) path.addFirst( ( VorRBTNode ) root.right );
                root = ( VorRBTNode ) rotateRight( root );
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
                root = ( VorRBTNode ) moveRedRight( root, path );
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
                // update status leaf node's circle event node to this one.
                root.key.updateNode( root );
                // delete the successor
                root.right = deleteMin( ( VorRBTNode ) root.right );

                deletedLinkedNode = deletedMinNode;
            }
            // the key may be in the right subtree,
            else root.right = delete( ( VorRBTNode ) root.right, path, node );
            // restore doubly-connected node, root <-> child.
            if ( root.right != null ) root.right.parent = root;
        }

        // update size and restore this R-B tree
        return ( VorRBTNode ) balance( root );
    }
}
