package com.fengkeyleaf.io.pkts;

/*
 * Protocol.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/4/2022$
 */

/**
 * Data structure of protocol layers.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public record Protocol (
        TCP tcp, // 6
        UDP udp, // 17
        ICMP icmp // 1
) {

    public static final int ICMP = 1;
    public static final int TCP = 6;
    public static final int UDP = 17;

    public static
    Protocol getProtocol( int protocolNum, byte[] B ) {
        return switch ( protocolNum ) {
            case 1 -> new Protocol(
                    null, null, com.fengkeyleaf.io.pkts.ICMP.getICMP( B )
            );
            case 6 -> new Protocol(
                    com.fengkeyleaf.io.pkts.TCP.getTCP( B ), null, null
            );
            case 17 -> new Protocol(
                    null, com.fengkeyleaf.io.pkts.UDP.getUDP( B ), null
            );
            default -> null;
        };
    }

    @Override
    public String toString() {
        if ( tcp != null ) return tcp.toString();
        else if ( udp != null ) return udp.toString();

        assert icmp != null;
        return icmp.toString();
    }
}
