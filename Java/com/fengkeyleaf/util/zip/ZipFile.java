package com.fengkeyleaf.util.zip;

/*
 * ZipFile.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $0.0$
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Do self-designing zipping procedure with functionalities:
 * 1) multi-threading,
 * 2) zipping files or folders from different paths into one zip file,
 * 3) removing redundant directories.
 *
 * @author Xiaoyu Tongyang, or call me sora for short
 * @see <a href="https://fengkeyleaf.com">person website</a>
 * @since  1.0
 */

public final class ZipFile implements Runnable {
    // buffer size
    private static final int BUFFER = 1024;
    // list to store folders
    private final List<File> folderPaths = new ArrayList<>();
    // list to store files
    private final List<File> filePaths = new ArrayList<>();
    // list to store all files and folders needed to zip later on
    private final List<File> fileList = new ArrayList<>();
    // txt file containing absolute path of files or folders
    private String inputFileName;
    // file name after zipping
    private String zipFileName;
    // path in which a zip is
    private String srcPath;
    // path needed to remove
    private String removedPath;

    /**
     * constructor to create an instance of ZipFile to do zipping
     */

    public ZipFile( String inputFileName, String zipFileName, String srcPath, String removedPath ) {
        this.inputFileName = inputFileName;
        this.srcPath = srcPath;
        this.zipFileName = zipFileName;
        this.removedPath = removedPath;
    }
	
	/**
     * constructor to create an instance of ZipFile to do zipping with command line arguments support
     */

    public ZipFile( String[] args ) {
        paraphraseArgs( args );
    }

    /**
     * compression
     * The original version of this method is from https://blog.csdn.net/qq_41885819/article/details/98186699,
     * and I modified it so as to fit my own purpose or kept is as it is
     * @return            how many files will have been zipped in the end
     */

    private int doZip() {
        // buffer
        byte[] buffer = new byte[BUFFER];
        // entry of a zip file
        ZipEntry zipEntry = null;
        // length to read
        int readLength = 0;
        // file name after zipping
        String newZipFileName;
        // how many files will have been zipped in the end
        int count = 0;

        if( zipFileName == null || zipFileName.length() == 0 ) {
            newZipFileName= srcPath + "newZip.zip";
        }else {
            newZipFileName = srcPath + "\\" + zipFileName;
        }

        try ( ZipOutputStream zipOutputStream = new ZipOutputStream( new FileOutputStream( newZipFileName ) ) ) {
            for ( File file : fileList ) {
                // if a file, zip it
                if ( file.isFile() ) {
                    count++;
                    zipEntry = new ZipEntry( getRelativePath( srcPath, file ) );
                    zipEntry.setSize( file.length() );
                    zipEntry.setTime( file.lastModified() );
                    zipOutputStream.putNextEntry( zipEntry );
                    InputStream inputStream = new BufferedInputStream( new FileInputStream( file ) );
                    while ( ( readLength = inputStream.read( buffer, 0, BUFFER ) ) != -1 ) {
                        zipOutputStream.write( buffer, 0, readLength );
                    }
                    inputStream.close();
                } else {
                    zipEntry = new ZipEntry(getRelativePath( srcPath, file ) + "/" );
                    zipOutputStream.putNextEntry( zipEntry );
                }
            }
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
            return -1;
        } catch ( IOException e ) {
            e.printStackTrace();
            return -2;
        }

        return count;
    }

    /**
     * get file list from srcFile
     * The original version of this method is from https://blog.csdn.net/qq_41885819/article/details/98186699,
     * and I modified it so as to fit my own purpose or kept is as it is
     * @param srcFile  file path
     */

    private void getAllFiles( File srcFile ) {
        File[] tmp = srcFile.listFiles();
        assert tmp != null;
        for ( File file : tmp ) {
            if ( file.isFile() ) {
                fileList.add(file);
            } else if ( file.isDirectory() ) {
                // if the directory is no empty, and recursively find its child files and directories in it
                if ( Objects.requireNonNull( file.listFiles() ) .length != 0 ) {
                    getAllFiles(file);
                }
                // if the directory is empty, then add is into fileList
                else {
                    fileList.add(file);
                }
            }
        }
    }

    /**
     * get Relative Path from file
     * The original version of this method is from https://blog.csdn.net/qq_41885819/article/details/98186699,
     * and I modified it so as to fit my own purpose or kept is as it is
     * @param dirPath    path in which a zip is
     * @param file       file needing to get relative path from
     */

    private  String getRelativePath( String dirPath, File file ) {
        File dir = new File( dirPath );
        String relativePath = file.getName();

        while ( true ) {
            file = file.getParentFile();
            if ( file == null ) {
                break;
            }

            if ( file.equals(dir) ) {
                break;
            } else {
                // if removedPath is null, not do the removal,
                // otherwise, remove all directories before removedPath and itself
                // i.e. directories before removedPath (including) will not be in the zip file
                if ( file.getName().equals( removedPath ) ) break;

                relativePath = file.getName() + "\\" + relativePath;
            }
        }

        return relativePath;
    }

    /**
     * read an input path from a txt file
     * */

    private void readFromFile() {
        try ( Scanner myScanner = new Scanner( new File( inputFileName ) ) ) {
            while ( myScanner.hasNext() ) {
                String path = myScanner.nextLine();
                if ( path.isEmpty() ) continue; // skip only a new line

                preprocessInputPaths( path );
            }

        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
    }

    /**
     * add all paths of files or folders together to be zipped
     * */

    private void obtainZippedAllPaths() {
        // add folders
        for ( File file : folderPaths )
            getAllFiles( file );

        // add files
        fileList.addAll( filePaths );
    }

    /**
     * separate files or folders
     * @param path          path to be zipped
     * */

    private void preprocessInputPaths( String path ) {
        File aFile = new File( path );
        if ( aFile.isFile() )
            filePaths.add( aFile );
        else if ( aFile.isDirectory() )
            folderPaths.add( aFile );
        else
            System.err.println( "\"" + path + "\" is invalid! Please check it out" );
    }

    /**
     * do the zipping procedure
     * */

    public void doTheJob() {
        readFromFile();
        System.out.println( "Starting zipping input file: "+ inputFileName + "------------->" );
        obtainZippedAllPaths();
        System.out.printf( "Done zipping %s -> (%d) files or folders have been zipped\n", inputFileName, doZip() );
    }

    /**
     * command line formats:
     * java ZipFile -inputFileName inputFileName -zipFileName zipFileName -srcPath srcPath [-removedPath removedPath]
     * Note that it is recommended that srcPath is an absolute path instead of relative one
     *
     * For example:
     * 1) keep all directories
     * java ZipFile -inputFileName input_file_1.txt -zipFileName himea_2.zip -srcPath D:\ZipFile\src
     *
     * 2) remove some directories.
     * Suppose we have the following absolute path:
     * C:\disk d\softwares\AppStore\Default
     *
     * Now we need to remove C:\disk d\softwares\ from the zip file,
     * so the argument after -removedPath should be softwares (not including \)
     * java ZipFile -inputFileName input_file_1.txt -zipFileName himea_2.zip -srcPath src -removedPath softwares
     *
     * Jump to designated path:
     * 1) in Windows 7:
     * cd /d D:\ZipFile\src
     *
     * 2) in Windows 10:
     * cd D:\ZipFile\src
     * */

    private void paraphraseArgs( String[] args ) {
        for ( int i = 0; i < args.length; i++ ) {
            switch ( args[i] ) {
                case "-inputFileName" -> inputFileName = args[++i];
                case "-zipFileName" -> zipFileName = args[++i];
                case "-srcPath" -> srcPath = args[++i];
                case "-removedPath" -> removedPath = args[++i];
                default -> {
                    System.err.println("Cannot reach here in paraphraseArgs()");
                    System.exit(1);
                }
            }
        }
    }

    /**
     * support multi-threads
     * */

    @Override
    public void run() {
        doTheJob();
    }

    public static void main( String[] args ) {
        // make sure the commend line arguments are valid to some extend
        if ( args.length < 6 || args.length > 8 ) {
            System.err.printf( "%s:\n%s", "Command line args are invalid. Please use the following formats",
                    "java ZipFile -inputFileName inputFileName -zipFileName zipFileName -srcPath srcPath " +
                            "[-removedPath removedPath]" );
            return;
        }

        // zip multiple folders and files into one zip file
        new Thread( new ZipFile( args ) ).start();

        // no multi-threads using
//        doTheJob("input_file.txt", "himea.zip", "src");
//        new Thread( new ZipFile("input_file_1.txt", "himea_2.zip", "src", "test_folder_3/", true ) ).start();
//        new Thread( new ZipFile("input_file_1.txt", "himea_1.zip", "src") ).start();
    }
}