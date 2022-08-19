package coding.leetcode.medium;

/*
 * RecoverBinarySearchTree.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/25/2022$
 */

import coding.leetcode.TreeNode;

/**
 * <a href="https://leetcode.com/problems/recover-binary-search-tree/">99. Recover Binary Search Tree</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public class RecoverBinarySearchTree {

    TreeNode errorPrev = null;
    TreeNode errorCur = null;

    // reference resource: https://www.cnblogs.com/AnnieKim/archive/2013/06/15/morristraversal.html
    public void recoverTree( TreeNode root ) {
        TreeNode cur = root;

        TreeNode prev = null;
        TreeNode next = null;
        // traversal process.
        while ( cur != null ) {
            if ( cur.left == null ) {
                prev = next;
                next = cur;

                checkError( prev, next );
                cur = cur.right;
                continue;
            }

            TreeNode p = max( cur.left, cur );
            // backup the node we'll return to.
            if ( p.right == null ) {
                p.right = cur;
                cur = cur.left;
                continue;
            }

            // recover the tree structure.
            if ( p.right == cur ) {
                p.right = null;

                prev = next;
                next = cur;

                checkError( prev, next );
                cur = cur.right;
            }
        }

        assert errorCur != null && errorPrev != null : errorCur + " | " + errorPrev;
        TreeNode.swap( errorPrev, errorCur );
    }

    void checkError( TreeNode prev,
                     TreeNode cur ) {

        if ( prev == null || cur == null ) return;

        if ( prev.val > cur.val ) {
            System.out.println( prev + " " + cur );
            if ( errorPrev == null && errorCur == null ) {
                errorPrev = prev;
                errorCur = cur;
                return;
            }

            assert errorPrev != null && errorCur != null;
            errorCur = cur;
        }
    }

    private TreeNode max( TreeNode left, TreeNode cur ) {

        while ( left.right != null && left.right != cur )
            left = left.right;

        return left;
    }

    static
    void test1() {
        TreeNode n1 = new TreeNode( 3 );
        TreeNode n2 = new TreeNode( 1 );
        TreeNode n3 = new TreeNode( 4 );
        TreeNode n4 = new TreeNode( 2 );

        n1.left = n2;
        n1.right = n3;
        n3.left = n4;

        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( n1 ) );
        new RecoverBinarySearchTree().recoverTree( n1 );
        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( n1 ) );
    }

    static
    void test2() {
        TreeNode n1 = new TreeNode( 3 );
        TreeNode n2 = new TreeNode( 1 );
        TreeNode n3 = new TreeNode( 2 );

        n2.left = n1;
        n1.right = n3;

        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( n2 ) );
        new RecoverBinarySearchTree().recoverTree( n2 );
        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( n2 ) );
    }

    public static
    void main( String[] args ) {
//        test1();
        test2();
    }
}
