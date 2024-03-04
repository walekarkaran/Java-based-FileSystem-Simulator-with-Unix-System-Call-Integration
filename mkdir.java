
public class mkdir
{

  /**
   * The name of this program.  
   * This is the program name that is used 
   * when displaying error messages.
   */
  public static final String PROGRAM_NAME = "mkdir" ;

  /**
   * Creates the directories given as command line arguments.
   * @exception java.lang.Exception if an exception is thrown
   * by an underlying operation
   */
  public static void main( String[] args ) throws Exception
  {
    // initialize the file system simulator kernel
    Kernel.initialize() ;

    // print a helpful message if no command line arguments are given
    if( args.length < 1 )
    {
      System.err.println( PROGRAM_NAME + ": too few arguments" ) ;
      Kernel.exit( 1 ) ;
    }

    // create a buffer for writing directory entries
    byte[] directoryEntryBuffer = 
      new byte[DirectoryEntry.DIRECTORY_ENTRY_SIZE] ;

    // for each argument given on the command line
    for( int i = 0 ; i < args.length ; i ++ )
    {
      // given the argument a better name
      String name = args[i] ;
      int status = 0 ;

      // call creat() to create the file
      int newDir = Kernel.creat( name , Kernel.S_IFDIR ) ;
      if( newDir < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        System.err.println( PROGRAM_NAME + ": \"" + name + "\"" ) ;
        Kernel.exit( 2 ) ;
      }

      // get file info for "."
      Stat selfStat = new Stat() ;
      status = Kernel.fstat( newDir , selfStat ) ;
      if( status < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        Kernel.exit( 3 ) ;
      }

      // add entry for "."
      DirectoryEntry self = new DirectoryEntry( 
        selfStat.getIno() , "." ) ;
      self.write( directoryEntryBuffer , 0 ) ;
      status = Kernel.write( newDir , 
        directoryEntryBuffer , directoryEntryBuffer.length ) ;
      if( status < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        Kernel.exit( 4 ) ;
      }

      // get file info for ".."
      Stat parentStat = new Stat() ;
      Kernel.stat( name + "/.." , parentStat ) ;

      // add entry for ".."
      DirectoryEntry parent = new DirectoryEntry( 
        parentStat.getIno() , ".." ) ;
      parent.write( directoryEntryBuffer , 0 ) ;
      status = Kernel.write( newDir , 
        directoryEntryBuffer , directoryEntryBuffer.length ) ;
      if( status < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        Kernel.exit( 5 ) ;
      }

      // call close() to close the file
      status = Kernel.close( newDir ) ;
      if( status < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        Kernel.exit( 6 ) ;
      }
    }

    // exit with success if we process all the arguments
    Kernel.exit( 0 ) ;
  }

}
