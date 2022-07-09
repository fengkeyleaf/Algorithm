package com.fengkeyleaf.util.geom;

/*
 * VoronoiFace.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 12/30/2021$
 */

import java.util.Objects;

/**
 * Data structure of Voronoi Face( Cell )
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

class VoronoiFace extends Face {
    final Vector site;

    /**
     * Constructs to create an instance of VoronoiFace
     */

    VoronoiFace( Vector site ) {
        this.site = site;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        VoronoiFace that = ( VoronoiFace ) o;
        return Objects.equals( site, that.site );
    }

    @Override
    public String toString() {
        return String.valueOf( ID );
    }

    //-------------------------------------------------------
    // Check integrity of VoronoiFace data structure.
    //-------------------------------------------------------

    boolean check() {
        return isInsideConvexHull( site );
    }
}
