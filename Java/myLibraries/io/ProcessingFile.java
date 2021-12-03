package myLibraries.io;

/*
 * ProcessingFile.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 *
 */

import java.util.Scanner;

/**
 * Define an functional interface to process input data
 *
 * @author       Xiaoyu Tongyang
 */

@FunctionalInterface
public interface ProcessingFile {
    void processingFile( Scanner sc );
}
