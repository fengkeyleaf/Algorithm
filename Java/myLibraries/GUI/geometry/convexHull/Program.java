package myLibraries.GUI.geometry.convexHull;

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

import myLibraries.GUI.geometry.DrawingProgram;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.elements.point.Vector;
import myLibraries.util.geometry.tools.Polygons;
import myLibraries.util.geometry.tools.Vectors;

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

public final class Program extends DrawingProgram {
    private static final String title = "Visualized debugger for DCEL";

    // data
    private final List<List<Vector>> vertices = new ArrayList<>();
    private final List<Color> colorsVertices = new ArrayList<>();
    private final List<List<Integer>> polygonPoints = new ArrayList<>();
    private final List<Color> colorsPoly = new ArrayList<>();

    public Program( int originWidth, int originHeight ) {
        super( title, originWidth, originHeight );
    }

    public Program( int CANVAS_WIDTH, int CANVAS_HEIGHT, int originWidth, int originHeight ) {
        super( title, CANVAS_WIDTH, CANVAS_HEIGHT, originWidth, originHeight );
    }

    public void addVertices( List<Vector> vertices, Color color ) {
        this.vertices.add( new ArrayList<>( vertices ) );
        colorsVertices.add( color );
    }

    public void addPoly( List<Face> faces, Color color ) {
        polygonPoints.add( Polygons.getAllDrawingPoints( faces, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT ) );
        colorsPoly.add( color );
    }

    public void addPoly( Face[] faces, Color color ) {
        List<Face> faceList = new ArrayList<>( faces.length + 1 );
        faceList.addAll( Arrays.asList( faces ) );
        addPoly( faceList, color );
    }

    public void reset() {
        vertices.clear();
        colorsVertices.clear();
        polygonPoints.clear();
        colorsPoly.clear();
    }

    public void initialize() {
        resetCanvas();
        canvas.setPreferredSize( new Dimension( CANVAS_WIDTH, CANVAS_HEIGHT ) );

//        assert polygonPoints.size() == colorsPoly.size();
//        for ( int i = 0; i < polygonPoints.size(); i++ ) {
//            Lines.drawLines( graphics, polygonPoints.get( i ), colorsPoly.get( i ) );
//        }
        Polygons.drawPolygons( graphics, polygonPoints, colorsPoly );

        for ( int i = 0; i < vertices.size(); i++ ) {
            Color color = colorsVertices.get( i );
            List<Vector> flippedY = Vectors.reversedY( vertices.get( i ) );
            flippedY.forEach( v -> {
                Vectors.drawPoint( graphics, v, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT, color );
            } );
        }

        Box canvases = Box.createHorizontalBox();
        canvases.add( canvas );
        frame.add( canvases );

        frame.pack();
        frame.setVisible( true );
    }
}
