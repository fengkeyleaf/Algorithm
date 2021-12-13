"use strict"

import MapTreeNode from "./MapTreeNode.js";
import CompareElement from "../../CompareElement.js";

/**
 * Data structure of Red Black Tree Node
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/tree/RedBlackTree.java>RedBlackTree</a>
 *
 * Inner class
 * */

export default class RedBlackTreeNode extends MapTreeNode {
    /**
     * node color
     * */

    static Color = {
        RED: 0, BLACK: 1, NONE: -1
    }

    /**
     * constructs to create an instance of Node
     * */

    constructor( paras ) {
        super( paras );
        this.color = CompareElement.chooseWhich( paras.color, RedBlackTreeNode.Color.NONE );
        console.assert( !( this.color === RedBlackTreeNode.Color.NONE ) );
    }

    toString() {
        return super.toString();
    }
}