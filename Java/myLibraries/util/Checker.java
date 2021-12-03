package myLibraries.util;

/*
 * Checker.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $0.0$
 */

/**
 * check whatever you want with Checker class
 *
 * @author       Xiaoyu Tongyang or call me sora for short
 */

public final class Checker {
    public static<E>
    boolean checkNull( E element ) {
        return element == null;
    }

    public static<E>
    void avoidNull( E element, String name ) {
        if ( checkNull( element ) ) {
            System.err.println( name + " is null" );
            System.exit(1);
        }
    }

    public static<E>
    void checkPrerequisiteForEquals( Object obj, String className ) {
        if ( !obj.getClass().getName().equals( className ) ) {
            System.err.println("The obj is not a" + className.substring( className.lastIndexOf('.') + 1 ) );
            System.exit(1);
        }
    }
}
