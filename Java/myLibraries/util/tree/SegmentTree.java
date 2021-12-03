package myLibraries.util.tree;

/*
 * SegmentTree.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.CompareElement;
import myLibraries.util.MyArrays;
import myLibraries.util.tree.elements.SegmentNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Data structure of SegmentTree with generics.
 *
 * Support two kinds of RMQ,
 * range minimum query and range maximum query
 *
 * Application: Windowing Query in computational geometry
 * @see <a href=https://www.edx.org/course/computational-geometry>Windowing Query</a>
 *
 * Related POJ problem:
 * @see <a href=http://poj.org/problem?id=2991>Crane</a>
 *
 * Reference resource:
 * Programming Contest Challenge Book, The Second edition
 * Authors: Takuya Akiba, Yoichi Iwata, Masatoshi Kitagawa
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class SegmentTree<E> extends PerfectBinaryTree<SegmentNode<E>> {
    private final int originSize;
    // power of 2 for the size of elements
    private final int n;
    // comparator to compare element, E
    protected final Comparator<E> comparator;

    /**
     * constructs to create an instance of SegmentTree
     * */

    public SegmentTree( List<E> elements ) {
        this( elements, null );
    }

    public SegmentTree( List<E> elements, Comparator<E> comparator ) {
        // total number of elements in this heap tree
        super( grow( elements.size() ) * 2 - 1 );

        if ( elements.isEmpty() )
            throw new RuntimeException( "Empty initialization in SegmentTree" );

        originSize = elements.size();
        n = grow( elements.size() );

        // initializing capacity, mainly for segment tree
        int capacity = n * 2 - 1;
        for ( int i = 0; i < capacity; i++ )
            tree.add( null );

        this.comparator = comparator;
        initialize( elements, 0, 0, n );
    }

    /**
     * make the size of heap tree the power of 2
     *
     * e.g.
     * 5, 3, 7 -> 5, 3, 7, null
     * */

    private static
    int grow( int capacity ) {
        int n = 1;
        while ( n < capacity ) n <<= 1;

        return n;
    }

    /**
     * initialize this Segment Tree
     *
     * @param left   inclusive
     * @param right  exclusive
     * */

    private void initialize( List<E> elements, int index, int left, int right ) {
        // base cases
        if ( left >= elements.size() ) {
            tree.set( index, new SegmentNode<E>( null ) );
            return;
        }
        // one element left
        if ( left + 1 >= right ) {
            assert left + 1 == right;
            tree.set( index, new SegmentNode<E>( elements.get( left ) ) );
            return;
        }

        // set this interval node
        List<E> minMax = CompareElement.minMax( comparator, elements,
                left, Math.min( right, elements.size() ) );
        tree.set( index, new SegmentNode<>( minMax.get( 0 ), minMax.get( 1 ) ) );

        // set its left and right children
        int mid = MyArrays.mid( left, right );
        initialize( elements, getChildrenIndex( index, true ), left, mid );
        initialize( elements, getChildrenIndex( index, false ), mid, right );
    }

    /**
     * update while handling null elements
     * */

    private void update( SegmentNode<E> node,
                         SegmentNode<E> left, SegmentNode<E> right ) {
        assert left.max != null || right.max != null;
        if ( left.max == null ) {
            node.min = right.min;
            node.max = right.max;
        }
        else if ( right.max == null ) {
            node.min = left.min;
            node.max = left.max;
        }
        else {
            node.min = CompareElement.min( comparator,
                    left.min, right.min );
            node.max = CompareElement.max( comparator,
                    left.max, right.max );
        }
    }

    /**
     * set the element at index to the new one
     * */

    public void update( int index, E element ) {
        if ( MyArrays.isOutOfIndex( index, originSize ) )
            return;

        // get the index of leaf node in the heap tree
        index += n - 1;
        // update the leaf node
        SegmentNode<E> node = tree.get( index );
        node.min = element;
        node.max = element;

        // bottom-up updating
        while ( index > 0 ) {
            index = getParentIndex( index );
            node = tree.get( index );
            SegmentNode<E> left = tree.get( getChildrenIndex( index, true ) );
            SegmentNode<E> right = tree.get( getChildrenIndex( index, false ) );

            update( node, left, right );
        }
    }

    /**
     * range minimum query and range maximum query
     *
     * @param  r     exclusive
     * */

    private E query( int a, int b, int k,
                     int l, int r, boolean isMin ) {
        // base cases
        // 1. not overlapping intervals, so no result
        if ( r <= a || b <= l )
            return null;
        // 2. target interval fully covers the current interval,
        // return the result
        if ( a <= l && r <= b )
            return isMin ? tree.get( k ).min : tree.get( k ).max;

        // query left and right children
        int mid = MyArrays.mid( l, r );
        E left = query( a, b, getChildrenIndex( k, true ),
                l, mid, isMin );
        E right = query( a, b, getChildrenIndex( k, false ),
                mid, r, isMin );

        // return the right result
        if ( left == null ) return right;
        else if ( right == null ) return left;

        return isMin ? CompareElement.min( comparator, left, right ) :
                CompareElement.max( comparator, left, right );
    }

    /**
     * range minimum query and range maximum query
     *
     * @param  isMin     true, minimum query; false maximum query
     * */

    public E query( int leftTarget, int rightTarget, boolean isMin ) {
        return MyArrays.isOutOfIndex( leftTarget, rightTarget - 1, originSize ) ? null :
                query( leftTarget, rightTarget, 0, 0, n, isMin );
    }

    private static
    void testOne() {
        int[] nums = { 5, 3, 7, 9, 6, 4, 1, 2 };
        List<Integer> numList = new ArrayList<>();
        for ( int num : nums )
            numList.add( num );

        SegmentTree<Integer> segmentTree = new SegmentTree<>( numList );
        System.out.println( segmentTree );

        // query test
        System.out.println( segmentTree.query( 0, 2, true ) ); // 3
        System.out.println( segmentTree.query( 0, 4, true ) ); // 3
        System.out.println( segmentTree.query( 2, 6, true ) ); // 4
        System.out.println( segmentTree.query( 4, 8, true ) ); // 1
        System.out.println( segmentTree.query( -1, 8, true ) ); // null

        System.out.println();
        System.out.println( segmentTree.query( 0, 2, false ) ); // 5
        System.out.println( segmentTree.query( 0, 4, false ) ); // 9
        System.out.println( segmentTree.query( 2, 6, false ) ); // 9
        System.out.println( segmentTree.query( 4, 8, false ) ); // 6
        System.out.println( segmentTree.query( 4, 152, false ) ); // null

        System.out.println();
        // update test
        segmentTree.update( 0, 2 );
        System.out.println( segmentTree );
        segmentTree.update( 3, 15 );
        System.out.println( segmentTree );
        segmentTree.update( 15, 15 );
        segmentTree.update( -5, 15 );
        System.out.println( segmentTree );
    }

    private static
    void testTwo() {
        int[] nums = {};
        List<Integer> numList = new ArrayList<>();

        SegmentTree<Integer> segmentTree = new SegmentTree<>( numList );
        System.out.println( segmentTree );
    }

    private static
    void testThree() {
        int[] nums = { 1, 2, 3 };
        List<Integer> numList = new ArrayList<>();
        for ( int num : nums )
            numList.add( num );

        SegmentTree<Integer> segmentTree = new SegmentTree<>( numList );
        System.out.println( segmentTree );

        // query test
        System.out.println( segmentTree.query( 0, 2, true ) ); // 1
        System.out.println( segmentTree.query( 1, 3, true ) ); // 2
        System.out.println( segmentTree.query( 0, 3, true ) ); // 1
        System.out.println( segmentTree.query( 0, 4, true ) ); // null

        System.out.println();
        System.out.println( segmentTree.query( 0, 2, false ) ); // 2
        System.out.println( segmentTree.query( 1, 3, false ) ); // 3
        System.out.println( segmentTree.query( 0, 3, false ) ); // 3

        System.out.println();
        // update test
        segmentTree.update( 0, 2 );
        System.out.println( segmentTree );
        segmentTree.update( 2, 19 );
        System.out.println( segmentTree );
        segmentTree.update( 3, 19 );
        System.out.println( segmentTree );
    }

    private static
    void testFour() {
        int[] nums = { 1, 2, 3, 4, 5, 6 };
        List<Integer> numList = new ArrayList<>();
        for ( int num : nums )
            numList.add( num );

        SegmentTree<Integer> segmentTree = new SegmentTree<>( numList );
        System.out.println( segmentTree );

        System.out.println( segmentTree.query( 0, 1, true ) ); // 1

        segmentTree.update( 0, 5 );
        System.out.println( segmentTree );
    }

    private static
    void testFive() {
//        int[] nums = { 1 };
        int[] nums = { 1, 2 };
        List<Integer> numList = new ArrayList<>();
        for ( int num : nums )
            numList.add( num );

        SegmentTree<Integer> segmentTree = new SegmentTree<>( numList );
        System.out.println( segmentTree );

        System.out.println( segmentTree.query( 0, 1, true ) ); // 1

        segmentTree.update( 0, 5 );
        System.out.println( segmentTree );
    }

    public static
    void main( String[] args ) {
//        testOne();
//        testTwo();
//        testThree();
        testFour();
//        testFive();
    }
}
