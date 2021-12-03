package myLibraries.util.graph.elements;

/*
 * SearchVertex.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 10/19/2021$
 */

import myLibraries.util.geometry.elements.Trapezoid;
import myLibraries.util.geometry.elements.line.Line;
import myLibraries.util.geometry.elements.point.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of SearchVertex for point location.
 *
 * In summary, this class servers mainly two purposes:
 * 1) x node(point), y node(segment) and leaf node(trapezoid) for the search structure.
 * 2) store top and bottom trapezoids for later merging, i.e. trimming walls
 *
 * Note that for leaf nodes(trapezoid),
 * not only their parents have pointers pointing to them,
 * presenting as left and right,
 * but also they have pointers pointing to their parents,
 * presenting as a parents array,
 * which gives us the ability to guide
 * several parents to point to the same child at constant time.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public class SearchVertex extends Vertex {
    private static int IDStatic = 0;
    public int matrixIndex = -1;
    // have parent pointer point to its parent nodes,
    // use list, public final List<Vertex> neighbours in class Vertex,
    // since may have more than one parents; merge vertex with fewer parents
    public final List<Vertex> parents = super.neighbours;
    // e.g. P1, Q1, S1, T1, ...
    public String name;

    public NodeType type;
    public Vector point;
    public Line line;
    public Trapezoid trapezoid;

    public enum NodeType {
        X_POINT_P, X_POINT_Q, // X node
        SEGMENT, // Y node
        TRAPEZOID, // leaf node
        TRIMMING
    }

    public SearchVertex left;
    public SearchVertex right;

    // trimming part
    public boolean isTrimmingTop;
    public Trapezoid top;
    public Trapezoid bottom;

    // Adjacency Matrix part
    // vertices with the same content
    public final List<SearchVertex> duplicates = new ArrayList<>();

    /**
     * constructs to create an instance of Vertex
     * */

    public SearchVertex() {
        super( IDStatic++ );
        name = "";
    }

    public SearchVertex( int ID ) {
        super( ID );
        name = "";
    }

    public SearchVertex( int ID, Vertex predecessor ) {
        super( ID, predecessor );
        name = "";
    }

    public SearchVertex( NodeType type, Vector p, Line line ) {
        super( IDStatic++ );
        this.type = type;
        this.point = p;
        this.line = line;

        switch ( type ) {
            case X_POINT_P -> name = "P" + ( line.ID + 1 );
            case X_POINT_Q -> name = "Q" + ( line.ID + 1 );
            default -> {
                assert false;
                name = "";
            }
        }
    }

    public SearchVertex( NodeType type ) {
        super( IDStatic++ );
        this.type = type;
        name = type.toString();
    }

    public SearchVertex( NodeType type, SearchVertex left, SearchVertex right ) {
        super( IDStatic++ );
        this.type = type;
        this.left = left;
        this.right = right;
        name = "";
    }

    public SearchVertex( Trapezoid trapezoid ) {
        super( IDStatic++ );
        this.type = NodeType.TRAPEZOID;
        this.trapezoid = trapezoid;
        this.trapezoid.vertex = this;
        this.name = "T" + ( trapezoid.ID + 1 );
    }

    public SearchVertex( Line line ) {
        super( IDStatic++ );
        this.type = NodeType.SEGMENT;
        this.line = line;
        this.name = "S" + ( line.ID + 1 );
    }

    /**
     * ascending order
     * */

    public static
    int sortByLineID( SearchVertex v1, SearchVertex v2 ) {
        return Integer.compare( v1.line.ID, v2.line.ID );
    }

    public static
    int sortByTrapezoidID( SearchVertex v1, SearchVertex v2 ) {
        return Integer.compare( v1.trapezoid.leafID, v2.trapezoid.leafID );
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public boolean check() {
        if ( trapezoid != null )
            return trapezoid.check();

        return true;
    }

    @Override
    public String toString() {
        String text = "";

        switch ( type ) {
            case X_POINT_P -> text = "PointP: " + line.ID + "-> " + point;
            case X_POINT_Q -> text = "PointQ: " + line.ID + "-> " + point;
            case SEGMENT -> text = "Segment: " + line.ID + "-> " + line;
            case TRAPEZOID -> text = "Trapezoid: " + trapezoid;
            default -> text = "root";
        }

        return text;
    }
}
