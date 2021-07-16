package myLibraries.util.geometry.DCEL;

/*
 * DCEL.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.tools.Triangles;

import java.util.ArrayList;
import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to DCEL, Doubly-Connected Edge List
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class DCEL {

    /**
     * find the first ClockWise Edge
     * with two vertices destination and origin
     *
     * for this one, we will find the edge with maximum clockwise angle,
     * i.e. minimum counter-clockwise angle
     * */

    public static
    HalfEdge firstClockWiseEdge( Vertex destination, Vertex origin ) {
        HalfEdge edge = destination.incidentEdge;
        HalfEdge first = null;

        do {
            if ( first == null ) {
                first = edge;
                edge = edge.twin.next;
                continue;
            }

            // found smaller angle in clock wise ordering
            if ( Triangles.clockWiseAngleCompareTo( destination, origin,
                    first.next.origin, edge.next.origin ) > 0 )
                first = edge;

            edge = edge.twin.next;
        } while ( edge != destination.incidentEdge );

        return first;
    }

    /**
     * find the first CounterClockWise Edge
     * with two vertices origin and destination
     *
     * for this one, we will find the edge with minimum clockwise angle
     * */

    public static
    HalfEdge firstCounterClockWiseEdge( Vertex origin, Vertex destination ) {
        HalfEdge edge = origin.incidentEdge.twin;
        HalfEdge first = null;

        do {
            if ( first == null ) {
                first = edge;
                edge = edge.next.twin;
                continue;
            }

            // found smaller angle in counter-clock wise ordering
            if ( Triangles.clockWiseAngleCompareTo( origin, destination,
                    first.origin, edge.origin ) < 0 )
                first = edge;

            edge = edge.next.twin;
        } while ( edge != origin.incidentEdge.twin );

        return first;
    }

    /**
     * find the incoming edge of the vertex
     * whose incidentFace is the incidentFace
     * */

    public static
    HalfEdge findIncomingEdge( Vertex vertex, Face incidentFace ) {
        // visit all incident edges of v,
        // return the one with its origin vertex that is not v,
        // as well as with the same face.
        HalfEdge edge = vertex.incidentEdge;
        do {
            if ( edge.incidentFace == incidentFace && edge.origin != vertex )
                return edge;
            edge = edge.twin;

            if ( edge.incidentFace == incidentFace && edge.origin != vertex )
                return edge;
            edge = edge.next;
        } while ( edge != vertex.incidentEdge );

        assert false : vertex + " " + incidentFace.outComponent.origin;
        return null;
    }

    /**
     * find the outgoing edge of the vertex
     * whose incidentFace is the incidentFace
     * */

    public static
    HalfEdge findOutgoingEdge( Vertex vertex, Face incidentFace ) {
        // visit all incident edges of v,
        // return the one with its origin vertex that is v,
        // as well as with the same face.
        HalfEdge edge = vertex.incidentEdge;
        do {
            if ( edge.incidentFace == incidentFace && edge.origin == vertex )
                return edge;
            edge = edge.twin;

            if ( edge.incidentFace == incidentFace && edge.origin == vertex )
                return edge;
            assert edge.next != null : edge.origin;
            edge = edge.next;
        } while ( edge != vertex.incidentEdge );

        assert false : vertex + " " + incidentFace.outComponent.origin;
        return null;
    }

    /**
     * reset IncidentEdge
     * */

    public static
    void resetIncidentFace( HalfEdge start, Face face ) {
        HalfEdge edge = start;
        do {
            edge.incidentFace = face;
            edge = edge.next;
        } while ( edge != start );
    }

    /**
     * walk around all halfEdges, starting at innerComponent
     * */

    public static
    List<HalfEdge> walkAroundEdge( HalfEdge outComponent ) {
        final List<HalfEdge> edges = new ArrayList<>();
        HalfEdge edge = outComponent;
        do {
            edges.add( edge );
            assert edge.incidentFace == outComponent.incidentFace : outComponent.origin;
            edge = edge.next;
        } while ( edge != outComponent );

        return edges;
    }

    /**
     * walk around all halfEdges, starting at face
     * and get visited halfEdges
     * */

    public static
    List<HalfEdge> walkAroundEdge( Face face ) {
        return walkAroundEdge( face.outComponent );
    }

    public static
    List<Vertex> walkAroundVertex( HalfEdge outComponent ) {
        final List<Vertex> vertices = new ArrayList<>();
        HalfEdge edge = outComponent;
        do {
            vertices.add( edge.origin );
            assert edge.incidentFace == outComponent.incidentFace;
            edge = edge.next;
        } while ( edge != outComponent );

        return vertices;
    }

    /**
     * walk around all halfEdges, starting at face
     * and get visited vertices
     * */

    public static
    List<Vertex> walkAroundVertex( Face face ) {
        return walkAroundVertex( face.outComponent );
    }

    /**
     * get all incident edges of the vertex
     * */

    public static
    List<HalfEdge> allIncidentEdges( Vertex vertex ) {
        final List<HalfEdge> edges = new ArrayList<>();
        HalfEdge edge = vertex.incidentEdge;
        do {
            edges.add( edge );
            edges.add( edge.twin );
            edge = edge.twin.next;
        } while ( edge != vertex.incidentEdge );

        return edges;
    }

    private static
    void testCaseOne() {
        int ID = -1;
        int len = 6;
        List<Vertex> vertices = new ArrayList<>();
        for ( int i = 0; i < len; i++ )
            vertices.add( new Vertex( ID, ID++ ) );

        Face face1 = new Face();
        Face face2 = new Face();
        List<HalfEdge> counterClockWiseEdges = new ArrayList<>();
        List<HalfEdge> clockWiseEdges = new ArrayList<>();

        for ( int i = 0; i < len; i++ ) {
            HalfEdge edge1 = new HalfEdge( vertices.get( i ), face1 );
            vertices.get( i ).incidentEdge = edge1;
            counterClockWiseEdges.add( edge1 );
            HalfEdge edge2 = new HalfEdge( vertices.get( ( i + 1 ) % len ), face2 );
            clockWiseEdges.add( edge2 );
            edge1.twin = edge2;
            edge2.twin = edge1;

            if ( i > 0 ) {
//                counterClockWiseEdges.get( i ).prev = counterClockWiseEdges.get( i - 1 );
//                counterClockWiseEdges.get( i - 1 ).next = counterClockWiseEdges.get( i );
                HalfEdge.connect(  counterClockWiseEdges.get( i ),  counterClockWiseEdges.get( i - 1 ) );
//                clockWiseEdges.get( i - 1 ).prev = clockWiseEdges.get( i );
//                clockWiseEdges.get( i ).next = clockWiseEdges.get( i - 1 );
                HalfEdge.connect(  clockWiseEdges.get( i - 1 ),  clockWiseEdges.get( i ) );
            }
        }
//        counterClockWiseEdges.get( 0 ).prev = counterClockWiseEdges.get( counterClockWiseEdges.size() - 1  );
//        counterClockWiseEdges.get( counterClockWiseEdges.size() - 1  ).next = counterClockWiseEdges.get( 0 );
        HalfEdge.connect(  counterClockWiseEdges.get( 0 ),  counterClockWiseEdges.get( counterClockWiseEdges.size() - 1 ) );
//        clockWiseEdges.get( clockWiseEdges.size() - 1  ).prev = clockWiseEdges.get( 0 );
//        clockWiseEdges.get( 0 ).next = clockWiseEdges.get( clockWiseEdges.size() - 1  );
        HalfEdge.connect(  clockWiseEdges.get( clockWiseEdges.size() - 1 ),  clockWiseEdges.get( 0 ) );

        face1.outComponent = counterClockWiseEdges.get( 0 );
        face2.innerComponents.add( clockWiseEdges.get( 0 ) );

        System.out.println( walkAroundEdge( face1 ) );

        for ( Vertex vertex : vertices )
            System.out.println( allIncidentEdges( vertex ) );
    }

    public static
    void main( String[] args ) {
        testCaseOne();
    }
}
