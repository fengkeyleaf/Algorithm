package coding.leetcode.medium;

/*
 * KthSmallestElementInABST.java
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
import com.fengkeyleaf.util.tree.BinarySearchTree;
import com.fengkeyleaf.util.tree.MapTreeNode;

import java.util.List;

/**
 * <a href="https://leetcode.com/problems/kth-smallest-element-in-a-bst/">230. Kth Smallest Element in a BST</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public class KthSmallestElementInABST {

    static class BST extends BinarySearchTree<Integer, Integer> {
        int kthSmallest( int k ) {
            return kthSmallest( root, k );
        }

        private int kthSmallest( MapTreeNode<Integer, Integer> root, int k ) {
            if ( ( root.left == null ? 1 : root.left.getNumberOfChildren() + 1 ) == k )
                return root.val;

            if ( root.left != null && root.left.getNumberOfChildren() + 1 > k )
                return kthSmallest( root.left, k );

            return kthSmallest( root.right, k - ( root.getNumberOfChildren() - root.right.getNumberOfChildren() ) );
        }
    }

    public int kthSmallest( TreeNode root, int k ) {
        return convertToBST( root ).kthSmallest( k );
    }

    // TODO: 7/25/2022 time limit exceeded due to the converting process O(n),
    //  but the idea is correct and can handle the follow up,
    //  and can solve the problem in O(logn).
    static
    BST convertToBST( TreeNode n ) {
        List<List<Integer>> L = new BinaryTreeLevelOrderTraversal().levelOrder( n );
        BST T = new BST();
        L.forEach( l -> l.forEach( i -> T.put( i, i ) ) );

        System.out.println( T );
        return T;
    }

    static
    void test1() {
        TreeNode n1 = new TreeNode( 3 );
        TreeNode n2 = new TreeNode( 1 );
        TreeNode n3 = new TreeNode( 4 );
        TreeNode n4 = new TreeNode( 2 );

        n1.left = n2;
        n1.right = n3;
        n2.right = n4;

//        System.out.println( new KthSmallestElementInABST().kthSmallest( n1, 1 ) );
        System.out.println( new KthSmallestElementInABST().kthSmallest( n1, 2 ) );
        System.out.println( new KthSmallestElementInABST().kthSmallest( n1, 3 ) );
        System.out.println( new KthSmallestElementInABST().kthSmallest( n1, 4 ) );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
