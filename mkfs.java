

import java.io.* ;


public class mkfs
{


  /**
   * Creates a "file system" in the named file with the specified 
   * blocksize and number of blocks.
   * @exception java.lang.Exception if any exception occurs
   */
  public static void main( String[] argv ) throws Exception
  {
    if( argv.length != 3 )
    {
      System.err.println( 
        "mkfs: usage: java mkfs <filename> <block-size> <blocks>" ) ;
      System.exit( 1 ) ;
    }

    String filename = argv[0] ;
    short block_size = Short.parseShort( argv[1] ) ;
    int blocks = Integer.parseInt( argv[2] ) ;
    int block_total = 0 ;

    /*
    blocks = 
      super_blocks + 
      free_list_blocks + 
      inode_blocks + 
      data_blocks
    
    We need one block for the superblock.
    super_blocks = 1
    
    We need one bit in the free list map for each data block.
    free_list_blocks = 
      ( data_blocks + block_size * 8 - 1 ) / 
        ( block_size * 8 )
    
    ??? Is this the correct number of inodes?

    At worse, there will be only directory entries and empty files.
    In other words, we might need as many inodes as we have blocks.
    inode_blocks = 
      ( data_blocks + block_size / inode_size - 1 ) / 
        ( block_size / inode_size )
    
    Then:
    
    blocks = 
      super_blocks +
      ( data_blocks + block_size * 8 - 1 ) / 
        ( block_size * 8 ) +
      ( data_blocks + block_size / inode_size - 1 ) / 
        ( block_size / inode_size ) +
      data_blocks

    We then seek the maximum number of data blocks where the total number
    of blocks is less than or equal to the number of blocks available.
    We use a binary searching technique in the following algorithm.
    */
    int inode_size = IndexNode.INDEX_NODE_SIZE ;
    int super_blocks = 1 ;
    int free_list_blocks = 0 ;
    int inode_blocks = 0 ;
    int data_blocks = 0 ;
    int lo = 0 ;
    int hi = blocks ;
    while( lo <= hi )
    {
      data_blocks = ( lo + hi + 1 ) / 2 ;
      free_list_blocks =
        ( data_blocks + block_size * 8 - 1 ) / 
          ( block_size * 8 ) ;
      inode_blocks =
        ( data_blocks + block_size / inode_size - 1 ) / 
          ( block_size / inode_size ) ;
      block_total = super_blocks + free_list_blocks + 
        inode_blocks + data_blocks ;

      /*
      Just in case you want to see it converge...

      System.out.println( "lo: " + lo + " hi: " + hi ) ;
      System.out.println( "block_size: " + block_size ) ;
      System.out.println( "blocks: " + blocks ) ;
      System.out.println( "free_list_blocks: " + free_list_blocks ) ;
      System.out.println( "inode_blocks: " + inode_blocks ) ;
      System.out.println( "data_blocks: " + data_blocks ) ;
      System.out.println( "block_total: " + block_total ) ;
      System.out.println() ;
      */

      if ( block_total < blocks )
        lo = data_blocks + 1 ;
      else if ( block_total > blocks )
        hi = data_blocks - 1 ;
      else
        break ;
    }

    // if the last block causes free_list_blocks or inode_blocks to
    // cross a block boundary, we "give" the extra space to the free
    // list and/or inodes and use whatever remains for the data blocks
    if( block_total > blocks )
    {
      // System.out.println( "adjusting data blocks..." ) ;
      // System.out.println() ;
      data_blocks -- ;
    }

    // calculate inode and free list blocks based on the final 
    // count of data blocks
    free_list_blocks =
      ( data_blocks + block_size * 8 - 1 ) / 
        ( block_size * 8 ) ;
    inode_blocks = blocks - super_blocks - free_list_blocks - data_blocks ;
    block_total = super_blocks + free_list_blocks + 
      inode_blocks + data_blocks ;

    if ( data_blocks <= 0 )
    {
      System.err.println( 
        "mkfs: parameters resulted in data block count less than one" ) ;
      System.exit( 2 ) ;
    }

    System.out.println( "block_size: " + block_size ) ;
    System.out.println( "blocks: " + blocks ) ;
    System.out.println( "super_blocks: " + super_blocks ) ;
    System.out.println( "free_list_blocks: " + free_list_blocks ) ;
    System.out.println( "inode_blocks: " + inode_blocks ) ;
    System.out.println( "data_blocks: " + data_blocks ) ;
    System.out.println( "block_total: " + block_total ) ;

    // If the file already exists, we delete it.
    // Under jdk 1.2 we can use setLength() to truncate, but
    // for now we must delete and re-create.
    File deleteFile = new File( filename ) ;
    deleteFile.delete() ;

    RandomAccessFile file = new RandomAccessFile( filename , "rw" ) ;

    int superBlockOffset = 0 ;
    int freeListBlockOffset = superBlockOffset + 1 ;
    int inodeBlockOffset = freeListBlockOffset + free_list_blocks ;
    int dataBlockOffset = inodeBlockOffset + inode_blocks ;

    /*
    System.out.println( "free list block offset: " + freeListBlockOffset ) ;
    System.out.println( "inode block offset: " + inodeBlockOffset ) ;
    System.out.println( "data block offset: " + dataBlockOffset ) ;
    */

    // create the superblock
    SuperBlock superBlock = new SuperBlock() ;
    superBlock.setBlockSize( block_size ) ;
    superBlock.setBlocks( blocks ) ;
    superBlock.setFreeListBlockOffset( freeListBlockOffset ) ;
    superBlock.setInodeBlockOffset( inodeBlockOffset ) ;
    superBlock.setDataBlockOffset( dataBlockOffset ) ;

    // write the superblock
    file.seek( superBlockOffset * block_size ) ;
    superBlock.write( file ) ;

    // create the free list bitmap block
    BitBlock freeListBlock = new BitBlock( block_size ) ;

    // all blocks are free except the first block, which contains
    // the directory block for the root directory.
    freeListBlock.setBit( 0 ) ;

    // write the free list bitmap blocks
    file.seek( freeListBlockOffset * block_size ) ;
    freeListBlock.write( file ) ;

    // write the rest of the free list blocks which should be empty
    BitBlock emptyFreeListBlock = new BitBlock( block_size ) ;
    for( int i = freeListBlockOffset + 1 ; i < inodeBlockOffset ; i ++ )
    {
      file.seek( i * block_size ) ;
      emptyFreeListBlock.write( file ) ; 
    }

    // create the root inode block
    Block rootInodeBlock = new Block( block_size ) ;

    // create the inode for the root directory
    IndexNode rootIndexNode = new IndexNode() ;

    // set the first block address to the the 
    // address of the first available data block.
    rootIndexNode.setBlockAddress( 0 , 0 ) ;

    // the root inode is a directory inode
    rootIndexNode.setMode( Kernel.S_IFDIR ) ;

    // there are two directory entries in the root file system,
    // so we set the file size accordingly.
    rootIndexNode.setSize( DirectoryEntry.DIRECTORY_ENTRY_SIZE * 2 ) ;

    // set the link count: itself, dot, dot-dot
    rootIndexNode.setNlink( (short)3 ) ;
    // write the rootIndexNode to the rootInodeBlock
    rootIndexNode.write( rootInodeBlock.bytes , 
      ( FileSystem.ROOT_INDEX_NODE_NUMBER * IndexNode.INDEX_NODE_SIZE ) % block_size ) ;

    // ??? write the rest of the inodes in the first block

    // write the first inode block
    file.seek( inodeBlockOffset * block_size + 
      FileSystem.ROOT_INDEX_NODE_NUMBER * IndexNode.INDEX_NODE_SIZE ) ;
    rootInodeBlock.write( file ) ;

    // ??? write the rest of the inode blocks

    // create the root directory block
    Block rootDirectoryBlock = new Block( block_size ) ;

    // the root directory block contains two directory entries:
    // one for itself ("."), and one for its parent ("..").
    // Both of these reference the root inode.
    DirectoryEntry itself = 
      new DirectoryEntry( FileSystem.ROOT_INDEX_NODE_NUMBER , "." ) ;
    DirectoryEntry parent = 
      new DirectoryEntry( FileSystem.ROOT_INDEX_NODE_NUMBER , ".." ) ;

    // write the root directory entries to the root directory block
    itself.write( rootDirectoryBlock.bytes , 0 ) ;
    parent.write( rootDirectoryBlock.bytes , DirectoryEntry.DIRECTORY_ENTRY_SIZE ) ;

    // write the root directory block to the file
    file.seek( dataBlockOffset * block_size ) ;
    rootDirectoryBlock.write( file ) ;

    // write a zero byte to the last byte of the file system file
    file.seek( blocks * block_size - 1 ) ;
    file.write( 0 ) ;
    file.close() ;
  }

}
