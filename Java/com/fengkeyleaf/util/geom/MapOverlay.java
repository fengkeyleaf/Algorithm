package com.fengkeyleaf.util.geom;

/*
 * MapOverlay.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 6/3/2022$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.util.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 * Class to compute the intersection of two subdivisions, Map Overlay.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class MapOverlay {

    /**
     * Class to handle segment intersection( vertex and half-edge, edge and edge ) for map overlay.
     *
     * This class is mainly to get rid of the intersection where new half-edges are created.
     * In this way, we can reduce the complexity of the segment intersection for MapOverlay.
     */

    static class Splitter extends GeometricIntersection {
        // Condition to see if to include an intersection for this splitter
        // Two usages: 1) Assertion; 2) Half-plane intersection.
        static final Predicate<EventPoint2D> sP = i -> i.I.size() == 1 || i.I.size() == 2;
        // Half-edges from the sub-subdivision with mappingID as 0.
        // we will reset the ID to -1 in order to traverse the face to get all cycles.
        final List<HalfEdge> E = new ArrayList<>();

        Splitter() {
            // horizontal sweep line to compute intersection for map overlay.
            // In production environment, no need to actually report intersections.
            super( i -> false, false );
            assert ( c = new Checker() ) != null;
        }

        /**
         * report intersection point and split half-edges.
         *
         * @param L shapes intersecting at p with p as its left endpoint.
         * @param R shapes intersecting at p with p as its right endpoint.
         * @param I shapes intersecting at p and containing p.
         * @return  intersection point with all intersecting shapes involved.
         */

        @Override
        EventPoint2D reportIntersection( List<EventPoint2D> L,
                                         List<EventPoint2D> R,
                                         List<EventPoint2D> I ) {

            EventPoint2D i = super.reportIntersection( L, R, I );

            // intersection with one half-edge and one vertex.
            if ( i.I.size() == 1 ) {
                assert !i.L.isEmpty() || !i.R.isEmpty();
                // split the half-edge.
                split( i.I.get( 0 ), i );
            }
            // intersection with two half-edges.
            else if ( i.I.size() == 2 )
                handleEdges( i );

            assert i.I.size() < 3;
            return i;
        }

        static
        void split( IntersectionShape s, Vector i ) {
            Segment sg = ( Segment ) s;
            assert sg.e.mappingID < 1;
            sg.e.split( i );
            selectEdge( sg );
        }

        /**
         * split two intersecting half-edges.
         *
         * @param i intersection point.
         */

        static
        void handleEdges( EventPoint2D i ) {
            assert i.shapes.size() == 2;

            // split two edges.
            split( i.shapes.get( 0 ), i );
            split( i.shapes.get( 1 ), i );
        }

        /**
         * set the half-edge associated with the segment, {@code s}, to the upper one.
         * This method must be called right after calling {@link HalfEdge#split(Vector)}
         * with no further modification to the half-edge.
         * Otherwise, errors may arise.
         * */

        static
        Segment selectEdge( Segment s ) {
            if ( Vectors.sortByY( s.e.origin, s.e.next.origin ) < 0 )
                s.e = s.e.next;

            assert Vectors.sortByY( s.e.origin, s.e.next.origin ) != 0;
            return s;
        }

        /**
         * reset mappingID of half-edges of the sub-subdivision to -1.
         */

        // TODO: 7/7/2022 put this method into Class Intersector?
        void reset() {
            E.forEach( e -> {
                // ignore removed half-edges.
                if ( e.mappingID < 1 )
                    e.mappingID = -1;
            } );
        }

        //-------------------------------------------------------
        // Class checker.
        //-------------------------------------------------------

        /**
         * Class to check the integrity of Segment intersection algorithm for map overlay.
         *
         * Note that code in this class won't have any effects on the main algorithm.
         */

        static class Checker extends GeometricIntersection.Checker {
            @Override
            boolean check( List<Vector> intersections,
                           List<IntersectionShape> S ) {
                visualization( intersections, S );
                return true;
            }
        }
    }

    /**
     * Class to handle segment intersection( half-edge and half-edge, vertex and vertex ) for map overlay.
     *
     * This class has the following four purposes:
     * 1) Determine main-subdivision and sub-subdivision.
     * Half-edges from the first one will be marked with mappingID as -1,
     * while the other ones will be marked with mapping ID as 0.
     * In this way, we always remove half-edges from the sub-subdivision
     * when handing overlapping half-edges, e.g. two identical subdivisions,
     * we will merge them into one subdivision.
     * 2) Find the half-edge directly left to a vertex.
     * This information is actually useful when we build the graph
     * to compute outComponent and innerComponents for a DCEL face.
     * 3) Compute segment intersection.
     * At this point, we only have one intersection type: vertex and vertex.
     * So we will remove overlapping half-edges and vertices,
     * and re-connect half-edges during the process.
     * 4) merge masters ( Label Faces ).
     * merge masters from two half-edges from different subdivision.
     *
     * Note that sorting points with duality is the key to re-connect half-edges in the implementation.
     * So it is easy to avoid precision issue with this technique
     * compared to the way of using {@link Vertex#firstCounterClockWiseEdge(Vertex)} and similar methods.
     */

    static class Intersector extends Splitter {
        // Condition to see if to include an intersection for this Intersector.
        // Two usages: 1) Assertion; 2) Half-plane intersection.
        static final Predicate<EventPoint2D> iP = i -> !isFromSameSubdivision( i );

        /**
         * constructs to create an intersector
         * to compute segment intersection of the two subdivisions.
         *
         * @param   s subdivision( face ) will be marked as the sub-subdivision,
         *          if the two subdivisions are identical.
         */

        Intersector( Face s ) {
            // horizontal sweep line to compute intersection for map overlay.
            // In production environment, no need to actually report intersections.
            assert ( p = iP ) != null;

            labelEdges( s );
        }

        /**
         * mark all half-edges from the sub-subdivision with mappingID as 0.
         *
         * @param s sub-subdivision.
         */

        void labelEdges( Face s ) {
            s.innerComponents.forEach( edge -> labelEdges( edge.twin.incidentFace ) );

            if ( s.outComponent == null ) return;
            labelEdges( s.outComponent );
        }

        void labelEdges( HalfEdge e ) {
            if ( e.mappingID >= 0 ) return;

            HalfEdge t = e;
            do {
                t.mappingID = 0;
                E.add( t );

                t = t.next;
            } while ( t != e );

            do {
                labelEdges( t.twin );
                t = t.next;
            } while ( t != e );
        }

        /**
         * report intersection point and split half-edges.
         *
         * @param   L shapes intersecting at p with p as its left endpoint.
         * @param   R shapes intersecting at p with p as its right endpoint.
         * @param   I shapes intersecting at p and containing p.
         * @return  intersection point with all intersecting shapes involved.
         */

        @Override
        EventPoint2D reportIntersection( List<EventPoint2D> L,
                                         List<EventPoint2D> R,
                                         List<EventPoint2D> I ) {

            // The override method in the supper class will
            // add the intersection point into the check class.
            // And it has no side-effects on this one.
            EventPoint2D i = super.reportIntersection( L, R, I );
            // Find the half-edge directly left to a vertex.
            setLeft( i );

            // intersection only involved half-edges from one of the subdivision,
            // do nothing.
            if ( isFromSameSubdivision( i ) ) return i;

            // intersection with two overlapping vertices.
            if ( i.I.isEmpty() )
                handleVertices( i );

            // no other possible intersection allowed.
            assert i.I.isEmpty() : i.I;
            return i;
        }

        /**
         * Half-edges incident to the intersection point e, are from the two subdivisions?
         */

        static
        boolean isFromSameSubdivision( EventPoint2D e ) {
            boolean isMinOne = false, isZero = false;

            for ( int i = 0; i < e.shapes.size(); i++ ) {
                Segment s = ( Segment ) e.shapes.get( i );
                // half-edge from the sub-subdivision.
                if ( s.e.mappingID == 0 )
                    isZero = true;
                // half-edge from the main-subdivision.
                else if ( s.e.mappingID == -1 )
                    isMinOne = true;
                // half-edge from the sub-subdivision,
                // but marked as removed, i.e. it's an overlapping one.
                else if ( s.e.mappingID == 1 ) {}
                else assert false;
            }

            assert isMinOne || isZero;
            // only intersection involved two subdivisions need to be handled separately.
            return isMinOne ^ isZero;
        }

        /**
         * Set the half-edge directly left to the intersection
         * to all involved vertices.
         * */

        void setLeft( EventPoint2D i ) {
            HalfEdge l = findEdge( i );
            if ( l == null ) return;

            i.l = l;
            setLeft( i, i.L, l );
            setLeft( i, i.R, l );
        }

        static
        void setLeft( EventPoint2D e, List<IntersectionShape> I, HalfEdge l ) {
            I.forEach( i -> {
                Segment s = ( Segment ) i;
                // skip removed edge.
                if ( s.e.mappingID == 1 ) return;

                assert s.e.origin != null : s;
                if ( s.e.origin.equalsXAndY( e ) )
                    s.e.origin.l = l;
            } );
        }

        /**
         * Find the half-edge directly left to a vertex.
         *
         * @return the left half-edge but make sure that the vertex is also left to it.
         */

        HalfEdge findEdge( EventPoint2D i ) {
            Event l = statusRBTree.lowerVal( i );
            // no left half-edges.
            if ( l == null ) return null;

            // have left half-edges,
            // but need the one to which the vertex is left.
            // take advantage of toLeft test here.
            HalfEdge e = ( ( Segment ) l.shape ).e;
            assert e != null : i + " | " + l;
            assert !MyMath.isEqualZero( Triangles.areaTwo( e.origin, e.next.origin, i ) ) : e + " | " + i;

            return Triangles.toLeft( e.origin, e.next.origin, i ) ? e : e.twin;
        }

        void handleVertices( EventPoint2D i ) {
            assert !i.L.isEmpty() || !i.R.isEmpty();

            List<HalfEdge> E = new ArrayList<>( i.L.size() + i.R.size() );
            wireHalfEdges( E, assembleHalfEdges( E, i ) );
        }

        /**
         * find all half-edges involving the intersection, {@code e}.
         *
         * @param    E target half-edges.
         * @param    e intersection point.
         * @return   DCEL vertex that the intersection point stands for.
         */

        static
        Vertex assembleHalfEdges( List<HalfEdge> E, EventPoint2D e ) {

            List<IntersectionShape> S = new ArrayList<>( e.L.size() + e.R.size() );
            S.addAll( e.L );
            S.addAll( e.R );

            // find the vertex.
            HalfEdge v = null;
            for ( IntersectionShape i : S ) {
                Segment s = ( Segment ) i;
                // removed half-edge,
                // unlink its relationship with the segment.
                if ( s.e.mappingID >= 1 ) {
                    assert s.e.mappingID == 1 : s.e;
                    assert s.e.next == null && s.e.prev == null && s.e.twin == null;
                    s.e = null;
                    continue;
                }

                // half-edge incident to the vertex.
                E.add( s.e );
                // find the vertex from the main-subdivision,
                // if no such vertex found, use the one from the sub-subdivision.
                v = findVertex( e, s.e, v );
            }

            assert v != null : e;
            return v.origin;
        }

        static
        HalfEdge findVertex( Vector i, HalfEdge e, HalfEdge v ) {
            // found the desired one, do thing.
            if ( v != null && v.mappingID < 0 ) return v;

            assert v == null || v.mappingID == 0;
            // find a new one.
            if ( e.origin.equalsXAndY( i ) )
                return e;

            assert e.twin.origin.equalsXAndY( i );
            return e.twin;
        }

        /**
         * re-connect half-edges incident to the vertex, {@code v}.
         */

        static
        void wireHalfEdges( List<HalfEdge> E, Vertex v ) {
            E = HalfEdges.sortInClockWise( E, v );
            E = removeOverlapping( E, v );

            v.connect( E );
            v.resetOrigin();
            assert v.allIncidentEdges() != null;
//            System.out.println( v + ": (" + v.allIncidentEdges().size() + ") " + v.allIncidentEdges() + "\n" );
        }

        /**
         * remove overlapping half-edges.
         *
         * @return half-edges with overlapping ones.
         */

        static
        List<HalfEdge> removeOverlapping( List<HalfEdge> E, Vertex v ) {
            assert E.size() > 1;
            List<HalfEdge> edges = new ArrayList<>( E.size() );
            for ( int i = 0; i < E.size() - 1; i++ ) {
                i += removeOverlapping( E, edges, i, i + 1, v );
                // don't miss the last one.
                if ( i == E.size() - 2 ) edges.add( E.get( E.size() - 1 ) );
            }

            // the first and last cannot be in the same direction,
            // so no need to check them like a loop checking.
            return edges;
        }

        static
        private int removeOverlapping( List<HalfEdge> E,
                                       List<HalfEdge> edges,
                                       int i, int j, Vertex v ) {

            // get half-edges with v as origin,
            // which can make the sorting easier.
            assert E.get( i ).mappingID < 1;
            HalfEdge e1 = E.get( i ).origin.equals( v ) ? E.get( i ) : E.get( i ).twin;
            assert e1.origin.equals( v );
            assert E.get( j ).mappingID < 1 : E.get( j ) + " | " + v;
            HalfEdge e2 = E.get( j ).origin.equals( v ) ? E.get( j ) : E.get( j ).twin;
            assert e2.origin.equals( v );

            // find overlapping.
            if ( v.isSameDirection( e1.twin.origin, e2.twin.origin ) ) {
                removeOverlapping( edges, e1, e2 );
                // skip the next one.
                return 1;
            }

            // no overlapping.
            edges.add( e1 );
            // check the next one.
            return 0;
        }

        static
        void removeOverlapping( List<HalfEdge> E,
                                HalfEdge e1, HalfEdge e2 ) {

            // the following assertion seems useless,
            // but it indicates that we can do the overlapping check with angleTo() in theory,
            // unfortunately, it has precision issue and thus causes unexpected error.
            assert MyMath.isEqualZero( e1.getSegment().getVector().angleTo( e2.getSegment().getVector() ) ) || true;

            if ( !e1.twin.origin.equals( e2.twin.origin ) )
                return;

            // e2 will be removed.
            if ( e1.mappingID < 0 ) {
                assert e2.mappingID == 0 : e1 + " | " + e2;
                removeOverlapping( e1, e2 );
                E.add( e1 );
                return;
            }

            // e1 will be removed.
            assert e2.mappingID < 0 && e1.mappingID == 0;
            removeOverlapping( e2, e1 );
            E.add( e2 );
        }

        /**
         * remove the half-edge and free up its space,
         * also, more importantly, store its master into another half-edge.
         *
         * @param e1 half-edge to be kept.
         * @param e2 half-edge to be removed.
         */

        static
        void removeOverlapping( HalfEdge e1, HalfEdge e2 ) {
            // merge masters.
            e1.addMasters( e1.master, e2.master );
            e1.masters = new HalfEdge[] { e1.master, e2.master };

            // free up space for removed half-edge which are typically overlapping ones.
            assert e2.mappingID == 0;
            // removed half-edge has 1 as its mappingID.
            e2.mappingID = e2.twin.mappingID = 1;
            e2.twin.reset();
            e2.reset();
        }

        /**
         * set the half-edges' mappingID due to the splitting process.
         *
         * Visualization:
         *
         *              e
         * -------------------------------> o
         * o <-------------------------------
         *              e.twin
         *
         *                   same mappingID
         *            e <====================> e.next
         * ----------------------> s ----------------------> o
         * o <--------------------   <-----------------------
         *      e.twin.next <================> e.twin
         *                    same mappingID
         * */

        static
        void setMappingIDs( HalfEdge e ) {
            assert e.mappingID == e.next.twin.mappingID : e.mappingID + " | " + e.twin.mappingID;
            e.next.mappingID = e.next.twin.mappingID;
            e.twin.mappingID = e.mappingID;
        }
    }
    // segment intersection handler.
    Intersector intersector;
    // Check integrity of Map Overlay data structure.
    Checker c;

    /**
     * Computing the overlay of two subdivisions
     *
     * Theorem 2.6
     * Let S1 be a planar subdivision of complexity n1,
     * let S2 be a subdivision of complexity n2, and let n := n1+n2.
     * The overlay of S1 and S2 can be constructed in O(nlogn+ klogn) time,
     * where k is the complexity of the overlay.
     *
     * @param    s1  given as the infinite face of this subdivision, S1.
     *               Self-intersecting points allowed, but half-edges and vertices around it
     *               should be constructed properly.
     * @param    s2  given as the infinite face of this subdivision, S2.
     *               Self-intersecting points allowed, but half-edges and vertices around it
     *               should be constructed properly.
     * @return       The overlay of S1 and S2.
     *               However, return null if one of or both the input subdivisions is/are null.
     * */

    public static
    Face compute( Face s1, Face s2 ) {
        return new MapOverlay().mapOverlay( s1, s2 );
    }

    // TODO: 7/2/2022 clean up GraphVertex for half-edge.
    // TODO: 7/2/2022 store the Graph G into the new subdivision to do the unlink();
    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm MAPOVERLAY(S1,S2)
    // Input. Two planar subdivisions S1 and S2 stored in doubly-connected edge lists.
    // Output. The overlay of S1 and S2 stored in a doubly-connected edge list D.
    Face mapOverlay( Face s1, Face s2 ) {
        if ( s1 == null || s2 == null ) return null;
        c = new Checker( s1, s2 );

        // 1. Copy the doubly-connected edge lists
        // for S1 and S2 to a new doubly-connected edge list D.
        // also set master for each half-edge.
        Face[] faces = copy( s1, s2 );
        assert c.checkCopy( faces );

        assert faces.length == 2;
        // 2. Compute all intersections between edges from S1 and S2
        // with the plane sweep algorithm of Section 2.1.
        // In addition to the actions on T and Q required at the event points, do the following:
        // 2.1 Update D as explained above if the event involves edges of both S1 and S2.
        // (This was explained for the case where an edge of S1 passes through a vertex of S2.)
        // 2.2 Store the half-edge immediately to the left of the event point
        // at the vertex in D representing it.
        computeIntersection( faces );

        // 3. (* Now D is the doubly-connected edge list for O(S1,S2),
        // except that the information about the faces has not been computed yet. *)
        // 4. Determine the boundary cycles in O(S1,S2) by traversing D.
        List<HalfEdge> C = findCycles( faces );

        // 5. Construct the graph G whose nodes correspond to boundary cycles and
        // whose arcs connect each hole cycle to the cycle to the left of its leftmost vertex,
        // and compute its connected components.
        // (The information to determine the arcs of G has been computed in line 2, second item.)
        Graph<GraphVertex> G = constructGraph( C );

        // 6. for each connected component in G
        // 7. do Let C be the unique outer boundary cycle in the component and
        // let f denote the face bounded by the cycle.
        // Create a face record for f,
        // set OuterComponent( f ) to some half-edge of C,
        // and construct the list InnerComponents( f ) consisting of pointers to one half-edge
        // in each hole cycle in the component.
        // Let the IncidentFace() pointers of all half-edges in the cycles point to the face record of f .
        buildLinks( G );
        Face f = G.getVertexByIndex( 0 ).f;
        f.G = G;

        // 8. Label each face of O(S1,S2)
        // with the names of the faces of S1 and S2 containing it, as explained above.
        HalfEdge.resetMappingID( labelFaces( f ) );
        assert Checker.checkLabel( f );

        return c.checkMapOverlay( f );
    }

    /**
     * copy two subdivisions.
     *
     * @return [ copyOfS1(Face), copyOfS2(Face) ]
     */

    static
    Face[] copy( Face s1, Face s2 ) {
        s1.getInners().forEach( innerFace -> innerFace.leader = s1 );
        s2.getInners().forEach( innerFace -> innerFace.leader = s2 );
        assert s1.leader == null && s2.leader == null;

        Face s1Copy = new Face( s1 );
        Face s2Copy = new Face( s2 );

        // set the infinite face of s2 to the one of s1.
        s2Copy.innerComponents.forEach( e -> e.resetIncidentFace( s1Copy ) );
        return new Face[] { s1Copy, s2Copy };
    }

    /**
     * compute segment intersection.
     *
     * @param faces typically, [ copyOfS1(Face), copyOfS2(Face) ]
     */

    // TODO: 7/6/2022 can only run segment intersection algorithm once, not twice?
    void computeIntersection( Face... faces ) {
        // firstly, we compute intersection where a vertex kisses a half-edge,
        // and split the half-edge to get rid of this type of intersection,
        // which can make our life easier.
        List<IntersectionShape> S = getSegments( faces );
        Splitter splitter = new Splitter();
        List<Vector> intersections = splitter.findIntersection( S );
        assert c.checkIntersection( intersections, S );
        // unlink the relationship between the segment and associated half-edge.
        S.forEach( s -> ( ( Segment ) s ).e = null );

        S = getSegments( faces );
        // compute intersection: half-edge and half-edge, vertex and vertex.
        intersector = new Intersector( faces[ 1 ] );
        // enabled assertion, intersections reported.
        // disabled assertion, no intersections reported.
        intersections = intersector.findIntersection( S );
        assert c.checkIntersection( intersections, S );
        S.forEach( s -> ( ( Segment ) s ).e = null );
        // reset the status of half-edges that have been marked as
        // the boundary of the assumed identical face to be removed.
        intersector.reset();
    }

    /**
     * get segments for segment intersection.
     */

    List<IntersectionShape> getSegments( Face[] faces ) {
        // get segments for half-edges.
        List<IntersectionShape> S = new ArrayList<>();
        getSegments( faces[ 0 ], S );
        getSegments( faces[ 1 ], S );
        assert c.checkSegment( S );

        return S;
    }

    static
    void getSegments( Face f, List<IntersectionShape> S ) {
        // go through every inner faces.
        f.innerComponents.forEach( e -> {
            List<HalfEdge> E = new ArrayList<>();

            List<HalfEdge> edges = e.walkAroundEdge();
            // mark the outer boundary as visited.
            edges.forEach( innerEdge -> {
                assert innerEdge.mappingID == -1;
                innerEdge.mappingID = 0;
                E.add( innerEdge );
            } );

            // go through faces bounded by the cycle of e.
            edges.forEach( innerEdge -> getSegments( innerEdge.twin, S, E ) );

            HalfEdge.resetMappingID( E );
        } );
    }

    static
    void getSegments( HalfEdge edge, List<IntersectionShape> S,
                      List<HalfEdge> E ) {

        if ( edge.mappingID >= 0 ) return;

        List<HalfEdge> edges = edge.walkAroundEdge();
        // mark the cycle bounded by edge as visited.
        edges.forEach( e -> {
            Segment s = e.getSegment();
            s.e = e;
            assert e.origin != null;
            S.add( s );

            assert e.mappingID == -1;
            e.mappingID = 0;
            E.add( e );
        } );

        // traverse neighbour half-edges.
        edges.forEach( e -> getSegments( e.twin, S, E ) );

        // go through inner faces contained by the edge.
        edge.incidentFace.innerComponents.forEach( e -> getSegments( e.incidentFace, S ) );
    }

    /**
     * Determine the boundary cycles in O(S1,S2) by traversing D.
     *
     * @param    faces typically, [ copyOfS1(Face), copyOfS2(Face) ]
     * @return   the boundary cycles.
     */

    List<HalfEdge> findCycles( Face... faces ) {
        List<HalfEdge> C = new ArrayList<>();
        List<HalfEdge> E = new ArrayList<>();
        for ( Face f : faces )
            findCycles( f, C, E );

        HalfEdge.resetMappingID( E );
        return C;
    }

    /**
     * @param C cycles.
     * @param E visited half-edges.
     * */

    static
    void findCycles( Face f, List<HalfEdge> C, List<HalfEdge> E ) {
        f.innerComponents.forEach( edge -> {
            // skip removed half-edges.
            if ( edge.mappingID >= 1 ) return;

            findCycles( edge.twin.incidentFace, C, E );
        } );

        if ( f.outComponent == null ) return;
        findCycles( f.outComponent, C, E );
    }

    static
    void findCycles( HalfEdge e, List<HalfEdge> C, List<HalfEdge> E ) {
        if ( e.mappingID >= 0 ) return;

        HalfEdge t = e;
        C.add( t );
        GraphVertex v = new GraphVertex( e );
        // mark the cycle bounded by edge as visited.
        do {
            // every half-edge of the cycle also pointing to the graph vertex.
            t.v = v;
            t.mappingID = 0;
            E.add( t );

            assert t.mappingID < 1 : t;
            assert t.next != null : t;
            t = t.next;
        } while ( t != e );

        // traverse neighbor half-edges.
        do {
            findCycles( t.twin, C, E );
            t = t.next;
        } while ( t != e );
    }

    /**
     * Construct the graph G whose nodes correspond to boundary cycles.
     *
     * @param C  the boundary cycles, the first one is always the graph vertex
     *           presenting the infinite face.
     * @return   the graph G.
     */

    static
    Graph<GraphVertex> constructGraph( List<HalfEdge> C ) {
        Graph<GraphVertex> G = new Graph<>( C.size() + 1 );
        // graph vertex presenting the infinite face.
        GraphVertex inf = new GraphVertex();
        G.add( inf );

        for ( HalfEdge e : C ) {
            G.add( e.v );
            // left then lowest vertex.
            HalfEdge LTL = findLTL( e );
            if ( LTL == null ) continue;

            // link to the infinite face.
            if ( LTL.origin.l == null ) {
                LTL.v.add( inf );
                continue;
            }

            // link to the face bounded the left half-edge.
            LTL.v.add( LTL.origin.l.v );
        }

        return G;
    }

    /**
     * find left then lowest vertex.
     */

    static
    HalfEdge findLTL( HalfEdge e ) {
        // find the leftmost then lowest DCEL vertex.
        HalfEdge LTL = e, t = e.next;
        do {
            assert Vectors.sortByX( LTL.origin, t.origin ) != 0 : LTL + " | " + t;
            if ( Vectors.sortByX( LTL.origin, t.origin ) > 0 )
                LTL = t;

            t = t.next;
        } while ( t != e );

        assert Triangles.areaTwo( LTL.prev.origin, LTL.origin, LTL.next.origin ) != 0;
        // not link for the inner boundary cycle.
        if ( Triangles.toLeft( LTL.prev.origin, LTL.origin, LTL.next.origin ) )
            return null;

        return LTL;
    }

    /**
     * Construct the graph G whose nodes correspond to boundary cycles.
     *
     * @param G the graph G.
     */

    static
    void buildLinks( Graph<GraphVertex> G ) {
        // initialize face for each cycle.
        for ( GraphVertex v : G ) {
            v.addFace();
        }

        // link the relationship of faces.
        for ( GraphVertex v : G ) {
            v.setFace();
        }
    }

    /**
     * @param f infinite face for the new subdivision.
     *          Note that this one is different from the infinite faces of the two copied ones.
     * @return  visited half-edges, later reset their mappingIDs.
     */

    // TODO: 7/2/2022 may be done with GraphVertex.
    static
    List<HalfEdge> labelFaces( Face f ) {
        List<HalfEdge> E = new ArrayList<>();
        if ( f.outComponent != null ) labelFaces( f.outComponent, E );

        f.innerComponents.forEach( e ->
                E.addAll( labelFaces( e.twin.incidentFace ) )
        );

        return E;
    }

    static
    void labelFaces( HalfEdge e, List<HalfEdge> E ) {
        if ( e.mappingID >= 0 ) return;

        e.incidentFace.innerComponents.forEach( edge ->
                E.addAll( labelFaces( edge.twin.incidentFace ) )
        );

        TreeMap<Integer, Face> filter = new TreeMap<>();
        HalfEdge t = e;
        // mark the cycle bounded by edge as visited.
        do {
            assert t.incidentFace == e.incidentFace;
            // not label faces that are the outer boundary.
            t.getMasters().stream().filter( m ->
                    findLTL( m ) == null
            ).forEach( m ->
                    filter.put( m.incidentFace.ID, m.incidentFace )
            );

            assert t.mappingID == -1;
            t.mappingID = 0;
            E.add( t );
            t = t.next;
        } while ( t != e );

        // add parents for the face.
        e.incidentFace.addParents( filter.values() );

        // traverse neighbor half-edges.
        e.walkAroundEdge().forEach( edge -> labelFaces( edge.twin, E ) );
    }

    //----------------------------------------------------------
    // Class Checker
    //----------------------------------------------------------

    /**
     * Class to check the integrity of Map Overlay algorithm.
     *
     * Note that code in this class won't have any effects on the main algorithm.
     */

    static class Checker {
        // data field for checking.
        BoundingBox b;
        int size;
        // segment set from a subdivision.
        List<Line> I;
        // point set from a subdivision.
        List<Vector> P;

        Checker( Face s1, Face s2 ) {
            assert ( b = getBoundingBox( s1, s2 ) ) != null || b == null;
        }

        BoundingBox getBoundingBox( Face s1, Face s2 ) {
            List<Vector> P = new ArrayList<>();
            if ( s1 != null ) getBoundingBox( s1, P );
            if ( s2 != null ) getBoundingBox( s2, P );
            b = BoundingBox.getBox( P, BoundingBox.OFFSET );

            if ( b != null )
                size = Math.abs( ( int ) MyMath.findMaxMinInAbs( b.maxX, b.maxY, b.minX, b.minY )[ 1 ] ) * 2;

            return b;
        }

        static
        void getBoundingBox( Face s, List<Vector> P ) {
            if ( s.outComponent != null )
                getBoundingBox( s.outComponent, P );

            s.innerComponents.forEach( e -> getBoundingBox( e.twin.incidentFace, P ) );
        }

        static
        void getBoundingBox( HalfEdge e, List<Vector> P ) {
            HalfEdge t = e;

            do {
                P.add( t.origin );
                t = t.next;
            } while ( t != e );
        }

        boolean checkCopy( Face[] faces ) {
            if ( faces == null ) return true;

            String title = "MapOverlay: Copy";
            DrawingProgram drawer = new DrawingProgram( title, size, size );
            drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, faces[ 0 ] );
            drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, faces[ 1 ] );
            drawer.initialize();

            return true;
        }

        boolean checkSegment( List<IntersectionShape> S ) {
            String title = "MapOverlay: Intersection Segment";
            DrawingProgram drawer = new DrawingProgram( title, size, size );
            I = new ArrayList<>( S.size() + 1 );
            P = new ArrayList<>( S.size() * 2 + 1 );
            S.forEach( s -> {
                Segment l = ( Segment ) s;
                assert l.e != null;
                I.add( l );
                P.add( l.startPoint );
                P.add( l.endPoint );
            } );
            drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, P );
            drawer.drawLines( DrawingProgram.NORMAL_POLYGON_COLOR, I );
            drawer.initialize();

            return true;
        }

        boolean checkIntersection( List<Vector> intersections,
                                   List<IntersectionShape> S ) {

            String title = "MapOverlay: Intersection Point";
            DrawingProgram drawer = new DrawingProgram( title, size, size );
            drawer.drawLines( DrawingProgram.NORMAL_POLYGON_COLOR, I );
            drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, P );
            drawer.drawPoints( DrawingProgram.INTERSECTION_COLOR, intersections );
            drawer.initialize();

            S.forEach( s -> checkIntersection( ( Segment ) s ) );
            return true;
        }

        static
        void checkIntersection( Segment s ) {
            // half-edge attached to the segment was removed,
            // or no half-edge directly left to the segment.
            if ( s.e.origin == null || s.e.origin.l == null ) return;

            Vector p1 = s.e.origin.l.origin;
            Vector p2 = s.e.origin.l.twin.origin;
            assert checkIntersection( s.e.origin, p1, p2 ) : s.e + " | " + s.e.origin.l;
        }

        static
        boolean checkIntersection( Vector p, Vector p1, Vector p2 ) {
            return Triangles.toLeftRigorously( p1, p2, p ) &&
                    new Line( p1, p2 ).intersect( new Line( 1, p.y, 2, p.y ) )[ 0 ] != null;
        }

        static
        boolean checkLabel( Face s ) {
            List<HalfEdge> E = new ArrayList<>();
            checkLabel( s, E );
            HalfEdge.resetMappingID( E );
            return true;
        }

        static
        void checkLabel( Face s, List<HalfEdge> E ) {
            if ( s.outComponent != null ) checkLabel( s.outComponent, E );

            s.innerComponents.forEach( e -> checkLabel( e.twin.incidentFace, E ) );
        }

        static
        void checkLabel( HalfEdge e, List<HalfEdge> E ) {
            if ( e.mappingID >= 0 ) return;

            e.incidentFace.innerComponents.forEach( edge -> checkLabel( edge.twin.incidentFace, E ) );

            if ( e.incidentFace.parents == null ) {
                assert findLTL( e ) != null;
                return;
            }

            List<HalfEdge> edges = e.walkAroundEdge();

            // each vertex of the cycle bounded by e must be contained by its parent face.
            e.incidentFace.parents.forEach( p -> {
                assert findLTL( p.outComponent ) == null;

                edges.forEach( innerEdge -> {
                    assert p.outComponent == null || p.isOnPolygon( innerEdge.origin ) : innerEdge + " | " + p + " \n" + p.walkAroundEdge();
                } );
            } );

            edges.forEach( edge -> {
                assert edge.mappingID == -1;
                edge.mappingID = 0;
                E.add( edge );
            } );

            edges.forEach( edge -> checkLabel( edge.twin, E ) );
        }

        Face checkMapOverlay( Face s ) {
            assert cleanUp();
            assert visualization( s );
            return s;
        }

        boolean visualization( Face s ) {
            String title = "MapOverlay: Final Result";
            DrawingProgram drawer = new DrawingProgram( title, size, size );
            drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, s );
            drawer.initialize();

            return true;
        }

        /**
         * free up space for the three data sets.
         * */

        private boolean cleanUp() {
            b = null;
            P = null;
            I = null;

            return true;
        }
    }
}
