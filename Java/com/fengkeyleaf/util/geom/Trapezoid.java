package com.fengkeyleaf.util.geom;

/*
 * Trapezoid.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 10/19/2021$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of Trapezoid for point location
 *
 * Implementation idea is from the textbook:
 *
 * To represent a trapezoidal map, we could use the doubly-connected edge
 * list described in Chapter 2; after all, a trapezoidal map is a planar subdivision.
 * However, the special shape of the trapezoidal map makes it more convenient
 * to use a specialized structure. This structure uses the adjacency of trapezoids
 * to link the subdivision as a whole. There are records for all line segments and
 * endpoints of S, since they serve as leftp(Δ), rightp(Δ), top(Δ), and bottom(Δ).
 * Furthermore, the structure contains records for the trapezoids of T(S), but not
 * for edges or vertices of T(S). The record for a trapezoid Δ stores pointers to
 * top(Δ) and bottom(Δ), pointers to leftp(Δ) and rightp(Δ), and finally, pointers
 * to its at most four neighbors. Note that the geometry of a trapezoid Δ (that is,
 * the coordinates of its vertices) is not available explicitly. However, Δ is uniquely
 * defined by top(Δ), bottom(Δ), leftp(Δ), and rightp(Δ). This means that we can
 * deduce the geometry of Δ in constant time from the information stored for Δ.
 *
 * Note that for this part of code,
 * it's very difficult to understand them just by looking through it.
 * So my suggestions:
 * 1) draw the structure of trapezoidal map;
 * 2) be careful with direction when understanding and using methods,
 * specially ones redirecting neighbors
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// TODO: 2/20/2022 get rid of static methods, too many of them.
public class Trapezoid {
    private static int IDStatic = 0;
    // initializing ID, starting with 0
    public final int ID;
    // ID in the search structure as leaf node
    // in pre-traversal ordering, starting with 1
    public int leafID = -1;

    // pointer to the leaf node in SS
    SearchVertex vertex;

    Vector leftP;
    SearchVertex leftSear;
    Vector rightP;
    SearchVertex rightSear;

    Line top;
    Line bottom;

    // even though the effect of shear transformation,
    // a trapezoid has up to four neighbors according to
    // the definition of neighbor.
    Trapezoid upperLeftNeighbor;
    Trapezoid lowerLeftNeighbor;
    Trapezoid upperRightNeighbor;
    Trapezoid lowerRightNeighbor;

    /**
     * constructs to create an instance of Trapezoid
     */

    Trapezoid() {
        ID = IDStatic++;
    }

    Trapezoid( Vector leftP, Vector rightP, Line top, Line bottom ) {
        ID = IDStatic++;
        this.leftP = leftP;
        this.rightP = rightP;
        this.top = top;
        this.bottom = bottom;
    }

    // -------------------------------------------
    // p -> s, p -> q
    // -------------------------------------------

    /**
     * When doing left separation,
     * redirect left(origin) and right's upper right neighbors in this case
     *
     * left <- right
     */

    // be careful with direction when using this
    static
    void mergeUpperRight( Trapezoid left, Trapezoid right ) {
        left.upperRightNeighbor = right.upperRightNeighbor;
        if ( right.upperRightNeighbor != null ) {
            right.upperRightNeighbor.upperLeftNeighbor = left;
            assert right.upperRightNeighbor.top == right.top;
        }
    }

    // be careful with direction when using this
    static
    void setUppers( Trapezoid left, Trapezoid right ) {
        left.upperRightNeighbor = right;
        right.upperLeftNeighbor = left;
    }

    /**
     * connect Upper Right Neighbors when dong Left separation
     *
     * Left separation, partition the original one into left and right part,
     * but the left is inherited from the origin.
     *
     * origin => left <-> right
     * older is on the left, newer is on the right
     *
     * p -> s, p -> q
     */

    // be careful with direction when using this
    static
    void connectUpperRightNeighbors( Trapezoid newer, Trapezoid older ) {
        mergeUpperRight( newer, older );

        setUppers( older, newer );
        assert older.top == newer.top;
    }

    /**
     * When doing Left separation.
     * redirect left(origin) and right's lower right neighbors in this case
     *
     * Also, this belongs to Left separation.
     *
     * left <- right
     */

    // be careful with direction when using this
    static
    void mergeLowerRight( Trapezoid left, Trapezoid right ) {
        left.lowerRightNeighbor = right.lowerRightNeighbor;
        if ( right.lowerRightNeighbor != null ) {
            right.lowerRightNeighbor.lowerLeftNeighbor = left;
            assert right.lowerRightNeighbor.bottom == left.bottom;
        }
    }

    // be careful with direction when using this
    static
    void setLowers( Trapezoid left, Trapezoid right ) {
        left.lowerRightNeighbor = right;
        right.lowerLeftNeighbor = left;
    }

    /**
     * connect lower Right Neighbors when dong Left separation
     *
     * Left separation, partition the original one into left and right part,
     * but the left is inherited from the origin.
     *
     * origin(left) => left <-> right
     * older is on the left, newer is on the right
     *
     * p -> s, p -> q
     */

    // be careful with direction when using this
    static
    void connectLowerRightNeighbors( Trapezoid newer, Trapezoid older ) {
        mergeLowerRight( newer, older );

        setLowers( older, newer );
        assert older.bottom == newer.bottom;
    }

    // -------------------------------------------
    // q -> S
    // -------------------------------------------

    /**
     * When doing right separation,
     * redirect left and right(origin)'s upper left neighbors in this case
     *
     * left <- right
     */

    // be careful with direction when using this
    static
    void mergeUpperLeft( Trapezoid newer, Trapezoid older ) {
        newer.upperLeftNeighbor = older.upperLeftNeighbor;
        if ( older.upperLeftNeighbor != null ) {
            older.upperLeftNeighbor.upperRightNeighbor = newer;
            assert older.upperLeftNeighbor.top == newer.top;
        }
    }

    /**
     * connect upper left Neighbors when dong right separation
     *
     * right separation, partition the original one into left and right part,
     * but the right is inherited from the origin.
     *
     * origin(right) => left <-> right
     * older is on the right, newer is on the left
     *
     * q -> S
     */

    static
    void connectUpperLeftNeighbors( Trapezoid newer, Trapezoid older ) {
        mergeUpperLeft( newer, older );

        older.upperLeftNeighbor = newer;
        newer.upperRightNeighbor = older;
        assert older.top == newer.top;
    }

    /**
     * When doing right separation,
     * redirect left and right(origin)'s lower left neighbors in this case
     *
     * left <- right
     */

    // be careful with direction when using this
    static
    void mergeLowerLeft( Trapezoid newer, Trapezoid older ) {
        newer.lowerLeftNeighbor = older.lowerLeftNeighbor;
        if ( older.lowerLeftNeighbor != null ) {
            older.lowerLeftNeighbor.lowerRightNeighbor = newer;
            assert older.lowerLeftNeighbor.bottom == newer.bottom;
        }
    }

    /**
     * connect lower left Neighbors when dong right separation
     *
     * right separation, partition the original one into left and right part,
     * but the right is inherited from the origin.
     *
     * origin(right) => left <-> right
     * older is on the right, newer is on the left
     *
     * q -> S
     */

    // be careful with direction when using this
    static
    void connectLowerLeftNeighbors( Trapezoid newer, Trapezoid older ) {
        mergeLowerLeft( newer, older );

        older.lowerLeftNeighbor = newer;
        newer.lowerRightNeighbor = older;
        assert older.bottom == newer.bottom;
    }

    /**
     * connect left Neighbors when dong right separation
     *
     * right separation, partition the original one into left and right part,
     * but the right is inherited from the origin.
     *
     * origin(right) => left <-> right
     * older is on the right, newer is on the left
     *
     * q -> S
     */

    // be careful with direction when using this
    static
    void connectLefts( Trapezoid newer, Trapezoid older ) {
        connectUpperLeftNeighbors( newer, older );
        connectLowerLeftNeighbors( newer, older );
    }

    /**
     * connect Right Neighbors when dong Left separation
     *
     * Left separation, partition the original one into left and right part,
     * but the left is inherited from the origin.
     *
     * origin(left) => left <-> right
     * older is on the left, newer is on the right
     *
     * p -> s, p -> q
     */

    // be careful with direction when using this
    static
    void connectRights( Trapezoid newer, Trapezoid older ) {
        connectUpperRightNeighbors( newer, older );
        connectLowerRightNeighbors( newer, older );
    }

    /**
     * When trimming wall, we always merge right into left( whether top or bottom ),
     * so the left one should inherit the right's right neighbors,
     * as well as its rightP
     */

    // left <- right
    // be careful with direction when using this
    static
    void mergeRights( Trapezoid left, Trapezoid right ) {
        mergeUpperRight( left, right );
        mergeLowerRight( left, right );
        left.rightP = right.rightP;
    }

    /**
     * In the case where we partition the original trapezoid into two,
     * top and bottom, i.e. handing S,
     * the top should inherit the origin's top neighbors
     *
     * origin(top) => top <-> bottom
     */

    // be careful with direction when using this
    static
    void mergeUppers( Trapezoid left, Trapezoid right ) {
        mergeUpperLeft( left, right );
        mergeUpperRight( left, right );
    }

    /**
     * In the case where we partition the original trapezoid into two,
     * top and bottom, i.e. handing S,
     * the bottom should inherit the origin's bottom neighbors
     *
     * origin(top) => top <-> bottom
     */

    // be careful with direction when using this
    static
    void mergeLowers( Trapezoid left, Trapezoid right ) {
        mergeLowerLeft( left, right );
        mergeLowerRight( left, right );
    }

    // useless for now
    void setRightNeighbours( Trapezoid another ) {
        this.upperRightNeighbor = another.upperRightNeighbor;
        this.lowerRightNeighbor = another.lowerRightNeighbor;
    }

    void setLowerNeighbor( Trapezoid another ) {
        this.lowerLeftNeighbor = another.lowerLeftNeighbor;
        this.lowerRightNeighbor = another.lowerRightNeighbor;
    }

    void resetLowerNeighbor() {
        this.lowerLeftNeighbor = null;
        this.lowerRightNeighbor = null;
    }

    /**
     * get the intersection of top or bottom with left or right vertical line
     *
     * @param line box edge.
     */

    static
    Vector getIntersection( Line line, Vector point ) {
        assert !line.isVertical;
        double y = line.updateY( point.x );
        return new Vector( point.x, y );
    }

    /**
     * get DCEL representation of this trapezoid
     */

    Face getDCEL() {
        Vector topLeft = top.isVertical ? top.startPoint : getIntersection( top, leftP );
        Vector bottomLeft = bottom.isVertical ? bottom.startPoint : getIntersection( bottom, leftP );
        Vector topRight = top.isVertical ? top.endPoint : getIntersection( top, rightP );
        Vector bottomRight = bottom.isVertical ? bottom.endPoint : getIntersection( bottom, rightP );

        List<Vertex> vertices = new ArrayList<>();
        vertices.add( new Vertex( topRight ) );
        vertices.add( new Vertex( topLeft ) );
        vertices.add( new Vertex( bottomLeft ) );
        vertices.add( new Vertex( bottomRight ));

        return Polygons.getDCEL( vertices )[ 1 ];
    }

    public Line getTop() {
        return top;
    }

    public Line getBottom() {
        return bottom;
    }

    public Trapezoid getLowerLeftNeighbor() {
        return lowerLeftNeighbor;
    }

    public Trapezoid getLowerRightNeighbor() {
        return lowerRightNeighbor;
    }

    public Trapezoid getUpperLeftNeighbor() {
        return upperLeftNeighbor;
    }

    public Trapezoid getUpperRightNeighbor() {
        return upperRightNeighbor;
    }

    //-------------------------------------------------------
    // Check integrity of Trapezoid data structure.
    //-------------------------------------------------------

    /**
     * check to see if this trapezoid is valid
     */

    boolean check() {
        // !leftP.equals( rightP )
        assert !( Vectors.sortByX( leftP, rightP ) == 0 );
        // leftP.isRight( rightP )
        assert Vectors.sortByX( leftP, rightP ) < 0 : this;
        assert top != bottom;
        assert !Triangles.toLeftRigorously( top.startPoint, top.endPoint, leftP );
        assert Triangles.toLeft( bottom.startPoint, bottom.endPoint, leftP ) : this;
        assert !Triangles.toLeftRigorously( top.startPoint, top.endPoint, rightP ) : this;
        assert Triangles.toLeft( bottom.startPoint, bottom.endPoint, rightP ) : this;

        boolean res = true;
        if ( upperLeftNeighbor != null ) {
            assert upperLeftNeighbor != this : "upperLeftNeighbor\n" + this;
            assert upperLeftNeighbor.top == top : "upperLeftNeighbor\n" + top + " " + upperLeftNeighbor.top + "\n" + this + "\n" + upperLeftNeighbor;
            assert upperLeftNeighbor.rightP.equals( leftP ) :  "upperLeftNeighbor\n" + leftP + " " + upperLeftNeighbor.rightP + "\n" + this + "\n" + upperLeftNeighbor;
        }

        if ( lowerLeftNeighbor != null ) {
            assert lowerLeftNeighbor != this: "lowerLeftNeighbor\n" + this;
            assert lowerLeftNeighbor.bottom == bottom : "lowerLeftNeighbor\n" + bottom + " " + lowerLeftNeighbor.bottom + "\n" + this + "\n" + lowerLeftNeighbor;
            assert lowerLeftNeighbor.rightP.equals( leftP ) : "lowerLeftNeighbor\n" + leftP + " " + lowerLeftNeighbor.rightP + "\n" + this + "\n" + lowerLeftNeighbor;
        }

        if ( upperRightNeighbor != null ) {
            assert upperRightNeighbor != this : "upperRightNeighbor\n" + this;
            assert upperRightNeighbor.top == top : "upperRightNeighbor\n" + bottom + " " + upperRightNeighbor.top + "\n" + this + "\n" + upperRightNeighbor;
            assert upperRightNeighbor.leftP.equals( rightP ) : "upperRightNeighbor\n" + rightP + " " + upperRightNeighbor.leftP + "\n" + this + "\n" + upperRightNeighbor;
        }

        if ( lowerRightNeighbor != null ) {
            assert lowerRightNeighbor != this:  "lowerRightNeighbor\n" + this;
            assert lowerRightNeighbor.bottom == bottom : "lowerRightNeighbor\n" + bottom + " " + lowerRightNeighbor.bottom + "\n" + this + "\n" + lowerRightNeighbor;
            assert lowerRightNeighbor.leftP.equals( rightP ) : "lowerRightNeighbor\n" + rightP + " " + lowerRightNeighbor.leftP + " " + "\n" + this + "\n" + lowerRightNeighbor;
        }

        return res;
    }

    /**
     * clone a new trapezoid with the same four data fields of this one:
     * leftP, rightP, top, bottom
     */

    @Override
    public Object clone() throws CloneNotSupportedException {
        Trapezoid trapezoid = new Trapezoid();
        trapezoid.leftP = leftP;
        trapezoid.rightP = rightP;
        trapezoid.top = top;
        trapezoid.bottom = bottom;
        return trapezoid;
    }

    @Override
    public String toString() {
        return ID + "-> leftP:" + leftP + " | " + "rightP: " + rightP + "\n" + "top: " + top + " | bottom: " + bottom;
    }
}
