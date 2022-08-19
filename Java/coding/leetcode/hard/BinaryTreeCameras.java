package coding.leetcode.hard;

/*
 * BinaryTreeCameras.java
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
 * <a href="https://leetcode.com/problems/binary-tree-cameras/">968. Binary Tree Cameras</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class BinaryTreeCameras {

    int numberCameras = 0;

    public int minCameraCover( TreeNode root ) {
        return color( root ) == -1 ? numberCameras + 1 : numberCameras;
    }

    // -1: NOT MONITORED
    //  0: MONITORED
    //  1: HAS CAMERA

    // reference resource: https://leetcode.com/problems/binary-tree-cameras/discuss/2160360/Visual-Explanation-or-JAVA-Greedy
    int color( TreeNode n ) {
        if ( n == null ) return 0;

        int l = color( n.left );
        int r = color( n.right );

        if ( l == -1 || r == -1 ) {
            numberCameras++;
            return 1;
        }

        if ( l == 1 || r == 1 )
            return 0;

        return -1;
    }
}
