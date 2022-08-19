package coding.leetcode.hard;

/*
 * WordLadder.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 8/7/2022$
 */

import java.util.*;

/**
 * <a href="https://leetcode.com/problems/word-ladder/">127. Word Ladder</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public final class WordLadder {

    final TreeSet<String> m = new TreeSet<>();
    final TreeSet<String> d = new TreeSet<>();

    final static char a = 'a';
    final static int n = 26;

    public int ladderLength( String beginWord, String endWord,
                             List<String> wordList ) {

        m.add( beginWord );
        m.addAll( wordList );

        Queue<String> q = new LinkedList<>();
        q.add( beginWord );
        d.add( beginWord );
        int curSize = 0, preSize = 1, c = 0;

        while ( !q.isEmpty() ) {
            String s1 = q.poll();
            if ( s1.equals( endWord ) ) return c + 1;

            for ( int i = 0; i < s1.length(); i++ ) {
                for ( int j = 0; j < n; j++ ) {
                    String s2 = s1.substring( 0, i ) + Character.toString( a + j ) + s1.substring( i + 1 );
                    if ( m.contains( s2 ) && !d.contains( s2 ) ) {
                        curSize++;
                        q.add( s2 );
                        d.add( s2 );
                    }
                }
            }

            if ( --preSize <= 0 ) {
                preSize = curSize;
                curSize = 0;
                c++;
            }
        }

        return 0;
    }


    public static
    void main( String[] args ) {
//        System.out.println( new WordLadder().ladderLength( "hit", "hog", Arrays.stream( new String[] { "hot", "hog" } ).toList() ) );
        System.out.println( new WordLadder().ladderLength( "hit", "cog", Arrays.stream( new String[] { "hot","dot","dog","lot","log","cog" } ).toList() ) );
        System.out.println( new WordLadder().ladderLength( "hit", "cog", Arrays.stream( new String[] { "hot","dot","dog","lot","log" } ).toList() ) );
        System.out.println( new WordLadder().ladderLength( "hit", "hit", Arrays.stream( new String[] {} ).toList() ) );

    }
}
