package myLibraries.lang;

/*
 * LinearOneUnknown.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 12/31/2021$
 */

/**
 * linear equation with one unknown, ax + b = 0 or ax = b（ a ≠ 0 ）
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public class LinearOneUnknown {
    private final double a;
    private final double b;

    /**
     * Constructs to create an instance of linear equation with one unknown,
     * ax + b = 0
     * */

    public LinearOneUnknown( double a, double b ) {
        if ( MyMath.isEqualZero( a ) ) {
            System.err.println( "a = 0 for linear equation with one unknown" );
            System.exit( 1 );
        }

        this.a = a;
        this.b = b;
    }

    /**
     * get the solution, x = - b / a
     * */

    public double getAnswer() {
       return -b / a;
    }

    public double getY( double x ) {
        return a * x + b;
    }
}
