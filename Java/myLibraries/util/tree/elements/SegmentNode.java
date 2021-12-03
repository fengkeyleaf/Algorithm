package myLibraries.util.tree.elements;

/*
 * SegmentNode.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 max, min on 8/20/2021$
 */

import myLibraries.util.Node;

/**
 * Data structure of SegmentNode
 * to support range minimum query and
 * range maximum query in segment tree
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class SegmentNode<E> extends Node {
    private static int IDStatic = 0;
    public E max;
    public E min;

    /**
     * constructs to create an instance of SegmentNode
     * */

    public SegmentNode() {
        super( IDStatic++ );
    }

    public SegmentNode( E threshold ) {
        super( IDStatic++ );
        this.max = this.min = threshold;
    }

    public SegmentNode( E minThreshold,
                        E maxThreshold ) {
        super( IDStatic++ );
        this.max = maxThreshold;
        this.min = minThreshold;
    }

    public SegmentNode( int ID ) {
        super( ID );
    }

    public SegmentNode( int ID, E maxThreshold,
                        E minThreshold ) {
        super( ID );
        this.max = maxThreshold;
        this.min = minThreshold;
    }

    private String toStringTwo() {
        return min + "|" + max;
    }

    private String toStringOne() {
        return min + "";
    }

    @Override
    public String toString() {
        return min == max ? toStringOne() : toStringTwo();
    }
}
