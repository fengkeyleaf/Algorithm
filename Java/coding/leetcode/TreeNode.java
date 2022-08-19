package coding.leetcode;

/*
 * TreeNode.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/22/2022$
 */

/**
 * Definition for a binary tree node.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public class TreeNode {
    public int val;
    public TreeNode left;
    public TreeNode right;

    public TreeNode() {}

    public TreeNode( int val ) {
        this.val = val;
    }

    public TreeNode( int val, TreeNode left, TreeNode right ) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    public static
    void swap( TreeNode n1, TreeNode n2 ) {
        System.out.println( n1 + " " + n2 );
        int t = n1.val;
        n1.val = n2.val;
        n2.val = t;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public String toString() {
        return val + "";
    }
}