package CGTsinghua;

/*
 * MainCG.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 4/30/2022$
 */

/**
 * class to generate input data file path
 * for the programming assignments of CG TsingHua at edX.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 */

public class MainCG {

    /**
     * get input file path for
     * @see <a href="https://www.edx.org/course/computational-geometry">computational geometry</a>
     * by TsinghuaX on edX
     * */

    protected static
    String getFilePathCG( int homeworkID, int problemID, int fileName ) {
        return "src/CGTsinghua/PA_" + homeworkID + "/problem_" + problemID + "/" + fileName;
    }

    protected static
    String getFilePathCG( int homeworkID, int problemID, int fileName, String prefix ) {
        return "src/CGTsinghua/PA_" + homeworkID + "/problem_" + problemID + "/" + prefix + fileName;
    }

    protected static
    String getFilePathCG( int homeworkID, int problemID, String fileName, String prefix ) {
        return "src/CGTsinghua/PA_" + homeworkID + "/problem_" + problemID + "/" + prefix + fileName;
    }
}
