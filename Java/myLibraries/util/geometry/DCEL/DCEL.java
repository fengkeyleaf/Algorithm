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

    public static
    HalfEdge findEdge( Vertex base, Vertex destination ) {
        List<HalfEdge> edges = allIncidentEdges( base );

        for ( HalfEdge edge : edges ) {
            if ( edge.origin.equals( base ) &&
                    edge.twin.origin.equals( destination ) )
                return edge;
        }

        return null;
    }

    /**
     * find the first ClockWise Edge
     * with two vertices destination and origin
     *
     * for this one, we will find the edge with maximum clockwise angle,
     * i.e. minimum counter-clockwise angle
     *
     * @param destination vertex to which the first-clock wise out-degree edge is incident
     * @param origin vertex to which the out-degree base edge is incident
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
     *
     * @param origin vertex to which the first-clock wise in-degree edge is incident
     * @param destination vertex to which the in-degree base edge is incident
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
            assert edge.twin != null;
            assert edge.incidentFace == outComponent.incidentFace : outComponent.origin;
            edge = edge.next;
        } while ( edge != outComponent );

        return edges;
    }

    /**
     * walk around all halfEdges, starting at face
     * and get visited halfEdges
     *
     * walkAroundEdgeFace() in JavaScript Version
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
     *
     * walkAroundVertexFace() in JavaScript Version
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
}
