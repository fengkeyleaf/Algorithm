package CSCI651.proj2;

/*
 * Storage.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $LOG$
 */

/**
 * a class to store data received from other matching through DatagramSocket
 *
 * @author Xiaoyu  Tongyang
 * @author Chenxuan li
 */

public class Storage implements Comparable<Storage> {
    private final int sequence;  // sequence order of this data
    private byte[] data;         // byte array stored data

    /**
     * constructor to create an instance of Storage
     *
     * @param sequence sequence order of this data
     * @param data     byte array stored data
     */

    public Storage( int sequence, byte[] data ) {
        this.sequence = sequence;
        this.data = data;
    }

    /**
     * compare two instances of Storage
     *
     * @param aStorage another instance of Storage needed to compare
     */

    @Override
    public int compareTo( Storage aStorage ) {
        return Integer.compare( this.sequence, aStorage.sequence );
    }

    /**
     * get a textural representation of this class
     */

    @Override
    public String toString() {
        return new String( this.data );
    }
}
