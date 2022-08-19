package coding.leetcode.medium;

/*
 * MostFrequentSubtreeSum.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/23/2022$
 */

import coding.leetcode.TreeNode;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * <a href="https://leetcode.com/problems/most-frequent-subtree-sum/">508. Most Frequent Subtree Sum</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class MostFrequentSubtreeSum {
    // TODO: 7/23/2022 HashMap to improve efficiency.
    final TreeMap<Integer, Integer> m = new TreeMap<>();

    public int[] findFrequentTreeSum( TreeNode root ) {
        sum( root );

        int max = findMaxFrequency();

        System.out.println( max );
        List<Integer> L = m.keySet().stream().filter( k -> m.get( k ) == max ).toList();
        int[] res = new int[ L.size() ];
        int idx = 0;
        for ( Integer n : L ) {
            res[ idx++ ] = n;
        }

        return res;
    }

    int findMaxFrequency() {
        int max = 0;
        for ( Integer k : m.keySet() )
            max = Math.max( max, m.get( k ) );

        return max;
    }

    Integer sum( TreeNode n ) {
        if ( n == null ) return 0;

        Integer l = sum( n.left );
        Integer r = sum( n.right );

        int s = l + r + n.val;
        if ( m.containsKey( s ) ) m.put( s, m.get( s ) + 1 );
        else m.put( s, 1 );

        return s;
    }

    static
    void test1() {
        TreeNode n1 = new TreeNode( 5 );
        TreeNode n2 = new TreeNode( 2 );
        TreeNode n3 = new TreeNode( -5 );

        n1.left = n2;
        n1.right = n3;

        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( n1 ) );
        System.out.println( Arrays.toString( new MostFrequentSubtreeSum().findFrequentTreeSum( n1 ) ) );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
