package coding.leetcode.medium;

/*
 * NumberOfIslands.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/26/2022$
 */

/**
 * <a href="https://leetcode.com/problems/number-of-islands/">200. Number of Islands</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public class NumberOfIslands {

    int c;
    int i;
    int j;

    public int numIslands( char[][] grid ) {
        i = grid.length;
        j = grid[ 0 ].length;

        for ( int i = 0; i < grid.length; i++ ) {
            for ( int j = 0; j < grid[ i ].length; j++ ) {
                if ( convert( grid, i, j ) == 1 ) {
                    c++;
                    numIslands( grid, i, j );
                }

            }
        }
        return c;
    }

    private int convert( char[][] grid, int i, int j ) {
        return Integer.parseInt( Character.toString( grid[ i ][ j ] ) );
    }

    private void numIslands( char[][] grid, int i, int j ) {
        if ( i < 0 || i >= this.i || j < 0 || j >= this.j ||
                convert( grid, i, j ) != 1 ) return;

        grid[ i ][ j ] = '2';
        numIslands( grid, i - 1, j );
        numIslands( grid, i + 1, j );
        numIslands( grid, i, j - 1 );
        numIslands( grid, i, j + 1 );
    }

    static
    void test1() {
        char[][] C = new char[][] {
                { '1', '1', '1', '1', '0' },
                { '1', '1', '0', '1', '0' },
                { '1', '1', '0', '0', '0' },
                { '0', '0', '0', '0', '0' }
        };

        System.out.println( new NumberOfIslands().numIslands( C ) );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
