package myLibraries.util.geometry;

/*
 * VoronoiDiagrams.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 voronoiDiagrams(), handleSiteEvent(), handleCircleEvent() and etc. on 12/29/2021$
 */

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.GUI.geometry.IntersectProgram;
import myLibraries.lang.MyMath;
import myLibraries.util.Node;
import myLibraries.util.geometry.DCEL.*;
import myLibraries.util.geometry.elements.Circle;
import myLibraries.util.geometry.elements.InterLine;
import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.Ray;
import myLibraries.util.geometry.elements.Segment;
import myLibraries.util.geometry.elements.EventSite;
import myLibraries.util.geometry.elements.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This class consists exclusively of static methods
 * that related to Voronoi Diagrams
 *
 * The definition of the degenerate case
 * where the sweep line is at the very beginning of the algorithm.
 *
 * -----s1----------s2---------- <= sweep line  |
 *                                              |
 *   s3                   s4                    | moving direction
 *                                              |
 *                                              |
 *                                              v
 *
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class VoronoiDiagrams {

    //-------------------------------------------------------
    // Voronoi Diagrams amd Point Location.
    //-------------------------------------------------------

    /**
     * Find on which cell ( Voronoi Face ) the query point is.
     * In order to use this query operation,
     * you need to construct a trapezoidal map for you Voronoi Diagrams first.
     *
     * @param v search res from Search Structure of Point Location.
     * @param p query point.
     * */

    public static
    List<Face> findCell( SearchVertex v, Vector p ) {
        if ( v == null ) return null;

        List<Face> faces = new ArrayList<>();
        switch ( v.type ) {
            // p lies on a Voronoi vertex.
            case X_POINT_Q, X_POINT_P -> {
                // all faces that can be accessed from the vertex directly,  are our targets.
                List<HalfEdge> outGoings = DCEL.allOutGoingEdges( ( Vertex ) v.point );
                assert outGoings.size() > 2;
                outGoings.forEach( e -> faces.add( e.incidentFace ) );
            }
            // p lies on a Voronoi edge.
            case SEGMENT -> {
                // two faces that can be accessed from the edge directly, are our targets.
                HalfEdge e = ( ( InterLine ) v.line ).dangling;
                faces.add( e.incidentFace );
                faces.add( e.twin.incidentFace );
            }
            // p lies in a cell ( Voronoi face )
            case TRAPEZOID -> {
                // dangling is null, meaning the line is from the bounding box,
                // they're useless.
                HalfEdge top = ( ( InterLine ) v.trapezoid.top ).dangling;
                HalfEdge bottom = ( ( InterLine ) v.trapezoid.bottom ).dangling;

                // cannot have a query point lying in a trapezoid
                // whose top and bottom are both box edges.
                assert !( top == null && bottom == null );

                // top is box edge, look into bottom.
                // Bottom is box edge, look into top.
                // or both are Voronoi edges,
                // we just look into one of them.
                findCell( faces, p, Objects.requireNonNullElse( top, bottom ) );

            }
            default -> { assert false; }
        }

        return faces;
    }

    /**
     * query point is on which face that the half-edge is incident to.
     * */

    private static
    void findCell( List<Face> faces, Vector p, HalfEdge e ) {
        if ( Triangles.toLeftRigorously( e.origin, e.next.origin, p ) ) {
            faces.add( e.incidentFace );
            return;
        }

        assert Triangles.toLeftRigorously( e.twin.origin, e.twin.next.origin, p );
        faces.add( e.twin.incidentFace );
    }

    /**
     * Generate segments from Voronoi edges to compute
     * the trapezoidal Map of the Voronoi Diagrams.
     *
     * @param b bounding box of the Voronoi Diagrams.
     * */

    public static
    List<Line> getSegments( BoundingBox b ) {
        if ( b == null ) return null;

        List<Line> segments = null;
        // only Voronoi Edges, no Voronoi vertex.
        if ( b.vertices.isEmpty() )
            segments = getSegmentsByEdge( b.vorEdges );
        // have both.
        else
            segments = getSegmentsByVertex( b.vertices );

        assert visualizeSegments( segments, b );
        return segments;
    }

    private static
    List<Line> getSegmentsByEdge( List<HalfEdge> vorEdges ) {
        // only with Voronoi edges, see them as a segment.
        List<Line> segments = new ArrayList<>( vorEdges.size() + 1 );
        vorEdges.forEach( e -> segments.add( new InterLine( e.origin, e.next.origin, e ) ) );
        return segments;
    }

    private static
    List<Line> getSegmentsByVertex( List<VoronoiVertex> vertices ) {
        // every Voronoi vertex has at least 3 edges,
        // i.e. in-degree of 3.
        // Have more than 3 edges when several sites that coincide.
        List<Line> segments = new ArrayList<>( vertices.size() * 3 + 1 );

        // give every Voronoi vertex an identifying number ( > -1 ),
        // so that we can differentiate them with the vertices on the bounding box.
        Node.setMappingID( vertices, 0 );

        vertices.forEach( v -> {
            List<HalfEdge> outGoings = DCEL.allOutGoingEdges( v );

            outGoings.forEach( e -> {
                // vertices with mappingID -1, are on the one on the bounding box,
                // no need to traverse their neighbours.
                if ( e.next.origin.mappingID == -1 ) {
                    segments.add( new InterLine( v, e.next.origin, e ) );
                    return;
                }

                // don't forget to ignore duplicate half-edge,
                // that we've visited before.
                // we don't know which half-edge we've traversed,
                // so need check both direction.
                if ( !e.isVisited && !e.twin.isVisited ) {
                    e.isVisited = true;
                    segments.add( new InterLine( v, e.next.origin, e ) );
                }
            } );

        } );

        Node.resetMappingID( vertices );

        return segments;
    }

    private static
    boolean visualizeSegments( List<Line> segments, BoundingBox b ) {
        IntersectProgram program = new IntersectProgram( b.findVisualizationArea( BoundingBox.OFFSET ) );
        program.draw( segments, null, null );
        program.initialize();
        return true;
    }

    //-------------------------------------------------------
    // Compute Voronoi Diagrams
    //-------------------------------------------------------

    /**
     * Compute Voronoi Diagrams
     *
     * Reference resource:
     * @see <a href=http://www.cs.uu.nl/geobook/>Computational Geometry: Algorithms and Applications(Third Edition)</a>
     *
     * @param sites sites, but in the form of DCEL Face with site.
     *              Duplicate sites allowed, the algorithm will them remove in the process.
     * @return bounding box covering all sites ( in the form of DCEL Faces ) and voronoi vertices.
     **/

    // Algorithm VORONOIDIAGRAM(P)
    // Input. A set P := { p1, ..., pn } of point sites in the plane.
    // Output. The Voronoi diagram Vor(P) given inside a bounding box in a doubly-connected edge list D.
    public static
    BoundingBox voronoiDiagrams( List<Face> sites ) {
        // 1. Initialize the event queue Q with all site events,
        EventRBTreeVoronoi eventQueue = new EventRBTreeVoronoi( ( p1, p2 ) -> {
            // This lambda method is for debugging purpose.
            // sites with smaller x-coor are considered larger when they have the same y-coor.
            // so you're free to use Vectors::sortByY.
            if ( MyMath.isEqual( p1.y, p2.y ) )
                return -MyMath.doubleCompare( p1.x, p2.x );

            return MyMath.doubleCompare( p1.y, p2.y );
        } );
        // EventRBTreeVoronoi eventQueue = new EventRBTreeVoronoi( Vectors::sortByY );
        sites.forEach( s -> eventQueue.put( new EventSite( ( VoronoiFace ) s, EventSite.Type.SITE ) ) );

        // initialize an empty status structure T
        StatusRBTreeVoronoi statusTree = new StatusRBTreeVoronoi( Vectors::sortByX );
        // and an empty doubly-connected edge list D.

        // 2. while Q is not empty
        while ( !eventQueue.isEmpty() ) {
            // 3. do Remove the event with largest y-coordinate from Q.
            EventSite event = eventQueue.deleteMaxAndGetVal();

            switch ( event.type ) {
                // 4. if the event is a site event, occurring at site pi
                // 5. then HANDLESITEEVENT(pi)
                case SITE -> handleSiteEvent( event, eventQueue, statusTree );
                // 6. else HANDLECIRCLEEVENT(γ), where γ is the leaf of T
                // representing the arc that will disappear
                case CIRCLE -> handleCircleEvent( event, eventQueue, statusTree );
                default -> { assert false; }
            }
        }

        // 7. The internal nodes still present in T
        // correspond to the half-infinite edges of the Voronoi diagram.
        // Compute a bounding box
        // that contains all vertices of the Voronoi diagram in its interior,
        // And the bounding boxes must be regular
        BoundingBox box = getBoundingBox( sites, eventQueue.vertices );
        BoundingBox imaginaryBox = box == null ? null : box.getBox( BoundingBox.OFFSET );
        // and attach the half-infinite edges to the bounding box
        // by updating the doubly-connected edge list appropriately.
        attachToBox( sites, statusTree, eventQueue, box, imaginaryBox );

        // 8. Traverse the half-edges of the doubly-connected edge list
        // to add the cell records and the pointers to and from them.
        // Check every half-edge belong to the same face have the right incidentFace pointer

        // only one site, set the inner face of the box to it.
        // box cannot be null in this case.
        if ( sites.size() == 1 ) sites.get( 0 ).outComponent = box.top;
        sites.forEach( DCEL::resetIncidentFace );

        // set upo inner faces of the bounding box,
        // and remove zero-length edges.
        if ( box != null && statusTree.size() > 0 ) {
            box.resetInnerFaces( sites );
            box.vertices = removeZeroLength( eventQueue.vertices );
            box.vorEdges = statusTree.edges;
        }

        // Check integrity of Voronoi Diagram data structure when assert is enabled.
        // and return the bounding box.
        return check( box, sites );
    }

    //-------------------------------------------------------
    // remove Zero-Length edges
    //-------------------------------------------------------

    /**
     * remove zero-length edges after computing the Voronoi Diagrams,
     * meaning there will be several coincident circle events
     * when there are four or more co-circular sites, such that
     * the interior of the circle through them is empty.
     * */

    private static
    List<VoronoiVertex> removeZeroLength( List<VoronoiVertex> vertices ) {
        // O( nlogn )
        vertices.sort( Vectors::sortByX );

        // TODO: 2/6/2022 XXX: linkedList to do remove in O(1).
        // remove all zero-length edges.
        // i.e. site events that coincide.
        for ( int i = 0; i < vertices.size() - 1; i++ ) {
            VoronoiVertex v1 = vertices.get( i );
            VoronoiVertex v2 = vertices.get( i + 1 );

            // vertices with zero-length edge will be adjacent.
            if ( v1.equals( v2 ) ) {
                assert v1.circle.equals( v2.circle );
                removeZeroLength( v1, v2 );
                vertices.remove( i + 1 );
            }
        }

        return vertices;
    }

    /**
     * redirect half-edge and set up origin vertex of half-edge correctly.
     *
     * @param v1 kept vertex.
     * @param v2 removed vertex.
     */

    private static
    void removeZeroLength( Vertex v1, Vertex v2 ) {
        List<HalfEdge> outComings1 = DCEL.allOutGoingEdges( v1 );
        List<HalfEdge> outComings2 = DCEL.allOutGoingEdges( v2 );
        int count = 0;
        
        for ( HalfEdge e : outComings1 ) {
            if ( e.twin.origin == v2 ) {
                // incident edge happens to be the zero-length edge,
                // set it to another out-coming edge.
                if ( v1.incidentEdge == e ) v1.incidentEdge = e.next;

                // redirect half-edges' next and pre pointers.
                // be careful with direction.
                e.next.connect( e.prev );
                e.twin.next.connect( e.twin.prev );

                // assert that there is only one zero-length edge between two vertices.
                assert count++ < 1;
            }
        }

        // set up the origin of out-coming half-edges of the removed vertex.
        outComings2.forEach( e -> e.origin = v1 );
    }

    //-------------------------------------------------------
    // Check integrity of Voronoi Diagram data structure.
    //-------------------------------------------------------

    private static
    BoundingBox check( BoundingBox box, List<Face> sites ) {
        if ( box == null ) return null;

        // check every site to be the right cell ( Voronoi Face )
        box.outer.innerComponents.forEach( f -> {
            assert ( ( VoronoiFace ) f.twin.incidentFace ).check() : f + " " + ( ( VoronoiFace ) f.twin.incidentFace ).site;
        } );

        // check integrity of Voronoi vertex and edge
        List<VoronoiFace> cells = new ArrayList<>( sites.size() );
        sites.forEach( s -> {
            // duplicate site(s) hasn't been assigned outComponent, ignore them.
            if ( s.outComponent != null )
                cells.add( ( VoronoiFace ) s );
        } );
        box.vertices.forEach( v -> {
            assert isVoronoiVertex( v, cells );
            assert isVoronoiEdge( v, cells, box.vertices );
        } );

        return box;
    }

    // Theorem 7.4 For the Voronoi diagram Vor(P) of a set of points P the following holds:
    // (i) A point q is a vertex of Vor(P) if and only if its largest empty circle Cp(q)
    // contains three or more sites on its boundary.
    // TODO: 1/21/2022 duality and inCircle test to do the check
    private static
    boolean isVoronoiVertex( VoronoiVertex vertex, List<VoronoiFace> sites ) {
        List<HalfEdge> outgoingEdges = DCEL.allOutGoingEdges( vertex );

        // get vertices closet to the voronoi vertex
        List<VoronoiFace> closetSites = new ArrayList<>( outgoingEdges.size() );
        outgoingEdges.forEach( e -> closetSites.add( ( VoronoiFace ) e.incidentFace ) );

        // they should be on the boundary of the circle defined by the vertex
        closetSites.forEach( s -> {
            assert MyMath.isEqual( new Line( s.site, vertex.circle.center ).getVector().length(), vertex.circle.radius ) : s.site + " " + new Line( s.site, vertex.circle.center ).getVector().length() + " " + vertex.circle;
        } );

        // remove them from the site list
        closetSites.forEach( sites::remove );

        // other sites should not be on or inside the circle
        sites.forEach( s -> {
            assert MyMath.doubleCompare( new Line( s.site, vertex.circle.center ).getVector().length(), vertex.circle.radius ) > 0;
        } );

        return true;
    }

    // (ii) The bisector between sites pi and pj defines an edge of Vor(P)
    // if and only if there is a point q on the bisector such that Cp(q)
    // contains both pi and pj on its boundary but no other site.
    private static
    boolean isVoronoiEdge( VoronoiVertex vertex, List<VoronoiFace> sites,
                           List<VoronoiVertex> vertices ) {

        List<HalfEdge> outgoingEdges = DCEL.allOutGoingEdges( vertex );

        outgoingEdges.forEach( e -> {
            VoronoiFace f1 = ( VoronoiFace ) e.incidentFace;
            VoronoiFace f2 = ( VoronoiFace ) e.twin.incidentFace;

            // remove the two sites from the site list.
            List<VoronoiFace> siteList = new ArrayList<>( sites );
            siteList.remove( f1 );
            siteList.remove( f2 );

            // center of the circle, formed by f1, f2 and arbitrary one from the list,
            // cannot be on the Vornoni edge of f1 and f2.
            assert isVoronoiEdge( f1.site, f2.site, e, siteList, vertices );
        } );

        return true;
    }

    /**
     * check the center of the circle formed by arbitrary three sites (pi, pj, pk) are not
     * on the Voronoi edge that is also a bisector of pi and pj.
     * Unless the center is also a Voronoi vertex of them.
     * Assume this is equivalent to (ii), but not for sure.
     *
     * @param sites sites that have removed those at which p1 and p2 are
     */

    // TODO: 2/7/2022 equivalent to (ii)?
    private static
    boolean isVoronoiEdge( Vector p1, Vector p2, HalfEdge edge,
                           List<VoronoiFace> sites, List<VoronoiVertex> vertices ) {

        Vector mid = p1.getMid( p2 );
        // pi and pj are the closet to the mid,
        // which is the center of the circle defined by pi and pj
        assert MyMath.isEqual( new Line( p1, mid ).getVector().length(), new Line( p2, mid ).getVector().length() );

        // other sites should not be on or inside the circle
        sites.forEach( s -> {
            Circle circle = Circles.getCircleByThreePoints( p1, p2, s.site );
            Segment segment = new Segment( edge.origin, edge.next.origin );
            // p1, p2 and s.site are on the same line.
            // no way to get a circle out of them in this case.
            assert ( circle == null && MyMath.isEqualZero( Triangles.areaTwo( p1, p2, s.site ) ) ) ||
                        // the center of the circle should not be on the Voronoi edge.
                        !segment.isOnThisSegment( circle.center ) ||
                            // except that the center is already a Voronoi vertex.
                            isVoronoiVertex( circle.center, vertices );
        } );

        return true;
    }

    private static
    boolean isVoronoiVertex( Vector center, List<VoronoiVertex> vertices ) {
        for ( VoronoiVertex v : vertices ) {
            if ( center.equalsXAndY( v ) ) return true;
        }

        return false;
    }

    //-------------------------------------------------------
    // attach to the bounding box
    //-------------------------------------------------------

    private static
    BoundingBox getBoundingBox( List<Face> sites, List<VoronoiVertex> vertices ) {
        if ( sites.isEmpty() ) return null;

        final List<Vector> points = new ArrayList<>( sites.size() + vertices.size() );
        sites.forEach( s -> points.add( ( ( VoronoiFace ) s ).site ) );
        points.addAll( vertices );

        return BoundingBox.getBoundingBox( points, BoundingBox.OFFSET );
    }

    // use Bentley Ottmann's algorithm ( Geometric intersection algorithm ) to report
    // which box line that each dangling half-edge intersects in the status tree.
    // ( intersection point, aka DCEL vertex, half-edge and box line)
    // And attach them to the DECL of the box properly.
    private static
    void attachToBox( List<Face> sites, StatusRBTreeVoronoi statusTree,
                      EventRBTreeVoronoi eventQueue,
                      BoundingBox box, BoundingBox imaginaryBox ) {

        if ( box == null ) return;

        // move downwards to update dangling brekapoints to know
        // which direction they're moving along.
        List<EventSite> danglings = statusTree.getDanglingEdges(
                Math.min( box.bottom.origin.y, eventQueue.minSweepY - BoundingBox.OFFSET ) );

        // get segments of the bounding box edges.
        final List<InterLine> boxLines = new ArrayList<>( 4 );
        box.edges.forEach( e -> boxLines.add( new InterLine( e.getSegment(), e ) ) );

        assert visualizeIntersection( sites, boxLines, danglings, box, imaginaryBox );

        // attach danglings to the box.
        // check every dangling to see if it intersects one of the box edges.
        // iterate every dangling.
        danglings.forEach( e -> {
            // ignore the other dangling half-edge in those two special cases.
            if ( e.edge.origin != null &&
                    e.edge.twin.origin != null ) return;

            assert e.direction != null;

            // two variables are assigned directly from doubles,
            // so cannot have precision issue.
            // In this case, may have several sites on the same horizontal line
            // at the very beginning.
            if ( statusTree.initY == statusTree.sweepY ||
                    // only two sites or all sites on the same line,
                    // but not on the same horizontal line
                    // at the very beginning.
                    // see test case: test_1/18
                    // Notice that both don't have any Voronoi vertex generated.
                    // And every dangling edge ( with two half-edges as a whole )
                    // can be regarded as a line.
                    eventQueue.vertices.isEmpty() ) {
                attachToBox( boxLines, e, box );
                return;
            }

            // normal cases with Voronoi vertex,
            // and every dangling edge ( only with one half-edge )
            // can be regarded as a ray.
            attachToVertex( boxLines, e, box );
        } );
    }

    private static
    void attachToVertex( List<InterLine> boxLines, EventSite e, BoundingBox b ) {
        // iterate every box edge
        boxLines.forEach( l -> {
            Vector intersection = e.bisector.lineIntersect( l );

            HalfEdge dangling = e.edge.origin == null ? e.edge.twin : e.edge;
            if ( intersection != null && // have intersection of this box edge.
                    b.isOnThisBox( intersection ) && // intersection must be on/in the box,
                    // including the ones on the boundary.
                    // the direction of the vector ( intersection - voronoi vertex ) is the same as
                    // that of current breakpoint is moving along
                    MyMath.isPositive( intersection.subtract( dangling.origin ).dot( e.direction ) ) ) {
                attachToVertex( intersection, l.dangling, e.edge, b );
            }
        } );
    }

    private static
    void attachToVertex( Vector intersection, HalfEdge boxEdge,
                         HalfEdge edge, BoundingBox box ) {

        // split the box edge.
        Vertex split = box.split( boxEdge, intersection );
        assert split != null : boxEdge + " " + intersection;
        // select the half-edge with origin vertex assigned.
        edge = edge.origin == null ? edge.twin : edge;

        // connect Voronoi vertex and the split vertex.
        HalfEdges.connect( split, edge, ( ( VoronoiFace ) edge.incidentFace ).site,
                ( ( VoronoiFace ) edge.twin.incidentFace ).site  );
    }

    /**
     * handle cases with no Voronoi Vertex,
     * e.g. all sites are on the same horizontal line at the very beginning.
     * ( All dangling edges are vertical. )
     * or all sites are on the same line.
     * Those case will not have Voronoi vertices generated.
     * */

    private static
    void attachToBox( List<InterLine> boxLines, EventSite e,
                      BoundingBox b ) {

        // every dangling edge ( regarded as a line ) must have
        // two intersection points with the box.
        List<Vertex> splits = new ArrayList<>( 2 );
        boxLines.forEach( l -> {
            Vector intersection = e.bisector.lineIntersect( l );

            if ( intersection != null &&
                    b.isOnThisBox( intersection ) ) {
                Vertex split = b.split( l.dangling, intersection );
                if ( !splits.contains( split ) ) splits.add( split );
            }
        } );

        assert splits.size() == 2 : splits.size();
        // connect the two intersection vertices.
        HalfEdges.connect( splits.get( 0 ), splits.get( 1 ), e.edge,
                ( ( VoronoiFace ) e.edge.incidentFace ).site,
                ( ( VoronoiFace ) e.edge.twin.incidentFace ).site );
    }

    /**
     * visualize the area covering sites, Voronoi vertices and the bounding box.
     * */

    private static
    boolean visualizeIntersection( List<Face> sites, List<InterLine> boxLines,
                                   List<EventSite> danglings,
                                   BoundingBox box, BoundingBox imaginaryBox ) {

        IntersectProgram program = new IntersectProgram( imaginaryBox.findVisualizationArea( BoundingBox.OFFSET ) );

        List<Line> shapes = new ArrayList<>( boxLines.size() + danglings.size() + 1 );
        shapes.addAll( boxLines );

        List<Vector> intersections = new ArrayList<>();
        // iterate every dangling.
        danglings.forEach( e -> {
            assert e.direction != null;

            // iterate every box edge
            boxLines.forEach( l -> {
                Vector intersection = e.bisector.lineIntersect( l );

                if ( intersection != null && // have intersection of this box edge.
                        // intersection must be on/in the box,
                        // including the ones on the boundary.
                        box.isOnThisBox( intersection ) ) {

                    // this dangling is not just ray, but a line.
                    if ( e.edge.origin == null && e.edge.twin.origin == null ) {
                        shapes.add( e.bisector.getSegment( imaginaryBox ) );
                        intersections.add( intersection );
                    }
                    // this dangling is a ray
                    else {
                        HalfEdge edge = e.edge.origin == null ? e.edge.twin : e.edge;
                        // the direction of the vector ( intersection - voronoi vertex ) is the same as
                        // that of current breakpoint is moving along
                        if ( MyMath.isPositive( intersection.subtract( edge.origin ).dot( e.direction ) ) ) {
                            intersections.add( intersection );
                            shapes.add( new Ray( edge.origin, intersection ).getSegment( imaginaryBox ) );
                        }
                    }
                }
            } );
        } );

        List<Vector> sitePoints = new ArrayList<>( sites.size() + 1 );
        sites.forEach( s -> sitePoints.add( ( ( VoronoiFace ) s ).site ) );

        program.draw( shapes, intersections, DrawingProgram.INTERSECTION_COLOR );
        program.draw( null, sitePoints, Color.CYAN );
        program.initialize();

        return true;
    }

    //-------------------------------------------------------
    // HANDLESITEEVENT(pi)
    //-------------------------------------------------------

    /**
     *
     * @param pi site event
     * */

    // Algorithm HANDLESITEEVENT(pi)
    private static
    void handleSiteEvent( EventSite pi, EventRBTreeVoronoi eventQueue,
                          StatusRBTreeVoronoi statusTree ) {

        statusTree.sweepY = pi.siteFace.site.y;

        // 1. If T is empty, insert pi into it
        // ( so that T consists of a single leaf storing pi, <pi, > ) and return.
        if ( statusTree.isEmpty() ) {
            statusTree.initY = statusTree.sweepY;
            statusTree.put( new EventSite( pi.siteFace, EventSite.Type.LEAF ) );
            return;
        }

        // Otherwise, continue with steps 2– 5.
        // 2. Search in T for the arc a vertically above pi.
        VorRBTNode pjNode = statusTree.getNode( pi );
        EventSite pj = pjNode.key; // status
        assert pj.type == EventSite.Type.LEAF;

        // If the leaf representing a has a pointer to a circle event in Q,
        // then this circle event is a false alarm and it must be deleted from Q.
        pj.deleteCircleEvent( eventQueue );

        // 3. Replace the leaf of T that represents a with a subtree having three leaves.
        // The middle leaf stores the new site pi and
        // the other two leaves store the site pj that was originally stored
        // with a Store the tuples <pj, pi> and <pi, pj>
        // representing the new breakpoints at the two new internal nodes.
        // Perform rebalancing operations on T if necessary.

        // 4. Create new half-edge records in the Voronoi diagram structure for the edge separating
        // V(pi) and V(pj), which will be traced out by the two new breakpoints.

        VorRBTNode pjPiNode = pjNode; // avoid confusion
        // 3.1 replace pj with <pj, pi>
        // pjNode => <pj, pi>
        pjPiNode.insertPjPi( pi, pj, statusTree );
        // 3.2.1 put pj of <pj, pi>
        // pj => leaf pj
        statusTree.put( pjPiNode, pj, true );

        // 3.2.2 put pi of <pj, pi>
        // if and only if sites have the same y-coordinate at the very beginning,
        if ( MyMath.isEqual( statusTree.initY, pi.y ) ) {
            // pi => leaf pi
            statusTree.put( pjPiNode, new EventSite( pi.siteFace, EventSite.Type.LEAF ), false );
            // 4.2 the edge( facing pi ) separating V(pi) and V(pj)
            pjPiNode.key.edge.setTwins( new HalfEdge( pjPiNode.key.rightArc ) );
            // set outComponent of the pi Voronoi face.
            pjPiNode.key.rightArc.outComponent = pjPiNode.key.edge.twin;
            // cannot have circle events at this point
            return;
        }

        // 3.3 put <pi, pj>
        EventSite piPj = new EventSite( pjPiNode.key, pi.siteFace, pj.siteFace, pjPiNode.key.bisector );
        statusTree.put( pjPiNode, piPj, false );
        piPj.node.insertPiPj( pjPiNode );

        // 3.4 put pi of <pi, pj>
        // site event => status ( leaf node storing pi )
        pi = new EventSite( pi.siteFace, EventSite.Type.LEAF );
        statusTree.put( piPj.node, pi, true );
        // 3.5 put pj of <pi, pj>
        EventSite pjRight = new EventSite( pj.siteFace, EventSite.Type.LEAF );
        statusTree.put( piPj.node, pjRight, false );

        // 3.6 update nodes in the triple in a circle event, if exists.
        // i.e. update leaf and right triple to the new leaf pj node.
        // since we replace the origin pj node with <pj, pi> node.
        pj.updateTriple( statusTree );
        pj.updateCircleEvent( pjRight );

        // 5. Check the triple of consecutive arcs
        // where the new arc for pi is the left arc to see if the breakpoints converge.
        // If there are newly created circle events,
        // insert the circle event into Q and add pointers between the pj in T and the node in Q.
        // Do the same for the triple where the new arc is the right arc.
        checkTripleLeftSite( pjPiNode, pj.node, pi.node, eventQueue, statusTree );
        checkTripleRightSite( piPj.node, pi.node, pjRight.node, eventQueue, statusTree );
    }

    /**
     * check left triple to see if there is a circle event here. ( Handle site event method limited )
     * Triple check for sites cannot allow overlapping breakpoints ( several sites that coincide ).
     *
     * @param pjPi <pj, pi>
     * @param left status leaf node, pj
     * @param right status leaf node, pi
     * */

    private static
    void checkTripleLeftSite( VorRBTNode pjPi,
                              VorRBTNode left, VorRBTNode right,
                              EventRBTreeVoronoi eventQueue, StatusRBTreeVoronoi statusTree ) {

        // <pk, pj>
        VorRBTNode predecessor = ( VorRBTNode ) statusTree.predecessor( pjPi );

        if ( predecessor != null &&
                EventSite.hasCircleEvent( pjPi.key, predecessor.key, statusTree.sweepY ) ) {
            checkTripleLeftCommon( predecessor, pjPi, left, right, eventQueue, statusTree );
        }
    }

    private static
    void checkTripleLeftCommon( VorRBTNode predecessor, VorRBTNode pjPi,
                                VorRBTNode left, VorRBTNode right,
                                EventRBTreeVoronoi eventQueue, StatusRBTreeVoronoi statusTree ) {

        assert predecessor.key.type == EventSite.Type.INTERNAL && predecessor.key.circleEvent == null;
        VorRBTNode preLeaf = ( VorRBTNode ) statusTree.max( predecessor.left );
        Circle circle = Circles.getCircleByThreePoints( preLeaf.val.siteFace.site, left.val.siteFace.site, right.val.siteFace.site );

        assert circle != null : pjPi + ", " + preLeaf + ", " + left + ", " + right;
        addCircleEvent( predecessor, pjPi, circle, preLeaf, left, right, eventQueue, false );
    }

    /**
     * check left triple to see if there is a circle event here. ( Handle site event method limited )
     *
     * @param preNode predecessor or piPj
     * @param nextNode pjPi or successor
     * @param left left status leaf node (pj) of the triple, <pj, pi, pk>
     * @param middle middle status leaf node (pi) of the triple, <pj, pi, pk>
     * @param right right status leaf node (pk) of the triple, <pj, pi, pk>
     * @param isSpecial is this circle event involving a special case,
     *                  where several sites that coincide and
     *                  current pi is the one that is at the lowest point of the circle.
     *                  In this case, we know for sure that <pj, pi> cannot be merged.
     * */

    private static
    void addCircleEvent( VorRBTNode preNode, VorRBTNode nextNode,
                         Circle circle, VorRBTNode left,
                         VorRBTNode middle, VorRBTNode right,
                         EventRBTreeVoronoi eventQueue, boolean isSpecial ) {

        EventSite circleEvent = new EventSite( circle, circle.getLowest() );
        addTriple( circleEvent, left, middle, right );
        // special case, this circle event needs to store which breakpoint cannot be merged.
        // and notice that this special twin breakpoint can only available for one circle at time.
        // different circle have different twin breakpoint.
        if ( isSpecial ) circleEvent.specTwin = left.key.siteFace;

        assert preNode.key.type == EventSite.Type.INTERNAL && preNode.key.circleEvent == null;
        circleEvent.addInternals( preNode, nextNode );

        eventQueue.put( circleEvent );

        // let the leaf node with this circle event have
        // the ability to know if there are adjacent circle events.
        // i.e. its predecessor and successor also have circle events?
        //           |-> predecessor's circle event
        // leaf node ->  circleEvent
        //           |-> successor's circle event
        left.key.rightCircleEvent = right.key.leftCircleEvent = middle.key.circleEvent = circleEvent.node;
        middle.key.leftCircleEvent = left.key.circleEvent;
        middle.key.rightCircleEvent = right.key.circleEvent;

        // circle event tree node <-> status leaf node.
        circleEvent.leaf = middle;
    }

    /**
     * check left triple to see if there is a circle event here.
     * ( Handle circle event method limited )
     * Triple check for circle events allows overlapping breakpoints
     * ( several sites that coincide ).
     * But in this case, we need to avoid merging twin brekapoint.
     *
     * @param left status leaf node, pj
     * @param right status leaf node, pi
     * */

    private static
    void checkTripleLeftCircle( EventSite gamma, VorRBTNode pjPi,
                                VorRBTNode left, VorRBTNode right,
                                EventRBTreeVoronoi eventQueue, StatusRBTreeVoronoi statusTree ) {

        VorRBTNode predecessor = ( VorRBTNode ) statusTree.predecessor( pjPi );

        if ( predecessor != null &&
                EventSite.hasCircleEvent( gamma, pjPi.key, predecessor.key, statusTree.sweepY ) ) {
            checkTripleLeftCommon( predecessor, pjPi, left, right, eventQueue, statusTree );
        }
    }

    /**
     * check right triple to see if there is a circle event here.
     * ( Handle site event method limited )
     * Triple check for sites cannot allow overlapping breakpoints
     * ( several sites that coincide ).
     *
     * @param left status leaf node, pi
     * @param right status leaf node, pj
     * */

    private static
    void checkTripleRightSite( VorRBTNode piPj,
                               VorRBTNode left, VorRBTNode right,
                               EventRBTreeVoronoi eventQueue, StatusRBTreeVoronoi statusTree ) {

        VorRBTNode successor = ( VorRBTNode ) statusTree.successor( piPj );

        // Degenerate case: a site pi that we process happens to be located exactly below
        // the breakpoint between two arcs on the beach line.
        // In this case the algorithm splits either of these two arcs and
        // inserts the arc for pi in between the two pieces, one of which has zero length.
        // And we define the zero-length arc always on the right.
        // i.e. we always search pj, lying ( overlapping ) on the pi,
        // in the left status subtree,
        // so we only need check the new triple arcs in the right direction, <pi, pj, pk>
        // no need to do so for the left direction, <pk, pi, pj>
        if ( successor != null ) {
            boolean isNormal = EventSite.hasCircleEvent( piPj.key, successor.key, statusTree.sweepY );
            boolean isSpecial = EventSite.hasCircleEvent( piPj.key.leftArc.site, piPj.key.rightArc.site, successor.key.rightArc.site );

            // site is not at the lowest point of the circle formed by the triple.
            if ( isNormal )
                checkTripleRightCommon( successor, piPj, left, right, eventQueue, statusTree, false );
            // site is at the lowest point of the circle formed by the triple.
            // need to remember which twin brekapoint is the one that cannot be merged later.
            else if ( isSpecial )
                checkTripleRightCommon( successor, piPj, left, right, eventQueue, statusTree, true );
        }
    }

    private static
    void checkTripleRightCommon( VorRBTNode successor, VorRBTNode piPj,
                                 VorRBTNode left, VorRBTNode right,
                                 EventRBTreeVoronoi eventQueue, StatusRBTreeVoronoi statusTree, boolean isSpecial ) {

        assert successor.key.type == EventSite.Type.INTERNAL && successor.key.circleEvent == null;
        VorRBTNode sucLeaf = ( VorRBTNode ) statusTree.min( successor.right );
        Circle circle = Circles.getCircleByThreePoints( left.val.siteFace.site, right.key.siteFace.site, sucLeaf.val.siteFace.site );

        addCircleEvent( piPj, successor, circle, left, right, sucLeaf, eventQueue, isSpecial );
    }

    /**
     * check right triple to see if there is a circle event here.
     * ( Handle circle event method limited )
     * Triple check for circle events allows overlapping breakpoints
     * ( several sites that coincide ).
     * But in this case, we need to avoid merging twin brekapoint.
     *
     * @param left status leaf node, pi
     * @param right status leaf node, pj
     * */

    private static
    void checkTripleRightCircle( EventSite gamma, VorRBTNode piPj,
                                 VorRBTNode left, VorRBTNode right,
                                 EventRBTreeVoronoi eventQueue, StatusRBTreeVoronoi statusTree ) {

        VorRBTNode successor = ( VorRBTNode ) statusTree.successor( piPj );

        if ( successor != null &&
                EventSite.hasCircleEvent( gamma, piPj.key, successor.key, statusTree.sweepY ) ) {
            // gamma.specTwin is not null,
            // meaning that future circle events should not merge this breakpoint.
            checkTripleRightCommon( successor, piPj, left, right, eventQueue, statusTree, gamma.specTwin != null );
        }
    }

    /**
     * add the triple of the circle event:
     * <pj, pi, pk> or to say, <pj, pi> <pi,pk>
     * */

    private static
    void addTriple( EventSite circleEvent, VorRBTNode... leaves ) {
        circleEvent.triple.addAll( Arrays.asList( leaves ) );
    }

    //-------------------------------------------------------
    // HANDLECIRCLEEVENT(γ)
    //-------------------------------------------------------

    // Algorithm HANDLECIRCLEEVENT(γ)
    private static
    void handleCircleEvent( EventSite gamma,
                            EventRBTreeVoronoi eventQueue,
                            StatusRBTreeVoronoi statusTree ) {

        statusTree.sweepY = gamma.y;

        // get leaf γ and its predecessor and successor
        VorRBTNode left = gamma.triple.get( 0 );
        VorRBTNode middle = gamma.triple.get( 1 );
        VorRBTNode right = gamma.triple.get( 2 );
        assert left.key.type == EventSite.Type.LEAF : left + ", " + middle + ", " + right;
        assert middle.key.type == EventSite.Type.LEAF : left + ", " + middle + ", " + right;
        assert middle.key.circleEvent != null;
        assert right.key.type == EventSite.Type.LEAF : left + ", " + middle + ", " + right;

        VorRBTNode parent = ( VorRBTNode ) middle.parent;
        // 1.1 Delete the leaf γ that represents the disappearing arc a from T.
        statusTree.delete( middle );
        // delete the circle event involving γ
        middle.deleteCircleEvent();

        // 1.2 Delete all circle events involving a from Q;
        // these can be found using the pointers
        // from the predecessor and the successor of γ in T.
        // ( The circle event where γ is the middle arc is currently being handled,
        // and has already been deleted from Q. )
        left.deleteCircleEvent( eventQueue );
        right.deleteCircleEvent( eventQueue );

        // 1.3 Update the tuples representing the breakpoints at the internal nodes.
        // Perform rebalancing operations on T if necessary.
        // 1.3.1 delete pi's internal parent
        assert parent.key.type == EventSite.Type.INTERNAL && parent.key.circleEvent == null: middle + " " + parent.key;
        statusTree.delete( parent );
        assert statusTree.check();

        // 1.3.2 update another internal node
        // <pj, pi> <pi, pk> => <pj, pk>
        VorRBTNode updated = gamma.update( parent, statusTree );
        // update the new breakpoint after merging two breakpoints,
        // but not update when a circle event is on a site event.
        // This is also a degenerate case, see example: test_1/8
        if ( !updated.key.isOnSweepLine( statusTree.sweepY ) )
            updated.key.updateY( statusTree.sweepY );

        // 2. Add the center of the circle causing the event as a vertex record
        // to the doubly-connected edge list D storing the Voronoi diagram under construction.
        HalfEdge[] originalEdges = gamma.getEdges();
        VoronoiVertex vertex = new VoronoiVertex( gamma.circle );
        vertex.incidentEdge = originalEdges[ 0 ];

        // store this Voronoi vertex.
        eventQueue.addVertex( vertex );

        // Set the pointers between them appropriately.
        // Attach the three new records to the half-edge records that end at the vertex.
        attachToVertex( updated, vertex, originalEdges );

        // 3. Check the new triple of consecutive arcs that has the former left neighbor of as its middle
        // arc to see if the two breakpoints of the triple converge. If so, insert the corresponding circle
        // event into Q. and set pointers between the new circle event in Q and the corresponding leaf
        // of T. Do the same for the triple where the former right neighbor is the middle arc.
        checkTripleLeftCircle( gamma, updated, left, right, eventQueue, statusTree );
        checkTripleRightCircle( gamma, updated, left, right, eventQueue, statusTree );
    }

    private static
    boolean check( Vertex origin, HalfEdge[] originals, HalfEdge merge ) {
        assert originals[ 0 ].origin == origin;
        assert originals[ 1 ].origin == origin;
        assert merge.origin == null || merge.origin == origin : merge + " " + origin;
        assert merge.twin.origin == null || merge.twin.origin == origin : merge.twin + " " + origin;

        return true;
    }

    /**
     * attach dangling edges of the Voronoi vertex
     * and set up their next and pre points correctly
     * */

    private static
    void attachToVertex( VorRBTNode updated,
                         Vertex vertex, HalfEdge[] originalEdges ) {

        // Create two half-edge records corresponding to the new breakpoint of the beach line.
        HalfEdge mergeEdge = new HalfEdge( updated.key.leftArc );
        HalfEdge twin = new HalfEdge( updated.key.rightArc );
        mergeEdge.setTwins( twin );

        updated.key.edge = mergeEdge;

        HalfEdge origin1 = originalEdges[ 0 ];
        HalfEdge origin2 = originalEdges[ 1 ];
        HalfEdge mergeTwin = mergeEdge.twin;

        // set the origin( vertex ) for the three edges
        // according to their faces they attach to
        if ( origin1.origin == null ) origin1.origin = vertex;
        if ( origin2.origin == null ) origin2.origin = vertex;

        HalfEdge[] facesByEdges = Arrays.copyOf( originalEdges, 3 );
        if ( whichEdge( originalEdges, mergeEdge ) ) {
            if ( mergeEdge.origin == null ) mergeEdge.origin = vertex;
            facesByEdges[ 2 ] = mergeEdge;
        }
        else {
            assert whichEdge( originalEdges, mergeTwin ) : vertex + " " + Arrays.toString( originalEdges ) + " " + mergeEdge;
            if ( mergeTwin.origin == null ) mergeTwin.origin = vertex;
            facesByEdges[ 2 ] = mergeTwin;
        }

        // set their twin edges
        setTwins( facesByEdges );
        assert check( vertex, originalEdges, mergeEdge );
    }

    /**
     * set up half-edges according to their incident faces.
     *
     * @param facesByEdges halfEdges already assigned origin
     * */

    private static
    void setTwins( HalfEdge[] facesByEdges ) {
        // Even though the following code looks like brute force,
        // but it's actually O(1),
        // because there are at most 6 half-edges to be attached to the vertex.
        for ( int i = 0; i < facesByEdges.length; i++ ) {
            boolean notFound = true;
            for ( int j = 0; j < facesByEdges.length; j++ ) {
                if ( i == j ) continue;

                if ( facesByEdges[ i ].twin.incidentFace == facesByEdges[ j ].incidentFace ) {
                    // every half-edge can only be attached to another edge once.
                    assert notFound;
                    notFound = false;
                    // be careful with direction
                    facesByEdges[ j ].connect( facesByEdges[ i ].twin );
                }
            }
        }
    }

    /**
     * mergeEdge has the same incidentFaces that the ones in originalEdges have?
     * */

    private static
    boolean whichEdge( HalfEdge[] originalEdges, HalfEdge mergeEdge ) {
        for ( HalfEdge edge : originalEdges )
            if ( mergeEdge.incidentFace == edge.incidentFace )
                return false;

        return true;
    }
}
