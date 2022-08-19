package coding.leetcode.medium;

/*
 * LowestCommonAncestorofaBinaryTree.java
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

import java.util.Iterator;
import java.util.LinkedList;

/**
 * <a href="https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/">236. Lowest Common Ancestor of a Binary Tree</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class LowestCommonAncestorOfABinaryTree {

    public TreeNode lowestCommonAncestor( TreeNode root, TreeNode p, TreeNode q ) {
        Iterator<TreeNode> iP = lowestCommonAncestor( root, p ).descendingIterator();
        Iterator<TreeNode> iQ = lowestCommonAncestor( root, q ).descendingIterator();
        TreeNode n = null;
        while ( iP.hasNext() && iQ.hasNext() ) {
            TreeNode nP = iP.next();
            TreeNode nQ = iQ.next();
            if ( nP != nQ ) return n;

            n = nP;
        }

        assert n != null;
        return n;
    }

    LinkedList<TreeNode> lowestCommonAncestor( TreeNode r, TreeNode t ) {
        if ( r == null ) return null;
        if ( r == t ) {
            LinkedList<TreeNode> Q = new LinkedList<>();
            Q.add( r );
            return Q;
        }

        LinkedList<TreeNode> L = lowestCommonAncestor( r.left, t );
        LinkedList<TreeNode> R = lowestCommonAncestor( r.right, t );

        if ( L != null ) {
            L.add( r );
            return L;
        }
        else if ( R != null ) {
            R.add( r );
            return R;
        }

        return null;
    }

    static
    void test1() {
        TreeNode n1 = new TreeNode( 3 );
        TreeNode n2 = new TreeNode( 5 );
        TreeNode n3 = new TreeNode( 1 );
        TreeNode n4 = new TreeNode( 6 );
        TreeNode n5 = new TreeNode( 2 );
        TreeNode n6 = new TreeNode( 0 );
        TreeNode n7 = new TreeNode( 8 );
        TreeNode n8 = new TreeNode( 7 );
        TreeNode n9 = new TreeNode( 4 );

        n1.left = n2;
        n1.right = n3;
        n2.left = n4;
        n2.right = n5;
        n3.left = n6;
        n3.right = n7;
        n5.left = n8;
        n5.right = n9;

        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( n1 ) );
        System.out.println( new LowestCommonAncestorOfABinaryTree().lowestCommonAncestor( n1, n2, n3 ) );
        System.out.println( new LowestCommonAncestorOfABinaryTree().lowestCommonAncestor( n1, n2, n9 ) );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
