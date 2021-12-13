"use strict"

/*
 * EventEdge.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Vector from "./Vector.js";
import CompareElement from "../../../CompareElement.js";

/**
 * Data structure of Event edge for partitioning monotone subpolygon
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/elements/point/EventEdge.java>EventEdge</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class EventEdge extends Vector {
    static IDStatic = 0;

    /**
     * Constructs to create an instance of EventEdge
     * */

    constructor( paras ) {
        super( CompareElement.chooseWhich( paras.x, null ),
            CompareElement.chooseWhich( paras.y, null ), EventEdge._IDStatic++ );
        // line
        this.shape = CompareElement.chooseWhich( paras.shape, null );
        // helper for split vertex, but maybe a merge vertex
        this.vertex = CompareElement.chooseWhich( paras.vertex, null );
    }

    /**
     * update Y And X with the shape in this EventEdge
     *
     * @param {Vector} update
     * */

    updateYAndX( update ) {
        if ( this.shape.outOfRangeX( update.x ) ) return;
        this.shape.updateYAndX( this, update, false );
    }

    /**
     * update Y with given X with the shape in this EventEdge
     *
     * @param {Number} x
     * */

    updateY( x ) {
        if ( this.shape.outOfRangeX( x ) ) return;
        this.shape.updateYAndXByX( this, x );
    }

    toString() {
        return super.toString() + "(h:" + this.vertex.valueOf() + ")";
    }
}
