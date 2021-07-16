package myLibraries.lang;
/*
 * MyMath.java
 *
 * Version:
 *     $1.5$
 *
 * Revisions:
 *     $1.0 convert Number To OtherBase and Arrangement, etc.$
 *     $1.1 added minAmongAl()l on 3/23/2021$
 *     $1.2 added entropy() and gain() for AI on 4/8/2021$
 *     $1.3 add equalFloats() on 4/13/2021$
 *     $1.4 add quadrant() on 5/14/2021$
 *     $1.5 add doubleCompare() and isSameSign() on 7/8/2021$
 */


import myLibraries.util.geometry.elements.point.Vector;

/**
 * Math tool box
 *
 * @author       Xiaoyu Tongyang or call me sora for short
 */

public final class MyMath {
    public static final double EPSILON = 0.00000001; // 1e-8

    /**
     * num is positive?
     * */

    public static
    boolean isGreaterThanZero( double num ) {
        return doubleCompare( num, 0 ) > 0;
    }

    /**
     * num is negative?
     * */

    public static
    boolean isSmallerThanZero( double num ) {
        return doubleCompare( num, 0 ) < 0;
    }

    /**
     * do num1 and num2 have the same sign?
     * */

    public static
    boolean isSameSign( double num1, double num2 ) {
        if ( isEqualZero( num1 ) && isEqualZero( num2 ) )
            return true;
        if ( isSmallerThanZero( num1 ) && isSmallerThanZero( num2 ) )
            return true;
        if ( isGreaterThanZero( num1 ) && isGreaterThanZero( num2 ) )
            return true;

        return false;
    }

    /**
     * double's compare with epsilon
     * */

    public static
    int doubleCompare( double num1, double num2 ) {
        double different = num1 - num2;
        // num1 is smaller
        if ( different < -EPSILON ) return -1;
        // num1 is greater
        else if ( different > EPSILON ) return 1;
        // equal
        return 0;
    }

    /**
     * which quadrant the point is at
     * */

    public static
    int quadrant( double x, double y ) {
        if ( x > 0 && y >= 0 )  return 1;
        if ( x <= 0 && y > 0 )  return 2;
        if ( x < 0 )  return 3;
        if ( y < 0 ) return 4;

        return -1;
    }

    public static
    int quadrant( float x, float y ) {
        if ( x > 0 && y >= 0 )  return 1;
        if ( x <= 0 && y > 0 )  return 2;
        if ( x < 0 )  return 3;
        if ( y < 0 ) return 4;

        return -1;
    }

    public static
    int quadrant( int x, int y ) {
        if ( x > 0 && y >= 0 )  return 1;
        if ( x <= 0 && y > 0 )  return 2;
        if ( x < 0 )  return 3;
        if ( y < 0 ) return 4;

        return -1;
    }

    public static
    int quadrant( long x, long y ) {
        if ( x > 0 && y >= 0 )  return 1;
        if ( x <= 0 && y > 0 )  return 2;
        if ( x < 0 )  return 3;
        if ( y < 0 ) return 4;

        return -1;
    }

    public static
    int quadrant( Vector point ) {
        return quadrant( point.x, point.y );
    }

    /**
     * check int Overflow
     * */

    public static
    boolean checkOverflow( int original, int result ) {
        return original < 0 && result > 0 ||
                original > 0 && result < 0;
    }

    /**
     * determine if the floating number equals zero
     * */

    public static
    boolean isEqualZero(double num ) {
        return Math.abs( num - 0 ) <= EPSILON;
    }

    /**
     * determine if two floating numbers are equal or not
     * */

    public static
    boolean isEqualFloats(double num1, double num2 ) {
        return Math.abs( num1 - num2 ) <= EPSILON;
    }

    /**
     * The information gain of a question is
     * how much entropy is removed by asking
     * the question
     * */

    public static
    double gain( double m, double n, double kOne,
                 double kOnePositive, double kTwo, double kTwoPositive ) {
        assert m >= n && kOne >= kOnePositive && kTwo >= kTwoPositive;
        double num1 = kOne == 0 ? 0 : kOne / m * entropy( kOnePositive / kOne );
        double num2 = kTwo == 0 ? 0 : kTwo / m * entropy( kTwoPositive / kTwo );
//        System.out.println( entropy( n / m ) + " " + num1 + " " + num2 );
        return entropy( n / m ) - ( num1 + num2 );
    }

    /**
     * logarithm with any base
     * */

    public static
    double myLog( double a, int base ) {
        return Math.log( a ) / Math.log( base );
    }

    /**
     * The entropy of a set of examples is a measure of how much
     * information is in each example
     * */

    public static
    double entropy( double q ) {
        double p = 1 - q;
        assert q >= 0 && p >= 0;
        if ( p <= 0 || q <= 0 ) return 0;
        return -( q * myLog( q, 2 ) + p * myLog( p, 2 ) );
    }

    /**
     * find min among numbers
     *
     * @param       numbers         numbers needed to find min among them
     * */

    public static
    int minAmongAll( int... numbers ) {
        int mins = Integer.MAX_VALUE;
        for ( int num : numbers )
            mins = Math.min( mins, num );

        return mins;
    }

    /*
     * methods below are irrelevant to hw_4
     * */

    /**
     * convert numbers in base 10 to numbers in base radix
     *
     * @param       number      number in base 10
     * @param       radix       other base
     * */

    public static
    NumberRadix convertNumberToOtherBase( long number, int radix ) {
        NumberRadix convertedNumber = new NumberRadix(
                number, radix, number >= 0 );
        number = Math.abs( number );

        do {
            convertedNumber.addDigit( ( int ) ( number % radix ) );
            number /= radix;
        } while ( number > 0 );

        return convertedNumber;
    }

    /**
     * compute distance between a point and the origin
     * */

    public static
    long distanceFromOrigin3DWithoutRadical( long x, long y, long z ) {
        return x * x + y * y + z * z;
    }

    /*
     * methods below are irrelevant to hw_2
     * */

    public static
    void checkForArrangementAndCombination( int n, int m ) {
        if ( n < m ) {
            System.err.println("Error: n < m in checkForArrangementAndCombination()");
            System.exit(1);
        }
    }

    private static
    int accumulativeProductForArrangement( int n, int c ) {
        int res = 1;
        for ( int i = n; i > c; i--)
            res *= i;

        return res;
    }

    public static
    int Arrangement( int n, int m ) {
        checkForArrangementAndCombination( n, m );
        if ( n - m == 0 ) return 1;

        return accumulativeProductForArrangement( n, n - m );
    }

    public static
    int Combination( int n, int m ) {
        checkForArrangementAndCombination( n, m );
        int c = n - m, res = 1;
        if ( c == 0 ) return res;

        res = c > m ? accumulativeProductForArrangement( n, c ) : accumulativeProductForArrangement( n, m );
        return res / factorial( Math.min( c, m ) );
    }

    public static
    int factorial( int n ) {
        int res = 1;
        for ( int i = 2; i <= n; i++)
            res *= i;

        return res;
    }

    public static
    void main( String[] args ) {
        /*System.out.println("Test factorial ------------>");
        System.out.println( factorial( 0 ) );
        System.out.println( factorial( 1 ) );
        System.out.println( factorial( 2 ) );
        System.out.println( factorial( 3 ) );
        System.out.println( factorial( 4 ) );
        System.out.println( factorial( 5 ) );

        System.out.println("\nTest Arrangement ------------>");
        System.out.println( Arrangement( 3, 0 ) );
        System.out.println( Arrangement( 3, 1 ) );
        System.out.println( Arrangement( 3, 2 ) );
        System.out.println( Arrangement( 3, 3 ) );
//        System.out.println( Arrangement( 3, 4 ) ); // error

        System.out.println("\nTest Combination ------------>");
        System.out.println( Combination( 3, 0 ) );
        System.out.println( Combination( 3, 1 ) );
        System.out.println( Combination( 3, 2 ) );
        System.out.println( Combination( 3, 3 ) );
//        System.out.println( Combination( 3, 4 ) ); // error*/

//        System.out.println("\ndistance From Origin 3D Without Radical------------>");
//        System.out.println( MyMath.distanceFromOrigin3DWithoutRadical( 0, 5, 0 ) );
//        System.out.println( MyMath.distanceFromOrigin3DWithoutRadical( 2, -2, 4 ) );
//        System.out.println( MyMath.distanceFromOrigin3DWithoutRadical( 5, -5, 1 ) );
//        System.out.println( MyMath.distanceFromOrigin3DWithoutRadical( -4, -3, -2 ) );
//        System.out.println( MyMath.distanceFromOrigin3DWithoutRadical( 3, 0, -4 ) );
//
//        System.out.println("\nTest Base Conversion ------------>");
//        System.out.println("\nBase 10 ------------>");
//        System.out.println( convertNumberToOtherBase( 0, 10 ) );
//        System.out.println( convertNumberToOtherBase( 5, 10 ) );
//        System.out.println( convertNumberToOtherBase( 9, 10 ) );
//        System.out.println( convertNumberToOtherBase( 12, 10 ) );
//        System.out.println( convertNumberToOtherBase( 123, 10 ) );
//        System.out.println( convertNumberToOtherBase( 12345, 10 ) );
//        System.out.println( convertNumberToOtherBase( 1234567890, 10 ) );
//
//        System.out.println("\nBase 16 ------------>");
//        System.out.println( 0 + ": " + convertNumberToOtherBase( 0, 16 ) + " - " + Integer.toHexString( 0 ) );
//        System.out.println( 1 + ": " + convertNumberToOtherBase( 1, 16 ) + " - " + Integer.toHexString( 1 ) );
//        System.out.println( 6 + ": " + convertNumberToOtherBase( 6, 16 ) + " - " + Integer.toHexString( 6 ) );
//        System.out.println( 12 + ": " + convertNumberToOtherBase( 12, 16 ) + " - " + Integer.toHexString( 12 ) );
//        System.out.println( 15 + ": " + convertNumberToOtherBase( 15, 16 ) + " - " + Integer.toHexString( 15 ) );
//        System.out.println( 16 + ": " + convertNumberToOtherBase( 16, 16 ) + " - " + Integer.toHexString( 16 ) );
//        System.out.println( 123 + ": " + convertNumberToOtherBase( 123, 16 ) + " - " + Integer.toHexString( 123 ) );
//        System.out.println( 1234 + ": " + convertNumberToOtherBase( 1234, 16 ) + " - " + Integer.toHexString( 1234 ) );
//        System.out.println( 123456 + ": " + convertNumberToOtherBase( 123456, 16 ) + " - " + Integer.toHexString( 123456 ) );
//        System.out.println( 1234567890 + ": " + convertNumberToOtherBase( 1234567890, 16 ) + " - " + Integer.toHexString( 1234567890 ) );
//
//        System.out.println( 127 + ": " + convertNumberToOtherBase( 127, 16 ) + " - " + Integer.toHexString( 127 ) );
//        System.out.println( 307 + ": " + convertNumberToOtherBase( 307, 16 ) + " - " + Integer.toHexString( 307 ) );
//        System.out.println( 127 + ": " + convertNumberToOtherBase( 127, 16 ) + " - " + Integer.toHexString( 124 ) );
//        System.out.println( 223 + ": " + convertNumberToOtherBase( 223, 16 ) + " - " + Integer.toHexString( 223 ) );
//        System.out.println( 772 + ": " + convertNumberToOtherBase( 772, 16 ) + " - " + Integer.toHexString( 772 ) );
//        System.out.println( 532 + ": " + convertNumberToOtherBase( 532, 16 ) + " - " + Integer.toHexString( 532 ) );

//        System.out.println( Math.abs( Math.sqrt( 5 / 9.0 ) - 1 ) );
//        System.out.println( Math.abs( Math.sqrt( 10 / 16.0 ) - 1 ) );

//        System.out.println( entropy( 0.5 ) ); // 1
//        System.out.println( entropy( 1 ) ); // 0
//        System.out.println( gain( 12, 6, 1, 1, 11, 5 ) ); // 0.089

//        System.out.println( gain( 20, 12, 11, 5, 9, 7 ) ); // 0.08034195021346324
//        System.out.println( gain( 20, 12, 10, 6, 10, 6 ) ); // 0.0
//        System.out.println( gain( 20, 12, 10, 4, 10, 8 ) ); // 0.12451124978365313
//        System.out.println( gain( 20, 12, 9, 6, 11, 6 ) ); // 0.011000852817822038
//        System.out.println( entropy( 12 / 20.0 ) );
//        System.out.println( entropy( 6 / 10.0 ) );

//        System.out.println( gain( 10, 4, 6, 2, 4, 2 ) ); // 0.01997309402197489
//        System.out.println( gain( 10, 4, 4, 1, 6, 3 ) ); // 0.0464393446710154
//        System.out.println( gain( 10, 4, 5, 2, 5, 2 ) ); // 0.0
//
//        System.out.println();
//        System.out.println( gain( 10, 8, 5, 3, 5, 5 ) ); // 0.23645279766002802
//        System.out.println( gain( 10, 8, 6, 5, 4, 3 ) ); // 0.007403392114696761
//        System.out.println( gain( 10, 8, 4, 4, 6, 4 ) ); // 0.17095059445466865

//        System.out.println( gain( 4, 1, 3, 1, 1, 0 ) ); // 0.12255624891826566
//        System.out.println( entropy( 0 ) );
//        System.out.println( myLog( 0, 2 ) );
//        System.out.println( gain( 6, 3, 2, 1, 4, 2 ) ); // 0.0

        System.out.println( gain( 5, 3, 3, 3, 2, 0 ) ); // 0.9709505944546686
        System.out.println( gain( 4, 1, 3, 1, 1, 0 ) ); // 0.12255624891826566
        System.out.println( gain( 6, 3, 3, 1, 3, 2 ) ); // 0.08170416594551044
        System.out.println( gain( 6, 3, 2, 1, 4, 2 ) ); // 0

        System.out.println( gain( 3, 1, 0, 0, 3, 1 ) ); // 0
        System.out.println( gain( 3, 2, 2, 1, 1, 1 ) ); // 0.2516291673878229
    }
}
