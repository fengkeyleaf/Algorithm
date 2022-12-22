package coding.oa.akunacapital;

/*
 * Q1.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/26/2022$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// optimize: pre-sort + binary search
public class Q1 {
    List<Integer> A;
    int d;
    int c;

    public Q1( List<Integer> arr, int d ) {
        A = arr;
        this.d = d;
    }

    /*
     * Complete the 'getTripletCount' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     *  1. INTEGER_ARRAY arr
     *  2. INTEGER d
     */

    public static int getTripletCount( List<Integer> arr, int d ) {
        Q1 q = new Q1( arr, d );
        for ( int i = 0; i < arr.size(); i++ ) {
            q.compute( 0, i, 0 );
        }
        // Write your code here
        return q.c;
    }

    private void compute( int s, int i, int c ) {
        if ( c + 1 == 3 && ( s + A.get( i ) ) % d == 0 ) {
            this.c++;
        }

        for ( int j = i + 1; j < A.size(); j++ ) {
            compute( s + A.get( i ), j, c + 1 );
        }
    }

    public static void main( String[] args ) {
        List<Integer> arr = new ArrayList<>();
        int[] A = new int[] { 3, 3, 4, 7, 8 };
        for ( int i : A ) arr.add( i );
        System.out.println( getTripletCount( arr, 5 ) );
    }
}

