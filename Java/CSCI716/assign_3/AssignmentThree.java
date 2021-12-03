package CSCI716.assign_3;

/*
 * AssignmentThree.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 10/23/2021$
 */

import myLibraries.GUI.geometry.convexHull.Program;
import myLibraries.io.MyWriter;
import myLibraries.io.ProcessingFile;
import myLibraries.io.ReadFromStdOrFile;
import myLibraries.lang.MyMath;
import myLibraries.util.Matrix;
import myLibraries.util.geometry.DCEL.Vertex;
import myLibraries.util.geometry.elements.line.Line;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.PointLocation;
import myLibraries.util.geometry.tools.TrapezoidalMap;
import myLibraries.util.geometry.tools.Vectors;
import myLibraries.util.graph.SearchStructure;
import myLibraries.util.graph.elements.SearchVertex;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Programming Assignment 3 - Trapezoidal Map and Planar Point Location
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class AssignmentThree implements ProcessingFile {
    // input, output and visualization
    private static final String prefix = "assign_3/";
    private String inputFilePath ;
    private String outputFilePath;
    private final boolean isIDEA;

    private boolean isVisualization;
    private int originWidth;
    private int originHeight;

    // data
    private List<Line> segments;
    private Vertex bottomLeft;
    private Vertex topRight;

    private Vector queryPoint;

    /**
     * constructs to create an instance of AssignmentThree
     * */

    public AssignmentThree( String fileName, boolean isVisualization, int originWidth, int originHeight ) {
        this( fileName, null, null, isVisualization, originWidth, originHeight );
    }

    public AssignmentThree( String fileName ) {
        this( fileName, null, null, false, 0, 0 );
    }

    public AssignmentThree( String fileName, double x, double y,
                            boolean isVisualization, int originWidth, int originHeight ) {
        this( fileName, null, x, y, isVisualization, originWidth, originHeight );
    }

    public AssignmentThree( String fileName, double x, double y ) {
        this( fileName, null, x, y, false, 0, 0 );
    }

    /**
     * constructs to create an instance of AssignmentThree
     *
     * @param fileName Path to the input file
     * @param filePath Path to the output file (optional)
     * @param x x coordinate of the query point (optional)
     * @param y y coordinate of the query point (optional)
     * @param isVisualization Turn on Visualization? (optional)
     * @param originWidth window width of input data (optional)
     * @param originHeight window height of input data (optional)
     * */

    public AssignmentThree( String fileName, String filePath, double x, double y,
                            boolean isVisualization, int originWidth, int originHeight ) {
        this( fileName, filePath, new Vector( x, y ), isVisualization, originWidth, originHeight );
    }

    AssignmentThree( String fileName, String filePath, Vector queryPoint,
                            boolean isVisualization, int originWidth, int originHeight ) {
        this.isVisualization = isVisualization;
        this.originWidth = originWidth;
        this.originHeight = originHeight;
        isIDEA = true;
        this.queryPoint = queryPoint;

        ReadFromStdOrFile.readFromFile( inputFilePath = fileName, this );
        doTheAlgorithm( outputFilePath = filePath, prefix );
    }

    public AssignmentThree( String fileName, String filePath, boolean isVisualization,
                            int originWidth, int originHeight ) {
        this( fileName, filePath, null, isVisualization, originWidth, originHeight );
    }

    /**
     * constructs to create an instance of AssignmentThree with arguments
     * */

    public AssignmentThree( String[] args ) {
        paraphraseArgs( args );
        isIDEA = false;

        ReadFromStdOrFile.readFromFile( inputFilePath , this );
        doTheAlgorithm( outputFilePath, prefix );
    }

    /**
     * paraphrase Arguments
     *
     * command line format:
     * java programPath -inputFilePath inputFilePath [ -originWidth originWidth -originHeight originWidth -x x -y y -outputFilePath outputFilePath -turnOnVisualization turnOnVisualization ]
     *
     * see more info in Instructions for Assignment 3 md. file
     * */

    private void paraphraseArgs( String[] args ) {
        for ( int i = 0; i < args.length; i++ ) {
            switch ( args[ i ] ) {
                case "-inputFilePath" -> inputFilePath = args[ ++i ];
                case "-outputFilePath" -> outputFilePath = args[ ++i ];
                case "-turnOnVisualization" -> isVisualization = Boolean.parseBoolean( args[ ++i ] );
                case "-originWidth" -> originWidth = Integer.parseInt( args[ ++i ] );
                case "-originHeight" -> originHeight = Integer.parseInt( args[ ++i ] );
                case "-x" -> {
                    if ( queryPoint == null )
                        queryPoint = new Vector( Double.parseDouble( args[ ++i ] ), 0 );
                    else queryPoint.x = Double.parseDouble( args[ ++i ] );
                }
                case "-y" -> {
                    if ( queryPoint == null )
                        queryPoint = new Vector( 0, Double.parseDouble( args[ ++i ] ) );
                    else queryPoint.y = Double.parseDouble( args[ ++i ] );
                }
            }
        }
    }

    /**
     * process input data
     * */

    @Override
    public void processingFile( Scanner sc ) {
        int initializeLength = 0;
        Vector[] points = new Vector[ 2 ];

        int index = 0;
        while ( sc.hasNext() ) {
            String content = sc.nextLine();
            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            // read info like number of points
            if ( initializeLength == 0 &&
                    Pattern.matches( ReadFromStdOrFile.PATTERN_LENGTH, content ) ) {
                segments = new ArrayList<>( Integer.parseInt( content ) + 1 );
                initializeLength++;
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

            if ( initializeLength++ == 1 ) {
                bottomLeft = new Vertex( points[ 0 ] );
                topRight = new Vertex( points[ 1 ] );
                continue;
            }
            assert initializeLength > 1;

            // skip two endpoints are the same,
            // i.e. this line is actually one endpoint on the other one
            if ( points[ 0 ].equals( points[ 1 ] ) )
                continue;

            Arrays.sort( points, Vectors::sortByX );
            segments.add( new Line( points[ 0 ], points[ 1 ] ) );
        }

//         System.out.println( segments );
    }

    /**
     * we may have the node traversed several times
     * */

    static
    List<SearchVertex> getDistinct( List<SearchVertex> nodes, Comparator<? super SearchVertex> c ) {
        nodes.sort( c );
        // only need for trapezoid, since they have no children,
        // they will appear in the tree multiple times with the same nodes,
        // but this is not true for y nodes, which also appear in the tree more than once,
        // but with different nodes wrapping the same lines.
        return nodes.stream().distinct().collect( Collectors.toList() );
    }

    /**
     * collect y nodes with the same content into one
     * */

    static
    void collect( List<SearchVertex> nodes ) {
        for ( int i = 0; i < nodes.size() - 1; i++ ) {
            if ( nodes.get( i ).line.equals( nodes.get( i + 1 ).line ) ) {
                nodes.get( i ).duplicates.add( nodes.remove( i + 1 ) );
                // track back
                i--;
            }
        }
    }

    /**
     * get all the nodes, x nodes, y nodes, leaf nodes, in an array,
     * and sort them in ascending order by their IDs
     * */

    static
    List<SearchVertex> getCol( SearchStructure SS ) {
        List<SearchVertex> xNodePs = getDistinct( SS.getAllXNodePs(), SearchVertex::sortByLineID );
        List<SearchVertex> xNodeQs = getDistinct( SS.getAllXNodeQs(), SearchVertex::sortByLineID );
        List<SearchVertex> yNodes = getDistinct( SS.getAllYNodes(), SearchVertex::sortByLineID );
        collect( yNodes );
        List<SearchVertex> leaves = getDistinct( SS.getAllLeafNodes(), SearchVertex::sortByTrapezoidID );

        // P -> Q -> S -> T
        List<SearchVertex> columns = new ArrayList<>( xNodePs.size() + xNodeQs.size() + yNodes.size() + leaves.size() + 1 );
        columns.addAll( xNodePs );
        columns.addAll( xNodeQs );
        columns.addAll( yNodes );
        columns.addAll( leaves );

        // set matrix index for each vertex
        for ( int i = 0; i < columns.size(); i++ ) {
            SearchVertex vertex = columns.get( i );
            vertex.matrixIndex = i;
            vertex.duplicates.forEach( d -> {
                d.matrixIndex = vertex.matrixIndex;
            } );
        }

        return columns;
    }

    /**
     * get Adjacency Matrix
     * */

    static
    Matrix getAdjacencyMatrix( List<SearchVertex> columns ) {
        Matrix matrix = new Matrix( columns.size() + 1, columns.size() + 1 );
        // counting
        columns.forEach( vertex -> {
            if ( !vertex.isLeaf() ) {
                assert MyMath.isEqualZero( matrix.get( vertex.left.matrixIndex, vertex.matrixIndex ) ) : vertex + "\n" + vertex.left;
                matrix.set( vertex.left.matrixIndex, vertex.matrixIndex, 1 );
                assert MyMath.isEqualZero( matrix.get( vertex.right.matrixIndex, vertex.matrixIndex ) ) : vertex + "\n" + vertex.right;
                matrix.set( vertex.right.matrixIndex, vertex.matrixIndex, 1 );
            }

            vertex.duplicates.forEach( d -> {
                if ( !d.isLeaf() ) {
                    matrix.set( d.left.matrixIndex, d.matrixIndex, matrix.get( d.left.matrixIndex, d.matrixIndex ) + 1 );
                    matrix.set( d.right.matrixIndex, d.matrixIndex, matrix.get( d.right.matrixIndex, d.matrixIndex ) + 1 );
                }
            } );
        } );

        // sum
        for ( int i = 0; i < columns.size(); i++ )
            matrix.set( i, columns.size(), matrix.sumRow( i ) );

        for ( int j = 0; j < columns.size(); j++ )
            matrix.set( columns.size(), j, matrix.sumCol( j ) );

        return matrix;
    }

    /**
     * The output of your incremental algorithm should be a file containing the adjacency matrix.
     * In the adjacency matrix, the rows and columns represent
     * the nodes of the rooted directed acyclic graph.
     * A value of 1 in the matrix indicates that the child node
     * in the row of the matrix is connected to the parent node in the column.
     * A value of 0 indicates that the node in the row is not connected to the column node.
     * The row with a sum value of zero value (highlighted in green) indicates that node to be the root.
     * The columns with sum value of zero (highlighted in green) indicate
     * the leaf nodes (i.e. the resulting trapezoids).
     *
     * output the adjacency matrix as string
     * */

    static
    String toStringMatrix( List<SearchVertex> columns, Matrix matrix ) {
        StringBuilder text = new StringBuilder( " " );
        // first row: Ps -> Qs -> Ts in ascending order of their IDs
        columns.forEach( vertex -> text.append( vertex.name ).append( " " ) );
        text.append( "\n" );

        int index = 0;
        for ( int i = 0; i < matrix.i; i++ ) {
            // each row: node's type -> 1 or 0 in a row
            text.append( index >= columns.size() ? "" : columns.get( index++ ).name ).append( " " );
            for ( int j = 0; j < matrix.j; j++ ) {
                text.append( matrix.get( i, j ) ).append( " " );
            }
            text.append( "\n" );
        }

        return text.toString();
    }

    /**
     * Do the algorithm to slave the problem.
     * */

    public void doTheAlgorithm( String outputFilePath, String prefix ) {
        // get points for drawing
        List<Vector> points = new ArrayList<>();
        for ( Line segment : segments ) {
            points.add( segment.startPoint );
            points.add( segment.endPoint );
        }
        // initialize the visualization program
        Program drawer = new Program( originWidth, originHeight );

        // get the bounding box R
        SearchVertex R = PointLocation.getBoundingBox( bottomLeft, topRight );
        // get the search structure, SS after adding randomized segments one by one
        SearchStructure SS = PointLocation.trapezoidalMap( segments, R );
        // reset trapezoid's ID to their position in the SS, as leaf node
        // in order to output them in the matrix and query path
        SS.setLeafIDs();

        // pass drawing info to the program
        TrapezoidalMap.drawTrapezoidalMap( SS, drawer, points );

        // query the point in SS and get the traversal path
        if ( queryPoint != null ) {
            List<SearchVertex> path = SS.getPath( queryPoint );

            if ( path.isEmpty() )
                System.out.println( "Ouf of the bounding box, R. The defined area is: \n" + SS.boundingBox);
            else {
                SearchVertex trapezoid = path.get( path.size() - 1 );
                // pass query info to the program
                TrapezoidalMap.drawQuery( trapezoid, queryPoint, drawer );
                // print the traversal path
                System.out.println( SearchStructure.getSearchPathString( path ) );
            }
        }

        if ( isVisualization ) drawer.initialize();

        // output the matrix to the file
        List<SearchVertex> col = getCol( SS );
        String matrix = toStringMatrix( col, getAdjacencyMatrix( col ) );
        MyWriter.fileWriterMethod( MyWriter.preprocessFilePath( outputFilePath, prefix, isIDEA ), matrix );
    }

    public static
    void main( String[] args ) {
        int size = 34;

        // 2.1 IDEA
        // 1) only given inputFilePath
//        new AssignmentThree( "src/CSCI716.assign_3/1", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/2", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/3", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/4", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/5", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/6", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/7", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/8", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/9", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/10", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/14", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/15", true, size, size );

//        new AssignmentThree( "src/CSCI716.assign_3/11", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/12", true, size, size );


        // 2) given both inputFilePath and outputFilePath
//        new AssignmentThree( "src/CSCI716.assign_3/10", "src/CSCI716.assign_3/res_10", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/14", "src/CSCI716.assign_3/res_14", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/15", "src/CSCI716.assign_3/res_15", true, size, size );

        // 3) given query point
//        new AssignmentThree( "src/CSCI716.assign_3/10", "src/CSCI716.assign_3/res_10", -20, -20, true, size, size );
        new AssignmentThree( "src/CSCI716.assign_3/10", "src/CSCI716.assign_3/res_10", 0, 0, true, size, size );

        size = 220;
//        new AssignmentThree( "src/CSCI716.assign_3/xt1643.txt", true, size, size );
//        new AssignmentThree( "src/CSCI716.assign_3/xt1643.txt", "src/CSCI716.assign_3/res_xt1643", true, size, size );

        // 2.2 Command Line
//        new AssignmentThree( args );
    }
}
