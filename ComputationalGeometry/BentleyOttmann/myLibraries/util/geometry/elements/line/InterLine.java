package myLibraries.util.geometry.elements.line;

/*
 * InterLine.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.elements.cycle.InterArc;
import myLibraries.util.geometry.elements.point.EventPoint2D;
import myLibraries.util.geometry.elements.IntersectionShape;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.GeometricIntersection;

/**
 * Data structure of a line to calculate intersection point
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

// TODO: 6/19/2021 super class of InterSegment and InterRay 
public class InterLine extends Line implements IntersectionShape {
    public static final ShapeType type = ShapeType.LINE;

    /**
     * Constructs to create an instance of InterLine
     * */

    public InterLine( Vector startPoint, Vector endPoint ) {
        super( startPoint, endPoint );
    }

    public InterLine( Line line ) {
        this( line.startPoint, line.endPoint );
    }

    /**
     * generate eventPoint for this line
     * */

    @Override
    public EventPoint2D[] preprocess() {
        EventPoint2D left = new EventPoint2D( startPoint.x, startPoint.y,
                this, EventPoint2D.EventType.LEFT );
        left.shapes.add( this );
        EventPoint2D right = new EventPoint2D( endPoint.x, endPoint.y,
                null, EventPoint2D.EventType.OTHER );
        return new EventPoint2D[] { left, right };
    }

    /**
     * does the give point on this line?
     * */

    @Override
    public boolean ifOnThisShape( Vector vector ) {
        return isOnThisSegment( vector );
    }

    /**
     * find Intersection for this shape
     * */

    @Override
    public Vector[] findIntersection( IntersectionShape shape ) {
        // line intersection
        if ( shape.getShapeType() == ShapeType.LINE ) {
            Vector intersection = GeometricIntersection.lineIntersect( this, ( InterLine ) shape );
            return intersection == null ?
                    null : new Vector[] { intersection };
        }

        // line and cycle intersection
        Line intersections = GeometricIntersection.lineArcIntersect( this, ( InterArc ) shape );
        return intersections == null ?
                null : new Vector[] { intersections.startPoint, intersections.endPoint };
    }

    /**
     * this shape has the endPoint?
     * */

    @Override
    public boolean isSameEndPoint( Vector endPoint ) {
        return this.endPoint.equalsXAndY( endPoint );
    }

    /**
     * get Shape Type
     * */

    @Override
    public ShapeType getShapeType() {
        return type;
    }

    /**
     * get Y of EndPoint
     * */

    @Override
    public double getEndPointY() {
        return endPoint.y;
    }

    /**
     * update the target point's x and y
     * based on the given x, using this line to calculate
     * */

    @Override
    public void updateYAndX( Vector target, double x ) {
        if ( isVertical ) return;
        else if ( isHorizontal ) {
            target.x = x;
            return;
        }

        target.setXAndY( x, updateY( x ) );
    }
}
