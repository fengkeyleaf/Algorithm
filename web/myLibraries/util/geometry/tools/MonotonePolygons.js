"use strict"

/*
 * MonotonePolygons.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import MyMath from "../../../lang/MyMath.js";
import MonotoneVertex from "../DCEL/MonotoneVertex.js";
import Triangles from "./Triangles.js";
import Vectors from "./Vectors.js";
import Vector from "../elements/point/Vector.js";
import HalfEdge from "../DCEL/HalfEdge.js";
import Line from "../elements/line/Line.js";
import EventEdge from "../elements/point/EventEdge.js";
import DCEL from "../DCEL/DCEL.js";
import Stack from "../../Stack.js";
import Polygons from "./Polygons.js";
import StatusRBTree from "./StatusRBTree.js";
import Vertex from "../DCEL/Vertex.js";
import Drawer from "../../../GUI/geometry/Drawer.js";
import SnapShot from "../../../GUI/geometry/SnapShot.js";
import Main from "../../../../finalProject/JavaScript/Main.js";

/**
 * This class consists exclusively of static methods
 * that related to MonotonePolygons
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/tools/MonotonePolygons.java>MonotonePolygons</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 */

export default class MonotonePolygons {

    /**
     * @param {HalfEdge} edge
     */

    static isCorner( edge ) {
        // topmost
        if ( MyMath.doubleCompare( edge.prev.origin.y, edge.origin.y ) < 0 &&
            MyMath.doubleCompare( edge.origin.y, edge.next.origin.y ) > 0 )
            return true;

        // bottommost
        if ( MyMath.doubleCompare( edge.prev.origin.y, edge.origin.y ) > 0 &&
            MyMath.doubleCompare( edge.origin.y, edge.next.origin.y ) < 0 )
            return true;

        return false;
    }

    /**
     * the passed in polygon is monotone?
     *
     * @param {Face} face
     */

    // TODO: 7/8/2021 not full tested
    static isMonotonePolygon( face ) {
        let cornerCount = 2;
        let edge = face.outComponent;
        do {
            if ( this.isCorner( edge ) ) cornerCount--;
            // the # of turn a monotone polygon has
            // is at most two,
            // i.e. they are at topmost and bottommost
            if ( cornerCount < 0 ) return false;
            console.assert( edge.incidentFace === face );
            edge = edge.next;
        } while ( edge !== face.outComponent );

        return true;
    }

    static SHIFT = 0.01;

    /**
     * handle Horizontal Vertices
     * in partitioning a simple polygon into monotone subpolygons
     *
     * @param {Vertex} pointToBeShifted
     * @param {Vertex} base
     */

    static handleHorizontalVertices( pointToBeShifted, base ) {
        let upShiftedPoint = new MonotoneVertex(
            pointToBeShifted.x, pointToBeShifted.y + MonotonePolygons.SHIFT );
        let downShiftedPoint = new MonotoneVertex(
            pointToBeShifted.x, pointToBeShifted.y - MonotonePolygons.SHIFT );

        // make the horizontal line tilted a bit upwards
        if ( Triangles.toLeftRigorously( base, pointToBeShifted, upShiftedPoint ) ) {
            console.assert( !Triangles.toLeftRigorously( base, pointToBeShifted, downShiftedPoint ) );
            return upShiftedPoint;
        }

        // make the horizontal line tilted a bit downwards
        console.assert( !Triangles.toLeftRigorously( base, pointToBeShifted, upShiftedPoint ) );
        console.assert( Triangles.toLeftRigorously( base, pointToBeShifted, downShiftedPoint ) );
        return downShiftedPoint;
    }

    /**
     * to left test is the key
     * to determine which type of vertex is
     *
     * @param {HalfEdge} edge
     */

    static __getVertexType( edge ) {
        let base = edge.origin;
        let next = edge.next.origin;
        let prev = edge.prev.origin;

        console.assert( base, edge );
        console.assert( next, next );
        console.assert( prev, prev );
        if ( MyMath.isEqualZero( base.y - next.y ) )
            next = this.handleHorizontalVertices( next, base );
        else if ( MyMath.isEqualZero( base.y - prev.y ) )
            base = this.handleHorizontalVertices( base, prev );

        // if its two neighbors lie below it
        if ( Vectors.isBelow( base, next ) &&
            Vectors.isBelow( base, prev ) ) {
            base = edge.origin;
            // 	if interior angle at v is less than π:
            if ( base.isSplitOrMergeVertex() )
                base.vertexType = MonotoneVertex.VertexType.SPLIT;
            else
                base.vertexType = MonotoneVertex.VertexType.START;

            return;
        }
        // else if two neighbors lie above it
        else if ( Vectors.isAbove( base, next ) &&
            Vectors.isAbove( base, prev ) ) {
            base = edge.origin;
            // 	if less than π:
            if ( base.isSplitOrMergeVertex() )
                base.vertexType = MonotoneVertex.VertexType.MERGE;
            else
                base.vertexType = MonotoneVertex.VertexType.END;

            return;
        }

        // regular vertex
        let rightShiftedPoint = new Vector( base.x + MonotonePolygons.SHIFT, base.y, -1 );
        let leftShiftedPoint = new Vector( base.x - MonotonePolygons.SHIFT, base.y, -1 );
        // left regular vertex,
        // if the interior of P lies to the right of vi
        if ( Polygons.isInsidePolygon( rightShiftedPoint, base, prev, next ) ) {
            console.assert( !Polygons.isInsidePolygon( leftShiftedPoint, base, prev, next ), base );
            base = edge.origin;
            base.vertexType = MonotoneVertex.VertexType.REGULAR_LEFT;
        }
            // right regular vertex,
        // if the interior of P lies to the left of vi
        else {
            console.assert( Polygons.isInsidePolygon( leftShiftedPoint, base, prev, next ), base );
            base = edge.origin;
            base.vertexType = MonotoneVertex.VertexType.REGULAR_RIGHT;
        }
    }

    /**
     * get the five Vertex Types:
     * start, end, split, merge, regular(left or right)
     *
     * @param {Face} face
     */

    static getVertexType( face ) {
        if ( face == null ) return;

        let edge = face.outComponent;
        do {
            this.__getVertexType( edge );
            edge = edge.next;
        } while ( edge !== face.outComponent );
    }

    /**
     * handle Regular Vertex
     *
     * @param {MonotoneVertex} vertex
     * @param {StatusRBTree} statusRBTree
     * @param {[Face]} faces
     * @param {SnapShot} snapshot
     */

    static __handleRegularVertex( vertex,
                                  statusRBTree, faces, snapshot ) {
        snapshot.addSubPseIndices( 1 );

        // if the interior of P lies to the right of vi
        if ( vertex.vertexType === MonotoneVertex.VertexType.REGULAR_LEFT ) {
            this.__handleEndVertex( vertex, statusRBTree, faces, snapshot );
            // map to end vertex pse indices
            for ( let i = 1; i < snapshot.pseudocode.indicesSub.length; i++ ) {
                snapshot.pseudocode.indicesSub[ i ]++;
            }

            this.__handleStartVertex( vertex, statusRBTree );
            // map to start vertex pse indices
            snapshot.addSubPseIndices( 5 );
            return;
        }

        // else the interior of P lies to the left of vi
        this.__handleMergeVertexCommonPart( vertex, statusRBTree, faces, snapshot );
        // map to merge vertex pse indices( common part only )
        for ( let i = 1; i < snapshot.pseudocode.indicesSub.length; i++ ) {
            snapshot.pseudocode.indicesSub[ i ] += 2;
        }
    }

    /**
     * common part of code for handling merge vertex,
     * i.e. we could reuse it for other cases
     *
     * @param {MonotoneVertex} vertex
     * @param {StatusRBTree} statusRBTree
     * @param {[Face]} faces
     * @param {SnapShot} snapshot
     */

    static __handleMergeVertexCommonPart( vertex,
                                          statusRBTree, faces, snapshot ) {
        snapshot.addSubPseIndices( 4, 5 );

        // Search in T to find the edge e j directly left of vi.
        let left = statusRBTree.lowerVal( vertex );
        // if helper(ej) is a merge vertex
        if ( left.vertex.vertexType === MonotoneVertex.VertexType.MERGE ) {
            console.assert( left.vertex.incidentEdge.incidentFace === vertex.incidentEdge.incidentFace );
            // 	then Insert the diagonal connecting vi to helper(e j) in D.
            HalfEdge.connectHelper( left.vertex, vertex, faces );

            snapshot.addSubPseIndices( 6 );
        }

        // helper(e j)←vi
        left.vertex = vertex;

        snapshot.addSubPseIndices( 7 );
    }

    /**
     * handle Merge Vertex
     *
     * @param {MonotoneVertex} vertex
     * @param {StatusRBTree} statusRBTree
     * @param {[Face]} faces
     * @param {SnapShot} snapshot
     */

    static __handleMergeVertex( vertex,
                                statusRBTree, faces, snapshot ) {
        this.__handleEndVertex( vertex, statusRBTree, faces, snapshot );
        this.__handleMergeVertexCommonPart( vertex, statusRBTree, faces, snapshot );
    }

    /**
     * handle Split Vertex
     *
     * @param {MonotoneVertex} vertex
     * @param {StatusRBTree} statusRBTree
     * @param {[Face]} faces
     */

    static __handleSplitVertex( vertex, statusRBTree, faces ) {
        // Search in T to find the edge e j directly left of vi.
        let left = statusRBTree.lowerVal( vertex );
        console.assert( left !== null, vertex );
        // Insert the diagonal connecting vi to helper(ej) in D.
        HalfEdge.connectHelper( left.vertex, vertex, faces );
        // helper(e j)←vi
        left.vertex = vertex;
        // Insert ei in T and set helper(ei) to vi.
        this.__handleStartVertex( vertex, statusRBTree );
    }

    /**
     * handle End Vertex
     *
     * @param {MonotoneVertex} vertex
     * @param {StatusRBTree} statusRBTree
     * @param {[Face]} faces
     * @param {SnapShot} snapshot
     */

    static __handleEndVertex( vertex, statusRBTree,
                              faces, snapshot ) {
        snapshot.addSubPseIndices( 1 );

        // Delete ei−1 from T.
        // vertex must be on the line of ei-1, as the lower endpoint
        let prevEvent = statusRBTree.deleteAndGetVal( vertex );
        console.assert( prevEvent != null, vertex );
        // if helper(ei−1) is a merge vertex
        // prevEvent != null && // orthogonal vertex may have null as prevEvent
        if ( prevEvent.vertex.vertexType === MonotoneVertex.VertexType.MERGE ) {
            console.assert( prevEvent.vertex.incidentEdge.incidentFace === vertex.incidentEdge.incidentFace, prevEvent.vertex + " " + vertex );
            // then Insert the diagonal connecting vi to helper(ei−1) in D.
            HalfEdge.connectHelper( prevEvent.vertex, vertex, faces );

            snapshot.addSubPseIndices( 2 );
        }

        snapshot.addSubPseIndices( 3 );
    }

    /**
     * handle Start Vertex
     *
     * @param {MonotoneVertex} vertex
     * @param {StatusRBTree} statusRBTree
     */

    static __handleStartVertex( vertex, statusRBTree ) {
        // Insert ei in T and set helper(ei) to vi.
        console.assert( vertex.incidentEdge.origin === vertex );
        let line = new Line( vertex, vertex.incidentEdge.next.origin );
        // line, vertex, vertex.ID
        let event = new EventEdge( { shape: line, x: vertex.x, y: vertex.y, vertex: vertex, ID: vertex.ID } );
        statusRBTree.put( event );
    }

    /**
     * partitioning a simple polygon into monotone subpolygons
     *
     * Reference resource:
     * @see <a href=http://www.cs.uu.nl/geobook/>portal gate</a>
     *
     * @param {[MonotoneVertex]} vertices
     * @return faces newly partitioned faces (Monotone polygons) by adding internal diagonals
     */

    // TODO: 7/14/2021 not support complex polygons
    // Input. A simple polygon P stored in a doubly-connected edge list D.
    // Output. A partitioning of P into monotone subpolygons, stored in D.
    static makeMonotone( vertices ) {
        let faces = [];
        // Construct a priority queue Q on the vertices of P,
        // using their y-coordinates as priority.
        // If two points have the same y-coordinate,
        // the one with smaller x-coordinate has higher priority.
        let priorityQueue = [ ...vertices ];
        priorityQueue.sort( Vectors.sortByY );

        // Initialize an empty binary search tree T.
        let statusRBTree = new StatusRBTree( Vectors.sortByX );

        // while Q is not empty
        for ( let i = priorityQueue.length - 1; i >= 0; i-- ) {
            // 	do Remove the vertex vi with the highest priority from Q.
            let vertex = priorityQueue[ i ];

            // add a snapshot for this vertex in partitioning monotone polygons
            let snapshot = SnapShot.addSnapshot( vertex );
            let main = Main.main;
            // load main pseudocode animation
            snapshot.setMainPse( main.pseudocodeEles.monoPse );
            snapshot.addMainPseIndices( 3, 4, 5 );

            // Call the appropriate procedure to handle the vertex, depending on its type.
            switch ( vertex.vertexType ) {
                case MonotoneVertex.VertexType.START:
                    this.__handleStartVertex( vertex, statusRBTree );
                    // and load sub pseudocode animation for start vertex
                    snapshot.setSubPse( main.pseudocodeEles.startV );
                    snapshot.addSubPseIndices( 1 );
                    break;
                case MonotoneVertex.VertexType.SPLIT:
                    this.__handleSplitVertex( vertex, statusRBTree, faces );
                    // and load sub pseudocode animation for split vertex
                    snapshot.setSubPse( main.pseudocodeEles.splitV );
                    snapshot.addSubPseIndices( 1, 2, 3, 4 );
                    break;
                case MonotoneVertex.VertexType.END:
                    this.__handleEndVertex( vertex, statusRBTree, faces, snapshot );
                    // and load sub pseudocode animation for end vertex
                    snapshot.setSubPse( main.pseudocodeEles.endV );
                    break;
                case MonotoneVertex.VertexType.MERGE:
                    this.__handleMergeVertex( vertex, statusRBTree, faces, snapshot );
                    // and load sub pseudocode animation for merge vertex
                    snapshot.setSubPse( main.pseudocodeEles.mergeV );
                    break;
                case MonotoneVertex.VertexType.REGULAR_LEFT:
                case MonotoneVertex.VertexType.REGULAR_RIGHT:
                    this.__handleRegularVertex( vertex, statusRBTree, faces, snapshot );
                    // and load sub pseudocode animation for regular vertex
                    snapshot.setSubPse( main.pseudocodeEles.regularV );
                    break;
                default:
                    console.assert( false );
            }
        }

        return faces;
    }

    /**
     * triangulating monotone polygons
     *
     * Reference resource:
     * @see <a href=http://www.cs.uu.nl/geobook/>portal gate</a>
     *
     * @param {Face} monotonePolygon
     * @return faces newly partitioned faces (triangles) by adding internal diagonals
     */

    // Input. A strictly y-monotone polygon P stored in a doubly-connected edge list D.
    // Output. A triangulation of P stored in the doubly-connected edge list D.
    static triangulateMonotonePolygon( monotonePolygon ) {
        const faces = [];
        if ( monotonePolygon == null ) return faces;

        // get drawing data for this monotone polygon
        let { points, colors } = Drawer.drawPolygons( [ monotonePolygon ], [ Drawer.grey ] );

        // Merge the vertices on the left chain and the vertices on the right chain of P
        // into one sequence, sorted on decreasing y-coordinate. If two vertices have
        // the same y-coordinate, then the leftmost one comes first. Let u1, . . . ,un
        // denote the sorted sequence.
        let sortedEdges = DCEL.walkAroundEdgeFace( monotonePolygon );
        sortedEdges.sort( HalfEdge.sort );
        let len = sortedEdges.length;
        // Initialize an empty stack S, and push u1 and u2 onto it.
        // and add the first two snapshots,
        let stack = new Stack();
        // push u1
        let snapshots = [];
        snapshots.push( SnapShot.addSnapshot( sortedEdges[ len - 1 ].origin ).addStack( stack.array ) );
        stack.push( sortedEdges[ len - 1 ] );
        // load main pseudocode animation
        snapshots.getLast().setMainPse( Main.main.pseudocodeEles.triPse );
        snapshots.getLast().addMainPseIndices( 1, 2 );
        snapshots.getLast().setIsANewMono( true );
        // push u2
        snapshots.push( SnapShot.addSnapshot( sortedEdges[ len - 2 ].origin ).addStack( stack.array ) );
        snapshots.getLast().setMainPse( Main.main.pseudocodeEles.triPse );
        snapshots.getLast().addMainPseIndices( 2 );
        stack.push( sortedEdges[ len - 2 ] );

        // for j←3 to n−1
        let edge = null;
        for ( let i = len - 3; i > 0; i-- ) {
            edge = sortedEdges[ i ];
            // add snapshot for this vertex
            snapshots.push( SnapShot.addSnapshot( edge.origin ).addStack( stack.array ) );
            snapshots.getLast().setMainPse( Main.main.pseudocodeEles.triPse );
            snapshots.getLast().addMainPseIndices( 3, 4 );

            // 	do if uj and the vertex on top of S are on different chains
            if ( edge.isOnTheDifferentChain( stack.peek() ) ) {
                // then Pop all vertices from S.
                // Insert into D a diagonal from uj to each popped vertex,
                // except the last one.
                let prev = stack.peek();
                while ( stack.size() > 1 ) {
                    HalfEdge.connectHelper( edge.origin, stack.pop().origin, faces );
                }
                stack.pop();

                // Push uj−1 and uj onto S.
                stack.push( prev );
                stack.push( edge );

                // load triangulation pseudocode animation
                snapshots.getLast().addMainPseIndices( 5, 6, 7 );
            } else {
                // else Pop one vertex from S.
                let prev = stack.pop();

                // Pop the other vertices from S as long as the diagonals from
                // uj to them are inside P. Insert these diagonals into D.
                while ( !stack.isEmpty() ) {
                    let isInside = false;
                    // if uj is on the left chain,
                    // the counter-clock-wise ordering of vertices is
                    // from stack.peek() to prev, to uj.
                    if ( edge.origin.isLeftChainVertex ===
                        MonotoneVertex.LEFT_CHAIN_VERTEX )
                        isInside = Triangles.toLeftRigorously( stack.peek().origin,
                            prev.origin, edge.origin );
                        // if uj is on the right chain,
                        // the counter-clock-wise ordering of vertices is
                    // from uj to prev, to stack.peek().
                    else
                        isInside = Triangles.toLeftRigorously( edge.origin,
                            prev.origin, stack.peek().origin );

                    // the diagonal to be added is inside the polygon?
                    if ( isInside ) {
                        // yes, add it to D.
                        HalfEdge.connectHelper( edge.origin,
                            ( prev = stack.pop() ).origin, faces );
                    }
                    // no, check the next vertex
                    else break;
                }
                // Push the last vertex that has been popped back onto S.
                stack.push( prev );
                // Push uj onto S.
                stack.push( edge );

                // load triangulation pseudocode animation
                snapshots.getLast().addMainPseIndices( 8, 9, 10 );
            }
        }

        // Add diagonals from un to all stack vertices
        // except the first and the last one.
        edge = sortedEdges[ 0 ];

        // add last snapshot for the last vertex
        snapshots.push( SnapShot.addSnapshot( edge.origin ).addStack( stack.array ) );
        // SnapShot.addSnapshot( edge.origin ).addStack( stack.array ).isEndOfThisMonotone = true;

        stack.pop();
        while ( stack.size() > 1 ) {
            HalfEdge.connectHelper( edge.origin,
                stack.pop().origin, faces );
        }
        // load triangulation pseudocode animation
        snapshots.getLast().setMainPse( Main.main.pseudocodeEles.triPse );
        snapshots.getLast().addMainPseIndices( 11 );

        // load the drawing data of this monotone polygon for each snapshot
        snapshots.forEach( s => {
            s.addMonotone( new Float32Array( points ), new Float32Array( colors ) );
        } );

        return faces;
    }

    /**
     * determine which chain a vertex is on
     *
     * @param {HalfEdge} topmost
     * @param {HalfEdge} bottommost
     */

    static __findLeftAndRightChainVertices( topmost,
                                            bottommost ) {
        let edge = topmost;
        // vertices on the left chain, including the topmost
        do {
            edge.origin.isLeftChainVertex =
                MonotoneVertex.LEFT_CHAIN_VERTEX;
            edge = edge.next;
        } while ( edge !== bottommost );

        // vertices on the right chain, including the bottommost
        do {
            edge.origin.isLeftChainVertex =
                MonotoneVertex.RIGHT_CHAIN_VERTEX;
            edge = edge.next;
        } while ( edge !== topmost );
    }

    /**
     * determine which chain a vertex is on,
     * first we need to find the topmost and bottommost
     *
     * topMost -> L; bottom -> R
     *
     * @param {Face} monotonePolygon
     */

    static findLeftAndRightChainVertices( monotonePolygon ) {
        let topmost = null;
        let bottommost = null;
        // Number.MIN_VALUE is the closest number to zero
        let maxY = -Number.MAX_VALUE;
        let minY = Number.MAX_VALUE;

        console.assert( monotonePolygon.innerComponents.isEmpty() );
        let edge = monotonePolygon.outComponent;

        // find the topmost and bottommost,
        // by visiting all vertices of the polygon
        do {
            let vertex = edge.origin;
            if ( vertex.y > maxY ) {
                maxY = vertex.y;
                topmost = edge;
            }

            if ( vertex.y < minY ) {
                minY = vertex.y;
                bottommost = edge;
            }

            edge = edge.next;
        } while ( edge !== monotonePolygon.outComponent );

        console.assert( topmost != null );
        console.assert( bottommost != null );
        console.assert( topmost !== bottommost );
        this.__findLeftAndRightChainVertices( topmost, bottommost );
    }

    /**
     * triangulate each monotone polygon
     *
     * @param {[Face]} monotonePolygons
     */

    static preprocessMonotonePolygon( monotonePolygons ) {
        let triangles = [];
        if ( monotonePolygons == null ||
            monotonePolygons.isEmpty() ) return triangles;

        // for each face, also a monotone polygon,
        // walk around to get all its vertices
        for ( let monotonePolygon of monotonePolygons ) {
            // skip the infinite face
            if ( !monotonePolygon.innerComponents.isEmpty() ) continue;
            // and determine on which chain it is
            this.findLeftAndRightChainVertices( monotonePolygon );
            // and triangulate the polygon
            triangles = triangles.concat( this.triangulateMonotonePolygon( monotonePolygon ) );
        }

        return triangles;
    }
}
