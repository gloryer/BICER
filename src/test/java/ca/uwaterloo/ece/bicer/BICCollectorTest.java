package ca.uwaterloo.ece.bicer;
import org.junit.Test;
import static org.junit.Assert.*;

/*
 * This Java source file was auto generated by running 'gradle init --type java-library'
 * by 'j22nam' at '27/05/16 5:45 PM' with Gradle 2.13
 *
 * @author j22nam, @date 27/05/16 5:45 PM
 */
public class BICCollectorTest {
    @Test public void testSomeLibraryMethod() {
        
    	BICCollector runner = new BICCollector();
    	
    	//String [] args ={"-d","data/exampleBIChanges.txt", "-g", System.getProperty("user.home") + "/git/BICER"};
    	String [] args ={"-g",System.getProperty("user.home") + "/Documents/ODP/projects/jackrabbit/git",
    						"-s", "2007-09-12 00:00:00", // "-s", "2010-09-17 00:00:00",
    						"-e", "2009-05-29 23:59:59", // "-e", "2011-06-29 23:59:59",
    						"-l", "2013-01-14 23:59:59", // "-l", "2013-01-16 23:59:59"
    						"-b", System.getProperty("user.home") + "/Documents/ODP/projects/jackrabbit/jackrabbit_bug_reports.txt"};
    	
    	runner.run(args);
    	
    	//assertTrue("someLibraryMethod should return 'true'", classUnderTest.someLibraryMethod());
    }
}
