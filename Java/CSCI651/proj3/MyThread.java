package CSCI651.proj3;

/*
 * MyThread.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/13/2022$
 */

import java.io.IOException;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://www.geeksforgeeks.org/killing-threads-in-java/
public class MyThread {
    static class Timer extends Thread {
        int ti = 5000; // 5 sec

        @Override
        public void run() {
            System.out.println( "Timer is running----->" );

            // Check neighbours every at a period of time.
            try {
                Thread.sleep( ti );
                System.out.println( "Timeout, retransmit." );
            } catch ( InterruptedException e ) {
//                e.printStackTrace();
//                throw new RuntimeException( e );
                System.out.println( "Timer was interrupted, canceled" );
            }
            System.out.println( "Timer stopped" );
        }
    }

    public static
    void test() throws InterruptedException {
        Timer t = new Timer();
        t.start();

        System.out.println( t.isAlive() );
        Thread.sleep( 1000 );
        t.interrupt();

        Thread.sleep( 1000 );
        System.out.println( t.isAlive() );
        t.start();
    }

    public static
    void main( String[] args ) throws InterruptedException {
        System.out.println( "".getBytes().length );
        System.out.println( "0".getBytes().length );
        System.out.println( "0".getBytes()[ 0 ] );
        System.out.println( '0' + 0 );
        System.out.println( Math.pow( 2, 8 ) );
    }
}
