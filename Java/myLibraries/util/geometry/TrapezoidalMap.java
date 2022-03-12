package myLibraries.util.geometry;

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
import myLibraries.GUI.geometry.DCELProgram;
import myLibraries.lang.MyMath;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.Vector;
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
    void drawTrapezoidalMap( SearchStructure SS, DCELProgram drawer, List<Vector> points ) {
        List<Face> faces = new ArrayList<>();
        SS.getAllLeafNodes().forEach( vertex -> {
            faces.add( vertex.trapezoid.getDCEL() );
        } );

        drawTrapezoidalMap( drawer, faces, points,
                DrawingProgram.NORMAL_POLYGON_COLOR, DrawingProgram.NORMAL_POLYGON_COLOR );
    }

    static
    void drawTrapezoidalMap( DCELProgram drawer, List<Face> faces,
                             List<Vector> points, Color poly, Color point ) {
        drawer.addPoly( faces, poly );
        drawer.addPoints( points, point );
    }

    public static
    void drawDs( List<SearchVertex> Ds, DCELProgram drawer ) {
        List<Face> faces = new ArrayList<>();
        Ds.forEach( vertex -> {
            faces.add( vertex.trapezoid.getDCEL());
        } );

        drawer.addPoly( faces, DrawingProgram.INTERSECTION_COLOR );
    }

    public static
    void drawQuery( SearchVertex d, Vector query, DCELProgram drawer ) {
        List<Face> faces = new ArrayList<>( 1 );
        List<Vector> points = new ArrayList<>( 2 );
        List<Line> lines = new ArrayList<>( 1 );
        switch ( d.type ) {
            case X_POINT_Q, X_POINT_P -> points.add( d.point );
            case SEGMENT -> {
                lines.add( d.line );
                drawer.addLines( lines, DrawingProgram.INTERSECTION_COLOR );
            }
            case TRAPEZOID -> faces.add( d.trapezoid.getDCEL() );
            default -> { assert false; }
        }

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
     * the original trapezoid into two parts ( vertical separation, left separation ):
     *              |
     * left(origin) | right
     *              |
     *
     * and pass the right into handleQ or handleS
     *
     * @param l si
     * */

    public static
    SearchVertex handleP( SearchVertex d, Line l, Stack<SearchVertex> de ) {
        assert d.type == SearchVertex.NodeType.TRAPEZOID;
        Vector p = l.startPoint;
        Vector q = l.endPoint;
        assert Vectors.sortByX( p, q ) <= 0;

        Trapezoid left = d.trapezoid;
        SearchVertex XNodeP = null;
        // normal case: p -> s or p -> q
        // imagine that we applied shear transformation to the map.
        // So two points are the same if and only if their x and y coors are the same.
        if ( !left.leftP.equals( p ) ) {
            // left separation
            Trapezoid right = new Trapezoid( p, left.rightP, left.top, left.bottom );
            Trapezoid.connectRights( right, left );
            left.rightP = p;

            // initialize the x node of P
            XNodeP = new SearchVertex( SearchVertex.NodeType.X_POINT_P, p, l );
            XNodeP.left = d;
            assert d.trapezoid.check();
            XNodeP.right = de == null ? handleQ( right, l, null ) : handleS( right, l, de );

            redirectParents( XNodeP, d );
            return XNodeP;
        }

        // degenerate case, p(x) -> s, or p(x) -> q
        // the left endPoint of the new segment and
        // the old one( the one already added into the map)
        // is the same
        XNodeP = de == null ? handleQ( left, l, null ) : handleS( left, l, de );
        redirectParents( XNodeP, d );
        return XNodeP;
    }

    /**
     * redirect Parents when replacing a leaf node
     * */

    private static
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
     * the original trapezoid into two parts( vertical separation, right separation ):
     *      |
     * left | right(origin)
     *      |
     *
     * and pass the left into handleQ or handleS
     *
     * @param l si
     * */

    // This method is only called when the segment crosses multiple trapezoids
    public static
    void handleQ( SearchVertex d, Line l, Stack<SearchVertex> de ) {
        redirectParents( handleQ( d.trapezoid, l, de ), d );
    }

    /**
     * @param l si
     * */

    // code to do the separation and update
    static
    SearchVertex handleQ( Trapezoid right, Line l, Stack<SearchVertex> de ) {
        Vector p = l.startPoint;
        Vector q = l.endPoint;
        assert Vectors.sortByX( p, q ) <= 0;

        SearchVertex XNodeQ = null;
        // normal case: q -> s
        // imagine that we applied shear transformation to the map.
        // So two points are the same if and only if their x and y coors are the same.
        if ( !right.rightP.equals( q ) ) {
            // right separation
            Trapezoid middle = new Trapezoid( p, q, right.top, right.bottom );
            Trapezoid.connectLefts( middle, right );
            right.leftP = q;

            // initialize the x node of Q
            XNodeQ = new SearchVertex( SearchVertex.NodeType.X_POINT_Q, q, l );
            XNodeQ.left = handleS( middle, l, de );
            XNodeQ.right = new SearchVertex( right );
            XNodeQ.right.parents.add( XNodeQ );
            assert XNodeQ.right.trapezoid.check();

            return XNodeQ;
        }

        // degenerate case, q(x) -> s
        // the right endPoint of the new segment and
        // the old one( the one already added into the map)
        // is the same
        XNodeQ = handleS( right, l, de );
        return XNodeQ;
    }

    /**
     * handle S when adding a new segment,
     * this operation will partition the original trapezoid into two parts( horizontal separation ):
     * top(origin)
     * ----------
     *   bottom
     *
     * this method will also handle trimming walls:
     * first, store trapezoids, top and bottom, to be merged later.
     * secondly, merge current top and bottom into previously stored ones.
     * */

    // This method is only called when the segment crosses multiple trapezoids
    public static
    void handleS( SearchVertex d, Line l, Stack<SearchVertex> de ) {
        redirectParents( handleS( d.trapezoid, l, de ), d );
    }

    /**
     * @param l si
     * */

    // code to do the separation and update
    public static
    SearchVertex handleS( Trapezoid middle, Line l, Stack<SearchVertex> de ) {
        Vector p = l.startPoint;
        Vector q = l.endPoint;
        assert Vectors.sortByX( p, q ) <= 0;
        Vector rightP = middle.rightP;

        // horizontal partition
        Trapezoid top = new Trapezoid( p, q, middle.top, l );
        Trapezoid bottom = new Trapezoid( p, q, l, middle.bottom );
        Trapezoid.mergeUppers( top, middle );
        Trapezoid.mergeLowers( bottom, middle );

        // p -> q -> s, no need to trim wall,
        // p -> s, p -> q, s -> s, q -> s need,
        if ( de != null ) {
            // store trapezoids, top and bottom, to be merged later.
            addTrims( top, bottom, rightP, l, de );

            // merge current top and bottom into previously stored ones.
            if ( de.size() > 1 ) {
                SearchVertex trim = trim( de );
                // get trimmed top and bottom
                top = trim.top;
                bottom = trim.bottom;
            }
        }

        // initialize the y node
        SearchVertex YNode = new SearchVertex( l );
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

        // bottom trapezoid has been added before?
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
     *
     * @param l si
     * */

    private static
    void addTrims( Trapezoid top, Trapezoid bottom, Vector rightP,
                   Line l, Stack<SearchVertex> de ) {
        SearchVertex trim = new SearchVertex( SearchVertex.NodeType.TRIMMING );

        double res1 = Triangles.areaTwo( l.startPoint, l.endPoint, rightP );
        // if rightP lies above s, trim lower wall
        if ( MyMath.isPositive( res1 ) )
            partitionBottom( top, rightP, trim );
        // if rightP lies below s, trim upper wall
        else if ( MyMath.isNegative( res1 ) )
            // assert rightP lies below s
            partitionTop( bottom, rightP, trim );
        // if rightP lies on s
        else addTrims( top, bottom, rightP, l, trim );

        // if rightP lies on s, do nothing
        // this happens when handling Q,
        // just trim wall, no need to add ones to be trimmed
        trim.top = top;
        trim.bottom = bottom;
        de.add( trim );
    }

    // suggestion to understand this method, draw a picture to show the cases.
    private static
    void addTrims( Trapezoid top, Trapezoid bottom,
                   Vector rightP, Line line, SearchVertex trim ) {

        Trapezoid upperRight = top.upperRightNeighbor;
        Trapezoid lowerRight = bottom.lowerRightNeighbor;

        // rightP is P node of sj
        if ( upperRight != null && lowerRight != null ) {
            // endpoint of si lies above sj
            if ( rightP.isAbove( line.endPoint ) )
                // partition tops
                partitionTop( bottom, rightP, trim );
            else
                // otherwise, partition bottoms.
                partitionBottom( top, rightP, trim );
        }
        // rightP is Q node of sj
        else if ( upperRight != null )
            // partition tops
            partitionTop( bottom, rightP, trim );
        else if ( lowerRight != null )
            // otherwise, partition bottoms.
            partitionBottom( top, rightP, trim );
        // impossible both are null.
        else assert false;
    }

    private static
    void partitionTop( Trapezoid bottom, Vector rightP, SearchVertex trim ) {
        bottom.rightP = rightP;
        trim.isTrimmingTop = true;
    }

    private static
    void partitionBottom( Trapezoid top, Vector rightP, SearchVertex trim ) {
        top.rightP = rightP;
        trim.isTrimmingTop = false;
    }

    /**
     * Second process of trimming walls:
     * secondly, merge current top and bottom into previously stored ones.
     *
     * */

    private static
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

    static
    boolean check( Trapezoid prev, Trapezoid current ) {
        assert prev.top == current.top : prev.top +  ", " + current.top;
        assert prev.bottom == current.bottom : prev.bottom + ", " + current.bottom;
        return true;
    }

}
