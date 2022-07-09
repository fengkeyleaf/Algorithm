package CGTsinghua.tutorial.add;

import com.fengkeyleaf.io.ProcessingFile;
import com.fengkeyleaf.io.ReadFromStdOrFile;

import java.math.BigInteger;
import java.util.Scanner;

public class Main implements ProcessingFile {
    private BigInteger a;
    private BigInteger b;

    public void processingFile( Scanner sc ) {
        String[] content = sc.nextLine().split( ReadFromStdOrFile.PATTERN_WHITE_CHARACTER );

        a = new BigInteger( content[ 0 ] );
        b = new BigInteger( content[ 1 ] );
    }

    public static void main( String[] args ) {
//        Subsequence main = new Subsequence();
//        ReadFromStdOrFile.readFromFile( "", main );
//        System.out.println( main.a.add( main.b ) );
        System.out.println( 100000 * 100000 );
    }
}
