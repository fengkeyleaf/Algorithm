"use strict"

/*
 * TestBBST.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import RedBlackTree from "../RedBlackTree.js";
import MyMath from "../../../lang/MyMath.js";

/**
 * Test BBST
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class TestBBST {
    static main() {
        let BBST = new RedBlackTree( String.compareTo );
        let num = 1;
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

        console.log( BBST.get( "M" ) ); // 8
        console.log( BBST.get( "A" ) ); // 3
        console.log( BBST.get( "X" ) ); // 7
        console.log( BBST.get( "O" ) ); // null
        console.log( BBST.get( "MM" ) ); // null

        BBST.deleteMin(); // A -> 3
        BBST.deleteMax(); // X -> 7
        BBST.inorderPrintStart();

        console.log();
        console.log( BBST.deleteAndGetVal( "M" ) ); // 8
        console.log( BBST.deleteAndGetVal( "H" ) ); // 6
        console.log( BBST.deleteAndGetVal( "R" ) ); // 4
        BBST.inorderPrintStart();

        num = 1;
        let BBST2 = new RedBlackTree( MyMath.doubleCompare );
        BBST2.put( 2, 2 ); // 1
        BBST2.put( 1, 1 ); // 2
        BBST2.put( 3, 3 ); // 3

        console.log();
        console.log( BBST2.deleteAndGetVal( 0 ) ); // null
        console.log( BBST2.deleteAndGetVal( 4 ) ); // null
        BBST2.inorderPrintStart();
        BBST2.deleteMin();
        BBST2.deleteMin();
        BBST2.deleteMin();
        BBST2.inorderPrintStart();

        console.log();
        let BBST3 = new RedBlackTree( String.compareTo );
        BBST3.put( "H", num++ );
        BBST3.put( "C", num++ ); // 2
        BBST3.put( "R", num++ );
        BBST3.put( "A", num++ ); // 4

        console.log( BBST3.deleteAndGetVal( "C" ) ); // 2
        BBST3.inorderPrintStart();

        console.log();
        num = 1;
        let BBST4 = new RedBlackTree( String.compareTo );
        BBST4.put( "C", num++ );
        BBST4.put( "B", num++ ); // 2
        BBST4.put( "D", num++ );
        BBST4.put( "A", num++ ); // 4

        console.log( BBST4.deleteAndGetVal( "B" ) ); // 2
        BBST4.inorderPrintStart();
    }
}

TestBBST.main();