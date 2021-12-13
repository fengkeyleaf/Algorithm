"use strict"


/*
 * TestBST.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import BinarySearchTree from "../BinarySearchTree.js";

/**
 * Test BST
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class TestBST {
    static main() {
        let ID = 0;
        // let node1 = new MapTreeNode( { ID: ID++, key: "S", val: 0 } );
        // let node2 = new MapTreeNode( { ID: ID++, key: "E", val: 1 } );
        // let node3 = new MapTreeNode( { ID: ID++, key: "A", val: 2 } );
        // let node4 = new MapTreeNode( { ID: ID++, key: "R", val: 3 } );
        // let node5 = new MapTreeNode( { ID: ID++, key: "C", val: 4 } );
        // let node6 = new MapTreeNode( { ID: ID++, key: "H", val: 5 } );
        // let node7 = new MapTreeNode( { ID: ID++, key: "E", val: 6 } );
        // let node8 = new MapTreeNode( { ID: ID++, key: "X", val: 7 } );

        let BST = new BinarySearchTree( String.compareTo );

        let num = 0;
        BST.put( "S", num++ );
        BST.put( "E", num++ );
        BST.put( "A", num++ );
        BST.put( "R", num++ );
        BST.put( "C", num++ );
        BST.put( "H", num++ );
        BST.put( "E", num++ );
        BST.put( "X", num++ );
        BST.put( "A", num++ );
        BST.put( "M", num++ );
        BST.put( "P", num++ );
        BST.put( "L", num++ );
        BST.put( "E", num++ );
        BST.inorderPrintStart();

        console.log( BST.get( "S" ) ); // 0
        console.log( BST.get( "C" ) ); // 4
        console.log( BST.get( "L" ) ); // 11
        console.log( BST.get( "O" ) ); // null
        console.log( BST.get( "MM" ) ); // null

        console.log( "\n" );
        // BST.deleteMin();
        // BST.deleteMax();
        BST.inorderPrintStart();

        // BST.delete( "E" );
        // BST.delete( "S" );
        // BST.inorderPrintStart();

        console.log( BST.floor( "E" ).toString() ); // E
        console.log( BST.lower( "E" ).toString() ); // C
        console.log( BST.ceiling( "E" ).toString() ); // E
        console.log( BST.higher( "E" ).toString() ); // H

        console.log( "\n" );
        console.log( BST.floor( "A" ).toString() ); // A
        console.log( BST.lower( "A" ) ); // NULL
        console.log( BST.ceiling( "A" ).toString() ); // A
        console.log( BST.higher( "A" ).toString() ); // C

        console.log( "\n" );
        console.log( BST.floor( "X" ).toString() ); // X
        console.log( BST.lower( "X" ).toString() ); // S
        console.log( BST.ceiling( "X" ).toString() ); // X
        console.log( BST.higher( "X" ) ); // NULL

        console.log( "\n" );
        console.log( BST.floor( "@" ) ); // NULL
        console.log( BST.lower( "@" ) ); // NULL
        console.log( BST.ceiling( "@" ).toString() ); // A
        console.log( BST.higher( "@" ).toString() ); // A

        console.log( "\n" );
        console.log( BST.floor( "Z" ).toString() ); // X
        console.log( BST.lower( "Z" ).toString() ); // X
        console.log( BST.ceiling( "Z" ) ); // NULL
        console.log( BST.higher( "Z" ) ); // NULL
    }
}

TestBST.main()
