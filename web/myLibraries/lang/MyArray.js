"use strict"

/*
 * MyArray.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 isEmpty(), getLast(), getFirst() on 9/12/2021$
 */

/**
 * Add methods to the Array class
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

import MyMath from "./MyMath.js";

// ---------------------------------
// general array class
// ---------------------------------

Array.prototype.isEmpty = function () {
    return this.length === 0;
}

Array.prototype.getLast = function () {
    return this.isEmpty() ? null : this[ this.length - 1 ];
}

Array.prototype.getFirst = function () {
    return this.isEmpty() ? null : this[ 0 ];
}

/**
 * clear this array while keeping original reference
 */

Array.prototype.clear = function () {
    return this.splice( 0 );
}

// ---------------------------------
// Float32Array class
// ---------------------------------

/**
 * @param {Number} nums
 */

function getFloat32( ...nums ) {
    return new Float32Array( nums );
}

Float32Array.prototype.isEmpty = function () {
    return this.length === 0;
};

/**
 * @param {Number} nums
 */

function push( ...nums ) {
    let res = [];
    this.forEach( n => res.push( n ) );
    return new Float32Array( res.concat( nums ) );
}

Float32Array.prototype.push = push;

/**
 * @param {[Number]} nums
 */

function concat( ...nums ) {
    let res = [];
    this.forEach( n => res.push( n ) );
    for ( const num of nums ) {
        res = res.concat( num );
    }

    return new Float32Array( res );
}

Float32Array.prototype.concat = concat;

/**
 * @param {Number} start
 * @param {Number} end
 */

function remove( start, end ) {
    if ( MyMath.isUndefined( end ) ) end = this.length;
    if ( start >= end ) return null;

    let keeps = [];
    this.slice( 0, start + 1 ).forEach( n => keeps.push(n));
    this.slice( end ).forEach( n => keeps.push(n));

    return new Float32Array( keeps );
}

Float32Array.prototype.remove = remove;

