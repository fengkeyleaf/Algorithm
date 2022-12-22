# Instructions for Project 1

## 1. Compilation

My program for Project 1 only uses Java stand library, [JDK 17](https://docs.oracle.com/en/java/javase/17/docs/api/index.html). So no need to import any external libraries. And you may notice that there are so many files provided since I integrated this project into my own library. So I recommend that you start looking into the entry file first, Pktsniffer.java:

```
project1\src\CSCI651\proj1\Pktsniffer.java
```

 and also most of relevant files are located in the following package: 

```java
package com.fengkeyleaf.io.pkts;
```

## 2. Feed with input data

To use command line, one first needs to go to the "src" folder and then follow the following command line formats:

> javac CSCI651/proj1/Pktsniffer.java

> java CSCI651/proj1/Pktsniffer -r inputFilePath [ -c numberOfPkts -host hostName -port portName -tcp -udp -icmp -net netName ]

| Identifier | Value         | Description                                                  | Example                        | Default |
| ---------- | ------------- | ------------------------------------------------------------ | ------------------------------ | ------- |
| -r/r       | inputFilePath | Path to the input file                                       | CSCI651/proj1/updTcpIcmp3.pcap |         |
| -c/c       | numberOfPkts  | Number of packets to be analyzed (Optional)                  | 5                              |         |
| -port/port | portName      | Source or destination port of the packet (Optional)          | 443                            |         |
| -host/host | hostName      | Source or destination of the packet (Optional)               | 10.230.205.207                 |         |
| -ip/ip     |               | Protocol type is IPv4? (Optional)                            |                                |         |
| -tcp/tcp   |               | Ipv4 protocol is TCP? (Optional)                             |                                |         |
| -udp/udp   |               | Ipv4 protocol is UDP? (Optional)                             |                                |         |
| -icmp/icmp |               | Ipv4 protocol is ICMP? (Optional)                            |                                |         |
| -net/net   | netName       | Source or destination address of the packet has the network number ( Up to four numbers ) (Optional) | 183.47.102                     |         |

For example,

>  javac CSCI651/proj1/Pktsniffer.java

> java CSCI651/proj1/Pktsniffer -r CSCI651/proj1/updTcpIcmp3.pcap -c 5 -host 10.230.205.207 and -icmp or -port 443 or not -udp

As we can see, we first compile the main entry java file, Pktsniffer.java, and link other imported files to the program. It will read data from the file ( named updTcpIcmp3.pcap ) located at "src/CSCI651/proj1/", and only reads 5 packets satisfying the requirement which is defined by the boolean expression:

> ( ( -host 10.230.205.207 ) and ( -icmp ) ) or ( -port 443 ) or ( not -udp ) )

meaning that display pkts whose host is 10.230.205.207 **AND** contains icmp protocol, **OR** pkts whose port is 443, **OR** whose protocol is **NOT** udp. 

Note: 

1. With arguments in IDEA, you need pre-append "src/" to the input file path.
2. java.lang.ClassNotFoundException may occur because some java files have not been complied and linked to the program, so we have to compile them manually. But this would not happen since I provided all complied files in the zipped file along with other files, unless one wants to modify and re-compile. 

At this point, nothing to explain and I plan to use similar configuration here for other assignments as well. Thanks for reading, let me know if there are anything ambiguous.



@author: Xiaoyu Tongyang. 09/06/2022
