package com.fengkeyleaf.lang;

/*
 * TestSort.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 10/16/2022$
 */

import java.util.Arrays;

/**
 * Test Sort
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

final class TestSort {

    /**
     * test the sorting algorithms
     */

    public static
    void main( String[] args ) {
//        System.out.println("insertionSort-------->");
//        int[] arr = { 3, 7, 5, 1, 2 };
//        int[] arr = { 3, 7, 5, 1 };
//        int[] arr = { 1, 2, 3 };
//        int[] arr = { 3, 2, 1 };
//        int[] arr = { 3, 2 };
//        int[] arr = { 1 };
/*        int[] arr = {};
        List<Integer> array = new ArrayList<>();
        for ( int num : arr )
            array.add( num );

        System.out.println( array );
        insertionSort( array );
        System.out.println( array );

        System.out.println("\nmergeSort-------->");
        List<Integer> array1 = new ArrayList<>();
        for ( int num : arr )
            array1.add( num );

        System.out.println( array1 );
        System.out.println( mergeSort( array1 ) );*/

/*        System.out.println("\nbucketSort-------->");
        double[] arrDouble = { 0.61, 0.98, 0.03 };
//        double[] arrDouble = { 0.61, 0.98, 0.03, 0.22, 0.84, 0.53, 0.33, 0.57 };
//        double[] arrDouble = { 0.98, 0.61 };
//        double[] arrDouble = { 0.61 };
//        double[] arrDouble = {};
        List<Double> array2 = new ArrayList<>();
        for ( double num : arrDouble )
            array2.add( num );

//        List<Double> array3 = array2.subList( 0, array2.size() );
        List<Double> array3 = new ArrayList<>( array2 );
        System.out.println( array3 );
        System.out.println( array2 );
        bucketSort( array3 );
        System.out.println( array3 );
        System.out.println( array2 );*/

        System.out.println( "radixSort-------->" );
        System.out.println( "10 base-------->" );
//        int[] arr = { 127, 307, 124, 223, 772, 532 };
        long[] arr = { 0, 5, 6, 8, 8, 90, 100, 10000, 2323432, 5345, 127, 307, 124, 223, 772, 532 };

        System.out.println( Arrays.toString( arr ) );

        System.out.println( "After sorting: " + MySorts.radixSort( arr, 10 ) );

//        System.out.println("16 base-------->");//
        System.out.println( Arrays.toString( arr ) );
        MySorts.radixSort( arr, 16 );
        System.out.println( "After sorting: " + MySorts.radixSort( arr, 16 ) );
    }
}
