"use strict"

/*
 * Program.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import MonotoneVertex from "../util/geometry/DCEL/MonotoneVertex.js";
import Main from "../../finalProject/JavaScript/Main.js";
import Vertex from "../util/geometry/DCEL/Vertex.js";
import Circle from "../util/geometry/elements/cycle/Circle.js";
import Vector from "../util/geometry/elements/point/Vector.js";
import Drawer from "./geometry/Drawer.js";
import Example from "../../finalProject/JavaScript/Example.js";

/**
 *
 * Reference resource for setting up webgl:
 * assignments of CSCI-610 foundation of graphics
 *
 * and:
 * http://www.yanhuangxueyuan.com/WebGL/
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

export default class Program {
    // console.log( gl.TRIANGLE_FAN, gl.LINE_STRIP, gl.LINE_LOOP ); // 6 3 2
    static LINE_LOOP = 2;
    static LINE_STRIP = 3;
    static TRIANGLE_FAN = 6;

    constructor( paras ) {
        Program.#check( paras );

        // Global variables that are set and used
        // across the application
        this.canvas = null;
        this.gl = null;

        this.vertexShader = null;
        this.fragmentShader = null;
        this.program = null;

        // original polygons points and their colors
        this.polygonsPoints = null;
        this.polygonsColors = null;
        this.monotonePoints = null;
        this.monotoneColors = null;
        this.triangulationPoints = null;
        this.triangulationColors = null;

        this.#initWebGl( paras );
        this.addCanvasEvents();
    }

    static #check( paras ) {
        console.assert( paras.webgl );
        console.assert( paras.vertexShader );
        console.assert( paras.fragmentShader );
        console.assert( paras.aVertexPosition );
        console.assert( paras.aColor );
    }

    /**
     * Entry point to our application
     * */

    #initWebGl( paras ) {
        // Retrieve the canvas
        this.canvas = document.getElementById( paras.webgl );
        // console.log( this.canvas.width, this.canvas.height );
        if ( !this.canvas ) {
            console.error( `There is no canvas with id ${ 'webgl-canvas' } on this page.` );
            return;
        }

        // Retrieve a WebGL context
        this.gl = this.canvas.getContext( 'webgl2' );

        // Set the clear color to be white
        this.gl.clearColor( 255, 255, 255, 1 );

        // some GL initialization
        this.gl.enable( this.gl.DEPTH_TEST );
        this.gl.enable( this.gl.CULL_FACE );

        this.gl.cullFace( this.gl.BACK );
        this.gl.frontFace( this.gl.CCW );
        this.gl.depthFunc( this.gl.LEQUAL )
        this.gl.clearDepth( 1.0 )

        // Read, compile, and link your shaders
        this.#initProgram( paras );
    }

    static canvasClickEvent = function ( event ) {
        // change instructions for canvas
        document.getElementById( "instructions" ).innerText = Example.canvasInstructions;

        Main.main.whichInput = Main.InputType.CANVAS;
        let vertices = Main.main.vertices;
        let x = event.offsetX;
        let y = event.offsetY;

        // console.log( x, y );
        // coordinate of a widget, assume width = height = 600
        //              0
        //
        //
        // 0            300             600 ( x )
        //
        //
        //              600( y )
        let vertex = new Vector( x, y, -1 );
        // ignore duplicate points
        if ( !vertices.isEmpty() && vertices.getLast().equalsXAndY( vertex ) ) return;

        // coordinate of the webgl canvas
        //                          1( y, 300 )
        //
        //
        // -1( x, -300 )            0                    1 ( x, 300 )
        //
        //
        //                          -1( y, -300 )
        let halfWidth = Main.main.originalWidth;
        let halfHeight = Main.main.originalHeight;
        // console.log( Main.main.originalWidth, Main.main.originalHeight );

        let xCal = vertex.x >= halfWidth ? vertex.x - halfWidth : vertex.x - halfWidth;
        let yCal = vertex.y >= halfHeight ? vertex.y - halfHeight : vertex.y - halfHeight;
        // The coordinate of a widget is much like that of Java GUI,
        // so we need to flip y
        vertices.push( new MonotoneVertex( xCal, -yCal ) );

        // must drawing counter-clock wise
        // if ( vertices.length > 2 &&
        //     !Triangles.toLeft( vertices[ vertices.length - 3 ], vertices[ vertices.length - 2 ], vertices[ vertices.length - 1 ] ) ) {
        //     vertices.pop();
        //     alert( "Must drawing counter-clock wise!" );
        //     return;
        // }

        // points lie in the range of [ 0. 2 ],
        // then minus 1, they lie in the range of [ -1, 1 ],
        // just the same range that webgl requires
        // note that we could use this result to get points for calculating those polygons:
        // xCal = xCanvas * halfWidth,
        // this is correct, but will introduce precision issue,
        // since we use division.
        // So this method is not recommended to compute actual polygon vertices
        // Further, the previous method can calculate points drawn in the webgl,
        // but I leave both approaches for your guys.
        let xCanvas = vertex.x / halfWidth - 1;
        let yCanvas = -( vertex.y / halfHeight - 1 );
        let vertexCanvas = new Vertex( xCanvas, yCanvas );

        // console.log( vertex, new Vertex( xCanvas, -yCanvas ), vertices.getLast() );
        let lastDraw = Main.pop();
        console.assert( lastDraw.length === 3 || lastDraw.length === 0 );

        // push drawing data for edges(lines),
        // we also put the drawing data of edges at the end of those drawing data arrays
        let linePoints = lastDraw.isEmpty() ? new Float32Array( [] ) : lastDraw[ 0 ];
        linePoints = linePoints.push( xCanvas, yCanvas )
        let lineColors = lastDraw.isEmpty() ? new Float32Array( [] ) : lastDraw[ 1 ];
        lineColors = lineColors.concat( Drawer.black.concat( Drawer.black ) );

        // push drawing data for points
        let { points, colors } = new Circle( {
            center: vertexCanvas,
            radius: 0.01,
            color: Vertex.NORMAL_COLOR
        } ).getPoints();
        Main.main.pushData( new Float32Array( points ), new Float32Array( colors ), Program.TRIANGLE_FAN );

        // push drawing data for lines
        Main.main.pushData( new Float32Array( linePoints ), new Float32Array( lineColors ), Program.LINE_LOOP );

        Main.main.draw();
    }

    static showCanvasInstructions() {
        document.getElementById( "canvasInstructions" ).style.display = "block";
    }

    static hideCanvasInstructions() {
        document.getElementById( "canvasInstructions" ).style.display = "none";
    }

    cancelCanvasEvents() {
        this.canvas.onclick = null;
        this.canvas.onmouseenter = null;
        this.canvas.onmouseleave = null;
    }

    addCanvasEvents() {
        console.assert( this.canvas != null );

        Main.main.originalWidth = this.canvas.width / 2;
        Main.main.originalHeight = this.canvas.height / 2;

        this.canvas.onclick = Program.canvasClickEvent;
        this.canvas.onmouseenter = Program.hideCanvasInstructions;
        this.canvas.onmouseleave = Program.showCanvasInstructions;
    }

    /**
     * Create a program with the appropriate vertex and fragment shaders
     * */

    #initProgram( paras ) {
        this.vertexShader = this.#getShader( paras.vertexShader );
        this.fragmentShader = this.#getShader( paras.fragmentShader );

        // Create a program
        this.program = this.gl.createProgram();
        // Attach the shaders to this program
        this.gl.attachShader( this.program, this.vertexShader );
        this.gl.attachShader( this.program, this.fragmentShader );
        this.gl.linkProgram( this.program );

        if ( !this.gl.getProgramParameter( this.program, this.gl.LINK_STATUS ) ) {
            console.error( 'Could not initialize shaders' );
        }

        // Use this program instance
        this.gl.useProgram( this.program );
        // We attach the location of these shader values to the program instance
        // for easy access later in the code
        this.program.aVertexPosition = this.gl.getAttribLocation( this.program, paras.aVertexPosition );
        this.program.aColor = this.gl.getAttribLocation( this.program, paras.aColor );
        // this.program.aBary = this.gl.getAttribLocation( this.program, 'bary' );
        // this.program.uTheta = this.gl.getUniformLocation( this.program, 'theta' );

        // console.log( this.gl.TRIANGLE_FAN, this.gl.LINE_STRIP, this.gl.LINE_LOOP ); // 6 3 2
    }

    /**
     * Given an id, extract the content's of a shader script
     * from the DOM and return the compiled shader
     * */

    #getShader( id ) {
        const script = document.getElementById( id );
        const shaderString = script.text.trim();

        // Assign shader depending on the type of shader
        let shader;
        if ( script.type === 'x-shader/x-vertex' ) {
            shader = this.gl.createShader( this.gl.VERTEX_SHADER );
        } else if ( script.type === 'x-shader/x-fragment' ) {
            shader = this.gl.createShader( this.gl.FRAGMENT_SHADER );
        } else {
            return null;
        }

        // Compile the shader using the supplied shader code
        this.gl.shaderSource( shader, shaderString );
        this.gl.compileShader( shader );

        // Ensure the shader is valid
        if ( !this.gl.getShaderParameter( shader, this.gl.COMPILE_STATUS ) ) {
            console.error( this.gl.getShaderInfoLog( shader ) );
            return null;
        }

        return shader;
    }

    reset() {
        // Clear the scene
        this.gl.clear( this.gl.COLOR_BUFFER_BIT | this.gl.DEPTH_BUFFER_BIT );
        this.gl.viewport( 0, 0, this.gl.canvas.width, this.gl.canvas.height );

        // Clean
        this.gl.bindVertexArray( null );
        this.gl.bindBuffer( this.gl.ARRAY_BUFFER, null );
    }

    /**
     * @param {[Float32Array]} points points
     * @param {[Float32Array]} colors colors
     * @param {[Number]} drawingTypes drawingTypes
     */

    draw( points, colors, drawingTypes ) {
        // console.log( points, colors, drawingTypes );
        console.assert( points.length === colors.length, points, colors );
        console.assert( points.length === drawingTypes.length, points, drawingTypes );

        // Clear the scene
        this.gl.clear( this.gl.COLOR_BUFFER_BIT | this.gl.DEPTH_BUFFER_BIT );
        this.gl.viewport( 0, 0, this.gl.canvas.width, this.gl.canvas.height );

        let bufferPos = null;
        let bufferColor = null;
        for ( let i = 0; i < points.length; i++ ) {
            // console.log( points[i], colors[i] );
            bufferPos = this.gl.createBuffer();
            // vertex buffer
            // bind and activate buffer
            this.gl.bindBuffer( this.gl.ARRAY_BUFFER, bufferPos );
            // pass data array into the buffer
            this.gl.bufferData( this.gl.ARRAY_BUFFER, points[ i ], this.gl.STATIC_DRAW );
            // define how to interpret data
            this.gl.vertexAttribPointer( this.program.aVertexPosition, 2, this.gl.FLOAT, false, 0, 0 );
            // enable data passing
            this.gl.enableVertexAttribArray( this.program.aVertexPosition );

            // color buffer
            bufferColor = this.gl.createBuffer();
            this.gl.bindBuffer( this.gl.ARRAY_BUFFER, bufferColor );
            this.gl.bufferData( this.gl.ARRAY_BUFFER, colors[ i ], this.gl.STATIC_DRAW );
            this.gl.vertexAttribPointer( this.program.aColor, 4, this.gl.FLOAT, false, 0, 0 );
            this.gl.enableVertexAttribArray( this.program.aColor );

            // starting drawing
            // console.log( points.length );
            let n = -1;
            switch ( drawingTypes[ i ] ) {
                case Program.TRIANGLE_FAN:
                    n = 38;
                    break;
                case Program.LINE_LOOP:
                case Program.LINE_STRIP:
                    n = points[ i ].length / 2
            }
            console.assert( drawingTypes[ i ] === 6 || ( drawingTypes[ i ] === 3 || drawingTypes[ i ] === 2 && points[ i ].length % 2 === 0 ), drawingTypes );
            console.assert( ( drawingTypes[ i ] === 3 || drawingTypes[ i ] === 2 ) || ( drawingTypes[ i ] === 6 && points[ i ].length === 76 ), drawingTypes[ i ], points[ i ] );
            // console.log( drawingTypes[ i ] !== 2 || ( drawingTypes[ i ] === 6 && points[ i ].length === 38 ) );
            // console.log( drawingTypes[ i ], n );
            this.gl.drawArrays( drawingTypes[ i ], 0, n );

            // Clean
            this.gl.bindVertexArray( null );
            this.gl.bindBuffer( this.gl.ARRAY_BUFFER, null );
            // this.gl.bindBuffer( this.gl.ELEMENT_ARRAY_BUFFER, null );
            // break;
        }
    }
}