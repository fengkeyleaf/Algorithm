"use strict"

/*
 * ball.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import RigidSphere from "../../../myLibraries/animation/RigidSphere.js";

/**
 * data structure of a ball
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Ball extends RigidSphere {

    /**
     * @param {Mesh} mesh
     * @param {Number} mass kg
     * @param {Number} radius
     * @param {String} name
     *
     */

    constructor( mesh, mass, radius, name ) {
        super( mesh, mass, radius, name );
    }

    /**
     * @param {Vector3} pos
     */

    removeFromTable( pos ) {
        this.reset();
        this.isStatic = true;
        this.mesh.position.set( pos.x, pos.y, pos.z );
    }
}