package myLibraries.util;

/*
 * DoublyLinkedNode.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/26/2022$
 *
 */

/**
 * data structure of doubly linked list node
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class DoublyLinkedNode<E> {

    private static int IDStatic = 0;
    final int ID;
    E data;
    DoublyLinkedNode<E> prev;
    DoublyLinkedNode<E> next;

    /**
     * constructs to create an instance of DoublyLinkedNode
     * */

    public DoublyLinkedNode( E data ) {
        this( null, null, data );
    }

    public DoublyLinkedNode( DoublyLinkedNode<E> pre,
                             DoublyLinkedNode<E> next, E data ) {
        ID = IDStatic++;
        this.prev = pre;
        this.next = next;
        this.data = data;
    }

    public DoublyLinkedNode<E> getNext() {
        return next;
    }

    public DoublyLinkedNode<E> getPrev() {
        return prev;
    }

    public E getData() {
        return data;
    }

    @Override
    public String toString() {
        return data.toString();
//        return ( next == null ? "" : next ) + "<-" + data + "<-" + ( prev == null ? "" : prev );
    }
}
