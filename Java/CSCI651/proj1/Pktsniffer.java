package CSCI651.proj1;

/*
 * Pktsniffer.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/2/2022$
 */

import com.fengkeyleaf.io.pkts.Packet;
import com.fengkeyleaf.io.pkts.Pacp;
import com.fengkeyleaf.util.function.BExp;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Project 1. Let’s Analyze Some Packets
 *
 * <p>Goal:
 * Write a network packet analyzer called pktsniffer
 * that reads packets and produces a detailed summary of those packets.
 * It works like tcpdump and Wireshark combined,
 * and runs as a shell command.
 * Like tcpdump, pktsniffer also allows packets to be filtered by a given Boolean expression.
 * It first reads packets, not from a network interface,
 * but from a specified file with the -r flag (like tcpdump).
 * The pktsniffer program then extracts
 * and displays the different headers of the captured packets. Specifically,
 * it displays the Ethernet header fields of the captured frames.
 * Then, if the Ethernet frame contains an IP datagram,
 * it prints the IP header. Finally,
 * it prints the packets encapsulated in the IP datagram.
 * TCP, UDP, or ICMP packets can be encapsulated in the IP packet.</p>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public final class Pktsniffer {

    private static final String AND = "and";
    private static final String OR = "or";
    private static final String NOT = "not";
    private String inputFileName;
    private int filteringNum = -1;
    private final BExp<Packet> expression;

    Pktsniffer( String[] args ) {
        int i = paraphraseArgs( args );
        // get the filtering expression.
        expression = i >= args.length - 1 ?
                BExp.getBool( null, ( p -> true ) ) :
                getExpression( Arrays.copyOfRange( args, i + 1, args.length ) );

        // get the filtered pkts.
        doTheAlgorithm();
    }

    /**
     * Command line formats:
     * java Pktsniffer -r inputFilePath [ -c numberOfPkts -host hostName -port portName -ip -tcp -udp -icmp -net netName ]
     *
     * For example:
     * java Pktsniffer -r src/CSCI651/proj1/updTcpIcmp3.pcap -c 5 -host 10.230.205.207 and -icmp or -port 443 or not -udp
     *
     * It will read data from the file ( named updTcpIcmp3.pcap ) located at "src/CSCI651/proj1/",
     * and only reads 5 packets satisfying the requirement which is defined by the boolean expression:
     *
     * ( ( -host 10.230.205.207 ) and ( -icmp ) ) or ( -port 443 ) or ( not -udp ) )
     *
     * meaning display pkts whose host is 10.230.205.207 AND contains icmp protocol,
     * OR pkts whose port is 443, OR whose protocol is NOT udp.
     *
     * And note that brackets for the boolean expression are not supported.
     * */

    private int paraphraseArgs( String[] args ) {
        int j = -1;
        for ( int i = 0; i < args.length; i++ ) {
            switch ( args[ i ] ) {
                case "-r", "r" -> inputFileName = args[ ( j = ++i ) ];
                case "-c", "c" -> filteringNum = Integer.parseInt( args[ ( j = ++i ) ] );
            }
        }

        // return the index where the boolean expression located.
        return j;
    }

    /**
     * Get the boolean expression.
     * */

    static
    BExp<Packet> getExpression( String[] E ) {
        assert E.length != 0 : Arrays.toString( E );
        assert E.length > 2 || E[ 0 ].equals( NOT ) ||
                decodeExp( E ) != null : Arrays.toString( E );

        if ( E.length <= 2 && !E[ 0 ].equals( NOT ) )
            return BExp.getBool( null, decodeExp( E ) );

        for ( int i = 0; i < E.length; i++ ) {
            if ( E[ i ].equals( OR ) ) {
                return BExp.getOr( getExpression( Arrays.copyOfRange( E, 0, i ) ),
                        getExpression( Arrays.copyOfRange( E, i + 1, E.length ) ) );
            }
        }

        for ( int i = 0; i < E.length; i++ ) {
            if ( E[ i ].equals( AND ) ) {
                return BExp.getAnd( getExpression( Arrays.copyOfRange( E, 0, i ) ),
                        getExpression( Arrays.copyOfRange( E, i + 1, E.length ) ) );
            }
        }

        for ( int i = 0; i < E.length; i++ ) {
            if ( E[ i ].equals( NOT ) ) {
                return BExp.getNot( getExpression( Arrays.copyOfRange( E, i + 1, E.length ) ) );
            }
        }

        assert false : Arrays.toString( E );
        return null;
    }

    private static
    Predicate<Packet> decodeExp( String[] E ) {
        return switch ( E[ 0 ] ) {
            case "host", "-host" -> ( p -> p.containsHost( E[ 1 ] ) );
            case "port", "-port" -> ( p -> p.containsPort( Integer.parseInt( E[ 1 ] ) ) );
            case "ip", "-ip" -> ( Packet::isIPv4 );
            case "icmp", "-icmp" -> ( Packet::isICMP );
            case "udp", "-udp" -> ( Packet::isUDP );
            case "tcp", "-tcp" -> ( Packet::isTCP );
            case "net", "-net" -> ( p -> p.containsNet( E[ 1 ] ) );
            default -> null;
        };
    }

    private void doTheAlgorithm() {
        Pacp p = new Pacp( inputFileName );
        p.filteringNum = filteringNum;

        List<Packet> packets = p.filter( expression );
        System.out.println( "# of pkts after filtering: " + packets.size() + "\n" );
        packets.forEach( pkt -> {
            System.out.println( "----- Ether Header -----" );
            System.out.println( pkt.data() );

            System.out.println( "----- IP Header -----" );
            System.out.println( pkt.data().iPv4() );

            if ( pkt.isICMP() ) {
                System.out.println( "----- ICMP Header -----" );
                System.out.println( pkt.data().iPv4().protocol().icmp() );
            }

            if ( pkt.isUDP() ) {
                System.out.println( "----- UDP Header -----" );
                System.out.println( pkt.data().iPv4().protocol().udp() );
            }

            if ( pkt.isTCP() ) {
                System.out.println( "----- TCP Header -----" );
                System.out.println( pkt.data().iPv4().protocol().tcp() );
            }

            System.out.println();
        } );
    }

    // -r src/CSCI651/proj1/updTcpIcmp3.pcap host 10.230.205.207
    // -r src/CSCI651/proj1/updTcpIcmp3.pcap not -ip
    // -r src/CSCI651/proj1/updTcpIcmp3.pcap port 443

    // -r src/CSCI651/proj1/updTcpIcmp3.pcap net 183.47.102.80
    // -r src/CSCI651/proj1/updTcpIcmp3.pcap net 183.47.102
    // -r src/CSCI651/proj1/updTcpIcmp3.pcap net 183.47
    // -r src/CSCI651/proj1/updTcpIcmp3.pcap net 183

    // -r src/CSCI651/proj1/updTcpIcmp3.pcap host 10.230.205.207 and icmp or port 443
    // -r src/CSCI651/proj1/updTcpIcmp3.pcap host -c 3 10.230.205.207 and icmp or port 443
    // -r src/CSCI651/proj1/updTcpIcmp3.pcap -host 10.230.205.207 and -icmp or -port 443 or not -udp

    // -r src/CSCI651/proj1/testicmpsingle.pcap
    // -r src/CSCI651/proj1/testtcpsingle.pcap
    // -r src/CSCI651/proj1/testudpsingle.pcap
    // -r src/CSCI651/proj1/testudpmulti.pcap
    // -r src/CSCI651/proj1/testicmpmulti.pcap
    // -r src/CSCI651/proj1/testtcpmulti.pcap
    // -r src/CSCI651/proj1/testmix.pcap port 49386 and host 34.232.2.134 or icmp

    public static
    void main( String[] args ) {
        new Pktsniffer( args );
    }
}
