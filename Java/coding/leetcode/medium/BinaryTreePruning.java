package coding.leetcode.medium;

/*
 * BinaryTreePruning.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/22/2022$
 */

import coding.leetcode.TreeNode;

/**
 * <a href="https://leetcode.com/problems/binary-tree-pruning/">814. Binary Tree Pruning</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class BinaryTreePruning {

    public TreeNode pruneTree( TreeNode root ) {
        if ( root == null || isZero( root ) ) return null;

        root.left = pruneTree( root.left );
        root.right = pruneTree( root.right );

        return isZero( root ) ? null : root;
    }

    boolean isZero( TreeNode root ) {
        return root.val == 0 && root.left == null && root.right == null;
    }

    static
    void test1() {
        TreeNode n1 = new TreeNode( 1 );
        TreeNode n2 = new TreeNode( 0 );
        TreeNode n3 = new TreeNode( 0 );
        TreeNode n4 = new TreeNode( 1 );

        n1.right = n2;
        n2.left = n3;
        n2.right = n4;

        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( new BinaryTreePruning().pruneTree( n1 ) ) );
    }

    static
    void test2() {
        TreeNode n1 = new TreeNode( 1 );
        TreeNode n2 = new TreeNode( 0 );
        TreeNode n3 = new TreeNode( 1 );
        TreeNode n4 = new TreeNode( 0 );
        TreeNode n5 = new TreeNode( 0 );
        TreeNode n6 = new TreeNode( 0 );
        TreeNode n7 = new TreeNode( 1 );

        n1.left = n2;
        n1.right = n3;
        n2.left = n4;
        n2.right = n5;
        n3.left = n6;
        n3.right = n7;

        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( n1 ) );
        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( new BinaryTreePruning().pruneTree( n1 ) ) );
    }

    public static
    void main( String[] args ) {
//        test1();
        test2();
    }
}
