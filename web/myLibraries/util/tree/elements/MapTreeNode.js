"use strict"

/*
 * MapTreeNode.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Node from "../../graph/elements/Node.js";
import CompareElement from "../../CompareElement.js";

/**
 * Data structure of tree node with mapping, key -> value
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/tree/elements/MapTreeNode.java>MapTreeNode</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class MapTreeNode extends Node {
    static IDStatic = 0;

    /**
     * constructs to create an instance of Node
     * */

    constructor( paras ) {
        super( CompareElement.chooseWhich( paras.ID, MapTreeNode.IDStatic-- ),
            CompareElement.chooseWhich( paras.parent, null ) );
        this.key = CompareElement.chooseWhich( paras.key, null );
        this.val = CompareElement.chooseWhich( paras.val, null );
        this.left = CompareElement.chooseWhich( paras.left, null );
        this.right = CompareElement.chooseWhich( paras.right, null );
        // including this node itself
        this.numberOfChildren = 1;
    }

    static swap( node1, node2 ) {
        let temp = new MapTreeNode( -1, node1.key, node1.val );
        node1.replace( node2 );
        node2.replace( temp );
    }

    /**
     * replace anther node at this node
     * */

    replace( node ) {
        this.val = node.val;
        this.key = node.key;
    }

    toStringWithoutKey() {
        return this.val.valueOf();
    }

    toStringWithoutID() {
        return "{" + this.key + "->" + this.val + "}";
    }

    toString() {
        return this.toStringWithoutID();
    }
}
