package com.fengkeyleaf.lang;

/*
 * LinearTwoUnknowns.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/14/2022$
 */

import com.fengkeyleaf.util.geom.Vector;

/**
 * linear equation with two unknowns,
 * ax + by + c = 0 ( a | b ≠ 0 ) or ax + by = c ( a | b ≠ 0 )
 * We use the second form here to be consistent with line.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class LinearTwoUnknowns {
    public final double a;
    public final double b;
    public final double c;

    /**
     * Constructs to create an instance of LinearTwoUnknowns,
     * ax + by = c ( a | b ≠ 0 )
     * */

    public LinearTwoUnknowns( double a, double b, double c ) {
        assert !( MyMath.isEqualZero( a ) && MyMath.isEqualZero( b ) ): a + " " + b + " " + c;

        this.a = a;
        this.b = b;
        this.c = c;
    }

    public LinearTwoUnknowns( Vector startPoint, Vector endPoint ) {
        a = startPoint.y - endPoint.y;
        b = endPoint.x - startPoint.x;
        c = b * startPoint.y + a * startPoint.x;
    }

    /**
     * intersection of two linear equations with two unknowns.
     * */

    // reference resource: https://baike.baidu.com/item/%E4%BA%8C%E5%85%83%E4%B8%80%E6%AC%A1%E6%96%B9%E7%A8%8B%E7%9A%84%E8%A7%A3%E6%B3%95/6379753?fr=aladdin#4_1
    public Vector intersect( LinearTwoUnknowns eq ) {
        double condition = a * eq.b - b * eq.a;
        if ( MyMath.isEqualZero( condition ) )
            return null;

        double x = ( eq.b * c - b * eq.c ) / condition;
        double y = ( a * eq.c - eq.a * c ) / condition;
        return new Vector( x, y );
    }

    @Override
    public String toString() {
        return b + "y + " + a + "x - " + c + " = 0";
    }
}
