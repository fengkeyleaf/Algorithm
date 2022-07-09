package com.fengkeyleaf.util.tree;

/*
 * MemorizedPriorityQueue.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.util.graph.ShortestVertex;

import java.util.Collections;
import java.util.Comparator;

/**
 * Data structure of a Memorized Heap implemented with array.
 * The difference from the MyPriorityQueue Class is that
 * every element in this heap has the index at the heap array
 * so as to achieve change change-key operation in O(logn)
 *
 * For now, don't support generic.
 * In order to do so, suggest look into
 * the implementation of PriorityQueue Class
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// TODO: 8/6/2021 not fix
public class MemorizedPriorityQueue extends MyPriorityQueue<ShortestVertex> {
    public MemorizedPriorityQueue( Comparator<ShortestVertex> comparator ) {
        super( comparator );
    }

    /**
     * do bubble up
     * */

    protected int bubbleUp( int currentIndex ) {
        int parentIndex = getParentIndex( currentIndex );

        // stop bubbling up when reaching root
        while ( currentIndex > 0 ){
            if ( compareElement( parentIndex, currentIndex ) < 0 ) {
                Collections.swap( tree, currentIndex, parentIndex );
                tree.get( parentIndex ).indexAtHeap = parentIndex;
                tree.get( currentIndex ).indexAtHeap = currentIndex;
            }
            else
                break;

            currentIndex = parentIndex;
            parentIndex = getParentIndex( currentIndex );
        }

        return currentIndex;
    }

    /**
     * insert an element into this heap
     * */

    public boolean insert( ShortestVertex data ) {
        if ( tree.isEmpty() ) {
            tree.add( data );
            data.indexAtHeap = tree.size() - 1;
            return true;
        }

        tree.add( data );
        data.indexAtHeap = tree.size() - 1;
        bubbleUp( tree.size() - 1 );
//        return bubbleUp( tree.size() - 1 );
        return true;
    }

    /**
     * do bubble down
     * */

    protected int bubbleDown( int parentIndex ) {
        int leftChildIndex = getChildrenIndex( parentIndex, true );
        int rightChildIndex = getChildrenIndex( parentIndex, false );

        // stop bubbling down if a parent doesn't have any children
        while ( leftChildIndex < tree.size() ||
                rightChildIndex < tree.size() ) {
            int swapIndex = ifBubbleDown( parentIndex, leftChildIndex, rightChildIndex );
            if ( swapIndex > -1 ) {
                Collections.swap( tree, parentIndex, swapIndex );
                tree.get( parentIndex ).indexAtHeap = parentIndex;
                tree.get( swapIndex ).indexAtHeap = swapIndex;
                parentIndex = swapIndex;
            }
            else
                break;

            leftChildIndex = getChildrenIndex( parentIndex, true );
            rightChildIndex = getChildrenIndex( parentIndex, false );
        }

        return parentIndex;
    }

    /**
     * delete an element from this heap
     * */

    public ShortestVertex delete() {
        if ( tree.isEmpty() ) return null;

        Collections.swap( tree, 0, tree.size() - 1 );
        tree.get( 0 ).indexAtHeap = 0;
        tree.get( tree.size() - 1 ).indexAtHeap = tree.size() - 1;

        ShortestVertex removedData = tree.remove( tree.size() - 1 );

        bubbleDown( 0 );
        return removedData;
    }

    public int changeKey( ShortestVertex currentVertex, ShortestVertex original ) {
        int finalIndex = -1;

        int currentIndex = currentVertex.indexAtHeap;
//        if ( extractMax ) {
//            if ( original.compareTo( currentVertex ) > 0 )
//                finalIndex = bubbleDown( currentIndex );
//            else if ( original.compareTo( currentVertex ) < 0 )
//                finalIndex = bubbleUp( currentIndex );
//        }
//        else {
//            if ( original.compareTo( currentVertex ) > 0 )
//                finalIndex = bubbleUp( currentIndex );
//            else if ( original.compareTo( currentVertex ) < 0 )
//                finalIndex = bubbleDown( currentIndex );
//        }

        return finalIndex < 0 ? currentIndex : finalIndex;
    }

    public static void main( String[] args ) {
        int ID = 0;
        ShortestVertex vertex1 = new ShortestVertex( ID++, 0 );
        ShortestVertex vertex2 = new ShortestVertex( ID++, 1 );
        ShortestVertex vertex3 = new ShortestVertex( ID++, 2 );
        ShortestVertex vertex4 = new ShortestVertex( ID++, 4 );

        System.out.println("\nExtract Max------------>");
        MemorizedPriorityQueue heapArrayMax = new MemorizedPriorityQueue( null );
        heapArrayMax.insert( vertex1 );
        heapArrayMax.insert( vertex2 );
        heapArrayMax.insert( vertex3 );
        heapArrayMax.insert( vertex4 );

//        MyPriorityQueue.deleteNodes( heapArrayMax );

//        vertex2.currentShortestDistance = 6;
//        heapArrayMax.changeKey( vertex2.indexAtHeap, new ShortestVertex( -1, 1 ) );

//        vertex3.currentShortestDistance = -1;
//        heapArrayMax.changeKey( vertex3.indexAtHeap, new ShortestVertex( -1, 2 ) );

        System.out.println("\nExtract Min------------>");
        MemorizedPriorityQueue heapArrayMin = new MemorizedPriorityQueue( null );
        heapArrayMin.insert( vertex1 );
        heapArrayMin.insert( vertex2 );
        heapArrayMin.insert( vertex3 );
        heapArrayMin.insert( vertex4 );

//        MyPriorityQueue.deleteNodes( heapArrayMin );

//        vertex2.currentShortestDistance = -1;
//        heapArrayMin.changeKey( vertex2.indexAtHeap, new ShortestVertex( -1, 1 ) );

        vertex2.currentShortestDistance = 6;
        heapArrayMin.changeKey( vertex2, new ShortestVertex( -1, 1 ) );
    }
}
