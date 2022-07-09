package com.fengkeyleaf.util.tree;

/*
 * TestBST.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import java.util.Iterator;

/**
 * Test BST
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class TestBST {
    static
    void testRegular() {
        BinarySearchTree<String, Integer> BST = new BinarySearchTree<>();
        BST.put( "S", 0 );
        BST.put( "E", 1 );
        BST.put( "A", 2 );
        BST.put( "R", 3 );
        BST.put( "C", 4 );
        BST.put( "H", 5 );
        BST.put( "E", 6 );
        BST.put( "X", 7 );
        BST.put( "A", 8 );
        BST.put( "M", 9 );
        BST.put( "P", 10 );
        BST.put( "L", 11 );
        BST.put( "E", 12 );

        System.out.println( BST.get( "S" ) ); // 0
        System.out.println( BST.get( "C" ) ); // 4
        System.out.println( BST.get( "L" ) ); // 11
        System.out.println( BST.get( "O" ) ); // null
        System.out.println( BST.get( "MM" ) ); // null

        BST.deleteMin();
        BST.deleteMax();
        System.out.println( BST );

        BST.delete( "E" );
        BST.delete( "S" );
        System.out.println( BST );

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

    static
    void testPreSucc() {
        BinarySearchTree<String, Integer> BST = new BinarySearchTree<>();
        BST.inorderPrint();
        BST.put( "S", 0 );
        BST.put( "E", 1 );
        BST.put( "A", 2 );
        BST.put( "R", 3 );
        BST.put( "C", 4 );
        BST.put( "H", 5 );
        BST.put( "E", 6 );
        BST.put( "X", 7 );
        BST.put( "A", 8 );
        BST.put( "M", 9 );
        BST.put( "P", 10 );
        BST.put( "L", 11 );
        BST.put( "E", 12 );

        Iterator<MapTreeNode<String, Integer>> iterator = BST.iterator();

//        MapTreeNode<String, Integer> nE = BST.getNode( "E" );
//        System.out.println( BST.predecessor( nE ) ); // C
//        System.out.println( BST.successor( nE ) ); // H
//
//        MapTreeNode<String, Integer> nA = BST.getNode( "A" );
//        System.out.println( BST.predecessor( nA ) ); // null
//        System.out.println( BST.successor( nA ) ); // C
//
//        MapTreeNode<String, Integer> nX = BST.getNode( "X" );
//        System.out.println( BST.predecessor( nX ) ); // S
//        System.out.println( BST.successor( nX ) ); // null
//
//        MapTreeNode<String, Integer> nM = BST.getNode( "M" );
//        System.out.println( BST.predecessor( nM ) ); // L
//        System.out.println( BST.successor( nM ) ); // P
    }

    public static
    void main( String[] args ) {
//        testRegular();
        testPreSucc();
    }
}
