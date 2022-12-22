package com.fengkeyleaf.util.geom;

/*
 * IntersectionShape.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import java.util.List;

/**
 * <p>Interface to calculate intersection point.
 * This interface must be implemented by intersection shape class
 * if one wants to feed shapes with {@link GeometricIntersection#findIntersection(List)} algorithm, ( Bentley Ottmann ),</p>
 *
 * <p>Generally speaking, the intersection shape must be monotone relative to x-axis,
 * and have the ability to compute intersection with other shape,
 * also with other abilities defined in the interface.</p>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public interface IntersectionShape extends Intersection {

    /**
     * generate eventPoint for this shape
     *
     * Interface definition
     *
     * @param isXMonotone     true, preprocess the input shape as monotone relative to x-axis;
     *                        false, monotone relative to y-axis.
     * */

    EventPoint2D[] preprocess( boolean isXMonotone );

    /**
     * this shape has the endPoint?
     *
     * Interface definition
     * */

    boolean isSameEndPoint( Vector endPoint );

    /**
     * does the give point on this shape?
     * This one is not necessary for the algorithm, it's just for checking.
     *
     * Interface definition
     * */

    boolean ifOnThisShape( Vector vector );

    /**
     * get Y of EndPoint.
     * This one is not necessary for the algorithm, it's just for checking.
     *
     * Interface definition
     * */

    Vector getEndPoint();

    /**
     * update the target point's x and y
     * based on the given update point,
     * using this shape to calculate
     *
     * Interface definition
     *
     * @param isUpdatingByX     update x and y based on which, x or y?
     *                          true(vertical sweep line) -> x;
     *                          false(horizontal sweep line) -> y
     * */

    void update( Vector target, Vector update, boolean isUpdatingByX );

    /**
     * update the target point's x and y based on the given coor.
     *
     * Interface definition
     *
     * @param coor updating coordinate, x-coor or y-coor
     * @param isUpdatingByX     update x and y based on which, x or y?
     *                          true(vertical sweep line) -> x;
     *                          false(horizontal sweep line) -> y
     * */

    void update( Vector target, double coor, boolean isUpdatingByX );
}
