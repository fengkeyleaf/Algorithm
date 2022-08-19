package com.fengkeyleaf.util.geom;

/*
 * Parabola.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 12/29/2021$
 */

import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.lang.QuadraticOneUnknown;

/**
 * Data structure of Parabola.
 * Only support parabolas with horizontal directrixes,
 * with the format: 2py = x²
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// reference resource:
// https://www.zhihu.com/question/442270490/answer/1709157319
// https://www.geogebra.org/
public class Parabola {
    final Vector focus;
    final Line directrix;
    private final double d;
    private final double midX;
    private final double midY;

    // ax² + bx + c = 0（ a ≠ 0 ）for this parabola
    public final QuadraticOneUnknown equation;

    /**
     * Constructs to create an instance of Parabola
     * */

    public Parabola( Vector focus, Line directrix ) {
        if ( MyMath.isEqualZero( directrix.distance( focus ) ) )
            throw new IllegalArgumentException( "Invalid focus and directrix for the parabola. Focus: " + focus + ", line: " + directrix );

        this.focus = focus;
        this.directrix = directrix;

        // https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Double.html#isNaN()
        assert !Double.isNaN( directrix.interceptX );
        d = Math.abs( focus.y - directrix.interceptX );
        midX = focus.x;
        midY = ( focus.y + directrix.interceptX ) / 2;

        equation = new QuadraticOneUnknown( 1 / ( 2 * d ), -midX / d, midX * midX / ( 2 * d ) + midY );
    }

    public Parabola( Vector focus, double y ) {
        this( focus, new Line( -1, y, 1, y ) );
    }

    public double updateY( double x ) {
        return equation.getY( x );
    }

    public double updateX( double y ) {
        double res = Math.sqrt( 2 * d * ( y + midY ) );
        assert MyMath.doubleCompare( res, 0 ) >= 0;
        return res - midX;
    }

    public void updateYAndX( Vector target, double x ) {
        assert MyMath.doubleCompare( x, target.x ) == 0;
        target.setXAndY( x, updateY( x ) );
    }

    public Vector[] intersect( Parabola p ) {
        return equation.intersect( p.equation );
    }

    @Override
    public String toString() {
        return equation.toString();
    }

}
