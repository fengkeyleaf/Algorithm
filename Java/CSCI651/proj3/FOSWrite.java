package CSCI651.proj3;

/*
 * FOSWrite.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/10/2022$
 */

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

// https://tobebetterjavaer.com/io/stream.html#fileoutputstream%E7%B1%BB
public class FOSWrite {
    public static void main( String[] args ) throws IOException {
        // 使用文件名称创建流对象
        FileOutputStream fos = new FileOutputStream( "src/CSCI651/proj3/1", true );
        // 字符串转换为字节数组
        byte[] b = "fengkeyleaf".getBytes();
        // 写出从索引2开始，2个字节。索引2是c，两个字节，也就是cd。
        fos.write( b );
        // 关闭资源
        fos.close();
    }
}
