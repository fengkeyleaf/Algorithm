package coding.leetcode.medium;

/*
 * ValidateBinarySearchTree.java
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

/**
 * <a href="https://leetcode.com/problems/validate-binary-search-tree/">98. Validate Binary Search Tree</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public final class ValidateBinarySearchTree {

    /**
     * reference resource: {@link BinarySearchTree}
     * */

    public boolean isValidBST( TreeNode root ) {
        return isBST( root, null, null );
    }

    private boolean isBST( TreeNode r, Integer min, Integer max ) {
        if ( r == null ) return true;

        if ( min != null && r.val <= min )
            return false;
        if ( max != null && r.val >= max )
            return false;

        return isBST( r.left, min, r.val ) && isBST( r.right, r.val, max );
    }

}
