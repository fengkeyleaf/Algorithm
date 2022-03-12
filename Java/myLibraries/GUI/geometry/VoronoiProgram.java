package myLibraries.GUI.geometry;

/*
 * Program.java
 *
 * JDK: 15
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 1/7/2022$
 */

import myLibraries.util.geometry.BoundingBox;
import myLibraries.util.geometry.DCEL.Face;
import myLibraries.util.geometry.DCEL.VoronoiFace;
import myLibraries.util.geometry.elements.Circle;
import myLibraries.util.geometry.elements.Vector;
import myLibraries.util.geometry.Circles;
import myLibraries.util.geometry.Polygons;
import myLibraries.util.geometry.Vectors;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Visualization program for Voronoi Diagrams
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public class VoronoiProgram extends DCELProgram {
    static final String title = "Visualized debugger for Voronoi Diagrams";
    static final Color vertexCircleColor = new Color( 0, 204, 255, 60 );
    static final Color siteColor = new Color( 21, 101, 192, 80 );

    public VoronoiProgram( int originWidth, int originHeight ) {
        super( title, originWidth, originHeight );
    }

    public void addVertices( List<Face> siteFaces ) {
        List<Vector> points = new ArrayList<>( siteFaces.size() );
        siteFaces.forEach( f -> points.add( ( ( VoronoiFace ) f ).site ) );
        addPoints( points, NORMAL_POLYGON_COLOR );
    }

    public void addPolyAndCircles( BoundingBox box ) {
        if ( box == null ) return;

        List<Face> faces = new ArrayList<>( box.outer.innerComponents.size() );
        box.outer.innerComponents.forEach( e -> faces.add( e.twin.incidentFace ) );
        addPoly( faces, NORMAL_POLYGON_COLOR );

        List<Circle> circles = new ArrayList<>( box.vertices.size() + 1 );
        box.vertices.forEach( v -> circles.add( v.circle ) );
        addCircles( circles, vertexCircleColor );
    }

    public void addQuery( List<Face> faces, Vector p ) {
        List<Vector> points = new ArrayList<>( 1 );
        points.add( p );
        addPoints( points, INTERSECTION_COLOR );

        addPoly( faces, INTERSECTION_COLOR );
    }

    public void initialize() {
        resetCanvas();
        canvas.setPreferredSize( new Dimension( CANVAS_WIDTH, CANVAS_HEIGHT ) );

        Polygons.drawPolygons( graphics, polygonPoints, colorsPoly );

        Vectors.drawPoints( graphics, points, originWidth, originHeight, CANVAS_WIDTH, CANVAS_HEIGHT, colorsPoint );

        Circles.draw( graphics, circlePoints, colorsCircle );

        Box canvases = Box.createHorizontalBox();
        canvases.add( canvas );
        frame.add( canvases );

        frame.pack();
        frame.setVisible( true );
    }
}
