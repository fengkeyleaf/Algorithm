package coding.POJ.ID_3276;

/*
 * FaceTheRightWay.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.io.ProcessingFile;
import myLibraries.io.ReadFromStdOrFile;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Face The Right Way
 * @see <a href=http://poj.org/problem?id=3276>Face The Right Way</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class FaceTheRightWay implements ProcessingFile {
    private static final String FORWARD = "F";
    private int[] cows;
    private int[] flips;

    public FaceTheRightWay( String fileName ) {
        ReadFromStdOrFile.readFromFile( fileName, this );

        doTheAlgorithm();
    }

    @Override
    public void processingFile( Scanner sc ) {
        boolean isReadLength = true;
        int index = 0;

        while ( sc.hasNext() ) {
            String content = sc.nextLine();
            // skip comments
            if ( ReadFromStdOrFile.skipInputData(
                    content, false ) )
                continue;

            if ( isReadLength ) {
                isReadLength = false;
                cows = new int[ Integer.parseInt( content ) ];
                flips = new int[ cows.length ];
                continue;
            }

            cows[ index++ ] = content.equals( FORWARD ) ? 0 : 1;
        }
    }

    private boolean isFlip( int i, int sum ) {
        return ( cows[ i ] + sum ) % 2 != 0;
    }

    private int remove( int i, int K ) {
        if ( i - K + 1 >= 0 )
            return flips[ i - K + 1 ];

        return 0;
    }

    private int doTheAlgorithm( int K ) {
        int res = 0;
        int sum = 0;
        // ∑f[ j ] = ∑f[j] + f[i] + f[i-K+1]
        for ( int i = 0; i + K <= cows.length; i++ ) {
            // ∑f[ j ] % 2 != 0,
            // i-th cow's direction is different from
            // the initializing one's
            if ( isFlip( i, sum ) ) {
                res++;
                flips[ i ] = 1;
            }

            // f[ i ]
            sum += flips[ i ];
            // f[ i - K + 1 ]
            sum -= remove( i, K );
        }

        for ( int i = cows.length - K + 1; i < cows.length; i++ ) {
            if ( isFlip( i, sum ) ) return -1;

            sum -= remove( i, K );
        }

        return res;
    }

    private void doTheAlgorithm() {
        int K = 1, M = cows.length;
        for ( int k = 1; k <= cows.length; k++ ) {
            int res = doTheAlgorithm( k );
            if ( res > -1 && M > res ){
                M = res;
                K = k;
            }

            Arrays.fill( flips, 0 );
        }

        System.out.println( K + " " + M );
    }

    public static
    void main( String[] args ) {
        new FaceTheRightWay( ReadFromStdOrFile.getFilePathPOJ( 3276, 1 ) ); // 3 3
//        new FaceTheRightWay( ReadFromStdOrFile.getFilePathPOJ( 3276, 2 ) ); // 1 0
    }
}
