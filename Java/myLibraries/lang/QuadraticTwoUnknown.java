package myLibraries.lang;

/*
 * QuadraticTwoUnknown.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/1/2022$
 */

/**
 * Quadratic equation of two unknown, ax² + bxy + cy² + dx + ey + f = 0,
 * where at least a, b or c is not zero.
 *
 * For now, only support equation for circle:  x² + y² + Dx + Ey + F = 0 ( D² + E² - 4F > 0 )
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public class QuadraticTwoUnknown {
    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final double e;
    private final double f;

    /**
     * constructs to create an instance of QuadraticTwoUnknown, general form
     * */

    public QuadraticTwoUnknown( double a, double b, double c, double d, double e, double f ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    /**
     * constructs to create an instance of QuadraticTwoUnknown, circle.
     *  x² + y² + Dx + Ey + F = 0 ( D² + E² - 4F > 0 )
     * */

    public QuadraticTwoUnknown( double d, double e, double f ) {
        this.a = 1;
        this.b = 0;
        this.c = 1;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    @Override
    public String toString() {
        return "x² +" + " y² + " + d + " x + " + e + " y + " + f + " = 0";
    }
}
