package myLibraries.lang;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RadixSortList<E> {
    private final LinkedList<Integer> whichIndexHasElements = new LinkedList<>();
    public final List< LinkedList<NumberRadix> > radixes = new ArrayList<>();

    public void add( int index, NumberRadix num ) {
        radixes.get( index ).addLast( num );
        whichIndexHasElements.addLast( index );
    }

    public int getAvailableIndex() {
        return whichIndexHasElements.isEmpty() ? -1 : whichIndexHasElements.removeFirst();
    }

    public boolean isEmpty() {
        return whichIndexHasElements.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();

        for ( int i = 0; i < radixes.size(); i++ ) {
            if ( !radixes.get( i ).isEmpty() ) {
                text.append( i ).append( ": " );

                for ( NumberRadix num : radixes.get( i ) )
                    text.append( num ).append( "-> " );

                text.append( "\n" );
            }
        }

        return text.toString();
    }
}
