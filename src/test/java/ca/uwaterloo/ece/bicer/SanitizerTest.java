package ca.uwaterloo.ece.bicer;
import org.junit.Test;

import ca.uwaterloo.ece.bicer.utils.Sanitizer;

import static org.junit.Assert.*;

/*
 * This Java source file was auto generated by running 'gradle init --type java-library'
 * by 'j22nam' at '27/05/16 5:45 PM' with Gradle 2.13
 *
 * @author j22nam, @date 27/05/16 5:45 PM
 */
public class SanitizerTest {
    @Test public void testSomeLibraryMethod() {
        
    	NoiseFilterRunner runner = new NoiseFilterRunner();
    	
    	String project = "jackrabbit";
    	String [] args ={"-s", "-d",System.getProperty("user.home") + "/Documents/ODP/projects/" + project + "/biChangesOldToolAllOldPathCorrected.txt",
    			"-g", System.getProperty("user.home") + "/Documents/ODP/projects/" + project + "/git"};
    	
    	runner.run(args);
    	
    	//assertTrue("someLibraryMethod should return 'true'", classUnderTest.someLibraryMethod());
    }
}
