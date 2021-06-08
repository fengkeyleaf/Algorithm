package myLibraries.util.geometry.tools;

/*
 * GeometricIntersection.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.*;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

/**
 * Provide algorithms related to Geometric Intersection
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class GeometricIntersection {
    public static int ID = -3;

    /**
     * get the intersection point of line and arc with vector, if exists
     */

    public static
    Line<Vector> lineArcIntersect( Line<Vector> line, Arc arc ) {
        if ( line == null || arc == null ) return null;

        // get the intersection points of the cycle of the arc and the line
        Line<Vector> segment = lineCycleIntersect( line, arc.cycle );
        Vector intersection1 = null;
        Vector intersection2 = null;
        // those points lay on the arc?
        // note that there are at most two intersection points, not only one
        if ( arc.belong( segment.startPoint ) )
            intersection1 = segment.startPoint;
        if ( arc.belong( segment.endPoint ) )
            intersection2 = segment.endPoint;

        return new Line<>( intersection1, intersection2 );
    }

    /**
     * get the intersection point of line and cycle with vector, if exists
     *
     * Reference resource:
     * https://blog.csdn.net/qq_40998706/article/details/87521165
     */

    public static
    Line<Vector> lineCycleIntersect( Line<Vector> line, Cycle cycle ) {
        if ( line == null || cycle == null
                || !cycle.ifIntersectsLine( line ) ) return null;

        Vector projectPoint = line.project( cycle.center );
        Vector lineVector = line.getVector();
        Vector e = lineVector.division( lineVector.norm() );
        float hypotenuse = cycle.radius * cycle.radius -
                projectPoint.subtract( cycle.center ).normWithoutRadical();
        float ratio = ( float ) Math.sqrt( Math.abs( hypotenuse ) );
        Vector distance = e.multiply( ratio );
        return new Line<>( projectPoint.add( distance ), projectPoint.subtract( distance ) );
    }

    /**
     * toLeft test to check if two lines intersect
     */

    private static
    boolean ifLinesIntersect( float res1, float res2 ) {
        // parallel cases:
        // case 1: overlap.
        if ( MyMath.equalFloats( res1, 0 ) &&
                MyMath.equalFloats( res2, 0 ) )
            return false;
        // case 2: parallel on the right side.
        if ( res1 < 0 && res2 < 0 )
            return false;
        // case 3: parallel on the left side.
        if ( res1 > 0 && res2 > 0 )
            return false;

        // intersecting cases: either intersect at
        // a common point other than endpoints,
        // or at one of the endpoints.
        return true;
    }

    /**
     * line1 and line2 intersects?
     */

    public static
    boolean ifLinesIntersect( Line<Vector> line1, Line<Vector> line2 ) {
        if ( line1 == null || line2 == null ) return false;

        // to left test based on line1.
        float res1 = Triangle.areaTwo( line1.endPoint, line1.startPoint, line2.endPoint );
        float res2 = Triangle.areaTwo( line1.endPoint, line1.startPoint, line2.startPoint );
        // to left test based on line2.
        float res3 = Triangle.areaTwo( line2.endPoint, line2.startPoint, line1.endPoint );
        float res4 = Triangle.areaTwo( line2.endPoint, line2.startPoint, line1.startPoint );

        boolean finalRes1 = ifLinesIntersect( res1, res2 );
        boolean finalRes2 = ifLinesIntersect( res3, res4 );
        // verify the answer.
        assert finalRes1 == finalRes2;
        return finalRes1;
    }

    /**
     * get line intersection point with vector, if exists
     *
     * Reference resource:
     * https://blog.csdn.net/qq_40998706/article/details/87482435
     */

    public static
    Vector linesIntersect( Line<Vector> line1, Line<Vector> line2 ) {
        if ( !ifLinesIntersect( line1, line2 ) ) return null;

        Vector base = line2.getVector();
        float d1 = Math.abs( Vector.cross(
                base, line1.startPoint.subtract( line2.startPoint ) ) );
        float d2 = Math.abs( Vector.cross(
                base, line1.endPoint.subtract( line2.startPoint ) ) );
        float t = d1 / ( d1 + d2 );
        Vector intersection = line1.getVector().multiply( t );

        // the following commented-out code is correct as well,
        // but with less computational accuracy because of ( 1 / t ),
        // one additional division compared to the method above.
//        float t = ( d1 + d2 ) / d1;
//        Vector intersection = line1.endPoint.subtract(
//        line1.startPoint ).multiply( 1 / t );

        return line1.startPoint.add( intersection );
    }

    /**
     * point0.leftEnd -> p0, point0.rightEnd -> p1,
     * point1.leftEnd -> p2, point1.rightEnd -> p3,
     *
     * Reference resource:
     * https://www.cnblogs.com/pluslius/p/13800167.html
     * */

    public static
    void linesIntersect( LinePoint2D point0, LinePoint2D point1 ) {
        float A1 = point0.rightEnd.y - point0.leftEnd.y;
        float B1 = point0.leftEnd.x - point0.rightEnd.x;
        float C1 = A1 * point0.leftEnd.x + B1 * point0.leftEnd.y;
        float A2 = point1.rightEnd.y - point1.leftEnd.y;
        float B2 = point1.leftEnd.x - point1.rightEnd.x;
        float C2 = A2 * point1.leftEnd.x + B2 * point1.leftEnd.y;
        float denominator = A1 * B2 - A2 * B1;

        float x = ( B2 * C1 - B1 * C2 ) / denominator;
        float y = ( A1 * C2 - A2 * C1 ) / denominator;

        point0.setXAndY( x, y );
        point1.setXAndY( x, y );
    }

    public static
    boolean ifLinesIntersection( LinePoint2D event, LinePoint2D other ) {
        if ( other == null ) return false;

        boolean oneSide = Triangle.toLeftRigorously( event.leftEnd, event.rightEnd, other.leftEnd ) &&
                Triangle.toLeftRigorously( event.leftEnd, event.rightEnd, other.rightEnd );
        boolean otherSide = Triangle.toLeftRigorously( other.leftEnd, other.rightEnd, other.leftEnd ) &&
                Triangle.toLeftRigorously( other.leftEnd, other.rightEnd, other.rightEnd );
        assert oneSide == otherSide;
        return oneSide;
    }

    public static
    List<LinePoint2D> BentleyOttmann( List<LinePoint2D> points ) {
        PriorityQueue<LinePoint2D> events = new PriorityQueue<>( points.size(), Vector::sortByX );
        TreeSet<LinePoint2D> statuses = new TreeSet<>( Vector::sortByY );

        events.addAll( points );
        List<LinePoint2D> intersections = new ArrayList<>();

        /*while ( !events.isEmpty() ) {
            LinePoint2D event = events.poll();
            if ( event.type == LEFT ) {
                LinePoint2D successor = statuses.higher( event );
                LinePoint2D predecessor = statuses.lower( event );

                if ( ifLinesIntersection( event, successor ) ) {
                    statuses.remove( successor );
                    lineIntersect( event, successor );
                    intersections.add( intersection );
                    events.add( intersection );
                }

                if ( ifLinesIntersection( event, predecessor ) ) {
                    lineIntersect( event, successor );
                    intersections.add( intersection );
                    events.add( intersection );
                }

                statuses.add( event );
            }
            else if ( event.type == RIGHT ) {
                statuses.remove( event );
                LinePoint2D successor = statuses.higher( event );
                LinePoint2D predecessor = statuses.lower( event );
                if ( ifLinesIntersection( successor, predecessor ) ) {
                    LinePoint2D intersection = lineIntersect( successor, predecessor );
                    intersections.add( intersection );
                    events.add( intersection );
                }
            }
            else if ( event.type == INTERSECTION ) {
                statuses.remove( event.oneEnd );
                statuses.remove( event.otherEnd );

                LinePoint2D successor = statuses.higher( event );
                LinePoint2D predecessor = statuses.lower( event );

                if ( ifLinesIntersection( event.otherEnd, successor ) ) {
                    LinePoint2D intersection = lineIntersect( event.otherEnd, successor );
                    intersections.add( intersection );
                    events.add( intersection );
                }

                if ( ifLinesIntersection( event.oneEnd, predecessor ) ) {
                    LinePoint2D intersection = lineIntersect( event.oneEnd, successor );
                    intersections.add( intersection );
                    events.add( intersection );
                }

                event.oneEnd.x = event.x;
                event.oneEnd.y = event.y;
                statuses.add( event.oneEnd );

                event.otherEnd.x = event.x;
                event.otherEnd.y = event.y;
                statuses.add( event.otherEnd );
            }
            else assert false;
        }*/

        return intersections;
    }

    public static
    void testSort() {
        int ID = 0;
        Vector point1 = new Vector( 0, 0, ID++ );
        Vector point2 = new Vector( 1, 0, ID++ );
        Vector point3 = new Vector( 1, 1, ID++ );
        Vector point4 = new Vector( 0, 1, ID++ );
        Vector point5 = new Vector( -1, 1, ID++ );
        Vector point6 = new Vector( -1, 0, ID++ );
        Vector point7 = new Vector( -1, -1, ID++ );
        Vector point8 = new Vector( 0, -1, ID++ );
        Vector point9 = new Vector( 1, -1, ID++ );
        Vector point10 = new Vector( 0, 0, ID++ );

//        List<Vector> points = new ArrayList<>();
//        points.add( point1 );
//        points.add( point2 );
//        points.add( point3 );
//        points.add( point4 );
//        points.add( point5 );
//
//        System.out.println( points );
//        points.sort( Vector::sortByX );
//        System.out.println( points );
//
//        System.out.println( points );
//        points.sort( Vector::sortByY );
//        System.out.println( points );

//        System.out.println( Integer.MAX_VALUE );
//        System.out.println( Long.MAX_VALUE );
//        System.out.println( Float.MAX_VALUE );
//        System.out.println( Double.MAX_VALUE );
//
//        System.out.println( Long.MAX_VALUE - Float.MAX_VALUE );
//        System.out.println( Long.MIN_VALUE - Float.MIN_VALUE );
    }

    public static
    void testLinesIntersection() {
        int ID = 0;
        Vector vector1 = new Vector( 1, 0, ID++ );
        Vector vector2 = new Vector( -1, 0, ID++ );
        Vector vector3 = new Vector( 0, -1, ID++ );
        Vector vector4 = new Vector( 0, 1, ID++ );
        Vector vector5 = new Vector( 1, 1, ID++ );
        Vector vector6 = new Vector( -1, -1, ID++ );
        Line<Vector> line1 = new Line<>( vector1, vector2 );
        Line<Vector> line2 = new Line<>( vector3, vector4 );
        System.out.println( linesIntersect( line1, line2 ) ); // 0,0
        Line<Vector> line3 = new Line<>( vector6, vector5 );
        System.out.println( linesIntersect( line1, line3 ) ); // 0,0
        System.out.println( linesIntersect( line2, line3 ) ); // 0,0
        Vector vector7 = new Vector( 0, 3, ID++ );
        Vector vector8 = new Vector( 3, 0, ID++ );
        Vector vector9 = new Vector( 3, 3, ID++ );
        Line<Vector> line4 = new Line<>( vector7, vector8 );
        Line<Vector> line5 = new Line<>( vector9, vector3 );
        System.out.println( linesIntersect( line4, line5 ) ); // 1.7142857,1.2857143
        System.out.println( linesIntersect( line5, line4 ) ); // 1.7142857,1.2857143
        Vector vector10 = new Vector( 4, 3, ID++ );
        Line<Vector> line6 = new Line<>( vector10, vector5 );
        System.out.println( linesIntersect( line3, line6 ) ); // 1.0,1.0
        System.out.println( linesIntersect( line6, line6 ) ); // null
        Vector vector11 = new Vector( 4, 2, ID++ );
        Line<Vector> line7 = new Line<>( vector11, vector1 );
        System.out.println( linesIntersect( line7, line6 ) ); // null
        System.out.println( linesIntersect( line6, line6 ) ); // null
        Vector vector12 = new Vector( 1, -1, ID++ );
        Vector vector13 = new Vector( 1, 1, ID++ );
        Vector vector14 = new Vector( -1, 2, ID++ );
        Vector vector15 = new Vector( 2, 2, ID++ );
        Vector vector16 = new Vector( -1, 2, ID++ );
        Line<Vector> line8 = new Line<>( vector12, vector13 );
        Line<Vector> line9 = new Line<>( vector14, vector13 );
        Line<Vector> line10 = new Line<>( vector15, vector13 );
        Line<Vector> line11 = new Line<>( vector16, vector13 );
        System.out.println( linesIntersect( line8, line9 ) ); // 1.0,1.0
        System.out.println( linesIntersect( line10, line9 ) ); // 1.0,1.0
        System.out.println( linesIntersect( line10, line11 ) ); // 1.0,1.0
        System.out.println( linesIntersect( line8, line11 ) ); // 1.0,1.0
    }

    public static
    void testLineCycleIntersection() {
        int ID = 0;
        float radius = ( float ) Math.sqrt( 2 );
        Vector vector1 = new Vector( -radius, 0, ID++ );
        Vector vector2 = new Vector( radius, 0, ID++ );
        Vector vector3 = new Vector( -radius, -1, ID++ );
        Vector vector4 = new Vector( 0, radius, ID++ );
        Vector vector5 = new Vector( 1, radius, ID++ );
        Vector vector6 = new Vector( 1, 1, ID++ );
        Vector vector7 = new Vector( -1, -1, ID++ );
        Vector vector8 = new Vector( -1, 1, ID++ );
        Vector vector9 = new Vector( 0, 2, ID++ );
        Line<Vector> line1 = new Line<>( vector1, vector3 );
        Line<Vector> line2 = new Line<>( vector6, vector7 );
        Line<Vector> line3 = new Line<>( vector4, vector5 );
        Line<Vector> line4 = new Line<>( vector9, vector8 );
//        Line<Vector> line5 = new Line<>( vector2, vector4 );
        Cycle cycle = new Cycle( Vector.origin, radius );
        System.out.println( lineCycleIntersect( line1, cycle ) ); // -11->-1.4142135|0.0 -12->-1.4142135|0.0
        System.out.println( lineCycleIntersect( line2, cycle ) ); // -23->-0.99999994|-0.99999994|-24->0.99999994|0.99999994
        System.out.println( lineCycleIntersect( line3, cycle ) ); // -35->0.0|1.4142135	-36->0.0|1.4142135
        System.out.println( lineCycleIntersect( line4, cycle ) ); // -11->-1.0002441|0.99975586	-12->-0.99975586|1.0002441
    }

    public static
    void testLineArcIntersection() {
        int ID = 0;
        float radius = ( float ) Math.sqrt( 2 );
        Vector vector1 = new Vector( -radius, 0, ID++ );
        Vector vector2 = new Vector( 0, 0, ID++ );
        Vector vector3 = new Vector( -radius, -1, ID++ );
        Vector vector4 = new Vector( 0, radius, ID++ );
        Vector vector5 = new Vector( 1, radius, ID++ );
        Vector vector6 = new Vector( 1, 1, ID++ );
        Vector vector7 = new Vector( -1, -1, ID++ );
        Vector vector8 = new Vector( -1, 1, ID++ );
        Vector vector9 = new Vector( 0, 2, ID++ );
        Line<Vector> line1 = new Line<>( vector1, vector3 );
        Line<Vector> line2 = new Line<>( vector6, vector7 );
        Line<Vector> line3 = new Line<>( vector4, vector5 );
        Line<Vector> line4 = new Line<>( vector9, vector8 );
        Line<Vector> line5 = new Line<>( vector2, vector8 );
        Cycle cycle = new Cycle( Vector.origin, radius );
        Arc arc1 = new Arc( cycle, -radius, 0, radius, 0 );
        Arc arc2 = new Arc( cycle, 0, radius, radius, 0 );
        Arc arc3 = new Arc( cycle, -radius, 0, 0, -radius );
        Arc arc4 = new Arc( cycle, 0, radius, 0, -radius );

        int index = 0;
        Line<Vector>[] lines = new Line[ 5 ];
        lines[ index++ ] = line1;
        lines[ index++ ] = line2;
        lines[ index++ ] = line3;
        lines[ index++ ] = line4;
        lines[ index++ ] = line5;

        index = 0;
        Arc[] arcs = new Arc[ 4 ];
        arcs[ index++ ] = arc1;
        arcs[ index++ ] = arc2;
        arcs[ index++ ] = arc3;
        arcs[ index++ ] = arc4;

        for ( int i = 0; i < lines.length; i++ ) {
            System.out.println( "lines(" + ( i + 1 ) + "): " + lines[ i ] );
            for ( int j = 0; j < arcs.length; j++ ) {
                System.out.println( lineArcIntersect( lines[ i ], arcs[ j ] ) );
            }
            System.out.println();
        }
    }

    public static
    void main( String[] args ) {
//        testLinesIntersection();
//        testLineCycleIntersection();
        testLineArcIntersection();
    }
}
;
