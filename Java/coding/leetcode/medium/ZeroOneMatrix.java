package coding.leetcode.medium;

/*
 * ZeroOneMatrix.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/8/2022$
 */

import com.fengkeyleaf.util.MyArrays;

import java.util.LinkedList;
import java.util.Queue;

/**
 * <a href="https://leetcode.com/problems/01-matrix/">542. 01 Matrix</a>
 * https://leetcode.cn/problems/01-matrix/
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public final class ZeroOneMatrix {

    int[][] m;
    int[][] mat;

    public int[][] updateMatrix( int[][] mat ) {
        this.mat = mat;
        setStatus();

        findClosetSea();

        return m;
    }

    static final int SEA = 0;
    static final int ISLAND = 1;
    static final int UNKNOWN = Integer.MAX_VALUE;

    private void setStatus() {
        m = new int[ mat.length ][ mat[ 0 ].length ];

        for ( int i = 0; i < mat.length; i++ ) {
            for ( int j = 0; j < mat[ i ].length; j++ ) {
                switch ( mat[ i ][ j ] ) {
                    case SEA -> m[ i ][ j ] = SEA;
                    case ISLAND -> m[ i ][ j ] = UNKNOWN;
                    default -> {
                        assert false;
                    }
                }
            }
        }
    }

    private void findClosetSea() {
        Queue<int[]> q = new LinkedList<>();
        for ( int i = 0; i < mat.length; i++ ) {
            for ( int j = 0; j < mat[ i ].length; j++ ) {
                if ( mat[ i ][ j ] == SEA ) q.add( new int[] { i, j } );
            }
        }

        int curSize = 0, preSize = 1, c = 0;
        while ( !q.isEmpty() ) {
            int[] I = q.poll();
            int[][] N = findNeighbours( I );

            for ( int[] n : N ) {
                if ( n == null ||
                        ( m[ n[ 0 ] ][ n[ 1 ] ] <=
                        m[ I[ 0 ] ][ I[ 1 ] ] + 1 ) ) continue;

                if ( mat[ n[ 0 ] ][ n[ 1 ] ] == ISLAND ) {
                    q.add( n );
                    curSize++;
                    m[ n[ 0 ] ][ n[ 1 ] ] = m[ I[ 0 ] ][ I[ 1 ] ] + 1;
                }
            }
        }
    }

    private int[][] findNeighbours( int[] I ) {
        int i = I[ 0 ], j = I[ 1 ], idx = 0;

        int[][] N = new int[ 4 ][];
        if ( i - 1 >= 0 ) N[ idx++ ] = new int[] { i - 1, j };
        if ( i + 1 < mat.length ) N[ idx++ ] = new int[] { i + 1, j };
        if ( j - 1 >= 0 ) N[ idx++ ] = new int[] { i, j - 1 };
        if ( j + 1 < mat[ 0 ].length ) N[ idx++ ] = new int[] { i, j + 1 };

        return N;
    }

    public static void main( String[] args ) {
//        System.out.println( MyArrays.print2DArrays( new ZeroOneMatrix().updateMatrix( new int[][]{
//                { 0, 0, 0 },
//                { 0, 1, 0 },
//                { 0, 0, 0 }
//        } ), false ) );

//        System.out.println( MyArrays.print2DArrays( new ZeroOneMatrix().updateMatrix( new int[][]{
//                { 0, 0, 0 },
//                { 0, 1, 0 },
//                { 1, 1, 1 }
//        } ), false ) );

        System.out.println( MyArrays.print2DArrays( new ZeroOneMatrix().updateMatrix( new int[][] {
                { 1, 0, 0 },
                { 1, 1, 0 },
                { 1, 1, 1 }
        } ), false ) );

//        System.out.println( MyArrays.print2DArrays( new ZeroOneMatrix().updateMatrix( new int[][] {
//                { 1, 0, 1, 1, 0, 0, 1, 0, 0, 1 },
//                { 0, 1, 1, 0, 1, 0, 1, 0, 1, 1 },
//                { 0, 0, 1, 0, 1, 0, 0, 1, 0, 0 },
//                { 1, 0, 1, 0, 1, 1, 1, 1, 1, 1 },
//                { 0, 1, 0, 1, 1, 0, 0, 0, 0, 1 },
//                { 0, 0, 1, 0, 1, 1, 1, 0, 1, 0 },
//                { 0, 1, 0, 1, 0, 1, 0, 0, 1, 1 },
//                { 1, 0, 0, 0, 1, 1, 1, 1, 0, 1 },
//                { 1, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
//                { 1, 1, 1, 1, 0, 1, 0, 0, 1, 1 }
//        } ), false ) );

        // [
        // [1,0,1,1,0,0,1,0,0,1],
        // [0,1,1,0,1,0,1,0,1,1],
        // [0,0,1,0,1,0,0,1,0,0],
        // [1,0,1,0,1,1,1,1,1,1],
        // [0,1,0,1,1,0,0,0,0,1],
        // [0,0,1,0,1,1,1,0,1,0],
        // [0,1,0,1,0,1,0,0,1,1],
        // [1,0,0,0,1,2,1,1,0,1],
        // [2,1,1,1,1,2,1,0,1,0],
        // [3,2,2,1,0,1,0,0,1,1]
        // ]

//        [[ 1, 0, 0, 0, 1, 1, 1, 1, 0, 1 ],
//        [ 1, 1, 1, 1, 1, 1, 1, 0, 1, 0 ],
//        [ 1, 1, 1, 1, 0, 1, 0, 0, 1, 1 ]

//        [[ 1, 0, 0, 0 ],
//        [ 1, 1, 1, 1 ],
//        [ 1, 1, 1, 1 ]]
    }
}
