package myLibraries.util.geometry.elements.point;

/*
 * SimplePoint2D.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

/**
 *
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class SimplePoint2D extends Vector {
    public final SimplePoint2D[] supportingEdges = new SimplePoint2D[ 2 ];
    public final SimplePoint2D successor;

    /**
     * constructs to create an instance of Matrix
     * */

    public SimplePoint2D( int x, int y, int ID,
                          SimplePoint2D successor ) {
        super( x, y, ID );
        this.successor = successor;
    }
}
