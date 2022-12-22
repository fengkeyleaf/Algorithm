package com.fengkeyleaf.GUI.geom;

/*
 * DrawingProgram.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import com.fengkeyleaf.util.geom.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Visualized program holding common data fields and methods
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public class DrawingProgram {
    public static final Color NORMAL_POLYGON_COLOR = Color.GRAY;
    public static final Color INTERSECTION_COLOR = Color.RED;
    public static final int DEFAULT_SIZE = 800;

    // radius for drawing a circle
    public static final int RADIUS = 6;
    public static final int CYCLE_OFFSET = RADIUS / 2;

    // program's components
    protected final BufferedImage image;
    protected final Graphics graphics;

    protected final Frame frame;
    protected final Canvas canvas = new Canvas() {
        @Override
        public void paint( Graphics g ) {
            g.drawImage( image, 0, 0, null );
        }
    };

    // dimension
    protected final int CANVAS_WIDTH;
    protected final int CANVAS_HEIGHT;
    // even if those two are 0, it doesn't matter
    // we cannot have the case where we divide by 0:
    // ( coordinates * windowWidth ) / originWidth + windowWidth / 2
    protected final int originWidth;
    protected final int originHeight;

    // drawing data
    final List<List<Vector>> points = new ArrayList<>();
    final List<Color> colorsPoint = new ArrayList<>();
    final List<List<Integer>> polygonPoints = new ArrayList<>();
    final List<Boolean> polyDrawingTypes = new ArrayList<>();
    static final boolean drawPoly = true;
    static final boolean fillPoly = false;

    final List<Color> colorsPoly = new ArrayList<>();
    final List<List<Integer>> linesPoints = new ArrayList<>();
    final List<Color> colorsLine = new ArrayList<>();
    final List<List<Integer>> circlePoints = new ArrayList<>();
    final List<Color> colorsCircle = new ArrayList<>();

    public DrawingProgram( String title, int originWidth, int originHeight ) {
        this( title, DEFAULT_SIZE, DEFAULT_SIZE, originWidth, originHeight );
    }

    public DrawingProgram( String title, int CANVAS_WIDTH, int CANVAS_HEIGHT,
                           int originWidth, int originHeight ) {

        if ( CANVAS_HEIGHT < 0 || CANVAS_WIDTH < 0 || originWidth < 0 || originHeight < 0 )
            throw new IllegalArgumentException( "Canvas size cannot be negative" );

        this.CANVAS_WIDTH = CANVAS_WIDTH;
        this.CANVAS_HEIGHT = CANVAS_HEIGHT;
        this.originWidth = originWidth;
        this.originHeight = originHeight;

        image = new BufferedImage( CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_BGR );
        graphics = image.getGraphics();

        frame = new Frame( title );
        // add the feature to close the window
        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
    }

    public static
    void storePoints( List<Integer> points, int ...nums ) {
        for ( int num : nums ) {
            points.add( num );
        }
    }

    /**
     * add drawing data for points
     * */

    public void drawPoints( Color c, List<Vector> points ) {
        this.points.add( Vectors.reversedY( points ) );
        colorsPoint.add( c );
    }

    public void drawPoints( Color c, Vector... points ) {
        drawPoints( c, Arrays.asList( points ) );
    }

    /**
     * add drawing data for the polygon.
     *
     * @param f face with non-nullable outerComponent.
     * */

    public void drawPoly( Color c, Face f ) {
        polygonPoints.add(
                Polygons.getDrawingPoints(
                        f,
                        originWidth,
                        originHeight,
                        CANVAS_WIDTH,
                        CANVAS_HEIGHT )
        );
        colorsPoly.add( c );
        polyDrawingTypes.add( drawPoly );
    }

    /**
     * add drawing data for polygons contained in the given one.
     *
     * @param outer given polygon face, infinite face or arbitrary one.
     * */

    public void drawPolyAll( Color c, Face outer ) {
        outer.getInners().forEach( f -> drawPoly( c, f ) );
    }

    /**
     * add filling drawing data for the polygon.
     *
     * @param f face with non-nullable outerComponent.
     * */

    public void fillPoly( Color c, Face f ) {
        drawPoly( c, f );
        polyDrawingTypes.set( polyDrawingTypes.size() - 1, fillPoly );
    }

    /**
     * add drawing data for lines, but will not draw endpoints.
     * */

    public void drawLines( Color c , List<Line> lines ) {
        linesPoints.add(
                Lines.getDrawingPoints(
                        lines,
                        originWidth,
                        originHeight,
                        CANVAS_WIDTH,
                        CANVAS_HEIGHT )
        );
        colorsLine.add( c );
    }

    /**
     * add drawing data for segments as well as their endpoints.
     * */

    public void drawSegments( Color c, List<Line> lines ) {
        drawLines( c, lines );

        List<Vector> P = new ArrayList<>( lines.size() * 2 );
        lines.forEach( s -> {
            P.add( s.startPoint );
            P.add( s.endPoint );
        } );
        drawPoints( DrawingProgram.NORMAL_POLYGON_COLOR, P );
    }

    /**
     * add drawing data for circles
     * */

    public void drawCircles( Color c, List<Circle> circles ) {
        circlePoints.add(
                Circles.getDrawingPoints(
                        circles,
                        originWidth, originHeight,
                        CANVAS_WIDTH, CANVAS_HEIGHT
                )
        );
        colorsCircle.add( c );
    }

    /**
     * reset drawing data
     * */

    public void resetData() {
        points.clear();
        colorsPoint.clear();
        polygonPoints.clear();
        colorsPoly.clear();
        linesPoints.clear();
        colorsLine.clear();
    }

    public void resetCanvas() {
        graphics.setColor( Color.WHITE );
        graphics.fillRect( 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT );
    }

    /**
     * draw shapes stored in this drawing program.
     * */

    public void draw() {
        Polygons.drawPolygons(
                graphics, polygonPoints,
                polyDrawingTypes, colorsPoly
        );
        Lines.drawLines( graphics, linesPoints, colorsLine );
        Circles.draw( graphics, circlePoints, colorsCircle );
        Vectors.drawPoints(
                graphics, points,
                originWidth, originHeight,
                CANVAS_WIDTH, CANVAS_HEIGHT, colorsPoint
        );
    }

    public void initialize() {
        resetCanvas();
        canvas.setPreferredSize( new Dimension( CANVAS_WIDTH, CANVAS_HEIGHT ) );

        draw();

        Box canvases = Box.createHorizontalBox();
        canvases.add( canvas );
        frame.add( canvases );

        frame.pack();
        frame.setVisible( true );
    }
}
