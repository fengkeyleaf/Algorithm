"""
file: Xt1643Traceroute.py
description: Build your own traceroute named your_CS_ID_traceroute with the following options.
language: python 3.9.4
author:  Xiaoyu Tongyang
email: xt1643@ RIT.EDU
"""

import os
import socket
import struct
import sys
import time
import _socket
import select

import Xt1643Ping

# reference material:
# https://github.com/eureyuri/traceroute

##############################################
# Global variables
##############################################
ICMP_ECHO_REPLY = 0
ICMP_TIME_EXCEEDED = 11

_h = None
_s = None
_d = None

"""
-w waittime
Set the time (in seconds) to wait for a response to a probe (default 5.0 sec).
"""
_timeout = 5

"""
-q nqueries
Set the number of probes per ttl to nqueries. The default value is 3.
"""
_nqueries = 3

"""
-n
Print hop addresses numerically rather than symbolically and numerically.
"""
_isOnlyNumerical = False

"""
-S
Print a summary of how many probes were not answered for each hop.
"""
_isSummaryUnanswered = False

"""
-m max_ttl
Specifies the maximum number of hops (max time-to-live value) traceroute will probe. 
The default is 64.
"""
_maxTtl = 64

##############################################
# Functions
##############################################


def _get_domain_name( a ):
    """
    Get the name of the router from a given IP ( Optional Excercise )
    :param a: address name
    :return: domain name if exists.
    """
    # https://docs.python.org/3.9/library/socket.html?highlight=gethostbyaddr#socket.gethostbyaddr
    # https://docs.python.org/3.9/library/socket.html?highlight=herror#socket.herror
    try:
        return _socket.gethostbyaddr( a )[ 0 ]
    except _socket.herror:  # Domain name Unknown
        return a


def _traceroute_( ttl ):
    """
    Sending and receiving process.
    :return: ( pinged, final, domain_name, addr_name )
    """
    _s.sendto(
        Xt1643Ping.get_packet( os.getpid() & 0xFFFF, 0 ),
        ( _d, 0 )  # ( dest, port )
    )

    t = _timeout
    time_sent = time.time()
    started_select = time.time()
    what_ready = select.select( [ _s ], [ ], [ ], t )
    time_in_select = time.time() - started_select

    if what_ready[ 0 ] == [] or t - time_in_select <= 0:  # Timeout
        print( "* ", end = "" )
        return False, False, None, None

    time_received = time.time()

    rec_packet, addr = _s.recvfrom( 1024 )
    icmp_header = rec_packet[ 20:28 ]
    icmp_type, code, checksum, packetID, sequence = struct.unpack( "bbHHh", icmp_header )
    domain_name = "" if _isOnlyNumerical else _get_domain_name( addr[ 0 ] )
    time_spent = ( time_received - time_sent ) * 1000

    if icmp_type == ICMP_TIME_EXCEEDED:  # TTL is 0
        print( "%d ms " % time_spent, end = "" )
        return True, False, domain_name, addr[ 0 ]
    elif icmp_type == ICMP_ECHO_REPLY:  # Final destination replied
        print( "%d ms " % time_spent, end = "" )
        return True, addr[ 0 ] == _d, domain_name, addr[ 0 ]

    # Handle other icmp_type
    print( "%d ms " % time_spent, end = "" )
    return True, False, domain_name, addr[ 0 ]


def _traceroute( ttl ):
    """
    Trace route with ttl
    :param ttl: Time to live
    :return: ( count, final )
    """
    # https://docs.python.org/3.9/library/socket.html?highlight=setsockopt#socket.socket.setsockopt
    _s.setsockopt( _socket.IPPROTO_IP, _socket.IP_TTL, struct.pack( 'I', ttl ) )
    # https://docs.python.org/3.9/library/socket.html?highlight=settimeout#socket.socket.settimeout
    _s.settimeout( _timeout )

    print( str( ttl ), end = "\t" )
    c = 0 # how many prob packets were answered.
    # final, domain_name, addr_name
    f, d, a = ( False, None, None )
    for _ in range( _nqueries ):
        s, f, d_, a_ = _traceroute_( ttl )
        if s:
            c += 1

        # Once get the address information, keep it.
        d = d_ if d is None else d
        a = a_ if a is None else a

    sm = "(" + str( _nqueries - c ) + " probes not answered out of " + str( _nqueries ) + ")" \
        if _isSummaryUnanswered \
        else ""

    if c <= 0:
        print( "Request timed out %s" % sm )
    else:
        print(
            "%s[%s] %s" %
            (
                "" if _isOnlyNumerical else d + " ",
                a,
                sm
            )
        )

    return c, f


def _traceroutes():
    """
    Try trace routes all the way to the destination.
    :return: None
    """
    global _d
    try:
        _d = socket.gethostbyname( _h )
    except socket.gaierror:
        print( "Ping request could not find host %s. Please check the name and try again." % _h )
        exit( 1 )

    # Starting tracing routers.
    print(
        "Traceroute to %s [%s] with %d max hops:\n" %
        ( _h, _d, _maxTtl )
    )

    # https://docs.python.org/3.9/library/socket.html?highlight=getprotobyname#socket.getprotobyname
    global _s
    # SOCK_RAW is a powerful socket type. For more details:
    # http://sock-raw.org/papers/sock_raw
    _s = socket.socket(
        socket.AF_INET,
        socket.SOCK_RAW,
        socket.getprotobyname( "icmp" )
    )

    # Increment ttl by one at a time.
    for ttl in range( 1, _maxTtl + 1 ):
        # count, final, domain_name, addr_name, time
        c, f = _traceroute( ttl )
        # Reached the final destination.
        if f:
            print( "\nTrace complete" )
            break

        # A hop timed out and exceeded the max hubs.
        if c <= 0 and ttl == _maxTtl:
            print( "\nTimeout: Exceeded " + str( _maxTtl ) + " hops" )

        assert ttl <= _maxTtl

    _s.close()


def _check_args():
    """
    Check command line arguments.
    :return: None
    """
    if _nqueries < 1:
        raise ValueError( "the number of probes cannot be less than 1." )

    if _maxTtl < 0:
        raise ValueError( "timeout cannot be negative." )


def _paraphrase_args( argv ):
    """
    paraphrase command line arguments.

    Command line formats:
    python Xt1643Traceroute.py [ -n -S -s packetsize -q nqueries -m max_ttl -w waittime ] hostName

    For example:
    python Xt1643Traceroute.py -n -S -q 5 google.com

    it will traceroute to google.com, sending 5 probe packets per hob,
    and only Print hop addresses numerically,
    and print a summary of how many probes were not answered for each hop.

    :param argv: command line arguments
    :return: None
    """
    i = 1
    while i < len( argv ) - 1:
        if argv[ i ] == "-q":
            global _nqueries
            _nqueries = int( argv[ i + 1 ] )
            i += 1
        elif argv[ i ] == "-n":
            global _isOnlyNumerical
            _isOnlyNumerical = True
        elif argv[ i ] == "-S":
            global _isSummaryUnanswered
            _isSummaryUnanswered = True
        elif argv[ i ] == "-m":
            global _maxTtl
            _maxTtl = int( argv[ i + 1 ] )
            i += 1
        elif argv[ i ] == "-w":
            global _timeout
            _timeout = int( argv[ i + 1 ] )
            i += 1

        i += 1

    _check_args()

    # https://www.w3schools.com/python/ref_keyword_assert.asp
    assert i == len( argv ) - 1, str( i ) + " " + str( len( argv ) )
    global _h
    _h = argv[ i ]

# python Xt1643Traceroute.py google.com
# python Xt1643Traceroute.py roame.net
# python Xt1643Traceroute.py study.163.com

# python Xt1643Traceroute.py -n google.com
# python Xt1643Traceroute.py -S google.com
# python Xt1643Traceroute.py -q 1 google.com
# python Xt1643Traceroute.py -q 5 -S 157.240.19.35
# python Xt1643Traceroute.py -S study.163.com
# python Xt1643Traceroute.py -n -S -q 5 google.com
# python Xt1643Traceroute.py -S -q 5 google.com


# TODO: not test -w waittime and -m max_ttl
if __name__ == '__main__':
    _paraphrase_args( sys.argv )
    _traceroutes()
