package PA_2.problem_2;

/*
 * Main.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/7/2022$
 */

import myLibraries.GUI.geometry.DCELProgram;
import myLibraries.GUI.geometry.VoronoiProgram;
import myLibraries.io.ProcessingFile;
import myLibraries.io.ReadFromStdOrFile;
import myLibraries.util.geometry.*;
import myLibraries.util.geometry.BoundingBox;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.DCEL.VoronoiFace;
import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.Vector;
import myLibraries.util.tree.MyPriorityQueue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * CG2017 PA2-2 Find Dancing Partners
 * https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1648
 *
 * hints:
 * One way is to create Voronoi diagram based on the positions of the boys.
 * Apply point location algorithm to find which cell each girl stays in and retrieve the nearest boy.
 * Another method is dividing the dance floor into grids.
 * Search nearby boys in neighboring cells of each girl.
 *
 * In this implementation, I used the first idea to solve the problem.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

// TODO: 3/10/2022 output ID for toString of VoronoiFace.
public final class Main implements ProcessingFile {
    private List<Face> boys;
    private List<Face> girls;
    private List<Face> siteFaces;

    /**
     * @param isMatching true, match the nearest partners;
     *                   false, only compute Voronoi Diagrams and
     *                   the trapezoidal map based on it, with all girls and boys.
     * */

    public Main( String fileName, boolean isMatching ) {
        ReadFromStdOrFile.readFromFile( fileName, this );

        if ( isMatching ) doTheAlgorithm( girls, boys ).forEach(
                h -> System.out.println( h.peek() ) );
        else doTheAlgorithm( new ArrayList<>(), siteFaces );
    }

    private void readInfo( int initializeLength, String length ) {
        switch ( initializeLength ) {
            case 0 -> boys = new ArrayList<>( Integer.parseInt( length ) + 1 );
            case 1 -> girls = new ArrayList<>( Integer.parseInt( length ) + 1 );
            default -> { assert false; }
        }
    }

    /**
     * process input data
     * */

    @Override
    public void processingFile( Scanner sc ) {
        int initializeLength = 0;

        while ( sc.hasNext() ) {
            String content = sc.nextLine();
            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            if ( initializeLength < 2 &&
                    Pattern.matches( "^(\\d+)", content ) ) {
                readInfo( initializeLength++, content );
                continue;
            }

            String[] numbers = content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS );
            int x = Integer.parseInt( numbers[ 0 ] );
            int y = Integer.parseInt( numbers[ 1 ] );

            switch ( initializeLength ) {
                case 1 -> boys.add( new VoronoiFace( new Vector( x, y ) ) );
                case 2 -> girls.add( new VoronoiFace( new Vector( x, y ) ) );
                default -> { assert false; }
            }
        }

        siteFaces = new ArrayList<>( boys.size() + girls.size() + 1 );
        siteFaces.addAll( boys );
        siteFaces.addAll( girls );

//        System.out.println( boys );
//        System.out.println( girls );
    }

    private
    List<MyPriorityQueue<Face>> doTheAlgorithm( List<Face> girls, List<Face> boys ) {

        // compute the Voronoi Diagrams with the boys.
        BoundingBox box = VoronoiDiagrams.voronoiDiagrams( boys );

        // visualization program for Voronoi.
        VoronoiProgram program = new VoronoiProgram( 0, 0 );
        // segments ( Voronoi edges ) to build the trapezoidal map.
        List<Line> segments = null;
        // visualization boundary for Voronoi and trapezoidal map.
        int boundary = 0;
        // get the visualization box for Voronoi and trapezoidal map.
        // And also constrict every query point in the bounding box
        // containing all sites, Voronoi vertices and query points.
        List<Vector> sites = new ArrayList<>( girls.size() + boys.size() + 1 );
        siteFaces.forEach( f -> sites.add( ( ( VoronoiFace ) f ).site ) );
        BoundingBox visualBox = BoundingBox.getBoundingBox( sites, 10 );
        boundary = visualBox == null ? 0 : visualBox.findVisualizationArea();
        // initialize visualization program for Voronoi.
        program = new VoronoiProgram( boundary, boundary );
        program.addVertices( siteFaces );
        program.addPolyAndCircles( box );

        // get segments from the Voronoi Diagrams to compute its trapezoidal map.
        segments = VoronoiDiagrams.getSegments( box );

        // initialize a priority queue to store the nearest partners for a girl,
        // considering a partner with smaller ID as higher priority in the queue.
        List<MyPriorityQueue<Face>> matches = new ArrayList<>( girls.size() + 1 );
        // compute the map and do the matching processing.
        doTheAlgorithm( girls, segments, matches, program, boundary );

        program.initialize();
        return matches;
    }

    private void doTheAlgorithm( List<Face> girls, List<Line> segments,
                                 List<MyPriorityQueue<Face>> matches,
                                 VoronoiProgram program, int boundary ) {
        if ( segments == null ) return;

        // initialize the visualization program for the map.
        BoundingBox b = PointLocation.trapezoidalMap( segments );
        DCELProgram drawer = new DCELProgram( boundary, boundary );
        List<Vector> points = new ArrayList<>();
        for ( Line segment : segments ) {
            points.add( segment.startPoint );
            points.add( segment.endPoint );
        }

        // pass drawing info to the program
        TrapezoidalMap.drawTrapezoidalMap( b.SS, drawer, points );

        // matching process.
        girls.forEach( g -> {
            // each girl is a query point.
            Vector p = ( ( VoronoiFace ) g ).site;
            // query it in the map.
            // and res cannot be null, since we constrict every query point
            // in the bounding box containing all sites, Voronoi vertices and query points.
            SearchVertex res = b.SS.get( p );

            // smaller number has higher priority.
            matches.add( new MyPriorityQueue<>( Comparator.comparingInt( f -> -f.ID ) ) );

            TrapezoidalMap.drawQuery( res, p, drawer );

            // thus, every query point must have some closest Voronoi faces.
            // i.e. partners cannot be null.
            List<Face> partners = null;
            // which boys to which this girl is the closest.
            program.addQuery( partners = VoronoiDiagrams.findCell( res, p ), p );

            partners.forEach( f -> matches.get( matches.size() - 1 ).insert( f ) );
        } );

        drawer.initialize();
    }

    static String prefix1 = "/test_1/";
    static String prefix2 = "/test_2/";

    private static
    void testVoronoi() {
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 1, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 2, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 3, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 4, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 5, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 6, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 7, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 8, prefix1 ), false ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 9, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 10, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 11, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 12, prefix1 ), false ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 13, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 14, prefix1 ), false );
        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 15, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 16, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 17, prefix1 ), false ); // ZeldaBreath
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 18, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 19, prefix1 ), false );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 20, prefix1 ), false ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 21, prefix1 ), false ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 22, prefix1 ), false ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 23, prefix1 ), false ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 24, prefix1 ), false ); // duplicate sites
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 25, prefix1 ), false ); //
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 26, prefix1 ), false );
    }

    private static
    void testMatching() {
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 1, prefix2 ), true );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 1, prefix1 ), true );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 2, prefix1 ), true );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 2, prefix2 ), true );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 3, prefix2 ), true );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 4, prefix2 ), true );
//        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 17, prefix1 ), true ); // ZeldaBreath
        new Main( ReadFromStdOrFile.getFilePathCG( 2, 2, 5, prefix2 ), true ); // ZeldaBreath, complete query
    }

    public static
    void main( String[] args ) {
        testVoronoi();
        testMatching();
    }
}
