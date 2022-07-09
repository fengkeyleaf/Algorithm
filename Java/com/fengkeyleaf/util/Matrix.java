package com.fengkeyleaf.util;

/*
 * Matrix.java
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
 * Data structure of matrix
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// TODO: 5/1/2021 not fully tested 
public class Matrix {
    public final double[][] matrix;
    public final int i;
    public final int j;

    /**
     * constructs to create an instance of Matrix
     * */

    public Matrix( int i, int j ) {
        invalidInitialization( i, j );
        this.i = i;
        this.j = j;
        matrix = new double[ i ][ j ];
    }

    public Matrix( double[][] matrix ) {
        i = matrix.length;
        j = matrix[ 0 ].length;
        invalidInitialization( i, j );
        this.matrix = matrix;
    }

    public double sumRow( int i ) {
        double sum = 0;
        Double[] row = getMatrixAtRow( i );
        for ( Double num : row )
            sum += num;

        return sum;
    }

    public double sumCol( int j ) {
        double sum = 0;
        Matrix col = getMatrixAtColumn( j );
        for ( int i = 0; i < this.i; i++ )
            sum += col.get( i, 0 );

        return sum;
    }

    private void invalidInitialization( int i, int j ) {
        if ( i == 0 || j == 0 ) throw new ArithmeticException( "Empty initialization" );
    }

    public void set( int i, int j, double value ) {
        matrix[ i ][ j ] = value;
    }

    public double get( int i, int j ) {
        return matrix[ i ][ j ];
    }

    /**
     * get a row at i of this Matrix
     * */

    public Double[] getMatrixAtRow( int i ) {
        Double[] res = new Double[ j ];
        for ( int j = 0; j < this.j; j++ )
            res[ j ] = get( i, j );

        return res;
    }

    /**
     * get a Column at j of this Matrix
     * */

    public Matrix getMatrixAtColumn( int j ) {
        Matrix res = new Matrix( i, 1 );
        for ( int i = 0; i < this.i; i++ )
            res.set( i, 0, get( i, j ) );

        return res;
    }

    public void multipleFactorByRow( Matrix factors ) {
        assert factors.i == i;
        assert factors.j == 1;

        for ( int i = 0; i < factors.i; i++ ) {
            for ( int j = 0; j < this.j; j++ )
                set( i, j, get( i, j ) * factors.get( i, 0 ) );
        }
    }

    /**
     * matrix multiplication
     *
     * overloaded
     * */

    private double multiple( int row, int column,
                          int common, Matrix matrix ) {
        float sum = 0;
        for ( int j = 0; j < common; j++ ) {
            sum += get( row, j ) *
                    matrix.get( j, column );
        }

        return sum;
    }

    /**
     * matrix multiplication
     * */

    public Matrix multiple( Matrix matrix ) {
        if ( matrix == null ) return this;
        // cannot perform matrix multiplication
        if ( j != matrix.i ) return null;

        Matrix res = new Matrix( i, matrix.j );
        for ( int i = 0; i < res.i; i++ ) {
            for ( int j = 0; j < res.j; j++ ) {
                res.set( i, j, multiple( i, j, this.j, matrix ) );;
            }
        }

        return res;
    }

    @Override
    public String toString() {
        return MyArrays.print2DArrays( matrix, false );
    }

    public static void main(String[] args ) {
        Matrix matrix1 = new Matrix( 3, 3 );
        Matrix matrix2 = new Matrix( 3, 1 );

        int value = 1;
        for ( int i = 0; i < matrix1.i; i++ ) {
            for ( int j = 0; j < matrix1.j; j++ ) {
                matrix1.set( i, j, value++ );
            }
        }

        value = 1;
        for ( int i = 0; i < matrix2.i; i++ ) {
            for ( int j = 0; j < matrix2.j; j++ ) {
                matrix2.set( i, j, value++ );
            }
        }

//        System.out.println( matrix1.getMatrixAtRow( 1 ) );
//        System.out.println( matrix1.getMatrixAtColumn( 1 ) );
//        System.out.println( matrix1.multiple( matrix2 ) );

        Matrix factors = matrix1.getMatrixAtColumn( 1 );
        matrix2.multipleFactorByRow( factors );
        System.out.println( matrix2 );
    }
}
