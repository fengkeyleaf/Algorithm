package com.fengkeyleaf.util;

/*
 * CompareElement.java
 * JDK: 16
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 added compare() on 6/15/2021$
 *     $1.1 added min(), max(), minMax() on 8/20/2021$
 *
 */

import java.util.*;

/**
 * This class consists exclusively of static methods
 * that related to comparing elements
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class CompareElement {

    /**
     * Compare two elements using either Comparator<E> or Comparable<E>
     *
     * Note that in order to avoid errors, either a Comparator<E> is provided
     * or the element, E, implements Comparable<E>
     * */

    public static<E>
    int compare( Comparator<? super E> c, E e1, E e2 ) {
        // use Comparable<K>
        if ( c == null ) {
            Comparable<? super E> comparable = ( Comparable<? super E> ) e1;
            return comparable.compareTo( e2 );
        }

        // use Comparator<K>
        return c.compare( e1, e2 );
    }

    /**
     * get min between two elements
     * */

    public static<E>
    E min( Comparator<? super E> c, E e1, E e2 ) {
        int res = compare( c, e1, e2 );

        if ( res < 0 ) return e1;
        return e2;
    }

    /**
     * get max between two elements
     * */

    public static<E>
    E max( Comparator<? super E> c, E e1, E e2 ) {
        int res = compare( c, e1, e2 );

        if ( res > 0 ) return e1;
        return e2;
    }

    /**
     * get min in the collection.
     *
     * Source code:
     * @see java.util.Collections
     * */

    // https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/util/Collections.html
    public static<E>
    E min( Comparator<? super E> c, Collection<E> coll ) {
        Iterator<? extends E> i = coll.iterator();
        E candidate = i.next();

        while ( i.hasNext() ) {
            E next = i.next();
            if ( compare( c, next, candidate ) < 0 )
                candidate = next;
        }
        return candidate;
    }

    /**
     * get max in the collection.
     *
     * Source code:
     * @see java.util.Collections
     * */

    public static<E>
    E max( Comparator<? super E> c, Collection<E> coll ) {
        Iterator<? extends E> i = coll.iterator();
        E candidate = i.next();

        while ( i.hasNext() ) {
            E next = i.next();
            if ( compare( c, next, candidate ) > 0 )
                candidate = next;
        }
        return candidate;
    }

    /**
     * get min and max in the list of the range [l,r)
     *
     * @return      { min, max }
     * */

    public static<E>
    List<E> minMax( Comparator<? super E> c,
                    List<E> elements, int l, int r ) {

        List<E> res = new ArrayList<>( 2 );
        // out of boundary or list is null
        if ( r <= l || elements == null ) return null;

        // find min and max,
        // iterating the list
        E min = elements.get( l );
        E max = elements.get( l );
        for ( int i = l + 1; i < r; i++ ) {
            E element = elements.get( i );
            if ( element == null ) continue;

            min = min( c, min, element );
            max = max( c, max, element );
        }

        res.add( min );
        res.add( max );
        return res;
    }

    /**
     * get min and max in the list
     *
     * @return      { min, max }
     * */

    public static<E>
    List<E> minMax( Comparator<? super E> c, List<E> elements ) {
        return minMax( c, elements, 0, elements.size() );
    }
}
