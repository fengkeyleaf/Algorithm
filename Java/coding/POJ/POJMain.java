package coding.POJ;

/*
 * POJMain.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/3/2022$
 */

/**
 * class to generate input data file path for POJ.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

public class POJMain {

    /**
     * get input file path for
     * @see <a href="http://poj.org/">POJ</a>
     * */

    public static
    String getFilePathPOJ( int problemID, int fileName ) {
        return "src/coding/POJ/ID_" + problemID + "/" + fileName;
    }
}
