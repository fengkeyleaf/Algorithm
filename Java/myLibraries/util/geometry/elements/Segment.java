package myLibraries.util.geometry.elements;

/*
 * Segment.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/6/2022$
 */

/**
 *
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public class Segment extends Ray {

    public Segment( Vector startPoint, Vector endPoint ) {
        super( startPoint, endPoint );
    }

    public Segment( Vector[] vectors ) {
        super( vectors );
    }
}
