package myLibraries.GUI.geometry.intersection;

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
 *
 * JDK: 16
 */

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.util.geometry.elements.line.Line;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.Lines;
import myLibraries.util.geometry.tools.Vectors;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * visualized program for geometric intersection
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class Program extends DrawingProgram {
    private static final String title = "Visualized debugger for intersection";

    // data
    private List<Integer> linesPoints;
    private List<Vector> intersections;

    public Program( int originWidth, int originHeight ) {
        super( title, originWidth, originHeight );
    }

    public Program( int CANVAS_WIDTH, int CANVAS_HEIGHT, int originWidth, int originHeight ) {
        super( title, CANVAS_WIDTH, CANVAS_HEIGHT, originWidth, originHeight );
    }

    /**
     * preprocess input data
     * */

    public void draw( List<Line> lines,
                      List<Vector> intersections ) {
        this.intersections = Vectors.reversedY( intersections );
        // draw lines only without intersection
//        this.intersections = new ArrayList<>();

        linesPoints = Lines.getDrawingPoints( lines, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT );
    }

    /**
     * initialize the drawing program
     * */

    public void initialize() {
        resetCanvas();
        canvas.setPreferredSize( new Dimension( CANVAS_WIDTH, CANVAS_HEIGHT ) );

        Lines.drawLines( graphics, linesPoints, NORMAL_POLYGON_COLOR );
        for ( int i = 0; i < linesPoints.size(); i += 2 ) {
            Vectors.drawPoint( graphics, linesPoints.get( i ), linesPoints.get( i + 1 ), NORMAL_POLYGON_COLOR );
        }

        Vectors.drawPoints( graphics, intersections, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT, INTERSECTION_COLOR );

        Box canvases = Box.createHorizontalBox();
        canvases.add( canvas );
        frame.add( canvases );

        frame.pack();
        frame.setVisible( true );
    }
}
