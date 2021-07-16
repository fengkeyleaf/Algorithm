package myLibraries.util.geometry.DCEL;

/*
 * Vertex.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.elements.point.Vector;

/**
 * Data structure of vertex for DCEL
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Vertex extends Vector {
    private static int IDStatic = 0;
    public HalfEdge incidentEdge;

    /**
     * constructs to create an instance of Vertex
     * */

    public Vertex( double x, double y ) {
        super( x, y, IDStatic++ );
    }

    public static
    void main( String[] args ) {
        Vertex vertex = new Vertex( 1, 1 );
        vertex.mappingID = 1;
    }
}
