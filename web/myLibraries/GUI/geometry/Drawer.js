"use strict"

import DCEL from "../../util/geometry/DCEL/DCEL.js";
import SnapShot from "./SnapShot.js";
import Main from "../../../finalProject/JavaScript/Main.js";

export default class Drawer {
    static black = [ 0.0, 0.0, 0.0, 1.0 ];
    static lightGrey = [ 0.8, 0.8, 0.8, 1 ];
    static grey = [ 0.7, 0.7, 0.7, 1 ];
    static darkGrey = [ 0.3, 0.3, 0.3, 1 ];
    static aqua = [ 0, 1, 1, 1 ];
    static DeepSkyBlue = [ 0, 191 / 255, 1, 1 ];
    static red = [ 1, 0, 51 / 255, 1 ]; // #FF0033


    /**
     * @param {[Number]} points
     * @param {Vertex} vertex
     */

    static addPointByVertex( points, vertex ) {
        // normalized coordinates, mapped to [ -1, 1 ]
        // coor * window / origin, window = 1
        let x = vertex.x / Main.main.originalWidth;
        let y = vertex.y / Main.main.originalHeight;
        points.push( x );
        points.push( y );
    }

    /**
     * @param {[HalfEdge]} edges
     * @param {[Number]} color
     */

    static getPolygonsPointsByEdges( edges, color ) {
        console.assert( edges.length > 2 );
        let points = [];
        let colors = [];
        const black = [ 0.0, 0.0, 0.0, 1.0 ];

        edges.forEach( edge => {
            Drawer.addPointByVertex( points, edge.origin );
            colors = colors.concat( color );
        } );
        Drawer.addPointByVertex( points, edges[ 0 ].origin );
        colors = colors.concat( color );
        return { points, colors };
    }

    // TODO not fixed
    static getPolygonsPointsByVertex( vertices ) {
        console.assert( vertices.length > 2 );
        let points = [];
        vertices.forEach( vertex => {
            Drawer.addPointByVertex( points, vertex );
        } );
        Drawer.addPointByVertex( points, vertices[ 0 ] );
        return points;
    };

    /**
     * @param {[Face]} faces
     * @param {[[Number]]} colorsFace
     */

    static drawPolygons( faces, colorsFace ) {
        let points = [];
        let colors = [];

        console.assert( colorsFace.length === 1 || faces.length === colorsFace.length );
        for ( let i = 0; i < faces.length; i++ ) {
            let face = faces[ i ];
            if ( face.outComponent == null ) continue;

            let edges = DCEL.walkAroundEdgeFace( face );
            let data = Drawer.getPolygonsPointsByEdges( edges, colorsFace.length === 1 ? colorsFace[ 0 ] : colorsFace[ i ] );
            points = points.concat( data.points );
            colors = colors.concat( data.colors );
        }

        return { points, colors };
    }

    /**
     * @param {Vector} vertices
     */

    static drawLines( ...vertices ) {
        let points = [];
        let colors = [];
        const grey = [ 0.7, 0.7, 0.7, 1 ];
        vertices.forEach( vertex => {
            Drawer.addPointByVertex( points, vertex );
            colors = colors.concat( grey );
        } );

        return { points, colors };
    }
}