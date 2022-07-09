package com.fengkeyleaf.util.tree;

/*
 * PerfectBinaryTree.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of PerfectBinaryTree.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class PerfectBinaryTree<E> extends AbstractTree<E> {
    // array to represent a Perfect Binary tree
    protected final List<E> tree;

    /**
     * constructs to create an instance of PerfectBinaryTree
     * */

    public PerfectBinaryTree() {
        this.tree = new ArrayList<>();
    }

    public PerfectBinaryTree( int capacity ) {
        this.tree = new ArrayList<>( capacity );
    }

    /**
     * Special constructor to initialize
     * a perfect binary tree directly from an array.
     * */

    protected PerfectBinaryTree( List<E> tree ) {
        this.tree = tree;
    }

    /**
     * get index for a parent element
     * */

    protected static
    int getParentIndex( int child ) {
        return ( child - 1 ) >> 1;
    }

    /**
     * get index for children
     * */

    protected static
    int getChildrenIndex( int parent, boolean left ) {
        parent <<= 1;
        return left ? parent + 1 : parent + 2;
    }

    protected static
    int getLeftChild( int p ) {
        return ( p << 1 ) + 1;
    }

    protected static
    int getRightChild( int p ) {
        return ( p << 1 ) + 2;
    }

    /**
     * is this node a leaf one ( represented as index )?
     * */

    protected boolean isLeaf( int parent ) {
        return getLeftChild( parent ) >= tree.size() ||
                getRightChild( parent ) >= tree.size();
    }

    @Override
    public int size() {
        return tree.size();
    }

    /**
     * print this heap tree using level traversal
     * */

    @Override
    public String toString() {
        return tree.toString();
    }
}
