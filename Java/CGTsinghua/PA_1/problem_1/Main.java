package CGTsinghua.PA_1.problem_1;

/*
 * Main.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 */

import CGTsinghua.MainCG;
import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.io.ProcessingFile;
import com.fengkeyleaf.io.ReadFromStdOrFile;
import com.fengkeyleaf.util.geom.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * <a href="https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1645">CG2017 PA1-1 Convex Hull</a>
 *
 * Analysis of time complexity:
 * 1) Find LTL; -> O(n)
 * 2) offset x and y by LTL's; -> O(n)
 * 3) sort points by polar angle; -> O(nlogn)
 * 3) Graham Scan; -> O(n)
 *
 * so O(nlogn) overall
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

// TODO: 5/16/2021 failed two test cases, exit 1
final class Main extends MainCG 
        implements ProcessingFile {
    private static final String title = "Visualized debugger for Convex Hull";
    private List<Vector> points;

    Main( String fileName ) {
        boolean ifReadFromFile = ReadFromStdOrFile.readFromFile(
                fileName, this );

        if ( ifReadFromFile )
            System.out.printf( "%s - %s\n",
                    fileName.substring( fileName.lastIndexOf( '/' ) + 1 ),
                    doTheAlgorithm() );
        else
            System.out.printf( "%s\n", doTheAlgorithm() );
    }

    Main( int fileName ) {
        this( getFilePathCG( 1, 1, fileName ) );
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

    BigInteger doTheAlgorithm() {
        List<Vector> extremePoints = new ConvexHull().grahamScan( points );
//      extremePoints = ConvexHull.slowConvexHull( points );
        if ( extremePoints == null ) return new BigInteger( "-1" );

        List<Vertex> vertices = new ArrayList<>();
        for ( int i = extremePoints.size() - 1; i >= 0; i-- )
            vertices.add( new Vertex( extremePoints.get( i ) ) );

        // get DCEL
        Face[] faces = Polygons.getDCEL( vertices );
        if ( faces == null ) return new BigInteger( "-1" );

        // visualized program
        BoundingBox b = BoundingBox.getBoundingBox( points, BoundingBox.OFFSET / 2 );
        DrawingProgram drawer = new DrawingProgram( title, b.width, b.height );

        drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, points );
        Arrays.asList( faces ).forEach( f -> drawer.drawPoly( DrawingProgram.NORMAL_POLYGON_COLOR, f ) );

        // draw.
        drawer.initialize();

        // process ID.
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
        new Main( 1 ); // 1 - 7, provided input data
        new Main( 2 ); // 2 - -1
        new Main( 3 ); // 3 - -1
        new Main( 4 ); // 4 - -1
        new Main( 5 ); // 5 - 2
        new Main( 6 ); // 6 - 0
        new Main( 7 ); // 7 - 4
        new Main( 8 ); // 8 - 6
        new Main( 9 ); // 9 - 0
        new Main( 10 ); // 10 - 6
        new Main( 11 ); // 11 - 0
        new Main( 12 ); // 12 - 0
        new Main( 13 ); // 13 - 10
    }
}
