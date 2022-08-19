package coding.leetcode.medium;

/*
 * PartitionToKEqualSumSubsets.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/9/2022$
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://leetcode.com/problems/partition-to-k-equal-sum-subsets/">698. Partition to K Equal Sum Subsets</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://leetcode.com/problems/partition-to-k-equal-sum-subsets/discuss/180014/Backtracking-x-2
public class PartitionToKEqualSumSubsets {

    int[] N;
    Object[] nums;
    int s;

    public boolean canPartitionKSubsets( int[] nums, int k ) {
        int s = Arrays.stream( nums ).sum();
        if ( s % k != 0 ) return false;

        this.s = s / k;
        N = new int[ k ];
        List<Integer> L = new ArrayList<>( nums.length );
        for ( int n : nums ) {
            L.add( n );
        }
        this.nums = L.stream().sorted( ( n1, n2 ) -> -Integer.compare( n1, n2 ) ).toArray();

        return search( 0 );
    }

    private boolean search( int i ) {
        if ( i == nums.length )
            return isSolution();

        for ( int j = 0; j < N.length; j++ ) {
            if ( N[ j ] + ( int ) nums[ i ] <= s ) {
                N[ j ] += ( int ) nums[ i ];
                if ( search( i + 1 ) )
                    return true;

                N[ j ] -= ( int ) nums[ i ];
            }
        }

        return false;
    }

    private boolean isSolution() {
        for ( int n : N ) {
            if ( n != s ) return false;
        }

        return true;
    }
}
