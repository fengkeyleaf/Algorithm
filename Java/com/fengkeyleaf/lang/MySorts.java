package com.fengkeyleaf.lang;

/*
 * MySorts.java
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 Added insertion sort, merge sort, bucket sort on 2/6/2021$
 *     $1.1 Added radix and counting sort on 2/15/2021$
 *
 * JDK: 15
 */

import com.fengkeyleaf.util.CompareElement;

import java.util.*;

/**
 * Provide four sorting algorithms:
 * radix sort, insertion sort, merge sort, bucket sort
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class MySorts {

    /**
     * counting sort associated with radix sort
     */

    public static
    void countingSort( List<NumberRadix> numbers, int digit,
                                     List<LinkedList<NumberRadix>> container ) {
        // sort by numbers at digit-th digit
        for ( NumberRadix num : numbers )
            container.get( num.getDigit( digit ) ).addLast( num );

        // combine results with stable sorting property
        int index = 0;
        for ( int i = 0; i < container.size(); i++ ) {
            if ( container.get( i ).isEmpty() ) continue;

            while ( !container.get( i ).isEmpty() ) {
                numbers.set( index++, container.get( i ).removeFirst() );
            }
        }
    }

    /**
     * radix sort with ascending order
     */

    public static List<NumberRadix>
    radixSort( long[] arr, int radix ) {
        List<NumberRadix> numbers = new ArrayList<>();
        if ( arr.length == 0 ) return numbers;

        int digits = 0;
        // convert numbers into ones in required base
        for ( long num : arr ) {
            NumberRadix numberRadix = MyMath.convertNumberToOtherBase( num, radix );
            numbers.add( numberRadix );
            digits = Math.max( digits, numberRadix.numberOfDigits() );
        }

        List<LinkedList<NumberRadix>> container = new ArrayList<>();
        for ( int i = 0; i < numbers.get( 0 ).radix; i++ )
            container.add( new LinkedList<>() );

        // counting sort with each digit
        for ( int i = 0; i < digits; i++ )
            countingSort( numbers, i, container );

        return numbers;
    }

    /**
     * insertion sort with ascending order
     */

    public static <E extends Comparable<E>>
    void insertionSort( List<E> arrayToSort ) {
        for ( int i = 1; i < arrayToSort.size(); i++ ) {
            int index = i;

            for ( int j = i - 1; j >= 0; j-- ) {
                if ( arrayToSort.get( j ).compareTo( arrayToSort.get( index ) ) > 0 ) {
                    // swap two elements in an arrayList is O(1) time complexity
                    Collections.swap( arrayToSort, j, index-- );
                }
                // all elements before the one at index are less than or equal to it,
                // no need to compare anymore
                else
                    break;
            }
        }
    }

    /**
     * merge sort with ascending order
     *
     * @param A array to be sorted.
     */

    public static<E>
    List<E> mergeSort( List<E> A, Comparator<E> c ) {
        // base case
        if ( A.size() <= 1 ) return A;

        // dividing step
        int mid = ( A.size() - 1 ) / 2;
        // getting a sublist of an arraylist is O(n) time complexity
        List<E> L = mergeSort( A.subList( 0, mid + 1 ), c );
        List<E> R = mergeSort( A.subList( mid + 1, A.size() ), c );

        return merge( L, R, c );
    }

    public static<E>
    List<E> mergeSort( List<E> A ) {
        return mergeSort( A, null );
    }

    /**
     * merge-sort like process.
     * */

    public static<E>
    List<E> merge( List<E> L1, List<E> L2, Comparator<E> c ) {
        List<E> M = new ArrayList<>( L1.size() + L2.size() + 1 );
        int idx1 = 0, idx2 = 0;

        // merging step
        while ( idx1 < L1.size() && idx2 < L2.size() ) {
            // element in the right list are less
            if ( CompareElement.compare( c, L1.get( idx1 ), L2.get( idx2 ) ) >= 0 )
                M.add( L2.get( idx2++ ) );
            // element in the left list are less
            else M.add( L1.get( idx1++ ) );
        }

        // concatenate remained elements in either left list or right list
        if ( idx1 >= L1.size() )
            M.addAll( L2.subList( idx2, L2.size() ) );
        else
            M.addAll( L1.subList( idx1, L1.size() ) );

        return M;
    }

    /**
     * bucket sort with ascending order
     */

    public static
    void bucketSort( List<Double> arrayToSort ) {
        int n = arrayToSort.size();
        double upperbound = 1.0, interval = upperbound / n;
        List<List<Double>> buckets = new ArrayList<>();

        // O(n) time complexity for allocating memory
        for ( int i = 0; i < n; i++ )
            buckets.add( new ArrayList<>() );

        for ( Double element : arrayToSort ) {
            // get an index for a give floating number
            // e.g. interval = .125, number = .61
            // the index = .61 / .125 = [4.88] = 4
            int index = ( int ) ( element / interval );
            // getting an element at a certain index is O(1) time complexity
            buckets.get( index ).add( element );
        }

        // removing all elements in an arraylist is O(n) time complexity
        arrayToSort.clear();
        for ( List<Double> bucket : buckets ) {
            insertionSort( bucket );
            // add elements in this interval into the original one
            // right after sorting it
            arrayToSort.addAll( bucket );
        }
    }

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

        System.out.println( "After sorting: " + radixSort( arr, 10 ) );

//        System.out.println("16 base-------->");//
        System.out.println( Arrays.toString( arr ) );
        radixSort( arr, 16 );
        System.out.println( "After sorting: " + radixSort( arr, 16 ) );
    }
}
