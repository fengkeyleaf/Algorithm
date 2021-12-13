"use strict"

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

/**
 * class providing methods of processing input file
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/io/ReadFromStdOrFile.java>MonotoneVertex</a>
 *
 * @author       Xiaoyu Tongyang
 */

export default class ReadFromFile {
    // matching patterns: "n", "n n", "n n n n"
    static PATTERN_LENGTH = "^(\\d+)$|^(\\d+ \\d+)$|^(\\d+ \\d+ \\d+ \\d+)$";
    // matching patterns: "// comments"
    static PATTERN_COMMENT = "^//.*$";
    // pattern for white-characters
    static PATTERN_WHITE_CHARACTER = "\\s";
    static PATTERN_MULTI_WHITE_CHARACTERS = "\\s+";

    /**
     * determine which input data to be skipped
     * */

    static skipInputData( content, ignoreInputLength ) {
        // true, ignore the length of input data, usually n
        let result = ignoreInputLength &&
            new RegExp( ReadFromFile.PATTERN_LENGTH ).test( content );

        return result |
            content.isEmpty() | // skip an empty line
            new RegExp( ReadFromFile.PATTERN_COMMENT ).test( content ); // skip comments
    }
}