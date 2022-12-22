package coding.oa.optiver;

/*
 * WorestTradeReporter.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 11/30/2022$
 */

import java.util.TreeMap;

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public class WorstTradeReporter {

    static class PnlCalculator {
        class Trade {
            long tradeID;
            String instrumentId;
            BuySell buySell;
            long price;
            long volume;

            Trade( long tradeID, String instrumentId, BuySell buySell, long price, long volume ) {
                this.tradeID = tradeID;
                this.instrumentId = instrumentId;
                this.buySell = buySell;
                this.price = price;
                this.volume = volume;
            }
        }
        final TreeMap<String, Long> i = new TreeMap<>();
        final TreeMap<Long, Trade> t = new TreeMap<>();

        public void processTrade( long tradeId, String instrumentId, BuySell buySell, long price, long volume ) {
            t.put( tradeId, new Trade( tradeId, instrumentId, buySell, price, volume ) );
        }

        public void processPriceUpdate( String instrumentId, long price ) {
            i.put( instrumentId, price );
        }

        private static final String NO_BAD = "NO BAD TRADES";

        // returns the output string to be printed
        String outputWorstTrade( String instrumentId ) {
            Trade tr = null;
            long n = -1;
            for ( Trade v : t.values() ) {
                long l = -1;
                if ( v.buySell == BuySell.BUY ) {
                    l = ( i.get( instrumentId ) - v.price ) * v.volume;
                    if ( l < 0 && Math.abs( l ) > n ) {
                        n = Math.abs( l );
                        tr = v;
                    }

                    continue;
                }

                assert v.buySell == BuySell.SELL;
                l = ( v.price - i.get( instrumentId ) ) * v.volume;
                if ( l < 0 && Math.abs( l ) > n ) {
                    n = Math.abs( l );
                    tr = v;
                }
            }

            return tr == null ? NO_BAD : String.valueOf( tr.tradeID );
        }
    }

    enum BuySell {
        BUY,
        SELL
    }
}
