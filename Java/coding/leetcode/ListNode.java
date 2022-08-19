package coding.leetcode;

/*
 * ListNode.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/22/2022$
 */

/**
 * Definition for singly-linked list.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public class ListNode {
    public int val;
    public ListNode next;

    public ListNode() {
    }

    public ListNode( int val ) {
        this.val = val;
    }

    public ListNode( int val, ListNode next ) {
        this.val = val;
        this.next = next;
    }

    public String printAll() {
        StringBuilder t = new StringBuilder();
        ListNode n = this;
        while ( n != null ) {
            t.append( n.val ).append( " " );
            n = n.next;
        }

        return t.toString();
    }

    @Override
    public String toString() {
        return String.valueOf( val );
    }
}
