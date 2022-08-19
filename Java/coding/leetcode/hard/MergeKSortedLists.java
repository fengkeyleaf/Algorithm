package coding.leetcode.hard;

/*
 * MergeKSortedLists.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/24/2022$
 */

import coding.leetcode.ListNode;
import com.fengkeyleaf.util.MyLinkedList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * <a href="https://leetcode.com/problems/merge-k-sorted-lists/">23. Merge k Sorted Lists</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public final class MergeKSortedLists {

    public ListNode mergeKLists( ListNode[] lists ) {
        if ( lists.length < 1 ) return null;

        List<MyLinkedList<ListNode>> l = convertToList( lists );
        System.out.println( l );
        return getResult( mergeSort( l ) );
    }

    public static
    ListNode getResult( MyLinkedList<ListNode> L ) {
        Iterator<ListNode> iter = L.iterator();
        ListNode prev = null;
        while ( iter.hasNext() ) {
            if ( prev == null ) {
                prev = iter.next();
                continue;
            }

            prev.next = iter.next();
            prev = prev.next;
        }

        if ( !L.isEmpty() ) L.getLast().next = null;
        return L.isEmpty() ? null : L.getFirst();
    }

    private MyLinkedList<ListNode> mergeSort( List<MyLinkedList<ListNode>> Ls ) {
        if ( Ls.size() < 2 ) return Ls.get( 0 );

        int mid = ( Ls.size() - 1 - 0 ) / 2 + 0;
        MyLinkedList<ListNode> L = mergeSort( Ls.subList( 0, mid ) );
        MyLinkedList<ListNode> R = mergeSort( Ls.subList( mid, Ls.size() ) );

        if ( L.isEmpty() ) return R;
        else if ( R.isEmpty() )
            return L;

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

        copyLefts( M, iL );
        copyLefts( M, iR );
        return M;
    }

    public static
    void copyLefts( MyLinkedList<ListNode> M,
                    ListIterator<ListNode> i ) {
        while ( i.hasNext() )
            M.add( i.next() );
    }

    public static
    List<MyLinkedList<ListNode>> convertToList( ListNode[] L ) {
        List<MyLinkedList<ListNode>> Ls = new ArrayList<>( L.length );
        for ( ListNode n : L ) {
            Ls.add( convertToList( n ) );
        }

        return Ls;
    }

    public static
    MyLinkedList<ListNode> convertToList( ListNode n ) {
        MyLinkedList<ListNode> l = new MyLinkedList<>();
        while ( n != null ) {
            l.add( n );
            n = n.next;
        }

        return l;
    }

    static
    void test1() {
        ListNode[] L = new ListNode[ 3 ];
        ListNode n1 = new ListNode( 1, new ListNode( 4, new ListNode( 5 ) ) );
        ListNode n2 = new ListNode( 1, new ListNode( 3, new ListNode( 4 ) ) );
        ListNode n3 = new ListNode( 2, new ListNode( 6 ) );

        L[ 0 ] = n1;
        L[ 1 ] = n2;
        L[ 2 ] = n3;

        System.out.println( new MergeKSortedLists().mergeKLists( L ).printAll() );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
