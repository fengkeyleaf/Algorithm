package com.fengkeyleaf.lang;

/*
 * MyMath.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.8$
 *
 * Revisions:
 *     $1.0 convert Number To OtherBase and Arrangement, etc.$
 *     $1.1 add minAmongAll() on 3/23/2021$
 *     $1.2 add entropy() and gain() for AI on 4/8/2021$
 *     $1.3 add equalFloats() on 4/13/2021$
 *     $1.4 add quadrant() on 5/14/2021$
 *     $1.5 add doubleCompare() and isSameSign() on 7/8/2021$
 *     $1.6 add generateRandomDoubles() with uniform distribution and Gaussian(normal) distribution on 9/10/2021$
 *     $1.7 add findMaxMinInAbs() on 2/10/2022$
 *     $1.8 add base convertors on 9/4/2022$
 */

import com.fengkeyleaf.util.CompareElement;
import com.fengkeyleaf.util.geom.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Math tool box.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class MyMath {
    public static final double EPSILON = 0.00000001; // 1e-8

    public static
    String binaryToHex( String bits ) {
        return Long.toHexString( Long.parseLong( bits, 2 ) );
    }

    public static
    long binaryToDecimal( String bits ) {
        return Long.valueOf( bits, 2 );
    }

    /**
     * byte arrays to binary string.
     **/

    // Reference resource:
    // https://www.orchome.com/1105
    // https://mkyong.com/java/java-how-to-convert-a-byte-to-a-binary-string/#:~:text=In%20Java%2C%20we%20can%20use,argument%20and%20returns%20a%20String.
    public static
    String[] bytesToBinary( byte[] B ) {
        String[] BStr = new String[ B.length ];
        for ( int i = 0; i < B.length; i++ ) {
            BStr[ i ] = String.format( "%8s", Integer.toBinaryString(B[ i ] & 0xff ) )
                    .replace( " ", "0" );
            assert BStr[ i ].length() == 8;
        }

        return BStr;
    }

    /**
     * Hex data to decimal numbers.
     */

    public static
    int hexToDecimal( String h ) {
        return Integer.parseInt( h, 16 );
    }

    /**
     * Byte array to Hex data.
     *
     * @param bytes Byte array to be converted.
     * @return Hex data in the form of string.
     */

    // Reference resource:
    // https://blog.csdn.net/qq_34763699/article/details/78650272
    public static
    String[] bytesToHex( byte[] bytes ) {
        String[] T = new String[ bytes.length ];

        for ( int i = 0; i < bytes.length; i++ ) {
            String hex = Integer.toHexString( bytes[ i ] & 0xFF );
            if ( hex.length() < 2 ) {
                hex = "0" + hex;
            }

            T[ i ] = hex;
        }

        return T;
    }

    /**
     * n is less than or equal to base?
     * */

    public static
    boolean isLessEq( double base, double n ) {
        return doubleCompare( base, n ) >= 0;
    }

    /**
     * n is less than base?
     * */

    public static
    boolean isLess( double base, double n ) {
        return doubleCompare( base, n ) > 0;
    }

    /**
     * n is greater than or equal to base?
     * */

    public static
    boolean isGreaterEq( double base, double n ) {
        return doubleCompare( base, n ) <= 0;
    }

    /**
     * n is greater than base?
     * */

    public static
    boolean isGreater( double base, double n ) {
        return doubleCompare( base, n ) < 0;
    }

    /**
     * find min amd max num, compared by Math.abs() among numbers.
     *
     * @return [ min, max ] compared by Math.abs()
     * */

    public static
    double[] findMaxMinInAbs( List<Double> nums ) {
        double[] minMax = new double[ 2 ];
        minMax[ 0 ] = CompareElement.min( Comparator.comparingDouble( Math::abs ), nums );
        minMax[ 1 ] = CompareElement.max( Comparator.comparingDouble( Math::abs ), nums );

        return minMax;
    }

    public static
    double[] findMaxMinInAbs( double... nums ) {
        List<Double> numbers = new ArrayList<>( nums.length + 1 );
        for ( double num : nums )
            numbers.add( num );

        return findMaxMinInAbs( numbers );
    }

    /**
     * generate random non-negative integers with uniform distribution
     * */

    public static
    List<Integer> generateRandomInts( int n ) {
        List<Integer> numbers = new ArrayList<>();
        final Random myRandom = new Random( System.currentTimeMillis() );

        for ( int i = 0; i < n; i++ )
            numbers.add( myRandom.nextInt() );

        return numbers;
    }

    /**
     * generate random non-negative  integers with uniform distribution and bound
     * */

    public static
    List<Integer> generateRandomInts( int n, int bound ) {
        List<Integer> numbers = new ArrayList<>();
        final Random myRandom = new Random( System.currentTimeMillis() );

        for ( int i = 0; i < n; i++ )
            numbers.add( myRandom.nextInt( bound ) );

        return numbers;
    }

    /**
     * generate random doubles with uniform distribution
     *
     * Returns the next pseudorandom,
     * uniformly distributed double value between 0.0 and 1.0
     * from this random number generator's sequence.
     * */

    public static
    List<Double> generateRandomDoubles( int n ) {
        List<Double> numbers = new ArrayList<>();
        final Random myRandom = new Random( System.currentTimeMillis() );

        for ( int i = 0; i < n; i++ )
            numbers.add( myRandom.nextDouble() );

        return numbers;
    }

    /**
     * generate random doubles with Gaussian(normal) distribution
     *
     * Returns the next pseudorandom,
     * uniformly distributed double value between 0.0 and 1.0
     * from this random number generator's sequence.
     * */

    public static
    List<Double> generateRandomDoubles( int n, double mu, double sigma ) {
        List<Double> numbers = new ArrayList<>();
        final Random myRandom = new Random();

        for ( int i = 0; i < n; i++)
            numbers.add( myRandom.nextGaussian() * sigma + mu );

        return numbers;
    }

    /**
     * num is positive?
     * */

    public static
    boolean isPositive( double num ) {
        return doubleCompare( num, 0 ) > 0;
    }

    /**
     * num is negative?
     * */

    public static
    boolean isNegative( double num ) {
        return doubleCompare( num, 0 ) < 0;
    }

    /**
     * do num1 and num2 have the same sign?
     * */

    public static
    boolean isSameSign( double num1, double num2 ) {
        if ( isEqualZero( num1 ) && isEqualZero( num2 ) )
            return true;
        if ( isNegative( num1 ) && isNegative( num2 ) )
            return true;
        if ( isPositive( num1 ) && isPositive( num2 ) )
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
        if ( isPositive( x ) && doubleCompare( y, 0 ) >= 0 )  return 1;
        if ( doubleCompare( x, 0 ) <= 0 && isPositive( y ) )  return 2;
        if ( isNegative( x ) ) return 3;
        if ( isNegative( y ) ) return 4;

        assert false;
        return -1;
    }

    public static
    int quadrant( long x, long y ) {
        if ( x > 0 && y >= 0 )  return 1;
        if ( x <= 0 && y > 0 )  return 2;
        if ( x < 0 )  return 3;
        if ( y < 0 ) return 4;

        assert false;
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
    boolean isEqualZero( double num ) {
        return Math.abs( num - 0 ) <= EPSILON;
    }

    /**
     * determine if two floating numbers are equal or not
     * */

    public static
    boolean isEqual( double num1, double num2 ) {
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
}
