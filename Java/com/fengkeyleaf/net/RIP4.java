package com.fengkeyleaf.net;

/*
 * RIP4.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/29/2022$
 */

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// java -enableassertions com.fengkeyleaf.net.RIP4
final class RIP4 extends RIP {
    static final String LOCAL_HOST = "127.0.0.4";

    RIP4() throws SocketException, UnknownHostException {
        super( LOCAL_HOST );

        RIP1.buildTable( t, new String[] {
                LOCAL_HOST + " " + 0 + " " + LOCAL_HOST,
                RIP1.LOCAL_HOST + " " + 15 + " " + RIP1.LOCAL_HOST,
                RIP2.LOCAL_HOST + " " + 15 + " " + RIP2.LOCAL_HOST,
                RIP3.LOCAL_HOST + " " + 1 + " " + RIP3.LOCAL_HOST
        } );
        RIP1.buildTableOrigin( originT, new String[] {
                LOCAL_HOST + " " + 0,
                RIP1.LOCAL_HOST + " " + 15,
                RIP2.LOCAL_HOST + " " + 15,
                RIP3.LOCAL_HOST + " " + 1
        } );

        run();
    }

    public static
    void main( String[] args )
            throws SocketException, UnknownHostException {
        new RIP4();
    }
}
