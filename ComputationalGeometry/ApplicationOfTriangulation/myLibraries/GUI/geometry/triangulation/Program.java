package myLibraries.GUI.geometry.triangulation;

/*
 * Program.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.DCEL.MonotoneVertex;
import myLibraries.util.geometry.DCEL.Vertex;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.graph.Graph;
import myLibraries.util.graph.elements.DualVertex;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * visualized program for polygon triangulation
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Program {
    // colors
    private static final Color[] colors = new Color[] {
        Color.BLACK, Color.BLUE, Color.CYAN,
        Color.DARK_GRAY, Color.GREEN, Color.LIGHT_GRAY,
        Color.MAGENTA, Color.ORANGE, Color.PINK,
        Color.RED, Color.YELLOW
    };
    private static final Color SHORTEST_PATH_COLOR = Color.CYAN;
    private static final Color NORMAL_POLYGON_COLOR = Color.GRAY;
    private static final Color START_POINT_COLOR = Color.GREEN;
    private static final Color END_POINT_COLOR = Color.RED;

    // program's components
    private final BufferedImage image;
    private BufferedImage imageVertexType;
    private BufferedImage imageVertexTypeDraw;
    private final Graphics graphics;
    private Graphics graphicsVertexType;

    private final Frame frame = new Frame( "Visualized debugger for polygon triangulation" );
    private final Canvas canvas = new Canvas() {
        @Override
        public void paint( Graphics g ) {
            g.drawImage( image, 0, 0, null );
        }
    };
    private final Canvas vertexTypeCanvas = new Canvas() {
        @Override
        public void paint( Graphics g ) {
            if ( isShowingVertexType )
                g.drawImage( imageVertexType, 0, 0, null );
            else
                g.drawImage( imageVertexTypeDraw, 0, 0, null );
        }
    };

    static class MyCanvas extends Canvas {
        private final BufferedImage image;

        public MyCanvas( BufferedImage image ) {
            this.image = image;
        }

        @Override
        public void paint( Graphics g ) {
            g.drawImage( image, 0, 0, null );
        }
    }

    final int LENGTH = 16;

    Box displayBox = Box.createHorizontalBox();
    Button showPolygon = new Button( "Show Polygon" );
    Button showVertices = new Button( "Show Vertices" );
    Button showVertexType = new Button( "Show vertex type" );
    Button showStartAndEndPoint = new Button( "Show start and end Point" );
    Button showStartAndEndTri = new Button( "Show start and end Tri" );

    Box partitioningBox = Box.createHorizontalBox();
    Button monoToning = new Button( "MonoToning" );
    Button triangulation = new Button( "Triangulation" );
    Button dualGraph = new Button( "Dual graph" );

    Box ShortestPathBox = Box.createHorizontalBox();
    Button shortestTri = new Button( "Shortest with Triangles" );
    Button InternalDiagonals = new Button( "Internal diagonals for Funnel" );
    Button shortestPath = new Button( "Shortest Path" );

    // dimension
    private final int CANVAS_WIDTH;
    private final int CANVAS_HEIGHT;
    private int CANVAS_VERTEX_TYPE_HEIGHT;
    private int CANVAS_VERTEX_TYPE_WIDTH;
    private final int WHITE_AREA;

    // controlling triggers
    private boolean isShowingVertices;
    private boolean isShowingStartAndEndTri;
    private boolean isShowingInternalDiagonals;
    private boolean isShowingShortestPath;
    private boolean isShowingStartAndEndPoint;
    private boolean isShowingVertexType;

    private DrawingType drawingType = DrawingType.POLYGON;
    private int[] mins;
    private int ratio;

    // data
    private List<Vertex> vertices;
    private List<Face> faces;
    private List<Integer> polygonPoints;
    private List<Integer> monotonePoints;
    private List<Integer> verticesPoints;
    private List<Integer> vertexTypePoints;
    private List<Integer> triangulationPoints;
    private List<Integer> shortestPathPoints;
    private Graph<DualVertex> graph;
    private DualVertex start;
    private DualVertex end;
    private Vector startPoint;
    private Vector endPoint;
    private final String VERTEX_TYPE_FILEPATH = "src/myLibraries/GUI/geometry/triangulation/pics/vertex_type.png";

    private enum DrawingType {
        // basic drawing-type
        POLYGON, MONOTONE, TRIANGULATION, DUAL, SHORTEST_TRI,
        // adding drawing-type
        VERTEX, VERTEX_TYPE, START_END_POINT, START_END_TRI, INTERNAL_DIAGONALS, SHORTEST
    }

    /**
     * constructs to create an visualized Program
     * */

    public Program() {
        this( 800, 600, 80 );
    }

    public Program( int CANVAS_WIDTH, int CANVAS_HEIGHT, int WHITE_AREA ) {
        this.CANVAS_WIDTH = CANVAS_WIDTH;
        this.CANVAS_HEIGHT = CANVAS_HEIGHT;
        this.WHITE_AREA = WHITE_AREA;
        image = new BufferedImage( CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_BGR );
        graphics = image.getGraphics();
    }

    private void resetCanvas() {
        graphics.setColor( Color.WHITE );
        graphics.fillRect( 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT );
    }

    private void resetVertexTypeCanvas() {
        graphicsVertexType.setColor( Color.WHITE );
        graphicsVertexType.fillRect( 0, 0, CANVAS_VERTEX_TYPE_WIDTH, CANVAS_VERTEX_TYPE_HEIGHT );
    }

    private void drawStartAndEndPoint() {
        Polygons.drawPoint( graphics, startPoint, WHITE_AREA, mins, ratio, START_POINT_COLOR );
        Polygons.drawPoint( graphics, endPoint, WHITE_AREA, mins, ratio, END_POINT_COLOR );
    }

    private void drawingWhich( DrawingType drawingType ) {
        switch ( drawingType ) {
            // basic drawing-type
            case POLYGON -> Polygons.drawPolygon( graphics, polygonPoints, NORMAL_POLYGON_COLOR);
            case MONOTONE -> Polygons.drawPolygon( graphics, monotonePoints, NORMAL_POLYGON_COLOR);
            case TRIANGULATION -> Polygons.drawPolygon( graphics, triangulationPoints, NORMAL_POLYGON_COLOR);
            case DUAL -> {
                drawingWhich( DrawingType.TRIANGULATION );
                Polygons.drawDualGraph( graph, graphics, WHITE_AREA, mins, ratio );
            }
            case SHORTEST_TRI -> {
                Polygons.drawPolygon( graphics, triangulationPoints, NORMAL_POLYGON_COLOR);
                Polygons.drawShortestPathWithTriangles( end, graphics, WHITE_AREA, mins, ratio );
            }

            // adding drawing-type
            case VERTEX -> Drawer.drawVertices( graphics, verticesPoints );
            case VERTEX_TYPE -> Drawer.drawVertexType( graphics, vertexTypePoints );
            case START_END_POINT -> {
                drawStartAndEndPoint();
            }
            case START_END_TRI -> {
                Polygons.drawPolygon( start.face, graphics, WHITE_AREA, mins, ratio, START_POINT_COLOR );
                Polygons.drawPolygon( end.face, graphics, WHITE_AREA, mins, ratio, END_POINT_COLOR );
            }
            case INTERNAL_DIAGONALS -> Polygons.drawInternalDiagonals( end, graphics, WHITE_AREA, mins, ratio );
            case SHORTEST -> {
                Polygons.drawPath( graphics, shortestPathPoints, SHORTEST_PATH_COLOR );
            }
        }
    }

    private void assemblyDisplayBox() {
        displayBox.add( Box.createHorizontalStrut( LENGTH ) );
        displayBox.add( showPolygon );
        displayBox.add( Box.createHorizontalStrut( LENGTH ) );
        displayBox.add( showVertices );
        displayBox.add( Box.createHorizontalStrut( LENGTH ) );
        displayBox.add( showVertexType );
        displayBox.add( Box.createHorizontalStrut( LENGTH ) );
        displayBox.add( showStartAndEndPoint );
        displayBox.add( Box.createHorizontalStrut( LENGTH ) );
        displayBox.add( showStartAndEndTri );
        displayBox.add( Box.createHorizontalStrut( LENGTH ) );
    }

    private void assemblyPartitioningBox() {
        partitioningBox.add( Box.createHorizontalStrut( LENGTH ) );
        partitioningBox.add( monoToning );
        partitioningBox.add( Box.createHorizontalStrut( LENGTH ) );
        partitioningBox.add( triangulation );
        partitioningBox.add( Box.createHorizontalStrut( LENGTH ) );
        partitioningBox.add( dualGraph );
        partitioningBox.add( Box.createHorizontalStrut( LENGTH ) );
    }

    private void assemblyShortestPathBox() {
        ShortestPathBox.add( Box.createHorizontalStrut( LENGTH ) );
        ShortestPathBox.add( shortestTri );
        ShortestPathBox.add( Box.createHorizontalStrut( LENGTH ) );
        ShortestPathBox.add( InternalDiagonals );
        ShortestPathBox.add( Box.createHorizontalStrut( LENGTH ) );
        ShortestPathBox.add( shortestPath );
        ShortestPathBox.add( Box.createHorizontalStrut( LENGTH ) );
    }

    private void showingVertices() {
        if ( isShowingVertices )
            drawingWhich( DrawingType.VERTEX );
    }

    private void showingVertexType() {
        if ( isShowingVertexType )
            drawingWhich( DrawingType.VERTEX_TYPE );
    }

    private void showingStartAndEndPoint() {
        if ( isShowingStartAndEndPoint )
            drawingWhich( DrawingType.START_END_POINT );
    }

    private void showingShortestPath() {
        if ( isShowingShortestPath )
            drawingWhich( DrawingType.SHORTEST );
    }

    // only show either start and end point or vertex type,
    // not both
    private void showingStartAndEndPointOrVertexType() {
        if ( isShowingStartAndEndPoint )
            drawingWhich( DrawingType.START_END_POINT );
        else if ( isShowingVertexType )
            drawingWhich( DrawingType.VERTEX_TYPE );
    }

    private void showingStartAndEndTri() {
        if ( isShowingStartAndEndTri )
            drawingWhich(  DrawingType.START_END_TRI );
    }

    private void showingInternalDiagonals() {
        if ( isShowingInternalDiagonals )
            drawingWhich( DrawingType.INTERNAL_DIAGONALS );
    }

    private void addListenerDisplayBox() {
        // show normal polygon;
        // basic drawing-type -> POLYGON.
        showPolygon.addActionListener( e -> {
            resetCanvas();
            drawingWhich( DrawingType.POLYGON );

            showingVertices();
            showingStartAndEndPointOrVertexType();
            showingShortestPath();

            drawingType = DrawingType.POLYGON;
            isShowingStartAndEndTri = false;
            isShowingInternalDiagonals = false;

            canvas.repaint();
        } );

        // show vertices;
        // adding drawing-type -> VERTEX;
        showVertices.addActionListener( e -> {
            if ( !isShowingVertices ) {
                drawingWhich( DrawingType.VERTEX );
                isShowingVertices = true;
            }
            else {
                resetCanvas();
                drawingWhich( drawingType );

                showingStartAndEndPointOrVertexType();
                showingStartAndEndTri();

                isShowingInternalDiagonals = false;
                isShowingShortestPath = false;
                isShowingVertices = false;
            }

            canvas.repaint();
        } );

        // show type of vertices;
        // adding drawing-type -> VERTEX_TYPE;
        showVertexType.addActionListener( e -> {
            resetCanvas();
            drawingWhich( drawingType );

            showingVertices();
            showingShortestPath();
            showingStartAndEndTri();

            if ( !isShowingVertexType ) {
                drawingWhich( DrawingType.VERTEX_TYPE );

                isShowingVertexType = true;
                isShowingStartAndEndPoint = false;
                isShowingInternalDiagonals = false;
            }
            else {
                showingStartAndEndPoint();
                showingInternalDiagonals();
                isShowingVertexType = false;
            }

            vertexTypeCanvas.repaint();
            canvas.repaint();
        } );

        // show start and end pont;
        // adding drawing-type -> START_END_POINT;
        showStartAndEndPoint.addActionListener( e -> {
            resetCanvas();
            drawingWhich( drawingType );

            showingVertices();
            showingShortestPath();
            showingStartAndEndTri();
            showingInternalDiagonals();

            if ( !isShowingStartAndEndPoint ) {
                drawingWhich( DrawingType.START_END_POINT );
                isShowingStartAndEndPoint = true;
                isShowingVertexType = false;
            }
            else {
                showingVertexType();
                isShowingStartAndEndPoint = false;
            }

            vertexTypeCanvas.repaint();
            canvas.repaint();
        } );

        // show start and end triangle in the dual graph;
        // adding drawing-type -> START_END_TRI;
        showStartAndEndTri.addActionListener( e -> {
            if ( !isShowingStartAndEndTri &&
                    drawingType != DrawingType.POLYGON && drawingType != DrawingType.MONOTONE ) {
                drawingWhich( DrawingType.START_END_TRI );
                isShowingStartAndEndTri = true;
            }
            else {
                resetCanvas();
                drawingWhich( drawingType );

                showingVertices();
                showingStartAndEndPointOrVertexType();

                isShowingStartAndEndTri = false;
                isShowingInternalDiagonals = false;
                isShowingShortestPath = false;
            }

            canvas.repaint();
        } );
    }

    private void addListenerPartitioningBox() {
        // show monetone polygons;
        // basic drawing-type -> MONOTONE.
        monoToning.addActionListener( e -> {
            resetCanvas();
            drawingWhich( DrawingType.MONOTONE );

            showingVertices();
            showingStartAndEndPointOrVertexType();
            showingShortestPath();

            drawingType = DrawingType.MONOTONE;
            isShowingStartAndEndTri = false;
            isShowingInternalDiagonals = false;

            canvas.repaint();
        } );

        // show monotone polygons;
        // basic drawing-type -> TRIANGULATION.
        triangulation.addActionListener( e -> {
            resetCanvas();
            drawingWhich( DrawingType.TRIANGULATION );

            showingVertices();
            showingStartAndEndPointOrVertexType();
            showingShortestPath();

            drawingType = DrawingType.TRIANGULATION;
            isShowingStartAndEndTri = false;
            isShowingInternalDiagonals = false;

            canvas.repaint();
        } );

        // show dual graph;
        // basic drawing-type -> DUAL.
        dualGraph.addActionListener( e -> {
            resetCanvas();
            drawingWhich( DrawingType.DUAL );

            showingVertices();
            showingStartAndEndPointOrVertexType();
            showingShortestPath();

            drawingType = DrawingType.DUAL;
            isShowingStartAndEndTri = false;
            isShowingInternalDiagonals = false;

            canvas.repaint();
        } );
    }

    private void addListenerShortestPath() {
        // show Shortest with Triangles;
        // basic drawing-type -> SHORTEST_TRI.
        shortestTri.addActionListener( e -> {
            resetCanvas();
            drawingWhich( DrawingType.SHORTEST_TRI );
            drawingWhich( DrawingType.START_END_TRI );

            showingVertices();
            showingStartAndEndPointOrVertexType();
            showingShortestPath();

            drawingType = DrawingType.SHORTEST_TRI;
            isShowingStartAndEndTri = true;
            isShowingInternalDiagonals = false;

            canvas.repaint();
        } );

        // show Internal diagonals for Funnel;
        // adding drawing-type -> INTERNAL_DIAGONALS;
        InternalDiagonals.addActionListener( e -> {
            if ( !isShowingInternalDiagonals &&
                    drawingType == DrawingType.SHORTEST_TRI ) {
                drawingWhich( DrawingType.INTERNAL_DIAGONALS );
                isShowingInternalDiagonals = true;
            }
            else {
                resetCanvas();
                drawingWhich( drawingType );

                showingVertices();
                showingStartAndEndPointOrVertexType();
                showingStartAndEndTri();
                showingShortestPath();

                isShowingInternalDiagonals = false;
            }

            canvas.repaint();
        } );

        // show Shortest Path;
        // adding drawing-type -> SHORTEST;
        shortestPath.addActionListener( e -> {
            resetCanvas();
            drawingWhich( drawingType );

            showingVertices();
            showingStartAndEndTri();
            showingInternalDiagonals();

            if ( !isShowingShortestPath ) {
                drawingWhich( DrawingType.SHORTEST );
                drawingWhich( DrawingType.START_END_POINT );
                isShowingVertexType = false;
                isShowingShortestPath = true;
                isShowingStartAndEndPoint = true;
            }
            else {
                showingStartAndEndPointOrVertexType();
                isShowingShortestPath = false;
            }

            vertexTypeCanvas.repaint();
            canvas.repaint();
        } );
    }

    private Box setButtons() {
        addListenerDisplayBox();
        assemblyDisplayBox();

        addListenerPartitioningBox();
        assemblyPartitioningBox();

        addListenerShortestPath();
        assemblyShortestPathBox();

        Box buttonBox = Box.createVerticalBox();
        buttonBox.add( displayBox );
        buttonBox.add( partitioningBox );
        buttonBox.add( ShortestPathBox );
        return buttonBox;
    }

    private void setVertexTypeCanvas() {
        try {
            File file = new File( VERTEX_TYPE_FILEPATH );
            if ( !file.canRead() )
                throw new FileNotFoundException();
            imageVertexType = ImageIO.read( file );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        CANVAS_VERTEX_TYPE_WIDTH = imageVertexType.getWidth();
        CANVAS_VERTEX_TYPE_HEIGHT = imageVertexType.getHeight();
        vertexTypeCanvas.setPreferredSize( new Dimension( CANVAS_VERTEX_TYPE_WIDTH, CANVAS_VERTEX_TYPE_HEIGHT ) );
        imageVertexTypeDraw = new BufferedImage( CANVAS_VERTEX_TYPE_WIDTH, CANVAS_VERTEX_TYPE_HEIGHT, BufferedImage.TYPE_INT_BGR );
        graphicsVertexType = imageVertexTypeDraw.getGraphics();
        resetVertexTypeCanvas();
        vertexTypeCanvas.setVisible( true );
    }

    public void initialize() {
        resetCanvas();
        canvas.setPreferredSize( new Dimension( CANVAS_WIDTH, CANVAS_HEIGHT ) );
        drawingWhich( DrawingType.POLYGON );

        Box canvases = Box.createHorizontalBox();
        canvases.add( canvas );

        setVertexTypeCanvas();
        canvases.add( vertexTypeCanvas );
        frame.add( canvases );

        frame.add( setButtons(), BorderLayout.SOUTH );

        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );

        frame.pack();
        frame.setVisible( true );
    }

    public void draw( List<Vertex> vertices,
                      List<Face> faces ) {
        this.vertices = vertices;
        this.faces = faces;
        mins = makeCoordinatesNonNegative();
        ratio = fitToCanvas();

        if ( polygonPoints == null ) {
            polygonPoints = Polygons.drawPolygon( this.faces, WHITE_AREA, mins, ratio );
        }

        if ( verticesPoints == null ) {
            verticesPoints = Drawer.drawVertices( WHITE_AREA, vertices, mins, ratio );
            return;
        }

        if ( monotonePoints == null ) {
            monotonePoints = Polygons.drawPolygon( this.faces, WHITE_AREA, mins, ratio );
            return;
        }

        if ( triangulationPoints == null ) {
            triangulationPoints = Polygons.drawPolygon( this.faces, WHITE_AREA, mins, ratio );
            return;
        }
    }

    public void draw( List<Vertex> vertices,
                      Face[] faces ) {
        draw( vertices, Arrays.asList( faces ) );
    }

    public void draw( Graph<DualVertex> graph ) {
        this.graph = graph;
    }

    public void draw( DualVertex start, DualVertex end ) {
        this.start = start;
        this.end = end;
    }

    public void draw( List<Vector> shortestPathPoints ) {
        this.shortestPathPoints = Polygons.drawCorners( shortestPathPoints, WHITE_AREA, mins, ratio );
    }

    public void draw( Vector startPoint, Vector endPoint ) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public void drawVertexType( List<Vertex> vertices ) {
        this.vertexTypePoints = Drawer.drawVertexType( vertices, WHITE_AREA, mins, ratio );
    }

    private int[] makeCoordinatesNonNegative() {
        int xMin = Integer.MAX_VALUE;
        int yMin = Integer.MAX_VALUE;

        for ( Vertex vertex : vertices ) {
            xMin = ( int ) Math.min( xMin, vertex.x );
            yMin = ( int ) Math.min( yMin, vertex.y );
        }

        xMin = Math.min( xMin, 0 );
        yMin = Math.min( yMin, 0 );

        return new int[] { -xMin, -yMin };
    }

    // TODO: 7/15/2021 not perfect, perhaps should use transforming operations in CG to do so
    private int fitToCanvas() {
        int xMax = Integer.MIN_VALUE;
        int yMax = Integer.MIN_VALUE;

        for ( Vertex vertex : vertices ) {
            xMax = ( int ) Math.max( xMax, vertex.x + mins[ 0 ] );
            yMax = ( int ) Math.max( yMax, vertex.y + mins[ 1 ] );
        }

        int ratio = Math.min( ( CANVAS_WIDTH - WHITE_AREA * 2 ) / xMax,
                ( CANVAS_HEIGHT - WHITE_AREA * 2 ) / yMax );

        return ratio <= 2 ? 1 : ratio;
    }

    public void initialize( List<Vertex> vertices,
                            Face[] faces ) {
        initialize( vertices, Arrays.asList( faces ) );
    }

    public void initialize( List<Vertex> vertices,
                            List<Face> faces ) {
        this.vertices = vertices;
        this.faces = faces;
        mins = makeCoordinatesNonNegative();
        ratio = fitToCanvas();
        initialize();
    }

    public static
    void main( String[] args ) {
        List<Vertex> vertices = new ArrayList<>();
        vertices.add( new MonotoneVertex( 5, 0 ) );
        vertices.add( new MonotoneVertex( 4, 5 ) );
        vertices.add( new MonotoneVertex( 1, 3 ) );
        vertices.add( new MonotoneVertex( -3, 5 ) );
        vertices.add( new MonotoneVertex( -2, 1 ) );
        vertices.add( new MonotoneVertex( 1, -1 ) );

        Face[] faces = myLibraries.util.geometry.tools.Polygons.getDCEL( vertices );
        new Program().initialize( vertices, faces );
    }
}
