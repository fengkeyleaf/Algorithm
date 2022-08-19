package coding.leetcode.medium;

/*
 * SumRootToLeafNumbers.java
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
 * <a href="https://leetcode.com/problems/sum-root-to-leaf-numbers/">129. Sum Root to Leaf Numbers</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class SumRootToLeafNumbers {

    public int sumNumbers( TreeNode root ) {
        int sum = 0;
        for ( String n : leafPaths( root ) )
            sum += Integer.parseInt( n );

        return sum;
    }

    // TODO: 7/22/2022 StringBuilder to improve efficiency.
    String[] leafPaths( TreeNode r ) {
        if ( r == null ) return new String[] {};

        String[] L = leafPaths( r.left );
        String[] R = leafPaths( r.right );

        String[] S = new String[ L.length + R.length ];
        int idx = 0;
        for ( String s : L )
            S[ idx++ ] = r.val + s;
        for ( String s : R )
            S[ idx++ ] = r.val + s;

        return S.length == 0 ? new String[] { String.valueOf( r.val ) } : S;
    }

    static
    void test1() {
        TreeNode n1 = new TreeNode( 1 );
        TreeNode n2 = new TreeNode( 2 );
        TreeNode n3 = new TreeNode( 3 );

        n1.left = n2;
        n1.right = n3;

        System.out.println( new SumRootToLeafNumbers().sumNumbers( n1 ) );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
