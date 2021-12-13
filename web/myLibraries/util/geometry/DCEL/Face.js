"use strict"

/*
 * Face.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

/**
 * Data structure of Face of DCEL
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/DCEL/Face.java>Face</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Face {
    static IDStatic = 0;

    /**
     * constructs to create an instance of Vertex
     *
     * @param {HalfEdge} outComponent
     * */

    constructor( outComponent ) {
        this.outComponent = outComponent;
        this.innerComponents = [];
        this.ID = Face.IDStatic++;
        this.IDOfDualVertex = -1;
    }

    static resetIDStatic() {
        Face.IDStatic = 0;
    }

    /**
     * @param {HalfEdge} halfEdge
     * */

    addInnerComponent( halfEdge ) {
        this.innerComponents.add( halfEdge );
    }
}
