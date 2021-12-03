package myLibraries.util.geometry.elements.circle;

/*
 * InterArc.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.elements.point.EventPoint2D;
import myLibraries.util.geometry.elements.IntersectionShape;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.elements.line.InterLine;
import myLibraries.util.geometry.elements.line.Line;
import myLibraries.util.geometry.tools.GeometricIntersection;

/**
 * Data structure of a Arc to calculate intersection point
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class InterArc extends Arc implements IntersectionShape {
    public static final ShapeType type = ShapeType.ARC;

    /**
     * Constructs to create an instance of InterArc
     *
     * x's range: left to right;
     * y's range: up to down;
     * */

    public InterArc( Vector center, double radius,
               double xRangeMax, double xRangeMin,
               double yRangeMax, double yRangeMin,
               Vector startPoint, Vector endPoint ) {
        super( center, radius, xRangeMax, xRangeMin, yRangeMax, yRangeMin, startPoint, endPoint );
    }

    public InterArc( Vector center, double radius ) {
        super( center, radius, 0, 0, 0, 0, null, null );
    }

    public InterArc( Arc arc ) {
        super( arc.center, arc.radius, arc.xRange[ 1 ], arc.xRange[ 0 ], arc.yRange[ 0 ], arc.yRange[ 1 ], arc.startPoint, arc.endPoint );
    }

    public InterArc( Circle circle ) {
        this( circle.center, circle.radius );
    }

    /**
     * generate eventPoint for this cycle
     * */

    @Override
    public EventPoint2D[] preprocess() {
        Arc[] arcs = getFourQuarters();
        EventPoint2D[] eventPoint2DS = new EventPoint2D[ 8 ];
        int index = 0;
        for ( Arc arc : arcs ) {
            InterArc interArc = new InterArc( arc );
            EventPoint2D left = new EventPoint2D( arc.startPoint.x, arc.startPoint.y,
                    interArc, EventPoint2D.EventType.LEFT );
            left.shapes.add( interArc );
            EventPoint2D right = new EventPoint2D( arc.endPoint.x, arc.endPoint.y,
                    null, EventPoint2D.EventType.OTHER );
            eventPoint2DS[ index++ ] = left;
            eventPoint2DS[ index++ ] = right;
        }

        return eventPoint2DS;
    }

    /**
     * does the give point on this arc?
     * */

    @Override
    public boolean ifOnThisShape( Vector vector ) {
        return belong( vector );
    }

    /**
     * find Intersection for this shape
     * */

    @Override
    public Vector[] findIntersection( IntersectionShape shape ) {
        // no cycle intersection in this problem
        if ( shape.getShapeType() == ShapeType.ARC ) return null;

        // line and cycle intersection
        Line intersection = GeometricIntersection.lineArcIntersect( ( InterLine ) shape, this );
        return intersection == null ?
                null : new Vector[] { intersection.startPoint, intersection.endPoint };
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

    public static
    void main( String[] args ) {
        int ID = 0;
        Vector temp = new Vector( 0, 0, ID++ );
        Circle circle1 = new Circle( Vector.origin, 1 );
        Arc[] arcs = circle1.getFourQuarters();
        for ( Arc arc : arcs ) {
            arc.updateYAndX( temp, 0.5 );
            System.out.println( temp );
            temp.setXAndY( 0, 0 );
        }

        System.out.println();
        for ( Arc arc : arcs ) {
            arc.updateYAndX( temp, -0.5 );
            System.out.println( temp );
            temp.setXAndY( 0, 0 );
        }

        System.out.println();
        Vector vector1 = new Vector( 0, 1, ID++ );
        Circle circle2 = new Circle( vector1, 1 );
        arcs = circle2.getFourQuarters();
        for ( Arc arc : arcs ) {
            arc.updateYAndX( temp, 0.5 );
            System.out.println( temp );
            temp.setXAndY( 0, 0 );
        }

        System.out.println();
        for ( Arc arc : arcs ) {
            arc.updateYAndX( temp, -0.5 );
            System.out.println( temp );
            temp.setXAndY( 0, 0 );
        }
    }
}
