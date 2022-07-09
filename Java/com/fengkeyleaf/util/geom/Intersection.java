package com.fengkeyleaf.util.geom;

/*
 * Intersection.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/30/2022$
 */

/**
 * interface to define intersect method for shape.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public interface Intersection {
    Vector[] intersect( Intersection s );
}
