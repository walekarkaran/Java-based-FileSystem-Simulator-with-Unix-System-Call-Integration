

import java.io.* ;
import java.lang.Integer ;

/**
* a simple dump program
* prints the offset, hexvalue, and decimal value for each byte in a
* file, for all files mentioned on the command line.
* <p>
* Usage:
* <pre>
*   java dump <i>input-file</i>
* </pre>
* @author Ray Ontko
*/
public class dump
{

  public static void main( String[] args )
  {
    for ( int i = 0 ; i < args.length ; i ++ )
    {
      // open a file
      try
      {
        FileInputStream ifile = new FileInputStream( args[i] ) ;
        BufferedInputStream in = new BufferedInputStream( ifile ) ;
        // while we are able to read bytes from it
        int c ;
        for ( int j = 0 ; ( c = in.read() ) != -1 ; j ++ )
        {
          if( c > 0 )
          {
            System.out.print( j + " " + Integer.toHexString( c ) + " " + c ) ;
            if( c >= 32 && c < 127 )
              System.out.print( " " + (char)c ) ;
            System.out.println() ;
          }
        }
        in.close() ;
      }
      catch ( FileNotFoundException e ) 
      {
        System.out.println( "error: unable to open input file " + args[i] ) ;
      }
      catch ( IOException e )
      {
        System.out.println( "error: unable to read from file " + args[i] ) ;
      }
    }
  }

}
