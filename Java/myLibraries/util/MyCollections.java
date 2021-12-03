package myLibraries.util;

/*
 * MyCollections.java
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 added upperBound() and lowerBound() on 2/24/2021$
 *     $1.1 added ceilingBound() on 8/7/2021$
 *
 * JDK: 16
 */

import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to the Collections Class
 *
 * @author       Xiaoyu Tongyang
 */

public class MyCollections {

    /**
     * find the first element that is equal or greater than key
     * in this list of the range [index, end)
     *
     *
     * @param start start index, inclusive
     * @param end end index, exclusive
     */

    public static<T>
    int ceilingBound( List<? extends Comparable<? super T>> list,
                      int start, int end, T key ) {
        return ceilingBoundFind( list, start, end - 1, key );
    }

    public static<T>
    int ceilingBound( List<? extends Comparable<? super T>> list, T key ) {
        return ceilingBoundFind( list, 0, list.size() - 1, key );
    }

    private static<T>
    int ceilingBoundFind( List<? extends Comparable<? super T>> list,
                      int left, int right, T key ) {
        while ( left <= right ) {
            int mid = ( right - left ) / 2 + left;

            int res = list.get( mid ).compareTo( key );
            // found duplicate
            if ( res == 0 )
                return mid;
            // look for higher
            else if ( res < 0 )
                left = mid + 1;
            // look for lower
            else
                right = mid - 1;
        }

        // first element greater than key is at left
        return left;
    }

    /**
     * find the first element in this list
     * that is strictly greater than the key
     */

    public static<T>
    int upperBound( List<? extends Comparable<? super T>> list, T key ) {
        int left = 0, right = list.size() - 1;

        while ( left <= right ) {
            int mid = ( right - left ) / 2 + left;

            // list[ mid ] <= key
            // consider elements less or equal to key as the whole
            if ( list.get( mid ).compareTo( key ) <= 0 )
                left = mid + 1;
            else
                right = mid - 1;
        }

        // first element greater than key is at left
        return left;
    }

    /**
     * find the first element in this list that is less than key
     */

    public static<T>
    int lowerBound( List<? extends Comparable<? super T>> list, T key ) {
        int left = 0, right = list.size() - 1;

        while ( left <= right ) {
            int mid = ( right - left ) / 2 + left;

            // list[ mid ] >= key
            // consider elements greater or equal to key as the whole
            if ( list.get( mid ).compareTo( key ) >= 0 )
                right = mid - 1;
            else
                left = mid + 1;
        }

        // first element less than key is at right
        return right;
    }

    /**
     * check if the passed-in index is out of boundary or not
     */

    public static <T>
    boolean ifOutOfBoundary( List<T> list, int index ) {
        return index < 0 || index >= list.size();

    }

    public static
    void main(String[] args) {

    }
}
