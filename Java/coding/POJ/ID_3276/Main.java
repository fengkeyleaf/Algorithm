package coding.POJ.ID_3276;

/*
 * Main.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * submitting version
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Main {
    // matching patterns: "n", "n n", "n n n n"
    public final static String PATTERN_LENGTH = "^(\\d+)|(\\d+ \\d+)|(\\d+ \\d+ \\d+ \\d+)$";
    // matching patterns: "// comments"
    public final static String PATTERN_COMMENT = "^//.*$";
    // pattern for white-characters
    public final static String PATTERN_WHITE_CHARACTER = "\\s";
    public final static String PATTERN_MULTI_WHITE_CHARACTERS = "\\s+";

    /**
     * determine which input data to be skipped
     * */

    public static
    boolean skipInputData( String content, boolean ignoreInputLength ) {
        // true, ignore the length of input data, usually n
        boolean result = ignoreInputLength &&
                Pattern.matches( PATTERN_LENGTH, content );

        return result |
                content.isEmpty() | // skip an empty line
                Pattern.matches( PATTERN_COMMENT, content ); // skip comments
    }

    /**
     * check which input source to read from
     * */

    public boolean readFromFile( String fileName ) {
        Scanner sc = null;
        boolean ifReadFromFile = true;

        // read from file
        try {
            sc = new Scanner( new File( fileName ) );
            processingFile( sc );
        }
        // read from standard input
        catch ( FileNotFoundException e ) {
            sc = new Scanner( System.in );
            processingFile( sc );
            ifReadFromFile = false;
        } finally {
            if ( sc != null ) sc.close();
        }

        return ifReadFromFile;
    }

    private static final String FORWARD = "F";
    private int[] cows;
    private int[] flips;

    public Main( String fileName ) {
        readFromFile( fileName );

        doTheAlgorithm();
    }

    public void processingFile( Scanner sc ) {
        boolean isReadLength = true;
        int index = 0;

        while ( sc.hasNext() ) {
            String content = sc.nextLine();
            if ( skipInputData(
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
        new Main( "" ); // 3 3
//        new Main( ReadFromStdOrFile.getFilePathPOJ( 3276, 2 ) ); // 1 0
    }
}
