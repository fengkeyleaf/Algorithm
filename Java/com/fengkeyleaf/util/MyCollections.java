package com.fengkeyleaf.util;

/*
 * MyCollections.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.2$
 *
 * Revisions:
 *     $1.0 added upperBound() and lowerBound() on 2/24/2021$
 *     $1.1 added ceilingBound() on 8/7/2021$
 *     $1.2 add randomPermutation() on 4/3/2022$
 */

import java.util.*;

/**
 * This class consists exclusively of static methods
 * that related to the Collections Class
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class MyCollections {

    public static<E>
    List<E> compare( List<E> l1, List<E> l2 ) {
        return compare( l1, l2, null );
    }

    /**
     * compare two lists, {@code l1} and {@code l2},
     * with the comparator {@code c}
     * */

    public static<E>
    List<E> compare( List<E> l1, List<E> l2, Comparator<E> c ) {
        l1.sort( c );
        l2.sort( c );

        int idx1 = 0;
        int idx2 = 0;
        List<E> diff = new ArrayList<>();

        while ( idx1 < l1.size() && idx2 < l2.size() ) {
            if ( CompareElement.compare( c, l1.get( idx1 ), l2.get( idx2 ) ) != 0 ) {
                diff.add( l1.get( idx1 ) );
                idx1++;
                continue;
            }

            idx1++;
            idx2++;
        }

        if ( idx1 < l1.size() )
            diff.addAll( l1.subList( idx1, l1.size() ) );
        else if ( idx2 < l2.size() )
            diff.addAll( l2.subList( idx2, l2.size() ) );

        return diff;
    }

    /**
     * split the passed-in list, {@code P}, into three part.
     * Ones that are smaller than {@code axis},
     * ones that are equal to {@code axis},
     * and ones that are greater than {@code axis}.
     *
     * @return a list of list containing the three parts.
     * */

    public static<E>
    List<List<E>> split( List<E> P, E axis, Comparator<E> c ) {
        if ( P == null || P.isEmpty() || axis == null )
            return null;

        // L = all elements less than axis
        // E = all elements equal to axis
        // G = all elements greater than axis
        List<E> L = new ArrayList<>();
        List<E> E = new ArrayList<>();
        List<E> G = new ArrayList<>();

        P.forEach( e -> {
            int res = CompareElement.compare( c, axis, e );

            if ( res == 0 ) E.add( e );
            else if ( res > 0 ) L.add( e );
            else G.add( e );
        } );

        List<List<E>> res = new ArrayList<>( 3 );
        res.add( L );
        res.add( E );
        res.add( G );

        return res;
    }

    public static<E>
    List<List<E>> split( List<E> P, E axis ) {
        return split( P, axis, null );
    }

    /**
     * Identify the k-th smallest element in an array for any value k,
     * not just the median. (In the implementation,
     * we will treat k as the index, starting at 0.)
     *
     * Implemented with the idea of quick sort.
     *
     * @param k target index, starting at 0;
     * */

    // SELECT-RAND (P, k)
    public static<E>
    E kSelect( List<E> P, int k, Comparator<E> c ) {
        if ( P == null || P.isEmpty() || isOutOfBoundary( P, k ) )
            return null;

        // 1. x = an element chosen at random from A
        E x = P.get( new Random().nextInt( P.size() ) );

        // 2. Partition P into three buckets: L, E, G.
        // L = all elements less than x
        // E = all elements equal to x
        // G = all elements greater than x
        List<List<E>> splits = split( P, x, c );
        List<E> L = splits.get( 0 );
        List<E> E = splits.get( 1 );
        List<E> G = splits.get( 2 );

        // 3. if (k < L.size) return SELECT-RAND(L, k)
        if ( k < L.size() ) return kSelect( L, k, c );
        // 4. if (k ≥ L.size and k < L.size + E.size) return x
        else if ( k < L.size() + E.size() ) return x;
        // 5. else return SELECT-RAND(G, k-L.size-E.size)
        return kSelect( G, k - L.size() - E.size(), c );
    }

    public static<E>
    E kSelect( List<E> P, int k ) {
        return kSelect( P, k, null );
    }

    /**
     * get a random permutation of an array.
     * Equivalent to {@link Collections}.shuffle(),
     *
     * Reference resource:
     * @see <a href=http://www.cs.uu.nl/geobook/>Computational Geometry: Algorithms and Applications(Third Edition)</a>
     * */

    // Algorithm RANDOMPERMUTATION(A)
    // Input. An array A[1 ... n].
    // Output. The array A[1 ... n] with the same elements,
    // but rearranged into a random permutation.
    // RANDOM(k), which has an integer k as input and
    // generates a random integer between 1 and k in constant time.
    // Computing a random permutation can then be done
    // with the following linear time algorithm.
    public static
    void randomPermutation( List<?> l ) {
        if ( l == null || l.isEmpty() ) return;

        // 1. for k <- n downto 2
        for ( int i = l.size() - 1; i > 0; i-- ) {
            // 2. do rndindex <- RANDOM(k)
            // 3. Exchange A[k] and A[rndindex].
            Collections.swap( l, i, new Random().nextInt( i ) + 1 );
        }
    }

    /**
     * remove duplicate elements, determined by comparator c, in the set P.
     *
     * @param P element set to be removed duplicate elements.
     * @param c comparator to determine which elements are identical.
     * */

    public static<E>
    List<E> removeDuplicates( List<E> P, Comparator<E> c ) {
        if ( P == null || P.isEmpty() ) return null;

        List<E> points = new ArrayList<>( P.size() );
        P.sort( c );

        for ( int i = 0; i < P.size() - 1; i++ ) {
            if ( c.compare( P.get( i ), P.get( i + 1 ) ) != 0 )
                points.add( P.get( i ) );
        }
        points.add( P.get( P.size() - 1 ) );

        return points;
    }

    /**
     * sort given elements with comparator c
     * */

    public static<E>
    List<E> sort( Comparator<E> c, E... elements ) {
        List<E> l = new ArrayList<>( elements.length + 1 );
        l.addAll( Arrays.asList( elements ) );
        l.sort( c );

        return l;
    }

    /**
     * find the first element that is equal or greater than key
     * in this list of the range [index, end)
     *
     * @param l start index, inclusive
     * @param r end index, exclusive
     */

    public static<T>
    int ceilBound( List<T> L, int l, int r, T k ) {
        return ceilBound( L, l, r, k, null );
    }

    public static<T>
    int ceilBound( List<T> list, T key, Comparator<T> c ) {
        return ceilBound( list, 0, list.size(), key, c );
    }

    public static<T>
    int ceilBound( List<T> list, T key ) {
        return ceilBound( list, 0, list.size(), key, null );
    }

    public static<T>
    int ceilBound( List<T> L, int l, int r,
                   T k, Comparator<T> c ) {
        r--;

        while ( l <= r ) {
            int mid = ( r - l ) / 2 + l;

            int res = CompareElement.compare( c, L.get( mid ),  k );
            // found duplicate
            if ( res == 0 )
                return mid;
            // look for higher
            else if ( res < 0 )
                l = mid + 1;
            // look for lower
            else
                r = mid - 1;
        }

        // first element greater than key is at left
        return l >= L.size() ? -1 : l;
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

    public static<T>
    int floorBound( List<T> L, T k, Comparator<T> c ) {
        return floorBound( L, 0, L.size(), k, c );
    }

    public static<T>
    int floorBound( List<T> L, T k ) {
        return floorBound( L, 0, L.size(), k, null );
    }

    /**
     * find the first element that is equal or less than key
     * in this list of the range [index, end)
     *
     * @param l start index, inclusive
     * @param r end index, exclusive
     */

    public static<T>
    int floorBound( List<T> L, int l, int r,
                    T k, Comparator<T> c ) {
        r--;

        while ( l <= r ) {
            int mid = ( r - l ) / 2 + l;

            int res = CompareElement.compare( c, L.get( mid ),  k );
            // found duplicate
            if ( res == 0 )
                return mid;
            // look for lower
            else if ( res > 0 )
                r = mid - 1;
            // look for higher
            else
                l = mid + 1;
        }

        // first element less than key is at right
        return r < 0 ? -1 : r;
    }

    public static<T>
    int lowerBound( List<T> L, T k ) {
        return lowerBound( L, 0, L.size(), k,null );
    }
    /**
     * find the first element in this list that is less than key
     *
     * @param l start index, inclusive
     * @param r end index, exclusive
     */

    public static<T>
    int lowerBound( List<T> L, int l, int r, T k, Comparator<T> c ) {
        r--;

        while ( l <= r ) {
            int mid = ( r - l ) / 2 + l;

            // list[ mid ] >= key
            // consider elements greater or equal to key as the whole
            if ( CompareElement.compare( c, L.get( mid ), ( k ) ) >= 0 )
                r = mid - 1;
            else
                l = mid + 1;
        }

        // first element less than key is at right
        return r < 0 ? -1 : r;
    }

    /**
     * check if the passed-in index is out of boundary or not
     */

    static <T>
    boolean isOutOfBoundary( List<T> list, int index ) {
        return index < 0 || index >= list.size();
    }

    static
    void testKSelect() {
        List<Integer> l = new ArrayList<>();
        l.add( 5 );
        l.add( 0 );
        l.add( 1 );
        l.add( 4 );
        l.add( 9 );
        l.add( 3 );
        l.add( 6 );
        l.add( 7 );
        l.add( 8 );
        l.add( 2 );

        System.out.println( kSelect( l, 0 ) ); // 0
        System.out.println( kSelect( l, 1 ) ); // 0
        System.out.println( kSelect( l, 2 ) ); // 0
        System.out.println( kSelect( l, 3 ) ); // 0
        System.out.println( kSelect( l, 4 ) ); // 0
        System.out.println( kSelect( l, 5 ) ); // 0
        System.out.println( kSelect( l, 7 ) ); // 0
        System.out.println( kSelect( l, 8 ) ); // 0
        System.out.println( kSelect( l, 9 ) ); // 0

        System.out.println( l );
        System.out.println( split( l, kSelect( l, 5 ) ) );
        System.out.println( split( l, kSelect( l, 0 ) ) );
        System.out.println( split( l, kSelect( l, 9 ) ) );
        System.out.println( split( l, kSelect( l, 7 ) ) );
    }

    static
    void testCeilBound() {
        List<Integer> L = new ArrayList<>();
        L.add( 1 );
        L.add( 2 );
        L.add( 3 );
        L.add( 4 );
        L.add( 5 );

        System.out.println( ceilBound( L, 6 ) ); // -1
        System.out.println( ceilBound( L, 0 ) ); // 0
        System.out.println( ceilBound( L, 1 ) ); // 0
        System.out.println( ceilBound( L, 2 ) ); // 1
        System.out.println( ceilBound( L, 3 ) ); // 2
        System.out.println( ceilBound( L, 4 ) ); // 3
        System.out.println( ceilBound( L, 5 ) ); // 4
    }

    static
    void testFloorBound() {
        List<Integer> L = new ArrayList<>();
        L.add( 1 );
        L.add( 2 );
        L.add( 3 );
        L.add( 4 );
        L.add( 5 );

        System.out.println( floorBound( L, 6 ) ); // 4
        System.out.println( floorBound( L, 0 ) ); // -1
        System.out.println( floorBound( L, 1 ) ); // 0
        System.out.println( floorBound( L, 2 ) ); // 1
        System.out.println( floorBound( L, 3 ) ); // 2
        System.out.println( floorBound( L, 4 ) ); // 3
        System.out.println( floorBound( L, 5 ) ); // 4
    }

    static
    void testLowerBound() {
        List<Integer> L = new ArrayList<>();
        L.add( 1 );
        L.add( 2 );
        L.add( 3 );
        L.add( 4 );
        L.add( 5 );

        System.out.println( lowerBound( L, 6 ) ); // 4
        System.out.println( lowerBound( L, 0 ) ); // -1
        System.out.println( lowerBound( L, 1 ) ); // -1
        System.out.println( lowerBound( L, 2 ) ); // 0
        System.out.println( lowerBound( L, 3 ) ); // 1
        System.out.println( lowerBound( L, 4 ) ); // 2
        System.out.println( lowerBound( L, 5 ) ); // 3
    }

    public static
    void main(String[] args) {
//        testKSelect();
//        testCeilBound();
        testLowerBound();
    }
}
