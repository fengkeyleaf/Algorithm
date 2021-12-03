package myLibraries.util.graph.elements;

import java.util.ArrayList;
import java.util.List;

public class ReversedVertex extends Vertex {
    public final List<Vertex> reversedNeighbours = new ArrayList<>();

    public ReversedVertex( int ID ) {
        super( ID );
    }

    public ReversedVertex( int ID, Vertex predecessor ) {
        super( ID, predecessor );
    }
}
