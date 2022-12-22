package CSCI651.proj2;

/*
 * udpBaseClient_2.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/25/2022$
 */

// Java program to illustrate Client side
// Implementation using DatagramSocket

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://www.geeksforgeeks.org/working-udp-datagramsockets-java/
public class udpBaseClient_2 {
    public static void main( String args[] ) throws IOException {
        Scanner sc = new Scanner( System.in );

        // Step 1:Create the socket object for
        // carrying the data.
        DatagramSocket ds = new DatagramSocket();

        InetAddress ip = InetAddress.getLocalHost();
        byte buf[] = null;

        // loop while user not enters "bye"
        while ( true ) {
            String inp = sc.nextLine();

            // convert the String input into the byte array.
            buf = inp.getBytes();

            // Step 2 : Create the datagramPacket for sending
            // the data.
            DatagramPacket DpSend =
                    new DatagramPacket( buf, buf.length, ip, 1234 );

            // Step 3 : invoke the send call to actually send
            // the data.
            ds.send( DpSend );

            // break the loop if user enters "bye"
            if ( inp.equals( "bye" ) )
                break;
        }

        ds.close();
    }
}
