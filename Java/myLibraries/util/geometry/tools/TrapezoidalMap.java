package myLibraries.util.geometry.tools;

/*
 * TrapezoidalMap.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 10/21/2021$
 */

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.GUI.geometry.convexHull.Program;
import myLibraries.lang.MyMath;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.elements.Trapezoid;
import myLibraries.util.geometry.elements.line.Line;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.graph.SearchStructure;
import myLibraries.util.graph.elements.SearchVertex;
import myLibraries.util.graph.elements.Vertex;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class consists exclusively of static methods
 * that related to Trapezoidal Map
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class TrapezoidalMap {

    // -------------------------------------------
    // drawing part ------------------------------
    // -------------------------------------------

    public static
    void drawTrapezoidalMap( SearchStructure SS, Program drawer, List<Vector> points ) {
        List<Face> faces = new ArrayList<>();
        SS.getAllLeafNodes().forEach( vertex -> {
            faces.add( vertex.trapezoid.getDCEL() );
        } );

        drawTrapezoidalMap( drawer, faces, points,
                DrawingProgram.NORMAL_POLYGON_COLOR, DrawingProgram.NORMAL_POLYGON_COLOR );
    }

    static
    void drawTrapezoidalMap( Program drawer, List<Face> faces,
                             List<Vector> points, Color poly, Color point ) {
        drawer.addPoly( faces, poly );
        drawer.addVertices( points, point );
    }

    public static
    void drawDs( List<SearchVertex> Ds, Program drawer ) {
        List<Face> faces = new ArrayList<>();
        Ds.forEach( vertex -> {
            faces.add( vertex.trapezoid.getDCEL());
        } );

        drawer.addPoly( faces, DrawingProgram.INTERSECTION_COLOR );
    }

    public static
    void drawQuery( SearchVertex d, Vector query, Program drawer ) {
        List<Face> faces = new ArrayList<>();
        faces.add( d.trapezoid.getDCEL() );
        List<Vector> points = new ArrayList<>();
        points.add( query );

        drawTrapezoidalMap( drawer, faces, points,
                DrawingProgram.INTERSECTION_COLOR, DrawingProgram.INTERSECTION_COLOR );
    }

    // -------------------------------------------
    // computational part ------------------------
    // -------------------------------------------

    /**
     * handle p when adding a new segment,
     * this operation will partition
     * the original trapezoid into two parts (left separation ):
     *              |
     * left(origin) | right
     *              |
     *
     * and pass the right into handleQ or handleS
     * */

    public static
    SearchVertex handleP( SearchVertex d, Line line, Stack<SearchVertex> de ) {
        assert d.type == SearchVertex.NodeType.TRAPEZOID;
        Vector p = line.startPoint;
        Vector q = line.endPoint;
        assert Vectors.sortByX( p, q ) <= 0;

        Trapezoid left = d.trapezoid;
        SearchVertex XNodeP = null;
        // normal case: p -> s or p -> q
        if ( !left.leftP.equals( p ) ) {
            // left separation
            Trapezoid right = new Trapezoid( p, left.rightP, left.top, left.bottom );
            Trapezoid.connectRights( right, left );
            left.rightP = p;

            // initialize the x node of P
            XNodeP = new SearchVertex( SearchVertex.NodeType.X_POINT_P, p, line );
            XNodeP.left = d;
            assert d.trapezoid.check();
            XNodeP.right = de == null ? handleQ( right, line, null ) : handleS( right, line, de );

            redirectParents( XNodeP, d );
            return XNodeP;
        }

        // degenerate case, p(x) -> s, or p(x) -> q
        // the left endPoint of the new segment and
        // the old one( the one already added into the map)
        // is the same
        XNodeP = de == null ? handleQ( left, line, null ) : handleS( left, line, de );
        redirectParents( XNodeP, d );
        return XNodeP;
    }

    /**
     * redirect Parents when replacing a leaf node
     * */

    public static
    void redirectParents( SearchVertex newer, SearchVertex older ) {
        // redirect parents and children
        // add parents of newer
        newer.parents.addAll( older.parents );

        // redirect children of newer
        for ( Vertex parent : older.parents ) {
            SearchVertex pa = ( SearchVertex ) parent;
            if ( pa.left == older )
                pa.left = newer;
            else {
                assert pa.right == older : pa + " " + newer;
                pa.right = newer;
            }
        }

        // add parents of the older
        older.parents.clear();
        older.parents.add( newer );
    }

    /**
     * handle q when adding a new segment,
     * this operation will partition
     * the original trapezoid into two parts(right separation ):
     *      |
     * left | right(origin)
     *      |
     *
     * and pass the left into handleQ or handleS
     * */

    public static
    void handleQ( SearchVertex d, Line line, Stack<SearchVertex> de ) {
        SearchVertex newer = handleQ( d.trapezoid, line, de );
        redirectParents( newer, d );
    }

    public static
    SearchVertex handleQ( Trapezoid right, Line line, Stack<SearchVertex> de ) {
        Vector p = line.startPoint;
        Vector q = line.endPoint;
        assert Vectors.sortByX( p, q ) <= 0;

        SearchVertex XNodeQ = null;
        // normal case: q -> s
        if ( !right.rightP.equals( q ) ) {
            // right separation
            Trapezoid middle = new Trapezoid( p, q, right.top, right.bottom );
            Trapezoid.connectLefts( middle, right );
            right.leftP = q;

            // initialize the x node of Q
            XNodeQ = new SearchVertex( SearchVertex.NodeType.X_POINT_Q, q, line );
            XNodeQ.left = handleS( middle, line, de );
            XNodeQ.right = new SearchVertex( right );
            XNodeQ.right.parents.add( XNodeQ );
            assert XNodeQ.right.trapezoid.check();

            return XNodeQ;
        }

        // degenerate case, q(x) -> s
        // the right endPoint of the new segment and
        // the old one( the one already added into the map)
        // is the same
        XNodeQ = handleS( right, line, de );
        return XNodeQ;
    }

    /**
     * handle S when adding a new segment,
     * this operation will partition the original trapezoid into two parts:
     * top(origin)
     * ----------
     *   bottom
     *
     * this method will also handle trimming walls:
     * first, store trapezoids, top and bottom, to be merged later.
     * secondly, merge current top and bottom into previously stored ones.
     * */

    public static
    void handleS( SearchVertex d, Line line, Stack<SearchVertex> de ) {
        SearchVertex newer = handleS( d.trapezoid, line, de );
        redirectParents( newer, d );
    }

    public static
    SearchVertex handleS( Trapezoid middle, Line line, Stack<SearchVertex> de ) {
        Vector p = line.startPoint;
        Vector q = line.endPoint;
        assert Vectors.sortByX( p, q ) <= 0;
        Vector rightP = middle.rightP;

        // horizontal partition
        Trapezoid top = new Trapezoid( p, q, middle.top, line );
        Trapezoid bottom = new Trapezoid( p, q, line, middle.bottom );
        Trapezoid.mergeUppers( top, middle );
        Trapezoid.mergeLowers( bottom, middle );

        // p -> q -> s, no need to trim wall,
        // p -> s, p -> q, s -> s, q -> s need,
        if ( de != null ) {
            // store trapezoids, top and bottom, to be merged later.
            addTrims( top, bottom, rightP, line, de );

            // merge current top and bottom into previously stored ones.
            if ( de.size() > 1 ) {
                SearchVertex trim = trim( de );
                // get trimmed top and bottom
                top = trim.top;
                bottom = trim.bottom;
            }
        }

        // initialize the y node
        SearchVertex YNode = new SearchVertex( line );
        // top trapezoid has been added before?
        if ( top.vertex == null )
            // no, create a new one
            YNode.left = new SearchVertex( top );
        else  {
            // yes, the y node points to it
            assert top.vertex.trapezoid == top;
            YNode.left = top.vertex;
        }
        YNode.left.parents.add( YNode );

        // top trapezoid has been added before?
        if ( bottom.vertex == null )
            // no, create a new one
            YNode.right = new SearchVertex( bottom );
        else {
            // yes, the y node points to it
            assert bottom.vertex.trapezoid == bottom;
            YNode.right = bottom.vertex;
        }
        YNode.right.parents.add( YNode );

        return YNode;
    }

    /**
     * First process of trimming walls:
     * store trapezoids, top and bottom, to be merged later.
     * */

    public static
    void addTrims( Trapezoid top, Trapezoid bottom, Vector rightP,
                          Line line, Stack<SearchVertex> de  ) {
        SearchVertex trim = new SearchVertex( SearchVertex.NodeType.TRIMMING );

        double res = Triangles.areaTwo( line.startPoint, line.endPoint, rightP );
        // if rightP lies above s, trim lower wall
        if ( MyMath.isPositive( res ) ) {
            bottom.rightP = null;
            top.rightP = rightP;
            trim.isTrimmingTop = false;
        }
        // if rightP lies below s, trim upper wall
        else if ( MyMath.isNegative( res ) ) {
            // assert rightP lies below s
            bottom.rightP = rightP;
            top.rightP = null;
            trim.isTrimmingTop = true;
        }
        // if rightP lies on s, do nothing
        // this happens when handling Q,
        // just trim wall, no need to add ones to be trimmed

        trim.top = top;
        trim.bottom = bottom;
        de.add( trim );
    }

    static
    boolean check( Trapezoid prev, Trapezoid current ) {
        assert prev.top == current.top;
        assert prev.bottom == current.bottom;
        return true;
    }

    /**
     * Second process of trimming walls:
     * secondly, merge current top and bottom into previously stored ones.
     *
     * */

    public static
    SearchVertex trim( Stack<SearchVertex> de ) {
        assert de.size() == 2;
        SearchVertex cur = de.pop();
        SearchVertex prev = de.pop();
        assert cur.type == SearchVertex.NodeType.TRIMMING;
        assert prev.type == SearchVertex.NodeType.TRIMMING;

        // trimming top
        if ( prev.isTrimmingTop ) {
            assert check( prev.top, cur.top );
            // merge tops
            Trapezoid.mergeRights( prev.top, cur.top );
            // current top was eaten by previous top
            cur.top = prev.top;
            // link bottoms
            Trapezoid.setUppers( prev.bottom, cur.bottom );
            cur.bottom.leftP = prev.bottom.rightP;
            // at this point, prev.bottom has been all set up
            assert prev.bottom.check();
        }
        // trimming bottom
        else {
            assert check( prev.bottom, cur.bottom );
            // merge bottoms
            Trapezoid.mergeRights( prev.bottom, cur.bottom );
            // current bottom was eaten by previous bottom
            cur.bottom = prev.bottom;
            // link tops
            Trapezoid.setLowers( prev.top, cur.top );
            cur.top.leftP = prev.top.rightP;
            // at this point, prev.top has been all set up
            assert prev.top.check();
        }

        de.push( cur );
        return cur;
    }
}
