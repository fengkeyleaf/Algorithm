package com.fengkeyleaf.util.geom;

/*
 * VorRBTNode.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 2/15/2022$
 */

import com.fengkeyleaf.util.tree.RBTreeNode;

/**
 * Red Black tree node wrapping EventSite as its key <-> value.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

class VorRBTNode extends RBTreeNode<EventSite, EventSite> {

    VorRBTNode( EventSite e ) {
        super( IDStatic++, e, e, true );
    }

    /**
     * insert <pj, pi> into the status tree.
     * */

    void insertPjPi( EventSite pi, EventSite pj,
                     StatusRBTreeVoronoi statusTree ) {

        key = val = EventSite.getSiteEvent( pi, pj.siteFace.site, statusTree, key.siteFace, pi.siteFace );
        // EventSite -> tree node.
        key.node = this;
        // 4.1 the edge( facing pj ) separating V(pi) and V(pj)
        key.edge = new HalfEdge( key.leftArc );
        // add Voronoi edge.
        statusTree.addEdge( key.edge );
        // set outComponent of the pj Voronoi face.
        if ( key.leftArc.outComponent == null )
            key.leftArc.outComponent = key.edge;
    }

    /**
     * insert <pi, pj> into the status tree.
     * */

    void insertPiPj( VorRBTNode pjPiNode ) {
        // 4.2 the edge( facing pi ) separating V(pi) and V(pj)
        key.edge = new HalfEdge( key.leftArc );
        // set outComponent of the pi Voronoi face.
        if ( key.leftArc.outComponent == null )
            key.leftArc.outComponent = key.edge;
        // half-edge( left pj ) <=> half-edge( pi )
        pjPiNode.key.edge.setTwins( key.edge );
    }

    /**
     * remove the circle event relationship
     * between a leaf status node with its predecessor and successor.
     * */

    void deleteCircleEvent() {
        key.circleEvent.key.deleteCircleEvent();
    }

    /**
     * delete a Circle Event, including removing the circle event in the queue and relationship.
     * */

    void deleteCircleEvent( EventRBTreeVoronoi eventQueue ) {
        key.deleteCircleEvent( eventQueue );
    }

    /**
     * get a bisector for an internal node, <pj, pi>
     * */

    public void getBisector() {
        assert key.type == EventSite.Type.INTERNAL;
        key.bisector = Lines.getBisector( key.leftArc.site, key.rightArc.site );
    }
}
