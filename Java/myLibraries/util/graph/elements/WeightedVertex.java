package myLibraries.util.graph.elements;

/*
 * WeightedVertex.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $0.0$
 */

import java.math.BigInteger;

/**
 * data structure of a WeightedVertex
 *
 * @author       Xiaoyu Tongyang or call me sora for short
 */

public class WeightedVertex extends Vertex {
    protected final BigInteger weight;

    public WeightedVertex(int ID, String weight) {
        super( ID );
        this.weight = new BigInteger( weight );
    }

    public BigInteger getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return ID + ": [ " + weight + " ]";
    }
}
