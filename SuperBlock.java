

import java.io.RandomAccessFile ;
import java.io.IOException ;
import java.util.*;

public class SuperBlock 
{

  /**
   * Size of each block in the file system.
   */
  private short blockSize ;

  /**
   * Total number of blocks in the file system.
   */
  private int blocks ;

  /**
   * Offset in blocks of the free list block region from the beginning 
   * of the file system.
   */
  private int freeListBlockOffset ;

  /**
   * Offset in blocks of the inode block region from the beginning 
   * of the file system.
   */
  private int inodeBlockOffset ;

  /**
   * Offset in blocks of the data block region from the beginning 
   * of the file system.
   */
  private int dataBlockOffset ;

  /**
   * Construct a SuperBlock.
   */
  public SuperBlock()
  {
    super();
  }

  public void setBlockSize( short newBlockSize )
  {
    blockSize = newBlockSize ;
  }

  public short getBlockSize()
  {
    return blockSize ;
  }

  public void setBlocks( int newBlocks )
  {
    blocks = newBlocks ;
  }

  public int getBlocks()
  {
    return blocks ;
  }

  /**
   * Set the freeListBlockOffset (in blocks)
   * @param newFreeListBlockOffset the new offset in blocks
   */
  public void setFreeListBlockOffset( int newFreeListBlockOffset )
  {
    freeListBlockOffset = newFreeListBlockOffset ;
  }

  /**
   * Get the free list block offset
   * @return the free list block offset
   */
  public int getFreeListBlockOffset()
  {
    return freeListBlockOffset ;
  }

  /**
   * Set the inodeBlockOffset (in blocks)
   * @param newInodeBlockOffset the new offset in blocks
   */
  public void setInodeBlockOffset( int newInodeBlockOffset )
  {
    inodeBlockOffset = newInodeBlockOffset ;
  }

  /**
   * Get the inode block offset (in blocks)
   * @return inode block offset in blocks
   */
  public int getInodeBlockOffset()
  {
    return inodeBlockOffset ;
  }

  /**
   * Set the dataBlockOffset (in blocks)
   * @param newDataBlockOffset the new offset in blocks
   */
  public void setDataBlockOffset( int newDataBlockOffset )
  {
    dataBlockOffset = newDataBlockOffset ;
  }

  /**
   * Get the dataBlockOffset (in blocks)
   * @return the offset in blocks to the data block region
   */
  public int getDataBlockOffset()
  {
    return dataBlockOffset ;
  }

  /**
   * writes this SuperBlock at the current position of the specified file.
   */
  public void write( RandomAccessFile file ) throws IOException
  {
    file.writeShort( blockSize ) ;
    file.writeInt( blocks ) ;
    file.writeInt( freeListBlockOffset) ;
    file.writeInt( inodeBlockOffset ) ;
    file.writeInt( dataBlockOffset ) ;
    for( int i = 0 ; i < blockSize - 18 ; i ++ )
      file.write( (byte) 0 ) ;
  }

  /**
   * reads this SuperBlock at the current position of the specified file.
   */
  public void read( RandomAccessFile file ) throws IOException
  {
    blockSize = file.readShort() ;
    blocks = file.readInt() ;
    freeListBlockOffset = file.readInt() ;
    inodeBlockOffset = file.readInt() ;
    dataBlockOffset = file.readInt() ;
    file.skipBytes( blockSize - 18 ) ;
  }

}
