"""
file: Xt1643Ping.py
description: Build your own ping named your_CS_ID_ping with the following options
language: python 3.9.4
author:  Xiaoyu Tongyang
email: xt1643@ RIT.EDU
"""

import binascii
import os
import socket
import struct
import sys
import time
import _socket
import select

# reference material:
# https://github.com/eureyuri/traceroute

##############################################
# Global variables
##############################################
# https://en.wikipedia.org/wiki/Internet_Control_Message_Protocol
ICMP_ECHO_REQUEST = 8

_h = None
_d = None
# https://docs.python.org/3.9/library/socket.html?highlight=socket#module-socket
# https://docs.python.org/3.9/library/socket.html?highlight=socket#creating-sockets
_s = None

"""
-c count
Stop after sending (and receiving) count ECHO_RESPONSE packets.
If this option is not specified, ping will operate until interrupted.
"""
_count = 3  # negative -> infinite ping

"""
-i wait
Wait wait seconds between sending each packet.
The default is i to wait for one second between each packet.
"""
_waitTime = 1  # sec

"""
-s packetsize
Specify the number of data bytes to be sent. 
The default is 56, which translates into 64 ICMP data bytes
when combined with the 8 bytes of ICMP header data.
"""
_packetSize = 56  # bytes

"""
-t timeout
Specify a timeout, in seconds, 
before ping exits regardless of how many packets have been received.
"""
_timeout = _waitTime  # sec


##############################################
# Functions
##############################################

def _receive( id ):
    """
    Server Side to receive sent-back data from remote server.
    :param id: OS thread ID
    :return: Respond String.
    """
    t = _timeout

    while True:
        # https://docs.python.org/3.9/library/time.html?highlight=time%20time#time.time
        startedSelect = time.time()
        # https://docs.python.org/3.9/library/select.html?highlight=select%20select#select.select
        whatReady = select.select( [ _s ], [ ], [ ], t )
        howLongInSelect = (time.time() - startedSelect)
        if whatReady[ 0 ] == [ ]:  # Timeout
            return "Request timed out. Not ready"

        timeReceived = time.time()
        # https://docs.python.org/3.9/library/socket.html?highlight=recvfrom#socket.socket.recvfrom
        # (bytes, address)
        # TODO: can receive very large packet all at once?
        recPacket, addr = _s.recvfrom( _packetSize + 28 )

        # Fetch the ICMPHeader from the received IP
        # Fill in start
        icmp_header = recPacket[ 20:28 ]
        # Fill in end

        # s char[] bytes
        rawTTL = struct.unpack( "s", bytes( [ recPacket[ 8 ] ] ) )[ 0 ]

        # binascii -- Convert between binary and ASCII
        TTL = int( binascii.hexlify( rawTTL ), 16 )

        # Fetch icmpType, code, checksum, packetID, and sequence from ICMPHeader
        # using struct.unpack method
        # Fill in start
        icmpType, code, checksum, packetID, sequence = \
            struct.unpack( "bbHHh", icmp_header )
        # Fill in end

        if packetID == id:
            # https://docs.python.org/3.9/library/struct.html?highlight=calcsize#struct.calcsize
            byte = struct.calcsize( "d" )
            timeSent = struct.unpack( "d", recPacket[ 28:28 + byte ] )[ 0 ]
            assert len( recPacket ) - 28 >= 0
            return "Reply from %s: bytes=%d sentBytes=%d time=%dms TTL=%d" % \
                   ( _d, len( recPacket ), len( recPacket ) - 28, ( timeReceived - timeSent ) * 1000, TTL )

        if t - howLongInSelect <= 0:
            return "Request timed out."


def _checksum( string ):
    """
    Calculate checksum.
    :param string: header + data
    :return: checksum string
    """
    csum = 0
    countTo = ( len( string ) // 2 ) * 2
    count = 0

    while count < countTo:
        thisVal = string[ count + 1 ] * 256 + string[ count ]
        csum = csum + thisVal
        csum = csum & 0xffffffff
        count = count + 2

    if countTo < len( string ):
        csum = csum + string[ len( string ) - 1 ]
        csum = csum & 0xffffffff

    csum = (csum >> 16) + (csum & 0xffff)
    csum = csum + (csum >> 16)
    answer = ~csum
    answer = answer & 0xffff
    answer = answer >> 8 | (answer << 8 & 0xff00)
    return answer


# TODO: XXX to append bytes
def _padding( s ):
    """
    Get padding bytes to fill the byte data to be sent.
    :return: Padding bytes
    """
    r = b''
    for i in range( s - 8 ):
        r += b'\x00' # struct.pack("d", 0) will produce 8 bytes.

    return r

def get_packet( id, s ):
    # Header is type (8 b), code (8 b), checksum (16 b), = 4 B
    # id (16 b), sequence (16 b) = 4 B
    # 4 + 4 = 8 B for the header.

    # https://docs.python.org/3.9/library/struct.html?highlight=pack#struct.pack
    # https://www.cnblogs.com/litaozijin/p/6506354.html
    # B unsigned char integer
    # H unsigned short integer
    # d double float

    # Make a dummy header with a 0 checksum
    # struct -- Interpret strings as packed binary data
    h = struct.pack( "BBHHH", ICMP_ECHO_REQUEST, 0, 0, id, 1 )  # 8 B
    d = struct.pack( "d", time.time() )  # 8 B
    p = _padding( s )

    c = _checksum( h + d + p )

    # https://docs.python.org/3.9/library/sys.html?highlight=sys%20platform#sys.platform
    # macOS 'darwin'
    # Get the right checksum, and put in the header
    if sys.platform == 'darwin':
        # Convert 16-bit integers from host to network byte order
        c = _socket.htons( c ) & 0xffff
    else:
        c = _socket.htons( c )

    return struct.pack( "bbHHh", ICMP_ECHO_REQUEST, 0, c, id, 1 ) + d + p


def _send( id ):
    """
    Client side to send pining ICMP packet.
    :param id: OS thread ID
    :return: None
    """
    # b signed char integer
    # h short integer
    # AF_INET address must be tuple, not string
    # https://docs.python.org/3.9/library/socket.html?highlight=sendto#socket.socket.sendto
    _s.sendto(
        get_packet( id, _packetSize ),
        ( _d, 1 )
    )
    # Both LISTS and TUPLES consist of a number of objects
    # which can be referenced by their position number within the object.


def _ping( ):
    """
    Ping once.
    :return: None
    """
    # https://docs.python.org/3.9/library/os.html?highlight=getpid#os.getpid
    id = os.getpid() & 0xFFFF  # Return the current process id
    _send( id )
    print( _receive( id ) )


# https://stackoverflow.com/questions/8689964/why-do-some-functions-have-underscores-before-and-after-the-function-name
def _pings( ):
    """
    Ping _count times.
    :return: None
    """
    global _d
    # https://www.w3schools.com/python/python_try_except.asp
    # https://stackoverflow.com/questions/4844654/how-to-handle-getaddrinfo-failed
    try:
        _d = socket.gethostbyname( _h )
    except socket.gaierror:
        print( "Ping request could not find host %s. Please check the name and try again." % _h )
        exit( 1 )

    print( "Pinging " + _h + " [" + _d + "] with "
           + str( _packetSize ) + " bytes of data:" )

    # https://docs.python.org/3.9/library/socket.html?highlight=getprotobyname#socket.getprotobyname
    global _s
    # SOCK_RAW is a powerful socket type. For more details:
    # http://sock-raw.org/papers/sock_raw
    _s = socket.socket(
        socket.AF_INET,
        socket.SOCK_RAW,
        socket.getprotobyname( "icmp" )
    )

    i = 0
    while _count < 0 or i < _count:
        _ping()
        if _count < 0 or i + 1 < _count:
            time.sleep( _waitTime )
        i += 1

    _s.close()


def _check_args( ):
    """
    Check command line arguments.
    :return: None
    """
    if _waitTime < 1:
        raise ValueError( "wait time cannot be less than 1." )

    if _packetSize < 36 or _packetSize > 65500:
        raise ValueError( "packet size cannot be less than 36 or greater than 65,500." )

    # if _timeout < 0 or _timeout < _waitTime:
    if _timeout < 0:
        raise ValueError( "timeout cannot be negative or less than wait time." )


def _paraphrase_args( argv ):
    """
    paraphrase command line arguments.

    Command line formats:
    python Xt1643Ping.py [ -c count -i wait -s packetsize -t timeout ] hostName

    For example:
    python Xt1643Ping.py -c 5 -i 6 -s 1000 -t 6 google.com

    it will ping the host named google.com 5 times and
    send 1000 bytes data with 5 sec wait time as well as 6 sec timeout time.

    :param argv: command line arguments
    :return: None
    """
    i = 1
    while i < len( argv ) - 1:
        if argv[ i ] == "-c":
            global _count
            _count = int( argv[ i + 1 ] )
            i += 1
        elif argv[ i ] == "-i":
            global _waitTime
            _waitTime = int( argv[ i + 1 ] )
            i += 1
        elif argv[ i ] == "-s":
            global _packetSize
            _packetSize = int( argv[ i + 1 ] )
            i += 1
        elif argv[ i ] == "-t":
            global _timeout
            _timeout = int( argv[ i + 1 ] )
            i += 1

        i += 1

    _check_args()

    # https://www.w3schools.com/python/ref_keyword_assert.asp
    assert i == len( argv ) - 1, str( i ) + " " + str( len( argv ) )
    global _h
    _h = argv[ i ]


# python Xt1643Ping.py google.com
# python Xt1643Ping.py roame.net

# python Xt1643Ping.py -c 3 google.com
# python Xt1643Ping.py -i 5 facebook.com
# python Xt1643Ping.py -s 88 store.steampowered.com
# python Xt1643Ping.py -t 2 www.capcom.com

# python Xt1643Ping.py -c 5 -i 6 -s 1000 -t 6 google.com
# python Xt1643Ping.py -c 5 -i 6 -s 1000 -t 1 google.com

if __name__ == '__main__':
    _paraphrase_args( sys.argv )
    _pings()
