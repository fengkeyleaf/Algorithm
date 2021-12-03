package myLibraries.util.tree.tools;

/*
 * TestBST.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.tree.BinarySearchTree;
import myLibraries.util.tree.elements.MapTreeNode;

/**
 * TestPointLoction BST
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class TestBST {
    public static
    void main( String[] args ) {
        int ID = 0;
        MapTreeNode<String, Integer> node1 = new MapTreeNode<>( ID++, "S", 0 );
        MapTreeNode<String, Integer> node2 = new MapTreeNode<>( ID++, "E", 1 );
        MapTreeNode<String, Integer> node3 = new MapTreeNode<>( ID++, "A", 2 );
        MapTreeNode<String, Integer> node4 = new MapTreeNode<>( ID++, "R", 3 );
        MapTreeNode<String, Integer> node5 = new MapTreeNode<>( ID++, "C", 4 );
        MapTreeNode<String, Integer> node6 = new MapTreeNode<>( ID++, "H", 5 );
        MapTreeNode<String, Integer> node7 = new MapTreeNode<>( ID++, "E", 6 );
        MapTreeNode<String, Integer> node8 = new MapTreeNode<>( ID++, "X", 7 );

        BinarySearchTree<String, Integer> BST = new BinarySearchTree<>();
        BST.inorderPrint();
        BST.put( "S", 0 );
//        BST.put( "E", 1 );
//        BST.put( "A", 2 );
//        BST.put( "R", 3 );
//        BST.put( "C", 4 );
//        BST.put( "H", 5 );
//        BST.put( "E", 6 );
//        BST.put( "X", 7 );
//        BST.put( "A", 8 );
//        BST.put( "M", 9 );
//        BST.put( "P", 10 );
//        BST.put( "L", 11 );
//        BST.put( "E", 12 );

//        System.out.println( BST.get( "S" ) ); // 0
//        System.out.println( BST.get( "C" ) ); // 4
//        System.out.println( BST.get( "L" ) ); // 11
//        System.out.println( BST.get( "O" ) ); // null
//        System.out.println( BST.get( "MM" ) ); // null

//        BST.deleteMin();
//        BST.deleteMax();
//        BST.inorderPrint();

//        BST.delete( "E" );
//        BST.delete( "S" );
//        BST.inorderPrint();

        System.out.println( BST.floor( "E" ) ); // E
        System.out.println( BST.lower( "E" ) ); // C
        System.out.println( BST.ceiling( "E" ) ); // E
        System.out.println( BST.higher( "E" ) ); // H

        System.out.println();
        System.out.println( BST.floor( "A" ) ); // A
        System.out.println( BST.lower( "A" ) ); // NULL
        System.out.println( BST.ceiling( "A" ) ); // A
        System.out.println( BST.higher( "A" ) ); // C

        System.out.println();
        System.out.println( BST.floor( "X" ) ); // X
        System.out.println( BST.lower( "X" ) ); // S
        System.out.println( BST.ceiling( "X" ) ); // X
        System.out.println( BST.higher( "X" ) ); // NULL

        System.out.println();
        System.out.println( BST.floor( "@" ) ); // NULL
        System.out.println( BST.lower( "@" ) ); // NULL
        System.out.println( BST.ceiling( "@" ) ); // A
        System.out.println( BST.higher( "@" ) ); // A

        System.out.println();
        System.out.println( BST.floor( "Z" ) ); // X
        System.out.println( BST.lower( "Z" ) ); // X
        System.out.println( BST.ceiling( "Z" ) ); // NULL
        System.out.println( BST.higher( "Z" ) ); // NULL
    }
}
