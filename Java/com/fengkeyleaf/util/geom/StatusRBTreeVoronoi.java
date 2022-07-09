package com.fengkeyleaf.util.geom;

/*
 * StatusRBTreeVoronoi.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 12/31/2021$
 */

import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.util.CompareElement;
import com.fengkeyleaf.util.DoublyLinkedNode;
import com.fengkeyleaf.util.tree.DoublyLinkedRBT;
import com.fengkeyleaf.util.tree.MapTreeNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * StatusRBTree for Voronoi
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// TODO: 2/15/2022 combine status trees
// TODO: 2/7/2022 root node is red, not correct
public class StatusRBTreeVoronoi
        extends DoublyLinkedRBT<EventSite, EventSite> {

    // should be initialized only once.
    public double initY;
    // no restriction.
    public double sweepY;
    // special danglings from horizontal sites at the very beginning.
    // They have only one intersection, thus one internal node in the status tree.
    // There would be no node
    // ( with the dangling half-edge that
    // should be attached to the bounding box later on )
    // remained in the tree
    // when we're dealing with a circle event.
    // see test case: test_1/6
    private final List<EventSite> specialDanglings = new ArrayList<>();
    // Voronoi edges, only the ones without Voronoi Vertex.
    public final List<HalfEdge> edges = new ArrayList<>();

    /**
     * constructs to create an instance of StatusRBTree
     * */

    StatusRBTreeVoronoi( Comparator<EventSite> c ) {
        super( c );
    }

    StatusRBTreeVoronoi() {
        this( null );
    }

    void addEdge( HalfEdge e ) {
        edges.add( e );
    }

    /**
     * add Special Dangling half-edge from two sites
     * with the same y-coor at the very beginning.
     * */

    public void addSpecialDangling( EventSite event ) {
        specialDanglings.add( event );
    }

    List<EventSite> getDanglingEdges( double sweepY ) {
        final List<EventSite> edges = new ArrayList<>();
        getDanglingEdges( root, edges, sweepY );

        specialDanglings.forEach( e -> {
            assert e.type == EventSite.Type.SPECIAL;
            // update current breakpoint based on current y-coor of the sweep line
            e.getDirection( sweepY );
            // very special here.
            // we need flip y-coor to get the right direction of the half-edge.
            // and this only happens when there are sites with the same y-coor
            // at the very beginning.
            e.direction.y = -e.direction.y;
            edges.add( e );
        } );
        return edges;
    }

    private void getDanglingEdges( MapTreeNode<EventSite, EventSite> root, List<EventSite> edges, double sweepY ) {
        if ( root == null || root.key.type == EventSite.Type.LEAF ) return;

        assert root.key.type == EventSite.Type.INTERNAL;
        assert root.val == root.key;
        assert MyMath.doubleCompare( root.key.y, sweepY ) > 0 : root.key.y + " " + sweepY;

        // update current breakpoint based on current y-coor of the sweep line
        root.val.getDirection( sweepY );
        edges.add( root.key );

        getDanglingEdges( root.left, edges, sweepY );
        getDanglingEdges( root.right, edges, sweepY );
    }

    /**
     * compare keys using Comparable<K> or Comparator<K>
     * */

    protected int compareKeys( VorRBTNode root, EventSite key ) {
        // degenerate case 1: there are two sites with the same y-coordinate.
        // However, if this happens right at the start of the algorithm, that is,
        // if the second site event has the same y-coordinate as the first site event, then
        // special code is needed because there is no arc above the second site yet.
        // Now zero-length edge suppose there are event points that coincide.

        // the way to handle degenerate case 1:
        // the same y-coordinate at the very beginning,
        // compare left and right site.
        // This only happens when all internals and the key are on the same line
        // at the very beginning. i.e. The sweep line is on the line
        // on which the first site is, even if we've had processed several sites.
        // see the test file, test_1/6.
        if ( root.key.isOnInitSweep( initY ) &&
                key.isOnInitSweep( initY ) ) {
            assert root.key.leftArc.site.isRight( root.key.rightArc.site ) : root.key.leftArc + " " + root.key.rightArc;
            if ( Vectors.sortByX( root.key.leftArc.site, key ) < 0 )
                return -1;

            if ( Vectors.sortByX( root.key.rightArc.site, key ) > 0 )
                return 1;

            return 0;
        }

        // don't update and compare root and key directly,
        // when the sweep line lies exactly on one of the breakpoints.
        // This only happens when two nodes on the same horizontal line,
        // but we're not at the very beginning of the algorithm.
        if ( root.key.isOnSweepLine( sweepY ) &&
               key.isOnSweepLine( sweepY ) ) {
            return CompareElement.compare( comparator, root.key, key );
        }

        // otherwise, compare vectors in the nodes.
        // update x based on the y-coor of the sweep line,
        // so that we can compare x's of two breakpoints
        assert check( root, key );
        // update breakpoint if it's not on the sweep line.
        if ( !root.key.isOnSweepLine( sweepY ) ) root.val.update( sweepY, true );
        if ( !key.isOnSweepLine( sweepY ) ) key.update( sweepY, true );
        return CompareElement.compare( comparator, root.key, key );
    }

    private boolean check( VorRBTNode root, EventSite key ) {
        assert root.val == root.key;

        if ( !root.key.isOnSweepLine( sweepY ) ) {
            assert MyMath.doubleCompare( root.key.y, sweepY ) > 0 : root.key.y + " " + root.key + " " + key + " " + sweepY;
            assert MyMath.doubleCompare( root.key.leftArc.site.y, sweepY ) > 0 : root + " " + sweepY;
            assert MyMath.doubleCompare( root.key.rightArc.site.y, sweepY ) > 0;
        }

        if ( !key.isOnSweepLine( sweepY ) ) {
            assert MyMath.doubleCompare( key.y, sweepY ) > 0 : key + " " + sweepY;
            assert MyMath.doubleCompare( key.leftArc.site.y, sweepY ) > 0;
            assert MyMath.doubleCompare( key.rightArc.site.y, sweepY ) > 0;
        }

        return true;
    }

    /**
     * get the node associated with the key
     * */

    public VorRBTNode getNode( EventSite key ) {
        return getNode( ( VorRBTNode ) root, key );
    }

    protected VorRBTNode getNode( VorRBTNode root, EventSite key ) {
        // base case, not found the key
        if ( root == null ) return null;
        if ( root.val.type == EventSite.Type.LEAF ) return root;

        int res = compareKeys( root, key );
        // the key may be in the left subtree
        if ( res >= 0 ) return getNode( ( VorRBTNode ) root.left, key );
        // the key may be in the right subtree
        return getNode( ( VorRBTNode ) root.right, key );
    }

    /**
     * put key -> val into this R-B tree
     * */

    public void put( EventSite key ) {
        if ( isNull( key, key ) ) return;

        root = put( null, ( VorRBTNode ) root, key, true );
        ( ( VorRBTNode ) root ).color = BLACK;
        assert check();
    }

    protected VorRBTNode put( VorRBTNode parent, VorRBTNode root,
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
        // added before, update value
        else assert false;

        // update size and restore this R-B tree
        return ( VorRBTNode ) balance( root );
    }

    public void put( VorRBTNode root, EventSite key, boolean isLeft ) {
        VorRBTNode node = new VorRBTNode( key );
        key.node = node;

        put( root, node, isLeft );
    }

    /**
     * delete the given node in this status R-B tree.
     * Doubly-connected nodes required.
     * check() dis-enabled.
     * */

    public void delete( VorRBTNode node ) {
        deleteCommon( node );
    }

    /**
     * find node's predecessor. Assume that the node and its predecessor ( if exists )
     * are all internal nodes. And this method takes O(1)
     * since there are no more two consecutive leaf nodes in a row
     *
     * Ordered linked list is the key to implementing this method.
     * */

    @Override
    public MapTreeNode<EventSite, EventSite> predecessor( MapTreeNode<EventSite, EventSite> node ) {
        if ( node == null ) return null;
        assert linkedList.contains( node.node ) : node;

        DoublyLinkedNode<MapTreeNode<EventSite, EventSite>> linkedNode = node.node;
        while ( linkedNode.getPrev() != null ) {
            if ( linkedNode.getPrev().getData().key.type != EventSite.Type.LEAF )
                return linkedNode.getPrev().getData();

            linkedNode = linkedNode.getPrev();
        }

        return null;
    }

    /**
     * find node's successor. Assume that the node and its successor ( if exists )
     * are all internal nodes. And this method takes O(1)
     * since there are no more two consecutive leaf nodes in a row
     *
     * Ordered linked list is the key to implementing this method.
     * */

    @Override
    public MapTreeNode<EventSite, EventSite> successor( MapTreeNode<EventSite, EventSite> node ) {
        if ( node == null ) return null;
        assert linkedList.contains( node.node );

        DoublyLinkedNode<MapTreeNode<EventSite, EventSite>> linkedNode = node.node;
        while ( linkedNode.getNext() != null ) {
            if ( linkedNode.getNext().getData().key.type != EventSite.Type.LEAF )
                return linkedNode.getNext().getData();

            linkedNode = linkedNode.getNext();
        }

        return null;
    }

    //-------------------------------------------------------
    // Check integrity of red-black tree data structure.
    //-------------------------------------------------------

    public boolean check() {
        boolean isLeaf = isLeaf();

        if ( !isLeaf ) System.err.println( "Leaf node has child" );

        return super.check() && isLeaf;
    }

    protected boolean isBST() {
        return isBST( ( VorRBTNode ) root, null, null );
    }

    // cannot use MapTreeNode, since we need to use the overwritten compareKeys();
    // in this R-B tree, we allow nodes with the same value,
    // since <pj, pi> == <pi, pj> when inserting them for the first time.
    private boolean isBST( VorRBTNode x, EventSite min, EventSite max ) {
        if ( x == null || x.key.type == EventSite.Type.LEAF ) return true;

        if ( min != null && compareKeys( x, min ) < 0 ) {
            System.err.println( "min: " + x + " " + min );
            return false;
        }

        if ( max != null && compareKeys( x, max ) > 0 ) {
            System.err.println( "max: " + x + " " + max );
            return false;
        }

        return isBST( ( VorRBTNode ) x.left, min, x.key ) &&
                isBST( ( VorRBTNode ) x.right, x.key, max );
    }

    // leaf node cannot have any children.
    private boolean isLeaf() {
        return isLeaf( root );
    }

    private boolean isLeaf( MapTreeNode<EventSite, EventSite> root ) {
        if ( root == null ) return true;

        if ( root.key.type == EventSite.Type.LEAF &&
                !( root.left == null && root.right == null ) ) {
            System.err.println( root );
            return false;
        }

        return isLeaf( root.left ) && isLeaf( root.right );
    }
}
