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
 *
 */

import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.Lines;
import myLibraries.util.geometry.elements.Line;
import myLibraries.util.geometry.elements.Vector;
import myLibraries.util.geometry.Polygons;
import myLibraries.util.geometry.Vectors;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * visualized program for DECL, including but not limited to convex hull
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

public class DCELProgram extends DrawingProgram {
    static final String title = "Visualized debugger for DCEL";

    public DCELProgram( int originWidth, int originHeight ) {
        super( title, originWidth, originHeight );
    }

    DCELProgram( String title, int originWidth, int originHeight ) {
        super( title, originWidth, originHeight );
    }

    public DCELProgram( int CANVAS_WIDTH, int CANVAS_HEIGHT, int originWidth, int originHeight ) {
        super( title, CANVAS_WIDTH, CANVAS_HEIGHT, originWidth, originHeight );
    }

    public void initialize() {
        resetCanvas();
        canvas.setPreferredSize( new Dimension( CANVAS_WIDTH, CANVAS_HEIGHT ) );

        Polygons.drawPolygons( graphics, polygonPoints, colorsPoly );

        Vectors.drawPoints( graphics, points, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT, colorsPoint );

        Lines.drawLines( graphics, linesPoints, colorsLine );

        Box canvases = Box.createHorizontalBox();
        canvases.add( canvas );
        frame.add( canvases );

        frame.pack();
        frame.setVisible( true );
    }
}
