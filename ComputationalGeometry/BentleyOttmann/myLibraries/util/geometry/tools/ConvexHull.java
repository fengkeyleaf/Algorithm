package myLibraries.util.geometry.tools;

/*
 * ConvexHull.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.elements.point.Vector;

import java.util.*;

/**
 * Provide algorithms related to Convex Hull
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class ConvexHull {

    /**
     * find LTL
     * */

    public static Vector findLTL(List<Vector> points ) {
        Vector LTL = new Vector( Integer.MAX_VALUE,
                Integer.MAX_VALUE, -2 );

        for ( int i = 0; i < points.size(); i++ ) {
            Vector point = points.get( i );
            if ( Vector.compare( point, LTL, Triangle::LTL ) < 0 )
                LTL = point;
        }

        return LTL;
    }

    /**
     * find convex hull with given points
     * */

    public static
    Stack<Vector> findConvexHull(List<Vector> points ) {
        if ( points.size() < 3 ) return null;

        Stack<Vector> upper = new Stack<>();
        Stack<Vector> down = new Stack<>();

        // find LTL
        Vector LTL = findLTL( points );
//        System.out.println( LTL );

        // offset x and y by LTL's -> O(n);
        points.forEach( point2D -> point2D.relativeToLTL( LTL.x, LTL.y ) );
        // sort yb polar angel
        points.sort( Triangle::sortByToLeft );

        // initialize stack s and t
        assert LTL.equals( points.get( 0 ) );
        down.push( LTL );
        down.push( points.get( 1 ) );

        for ( int i = points.size() - 1; i >= 2 ; i-- )
            upper.push( points.get( i ) );

//        System.out.println( upper );
//        System.out.println( down );

        // true, all points lying on the same line
        // no convex hull in this case
        boolean allLine = true;
        // linear scan to find the convex hull
        while ( !upper.isEmpty() ) {
            Vector downTop = down.pop();
            Vector downViceTop = down.peek();
            Vector upperTop = upper.peek();

            Turn whichTurn = Triangle.toLeftByLTL( downViceTop, downTop, upperTop );
//            System.out.println( whichTurn );
            // add one point into s
            if ( whichTurn == Turn.LEFT_TURN || whichTurn == Turn.NONE ) {
                if ( whichTurn == Turn.LEFT_TURN ) allLine = false;
                down.push( downTop );
                down.push( upperTop );
                upper.pop();
            }
            // backtrack
            else {
                down.push( upperTop );
                upper.pop();

                downTop = down.pop();
                downViceTop = down.pop();
                Vector downThirdTop = down.peek();
                whichTurn = Triangle.toLeftByLTL( downThirdTop, downViceTop, downTop );
//                System.out.println( whichTurn );
                while ( whichTurn == Turn.RIGHT_RIGHT ) {
                    down.push( downTop );
                    downTop = down.pop();
                    downViceTop = down.pop();
                    downThirdTop = down.peek();
                    whichTurn = Triangle.toLeftByLTL( downThirdTop, downViceTop, downTop );
//                    System.out.println( whichTurn );
                }

                down.push( downViceTop );
                down.push( downTop );
            }
        }

        return allLine ? null : down;
    }

    public static
    void main( String[] args ) {
        int ID = 0;
        List<Vector> point2Ds = new ArrayList<>();
        point2Ds.add( new Vector( 7, 9, ID++ ) );
        point2Ds.add( new Vector( -8, -1, ID++ ) );
        point2Ds.add( new Vector( -3, -1, ID++ ) );
        point2Ds.add( new Vector( 1, 4, ID++ ) );
        point2Ds.add( new Vector( -3, 9, ID++ ) );
        point2Ds.add( new Vector( 6, -4, ID++ ) );
        point2Ds.add( new Vector( 7, 5, ID++ ) );
        point2Ds.add( new Vector( 6, 6, ID++ ) );
        point2Ds.add( new Vector( -6, 10, ID++ ) );
        point2Ds.add( new Vector( -0, 8, ID++ ) );

        point2Ds.clear();
        Vector point1 = new Vector( 2, 1, ID++ );
        Vector point2 = new Vector( 2, 2, ID++ );
        Vector point3 = new Vector( 0, 4, ID++ );
        Vector point4 = new Vector( -2, 1, ID++ );
        Vector point5 = new Vector( -2, 2, ID++ );
        Vector point6 = new Vector( -4, 0, ID++ );
        Vector point7 = new Vector( -2, -1, ID++ );
        Vector point8 = new Vector( -2, -2, ID++ );
        Vector point9 = new Vector( 0, -4, ID++ );
        Vector point10 = new Vector( 2, -1, ID++ );
        Vector point11 = new Vector( 2, -2, ID++ );

        Vector point12 = new Vector( -2, -2, ID++ );
        Vector point13 = new Vector( 2, -2, ID++ );
        Vector point14 = new Vector( 2, 1, ID++ );
        Vector point15 = new Vector( 1, 1, ID++ );
        Vector point16 = new Vector( -2, 2, ID++ );
        Vector point17 = new Vector( -4, 1, ID++ );

//        point2Ds.add( point1 );
//        point2Ds.add( point2 );
//        point2Ds.add( point3 );
//        point2Ds.add( point4 );
//        point2Ds.add( point5 );
//        point2Ds.add( point6 );
//        point2Ds.add( point7 );
//        point2Ds.add( point8 );
//        point2Ds.add( point9 );
//        point2Ds.add( point10 );
//        point2Ds.add( point11 );
//
//        System.out.println( point2Ds );
//        point2Ds.sort( Point2DComparator::sortByPolar );
//        System.out.println( point2Ds );
//
//        point2Ds.clear();
//        point2Ds.add( point12 );
//        point2Ds.add( point13 );
//        point2Ds.add( point14 );
//        point2Ds.add( point15 );
//        point2Ds.add( point16 );
//        point2Ds.add( point17 );
//        System.out.println( point2Ds );
//
//        Vector LTL = findLTL( point2Ds );
//        System.out.println( LTL );
//        System.out.println( LTL.x + " " + LTL.y );
//
//        point2Ds.forEach( point2D -> point2D.relativeToLTL( LTL.x, LTL.y ) );
//        point2Ds.sort( Point2DComparator::sortByToLeft );
//        System.out.println( point2Ds );

        Vector point18 = new Vector( 2, -2, ID++ );
        point18.xRelativeToLTL = 2;
        point18.yRelativeToLTL = -2;
        Vector point19 = new Vector( 0, 2, ID++ );
        point19.xRelativeToLTL = 0;
        point19.yRelativeToLTL = 2;
        Vector point20 = new Vector( -2, 2, ID++ );
        point20.xRelativeToLTL = -2;
        point20.yRelativeToLTL = 2;

        System.out.println( Triangle.areaTwoByLTL( point18, point19, point20 ) );
        System.out.println( Triangle.areaTwo( point18, point19, point20 ) );
    }
}
