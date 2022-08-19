package coding.leetcode.medium;

/*
 * InsertionSortList.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/25/2022$
 */

import coding.leetcode.ListNode;
import coding.leetcode.hard.MergeKSortedLists;
import com.fengkeyleaf.util.DoublyLinkedNode;
import com.fengkeyleaf.util.MyLinkedList;

/**
 * <a href="https://leetcode.com/problems/insertion-sort-list/">147. Insertion Sort List</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

final class InsertionSortList {

    public ListNode insertionSortList( ListNode head ) {
        return MergeKSortedLists.getResult( insertSort( MergeKSortedLists.convertToList( head ) ) );
    }

    private MyLinkedList<ListNode> insertSort( MyLinkedList<ListNode> L ) {
        System.out.println( L );
        MyLinkedList<ListNode> res = new MyLinkedList<>();
        for ( ListNode n : L )
            insertSort( res, n );

        System.out.println( res );
        return res;
    }

    private void insertSort( MyLinkedList<ListNode> L, ListNode n ) {
        if ( L.isEmpty() ) {
            L.add( n );
            return;
        }

        DoublyLinkedNode<ListNode> f = L.getFirstNode();

        while ( f != null ) {
            if ( f.getData().val >= n.val ) {
                L.addBefore( f, n );
                return;
            }

             f = f.getNext();
        }

        L.addLast( n );
    }

    static
    void test1() {
        ListNode n1 = new ListNode( 4, new ListNode( 2, new ListNode( 1, new ListNode( 3 ) ) ) );

        System.out.println( new InsertionSortList().insertionSortList( n1 ).printAll() );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
