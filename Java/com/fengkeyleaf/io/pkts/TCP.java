package com.fengkeyleaf.io.pkts;

/*
 * TCP.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/4/2022$
 */

import com.fengkeyleaf.lang.MyMath;
import com.fengkeyleaf.util.MyArrays;
import com.fengkeyleaf.util.MyBitSet;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Data structure of TCP layer, Transmission Control Protocol.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

//            source port            |             Destination port
//                            Sequence number
//                      Acknowledgment number (if ACK set)
// Data offset | Reserved | NS, CWR, ECE, URG, ACK, PSH, RST, SYN, FIN | Window Size
//             Checksum              |         Urgent pointer (if URG set)
//    Options (if data offset > 5. Padded at the end with "0" bits if necessary.)
//                                  Data

//https://en.wikipedia.org/wiki/Transmission_Control_Protocol
public record TCP (
        long srcPort, // 16b
        long desPort, // 16b
        long seqNum, // 32b
        long ackNum, // 32b
        long headerLen, // 4b, in words, result = headerLne * 4 B
        String reserved, // 3b
        String flags, // 9b
        long windowSize, // 16b
        long checksum, // 16b
        String urgentPtr, // 16b
        // TODO: 11/12/2022 Add data field: Options
        byte[] data
) {

    /**
     * Get a TCP with give pkt byte data.
     *
     * @param B pkt data in bytes.
     * @return TCP initialized with give bytes
     */

    public static
    TCP getTCP( byte[] B ) {
        // Fixed part of TCP header.
        byte[] fixedPart = Arrays.copyOfRange( B, 0, 20 );
        String bits = IPv4.getBits( fixedPart );

        assert bits.length() == 160;
        return new TCP(
                MyMath.binaryToDecimal( bits.substring( 0, 16 ) ), // srcPort
                MyMath.binaryToDecimal( bits.substring( 16, 32 ) ), // desPort
                MyMath.binaryToDecimal( bits.substring( 32, 64 ) ), // seqNum
                MyMath.binaryToDecimal( bits.substring( 64, 96 ) ), // ackNum
                MyMath.binaryToDecimal( bits.substring( 96, 100 ) ), // headerLen
                bits.substring( 100, 103 ), // reserved
                bits.substring( 103, 112 ), // flags
                MyMath.binaryToDecimal( bits.substring( 112, 128 ) ), // windowSize
                MyMath.binaryToDecimal( bits.substring( 128, 144 ) ), // checksum
                MyMath.binaryToHex( bits.substring( 144, 160 ) ), // urgentPtr
                B.length > 20 ? Arrays.copyOfRange( B, 20, B.length ) : null // data
        );
    }

    /**
     * Tells if this TCP pkt enabled SYN.
     *
     * @return
     */

    public boolean isSyn() {
        assert flags.charAt( 7 ) == '0' || flags.charAt( 7 ) == '1' && flags.charAt( 8 ) == '0';
        return flags.charAt( 7 ) == '1';
    }

    /**
     * Tells if this TCP pkt enabled FIN.
     *
     * @return
     */

    public boolean isFin() {
        assert flags.charAt( 8 ) == '0' || flags.charAt( 8 ) == '1' && flags.charAt( 7 ) == '0';
        return flags.charAt( 8 ) == '1';
    }

    /**
     * Tells if this TCP pkt enabled ACK.
     *
     * @return
     */

    public boolean isAck() {
        return flags.charAt( 4 ) == '1';
    }

    /**
     * Convert this tcp pkt into bytes.
     *
     * @return Byte array presenting this tcp pkt.
     */

    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/ByteBuffer.html
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/ByteBuffer.html#array()
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/ByteBuffer.html#putShort(short)
    // TODO: 11/21/2022 ignore urgentPtr.
    // TODO: 11/13/2022 convert checksum and urgentPtr
    // TODO: 11/13/2022 struct.pack() in python to get byte[].
    public byte[] getBytes() {
        // Encode header to bytes.
        ByteBuffer b = ByteBuffer.allocate( ( int ) ( headerLen * 4 ) );
        b.putShort( ( short ) srcPort ); // 16b, 2B
        b.putShort( ( short ) desPort ); // 16b, 2B
        b.putInt( ( int ) seqNum ); // 32b, 4B
        b.putInt( ( int ) ackNum ); // 32b, 4B
        assert getThree().length == 2;
        b.put( getThree() ); // data offset, reserved, flags => 16b, 2B
        b.putShort( ( short ) windowSize ); // 16b, 2B
        b.putShort( ( short ) checksum ); // 16b, 2B
        b.putShort( Short.parseShort( urgentPtr ) ); // 16b, 2B

        byte[] B = b.array();
        assert check( B );
        // Concatenate data field to get a full TCP pkt.
        return data == null || data.length == 0 ? B : MyArrays.concatAll( B, data );
    }

    /**
     * Cook the 16 bits for data offset, reserved and flags.
     *
     * @return 16 bits in byte array.
     */

    private byte[] getThree() {
        // Be careful with the reading direction of BitSet.toByteArray().
        // e.g. 0000 0000 0000 0010
        // reading direction: ---->
        // ( 0000 0000 ) ( 0000 0010 )
        // Interpret each byte from right to left.
        // So ( 0000 0010 ) => 64, not 2.

        // Now show a example to get the 16 bits.
        // e.g. 0000 0000 0000 0000
        BitSet b = new BitSet( 16 );
        // Assume headerLen = 5, 0101 in binary.
        // Note that: 101 when performing Integer.toBinaryString( 5 );
        assert Integer.toBinaryString( ( int ) headerLen ).length() < 5;
        // 0000 0000 0000 0000
        // headerLen: Set bits from 12th to 15th.
        // 0000 0000 0000 1010
        MyBitSet.setBits( b, 12, Integer.toBinaryString( ( int ) headerLen ) );
        assert flags.length() == 9 : flags;
        // reserved: always 0, skip.
        // flags: Set bits from 0th to 9th.
        // Assume flags = 00001 1000
        // 0001 1000 0000 1010
        MyBitSet.setBits( b, 0, flags );
        // Bits to bytes.
        // ( 0001 1000 ) ( 0000 1010 ) - bits
        // [ 24 ,80 ] - bytes
        // Need reverse it:
        // [ 80, 24 ], this is what we wanted.
        return MyArrays.reverse( MyBitSet.toByteArray( b, 2 ) );
    }

    @Override
    public String toString() {
        return "Source Port = " + srcPort +
               "\nDestination Port = " + desPort +
               "\nSequence number = " + seqNum +
               "\nAcknowledgement number = " + ackNum +
               "\nHeader Length = " + headerLen +
               "\nFlags = " + flags +
               "\nWindow Size = " + windowSize +
               "\nChecksum = 0x" + checksum +
               "\nUrgent Pointer = " + urgentPtr;
    }

    /**
     * TCP header from encoding this tcp pkt to bytes should be identical.
     */

    private boolean check( byte[] B ) {
        assert B.length == 20 : B.length;

        TCP t = getTCP( B );
        assert t.srcPort == srcPort : t.srcPort + " | " + srcPort;
        assert t.desPort == desPort;
        assert t.seqNum == seqNum;
        assert t.ackNum == ackNum;
        assert t.headerLen == headerLen : t.headerLen + " | " + headerLen;
        assert t.reserved.equals( reserved ) : t.reserved + " | " + reserved;
        assert t.flags.equals( flags ) : t.flags + " | " + flags;
        assert t.windowSize == windowSize : t.windowSize + " | " + windowSize;
        assert t.checksum == checksum : t.checksum + " | " + checksum;
//        assert t.urgentPtr.equals( urgentPtr ) : t.urgentPtr + " | " + urgentPtr;

        return true;
    }
}
