package myLibraries.io;

/*
 * MyWriter.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 */

import java.io.FileWriter;
import java.io.IOException;

/**
 * This class consists exclusively of static methods
 * that related to writing content into file
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class MyWriter {
    // default fileName ID
    private static int fileID = 0;

    /**
     * write content into the output file
     *
     * @param   filepath    file path
     * @param   content     content to be written
     */

    public static
    void fileWriterMethod( String filepath, String content ) {
        try ( FileWriter fileWriter = new FileWriter( filepath ) ) {
            fileWriter.append( content );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * get output file path, if filePath is not null, use it,
     * or otherwise generate a default one
     * */

    public static
    String preprocessFilePath( String filePath, String prefix, boolean isIDEA ) {
        if ( filePath == null )
            return ( isIDEA ? "src/" : "" ) + prefix + fileID++;

        return filePath;
    }
}
