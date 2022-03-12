package myLibraries.util.geometry.elements;

/*
 * InterLine.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.DCEL.HalfEdge;
import myLibraries.util.geometry.GeometricIntersection;

/**
 * Data structure of a line to calculate intersection point
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class InterLine extends Segment implements IntersectionShape {
    public static final ShapeType type = ShapeType.LINE;
    public final HalfEdge dangling;

    /**
     * Constructs to create an instance of InterLine
     * */

    public InterLine( Vector startPoint, Vector endPoint, HalfEdge dangling ) {
        super( sortedByX( startPoint, endPoint ) );
        this.dangling = dangling;
    }

    public InterLine( Line line ) {
        this( line.startPoint, line.endPoint, null );
    }

    public InterLine( Line line, HalfEdge dangling ) {
        this( line.startPoint, line.endPoint, dangling );
    }

    public InterLine( Vector startPoint, Vector endPoint  ) {
        this( startPoint, endPoint, null );
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
            Vector intersection = segmentIntersect( ( InterLine ) shape );
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
}
