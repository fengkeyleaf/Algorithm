package coding.leetcode.medium;

/*
 * CourseSchedule.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/27/2022$
 */

import com.fengkeyleaf.util.graph.DAG;
import com.fengkeyleaf.util.graph.Vertex;

import java.util.TreeMap;

/**
 * <a href="https://leetcode.com/problems/course-schedule/">207. Course Schedule</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public class CourseSchedule {

    public boolean canFinish( int numCourses, int[][] prerequisites ) {
        return prerequisites.length == 0 || !buildGraph( prerequisites ).getTopologicalOrdering().isEmpty();
    }

    TreeMap<Integer, Vertex> m = new TreeMap<>();

    private DAG<Vertex> buildGraph( int[][] P ) {
        for ( int[] p : P ) {
            Vertex v1 = getVertex( p[ 0 ] );
            Vertex v2 = getVertex( p[ 1 ] );

            v1.add( v2 );
        }

        DAG<Vertex> d = new DAG<>( m.values() );
        return d;
    }

    private Vertex getVertex( int i ) {
        if ( m.containsKey( i ) )
            return m.get( i );

        Vertex v = new Vertex();
        m.put( i, v );
        return v;
    }

    public static
    void main( String[] args ) {
//        System.out.println( new CourseSchedule().canFinish( 2, new int[][]{ { 1, 0 } } ) );
//        System.out.println( new CourseSchedule().canFinish( 2, new int[][]{ { 1, 0 }, { 0, 1 } } ) );
//        System.out.println( new CourseSchedule().canFinish( 2, new int[][]{} ) );
        System.out.println( new CourseSchedule().canFinish( 2, new int[][]{ { 1, 1 } } ) );
    }
}
