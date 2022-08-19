package coding.leetcode.medium;

/*
 * KeysAndRooms.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 7/27/2022$
 */

import java.util.LinkedList;
import java.util.List;

/**
 * <a href="https://leetcode.com/problems/keys-and-rooms/">841. Keys and Rooms</a>
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.0
 */

public class KeysAndRooms {

    class Room {
        final int id;
        final List<Integer> K;

        Room( int id, List<Integer> k ) {
            this.id = id;
            K = k;
        }
    }

    boolean[] B;

    public boolean canVisitAllRooms( List<List<Integer>> rooms ) {
        B = new boolean[ rooms.size() ];

        LinkedList<Room> q = new LinkedList<>();
        q.add( new Room( 0, rooms.get( 0 ) ) );
        while ( !q.isEmpty() ) {
            Room r = q.poll();

            if ( B[ r.id ] ) continue;
            B[ r.id ] = true;

            r.K.forEach( k -> {
                q.add( new Room( k, rooms.get( k ) ) );
            } );
        }

        return isAllUnlocked();
    }

    private boolean isAllUnlocked() {
        for ( boolean b : B ) {
            if ( !b ) return false;
        }

        return true;
    }
}
