package PA_1.problem_2;

/*
 * Subsequence.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 * JDK: 16
 */

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.GUI.geometry.IntersectProgram;
import myLibraries.io.ProcessingFile;
import myLibraries.io.ReadFromStdOrFile;
import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.IntersectionShape;
import myLibraries.util.geometry.elements.Circle;
import myLibraries.util.geometry.elements.InterArc;
import myLibraries.util.geometry.elements.InterLine;
import myLibraries.util.geometry.elements.EventPoint2D;
import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.Vector;
import myLibraries.util.geometry.Circles;
import myLibraries.util.geometry.GeometricIntersection;
import myLibraries.util.geometry.Lines;
import myLibraries.util.geometry.Vectors;

import java.util.*;
import java.util.regex.Pattern;

/**
 * CG2017 PA1-2 Crossroad
 * https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1646
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

// TODO: 1/13/2022 test lines and circles
// TODO: 1/11/2022 visualization
public final class Main implements ProcessingFile {
    private static final double boundary;
    private static final Vector quadrantFirst;
    private static final Vector quadrantSecond;
    private static final Vector quadrantThird;
    private static final Vector quadrantFourth;
    private List<Line> segments;
    private List<IntersectionShape> nonOverlappingSegments;
    private int segmentsLineNumber;
    private List<Line>  rays;
    private int raysLineNumber;
    private List<Line> lines;
    private int lineNumber;
    private List<Circle> circles;
    private List<IntersectionShape> nonOverlappingCycles;
    private int cyclesLineNumber;

    enum ShapeType {
        SEGMENT, RAY, LINE, CYCLE, ARC
    }

    static {
        double num = Math.pow( 10, 5 );
        double res = 74 * Math.pow( num, 2 ) - 86 * num + 25;
        boundary = Math.ceil( Math.sqrt( res ) ); // 860228.0
        quadrantFirst = new Vector( boundary, boundary, -1 );
        quadrantSecond = new Vector( -boundary, boundary, -2 );
        quadrantThird = new Vector( -boundary, -boundary, -3 );
        quadrantFourth = new Vector( boundary, -boundary, -4 );
//        System.out.println( res );
//        System.out.println( Math.sqrt( res ) );
//        System.out.println( Math.ceil( Math.sqrt( res ) ) );
    }

    public Main( String fileName ) {
        boolean ifReadFromFile = ReadFromStdOrFile.readFromFile(
                fileName, this );

        if ( ifReadFromFile )
            System.out.printf( "%s - %d\n",
                    fileName.substring( fileName.lastIndexOf( '/' ) + 1 ),
                    doTheAlgorithm() );
        else
            System.out.printf( "%d\n", doTheAlgorithm() );
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

    private Vector generatePoint( int quadrant, Line line ) {
        switch ( quadrant ) {
            case 1:
                return line.processRayOrLine( boundary, boundary );
            case 2:
                return line.processRayOrLine( -boundary, boundary );
            case 3:
                return line.processRayOrLine( -boundary, -boundary );
            case 4:
                return line.processRayOrLine( boundary, -boundary );
            default:
                assert false;
        }

        assert false;
        return null;
    }

    /**
     * @param num1 an startpoint of a line or center of a cycle
     * @param num3  an endpoint of a line or radius of a cycle
     * */

    private void generatePoints( int ID, ShapeType shapeType,
                                 int num1, int num2,
                                 int num3, int num4 ) {
        Vector[] points = new Vector[ 2 ];
        points[ 0 ] = new Vector( num1, num2, ID );
        points[ 1 ] = new Vector( num3, num4, ID++ );
        switch ( shapeType ) {
            case SEGMENT:
                Arrays.sort( points, Vectors::sortByX );
                segments.add( new Line( points[ 0 ], points[ 1 ] ) );
                break;
            case RAY:
                Line ray = new Line( points[ 0 ], points[ 1 ] );
                Vector vectorRay = ray.getVector();
                points[ 1 ] = generatePoint( MyMath.quadrant( vectorRay ), ray );
                Arrays.sort( points, Vectors::sortByX );
                rays.add( new Line( points[ 0 ], points[ 1 ] ) );
                break;
            case LINE:
                Line line1 = new Line( points[ 0 ], points[ 1 ] );
                Line line2 = new Line( points[ 1 ], points[ 0 ] );
                Vector vectorLineLeft = line1.getVector();
                Vector vectorLineRight = line2.getVector();
                points[ 0 ] = generatePoint( MyMath.quadrant( vectorLineLeft.x, vectorLineLeft.y ), line1 );
                points[ 1 ] = generatePoint( MyMath.quadrant( vectorLineRight.x, vectorLineRight.y ), line2 );
                Arrays.sort( points, Vectors::sortByX );
                rays.add( new Line( points[ 0 ], points[ 1 ] ) );
                break;
            case CYCLE:
                assert num3 >= 0;
                circles.add( new Circle( points[ 0 ], num3 ) );
                break;
            default:
                assert false;
        }
    }

    /**
     * process input data
     * */

    @Override
    public void processingFile( Scanner sc ) {
        boolean initializeLength = true;
        int ID = 0;
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
                generatePoints( ID, ShapeType.SEGMENT, num1, num2, num3, num4 );
                ID += 2;
            }
            else if ( lineNumber < raysLineNumber ) {
                generatePoints( ID, ShapeType.RAY, num1, num2, num3, num4 );
                ID +=2;
            }
            else if ( lineNumber < this.lineNumber ) {
                generatePoints( ID, ShapeType.LINE, num1, num2, num3, num4 );
                ID +=2;
            }
            else if ( lineNumber < cyclesLineNumber )
                generatePoints( ID++, ShapeType.CYCLE, num1, num2, num3, num4 );
            else assert false;
        }

//         System.out.println( segments );
    }

    private void checker( List<EventPoint2D> intersections,
                          TreeSet<Vector> intersectionsBruteForceAll ) {
        intersections.sort( Vectors::sortByX );
        List<Vector> intersectionsBruteForce = new ArrayList<>( intersectionsBruteForceAll.size() + 1 );
        intersectionsBruteForce.addAll( intersectionsBruteForceAll );
        intersectionsBruteForce.sort( Vectors::sortByX );

        int index1 = 0;
        int index2 = 0;
        List<Vector> differences = new ArrayList<>();
        while ( index1 < intersectionsBruteForce.size()
                && index2 < intersections.size() ) {
            if ( !intersectionsBruteForce.get( index1 ).equalsXAndY( intersections.get( index2 ) ) ) {
                differences.add( intersectionsBruteForce.get( index1 ) );
                index1++;
                continue;
            }

            index2++;
            index1++;
        }

        if ( index1 < intersectionsBruteForce.size() )
            differences.addAll( intersectionsBruteForce.subList( index1, intersectionsBruteForce.size() ) );
        else if ( index2 < intersections.size() )
            differences.addAll( intersections.subList( index2, intersections.size() ) );

        if ( !differences.isEmpty() )
            System.out.println( "Diff: " + differences );

        System.out.println( intersections );
        System.out.println( intersectionsBruteForce );
    }

    public int doTheAlgorithm() {
        segments.addAll( rays );
        segments.addAll( lines );
        segments = Lines.mergeOverlappingLines( segments );
//        System.out.println( segments );
        nonOverlappingSegments = new ArrayList<>( segments.size() );
        for ( Line seg : segments ) {
            nonOverlappingSegments.add( new InterLine( seg ) );
        }

        circles = Circles.mergeOverlappingCycles( circles );
        nonOverlappingCycles = new ArrayList<>( circles.size() );
        for ( Circle circle : circles ) {
            nonOverlappingCycles.add( new InterArc( circle ) );
        }

        List<IntersectionShape> shapes = new ArrayList<>();
        shapes.addAll( nonOverlappingSegments );
        shapes.addAll( nonOverlappingCycles );

        List<EventPoint2D> intersections = GeometricIntersection.findIntersection( shapes );
//        TreeSet<Vector> intersectionsBruteForce = GeometricIntersection.bruteForceLinesIntersection( segments );
        TreeSet<Vector> intersectionsBruteForceAll = GeometricIntersection.bruteForceLineCycleIntersection( segments, circles );
        checker( intersections, intersectionsBruteForceAll );

        IntersectProgram program = new IntersectProgram( 80, 80 );

        List<Line> lines = new ArrayList<>( nonOverlappingSegments.size() + 1 );
        nonOverlappingSegments.forEach( l -> lines.add( ( Line ) l ) );

        List<Vector> points = new ArrayList<>( intersections.size() + 1 );
        points.addAll( intersections );

        program.draw( lines, points, DrawingProgram.NORMAL_POLYGON_COLOR );
        program.initialize();

        assert intersections.size() == intersectionsBruteForceAll.size();
        return intersections.size();
    }

    public static
    void testOnlyLines() {
        String prefix = "/test_1/";
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 1, prefix ) ); // 1 - 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 2, prefix ) ); // 2 - 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 3, prefix ) ); // 3 - 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 4, prefix ) ); // 4 - 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 5, prefix ) ); // 5 - 3
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 6, prefix ) ); // 6 - 3
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 7, prefix ) ); // 7 - 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 8, prefix ) ); // 8 - 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 9, prefix ) ); // 9 - 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 10, prefix ) ); // 10 - 3
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 11, prefix ) ); // 11 - 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 12, prefix ) ); // 12 - 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 13, prefix ) ); // 13 - 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 14, prefix ) ); // 14 - 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 15, prefix ) ); // 15 - 3
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 16, prefix ) ); // 16 - 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 17, prefix ) ); // 17 - 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 18, prefix ) ); // 18 - 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 19, prefix ) ); // 19 - 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 20, prefix ) ); // 20 - 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 21, prefix ) ); // 21 - 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 22, prefix ) ); // 22 - 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 23, prefix ) ); // 23 - 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 24, prefix ) ); // 24 - 3
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 25, prefix ) ); // 25 - 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 26, prefix ) ); // 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 31, prefix ) ); // 3 X - tree symmetric
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, 36, prefix ) ); // 4
    }

    public static
    void testLinesCycles() {
        String prefix = "/test_2/";
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_1", prefix ) ); // 0
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_2" , prefix ) ); // 2
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_3" , prefix ) ); // 4
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_4" , prefix ) ); // 2
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "5_1" , prefix ) ); // 7
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_6" , prefix ) ); // 1
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_7" , prefix ) ); // 3
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_8" , prefix ) ); // 5
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_9" , prefix ) ); // 1
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_10" , prefix ) ); // 4
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_11" , prefix ) ); // 2
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_12" , prefix ) ); // 5
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_13" , prefix ) ); // 0
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_14" , prefix ) ); // 1
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "1_15" , prefix ) ); // 1
    }

    public static
    void testLinesCyclesComplex() {
        String prefix = "/test_3/";
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_1", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_2", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_3", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_4", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_5", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_6", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_7", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_8", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_9", prefix ) ); // 8
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_10", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_11", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_12", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_13", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_14", prefix ) ); // 8
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_15", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_16", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_17", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_18", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_19", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_20", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_21", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_22", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_23", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_24", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_25", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_26", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_26", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_27", prefix ) ); // 7
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_28", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_29", prefix ) ); //
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "2_30", prefix ) ); //
    }

    public static
    void testAllTypes() {
        String prefix = "/test_4/";
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "3_1", prefix ) ); // 2
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "3_2", prefix ) ); // 3
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "3_3", prefix ) ); // 8
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "3_4", prefix ) ); // 8
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "3_5", prefix ) ); // 5
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "3_6", prefix ) ); // 7
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "3_7", prefix ) ); // 9
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "3_8", prefix ) ); // 3
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "3_9", prefix ) ); // 22
    }

    private static
    void testBoundingBox() {
        String prefix = "/test_5/";
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "5_1", prefix ) ); // 6
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 2, "5_2", prefix ) ); // 8
    }

    public static
    void main( String[] args ) {
//        testOnlyLines(); // 26
//        testLinesCycles(); // 15
//        testLinesCyclesComplex(); // 30
//        testAllTypes();
        testBoundingBox();

        // read from std
//        new Subsequence( ReadFromStdOrFile.getFilePathCG( 1, 2, "3_1", "" ) ); // 2
    }
}
