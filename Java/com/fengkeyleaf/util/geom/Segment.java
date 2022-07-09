package com.fengkeyleaf.util.geom;

/*
 * Segment.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.lang.MyMath;

import java.util.Comparator;

/**
 * Data structure of a segment to calculate intersection point
 * This class guarantees that left endpoint <= right endpoint.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Segment extends Ray
        implements IntersectionShape {

    public HalfEdge e;

    /**
     * Constructs to create an instance of InterLine.
     * */

    public Segment( double x1, double y1, double x2, double y2 ) {
        this( new Vector( x1, y1 ), new Vector( x2, y2 ) );
    }

    public Segment( double x1, double y1, double x2, double y2, Comparator<Vector> c ) {
        this( new Vector( x1, y1 ), new Vector( x2, y2 ), c );
    }

    public Segment( Vector startPoint, Vector endPoint ) {
        super( startPoint, endPoint );
    }

    public Segment( Line line ) {
        this( line.startPoint, line.endPoint );
    }

    public Segment( Vector s, Vector e, Comparator<Vector> c ) {
        super( presort( s, e, c ) );
    }

    @Override
    public Segment getSegment( BoundingBox box ) {
        return this;
    }

    /**
     * Generate eventPoint for this line
     *
     * Note that this preprocess will create new segments for the input ones.
     * i.e. the segment in the intersection point is not the original one passed into the algorithm.
     *
     * @param isXMonotone     true, preprocess the input shape as monotone relative to x-axis;
     *                        false, monotone relative to y-axis.
     * */

    @Override
    public EventPoint2D[] preprocess( boolean isXMonotone ) {
        Segment s = new Segment( startPoint, endPoint, isXMonotone ? Vectors::sortByX : Vectors::sortByY );
        // copy the half-edge of this segment. MapOverlay required.
        s.e = e;

        EventPoint2D left = new EventPoint2D( s.startPoint.x, s.startPoint.y,
                s, true );
        EventPoint2D right = new EventPoint2D( s.endPoint.x, s.endPoint.y,
                null, false );
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
     * this shape has the endPoint?
     * */

    @Override
    public boolean isSameEndPoint( Vector endPoint ) {
        return this.endPoint.equalsXAndY( endPoint );
    }

    /**
     * get Y of EndPoint
     * */

    @Override
    public Vector getEndPoint() {
        return endPoint;
    }


    /**
     * update the target point's x and y
     * based on the given update point,
     * using this line to calculate
     *
     * @param isUpdatingByX     update x and y based which, x or y?
     *                          true -> x; false -> y
     */

    @Override
    public void update( Vector target, Vector update,
                        boolean isUpdatingByX ) {

        // vertical line
        if ( isVertical ) {
            // given x == the x of the line?
            if ( MyMath.isEqual( update.x, verticalX ) ) {
                // yes, update Y
//                assert target.x == verticalX : target + " " + verticalX;
                target.y = update.y;
            }
            return;
        }
        // Horizontal line
        else if ( isHorizontal ) {
            // given y == the y of the line?
            if ( MyMath.isEqual( update.y, horizontalY ) ) {
                // yes, update x
                assert target.y == horizontalY;
                target.x = update.x;
            }
            return;
        }

        // neither vertical nor horizontal,
        // update y based on given x
        if ( isUpdatingByX ) target.setXAndY( update.x, updateY( update.x ) );
        else target.setXAndY( updateX( update.y ), update.y );
    }

    /**
     * update the target point's x and y
     * based on the given x, using this line to calculate
     * */

    @Override
    public void update( Vector target, double coor,
                        boolean isUpdatingByX ) {

        if ( isUpdatingByX ) {
            update( target, new Vector( coor, horizontalY ), true );
            return;
        }

        update( target, new Vector( verticalX, coor ), false );
    }

    @Override
    public Vector[] intersect( Intersection s ) {
        if ( s instanceof Segment )
            return new Vector[] { GeometricIntersection.segments( this, ( Segment ) s ) };
        else if ( s instanceof Arc )
            return GeometricIntersection.lineArc( this, ( Arc ) s );
        else if ( s instanceof Circle )
            return GeometricIntersection.segmentCircle( this, ( Circle ) s );

        // TODO: 5/30/2022 other intersection type.
        return new Vector[] {};
    }
}
