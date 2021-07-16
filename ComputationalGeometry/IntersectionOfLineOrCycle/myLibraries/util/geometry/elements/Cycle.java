package myLibraries.util.geometry.elements;

/*
 * Cycle.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

/**
 * Data structure of Cycle
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Cycle {
    public final Vector center;
    public final float radius;

    /**
     * constructs to create an instance of Cycle
     * */

    public Cycle( Vector center, float radius ) {
        this.center = center;
        this.radius = radius;
    }

    /**
     * intersects with the line?
     * */

    public boolean ifIntersectsLine( Line<Vector> line ) {
        return MyMath.doubleCompare( line.distance( center ), radius ) <= 0;
    }
}
