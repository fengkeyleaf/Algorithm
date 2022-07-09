package com.fengkeyleaf.lang;

/*
 * NumberRadix.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 * JDK: 15
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Number in base radix for radix sort
 * Note that current implementation cannot handle negative numbers,
 * since Q3 of hw_2 don't have negative numbers to deal with
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class NumberRadix implements Comparable<NumberRadix> {
    // original number in base 10
    public final long origin;
    // array to represent each digit
    private final List<Integer> digits = new ArrayList<>();
    // base in this number
    public final int radix;
    public final boolean ifNonNegative;

    public NumberRadix( long origin, int radix, boolean ifNonNegative ) {
        this.origin = origin;
        this.radix = radix;
        this.ifNonNegative = ifNonNegative;
    }

    /**
     * return the number digits to represent this number in base radix
     */

    public int numberOfDigits() {
        return digits.size();
    }

    /**
     * add a digit with O(1) time complexity
     */

    public void addDigit( int digit ) {
        digits.add( digit );
    }

    /**
     * get value at a given digit
     */

    public int getDigit( int index ) {
        // treat digits out of boundary as 0
        return ( index >= digits.size() || index < 0 ) ? 0 : digits.get( index );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null || getClass() != obj.getClass() ) return false;
        NumberRadix that = ( NumberRadix ) obj;
        return origin == that.origin;
    }

    @Override
    public int compareTo( NumberRadix obj ) {
        return Long.compare( origin, obj.origin );
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder( ifNonNegative ? "" : "-" );
        for ( int i = digits.size() - 1; i >= 0; i-- )
            text.append( digits.get( i ) ).append( "'" );

        return text.toString();
    }
}
