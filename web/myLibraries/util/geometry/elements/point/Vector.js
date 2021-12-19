"use strict"

/*
 * Vector.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import MyMath from "../../../../lang/MyMath.js";
import Node from "../../../graph/elements/Node.js";

/**
 * Data structure of Vector, aka, 2D point
 *
 * The source code in java is from my now github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/elements/point/Vector.java>Vector</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Vector extends Node {
    // Vector
    static origin = new Vector( 0, 0, -1 );
    static _minsID = -1;
    static _IDStatic = 0;

    /**
     * Constructs to create an instance of Vector
     *
     * @param {Number} x
     * @param {Number} y
     * @param {Number} ID
     * */

    constructor( x, y, ID = Vector._IDStatic++ ) {
        super( ID, null );
        console.assert( x !== null );
        console.assert( y !== null );
        this.x = x;
        this.y = y;
    }

    /**
     * set X And Y
     *
     * @param {Number} x
     * @param {Number} y
     * */

    setXAndY( x, y ) {
        this.x = x;
        this.y = y;
    }

    /**
     * vector addition
     *
     * @param {Vector} vector
     * */

    add( vector ) {
        return new Vector( this.x + vector.x, this.y + vector.y, Vector._minsID-- );
    }

    /**
     * vector subtract
     *
     * @param {Vector} vector
     * */

    subtract( vector ) {
        return new Vector( this.x - vector.x, this.y - vector.y, Vector._minsID-- );
    }

    /**
     * vector multiplication
     *
     * @param {number} ratio
     * */

    multiply( ratio ) {
        return new Vector( ratio * this.x, ratio * this.y, Vector._minsID-- );
    }

    /**
     * vector division
     *
     * @param {number} ratio
     * */

    division( ratio ) {
        return new Vector( this.x / ratio, this.y / ratio, Vector._minsID-- );
    }

    /**
     * vector's norm, but without radical
     * */

    normWithoutRadical() {
        return this.x * this.x + this.y * this.y;
    }

    /**
     * vector's norm
     * */

    norm() {
        return Math.sqrt( this.normWithoutRadical() );
    }

    __equalsXAndY( x, y ) {
        return MyMath.isEqualZero( x - this.x ) &&
            MyMath.isEqualZero( y - this.y );
    }

    /**
     * Are x and y of this vector and the vector same?
     *
     * @param {Vector} vector
     * */

    equalsXAndY( vector ) {
        if ( vector == null ) return false;
        return this.__equalsXAndY( vector.x, vector.y );
    }

    /**
     * @param {Object} o
     * */

    equals( o ) {
        if ( this === o ) return true;
        if ( o == null || !( this instanceof Vector ) ) return false;
        return this.equalsXAndY( o );
    }

    toStringNormalWithoutID() {
        return this.x + "|" + this.y;
    }

    toString() {
        return this.toStringNormalWithoutID();
    }
}
