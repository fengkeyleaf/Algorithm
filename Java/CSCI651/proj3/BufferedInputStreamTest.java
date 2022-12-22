package CSCI651.proj3;

/*
 * BufferedInputStreamTest.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/10/2022$
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://blog.csdn.net/wenzhi20102321/article/details/52583551
public class BufferedInputStreamTest {

    /**
     * 高效输入流的使用 BufferedInputStream的使用
     */
    public static void main( String[] args ) {

        // 定义一个输入流对象

        // 定义一个存放输入流的缓冲对象

        // 定义一个输出流，相当StringBuffer（），会根据读取数据的大小，调整byte的数组长度


        try ( FileInputStream fis = new FileInputStream( "src/CSCI651/proj3/1" );
              BufferedInputStream bis = new BufferedInputStream( fis );
              ByteArrayOutputStream baos = new ByteArrayOutputStream()
            ) {
            // 把文件路径和文件名作为参数 告诉读取流

            // 把文件读取流对象传递给缓存读取流对象

            // 获得缓存读取流开始的位置
            int len = -1;

            // 定义一个容量来盛放数据
            byte[] buf = new byte[ 1024 ];

            while ( ( len = bis.read( buf ) ) != -1 ) {
                // 如果有数据的话，就把数据添加到输出流
                // 这里直接用字符串StringBuffer的append方法也可以接收
                baos.write( buf, 0, len );
                System.out.println( len );
            }

            // 把文件输出流的数据，放到字节数组
            // 打印输出
            System.out.println( baos );

        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }
}
