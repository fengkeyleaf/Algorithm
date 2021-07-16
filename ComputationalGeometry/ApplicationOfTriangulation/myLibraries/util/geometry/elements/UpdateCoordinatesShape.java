package myLibraries.util.geometry.elements;

/*
 * UpdateCoordinatesShape.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.elements.point.Vector;

/**
 * interface includes methods to update x or y
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public interface UpdateCoordinatesShape {

    /**
     * update the target point's x and y
     * based on the given x, using this line to calculate
     *
     * Interface definition
     *
     * @param isUpdatingByX     update x and y based on which, x or y?
     *                          true -> x; false -> y
     * */

    void updateYAndX( Vector target, Vector update, boolean isUpdatingByX );

    /**
     * update the target point's x and y
     * based on the given update point,
     * using this line to calculate
     *
     * Interface definition
     *
     * */

    void updateYAndX( Vector target, double x );
}
