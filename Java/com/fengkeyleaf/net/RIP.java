package com.fengkeyleaf.net;

/*
 * RIP.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/25/2022$
 */

import com.fengkeyleaf.lang.MyMath;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * RIP, Routing Information Protocol
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/DatagramPacket.html
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/DatagramSocket.html
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Runnable.html
// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/InetAddress.html
// https://stackoverflow.com/questions/55795983/two-java-files-getting-illegalaccesserror-when-running-class-with-main-method-t
// https://stackoverflow.com/questions/18093928/what-does-could-not-find-or-load-main-class-mean
// TODO: 11/15/2022 make value greater than 15 infinite.
// TODO: 9/26/2022 command line to make a broken router with prob.
// TODO: 9/30/2022 implement subnet mask
// TODO: 9/26/2022 implement local link change.
// TODO: 10/2/2022 Recover a broken router or a broken link.
public class RIP implements Runnable {
    static final int DEFAULT_PORT = 1234;
    static final int DEFAULT_INFINITE = 100;
    static final int DEFAULT_PERIOD = 5000; // 5 sec
    // Cannot too big, be careful with Integer overflow.
    int infinite = DEFAULT_INFINITE;
    int period = DEFAULT_PERIOD;
    int failureTime = -1;
    String localHost;
    String mask = "255.255.255.0";
    int port = -1;
    // TODO: 9/27/2022 merge the table and origin table into one table?
    // Routing tables.
    // Updating table.
    // Contains all routers, including ones not adjacent to this router.
    final TreeMap<String, Data> t = new TreeMap<>();
    // Initializing table.
    // Only contains adjacent routers( neighbours )
    final TreeMap<String, Integer> originT = new TreeMap<>();

    /**
     * Class to store cost from src -> dst, dropping by the next hub.
     */

    class Data {
        int c; // cost
        String n; // next hub
        long t;

        Data( int c, String n ) {
            this( c, n, 0 );
        }

        Data( int c, String n, long t ) {
            this.c = Math.min( c, infinite );
            this.n = n;
            this.t = t;
        }

        /**
         * Split horizon with poisoned reverse.
         *
         * @param n next hub from src
         * @return cost if the next hub form src isn't n, meaning no bad route loop;
         *         otherwise, infinite cost 16.
         */

        int getCost( String n ) {
            if ( this.n.equals( n ) ) return infinite;

            return c;
        }

        @Override
        public String toString() {
            return c + " " + n;
        }
    }

    private static final int SIZE = 65535;
    // UDP Datagram Packet
    // client side
    final Client c = new Client();
    // server side
    final Server s;
    // board caster
    final BoardCaster bCaster = new BoardCaster();
    // timer-out timer
    final Timer timer = new Timer();

    // reference resources for Client and Server:
    // https://www.geeksforgeeks.org/working-udp-datagramsockets-java/

    /**
     * Class to send data.
     */

    static class Client {
        final DatagramSocket ds = new DatagramSocket();

        Client() throws SocketException {}

        void send( String data,
                   InetAddress ip, int port )
                throws IOException {

            byte[] b = data.getBytes();
            assert b.length <= SIZE;
            ds.send( new DatagramPacket( b, b.length, ip, port ) );
        }
    }

    /**
     * Class to receive data.
     */

    class Server extends Thread {
        final DatagramSocket ds;
        byte[] b = new byte[ SIZE ];

        Server( int port, InetAddress ip ) throws SocketException {
            ds = new DatagramSocket( port, ip );
        }

        /**
         * Run this server to listen on the port, and receive message from client,
         * and update the routing table.
         */

        public void run() {
            System.out.println( "The Server starting....." );
            DatagramPacket r = null;

            while ( true ) {
                // Receive message.
                // 1. wait for the two events;
                //     1) route updates;
                //     2) local link changes( starts )
                r = new DatagramPacket( b, b.length );
                try {
                    ds.receive( r );

                    System.out.println( "Receiving data from other router..." );
                    System.out.print( convert( b ).toString() );
                    System.out.println( "...Data received\n" );
                    // Update the routing table.
                    update( convert( b ).toString() );
                } catch ( IOException e ) {
                    throw new RuntimeException( e );
                }

                // Clear the buffer.
                b = new byte[ SIZE ];
            }
        }

        // A utility method to convert the byte array
        // data into a string representation.
        static
        StringBuilder convert( byte[] a ) {
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

    /**
     * Class to broadcast routing table at a period of time.
     */

    class BoardCaster extends Thread {
        @Override
        public void run() {
            System.out.println( "Board caster is Broadcasting----->" );

            while ( true ) {
                // send message to neighbours at a period of time.
                try {
                    System.out.println( "Broadcasting to neighbours." );
                    send();
                    Thread.sleep( period );
                } catch ( InterruptedException | IOException e ) {
                    e.printStackTrace();
                    throw new RuntimeException( e );
                }
            }
        }
    }

    /**
     * Class time-out timer.
     */

    class Timer extends Thread {
        @Override
        public void run() {
            System.out.println( "Timer is running----->" );

            while ( true ) {
                // Check neighbours every at a period of time.
                try {
                    System.out.println( "Checking neighbours..." );
                    check();
                    Thread.sleep( period );
                } catch ( InterruptedException | IOException e ) {
                    e.printStackTrace();
                    throw new RuntimeException( e );
                }
            }
        }
    }

    /**
     * Class to terminator the program when time-outed.
     */

    class Terminator extends Thread {
        final long t;

        Terminator() {
            this.t = System.currentTimeMillis();
        }

        @Override
        public void run() {
            System.out.println( "Terminator is running----->" );

            while ( true ) {
                assert t <= System.currentTimeMillis();
                assert failureTime >= 0;

                if ( System.currentTimeMillis() - t >= failureTime ) {
                    // Terminate this router.
                    System.err.println( "Local Host: " + localHost + " is broken" );
                    System.exit( 0 );
                }
            }
        }
    }

    //----------------------------------------------------------
    // Constructors
    //----------------------------------------------------------

    public RIP( String localHost )
            throws SocketException, UnknownHostException {

        this( localHost, DEFAULT_PORT );
    }

    public RIP( String[] args )
            throws SocketException, UnknownHostException {

        paraphraseArgs( args );

        port = port < 0 ? DEFAULT_PORT : port;
        s = new Server( port, InetAddress.getByName( localHost ) );

        if ( failureTime >= 0 ) new Terminator().start();
    }

    public RIP( String localHost, int port )
            throws SocketException, UnknownHostException {

        this.localHost = localHost;
        this.port = port;
        s = new Server( port, InetAddress.getByName( localHost ) );

        if ( failureTime >= 0 ) new Terminator().start();
    }

    //----------------------------------------------------------
    // Subnet mask
    //----------------------------------------------------------

    // Ignore whitespaces for the rest of comments in this part.
    // e.g. a1 b1 c1 d1 should be a1b1c1d1 in reality.
    static
    String initSubnet( String localHost, String mask ) {
        // a1.b1.c1.d1 => ab1 bb1 cb1 db1 in the form of binary.
        String ip = toBinary( localHost );
        // a2.b2.c2.d2 => ab2 bb2 cb2 db2 in the form of binary.
        String m = toBinary( mask );

        //    ab1 bb1 cb1 db1
        // &  ab2 bb2 cb2 db2
        // ------------------
        //    ab3 bb3 cb3 db3
        String res = and( ip, m );
        // ab3 bb3 cb3 db3 => [0-255].[0-255].[0-255].[0-255]\length
        for ( int i = 0; i < ip.length(); i++ )
            if ( ip.charAt( i ) != res.charAt( i ) )
                return initSubnet( res ) + "\\" + i;

        return initSubnet( res ) + "\\" + 0;
    }

    private static
    String toBinary( String ip ) {
        String[] S = ip.split( "\\." );
        StringBuilder res = new StringBuilder();
        for ( String s : S )
            res.append( paddingZero( Integer.toBinaryString( Integer.parseInt( s ) ) ) );

        assert res.toString().length() == 32;
        return res.toString();
    }

    private static
    String and( String ip, String m ) {
        StringBuilder b = new StringBuilder();
        for ( int i = 0; i < ip.length(); i++ )
            b.append( ip.charAt( i ) == m.charAt( i ) ? String.valueOf( ip.charAt( i ) ) : 0 );

        assert b.toString().length() == 32;
        return b.toString();
    }

    private static
    String paddingZero( String s ) {
        int n = s.length();
        StringBuilder b = new StringBuilder( s );
        // e.g. 101 => 00000101
        for ( int i = 0; i < 8 - n; i++ )
            b.insert( 0, "0" );

        assert b.toString().length() == 8;
        return b.toString();
    }

    // ab3 bb3 cb3 db3 => [0-255].[0-255].[0-255].[0-255]
    private static
    String initSubnet( String ip ) {
        assert ip.length() == 32;
        return MyMath.binaryToDecimal( ip.substring( 0, 8 ) ) + "." +
                MyMath.binaryToDecimal( ip.substring( 8, 16 ) ) + "." +
                MyMath.binaryToDecimal( ip.substring( 16, 24 ) ) + "." +
                MyMath.binaryToDecimal( ip.substring( 24, 32 ) );
    }

    //----------------------------------------------------------
    // RIP
    //----------------------------------------------------------

    /**
     * Send the routing table to all neighbours.
     */

    void send() throws IOException {
        for ( String i : originT.keySet() ) {
            // Don't send to itself.
            if ( i.equals( localHost ) ) continue;

            InetAddress ip = InetAddress.getByName( i );
            c.send( encode(), ip, port );
        }
    }

    /**
     * Encode routing table.
     */

    private String encode() {
        // localhost\n
        StringBuilder b = new StringBuilder( localHost + "\n" );
        // dst cost nextHub\n
        t.keySet().forEach( k ->
                b.append( k ).append( " " ).append( t.get( k ) ).append( "\n" )
        );

        return b.toString();
    }

    /**
     * Update routing table.
     * */

    void update( String data ) throws IOException {
        String[] D = data.split( "\n" );
        String interIp = D[ 0 ];

        assert !interIp.equals( localHost );
        assert originT.containsKey( interIp ) : interIp;
        assert t.containsKey( interIp );
        assert t.get( interIp ).t >= 0 : interIp;
        assert t.get( interIp ).t <= System.currentTimeMillis() : t.get( interIp ).t + " | " + System.currentTimeMillis();
        // Mark the intermediate/neighbour router as being active.
        t.get( interIp ).t = System.currentTimeMillis();

        // Unreachable to the incoming router.
        // No updates at all.
        // Note that this only means that the link from this router to it is unreachable,
        // not necessarily it's broken.
        if ( originT.get( interIp ) >= infinite ) return;

        // Decode route table
        TreeMap<String, Data> interT = decode( Arrays.copyOfRange( D, 1, D.length ) );

        // Receive rout updates from v
        // Recompute and update the shortest paths via v.
        boolean hasUpdated = false;
        // Look into destination routers via v.
        for ( String dstIp : interT.keySet() ) {
            // This router hasn't reached out to the destination before.
            // Add the routing information into the table.
            if ( !t.containsKey( dstIp ) ) {
                // Not a neighbour to this router.
                // Updating time is unnecessary, set to negative value, -1.
                t.put(
                        dstIp,
                        new Data(
                                originT.get( interIp ) + interT.get( dstIp ).c, interIp, -1
                        )
                );

                continue;
            }

            // Reached out before, try to update the information.
            // Path data: this route to the dst,
            Data d = t.get( dstIp );

            // R1: current router.
            // R2: next hub, R1's neighbour.
            // Dst: destination router.

            assert interT.containsKey( dstIp ) : dstIp;
            // Current cost = min( directCost( R1, Dst ), previousCost, currentCost )
            Data cur = getCur( dstIp, interIp, interT );

            // Update happens when the cost of the shortest path changed.
            hasUpdated = d.c != cur.c;

            // Update the table, but keep the updating time unchanged.
            d.n = cur.n;
            d.c = cur.c;

            // The next hub isn't the current updating router,
            assert !d.n.equals( interIp ) ||
                    // or the added-up cost must be rebuilt via the current next hub.
                    originT.get( d.n ) + interT.get( dstIp ).c == d.c : d.n + " | " + interIp + " | " + ( originT.get( d.n ) + interT.get( dstIp ).c ) + " | " + d.c;
        }

        // Broadcast updates to neighbors if this table has been updated.
        if ( hasUpdated ) send();
    }

    // TODO: 10/3/2022 simplify code logics
    private Data getCur( String dstIp, String interIp,
                         TreeMap<String, Data> interT ) {

        int cur = originT.get( interIp ) + interT.get( dstIp ).getCost( localHost );
        // directCost( R1, R2 ) is unavailable.
        if ( originT.get( interIp ) >= infinite )
            // min( directCost( R1, Dst ), currentCost )
            return getCur( originT.getOrDefault( dstIp, infinite ), cur, dstIp, interIp );

        Data d = t.get( dstIp );
        return d.n.equals( interIp ) ? // current next hub == previous next hub,
                                       // meaning dropping by the same neighbour.
                // min( directCost( R1, Dst ), currentCost )
                getCur( originT.getOrDefault( dstIp, infinite ), cur, dstIp, interIp ) :
                // min( previousCost, currentCost )
                getCur( d.c, cur, d.n, interIp );
    }

    /**
     * Get current cost and next hub.
     * */

    private Data getCur( int prev, int cur,
                         String prevHub, String curHub ) {

        int c = Math.min( prev, cur );
        String n = c == prev ? prevHub : curHub;
        // c == prev && n == prevHub.
        assert c != prev || n.equals( prevHub );
        // c == cur && n == curHub.
        assert c != cur || n.equals( curHub );

        return new Data( c, n );
    }

    /**
     * Decode routing table.
     */

    TreeMap<String, Data> decode( String[] D ) {
        TreeMap<String, Data> t = new TreeMap<>();
        for ( String d : D ) {
            // dst cost nextHub\n
            String[] C = d.split( " " );
            assert !t.containsKey( C[ 0 ] );

            t.put( C[ 0 ],
                    new Data(
                            Integer.parseInt( C[ 1 ] ), C[ 2 ]
                    )
            );
        }

        return t;
    }

    /**
     * Check to see if there are any neighbours have been down.
     */

    // The output includes the four fields:
    // 1) destination IP address, 2) subnet mask,
    // 3) next hop, and 4) distance.
    private void check() throws IOException {
        long curTime = System.currentTimeMillis();
        boolean hasUnreachable = false;

        System.out.println( "IP of this router: " + localHost + "----->" );
        for ( String ip : t.keySet() ) {
            // Skip this router itself.
            if ( ip.equals( localHost ) ) continue;

            Data d = t.get( ip );
            // Check to see if the neighbour is down.
            if ( originT.containsKey( ip ) && // Router with ip is a neighbour of this router.
                    curTime - d.t > period * 3 ) {
                assert d.t > 0 : ip;
                // First updated updatingTime in update( ip ),
                // but not yet check ip,
                // which can lead to curTime - d.updatingTime < 0,
                // but has no negative effect on the checking process.
                assert true || curTime >= d.t : ip + " | " + curTime + " | " + d.t;

                // Direct cost is infinite.
                originT.put( ip, infinite );
                // Update paths in the table.
                updateUnreachable( ip );
                hasUnreachable = true;
            }

            // TODO: 10/10/2022 Dynamically computer the network ip with the given netmask for each network.
            // Output routing table.
            System.out.println( "Dst ip: " + initSubnet( ip, mask ) + " | Netmask: " + mask +
                    ( d.c >= infinite ?
                    " | Unreachable" :
                    " | next: " + d.n + " | dis:" + d.c
            ) );
        }
        System.out.println();

        // broadcast to other neighbours.
        if ( hasUnreachable ) send();
    }

    private void updateUnreachable( String n ) {
        // Cost of the paths via the unreachable neighbours should be INFINITE.
        t.keySet().forEach( k -> {
            Data d = t.get( k );
            if ( d.n.equals( n ) ) d.c = infinite;
        } );
    }

    @Override
    public void run() {
        // Initializing output information.
        System.out.println( "Route: " + localHost + " is starting----->" );
        // Routing table.
        t.keySet().forEach( k ->
                System.out.println(
                        "" + k + " | Next:" + t.get( k ).n + " | Cost:" + t.get( k ).c +
                                " | Time:" + t.get( k ).t
                )
        );
        System.out.println();
        // Original Routing table.
        originT.keySet().forEach( k ->
                System.out.println(
                        "" + k + " | Next:" + t.get( k ).n + " | Cost:" + t.get( k ).c +
                                " | Time:" + t.get( k ).t
                )
        );
        System.out.println();

        // Run this router.
        s.start();
        bCaster.start();
        timer.start();

        System.out.println( "Route: " + localHost + " initialization complete----->\n" );
    }

    //----------------------------------------------------------
    // Command Line
    //----------------------------------------------------------

    /**
     * Command line formats:
     * java com.fengkeyleaf.net.RIP -localAddr localAddress [ -port port -failureTime failureTime -sendPeriod sendPeriod -infValue infValue -mask mask ] <Other routers and initializing cost> ...
     *
     * Command line formats for neighbour routers:
     * -addr address initCost
     *
     * For example:
     * java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.30.37 -infValue 100 -failureTime 90 -sendPeriod 1 -addr 129.21.34.80 50 -addr 129.21.22.196 4
     *
     * It will start a router hosting on localAddr with RIP2 Infinite value 100,
     * and it will be down after 90 sec and its updating period is 1 sec.
     * Also this router has two neighbour routers,
     * a router hosting on 129.21.34.80 with the link cost 50 to this router.
     * and anther router hosting on 129.21.22.1960 with the link cost 4 to this router.
     * */

    private void paraphraseArgs( String[] args ) {
        for ( int i = 0; i < args.length; i++ ) {
            switch ( args[ i ] ) {
                case "-localAddr", "localAddr" -> localHost = args[ ++i ];
                case "-addr", "addr" -> originT.put( args[ ++i ], Integer.parseInt( args[ ++i ] ) );
                case "-port", "port" -> port = Integer.parseInt( args[ ++i ] );
                case "-failureTime", "failureTime" -> failureTime = Integer.parseInt( args[ ++i ] ) * 1000;
                case "-sendPeriod", "sendPeriod" -> period = Integer.parseInt( args[ ++i ] )  * 1000;
                case "-infValue", "infValue" -> infinite = Integer.parseInt( args[ ++i ] );
                case "-mask", "mask" -> mask = args[ ++i ];
            }
        }

        // Cost to this router itself is 0.
        originT.put( localHost, 0 );

        // Build the initializing routing table.
        originT.keySet().forEach( k ->
                t.put( k,
                        new Data(
                                originT.get( k ), k, System.currentTimeMillis()
                        ) )
        );
    }

    // Local testing network environment.
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.1 -addr 127.0.0.2 15
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.2 -addr 127.0.0.1 15

    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.1 -infValue 100 -failureTime 40 -addr 127.0.0.2 15
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.2 -infValue 100 -failureTime 10 -addr 127.0.0.1 15

    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.1 -addr 127.0.0.2 15 -addr 127.0.0.3 4
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.2 -addr 127.0.0.1 15 -addr 127.0.0.3 1
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.3 -addr 127.0.0.1 4 -addr 127.0.0.2 1

    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.1 -infValue 100 -addr 127.0.0.2 50 -addr 127.0.0.3 4
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.2 -infValue 100 -addr 127.0.0.1 50 -addr 127.0.0.3 1
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.3 -infValue 100 -addr 127.0.0.1 4 -addr 127.0.0.2 1

    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.1 -infValue 100 -addr 127.0.0.2 50 -addr 127.0.0.4 4
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.2 -infValue 100 -addr 127.0.0.1 50 -addr 127.0.0.3 1
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.3 -infValue 100 -addr 127.0.0.4 10 -addr 127.0.0.2 1
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.4 -infValue 100 -addr 127.0.0.1 4 -addr 127.0.0.3 10

    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.1 -infValue 100 -failureTime 100 -addr 127.0.0.2 50 -addr 127.0.0.4 4
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.2 -infValue 100 -failureTime 80 -addr 127.0.0.1 50 -addr 127.0.0.3 1
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.3 -infValue 100 -failureTime 10 -addr 127.0.0.4 10 -addr 127.0.0.2 1
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 127.0.0.4 -infValue 100 -failureTime 40 -addr 127.0.0.1 4 -addr 127.0.0.3 10

    // Real testing network environment.
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.30.37 -infValue 100 -failureTime 100 -addr 129.21.34.80 50 -addr 129.21.22.196 4
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.34.80 -infValue 100 -failureTime 80 -addr 129.21.30.37 50 -addr 129.21.37.49 1
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.37.49 -infValue 100 -failureTime 10 -addr 129.21.22.196 10 -addr 129.21.34.80 1
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.22.196 -infValue 100 -failureTime 40 -addr 129.21.30.37 4 -addr 129.21.37.49 10

    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.30.37 -infValue 100 -addr 129.21.34.80 50 -addr 129.21.22.196 4
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.34.80 -infValue 100 -addr 129.21.30.37 50 -addr 129.21.37.49 1
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.37.49 -infValue 100 -addr 129.21.22.196 10 -addr 129.21.34.80 1
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.22.196 -infValue 100 -addr 129.21.30.37 4 -addr 129.21.37.49 10

    // queeg.cs.rit.edu
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.30.37 -infValue 100 -failureTime 90 -sendPeriod 1 -addr 129.21.34.80 50 -addr 129.21.22.196 4
    // comet.cs.rit.edu
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.34.80 -infValue 100 -failureTime 70 -sendPeriod 1 -addr 129.21.30.37 50 -addr 129.21.37.49 1
    // rhea.cs.rit.edu
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.37.49 -infValue 100 -failureTime 30 -sendPeriod 1 -addr 129.21.22.196 10 -addr 129.21.34.80 1
    // glados.cs.rit.edu
    // java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.22.196 -infValue 100 -failureTime 50 -sendPeriod 1 -addr 129.21.30.37 4 -addr 129.21.37.49 10

    public static
    void main( String[] args )
            throws SocketException, UnknownHostException {
        // TODO: 11/15/2022 new Thread( new RIP( args ) ).start(); ?
        new RIP( args ).run();
    }
}
