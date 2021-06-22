package myLibraries.util.graph.elements;

/*
 * Node.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $basic structure:
 *      super class for tree node and vertex on 4/3/2021$
 */

/**
 * Data structure of a general node
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Node {
    public final int ID;
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

    @Override
    public String toString() {
        return String.valueOf( ID );
    }
}
