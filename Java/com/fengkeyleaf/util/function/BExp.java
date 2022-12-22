package com.fengkeyleaf.util.function;

/*
 * BExp.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/5/2022$
 */

import java.util.function.Predicate;

/**
 * Data structure of boolean expression.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// This implementation idea is from functional programming ( Haskell ).
// data BExp = BConst Bool | Var String | And BExp BExp | Or BExp BExp | Not BExp deriving ( Show, Eq )
public class BExp<T> {
    // target object to be evaluated
    public T t;
    // evaluating rule
    public Predicate<T> p;
    // AND, OR, NOT have.
    final BExp<T> a;
    // only AND, OR both have.
    final BExp<T> b;
    final BoolType type;

    enum BoolType {
        BOOL,
        AND,
        OR,
        NOT
    }

    BExp( T t, Predicate<T> p, BExp<T> a, BExp<T> b, BoolType type ) {
        this.t = t;
        this.p = p;
        this.a = a;
        this.b = b;
        this.type = type;
    }

    /**
     * Get a boolean expression
     * containing evaluating expression and target to be evaluated.
     * */

    public static<T>
    BExp<T> getBool( T t, Predicate<T> p ) {
        return new BExp<>( t, p, null, null, BoolType.BOOL );
    }

    public static<T>
    BExp<T> getBool() {
        return new BExp<>( null, null, null, null, BoolType.BOOL );
    }

    /**
     * Get an AND expression, a And b.
     * */

    public static<T>
    BExp<T> getAnd( BExp<T> a, BExp<T> b ) {
        return new BExp<>( null, null, a, b, BoolType.AND );
    }

    /**
     * Get a OR expression, a Or b.
     * */

    public static<T>
    BExp<T> getOr( BExp<T> a, BExp<T> b ) {
        return new BExp<>( null, null, a, b, BoolType.OR );
    }

    /**
     * Get a NOT expression, Not a.
     * */

    public static<T>
    BExp<T> getNot( BExp<T> a ) {
        return new BExp<>( null, null, a, null, BoolType.NOT );
    }

    /**
     * Assemble target objects into this boolean expression.
     * But for now, only support all target objects are the same one.
     *
     * @param T [ target objects to be evaluated ]
     */

    @SafeVarargs
    public final void assemble( T... T ) {
        assemble( this, T, 0 );
    }

    // assembly process.
    private static<T>
    int assemble ( BExp<T> e, T[] T, int i ) {
        if ( e == null ) return i;

        if ( e.type == BoolType.BOOL ) {
            e.t = T[ i ];
            return i + 1 >= T.length ? i : i + 1;
        }

        return Math.max( assemble( e.a, T, i ), assemble( e.b, T, i ) );
    }

    /**
     * Evaluate this boolean expression.
     * */

    public boolean evaluate() {
        assert Checker.check( this );

        return switch ( type ) {
            case BOOL -> p.test( t );
            case AND -> a.evaluate() & b.evaluate();
            case OR -> a.evaluate() | b.evaluate();
            case NOT -> !a.evaluate();
        };
    }

    //----------------------------------------------------------
    // toString
    //----------------------------------------------------------

    @Override
    public String toString() {
        return toString( this );
    }

    private static<T>
    String toString( BExp<T> e ) {
        return switch ( e.type ) {
            case BOOL -> "( Bool Exp )";
            case AND -> "( " + e.a + " and " + e.b + " )";
            case OR -> "( " + e.a + " or " + e.b + " )";
            case NOT -> " ( not " + e.a + " )";
        };
    }

    //----------------------------------------------------------
    // Class Checker
    //----------------------------------------------------------

    /**
     * Class to check the integrity of Boolean Expression algorithm.
     *
     * Note that code in this class won't have any effects on the main algorithm.
     */

    static class Checker {

        static<T>
        boolean check( BExp<T> e ) {
            assert e.type != BoolType.BOOL || e.t != null;
            assert e.type != BoolType.BOOL || e.p != null;
            assert ( e.type == BoolType.BOOL || e.type == BoolType.NOT )
                    || ( e.a != null && e.b != null );
            assert e.type != BoolType.NOT || e.a != null;
            return true;
        }
    }
}
