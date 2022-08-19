package coding.leetcode.medium;

/*
 * DeleteNodeInABST.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/26/2022$
 */

import coding.leetcode.TreeNode;
import com.fengkeyleaf.util.tree.BinarySearchTree;

/**
 * <a href="https://leetcode.com/problems/delete-node-in-a-bst/">450. Delete Node in a BST</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public class DeleteNodeInABST {

    /**
     * reference resource: {@link BinarySearchTree#delete(Object)}
     * */

    public TreeNode deleteNode( TreeNode root, int key ) {
        if ( root == null ) return null;

        if ( root.val > key )
            root.left = deleteNode( root.left, key );
        else if ( root.val < key )
            root.right = deleteNode( root.right, key );
        else {
            if ( root.left == null ) return root.right;
            if ( root.right == null ) return root.left;

            TreeNode t = root;
            root = min( root.right );
            root.right = deleteMin( t.right );
            root.left = t.left;
        }

        return root;
    }

    private TreeNode min( TreeNode n ) {
        if ( n == null ) return null;
        if ( n.left == null ) return n;

        return min( n.left );
    }

    TreeNode deleteMin( TreeNode n ) {
        if ( n == null ) return null;
        if ( n.left == null ) return n.right;

        n.left = deleteMin( n.left );
        return n;
    }

    static
    void test1() {
        TreeNode n1 = new TreeNode( 5 );
        TreeNode n2 = new TreeNode( 2 );
        TreeNode n3 = new TreeNode( 6 );
        TreeNode n4 = new TreeNode( 4 );
        TreeNode n5 = new TreeNode( 7 );

        n1.left = n2;
        n1.right = n3;
        n2.right = n4;
        n3.right = n5;

        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( new DeleteNodeInABST().deleteNode( n1, 5 ) ) );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
