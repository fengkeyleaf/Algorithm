package coding.oa.optiver;

/*
 * DateBetween.java
 *
 * JDK: 17
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 basic operations on 9/9/2022$
 */

/**
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since 1.1
 */

public class DateBetween {

    /*
     * Complete the function below.
     */

    static
    int DaysBetween( int year1, int month1,
                     int day1, int year2,
                     int month2, int day2
    ) throws Exception {
        if ( isIllegalDate( year1, month1, day1 ) || isIllegalDate( year2, month2, day2 ) )
            throw new IllegalArgumentException( "Input Date is invalid." );

        // we won't calculate the days one month by one month,
        // we first compute the days for the whole years,
        // and subtract redundant months,
        // and then subtract redundant days.

        // days for the whole years.
        int days = computeYears( year1, year2 );
        // remove redundant months.
        days -= computeMonthsBefore( year1, month1 );
        days -= computeMonthAfter( year2, month2 );
        // remove redundant days.
        days -= 1 - day1;
        days -= DaysInMonth( month2, year2 ) - ( day2 - 1 );

        assert days >= 0;
        return days;
    }

    private static
    boolean isIllegalDate( int y, int m, int d ) {
        return y < 0 || m < 0 || m > 12 || d < 0 || d > 31;
    }

    private static
    int computeMonthAfter( int year2, int month2 ) throws Exception {
        int c = 0;
        for ( int i = month2 + 1; i < 13; i++ ) {
            c += DaysInMonth( i, year2 );
        }

        return c;
    }

    private static
    int computeMonthsBefore( int year1, int month1 ) throws Exception {
        int c = 0;
        for ( int i = 1; i < month1; i++ ) {
            c += DaysInMonth( i, year1 );
        }

        return c;
    }

    private static final int LEAR_YEAR_DAYS = 366;
    private static final int NORMAL_YEAR_DAYS = 365;

    private static
    int computeYears( int year1, int year2 ) {
        int c = 0;
        for ( ; year1 <= year2; year1++ ) {
            c += isLeapYear( year1 ) ? LEAR_YEAR_DAYS : NORMAL_YEAR_DAYS;
        }

        return c;
    }

    static
    boolean isLeapYear( int year ) {
        return ( year % 4 == 0 && year % 100 != 0 ) || ( year % 400 == 0 );
    }

    static
    int DaysInMonth( int month, int year ) throws Exception {
        return 0;
    }

    public static
    void main( String[] args ) {
        System.out.println( isLeapYear( 2010 ) );
        System.out.println( isLeapYear( 2011 ) );
    }
}