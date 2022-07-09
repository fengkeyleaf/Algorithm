package com.fengkeyleaf.io;

/*
 * ReadFromStdOrFile.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.2$
 *
 * Revisions:
 *     $1.0 basic data process on 2/18/2021$
 *     $1.2 added skipping unnecessary input data on 3/21/2021$
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Determine which input source to read from, file or std
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class ReadFromStdOrFile {
    // matching patterns: "n", "n n", "n n n n"
    public final static String PATTERN_LENGTH = "^(\\d+)|(\\d+ \\d+)|(\\d+ \\d+ \\d+ \\d+)$";
    // matching patterns: "// comments"
    public final static String PATTERN_COMMENT = "^//.*$";
    // pattern for white-characters
    public final static String PATTERN_WHITE_CHARACTER = "\\s";
    public final static String PATTERN_MULTI_WHITE_CHARACTERS = "\\s+";

    public final static String POSITIVE_OUTPUT = "YES";
    public final static String NEGATIVE_OUTPUT = "NO";

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

    public static
    boolean readFromFile( String fileName, ProcessingFile processor ) {
        Scanner sc = null;
        boolean ifReadFromFile = true;

        // read from file
        try {
            sc = new Scanner( new File( fileName ) );
            processor.processingFile( sc );
        }
        // read from standard input
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            sc = new Scanner( System.in );
            processor.processingFile( sc );
            ifReadFromFile = false;
        } finally {
            if ( sc != null ) sc.close();
        }

        return ifReadFromFile;
    }

    public static
    void main( String[] args ) {
        System.out.println( Pattern.matches( PATTERN_LENGTH, "1 2" ) ); // True
        System.out.println( Pattern.matches( PATTERN_LENGTH, "1 2 a" ) ); // False
        System.out.println( Pattern.matches( PATTERN_LENGTH, "a1 3 4 a" ) ); // False
    }
}
