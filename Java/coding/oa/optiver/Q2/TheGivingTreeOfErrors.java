package coding.oa.optiver.Q2;

/*
 * TheGivingTreeOfErrors.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/9/2022$
 */

import com.fengkeyleaf.io.ProcessingFile;
import com.fengkeyleaf.io.ReadFromStdOrFile;
import com.fengkeyleaf.util.graph.Graph;
import com.fengkeyleaf.util.graph.Vertex;

import java.util.*;
import java.util.regex.Pattern;

/**
 * The Giving Tree Of Errors
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public class TheGivingTreeOfErrors
        implements ProcessingFile {

    private static final int E1 = 1; // invalid input format
    private static final int E2 = 2; // duplicate pair
    private static final int E3 = 3; // parent has more than two children
    private static final int E4 = 4; // multiple roots
    private static final int E5 = 5; // input contains cycle
    private static final int NO_ERROR = 6; // no error.
    private int e = NO_ERROR; // current error level.
    private String[] pairs;
    private Graph<GraphTreeNode> d;

    public TheGivingTreeOfErrors( String filename ) {
        ReadFromStdOrFile.readFromFile( filename, this );

        doTheAlgorithm();
    }

    /**
     * Tree-like graph vertex.
     */

    static class GraphTreeNode extends Vertex {
        final char letter;
        GraphTreeNode l;
        GraphTreeNode r;

        GraphTreeNode( char letter ) {
            this.letter = letter;
        }

        void addSubtree( GraphTreeNode c ) {
            if ( l == null && r == null ) {
                r = c;
                return;
            }

            assert l == null || r == null;
            List<GraphTreeNode> N = new ArrayList<>( 2 );
            N.add( l == null ? r : l );
            N.add( c );
            // make sure lexicographically smaller.
            N.sort( Comparator.comparingInt( n2 -> n2.letter ) );
            l = N.get( 0 );
            r = N.get( 1 );
            assert l.letter < r.letter;
        }

        @Override
        public String toString() {
            if ( l == null && r == null )
                return  "(" + letter + ")";

            assert r != null;
            if ( l == null )
                return "(" + letter + r + ")";

            return "(" + letter + l + r + ")";
        }
    }

    @Override
    public void processingFile( Scanner sc ) {
        String input = sc.hasNextLine() ? sc.nextLine() : null;
        // empty input file.
        if ( input == null ) return;

        // Has more one line.
        // Assumption here, those two are considered has only one line:
        // 1) content
        // 2) content\n
        if ( sc.hasNextLine() ) {
            e = Math.min( e, E1 );
            return;
        }

        // Leading or tailing whitespace
        if ( input.startsWith( " " ) || input.endsWith( " " ) ) {
            e = Math.min( e, E1 );
            return;
        }

        pairs = input.split( " " );
        // Invalid node pair.
        if ( isNotValidPair( pairs ) )
            e = Math.min( e, E1 );
    }

    private static
    boolean isNotValidPair( String[] pairs ) {
        for ( String p : pairs )
            if ( !Pattern.matches( "\\([A-Z],[A-Z]\\)", p ) )
                return true;

        return false;
    }

    private final TreeMap<Character, GraphTreeNode> m = new TreeMap<>();

    void doTheAlgorithm() {
        if ( e < NO_ERROR ) {
            System.out.print( "E" + e );
            return;
        }

        // empty input file.
        if ( pairs == null ) return;

        buildTheTree( pairs );

        if ( e < NO_ERROR ) {
            System.out.print( "E" + e );
            return;
        }

        GraphTreeNode r = findTheRoot();

        if ( e < NO_ERROR ) {
            System.out.print( "E" + e );
            return;
        }

        if ( d.containsCycle() ) {
            System.out.print( "E" + ( e = E5 ) );
            return;
        }

        assert r != null;
        System.out.print( r );
    }

    private void buildTheTree( String[] pairs ) {
        for ( String pair : pairs ) {
            assert pair.length() == 5;

            GraphTreeNode p = getNode( pair.charAt( 1 ) );
            assert 'A' <= pair.charAt( 1 ) && pair.charAt( 1 ) <= 'Z';
            GraphTreeNode c = getNode( pair.charAt( 3 ) );
            assert 'A' <= pair.charAt( 3 ) && pair.charAt( 3 ) <= 'Z';

            if ( isNotValidPair( p, c ) ) return;

            // build the link between the parent and its child,
            // meaning doubly linked node in this context.
            // parent -> child.
            p.add( c );
            c.add( p );
            // child -> parent.
            c.parent = p;
            // add subtree.
            p.addSubtree( c );
        }

        d = new Graph<>( m.values() );
    }

    private boolean isNotValidPair( GraphTreeNode p, GraphTreeNode c ) {
        assert p.getNeighbours().size() < 3;

        // duplicate pair.
        if ( p.getNeighbours().contains( c ) ) {
            e = Math.min( e, E2 );
            return true;
        }

        // more than two children.
        if ( p.getNeighbours().size() == 2 ) {
            e = Math.min( e, E3 );
            return true;
        }

        return false;
    }

    // one char mapped to only one node,
    // one-to-one mapping.
    private GraphTreeNode getNode( char l ) {
        if ( m.containsKey( l ) ) return m.get( l );

        GraphTreeNode n = new GraphTreeNode( l );
        m.put( l, n );
        return n;
    }

    private GraphTreeNode findTheRoot() {
        GraphTreeNode r = null;
        for ( GraphTreeNode n : m.values() ) {
            // find a root.
            if ( n.parent == null ) {
                // more than one root.
                if ( r != null ) {
                    e = Math.min( e, E4 );
                    return null;
                }

                r = n;
            }
        }

        assert r != null;
        return r;
    }

    private static final String PREFIX = "src/coding/oa/optiver/Q2/";

    // -enableassertions
    static
    void test() {
//        new TheGivingTreeOfErrors( PREFIX + 1 );
//        new TheGivingTreeOfErrors( PREFIX + 2 );
//        new TheGivingTreeOfErrors( PREFIX + 3 );
//        new TheGivingTreeOfErrors( PREFIX + 4 );
        new TheGivingTreeOfErrors( PREFIX + 5 ); // more than one child, E3
//        new TheGivingTreeOfErrors( PREFIX + 6 ); // contains cycle, E5
//        new TheGivingTreeOfErrors( PREFIX + 7 ); // invalid format, pair, E1
//        new TheGivingTreeOfErrors( PREFIX + 8 ); // multiple roots, E4
//        new TheGivingTreeOfErrors( PREFIX + 9 ); // invalid format, more than one line, E1
//        new TheGivingTreeOfErrors( PREFIX + 10 ); // invalid format, lowercase letter, E1
//        new TheGivingTreeOfErrors( PREFIX + 11 ); // invalid format, parent-child not single whitespace, E1
//        new TheGivingTreeOfErrors( PREFIX + 12 ); // duplicate pair., E2
//        new TheGivingTreeOfErrors( PREFIX + 13 ); // multiple-layer errors. E2
    }

    public static
    void main( String args[] ) throws Exception {
        // local test.
//        test();

        // For online judge.
        /* Enter your code here. Read input from STDIN. Print output to STDOUT */
        new TheGivingTreeOfErrors( "src/coding/oa/optiver/1" );
    }
}
