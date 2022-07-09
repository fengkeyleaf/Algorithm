package CGTsinghua.PA_3.problem_2;

/*
 * Main.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 4/18/2022$
 */

import CGTsinghua.MainCG;
import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.io.ProcessingFile;
import com.fengkeyleaf.io.ReadFromStdOrFile;
import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.util.geom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * <a href="https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1650">CG2017 PA3-2 Which wall are you looking at</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class Main extends MainCG
        implements ProcessingFile {
    static final String title = "Which wall are you looking at";
    List<Line> S;
    int lineNumber;
    List<Vector> Q;
    int lineID;
    DrawingProgram drawer;
    int fileName;

    Main( int fileName ) {
        ReadFromStdOrFile.readFromFile( getFilePathCG( 3, 2, this.fileName = fileName ), this );
        doTheAlgorithm();
    }

    @Override
    public void processingFile( Scanner sc ) {
        int initializeLength = 0;

        while ( sc.hasNext() ) {
            String content = sc.nextLine();
            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            String[] numbers = content.split( ReadFromStdOrFile.PATTERN_WHITE_CHARACTER );
            // read info like number of points
            if ( initializeLength < 1 &&
                    Pattern.matches( ReadFromStdOrFile.PATTERN_LENGTH, content ) ) {
                initializeLength++;
                assert numbers.length == 2;

                lineNumber = Integer.parseInt( numbers[ 0 ] );
                S = new ArrayList<>( lineNumber + 1 );
                Q = new ArrayList<>(  Integer.parseInt( numbers[ 1 ] ) + 1 );
                continue;
            }

            // build segments from points
            double x1 = Double.parseDouble( numbers[ 0 ] );
            double y1 = Double.parseDouble( numbers[ 1 ] );
            if ( initializeLength++ <= lineNumber ) {
                double x2 = Double.parseDouble( numbers[ 2 ] );
                double y2 = Double.parseDouble( numbers[ 3 ] );
                if ( fileName == 8 ) {
                    x1 -= 300000;
                    x2 -= 300000;
                    y1 -= 3800000;
                    y2 -= 3800000;
                }

                S.add( new Segment( x1, y1, x2, y2, Vectors::sortByX ) );

                if ( S.size() == 1 )
                    lineID = S.get( 0 ).ID;
                continue;
            }

            // query points
            Q.add( new Vector( x1, y1 ) );
        }

//        System.out.println( S );
//        System.out.println( Q );
    }

    void doTheAlgorithm() {
        // compute trapezoidal map and search structure.
        BoundingBox b = PointLocation.trapezoidalMap( S, Q );

        // initialize visualization program.
        int size = Math.abs( ( int ) MyMath.findMaxMinInAbs( b.maxX, b.maxY, b.minX, b.minY )[ 1 ] ) * 2 - 12;
        drawer = new DrawingProgram( title, size, size );
        List<Vector> P = new ArrayList<>( S.size() * 2 + 1 );
        S.forEach( s -> {
            P.add( s.startPoint );
            P.add( s.endPoint );
        } );

        TrapezoidalMap.drawTrapezoidalMap( b.SS, drawer, P );
        drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, P );
        drawer.drawPoints( DrawingProgram.INTERSECTION_COLOR, Q );

        // query process
        Q.forEach( q -> {
            SearchVertex r = b.SS.get( q );
            // vertical ray emitting from the query point.
            Segment s = new Segment( q.x, b.minY, q.x, b.maxY );

            switch ( r.getType() ) {
                case X_POINT_P, X_POINT_Q, SEGMENT -> {
                    assert r.getLine().isVertical || r.getLine().intersect( s )[ 0 ] != null : r.getLine();
                    drawWall( q, r.getLine(), s );
                    System.out.println( r.getLine().ID - lineID + 1 );
                }
                case TRAPEZOID -> {
                    // having degenerate case with query point( the robot ) falling inside a trapezoid.
                    Line l = getWall( q, r, s );
                    if ( l != null ) {
                        assert l.isVertical || l.intersect( s )[ 0 ] != null : l;
                        drawWall( q, l, s );
                        System.out.println( l.ID - lineID + 1 );
                    }
                    else System.out.println( "N" );
                }
                default -> { assert false; }
            }
        } );

        drawer.initialize();
    }

    /**
     * get the wall that robot is looking at.
     * */

    private Line getWall( Vector q, SearchVertex v, Segment s ) {
        Trapezoid t = v.getTrapezoid();
        // the wall(l) that the robot is looking at is from the bounding box, meaning it's illegal.
        Line l = t.getTop().ID - lineID < lineNumber ? t.getTop() : null;
        // The wall that the robot is looking at maybe the one right to l
        // ( as the bottom line of the upper right neighbor of l ),
        // which happens when the robot happens to lie on the vertical line passing an endpoint of the segment.
        // And this segment must be above the robot.
        Line r = t.getUpperRightNeighbor() == null ? null : t.getUpperRightNeighbor().getBottom();
        // the wall(r) that the robot is looking at is from the bounding box, meaning it's illegal.
        if ( r != null ) r = r.ID - lineID < lineNumber ? r : null;

        // no wall above the robot.
        if ( l == null && r == null ) return null;
        // no upper right neighbor.
        else if ( r == null ) return l;
        // current wall is invalid, check the upper right one.
        else if ( l == null )
            return  // be careful with r being vertical.
                    r.isVertical && Vectors.sortByY( r.startPoint, q ) > 0 ||
                            // r must be legal if and only if the robot is below it
                            // and the robot is looking at it.
                            !( Triangles.toLeft( r.startPoint, r.endPoint, q ) ||
                                    r.intersect( s )[ 0 ] == null ) ? r : null;

        // both walls are candidates.
        Vector interL = l.intersect( s )[ 0 ];
        Vector interR = r.intersect( s )[ 0 ];
        // the intersection is the startPoint( left endpoint ) when the wall is vertical.
        if ( r.isVertical ) {
            assert interR == null;
            interR = Vectors.sortByY( r.startPoint, q ) > 0 ? r.startPoint : null;
        }

        // the robot isn't looking at the upper right neighbour.
        if ( interR == null ) return l;
        assert Vectors.sortByY( interL, interR ) != 0;
        // the vertical line passing the robot can stab through both walls,
        // so the correct wall is the one close to the robot.
        return Vectors.sortByY( interL, interR ) <= 0 ? l : r;
    }

    /**
     * draw the wall the robot is looking at and the sight line.
     * */

    private void drawWall( Vector q, Line l, Segment s ) {
        Vector i = l.intersect( s )[ 0 ];

        List<Line> L = new ArrayList<>();
        if ( l.isVertical ) i = l.startPoint;
        if ( !l.isOnThisSegment( q ) ) {
            assert Vectors.sortByY( q, i ) <= 0 : l;
            L.add( new Line( q, i ) );
            assert L.get( 0 ).isVertical;
        }
        L.add( l );

        drawer.drawLines( DrawingProgram.INTERSECTION_COLOR , L );
    }

    public static
    void main( String[] args ) {
//        new Main( 1 );
//        new Main( 2 );
//        new Main( 3 ); // provided test case.
//        new Main( 4 );
        new Main( 5 );
        new Main( 6 );
//        new Main( 7 );
//        new Main( 8 );
//        new Main( 9 );
    }
}
