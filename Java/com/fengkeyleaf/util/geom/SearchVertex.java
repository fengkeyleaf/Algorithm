package com.fengkeyleaf.util.geom;

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

import com.fengkeyleaf.util.graph.Vertex;

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
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class SearchVertex extends Vertex {
    private static int IDStatic = 0;
    public int matrixIndex = -1;
    // have parent pointer point to its parent nodes,
    // use list, public final List<Vertex> neighbours in class Vertex,
    // since may have more than one parents; merge vertex with fewer parents
    final List<Vertex> parents = neighbours;
    // e.g. P1, Q1, S1, T1, ...
    public String name;

    NodeType type;
    Vector point;
    Line line;
    Trapezoid trapezoid;

    public enum NodeType {
        X_POINT_P, X_POINT_Q, // X node
        SEGMENT, // Y node
        TRAPEZOID, // leaf node
        TRIMMING
    }

    SearchVertex left;
    SearchVertex right;

    // trimming part
    boolean isTrimmingTop;
    Trapezoid top;
    Trapezoid bottom;

    // Adjacency Matrix part
    // vertices with the same content
    public final List<SearchVertex> duplicates = new ArrayList<>();

    /**
     * constructs to create an instance of Vertex
     * */

    SearchVertex( NodeType type, Vector p, Line line ) {
        super( IDStatic++ );
        this.type = type;
        point = p;
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

    SearchVertex( NodeType type ) {
        super( IDStatic++ );
        this.type = type;
        name = type.toString();
    }

    SearchVertex( Trapezoid trapezoid ) {
        super( IDStatic++ );
        type = NodeType.TRAPEZOID;
        this.trapezoid = trapezoid;
        this.trapezoid.vertex = this;
        name = "T" + ( trapezoid.ID + 1 );
    }

    SearchVertex( Line line ) {
        super( IDStatic++ );
        type = NodeType.SEGMENT;
        this.line = line;
        name = "S" + ( line.ID + 1 );
    }

    public NodeType getType() {
        return type;
    }

    public Vector getPoint() {
        return point;
    }

    public Line getLine() {
        return line;
    }

    public Trapezoid getTrapezoid() {
        return trapezoid;
    }

    public SearchVertex getLeft() {
        return left;
    }

    public SearchVertex getRight() {
        return right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
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
