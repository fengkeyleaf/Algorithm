package coding.leetcode.hard;

/*
 * BinaryTreeMaximumPathSum.java
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

/**
 * <a href="https://leetcode.com/problems/binary-tree-maximum-path-sum/">124. Binary Tree Maximum Path Sum</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class BinaryTreeMaximumPathSum {
    int maxGlobal = Integer.MIN_VALUE;

    public int maxPathSum( TreeNode root ) {
        findMax( root );
        return maxGlobal;
    }

    // reference resource: https://leetcode.com/problems/binary-tree-maximum-path-sum/discuss/389609/Full-Explanation-article-with-pseudo-code-Beats-JAVA-100-time-and-100-space-Solution
    int findMax( TreeNode n ) {
        if ( n == null ) return 0;

        // get max from left and right subtree separately.
        int maxL = findMax( n.left );
        int maxR = findMax( n.right );

        // maximize only including one of the subtrees.
        int maxLR = Math.max( maxL, maxR );
        // maximize between only the root and root plus one of the subtrees.
        int maxRootIncluded = Math.max( n.val, n.val + maxLR );
        // maximize between others and root plus two subtrees,
        // but in this way, its parents will be excluded.
        int maxAll = Math.max( maxRootIncluded, n.val + maxL + maxR );

        // update global max.
        maxGlobal = Math.max( maxGlobal, maxAll );

        // return the local maximum,
        // and assume this root is included in the maxiumu path.
        return maxRootIncluded;
    }
}
