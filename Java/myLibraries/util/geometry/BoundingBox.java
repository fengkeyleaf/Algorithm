package myLibraries.util.geometry;

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

import myLibraries.lang.MyMath;
import myLibraries.util.geometry.DCEL.*;
import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.Vector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
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
 */

public final class BoundingBox {
    static final double OFFSET = 10;

    public final Face outer;

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
    public List<HalfEdge> vorEdges;
    // Search Structure for Point Location.
    public SearchStructure SS;

    /**
     * Constructs to create an instance of BoundingBox
     */

    public BoundingBox( Vector topRight, Vector topLeft,
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

    /**
     * refill inner faces of this bounding box after separating it.
     * Like Triangulation or Voronoi Diagrams.
     * */

    public void resetInnerFaces( List<Face> faces ) {
        outer.innerComponents.clear();
        faces.forEach( f -> {
            // duplicate site(s) hasn't been assigned outComponent.
            if ( f.outComponent != null )
                outer.addInnerComponent( f.outComponent.twin );
        } );
    }

    public Vertex splitTop( Line line ) {
        return split( top, topEdges, line );
    }

    public Vertex splitBottom( Line line ) {
        return split( bottom, bottomEdges, line );
    }

    public Vertex split( HalfEdge boxEdge, Vector intersection ) {
        return splitCommon( split( boxEdge ), intersection );
    }

    /**
     * the point, p, is on this bounding box?
     * ( including lying on its boundary. )
     * */

    // TODO: 2/11/2022 extract a method for this one. i.e. use outer face to see if a point lies inside it.
    public boolean isOnThisBox( Vector p ) {
        HalfEdge edge = top.twin;

        do {
            if ( Triangles.toLeftRigorously( edge.origin, edge.next.origin, p ) )
                return false;

            assert edge.incidentFace == top.twin.incidentFace;
            edge = edge.next;
        } while ( edge != top.twin );

        return true;
    }

    private static
    Vertex split( HalfEdge boxEdge, LinkedList<HalfEdge> edges, Line line ) {
        Vector intersection = boxEdge.getSegment().lineIntersect( line );

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
                split = HalfEdges.split( edge, intersection );
                edges.add( edges.indexOf( edge ), edge.next );
                break;
            }
        }

        return split;
    }

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

    public static
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
     * @param offset non-negative
     * */

    public BoundingBox getBox( double offset ) {
        assert !MyMath.isNegative( offset );

        // BoundingBox getBox( double minX, double maxX, double minY, double maxY, Vector offset )
        return getBox( getOutsiderBoundary( minX, offset ),
                          getOutsiderBoundary( maxX, offset ),
                              getOutsiderBoundary( minY, offset ),
                                  getOutsiderBoundary( maxY, offset ), Vector.origin );
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

    public static
    BoundingBox getBoundingBox( List<Vector> points ) {
        return getBoundingBox( points, 0 );
    }

    public static
    BoundingBox getBoundingBox( List<Vector> points, double OFFSET ) {
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
}
