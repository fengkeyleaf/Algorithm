package CSCI651.proj2;

/*
 * BattleShip.java
 *
 * Version:
 *     $1.2$
 *
 * Revisions:
 *     $Adapted to hw 13.2 on 11/21/2020$
 */

// The main idea here is use a 2-D integer array to represent the battlefield.
// In particular, 1 <= id <= 128(stand for ships) and 0(stands for ocean) are unrevealed areas to the player,
// so print a single dot '.' when encountering them.
// On the other hand, -1 when hit ships and print a 'x' correspondingly. Similarly, -2 when hit ocean and print a 'w'.

// The names of test files match the following format: b_numbers.txt

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This program simulates a battleship game
 *
 * @author Xiaoyu  Tongyang
 * @author Chenxuan li
 */

public class BattleShip {
    private int[][] ocean;                         // 2-D integer array to stimulate the battlefield or the ocean
    private int leftAreas;                         // remained coordinates that haven't been hit yet
    private int leftShips;                         // remained coordinates of ships that haven't been hit yet
    private int columnsOcean = -1;                 // how many columns the battlefield has
    private int rowsOcean = -1;                    // how many rows the battlefield has

    private String hostname;                       // hostname
    private int port;                              // port
    private boolean goFirst;                       // determine which machine is a server
    private String filename;                       // name of file

    private String battleFieldString;              // string stands for battlefield
    private Scanner battleFieldParser = null;      // scanner to read
    private final String lineDelimiter = "#";      // delimiter represents a new line

    private DatagramSocket serverSocket;           // DatagramSocket used to send or receive data
    private final int maxByteLength = 1024;        // maximum length of a buf used to send or receive data
    private byte[] buf = new byte[ maxByteLength ];  // the byte array to send or receive data

    /**
     * print the ocean or the battlefield
     */

    private void printOcean() {
        // print column indicator
        System.out.printf( "%-5s", "" );
        for ( int i = 0; i < columnsOcean; i++ ) {
            System.out.print( i + " " );
        }
        System.out.println( " ---> columns" );

        // print the ocean and row indicator
        for ( int i = 0; i < ocean.length; i++ ) {
            System.out.printf( "%d%-3s", i, ":" );

            for ( int j = 0; j < ocean[ 0 ].length; j++ ) {
                if ( ocean[ i ][ j ] >= 0 ) { // print '.' when 1 <= id <= 128 and 0
                    System.out.print( " " + '.' );
                } else if ( ocean[ i ][ j ] == -1 ) { // print 'x' when hit a ship
                    System.out.print( " " + 'x' );
                } else { // print 'w' when hit ocean
                    System.out.print( " " + 'w' );
                }
            }

            System.out.println();
        }
    }

    /**
     * initialize a board or a battlefield
     */

    private void initializeBoard() {
        this.ocean = new int[ this.rowsOcean ][ this.columnsOcean ];
        this.leftAreas = this.rowsOcean * this.columnsOcean;
    }

    /**
     * read a ship's ID and convert it into an integer, and store into the 2-D array
     *
     * @param row    the row we are at now
     * @param scLine the scanner used to read the file
     */

    private void setShipsOnOcean( int row, Scanner scLine ) {
        int column = 0;
        String thing;

        while ( scLine.hasNext() ) {
            thing = scLine.next();

            if ( thing.charAt( 0 ) == 'b' ) {
                ocean[ row ][ column ] = Integer.parseInt( thing.substring( 1 ) );
                this.leftShips++;
            }

            column++;
        }
    }

    /**
     * check whether the row and column the player typed in contains only numbers and at least one number
     *
     * @param row string standing for rows
     * @param col string standing for columns
     * @return true, valid row and column; false, invalid
     */

    private boolean checkGuessValid( String row, String col ) {
        return Pattern.matches( "\\d+", row ) && Pattern.matches( "\\d+", col );
    }

    /**
     * check whether the row and column the player typed in are valid or not, in terms of the battlefield
     *
     * @param row rows in the form of integer
     * @param col columns in the form of integer
     * @return true, valid row and column; false, invalid
     */

    private boolean checkHitOnOcean( int row, int col ) {
        return row >= 0 && row < this.rowsOcean && col >= 0 && col < this.columnsOcean;
    }

    /**
     * iterate the 2-D array to check the player is hitting either ocean or a ship or nothing
     *
     * @param row rows in the form of integer
     * @param col columns in the form of integer
     */

    private void hitShips( int row, int col ) {
        if ( !checkHitOnOcean( row, col ) ) {
            System.out.println( "The hit is out of the ocean range!" );
            return;
        }

        // hit the places that have been hit before
        if ( ocean[ row ][ col ] < 0 ) {
            return;
        } else if ( ocean[ row ][ col ] == 0 ) { // hit the ocean
            ocean[ row ][ col ] = -2;
            this.leftAreas--;
            return;
        }

        // hit a ship and find all its coordinates and set them to -1
        System.out.println( "HIT!\n" );
        int shipID = ocean[ row ][ col ];

        for ( int i = 0; i < ocean.length; i++ ) {
            for ( int j = 0; j < ocean[ 0 ].length; j++ ) {
                if ( ocean[ i ][ j ] == shipID ) {
                    ocean[ i ][ j ] = -1;
                    this.leftAreas--;
                    this.leftShips--;
                }
            }
        }
    }

    /**
     * read the battlefield file and get the information about width, height or ships' ID, ect.
     *
     * @param scFile the scanner used to read the file
     */

    private void readFromFile( Scanner scFile ) {
        int rowCount = 0;

        // after initializing the battlefield, set flag to false to avoid repeating to initialize the battlefield
        boolean flag = true;

        while ( scFile.hasNext() ) {
            // scanner to process a line read from the file
            Scanner scLine = new Scanner( scFile.nextLine() );

            switch ( scLine.next() ) {
                case "width":
                    this.columnsOcean = Integer.parseInt( scLine.next() );
                    break;
                case "height":
                    this.rowsOcean = Integer.parseInt( scLine.next() );
                    break;
                case "row":
                    setShipsOnOcean( rowCount++, scLine );
                    break;
                default:
                    System.out.println( "Cannot reach here!" );
                    System.exit( 1 );
                    break;
            }

            // have gotten the rows and columns and need to initialize the battlefield
            if ( flag && this.columnsOcean != -1 && this.rowsOcean != -1 ) {
                initializeBoard();
                flag = false;
            }

            scLine.close();
        }

        scFile.close();
    }

    /**
     * replace lineDelimiter with "\n" and build a read battlefield
     */

    private void readBattleFieldFromString( String theBattleFieldInStringForm ) {   // new
        // discard useless data from theBattleFieldInStringForm
        int first = theBattleFieldInStringForm.indexOf( '$' );
        int last = theBattleFieldInStringForm.lastIndexOf( '$' );
        theBattleFieldInStringForm = theBattleFieldInStringForm.substring( first + 1, last ).
                replaceAll( lineDelimiter, "\n" );

        battleFieldParser = new Scanner( theBattleFieldInStringForm );
        this.readFromFile( battleFieldParser );
    }

    /**
     * the main code body to execute the game
     */

    public void startPlayingGame() {
        if ( this.columnsOcean <= 0 || this.rowsOcean <= 0 ) {
            System.err.println( "The battlefield is null!" );
            System.exit( 1 );
        }

        // open the battlefield file
        Scanner scUser = new Scanner( System.in );

        String row; // store the row that the player types in
        String col; // store the column that the player types in

        // start playing the game
        do {
            System.out.printf( "\n%s\n%s\n\n", "x indicates a hit.",
                    "w indicates a miss, but you know now there is water." );
            printOcean();

            do {
                // get row and column from the player
                System.out.printf( "%s%d%s", "row  coordinate 0 <= row  < ", this.rowsOcean, "): " );
                row = scUser.next();
                System.out.printf( "%s%d%s", "column coordinate 0 <= column < ", this.columnsOcean, "): " );
                col = scUser.next();
            } while ( !checkGuessValid( row, col ) ); // check if the row and column from user are valid or not

            hitShips( Integer.parseInt( row ), Integer.parseInt( col ) );

        } while ( this.leftShips > 0 && this.leftAreas > 0 );

        // Game over, print the ending message
        System.out.println( "Game ends, all boats have been hit!\n" );
        printOcean();

        scUser.close();
    }

    /**
     * set command line arguments
     */

    private void parseArgs( String[] args ) {   // new
        for ( int i = 0; i < args.length; i++ ) {
            switch ( args[ i ] ) {
                case "-hostname":
                    this.hostname = args[ ++i ];
                    break;
                case "-port":
                    this.port = Integer.parseInt( args[ ++i ] );
                    break;
                case "-ocean":
                    this.filename = args[ ++i ];
                    break;
                case "-first":
                    this.goFirst = true;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * read battlefield from file and convert to a string
     */

    private void readBattleFieldFile() {   // new
        try {
            battleFieldParser = new Scanner( new File( this.filename ) );
            // the starting symbol marking the beginning of the input file
            StringBuilder oceanBoard = new StringBuilder( "$" );

            while ( battleFieldParser.hasNextLine() ) {
                oceanBoard.append( battleFieldParser.nextLine() ).append( lineDelimiter );
            }

            if ( battleFieldParser != null ) {
                battleFieldParser.close();
                battleFieldParser = null;
            }

            // the ending symbol marking the end of the input file
            oceanBoard.append( "$" );
            battleFieldString = oceanBoard.toString();
        } catch ( FileNotFoundException e ) {
            System.out.println( "Can't find that file! Try Again." );
        }

    }

    /**
     * convert an integer into a byte
     *
     * @param number an integer needed to convert into a byte array
     */

    private byte[] toBytes( int number ) {
        byte[] bytes = new byte[ 4 ];
        bytes[ 0 ] = ( byte ) number;
        bytes[ 1 ] = ( byte ) ( number >> 8 );
        bytes[ 2 ] = ( byte ) ( number >> 16 );
        bytes[ 3 ] = ( byte ) ( number >> 24 );
        return bytes;
    }

    /**
     * convert a byte array into a integer
     *
     * @param bytes an integer in the form of byte array
     */

    private int toInt( byte[] bytes ) {
        int number = 0;
        for ( int i = 0; i < 4; i++ ) {
            number += bytes[ i ] << ( i * 8 );
        }
        return number;
    }

    /**
     * divide a specific byte array to multiple ones with the length of maxByteLength
     *
     * @param buf battleFieldString in the form of byte array
     */

    private byte[][] processBytes( byte[] buf ) {
        int howMany = ( ( ( buf.length / this.maxByteLength ) * this.maxByteLength ) == buf.length ) ?
                buf.length / this.maxByteLength : buf.length / this.maxByteLength + 1;
        byte[][] newBuf = new byte[ howMany ][ this.maxByteLength ];

        // store the sequence into the front four bytes in order to make sure delivered packages are in order
        int sequence = 0;
        for ( int i = 0; i < howMany; i++ ) {
            byte[] temp = this.toBytes( sequence++ );
            int idx = 0;
            System.arraycopy( temp, 0, newBuf[ i ], 0, 4 );
        }

        // store the battleFieldString into the 2-D byte array
        int index = 0;
        for ( int i = 0; i < howMany; i++ ) {
            for ( int j = 4; j < this.maxByteLength; j++ ) {
                newBuf[ i ][ j ] = buf[ index++ ];
                if ( index >= buf.length ) return newBuf;
            }
        }

        return newBuf;
    }

    /**
     * send data to DatagramPacket of a server or client
     */

    private void readData( DatagramPacket packet ) {    // new
        System.out.println( "readData: " );
        StringBuilder oceanBoard = new StringBuilder();
        List<Storage> myStorage = new ArrayList<>();

        try {
            this.serverSocket.setSoTimeout( 5000 );

            while ( true ) {
                try {
                    this.serverSocket.receive( packet );
                    byte[] temp = packet.getData();
                    myStorage.add( new Storage( this.toInt( temp ), Arrays.copyOfRange( temp, 4, temp.length ) ) );
                } catch ( SocketTimeoutException e ) {
                    break;
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        // make sure that the packages are in order
        Collections.sort( myStorage );
        for ( Storage aStorage : myStorage ) {
            oceanBoard.append( aStorage.toString() );
        }

        this.readBattleFieldFromString( oceanBoard.toString() );
    }

    /**
     * read data from DatagramPacket of a server or client
     */

    private void sendData( DatagramPacket packet ) {    // new
        System.out.println( "sendData: " );
        byte[][] newBuf = this.processBytes( this.battleFieldString.getBytes() );

        try {
            for ( byte[] bytes : newBuf ) {
                packet.setData( bytes );
                this.serverSocket.send( packet );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }

    /**
     * set server or client
     */

    private void setUpIO() {   // new
        if ( this.goFirst ) {
            try {
                this.serverSocket = new DatagramSocket( port );
            } catch ( Exception e ) {
                e.printStackTrace();
            }

            System.out.println( "Listening on port: "
                    + this.serverSocket.getLocalPort() );

            DatagramPacket packet = new DatagramPacket( buf, buf.length );  // receiving data
            this.readData( packet );

            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            packet = new DatagramPacket( buf, buf.length, address, port ); // sending data
            this.sendData( packet );
        } else {
            try {
                System.out.println( "Client is being built!" );
                this.serverSocket = new DatagramSocket();
                InetAddress aInetAddress = InetAddress.getByName( this.hostname );
                DatagramPacket packet = new DatagramPacket( buf,  // sending data
                        buf.length, aInetAddress, port );
                this.sendData( packet );

                Thread.sleep( 1000 );
                DatagramPacket dp = new DatagramPacket( buf, buf.length );  // receiving data
                this.readData( dp );

            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        this.serverSocket.close();
    }

    /**
     * initialize server or client and read ocean from file
     *
     * @param args command line arguments
     */

    public void preprocess( String[] args ) {   // new
        this.parseArgs( args );
        this.readBattleFieldFile();
        this.setUpIO();
        this.startPlayingGame();
    }

    /**
     * The main program.
     *
     * @param args command line arguments, in the form of -hostname 127.0.0.1 -port 4567 -first -ocean b_1.txt
     */

    public static void main( String[] args ) {
        new BattleShip().preprocess( args );
    }
}
