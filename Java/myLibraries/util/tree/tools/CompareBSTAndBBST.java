package myLibraries.util.tree.tools;

/*
 * CompareBSTAndBBST.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.tree.BinarySearchTree;
import myLibraries.util.tree.RedBlackTree;

import java.util.Random;

/**
 * compare BST and R-B tree
 * Testing environment：surface book
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class CompareBSTAndBBST {
    public final static BinarySearchTree<Integer, Integer> BST = new BinarySearchTree<>();
    public final static RedBlackTree<Integer, Integer> RBTree = new RedBlackTree<>();
    public static int dataScale;
    public final static Random random = new Random( System.currentTimeMillis() );

    static {
        /*
         * Worst case:
         * BST running time exceeded 36 mins
         * put(): / <-> 6176
         * deleteMax(): / <-> 0
         * deleteMin(): / <-> 0
         * delete(): / <-> 195
         * get(): / <-> 143
         *
         * Randomly test:
         * put(): 26535 <-> 23309
         * deleteMax(): 1647 <-> 0
         * deleteMin(): 1419 <-> 0
         * delete(): 34419 <-> 143
         * get(): 20438 <-> 144
         * */
        dataScale = 10000000;

        /*
         * Worst case:
         * put(): 111962 <-> 90
         * deleteMax(): 9 <-> 0
         * deleteMin(): 6 <-> 0
         * delete(): 53219 <-> 4
         *
         * Randomly test:
         * put(): 83 <-> 73
         * deleteMax(): 19 <-> 0
         * deleteMin(): 13 <-> 0
         * delete(): 80 <-> 1
         * */
//        dataScale = 100000;

        /*
        * Worst case:
        * put(): 927 <-> 28
        * deleteMax(): 4 <-> 0
        * deleteMin(): 2 <-> 0
        * delete(): 317 <-> 1
        *
        * put(): 1355 <-> 32
        * deleteMax(): 4 <-> 0
        * deleteMin(): 2 <-> 0
        * delete(): 322 <-> 3
        *
        * put(): 952 <-> 45
        * deleteMax(): 3 <-> 1
        * deleteMin(): 4 <-> 0
        * delete(): 356 <-> 0
        *
        *
        * Randomly test:
        * put(): 16 <-> 5
        * deleteMax(): 6 <-> 0
        * deleteMin(): 3 <-> 0
        * delete(): 4 <-> 1
        *
        * put(): 12 <-> 8
        * deleteMax(): 4 <-> 0
        * deleteMin(): 3 <-> 0
        * delete(): 7 <-> 1
        *
        * put(): 13 <-> 7
        * deleteMax(): 7 <-> 0
        * deleteMin(): 4 <-> 0
        * delete(): 7 <-> 1
        * */
//        dataScale = 10000;

        /*
        *
        * Worst case:
        * put(): 23 <-> 8
        * deleteMax(): 1 <-> 0
        * deleteMin(): 2 <-> 0
        * delete(): 47 <-> 2
        *
        * put(): 23 <-> 7
        * deleteMax(): 0 <-> 0
        * deleteMin(): 0 <-> 0
        * delete(): 36 <-> 4
        *
        * put(): 27 <-> 13
        * deleteMax(): 1 <-> 0
        * deleteMin(): 0 <-> 0
        * delete(): 26 <-> 2
        *
        *
        * Randomly test:
        * put(): 5 <-> 8
        * deleteMax(): 2 <-> 0
        * deleteMin(): 0 <-> 0
        * delete(): 3 <-> 1
        *
        * put(): 6 <-> 5
        * deleteMax(): 3 <-> 0
        * deleteMin(): 0 <-> 0
        * delete(): 6 <-> 0
        *
        * put(): 2 <-> 6
        * deleteMax(): 2 <-> 0
        * deleteMin(): 0 <-> 0
        * delete(): 3 <-> 2
        * */
//        dataScale = 1000;
    }

    /**
     * enumerate type to represent BST or R-B tree
     * */

    public enum Tree {
        BST, RBTREE
    }

    public static void putBST( int i ) {
        BST.put( i, i );
    }

    public static void deleteMaxBST() {
        while ( !BST.isEmpty() )
            BST.deleteMax();
    }

    public static void deleteMinBST() {
        while ( !BST.isEmpty() )
            BST.deleteMin();
    }

    public static void putBBST( int i ) {
        RBTree.put( i, i );
    }

    public static void deleteMaxBBST() {
        while ( !RBTree.isEmpty() )
            RBTree.deleteMax();
    }

    public static void deleteMinBBST() {
        while ( !RBTree.isEmpty() )
            RBTree.deleteMin();
    }

    public static long measurePuttingRunningTime( Tree tree ) {
        long startTime = System.currentTimeMillis();

        for ( int i = 0; i < dataScale; i++ ) {
            switch ( tree ) {
                case BST -> BST.put( i, i );
                case RBTREE -> RBTree.put( i, i );
            }
        }

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    public static long measureDeleteMaxRunningTime( Tree tree ) {
        long startTime = System.currentTimeMillis();

        switch ( tree ) {
            case BST -> deleteMaxBST();
            case RBTREE -> deleteMaxBBST();
        }

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    public static long measureDeleteMinRunningTime( Tree tree ) {
        long startTime = System.currentTimeMillis();

        switch ( tree ) {
            case BST -> deleteMinBST();
            case RBTREE -> deleteMinBBST();
        }

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    public static long MeasureDeleteRandomly( Tree tree ) {
        boolean[] ifDeleted = new boolean[ dataScale ];
        int count = dataScale;
        long totalTimeCost = 0;

        while ( count > 0 ) {
            int num = random.nextInt( dataScale );
            while ( ifDeleted[ num ] ) {
                num = random.nextInt( dataScale );
            }
            ifDeleted[ num ] = true;

            long startTime = System.currentTimeMillis();
            switch ( tree ) {
                case BST -> BST.delete( num );
                case RBTREE -> RBTree.delete( num );
            }
            totalTimeCost += System.currentTimeMillis() - startTime;
            count--;
        }

        if ( tree == Tree.BST ) assert BST.isEmpty();
        else assert RBTree.isEmpty();

        return totalTimeCost;
    }

    public static long measurePuttingRunningTimeRandomly( Tree tree ) {
        boolean[] ifDeleted = new boolean[ dataScale ];
        int count = dataScale;
        long totalTimeCost = 0;

        while ( count > 0 ) {
            int num = random.nextInt( dataScale );
            while ( ifDeleted[ num ] ) {
                num = random.nextInt( dataScale );
            }
            ifDeleted[ num ] = true;

            long startTime = System.currentTimeMillis();
            switch ( tree ) {
                case BST -> BST.put( num, num );
                case RBTREE -> RBTree.put( num, num );
            }
            totalTimeCost += System.currentTimeMillis() - startTime;
            count--;
        }

        return totalTimeCost;
    }

    public static long MeasureGetRandomly( Tree tree ) {
        boolean[] ifDeleted = new boolean[ dataScale ];
        int count = dataScale;
        long totalTimeCost = 0;

        while ( count > 0 ) {
            int num = random.nextInt( dataScale );
            while ( ifDeleted[ num ] ) {
                num = random.nextInt( dataScale );
            }
            ifDeleted[ num ] = true;

            long startTime = System.currentTimeMillis();
            switch ( tree ) {
                case BST -> BST.get( num );
                case RBTREE -> RBTree.get( num );
            }
            totalTimeCost += System.currentTimeMillis() - startTime;
            count--;
        }

        return totalTimeCost;
    }

    public static void testWorstCase() {
        // Worst case,
        // in which the visualized data structure of BST
        // is like an array
//        long BSTime = measurePuttingRunningTime( Tree.BST );
        long BSTime = 0;
        long BBSTime = measurePuttingRunningTime( Tree.RBTREE );

        System.out.println( "Worst case:\t------>" );
        System.out.println( "put(): " + BSTime + " <-> " + BBSTime );

//        BSTime = measureDeleteMaxRunningTime( Tree.BST );
//        BBSTime = measureDeleteMaxRunningTime( Tree.RBTREE );

//        System.out.println( "deleteMax(): " + BSTime + " <-> " + BBSTime );

//        measurePuttingRunningTime( Tree.BST );
//        measurePuttingRunningTime( Tree.RBTREE );
//        RBTree.checkValidTreeStructure();

//        BSTime = measureDeleteMinRunningTime( Tree.BST );
//        BBSTime = measureDeleteMinRunningTime( Tree.RBTREE );

//        System.out.println( "deleteMin(): " + BSTime + " <-> " + BBSTime );

//        measurePuttingRunningTime( Tree.BST );
//        measurePuttingRunningTime( Tree.RBTREE );
//        RBTree.checkValidTreeStructure();

//        BSTime = MeasureDeleteRandomly( Tree.BST );
//        BBSTime = MeasureDeleteRandomly( Tree.RBTREE );

//        System.out.println( "delete(): " + BSTime + " <-> " + BBSTime );

        //        measurePuttingRunningTime( Tree.BST );
//        measurePuttingRunningTime( Tree.RBTREE );
//        RBTree.checkValidTreeStructure();

        BSTime = MeasureGetRandomly( Tree.BST );
        BBSTime = MeasureGetRandomly( Tree.RBTREE );

        System.out.println( "get(): " + BSTime + " <-> " + BBSTime );

    }

    public static void testRandomly() {
        // Randomly test
        System.out.println( "Randomly test:\t------>" );

        long BSTime = 0;
//      BSTime = measurePuttingRunningTimeRandomly( Tree.BST );

        long BBSTime = measurePuttingRunningTimeRandomly( Tree.RBTREE );

        System.out.println( "put(): " + BSTime + " <-> " + BBSTime );

//        BSTime = measureDeleteMaxRunningTime( Tree.BST );
//        BBSTime = measureDeleteMaxRunningTime( Tree.RBTREE );
//
//        System.out.println( "deleteMax(): " + BSTime + " <-> " + BBSTime );
//
//        measurePuttingRunningTimeRandomly( Tree.BST );
//        measurePuttingRunningTimeRandomly( Tree.RBTREE );
//        RBTree.checkValidTreeStructure();
//
//        BSTime = measureDeleteMinRunningTime( Tree.BST );
//        BBSTime = measureDeleteMinRunningTime( Tree.RBTREE );
//
//        System.out.println( "deleteMin(): " + BSTime + " <-> " + BBSTime );
//
//        measurePuttingRunningTimeRandomly( Tree.BST );
//        measurePuttingRunningTimeRandomly( Tree.RBTREE );
//        RBTree.checkValidTreeStructure();
//
//        BSTime = MeasureDeleteRandomly( Tree.BST );
        BBSTime = MeasureDeleteRandomly( Tree.RBTREE );

        System.out.println( "delete(): " + BSTime + " <-> " + BBSTime );
//
//        measurePuttingRunningTimeRandomly( Tree.BST );
//        measurePuttingRunningTimeRandomly( Tree.RBTREE );
//        RBTree.checkValidTreeStructure();

//        BSTime = MeasureGetRandomly( Tree.BST );
//        BBSTime = MeasureGetRandomly( Tree.RBTREE );

//        System.out.println( "get(): " + BSTime + " <-> " + BBSTime );
    }

    public static
    void main( String[] args ) {
        // allow more stack space with 1024mb:
        // -Xss1024m
//        testWorstCase();
//        System.out.println();
        testRandomly();
    }
}
