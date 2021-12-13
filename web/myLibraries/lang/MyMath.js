"use strict"

/*
 * MyMath.java
 *
 * Version:
 *     $1.6$
 *
 * Revisions:
 *     $1.0 convert Number To OtherBase and Arrangement, etc.$
 *     $1.1 added minAmongAl()l on 3/23/2021$
 *     $1.2 added entropy() and gain() for AI on 4/8/2021$
 *     $1.3 add equalFloats() on 4/13/2021$
 *     $1.4 add quadrant() on 5/14/2021$
 *     $1.5 add doubleCompare() and isSameSign() on 7/8/2021$
 *     $1.6 add radians(), degrees() and equalsVector3() on 9/16/2021$
 *     $1.6 add randomInRange(), equalsQuaternion() on 11/16/2021$
 */

/**
 * Math tool box
 *
 * The source code in java is from my now github:
 * @see <a href=https://github.com/fengkeyleaf/Algorithm/blob/main/ComputationalGeometry/ApplicationOfTriangulation/myLibraries/lang/MyMath.java>MyMath</a>
 *
 * @author       Xiaoyu Tongyang or call me sora for short
 */

export default class MyMath {
    static EPSILON = 0.00000001; // 1e-8
    static PI = Math.PI;

    static isUndefined( element ) {
        return typeof ( element ) === "undefined";
    }

    /**
     * get a number lies in range [min, max]
     *
     * @param min minimum
     * @param max maximum
     * @returns {Number} a number lies in range [min, max]
     */

    static randomInRange( min, max ) {
        return Math.random() * ( max - min ) + min;
    }

    /**
     * equals Quaternion, without floating number precision problem
     *
     * @param {Quaternion} q1
     * @param {Quaternion} q2
     * */

    static equalsQuaternion( q1, q2 ) {
        return ( MyMath.doubleCompare( q1.x, q2.x ) === 0 &&
            MyMath.doubleCompare( q1.y, q2.y ) === 0 &&
            MyMath.doubleCompare( q1.z, q2.z ) === 0 ) &&
            MyMath.doubleCompare( q1.w, q2.w ) === 0;
    }

    /**
     * reference source:
     * http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/code/index.htm#add
     *
     * @param {Quaternion} q1
     * @param {Quaternion} q2
     *
     * @return {Quaternion} quaternion
     */

    static addQuaternion( q1, q2 ) {
        let quaternion = new THREE.Quaternion();
        quaternion.set( q1.x + q2.x, q1.y + q2.y, q1.z + q2.z, q1.w + q2.w );
        return quaternion;
    }

    /**
     * @param {Quaternion} q
     * @param {Number} c
     *
     * @return {Quaternion} quaternion
     */

    static multipleConstQuaternion( q, c ) {
        let quaternion = new THREE.Quaternion();
        quaternion.set( q.x * c, q.y * c, q.z * c, q.w * c );
        return quaternion;
    }

    /**
     * equals Vector3, without floating number precision problem
     * */

    static equalsVector3( vector1, vector2 ) {
        return ( MyMath.doubleCompare( vector1.x, vector2.x ) === 0 &&
            MyMath.doubleCompare( vector1.y, vector2.y ) === 0 &&
            MyMath.doubleCompare( vector1.z, vector2.z ) === 0 );
    }

    /**
     * degrees to radians
     * */

    static radians( degrees ) {
        return degrees * ( MyMath.PI / 180 );
    }

    /**
     * radians to degrees
     * */

    static degrees( radians ) {
        return ( radians * 180 ) / MyMath.PI;
    }

    static findMaxDigits( nums ) {
        let maxLen = 0;
        nums.forEach( num => {
            let temp = MyMath.findDigits( num );
            if ( temp > maxLen )
                maxLen = temp;
        } );

        return maxLen;
    }

    static findDigits( num ) {
        let count = 0;
        while ( Math.floor( num ) > 0 ) {
            num /= 10;
            count++;
        }

        return count;
    }

    /**
     * num is positive?
     * */

    static isPositive( num ) {
        return MyMath.doubleCompare( num, 0 ) > 0;
    }

    /**
     * num is negative?
     * */

    static isNegative( num ) {
        return MyMath.doubleCompare( num, 0 ) < 0;
    }

    /**
     * do num1 and num2 have the same sign?
     * */

    static isSameSign( num1, num2 ) {
        if ( MyMath.isEqualZero( num1 ) && MyMath.isEqualZero( num2 ) )
            return true;
        if ( MyMath.isNegative( num1 ) && MyMath.isNegative( num2 ) )
            return true;
        if ( MyMath.isPositive( num1 ) && MyMath.isPositive( num2 ) )
            return true;

        return false;
    }

    /**
     * double's compare with epsilon,
     * also sort array with numbers in ascending order
     * */

    static doubleCompare( num1, num2 ) {
        let different = num1 - num2;
        // num1 is smaller
        if ( different < -MyMath.EPSILON ) return -1;
        // num1 is greater
        else if ( different > MyMath.EPSILON ) return 1;
        // equal
        return 0;
    }

    /**
     * which quadrant the point is at
     * */

    static quadrant( point ) {
        let x = point.x;
        let y = point.y;

        if ( x > 0 && y >= 0 ) return 1;
        if ( x <= 0 && y > 0 ) return 2;
        if ( x < 0 ) return 3;
        if ( y < 0 ) return 4;

        return -1;
    }

    /**
     * check int Overflow
     * */

    static checkOverflow( original, result ) {
        return original < 0 && result > 0 ||
            original > 0 && result < 0;
    }

    /**
     * determine if the floating number equals zero
     * */

    static isEqualZero( num ) {
        return Math.abs( num - 0 ) <= MyMath.EPSILON;
    }

    /**
     * determine if two floating numbers are equal or not
     * */

    static isEqual( num1, num2 ) {
        return Math.abs( num1 - num2 ) <= MyMath.EPSILON;
    }
}

// let nums = [ 1, 2, 3, 4 ];
// console.log( MyMath.findMaxDigits( nums ) );
