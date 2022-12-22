package com.fengkeyleaf.net;

/*
 * Router.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/25/2022$
 */

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

abstract class Router {
    abstract void send() throws IOException;
    abstract void receive();
    abstract void update( String data );
}
