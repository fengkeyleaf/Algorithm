package myLibraries.util.geometry.elements;

/*
 * IntersectionShape.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.elements.point.EventPoint2D;
import myLibraries.util.geometry.elements.point.Vector;

import java.util.List;

/**
 * Interface to calculate intersection point
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public interface IntersectionShape {
    enum ShapeType {
        LINE, ARC
    }

    /**
     * this intersection only have cycles?
     */

    static boolean isAllTheCycles( List<EventPoint2D> eventPoint2DS ) {
        for ( EventPoint2D eventPoint2D : eventPoint2DS ) {
            if ( eventPoint2D.shape.getShapeType() == ShapeType.LINE )
                return false;
        }

        return true;
    }

    /**
     * generate eventPoint for this shape
     *
     * Interface definition
     * */

    EventPoint2D[] preprocess();

    /**
     * this shape has the endPoint?
     *
     * Interface definition
     * */

    boolean isSameEndPoint( Vector endPoint );

    /**
     * does the give point on this shape?
     *
     * Interface definition
     * */

    boolean ifOnThisShape( Vector vector );

    /**
     * find Intersection for this shape
     *
     * Interface definition
     * */

    Vector[] findIntersection( IntersectionShape shape );

    /**
     * get Shape Type
     *
     * Interface definition
     * */

    ShapeType getShapeType();

    /**
     * get Y of EndPoint
     *
     * Interface definition
     * */

    double getEndPointY();

    /**
     * update the target point's x and y
     * based on the given x, using this line to calculate
     *
     * Interface definition
     * */

    void updateYAndX( Vector target, Vector update );

    /**
     * update the target point's x and y
     * based on the given update point,
     * using this line to calculate
     *
     * Interface definition
     * */

    void updateYAndX( Vector target, double x );
}
