# Instructions for Project 3

## 1. Compilation

My program for Project 3 only uses Java stand library, [JDK 17](https://docs.oracle.com/en/java/javase/17/docs/api/index.html). So no need to import any external libraries. And you may notice that there are so many files provided since I integrated this project into my own library. So I recommend that you start looking into the entry files first, MyFTP.java and MySocket.java:

```java
com\fengkeyleaf\io\MyFTP.java
com\fengkeyleaf\net\MySocket.java
```

The first class is the upper-layer transportation application and the second is lower-layer, network transportation layer.nd also most of relevant files are located in the following packages: 

```java
package com.fengkeyleaf.io;
package com.fengkeyleaf.net;
```

Test demo video: Link Portal

## 2. Transfer file with the FTP program

### 2.1  Initialize a FTP program with command line

To use command line, one first needs to go to the "src" folder and then follow the following command line formats:

> javac com/fengkeyleaf/net/*.java com/fengkeyleaf/io/*.java com/fengkeyleaf/util/*.java

> java com.fengkeyleaf.net.MyFTP -srcHost srcHost -dstHost dstHost
>      [ -srcPort srcPort ]  [ -dstPort dstPort ] [ -windowSize windowSize ]
>      [ -failsSend failsSendProb ] [ -failsAck failsAckProb ]  [ -corrupt corruptProb ]
>      [ -turnOffCC ] [ -debug ] [ -warning ]
>      [ -timeoutInterval timeoutInterval ]

**Options**

**-srcHost** srcHost 

Source host bound to local machine, required.

**-dstHost** dstHost

Destination host to which the pkt is sent, required.

**-srcPort** srcPort 

Source port bound to the local machine, optional. Default value is 1234

 **-dstPort** dstPort 

Destination port to which the pkt is sent, optional. Default value is 1234

**-windowSize** windowSize

Advertised-window size, optional. Default value is 512

**-failsSend** failsSendProb

Probability of failing to send a data pkt to the receiver, optional. Default value is 0, meaning this failure is disabled.

**-failsAck** failsAckProb

Probability of failing to send an ack pkt, optional. Default value is 0, meaning this failure is disabled.

**-corrupt** corruptProb

Probability of pkt corruption, optional. Default value is 0, meaning this failure is disabled.

**-turnOffCC**

If turns off congestion control(CC), optional. Default is false, meaning CC is enabled by default.

-**debug** 

Set logging level to debug, optional. Default logging level is normal, meaning no logs printed.

**-warning**

Set logging level to warning, optional. Default logging level is normal, meaning no logs printed.

**-timeoutInterval** timeoutInterval

Set timeout interval( in seconds ) for all types of timer( timeout timer and wait-time timer ), optional. Default value is 0.2 s.

For example,

>  javac com/fengkeyleaf/net/*.java com/fengkeyleaf/io/*.java com/fengkeyleaf/util/*.java

> java -enableassertions com.fengkeyleaf.io.MyFTP -srcHost 127.0.0.2 -srcPort 1235 -dstHost 127.0.0.1 -dstPort 1234 -windowSize 40 -failsAck 80 -debug

This will start a FTP program with source host 127.0.0.2 and port 1235, with destination host 127.0.0.1 and port 1234. Also its advertised-window size is 40, probability of ack pkt loss is 80%, enabling DEBUG level logging 

Note: 

1. With arguments in IDEA, you need pre-append "src/" to the input file path.
2. java.lang.ClassNotFoundException may occur because some java files have not been complied and linked to the program, so we have to compile them manually. But this would not happen since I provided all complied files in the zipped file along with other files, unless one wants to modify and re-compile. 

### 2.2  Use FTP to transfer and receive file.

Once initializing a FTP program with the command line arguments provided above, you can use the following commands to interact with the FTP program, like transferring or receiving file.

**-c/c** 

To connect to another side. Only one of them sends connection signal is enough.

**-p/p** inputfilepath

To send file in the path inputfilepath, but no whitespaces allowed in the path.

**-g/g** outputfilepath

To receive file and store it into the path outputfilepath, but no whitespaces allowed in the path. And be sure to include the file name extension in the path, or otherwise, OS cannot open it in the correct format.

**-d/d**

Set logging level to debug.

**-w/w**

Set logging level to warning.

**-n/n**

Set logging level to normal.

**-q/q**

To quit.

**-?/?**

Print help information.

---

At this point, nothing to explain and I plan to use similar configuration here for other assignments as well. Thanks for reading, let me know if there are anything ambiguous.



@author: Xiaoyu Tongyang, 11/23/2022
