package com.fengkeyleaf.util.geom;

/*
 * LineNode.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/25/2022$
 */

import java.util.List;

/**
 * Class to store an endpoint of line.
 * And this class is mainly used in windowing query,
 * including stabbing query and orthogonal windowing query.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

class LineNode extends Vector {
    // endpoint.
    final Vector p;
    // left or right end point.
    final boolean isLeft;
    // the segment.
    final Line l;
    // twin node storing another endpoint.
    LineNode twin;
    List<LineNode> L;
    boolean isNotAdded = true;

    LineNode( Vector p, boolean isLeft, Line l ) {
        super( p );

        this.p = p;
        this.isLeft = isLeft;
        this.l = l;
    }

    // node with p as the intersection point of a line, l, and another.
    LineNode( Vector p, Line l ) {
        super( p );

        this.p = p;
        isLeft = true;
        this.l = l;
        twin = this;
    }

    void setTwin( LineNode n ) {
        twin = n;
        n.twin = this;
    }

    boolean isNotAdded() {
        return isNotAdded && twin.isNotAdded;
    }

    /**
     * add all LineNode to this node. They have the same point signature,
     * either endpoint or intersection point.
     */

    Vector addAll( List<LineNode> L ) {
        assert !L.isEmpty();
        this.L = L;
        return this;
    }

    @Override
    public String toString() {
        return l.startPoint + ( isLeft ? "*" : "" ) + "<->" + l.endPoint + ( !isLeft ? "*" : "" );
    }

}
