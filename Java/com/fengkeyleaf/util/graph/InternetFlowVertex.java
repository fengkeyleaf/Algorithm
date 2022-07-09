package com.fengkeyleaf.util.graph;

/*
 * InternetFlowVertex.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of InternetFlowVertex
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class InternetFlowVertex extends Vertex {
    public final Type type;
    // forward capacity indexing with vertex's ID
    public int[] forwardsDistances;
    // backward neighbours
    public final List<InternetFlowVertex> backwardsNeighbours = new ArrayList<>();
    // backward capacity indexing with  by vertex's ID
    public int[] backwardsDistances;

    // data field for Q2 of hw_6
    public int originalID;
    // true -> child; false -> cloth;
    public boolean childOrCloth;

    /**
     * constructs to create an instance of InternetFlowVertex
     * */

    public InternetFlowVertex( int ID, Type type ) {
        this( ID, null, type );
    }

    public InternetFlowVertex( int ID, Vertex predecessor, Type type ) {
        super( ID, predecessor );
        this.type = type;
    }

    /**
     * add forward capacity
     * */

    public void addForwardDistance( int ID, int weight ) {
        forwardsDistances[ ID ] = weight;
    }

    /**
     * add a backward neighbour
     * */

    public void addBackwardNeighbour( InternetFlowVertex neighbour ) {
        backwardsNeighbours.add( neighbour );
    }

    /**
     * add backward capacity
     * */

    public void addBackwardDistance( int ID, int weight ) {
        backwardsDistances[ ID ] = weight;
    }

    public enum Type {
        START, END, INTERMEDIATE
    }

    @Override
    public String toString() {
        String childOrClothString = childOrCloth ? "C" : "Ch";
        if ( type == Type.START ) childOrClothString = "S";
        if ( type == Type.END ) childOrClothString = "E";

        StringBuilder text = new StringBuilder( childOrClothString + ID + "(" + originalID + ")" + " -> F: [ " );
        for ( int i = 0; i < neighbours.size(); i++ ) {
            text.append( neighbours.get( i ).ID ).append( "|" ).
                    append( forwardsDistances[ neighbours.get( i ).ID ] ).append( ", " );
        }

        text.append( "] ~ " ).append( "B: [ ");
        for ( int i = 0; i < backwardsNeighbours.size(); i++ ) {
                text.append( backwardsNeighbours.get( i ).ID ).append( "|" ).
                        append( backwardsDistances[ backwardsNeighbours.get( i ).ID ] ).append( ", " );
        }

        return text.append( "]" ).toString();
    }
}
