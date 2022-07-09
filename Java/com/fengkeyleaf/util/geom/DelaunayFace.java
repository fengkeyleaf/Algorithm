package com.fengkeyleaf.util.geom;

/*
 * DelaunayFace.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 4/4/2022$
 */

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

// TODO: 4/9/2022 necessary class?
class DelaunayFace extends Face {
    DelaunayVertex vertex;

    // called by DelaunayFace.class.getDeclaredConstructor()
    DelaunayFace() {}

    DelaunayFace( HalfEdge e ) {
        super( e );
    }

    @Override
    public String toString() {
        if ( outComponent == null ) return "Infinite face";

        StringBuilder text = new StringBuilder( "Tri: [ " );
        walkAroundEdge().forEach( e -> text.append( e.origin.mappingID ).append( ", " ) );
        return text.append( " ]" ).toString();
    }
}
