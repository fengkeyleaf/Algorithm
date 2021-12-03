package PA_1.problem_1;

/*
 * Main.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 * JDK: 15
 */

import myLibraries.GUI.geometry.convexHull.Program;
import myLibraries.io.ProcessingFile;
import myLibraries.io.ReadFromStdOrFile;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.DCEL.Vertex;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.ConvexHull;
import myLibraries.util.geometry.tools.Polygons;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * CG2017 PA1-1 Convex Hull
 * https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1645
 *
 * Analysis of time complexity:
 * 1) Find LTL; -> O(n)
 * 2) offset x and y by LTL's; -> O(n)
 * 3) sort points by polar angle; -> O(nlogn)
 * 3) Graham Scan; -> O(n)
 *
 * so O(nlogn) overall
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

// TODO: 5/16/2021 for now, failed two test cases, exit 1
public final class Main implements ProcessingFile {
    private List<Vector> points;

    public Main( String fileName ) {
        boolean ifReadFromFile = ReadFromStdOrFile.readFromFile(
                fileName, this );

        if ( ifReadFromFile )
            System.out.printf( "%s - %s\n",
                    fileName.substring( fileName.lastIndexOf( '/' ) + 1 ),
                    doTheAlgorithm() );
        else
            System.out.printf( "%s\n", doTheAlgorithm() );
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
     * Do the algorithm to slave the problem.
     * The multiplication of IDs is so large that we have to use BIgInteger
     * */

    public BigInteger doTheAlgorithm() {
        // visualized program
        Program drawer = new Program( 12, 12 );

        List<Vector> extremePoints = ConvexHull.grahamScan( points );
//        List<Vector> extremePoints = ConvexHull.slowConvexHull( points );
        if ( extremePoints == null ) return new BigInteger( "-1" );

        List<Vertex> vertices = new ArrayList<>();
        for ( int i = extremePoints.size() - 1; i >= 0; i-- )
            vertices.add( new Vertex( extremePoints.get( i ) ) );

        // get DCEL
        Face[] faces = Polygons.getDCEL( vertices );
        if ( faces == null ) return new BigInteger( "-1" );

//        drawer.draw( points, faces );

        // draw
        drawer.initialize();

//        System.out.println( extremePoints );
        BigInteger res = new BigInteger( "1" );
        int len = extremePoints.size();

        // multiply IDs
        for ( Vector point : extremePoints ) {
            if ( point == null ) {
                System.err.println( "null point ");
                System.exit( 3 );
            }
            res = res.multiply( new BigInteger( String.valueOf( point.ID + 1 ) ) );
        }

        // multiply #'s of extreme points
        res = res.multiply( new BigInteger( String.valueOf( len ) ) );
        if ( res.compareTo( new BigInteger( "0" ) ) < 0 ) {
            System.err.println( "Overflow ");
            System.exit( 2 );
        }

        // % ( n + 1 )
        return res.mod( new BigInteger( String.valueOf( points.size() + 1 ) ) );
    }

    public static
    void main( String[] args ) {
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 1 ) ); // 1 - 7
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 2 ) ); // 2 - -1
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 3 ) ); // 3 - -1
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 4 ) ); // 4 - -1
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 5 ) ); // 5 - 2
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 6 ) ); // 6 - 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 7 ) ); // 7 - 4
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 8 ) ); // 8 - 6
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 9 ) ); // 9 - 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 10 ) ); // 10 - 6
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 11 ) ); // 11 - 0
//        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 12 ) ); // 12 - 0
        new Main( ReadFromStdOrFile.getFilePathCG( 1, 1, 13 ) ); // 13 - 10
    }
}
