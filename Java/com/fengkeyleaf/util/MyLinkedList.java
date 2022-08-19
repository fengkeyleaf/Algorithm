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

import java.util.*;

/**
 * data structure of doubly linked list
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/LinkedList.html
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Iterable.html
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Iterator.html
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ListIterator.html
public class MyLinkedList<E> implements Iterable<E> {
    DoublyLinkedNode<E> front;
    DoublyLinkedNode<E> end;
    int size = 0;
    Checker c = new Checker( this );

    /**
     * constructs to create an instance of MyLinkedList
     * */

    public MyLinkedList() {}

    public MyLinkedList( E e ) {
        if ( e == null )
            throw new NullPointerException();

        initAdd( new DoublyLinkedNode<>( e ) );
        size++;
    }

    MyLinkedList( DoublyLinkedNode<E> n ) {
        if ( n != null && n.data == null )
            throw new NullPointerException();

        initAdd( n );
        if ( n != null ) updateSize();
    }

    private void updateSize() {
        int size = 0;
        DoublyLinkedNode<E> n = front;
        while ( n != null ) {
            size++;
            if ( n.next == null ) end = n;
            n = n.next;
        }

        this.size = size;
    }

    private void initAdd( DoublyLinkedNode<E> n ) {
        front = n;
        end = front;
    }

    //-------------------------------------------------------
    // Insert Operations.
    //-------------------------------------------------------

    /**
     * insert the element after the before node.
     * */

    public void addAfter( DoublyLinkedNode<E> before, E e ) {
        if ( e == null )
            throw new NullPointerException();

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
        assert c.check();
    }

    /**
     * insert the element before the after node.
     * */

    public void addBefore( DoublyLinkedNode<E> after, E e ) {
        if ( e == null )
            throw new NullPointerException();

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
        assert c.check();
    }

    /**
     * Appends the specified element to the end of this list.
     */

    public boolean add( E e ) {
        return addLast( e );
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * */

    public void add( int i, E e ) {
        if ( MyArrays.isOutOfIndex( i, size ) )
            throw new IndexOutOfBoundsException();

        DoublyLinkedNode<E> n = getNode( i );
        addBefore( n, e );
    }

    /**
     * Appends the specified element to the end of this list.
     */

    public boolean addLast( E e ) {
        addAfter( end, e );
        return true;
    }

    /**
     * Inserts the specified element at the beginning of this list.
     * */

    public boolean addFirst( E e ) {
        addBefore( front, e );
        return true;
    }

    //-------------------------------------------------------
    // Query Operations.
    //-------------------------------------------------------

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

    DoublyLinkedNode<E> getNode( int i ) {
        if ( MyArrays.isOutOfIndex( i, size ) )
            throw new IndexOutOfBoundsException();

        DoublyLinkedNode<E> n = front;
        int idx = 0;
        while ( idx++ < i )
            n = n.next;

        return n;
    }

    /**
     * Returns the first element in this list.
     *
     * @throws NoSuchElementException - if this list is empty
     */

    public E getFirst() {
        if ( isEmpty() ) throw new NoSuchElementException( "Query in an empty list" );

        return front.data;
    }

    /**
     * Returns the first node in this list.
     *
     * @throws NoSuchElementException - if this list is empty
     */

    public DoublyLinkedNode<E> getFirstNode() {
        if ( isEmpty() ) throw new NoSuchElementException( "Query in an empty list" );

        return front;
    }

    /**
     * Returns the last element in this list.
     *
     * @throws NoSuchElementException - if this list is empty
     */

    public E getLast() {
        if ( isEmpty() ) throw new NoSuchElementException( "Query in an empty list" );

        return isEmpty() ? null : end.data;
    }

    /**
     * Returns the last node in this list.
     *
     * @throws NoSuchElementException - if this list is empty
     */

    public DoublyLinkedNode<E> getLastNode() {
        if ( isEmpty() ) throw new NoSuchElementException( "Query in an empty list" );

        return isEmpty() ? null : end;
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     * */

    public E get( int i ) {
        if ( MyArrays.isOutOfIndex( i, size ) )
            throw new IndexOutOfBoundsException();

        return listIterator( i ).next();
    }

    //-------------------------------------------------------
    // Remove Operations.
    //-------------------------------------------------------


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
        assert c.check();
        return n.data;
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
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     * */

    public E remove( int i ) {
        if ( MyArrays.isOutOfIndex( i, size ) )
            throw new IndexOutOfBoundsException();

        return remove( getNode( i ) );
    }

    //-------------------------------------------------------
    // Iterable
    //-------------------------------------------------------

    /**
     * Returns a list-iterator of the elements
     * in this list (in proper sequence), starting at the specified position in the list.
     * @param i index of the first element to be returned from the list-iterator (by a call to next)
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     */

    public ListIterator<E> listIterator( int i ) {
        if ( MyArrays.isOutOfIndex( i, size ) )
            throw new IndexOutOfBoundsException( i + "is out of index range" );

        return new ListItr( i );
    }

    /**
     * Adapter to provide list iterator.
     * Reference resource: {@link LinkedList}
     */

    class ListItr implements ListIterator<E> {

        DoublyLinkedNode<E> lastReturn;
        DoublyLinkedNode<E> next;

        ListItr( int i ) {
            next = front;
            int idx = 0;
            while ( idx++ < i ) {
                lastReturn = next;
                next = next == null ? null : next.next;
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public E next() {
            if ( !hasNext() )
                throw new NoSuchElementException();

            lastReturn = next;
            next = next.next;
            return lastReturn.data;
        }

        @Override
        public boolean hasPrevious() {
            return next == null ? end != null : next.prev != null;
        }

        @Override
        public E previous() {
            if ( !hasPrevious() )
                throw new NoSuchElementException();

            lastReturn = next = next == null ? end : next.prev;
            return lastReturn.data;
        }

        @Override
        public int nextIndex() {
            System.err.println( "Not available now: nextIndex" );
            System.exit( 1 );
            return -1;
        }

        @Override
        public int previousIndex() {
            System.err.println( "Not available now: previousIndex" );
            System.exit( 1 );
            return -1;
        }

        @Override
        public void remove() {
            System.err.println( "Not available now: remove" );
            System.exit( 1 );
        }

        @Override
        public void set( E e ) {
            System.err.println( "Not available now: set" );
            System.exit( 1 );
        }

        @Override
        public void add( E e ) {
            System.err.println( "Not available now: add" );
            System.exit( 1 );
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new ListItr( 0 );
    }

    /**
     * Returns an iterator over the elements in this deque in reverse sequential order.
     * The elements will be returned in order from last (tail) to first (head).
     *
     * reference resource: {@link LinkedList}
     * */

    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /**
     * Adapter to provide descending iterators via ListItr.previous
     */

    private class DescendingIterator implements Iterator<E> {

        final ListItr iter = new ListItr( size );

        @Override
        public boolean hasNext() {
            return iter.hasPrevious();
        }

        @Override
        public E next() {
            return iter.previous();
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

    /**
     * Split this list two parts, splitting at the given index.
     *
     * [ 1,2,3,4 ], s = 2
     * =>
     * [ [ 1,2,3 ], [ 4 ] ]
     *
     * @param s position to be split, indexing at 0;
     *          We'll split the list right after s.
     *          e.g s = 2, we will split the list after 2 but before 3;
     * @return [ list containing elements prior to s( including s ), list containing elements after s ]
     */

    public List<MyLinkedList<E>> split( int s ) {
        if ( MyArrays.isOutOfIndex( s, size ) )
            throw new IndexOutOfBoundsException();

        if ( isEmpty() ) return null;

        DoublyLinkedNode<E> n = front;
        int idx = 0;
        while ( idx++ < s )
            n = n.next;

        List<MyLinkedList<E>> res = new ArrayList<>( 2 );

        DoublyLinkedNode<E> other = n.next;
        n.next = null;
        if ( other != null ) other.prev = null;

        res.add( this );
        res.add( new MyLinkedList<>( other ) );

        size -= res.get( 1 ).size;
        end = n;

        assert c.check();
        assert res.get( 1 ).c.check();
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

    class Checker {
        final MyLinkedList<E> l;

        Checker( MyLinkedList<E> l ) {
            this.l = l;
        }

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
            DoublyLinkedNode<E> front = l.front;
            while ( front != null ) {
                assert front.prev == null || front.prev.next == front;
                assert front.next == null || front.next.prev == front : front + " " + front.next;
                front = front.next;
            }

            return true;
        }

        private boolean isRightSize() {
            int count = 0;
            DoublyLinkedNode<E> front = l.front;
            while ( front != null ) {
                count++;
                front = front.next;
            }

            return count == size;
        }

        private boolean isRightFront() {
            DoublyLinkedNode<E> end = l.end;
            while ( end != null && end.prev != null ) {
                end = end.prev;
            }

            return front == end;
        }

        private boolean isRightEnd() {
            DoublyLinkedNode<E> front = l.front;
            while ( front != null && front.next != null ) {
                front = front.next;
            }

            return front == end;
        }
    }
}
