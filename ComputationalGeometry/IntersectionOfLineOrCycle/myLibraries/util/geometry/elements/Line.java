package myLibraries.util.geometry.elements;

/*
 * Line.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

/**
 * Data structure of Line
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class Line<P extends Vector> {
    public final P startPoint;
    public final P endPoint;

    /**
     * Constructs to create an instance of Line
     * */

    public Line( P startPoint, P endPoint ) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    /**
     * get Vector of Line
     * */

    public Vector getVector() {
        return endPoint.subtract( startPoint );
    }

    /**
     * get projecting point of the point
     * */

    public Vector project( P point ) {
        Vector base = getVector();
        float ratio = Vector.dot(
                point.subtract( startPoint ), base )
                / base.normWithoutRadical();
        return startPoint.add( base.multiply( ratio ) );
    }

    /**
     * get the linear distance from the point
     * */

    public float distance( P point ) {
        Vector vector = getVector();
        float area = Vector.cross( vector, point.subtract( startPoint ) );
        return Math.abs( area / vector.norm() );
    }

    @Override
    public String toString() {
        return startPoint + "\t" + endPoint;
    }
}
