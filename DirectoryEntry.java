
public class DirectoryEntry 
{

  /**
   * Maximum length of a file name.
   */
  public static final int MAX_FILENAME_LENGTH = 14 ;

  /**
   * Size of a directory entry (on disk) in bytes.
   */
  public static final int DIRECTORY_ENTRY_SIZE = MAX_FILENAME_LENGTH + 2 ;

  /**
   * i-node number for this DirectoryEntry
   */
  public short d_ino = 0 ;

  /**
   * file name for this DirectoryEntry
   */
  public byte[] d_name = new byte[ MAX_FILENAME_LENGTH ] ;

  /**
   * Constructs an empty DirectoryEntry.
   */
  public DirectoryEntry()
  {
    super() ;
  }

  /**
   * Constructs a DirectoryEntry for the given inode and name.
   * Note that the name is stored internally as a byte[],
   * not as a string.
   * @param ino the inode number for this DirectoryEntry
   * @param name the file name for this DirectoryEntry
   */
  public DirectoryEntry(short ino, String name) 
  {
    super() ;
    setIno( ino );
    setName( name );
  }

  /**
   * Sets the inode number for this DirectoryEntry
   * @param newIno the new inode number
   */
  public void setIno( short newIno )
  {
    d_ino = newIno ;
  }

  /**
   * Gets the inode number for this DirectoryEntry
   * @return the inode number
   */
  public short getIno()
  {
    return d_ino ;
  }

  /**
   * Sets the name for this DirectoryEntry
   * @param newName the new name
   */
  public void setName( String newName )
  {
    for( int i = 0 ; i < MAX_FILENAME_LENGTH && i < newName.length() ; i ++ )
      if( i < newName.length() )
        d_name[i] = (byte)newName.charAt(i) ;
      else
        d_name[i] = (byte)0 ;
  }

  /**
   * Gets the name for this DirectoryEntry
   * @return the name
   */
  public String getName()
  {
    StringBuffer s = new StringBuffer( MAX_FILENAME_LENGTH ) ;
    for( int i = 0 ; i < MAX_FILENAME_LENGTH ; i ++ )
    {
      if ( d_name[i] == (byte)0 )
        break ;
      s.append( (char)d_name[i] ) ;
    }
    return s.toString() ;
  }

  /**
   * Writes a DirectoryEntry to the specified byte array at the specified
   * offset.
   * @param buffer the byte array to which the directory entry should be written
   * @param offset the offset from the beginning of the buffer to which the 
   * directory entry should be written
   */
  public void write( byte[] buffer , int offset )
  {
    buffer[offset] = (byte)( d_ino >>> 8 );
    buffer[offset+1] = (byte) d_ino ;
    for( int i = 0 ; i < d_name.length ; i ++ )
      buffer[offset+2+i] = d_name[i] ;
  }

  /**
   * Reads a DirectoryEntry from the spcified byte array at the specified 
   * offset.
   * @param buffer the byte array from which the directory entry should be read
   * @param offset the offset from the beginning of the buffer from which the 
   * directory entry should be read
   */
  public void read( byte[] buffer , int offset )
  {
    int hi = buffer[offset] & 0xff ;
    int lo = buffer[offset+1] & 0xff ;
    d_ino = (short)( hi << 8 | lo ) ;
    for( int i = 0 ; i < d_name.length ; i ++ )
       d_name[i] = buffer[offset+2+i] ;
  }

  /**
   * Converts a DirectoryEntry to a printable string.
   * @return the printable string
   */
  public String toString()
  {
    StringBuffer s = new StringBuffer( "DirectoryEntry[" ) ;
    s.append( getIno() ) ;
    s.append(',') ;
    s.append( getName() ) ;
    s.append(']') ;
    return s.toString() ;
  }

  /**
   * A test driver for this class.
   * @exception java.lang.Exception any exception which may occur.
   */
  public static void main( String[] args ) throws Exception
  {
    DirectoryEntry root = new DirectoryEntry( (short)1 , "/" ) ;
    System.out.println( root.toString() ) ;
  }

}
