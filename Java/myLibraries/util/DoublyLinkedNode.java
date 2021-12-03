package myLibraries.util;

/*
 * DoublyLinkedNode.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 * JDK: 16
 */

/**
 *
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class DoublyLinkedNode<E> {
    private static int IDStatic = 0;
    public final int ID;
    public E dataField;
    public DoublyLinkedNode<E> prev;
    public DoublyLinkedNode<E> next;

    /**
     * constructs to create an instance of Node
     * */

    public DoublyLinkedNode( E dataField ) {
        this( null, null, dataField );
    }

    public DoublyLinkedNode( DoublyLinkedNode<E> pre,
                             DoublyLinkedNode<E> next, E dataField ) {
        ID = IDStatic++;
        this.prev = pre;
        this.next = next;
        this.dataField = dataField;
    }

    @Override
    public String toString() {
        return ( next == null ? "" : next ) + "<-" + dataField + "<-" + ( prev == null ? "" : prev );
    }
}
