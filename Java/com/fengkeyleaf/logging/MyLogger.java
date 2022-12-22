package com.fengkeyleaf.logging;

/*
 * MyLog.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/19/2022$
 */

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://www.geeksforgeeks.org/how-to-print-colored-text-in-java-console/
public class MyLogger {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE_BG = "\u001B[44m";
    private static final String DEBUG_BG = "\u001B[44m";
    private static final String ANSI_YELLOW_BG = "\u001B[43m";
    private static final String WARNING_BG = "\u001B[43m";
    private static final String ANSI_RED_BG = "\u001B[41m";
    private static final String ERROR_BG = "\u001B[41m";

    private static final String ANSI_WHITE_BG = "\u001B[47m";
    private Level l = Level.NORMAL;

    enum Level {
        ERROR,
        WARNING,
        DEBUG,
        NORMAL
    }

    public void setNormal() {
        l = Level.NORMAL;
    }

    public void setError() {
        l = Level.ERROR;
    }

    public void setWarning() {
        l = Level.WARNING;
    }

    public void setDebug() {
        l = Level.DEBUG;
    }

    public boolean debugLog( String s ) {
        if ( l == Level.DEBUG ) {
            System.out.println( DEBUG_BG + "[DEBUG]" + ANSI_RESET + " " + s );
            return true;
        }

        return true;
    }

    public boolean warningLog( String s ) {
        if ( l == Level.NORMAL || l == Level.ERROR ) return true;

        System.out.println( WARNING_BG + "[WARNING]" + ANSI_RESET + " " + s );
        return true;
    }

    public boolean errorLog( String s ) {
        if ( l == Level.ERROR ) {
            System.out.println( ERROR_BG + "[ERROR]" + ANSI_RESET + " " + s );
            return true;
        }

        return true;
    }
}
