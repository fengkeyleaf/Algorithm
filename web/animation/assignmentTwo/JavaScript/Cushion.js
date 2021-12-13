"use strict"

/*
 * Cushion.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import CollidingObject from "../../../myLibraries/animation/CollidingObject.js";

/**
 * data structure of a Cushion
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

// TODO: 12/6/2021 rotate a cushion also rotate its normal vector
export default class Cushion extends CollidingObject {
    static typeID = CollidingObject.Types.Cushion;

    /**
     * @param {Mesh} mesh
     * @param {Number} mass kg
     * @param {Vector3} n normal to this cushion
     * @param {String} name
     *
     */

    constructor( mesh, mass, n, name ) {
        super( mesh, mass, name, Cushion.typeID );
        super.isStatic = true;

        // vector normal to this cushion
        this.n = n.clone();
    }

    /**
     * @param {Vector3} axis
     * @param {Number} angle in radians
     */

    rotateOnAxis( axis, angle ) {
        this.mesh.rotateOnAxis( axis, angle );
        // this.n.applyAxisAngle( axis, angle );
    }

    /**
     * @param {Ball} ball
     */

    lineOfAction( ball ) {
        return this.n.clone();
    }
}