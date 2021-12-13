"use strict"

/*
 * Node.java
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 basic structure:
 *      super class for tree node and vertex on 4/3/2021$
 *     $1.1 super class for Vector and added mapping ID on 7/11/2021$
 */

/**
 * Data structure of a general node
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Node {

    /**
     * constructs to create an instance of Node
     * */

    constructor( ID, parent ) {
        // unique identifying number
        this.ID = ID;
        // mapping ID,
        // sometimes we can't use ID
        // to map this node to something else,
        // in this case, we use this mappingID to do so.
        // note that must reset this ID to -1,
        // after using it, since in some case,
        // we will use -1 to identify something,
        // so we need guarantee mapping ID to be -1,
        // every time we use it
        this.mappingID = - 1;
        // parent node of this node
        this.parent = parent;
    }


    /**
     * reset mapping ID to -1
     */

    resetMappingID( nodes ) {
        nodes.forEach( n => n.mappingID = - 1 );
    }

    toString() {
        return this.ID.valueOf();
    }
}
