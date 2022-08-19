package CGTsinghua.PA_1.problem_2;

/*
 * Main.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import CGTsinghua.MainCG;
import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.io.ProcessingFile;
import com.fengkeyleaf.io.ReadFromStdOrFile;
import com.fengkeyleaf.util.MyCollections;
import com.fengkeyleaf.util.geom.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * <a href="https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1646">CG2017 PA1-2 Crossroad</a><br>
 *
 * hints: Segment, line and ray roads can be processed similarly.
 * Circle roads can be broken into a number of arcs.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class Main extends MainCG
        implements ProcessingFile {
    private static final String title = "Visualization debugger for geometric intersection";
    private List<Segment> segments;
    private int segmentsLineNumber;
    private List<Ray> rays;
    private int raysLineNumber;
    private List<Line> lines;
    private int lineNumber;
    private List<Circle> circles;
    private int cyclesLineNumber;

    Main( String fileName ) {
        boolean ifReadFromFile = ReadFromStdOrFile.readFromFile(
                fileName, this );

        if ( ifReadFromFile )
            System.out.printf( "%s - %d\n",
                    fileName.substring( fileName.lastIndexOf( '/' ) + 1 ),
                    doTheAlgorithm() );
        else
            System.out.printf( "%d\n", doTheAlgorithm() );
    }

    Main( int fileName,  String prefix ) {
        this( getFilePathCG( 1, 2, fileName, prefix ) );
    }

    Main( String fileName, String prefix ) {
        this( getFilePathCG( 1, 2, fileName, prefix ) );
    }

    private void processLength( String[] lens ) {
        int index = 0;
        segmentsLineNumber = 1 + Integer.parseInt( lens[ index++ ] );
        segments = new ArrayList<>( segmentsLineNumber - 1 );
        raysLineNumber = segmentsLineNumber + Integer.parseInt( lens[ index++ ] );
        rays = new ArrayList<>( raysLineNumber - segmentsLineNumber );
        lineNumber = raysLineNumber + Integer.parseInt( lens[ index++ ] );
        lines = new ArrayList<>( lineNumber - raysLineNumber );
        cyclesLineNumber = lineNumber + Integer.parseInt( lens[ index++ ] );
        circles = new ArrayList<>( cyclesLineNumber - lineNumber );
    }

    /**
     * process input data
     * */

    @Override
    public void processingFile( Scanner sc ) {
        boolean initializeLength = true;
        int lineNumber = -1;

        while ( sc.hasNext() ) {
            String content = sc.nextLine();
            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            lineNumber++;
            if ( initializeLength &&
                    Pattern.matches( ReadFromStdOrFile.PATTERN_LENGTH, content ) ) {
                processLength( content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS ) );
                initializeLength = false;
                continue;
            }

            String[] numbers = content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS );
            int num1 = Integer.parseInt( numbers[ 0 ] );
            int num2 = Integer.parseInt( numbers[ 1 ] );
            int num3 = Integer.parseInt( numbers[ 2 ] );
            int num4 = -1;
            if ( numbers.length > 3 )
                num4 = Integer.parseInt( numbers[ 3 ] );

            if ( lineNumber < segmentsLineNumber ) {
                segments.add( new Segment( num1, num2, num3, num4 ) );
            }
            else if ( lineNumber < raysLineNumber ) {
                rays.add( new Ray( num1, num2, num3, num4 ) );
            }
            else if ( lineNumber < this.lineNumber ) {
                lines.add( new Line( num1, num2, num3, num4 ) );
            }
            else if ( lineNumber < cyclesLineNumber )
                circles.add( new Circle( new Vector( num1, num2 ), num3 ) );
            else assert false;
        }

//         System.out.println( segments );
    }

    static int boundary = Integer.MAX_VALUE / 1000;

    int doTheAlgorithm() {
        // although putting the bounding box for line and ray,
        // we can regard line and ray as segment and feed the algorithm with them,
        // but this might miss intersections that are far away from the box.
        // Further, the box cannot be so large that there will be precision issue occurring.
        // see test case 3_2, will have infinite loop when the boundary
        // for the bounding box is Integer.MAX_VALUE, but no problem with 10 ^ 6.
        // But this cannot guarantee that all intersections te bo report,
        // particular, intersection far way from the bounding box, usually formed by line and ray.
        // They can have intersection almost infinite far.
        // i.e. if one only interests in finding intersection bounded by a certain area,
        // this algorithm is quite proper one and relatively easy to implement.
        // TODO: 5/31/2022 handle infinite intersection correctly.
        BoundingBox b = BoundingBox.getBox( boundary, Vector.origin );

        // process line and ray as segment.
        List<Line> I = new ArrayList<>( segments.size() + rays.size() + lines.size() + 1 );
        lines.forEach( l -> I.add( l.getSegment( b ) ) );
        rays.forEach( r -> I.add( r.getSegment( b ) ) );
        I.addAll( segments );

        List<IntersectionShape> shapes = new ArrayList<>();
        I.forEach( i -> shapes.add( ( IntersectionShape ) i ) );
        for ( Circle circle : Circles.mergeOverlappingCycles( circles ) )
            shapes.add( new Arc( circle ) );

        // find intersection
        GeometricIntersection intersector = new GeometricIntersection( false );
        List<Vector> intersections = intersector.findIntersection( shapes );
        // verify with brute force.
//        checker( intersections, I );
        return intersections.size();
    }

    static
    void testOnlyLines() {
        String prefix = "/test_1/";
//        new Main( 1, prefix ); // 1 - 0
//        new Main( 2, prefix ); // 2 - 0
//        new Main( 3, prefix ); // 3 - 1
//        new Main( 4, prefix ); // 4 - 2
//        new Main( 5, prefix ); // 5 - 3
//        new Main( 6, prefix ); // 6 - 3
//        new Main( 7, prefix ); // 7 - 1
//        new Main( 8, prefix ); // 8 - 2
//        new Main( 9, prefix ); // 9 - 2
//        new Main( 10, prefix ); // 10 - 3
//        new Main( 11, prefix ); // 11 - 2
//        new Main( 12, prefix ); // 12 - 2
//        new Main( 13, prefix ); // 13 - 2
//        new Main( 14, prefix ); // 14 - 2
//        new Main( 15, prefix ); // 15 - 3
//        new Main( 16, prefix ); // 16 - 2
//        new Main( 17, prefix ); // 17 - 1
//        new Main( 18, prefix ); // 18 - 0
//        new Main( 19, prefix ); // 19 - 1
//        new Main( 20, prefix ); // 20 - 1
//        new Main( 21, prefix ); // 21 - 1
//        new Main( 22, prefix ); // 22 - 2
//        new Main( 23, prefix ); // 23 - 1
//        new Main( 24, prefix ); // 24 - 3
//        new Main( 25, prefix ); // 25 - 0
//        new Main( 26, prefix ); // 1
//        new Main( 31, prefix ); // 3
//        new Main( 36, prefix ); // 4
        new Main( 37, prefix ); // 0, very special one.

    }

    static
    void testLinesCycles() {
        String prefix = "/test_2/";
//        new Main( "1_1", prefix ); // 0
//        new Main( "1_2" , prefix ); // 2
//        new Main( "1_3" , prefix ); // 4
//        new Main( "1_4" , prefix ); // 2
//        new Main( "1_5" , prefix ); // 7
//        new Main( "1_6" , prefix ); // 1
//        new Main( "1_7" , prefix ); // 3
//        new Main( "1_8" , prefix ); // 7
        new Main( "1_9" , prefix ); // 1
        new Main( "1_10" , prefix ); // 4
        new Main( "1_11" , prefix ); // 2
        new Main( "1_12" , prefix ); // 5
        new Main( "1_13" , prefix ); // 0
        new Main( "1_14" , prefix ); // 1
        new Main( "1_15" , prefix ); // 1
    }

    static
    void testLinesCyclesComplex() {
        String prefix = "/test_3/";
//        new Main( "2_1", prefix ); //
//        new Main( "2_2", prefix ); //
//        new Main( "2_3", prefix ); //
//        new Main( "2_4", prefix ); //
//        new Main( "2_5", prefix ); //
//        new Main( "2_6", prefix ); //
//        new Main( "2_7", prefix ); //
//        new Main( "2_8", prefix ); //
//        new Main( "2_9", prefix ); // 8
//        new Main( "2_10", prefix ); //
//        new Main( "2_11", prefix ); //
//        new Main( "2_12", prefix ); //
//        new Main( "2_13", prefix ); //
//        new Main( "2_14", prefix ); // 8
//        new Main( "2_15", prefix ); //
//        new Main( "2_16", prefix ); //
//        new Main( "2_17", prefix ); //
//        new Main( "2_18", prefix ); //
//        new Main( "2_19", prefix ); //
//        new Main( "2_20", prefix ); //
//        new Main( "2_21", prefix ); //
//        new Main( "2_22", prefix ); //
        new Main( "2_23", prefix ); //
        new Main( "2_24", prefix ); //
        new Main( "2_25", prefix ); //
        new Main( "2_26", prefix ); //
        new Main( "2_26", prefix ); //
        new Main( "2_27", prefix ); // 7
        new Main( "2_28", prefix ); //
        new Main( "2_29", prefix ); //
        new Main( "2_30", prefix ); //
    }

    static
    void testAllTypes() {
        String prefix = "/test_4/";
//        new Main( "3_1", prefix ); // 2
//        new Main( "3_2", prefix ); // 3
//        new Main( "3_3", prefix ); // 8
//        new Main( "3_4", prefix ); // 8
//        new Main( "3_5", prefix ); // 5
//        new Main( "3_6", prefix ); // 7
//        new Main( "3_7", prefix ); // 9
//        new Main( "3_8", prefix ); // 3
        new Main( "3_9", prefix ); // 22, provided case
    }

    static
    void testBoundingBox() {
        String prefix = "/test_5/";
        new Main( "5_1", prefix ); // 6
        new Main( "5_2", prefix ); // 8
    }

    public static
    void main( String[] args ) {
        testOnlyLines(); // 26
//        testLinesCycles(); // 15
//        testLinesCyclesComplex(); // 30
//        testAllTypes(); // 9
//        testBoundingBox();

        // read from std
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "3_1", "" ) ); // 2
    }
}
