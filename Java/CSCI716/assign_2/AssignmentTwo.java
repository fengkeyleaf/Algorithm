package CSCI716.assign_2;

/*
 * AssignmentTwo.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.GUI.geometry.IntersectProgram;
import myLibraries.io.MyWriter;
import myLibraries.io.ProcessingFile;
import myLibraries.io.ReadFromStdOrFile;
import myLibraries.lang.MyMath;
import myLibraries.util.geometry.elements.IntersectionShape;
import myLibraries.util.geometry.elements.InterLine;
import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.EventPoint2D;
import myLibraries.util.geometry.elements.Vector;
import myLibraries.util.geometry.GeometricIntersection;
import myLibraries.util.geometry.Lines;
import myLibraries.util.geometry.Vectors;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Programming Assignment 2 - Line Segment Intersection (Plane Sweep)
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class AssignmentTwo implements ProcessingFile {
    // input, output and visualization
    private static final String prefix = "assign_2/";
    private String inputFilePath ;
    private String outputFilePath;
    private final boolean isIDEA;

    private boolean isVisualization;
    private int originWidth;
    private int originHeight;

    // data
    private List<Line> segments;
    private List<IntersectionShape> nonOverlappingSegments;

    /**
     * constructs to create an instance of AssignmentTwo
     * */

    public AssignmentTwo( String fileName, boolean isVisualization, int originWidth, int originHeight ) {
        this( fileName, null, isVisualization, originWidth, originHeight );
    }

    public AssignmentTwo( String fileName ) {
        this( fileName, null, false, 0, 0 );
    }

    public AssignmentTwo( String fileName, String filePath ) {
        this( fileName, filePath, false, 0, 0 );
    }

    /**
     * constructs to create an instance of AssignmentTwo
     *
     * @param fileName Path to the input file
     * @param filePath Path to the output file (optional)
     * @param isVisualization Turn on Visualization? (optional)
     * @param originWidth window width of input data (optional)
     * @param originHeight window height of input data (optional)
     * */

    public AssignmentTwo( String fileName, String filePath,
                          boolean isVisualization, int originWidth, int originHeight ) {
        this.isVisualization = isVisualization;
        this.originWidth = originWidth;
        this.originHeight = originHeight;
        isIDEA = true;

        ReadFromStdOrFile.readFromFile( inputFilePath  = fileName, this );
        doTheAlgorithm( outputFilePath = filePath, prefix );
    }

    /**
     * constructs to create an instance of AssignmentTwo with arguments
     * */

    public AssignmentTwo( String[] args ) {
        paraphraseArgs( args );
        isIDEA = false;

        ReadFromStdOrFile.readFromFile( inputFilePath , this );
        doTheAlgorithm( outputFilePath, prefix );
    }

    // runTimeAnalysis --------------------------------------

    private static
    void test() {
        Vector[] endpoints = new Vector[ 2 ];

        int count = 0;
        int bound = 10000;
        int maxCount = 1;
        while ( count++ < maxCount ) {
//            System.out.println("n = " + size + " Get Started-------------->");

            List<Integer> coorX = MyMath.generateRandomInts( 2 * bound, bound );

            try {
                Thread.sleep( 10 );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }

            List<Integer> coorY = MyMath.generateRandomInts( 2 * bound, bound );

            List<Line> lines = new ArrayList<>( 10 + 1 );
            for ( int i = 0; i < bound; i += 2 ) {
                endpoints[ 0 ] = new Vector( coorX.get( i ), coorY.get( i ) );
                endpoints[ 1 ] = new Vector( coorX.get( i + 1 ), coorY.get( i + 1 ) );
                Arrays.sort( endpoints, Vectors::sortByX );
//                System.out.println( Arrays.toString( endpoints ));
                if ( endpoints[0].equalsXAndY( endpoints[1] ))
                    continue;

                lines.add( new Line( endpoints[ 0 ], endpoints[ 1 ] ) );
            }

//            System.out.println( lines );
            lines = Lines.mergeOverlappingLines( lines );
//            System.out.println( lines );
//            System.out.println( "Brute Force: " + measureRunningTime( lines, Algorithm.BRUTE_FORCE ) + "ms" );
            System.out.println( "Plane Sweep: " + measureRunningTime( lines, Algorithm.PLANE_SWEEP ) + "ms" );
        }

        System.out.println( "\n" + bound + "------>" );
    }

    private enum Algorithm {
        BRUTE_FORCE, PLANE_SWEEP
    }

    /**
     * measure running time for a give Convex Hull algorithm
     * */

    private static
    long measureRunningTime( List<Line> lines, Algorithm whichAlgorithm ) {
        long startTime = System.currentTimeMillis();

        switch ( whichAlgorithm ) {
            case BRUTE_FORCE -> GeometricIntersection.bruteForceLinesIntersection( lines );
            case PLANE_SWEEP -> {
                List<IntersectionShape> nonOverlappingSegments = new ArrayList<>( lines.size() + 1 );

                for ( Line seg : lines ) {
                    nonOverlappingSegments.add( new InterLine( seg ) );
                }

                startTime = System.currentTimeMillis();
                GeometricIntersection.findIntersection( nonOverlappingSegments );
            }
            default -> {
                assert false;
            }
        }

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    /**
     * You should randomly generate these files.
     * Compare the run-time (in seconds) for input sizes 10, 100, 1000, 10000, 100000, etc.
     * */

    /*
     * n = 10 Get Started-------------->
     * Brute Force: 2ms
     * Plane Sweep: 15ms
     * n = 100 Get Started-------------->
     * Brute Force: 4ms
     * Plane Sweep: 26ms
     * n = 1000 Get Started-------------->
     * Brute Force: 63ms
     * Plane Sweep: 759ms
     * n = 10000 Get Started-------------->
     * Brute Force: 6072ms
     * Plane Sweep: 1531ms
     * n = 100000 Get Started-------------->
     * Brute Force: out of heap space
     * Plane Sweep: assert failed in red-black tree
     * */

    private static
    void runTimeAnalysis() {
        int[] sizes = new int[] { 10, 100, 1000, 10000, 100000, 1000000 };
//        int[] sizes = new int[] { 10 };
        Vector[] endpoints = new Vector[ 2 ];
        int bound = 1000;

        for ( int num : sizes ) {
            System.out.println("n = " + num + " Get Started-------------->");

            List<Integer> coorX = MyMath.generateRandomInts( 2 * num, num );
            List<Integer> coorY = MyMath.generateRandomInts( 2 * num, num );

            List<Line> lines = new ArrayList<>( num + 1 );
            for ( int i = 0; i < num; i += 2 ) {
                endpoints[ 0 ] = new Vector( coorX.get( i ), coorY.get( i ) );
                endpoints[ 1 ] = new Vector( coorX.get( i + 1 ), coorY.get( i + 1 ) );
                if ( endpoints[ 0 ].equalsXAndY( endpoints[ 1 ] ))
                    continue;

                // guarantee that left Endpoint <= right Endpoint
                Arrays.sort( endpoints, Vectors::sortByX );
                lines.add( new Line( endpoints[ 0 ], endpoints[ 1 ] ) );
            }

            lines = Lines.mergeOverlappingLines( lines );
//            System.out.println( lines );
//            System.out.println( "Brute Force: " + measureRunningTime( lines, Algorithm.BRUTE_FORCE ) + "ms" );
            System.out.println( "Plane Sweep: " + measureRunningTime( lines, Algorithm.PLANE_SWEEP ) + "ms" );
        }
    }

    // runTimeAnalysis --------------------------------------

    /**
     * process input data
     * */

    @Override
    public void processingFile( Scanner sc ) {
        boolean initializeLength = true;
        Vector[] points = new Vector[ 2 ];

        while ( sc.hasNext() ) {
            String content = sc.nextLine();
            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            // read info like number of points
            if ( initializeLength &&
                    Pattern.matches( ReadFromStdOrFile.PATTERN_LENGTH, content ) ) {
                String[] numbers = content.split( ReadFromStdOrFile.PATTERN_WHITE_CHARACTER );
                segments = new ArrayList<>( Integer.parseInt( numbers[ 0 ] ) );
                initializeLength = false;
                continue;
            }

            // build lines from points
            String[] numbers = content.split( ReadFromStdOrFile.PATTERN_WHITE_CHARACTER );
            int x1 = Integer.parseInt( numbers[ 0 ] );
            int y1 = Integer.parseInt( numbers[ 1 ] );
            int x2 = Integer.parseInt( numbers[ 2 ] );
            int y2 = Integer.parseInt( numbers[ 3 ] );

            points[ 0 ] = new Vector( x1, y1 );
            points[ 1 ] = new Vector( x2, y2 );
            // skip two endpoints are the same,
            // i.e. this line is actually one endpoint on the other one
            if ( points[ 0 ].equalsXAndY( points[ 1 ] ) )
                continue;

            Arrays.sort( points, Vectors::sortByX );
            segments.add( new InterLine( points[ 0 ], points[ 1 ] ) );
        }

//         System.out.println( segments );
    }

    /**
     * paraphrase Arguments
     *
     * command line format:
     * java programPath -inputFilePath inputFilePath [ -originWidth originWidth -originHeight originWidth -outputFilePath outputFilePath -turnOnVisualization turnOnVisualization ]
     *
     * see more info in Instructions for Assignment 2 md. file
     * */

    private void paraphraseArgs( String[] args ) {
        for ( int i = 0; i < args.length; i++ ) {
            switch ( args[ i ] ) {
                case "-inputFilePath" -> inputFilePath = args[ ++i ];
                case "-outputFilePath" -> outputFilePath = args[ ++i ];
                case "-turnOnVisualization" -> isVisualization = Boolean.parseBoolean( args[ ++i ] );
                case "-originWidth" -> originWidth = Integer.parseInt( args[ ++i ] );
                case "-originHeight" -> originHeight = Integer.parseInt( args[ ++i ] );
            }
        }
    }

    /**
     * Do the algorithm to slave the problem.
     * */

    public void doTheAlgorithm( String outputFilePath, String prefix ) {
        // visualized program
        IntersectProgram drawer = new IntersectProgram( originWidth, originHeight );

//        System.out.println( segments );
        segments = Lines.mergeOverlappingLines( segments );
//        System.out.println( segments );
        nonOverlappingSegments = new ArrayList<>( segments.size() + 1 );

        for ( Line seg : segments ) {
            nonOverlappingSegments.add( new InterLine( seg ) );
        }

        // draw lines only without intersection，for testing
//        drawer.draw( segments, null );
//        drawer.initialize();

        // compute intersections
        List<EventPoint2D> intersections = GeometricIntersection.findIntersection( nonOverlappingSegments );
//        List<EventPoint2D>  intersections = new ArrayList<>();
        TreeSet<Vector> intersectionsBruteForce = GeometricIntersection.bruteForceLinesIntersection( segments );

        List<Vector> intersectionsClever = new ArrayList<>( intersections.size() + 1 );
        intersectionsClever.addAll( intersections );

        drawer.draw( segments, intersectionsClever, DrawingProgram.INTERSECTION_COLOR );
        if ( isVisualization )
            drawer.initialize();

        // print the result for testing
//        System.out.println( intersectionsBruteForce );
//        System.out.println( intersectionsClever );
        assert intersections.size() == intersectionsBruteForce.size();

        // output to the file
        StringBuilder test = new StringBuilder( intersections.size() + "\n" );
        intersections.forEach( vertex -> test.append( vertex.x ).append( " " ).append( vertex.y ).append( "\n" ) );
        MyWriter.fileWriterMethod( MyWriter.preprocessFilePath( outputFilePath, prefix, isIDEA ), test.toString() );
    }

    public static
    void main( String[] args ) {
        int size = 20;

        // 2.1 IDEA
        // 1) only given inputFilePath
//        new AssignmentTwo( "src/PA_1/problem_2/test_1/24", true, size, size );

        // 2) given both inputFilePath and outputFilePath
        size = 3;
        new AssignmentTwo( "src/PA_1/problem_2/test_1/3", "src/CSCI716/assign_2/3", true, size, size );
        size = 16;
        new AssignmentTwo( "src/PA_1/problem_2/test_1/24", "src/CSCI716/assign_2/24", true, size, size );
        size = 240;
        new AssignmentTwo( "src/PA_1/problem_2/test_1/27", "src/CSCI716/assign_2/27", true, size, size );
        size = 20;
//        new AssignmentTwo( "src/PA_1/problem_2/test_1/28", "src/CSCI716.assign_2/27", true, size, size );
//        new AssignmentTwo( "src/PA_1/problem_2/test_1/29", "src/CSCI716.assign_2/29", true, size, size );
//        new AssignmentTwo( "src/PA_1/problem_2/test_1/30", "src/CSCI716.assign_2/30", true, size, size );
//        new AssignmentTwo( "src/PA_1/problem_2/test_1/31", "src/CSCI716.assign_2/31", true, size, size );
//        new AssignmentTwo( "src/PA_1/problem_2/test_1/24", "src/CSCI716.assign_2/24", true, size, size );
//        new AssignmentTwo( "src/PA_1/problem_2/test_1/32", "src/CSCI716.assign_2/32", true, size, size );
//        new AssignmentTwo( "src/PA_1/problem_2/test_1/33", "src/CSCI716.assign_2/33", true, size, size );
//        new AssignmentTwo( "src/PA_1/problem_2/test_1/34", "src/CSCI716.assign_2/34", true, size, size );
//        new AssignmentTwo( "src/PA_1/problem_2/test_1/35", "src/CSCI716.assign_2/35", true, size, size );

        // 2.2 Command Line
//        new AssignmentTwo( args );

        // run Time Analysis code
//        runTimeAnalysis();

//        test();
    }
}
