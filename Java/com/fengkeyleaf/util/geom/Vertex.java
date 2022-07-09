package com.fengkeyleaf.util.geom;

/*
 * Vertex.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of DCEL vertex
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class Vertex extends Vector {
    private static int IDStatic = 0;
    // It also stores a pointer IncidentEdge(v) to an arbitrary
    // half-edge that has v as its origin.
    public HalfEdge incidentEdge;
    Vertex master;
    Vertex servant;
    HalfEdge l;

    /**
     * constructs to create an instance of Vertex
     * */

    public Vertex( double x, double y ) {
        super( x, y, IDStatic++ );
    }

    public Vertex( Vector point ) {
        this( point.x, point.y );
    }

    Vertex( Vertex v ) {
        super( v.x, v.y );

        v.servant = this;
        master = v;

        incidentEdge = v.incidentEdge.servant == null ? new HalfEdge( v.incidentEdge ) : v.incidentEdge.servant;
    }

    /**
     * get twin edges with this vertex and v as their origins.
     *
     * this ----------------------------->
     *                   ^
     *                   |
     *                   v
     *      <---------------------------- v
     *
     * @return half-edge with this vertex as origin.
     * */

    public HalfEdge getEdges( Vertex v ) {
        HalfEdge e = new HalfEdge( this );
        e.setTwins( new HalfEdge( v ) );
        return e;
    }

    /**
     * left is already connected to right?
     * */

    public boolean isAlreadyConnected( Vertex v ) {
        if ( incidentEdge == null ||
                v.incidentEdge == null ) return false;

        List<HalfEdge> incidentEdges = allIncidentEdges();

        for ( HalfEdge edge : incidentEdges ) {
            if ( edge.origin.equals( v ) ) {
                assert edge.twin.origin.equals( this );
                return true;
            }
        }

        return false;
    }

    List<HalfEdge> allIncidentEdges( boolean isOutgoing, boolean isIncoming ) {
        final List<HalfEdge> edges = new ArrayList<>();

        // outgoing edge
        HalfEdge edge = incidentEdge;
        do {
            assert edge.origin == this : edge + " | " + this;
            assert edge.twin.twin.origin == this;

            // outgoing edge
            if ( isOutgoing ) edges.add( edge );
            // incoming edge
            if ( isIncoming ) edges.add( edge.twin );
            edge = edge.twin.next;
        } while ( edge != incidentEdge );

        return edges;
    }

    /**
     * get all incident edges of the vertex
     * */

    public List<HalfEdge> allIncidentEdges() {
        return allIncidentEdges( true, true );
    }

    public List<HalfEdge> allOutGoingEdges() {
        return allIncidentEdges( true, false );
    }

    public List<HalfEdge> allIncomingEdges() {
        return allIncidentEdges( false, true );
    }

    /**
     * find the outgoing edge of the vertex
     * whose incidentFace is the incidentFace
     * */

    public HalfEdge findOutgoingEdge( Face f ) {
        // visit all incident edges of v,
        // return the one with its origin vertex that is v,
        // as well as with the same face.
        HalfEdge edge = incidentEdge;
        do {
            if ( edge.incidentFace == f && edge.origin == this )
                return edge;
            edge = edge.twin;

            if ( edge.incidentFace == f && edge.origin == this )
                return edge;
            assert edge.next != null : edge.origin;
            edge = edge.next;
        } while ( edge != incidentEdge );

        assert false : this + " " + f.outComponent.origin;
        return null;
    }

    /**
     * find the incoming edge of the vertex
     * whose incidentFace is the incidentFace
     * */

    public HalfEdge findIncomingEdge( Face f ) {
        // visit all incident edges of v,
        // return the one with its origin vertex that is not v,
        // as well as with the same face.
        HalfEdge edge = incidentEdge;
        do {
            if ( edge.incidentFace == f && edge.origin != this )
                return edge;
            edge = edge.twin;

            if ( edge.incidentFace == f && edge.origin != this )
                return edge;
            edge = edge.next;
        } while ( edge != incidentEdge );

        assert false : this + " " + f.outComponent.origin;
        return null;
    }

    public HalfEdge findEdge( Vertex destination ) {
        List<HalfEdge> edges = allIncidentEdges();

        for ( HalfEdge edge : edges ) {
            if ( edge.origin.equals( this ) &&
                    edge.twin.origin.equals( destination ) )
                return edge;
        }

        return null;
    }


    /**
     * find the first ClockWise Edge
     * with this vertex( destination ) and origin
     *
     * for this one, we will find the edge with maximum clockwise angle,
     * i.e. minimum counter-clockwise angle
     *
     * destination: vertex to which the first-clock wise out-degree edge is incident
     *
     * @param        origin vertex to which the out-degree base edge is incident
     * @deprecated   Be careful with precision issue which can lead to unexpected error
     *               since this method use vector dot production to compute the angle between two vectors.
     * */

    @Deprecated
    public HalfEdge firstClockWiseEdge( Vertex origin ) {
        HalfEdge edge = incidentEdge;
        HalfEdge first = null;

        do {
            if ( first == null ) {
                first = edge;
                edge = edge.twin.next;
                continue;
            }

            // found smaller angle in clock wise ordering
            if ( Triangles.clockWiseAngleCompareTo( this, origin,
                    first.next.origin, edge.next.origin ) > 0 )
                first = edge;

            edge = edge.twin.next;
        } while ( edge != incidentEdge );

        return first;
    }

    /**
     * find the first CounterClockWise Edge
     * with this vertex( origin ) and destination
     *
     * for this one, we will find the edge with minimum clockwise angle
     * origin: vertex to which the first-clock wise in-degree edge is incident
     *
     * @param        destination vertex to which the in-degree base edge is incident
     * @deprecated   Be careful with precision issue which can lead to unexpected error
     *               since this method use vector dot production to compute the angle between two vectors.
     * */

    @Deprecated
    public HalfEdge firstCounterClockWiseEdge( Vertex destination ) {
        HalfEdge edge = incidentEdge.twin;
        HalfEdge first = null;

        do {
            if ( first == null ) {
                first = edge;
                edge = edge.next.twin;
                continue;
            }

            // found smaller angle in counter-clock wise ordering
            if ( Triangles.clockWiseAngleCompareTo( this, destination,
                    first.origin, edge.origin ) < 0 )
                first = edge;

            edge = edge.next.twin;
        } while ( edge != incidentEdge.twin );

        return first;
    }

    /**
     * connect this vertex with another one.
     * In this case, half-edges connecting two vertices are not provided,
     * they'll be generated automatically in the process.
     *
     * @param v      vertex to be connected and v must have incidentEdge and
     *               it's required that this vertex cannot be connected to a vertex overlapping the given vertex.
     * @deprecated   Be careful with precision issue which can lead to unexpected error
     *               since this method use vector dot production to compute the angle between two vectors.
     * */

    @Deprecated
    Face connect( Vertex v ) {
        if ( isAlreadyConnected( v ) ) return null;

        // create two new halfEdges,
        // newer and older,
        // connecting left and right
        HalfEdge newer = new HalfEdge( v );
        HalfEdge older = new HalfEdge( this );
        newer.setTwins( older );

        Face f = new Face( newer );
        newer.incidentFace = f;

        HalfEdges.connectBoth( this, v, newer );
        return f;
    }

    /**
     * re-connect half-edges incident to this vertex.
     *
     * @param E half-edges in clock-wise order.
     */

    void connect( List<HalfEdge> E ) {
        for ( int i = 0; i < E.size(); i++ ) {
            int prev = i == 0 ? E.size() - 1 : i - 1;
            int next = i == E.size() - 1 ? 0 : i + 1;
            connect( E.get( prev ), E.get( i ), E.get( next ) );
        }

        // set outgoing half-edge for this vertex.
        HalfEdge e = E.get( 0 );
        incidentEdge = e.origin == this || e.origin.equalsXAndY( this ) ? e : e.twin;
        assert incidentEdge.origin.equalsXAndY( this );
    }

    private void connect( HalfEdge prev, HalfEdge cur,
                          HalfEdge next ) {

        // cur is an outgoing half-edge,
        // so find the first counter-clock wise incoming half-edge.
        if ( cur.origin.equalsXAndY( this ) ) {
            cur.connect( prev.origin.equalsXAndY( this ) ? prev.twin : prev );
            // cur is an incoming half-edge,
            // so find the first clock wise outgoing half-edge.
            assert !cur.twin.origin.equalsXAndY( this ) : this + " | " + cur.twin;
            ( next.origin.equalsXAndY( this ) ? next : next.twin ).connect( cur.twin );
            return;
        }

        assert cur.twin.origin.equalsXAndY( this );
        cur.twin.connect( prev.origin.equalsXAndY( this ) ? prev.twin : prev );
        assert !cur.origin.equalsXAndY( this ) : this + " | " + cur.twin;
        ( next.origin.equalsXAndY( this ) ? next : next.twin ).connect( cur );
    }

    /**
     * reset the origin of each incident outgoing half-edges to this vertex.
     * Usually, we use the following code to re-connect half-edges incident to a vertex:
     *
     * vertex.connect( half-edges ), {@link Vertex#connect(List)};
     * vertex.resetOrigin()
     */

    void resetOrigin() {
        // outgoing edge
        HalfEdge e = incidentEdge;
        do {
            // outgoing edge
            if ( e.origin != null ) e.origin = this;
            // incoming edge
            assert !e.twin.origin.equalsXAndY( this );
            e = e.twin.next;
        } while ( e != incidentEdge );
    }

    /**
     * reset this vertex.
     * */

    void reset() {
        incidentEdge = null;
        if ( master != null && master.servant == this )
            master.servant = null;
        if ( servant != null )
            servant.master = null;

        master = servant = null;
    }

    //----------------------------------------------------------
    // Monotone part for triangulation.
    // With the following data field and methods,
    // we can do partitioning monotone polygons and
    // triangulation for any DCEL faces.
    //----------------------------------------------------------

    static final boolean LEFT_CHAIN_VERTEX = true;
    static final boolean RIGHT_CHAIN_VERTEX = false;
    boolean isLeftChainVertex;
    VertexType vertexType;

    /**
     * enumerative Vertex Type for partitioning monotone subpolygons
     * */

    enum VertexType {
        START, SPLIT, // 0, 1
        END, MERGE, // 2, 3
        REGULAR_LEFT, REGULAR_RIGHT, // 4
    }


    // -------------------------------------------
    // computational part ------------------------
    // -------------------------------------------

    /**
     * are both vertices on the same monotone chain?
     * */

    boolean isOnTheDifferentChain( Vertex v ) {
        return isLeftChainVertex != v.isLeftChainVertex;
    }

    // -------------------------------------------
    // drawing part ------------------------------
    // -------------------------------------------

    private static
    void drawTri( Graphics graphics,
                  int x, int y, boolean isUpright ) {
        final int N_POINTS = 3;
        int[] xPoints = new int[ N_POINTS ];
        int[] yPoints = new int[ N_POINTS ];

        final int UP_OFFSET = 4;
        final int DOWN_OFFSET = 4;

        xPoints[ 0 ] = x;
        xPoints[ 1 ] = x - DOWN_OFFSET;
        xPoints[ 2 ] = x + DOWN_OFFSET;
        if ( isUpright ) {
            yPoints[ 0 ] = y + UP_OFFSET;
            yPoints[ 1 ] = y - DOWN_OFFSET;
            yPoints[ 2 ] = y - DOWN_OFFSET;
        }
        else {
            yPoints[ 0 ] = y - UP_OFFSET;
            yPoints[ 1 ] = y + DOWN_OFFSET;
            yPoints[ 2 ] = y + DOWN_OFFSET;
        }

        graphics.setColor( Color.GRAY );
        graphics.fillPolygon( xPoints, yPoints, N_POINTS );;
    }

    public static
    void drawVertexType( Graphics graphics, java.util.List<Integer> vertexTypePoints ) {
        assert vertexTypePoints.size() % 3 == 0;
        graphics.setColor( Color.GRAY );
        int CYCLE_OFFSET = DrawingProgram.CYCLE_OFFSET;
        int RADIUS = DrawingProgram.RADIUS;

        for ( int i = 0; i < vertexTypePoints.size(); i += 3 ) {
            int x = vertexTypePoints.get( i + 1 );
            int y = vertexTypePoints.get( i + 2 );

            switch ( vertexTypePoints.get( i ) ) {
                case 0 -> graphics.drawRect(  x - CYCLE_OFFSET, y - CYCLE_OFFSET, RADIUS, RADIUS );
                case 1 -> drawTri( graphics, x, y, true );
                case 2 -> graphics.fillRect( x - CYCLE_OFFSET, y - CYCLE_OFFSET, RADIUS, RADIUS );
                case 3 -> drawTri( graphics, x, y, false );
                case 4 -> graphics.fillOval( x - CYCLE_OFFSET, y - CYCLE_OFFSET, RADIUS, RADIUS );
                default -> {
                    assert false;
                }
            }
        }
    }

    /**
     * START, SPLIT, // 0, 1
     * END, MERGE, // 2, 3
     * REGULAR_LEFT, REGULAR_RIGHT, // 4
     **/

    static
    void storePoints( java.util.List<Integer> points,
                      VertexType vertexType,
                      int x, int y ) {
        int vertexTypeInt = -1;
        switch ( vertexType ) {
            case START -> vertexTypeInt = 0;
            case SPLIT -> vertexTypeInt = 1;
            case END -> vertexTypeInt = 2;
            case MERGE -> vertexTypeInt = 3;
            default -> vertexTypeInt = 4;
        }

        points.add( vertexTypeInt );
        points.add( x );
        points.add( y );
    }

    public static
    List<Integer> drawVertexType( List<Vertex> vertices,
                                  int originWidth, int originHeight,
                                  int windowWidth, int windowHeight ) {
        List<Vector> flipedYs = new ArrayList<>( vertices );
        flipedYs = Vectors.reversedY( flipedYs );
        List<Integer> vertexPoints = new ArrayList<>();
        assert flipedYs.size() == vertices.size();

        for ( int i = 0; i < vertices.size(); i++ ) {
            Vector normalized = Vectors.normalize( flipedYs.get( i ), originWidth, originHeight, windowWidth, windowHeight );
            storePoints( vertexPoints, vertices.get( i ).vertexType, ( int ) normalized.x, ( int ) normalized.y );
        }

        return vertexPoints;
    }
}
