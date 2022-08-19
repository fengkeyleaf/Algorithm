package coding.leetcode.medium;

/*
 * DesignLinkedList.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/25/2022$
 */

import com.fengkeyleaf.util.MyArrays;
import com.fengkeyleaf.util.MyLinkedList;

/**
 * <a href="https://leetcode.com/problems/design-linked-list/">707. Design Linked List</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

/*
 * Your MyLinkedList object will be instantiated and called as such:
 * MyLinkedList obj = new MyLinkedList();
 * int param_1 = obj.get(index);
 * obj.addAtHead(val);
 * obj.addAtTail(val);
 * obj.addAtIndex(index,val);
 * obj.deleteAtIndex(index);
 */

public final class DesignLinkedList {

    final MyLinkedList<Integer> L = new MyLinkedList<>();

    public DesignLinkedList() {}

    public int get( int index ) {
        if ( MyArrays.isOutOfIndex( index, L.size() ) )
            return -1;

        return L.get( index );
    }

    public void addAtHead( int val ) {
        L.addFirst( val );
    }

    public void addAtTail( int val ) {
        L.addLast( val );
    }

    public void addAtIndex( int index, int val ) {
        if ( index == L.size() ) {
            L.add( val );
            return;
        } else if ( index > L.size() )
            return;

        L.add( index, val );
    }

    public void deleteAtIndex( int index ) {
        if ( MyArrays.isOutOfIndex( index, L.size() ) )
            return;

        L.remove( index );
    }

    static
    void test1() {
        DesignLinkedList L = new DesignLinkedList();
        L.addAtHead( 1 );
        L.addAtTail( 3 );
        L.addAtIndex( 3, 2 );
    }
}
