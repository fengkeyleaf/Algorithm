package myLibraries.GUI.geometry;

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

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Visualized program holding common data fields and methods
 *
 * @author Xiaoyu Tongyang, or call me sora for short
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

    public DrawingProgram( String title, int originWidth, int originHeight ) {
        this( title, DEFAULT_SIZE, DEFAULT_SIZE, originWidth, originHeight );
    }

    public DrawingProgram( String title, int CANVAS_WIDTH, int CANVAS_HEIGHT, int originWidth, int originHeight ) {
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
            points.add( num );;
        }
    }

    protected void resetCanvas() {
        graphics.setColor( Color.WHITE );
        graphics.fillRect( 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT );
    }
}