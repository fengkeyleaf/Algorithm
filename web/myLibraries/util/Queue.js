"use strict"

/*
 * Queue.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 Basic operations on 12/10/2021$
 */

/**
 * Data structure of Queue
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Queue {

    /**
     * Creates an empty Queue.
     */

    constructor() {
        this.array = [];
    }

    enqueue( item ) {
        this.array.push( item );
    }

    dequeue() {
        return this.isEmpty() ? null : this.array.shift();
    }

    front() {
        return this.isEmpty() ? null : this.array[ 0 ];
    }

    back() {
        return this.array.getLast();
    }

    /**
     * Tests if this stack is empty
     */

    isEmpty() {
        return this.array.isEmpty();
    }

    size() {
        return this.array.length;
    }

    /**
     * clear this queue, but this will give a new pointer
     */

    clear() {
        this.array = [];
    }
}
