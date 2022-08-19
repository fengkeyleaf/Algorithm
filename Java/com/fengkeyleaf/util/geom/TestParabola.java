package com.fengkeyleaf.util.geom;

/*
 * TestParabola.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/31/2022$
 */

import com.fengkeyleaf.lang.MyMath;

import java.util.Arrays;

/**
 * Test Parabola
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class TestParabola {

    static
    void testNormal() {

        Line directrix = new Line( new Vector( 1, -1 ), new Vector( -1, -1 ) );
        Parabola p1 = new Parabola( new Vector( 0, 1 ), directrix );
        Parabola p2 = new Parabola( new Vector( 2, 1 ), directrix );

//        System.out.println( p1 ); // y = 0.25x^2 + 0.0x + 0.0
//        System.out.println( p2 ); //  y = 0.25x^2 + 1.0x + 4.0
//        System.out.println( Arrays.toString( GeometricIntersection.parabolasIntersect( p1, p2 ) ) ); // [1.0|0.25]

//        p = new Parabola( new Vector( 0, -1 ), new Line( new Vector( 1, -1 ), new Vector( -1, -1 ) ) );
//        System.out.println( p ); // error

//        System.out.println();
        directrix = new Line( new Vector( 1, -6 ), new Vector( -1, -6 ) );
        p1 = new Parabola( new Vector( -4, 2 ), directrix );
        p2 = new Parabola( new Vector( 2, -2 ), directrix );

//        System.out.println( p1 ); // y = 0.0625x^2 + 0.5x + -1.0
//        System.out.println( p2 ); // y = 0.125x^2 + -0.5x + -3.5
//        System.out.println( Arrays.toString( GeometricIntersection.parabolasIntersect( p1, p2 ) ) );;

        directrix = new Line( new Vector( 1, -12 ), new Vector( -1, -12 ) );
        p1 = new Parabola( new Vector( 2, 2 ), directrix );
        p2 = new Parabola( new Vector( -2, -2 ), directrix );

//        System.out.println( p1 ); // y = 0.03571428571428571x^2 + -0.14285714285714285x + -4.857142857142857
//        System.out.println( p2 ); // y = 0.05x^2 + 0.2x + -6.8
//        System.out.println( Arrays.toString( GeometricIntersection.parabolasIntersect( p1, p2 ) ) ); // [-28.733200530681504|28.73320053068149, 4.733200530681513|-4.73320053068151]

        directrix = new Line( -1, 0, 1, 0 );
        p1 = new Parabola( new Vector( 0, 2 ), directrix );

//        System.out.println( p1 );

        directrix = new Line( -1, -5, 1, -5 );
        p1 = new Parabola( new Vector( 1, 4 ), directrix );
        p2 = new Parabola( new Vector( -4, 1 ), directrix );

        // y = 0.05555555555555555x^2 + -0.1111111111111111x + -0.4444444444444444
        // y = 0.08333333333333333x^2 + 0.6666666666666666x + -0.6666666666666667
//        System.out.println( p1 );
//        System.out.println( p2 );

        directrix = new Line( -1, -5.00001, 1, -5.00001 );
        p1 = new Parabola( new Vector( 1, 4 ), directrix );
        p2 = new Parabola( new Vector( -4, 1 ), directrix );
        Parabola p3 = new Parabola( new Vector( -2, -5 ), directrix );

        // y = 0.05555549382722908x^2 + -0.11111098765445816x + -0.44444950617277074
        //y = 0.08333319444467593x^2 + 0.6666655555574075x + -0.6666738888851849
        // y = 50000.000001892884x^2 + 200000.00000757154x + 199995.00000257153
//        System.out.println( p1 );
//        System.out.println( p2 );
//        System.out.println( p3 );

        directrix = new Line( -1, -2, 1, -2 );
        p1 = new Parabola( new Vector( -3, 4 ), directrix );
        p2 = new Parabola( new Vector( 4, 1 ), directrix );

//        System.out.println( p1 );
//        System.out.println( p2 );
//        p3 = new Parabola( new Vector( -2, -5 ), directrix );

        // y = 0.045454545454545456x^2 + 2.272727272727273x + 29.90909090909091
        //y = 0.25x^2 + 14.5x + 207.25
        directrix = new Line( -1, -4, 1, -4 );
        p1 = new Parabola( new Vector( -25, 7 ), directrix );
        p2 = new Parabola( new Vector( -29, -2  ), directrix );

//        System.out.println( p1 );
//        System.out.println( p2 );

        directrix = new Line( -1, 4, 1, 4 );
        p1 = new Parabola( new Vector( 28, 31 ), directrix );
        p2 = new Parabola( new Vector( 32, 25  ), directrix );
        p3 = new Parabola( new Vector( 28, 8 ), directrix );

        System.out.println( p1 );
        System.out.println( p2 );
        System.out.println( p3 );
    }

    static
    void testIntersect() {
        Line directrix = new Line( new Vector( 1, -2.00001 ), new Vector( -1, -2.00001 ) );
        Parabola p1 = new Parabola( new Vector( -29, -2 ), directrix );
        Parabola p2 = new Parabola( new Vector( -25, 7 ), directrix );

        // y = 49999.99999967244x^2 + 2899999.9999810015x + 4.204999799971952E7
        //y = 0.05555549382722908x^2 + 2.7777746913614543x + 37.22217864201818
        System.out.println( p1 );
        System.out.println( p2 );
        // -29.01038605787481
        // 3.3935049176216125 3.3935049146110146
        System.out.println( p1.updateY( -29.01038605787481 ) + " " + p2.updateY( -29.01038605787481 ) );
        System.out.println( MyMath.isEqual( p1.updateY( -29.01038605787481 ), p2.updateY( -29.01038605787481 ) ) ); // true
        // -28.989622831014074
        // 3.3842767998576164 3.3842768137840267
        System.out.println( p1.updateY( -28.989622831014074 ) + " " + p2.updateY( -28.989622831014074 ) );
        System.out.println( MyMath.isEqual( p1.updateY( -28.989622831014074 ), p2.updateY( -28.989622831014074 ) ) ); // false
        System.out.println( Arrays.toString( p1.intersect( p2 ) ) );
    }

    public static
    void main( String[] args ) {
        testNormal();
//        testIntersect();
    }
}
