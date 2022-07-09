package CGTsinghua.PA_5.problem_1;

/*
 * Main.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 5/6/2022$
 */

import CGTsinghua.MainCG;
import com.fengkeyleaf.io.ProcessingFile;
import com.fengkeyleaf.io.ReadFromStdOrFile;
import com.fengkeyleaf.util.geom.ConvexHull;
import com.fengkeyleaf.util.geom.Vector;

import java.util.Scanner;

/**
 * <a href="https://dsa.cs.tsinghua.edu.cn/oj/problem.shtml?id=1924">CG2017 PA5-1 Dynamic Convex Hull</a>
 *
 * Convex hull algorithm: Graham Scan.
 * BBST: Red black tree.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

class Main extends MainCG
        implements ProcessingFile {

    private final ConvexHull c = new ConvexHull();

    Main( int fileName ) {
        ReadFromStdOrFile.readFromFile( getFilePathCG( 5, 1, fileName ), this );
    }

    @Override
    public void processingFile( Scanner sc ) {
        while ( sc.hasNext() ) {
            String content = sc.nextLine();

            // skip unnecessary input data
            if ( ReadFromStdOrFile.skipInputData(
                    content, true ) )
                continue;

            String[] contents = content.split( ReadFromStdOrFile.PATTERN_MULTI_WHITE_CHARACTERS );
            Vector p = new Vector( Integer.parseInt( contents[ 1 ] ), Integer.parseInt( contents[ 2 ] ) );

            // add operation.
            if ( contents[ 0 ].equals( "1" ) ) {
                c.add( p );
                continue;
            }

            // query operation.
            System.out.println( c.contains( p ) ? "YES" : "NO" );
        }
    }

    public static
    void main( String[] args ) {
        new Main( 1 ); // provided test case.
    }
}
