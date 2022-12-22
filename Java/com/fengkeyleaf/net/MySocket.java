package com.fengkeyleaf.net;

/*
 * MySocket.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/11/2022$
 */

import com.fengkeyleaf.io.pkts.IPv4;
import com.fengkeyleaf.io.pkts.TCP;
import com.fengkeyleaf.logging.MyLogger;
import com.fengkeyleaf.util.MyArrays;
import com.fengkeyleaf.util.MyBitSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import java.util.TreeMap;

/**
 * TCP Socket.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// TODO: 11/18/2022 Be carefully with ack, syn, fin when allowing to send and receive in one side.
// TODO: 11/20/2022 dynamically control RN in both sides.
// TODO: 11/21/2022 send and receive at the same time.
public class MySocket implements Runnable {
    private static final int BUFFER_SIZE = 1024;
    private static final int DEFAULT_SSTHREAD = 64000; // 64 KB
    // Both sender and receiver have.
    DatagramSocket s; // src socket
    InetAddress dip; // dst ip
    int dp; // dst port
    int isn; // initial sequence number
    boolean is = true; // is syn status
    boolean ic; // is closing status

    //----------------------------------------------------------
    // Sender
    //----------------------------------------------------------

    int ti; // timeoutInterval
    int b; // base
    int nsn; // nextSeqNum
    // ------------->
    // Controlled by the other side.
    int RN = -1; // received advertised-window size.
    int n = -1; // Usable window size ( flow control ), multiple of RN.
    // ------------->
    int cwnd = 1; // Congestion window
    int ssthresh = DEFAULT_SSTHREAD;
    int c = -1; // cwnd count
    boolean isSS = true; // Congestion control status
    boolean isReady = true;
    // TODO: 11/12/2022 sndpkt and dupAckCount: Optimize space usage.
    // sn => UPD pkt
    final TreeMap<Integer, DatagramPacket> sndpkt = new TreeMap<>();
    // ack => count
    final TreeMap<Integer, Integer> dupAckCount = new TreeMap<>();
    final Server ser;
    // Generates pkt transmission timeout or ack pkt timeout.
    final Failer f = new Failer();
    final MyLogger lg;
    // data buffer
    ByteArrayOutputStream db = new ByteArrayOutputStream();
    // Timeout timer
    Timer to;
    // Time-wait timer
    WaitTimeTimer tw;

    /**
     * Pkt timeout timer.
     */

    class Timer extends Thread {
        int sn; // sequence number
        int t; // timeoutInterval

        Timer( int nsn, int t ) {
            this.sn = nsn;
            this.t = t;
            start();
        }

        @Override
        public void run() {
            lg.debugLog( "Timer with sn(" + sn + ") is running----->" );

            // Start timeout timer.
            try {
                Thread.sleep( t );
                // Pkt timeouted.
                lg.debugLog( "Pkt with sn(" + sn + ") timeouted, retransmit." );
                timeoutEvent();
            } catch ( InterruptedException e ) {
                // Pkt acked, stop timer.
                lg.debugLog( "Timer with sn(" + sn + ") was interrupted, canceled" );
            } catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }
    }

    /**
     * Wait-time timer for closing connection.
     */

    class WaitTimeTimer extends Thread {
        int wt; // wait time

        WaitTimeTimer( int wt ) {
            this.wt = wt;
            start();
        }

        @Override
        public void run() {
            lg.debugLog( "Wait-time timer is running----->" );

            try {
                Thread.sleep( wt );
                lg.debugLog( "Time to close everything..." );
                s.close();
                if ( to.isAlive() ) to.interrupt();
                // TODO: 11/22/2022 exit here may produce Exception in thread "Thread-0".
                System.exit( 0 );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Thread to continue receving pkts from the internet and deliver it to the TCP socket.
     */

    // Here, N is the desire window size.
    // b/c server would like to receive pkt of length provided by it's own socket,
    // not the one from other side. Be careful with this.
    class Server extends Thread {
        private static final int MAX_TCP_HEADER_LENGTH = 60;
        // TODO: 11/17/2022 buffer b is variable, not fixed due to N. and TCP is not always 20.
        // First initlize the buffer with this socket's N to recieve the first syn ack pkt,
        // but we will update the buffer size based on the received window size.
        byte[] b = new byte[ N + MAX_TCP_HEADER_LENGTH ]; // buffer, data length + TCP header length.
        DatagramPacket r = new DatagramPacket( b, b.length );

        /**
         * Run this server to listen on the port, and receive message from client,
         * and update the routing table.
         */

        @Override
        public void run() {
            lg.debugLog( "The Server starting....." );

            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/DatagramSocket.html#receive(java.net.DatagramPacket)
            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/DatagramPacket.html#getLength()

            while ( true ) {
                try {
                    s.receive( r );
                    assert is || r.getLength() <= N + MAX_TCP_HEADER_LENGTH : r.getLength() + " | " + ( N + MAX_TCP_HEADER_LENGTH );
                    // TODO: 11/18/2022 First finish process received pkt and then try to receive next pkt.
//                    System.out.println( b.length + " " + r.getLength() );
                    retrieve( Arrays.copyOf( b, r.getLength() ) );
                } catch ( IOException e ) {
                    throw new RuntimeException( e );
                }
            }
        }
    }

    /**
     * Class to stimulte triggering pkt loss and corrupion.
     */

    // https://stackoverflow.com/a/8183871
    static class Failer {
        final Random r = new Random();
        // probability of pkt loss, 0 <= pl <= 1
        double failsSendProb;
        // probability of ack loss, 0 <= pl <= 1
        double failsAckProb;
        // probability of corruption, 0 <= pl <= 1
        double corruptProb;
        // turn off congestion control
        boolean tocc;

        boolean failsSend() {
            return r.nextDouble() < failsSendProb;
        }

        boolean failsAck() {
            return r.nextDouble() < failsAckProb;
        }

        boolean corrupts() {
            return r.nextDouble() < corruptProb;
        }
    }

    //----------------------------------------------------------
    // Constructor
    //----------------------------------------------------------

    /**
     * Get receiver TCP socket.
     *
     * @param sh src host name,
     * @param sp src sending and listening port,
     * @param dh dst host name,
     * @param dp dst sending and listening port,
     * @param ti timeoutInterval
     * @param N initial advertised-window size,
     * @param isn initial sequence number,
     * @throws SocketException
     * @throws UnknownHostException
     */

    public MySocket(
            String sh, int sp,
            String dh, int dp,
            int ti, int N, int isn,
            MyLogger lg
    ) throws SocketException, UnknownHostException {
        this.lg = lg;

        // Network setting
        // Socket listening to src addr and src port,
        // as well as sending pkts.
        s = new DatagramSocket( sp, InetAddress.getByName( sh ) );
        // dst addr and port.
        dip = InetAddress.getByName( dh );
        this.dp = dp;

        // TCP setting
        this.ti = ti;
        this.N = N;
        b = nsn = this.isn = isn;
        // Initialize the default ack pkt.
        sndpkt.put(
                esn,
                makePkt( nsn, esn, new byte[ 0 ], 0, true, true, false )
        );

        // Start local server to receive pkts.
        // Must be done after TCP setting,
        // b/c N may be given a different value,
        // otherwise, the server will always have the default N( 1024 ).
        ser = new Server();

    }

    //----------------------------------------------------------
    // Cook TCP packet
    //----------------------------------------------------------

    /**
     * Cook TCP header and data, encapsulating then into a UDP packet.
     *
     * @param sn sequence number
     * @param an ackNum number.
     * @param D data field.
     * @param l valid length of data byte array.
     * @return UDP packet to send.
     */

    //            source port            |             Destination port
    //                            Sequence number
    //                      Acknowledgment number (if ACK set)
    // Data offset | Reserved | NS, CWR, ECE, URG, ACK, PSH, RST, SYN, FIN | Window Size
    //             Checksum              |         Urgent pointer (if URG set)
    //    Options (if data offset > 5. Padded at the end with "0" bits if necessary.)
    //                                  Data

    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/DatagramSocket.html#send(java.net.DatagramPacket)
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/DatagramPacket.html
    private DatagramPacket makePkt(
            int sn, int an,
            byte[] D, int l,
            boolean ack, boolean syn,
            boolean fin
    ) {
        assert D.length == 0 || l <= D.length;
        // Extract valid positions from the byte array D.
        // D,  l
        // [], 1 => []
        // [1,2,3,4], 4 => [1,2,3,4]
        // [1,2,3,4,0,0], 4 => [1,2,3,4]
        D = D.length < l ? D : Arrays.copyOf( D, l );

        String f = getFlags( ack, syn, fin );
        int c = 0;
        TCP h = new TCP(
                s.getLocalPort(), dp,
                sn, an, 5,"000",
                f, N, c, "00", D
        );

        c = getChecksum( getChecksumBytes( h, s.getLocalAddress(), dip ) );
        assert c >= 0 : c;
        h = new TCP(
                s.getLocalPort(), dp,
                sn, an, 5,"000",
                f, N, c, "00", D
        );

        byte[] B = h.getBytes();
        return new DatagramPacket( B, B.length, dip, dp );
    }

    private static
    String getFlags( boolean ack, boolean syn, boolean fin ) {
        assert !( syn && fin );
        return "0000" + // NS, CWR, ECE, URG
                ( ack ? "1" : "0" ) +
                "00" + // PSH, RST
                ( syn ? "1" : "0" ) +
                ( fin ? "1" : "0" );
    }

    //----------------------------------------------------------
    // Checksum
    //----------------------------------------------------------

    /**
     * Cook the byte array for checksum computation.
     *
     * @param h TCP pkt with header and data.
     * @param sip src ip addr.
     * @param dip dst ip addr.
     * @return byte array for checksum computation.
     */

    // The TCP checksum includes the:
    // 1. Pseudo IP header
    // 2. TCP header
    // 3. TCP body

    // https://www.geeksforgeeks.org/calculation-of-tcp-checksum/
    // The Fields of the Pseudo IP header are: ( 12 byts )
    //                                  IP of the Source ( 32 b )
    //                                IP of the Destination ( 32 b )
    // Fixed of 8-bits | Protocol (stating the type of the protocol used, 8 b) | TCP/UDP segment Length (16 b)

    static
    byte[] getChecksumBytes( TCP h, InetAddress sip, InetAddress dip ) {
        byte[] B = h.getBytes();
        // The Fields of the Pseudo IP header are: ( 12 byts )
        ByteBuffer b = ByteBuffer.allocate( 12 );
        assert sip.getAddress().length == 4;
        // IP of the Source ( 32 b )
        b.put( sip.getAddress() );
        // IP of the Destination ( 32 b )
        assert dip.getAddress().length == 4;
        b.put( dip.getAddress() );
        // Fixed of 8-bits, Protocol, TCP/UDP segment Length
        assert getThree( B.length ).length == 4 : getThree( B.length ).length;
        b.put( getThree( B.length ) );

        // Pseudo IP header + TCP header + TCP body.
//        System.out.println( Arrays.toString( MyArrays.concatAll( b.array(), B ) ) );
        return MyArrays.concatAll( b.array(), B );
    }

    /**
     * Cook the 32 bits for fixed 8 bits, protocol and segment length.
     * See a similar example in {@link TCP}'s getThree()
     * to get a good taste of how BitSet works.
     *
     * @return 32 bits in byte array.
     */

    static
    private byte[] getThree( int l ) {
        BitSet b = new BitSet( 32 );
        // Fixed 8 bits are all zeros, skip.
        // protocol
        MyBitSet.setBits( b, 8, Integer.toBinaryString( IPv4.TCP ) );
        // segment length
        MyBitSet.setBits( b, 16, Integer.toBinaryString( l ) );
        return MyArrays.reverse( MyBitSet.toByteArray( b, 4 ) );
    }

    // 00000000 00000001 00000000 00000000
    private static final int W = 1 << 16; // 65536
    // 11111111 1111110 11111111 11111111
    private static final int CW = ~W; // -65537
    // Bit mask to make bits prior to 15th all zero
    // 00000000 00000000 11111111 11111111
    private static final int M = -1 >>> 16; // 65535

    // https://www.alpharithms.com/internet-checksum-calculation-steps-044921/
    // https://www.geeksforgeeks.org/calculation-of-tcp-checksum/
    // https://www.geeksforgeeks.org/error-detection-in-computer-networks/
    // Chapter_3_v8.0, Chapter 3 Transport Layer, P31

    /**
     * Compute TCP checksum.
     *
     * @param B Pseudo IP header, TCP header and TCP body in byte array.
     * @return TCP checksum.
     */

    // Calculate internet checksum, bit manipulation version.
    // Algorithm CHECKSUM( d )
    // Input. Byte data array containing pseudo IP header, TCP header and TCP body.
    // Output. Checksum for the input byte array.
    // Counting bits from right to left.
    static
    int getChecksum( byte[] B ) {
        // 1. d <- padding( d ) // Make sure that the length of d is even.
        B = B.length % 2 == 0 ? B : Arrays.copyOf( B, B.length + 1 );
        assert B.length % 2 == 0;

        // 2. I <- 32-bit integer array of length floor( len( d ) / 2 ).
        int[] I = new int[ B.length / 2 ];
        int idx = 0;
        // 3. for every two consecutive bytes in d, ( i, j )
        for ( int i = 0; i < B.length; i += 2 ) {
            assert idx < I.length;
            // 4. do n <- (  i & 0xff ) << 8 ) | ( j & 0xff )
            // 5. Append n to I.
            // https://stackoverflow.com/questions/9609394/java-byte-array-contains-negative-numbers
            I[ idx++ ] = ( ( B[ i ] & 0xff ) << 8 ) | ( B[ i + 1 ] & 0xff );
            assert 0 <= I[ idx - 1 ] && I[ idx - 1 ] < W : I[ idx - 1 ] + " | " + B[ i - 1 ] + " | " + ( ( int ) ( B[ i - 1 ] & 0xff ) );
        }

        // 6. w <- 1 << 16 // Should be 65536 in decimal
        // 7. cw <- ~w // Should be -65537 in decimal

        // 8. s <- 0
        int s = 0;
        // 9. for every 32-bit integer n in I
        for ( int n : I ) {
            // 10. do s += n
            s += n;
            // 11. if s & w != 0
            // 12. then s = ( s & cw ) + 1
            if ( ( s & W ) != 0 ) s = ( s & CW ) + 1;
        }

        // https://www.cs.bilkent.edu.tr/~guvenir/courses/CS101/op_precedence.html
        // 13. s <- Make bits prior to 15th all zero in ~s
        // 14. return s
        return ~s & M;
    }

    /**
     * Tells if received pkt, p, corrupted.
     *
     * @param p Received TPC pkt from the sender
     * @return  True, pkt corrupted; false, pkt not corrupted.
     */

    // Checksum Validation, bit manipulation version.
    // Algorithm CORRUPT( d, c )
    // Input. Received TPC pkt in byte array from the sender, but its checksum field should be set to 0.
    //        and checksum from the received TCP header.
    // Output. Tells if received pkt is corrupted or not.
    boolean corrupts( TCP p ) {
        int cs = ( int ) p.checksum();
        assert cs >= 0;

        // Set checksum field to 0.
        p = new TCP(
                p.srcPort(), p.desPort(), p.seqNum(),
                p.ackNum(), p.headerLen(), p.reserved(),
                p.flags(), p.windowSize(), 0,
                p.urgentPtr(), p.data()
        );
        // 1. cs <- CHECKSUM( d )
        int c = getChecksum( getChecksumBytes( p, dip, s.getLocalAddress() ) );
        // 2. return ~( cs + ~c ) != 0 // ~: complement
        return ~( cs + ~c ) != 0;
    }

    //----------------------------------------------------------
    // Sender events
    //----------------------------------------------------------

    /**
     * Send data in byte to the receiver.
     *
     * @param d file data in byte array.
     * @param l valid length of the byte array.
     * @throws IOException
     */

    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/PrintStream.html#printf(java.util.Locale,java.lang.String,java.lang.Object...)

    // Calling by the upper-layer application.
    // Algorithm SENDDATAEVENT( d )
    // Input. Byte data passed from the upper-layer application.
    public boolean send(
            byte[] d, int l,
            boolean ack, boolean syn,
            boolean fin
    ) throws IOException {

        // SYN and FIN or send normally.
        assert d.length == 0 || l <= d.length : l + " | " + d.length;
        // d is too large to be received by the receiver, refuse to send.
        // 7. else refuse_data( d )
        if ( !is && // First time to syn, RN and n haven't initialized.
                // data to sent exceeds the limit by the receiver.
                nsn >= b + n ) {
//            System.out.println( nsn + " " + b + " " + n );
            lg.debugLog( "S: Receiver cannot handle data of len(%d) nsn(%d) b(%d) n(%d)".formatted( d.length, nsn, b, n ) );
            // Notify the app that the data was refused.
            return false;
        }

        // length( d ) fits into the advertised window size.
        // 1. if nextSeqNum < base + n
        // Cook the packet, send and start the timer.
        // 2. then sndpkt[ nextSeqNum ] = make_pkt( nextSeqNum, d, chksum, SYN, FIN )
        // checksum( header, data )
        DatagramPacket pkt = makePkt( nsn, esn, d, l, ack, syn, fin ); // UDP packet
        sndpkt.put( nsn, pkt ); // cache pkt

        boolean fts = f.failsSend(); // fail to send.
        if ( fts ) lg.warningLog( "Sender failed to send pkt with sn(" + nsn + ")" );
        // 4. then udt_send( sndpkt[ nextSeqNum ] )
        if ( !fts ) s.send( pkt );
        lg.debugLog( "S: Sent pkt with seq(" + nsn + "), len(" + pkt.getLength() + "), dstIp(" + pkt.getSocketAddress() + ")" );

        // 5. if t is not already running
        // 6. then start t,
        // setting its next sequence number to nextSeqNum and timeout interveral to i.
        if ( to == null || // First time to start the socket.
                // to started at least once.
                !to.isAlive() ) to = new Timer( nsn, ti );

        // 7. nextSeqNum += length( d )
        nsn += l;
        // Notify the app that the data was sent.
        return true;
    }

    /**
     * Receive akc pkts from the receiver.
     *
     * @param p Packet received from the receiver side.
     * @throws IOException
     */

    // Algorithm RECEIVEPACKETEVENT( p )
    // Input. Packet received from the receiver side.
    void recPktSender( TCP p ) throws IOException {
        boolean bc = f.corrupts(); // bit corruption.
        // Simulate pkt loss and corruption or
        // Corrupted pkt received, wait for the timer to timeout and resend.
        if ( bc || corrupts( p ) ) {
            if ( bc ) lg.warningLog( "S: Pkt with ack(" + p.ackNum() + ") corrupted" );
            return;
        }

        // Acknowledgment received
        // 1. if notcorrupt( p )
        // 2. then ackNum <- getacknum( p )
        int ack = ( int ) p.ackNum();
        assert ack >= b; // ack < b is impossible.
        // Sent pkt with sn( ack - 1 ) must be cached.
        assert sndpkt.floorKey( ack ) != null : ack;

        // 28. else dupACKcount[ ackNum ]++
        if ( dupAckCount.containsKey( ack ) ) {
        // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Map.html#putIfAbsent(K,V)
            dupAckCount.put( ack, dupAckCount.get( ack ) + 1 );
            // 29. if dupACKcount[ ackNum ] == 3 // Fast Retransmission
            // 30. TIMEOUTEVENT()
            if ( dupAckCount.get( ack ) == 3 ) {
                lg.warningLog( "FR: 3 dup pks with ack(" +ack + ")" );
                timeoutEvent();
            }
            return;
        }

        boolean ina = ack == b; // invalid ack
        //4. base = ackNum
        b = ack;
        //3. if first time to receive ackNum
        //5. dupACKcount[ ackNum ] = 1
        assert !dupAckCount.containsKey( ack );
        dupAckCount.put( ack, 1 );
        // 6. if base == nextSeqNum or getseqnum( t ) < base
        // 7. then stop t
        // Due to sending and receiving order, maybe b > nsn.
        // assert b <= nsn : b + " | " + nsn;
        // Acked not-yet-acked pkts, or pkt associated with the timer has been akced.
        if ( to != null && ( b == nsn || to.sn < b ) ) to.interrupt();
        // 8. else if there are still unACKed segments
        if ( b != nsn ) checkStillUnacked();

        // Ack pkt loss may have happened,
        // just count duplicate and no response.
        if ( ina ) return;

        // Established the connection, ready to send data.
        // 10. if getSyn( p )
        if ( p.isSyn() ) {
            // 11. then SYN = 0
            is = false;
            // Get advertised-window size from the receiver.
            n = RN = ( int ) p.windowSize();
            // 12. return
            System.out.println( "Sender: Connection established......" );
            return;
        }
        // Closing the connection.
        // 13. else if getFIN( p )
        else if ( p.isFin() ) {
            // At this point, sender is already in the closing status.
            // 14. then start TIME_WAIT timer and close the socket when the timer timeout.
            tw = new WaitTimeTimer( ti );
            // 15. return
            return;
        }

        // Congestion Control flow
        congestionControl();
    }

    /**
     * Check if there are still unacked pkts.
     *
     * @return unacked pkt with the oldest seg's #.
     */

    private DatagramPacket checkStillUnacked() {
        // 8. else if there are still unACKed segments
        if ( b < nsn ) {
            // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/TreeMap.html#ceilingKey(K)
            // 9. then start t,
            // setting its next seqence number to nextSeqNum of the oldest unACKed segments
            // and timeout interveral to i.
            assert sndpkt.ceilingKey( b ) != null : b + " | " + nsn;
            int n = sndpkt.ceilingKey( b );
            assert n < nsn; // b <= n < nsn
            if ( to.isAlive() ) to.interrupt();
            to = new Timer( n, ti );

            // Get and return the pkt to be retransmitted.
            assert sndpkt.get( n ) != null : b + " | " + nsn;
            return sndpkt.get( n );
        }

        // No unacked segments.
        return null;
    }

    /**
     * Congestion control procedure.
     */

    private void congestionControl() {
        // Congestion control is disabled.
        if ( f.tocc ) return;

        assert n > 0;
        // Slow start
        // 19. if isSlowStart()
        if ( isSS ) {
            // 20. n += RN
            n += RN;
            // 22. if cwnd >= ssthresh
            if ( cwnd >= ssthresh ) {
                lg.debugLog( "Go into congestion avoidance." );
                // 23. then go into Congestion Avoidance phase
                isSS = false;
                // 24. c <- cwnd
                c = cwnd;
            }

            return;
        }

        // Congestion Avoidance
        // 25. else c--
        c--;
        assert c >= 0;
        // 26. if c == 0
        if ( c == 0 ) {
            // 27. then cwnd += 1
            cwnd++;
            // 28. c = cwnd
            c = cwnd;
            // 29. n += RN
            n += RN;
        }
    }

    /**
     * Reset sending rate before going into SS.
     */

    // Algorithm RESET()
    void reset() {
        // 1. ssthresh = floor( cwnd / 2 )
        ssthresh = cwnd / 2;
        // 2. cwnd = 1 MSS
        cwnd = 1;
        // 3. n = RN
        n = RN;
        // 4. reset( dupAckCount )
        dupAckCount.clear();
        // 5. Go into Slow Start phase.
        isSS = true;
        lg.debugLog( "Go into slow start." );
    }

    /**
     * Triggered when a pkt is timeouted.
     *
     * @throws IOException
     */

    // Algorithm TIMEOUTEVENT()
    void timeoutEvent() throws IOException {
        // 1. Stop t
        if ( to.isAlive() ) to.interrupt();
        // 2. if not try to establish connect or to close congestion
        // 3. then RESET()
        reset();

        // 4. retransmit not-yet-acked segment with smallest seq. #.
        // 5. Restart t
        DatagramPacket p = checkStillUnacked();
        // Pkt associated with timer has been acked.
        if ( p != null ) {
            lg.debugLog( "Pkt with sn(" + TCP.getTCP( p.getData() ).seqNum() + ") retransmitted." );
            s.send( p );
        }
    }

    //----------------------------------------------------------
    // Receiver
    //----------------------------------------------------------

    // self advertised-window size,
    // controlled by the app.
    int N = BUFFER_SIZE;
    int esn; // expectedSeqNum

    //----------------------------------------------------------
    // Receiver events
    //----------------------------------------------------------

    /**
     * Receive pkst from the sender.
     *
     * @param p Packet received from the sender side.
     * @throws IOException
     */

    // Algorithm RECEIVEPACKETEVENT( p )
    // Input. Packet received from the sender side.
    void recPktReceiver( TCP p ) throws IOException {
        boolean bc = f.corrupts(); // bit corruption.

        // Any other events
        // 14. else udt_send( ackpkt )
        // Simulate pkt loss and corruption or
        // bits corrupted or received pkt isn't the expected one.
        if ( bc || corrupts( p ) || p.seqNum() != esn ) {
            if ( bc || corrupts( p ) ) lg.warningLog( "R: Pkt with sn(" + p.seqNum() + ") corrupted" );
            if ( p.seqNum() != esn ) lg.warningLog( "R: Pkt with sn(" + p.seqNum() + " != esn(" + esn + ")" );
            s.send( sndpkt.get( esn ) );
            return;
        }

        // 1. if notcorrupt( p ) and hasseqnum( p, expectedSeqNum ) // Receive pkt event
        // 2. then d = extract( p )
        byte[] d = ( p.isSyn() || p.isFin() && p.isAck() ) ? // Syn or closing status?
                new byte[ 1 ] : // yes, artificial data array of length 1, but not deliver data.
                p.data(); // no, data array from the TCP pkt, and indeed deliver data.
        // 3. expectedSeqNum += length( d )
        esn += d.length;
        // 4. SYN <- 0, FIN <- 0
        // Established the connection, ready to receive data.
        // 5. if getSyn( p )
        // 6. then SYN = 1
        if ( p.isSyn() ) {
            is = false;
            n = RN = ( int ) p.windowSize();
            System.out.println( "Receiver: Connection established" );
        }
        // Close the connection
        // 7. else if getFIN( p ) && getAkc( p )
        // 8. then FIN = 1
        // 9. Notify the application to close
        // and then close the receiver socket.
        else if ( p.isFin() && p.isAck() ) {
            ic = true;
            System.out.println( "Receiver: Connection closed" );
        }
        // Normal acknowledgement, including last pkt.
        // 11. else deliver_data( d )
        else deliverData( d, p.isFin() );

        // 12 ackpkt = make_pkt( expectedSeqNum, ACK, SYN, FIN, chksum )
        DatagramPacket up = makePkt(
                nsn, esn, new byte[ 0 ], 0,
                true, p.isSyn(), p.isFin() && p.isAck()
        ); // UDP packet
        // Cook syn pkt once, never cook again.
        sndpkt.put( esn, up ); // cache pkt

        // Fail to send ack
        boolean fa = f.failsAck();
        if ( fa ) lg.warningLog( "Receiver failed to send ack(" + esn + ")" );
        // 13 udt_send( ackpkt )
        if ( !fa ) s.send( up );
        lg.debugLog( "R: sent ack(" + esn + ") pkt" );
    }

    /**
     * Deliver data to the app.
     *
     * @param d data contained in the oldest pkt.
     * @param fin is this pkt the last one.
     * @throws IOException
     */

    private void deliverData( byte[] d, boolean fin ) throws IOException {
        assert d != null && d.length > 0;
        db.write( d );

        // Only FIN was set, meaning this is the last pkt.
        if ( fin ) {
            System.out.println( "File transformation completed......" );
            isReady = false;
        }
    }

    /**
     * Retrieve data from the sever. And process it based on its type, normal pkt or ack pkt.
     *
     * @param B bytes received from the server.
     * @throws IOException
     */

    // TODO: 11/19/2022 Cannot handle the case in which both send closing signal at the same time.
    private void retrieve( byte[] B ) throws IOException {
        TCP p = TCP.getTCP( B );
        // pkt( ACK, FIN ) => closing pkt
        if ( p.isAck() && p.isFin() ) {
            retrieve( ic, p );
            return;
        }

        // Normal pkt
        retrieve( p.isAck(), p );
    }

    private void retrieve( boolean f, TCP p ) throws IOException {
        // Normal: pkt( ACK ) => normal ack pkt
        // Closing: already in closing status.
        if ( f ) {
            lg.debugLog( "S: Received pkt with ack = " + p.ackNum() );
            recPktSender( p );
            return;
        }

        // Normal: pkt() => normal pkt
        // Closing: not in closing status.
        lg.debugLog( "R: Received pkt with seq(" + p.seqNum() + "), data(" + ( p.data() == null ? 0 : p.data().length ) + ")" );
        recPktReceiver( p );
    }

    // Calling by the upper-layer application.
    @Override
    public void run() {
        String h = s.getLocalAddress().getHostName();
        // Initializing output information.
        lg.debugLog( "TCP Socket: " + h + " is starting----->" );

        // Run this TCP socket.
        ser.start();

        lg.debugLog( "TCP Socket: " + h + " initialization complete----->" );
    }

    //----------------------------------------------------------
    // Interactive operations with upper-layer application.
    //----------------------------------------------------------

    /**
     * Try to connect to the other side.
     *
     * @throws IOException
     */

    // Calling by the upper-layer application.
    // Algorithm CONNECTIONEVENT()
    public void connect() throws IOException {
        // 3-way handshakes to establish connection with the receiver
        // and determine N based on the information.
        // 1. if try to establish connection
        // 2. SENDDATAEVENT( 1 byte data )
        send( new byte[ 0 ], 1, false, true, false );
        // 3. Notify the upper-layer application the advertised-window size, N.
    }


    /**
     * Close this socket and notify the other side to close.
     *
     * @throws IOException
     */

    public void close() throws IOException {
        System.out.println( "Closing the app......" );
        // No connection established, terminate JVM.
        if ( is || ic ) System.exit( 0 );

        // Close the connection with the receiver
        // when received a signal to close from the application.
        // And send a signal to the receiver and waiting for its FIN ackNum.
        // 4. else ACK = FIN = 1
        ic = true;
        // 5. SENDDATAEVENT( 1 byte data )
        send( new byte[ 0 ], 1, true, false, true );
    }

    /**
     * Get the receiver's advertised-window size.
     *
     * Note that this returned window size must be the one from the other side.
     * NOT this program's window size, don't be confused by this!
     *
     * @return Receiver's advertised-window size
     */

    public int getReceivedWindowSize() {
        return RN;
    }

    /**
     * Tells if this socket is ready to send and receive data.
     *
     * @return
     */

    public boolean isReady() {
        // This socket is not in the SYN and FIN status
        // and ready to send and receive data.
        return !is && !ic && isReady;
    }

    /**
     * Tells if there are any file buffers in this socket available to read from.
     *
     * @return
     */

    public boolean isAvailable() {
        return !isReady;
    }

    /**
     * Get file byte data stored in this socket.
     *
     * @return file data in bytes.
     */

    // TODO: 11/14/2022 support retrieving multiple files.
    public byte[] getFile() {
        assert !isReady;
        // File data in byte.
        byte[] d = db.toByteArray();
        // Clear byte buffer.
        db.reset();
        // Deliver file data to the app, ready to receive new data.
        isReady = true;
        return d;
    }

    /**
     * Enable pkt transmission failures with some probabilities.
     *
     * @param failsSendProb probability of pkt loss sent by sender in %
     */

    public void enableFailure(
            int failsSendProb, int failsAckProb,
            int corruptProb, boolean tocc
    ) {
        assert 0 <= failsSendProb && failsSendProb <= 100;
        f.failsSendProb = failsSendProb / 100.0;
        assert 0 <= failsAckProb && failsAckProb <= 100;
        f.failsAckProb = failsAckProb / 100.0;
        assert 0 <= corruptProb && corruptProb <= 100;
        f.corruptProb = corruptProb / 100.0;
        f.tocc = tocc;
    }

    /**
     * Disable pkt transmission failure.
     *
     * @return
     */

    public void disableFailure() {
        f.corruptProb = f.failsAckProb = f.failsSendProb = 0;
    }
}
