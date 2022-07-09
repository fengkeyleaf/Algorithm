package com.fengkeyleaf.util.tree;

/*
 * TestLinkedBBST.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/28/2022$
 */

/**
 * Test Doubly-linked BBST
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class TestLinkedBBST {

    private static
    void testDoublyConnected() {
        DoublyLinkedRBT<String, String> BBST = new DoublyLinkedRBT<>();
        BBST.put( "S", "S", null, true );
        BBST.put( "E", "E", "S", true );
        BBST.put( "A", "A", "E", true );
//        System.out.println( BBST );

//        System.out.println();
//        System.out.println( BBST.deleteNodeAndGetVal( "S" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "E" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "A" ) );
//        System.out.println( BBST );

//        System.out.println();
//        BBST.put( "R", "R", "S", true );
//        System.out.println( BBST );

//        System.out.println();
//        System.out.println( BBST.deleteNodeAndGetVal( "R" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "S" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "E" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "A" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "Z" ) ); // null
//        System.out.println( BBST );

//        BBST.put( "C", "C", "A", false );
//        BBST.put( "H", "H", "R", true );
//        BBST.put( "X", "X", "S", false );
//        BBST.put( "M", "M", "H", false );
//        BBST.put( "P", "P", "M", false );
//        BBST.put( "L", "L", "H", false );

//        System.out.println( BBST );
//        BBST.deleteMin(); // A -> 3
//        System.out.println( BBST );
//        BBST.deleteMax(); // X -> 7
//        System.out.println( BBST );

//        System.out.println( BBST.deleteNodeAndGetVal( "M" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "H" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "R" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "P" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "S" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "E" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "A" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "L" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "X" ) );
//        System.out.println( BBST.deleteNodeAndGetVal( "C" ) );
//        System.out.println( BBST );

        System.out.println();
        DoublyLinkedRBT<Integer, Integer> BBST2 = new DoublyLinkedRBT<>();
        BBST2.put( 2, 2 ); // 1
        BBST2.put( 1, 1 ); // 2
        BBST2.put( 3, 3 ); // 3
        System.out.println( BBST2 );

        System.out.println( BBST2.deleteAndGetVal( 0 ) ); // null
        System.out.println( BBST2.deleteAndGetVal( 4 ) ); // null
        System.out.println( BBST2 );
        BBST2.deleteMin();
        BBST2.deleteMin();
        BBST2.deleteMin();
        System.out.println( BBST2 );

        DoublyLinkedRBT<String, String> BBST3 = new DoublyLinkedRBT<>();
        BBST3.put( "H", "H" );
        BBST3.put( "C", "C" );
        BBST3.put( "R", "R" );
        BBST3.put( "A", "A" );

        System.out.println( BBST3.deleteAndGetVal( "C" ) );
        System.out.println( BBST2 );

        DoublyLinkedRBT<String, String> BBST4 = new DoublyLinkedRBT<>();
        BBST4.put( "C", "C" );
        BBST4.put( "B", "B" );
        BBST4.put( "D", "D" );
        BBST4.put( "A", "A" );

        System.out.println( BBST4.deleteAndGetVal( "B" ) ); // 2
        System.out.println( BBST2 );
    }

    static
    void testPreSucc() {
        DoublyLinkedRBT<String, Integer> BBST = new DoublyLinkedRBT<>();
        int num = 1;
        BBST.put( "S", num++ ); // 1
        BBST.put( "E", num++ );
        BBST.put( "A", num++ ); // 3
        BBST.put( "R", num++ );
        BBST.put( "C", num++ ); // 5
        BBST.put( "H", num++ );
        BBST.put( "X", num++ ); // 7
        BBST.put( "M", num++ );
        BBST.put( "P", num++ ); // 9
        BBST.put( "L", num++ );

        MapTreeNode<String, Integer> nE = BBST.getNode( "E" );
        System.out.println( BBST.predecessor( nE ) ); // C
        System.out.println( BBST.successor( nE ) ); // H

        System.out.println();
        MapTreeNode<String, Integer> nC = BBST.getNode( "C" );
        System.out.println( BBST.predecessor( nC ) ); // A
        System.out.println( BBST.successor( nC ) ); // E

        System.out.println();
        MapTreeNode<String, Integer> nA = BBST.getNode( "A" );
        System.out.println( BBST.predecessor( nA ) ); // NULL
        System.out.println( BBST.successor( nA ) ); // C

        System.out.println();
        MapTreeNode<String, Integer> nL = BBST.getNode( "L" );
        System.out.println( BBST.predecessor( nL ) ); // H
        System.out.println( BBST.successor( nL ) ); // M

        System.out.println();
        MapTreeNode<String, Integer> nH = BBST.getNode( "H" );
        System.out.println( BBST.predecessor( nH ) ); // E
        System.out.println( BBST.successor( nH ) ); // L

        System.out.println();
        MapTreeNode<String, Integer> nM = BBST.getNode( "M" );
        System.out.println( BBST.predecessor( nM ) ); // L
        System.out.println( BBST.successor( nM ) ); // P

        System.out.println();
        MapTreeNode<String, Integer> nP = BBST.getNode( "P" );
        System.out.println( BBST.predecessor( nP ) ); // M
        System.out.println( BBST.successor( nP ) ); // R

        System.out.println();
        MapTreeNode<String, Integer> nR = BBST.getNode( "R" );
        System.out.println( BBST.predecessor( nR ) ); // P
        System.out.println( BBST.successor( nR ) ); // S

        System.out.println();
        MapTreeNode<String, Integer> nS = BBST.getNode( "S" );
        System.out.println( BBST.predecessor( nS ) ); // R
        System.out.println( BBST.successor( nS ) ); // X

        System.out.println();
        MapTreeNode<String, Integer> nX = BBST.getNode( "X" );
        System.out.println( BBST.predecessor( nX ) ); // S
        System.out.println( BBST.successor( nX ) ); // NULL

        System.out.println();
        MapTreeNode<String, Integer> nZ = BBST.getNode( "Z" );
        System.out.println( BBST.predecessor( nZ ) ); // NULL
        System.out.println( BBST.successor( nZ ) ); // NULL
    }

    static
    void testPreSucc2() {
        DoublyLinkedRBT<String, String> BBST = new DoublyLinkedRBT<>();
        BBST.put( "S", "S", null, true );
        BBST.put( "E", "E", "S", true );
        BBST.put( "A", "A", "E", true );
        BBST.put( "R", "R", "S", true );
        BBST.put( "C", "C", "A", false );
        BBST.put( "H", "H", "R", true );
        BBST.put( "X", "X", "S", false );
        BBST.put( "M", "M", "H", false );
        BBST.put( "P", "P", "M", false );
        BBST.put( "L", "L", "H", false );

        System.out.println( BBST );
        MapTreeNode<String, String> nE = BBST.getNode( "E" );
        System.out.println( BBST.predecessor( nE ) ); // C
        System.out.println( BBST.successor( nE ) ); // H

        System.out.println();
        MapTreeNode<String, String> nC = BBST.getNode( "C" );
        System.out.println( BBST.predecessor( nC ) ); // A
        System.out.println( BBST.successor( nC ) ); // E

        System.out.println();
        MapTreeNode<String, String> nA = BBST.getNode( "A" );
        System.out.println( BBST.predecessor( nA ) ); // NULL
        System.out.println( BBST.successor( nA ) ); // C

        System.out.println();
        MapTreeNode<String, String> nL = BBST.getNode( "L" );
        System.out.println( BBST.predecessor( nL ) ); // H
        System.out.println( BBST.successor( nL ) ); // M

        System.out.println();
        MapTreeNode<String, String> nH = BBST.getNode( "H" );
        System.out.println( BBST.predecessor( nH ) ); // E
        System.out.println( BBST.successor( nH ) ); // L

        System.out.println();
        MapTreeNode<String, String> nM = BBST.getNode( "M" );
        System.out.println( BBST.predecessor( nM ) ); // L
        System.out.println( BBST.successor( nM ) ); // P

        System.out.println();
        MapTreeNode<String, String> nP = BBST.getNode( "P" );
        System.out.println( BBST.predecessor( nP ) ); // M
        System.out.println( BBST.successor( nP ) ); // R

        System.out.println();
        MapTreeNode<String, String> nR = BBST.getNode( "R" );
        System.out.println( BBST.predecessor( nR ) ); // P
        System.out.println( BBST.successor( nR ) ); // S

        System.out.println();
        MapTreeNode<String, String> nS = BBST.getNode( "S" );
        System.out.println( BBST.predecessor( nS ) ); // R
        System.out.println( BBST.successor( nS ) ); // X

        System.out.println();
        MapTreeNode<String, String> nX = BBST.getNode( "X" );
        System.out.println( BBST.predecessor( nX ) ); // S
        System.out.println( BBST.successor( nX ) ); // NULL

        System.out.println();
        MapTreeNode<String, String> nZ = BBST.getNode( "Z" );
        System.out.println( BBST.predecessor( nZ ) ); // NULL
        System.out.println( BBST.successor( nZ ) ); // NULL
    }

    public static
    void main( String[] args ) {
        testDoublyConnected();
        testPreSucc();
        testPreSucc2();
    }

}
