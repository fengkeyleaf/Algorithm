package coding.oa.optiver;

/*
 * ElephantCompetition.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/30/2022$
 */

import java.util.*;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

class ElephantCompetition {
    class Elephant {
        String name;
        int height = -1;
        int enterTime = -1;
        int exitTime = -1;

        Elephant( String name, int height ) {
            this.name = name;
            this.height = height;
        }

        Elephant( int height ) {
            this.height = height;
        }

        @Override
        public String toString() {
            return "(name='" + name +
                    ", height=" + height +
                    ", enterTime=" + enterTime +
                    ", exitTime=" + exitTime + ")";
        }
    }

    final PriorityQueue<Elephant> h1 = new PriorityQueue<>( Comparator.comparingInt( e -> e.enterTime ) );
    // Our team
    final TreeMap<Integer, Elephant> t1 = new TreeMap<>();
    final PriorityQueue<Elephant> h2 = new PriorityQueue<>( Comparator.comparingInt( e -> e.exitTime ) );
    // Other team
    final TreeMap<Integer, Elephant> t2 = new TreeMap<>();

    public ElephantCompetition(
            List<ElephantDescription> elephants,
            List<ElephantSchedule> schedule ) {
        TreeMap<String, Elephant> E = new TreeMap<>();
        /* Enter your code here. */
        elephants.forEach( e -> E.put( e.name, new Elephant( e.name, e.height ) ) );

        schedule.forEach( e -> {
            Elephant e1 = E.get( e.name );
            e1.enterTime = e.enterTime;
            e1.exitTime = e.exitTime;
        } );

        h1.addAll( E.values() );
    }

    public void elephantEntered( int currentTime, int height ) {
        /* Enter your code here. */
        if ( !h1.isEmpty() && h1.peek().enterTime == currentTime &&
                h1.peek().height == height ) {
            Elephant e = h1.poll();
            t1.put( e.height, e );
            h2.add( e );
            return;
        }

        Elephant e = new Elephant( height );
        t2.put( height, e );
    }

    public void elephantLeft( int currentTime, int height ) {
        System.out.println( currentTime + " " + height + h2.peek().exitTime + " " + h2.peek().height + " " );
        /* Enter your code here. */
        if ( !h2.isEmpty() && h2.peek().exitTime == currentTime &&
                h2.peek().height == height ) {
            Elephant e = h2.poll();
            t1.remove( e.height );
            return;
        }

        t2.remove( height );
    }

    public List<String> getBiggestElephants() {
        ArrayList<String> S = new ArrayList<>();
        if ( t2.isEmpty() ) return S;
        /* Enter your code here. */
        Integer k = t2.lastKey();
        System.out.println( t2 );
        if ( t1.lastKey() < k ) return S;

        t1.tailMap( k ).values().forEach( e -> S.add( e.name ) );
        S.sort( String::compareTo );
        return S;
    }

    class ElephantDescription {
        public String name;
        public int height;
    }

    class ElephantSchedule {
        public String name;
        public int enterTime;
        public int exitTime;
    }
}
