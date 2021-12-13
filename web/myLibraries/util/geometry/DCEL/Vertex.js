"use strict"

/*
 * Vertex.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Vector from "../elements/point/Vector.js";

/**
 * Data structure of vertex for DCEL
 *
 * The source code in java is from my own github:
 * @see <a href=>Vertex</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Vertex extends Vector {
    static IDStatic = 0;
    static NORMAL_COLOR = [ 136 / 255, 136 / 255, 136 / 255, 1 ];

    /**
     * constructs to create an instance of Vertex
     *
     * @param {Number} x
     * @param {Number} y
     * */

    constructor( x, y ) {
        super( x, y, Vertex._IDStatic++ );
        this.incidentEdge = null;
    }
}

