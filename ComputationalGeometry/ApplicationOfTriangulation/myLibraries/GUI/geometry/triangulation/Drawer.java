package myLibraries.GUI.geometry.triangulation;

/*
 * Drawer.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.DCEL.MonotoneVertex;
import myLibraries.util.geometry.DCEL.Vertex;
import myLibraries.util.geometry.elements.point.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class consists exclusively of static methods that
 * related to drawing for triangulation.
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Drawer {
    static final int RADIUS = 6;
    static final int CYCLE_OFFSET = RADIUS / 2;

    private static
    void drawTri( Graphics graphics, int x, int y, boolean isUpright ) {
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

    static
    void drawVertexType( Graphics graphics, List<Integer> vertexTypePoints ) {
        assert vertexTypePoints.size() % 3 == 0;
        graphics.setColor( Color.GRAY );

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
    void storePoints( List<Integer> points, MonotoneVertex.VertexType vertexType, int x, int y ) {
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

    static
    List<Integer> drawVertexType( List<Vertex> vertices, int WHITE_AREA, int[] mins, int ratio ) {
        List<Integer> vertexPoints = new ArrayList<>();
        for ( Vertex vertex : vertices ) {
            int x = getShiftedCoordinate( vertex.x, WHITE_AREA, mins[ 0 ], ratio );
            int y = getShiftedCoordinate( vertex.y, WHITE_AREA, mins[ 1 ], ratio );
            storePoints( vertexPoints, ( ( MonotoneVertex ) vertex ).vertexType, x, y );
        }

        return vertexPoints;
    }

    static
    int getShiftedCoordinate( double coordinate, int WHITE_AREA, int offset, int ratio ) {
        return ( ( int ) coordinate + offset ) * ratio + WHITE_AREA;
    }

    static
    int getShiftedCoordinate( int coordinate, int WHITE_AREA, int offset, int ratio ) {
        return ( coordinate + offset ) * ratio + WHITE_AREA;
    }

    static
    Vector getShiftedPoint( Vector vector, int WHITE_AREA, int[] mins, int ratio  ) {
        return new Vector( getShiftedCoordinate( vector.x, WHITE_AREA, mins[ 0 ], ratio ),
                getShiftedCoordinate( vector.y, WHITE_AREA, mins[ 1 ], ratio ) );
    }

    static
    List<Vector> getShiftedPoint( List<Vector> vertices, int WHITE_AREA, int[] mins, int ratio  ) {
        List<Vector> vectors = new ArrayList<>( vertices.size() + 1 );

        for ( int i = 0; i < vertices.size(); i++ )
            vectors.add( getShiftedPoint( vertices.get( i ), WHITE_AREA, mins, ratio )  );

        return vectors;
    }

    private static
    void storePoints( List<Integer> points, int ID, int x, int y ) {
        points.add( ID );
        points.add( x );
        points.add( y );
    }

    static
    void drawVertices( Graphics graphics, List<Integer> vertexPoints ) {
        assert vertexPoints.size() % 3 == 0;
        graphics.setColor( Color.BLACK );
        graphics.setFont( new Font( "Times", Font.PLAIN, 14 ) );
        for ( int i = 0; i < vertexPoints.size(); i += 3 ) {
            graphics.drawString( "v" + vertexPoints.get( i ), vertexPoints.get( i + 1 ), vertexPoints.get( i + 2 ) );
        }
    }

    static List<Integer> drawVertices( int WHITE_AREA, List<Vertex> vertices, int[] mins, int ratio ) {
        List<Integer> vertexPoints = new ArrayList<>();
        for ( Vertex vertex : vertices ) {
            int x = ( ( int ) vertex.x + mins[ 0 ] ) * ratio + WHITE_AREA + 2;
            int y = ( ( int ) vertex.y + mins[ 1 ] ) * ratio + WHITE_AREA + 2;
            storePoints( vertexPoints, vertex.ID, x, y );
        }

        return vertexPoints;
    }
}
