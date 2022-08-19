package coding.leetcode.hard;

/*
 * SudokuSolver.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/6/2022$
 */

/**
 * <a href="https://leetcode.com/problems/sudoku-solver/">37. Sudoku Solver</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// reference resource: https://leetcode.com/problems/sudoku-solver/discuss/15752/Straight-Forward-Java-Solution-Using-Backtracking
public final class SudokuSolver {

    int i;
    int j;
    char[][] board;

    public void solveSudoku( char[][] board ) {
        i = board.length;
        j = board[ 0 ].length;
        this.board = board;
        solver( 0, 0 );
    }

    static final char empty = '.';
    static final char one = '1';
    static final char[] N = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    private boolean solver( int i, int j ) {
        if ( i == 0 && j == this.j ) return true;

        if ( board[ i ][ j ] == empty ) {
            for ( char n : N ) {

                // first check to see if n is valid or not,
                // if it is, then go on the next one.
                // In this way, we can avoid putting duplicate digits in the same row, column, or sub-box.
                if ( isValid( i, j, n ) ) {
                    board[ i ][ j ] = n;
                    if ( solver( i + 1 >= this.i ? 0 : i + 1, i + 1 >= this.i ? j + 1 : j ) )
                        return true;
                    else board[ i ][ j ] = empty;
                }
            }

            return false;
        }

        return solver( i + 1 >= this.i ? 0 : i + 1, i + 1 >= this.i ? j + 1 : j );
    }

    private boolean isValid( int row, int col, char c ) {
        // just check the one row and one column at a time.
        for ( int i = 0; i < 9; i++ ) {
            if ( board[ i ][ col ] == c ) return false; // check row
            if ( board[ row ][ i ] == c ) return false; // check column
            if ( board[ 3 * ( row / 3 ) + i / 3 ][ 3 * ( col / 3 ) + i % 3 ] == c ) return false; //check 3*3 block
        }
        return true;
    }
}
