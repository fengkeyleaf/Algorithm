package coding.leetcode.medium;

/*
 * SortList.java
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
import com.fengkeyleaf.util.MyLinkedList;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * <a href="https://leetcode.com/problems/sort-list/">148. Sort List</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public final class SortList {
    public ListNode sortList( ListNode head ) {
        return mergeSort( head );
//        return MergeKSortedLists.getResult( mergeSort( MergeKSortedLists.convertToList( head ) ) );
    }

    // TODO: 7/25/2022 implement space complexity O(1)
    private ListNode mergeSort( ListNode h ) {
        List<Integer> L = new ArrayList<>();
        while ( h != null ) {
            L.add( h.val );
            h = h.next;
        }

        System.out.println( L );
        L.sort( Integer::compareTo );

        ListNode n = null;
        ListNode r = null;
        for ( Integer i : L ) {
            if ( n == null ) {
                r = n = new ListNode( i );
                continue;
            }

            n.next = new ListNode( i );
            n = n.next;
        }

        return r;
    }

    private MyLinkedList<ListNode> mergeSort( MyLinkedList<ListNode> Ls ) {
        if ( Ls.size() < 2 ) return Ls;

        int mid = ( Ls.size() - 1 - 0 ) / 2 + 0;
        List<MyLinkedList<ListNode>> S = Ls.split( mid );
        MyLinkedList<ListNode> L = mergeSort( S.get( 0 ) );
        MyLinkedList<ListNode> R = mergeSort( S.get( 1 ) );

        if ( L.isEmpty() ) return R;
        else if ( R.isEmpty() ) return L;

        ListIterator<ListNode> iL = L.listIterator( 0 );
        ListIterator<ListNode> iR = R.listIterator( 0 );
        MyLinkedList<ListNode> M = new MyLinkedList<>();
        while ( iL.hasNext() && iR.hasNext() ) {
            ListNode l = iL.next();
            ListNode r = iR.next();
            if ( l.val < r.val ) {
                M.add( l );
                iR.previous();
            }
            else {
                M.add( r );
                iL.previous();
            }
        }

        MergeKSortedLists.copyLefts( M, iL );
        MergeKSortedLists.copyLefts( M, iR );
        return M;
    }

    static
    void test1() {
        ListNode n1 = new ListNode( 4, new ListNode( 2, new ListNode( 1, new ListNode( 3 ) ) ) );

        System.out.println( new SortList().sortList( n1 ).printAll() );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
