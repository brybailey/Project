import java.util.Map;
import java.util.TreeMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

/*
 *  A text analyzer that processes text and provides information about its word contents
 *  and its constituent character counts (extension).
 * 
 *  Possible extentions include: 
 *      n-gram analysis for word prediction
 *      Word and character set entropy analysis
 *      Semantic parsing (e.g. Chomsky formal grammar or Latent Semantic Analysis) 
 *
 *  (c) Bryan C. Bailey 2017 for Symphony
 */


class TextAnalyzer { 
    
    private TreeMap<Integer, TreeMap<String, Integer>> firstOrder; // Ordering by string length
    private TreeMap<String, Integer> secondOrder;                  // Ordering by ASCII
    private TreeMap<Character, Integer> charFreq;                  // Ordering (char) by ASCII 


    /*
     * @constructor reads words from input file
     */
    public TextAnalyzer( File file ) throws FileNotFoundException {
	Scanner in = new Scanner( file );
	firstOrder = new TreeMap<Integer, TreeMap<String, Integer>>();
	charFreq = new TreeMap<Character, Integer>();
	while( in.hasNext() ) {
	    addWord( in.next() );
	}	
    }

    /*
     * Builds out word treemaps 
     */
    private void addWord( String word ) {
	addChars( word );
	int len = word.length();
	if( firstOrder.containsKey(len) ) {
	    secondOrder = firstOrder.get( len );
	    if( secondOrder.containsKey( word ) ) {  // Already contains the word; update count
		int count = secondOrder.get( word );
		secondOrder.put( word, ++count );  
	    } else {
		secondOrder.put( word, 1 );          // New entry, count is 1
	    }
	} else {
	    secondOrder = new TreeMap<String, Integer>();
	    secondOrder.put( word, 1 );              // New string length; add new treemap
	    firstOrder.put( len, secondOrder );
	}
    }


    /*
     * Builds out character frequenct treemap
     */
    private void addChars( String word ) {
	for( char c: word.toCharArray() ) {
	    if( charFreq.containsKey( c ) ) {
		int count = charFreq.get(c);  // Already contains the character; update count
		charFreq.put( c, ++count );
	    } else {
		charFreq.put( c, 1 );         // New character, count is 1
	    }
	}
    }
    
    /*
     * Creates an output file report from the constructed treemaps
     */
    private void createReport() {
	try{ 
	    File file = new File( "analyzed.txt" );
	    FileWriter fileWriter = new FileWriter(file);
	    fileWriter.write( "Word Analysis:\n" );         	    // Word Analysis
	    for( Integer lenOrder: firstOrder.keySet() ) {
		TreeMap<String, Integer> out = firstOrder.get(lenOrder);
		for( String alphOrder: out.keySet() ) {
		    fileWriter.write( out.get(alphOrder) + " " + alphOrder + "\n" );
		}
	    }
	    fileWriter.write( "\nCharacter Analysis:\n" );         // Character Analysis
	    for( char c: charFreq.keySet() ) {
		fileWriter.write( charFreq.get( c ) + " " + c + "\n" );
	    }
	    fileWriter.close();                                    // File written
	    
	} catch(IOException e) {
	    e.printStackTrace();
	}
    }

    /*
     * Prints a report of the word and character frequencies
     */
    private void printReport() {
	System.out.println( "Word Analysis:\n" );         	    // Word Analysis
	for( Integer lenOrder: firstOrder.keySet() ) {
	    TreeMap<String, Integer> out = firstOrder.get(lenOrder);
	    for( String alphOrder: out.keySet() ) {
		System.out.println( out.get(alphOrder) + " " + alphOrder );
	    }
	}    
	System.out.println( "\nCharacter Analysis\n" );
	for( char c: charFreq.keySet() ) {
	    System.out.println( charFreq.get(c) + " " + c );
	}
    }

    /*
     * Decide what the user's goal is
     */
    private void decodeArgs( String args[] ) {
	int i = 0;
	while( i < args.length ) {
	    if( "-f".equals( args[i].toLowerCase() ) ) {
		createReport();
	    } else if( "-p".equals( args[i].toLowerCase() ) ) {
		printReport();
	    } else if ( !args[i].contains( ".txt" ) ) {
		System.err.println( "Unknown arg: " + args[i] );
		break;
	    }
	    i++;
	}
    }
    
    // For lack of a client framework
    public static void main( String args[] ) throws FileNotFoundException{
	File file;
	if( args.length !=2 && args.length != 3 ) { // Make sure user input the right arguments
	    System.err.println( "Usage: -f -p filename.txt\n      <-f> creates a report in a output file 'analyzed.txt'\n      <-p> prints the report" );
	    return;
	} else {
	    boolean fileFound = false;
	    for( String s: args ) {         	    // Make sure the user put in a file to analyze
		if( s.contains(".txt" ) ) {
		    file = new File( s );
		    fileFound = true;
		    TextAnalyzer textAnalyzer = new TextAnalyzer( file );
		    textAnalyzer.decodeArgs( args );
		    break;
		}
	    }
	    if( !fileFound ) {
		System.err.println( "Please input a .txt file" );
		return;
	    }
	}
	
    }
}