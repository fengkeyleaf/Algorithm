package CGTsinghua.PA_2.problem_1.GUI;

/*
 * Program.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.1$
 *
 * Revisions:
 *     $1.0 basic but bad visualization on 7/21/2021 $
 */

import com.fengkeyleaf.util.geom.Face;
import com.fengkeyleaf.util.geom.Lines;
import com.fengkeyleaf.util.geom.Vector;
import com.fengkeyleaf.util.geom.Vectors;
import com.fengkeyleaf.util.graph.DualVertex;
import com.fengkeyleaf.util.graph.Graph;
import com.fengkeyleaf.util.graph.Graphs;
import com.fengkeyleaf.GUI.geom.DrawingProgram;
import com.fengkeyleaf.util.geom.Vertex;
import com.fengkeyleaf.util.geom.Polygons;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * visualized program for polygon triangulation,
 * BFS in a dual graph and funnel algorithm
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public final class Program extends DrawingProgram {
    private static final String title = "Visualized debugger for Shortest path in a polygon";

    // colors
    private static final Color[] colors = new Color[] {
        Color.BLACK, Color.BLUE, Color.CYAN,
        Color.DARK_GRAY, Color.GREEN, Color.LIGHT_GRAY,
        Color.MAGENTA, Color.ORANGE, Color.PINK,
        Color.RED, Color.YELLOW
    };
    private static final Color SHORTEST_PATH_COLOR = Color.CYAN;
    private static final Color START_POINT_COLOR = Color.GREEN;
    private static final Color END_POINT_COLOR = Color.RED;

    // program's components
    private BufferedImage imageVertexType;
    private BufferedImage imageVertexTypeDraw;
    private Graphics graphicsVertexType;

    final Canvas vertexTypeCanvas = new Canvas() {
        @Override
        public void paint( Graphics g ) {
            if ( buttons.isShowingVertexType )
                g.drawImage( imageVertexType, 0, 0, null );
            else
                g.drawImage( imageVertexTypeDraw, 0, 0, null );
        }
    };

    private final Buttons buttons = new Buttons( this );

    // dimension
    private int CANVAS_VERTEX_TYPE_HEIGHT;
    private int CANVAS_VERTEX_TYPE_WIDTH;

    DrawingType drawingType = DrawingType.POLYGON;

    // data
    private List<Face> faces;
    private List<List<Integer>> polygonPoints;
    private List<Color> polygonColors = new ArrayList<>();
    private List<List<Integer>> monotonePoints;
    private List<Color> monotoneColors = new ArrayList<>();
    private List<Integer> verticesPoints;
    private List<Color> verticesColors = new ArrayList<>();
    private List<Integer> vertexTypePoints;
    private List<List<Integer>> triangulationPoints;
    private List<Color> triangulationColors = new ArrayList<>();
    private List<Integer> shortestPathPoints;
    private Graph<DualVertex> graph;
    private DualVertex start;
    private DualVertex end;
    private Vector startPoint;
    private Vector endPoint;
    private final String VERTEX_TYPE_FILEPATH = "src/CGTsinghua/PA_2/problem_1/GUI/pics/vertex_type.png";
//    private final String VERTEX_TYPE_FILEPATH = "myLibraries/GUI/geometry/triangulation/pics/vertex_type.png"; // - command line

    enum DrawingType {
        // basic drawing-type
        POLYGON, MONOTONE, TRIANGULATION, DUAL, SHORTEST_TRI,
        // adding drawing-type
        VERTEX, VERTEX_TYPE, START_END_POINT, START_END_TRI, INTERNAL_DIAGONALS, SHORTEST
    }

    /**
     * constructs to create a visualized Program
     * */

    public Program( int originWidth, int originHeight ) {
        super( title, originWidth, originHeight );
    }

    public Program( int CANVAS_WIDTH, int CANVAS_HEIGHT, int originWidth, int originHeight ) {
        super( title, CANVAS_WIDTH, CANVAS_HEIGHT, originWidth, originHeight );
    }

    public void resetCanvas() {
        super.resetCanvas();
    }

    void repaint() {
        canvas.repaint();
    }

    void drawingWhich( Program.DrawingType drawingType ) {
        switch ( drawingType ) {
            // basic drawing-type
            case POLYGON -> Polygons.drawPolygons( graphics, polygonPoints, polygonColors );
            case MONOTONE -> Polygons.drawPolygons( graphics, monotonePoints, monotoneColors );
            case TRIANGULATION -> Polygons.drawPolygons( graphics, triangulationPoints, triangulationColors );
            case DUAL -> {
                drawingWhich( Program.DrawingType.TRIANGULATION );
                Graphs.drawDualGraph( graph, graphics, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT );
            }
            case SHORTEST_TRI -> {
                Polygons.drawPolygons( graphics, triangulationPoints, triangulationColors );
                Drawer.drawShortestPathWithTriangles( end, graphics, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT );
            }

            // adding drawing-type
            case VERTEX -> Vectors.drawPointsWithID( graphics, verticesPoints );
            case VERTEX_TYPE -> Vertex.drawVertexType( graphics, vertexTypePoints );
            case START_END_POINT -> drawStartAndEndPoint();
            case START_END_TRI -> {
                Polygons.drawPolygon( graphics, start.face, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT, START_POINT_COLOR );
                Polygons.drawPolygon( graphics, end.face, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT, END_POINT_COLOR );
            }
            case INTERNAL_DIAGONALS -> Drawer.drawInternalDiagonals( end, graphics, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT );
            case SHORTEST -> Lines.drawPath( graphics, shortestPathPoints, SHORTEST_PATH_COLOR );
            default -> {
                assert false;
            }
        }
    }

    private void resetVertexTypeCanvas() {
        graphicsVertexType.setColor( Color.WHITE );
        graphicsVertexType.fillRect( 0, 0, CANVAS_VERTEX_TYPE_WIDTH, CANVAS_VERTEX_TYPE_HEIGHT );
    }

    private void drawStartAndEndPoint() {
        Vector reversedStart = Vectors.reversedY( startPoint );
        Vector reversedEnd = Vectors.reversedY( endPoint );
        Vectors.drawPoint( graphics, reversedStart, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT, START_POINT_COLOR );
        Vectors.drawPoint( graphics, reversedEnd, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT, END_POINT_COLOR );
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

        frame.add( buttons.setButtons(), BorderLayout.SOUTH );

        frame.pack();
        frame.setVisible( true );
    }

    public void addDrawingData( List<Vertex> vertices,
                      List<Face> faces ) {
        this.faces = faces;

        if ( polygonPoints == null ) {
            polygonPoints = new ArrayList<>();
            polygonPoints.add( Polygons.getDrawingPoints( this.faces, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT ) );
            polygonColors = Polygons.getColors( polygonPoints.get( 0 ).size(), NORMAL_POLYGON_COLOR );
        }

        if ( verticesPoints == null ) {
            List<Vector> points = new ArrayList<>( vertices.size() + 1 );
            points.addAll( vertices );
            verticesPoints = Vectors.getDrawingPointsWithID( Vectors.reversedY( points ), originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT );
            verticesColors = Polygons.getColors( verticesPoints.size(), NORMAL_POLYGON_COLOR );
            return;
        }

        if ( monotonePoints == null ) {
            monotonePoints = new ArrayList<>();
            monotonePoints.add( Polygons.getDrawingPoints( this.faces, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT  ) );
            monotoneColors = Polygons.getColors( monotonePoints.get( 0 ).size(), NORMAL_POLYGON_COLOR );
            return;
        }

        if ( triangulationPoints == null ) {
            triangulationPoints = new ArrayList<>();
            triangulationPoints.add( Polygons.getDrawingPoints( this.faces, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT ) );
            triangulationColors = Polygons.getColors( triangulationPoints.get( 0 ).size(), NORMAL_POLYGON_COLOR );
            return;
        }
    }

    public void addDrawingData( List<Vertex> vertices,
                      Face[] faces ) {
        addDrawingData( vertices, Arrays.asList( faces ) );
    }

    public void addDualGraph( Graph<DualVertex> graph ) {
        this.graph = graph;
    }

    public void addStartAndEndPoints( DualVertex start, DualVertex end ) {
        this.start = start;
        this.end = end;
    }

    /**
     * draw shortest Path Points
     */

    public void draw( List<Vector> shortestPathPoints ) {
        this.shortestPathPoints = Vectors.getDrawingPoints( Vectors.reversedY( shortestPathPoints ), originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT );
    }

    public void addStartAndEndPoints( Vector startPoint, Vector endPoint ) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    /**
     * draw Vertex Type
     */

    public void drawVertexType( List<Vertex> vertices ) {
        this.vertexTypePoints = Vertex.drawVertexType( vertices, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT );
    }

    public void initialize( List<Vertex> vertices,
                            Face[] faces ) {
        initialize( vertices, Arrays.asList( faces ) );
    }

    public void initialize( List<Vertex> vertices,
                            List<Face> faces ) {
        this.faces = faces;

        initialize();
    }

    public static
    void main( String[] args ) {
        List<Vertex> vertices = new ArrayList<>();
        vertices.add( new Vertex( 5, 0 ) );
        vertices.add( new Vertex( 4, 5 ) );
        vertices.add( new Vertex( 1, 3 ) );
        vertices.add( new Vertex( -3, 5 ) );
        vertices.add( new Vertex( -2, 1 ) );
        vertices.add( new Vertex( 1, -1 ) );

        Face[] faces = Polygons.getDCEL( vertices );
//        new Program().initialize( vertices, faces );
    }
}
