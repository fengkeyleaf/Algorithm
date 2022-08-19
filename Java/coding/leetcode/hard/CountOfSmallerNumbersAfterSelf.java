package coding.leetcode.hard;

/*
 * CountOfSmallerNumbersAfterSelf.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/23/2022$
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://leetcode.com/problems/count-of-smaller-numbers-after-self/">315. Count of Smaller Numbers After Self</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public final class CountOfSmallerNumbersAfterSelf {

    class Num {
        int n;
        int idx = -1;
        int c;

        Num( int n, int idx, int c ) {
            this.n = n;
            this.idx = idx;
            this.c = c;
        }

        @Override
        public String toString() {
            return n + " | " + c;
        }
    }

    public List<Integer> countSmaller( int[] nums ) {
        Num[] N = convertToNum( nums );
        return collect( mergeSort( N ) );
    }

    Num[] mergeSort( Num[] N ) {
        if ( N.length <= 1 ) return N;
//        System.out.println( Arrays.toString( N ) );

        int mid = ( N.length - 0 ) / 2 + 0;
        Num[] L = mergeSort( Arrays.copyOfRange( N, 0, mid ) );
        Num[] R = mergeSort( Arrays.copyOfRange( N, mid, N.length ) );

        int idxL = 0;
        int idxR = 0;

        Num[] M = new Num[ L.length + R.length ];
        int idx = 0;
        int more = 0;
        while ( idxL < L.length && idxR < R.length ) {
            if ( L[ idxL ].n <= R[ idxR ].n ) {
                M[ idx++ ] = L[ idxL++ ];
                M[ idx - 1 ].c += more;
            } else {
                M[ idx++ ] = R[ idxR++ ];
                more++;
            }
        }

        if ( idxL < L.length ) {
            copyleft( M, L, idx, idxL, more );
        } else if ( idxR < R.length ) {
            copyleft( M, R, idx, idxR, 0 );
        }

//        System.out.println( Arrays.toString( M ) );
        return M;
    }

    void copyleft( Num[] M, Num[] N, int idxM, int idxN, int more ) {
        while ( idxN < N.length ) {
            M[ idxM++ ] = N[ idxN++ ];
            M[ idxM - 1 ].c += more;
        }
    }


    Num[] convertToNum( int[] nums ) {
        Num[] N = new Num[ nums.length ];

        for ( int i = 0; i < nums.length; i++ ) {
            N[ i ] = new Num( nums[ i ], i, 0 );
        }

        return N;
    }

    List<Integer> collect( Num[] N ) {
        int[] res = new int[ N.length ];

        for ( Num n : N ) {
            res[ n.idx ] = n.c;
        }

        List<Integer> L = new ArrayList<>( res.length );
        for ( int n : res ) {
            L.add( n );
        }
        return L;
    }

    public static void main( String[] args ) {
//        System.out.println( new CountOfSmallerNumbersAfterSelf().countSmaller( new int[] { 5, 2, 6, 1 } ) );
//        System.out.println( new CountOfSmallerNumbersAfterSelf().countSmaller( new int[] { -1 } ) );
//        System.out.println( new CountOfSmallerNumbersAfterSelf().countSmaller( new int[] { -1, -1 } ) );
        System.out.println( new CountOfSmallerNumbersAfterSelf().countSmaller( new int[] { 1, 9, 7, 8, 5 } ) );
    }
}
