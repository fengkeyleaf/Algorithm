package CGTsinghua.PA_2.problem_1.GUI;

/*
 * Buttons.java
 *
 * JDK: 16
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/28/2021$
 */

import javax.swing.*;
import java.awt.*;

/**
 * class holding buttons on the program
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

public final class Buttons {
    private final Program program;

    private static final int LENGTH = 16;

    // buttons
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

    // controlling triggers
    boolean isShowingVertices;
    boolean isShowingStartAndEndTri;
    boolean isShowingInternalDiagonals;
    boolean isShowingShortestPath;
    boolean isShowingStartAndEndPoint;
    boolean isShowingVertexType;

    public Buttons( Program program ) {
        this.program = program;
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

    void showingVertices() {
        if ( isShowingVertices )
            program.drawingWhich( Program.DrawingType.VERTEX );
    }

    void showingVertexType() {
        if ( isShowingVertexType )
            program.drawingWhich( Program.DrawingType.VERTEX_TYPE );
    }

    void showingStartAndEndPoint() {
        if ( isShowingStartAndEndPoint )
            program.drawingWhich( Program.DrawingType.START_END_POINT );
    }

    void showingShortestPath() {
        if ( isShowingShortestPath )
            program.drawingWhich( Program.DrawingType.SHORTEST );
    }

    // only show either start and end point or vertex type,
    // not both
    void showingStartAndEndPointOrVertexType() {
        if ( isShowingStartAndEndPoint )
            program.drawingWhich( Program.DrawingType.START_END_POINT );
        else if ( isShowingVertexType )
            program.drawingWhich( Program.DrawingType.VERTEX_TYPE );
    }

    void showingStartAndEndTri() {
        if ( isShowingStartAndEndTri )
            program.drawingWhich(  Program.DrawingType.START_END_TRI );
    }

    void showingInternalDiagonals() {
        if ( isShowingInternalDiagonals )
            program.drawingWhich( Program.DrawingType.INTERNAL_DIAGONALS );
    }

    private void addListenerDisplayBox() {
        // show normal polygon;
        // basic drawing-type -> POLYGON.
        showPolygon.addActionListener( e -> {
            program.resetCanvas();
            program.drawingWhich( Program.DrawingType.POLYGON );

            showingVertices();
            showingStartAndEndPointOrVertexType();
            showingShortestPath();

            program.drawingType = Program.DrawingType.POLYGON;
            isShowingStartAndEndTri = false;
            isShowingInternalDiagonals = false;

            program.repaint();
        } );

        // show vertices;
        // adding drawing-type -> VERTEX;
        showVertices.addActionListener( e -> {
            if ( !isShowingVertices ) {
                program.drawingWhich( Program.DrawingType.VERTEX );
                isShowingVertices = true;
            }
            else {
                program.resetCanvas();
                program.drawingWhich( program.drawingType );

                showingStartAndEndPointOrVertexType();
                showingStartAndEndTri();

                isShowingInternalDiagonals = false;
                isShowingShortestPath = false;
                isShowingVertices = false;
            }

            program.repaint();
        } );

        // show type of vertices;
        // adding drawing-type -> VERTEX_TYPE;
        showVertexType.addActionListener( e -> {
            program.resetCanvas();
            program.drawingWhich( program.drawingType );

            showingVertices();
            showingShortestPath();
            showingStartAndEndTri();

            if ( !isShowingVertexType ) {
                program.drawingWhich( Program.DrawingType.VERTEX_TYPE );

                isShowingVertexType = true;
                isShowingStartAndEndPoint = false;
                isShowingInternalDiagonals = false;
            }
            else {
                showingStartAndEndPoint();
                showingInternalDiagonals();
                isShowingVertexType = false;
            }

            program.vertexTypeCanvas.repaint();
            program.repaint();
        } );

        // show start and end pont;
        // adding drawing-type -> START_END_POINT;
        showStartAndEndPoint.addActionListener( e -> {
            program.resetCanvas();
            program.drawingWhich( program.drawingType );

            showingVertices();
            showingShortestPath();
            showingStartAndEndTri();
            showingInternalDiagonals();

            if ( !isShowingStartAndEndPoint ) {
                program.drawingWhich( Program.DrawingType.START_END_POINT );
                isShowingStartAndEndPoint = true;
                isShowingVertexType = false;
            }
            else {
                showingVertexType();
                isShowingStartAndEndPoint = false;
            }

            program.vertexTypeCanvas.repaint();
            program.repaint();
        } );

        // show start and end triangle in the dual graph;
        // adding drawing-type -> START_END_TRI;
        showStartAndEndTri.addActionListener( e -> {
            if ( !isShowingStartAndEndTri &&
                    program.drawingType != Program.DrawingType.POLYGON &&
                    program.drawingType != Program.DrawingType.MONOTONE ) {
                program.drawingWhich( Program.DrawingType.START_END_TRI );
                isShowingStartAndEndTri = true;
            }
            else {
                program.resetCanvas();
                program.drawingWhich( program.drawingType );

                showingVertices();
                showingStartAndEndPointOrVertexType();

                isShowingStartAndEndTri = false;
                isShowingInternalDiagonals = false;
                isShowingShortestPath = false;
            }

            program.repaint();
        } );
    }

    private void addListenerPartitioningBox() {
        // show monetone polygons;
        // basic drawing-type -> MONOTONE.
        monoToning.addActionListener( e -> {
            program.resetCanvas();
            program.drawingWhich( Program.DrawingType.MONOTONE );

            showingVertices();
            showingStartAndEndPointOrVertexType();
            showingShortestPath();

            program.drawingType = Program.DrawingType.MONOTONE;
            isShowingStartAndEndTri = false;
            isShowingInternalDiagonals = false;

            program.repaint();
        } );

        // show monotone polygons;
        // basic drawing-type -> TRIANGULATION.
        triangulation.addActionListener( e -> {
            program.resetCanvas();
            program.drawingWhich( Program.DrawingType.TRIANGULATION );

            showingVertices();
            showingStartAndEndPointOrVertexType();
            showingShortestPath();

            program.drawingType = Program.DrawingType.TRIANGULATION;
            isShowingStartAndEndTri = false;
            isShowingInternalDiagonals = false;

            program.repaint();
        } );

        // show dual graph;
        // basic drawing-type -> DUAL.
        dualGraph.addActionListener( e -> {
            program.resetCanvas();
            program.drawingWhich( Program.DrawingType.DUAL );

            showingVertices();
            showingStartAndEndPointOrVertexType();
            showingShortestPath();

            program.drawingType = Program.DrawingType.DUAL;
            isShowingStartAndEndTri = false;
            isShowingInternalDiagonals = false;

            program.repaint();
        } );
    }

    private void addListenerShortestPath() {
        // show Shortest with Triangles;
        // basic drawing-type -> SHORTEST_TRI.
        shortestTri.addActionListener( e -> {
            program.resetCanvas();
            program.drawingWhich( Program.DrawingType.SHORTEST_TRI );
            program.drawingWhich( Program.DrawingType.START_END_TRI );

            showingVertices();
            showingStartAndEndPointOrVertexType();
            showingShortestPath();

            program.drawingType = Program.DrawingType.SHORTEST_TRI;
            isShowingStartAndEndTri = true;
            isShowingInternalDiagonals = false;

            program.repaint();
        } );

        // show Internal diagonals for Funnel;
        // adding drawing-type -> INTERNAL_DIAGONALS;
        InternalDiagonals.addActionListener( e -> {
            if ( !isShowingInternalDiagonals &&
                    program.drawingType == Program.DrawingType.SHORTEST_TRI ) {
                program.drawingWhich( Program.DrawingType.INTERNAL_DIAGONALS );
                isShowingInternalDiagonals = true;
            }
            else {
                program.resetCanvas();
                program.drawingWhich( program.drawingType );

                showingVertices();
                showingStartAndEndPointOrVertexType();
                showingStartAndEndTri();
                showingShortestPath();

                isShowingInternalDiagonals = false;
            }

            program.repaint();
        } );

        // show Shortest Path;
        // adding drawing-type -> SHORTEST;
        shortestPath.addActionListener( e -> {
            program.resetCanvas();
            program.drawingWhich( program.drawingType );

            showingVertices();
            showingStartAndEndTri();
            showingInternalDiagonals();

            if ( !isShowingShortestPath ) {
                program.drawingWhich( Program.DrawingType.SHORTEST );
                program.drawingWhich( Program.DrawingType.START_END_POINT );
                isShowingVertexType = false;
                isShowingShortestPath = true;
                isShowingStartAndEndPoint = true;
            }
            else {
                showingStartAndEndPointOrVertexType();
                isShowingShortestPath = false;
            }

            program.vertexTypeCanvas.repaint();
            program.repaint();
        } );
    }

    Box setButtons() {
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
}
