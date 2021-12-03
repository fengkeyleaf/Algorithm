package coding.POJ.ID_3061;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * submitting version
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Main {

    /**
     * find the first element that is equal or greater than key
     * in this list of the range [index, end)
     *
     *
     * @param start start index, inclusive
     * @param end end index, exclusive
     */

    public static<T>
    int ceilingBound( List<? extends Comparable<? super T>> list,
                      int start, int end, T key ) {
        return ceilingBoundFind( list, start, end - 1, key );
    }

    public static<T>
    int ceilingBound( List<? extends Comparable<? super T>> list, T key ) {
        return ceilingBoundFind( list, 0, list.size() - 1, key );
    }

    private static<T>
    int ceilingBoundFind( List<? extends Comparable<? super T>> list,
                          int left, int right, T key ) {
        while ( left <= right ) {
            int mid = ( right - left ) / 2 + left;

            int res = list.get( mid ).compareTo( key );
            // found duplicate
            if ( res == 0 )
                return mid;
                // look for higher
            else if ( res < 0 )
                left = mid + 1;
                // look for lower
            else
                right = mid - 1;
        }

        // first element greater than key is at left
        return left;
    }

    // matching patterns: "n", "n n", "n n n n"
    public final static String PATTERN_LENGTH = "^(\\d+)|(\\d+ \\d+)|(\\d+ \\d+ \\d+ \\d+)$";
    // matching patterns: "// comments"
    public final static String PATTERN_COMMENT = "^//.*$";
    // pattern for white-characters
    public final static String PATTERN_WHITE_CHARACTER = "\\s";
    public final static String PATTERN_MULTI_WHITE_CHARACTERS = "\\s+";

    /**
     * get input file path for
     * @see <a href=http://poj.org/>POJ</a>
     * */

    public static
    String getFilePathPOJ( int problemID, int fileName ) {
        return "src/coding/POJ/ID_" + problemID + "/" + fileName;
    }

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

    private List<Integer> numbers;
    private int target;

    public Main( String fileName ) {
        readFromFile( fileName );
    }

    public void processingFile( Scanner sc ) {
        int numberOfTestCases = Integer.parseInt( sc.nextLine() );

        for ( int i = 0; i < numberOfTestCases; i++ ) {
            int counter = 0;

            while ( counter < 2 ) {
                String content = sc.nextLine();
                if ( skipInputData(
                        content, false ) )
                    continue;

                String[] nums = content.split( PATTERN_WHITE_CHARACTER );

                if ( counter == 0 ) {
                    numbers = new ArrayList<Integer>( Integer.parseInt( nums[ 0 ] ) );
                    target = Integer.parseInt( nums[ 1 ] );
                }
                else if ( counter == 1 ) {
                    for ( String num : nums )
                        numbers.add( Integer.parseInt( num ) );
                }

                counter++;
            }

            System.out.println( doTheAlgorithm() );
//            System.out.println( doTheAlgorithmLinear() );
        }
    }

    private int doTheAlgorithm() {
        if ( numbers == null || numbers.isEmpty() ) return 0;
        // no such a sequence exist
        // given non-positive S
        if ( target <= 0 ) return 1;

        // get cumulative sum
        for ( int i = 1; i < numbers.size(); i++ )
            numbers.set( i, numbers.get( i - 1 ) + numbers.get( i ) );

        int maxSum = numbers.get( numbers.size() - 1 );
        if ( target > maxSum )
            return 0;

        int minLength = numbers.size();
        // give non-positive S, no need to check if i is out of index
        for ( int i = 0; numbers.get( i ) + target <= maxSum; i++ ) {
            // S_t - S_i >= S -> S_t >= S + S_i
            int endIndex = ceilingBound( numbers, i, i + minLength,
                    numbers.get( i ) + target );
            minLength = Math.min( minLength, endIndex - i );
        }

        return minLength;
    }

    private int doTheAlgorithmLinear() {
        if ( numbers == null || numbers.isEmpty() ) return 0;
        // no such a sequence exist
        // given non-positive S
        if ( target <= 0 ) return 1;

        int len = numbers.size();
        int minLength = len + 1;
        int start = 0;
        int end = 0;
        int sum = 0;
        while ( true ) {
            // find the sequence
            // greater than or equal to S
            while ( end < len &&
                    sum < target )
                sum += numbers.get( end++ );

            // no sequence greater than S at this point
            // stop searching, break
            if ( sum < target ) break;

            // reset searching status
            minLength = Math.min( minLength, end - start );
            sum -= numbers.get( start++ );
        }

        return minLength > len ? 0 : minLength;
    }

    public static
    void main( String[] args ) {
        new Main( getFilePathPOJ( 3061, 1 ) );
    }
}
