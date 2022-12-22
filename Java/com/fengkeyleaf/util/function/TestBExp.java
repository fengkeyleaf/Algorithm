package com.fengkeyleaf.util.function;

/*
 * TestBExp.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/5/2022$
 */

/**
 * Test BExp
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

final class TestBExp {

    static
    void test1() {
        // !T & ( F | T ) = F
        System.out.println( BExp.getAnd(
                BExp.getNot(
                        BExp.getBool( 0, i -> true )
                ),
                BExp.getOr(
                        BExp.getBool( 0, i -> false ),
                        BExp.getBool( 0, i -> true )
                )
        ).evaluate() );
    }

    public static
    void main( String[] args ) {
        test1();
    }
}
