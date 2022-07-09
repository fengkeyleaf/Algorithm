package com.fengkeyleaf.util.geom;

/*
 * Delaunay.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 4/3/2022$
 */

import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.io.MyWriter;
import com.fengkeyleaf.util.CompareElement;
import com.fengkeyleaf.util.MyCollections;
import com.fengkeyleaf.util.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class consists exclusively of static methods
 * that related to Delaunay Triangulation.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class Delaunay {

    /**
     * get Delaunay triangulation of the point set P.
     *
     * Theorem 9.12
     * The Delaunay triangulation of a set P of n points in the plane
     * can be computed in O(nlogn) expected time, using O(n) expected storage.
     * 
     * @param  P point set P.
     * @return an infinite face covering delaunay triangles.
     * */

    // Reference resource:http://www.cs.uu.nl/geobook/
    // Algorithm DELAUNAYTRIANGULATION(P)
    // Input. A set P of n+1 points in the plane.
    // Output. A Delaunay triangulation of P.
    public static
    Face delaunayTriangulation( List<Vertex> P ) {
        if ( P == null || P.isEmpty() ) return null;

        // remove duplicate points
        P = MyCollections.removeDuplicates( P, Vectors::sortByX );

        // 1. Let p0 be the lexicographically highest point of P, that is,
        // the rightmost among the points with largest y-coordinate.
        Vertex p0 = CompareElement.max( Vectors::sortByY, P ); // P cannot be null.
        // set p0's index to 0.
        p0.mappingID = 0;
        // remove it from the point set P.
        List<Vertex> points = new ArrayList<>( P.size() - 1 );
        P.forEach( p -> {
            if ( !p0.equals( p ) ) points.add( p );
        } );

        // 2. Let p−1 and p−2 be two points in R2 sufficiently far away
        // and such that P is contained in the triangle p0P−1P−2.

        // The idea of choosing p-1 and p-2:
        // In the following, we will say that p = (xp,yp) is higher than q = (xq,yq)
        // if yp > yq or yp = yq and xq > xp,
        // and use the (lexicographic) ordering on P induced by this relation.
        // Let l−1 be a horizontal line lying below the entire set P,
        // and let l−2 be a horizontal line lying above P.
        // Conceptually, we choose p−1 to lie on the line l−1 sufficiently far to
        // the right that p−1 lies outside every circle defined by
        // three non-collinear points of P,
        // and such that the clockwise ordering of the points of P around p−1 is
        // identical to their (lexicographic) ordering.
        Vertex pMin1 = new Vertex( 10, -10 );
        pMin1.mappingID = -1; // may be redundant
        // Next, we choose p−2 to lie on the line l−2 sufficiently far to
        // the left that p−2 lies outside every circle defined by
        // three non-collinear points of P∪{p−1},
        // and such that the counterclockwise ordering of the points of P∪{p−1} around p−2 is
        // identical to their (lexicographic) ordering.
        Vertex pMin2 = new Vertex( -10, 10 );
        pMin2.mappingID = -2;

        // 3.1 Initialize T as the triangulation consisting of the single triangle p0P−1P−2.
        List<Vertex> vertices = new ArrayList<>( 3 );
        // counter-clock wise order. p-1 -> p0 -> p-2
        vertices.add( pMin1 );
        vertices.add( p0 );
        vertices.add( pMin2 );
        // triangle p0P-1P02 -> faces[ 1 ]
        Face[] faces = null;
        try {
            faces = Polygons.getDCEL( vertices, DelaunayFace.class.getDeclaredConstructor() );
        } catch ( NoSuchMethodException | SecurityException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        // 3.2 initialize point location data structure D.
        DelaunaySearch D = new DelaunaySearch( faces );

        // 4. Compute a random permutation p1, p2, ... , pn of P\{p0}.
        MyCollections.randomPermutation( points );

        assert writeFile( points );
        // 5. for r <- 1 to n
        for ( int i = 0; i < points.size(); i++ ) {
            Vertex pr = points.get( i );
            // set others' indices to positive numbers.
            pr.mappingID = i + 1;

            // 6. do (* Insert pr into T: *)
            // 7. Find a triangle piPjPk ∈ T containing pr.
            DelaunayVertex res = D.get( pr );
            assert res.triangle != null : res;

            if ( res.isInside )
                // 8. if pr lies in the interior of the triangle piPjPk
                insideTriangle( pr, res.triangle, D.outer );
            else
                // 13. else (* pr lies on an edge of piPjPk, say the edge piPj *)
                onEdge( pr, res.edge, D.outer );

            res.isInside = false;
        }

        // visualize delaunay triangulation with p-1 and p-2.
        assert visualization( P, D );

        // 19. Discard p−1 and p−2 with all their incident edges from T.
        pMin1.allOutGoingEdges().forEach( HalfEdge::delete );
        pMin2.allOutGoingEdges().forEach( HalfEdge::delete );

        // 20. return T
        return check( D, P );
    }

    /**
     * write current random permutation of the points excluding p0 to a file.
     * Debugger purpose.
     * */

    private static
    boolean writeFile( List<Vertex> points ) {
        StringBuilder text = new StringBuilder( points.size() + "\n" );
        points.forEach( p -> text.append( ( int ) p.x ).append( " " ).append( ( int ) p.y ).append( "\n" ) );

        // file path only limited to IDEA.
        MyWriter.writeToFile( "src/CGTsinghua/PA_3/problem_1/0", text.toString() );
        return true;
    }

    /**
     * visualize delaunay triangulation with p-1 and p-2.
     * */

    private static
    boolean visualization( List<Vertex> P, DelaunaySearch D ) {
        List<Vector> points = new ArrayList<>( P.size() + 1 );
        points.addAll( P );
        BoundingBox b = BoundingBox.getBoundingBox( points, 10 );
        if ( b == null ) return true;

        DrawingProgram drawer = new DrawingProgram( "Conceptual Delaunay Triangulation", b.width, b.height );

        drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, points );
        Face.getInners( P, D.outer ).forEach( f -> drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f ) );

        drawer.initialize();
        return true;
    }

    /**
     * split a triangle into three.
     * */

    private static
    void insideTriangle( Vertex pr, Face f, Face outer ) {
        // get three vertices of the triangle.
        List<HalfEdge> edges = f.walkAroundEdge();
        assert edges.size() == 3;
        // fix the status of current triangle before splitting.
        assert ( ( DelaunayFace ) f ).vertex != null : pr;
        DelaunayVertex v = ( ( DelaunayFace ) f ).vertex.fix();
        assert v.children.isEmpty() : pr + " | " + f + " | " + v;

        // 9. then Add edges from pr to the three vertices of piPjPk,
        // thereby splitting piPjPk into three triangles and add them into D.
        insideTriangle( pr, f, edges, v );

        // 10. LEGALIZEEDGE( pr, piPj, T )
        legalizeEdge( pr, edges.get( 0 ), outer );
        // 11. LEGALIZEEDGE( pr, pjPk, T )
        legalizeEdge( pr, edges.get( 1 ), outer );
        // 12. LEGALIZEEDGE( pr, pkPi, T )
        legalizeEdge( pr, edges.get( 2 ), outer );
    }

    /**
     * splitting piPjPk into three triangles and add them into D.
     *
     * @param f triangle piPjPk
     * @param edges pk -> pi -> pj, pkPi -> piPj -> pjPk
     * @param v vertex containing triangle piPkPk in the point location structure.
     * */

    private static
    void insideTriangle( Vertex pr, Face f,
                         List<HalfEdge> edges,
                         DelaunayVertex v ) {

        HalfEdge e1 = edges.get( 0 ); // pkPi
        HalfEdge e2 = edges.get( 1 ); // piPj
        HalfEdge e3 = edges.get( 2 ); // pjPk

        // 9.1 first connect the first vertex and pr, no new face generated.
        HalfEdge e4 = pr.getEdges( e1.origin );
        pr.incidentEdge = e4;
        e1.connect( e4 );
        e4.twin.connect( e3 );

        // 9.2 connect other two vertices, and two new faces generated.
        HalfEdge e5 = pr.getEdges( e2.origin );
        e4.connect( e5.twin );
        e2.connect( e5 );
        e5.twin.connect( e1 );
        insideTriangle( f, e4, v );

        HalfEdge e6 = pr.getEdges( e3.origin );
        e5.connect( e6.twin );
        e6.twin.connect( e2 );
        insideTriangle( new DelaunayFace(), e5, v );

        e6.connect( e4.twin );
        e3.connect( e6 );
        insideTriangle( new DelaunayFace(), e6, v );
    }

    /**
     * reset incident faces pf half-edges bounding f,
     * and add newly created triangle into
     * the structure D as the children of vertex v.
     *
     * @param f newly created triangle face.
     * @param e half-edge of f's outComponent.
     * @param v vertex of the parent triangle.
     * */

    private static
    DelaunayVertex insideTriangle( Face f, HalfEdge e, DelaunayVertex v ) {
        f.outComponent = e;
        f.resetIncidentFace();
        assert f.walkAroundEdge() != null;
        return v.add( f );
    }

    /**
     * split two triangles into four.
     *
     * @param e pjPi
     * @param outer infinite face
     * */

    private static
    void onEdge( Vertex pr, HalfEdge e, Face outer ) {
        // impossible to have pr lying on piP-1 or piP-2.
        assert pr.mappingID > 0;
        assert e.origin.mappingID >= 0 && e.twin.origin.mappingID >= 0 : e + " | " + e.origin.mappingID + " " + e.twin.origin.mappingID;

        // get the four half-edges bounding two adjacent triangles.
        HalfEdge[] edges = onEdge( e );
        assert edges.length == 4;

        // splitting process.
        onEdge( pr, e, edges );

        // 15. LEGALIZEEDGE( pr, piPl, T )
        legalizeEdge( pr, edges[ 0 ], outer );
        // 16. LEGALIZEEDGE( pr, plPj, T )
        legalizeEdge( pr, edges[ 1 ], outer );
        // 17. LEGALIZEEDGE( pr, pjPk, T )
        legalizeEdge( pr, edges[ 2 ], outer );
        // 18. LEGALIZEEDGE( pr, pkPi, T )
        legalizeEdge( pr, edges[ 3 ], outer );
    }

    /**
     * get the four half-edges bounding two adjacent triangles.
     * */

    private static
    HalfEdge[] onEdge( HalfEdge e ) {
        HalfEdge[] edges = new HalfEdge[ 4 ];
        edges[ 0 ] = e.next;
        edges[ 1 ] = e.next.next;
        edges[ 2 ] = e.twin.next;
        edges[ 3 ] = e.twin.next.next;

        return check( e, edges );
    }

    private static
    HalfEdge[] check( HalfEdge e, HalfEdge[] edges ) {
        assert edges[ 0 ] != null;
        assert edges[ 0 ].next.next == e : Arrays.toString( edges );
        assert edges[ 2 ] != null && edges[ 2 ].next.next == e.twin;
        return edges;
    }

    // splitting process.
    private static
    void onEdge( Vertex pr, HalfEdge e,
                 HalfEdge[] edges ) {

        DelaunayFace f1 = ( ( DelaunayFace ) e.incidentFace );
        DelaunayVertex v1 = f1.vertex.fix();
        assert f1.vertex.children.isEmpty();
        DelaunayFace f2 = ( ( DelaunayFace ) e.twin.incidentFace );
        DelaunayVertex v2 = f2.vertex.fix();
        assert f2.vertex.children.isEmpty();

        HalfEdge e1 = edges[ 0 ]; // piPl
        HalfEdge e2 = edges[ 1 ]; // plPj
        HalfEdge e3 = edges[ 2 ]; // pjPk
        HalfEdge e4 = edges[ 3 ]; // pkPi

        // 14. Add edges from pr to pk and to
        // the third vertex pl of the other triangle
        // that is incident to piPj,
        // thereby splitting the two triangles incident to piPj into four triangles.
        // And add them into D.
        e.split( pr );

        // 14.1 split the face to which e is incident
        HalfEdge e5 = pr.getEdges( e2.origin );
        e.next.connect( e5.twin );
        e5.twin.connect( e1 );
        insideTriangle( new DelaunayFace(), e5.twin, v1 ); // f3

        e5.connect( e );
        e2.connect( e5 );
        insideTriangle( f1, e5, v1 );

        // 14.2 split the face to which e.twin is incident
        HalfEdge e6 = pr.getEdges( e4.origin );
        e6.connect( e.twin.prev );
        e4.connect( e6 );
        insideTriangle( f2, e6, v2 );

        e.twin.connect( e6.twin );
        e6.twin.connect( e3 );
        insideTriangle( new DelaunayFace(), e6.twin, v2 ); // f4
    }

    /**
     * flip illegal edges.
     *
     * @param e the face that e is incident to must be the one closer to pr.
     * @param outer the infinite face.
     * */

    // LEGALIZEEDGE(pr, piPj, T)
    private static
    void legalizeEdge( Vertex pr, HalfEdge e, Face outer ) {
        // no triangle incident to e.incidentFace.
        if ( e.twin.incidentFace == outer ) return;

        // 1. (* The point being inserted is pr,
        // and piPj is the edge of T that may need to be flipped. *)
        HalfEdge[] edges = onEdge( e );
        HalfEdge e1 = edges[ 0 ]; // pjPr
        HalfEdge e2 = edges[ 1 ]; // prPi
        HalfEdge e3 = edges[ 2 ]; // piPk
        HalfEdge e4 = edges[ 3 ]; // pkPj

        // 2. if piPj is illegal
        // 3. then Let piPjPk be the triangle adjacent to prPiPj along piPj.
        // boolean isInCircle( pr, pk, pi, pj )
        if ( isInCircle( pr, e4.origin, e1.origin, e3.origin ) ) {
            // 4. (* Flip pipj: *) Replace piPj with prPk.
            legalizeEdge( e, e1, e2, e3, e4 );

            // 5. LEGALIZEEDGE(pr, piPk, T)
            legalizeEdge( pr, e3, outer );
            // 6. LEGALIZEEDGE(pr, pkPj,T)
            legalizeEdge( pr, e4, outer );
        }
    }

    /**
     * flipping-edge process
     *
     * Notice that pr is not necessarily the one we just inserted.
     *
     * @param e piPj.
     * @param e1 pjPr.
     * @param e2 prPi.
     * @param e3 piPk.
     * @param e4 pkPj.
     **/

    private static
    void legalizeEdge( HalfEdge e, HalfEdge e1, HalfEdge e2,
                       HalfEdge e3, HalfEdge e4 ) {

        DelaunayFace f1 = ( DelaunayFace ) e.incidentFace;
        DelaunayVertex v1 = f1.vertex.fix();
        assert f1.vertex.children.isEmpty();
        DelaunayFace f2 = ( DelaunayFace ) e.twin.incidentFace;
        DelaunayVertex v2 = f2.vertex.fix();
        assert f2.vertex.children.isEmpty();

        // first delete e
        HalfEdge twin = e.twin;
        Face[] faces = e.delete();
        e.setTwins( twin );

        // and then connect e2.origin and e4.origin.
        e4.connect( e );
        e.connect( e1 );
        f1 = ( DelaunayFace ) faces[ 0 ];
        e1.origin.incidentEdge = e1.origin.incidentEdge == e.twin ? e1 : e1.origin.incidentEdge;
        e.origin = e2.origin;
        v2.add( insideTriangle( f1, e, v1 ) );

        e2.connect( e.twin );
        e.twin.connect( e3 );
        f2 = ( DelaunayFace ) faces[ 1 ];
        e3.origin.incidentEdge = e3.origin.incidentEdge == e ? e3 : e3.origin.incidentEdge;
        e.twin.origin = e4.origin;
        v1.add( insideTriangle( f2, e.twin, v2 ) );

        assert v1.children.size() == 2 : v1.children.size() + " " + v2.children.size();
        assert v1.children.size() == v2.children.size();
    }

    /**
     * inCircle with the ability to handle p-1 and p2.
     * */

    static
    boolean isInCircle( Vector pr, Vector pk,
                        Vector pi, Vector pj ) {

        // The indices i, j, k, l are all non-negative.
        // This is the normal case;
        // none of the points involved in the test is treated symbolically.
        // Hence, pi pj is illegal if and only if
        // pl lies inside the circle defined by pi, pj, and pk.
        if ( nonNegative( pr, pk, pj, pi ) )
            return Circles.inCircle( pr, pj, pi, pk ) > 0;

        // piPj is an edge of the triangle p0P−1P−2.
        // These edges are always legal.
        if ( Math.max( pi.mappingID, pj.mappingID ) <= 0 ) return false;

        // All other cases.
        // In this case, pi pj is legal if and only if min(k, l) < min(i, j).
        int left = Math.min( pr.mappingID, pk.mappingID );
        int right = Math.min( pi.mappingID, pj.mappingID );
        // min(k, r) < min(i, j),
        // or min(k, r) < 0 && min(i, j) < 0
        if ( left < 0 && right < 0 || left < right ) return false;

        List<Vector> points = MyCollections.sort( Comparator.comparingInt( v -> v.mappingID ),
                                                    pi, pj, pk, pr );

        // get the point with negative index.
        Vector p = points.remove( 0 );
        assert points.size() == 3;
        points.sort( Vectors::sortByY );

        // notice that we won't flip when pi, pj and pk form a convex hull,
        // where i, j and k >= 0.
        if ( p.mappingID == -1 )
            return !Triangles.toLeft( points.get( 0 ), points.get( 1 ), points.get( 2 ) );

        assert p.mappingID == -2 : pr.mappingID + ", " + pk.mappingID + ", " + pi.mappingID + ", " + pj.mappingID;
        return !Triangles.toLeft( points.get( 2 ), points.get( 1 ), points.get( 0 ) );
    }

    private static
    boolean nonNegative( Vector pr, Vector pk, Vector pi, Vector pj ) {
        return pr.mappingID >= 0 && pk.mappingID >= 0 &&
                pj.mappingID >= 0 && pi.mappingID >= 0;
    }

    //-------------------------------------------------------
    // Check integrity of Delaunay Triangulation data structure.
    //-------------------------------------------------------

    private static
    Face check( DelaunaySearch D, List<Vertex> P ) {
        BoundingBox b = new BoundingBox( D.outer );
        // let the infinite face contain all triangles.
//        b.resetInnerFaces( P.size() < 3 ? new ArrayList<>() : Face.getInners( P, D.outer ) );
//        b.F = P.size() < 3 ? new ArrayList<>() : D.outer.getInners();

        // Check integrity of Delaunay Triangulation
        assert check( P );
        assert voronoiCheck( P );

        Node.resetMappingID( P );
        return D.outer;
    }

    private static
    boolean check( List<Vertex> P ) {
        if ( P.size() < 3 ) return true;

        // take advantage of DFS to do the checking.
        // i.e. regard DCEL as some type of graph.
        P.get( 0 ).mappingID = 1;
        check( P.get( 0 ), P );

        return true;
    }

    private static
    void check( Vertex v, List<Vertex> vertices ) {
        // base case
        if ( v.mappingID > 0 ) return;
        v.mappingID = 1;

        // recursive process
        List<HalfEdge> outGoings = v.allOutGoingEdges();
        outGoings.forEach( e -> {
            isOnlyThree( e.incidentFace, vertices );
            isOnlyThree( e.twin.incidentFace, vertices );
            check( e.next.origin, vertices );
        } );
    }

    // Theorem 9.6 Let P be a set of points in the plane.
    // (i) Three points pi, pj, pk ∈ P are
    // vertices of the same face of the Delaunay graph of P
    // if and only if the circle through pi, pj, pk contains
    // no point of P in its interior.
    private static
    void isOnlyThree( Face f, List<Vertex> vertices ) {
        List<Vertex> three = f.walkAroundVertex();
        List<Vertex> removed = new ArrayList<>( vertices );
        three.forEach( removed::remove );

        // all other vertices lying outside the circle formed by current three vertices.
        removed.forEach( v -> {
            assert Circles.inCircle( three.get( 0 ), three.get( 1 ), three.get( 2 ), v ) < 0;
        } );
    }

    // TODO: 4/6/2022 verify (ii), but how to do it?
    // (ii) Two points pi, pj ∈ P form an edge of the Delaunay graph of P if and only
    // if there is a closed disc C that contains pi and pj on its boundary and does
    // not contain any other point of P.

    // Although, it's difficult to verify (ii),
    // but in the following, we'll compute corresponding Voronoi diagrams
    // to verify the delaunay triangulation.
    // TODO: 4/15/2022 zero-length edge verification, but how to do it?
    private static
    boolean voronoiCheck( List<Vertex> vertices ) {
        List<Vector> siteFaces = new ArrayList<>( vertices.size() );
        vertices.forEach( v -> siteFaces.add( v ) );

        visualization( siteFaces );
        return true;
    }

    private static
    void visualization( List<Vector> sites ) {
        // visualization verification.
        BoundingBox b = VoronoiDiagrams.voronoiDiagrams( sites );
        DrawingProgram program = new DrawingProgram( "Dual Graph: Voronoi Diagrams", b.width, b.width );
        program.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, sites );
        b.outer.innerComponents.forEach( e -> program.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, e.twin.incidentFace ) );

        List<Circle> circles = new ArrayList<>( b.vertices.size() + 1 );
        b.vertices.forEach( v -> circles.add( v.circle ) );
        program.drawCircles( VoronoiDiagrams.vertexCircleColor, circles );

        program.initialize();

        // computational verification.
        voronoiCheck( b );
    }

    private static
    void voronoiCheck( BoundingBox b ) {
        b.vertices.forEach( v -> {
            List<HalfEdge> outGoings = v.allOutGoingEdges();

            // every Voronoi edge indicate a delaunay triangulation edge between two sites.
            outGoings.forEach( e -> {
                VoronoiFace f1 = ( VoronoiFace ) e.incidentFace;
                VoronoiFace f2 = ( VoronoiFace ) e.twin.incidentFace;
                assert f1 != f2;

                // does f1.site connect to f2.site?
                // i.e. do they form a delaunay triangulation edge?
                assert voronoiCheck( ( Vertex ) f1.site, ( Vertex ) f2.site );
            } );
        } );
    }

    static
    boolean voronoiCheck( Vertex v1, Vertex v2 ) {
        assert v1.incidentEdge != null : v1;

        for ( HalfEdge e : v1.allIncomingEdges() )
            if ( e.origin == v2 ) return true;

        return false;
    }
}
