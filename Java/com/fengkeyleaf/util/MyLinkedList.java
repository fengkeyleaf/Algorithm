package com.fengkeyleaf.util;

/*
 * MyLinkedList.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 addBefore(), addAfter() and remove( node ) on 1/26/2022$
 */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * data structure of doubly linked list
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// TODO: 5/21/2022 implement ListIterator.
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/LinkedList.html
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Iterable.html
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Iterator.html
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ListIterator.html
public class MyLinkedList<E> implements Iterable<E> {
    DoublyLinkedNode<E> front;
    DoublyLinkedNode<E> end;
    int size = 0;

    /**
     * constructs to create an instance of MyLinkedList
     * */

    public MyLinkedList() {}

    public MyLinkedList( E e ) {
        initAdd( new DoublyLinkedNode<>( e ) );
        size++;
    }

    private void initAdd( DoublyLinkedNode<E> n ) {
        front = n;
        end = front;
    }

    /**
     * insert the element after the before node.
     * */

    public void addAfter( DoublyLinkedNode<E> before, E e ) {
        addAfter( before, new DoublyLinkedNode<>( e ) );
    }

    public void addAfter( DoublyLinkedNode<E> before,
                          DoublyLinkedNode<E> n ) {

        size++;

        if ( front == null ) {
            initAdd( n );
            return;
        }

        n.next = before.next;
        before.next = n;
        n.prev = before;
        if ( n.next != null ) n.next.prev = n;

        if ( before == end ) end = n;
        assert check();
    }

    /**
     * insert the element before the after node.
     * */

    public void addBefore( DoublyLinkedNode<E> after, E e ) {
        addBefore( after, new DoublyLinkedNode<>( e ) );
    }

    public void addBefore( DoublyLinkedNode<E> after,
                           DoublyLinkedNode<E> n ) {
        size++;

        if ( front == null ) {
            initAdd( n );
            return;
        }

        n.next = after;
        n.prev = after.prev;
        after.prev = n;
        if ( n.prev != null ) n.prev.next = n;

        if ( after == front ) front = n;
        assert check();
    }

    /**
     * remove the given node in this linked list and return its value.
     * return null if this list is empty.
     * Do nothing if the node is not in this list.
     * */

    public E remove( DoublyLinkedNode<E> n ) {
        if ( n == null ) return null;
        assert contains( n );

        if ( n.prev != null ) n.prev.next = n.next;
        if ( n.next != null ) n.next.prev = n.prev;

        if ( front == n ) front = n.next;
        if ( end == n ) end = n.prev;

        n.prev = null;
        n.next = null;

        size--;
        assert check();
        return n.data;
    }

    /**
     * does this linked list contain this node?
     *
     * when to use this method:
     * it is recommended that assert a given node to be deleted is in this list,
     * when removing it directly from the list.
     * i.e. we do not first find where it is and delete it.
     *
     * For example:
     * remove( node ):
     *      assert contains( node );
     *      // code to delete
     * */

    public boolean contains( DoublyLinkedNode<E> n ) {
        if ( n == null ) return false;

        DoublyLinkedNode<E> front = this.front;
        while ( front != null ) {
            if ( front == n ) return true;
            front = front.next;
        }

        return false;
    }

    /**
     * Retrieves and removes the head (first element) of this list.
     * the head of this list, or null if this list is empty.
     * */

    public E poll() {
        return remove( front );
    }

    public E pollLast() {
        return remove( end );
    }

    /**
     * get the node containing the element in this linked list
     * */

    public DoublyLinkedNode<E> getNode( E e ) {
        DoublyLinkedNode<E> front = this.front;
        while ( front != null ) {
            if ( e == front.data ) return front;
            front = front.next;
        }

        return null;
    }

    //-------------------------------------------------------
    // Iterable
    //-------------------------------------------------------

    @Override
    public Iterator<E> iterator() {
        return new IterList( front );
    }

    /**
     * class to implement Iterator.
     * */

    private class IterList implements Iterator<E> {
        DoublyLinkedNode<E> front;

        IterList( DoublyLinkedNode<E> front ) {
            this.front = front;
        }

        /**
         * Returns true if the iteration has more elements.
         * */

        @Override
        public boolean hasNext() {
            return front != null;
        }

        /**
         * Returns the next element in the iteration.
         * */

        @Override
        public E next() {
            if ( !hasNext() )
                throw new NoSuchElementException();

            DoublyLinkedNode<E> lastReturn = front;
            front = front.next;
            return lastReturn.data;
        }
    }

    /**
     * Returns an iterator over the elements in this deque in reverse sequential order.
     * The elements will be returned in order from last (tail) to first (head).
     *
     * reference resource: {@link LinkedList}
     * */

    public Iterator<E> descendingIterator() {
        return new DescendingIterator( end );
    }

    /**
     * Adapter to provide descending iterators via ListItr.previous
     */
    private class DescendingIterator extends IterList {

        DescendingIterator( DoublyLinkedNode<E> front ) {
            super( front );
        }
        public E next() {
            if ( !hasNext() )
                throw new NoSuchElementException();

            DoublyLinkedNode<E> lastReturn = front;
            front = front.prev;
            return lastReturn.data;
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        boolean res = size == 0;
        assert ( !res && front != null ) || ( res && front == null );
        return res;
    }

    @Override
    public String toString() {
        LinkedList<E> list = new LinkedList<>();

        for ( E e : this ) {
            list.add( e );
        }

        return list + " | Fr: " + this.front + ", Ee: " + end;
    }

    //-------------------------------------------------------
    // Check integrity of Linked list data structure.
    //-------------------------------------------------------

    private boolean check() {
        boolean isDoublyLinked = isDoublyLinked();
        boolean isSize = isRightSize();
        boolean isRightFront = isRightFront();
        boolean isRightEnd = isRightEnd();

        if ( !isDoublyLinked ) System.err.println( "Not doubly linked" );
        if ( !isSize ) System.err.println( "Size is inconsistent with nodes" );
        if ( !isRightFront ) System.err.println( "Front is not right" );
        if ( !isRightEnd ) System.err.println( "End is not right" );

        return isDoublyLinked && isSize && isRightEnd && isRightFront;
    }

    private boolean isDoublyLinked() {
        DoublyLinkedNode<E> front = this.front;
        while ( front != null ) {
            assert front.prev == null || front.prev.next == front;
            assert front.next == null || front.next.prev == front : front + " " + front.next;
            front = front.next;
        }

        return true;
    }

    private boolean isRightSize() {
        int count = 0;
        DoublyLinkedNode<E> front = this.front;
        while ( front != null ) {
            count++;
            front = front.next;
        }

        return count == size;
    }

    private boolean isRightFront() {
        DoublyLinkedNode<E> end = this.end;
        while ( end != null && end.prev != null ) {
            end = end.prev;
        }

        return front == end;
    }

    private boolean isRightEnd() {
        DoublyLinkedNode<E> front = this.front;
        while ( front != null && front.next != null ) {
            front = front.next;
        }

        return front == end;
    }

    //-------------------------------------------------------
    // Test.
    //-------------------------------------------------------

    public static
    void main( String[] args ) {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addBefore( null, 0 );
        list.addBefore( list.getNode( 0 ), 1 );
        list.addAfter( list.getNode( 0 ), 2 );
        list.addBefore( list.getNode( 1 ), 3 ); // 3, 1, 0, 2
        System.out.println( list );

        list.remove( list.getNode( 1 ) );
        System.out.println( list );
        list.remove( list.getNode( 2 ) );
        System.out.println( list );
        list.remove( list.getNode( 3 ) );
        System.out.println( list );
        list.remove( list.getNode( 0 ) );
        System.out.println( list );

        list.remove( list.getNode( 6 ) );
        System.out.println( list );

        list.addAfter( list.getNode( 0 ), 2 );
        System.out.println( list );
    }
}
