package CGTsinghua.PA_4.problem_1;

/*
 * Main.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 4/21/2022$
 */

import CGTsinghua.MainCG;
import com.fengkeyleaf.io.ProcessingFile;
import com.fengkeyleaf.io.ReadFromStdOrFile;
import com.fengkeyleaf.util.geom.LayeredRangeTree;
import com.fengkeyleaf.util.geom.KdTree;
import com.fengkeyleaf.util.geom.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * <a href="https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1651">CG2017 PA4-1 Planar Range Query</a>
 *
 * Three ways to solve it:
 * 1) Kd-tree -> O(√n +k）;
 * 2) Range tree -> O( logn * logn + k);
 * 3) Fractional cascading* -> O(logn + k),
 * where n is # of points and k is # of reported ones.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class Main extends MainCG
        implements ProcessingFile {

    private List<Vector> P;
    // orthogonal searching areas
    private List<List<Vector>> boxes;
    private List<String[]> Rs;
    private enum Type {
        KD_TREE,
        RANGE_TREE,
        FRACTIONAL_CASCADING
    }

    Main( int fileName ) {
        ReadFromStdOrFile.readFromFile( getFilePathCG( 4, 1, fileName ), this );
    }

    /**
     * process input data
     * */

    @Override
    public void processingFile( Scanner sc ) {
        boolean initializeLength = true;
        int index = 0;
        int numberOfPoints = 0;
        String[] numbers = null;
        String content = null;

        while ( sc.hasNext() ) {
            content = sc.nextLine();
            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            if ( initializeLength &&
                    Pattern.matches( ReadFromStdOrFile.PATTERN_LENGTH, content ) ) {
                numbers = content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS );
                numberOfPoints = Integer.parseInt( numbers[ 0 ] );
                P = new ArrayList<>( numberOfPoints );
                boxes = new ArrayList<>( Integer.parseInt( numbers[ 1 ] ) );
                Rs = new ArrayList<>( Integer.parseInt( numbers[ 1 ] ) );
                initializeLength = false;
                continue;
            }

            numbers = content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS );
            if ( containsInf( numbers ) ) {
                Rs.add( numbers );
                continue;
            }

            int x1 = Integer.parseInt( numbers[ 0 ] );
            int y1 = Integer.parseInt( numbers[ 1 ] );
            Vector v1 = new Vector( x1, y1 );

            if ( index++ < numberOfPoints ) {
                assert numbers.length == 2;
                P.add( v1 );
                continue;
            }

            assert numbers.length == 4 : Arrays.toString( numbers );
            int x2 = Integer.parseInt( numbers[ 2 ] );
            int y2 = Integer.parseInt( numbers[ 3 ] );

            List<Vector> l = new ArrayList<>( 2 );
            l.add( v1 );
            l.add( new Vector( x2, y2 ) );
            boxes.add( l );

            Rs.add( numbers );
        }

//        System.out.println( P );
    }

    static final String INF_PATTERN = "^((-?)|(\\+?))INF$";

    private boolean containsInf( String[] numbers ) {
        for ( String number : numbers ) {
            if ( Pattern.matches( INF_PATTERN, number ) )
                return true;
        }

        return false;
    }

    void doTheAlgorithm( Type t ) {
        LayeredRangeTree rangeTree = new LayeredRangeTree( P );

        switch ( t ) {
            case KD_TREE -> {
                KdTree kdTree = new KdTree( P );

                boxes.forEach( b -> {
                    List<Vector> res = kdTree.query( b );

                    if ( res != null )
                        System.out.println( res.size() );
                } );
            }
            case RANGE_TREE -> boxes.forEach( b -> {
                // query like a normal range tree.
                List<Vector> res = rangeTree.query2D( b );

                if ( res != null )
                    System.out.println( res.size() );
            } );
            case FRACTIONAL_CASCADING ->
                Rs.forEach( b -> {
                    List<Vector> res = rangeTree.query( b );

                    if ( res != null )
                        System.out.println( res.size() );
                } );
            default -> { assert false; }
        }
    }

    static
    void testKdTree() {
//        new Main( 1 ).doTheAlgorithm( Type.KD_TREE );
//        new Main( 2 ).doTheAlgorithm( Type.KD_TREE );
//        new Main( 3 ).doTheAlgorithm( Type.KD_TREE );
//        new Main( 4 ).doTheAlgorithm( Type.KD_TREE );
//        new Main( 5 ).doTheAlgorithm( Type.KD_TREE ); // degenerate case
//        new Main( 6 ).doTheAlgorithm( Type.KD_TREE );
//        new Main( 7 ).doTheAlgorithm( Type.KD_TREE ); // provided test case
        new Main( 8 ).doTheAlgorithm( Type.KD_TREE ); // ZeldaBreath,
//        new Main( 9 ).doTheAlgorithm( Type.KD_TREE );
//        new Main( 11 ).doTheAlgorithm( Type.KD_TREE );
    }

    static
    void testRangeTree() {
//        System.out.println( Pattern.matches( "((-?)|(\\+?))INF", "-INF" ) );
//        System.out.println( Pattern.matches( "((-?)|(\\+?))INF", "INF" ) );
//        System.out.println( Pattern.matches( "((-?)|(\\+?))INF", "+INF" ) );
//        System.out.println( Pattern.matches( "((-?)|(\\+?))INF", "123" ) );

//        new Main( 1 ).doTheAlgorithm( Type.RANGE_TREE );
        new Main( 2 ).doTheAlgorithm( Type.RANGE_TREE );
//        new Main( 3 ).doTheAlgorithm( Type.RANGE_TREE );
//        new Main( 4 ).doTheAlgorithm( Type.RANGE_TREE );
//        new Main( 5 ).doTheAlgorithm( Type.RANGE_TREE );
//        new Main( 6 ).doTheAlgorithm( Type.RANGE_TREE );
//        new Main( 7 ).doTheAlgorithm( Type.RANGE_TREE ); // provided test case
//        new Main( 8 ).doTheAlgorithm( Type.RANGE_TREE ); // ZeldaBreath,
//        new Main( 9 ).doTheAlgorithm( Type.RANGE_TREE );
//        new Main( 10 ).doTheAlgorithm( Type.RANGE_TREE );
//        new Main( 11 ).doTheAlgorithm( Type.RANGE_TREE );
    }

    static
    void testFractionalCascading() {
//        new Main( 1 ).doTheAlgorithm( Type.FRACTIONAL_CASCADING );
//        new Main( 2 ).doTheAlgorithm( Type.FRACTIONAL_CASCADING );
//        new Main( 3 ).doTheAlgorithm( Type.FRACTIONAL_CASCADING );
//        new Main( 4 ).doTheAlgorithm( Type.FRACTIONAL_CASCADING );
//        new Main( 5 ).doTheAlgorithm( Type.FRACTIONAL_CASCADING );
//        new Main( 6 ).doTheAlgorithm( Type.FRACTIONAL_CASCADING );
//        new Main( 7 ).doTheAlgorithm( Type.FRACTIONAL_CASCADING ); // provided test case
        new Main( 8 ).doTheAlgorithm( Type.FRACTIONAL_CASCADING ); // ZeldaBreath,
//        new Main( 9 ).doTheAlgorithm( Type.FRACTIONAL_CASCADING );
//        new Main( 10 ).doTheAlgorithm( Type.FRACTIONAL_CASCADING );
//        new Main( 11 ).doTheAlgorithm( Type.FRACTIONAL_CASCADING );
    }

    public static
    void main( String[] args ) {
        testKdTree();
//        testRangeTree();
        testFractionalCascading();
    }
}
