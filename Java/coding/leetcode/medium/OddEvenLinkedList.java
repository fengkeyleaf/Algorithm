package coding.leetcode.medium;

/*
 * OddEvenLinkedList.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/22/2022$
 */

import coding.leetcode.ListNode;

/**
 * <a href="https://leetcode.com/problems/odd-even-linked-list/">328. Odd Even Linked List/a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class OddEvenLinkedList {

    public ListNode oddEvenList( ListNode head ) {
        if ( head == null ) return null;

        ListNode evenHead = head.next;
        if ( evenHead == null ) return head;

        ListNode oddPtr = evenHead.next;
        ListNode oddPre = head;
        if ( oddPtr == null ) return head;

        ListNode eventPtr = oddPtr.next;
        ListNode eventPre = evenHead;

        // first connect odd and even nodes separately,
        // and then connect the odd part to the event part.
        do {
            oddPre.next = oddPtr;
            eventPre.next = eventPtr;

            oddPre = oddPtr;
            if ( eventPtr != null ) oddPtr = eventPtr.next;
            else oddPtr = null;

            eventPre = eventPtr;
            if ( oddPtr != null ) eventPtr = oddPtr.next;
            else eventPtr = null;

        } while ( oddPtr != null );

        oddPre.next = evenHead;
        return head;
    }

    static
    void test1() {
        ListNode n1 = new ListNode( 1, new ListNode( 2, new ListNode( 3, new ListNode( 4, new ListNode( 5 ) ) ) ) );
        System.out.println( n1 );
        System.out.println( new OddEvenLinkedList().oddEvenList( n1 ) );
    }

    static
    void test2() {
        ListNode n1 = new ListNode( 2, new ListNode( 1, new ListNode( 3, new ListNode( 5, new ListNode( 6, new ListNode( 4, new ListNode( 7 ) ) ) ) ) ) );
        System.out.println( n1 );
        System.out.println( new OddEvenLinkedList().oddEvenList( n1 ) );
    }

    static
    void test3() {
        ListNode n1 = new ListNode( 2, new ListNode( 1, new ListNode( 3 ) ) );
        System.out.println( n1 );
        System.out.println( new OddEvenLinkedList().oddEvenList( n1 ) );
    }

    public static
    void main( String[] args ) {
//        test1();
//        test2();
        test3();
    }
}
