package com.fengkeyleaf.util.tree;

/*
 * TestLinkedBST.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/27/2022$
 */

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class TestLinkedBST {
    static
    void testRegular() {
        DoublyLinkedBST<String, Integer> linkedBST = new DoublyLinkedBST<>();
        linkedBST.put( "S", 0 );
        linkedBST.put( "E", 1 );
        linkedBST.put( "A", 2 );
        linkedBST.put( "R", 3 );
        linkedBST.put( "C", 4 );
        linkedBST.put( "H", 5 );
        linkedBST.put( "E", 6 );
        linkedBST.put( "X", 7 );
        linkedBST.put( "A", 8 );
        linkedBST.put( "M", 9 );
        linkedBST.put( "P", 10 );
        linkedBST.put( "L", 11 );
        linkedBST.put( "E", 12 );
        System.out.println( linkedBST );

//        linkedBST.deleteMin();
//        System.out.println( linkedBST );
//        linkedBST.deleteMax();
//        System.out.println( linkedBST );
//        linkedBST.deleteMin();
//        System.out.println( linkedBST );
//        linkedBST.deleteMax();
//        System.out.println( linkedBST );

//        linkedBST.delete( "A" );
//        linkedBST.delete( "H" );
//        linkedBST.delete( "P" );
//        linkedBST.delete( "R" );
//        linkedBST.delete( "S" );
//        linkedBST.delete( "X" );
//        linkedBST.delete( "M" );
//        linkedBST.delete( "Z" );
//        linkedBST.delete( "E" );
//        linkedBST.delete( "L" );
//        linkedBST.delete( "C" );
//        linkedBST.delete( "C" );
//        System.out.println( linkedBST );

        System.out.println( linkedBST.predecessor( linkedBST.getNode( "A" ) ) ); // null
        System.out.println( linkedBST.successor( linkedBST.getNode( "A" ) ) ); // C

        System.out.println();
        System.out.println( linkedBST.predecessor( linkedBST.getNode( "C" ) ) ); // A
        System.out.println( linkedBST.successor( linkedBST.getNode( "C" ) ) ); // E

        System.out.println();
        System.out.println( linkedBST.predecessor( linkedBST.getNode( "E" ) ) ); // C
        System.out.println( linkedBST.successor( linkedBST.getNode( "E" ) ) ); // H

        System.out.println();
        System.out.println( linkedBST.predecessor( linkedBST.getNode( "H" ) ) ); // E
        System.out.println( linkedBST.successor( linkedBST.getNode( "H" ) ) ); // L

        System.out.println();
        System.out.println( linkedBST.predecessor( linkedBST.getNode( "R" ) ) ); // P
        System.out.println( linkedBST.successor( linkedBST.getNode( "R" ) ) ); // S

        System.out.println();
        System.out.println( linkedBST.predecessor( linkedBST.getNode( "S" ) ) ); // R
        System.out.println( linkedBST.successor( linkedBST.getNode( "S" ) ) ); // X

        System.out.println();
        System.out.println( linkedBST.predecessor( linkedBST.getNode( "X" ) ) ); // S
        System.out.println( linkedBST.successor( linkedBST.getNode( "X" ) ) ); // NULL

        System.out.println();
        System.out.println( linkedBST.predecessor( linkedBST.getNode( "z" ) ) ); // NULL
        System.out.println( linkedBST.successor( linkedBST.getNode( "z" ) ) ); // NULL
    }

    public static
    void main( String[] args ) {
        testRegular();
    }
}
