"use strict"

/*
 * MyString.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 compareTo() on 9/12/2021$
 */

/**
 * Add methods to the String class
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

String.prototype.compareTo = function ( str ) {
    if ( this > str )
        return 1;
    else if ( this < str )
        return -1;

    return 0;
}

String.prototype.isEmpty = function () {
    return this.length === 0;
}
