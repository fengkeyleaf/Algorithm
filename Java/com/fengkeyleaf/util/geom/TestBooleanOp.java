package com.fengkeyleaf.util.geom;

/*
 * TestBooleanOp.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/3/2022$
 */

/**
 * Class to test algorithms related to Boolean operations.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

final class TestBooleanOp {

    enum Type {
        INTERSECTION,
        UNION,
        DIFFERENCE
    }

    static
    void testBoolean1( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay1();

        int idx1 = 1, idx2 = 0;
        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean2( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay2();

        int idx1 = 1, idx2 = 0;
//        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean3( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay3();

        int idx1 = 1, idx2 = 0;
        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean4( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay4();

        int idx1 = 1, idx2 = 0;
//        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean5( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay5();

        int idx1 = 1, idx2 = 0;
//        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean6( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay6();

        int idx1 = 1, idx2 = 0;
//        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean7( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay7();

        int idx1 = 1, idx2 = 0;
//        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean8( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay8();

        int idx1 = 1, idx2 = 0;
        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean9( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay9();

        int idx1 = 1, idx2 = 0;
        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean10( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay10();

        int idx1 = 1, idx2 = 0;
        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean11( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay11();

        int idx1 = 1, idx2 = 0;
        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean12( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay12();

        int idx1 = 1, idx2 = 0;
//        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean13( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay13();

        int idx1 = 1, idx2 = 0;
        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean14( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay14();

        int idx1 = 1, idx2 = 0;
//        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean15( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay15();

        int idx1 = 1, idx2 = 0;
        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean16( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay16();

        int idx1 = 1, idx2 = 0;
//        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean17( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay17();

        int idx1 = 1, idx2 = 0;
        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean18( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay18();

        int idx1 = 1, idx2 = 0;
//        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean19( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay19();

        int idx1 = 1, idx2 = 0;
        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void testBoolean20( Type t ) {
        Face[] faces = TestMapOverlay.testMapOverlay20();

        int idx1 = 1, idx2 = 0;
        idx1 = 0; idx2 = 1;
        switch ( t ) {
            case INTERSECTION -> BooleanOperations.intersection( faces[ idx1 ], faces[ idx2 ] );
            case UNION -> BooleanOperations.union( faces[ idx1 ], faces[ idx2 ] );
            case DIFFERENCE -> BooleanOperations.difference( faces[ idx1 ], faces[ idx2 ] );
            default -> { assert false; }
        }
    }

    static
    void test1() {
//        testBoolean1( Type.INTERSECTION );
        testBoolean1( Type.UNION );
//        testBoolean1( Type.DIFFERENCE );
    }

    static
    void test2() {
        testBoolean2( Type.INTERSECTION );
        testBoolean2( Type.UNION );
        testBoolean2( Type.DIFFERENCE );
    }

    static
    void test3() {
//        testBoolean3( Type.INTERSECTION );
//        testBoolean3( Type.UNION );
        testBoolean3( Type.DIFFERENCE );
    }

    static
    void test4() {
//        testBoolean4( Type.INTERSECTION );
//        testBoolean4( Type.UNION );
        testBoolean4( Type.DIFFERENCE );
    }

    static
    void test5() {
//        testBoolean5( Type.INTERSECTION );
        testBoolean5( Type.UNION );
//        testBoolean5( Type.DIFFERENCE );
    }

    static
    void test6() {
//        testBoolean6( Type.INTERSECTION );
        testBoolean6( Type.UNION );
//        testBoolean6( Type.DIFFERENCE );
    }

    static
    void test7() {
//        testBoolean7( Type.INTERSECTION );
//        testBoolean7( Type.UNION );
        testBoolean7( Type.DIFFERENCE );
    }

    static
    void test8() {
//        testBoolean8( Type.INTERSECTION );
//        testBoolean8( Type.UNION );
        testBoolean8( Type.DIFFERENCE );
    }

    static
    void test9() {
        testBoolean9( Type.INTERSECTION );
//        testBoolean9( Type.UNION );
//        testBoolean9( Type.DIFFERENCE );
    }

    static
    void test10() {
        testBoolean10( Type.INTERSECTION );
//        testBoolean10( Type.UNION );
//        testBoolean10( Type.DIFFERENCE );
    }

    static
    void test11() {
//        testBoolean11( Type.INTERSECTION );
//        testBoolean11( Type.UNION );
        testBoolean11( Type.DIFFERENCE );
    }

    static
    void test12() {
//        testBoolean12( Type.INTERSECTION );
//        testBoolean12( Type.UNION );
        testBoolean12( Type.DIFFERENCE );
    }

    static
    void test13() {
//        testBoolean13( Type.INTERSECTION );
        testBoolean13( Type.UNION );
//        testBoolean13( Type.DIFFERENCE );
    }

    static
    void test14() {
//        testBoolean14( Type.INTERSECTION );
//        testBoolean14( Type.UNION );
        testBoolean14( Type.DIFFERENCE );
    }

    static
    void test15() {
        testBoolean15( Type.INTERSECTION );
//        testBoolean15( Type.UNION );
//        testBoolean15( Type.DIFFERENCE );
    }

    static
    void test16() {
//        testBoolean16( Type.INTERSECTION );
//        testBoolean16( Type.UNION );
        testBoolean16( Type.DIFFERENCE );
    }

    static
    void test17() {
        testBoolean17( Type.INTERSECTION );
//        testBoolean17( Type.UNION );
//        testBoolean17( Type.DIFFERENCE );
    }

    static
    void test18() {
        testBoolean18( Type.INTERSECTION );
//        testBoolean18( Type.UNION );
//        testBoolean18( Type.DIFFERENCE );
    }

    static
    void test19() {
//        testBoolean19( Type.INTERSECTION );
//        testBoolean19( Type.UNION );
        testBoolean19( Type.DIFFERENCE );
    }

    static
    void test20() {
        testBoolean20( Type.INTERSECTION );
//        testBoolean20( Type.UNION );
//        testBoolean20( Type.DIFFERENCE );
    }

    public static
    void main( String[] args ) {
//        test1();
//        test2();
//        test3();
//        test4();
//        test5();
//        test6();
//        test7();
//        test8();
//        test9();
//        test10();
//        test11();
//        test12();
//        test13();
//        test14();
//        test15();
//        test16();
//        test17();
//        test18();
//        test19();
        test20();
    }
}
