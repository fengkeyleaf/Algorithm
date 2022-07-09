package com.fengkeyleaf.io;

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
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

@FunctionalInterface
public interface ProcessingFile {
    void processingFile( Scanner sc );
}
