package coding.oa.citadel;

/*
 * Q1.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/25/2022$
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public class Q1 {
    int k;
    List<Integer> N;
    long[] S;
    int[] D;

    public Q1( int k, List<Integer> nums ) {
        this.k = k;
        N = nums;
        S = new long[ nums.size() + 1 ];
        D = new int[ nums.size() + 1 ];
    }


    /*
     * Complete the 'kSub' function below.
     *
     * The function is expected to return a LONG_INTEGER.
     * The function accepts following parameters:
     *  1. INTEGER k
     *  2. INTEGER_ARRAY nums
     */

    public static
    long kSub( int k, List<Integer> nums ) {
        return new Q1( k, nums ).compute();

    }

    private long compute() {
        for ( int i = 1; i < S.length; i++ ) {
            int n = N.get( i - 1 );
            S[ i ] = S[ i - 1 ] + n;
            D[ i ] = D[ i - 1 ] + f( S[ i ], i - 1 );
        }

        System.out.println( Arrays.toString( D ) );
        return D[ N.size() ];
    }

    private int f( long s, int i ) {
        int c = 0;

        if ( s % k == 0 ) c++;
        for ( int j = 0; j < i; j++ ) {
            s -= N.get( j );
            if ( s % k == 0 ) c++;
        }

        return c;
    }

    public static void main( String[] args ) {
        List<Integer> I = new ArrayList<>();
        int[] Is = new int[] { 5, 10, 11, 9, 5 };
        for ( int i : Is ) {
            I.add( i );
        }
        System.out.println( Q1.kSub( 5, I ) );
    }
}
