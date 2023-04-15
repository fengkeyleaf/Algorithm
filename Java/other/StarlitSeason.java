package other;

/*
 * StarlitSeason.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 3/17/2023
 */

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * THE IDOLM@STER STARLIT SEASON, DLC
 * <a href="https://starlit-season.idolmaster.jp/">...</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public final class StarlitSeason {
    record Idol(
            int id,
            String eng_name,
            String jap_name,
            String cht_name,
            String group,
            int vo,
            int da,
            int vi,
            int s
    ) {
        @Override
        public String toString() {
            return cht_name + ", " + s;
        }
    }

    // DLC + 5 for each
    // 《Nando demo waraou》
    // 演唱偶像：【春香】 【千早】 【美希】 【雪步】 【弥生】 【律子】 【梓】 【伊织】 【真】 【亚美】 【真美】 【贵音】 【响】 【心白】 【亚夜】
    static final int[] DCL1 = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 32, 35 };
    static final int[] DCL1_LIMITED = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 32, 35 };
    // 《Dance Dance Dance》
    // 演唱偶像：【千早】 【律子】 【梓】 【贵音】 【静香】 【紬】 【兰子】 【枫】 【咲耶】【凛世】 【心白】 【玲音】
    // 【紬】 【兰子】 【枫】【心白】 【玲音】
    static final int[] DCL2 = new int[] { 23, 15, 19, 32, 33 };
    static final int[] DCL2_LIMITED = new int[] { 19 };
    // 《KAWAII WARS》
    // 演唱偶像：【春香】 【伊织】 【真】 【响】 【未来】 【琴叶】 【菜菜】 【杏】 【甘奈】 【甜花】 【心白】 【诗花】
    // 【琴叶】【春香】【心白】 【诗花】【伊织】
    static final int[] DCL3 = new int[] { 1, 8, 25, 32, 34 };
    static final int[] DCL3_LIMITED = new int[] { 25 };
    // 《Zenryoku★Dreaming Girls》
    // 演唱偶像：【美希】 【雪步】 【弥生】 【亚美】 【真美】 【翼】 【歌织】 【美嘉】 【琪拉莉】 【果穗】 【摩美美】【心白】 【亚夜】
    static final int[] DCL4 = new int[] { 3, 4, 5, 10, 11, 13, 22, 24, 16, 18, 27, 31, 32, 35 };
    static final int[] DCL4_LIMITED = new int[] { 3, 4, 5, 10, 11, 13, 22, 24, 16, 18, 27, 31, 32, 35 };
    // 《IDOL☆HEART》
    // 演唱偶像：【春香】 【千早】 【美希】 【雪步】 【弥生】 【律子】 【梓】 【伊织】 【真】 【亚美】 【真美】 【贵音】 【响】
    static final int[] DCL5 = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
    static final int[] DCL5_LIMITED = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
    static final int[][] DLCs = new int[][] { DCL1, DCL2, DCL3, DCL4, DCL5 };
    static final int[][] DLC_LIMITEDs = new int[][] {
            DCL1_LIMITED, DCL2_LIMITED, DCL3_LIMITED, DCL4_LIMITED, DCL5_LIMITED
    };

    // https://www.geeksforgeeks.org/reading-csv-file-java-using-opencsv/
    private static
    TreeMap<Integer, Idol> getIdols() {
        TreeMap<Integer, Idol> I = new TreeMap<>();

        try {
            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader( "./src/other/StarlitIdols.csv" );

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader( filereader );
            String[] nextRecord;
            boolean f = true;

            // we are going to read data line by line
            while ( ( nextRecord = csvReader.readNext() ) != null ) {
//                System.out.println( Arrays.toString( nextRecord ) );
                if ( f ) {
                    f = false;
                    continue;
                }

                assert nextRecord.length == 8;
                int id = Integer.parseInt( nextRecord[ 0 ] );
                I.put(
                        id,
                        new Idol(
                                id, nextRecord[ 1 ], nextRecord[ 2 ],
                                nextRecord[ 3 ], nextRecord[ 4 ],
                                Integer.parseInt( nextRecord[ 5 ] ),
                                Integer.parseInt( nextRecord[ 6 ] ),
                                Integer.parseInt( nextRecord[ 7 ] ),
                                computeScore(
                                        id,
                                        Integer.parseInt( nextRecord[ 5 ] ),
                                        Integer.parseInt( nextRecord[ 6 ] ),
                                        Integer.parseInt( nextRecord[ 7 ] )
                                )
                        )
                );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( CsvValidationException e ) {
            throw new RuntimeException( e );
        }

        return I;
    }

    static final int DLC_SCORE = 5;
    static final int DLC_LIMITED_SCORE = 10;

    private static
    int computeScore( int id, int vo, int da, int vi ) {
        int s = 0;
        for ( int[] DLC : DLCs ) {
            if ( Arrays.stream( DLC ).anyMatch( i -> i == id ) )
                s += DLC_SCORE;
        }

        for ( int[] DLC : DLC_LIMITEDs ) {
            if ( Arrays.stream( DLC ).anyMatch( i -> i == id ) )
                s += DLC_LIMITED_SCORE;
        }

        return vo + da + vi;
//        return s + vo + da + vi;
    }

    static
    void doTheAlgorithm() {
        // Compute how many times an idol appears in a DCL group.
        getIdols().values().stream().sorted(
                ( i1, i2 ) -> Integer.compare( i2.s, i1.s )
        ).forEach( System.out::println );
    }

    public static
    void main( String[] args ) {
        doTheAlgorithm();
    }
}
