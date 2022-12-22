package com.fengkeyleaf.io;

/*
 * MyFTP.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/11/2022$
 */

import com.fengkeyleaf.logging.MyLogger;
import com.fengkeyleaf.net.MySocket;

import java.io.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * File Transfer Protocol (FTP) using self-implemented TCP.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// TODO: 11/25/2022 Bug: Invalid data added to file without retrieving the oldest one first.
// https://en.wikipedia.org/wiki/File_Transfer_Protocol#Data_transfer_modes
// Shell Command:
// 1) connect - connect to remote myftp
// 2) put - send file
// 3) get - receive file
// 4) quit - exit tftp
// 5) ? - print help information
public class MyFTP {
    private static final double TIMEOUT_INTERVAL = 0.2; // sec
    private static final int INIT_SEQUENCE_NUMBER = 0;
    private static final int DEFAULT_PORT = 1234;
    private static final int WINDOW_SIZE = 512; // byte
    final MySocket s; // TCP socket
    // Logging
    final MyLogger l = new MyLogger();
    String sh; // src host
    int sp = DEFAULT_PORT; // Src port
    String dh; // dst host
    int dp = DEFAULT_PORT; // dst port
    int N = WINDOW_SIZE; // advertised-window size
    //  probability of pkt loss sent by sender, %, 0 <= failsSendProb <= 100
    int failsSendProb;
    //  probability of ack loss sent by sender, %, 0 <= failsAckProb <= 100
    int failsAckProb;
    //  probability of corruption, %, 0 <= corruptProb <= 100
    int corruptProb;
    // timeout interval
    double ti = TIMEOUT_INTERVAL; // sec
    // is turn off congestion control
    boolean itocc;

    //----------------------------------------------------------
    // Shell Command
    //----------------------------------------------------------

    class ShellCommand {
        // https://blog.csdn.net/weixin_35475608/article/details/114049006
        // https://www.geeksforgeeks.org/java-string-format-method-with-examples/
        // Initial output information.
        private static final String i = """
            Commands may be abbreviated. Commands are:
                        
            %-20s\tConnect to remote myftp
            %-20s\tSend file
            %-20s\tReceive file
            %-20s\tSet logging level to debug
            %-20s\tSet logging level to warning
            %-20s\tSet logging level to normal
            %-20s\tExit tftp
            %-20s\tPrint help information
            """.formatted(
                    "-c:", "-p inputfilepath:",
                    "-g outputfilepath:", "-d",
                    "-w", "-n",
                    "-q:", "?:"
        );
        // Help information.
        private static final String h = """
             General instructions to send file.
             
             1) [ -c ] To connect to another side. Only one of them sends connection signal is enough.
             2) [ -p inputfilepath ] To send file in the path inputfilepath.
             3) [ -g outputfilepath ] To receive file and store it into the path outputfilepath.
             4) [ -d ] Set logging level to debug.
             5) [ -w ] Set logging level to warning.
             6) [ -n ] Set logging level to normal.
             7) [ -q ] To quit.
             8) [ -? ] Print help information.
             """;

        void run() {
            try ( Scanner us = new Scanner( System.in ) ) {
                System.out.print( i );

                String i = null;
                while ( true ) {
                    System.out.print( "myftp> " );

                    i = us.next();
                    if ( i.equals( "-q" ) || i.equals( "q" ) ) break;

                    switch ( i ) {
                        case "-c", "c" -> s.connect(); // establish connect.
                        case "-p", "p" -> put( us.next() ); // send file.
                        case "-g", "g" -> get( us.next() ); // receive file.
                        case "-d", "d" -> l.setDebug(); // set logging level to debug
                        case "-w", "w" -> l.setWarning(); // set logging level to warning
                        case "-n", "n" -> l.setNormal(); // set logging level to normal
                        case "?" -> System.out.println( h ); // print help information.
                        default -> undefinedMsg( i );
                    }
                }

                s.close();
            } catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }

        private static
        void undefinedMsg( String c ) {
            System.out.printf( """
                    No such command: %s.
                    Type ? for help information.
                    %n""", c );
        }
    }

    public MyFTP( String[] args )
            throws SocketException, UnknownHostException {

        paraphraseArgs( args );

        // Initialize and start TCP socket.
        s = new MySocket(
                sh, sp, dh, dp,
                ( int ) ( ti * 1000 ), N,
                INIT_SEQUENCE_NUMBER, l
        );
        s.enableFailure( failsSendProb, failsAckProb, corruptProb, itocc );
        new Thread( s ).start();
        // Start FTP shell command thread.
        new ShellCommand().run();
    }

    /**
     * Transmit a file through the TCP socket.
     *
     * @param p file path to read from.
     */

    // TODO: 11/14/2022 Putter is a thread.
    // TODO: 11/21/2022 Cannot send another file until current one was retrieved by the app at the receiver side.
    private void put( String p ) {
        if ( !s.isReady() ) {
            System.out.println( "Connection is not ready or retrieve the file first and then send another." );
            return;
        }

        // Read file and send it through the TCP socket.
        try ( FileInputStream fis = new FileInputStream( p );
              BufferedInputStream bis = new BufferedInputStream( fis )
        ) {

            l.debugLog( "File len: " + bis.available() + " bytes" );

            boolean f = false; // sending flag
            int len = -1;
            byte[] buf = new byte[ s.getReceivedWindowSize() ];

            // No read from the file buffer when f is true, boolean laziness.
            while ( f || ( len = bis.read( buf ) ) != -1 ) {
                // Failed to send then sleep for a while,
                // then let the socket resend.
                f = !s.send(
                        buf, len,
                        false, false,
                        bis.available() <= 0
                );
                if ( f ) Thread.sleep( 100 );
            }
            // Send a header-only pkt to notify the receiver that no more to send.
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve a file through the TCP socket.
     *
     * @param p file path to write to.
     */

    // TODO: 11/14/2022 get multiple files from the socket.
    private void get( String p ) throws IOException {
        if ( !s.isAvailable() ) {
            System.out.println( "No file is ready to retrieve." );
            return;
        }

        // Write to the file.
        File f = new File( p );
        try ( FileOutputStream fos = new FileOutputStream( f ) ) {
            fos.write( s.getFile() );
            System.out.println( "Successfully wrote the file to\n" + f.getAbsolutePath() );
        }
    }

    //----------------------------------------------------------
    // Command Line
    //----------------------------------------------------------

    /**
     * <p>Command line formats:
     * java com.fengkeyleaf.net.MyFTP -srcHost srcHost -dstHost dstHost
     *      [ -srcPort srcPort ]  [ -dstPort dstPort ] [ -windowSize windowSize ]
     *      [ -failsSend failsSendProb ] [ -failsAck failsAckProb ]  [ -corrupt corruptProb ]
     *      [ -turnOffCC turnOffCongestionControl ] [ -debug ] [ -warning ]
     *      [ -timeoutInterval timeoutInterval ]</p>
     *
     * <p>For example:
     * java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -windowSize 40 -failsAck 80 -debug</p>
     *
     * <p>This will start a FTP program with source host 127.0.0.2 and port 1235,
     * with destination host 127.0.0.1 and port 1234. Also its advertised-window size is 40,
     * probability of ack pkt loss is 80%, enabling DEBUG level logging </p>
     * */

    private void paraphraseArgs( String[] args ) {
        for ( int i = 0; i < args.length; i++ ) {
            switch ( args[ i ] ) {
                case "-srcHost", "srcHost" -> sh = args[ ++i ];
                case "-srcPort", "srcPort" -> sp = Integer.parseInt( args[ ++i ] );
                case "-dstHost", "dstHost" -> dh = args[ ++i ];
                case "-dstPort", "dstPort" -> dp = Integer.parseInt( args[ ++i ] );
                case "-windowSize", "windowSize" -> N = Integer.parseInt( args[ ++i ] );
                case "-failsSend", "failsSend" -> failsSendProb = Integer.parseInt( args[ ++i ] );
                case "-failsAck", "failsAck" -> failsAckProb = Integer.parseInt( args[ ++i ] );
                case "-corrupt", "corrupt" -> corruptProb = Integer.parseInt( args[ ++i ] );
                case "-turnOffCC", "turnOffCC" -> itocc = true;
                case "-debug", "debug" -> l.setDebug();
                case "-warning", "warning" -> l.setWarning();
                case "-timeoutInterval", "timeoutInterval" -> ti = Double.parseDouble( args[ ++i ] );
                default -> l.warningLog( "Unhandled arg: " + args[ i ] );
            }
        }

        check();
    }

    /**
     * Validate some arguments.
     */

    private void check() {
        if ( sp < 0 || dp < 0 )
            throw new IllegalArgumentException( "No negative port!" );

        if ( N == 0 )
            System.err.println( "Warning: initial zero advertised-window!" );
        if ( N < 0 )
            throw new IllegalArgumentException( "No negative advertised-window size!" );

        if ( failsSendProb < 0 || failsSendProb > 100 ||
                failsAckProb < 0 || failsAckProb > 100 ||
                corruptProb < 0 || corruptProb > 100 )
            throw new IllegalArgumentException( "No negative or more than 100% failure probability!" );

        if ( ti < 0 )
            throw new IllegalArgumentException( "No negative timeoutInterval!" );
    }

    // cd C:\Users\fengk\OneDrive\documents\computerScience\Computational Geometry\programming_assignments\src

    // javac com/fengkeyleaf/net/*.java
    // javac com/fengkeyleaf/io/*.java
    // javac com/fengkeyleaf/util/*.java
    // javac com/fengkeyleaf/net/*.java com/fengkeyleaf/io/*.java com/fengkeyleaf/util/*.java

    // -p CSCI651/proj3/1
    // -g CSCI651/proj3/1R

    // -p CSCI651/proj3/SummerTimeRenderingED.png
    // -g CSCI651/proj3/Summer_Time_Rendering_ED.png

    // -p CSCI651/proj3/SummerTimeRendingKnifeMio.png
    // -g CSCI651/proj3/SummerTime_Rending_Knife_Mio.png

    // -p CSCI651/proj3/TheBrothersKaramazovbyFyodorDostoyevsky.txt
    // -g CSCI651/proj3/The_Brothers_Karamazov_by_Fyodor_Dostoyevsky.txt


    // Local test

    // 1-send
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.1 -srcPort 1234 -dstHost 127.0.0.2 -dstPort 1235 -debug
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -debug

    // 2-send
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.1 -srcPort 1234 -dstHost 127.0.0.2 -dstPort 1235 -debug
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -windowSize 80 -debug

    // 4-send
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.1 -srcPort 1234 -dstHost 127.0.0.2 -dstPort 1235 -debug
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -windowSize 40 -debug

    // 1-send, pkt loss
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.1 -srcPort 1234 -dstHost 127.0.0.2 -dstPort 1235 -failsSend 80 -debug -timeoutInterval 2
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -debug -timeoutInterval 2

    // 1-send, ack loss
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.1 -srcPort 1234 -dstHost 127.0.0.2 -dstPort 1235 -debug -timeoutInterval 2
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -windowSize 40 -failsAck 80 -debug -timeoutInterval 2

    // 1-send, corruption
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.1 -srcPort 1234 -dstHost 127.0.0.2 -dstPort 1235 -corrupt 30 -debug -timeoutInterval 2
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -windowSize 40 -debug -timeoutInterval 2

    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.1 -srcPort 1234 -dstHost 127.0.0.2 -dstPort 1235 -debug -timeoutInterval 2 -timeoutInterval 2
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -windowSize 40 -corrupt 30 -debug -timeoutInterval 2

    // 1-send, mix
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.1 -srcPort 1234 -dstHost 127.0.0.2 -dstPort 1235 -failsSend 30 -corrupt 20 -debug
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -windowSize 40 -corrupt 30 -failsAck 80 -debug

    // Congestion control
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.1 -srcPort 1234 -dstHost 127.0.0.2 -dstPort 1235 -debug
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -windowSize 10000 -debug

    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.1 -srcPort 1234 -dstHost 127.0.0.2 -dstPort 1235 -turnOffCC -debug
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -windowSize 10000 -debug


    // Real-world network

    // cd CSCI651/src

    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost glados.cs.rit.edu -dstHost 10.230.205.207
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 10.230.205.207 -dstHost glados.cs.rit.edu -windowSize 10000

    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost glados.cs.rit.edu -dstHost rhea.cs.rit.edu
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost rhea.cs.rit.edu -dstHost glados.cs.rit.edu -windowSize 10000

    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.1 -srcPort 1234 -dstHost 127.0.0.2 -dstPort 1235 -turnOffCC -debug
    // java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -windowSize 10000 -debug

    public static
    void main( String[] args )
            throws SocketException, UnknownHostException {

        new MyFTP( args );
    }
}
