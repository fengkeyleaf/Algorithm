package com.fengkeyleaf.util.geom;

/*
 * BoundingBox.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 12/30/2021$
 */

import com.fengkeyleaf.lang.MyMath;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A typical bounding box:
 *
 *              top
 *      <------------------
 *      |                 ^
 *      |                 |
 *      |                 |
 * left |                 | right
 *      |                 |
 *      |                 |
 *      v                 |
 *      ------------------>
 *             bottom
 *
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class BoundingBox {
    public static final int OFFSET = 10;

    // infinite face for this bounding box.
    public final Face outer;

    // counter-clock wise order:
    // top -> left -> bottom -> right.
    public final List<HalfEdge> edges = new ArrayList<>( 4 );
    public final HalfEdge top;
    public final HalfEdge bottom;
    public final HalfEdge left;
    public final HalfEdge right;

    // half-edge components for the four boundaries.
    final LinkedList<HalfEdge> topEdges = new LinkedList<>();
    final LinkedList<HalfEdge> bottomEdges = new LinkedList<>();
    final LinkedList<HalfEdge> leftEdges = new LinkedList<>();
    final LinkedList<HalfEdge> rightEdges = new LinkedList<>();

    public final double minX;
    public final double minY;
    public final double maxX;
    public final double maxY;

    public final Vector bottomLeft;
    public final Vector bottomRight;
    public final Vector topRight;
    public final Vector topLeft;

    // may be useless
    public final int width;
    public final int height;

    // data out of scope of the box itself.
    // Voronoi Vertices for Voronoi Diagrams.
    public List<VoronoiVertex> vertices;
    // Voronoi edges for Voronoi Diagrams.
    List<HalfEdge> vorEdges;
    // Search Structure for Point Location.
    public SearchStructure SS;
    // Faces contained in this bounding box.
    // This face set is typically assigned when we know all inner face in advance
    // without traversing the DCEL structure. e.g. Voronoi Diagrams.
    public List<Face> F;

    /**
     * Constructs to create an instance of BoundingBox
     */

    BoundingBox( Vector topRight, Vector topLeft,
                 Vector bottomLeft, Vector bottomRight ) {
        List<Vertex> vertices = getVertices( topRight, topLeft, bottomLeft, bottomRight );

        Face[] faces = Polygons.getDCEL( vertices );
        outer = faces[ 0 ];

        edges.add( top = faces[ 1 ].outComponent );
        edges.add( left = top.next );
        edges.add( bottom = left.next );
        edges.add( right = bottom.next );

        topEdges.add( top );
        bottomEdges.add( bottom );
        leftEdges.add( left );
        rightEdges.add( right );

        width = ( int ) Math.abs( topLeft.x - topRight.x );
        height = ( int ) Math.abs( topLeft.y - bottomLeft.y );

        maxX = top.origin.x;
        maxY = top.origin.y;
        minX = bottom.origin.x;
        minY = bottom.origin.y;

        // use DCEL Vertex, not Vector.
        this.bottomLeft = bottom.origin;
        this.topRight = top.origin;
        this.bottomRight = right.origin;
        this.topLeft = left.origin;
    }

    BoundingBox( Vector bottomLeft, Vector topRight ) {
        this( topRight, new Vector( bottomLeft.x, topRight.y ),
                bottomLeft, new Vector( topRight.x, bottomLeft.y ) );
    }

    /**
     * get a bounding box only with the infinite face.
     * i.e. the box doesn't have to be a regular quad.
     * */

    BoundingBox( Face outer ) {
        this.outer = outer;
        top = bottom = left = right = null;
        minX = minY = maxX = maxY = 0;
        bottomLeft = bottomRight = topRight = topLeft = null;
        width = height = 0;
    }

    private List<Vertex> getVertices( Vector topRight, Vector topLeft,
                                      Vector bottomLeft, Vector bottomRight ) {
        List<Vertex> vertices = new ArrayList<>( 4 );
        vertices.add( new Vertex( topRight ) );
        vertices.add( new Vertex( topLeft ) );
        vertices.add( new Vertex( bottomLeft ) );
        vertices.add( new Vertex( bottomRight ) );

        return vertices;
    }

    private static
    Vector[] getFourPoints( double minX, double maxX, double minY, double maxY ) {
        Vector[] points = new Vector[ 4 ];
        points[ 0 ] = new Vector( maxX, maxY ); // topRight
        points[ 1 ] = new Vector( minX, maxY ); // topLeft
        points[ 2 ] = new Vector( minX, minY ); // bottomLeft
        points[ 3 ] = new Vector( maxX, minY ); // bottomRight

        return points;
    }

    public Vertex splitTop( Line line ) {
        return split( top, topEdges, line );
    }

    public Vertex splitBottom( Line line ) {
        return split( bottom, bottomEdges, line );
    }

    Vertex split( HalfEdge boxEdge, Vector intersection ) {
        return splitCommon( split( boxEdge ), intersection );
    }

    private static
    Vertex split( HalfEdge boxEdge, LinkedList<HalfEdge> edges, Line line ) {
        Vector intersection = boxEdge.getSegment().intersect( line )[ 0 ];

        assert intersection != null;
        return splitCommon( edges, intersection );
    }

    /**
     * split which box half-edges?
     * Notice that a box edge may be split into several parts,
     * we need to find which one to split.
     * */

    private LinkedList<HalfEdge> split( HalfEdge boxEdge ) {
        if ( boxEdge == top ) return topEdges;
        else if ( boxEdge == bottom ) return bottomEdges;
        else if ( boxEdge == left ) return leftEdges;

        assert boxEdge == right;
        return rightEdges;
    }

    /**
     * split which a box half-edge among the list of box edges.
     * */

    private static
    Vertex splitCommon( LinkedList<HalfEdge> edges, Vector intersection ) {
        // TODO: 2/2/2022 XXX: brute force, but could take advantage of BBST
        Vertex split = null;
        for ( HalfEdge edge : edges ) {
            if ( edge.getSegment().isOnThisSegment( intersection ) ) {
                split = edge.split( intersection );
                edges.add( edges.indexOf( edge ), edge.next );
                break;
            }
        }

        return split;
    }

    /**
     * the point, p, is on this bounding box?
     * ( including lying on its boundary. )
     * */

    // TODO: 2/11/2022 extract a method for this one. i.e. use outer face to see if a point lies inside it.
    boolean isOnThisBox( Vector p ) {
        HalfEdge edge = top.twin;

        do {
            if ( Triangles.toLeftRigorously( edge.origin, edge.next.origin, p ) )
                return false;

            assert edge.incidentFace == top.twin.incidentFace;
            edge = edge.next;
        } while ( edge != top.twin );

        return true;
    }

    public int findVisualizationArea() {
        return findVisualizationArea( 0 );
    }

    // TODO: 2/24/2022 16 : 9
    public int findVisualizationArea( double OFFSET ) {
        List<Double> coors = new ArrayList<>( 4 );
        coors.add( maxX ); // max x
        coors.add( maxY ); // max Y
        coors.add( minX ); // min x
        coors.add( minY ); // min y

        return ( int ) ( Math.abs( MyMath.findMaxMinInAbs( coors )[ 1 ] ) + OFFSET * 2 ) * 2;
    }

    //-------------------------------------------------------
    // get bounding box
    //-------------------------------------------------------

    /**
     * get a box with minX, maxX, minY and maxY.
     *
     *                 top
     *         <-----------------o
     *         |                 ^
     *         |                 |
     *         |                 |
     *    left |                 | right
     *         |                 |
     *         |                 |
     *         v                 |
     *         o---------------->
     *               bottom
     *
     * top.origin = ( maxX, maxY )
     * bottom.origin = ( minX, minY )
     * */

    static
    BoundingBox getBox( double minX, double maxX, double minY, double maxY, Vector offset ) {
        offset = new Vector( Math.ceil( offset.x ), Math.ceil( offset.y ) );
        Vector[] points = getFourPoints( minX, maxX, minY, maxY );
        return new BoundingBox( points[ 0 ].add( offset ), points[ 1 ].add( offset ),
                points[ 2 ].add( offset ), points[ 3 ].add( offset ) );
    }

    /**
     * get a box with the boundary.
     *
     * @param boundary non-negative
     * */

    public static
    BoundingBox getBox( double boundary, Vector offset ) {
        assert !MyMath.isNegative( boundary );
        return getBox( -boundary, boundary, -boundary, boundary, offset );
    }

    private static
    double getOutsiderBoundary( double coor, double boundary ) {
        return boundary * ( coor / Math.abs( coor ) ) + coor;
    }

    /**
     * get a larger box compared to the box by offsetting offset.
     *
     *                       top ( new )
     *       <--------------------------------------
     *       |                                     ^
     *       |        <------------------          |
     *       |        |     ( old )     ^          |
     *       |        |                 |          |
     *       |        |                 |          |
     * left  |        |                 |  offset  | right
     *       |        |                 |          |
     *       |        |                 |          |
     *       |        v                 |          |
     *       |        ------------------>          |
     *       v                                     |
     *       -------------------------------------->
     *                       bottom
     *
     * @throws IllegalArgumentException - The offset is negative.
     * */

    public BoundingBox getBox( double offset ) {
        if ( MyMath.isNegative( offset ) )
            throw new IllegalArgumentException( "Offset is negative" );

        // BoundingBox getBox( double minX, double maxX, double minY, double maxY, Vector offset )
        return getBox( getOutsiderBoundary( minX, offset ),
                          getOutsiderBoundary( maxX, offset ),
                              getOutsiderBoundary( minY, offset ),
                                  getOutsiderBoundary( maxY, offset ), Vector.origin );
    }

    /**
     * Get a bounding box containing all the points in the point set with <b>No</b> offset.
     */

    public static
    BoundingBox getBox( List<Vector> points ) {
        return getBox( points, 0 );
    }

    /**
     * Get a bounding box containing all the points in the point set,
     * with some offset to separate points and the box.
     */

    public static
    BoundingBox getBox( List<Vector> points, int OFFSET ) {
        if ( points.isEmpty() ) return null;

        // get min and max of x and y
        // to compute the box by offsetting each direction.
        // The box should have the DCEL for the box as well as four lines defining it,
        // where the area defined by the four lines is supposed to be bigger
        // than the box defined by the DCEL
        points.sort( Vectors::sortByX );
        double minX = points.get( 0 ).x;
        double maxX = points.get( points.size() - 1 ).x;
        points.sort( Vectors::sortByY );
        double minY = points.get( 0 ).y;
        double maxY = points.get( points.size() - 1 ).y;

        // determine the width( height ) the regular bounding box
        double boundary = Math.abs( MyMath.findMaxMinInAbs( minX, maxX, minY, maxY )[ 1 ] ) + OFFSET;
        return BoundingBox.getBox( boundary, new Vector( ( minX + maxX ) / 2, ( minY + maxY ) / 2 ) );
    }

    /**
     * Get a bounding box with w as its width, h as its height and c as its center.
     *
     * Visualization:
     *
     *                        top
     *       <--------------------------------------
     *       |                  |                  ^
     *       |                  |                  |
     *       |                  | +h/2             |
     *       |                  |                  |
     *       |                  |                  |
     * left  | ---------------- c                  | right
     *       |        -w/2                         |
     *       |                                     |
     *       |                                     |
     *       |                                     |
     *       v                                     |
     *       -------------------------------------->
     *                       bottom
     *
     * @throws IllegalArgumentException - Width or height is non-positive.
     */

    public static
    BoundingBox getBox( Vector c, int w, int h ) {
        if ( w <= 0 && h <= 0 )
            throw new IllegalArgumentException( "Width or height cannot be non-positive.");

        w /= 2;
        h /= 2;

        Vector topLeft = new Vector( Math.ceil( c.x - w ), Math.ceil( c.y + h ) );
        Vector topRight = new Vector( Math.ceil( c.x + w ), Math.ceil( c.y + h ) );
        Vector bottomLeft = new Vector( Math.ceil( c.x - w ), Math.ceil( c.y - h ) );
        Vector bottomRight = new Vector( Math.ceil( c.x + w ), Math.ceil( c.y - h ) );
        return new BoundingBox( topRight, topLeft, bottomLeft, bottomRight );
    }
}
