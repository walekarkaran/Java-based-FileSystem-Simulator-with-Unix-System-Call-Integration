

import java.io.RandomAccessFile ;
import java.io.IOException ;
import java.io.EOFException ;


public class Block 
{

  /**
   * The block size in bytes for this Block.
   */
  private short blockSize = 0 ;

  /**
   * The array of bytes for this block.
   */
  public byte[] bytes = null ;

  /**
   * Construct a block.
   */
  public Block()
  {
    super() ;
  }

  /**
   * Construct a block with a given block size.
   * @param blockSize the block size in bytes
   */
  public Block( short blockSize )
  {
    super() ;
    setBlockSize( blockSize ) ;
  }

  /**
   * Set the block size in bytes for this Block.
   * @param newBlockSize the new block size in bytes
   */
  public void setBlockSize( short newBlockSize )
  {
    blockSize = newBlockSize ;
    bytes = new byte[blockSize] ;
  } 

  /**
   * Get the block size in bytes for this Block.
   * @return the block size in bytes
   */
  public short getBlockSize( )
  {
    return blockSize ;
  }

  /**
   * Read a block from a file at the current position.
   * @param file the random access file from which to read
   * @exception java.io.EOFException if attempt to read past end of file
   * @exception java.io.IOException if an I/O error occurs
   */
  public void read( RandomAccessFile file ) throws IOException , EOFException
  {
    file.readFully( bytes ) ;
  }

  /**
   * Write a block to a file at the current position.
   * @param file the random access file to which to write
   * @exception java.io.IOException if an I/O error occurs
   */
  public void write( RandomAccessFile file ) throws IOException
  {
    file.write( bytes ) ;
  }

}
