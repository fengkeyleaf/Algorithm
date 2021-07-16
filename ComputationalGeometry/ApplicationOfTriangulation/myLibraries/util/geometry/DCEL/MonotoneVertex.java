package myLibraries.util.geometry.DCEL;

/*
 * MonotoneVertex.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.Triangles;

/**
 * Data structure of Monotone Vertex
 * for partitioning monotone subpolygon
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class MonotoneVertex extends Vertex {
    public static final boolean LEFT_CHAIN_VERTEX = true;
    public static final boolean RIGHT_CHAIN_VERTEX = false;
    public boolean isLeftChainVertex;

    public VertexType vertexType;

    /**
     * enumerative Vertex Type for partitioning monotone subpolygons
     * */

    public enum VertexType {
        START, SPLIT, // 0, 1
        END, MERGE, // 2, 3
        REGULAR_LEFT, REGULAR_RIGHT, // 4
    }

    /**
     * constructs to create an instance of MonotoneVertex
     * */

    public MonotoneVertex( double x, double y ) {
        super( x, y );
    }

    /**
     * this vertex is a split or merge one
     * when the angle it forms is greater than pi.
     * */

    public boolean isSplitOrMergeVertex() {
        Vector prev = incidentEdge.prev.origin;
        Vector base = incidentEdge.origin;
        Vector next = incidentEdge.next.origin;
        return Triangles.areaTwo( prev, base, next ) < 0;
    }

    /**
     * are both vertices on the same monotone chain?
     * */

    public boolean isOnTheDifferentChain( MonotoneVertex vertex ) {
        return isLeftChainVertex != vertex.isLeftChainVertex;
    }

    public static
    void main( String[] args ) {
        Vector vector1 = new Vector( -5, -5 );
        Vector vector2 = new Vector( 0, 3 );
        Vector vector3 = new Vector( 2, -1 );
        System.out.println( Triangles.areaTwo( vector1, vector2, vector3 ) );
    }
}
