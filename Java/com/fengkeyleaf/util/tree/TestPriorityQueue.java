package com.fengkeyleaf.util.tree;

/*
 * TestPriorityQueue.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/31/2022$
 */

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class TestPriorityQueue {

    /**
     * delete all elements in a heap, for testing purpose
     * */

    static<T extends Comparable<T>>
    void deleteNodes( MyPriorityQueue<T> aHeap ) {
        System.out.print("Deleting order: ");
        while ( !aHeap.isEmpty() ) {
            System.out.printf( "%s -> ", aHeap.delete() );
//            System.out.println( aHeap );
        }
        System.out.println();
    }

    /**
     * test MyPriorityQueue Class
     * */

    public static
    void main(String[] args) {
        int[] arr = { 1, 2 ,5, 10 };

        System.out.println( "Extract Max------------>" );
        MyPriorityQueue<Integer> aHeapMax = new MyPriorityQueue<>();
        for ( int num : arr )
            aHeapMax.insert( num );

        System.out.println( aHeapMax );
        deleteNodes( aHeapMax );
        aHeapMax.delete();
        System.out.println( aHeapMax );

        int[] arr1 = { 3, 7, 11 };
//        int[] arr1 = { 3, 7, 11, 15, 17, 20, 9, 15, 8, 16 };
//        int[] arr1 = { 3, 7, 11, 15, 17, 20, 9, 15, 8, 16, 3, 3 };
//        int[] arr1 = { 1, 2, 3, 4 };
        for ( int num : arr )
            aHeapMax.insert( num );
        for ( int num : arr1 )
            aHeapMax.insert( num );

        System.out.println( aHeapMax );
        deleteNodes( aHeapMax );
        aHeapMax.delete();
        System.out.println( aHeapMax );

        System.out.println("\nExtract Min------------>");
        MyPriorityQueue<Integer> aHeapMin = new MyPriorityQueue<>( ( num1, num2 ) -> -Integer.compare( num1, num2 ) );
        for ( int num : arr )
            aHeapMin.insert( num );
        for ( int num : arr1 )
            aHeapMin.insert( num );

        System.out.println( aHeapMin );
        deleteNodes( aHeapMin );
        aHeapMax.delete();
        System.out.println( aHeapMin );

        int[] arr2 = { 0, -2, -100, 200, };
        for ( int num : arr )
            aHeapMin.insert( num );
        for ( int num : arr1 )
            aHeapMin.insert( num );
        for ( int num : arr2 )
            aHeapMin.insert( num );

        System.out.println( aHeapMin );
        deleteNodes( aHeapMin );
        aHeapMax.delete();
        System.out.println( aHeapMin );
    }
}
