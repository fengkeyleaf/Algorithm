package myLibraries.util.geometry.DCEL;

/*
 * MonotoneVertex.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 basic operations on 7/21/2021$
 *     $1.1 added drawing methods on 11/28/2021$
 */

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.Triangles;
import myLibraries.lang.MyMath;
import myLibraries.util.geometry.tools.Vectors;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data structure of Monotone Vertex
 * for partitioning monotone subpolygon
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class MonotoneVertex extends Vertex {
    public static final boolean LEFT_CHAIN_VERTEX = true;
    public static final boolean RIGHT_CHAIN_VERTEX = false;
    public boolean isLeftChainVertex;

    public VertexType vertexType;

    /**
     * enumerative Vertex Type for partitioning monotone subpolygons
     * */

    public enum VertexType {
        START, SPLIT, // 0, 1
        END, MERGE, // 2, 3
        REGULAR_LEFT, REGULAR_RIGHT, // 4
    }

    /**
     * constructs to create an instance of MonotoneVertex
     * */

    public MonotoneVertex( double x, double y ) {
        super( x, y );
    }

    // -------------------------------------------
    // drawing part ------------------------------
    // -------------------------------------------

    private static
    void drawTri( Graphics graphics,
                  int x, int y, boolean isUpright ) {
        final int N_POINTS = 3;
        int[] xPoints = new int[ N_POINTS ];
        int[] yPoints = new int[ N_POINTS ];

        final int UP_OFFSET = 4;
        final int DOWN_OFFSET = 4;

        xPoints[ 0 ] = x;
        xPoints[ 1 ] = x - DOWN_OFFSET;
        xPoints[ 2 ] = x + DOWN_OFFSET;
        if ( isUpright ) {
            yPoints[ 0 ] = y + UP_OFFSET;
            yPoints[ 1 ] = y - DOWN_OFFSET;
            yPoints[ 2 ] = y - DOWN_OFFSET;
        }
        else {
            yPoints[ 0 ] = y - UP_OFFSET;
            yPoints[ 1 ] = y + DOWN_OFFSET;
            yPoints[ 2 ] = y + DOWN_OFFSET;
        }

        graphics.setColor( Color.GRAY );
        graphics.fillPolygon( xPoints, yPoints, N_POINTS );;
    }

    public static
    void drawVertexType( Graphics graphics, java.util.List<Integer> vertexTypePoints ) {
        assert vertexTypePoints.size() % 3 == 0;
        graphics.setColor( Color.GRAY );
        int CYCLE_OFFSET = DrawingProgram.CYCLE_OFFSET;
        int RADIUS = DrawingProgram.RADIUS;

        for ( int i = 0; i < vertexTypePoints.size(); i += 3 ) {
            int x = vertexTypePoints.get( i + 1 );
            int y = vertexTypePoints.get( i + 2 );

            switch ( vertexTypePoints.get( i ) ) {
                case 0 -> graphics.drawRect(  x - CYCLE_OFFSET, y - CYCLE_OFFSET, RADIUS, RADIUS );
                case 1 -> drawTri( graphics, x, y, true );
                case 2 -> graphics.fillRect( x - CYCLE_OFFSET, y - CYCLE_OFFSET, RADIUS, RADIUS );
                case 3 -> drawTri( graphics, x, y, false );
                case 4 -> graphics.fillOval( x - CYCLE_OFFSET, y - CYCLE_OFFSET, RADIUS, RADIUS );
                default -> {
                    assert false;
                }
            }
        }
    }

    /**
     * START, SPLIT, // 0, 1
     * END, MERGE, // 2, 3
     * REGULAR_LEFT, REGULAR_RIGHT, // 4
     **/

    static
    void storePoints( java.util.List<Integer> points,
                      MonotoneVertex.VertexType vertexType,
                      int x, int y ) {
        int vertexTypeInt = -1;
        switch ( vertexType ) {
            case START -> vertexTypeInt = 0;
            case SPLIT -> vertexTypeInt = 1;
            case END -> vertexTypeInt = 2;
            case MERGE -> vertexTypeInt = 3;
            default -> vertexTypeInt = 4;
        }

        points.add( vertexTypeInt );
        points.add( x );
        points.add( y );
    }

    public static
    List<Integer> drawVertexType( List<Vertex> vertices,
                                  int originWidth, int originHeight,
                                  int windowWidth, int windowHeight ) {
        List<Vector> flipedYs = new ArrayList<>( vertices );
        flipedYs = Vectors.reversedY( flipedYs );
        List<Integer> vertexPoints = new ArrayList<>();
        assert flipedYs.size() == vertices.size();

        for ( int i = 0; i < vertices.size(); i++ ) {
            Vector normalized = Vectors.normalize( flipedYs.get( i ), originWidth, originHeight, windowWidth, windowHeight );
            storePoints( vertexPoints, ( ( MonotoneVertex ) vertices.get( i ) ).vertexType, ( int ) normalized.x, ( int ) normalized.y );
        }

        return vertexPoints;
    }

    // -------------------------------------------
    // computational part ------------------------
    // -------------------------------------------

    /**
     * this vertex is a split or merge one
     * when the angle it forms is greater than pi.
     * */

    public boolean isSplitOrMergeVertex() {
        return MyMath.isNegative(
                Triangles.areaTwo( incidentEdge.prev.origin, incidentEdge.origin,
                        incidentEdge.next.origin ) );
    }

    /**
     * are both vertices on the same monotone chain?
     * */

    public boolean isOnTheDifferentChain( MonotoneVertex vertex ) {
        return isLeftChainVertex != vertex.isLeftChainVertex;
    }

    public static
    void main( String[] args ) {
        Vector vector1 = new Vector( -5, -5 );
        Vector vector2 = new Vector( 0, 3 );
        Vector vector3 = new Vector( 2, -1 );
        System.out.println( Triangles.areaTwo( vector1, vector2, vector3 ) );
    }
}
