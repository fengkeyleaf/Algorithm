package myLibraries.GUI.geometry;

/*
 * Program.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.Vector;
import myLibraries.util.geometry.Lines;
import myLibraries.util.geometry.Vectors;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * visualized program for geometric intersection
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public class IntersectProgram extends DrawingProgram {
    private static final String title = "Visualized debugger for intersection";

    public IntersectProgram( int size ) {
        super( title, size, size );
    }

    public IntersectProgram( int originWidth, int originHeight ) {
        super( title, originWidth, originHeight );
    }

    public IntersectProgram( int CANVAS_WIDTH, int CANVAS_HEIGHT, int originWidth, int originHeight ) {
        super( title, CANVAS_WIDTH, CANVAS_HEIGHT, originWidth, originHeight );
    }

    IntersectProgram( String title, int originWidth, int originHeight ) {
        super( title, originWidth, originHeight );
    }

    /**
     * preprocess input data
     *
     * @param lines lines for drawing
     * @param points points for drawing
     * @param pointColor color to draw the points
     * */

    public void draw( List<Line> lines,
                      List<Vector> points,
                      Color pointColor ) {
        if ( points != null ) addPoints( points, pointColor );

        // draw lines only without intersection
//        this.points.add( new ArrayList<>() );

        if ( lines != null ) {
            addLines( lines, NORMAL_POLYGON_COLOR );

            List<Vector> segPoints = new ArrayList<>( lines.size() * 2 + 1 );
            lines.forEach( l -> {
                segPoints.add( l.startPoint );
                segPoints.add( l.endPoint );
            } );
            addPoints( segPoints, NORMAL_POLYGON_COLOR );
        }
    }

    /**
     * initialize the drawing program
     * */

    public void initialize() {
        resetCanvas();
        canvas.setPreferredSize( new Dimension( CANVAS_WIDTH, CANVAS_HEIGHT ) );

        Lines.drawLines( graphics, linesPoints, colorsLine );

        Vectors.drawPoints( graphics, points, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT, colorsPoint );

        Box canvases = Box.createHorizontalBox();
        canvases.add( canvas );
        frame.add( canvases );

        frame.pack();
        frame.setVisible( true );
    }
}
