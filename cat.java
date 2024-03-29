
public class cat
{
  /**
   * The name of this program.  
   * This is the program name that is used 
   * when displaying error messages.
   */
  public static final String PROGRAM_NAME = "cat" ;

  /**
   * The size of the buffer to be used for reading from the 
   * file.  A buffer of this size is filled before writing
   * to the output file.
   */
  public static final int BUF_SIZE = 4096 ;

  /**
   * Reads files and writes to standard output.
   * @exception java.lang.Exception if an exception is thrown
   * by an underlying operation
   */
  public static void main( String[] argv ) throws Exception
  {
    // initialize the file system simulator kernel
    Kernel.initialize() ;

    // display a helpful message if no arguments are given
    if( argv.length == 0 )
    {
      System.err.println( PROGRAM_NAME + ": usage: java " + PROGRAM_NAME + 
        " input-file ..." ) ;
      Kernel.exit( 1 ) ;
    }

    // for each filename specified
    for( int i = 0 ; i < argv.length ; i ++ )
    {
      String name = argv[i] ;

      // open the file for reading
      int in_fd = Kernel.open( name , Kernel.O_RDONLY ) ;
      if( in_fd < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        System.err.println( PROGRAM_NAME + ": unable to open input file \"" +
          name + "\"" ) ;
        Kernel.exit( 2 ) ;
      }

      // create a buffer for reading data
      byte[] buffer = new byte[BUF_SIZE] ;

      // read data while we can
      int rd_count ;
      while( true )
      {
        // read a buffer full of data
        rd_count = Kernel.read( in_fd , buffer , BUF_SIZE ) ;

        // if we encounter an error or get to the end, quit the loop
        if( rd_count <= 0 )
          break ;

        // write whatever we read to standard output
        System.out.write( buffer , 0 , rd_count ) ;
      }

      // close the input file
      Kernel.close( in_fd ) ;

      // exit with failure if we encounter an error
      if( rd_count < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        System.err.println( PROGRAM_NAME + 
          ": error during read from input file" ) ;
        Kernel.exit( 3 ) ;
      }
    }

    // exit with success if we read all the files without error
    Kernel.exit( 0 ) ;
  }

}
