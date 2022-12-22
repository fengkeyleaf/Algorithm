package com.fengkeyleaf.lang;

/*
 * TestMath.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/6/2022$
 */

/**
 * Test Math
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

final class TestMath {

    public static
    void main( String[] args ) {
        /*System.out.println("TestPointLoction factorial ------------>");
        System.out.println( factorial( 0 ) );
        System.out.println( factorial( 1 ) );
        System.out.println( factorial( 2 ) );
        System.out.println( factorial( 3 ) );
        System.out.println( factorial( 4 ) );
        System.out.println( factorial( 5 ) );

        System.out.println("\nTestPointLoction Arrangement ------------>");
        System.out.println( Arrangement( 3, 0 ) );
        System.out.println( Arrangement( 3, 1 ) );
        System.out.println( Arrangement( 3, 2 ) );
        System.out.println( Arrangement( 3, 3 ) );
//        System.out.println( Arrangement( 3, 4 ) ); // error

        System.out.println("\nTestPointLoction Combination ------------>");
        System.out.println( Combination( 3, 0 ) );
        System.out.println( Combination( 3, 1 ) );
        System.out.println( Combination( 3, 2 ) );
        System.out.println( Combination( 3, 3 ) );
//        System.out.println( Combination( 3, 4 ) ); // error*/

//        System.out.println("\ndistance From Origin 3D Without Radical------------>");
//        System.out.println( MyMath.distanceFromOrigin3DWithoutRadical( 0, 5, 0 ) );
//        System.out.println( MyMath.distanceFromOrigin3DWithoutRadical( 2, -2, 4 ) );
//        System.out.println( MyMath.distanceFromOrigin3DWithoutRadical( 5, -5, 1 ) );
//        System.out.println( MyMath.distanceFromOrigin3DWithoutRadical( -4, -3, -2 ) );
//        System.out.println( MyMath.distanceFromOrigin3DWithoutRadical( 3, 0, -4 ) );
//
//        System.out.println("\nTest Base Conversion ------------>");
//        System.out.println("\nBase 10 ------------>");
//        System.out.println( convertNumberToOtherBase( 0, 10 ) );
//        System.out.println( convertNumberToOtherBase( 5, 10 ) );
//        System.out.println( convertNumberToOtherBase( 9, 10 ) );
//        System.out.println( convertNumberToOtherBase( 12, 10 ) );
//        System.out.println( convertNumberToOtherBase( 123, 10 ) );
//        System.out.println( convertNumberToOtherBase( 12345, 10 ) );
//        System.out.println( convertNumberToOtherBase( 1234567890, 10 ) );
//
//        System.out.println("\nBase 16 ------------>");
//        System.out.println( 0 + ": " + convertNumberToOtherBase( 0, 16 ) + " - " + Integer.toHexString( 0 ) );
//        System.out.println( 1 + ": " + convertNumberToOtherBase( 1, 16 ) + " - " + Integer.toHexString( 1 ) );
//        System.out.println( 6 + ": " + convertNumberToOtherBase( 6, 16 ) + " - " + Integer.toHexString( 6 ) );
//        System.out.println( 12 + ": " + convertNumberToOtherBase( 12, 16 ) + " - " + Integer.toHexString( 12 ) );
//        System.out.println( 15 + ": " + convertNumberToOtherBase( 15, 16 ) + " - " + Integer.toHexString( 15 ) );
//        System.out.println( 16 + ": " + convertNumberToOtherBase( 16, 16 ) + " - " + Integer.toHexString( 16 ) );
//        System.out.println( 123 + ": " + convertNumberToOtherBase( 123, 16 ) + " - " + Integer.toHexString( 123 ) );
//        System.out.println( 1234 + ": " + convertNumberToOtherBase( 1234, 16 ) + " - " + Integer.toHexString( 1234 ) );
//        System.out.println( 123456 + ": " + convertNumberToOtherBase( 123456, 16 ) + " - " + Integer.toHexString( 123456 ) );
//        System.out.println( 1234567890 + ": " + convertNumberToOtherBase( 1234567890, 16 ) + " - " + Integer.toHexString( 1234567890 ) );
//
//        System.out.println( 127 + ": " + convertNumberToOtherBase( 127, 16 ) + " - " + Integer.toHexString( 127 ) );
//        System.out.println( 307 + ": " + convertNumberToOtherBase( 307, 16 ) + " - " + Integer.toHexString( 307 ) );
//        System.out.println( 127 + ": " + convertNumberToOtherBase( 127, 16 ) + " - " + Integer.toHexString( 124 ) );
//        System.out.println( 223 + ": " + convertNumberToOtherBase( 223, 16 ) + " - " + Integer.toHexString( 223 ) );
//        System.out.println( 772 + ": " + convertNumberToOtherBase( 772, 16 ) + " - " + Integer.toHexString( 772 ) );
//        System.out.println( 532 + ": " + convertNumberToOtherBase( 532, 16 ) + " - " + Integer.toHexString( 532 ) );

//        System.out.println( Math.abs( Math.sqrt( 5 / 9.0 ) - 1 ) );
//        System.out.println( Math.abs( Math.sqrt( 10 / 16.0 ) - 1 ) );

//        System.out.println( entropy( 0.5 ) ); // 1
//        System.out.println( entropy( 1 ) ); // 0
//        System.out.println( gain( 12, 6, 1, 1, 11, 5 ) ); // 0.089

//        System.out.println( gain( 20, 12, 11, 5, 9, 7 ) ); // 0.08034195021346324
//        System.out.println( gain( 20, 12, 10, 6, 10, 6 ) ); // 0.0
//        System.out.println( gain( 20, 12, 10, 4, 10, 8 ) ); // 0.12451124978365313
//        System.out.println( gain( 20, 12, 9, 6, 11, 6 ) ); // 0.011000852817822038
//        System.out.println( entropy( 12 / 20.0 ) );
//        System.out.println( entropy( 6 / 10.0 ) );

//        System.out.println( gain( 10, 4, 6, 2, 4, 2 ) ); // 0.01997309402197489
//        System.out.println( gain( 10, 4, 4, 1, 6, 3 ) ); // 0.0464393446710154
//        System.out.println( gain( 10, 4, 5, 2, 5, 2 ) ); // 0.0
//
//        System.out.println();
//        System.out.println( gain( 10, 8, 5, 3, 5, 5 ) ); // 0.23645279766002802
//        System.out.println( gain( 10, 8, 6, 5, 4, 3 ) ); // 0.007403392114696761
//        System.out.println( gain( 10, 8, 4, 4, 6, 4 ) ); // 0.17095059445466865

//        System.out.println( gain( 4, 1, 3, 1, 1, 0 ) ); // 0.12255624891826566
//        System.out.println( entropy( 0 ) );
//        System.out.println( myLog( 0, 2 ) );
//        System.out.println( gain( 6, 3, 2, 1, 4, 2 ) ); // 0.0

        System.out.println( MyMath.gain( 5, 3, 3, 3, 2, 0 ) ); // 0.9709505944546686
        System.out.println( MyMath.gain( 4, 1, 3, 1, 1, 0 ) ); // 0.12255624891826566
        System.out.println( MyMath.gain( 6, 3, 3, 1, 3, 2 ) ); // 0.08170416594551044
        System.out.println( MyMath.gain( 6, 3, 2, 1, 4, 2 ) ); // 0

        System.out.println( MyMath.gain( 3, 1, 0, 0, 3, 1 ) ); // 0
        System.out.println( MyMath.gain( 3, 2, 2, 1, 1, 1 ) ); // 0.2516291673878229
    }
}
