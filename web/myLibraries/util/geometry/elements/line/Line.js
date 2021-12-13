"use strict"

/*
 * Line.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import Vectors from "../../tools/Vectors.js";
import MyMath from "../../../../lang/MyMath.js";

/**
 * Data structure of Line
 *
 * The source code in java is from my own github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/util/geometry/elements/line/Line.java>Line</a>
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Line {

    /**
     * Constructs to create an instance of Line
     *
     * @param {Vector} startPoint
     * @param {Vector} endPoint
     * */

    constructor( startPoint, endPoint ) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;

        this.isVerticalVar = this.isVertical();
        this.verticalX = this.isVertical ? startPoint.x : 0;
        this.isHorizontalVar = this.isHorizontal();
        this.horizontalY = this.isHorizontal ? startPoint.y : 0;
        console.assert( !( this.isVerticalVar & this.isHorizontalVar ) );

        this.dx = startPoint.x - endPoint.x;
        this.dy = startPoint.y - endPoint.y;

        this.getLineStandardFunction();
    }


    /**
     * get Vector of Line
     * */

    getVector() {
        return this.endPoint.subtract( this.startPoint );
    }

    /**
     * get projecting point of the point
     *
     * @param {Vector} point
     * */

    project( point ) {
        let base = this.getVector();
        let ratio = Vectors.dot( point.subtract( this.startPoint ), base ) /
            base.normWithoutRadical();
        return this.startPoint.add( base.multiply( ratio ) );
    }

    /**
     * get the linear distance from the point
     *
     * @param {Vector} point
     * */

    distance( point ) {
        let vector = this.getVector();
        let area = Vectors.cross( vector, point.subtract( this.startPoint ) );
        console.assert( !MyMath.isEqualZero( vector.norm() ) );
        return Math.abs( area / vector.norm() );
    }

    /**
     * B * y + A * x = C
     * */

    getLineStandardFunction() {
        this.A = this.startPoint.y - this.endPoint.y;
        this.B = this.endPoint.x - this.startPoint.x;
        this.C = this.B * this.startPoint.y + this.A * this.startPoint.x;
    }


    updateY( x ) {
        console.assert( this.B !== 0, this );
        return ( this.C - x * this.A ) / this.B;
    }

    updateX( y ) {
        console.assert( this.A !== 0, this );
        return ( this.C - y * this.B ) / this.A;
    }

    /**
     * update Y And X, for line
     *
     * @param {Vector} target
     * @param {Vector} update
     * @param {Boolean} isUpdatingByX     update x and y based which, x or y?
     *                          true -> x; false -> y
     */

    updateYAndX( target, update, isUpdatingByX ) {
        // vertical line
        if ( this.isVerticalVar ) {
            // given x == the x of the line?
            if ( MyMath.isEqual( update.x, this.verticalX ) ) {
                // yes, update Y
                console.assert( target.x === this.verticalX );
                target.y = update.y;
            }
            return;
        }
        // Horizontal line
        else if ( this.isHorizontalVar ) {
            // given y == the y of the line?
            if ( MyMath.isEqual( update.y, this.horizontalY ) ) {
                // yes, update x
                console.assert( target.y === this.horizontalY );
                target.x = update.x;
            }
            return;
        }

        // neither vertical nor horizontal,
        // update y based on given x
        if ( isUpdatingByX ) target.setXAndY( update.x, this.updateY( update.x ) );
        else target.setXAndY( this.updateX( update.y ), update.y );
    }

    /**
     * update the target point's x and y
     * based on the given x, using this line to calculate
     *
     * @param {Vector} target
     * @param {Number} x
     * */

    updateYAndXByX( target, x ) {
        if ( this.isVerticalVar ) return;
        else if ( this.isHorizontalVar ) {
            target.x = x;
            return;
        }

        target.setXAndY( x, this.updateY( x ) );
    }


    outOfRangeX( x ) {
        let xCoordinates = new Array( 2 );
        xCoordinates[ 0 ] = this.startPoint.x;
        xCoordinates[ 1 ] = this.endPoint.x;
        xCoordinates.sort( MyMath.doubleCompare );

        return x < xCoordinates[ 0 ] || x > xCoordinates[ 1 ];
    }

    outOfRangeY( y ) {
        let yCoordinates = new Array( 2 );
        yCoordinates[ 0 ] = this.startPoint.y;
        yCoordinates[ 1 ] = this.endPoint.y;
        yCoordinates.sort( MyMath.doubleCompare );

        return y < yCoordinates[ 0 ] || y > yCoordinates[ 1 ];
    }

    /**
     * the point is out of the range of x and y of this segment?
     *  but of a line, this method is useless
     *
     *  @param {Vector} vector
     * */

    outOfRange( vector ) {
        return this.outOfRangeX( vector.x ) || this.outOfRangeY( vector.y );
    }


    isVertical() {
        return MyMath.isEqual( this.startPoint.x - this.endPoint.x, 0 );
    }

    isHorizontal() {
        return MyMath.isEqual( this.startPoint.y - this.endPoint.y, 0 );
    }

    toString() {
        return this.startPoint + "<->" + this.endPoint;
    }
}
