package com.fengkeyleaf.util;

/*
 * TestMyLinkedList.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/25/2022$
 */

import java.util.Iterator;
import java.util.ListIterator;

/**
 * Test MyLinkedList.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class TestMyLinkedList {

    static
    void testNormal() {
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

    static
    void testIterator() {
        MyLinkedList<Integer> L = new MyLinkedList<>();
        L.add( 1 );
        L.add( 1 );
        L.add( 2 );
        L.add( 3 );
        L.add( 4 );
        L.add( 5 );
        L.add( 6 );

        Iterator<Integer> iter = L.iterator();
        while ( iter.hasNext() )
            System.out.print( iter.next() + " " );

        System.out.println();
        iter = L.descendingIterator();
        while ( iter.hasNext() )
            System.out.print( iter.next() + " " );

        System.out.println();
        ListIterator<Integer> listIter = L.listIterator( 2 );
//        while ( listIter.hasNext() )
            System.out.print( listIter.next() + " " );
            System.out.print( listIter.next() + " " );
            System.out.print( listIter.next() + " " );

        System.out.println();
        while ( listIter.hasPrevious() )
            System.out.print( listIter.previous() + " " );
    }

    static
    void testSplit() {
        MyLinkedList<Integer> L = new MyLinkedList<>();
        L.add( 1 );
        L.add( 1 );
//        L.add( 2 );
//        L.add( 3 );
//        L.add( 4 );
//        L.add( 5 );
//        L.add( 6 );

        System.out.println( L.split( 0 ) );
    }

    static
    void testOthers() {
        MyLinkedList<Integer> L = new MyLinkedList<>();
        L.add( 1 );
        L.add( 1 );
        L.add( 2 );
        L.add( 3 );
        L.add( 4 );
        L.add( 5 );
        L.add( 6 );

//        L.get( -1 );
        System.out.println( L.get( 0 ) );
        System.out.println( L.get( 4 ) );
        System.out.println( L.get( 6 ) );
//        L.get( 7 );

        System.out.println();
        System.out.println( L.remove( 0 ) );
        System.out.println( L );
        System.out.println( L.remove( L.size() - 1 ) );
        System.out.println( L );
    }

    public static
    void main( String[] args ) {
//        testNormal();
//        testIterator();
//        testSplit();
        testOthers();
    }
}
