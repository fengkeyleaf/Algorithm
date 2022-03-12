package myLibraries.util;

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
 *
 * JDK: 15
 */

import java.util.List;

/**
 * Data structure of a general node
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Node {
    // unique identifying number
    public final int ID;
    // mapping ID,
    // sometimes we can't use ID
    // to map this node to something else,
    // in this case, we use this mappingID to do so.
    // Note that must reset this ID to -1,
    // after using it, since in some case,
    // we will use -1 to identify something,
    // so we need guarantee mapping ID to be -1,
    // every time we wanna to use it.
    public int mappingID = -1;
    // parent/predecessor node of this node
    public Node parent = null;

    /**
     * constructs to create an instance of Node
     * */

    public Node( int ID ) {
        this.ID = ID;
    }

    public Node( int ID, Node parent ) {
        this( ID );
        this.parent = parent;
    }

    /**
     * reset mapping ID to -1
     */

    public static<N extends Node>
    void resetMappingID( List<N> nodes ) {
        nodes.forEach( n -> n.mappingID = -1 );
    }

    /**
     * @param initID initializing mapping ID, usually starting with 0.
     */

    public static<N extends Node>
    void setMappingID( List<N> nodes, int initID ) {
        for ( Node n : nodes ) n.mappingID = initID++;
    }

    @Override
    public String toString() {
        return String.valueOf( ID );
    }
}
