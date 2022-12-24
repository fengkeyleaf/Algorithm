# Instructions for Lab3

## 1. Compilation

My program for Lab 3 only uses built-in python libraries, [Version 3.9.4](https://docs.python.org/3.9/). So no need to import any external libraries. 

## 2. Usage

### 2.1 Ping

> python Xt1643Ping.py [ -c count -i wait -s packetsize -t timeout ] hostName

| Identifier | Value      | Description                                                  | Example | Default     |
| ---------- | ---------- | ------------------------------------------------------------ | ------- | ----------- |
| -c         | count      | Stop after sending (and receiving) count ECHO_RESPONSE packets. If this option is not specified, ping will operate until interrupted. (Optional) | 5       | 3           |
| -i         | wait       | Wait wait seconds between sending each packet. The default is 1 to wait for one second between each packet. (Optional) | 2       | 1           |
| -s         | packetsize | Specify the number of data bytes to be sent. The default is 56, which translates into 64 ICMP data bytes when combined with the 8 bytes of ICMP header data. (Optional) | 100     | 56          |
| -t         | timeout    | Specify a timeout, in seconds, before ping exits regardless of how many packets have been received.(Optional) | 5       | Equals wait |

For example,

>  python Xt1643Ping.py -c 5 -i 6 -s 1000 -t 6 google.com

it will ping the host named google.com 5 times and send 1000 bytes data with 5 sec wait time as well as 6 sec timeout time.

### 2.2 Traceroute

> python Xt1643Traceroute.py [ -n -S -s packetsize -q nqueries -m max_ttl -w waittime ] hostName

| Identifier | Value    | Description                                                  | Example | Default |
| ---------- | -------- | ------------------------------------------------------------ | ------- | ------- |
| -n         |          | Print hop addresses numerically rather than symbolically and numerically. (Optional) |         |         |
| -S         |          | Print a summary of how many probes were not answered for each hop. (Optional) |         |         |
| -q         | nqueries | Set the number of probes per ttl to nqueries. The default value is 3. (Optional) | 1       | 3       |
| -m         | max_ttl  | Specifies the maximum number of hops (max time-to-live value) traceroute will probe. The default is 64. (Optional) | 30      | 64      |
| -w         | waittime | Set the time (in seconds) to wait for a response to a probe (default 5.0 sec). (Optional) | 1       | 5       |

For example,

> python Xt1643Traceroute.py -n -S -q 5 google.com

it will traceroute to google.com, sending 5 probe packets per hob, and only Print hop addresses numerically, and print a summary of how many probes were not answered for each hop.

At this point, nothing to explain and I plan to use similar configuration here for other assignments as well. Thanks for reading, let me know if there are anything ambiguous.

Note:

1. For Windows users, please turn the firewall down. Recommend that Domain, Private and Public network firewalls are all closed when testing.

 

@author: Xiaoyu Tongyang. 10/28/2022
