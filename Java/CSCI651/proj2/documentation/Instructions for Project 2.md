# Instructions for Project 2

## 1. Compilation

My program for Project 2 only uses Java stand library, [JDK 17](https://docs.oracle.com/en/java/javase/17/docs/api/index.html). So no need to import any external libraries. And you may notice that there are so many files provided since I integrated this project into my own library. So I recommend that you start looking into the entry file first, RIP.java:

```
com\fengkeyleaf\net\RIP.class
```

 and also most of relevant files are located in the following package: 

```java
package com.fengkeyleaf.net;
```

Test demo video: [Link Portal](https://youtu.be/fIM9glXJfS8).

## 2. Feed with input data

To use command line, one first needs to go to the "src" folder and then follow the following command line formats:

> javac ./com/fengkeyleaf/net/*.java

> java com.fengkeyleaf.net.RIP -localAddr localAddress [ -port port -failureTime failureTime -sendPeriod sendPeriod -infValue infValue -mask mask ] \<Other routers and initializing cost> ...
>
> Command line formats for neighbour routers:
> -addr address initCost

| Identifier               | Value            | Description                                 | Example         | Default                 |
| ------------------------ | ---------------- | ------------------------------------------- | --------------- | ----------------------- |
| -localAddr/localAddr     | localAddress     | Host to this router                         | 129.21.30.37    |                         |
| -port/port               | port             | Port this router is listening on (Optional) | 1234            | 1234                    |
| -failureTime/failureTime | failureTime      | Time to be down after starting. (Optional)  | 90              | -1( meaning no failure) |
| -sendPeriod/sendPeriod   | sendPeriod       | Updating period in sec (Optional)           | 1               | 30                      |
| -infValue/infValue       | infValue         | Value to be seen as unreachable (Optional)  | 16              | 100                     |
| -mask/mask               | mask             | Subnet mask (Optional)                      | 255.255.255.0   | 255.255.255.0           |
| -addr/addr               | address initCost | Nieghbour router's ip                       | 129.21.22.196 4 |                         |

For example,

>  javac ./com/fengkeyleaf/net/*.java

> java -enableassertions com.fengkeyleaf.net.RIP -localAddr 129.21.30.37 -infValue 100 -failureTime 90 -sendPeriod 1 -addr 129.21.34.80 50 -addr 129.21.22.196 4

It will start a router hosting on localAddr with RIP2 Infinite value 100, and it will be down after 90 sec and its updating period is 1 sec. Also this router has two neighbour routers, a router hosting on 129.21.34.80 with the link cost 50 to this router. and anther router hosting on 129.21.22.1960 with the link cost 4 to this router.

Note: 

1. With arguments in IDEA, you need pre-append "src/" to the input file path.
2. java.lang.ClassNotFoundException may occur because some java files have not been complied and linked to the program, so we have to compile them manually. But this would not happen since I provided all complied files in the zipped file along with other files, unless one wants to modify and re-compile. 

At this point, nothing to explain and I plan to use similar configuration here for other assignments as well. Thanks for reading, let me know if there are anything ambiguous.



@author: Xiaoyu Tongyang. 10/11/2022
