package CGTsinghua.PA_5.problem_2;

/*
 * Main.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/6/2022$
 */

import CGTsinghua.MainCG;
import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.io.ProcessingFile;
import com.fengkeyleaf.io.ReadFromStdOrFile;
import com.fengkeyleaf.lang.LinearTwoUnknowns;
import com.fengkeyleaf.util.geom.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * <a href="https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1947">CG2017 PA5-2 FruitNinja</a><br>
 *
 * Two ways to solve it:
 * 1) Half-plane intersection - O( nlogn );
 * 2) Linear programming - O( n ), optimal;
 * where n is # of the input segments( fruits ).
 *
 * In this implementation, we'll focus on the first method.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

// 1 ≤ m ≤ 10
// 1 ≤ ni ≤ 100,000 , i = 1, 2, 3, …, m
// 所有输入点的坐标均为范围[-10^6, 10^6)内的整数
final class Main extends MainCG
        implements ProcessingFile {

    final int SIZE = 1000000; // 10 ^ 6
    // segment set.
    List<List<Line>> F;
    // half-plane intersection handler.
    final HalfPlaneIntersection i = new HalfPlaneIntersection( SIZE, SIZE, Vector.origin );
    static final String POS_ANSWER = "Y";
    static final String NEG_ANSWER = "N";

    Main( int fileName ) {
        ReadFromStdOrFile.readFromFile( getFilePathCG( 5, 2, fileName ), this );
        doTheAlgorithm();
    }

    @Override
    public void processingFile( Scanner sc ) {
        boolean isFirst = true;

        while ( sc.hasNext() ) {
            String content = sc.nextLine();

            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            if ( Pattern.matches( ReadFromStdOrFile.PATTERN_LENGTH, content ) ) {
                if ( isFirst ) {
                    F = new ArrayList<>( Integer.parseInt( content ) );
                    isFirst = false;
                    continue;
                }

                F.add( new ArrayList<>( Integer.parseInt( content ) ) );
                continue;
            }

            String[] numbers = content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS );
            assert numbers.length == 3;

            List<Line> L = F.get( F.size() - 1 );
            int x = Integer.parseInt( numbers[ 0 ] );
            Vector v1 = new Vector( x, Integer.parseInt( numbers[ 1 ] ) );
            Vector v2 = new Vector( x, Integer.parseInt( numbers[ 2 ] ) );

            L.add( new Segment( v1, v2, Vectors::sortByY ) );
        }

//        System.out.println( F );
    }

    void doTheAlgorithm() {
        // for each segment set.
        F.forEach( f -> {
            // convert the endpoints of segments to lines in the dual plane.
            // and do half-plane intersecting algorithm
            // to compute the result set.
            i.intersect( getHalfPlanes( f ) );
            assert Checker.check( i, f );

            // print the result.
            System.out.print(
                    i.getResultType() == HalfPlaneIntersection.Type.EMPTY ? NEG_ANSWER : POS_ANSWER
            );
            assert println();
        } );
    }

    static
    boolean println() {
        System.out.println();
        return true;
    }

    /**
     * Convert the endpoints of segments in the primary plane
     * to lines in the dual plane.
     */

    static
    List<HalfPlane> getHalfPlanes( List<Line> f ) {
        List<HalfPlane> H = new ArrayList<>( f.size() );
        if ( f.isEmpty() ) return H;

        f.forEach( s -> {
            H.add( new HalfPlane( s.startPoint.toDuality().equation, HalfPlane.GREATER_EQ ) );
            LinearTwoUnknowns eq = s.endPoint.toDuality().equation;
            H.add( new HalfPlane( -eq.a, -eq.b, -eq.c, HalfPlane.GREATER_EQ ) );

            assert Checker.check( H.get( H.size() - 1 ), H.get( H.size()- 2 ) );
        } );

//        System.out.println( H );
        return H;
    }

    //-------------------------------------------------------
    // Class checker.
    //-------------------------------------------------------

    // Note that code in this class won't have any effects on the main algorithm.
    static class Checker {
        private static final String TITLE = "CG2017 PA5-2 FruitNinja";

        static
        boolean check( HalfPlaneIntersection i, List<Line> S ) {
            if ( S.isEmpty() ) return true;

            // get the result line and check to see if it intersects each segment if exists.
            Line l = getCut( i );
            check( S, l );

            // visualization.
            List<Vector> P = new ArrayList<>( S.size() * 2 );
            S.forEach( s -> {
                P.add( s.startPoint );
                P.add( s.endPoint );
            } );

            BoundingBox b = BoundingBox.getBox( P, BoundingBox.OFFSET );
            DrawingProgram drawer = new DrawingProgram( TITLE, b.width, b.height );

            drawer.drawSegments( DrawingProgram.NORMAL_POLYGON_COLOR, S );
            if ( l != null ) drawer.drawLines( DrawingProgram.INTERSECTION_COLOR, List.of( l.getSegment( b ) ) );

            drawer.initialize();
            return true;
        }

        private static
        void check( List<Line> S, Line l ) {
            if ( l == null ) return;

            // the result line should intersect each segment if exists.
            S.forEach( s -> {
                assert s.intersect( l ).length > 0;
            } );
        }

        /**
         * Get the result line.
         *
         * @return the line in the primary plane if it exists; Otherwise, return null.
         */

        private static
        Line getCut( HalfPlaneIntersection i ) {
            return switch ( i.getResultType() ) {
                case POINT -> i.getPoint().fromDuality();
                case SEGMENT -> i.getSeg().startPoint.fromDuality();
                case PLANE -> i.getFace().innerComponents.get( 0 ).twin.incidentFace.walkAroundVertex().get( 0 ).fromDuality();
                default -> null;
            };
        }

        private static final Line TEST_LINE = new Line( 0, -1, 0, 1 );

        static
        boolean check( HalfPlane h1, HalfPlane h2 ) {
            // Each half-plane should contain the intersection point on the other half-plane.
            assert h2.contains( h1.eq.intersect( TEST_LINE.equation ) );
            assert h1.contains( h2.eq.intersect( TEST_LINE.equation ) );

            return true;
        }
    }

    static
    void test() {
        Vector prev = new Vector( 5000000.0, 5000000.0 );
        Vector base = new Vector( -5000000.0, 4999999.99 );
        Vector l = new Vector( -5000000.01, 4999999.99 );
        Vector next = new Vector( -5000000.0, 4999997.0 );
        Vector r = new Vector( -4999999.99, 4999999.99 );

        System.out.println( Triangles.areaTwo( prev, base, l ) );
        System.out.println( Triangles.areaTwo( base, next, l ) );
        System.out.println( Triangles.areaTwo( prev, base, r ) );
        System.out.println( Triangles.areaTwo( base, next, r ) );

        System.out.println( Triangles.toLeftRigorously( prev, base, next ) );
    }

    public static
    void main( String[] args ) {
//        test();

//        new Main( 0 );
//        new Main( 1 ); // Y
//        new Main( 2 ); // Y
//        new Main( 3 ); // N
//        new Main( 4 ); // Y
//        new Main( 5 ); // N
//        new Main( 6 ); // N
//        new Main( 7 ); // Y, overlapping endpoints.
//        new Main( 8 ); // Y, overlapping segments.
//        new Main( 9 ); // Y, duplicate segments.
//        new Main( 10 ); // N, include above three.
//        new Main( 11 ); // YYN, provided test case.
        new Main( 12 ); // Y, provided test case from the webpage.
    }
}
