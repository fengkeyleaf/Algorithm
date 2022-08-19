package coding.leetcode.medium;

/*
 * IncreasingTripletSubsequence.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/21/2022$
 */

/**
 * <a href="https://leetcode.com/problems/increasing-triplet-subsequence/">334. Increasing Triplet Subsequence</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class IncreasingTripletSubsequence {

    // reference resource: https://leetcode.com/problems/increasing-triplet-subsequence/discuss/79004/Concise-Java-solution-with-comments.
    public boolean increasingTriplet( int[] nums ) {
        int s = Integer.MAX_VALUE, b = Integer.MAX_VALUE;
        for ( int n : nums ) {
            // found a number less than the smallest.
            if ( n <= s ) s = n;
            // found a number less than the second smallest,
            // but greater than the smallest.
            else if ( n <= b ) b = n;
            // found a number greater than both numbers.
            // i.e. found the target triplet.
            else return true;
        }

        // no such a triple.
        return false;
    }

    public static
    void main( String[] args ) {
        System.out.println( new IncreasingTripletSubsequence().increasingTriplet( new int[] { 1, 2, 3, 4, 5 } ) );
        System.out.println( new IncreasingTripletSubsequence().increasingTriplet( new int[] { 5, 4, 3, 2, 1 } ) );
        System.out.println( new IncreasingTripletSubsequence().increasingTriplet( new int[] { 2, 1, 5, 0, 4, 6 } ) );
    }
}
