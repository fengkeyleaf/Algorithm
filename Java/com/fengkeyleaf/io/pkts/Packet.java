package com.fengkeyleaf.io.pkts;

/*
 * Packet.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/2/2022$
 */

import java.util.Arrays;

/**
 * Data structure of Packet.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public record Packet (
        PacketHeader header,
        PacketData data
) {

    //-------------------------------------------------------
    // filtering Operations.
    //-------------------------------------------------------

    // Reference resource:
    // https://www.tcpdump.org/manpages/pcap-filter.7.html
    // https://juejin.cn/post/6844904084168769549

    /**
     * dst host hostnameaddr
     *
     * @return True if the IPv4/v6 destination field of the packet is hostnameaddr,
     * which may be either an address or a name.
     * */

    public boolean containsDesHost( String desHostNameAddr ) {
        return Arrays.equals( data.iPv4().desIPAddr(), getIpFromStr( desHostNameAddr ) );
    }

    /**
     * src host hostnameaddr
     *
     * @return True if the IPv4/v6 source field of the packet is hostnameaddr.
     * */

    public boolean containsSrcHost( String srcHostNameAddr ) {
        return Arrays.equals( data.iPv4().srcIPAddr(), getIpFromStr( srcHostNameAddr ) );
    }

    /**
     * host hostnameaddr
     *
     * Any of the above host expressions can be prepended with the keywords,
     * ip, arp, rarp, or ip6 as in:
     *
     * ip host hostnameaddr
     *
     * which is equivalent to:
     *
     * ether proto \ip and host hostnameaddr
     *
     * If hostnameaddr is a name with multiple IPv4/v6 addresses,
     * each address will be checked for a match.
     *
     * @return True if either the IPv4/v6 source or
     *         destination of the packet is hostnameaddr.
     * */

    public boolean containsHost( String hostNameAddr ) {
        return containsDesHost( hostNameAddr ) || containsSrcHost( hostNameAddr );
    }

    private static final int ALL_IP = 256;

    static
    int[] getIpFromStr( String hostNameAddr ) {
        String[] N = hostNameAddr.split( "\\." );
        assert N.length <= 4 && N.length > 0;
        int[] ip = new int[ 4 ];
        Arrays.fill( ip, ALL_IP );

        for ( int i = 0; i < N.length; i++ ) {
            assert Integer.parseInt( N[ i ] ) > -1 && Integer.parseInt( N[ i ] ) < 256;
            ip[ i ] = Integer.parseInt( N[ i ] );
        }

        return ip;
    }

    /**
     * src port portnamenum
     *
     * @return True if the packet has a source port value of portnamenum.
     * */

    public boolean containsSrcPort( int srcPortNameNum ) {
        IPv4 iPv4 = data.iPv4();

        return switch ( iPv4.protocolNum() ) {
            case IPv4.TCP -> iPv4.protocol().tcp().srcPort() == srcPortNameNum;
            case IPv4.UDP -> iPv4.protocol().udp().srcPort() == srcPortNameNum;
            default -> false;
        };
    }

    /**
     * port portnamenum
     *
     * @return True if either the source or destination port of the packet is portnamenum.
     * */

    public boolean containsDesPort( int desPortNameNum ) {
        IPv4 iPv4 = data.iPv4();

        return switch ( iPv4.protocolNum() ) {
            case IPv4.TCP -> iPv4.protocol().tcp().desPort() == desPortNameNum;
            case IPv4.UDP -> iPv4.protocol().udp().desPort() == desPortNameNum;
            default -> false;
        };
    }

    /**
     * port portnamenum
     *
     * @return True if either the source or
     *         destination port of the packet is portnamenum.
     * */

    public boolean containsPort( int portNameNum ) {
        return containsDesPort( portNameNum ) || containsSrcPort( portNameNum );
    }

    /**
     * ip proto protocol
     *
     * <p>Protocol can be a number or one of the names recognized by getprotobyname(3)
     * (as in e.g. `getent(1) protocols'), typically from an entry in /etc/protocols,
     * for example: ah, esp, eigrp (only in Linux, FreeBSD, NetBSD, DragonFly BSD, and macOS),
     * icmp, igmp, igrp (only in OpenBSD), pim, sctp, tcp, udp or vrrp.
     * Note that most of these example identifiers are also keywords and must be escaped via backslash (\).
     * Note that this primitive does not chase the protocol header chain.</p>
     *
     * @return True if the packet is an IPv4 packet (see ip(4P)) of protocol type protocol.
     * */

    public boolean containsIpProtocol( int protocol ) {
        return switch ( data.iPv4().protocolNum() ) {
            case IPv4.ICMP -> protocol == Protocol.ICMP;
            case IPv4.TCP -> protocol == Protocol.TCP;
            case IPv4.UDP -> protocol == Protocol.UDP;
            default -> false;
        };
    }

    /**
     * Is this packet a IPv4 protocol?
     */

    public boolean isIPv4() {
        return PacketData.isIPv4( data.type() );
    }


    /**
     * Abbreviation for:
     *
     * ip proto 1
     * */

    public boolean isICMP() {
        return containsIpProtocol( Protocol.ICMP );
    }

    /**
     * Abbreviation for:
     *
     * ip proto 6
     * */

    public boolean isTCP() {
        return containsIpProtocol( Protocol.TCP );
    }

    /**
     * Abbreviation for:
     *
     * ip proto 17
     * */

    public boolean isUDP() {
        return containsIpProtocol( Protocol.UDP );
    }

    /**
     * dst net netnameaddr
     *
     * Net may be either a name from the networks database (/etc/networks, etc.) or a network number.
     * An IPv4 network number can be written as a dotted quad (e.g., 192.168.1.0), dotted triple (e.g., 192.168.1),
     * dotted pair (e.g, 172.16), or single number (e.g., 10);
     * the netmask is 255.255.255.255 for a dotted quad (which means that it's really a host match),
     * 255.255.255.0 for a dotted triple, 255.255.0.0 for a dotted pair, or 255.0.0.0 for a single number.
     * An IPv6 network number must be written out fully; the netmask is ff:ff:ff:ff:ff:ff:ff:ff,
     * so IPv6 "network" matches are really always host matches, and a network match requires a netmask length.
     *
     * @return True if the IPv4/v6 destination address of the packet has a network number of netnameaddr.
     * */

    public boolean containsDesNet( String desNetNameAddr ) {
        return containsNet( data.iPv4().desIPAddr(), getIpFromStr( desNetNameAddr ) );
    }

    static
    boolean containsNet( int[] targetIp, int[] ip ) {
        for ( int i = 0; i < targetIp.length; i++ )
            if ( ip[ i ] != targetIp[ i ] &&
                    ip[ i ] != ALL_IP ) return false;

        return true;
    }

    /**
     * src net netnameaddr
     *
     * @return True if the IPv4/v6 source address of the packet has a network number of netnameaddr.
     * */

    public boolean containsSrcNet( String srcNetNameAddr ) {
        return containsNet( data.iPv4().srcIPAddr(), getIpFromStr( srcNetNameAddr ) );
    }

    /**
     * net netnameaddr
     *
     * @return True if either the IPv4/v6 source or destination address of the packet
     *         has a network number of netnameaddr.
     * */

    public boolean containsNet( String netNameAddr ) {
        return containsDesNet( netNameAddr ) || containsSrcNet( netNameAddr );
    }
}
