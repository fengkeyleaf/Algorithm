package myLibraries.util;

/*
 * Determinant.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 * JDK: 16
 */

/**
 * Data structure of Determinant
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Determinant extends Matrix {

    /**
     * constructs to create an instance of Determinant
     * */

    public Determinant( int dimension ) {
        super( dimension, dimension );
    }

    public Determinant( double[][] matrix ) {
        super( matrix );
        if ( i != j )
            throw new ArithmeticException( "not equal length in determinant" );
    }

    /**
     * get result of this determinant
     * */

    public double getResult() {
        assert i == j;
        if ( i == 2 ) return twoDeterminant();

        return threeDeterminant();
    }

    private double multiple( int i1, int j1, int i2, int j2 ) {
        return get( i1, j1 ) * get( i2, j2 );
    }

    private double minus( int i1, int j1, int i2, int j2,
                          int i3, int j3, int i4, int j4 ) {
        return multiple( i1, j1, i2, j2 ) - multiple( i3, j3, i4, j4 );
    }

    private double twoDeterminant() {
        return minus( 0, 0, 1, 1, 1, 0, 0, 1 );
    }

    /**
     * | 00, 01, 02 |
     * | 10, 11, 12 |
     * | 20, 21, 22 |
     *
     * 00 * 11 * 22 + 01 * 12 * 20 + 10 * 21 * 02
     * - 02 * 11 * 20 - 01 * 10 * 22 - 12 * 21 * 00
     *
     * --->
     *
     * 00 * ( 11 * 22 - 12 * 21 ) +
     * 01 * ( 12 * 20 - 10 * 22 ) +
     * 02 * ( 10 * 21 - 11 * 20 )
     * */

    private double threeDeterminant() {
        return get( 0, 0 ) * minus( 1, 1, 2, 2, 1, 2, 2, 1 ) +
                get( 0, 1 ) * minus( 1, 2, 2, 0, 1, 0, 2, 2 ) +
                get( 0, 2 ) * minus( 1, 0, 2, 1, 1, 1, 2, 0 );
    }

    public static
    void main( String[] args ) {
        double[][] matrix = {
                { 2, 0, 1 },
                { 1, -4, -1 },
                { -1, 8, 3 }
        };

        Determinant determinant = new Determinant( matrix );
        System.out.println( determinant.getResult() ); // -4
//        determinant = new Determinant( 0 );

        double[][] matrix2 = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 9 }
        };

        Determinant determinant2 = new Determinant( matrix2 );
        System.out.println( determinant2.getResult() ); // 0
    }
}
