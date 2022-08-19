package coding.leetcode.medium;

/*
 * MinimumGeneticMutation.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/29/2022$
 */

import java.util.LinkedList;
import java.util.TreeMap;

/**
 * <a href="https://leetcode.com/problems/minimum-genetic-mutation/">433. Minimum Genetic Mutation</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public class MinimumGeneticMutation {
    TreeMap<String, String> map = new TreeMap<>();
    TreeMap<String, Boolean> memo = new TreeMap<>();

    public int minMutation( String start, String end, String[] bank ) {
        buildMap( start, bank );

        if ( !map.containsKey( end ) ) return -1;

        LinkedList<String> q = new LinkedList<>();
        q.add( start );
        int c = 0;
        int preSize = 1, curSize = 0;

        while ( !q.isEmpty() ) {
            String g = q.poll();
            if ( g.equals( end ) ) return c;
            memo.put( g, true );

            assert g.length() == 8;
            for ( int i = 0; i < 8; i++ ) {
                curSize += generate( q, g, i );
            }

            if ( --preSize <= 0 ) {
                c++;

                preSize = curSize;
                curSize = 0;
            }
        }

        return -1;
    }

    static final String[] L = new String[] { "A", "C", "G", "T" };
    private int generate( LinkedList<String> q, String g, int i ) {
        int c = 0;

        for ( String m : L ) {
            String s = g.substring( 0, i ) + m + g.substring( i + 1 );
            if ( !map.containsKey( s ) || memo.containsKey( s ) ) continue;

            q.add( s );
            c++;
        }

        return c;
    }

    private void buildMap( String start, String[] bank ) {
        map.put( start, start );

        for ( String s : bank ) {
            map.put( s, s );
        }
    }

    public static
    void main( String[] args ) {
        System.out.println( new MinimumGeneticMutation().minMutation( "AACCGGTT", "AACCGGTA", new String[]{ "AACCGGTA" } ) );
        System.out.println( new MinimumGeneticMutation().minMutation( "AACCGGTT", "AAACGGTA", new String[]{ "AACCGGTA","AACCGCTA","AAACGGTA" } ) );
        System.out.println( new MinimumGeneticMutation().minMutation( "AAAAACCC", "AACCCCCC", new String[]{ "AAAACCCC","AAACCCCC","AACCCCCC" } ) );
        System.out.println( new MinimumGeneticMutation().minMutation( "AAAAACCC", "AAAAACCC", new String[]{ "AAAACCCC","AAACCCCC","AACCCCCC" } ) );
        System.out.println( new MinimumGeneticMutation().minMutation( "AAAAACCC", "AAAAACCC", new String[]{} ) );
        System.out.println( new MinimumGeneticMutation().minMutation( "AAAAACCC", "AACCCCCC", new String[]{ "AACCCCCC" } ) );
        System.out.println( new MinimumGeneticMutation().minMutation( "AACCGGTT", "AAACGGTA", new String[]{ "AACCGATT","AACCGATA","AAACGATA","AAACGGTA" } ) );
    }
}
