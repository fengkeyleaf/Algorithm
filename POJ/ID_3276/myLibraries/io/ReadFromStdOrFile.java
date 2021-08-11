package myLibraries.io;

/*
 * ReadFromStdOrFile.java
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 basic data process on 2/18/2021$
 *     $1.1 added skipping unnecessary input data on 3/21/2021$
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Determine which input source to read from, file or std
 *
 * @author       Xiaoyu Tongyang
 */

public final class ReadFromStdOrFile {
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
     * get input file path for
     * @see <a href=http://poj.org/>POJ</a>
     * */

    public static
    String getFilePathPOJ( int problemID, int fileName ) {
        return "src/coding/POJ/ID_" + problemID + "/" + fileName;
    }

    /**
     * get input file path for
     * @see <a href=https://www.edx.org/course/computational-geometry>computational geometry</a>
     * by TsinghuaX on edX
     * */

    public static
    String getFilePathCG( int homeworkID, int problemID, int fileName ) {
        return "src/PA_" + homeworkID + "/problem_" + problemID + "/" + fileName;
    }

    public static
    String getFilePathCG( int homeworkID, int problemID, int fileName, String prefix ) {
        return "src/PA_" + homeworkID + "/problem_" + problemID + "/" + prefix + fileName;
    }

    public static
    String getFilePathCG( int homeworkID, int problemID, String fileName, String prefix ) {
        return "src/PA_" + homeworkID + "/problem_" + problemID + "/" + prefix + fileName;
    }

    /**
     * get input file path for CSCI-665
     * */

    public static
    String getFilePath( int homeworkID, int problemID, int fileName ) {
        return "src/hw_" + homeworkID + "/Q" + problemID + "/" + fileName;
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
}
