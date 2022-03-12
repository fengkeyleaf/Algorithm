package myLibraries.util.tree.tools;

/*
 * TestBBST.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.tree.RedBlackTree;
import myLibraries.util.tree.elements.MapTreeNode;

/**
 * Test BBST
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class TestBBST {
    private static
    void testNormal() {
        RedBlackTree<String, Integer> BBST = new RedBlackTree<>();
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
        System.out.println( BBST );

        System.out.println( BBST.get( "M" ) ); // 8
        System.out.println( BBST.get( "A" ) ); // 3
        System.out.println( BBST.get( "X" ) ); // 7
        System.out.println( BBST.get( "O" ) ); // null
        System.out.println( BBST.get( "MM" ) ); // null

        BBST.deleteMin(); // A -> 3
        BBST.deleteMax(); // X -> 7
        System.out.println( BBST );

        System.out.println();
        System.out.println( BBST.deleteAndGetVal( "M" ) ); // 8
        System.out.println( BBST.deleteAndGetVal( "H" ) ); // 6
        System.out.println( BBST.deleteAndGetVal( "R" ) ); // 4
        System.out.println( BBST );

        num = 1;
        RedBlackTree<Integer, Integer> BBST2 = new RedBlackTree<>();
        BBST2.put( 2, 2 ); // 1
        BBST2.put( 1, 1 ); // 2
        BBST2.put( 3, 3 ); // 3

        System.out.println( BBST2.deleteAndGetVal( 0 ) ); // null
        System.out.println( BBST2.deleteAndGetVal( 4 ) ); // null
        BBST2.inorderPrint();
        BBST2.deleteMin();
        BBST2.deleteMin();
        BBST2.deleteMin();
        BBST2.inorderPrint();

        RedBlackTree<String, Integer> BBST3 = new RedBlackTree<>();
        BBST3.put( "H", num++ );
        BBST3.put( "C", num++ ); // 2
        BBST3.put( "R", num++ );
        BBST3.put( "A", num++ ); // 4

        System.out.println( BBST3.deleteAndGetVal( "C" ) ); // 2
        BBST3.inorderPrint();

        num = 1;
        RedBlackTree<String, Integer> BBST4 = new RedBlackTree<>();
        BBST4.put( "C", num++ );
        BBST4.put( "B", num++ ); // 2
        BBST4.put( "D", num++ );
        BBST4.put( "A", num++ ); // 4

        System.out.println( BBST4.deleteAndGetVal( "B" ) ); // 2
        BBST4.inorderPrint();
    }

    public static
    void main( String[] args ) {
        testNormal();
//        testDoublyConnected();
//        testPreSucc();
    }
}
