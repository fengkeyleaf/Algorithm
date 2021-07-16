package myLibraries.util.tree;

/*
 * AbstractTree.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

/**
 * Abstract data structure of tree
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public abstract class AbstractTree {
    // nodes' ID
    protected static int ID = 0;

    /**
     * size of this tree
     *
     * Abstract definition
     * */

    public abstract int size();

    /**
     * is this tree empty?
     * */

    public boolean isEmpty() {
        return size() == 0;
    }
}
