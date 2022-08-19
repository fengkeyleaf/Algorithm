package coding.leetcode.hard;

/*
 * SerializeAndDeserializeBinaryTree.java
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
import coding.leetcode.medium.BinaryTreeLevelOrderTraversal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://leetcode.com/problems/serialize-and-deserialize-binary-tree/">297. Serialize and Deserialize Binary Tree</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class SerializeAndDeserializeBinaryTree {

    // Encodes a tree to a single string.
    public String serialize( TreeNode root ) {
        StringBuilder t = new StringBuilder( "[" );
        List<List<Integer>> Ls = new BinaryTreeLevelOrderTraversal( true ).levelOrder( root );

        // encode a tree into the following format:
        // [ first level | second level | and so on ]
        // for each level: 1,2,3,4,Integer.MIN_VALUE(null => N)
        for ( int i = 0; i < Ls.size(); i++ ) {
            for ( int j = 0; j < Ls.get( i ).size(); j++ ) {
                t.append( Ls.get( i ).get( j ) < -1000 ? "N" : Ls.get( i ).get( j ) );
                if ( j != Ls.get( i ).size() - 1 ) t.append( "," );
            }

            if ( i != Ls.size() - 1 ) t.append( "|" );
        }
        t.append( ']' );

        return t.toString();
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize( String data ) {
        System.out.println( data );

        // replace the first and last []
        String[] S = data.replace( '[', '|' ).replace( ']', '|' ).split( "\\|" );
        List<String> L = Arrays.stream( S ).filter( s -> !s.equals( "" ) ).toList();

        List<List<String>> Ls = new ArrayList<>( L.size() );
        L.forEach( l -> Ls.add( Arrays.asList( l.split( "," ) ) ) );

        System.out.println( Ls );
        // map each node value to a real tree node.
        List<List<TreeNode>> Ts = Ls.stream().map( l -> {
            List<TreeNode> T = new ArrayList<>( l.size() );
            l.forEach( n -> {
                if ( n.equals( "N" ) )
                    T.add( null );
                else
                    T.add( new TreeNode( Integer.parseInt( n ) ) );
            } );

            return T;
        } ).toList();

        // re-construct the tree structure.
        for ( int i = 0; i < Ts.size() - 1; i++ ) {
            List<TreeNode> curT = Ts.get( i );
            List<TreeNode> nextT = Ts.get( i + 1 );

            int idx = 0;
            for ( int j = 0; j < curT.size(); j++ ) {
                // note that null tree node should ignored.
                if ( curT.get( j ) == null ) continue;

                curT.get( j ).left = nextT.get( ( idx << 1 ) + 0 );
                curT.get( j ).right = nextT.get( ( idx << 1 ) + 1 );
                idx++;
            }
        }

        assert data.equals( "[]" ) || Ts.get( 0 ).size() == 1;
        return data.equals( "[]" ) ? null : Ts.get( 0 ).get( 0 );
    }

    static
    void test1() {
        TreeNode n1 = new TreeNode( 1 );
        TreeNode n2 = new TreeNode( 2 );
        TreeNode n3 = new TreeNode( 3 );
        TreeNode n4 = new TreeNode( 4 );
        TreeNode n5 = new TreeNode( 5 );

        n1.left = n2;
        n1.right = n3;
        n3.left = n4;
        n3.right = n5;

        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( n1 ) );

        SerializeAndDeserializeBinaryTree ser = new SerializeAndDeserializeBinaryTree();
        SerializeAndDeserializeBinaryTree deser = new SerializeAndDeserializeBinaryTree();
        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( deser.deserialize( ser.serialize( n1 ) ) ) );
    }

    static void test2() {
        SerializeAndDeserializeBinaryTree ser = new SerializeAndDeserializeBinaryTree();
        SerializeAndDeserializeBinaryTree deser = new SerializeAndDeserializeBinaryTree();
        System.out.println( new BinaryTreeLevelOrderTraversal().levelOrder( deser.deserialize( ser.serialize( null ) ) ) );
    }

    public static void main( String[] args ) {
//        System.out.println( getrightChildIdx( 1 ) );
//        System.out.println( getLeftChildIdx( 1 ) );

//        test1();
        test2();
    }
}
