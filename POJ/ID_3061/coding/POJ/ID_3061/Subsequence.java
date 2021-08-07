package coding.POJ.ID_3061;

/*
 * Subsequence.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.io.ProcessingFile;
import myLibraries.io.ReadFromStdOrFile;
import myLibraries.util.MyCollections;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Subsequence
 * @see <a href=http://poj.org/problem?id=3061>Subsequence</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Subsequence implements ProcessingFile {
    private List<Integer> numbers;
    private int target;

    public Subsequence( int[] nums, int target ) {
        this.target = target;

        numbers = new ArrayList<>( nums.length );
        for ( int num : nums ) {
            numbers.add( num );
        }

//        System.out.println( doTheAlgorithm() );
        System.out.println( doTheAlgorithmLinear() );
    }

    public Subsequence( String fileName ) {
        ReadFromStdOrFile.readFromFile( fileName, this );
    }

    @Override
    public void processingFile( Scanner sc ) {
        int numberOfTestCases = Integer.parseInt( sc.nextLine().strip() );

        for ( int i = 0; i < numberOfTestCases; i++ ) {
            int counter = 0;

            while ( counter < 2 ) {
                String content = sc.nextLine().strip();
                if ( ReadFromStdOrFile.skipInputData(
                        content, false ) )
                    continue;

                String[] nums = content.split( ReadFromStdOrFile.PATTERN_WHITE_CHARACTER );

                if ( counter == 0 ) {
                    numbers = new ArrayList<>( Integer.parseInt( nums[ 0 ] ) );
                    target = Integer.parseInt( nums[ 1 ] );
                }
                else if ( counter == 1 ) {
                    for ( String num : nums )
                        numbers.add( Integer.parseInt( num ) );
                }

                counter++;
            }

//            System.out.println( doTheAlgorithm() );
            System.out.println( doTheAlgorithmLinear() );
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
            int endIndex = MyCollections.ceilingBound( numbers, i, i + minLength,
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

    private static
    void testOne() {
//        int[] nums = { 1, 2, 3, 4, 5 };
        int target = 11;
//        new Subsequence( nums, target ); // 3
//
//        int[] nums2 = { 5, 1, 3, 5, 10, 7, 4, 9, 2, 8 };
//        target = 15;
//        new Subsequence( nums2, target ); // 2

//        int[] nums3 = { 1, 2, 3, 4, 5 };
//        target = 0;
//        new Subsequence( nums3, target ); // 1
//
//        int[] nums4 = { 1, 2, 3, 4, 5 };
//        target = 1;
//        new Subsequence( nums4, target ); // 1

        int[] nums5 = { 1, 2 };
        target = 0;
        new Subsequence( nums5, target ); // 1

        int[] nums6 = {};
        new Subsequence( nums6, target ); // 1
    }

    public static
    void main( String[] args ) {
        testOne();
//        new Subsequence( ReadFromStdOrFile.getFilePathPOJ( 3061, 1 ) );
    }
}
