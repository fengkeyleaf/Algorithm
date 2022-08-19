package coding.leetcode.medium;

/*
 * KokoEatingBananas.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/9/2022$
 */

import com.fengkeyleaf.util.MyArrays;

import java.util.Arrays;
import java.util.OptionalInt;

/**
 * <a href="https://leetcode.com/problems/koko-eating-bananas/">875. Koko Eating Bananas</a>
 * https://leetcode.cn/problems/koko-eating-bananas/
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public class KokoEatingBananas {

    int[] p;

    public int minEatingSpeed( int[] piles, int h ) {
        p = piles;
        int s = Arrays.stream( p ).sum();
        int l = ( int ) Math.ceil( s * 1.0 / h );
        OptionalInt u = Arrays.stream( p ).max();
        assert u.isPresent();
        int up = u.getAsInt();

        int k = -1;
        while ( l < up ) {
            k = MyArrays.mid( l, up );
            if ( isAllEaten( k, h ) ) up = k - 1;
            else l = k + 1;
        }

        assert k > 0;
        return l;
    }

    private boolean isAllEaten( int k, int h ) {
        int c = 0;
        for ( int n : p ) {
            if ( ( c += Math.ceil( n * 1.0 / k ) ) > h ) return false;
        }

        return true;
    }

    public static
    void main( String[] args ) {
//        System.out.println( new KokoEatingBananas().minEatingSpeed( new int[] { 3, 6, 7, 11 }, 8 ) );
//        System.out.println( new KokoEatingBananas().minEatingSpeed( new int[] { 30, 11, 23, 4, 20 }, 5 ) );
        System.out.println( new KokoEatingBananas().minEatingSpeed( new int[] { 30, 11, 23, 4, 20 }, 6 ) );
    }
}
