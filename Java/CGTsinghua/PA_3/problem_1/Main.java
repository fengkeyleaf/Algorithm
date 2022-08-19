package CGTsinghua.PA_3.problem_1;

/*
 * Main.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 3/30/2022$
 */

import CGTsinghua.MainCG;
import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.io.ProcessingFile;
import com.fengkeyleaf.io.ReadFromStdOrFile;
import com.fengkeyleaf.util.geom.Vector;
import com.fengkeyleaf.util.geom.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * <a href="https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1649">CG2017 PA3-1 Delaunay Triangulation</a><br>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class Main extends MainCG
        implements ProcessingFile {
    private List<Vertex> vertices;

    Main( String fileName ) {
        boolean isReadFromFile = ReadFromStdOrFile.readFromFile( fileName, this );

        if ( isReadFromFile )
            System.out.printf( "%s - %d\n",
                    fileName.substring( fileName.lastIndexOf( '/' ) + 1 ),
                    doTheAlgorithm() );
        else
            System.out.printf( "%d\n", doTheAlgorithm() );
    }

    Main( int fileName ) {
        this( getFilePathCG( 3, 1, fileName ) );
    }

    Main( int fileName, String prefix ) {
        this( getFilePathCG( 2, 2, fileName, prefix ) );
    }

    /**
     * process input data
     * */

    @Override
    public void processingFile( Scanner sc ) {
        boolean initializeLength = true;

        while ( sc.hasNext() ) {
            String content = sc.nextLine();
            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            if ( initializeLength &&
                    Pattern.matches( ReadFromStdOrFile.PATTERN_LENGTH, content ) ) {
                vertices = new ArrayList<>( Integer.parseInt( content ) );
                initializeLength = false;
                continue;
            }

            String[] numbers = content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS );
            // skip input format, n, for input data from Voronoi.
            if ( numbers.length < 2 ) continue;

            int x = Integer.parseInt( numbers[ 0 ] );
            int y = Integer.parseInt( numbers[ 1 ] );
            vertices.add( new Vertex( x, y ) );
        }

//        System.out.println( vertices );
    }

    int doTheAlgorithm() {
        Face f = Delaunay.triangulate( vertices );

        if ( f != null ) {
            List<Vector> points = new ArrayList<>( vertices.size() + 1 );
            points.addAll( vertices );
            BoundingBox b = BoundingBox.getBox( points, 10 );
            // visualize when # of points less than 3.

            DrawingProgram drawer = new DrawingProgram( "CG2017 PA3-1 Delaunay Triangulation", b.width, b.height );
            drawer.drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, points );
            drawer.drawPolyAll( DrawingProgram.NORMAL_POLYGON_COLOR, f );

            drawer.initialize();
        }

        return f == null ? -1 : computeResult();
    }

    // TODO: 7/10/2022 result may be different due to running multiple threads to compute Delaunay Triangulation.
    //  That is, ID is not always starting at 0.
    int computeResult() {
        int sum = 0;
        int count = 0;
        Queue<Vertex> q = new LinkedList<>();
        q.add( vertices.get( 0 ) );

        while ( !q.isEmpty() ) {
            Vertex v = q.poll();

            for ( HalfEdge e : v.allIncidentEdges() ) {
                if ( e.mappingID < 0 ) {
                    q.add( e.twin.origin );

                    e.mappingID = e.twin.mappingID = 0;
                    sum += v.ID + e.twin.origin.ID + 2;
                    count++;
                }
            }
        }

//        System.out.println( sum + " | " + ( count + 1 ) );
        return sum % ( count + 1 );
    }

    static
    void normalTest() {
//        System.out.println( ( Math.pow( 10, 5 ) ) * ( Math.pow( 10, 5 ) ) );

//        new Main( 1 ); // -1
//        new Main( 2 ); // -1
//        new Main( 3 ); // -1
//        new Main( 4 ); // 0
//        new Main( 5 ); // 0
//        new Main( 6 ); // 3
//        new Main( 7 ); // 4
//        new Main( 8 ); // 7
//        new Main( 9 ); // 2
//        new Main( 10 ); // 4
//        new Main( 11 ); // 0
//        new Main( 12 ); // duplicate sites
//        new Main( 13 ); // ZeldaBreath
//        new Main( 14 ); // 5
        new Main( 15 ); // 132
//        new Main( 16 ); // -1
    }

    static
    void VorTest() {
        // input data from Voronoi Diagrams.
        String prefix1 = "/test_1/";
//        new Main( 1, prefix1 ); // -1
//        new Main( 2, prefix1 ); // -1
//        new Main( 3, prefix1 ); // -1
//        new Main( 4, prefix1 ); // 2
//        new Main( 5, prefix1 ); // -1
//        new Main( 6, prefix1 ); // 0
//        new Main( 7, prefix1 ); // 2
//        new Main( 8, prefix1 ); // 0
//        new Main( 9, prefix1 ); // 1
//        new Main( 10, prefix1 ); // 5
//        new Main( 11, prefix1 ); // 4
//        new Main( 12, prefix1 ); // 6
//        new Main( 13, prefix1 ); // 6
//        new Main( 14, prefix1 ); // 6
//        new Main( 15, prefix1 ); // 15
//        new Main( 16, prefix1 ); //
//        new Main( 17, prefix1 ); // ZeldaBreath
//        new Main( 18, prefix1 ); //
//        new Main( 19, prefix1 ); //
//        new Main( 20, prefix1 ); //
//        new Main( 21, prefix1 ); //
//        new Main( 22, prefix1 ); //
//        new Main( 23, prefix1 ); //
        new Main( 24, prefix1 ); // duplicate sites
        new Main( 25, prefix1 ); //
        new Main( 26, prefix1 ); //
        new Main( 27, prefix1 ); // ZeldaBreath, only towers
        new Main( 28, prefix1 ); //
    }

    public static
    void main( String[] args ) {
        normalTest();
//        VorTest();
    }
}
