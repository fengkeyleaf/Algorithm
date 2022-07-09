package CGTsinghua.PA_2.problem_1;

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
import CGTsinghua.PA_2.problem_1.GUI.Program;
import com.fengkeyleaf.io.ProcessingFile;
import com.fengkeyleaf.io.ReadFromStdOrFile;
import com.fengkeyleaf.util.geom.*;
import com.fengkeyleaf.util.geom.Vector;
import com.fengkeyleaf.util.graph.Graph;
import com.fengkeyleaf.util.graph.DualVertex;
import com.fengkeyleaf.util.graph.Graphs;
import com.fengkeyleaf.util.graph.SingleShortestPath;

import java.util.*;
import java.util.regex.Pattern;

/**
 * <a href="https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1647">CG2017 PA2-1 Shortest Path in The Room</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class Main extends MainCG
        implements ProcessingFile {

    private final static String PATTERN_START_END_POINT = "^-*\\d+ -*\\d+ -*\\d+ -*\\d+$";
    private List<Vertex> vertices;
    private Vector startPoint;
    private Vector endPoint;
    // TODO: 5/6/2022 remove the following two, using bounding box.
    private final int originalWidth;
    private final int originalHeight;

    /**
     * process data and output the result
     * */

    Main( String fileName, int originalWidth, int originalHeight ) {
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

    Main( int fileName, int originalWidth, int originalHeight, String prefix ) {
        this( getFilePathCG( 2, 1, fileName, prefix ), originalWidth, originalHeight);
    }

    Main( String fileName, int originalWidth, int originalHeight, String prefix ) {
        this( getFilePathCG( 2, 1, fileName, prefix ), originalWidth, originalHeight);
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

        Vertex last = null;
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
            double num1 = Double.parseDouble( numbers[ 0 ] );
            double num2 = Double.parseDouble( numbers[ 1 ] );
            Vertex vertex = new Vertex( num1, num2 );

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

    int doTheAlgorithm() {
        // visualized program
        Program drawer = new Program( originalWidth, originalHeight );
        drawer.addStartAndEndPoints( startPoint, endPoint );

        vertices = Polygons.removePointsOnTheSameLine( vertices );
        if ( vertices.size() < 3 ) return -2;

        // get DCEL
        Face[] faces = Polygons.getDCEL( vertices );
        if ( faces == null ) return -2;

        // add original polygon drawing data.
        drawer.addDrawingData( vertices, faces );

        // partitioning monotone polygons
        List<Face> monotonePolygons = MonotonePolygons.makeMonotone( faces[ 1 ] );
        monotonePolygons.addAll( Arrays.asList( faces ) );

        drawer.drawVertexType( vertices );
        drawer.addDrawingData( vertices, monotonePolygons );

        // triangulation
        List<Face> triangles = new ArrayList<>();
        Triangulation.triangulate( monotonePolygons ).forEach( triangles::addAll );
        triangles.addAll( monotonePolygons );
        drawer.addDrawingData( vertices, triangles );

        // get dual graph
        Graph<DualVertex> graph = Graphs.getDualGraph( triangles, faces[ 0 ] );
        drawer.addDualGraph( graph );

        Face startFace = ConvexHull.OnWhichFace( triangles, startPoint );
        Face endFace = ConvexHull.OnWhichFace( triangles, endPoint );

        if ( startFace == null || endFace == null )
            return -2;

        // shortest path with dual graph
        DualVertex start = graph.getVertexByIndex( startFace.IDOfDualVertex );
        DualVertex end = graph.getVertexByIndex( endFace.IDOfDualVertex );
        SingleShortestPath.BFS( graph.size(), start, end );
        // add Start And End Points, dual vertices
        drawer.addStartAndEndPoints( start, end );

        // funnel to find the real shortest path
        // FIXME: 6/30/2022 Funnel has a bug to compute how many corners to go through.
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
        int size = 34;
        //  be sure to add original width and height for the visualization program

        new Main( 1, size, size, prefix ); //
//        new Main( 2, size, size, prefix ); //
//        new Main( 3, size, size, prefix ); //
//        new Main( 4, size, size, prefix ); //
//        new Main( 5, size, size, prefix ); //
//        new Main( 6, size, size, prefix ); //
//        new Main( 7, size, size, prefix ); //
//        new Main( 8, size, size, prefix ); //
//        new Main( 9, size, size, prefix ); //
//        new Main( 10,size, size, prefix ); //
        new Main( 11,size, size, prefix ); //
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
//        new Main( "1_1", size, size, prefix ); // 0
//        new Main( "1_2", size, size, prefix ); // 1
//        new Main( "1_3", size, size, prefix ); // 0
//        new Main( "1_4", size, size, prefix ); // 1
//        new Main( "1_5", size, size, prefix ); // 0
//        new Main( "1_6", size, size, prefix ); // 0
//        new Main( "1_7", size, size, prefix ); // 0
//        new Main( "1_8", size, size, prefix ); // 1
//        new Main( "1_9", size, size, prefix ); // 2
//        new Main( "1_10",size, size,  prefix ); // 2
//        new Main( "1_11",size, size,  prefix ); // 1, example in the textbook
//        new Main( "1_12",size, size,  prefix ); // 1
//        new Main( "1_13",size, size,  prefix ); // 1
//        new Main( "1_14",size, size,  prefix ); // 1
//        new Main( "1_15",size, size,  prefix ); // 1
//        new Main( "1_16",size, size,  prefix ); // 1
//        new Main( "1_17",size, size,  prefix ); // 0
//        new Main( "1_18",size, size,  prefix ); // None
//        new Main( "1_19",size, size,  prefix ); // None
//        new Main( "1_20",size, size,  prefix ); // None
        size = 34;
        new Main( "1_21", size, size, prefix ); // 13, maze
//        new Main( "1_22", size, size, prefix ); // 1
//        new Main( "1_23", size, size, prefix ); // 5
//        new Main( "1_24", size, size, prefix ); // 1
//        new Main( "1_25", size, size, prefix ); // 2
//        new Main( "1_26", size, size, prefix ); // 2
//        new Main( "1_27", size, size, prefix ); // 3
//        new Main( "1_28", size, size, prefix ); // 8
//        new Main( "1_29", size, size, prefix ); // None
//        new Main( "1_30", size, size, prefix ); // None
//        new Main( "1_31", size, size, prefix ); // None
//        new Main( "1_32", size, size, prefix ); // None
//        new Main( "1_33", size, size, prefix ); // None
//        new Main( "1_34", size, size, prefix ); //  0
//        new Main( "1_35", size, size, prefix ); //  9
//        new Main( "1_36", size, size, prefix ); //  None
//        new Main( "1_42", size, size, prefix ); //

        // command line.
//        new Main( "CGTsinghua.PA_2/problem_1/testCase_2/1_1", size, size ); // 0
//        new Main( "CGTsinghua.PA_2/problem_1/testCase_2/1_21", size, size ); // 13, maze
    }

    public static
    void main( String[] args ) {
//        testCaseOne();
        testCaseTwo();
    }
}
