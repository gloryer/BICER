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
    	
    	/*String project="jdt";
    	 * 
    	 */
    	//String project="jackrabbit"
        //String [] args ={"-d","data/exampleBIChanges.txt", "-g", System.getProperty("user.home") + "/git/BICER"};
    	String [] args ={"-g",System.getProperty("user.home") + "/Document/jackrabbit/",
    						"-t",
    						//"-u",
    						// jackrabbit
    						"-s", "2009-07-30 00:00:00",
    						"-e", "2009-09-27 23:59:59",
    						"-l", "2013-01-14 23:59:59",
    						//lucene
    						/*"-s", "2010-09-17 00:00:00",
    						"-e", "2011-06-29 23:59:59",
    						"-l", "2013-01-16 23:59:59",*/
    						// jdt
    						/*"-s", "2005-06-06 00:00:00",
    						"-e", "2006-05-03 23:59:59",
    						"-l", "2012-07-24 23:59:59", // TODO but the last date of the git repository is Wed Mar 8 19:44:52 2006 +0000
    						//test
			    			/*"-s", "2010-12-18 00:00:00",
							"-e", "2010-12-20 23:59:59",
							"-l", "2010-12-20 23:59:59",*/
    						//"-b", System.getProperty("user.home") + "/Documents/ODP/projects/" + project + "/" + project + "_bug_reports.txt"
    						};
    	
    	runner.run(args);
    	
    	//assertTrue("someLibraryMethod should return 'true'", classUnderTest.someLibraryMethod());
    }
}
