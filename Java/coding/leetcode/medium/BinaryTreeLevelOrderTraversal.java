package coding.leetcode.medium;

/*
 * BinaryTreeLevelOrderTraversal.java
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <a href="https://leetcode.com/problems/binary-tree-level-order-traversal/">102. Binary Tree Level Order Traversal</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public final class BinaryTreeLevelOrderTraversal {
    boolean isIncludingNull;

    public BinaryTreeLevelOrderTraversal() {}

    public BinaryTreeLevelOrderTraversal( boolean isIncludingNull ) {
        this.isIncludingNull = isIncludingNull;
    }

    public List<List<Integer>> levelOrder( TreeNode root ) {
        List<List<Integer>> res = new ArrayList<>();

        if ( root == null ) return res;

        LinkedList<TreeNode> q = new LinkedList<>();
        q.add( root );
        int sizePre = 1;
        List<Integer> L = new ArrayList<>();
        int sizeCur = 0;

        while ( !q.isEmpty() ) {
            TreeNode f = q.poll();
            if ( sizePre-- > 0 ) L.add( f == null ? Integer.MIN_VALUE : f.val );

            if ( ( isIncludingNull && f != null ) || ( !isIncludingNull && f.left != null ) ) {
                q.add( f.left );
                sizeCur++;
            }

            if ( ( isIncludingNull && f != null ) || ( !isIncludingNull && f.right != null ) ) {
                q.add( f.right );
                sizeCur++;
            }

            if ( sizePre <= 0 ) {
                res.add( L );
                L = new ArrayList<>();
                sizePre = sizeCur;
                sizeCur = 0;
            }
        }

        return res;
    }

    static
    void test1() {
        TreeNode n1 = new TreeNode( 3 );
        TreeNode n2 = new TreeNode( 9 );
        TreeNode n3 = new TreeNode( 20 );
        TreeNode n4 = new TreeNode( 15 );
        TreeNode n5 = new TreeNode( 7 );

        n1.left = n2;
        n1.right = n3;
        n3.left = n4;
        n3.right = n5;

        System.out.println( new BinaryTreeLevelOrderTraversal( true ).levelOrder( n1 ) );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
