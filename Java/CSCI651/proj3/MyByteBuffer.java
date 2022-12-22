package CSCI651.proj3;

/*
 * MyByteBuffer.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/13/2022$
 */

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Scanner;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/ByteBuffer.html
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/ByteBuffer.html#array()
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/ByteBuffer.html#putShort(short)
// https://blog.csdn.net/mrliuzhao/article/details/89453082
public class MyByteBuffer {

    private static
    void testBitSet() {
        BitSet set = new BitSet( 10 ); //10 bits set

        //set() 设为true
        set.set( 0 );
        set.set( 1 );
        set.set( 5 );
        // {0, 1, 5}
        System.out.println( set ); // 应该是列出值为true的那些位的坐标！

        // 8 bit >> 1 byte,  就是说截取8位，转成byte。 就是0010 0011 >>
        System.out.println( Arrays.toString( set.toByteArray() ) ); // [35]
        // 64 bit >> 1 long
        System.out.println( Arrays.toString( set.toLongArray() ) ); // [35]
    }

    private static
    void testByteBuffer() {
        ByteBuffer b = ByteBuffer.allocate( 256 );
        b.putInt( 1 );
        b.putChar( 'x' );
        b.flip();
        System.out.println( b.getInt() );
        System.out.println( b.getChar() );
        System.out.println( Arrays.toString( b.array() ) );

        System.out.println( Integer.toBinaryString( 20 ) );
    }

    private static
    void testScanner() {
        try ( Scanner s = new Scanner( System.in ) ) {
            System.out.println( "Input:" );
            String i = s.next();
            while ( !i.equals( "q" ) ) {
                System.out.println( i );
                i = s.next();
            }
        }
    }

    public static void main( String[] args ) {
//        testBitSet();
        testScanner();
    }
}
