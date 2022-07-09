package com.fengkeyleaf.util.tree;

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
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// TODO: 11/5/2021 implement Collection<E> in JDK
// TODO: 4/23/2022 necessary class?
public abstract class AbstractTree<E> {
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
