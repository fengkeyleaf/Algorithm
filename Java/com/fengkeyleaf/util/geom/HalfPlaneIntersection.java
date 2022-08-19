package com.fengkeyleaf.util.geom;

/*
 * HalfPlaneIntersection.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/1/2022$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.util.MyArrays;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Computer the intersection of half-planes.
 *
 * Corollary 4.4 The common intersection of a set of n half-planes in the plane
 * can be computed in O(nlogn) time and linear storage.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public class HalfPlaneIntersection {

    /**
     * Class to handle map overlay for half-plane intersection.
     */

    static class HalfPlaneMapOverLay extends MapOverlay {
        // intersection point set to determine the result of two subdivision.
        // ( all points in this set should be the intersection between two faces )
        // Empty: empty intersection area.
        // One point: intersection area is a point.
        // two points: intersection area is a segment.
        // More than twoL intersection area is a subdivision.
        final TreeSet<Vector> S = new TreeSet<>( Vectors::sortByX );

        /**
         * Compute segment intersection.
         * But we have different intersection selection conditions for Half-plane intersection
         *
         * @param faces typically, [ copyOfS1(Face), copyOfS2(Face) ]
         */

        @Override
        void computeIntersection( Face... faces ) {
            // firstly, we compute intersection where a vertex kisses a half-edge,
            // and split the half-edge to get rid of this type of intersection,
            // which can make our life easier.
            List<IntersectionShape> S = getSegments( faces );
            Splitter splitter = new Splitter();

            // change the condition for collecting intersections for the splitter.
            splitter.setConditions( Splitter.sP );

            List<Vector> Is = splitter.findIntersection( S );
            assert c.checkIntersection( Is, S );
            // unlink the relationship between the segment and associated half-edge.
            S.forEach( s -> ( ( Segment ) s ).e = null );

            S = getSegments( faces );
            // compute intersection: half-edge and half-edge, vertex and vertex.
            intersector = new Intersector( faces[ 1 ] );

            // change the condition for collecting intersections for the intersector.
            intersector.setConditions( Intersector.iP );

            // enabled assertion, intersections reported.
            // disabled assertion, no intersections reported.
            List<Vector> Ii = intersector.findIntersection( S );
            assert c.checkIntersection( Ii, S );
            S.forEach( s -> ( ( Segment ) s ).e = null );
            // reset the status of half-edges that have been marked as
            // the boundary of the assumed identical face to be removed.
            intersector.reset();

            // collect intersections from the two segment intersection algorithms.
            addIntersections( Is );
            addIntersections( Ii );
        }

        private void addIntersections( List<Vector> I ) {
            for ( Vector v : I ) {
                // more than 2 intersections,
                // meaning the intersection is a plane,
                // cannot be a point or a segment.
                // No need to add points into the set anymore.
                if ( S.size() > 2 ) return;
                S.add( v );
            }
        }

        /**
         * Intersection between two convex regions.
         * */

        ConvexRegion intersect( Face f1, Face f2 ) {
            S.clear();

            // have intersection area?
            Face s = extractFace( f1, f2 );
            // yes, return the area.
            if ( s != null ) return new ConvexRegion( s );

            // no, is the area degenerated to point or segment?
            if ( S.isEmpty() ) return null;
            // degenerated to a point.
            else if ( S.size() == 1 ) return new ConvexRegion( S.first() );

            // degenerated to a segment.
            assert S.size() == 2;
            return new ConvexRegion( new Segment( S.first(), S.last() ) );
        }

        /**
         * Extreact the intersection face from the intersection between convex regions.
         *
         * Same logics as {@link BooleanOperations#intersection(Face, Face)}
         * */

        // TODO: 8/16/2022 clean up s1 and s2.
        private Face extractFace( Face s1, Face s2 ) {
            List<Face> F = new ArrayList<>();

            // must use the instance mapOverlay method in this instance
            // to collect all intersection points.
            mapOverlay( s1, s2 ).G.forEach( v -> {
                // exclude cycles with no parents.
                if ( v.f.parents == null ) return;

                // extract the faces in the overlay that are labeled with P1 and P2.
                if ( BooleanOperations.contains( v, s1 ) &&
                        BooleanOperations.contains( v, s2 ) )
                    F.add( v.f );
            } );

            BooleanOperations.Checker.check( s1, s2, F, BooleanOperations.intersectionStr );

            // no intersection area.
            // This only indicate that the area is not plane,
            // but could be a point or a segment.
            if ( F.isEmpty() ) return null;

            // have intersection area.
            assert F.size() == 1;
            return F.get( 0 ).copy();
        }
    }

    /**
     * A convex region can be a point, a segment and a subdivision,
     * and also a half-plane and unbounded convex region can be reduced to
     * a subdivision with a bounding box applied.
     */

    static class ConvexRegion {
        // intersection area is a point.
        final Vector v;
        // intersection area is a segment.
        final Segment s;
        // intersection area is a face,
        // which must be a convex subdivision.
        // Infinite face limited.
        final Face f;

        ConvexRegion( Vector v ) {
            this( v, null, null );
        }

        ConvexRegion( Segment s ) {
            this( null, s, null );
        }

        ConvexRegion( Face f ) {
            this( null, null, f );
        }

        ConvexRegion( Vector v, Segment s, Face f ) {
            this.v = v;
            this.s = s;
            this.f = f;
        }

        /**
         * Deal with different intersection cases.
         * */

        static
        ConvexRegion intersect( ConvexRegion c1, ConvexRegion c2 ) {
            if ( c1 == null || c2 == null ) return null;

            assert c1.check() & c2.check();

            if ( c1.v != null ) return c2.intersect( c1.v );
            if ( c1.s != null ) return c2.intersect( c1.s );

            assert c1.f != null;
            return c2.intersect( c1.f );
        }

        private boolean check() {
            return !( v == null && s == null && f == null );
        }

        //-------------------------------------------------------
        // Vector as the priority
        //-------------------------------------------------------

        ConvexRegion intersect( Vector v ) {
            if ( this.v != null ) return handleVertices( v, this.v );
            if ( s != null ) return handleVertexSeg( v, s );

            assert f != null;
            return handleVertexFace( v, f );
        }

        private static
        ConvexRegion handleVertices( Vector v1, Vector v2 ) {
            return v1.equals( v2 ) ? new ConvexRegion( v1 ) : null;
        }

        private static
        ConvexRegion handleVertexSeg( Vector v, Segment s ) {
            return s.isOnThisSegment( v ) ? new ConvexRegion( v ) : null;
        }

        /**
         * @param f infinite face containing the convex region.
         */

        private static
        ConvexRegion handleVertexFace( Vector v, Face f ) {
            assert f.outComponent == null;
            return f.innerComponents.get( 0 ).twin.incidentFace.isOnConvexHull( v ) ? new ConvexRegion( v ) : null;
        }

        //-------------------------------------------------------
        // Segment as the priority
        //-------------------------------------------------------

        private ConvexRegion intersect( Segment s ) {
            if ( v != null ) return handleVertexSeg( v, s );
            if ( this.s != null ) return handleSegments( this.s, s );

            assert f != null;
            return handleSegFace( f, s );
        }

        private static
        ConvexRegion handleSegments( Segment s1, Segment s2 ) {
            List<IntersectionShape> S = new ArrayList<>();
            S.add( addSegment( s1, -1 ) );
            S.add( addSegment( s2, 0 ) );

            return handleSegments( S );
        }

        private static
        IntersectionShape addSegment( Segment s, int mappingID ) {
            assert s.e == null;
            s.e = new HalfEdge();
            s.e.mappingID = mappingID;
            return s;
        }

        /**
         * We'll use segment intersection algorithm to determine
         * whether two segments or one segment and one face have intersection area.
         * */

        private static
        ConvexRegion handleSegments( List<IntersectionShape> S ) {
            List<Vector> I = i.findIntersection( S );
            // free up space for half-edges.
            S.forEach( s -> ( ( Segment ) s ).e = null );

            // two segments cannot have no more than 1 intersection point,
            // while one segment and one face cannot have no more than 2 intersection points.
            assert I.size() < 3 : I.size() + " | " + I + "\n" + S;
            // no intersection area.
            if ( I.isEmpty() ) return null;
            // intersection area is a point.
            if ( I.size() == 1 ) return new ConvexRegion( I.get( 0 ) );

            // intersection area is a segment.
            assert !I.get( 0 ).equals( I.get( 1 ) );
            return new ConvexRegion( new Segment( I.get( 0 ), I.get( 1 ) ) );
        }

        /**
         * @param f convex region presented in an infinite face.
         * */

        private static
        ConvexRegion handleSegFace( Face f, Segment s ) {
            List<IntersectionShape> S = new ArrayList<>();
            MapOverlay.getSegments( f, S );
            S.add( addSegment( s, 0 ) );

            ConvexRegion r = handleSegments( S );
            // no intersection area.
            if ( r == null ) return null;
            // intersection area is already a segment.
            if ( r.s != null ) return r;

            // only one intersection point found.
            // but we need to test to see if the two endpoints are in inside the convex region.
            assert r.v != null && r.f == null;
            Face c = f.innerComponents.get( 0 ).twin.incidentFace;
            // Yes, one of the endpoints is inside.
            // The intersection area should be a segment, not a point.
            if ( c.isInsideConvexHull( s.startPoint ) )
                return new ConvexRegion( new Segment( s.startPoint, r.v ) );
            if ( c.isInsideConvexHull( s.endPoint ) )
                return new ConvexRegion( new Segment( s.endPoint, r.v ) );

            // No, none of them is inside.
            // The intersection area is a point.
            return r;
        }

        //-------------------------------------------------------
        // Face( Subdivision ) as the priority
        //-------------------------------------------------------

        private ConvexRegion intersect( Face f ) {
            if ( v != null ) return handleVertexFace( v, f );
            if ( s != null ) return handleSegFace( f, s );

            assert this.f != null;
            return handleFaces( this.f, f );
        }

        private static
        ConvexRegion handleFaces( Face f1, Face f2 ) {
            return h.intersect( f1, f2 );
        }
    }

    // bounding box default range: [ -10^8, 10^8 ] x [ -10^8, 10^8 ]
    static final int DEFAULT_SIZE = 100000000;
    // bounding box for the whole intersection area.
    // With this, we can deduce half-plane, unbounded convex region
    // ( all of them are infinite area )
    // to finite area, subdivision.
    BoundingBox b;
    // segment intersection and map overlay handler.
    static final GeometricIntersection i = new GeometricIntersection() {

        // constructor code for anonymous class.
        {
            // only report the intersection points from the two subdivision.
            p =  e -> !MapOverlay.Intersector.isFromSameSubdivision( e );
            assert ( c = new MapOverlay.Splitter.Checker() ) != null;
        }
    };
    static final HalfPlaneMapOverLay h = new HalfPlaneMapOverLay();
    // current result for Half-plane intersection.
    ConvexRegion r;
    // current result type.
    Type t = null;
    // Result type
    public enum Type {
        // intersection area is a point.
        POINT,
        // intersection area is a segment.
        SEGMENT,
        // intersection area is a plane,
        // May be a subdivision, a half-plane,
        // or a unbounded convex region.
        PLANE,
        // no intersection area available.
        EMPTY
    }
    // Checker.
    Checker c;

    /**
     * Create to construct an instance of HalfPlaneIntersection
     * with the default computing area [ -10^8, 10^8 ] x [ -10^8, 10^8 ],
     * centered at the origin.
     */

    public HalfPlaneIntersection() {
        this( DEFAULT_SIZE, DEFAULT_SIZE, Vector.origin );
    }

    /**
     * Create to construct an instance of HalfPlaneIntersection
     * with the default computing area [ -width/2, width/2 ] x [ -height/2, height/2 ],
     * centered at the center.
     */

    public HalfPlaneIntersection( int width, int height, Vector center ) {
        assert ( c = new Checker() ) != null;
        setBoundingBox( width, height, center );
    }

    void setBoundingBox( int width, int height, Vector center ) {
        b = BoundingBox.getBox( center, width, height );
    }

    /**
     * Compute half-plane intersection.
     *
     * @param H  A set H of n half-planes in the plane.
     */

    public void intersect( List<HalfPlane> H ) {
        assert H == null || ( c.H = H ) != null;
        assert c.check();

        t = Type.EMPTY;
        r = H == null || H.isEmpty() ? null : compute( H );

        if ( r == null ) {
            assert c.visualization();
            return;
        }

        if ( r.v != null ) t = Type.POINT;
        else if ( r.s != null ) t = Type.SEGMENT;
        else t = Type.PLANE;

        assert r.f == null || t == Type.PLANE;
        assert c.visualization();
    }

    // Reference resource: http://www.cs.uu.nl/geobook/
    // Algorithm INTERSECTHALFPLANES(H)
    // Input. A set H of n half-planes in the plane.
    // Output. The convex polygonal region C := h∈H h.
    ConvexRegion compute( List<HalfPlane> H ) {
        assert !H.isEmpty();

        // 1. if card(H) = 1
        if ( H.size() == 1 )
            // 2. then C <- the unique half-plane h ∈ H
            return new ConvexRegion( H.get( 0 ).getSubdivision( b ) );

        // 3. else Split H into sets H1 and H2 of size ceiling(n/2) and floor(n/2).
        int mid = MyArrays.mid( 0, H.size() - 1 ); // ( H.size() - 1 - 0 ) / 2 + 0
        // 4. C1 <- INTERSECTHALFPLANES(H1)
        ConvexRegion c1 = compute( H.subList( 0, mid + 1 ) );
        // 5. C2 <- INTERSECTHALFPLANES(H2)
        ConvexRegion c2 = compute( H.subList( mid + 1, H.size() ) );
        // 6. C <- INTERSECTCONVEXREGIONS(C1,C2)
        return Checker.check( ConvexRegion.intersect( c1, c2 ), H );
    }

    //-------------------------------------------------------
    // Result.
    //-------------------------------------------------------

    /**
     * Get current result type.
     *
     * The result should be one of the following four types:
     * 1) A point.
     * 2) A segment.
     * 3) A face( Unbounded area with a bounding box ).
     * 4) Empty ( No intersection area available ).
     *
     * @throws RuntimeException - invoked when this handler hasn't processed any data set yet.
     */

    public Type getResultType() {
        if ( t == null )
            throw new RuntimeException( "No result available at this point." );

        return t;
    }

    static final String ERROR_MESSAGE = "No result available or no intersection area available at this point.";

    public Vector getPoint() {
        if ( t == null || r == null )
            throw new RuntimeException( ERROR_MESSAGE );

        assert r.v != null;
        return r.v;
    }

    public Segment getSeg() {
        if ( t == null || r == null )
            throw new RuntimeException( ERROR_MESSAGE );

        assert r.s != null;
        return r.s;
    }

    public Face getFace() {
        if ( t == null || r == null )
            throw new RuntimeException( ERROR_MESSAGE );

        assert r.f != null;
        return r.f;
    }

    //-------------------------------------------------------
    // Class checker.
    //-------------------------------------------------------

    /**
     * Class to check the integrity of Half-plane intersection algorithm.
     *
     * Note that code in this class won't have any effects on the main algorithm.
     */

    class Checker {
        private static final String TITLE = "Half-plane Intersection";
        private static final Color HALF_PLANE_COLOR = new Color( 238, 238, 238, 80 );
        private static final Color HALF_PLANE_LINE_COLOR = new Color( 200, 200, 200, 150 );
        private static final Color INTERSECTION_COLOR = new Color( 255, 51, 51, 20 );
        // input half-plane set.
        List<HalfPlane> H;

        /**
         * Potential intersection areas must be inside the bounding box {@link HalfPlaneIntersection#b}.
         */

        boolean check() {
            for ( int i = 0; i < H.size(); i++ ) {
                for ( int j = 0; j < H.size(); j++ ) {
                    if ( i == j ) continue;

                    Vector v = H.get( i ).eq.intersect( H.get( j ).eq );
                    // either no intersection
                    assert v == null ||
                            // or the intersection is contained by the convex region.
                            b.outer.innerComponents.get( 0 ).twin.incidentFace.isOnConvexHull( v ) : v;
                }
            }

            return true;
        }

        /**
         * Resulted convex region ( if exists ) must be
         * in the area bounded by the half-planes in H.
         *
         * @param H half-plane set at each recursion level.
         */

        static
        ConvexRegion check( ConvexRegion i, List<HalfPlane> H ) {
            assert isValidIntersection( i, H );
            return i;
        }

        private static
        boolean isValidIntersection( ConvexRegion i, List<HalfPlane> H ) {
            if ( i == null ) return true;

            assert i.check();
            if ( i.v != null ) return isValidIntersection( i.v, H );
            else if ( i.s != null ) return isValidIntersection( i.s, H );

            assert i.f != null;
            return isValidIntersection( i.f.innerComponents.get( 0 ).twin.incidentFace, H );
        }

        // point.
        private static
        boolean isValidIntersection( Vector v, List<HalfPlane> H ) {
            for ( HalfPlane h : H ) {
                assert h.contains( v ) : v;
            }

            return true;
        }

        // segment.
        private static boolean isValidIntersection( Segment s, List<HalfPlane> H ) {
            for ( HalfPlane h : H ) {
                assert h.contains( s.startPoint ) && h.contains( s.endPoint ) : s;
            }

            return true;
        }

        /**
         * @param f face representing the convex region, but the infinite face.
         */

        // face or plane.
        private static
        boolean isValidIntersection( Face f, List<HalfPlane> H ) {
            List<Vertex> V = f.walkAroundVertex();
            for ( HalfPlane h : H ) {
                for ( Vertex v : V ) {
                    assert h.contains( v ) : h + " | " + v + "\n" + f.walkAroundVertex();
                }
            }

            return true;
        }

        boolean visualization() {
            if ( H == null ) return true;

            int size = findVisualizationArea();
            DrawingProgram drawer = new DrawingProgram( TITLE, size, size );

            // visualize each half-plane.
            H.forEach( h -> {
                drawer.drawLines( HALF_PLANE_LINE_COLOR, getLine( h ) );
                drawer.fillPoly( HALF_PLANE_COLOR, h.getSubdivision( b ).innerComponents.get( 0 ).twin.incidentFace );
            } );

            // visualize the result.
            switch ( t ) {
                case POINT -> {
                    System.out.println( "Res(Point): " + r.v );
                    drawer.drawPoints( DrawingProgram.INTERSECTION_COLOR, r.v );
                }
                case SEGMENT -> {
                    System.out.println( "Res(Segment): " + r.s );
                    List<Line> L = new ArrayList<>( 1 );
                    L.add( r.s );
                    drawer.drawLines( DrawingProgram.INTERSECTION_COLOR, L );
                }
                case PLANE -> {
                    System.out.println( "Res(Convex Region): " + r.f.innerComponents.get( 0 ).twin.incidentFace.walkAroundVertex() );
                    drawer.fillPoly( INTERSECTION_COLOR,
                            r.f.innerComponents.get( 0 ).twin.incidentFace );
                }
            }

            drawer.initialize();
            return true;
        }

        /**
         * Get the line for the half-plane h.
         * */

        private List<Line> getLine( HalfPlane h ) {
            TreeSet<Vector> s = new TreeSet<>( Vectors::sortByX );
            b.edges.forEach( e -> s.addAll( Arrays.asList( e.getSegment().intersect( new Line( h.eq ) ) ) ) );

            assert s.size() == 2 : h + " | " + s;
            List<Line> L = new ArrayList<>( 1 );
            L.add( new Line( s.first(), s.last() ) );
            return L;
        }

        // We'll focus on the intersection area if it exists.
        private int findVisualizationArea() {
            List<Vector> P = new ArrayList<>();

            switch ( t ) {
                case POINT -> P.add( r.v );
                case SEGMENT -> {
                    P.add( r.s.startPoint );
                    P.add( r.s.endPoint );
                }
                case PLANE -> P.addAll( r.f.innerComponents.get( 0 ).twin.incidentFace.walkAroundVertex() );
            }

            return P.isEmpty() ? b.width : BoundingBox.getBox( P, BoundingBox.OFFSET ).width;
        }
    }
}
