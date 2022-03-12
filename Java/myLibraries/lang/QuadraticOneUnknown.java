package myLibraries.lang;

/*
 * QuadraticOneUnknown.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 12/31/2021$
 */

import myLibraries.util.geometry.elements.Vector;

/**
 * Quadratic equation of one unknown, ax² + bx + c = 0（ a ≠ 0 ）
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public class QuadraticOneUnknown {
    private final double a;
    private final double b;
    private final double c;

    private final LinearOneUnknown linear;

    /**
     * constructs to create an instance of QuadraticOneUnknown
     * */

    public QuadraticOneUnknown( double a, double b, double c ) {
        this.a = a;
        this.b = b;
        this.c = c;

        if ( MyMath.isEqualZero( a ) ) {
            linear = new LinearOneUnknown( b, c );
            return;
        }

        linear = null;
    }

    public Vector[] intersect( QuadraticOneUnknown e ) {
        QuadraticOneUnknown e3 = new QuadraticOneUnknown( this.a - e.a, this.b - e.b, this.c - e.c );
        double[] x = e3.getAnswer();
        // no solutions
        if ( x == null )
            return null;

        // one solution
        // precision issue, cannot assert in this way.
        // but it can in theory.
//        assert MyMath.isEqual( this.getY( x[ 0 ] ), e.getY( x[ 0 ] ) ) : this.getY( x[ 0 ] ) + " " + e.getY( x[ 0 ] ) + "\n" + this + "\n" + e;
        Vector v1 = new Vector( x[ 0 ], this.getY( x[ 0 ] ) );
        if ( x.length == 1 )
            return new Vector[] { v1 };

        // two solutions
//        assert MyMath.doubleCompare( this.getY( x[ 1 ] ), e.getY( x[ 1 ] ) ) == 0 : this.getY( x[ 0 ] ) + " " + e.getY( x[ 0 ] );
        Vector v2 = new Vector( x[ 1 ], this.getY( x[ 1 ] ) );
        return new Vector[] { v1, v2 };
    }

    public double getY( double x ) {
        if ( linear != null ) return linear.getY( x );

        return a * x * x + b * x + c;
    }

    /**
     * get the solutions, up to two.
     *
     *      -b +- sqt( Delta )
     * x =  ---------------------
     *               2a
     *
     * , where Delta = b² - 4ac
     * */

    public double[] getAnswer() {
        if ( linear != null )
            return new double[] { linear.getAnswer() };

        double delta = getDelta();
        double s1 = ( -b + Math.sqrt( delta ) ) / ( 2 * a );
        double s2 = ( -b - Math.sqrt( delta ) ) / ( 2 * a );
        // two solutions
        if ( MyMath.isPositive( delta ) ) {
            assert !( MyMath.doubleCompare( s1, s2 ) == 0 );
            return new double[] { s1, s2 };
        }
        // one solution
        else if ( MyMath.isEqualZero( delta ) ) {
            assert MyMath.doubleCompare( s1, s2 ) == 0;
            return new double[] { s1 };
        }

        // no solutions
        return null;
    }

    /**
     * get the Delta,
     *
     * Delta = b² - 4ac
     * */

    private double getDelta() {
        return b * b - 4 * a * c;
    }

    @Override
    public String toString() {
        return "y = " + a + "x^2 + " + b + "x + " + c;
    }

    public static
    void main( String[] args ) {
//        QuadraticTwoUnknown q1 = new QuadraticTwoUnknown(  )
    }
}
