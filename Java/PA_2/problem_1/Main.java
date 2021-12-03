package PA_2.problem_1;

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

import myLibraries.GUI.geometry.triangulation.Program;
import myLibraries.io.ProcessingFile;
import myLibraries.io.ReadFromStdOrFile;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.DCEL.MonotoneVertex;
import myLibraries.util.geometry.DCEL.Vertex;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.MonotonePolygons;
import myLibraries.util.geometry.tools.Polygons;
import myLibraries.util.graph.Graph;
import myLibraries.util.graph.elements.DualVertex;
import myLibraries.util.graph.tools.Graphs;
import myLibraries.util.graph.tools.SingleShortestPath;

import java.util.*;
import java.util.regex.Pattern;

/**
 * CG2017 PA2-1 Shortest Path in The Room
 * https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1647
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class Main implements ProcessingFile {
    private final static String PATTERN_START_END_POINT = "^-*\\d+ -*\\d+ -*\\d+ -*\\d+$";
    private List<Vertex> vertices;
    private Vector startPoint;
    private Vector endPoint;
    private final int originalWidth;
    private final int originalHeight;

    /**
     * process data and output the result
     * */

    public Main( String fileName, int originalWidth, int originalHeight ) {
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;

        boolean ifReadFromFile = ReadFromStdOrFile.readFromFile(
                fileName, this );

        if ( ifReadFromFile )
            System.out.printf( "%s - \n%s\n",
                    fileName.substring( fileName.lastIndexOf( '/' ) + 1 ),
                    output( doTheAlgorithm() ) );
        else
            System.out.printf( "%s\n", output( doTheAlgorithm() ) );

        Face.resetIDStatic();
    }

    private String output( int corners ) {
        if ( corners < -1 )
            return "None";
        else if ( corners <= 0 )
            return "s\ne";

        return "s\n" + corners + "\ne";
    }

    private void readInfo( int initializeLength, String[] info ) {
        switch ( initializeLength ) {
            case 0:
                assert info.length == 4;
                startPoint = new Vector( Integer.parseInt( info[ 0 ] ), Integer.parseInt( info[ 1 ] ) );
                endPoint = new Vector( Integer.parseInt( info[ 2 ] ), Integer.parseInt( info[ 3 ] ) );
                break;
            case 1:
                assert info.length == 1;
                vertices = new ArrayList<>( Integer.parseInt( info[ 0 ] ) );
                break;
            default:
                assert false;
        }
    }

    /**
     * process input data
     */

    public void processingFile( Scanner sc ) {
        int initializeLength = 0;

        MonotoneVertex last = null;
        while ( sc.hasNext() ) {
            String content = sc.nextLine().strip();
            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            boolean isLength = Pattern.matches( ReadFromStdOrFile.PATTERN_LENGTH, content );
            boolean arePoints = Pattern.matches( PATTERN_START_END_POINT, content );
            if ( initializeLength < 2 &&
                    ( isLength || arePoints ) ) {
                readInfo( initializeLength++, content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS ) );
                continue;
            }

            String[] numbers = content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS );
            int num1 = Integer.parseInt( numbers[ 0 ] );
            int num2 = Integer.parseInt( numbers[ 1 ] );
            MonotoneVertex vertex = new MonotoneVertex( num1, num2 );

            // ignore duplicate points
            if ( last != null && last.equalsXAndY( vertex ) ) continue;
            last = vertex;

            vertices.add( vertex );
        }

//         System.out.println( vertices );
    }

    /**
     * do the four algorithms to solve the problem:
     *
     * 1. partitioning monotone polygons;
     * 2. triangulation;
     * 3. BFS in dual graph;
     * 4. funnel algorithm;
     */

    public int doTheAlgorithm() {
        // visualized program
        Program drawer = new Program( originalWidth, originalHeight );
        drawer.addStartAndEndPoints( startPoint, endPoint );

        vertices = Polygons.removePointsOnTheSameLine( vertices );
        if ( vertices.size() < 3 ) return -2;

        // get DCEL
        Face[] faces = Polygons.getDCEL( vertices );
        if ( faces == null ) return -2;

        // And also determine Vertex type for each vertex.
        MonotonePolygons.getVertexType( faces[ 1 ] );

        drawer.addDrawingData( vertices, faces );

        // partitioning monotone polygons
        List<Face> monotonePolygons = MonotonePolygons.makeMonotone( vertices );
        monotonePolygons.addAll( Arrays.asList( faces ) );
        drawer.drawVertexType( vertices );
        drawer.addDrawingData( vertices, monotonePolygons );

        // triangulation
        List<Face> triangles = MonotonePolygons.preprocessMonotonePolygon( monotonePolygons );
        triangles.addAll( monotonePolygons );
        drawer.addDrawingData( vertices, triangles );

        // get dual graph
        Graph<DualVertex> graph = Graphs.getDualGraph( triangles, faces[ 0 ] );
        drawer.addDualGraph( graph );

        Face startFace = Polygons.OnWhichFace( triangles, startPoint );
        Face endFace = Polygons.OnWhichFace( triangles, endPoint );

        if ( startFace == null || endFace == null )
            return -2;

        // shortest path with dual graph
        DualVertex start = graph.getVertexByIndex( startFace.IDOfDualVertex );
        DualVertex end = graph.getVertexByIndex( endFace.IDOfDualVertex );
        SingleShortestPath.BFS( graph.size(), start, end );
        // add Start And End Points, dual vertices
        drawer.addStartAndEndPoints( start, end );

        // funnel to find the real shortest path
        List<Vector> corners = SingleShortestPath.Funnel( end, endPoint, startPoint );
        drawer.draw( corners );

        // draw
        drawer.initialize();
        return corners.size() - 2;
    }

    /**
     * test:
     * 1. partitioning monotone polygons;
     * 2. triangulation;
     */

    private static
    void testCaseOne() {
        String prefix = "/testCase_1/";
        int size = 12;
        //  be sure to add original width and height for the visualization program

//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, 1, prefix ) ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, 2, prefix ) ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, 3, prefix ) ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, 4, prefix ) ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, 5, prefix ) ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, 6, prefix ) ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, 7, prefix ) ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, 8, prefix ) ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, 9, prefix ) ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, 10, prefix ) ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, 11, prefix ) ); //
    }

    /**
     * test:
     * 3. BFS in dual graph;;
     * 4. funnel algorithm;
     */

    private static
    void testCaseTwo() {
        String prefix = "/testCase_2/";
        int size = 12;
        //  be sure to add original width and height for the visualization program
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_1", prefix ), size, size ); // 0
//        new Main( "PA_2/problem_1/testCase_2/1_1", size, size ); // 0 - command line
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_2", prefix ) ); // 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_3", prefix ) ); // 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_4", prefix ) ); // 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_5", prefix ) ); // 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_6", prefix ) ); // 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_7", prefix ) ); // 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_8", prefix ) ); // 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_9", prefix ) ); // 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_10", prefix ) ); // 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_11", prefix ) ); // 1, example in the textbook
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_12", prefix ) ); // 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_13", prefix ) ); // 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_14", prefix ) ); // 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_15", prefix ) ); // 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_16", prefix ) ); // 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_17", prefix ) ); // 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_18", prefix ) ); // None
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_19", prefix ) ); // None
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_20", prefix ) ); // None
        size = 34;
        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_21", prefix ), size, size ); // 13, maze
//        new Main( "PA_2/problem_1/testCase_2/1_21", size, size ); // 13, maze - command line
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_22", prefix ) ); // 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_23", prefix ) ); // 5
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_24", prefix ) ); // 1
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_25", prefix ) ); // 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_26", prefix ) ); // 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_27", prefix ) ); // 3
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_28", prefix ) ); // 8
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_29", prefix ) ); // None
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_30", prefix ) ); // None
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_31", prefix ) ); // None
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_32", prefix ) ); // None
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_33", prefix ) ); // None
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_34", prefix ) ); //  0
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_35", prefix ) ); //  9
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 1, "1_36", prefix ) ); //  None
    }

    public static
    void main( String[] args ) {
//        testCaseOne();
        testCaseTwo();
    }
}
