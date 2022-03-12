package CSCI716.assign_1;

/*
 * AssignmentOne.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 * JDK: 15
 */

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.GUI.geometry.DCELProgram;
import myLibraries.io.MyWriter;
import myLibraries.io.ProcessingFile;
import myLibraries.io.ReadFromStdOrFile;
import myLibraries.lang.MyMath;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.DCEL.Vertex;
import myLibraries.util.geometry.elements.Vector;
import myLibraries.util.geometry.ConvexHull;
import myLibraries.util.geometry.Polygons;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * CSCI.716.01 - Computational Geometry
 * Programming Assignment 1 - Convex Hull
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class AssignmentOne implements ProcessingFile {
    // input, output and visualization
    private static final String prefix = "assign_1/";
    private String inputFilePath ;
    private String outputFilePath;
    private final boolean isIDEA;

    private boolean isVisualization;
    private int originWidth;
    private int originHeight;

    // data
    private List<Vector> points;

    /**
     * constructs to create an instance of AssignmentOne
     * */

    public AssignmentOne( String fileName ) {
        this( fileName, null, false, 0, 0 );
    }

    public AssignmentOne( String fileName, String filePath ) {
        this( fileName, filePath, false, 0, 0 );
    }

    public AssignmentOne( String fileName, boolean isVisualization, int originWidth, int originHeight ) {
        this( fileName, null, isVisualization, originWidth, originHeight );
    }

    /**
     * constructs to create an instance of AssignmentOne
     *
     * @param fileName Path to the input file
     * @param filePath Path to the output file (optional)
     * @param isVisualization Turn on Visualization? (optional)
     * @param originWidth window width of input data (optional)
     * @param originHeight window height of input data (optional)
     * */

    public AssignmentOne( String fileName, String filePath,
                          boolean isVisualization, int originWidth, int originHeight ) {
        this.isVisualization = isVisualization;
        this.originWidth = originWidth;
        this.originHeight = originHeight;
        isIDEA = true;

        ReadFromStdOrFile.readFromFile( inputFilePath  = fileName, this );
        doTheAlgorithm( outputFilePath = filePath, prefix );
    }

    /**
     * constructs to create an instance of AssignmentOne with arguments
     * */

    public AssignmentOne( String[] args ) {
        paraphraseArgs( args );
        isIDEA = false;

        ReadFromStdOrFile.readFromFile( inputFilePath , this );
        doTheAlgorithm( outputFilePath, prefix );
    }

    private enum Algorithm {
        BRUTE_FORCE, GRAHAM_SCAN
    }

    /**
     * measure running time for a give Convex Hull algorithm
     * */

    private static
    long measureRunningTime( List<Vector> points, Algorithm whichAlgorithm ) {
        long startTime = System.currentTimeMillis();

        switch ( whichAlgorithm ) {
            case BRUTE_FORCE -> ConvexHull.slowConvexHull( points );
            case GRAHAM_SCAN -> ConvexHull.grahamScan( points );
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
    * Brute Force: 0ms
    * Graham Scan: 0ms
    * n = 100 Get Started-------------->
    * Brute Force: 0ms
    * Graham Scan: 0ms
    * n = 1000 Get Started-------------->
    * Brute Force: 93ms
    * Graham Scan: 16ms
    * n = 10000 Get Started-------------->
    * Brute Force: 3019ms
    * Graham Scan: 31ms
    * n = 100000 Get Started-------------->
    * Brute Force: 253352ms
    * Graham Scan: 95ms
    * n = 1000000 Get Started-------------->
    * Brute Force: more than 5 mins
    * Graham Scan: 1890ms
    * */

    private static
    void runTimeAnalysis() {
        int[] sizes = new int[] { 10, 100, 1000, 10000, 100000, 1000000 };

        for ( int num : sizes ) {
            System.out.println("n = " + num + " Get Started-------------->");

            List<Integer> coorX = MyMath.generateRandomInts( num );
            List<Integer> coorY = MyMath.generateRandomInts( num );

            List<Vector> points = new ArrayList<>( num + 1 );
            for ( int i = 0; i < num; i++ ) {
                points.add( new Vector( coorX.get( i ), coorY.get( i ) ) );
            }

            System.out.println( "Brute Force: " + measureRunningTime( points, Algorithm.BRUTE_FORCE ) + "ms" );
            System.out.println( "Graham Scan: " + measureRunningTime( points, Algorithm.GRAHAM_SCAN ) + "ms" );
        }

    }

    /**
     * process input data
     * */

    public void processingFile( Scanner sc ) {
        boolean initializeLength = true;
        int ID = 0;

        while ( sc.hasNext() ) {
            String content = sc.nextLine();
            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            if ( initializeLength &&
                    Pattern.matches( ReadFromStdOrFile.PATTERN_LENGTH, content ) ) {
                points = new ArrayList<>( Integer.parseInt( content ) );
                initializeLength = false;
                continue;
            }

            String[] numbers = content.split( ReadFromStdOrFile.PATTERN_WHITE_CHARACTER );
            int x = Integer.parseInt( numbers[ 0 ] );
            int y = Integer.parseInt( numbers[ 1 ] );
            points.add( new Vector( x, y, ID++ ) );
        }

//         System.out.println( points );
    }

    /**
     * paraphrase Arguments
     *
     * command line format:
     * java programPath -inputFilePath inputFilePath [ -originWidth originWidth -originHeight originWidth -outputFilePath outputFilePath -turnOnVisualization turnOnVisualization ]
     *
     * see more info in Instructions for Assignment 1 md. file
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
     * The multiplication of IDs is so large that we have to use BIgInteger
     * */

    public void doTheAlgorithm( String filePath, String prefix ) {
        // visualized program
        DCELProgram drawer = new DCELProgram( originWidth, originHeight );

        // note that extreme points here are in clock wise order
        List<Vector> extremePoints = ConvexHull.grahamScan( points );
//        List<Vector> extremePoints = ConvexHull.slowConvexHull( points );
        if ( extremePoints == null ) return;

        // get extreme points in counter-clock wise order
        List<Vertex> vertices = new ArrayList<>();
        for ( int i = extremePoints.size() - 1; i >= 0; i-- )
            vertices.add( new Vertex( extremePoints.get( i ) ) );

        // get DCEL
        Face[] faces = Polygons.getDCEL( vertices );
        if ( faces == null ) return;

        drawer.addPoints( points, DrawingProgram.NORMAL_POLYGON_COLOR );
        drawer.addPoly( faces, DrawingProgram.NORMAL_POLYGON_COLOR );

        // draw
        if ( isVisualization )
            drawer.initialize();

        // output to the file
        StringBuilder test = new StringBuilder( extremePoints.size() + "\n" );
        vertices.forEach( vertex -> test.append( vertex.x ).append( " " ).append( vertex.y ).append( "\n" ) );
        MyWriter.fileWriterMethod( MyWriter.preprocessFilePath( filePath, prefix, isIDEA ), test.toString() );
    }

    public static
    void main( String[] args ) {
        int size = 22;
        // 2.1 IDEA
        // 1) only given inputFilePath
//        new AssignmentOne( "src/PA_1/problem_1/1", true, size, size );

        // 2) given both inputFilePath and outputFilePath
        new AssignmentOne( "src/PA_1/problem_1/1", "src/CSCI716/assign_1/1", true, size, size );
        size = 40;
        new AssignmentOne( "src/PA_1/problem_1/13", "src/CSCI716/assign_1/1", true, size, size );

        // 2.2 Command Line
//        new AssignmentOne( args );

        // run Time Analysis code
//        runTimeAnalysis();
    }
}
