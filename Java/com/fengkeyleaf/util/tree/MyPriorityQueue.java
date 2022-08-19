package com.fengkeyleaf.util.tree;

/*
 * HeadArray.java
 *
 * Version:
 *     $1.2$
 *
 * Revisions:
 *     $1.0 basic operations$
 *     $1.1 added update-key operation on 4/16/2021$
 *     $1.2 allowed using Comparator<T> or Comparable<T>
 *          to compare elements in this heap on 8/4/2021$
 */

import com.fengkeyleaf.util.CompareElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Data structure of a heap with generics,
 * implemented with array
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/PriorityQueue.html
public class MyPriorityQueue<E>
        extends PerfectBinaryTree<E> implements Iterable<E> {

    // comparator to compare element, E
    protected final Comparator<E> comparator;

    /**
     * constructs to create an instance of MyPriorityQueue
     * */

    public MyPriorityQueue() {
        this( null );
    }

    public MyPriorityQueue( Comparator<E> comparator ) {
        this.comparator = comparator;
    }

    public MyPriorityQueue( int capacity ) {
        this( capacity, null );
    }

    public MyPriorityQueue( int capacity, Comparator<E> comparator ) {
        super( capacity );
        this.comparator = comparator;
    }

    /**
     * bubble up when children are greater than their parent with extracting Max，
     * or when children are less than their parent with extracting Min
     * */

    protected int compareElement( int parentIndex, int currentIndex ) {
        E parent = tree.get( parentIndex );
        E current = tree.get( currentIndex );

        return CompareElement.compare( comparator, parent, current );
    }

    /**
     * Retrieves, but does not remove,
     * the head of this queue, or returns null if this queue is empty.
     *
     * @return the head of this queue,
     *         also the element with the highest priority, if not empty.
     * */

    // https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/util/PriorityQueue.html#peek()
    public E peek() {
        if ( isEmpty() ) return null;

        return tree.get( 0 );
    }

    /**
     * do bubble up
     * */

    protected int bubbleUp( int currentIndex ) {
        int parentIndex = getParentIndex( currentIndex );

        // stop bubbling up when reaching root
        while ( currentIndex > 0 ){
            if ( compareElement( parentIndex, currentIndex ) < 0 )
                Collections.swap( tree, currentIndex, parentIndex );
            else
                break;

            currentIndex = parentIndex;
            parentIndex = getParentIndex( currentIndex );
        }

        return currentIndex;
    }

    /**
     * insert an element into this heap
     * */

    public boolean insert( E data ) {
        if ( data == null )
            throw new NullPointerException( "insert null in heap" );

        if ( tree.isEmpty() ) {
            tree.add( data );
            return true;
        }

        tree.add( data );
        bubbleUp( tree.size() - 1 );
        return true;
    }

    /**
     * Inserts the specified element into this priority queue.
     */

    public boolean add( E data ) {
        return insert( data );
    }

    /**
     * bubble down when a parent is less than its children with extracting Max，
     * or when a parent is greater than its children with extracting Min
     * */

    protected int ifBubbleDown( int parent, int left, int right ) {
        // no children
        if ( left >= tree.size() && right >= tree.size() )
            return -1;
        // left child is empty
        else if ( left >= tree.size() )
            return compareElement( parent, right ) < 0 ? right : -1;
        // right child is empty
        else if ( right >= tree.size() )
            return compareElement( parent, left ) < 0 ? left : -1;

        // swap the one with the highest priority
        int maxChildren = compareElement( left, right ) > 0 ? left : right;
        return compareElement( parent, maxChildren ) < 0 ? maxChildren : -1;
    }

    /**
     * do bubble down
     * */

    protected int bubbleDown( int parentIndex ) {
        int leftChildIndex = getChildrenIndex( parentIndex, true );
        int rightChildIndex = getChildrenIndex( parentIndex, false );

        // stop bubbling down if a parent doesn't have any children
        while ( leftChildIndex < tree.size() ||
                rightChildIndex < tree.size() ) {
            int swapIndex = ifBubbleDown( parentIndex, leftChildIndex, rightChildIndex );
            if ( swapIndex > -1 ) {
                Collections.swap( tree, parentIndex, swapIndex );
                parentIndex = swapIndex;
            }
            else
                break;

            leftChildIndex = getChildrenIndex( parentIndex, true );
            rightChildIndex = getChildrenIndex( parentIndex, false );
        }

        return parentIndex;
    }

    /**
     * delete an element with the highest priority from this heap.
     *
     * @return the head of this queue, or null if this queue is empty
     * */

    public E delete() {
        if ( tree.isEmpty() ) return null;

        Collections.swap( tree, 0, tree.size() - 1 );
        E removedData = tree.remove( tree.size() - 1 );

        bubbleDown( 0 );
        return removedData;
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     *
     * @return the head of this queue, or null if this queue is empty
     */

    public E poll() {
        return delete();
    }

    public void addAll( Collection<E> list ) {
        for ( E element : list )
            insert( element );
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return tree.iterator();
    }
}
