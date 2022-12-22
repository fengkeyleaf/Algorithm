package CSCI651.proj2;

/*
 * udpBaseServer_2.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/25/2022$
 */


// Java program to illustrate Server side
// Implementation using DatagramSocket

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://www.geeksforgeeks.org/working-udp-datagramsockets-java/
public class udpBaseServer_2 {
    public static void main( String[] args ) throws IOException {
        // Step 1 : Create a socket to listen at port 1234
        DatagramSocket ds = new DatagramSocket( 1234 );
        byte[] receive = new byte[ 65535 ];

        DatagramPacket DpReceive = null;
        while ( true ) {

            // Step 2 : create a DatgramPacket to receive the data.
            DpReceive = new DatagramPacket( receive, receive.length );

            // Step 3 : revieve the data in byte buffer.
            ds.receive( DpReceive );

            System.out.println( "Client:-" + data( receive ) );

            // Exit the server if the client sends "bye"
            if ( data( receive ).toString().equals( "bye" ) ) {
                System.out.println( "Client sent bye.....EXITING" );
                break;
            }

            // Clear the buffer after every message.
            receive = new byte[ 65535 ];
        }
    }

    // A utility method to convert the byte array
    // data into a string representation.
    public static StringBuilder data( byte[] a ) {
        if ( a == null )
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while ( a[ i ] != 0 ) {
            ret.append( ( char ) a[ i ] );
            i++;
        }
        return ret;
    }
}
