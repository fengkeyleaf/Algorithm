"use strict"

/*
 * MonotoneVertex.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Vertex from "./Vertex.js";
import Triangles from "../tools/Triangles.js";
import MyMath from "../../../lang/MyMath.js";

/**
 * Data structure of Monotone Vertex
 * for partitioning monotone subpolygon
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/DCEL/MonotoneVertex.java>MonotoneVertex</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class MonotoneVertex extends Vertex {
    static LEFT_CHAIN_VERTEX = true;
    static RIGHT_CHAIN_VERTEX = false;

    static LEFT_CHAIN_VERTEX_COLOR = [ 255 / 255, 0, 102 / 255, 1 ];
    static RIGHT_CHAIN_VERTEX_COLOR = [ 0, 204 / 255, 255 / 255, 1 ];

    /**
     * enumerative Vertex Type for partitioning monotone subpolygons
     * */

    static VertexType = {
        START: 0, SPLIT: 1, // 0, 1
        END: 2, MERGE: 3, // 2, 3
        REGULAR_LEFT: 4, REGULAR_RIGHT: 5, // 4
    };

    /**
     * constructs to create an instance of MonotoneVertex
     *
     * @param {Number} x
     * @param {Number} y
     * */

    constructor( x, y ) {
        super( x, y );
        this.isLeftChainVertex = false;
        this.vertexType = -1;
    }

    /**
     * this vertex is a split or merge one
     * when the angle it forms is greater than pi.
     * */

    isSplitOrMergeVertex() {
        return MyMath.isNegative(
            Triangles.areaTwo( this.incidentEdge.prev.origin, this.incidentEdge.origin,
                this.incidentEdge.next.origin ) );
    }

    /**
     * are both vertices on the same monotone chain?
     *
     * @param {MonotoneVertex} vertex
     * */

    isOnTheDifferentChain( vertex ) {
        return this.isLeftChainVertex !== vertex.isLeftChainVertex;
    }

}
