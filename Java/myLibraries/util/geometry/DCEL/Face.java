package myLibraries.util.geometry.DCEL;

/*
 * Face.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of Face of DCEL
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Face {
    private static int IDStatic = 0;
    public final int ID;
    public HalfEdge outComponent;
    public final List<HalfEdge> innerComponents = new ArrayList<>();
    // half-edge that the infinite face only has.
    private HalfEdge infiniteBoundary;

    public int IDOfDualVertex;

    /**
     * constructs to create an instance of Vertex
     * */

    public Face() { ID = IDStatic++; }

    public Face( HalfEdge outComponent ) {
        this.outComponent = outComponent;
        ID = IDStatic++;
    }

    public static
    void resetIDStatic() {
        IDStatic = 0;
    }

    public void addInnerComponent( HalfEdge halfEdge ) {
        innerComponents.add( halfEdge );
    }
}
