package coding.oa.citadel;

/*
 * Q2.java
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
import java.util.List;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public class Q2 {
    List<Integer> R;
    List<Integer> B;
    int bc;
    List<Long> Res;
    boolean f;

    public Q2( List<Integer> red, List<Integer> blue, int blueCost ) {
        R = red;
        B = blue;
        bc = blueCost;
        Res = new ArrayList<>( red.size() + 1 );
        for ( int i = 0; i < R.size() + 1; i++ ) {
            Res.add( 0L );
        }
    }

    /*
     * Complete the 'minimumCost' function below.
     *
     * The function is expected to return a LONG_INTEGER_ARRAY.
     * The function accepts following parameters:
     *  1. INTEGER_ARRAY red
     *  2. INTEGER_ARRAY blue
     *  3. INTEGER blueCost
     */

    public static List<Long> minimumCost( List<Integer> red, List<Integer> blue, int blueCost ) {
        return new Q2( red, blue, blueCost ).compute();
    }

    private List<Long> compute() {
        for ( int i = 1; i < Res.size(); i++ ) {
            Res.set( i, Res.get( i - 1 ) + min( R.get( i - 1 ), B.get( i - 1 ) + ( f ? bc : 0 ) ) );
        }

        return Res;
    }

    private long min( long r, long b ) {
        if ( r <= b ) {
            f = false;
            return r;
        }

        f = true;
        return b;
    }

    public static void main( String[] args ) {
        List<Integer> red = new ArrayList<>();
        List<Integer> blue = new ArrayList<>();
        int[] reds = new int[] { 2, 3, 4 };
        int[] blues = new int[] { 2, 1, 1 };
        for ( int i : reds ) {
            red.add( i );
        }
        for ( int i : blues ) {
            blue.add( i );
        }
        System.out.println( minimumCost( red, blue, 2 ) );

    }
}
