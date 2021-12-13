"use strict"

/*
 * CompareElement.java
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 added compare() on 6/15/2021$
 *     $1.1 added min(), max(), minMax() on 8/20/2021$
 */

/**
 * This class consists exclusively of static methods
 * that related to comparing elements
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class CompareElement {

    /**
     * Compare two elements using either Comparator<E> or Comparable<E>
     *
     * Note that in order to avoid errors, either a Comparator<E> is provided
     * or the element, E, implements Comparable<E>.
     * More importantly, a comparator must be passed in
     * for comparison of primitive type, at least for now
     *
     * */

    static compare( comparator, element1, element2 ) {
        // use Comparable<K>
        if ( comparator == null ) {
            return element1.compareTo( element2 );
        }

        // use Comparator<K>
        return comparator( element1, element2 );
    }

    /**
     * choose option1 when it isn't null or undefined,
     * otherwise choose option2.
     * */

    static chooseWhich( option1, option2 ) {
        if ( CompareElement.isNullOrUndefined( option1 ) )
            return option2;

        return option1;
    }

    static isNullOrUndefined( element ) {
        return typeof ( element ) == "undefined" ||
            ( !element && element !== 0 );
    }
}