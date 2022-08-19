package coding.leetcode.hard;

/*
 * MedianOfTwoSortedArrays.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/11/2022$
 */

import com.fengkeyleaf.util.MyArrays;

import java.util.Arrays;

/**
 * <a href="https://leetcode.com/problems/median-of-two-sorted-arrays/">4. Median of Two Sorted Arrays</a>
 * https://leetcode.cn/problems/median-of-two-sorted-arrays/
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://leetcode.com/problems/median-of-two-sorted-arrays/discuss/2496/Concise-JAVA-solution-based-on-Binary-Search
public final class MedianOfTwoSortedArrays {

    public double findMedianSortedArrays( int[] nums1, int[] nums2 ) {
        // Deal with invalid corner case.
        if ( nums1.length == 0 ) return findMedian( nums2 );
        else if ( nums2.length == 0 ) return findMedian( nums1 );

        int m = nums1.length, n = nums2.length;
        int l = ( m + n + 1 ) / 2; //left half of the combined median
        int r = ( m + n + 2 ) / 2; //right half of the combined median

        // If the nums1.length + nums2.length is odd, the 2 function will return the same number
        // Else if nums1.length + nums2.length is even, the 2 function will return the left number and right number that make up a median
        return ( getKth( nums1, 0, nums2, 0, l ) + getKth( nums1, 0, nums2, 0, r ) ) / 2.0;
    }

    private double getKth( int[] nums1, int start1,
                           int[] nums2, int start2, int k ) {

        // This function finds the Kth element in nums1 + nums2

        // If nums1 is exhausted, return kth number in nums2
        if ( start1 > nums1.length - 1 ) return nums2[ start2 + k - 1 ];

        // If nums2 is exhausted, return kth number in nums1
        if ( start2 > nums2.length - 1 ) return nums1[ start1 + k - 1 ];

        // If k == 1, return the first number
        // Since nums1 and nums2 is sorted, the smaller one among the start point of nums1 and nums2 is the first one
        if ( k == 1 ) return Math.min( nums1[ start1 ], nums2[ start2 ] );

        int mid1 = Integer.MAX_VALUE;
        int mid2 = Integer.MAX_VALUE;
        if ( start1 + k / 2 - 1 < nums1.length ) mid1 = nums1[ start1 + k / 2 - 1 ];
        if ( start2 + k / 2 - 1 < nums2.length ) mid2 = nums2[ start2 + k / 2 - 1 ];

        // Throw away half of the array from nums1 or nums2. And cut k in half
        if ( mid1 < mid2 ) {
            return getKth( nums1, start1 + k / 2, nums2, start2, k - k / 2 ); //nums1.right + nums2
        } else {
            return getKth( nums1, start1, nums2, start2 + k / 2, k - k / 2 ); //nums1 + nums2.right
        }
    }

    private double findMedian( int[] N ) {
        int mid = MyArrays.mid( 0, N.length - 1 );
        if ( N.length % 2 == 0 ) return ( N[ mid ] + N[ mid + 1 ] ) / 2.0;

        return N[ mid ];
    }

    public static
    void main( String[] args ) {
//        System.out.println( new MedianOfTwoSortedArrays().findMedianSortedArrays( new int[] { 1, 2 }, new int[] { 3, 4 } ) );
//        System.out.println( new MedianOfTwoSortedArrays().findMedianSortedArrays( new int[] { 1, 3 }, new int[] { 2 } ) );
//        System.out.println( new MedianOfTwoSortedArrays().findMedianSortedArrays( new int[] { 1, 3 }, new int[] {} ) );
//        System.out.println( new MedianOfTwoSortedArrays().findMedianSortedArrays( new int[] { 1 }, new int[] {} ) );
//        System.out.println( new MedianOfTwoSortedArrays().findMedianSortedArrays( new int[] { 1, 2, 3, 4 }, new int[] { -1, 3, 10, 29 } ) );
        System.out.println( new MedianOfTwoSortedArrays().findMedianSortedArrays( new int[] { 1, 2 }, new int[] { -1, 3 } ) );
    }
}
