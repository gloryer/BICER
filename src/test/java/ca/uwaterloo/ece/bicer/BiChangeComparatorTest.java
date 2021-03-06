package ca.uwaterloo.ece.bicer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import ca.uwaterloo.ece.bicer.data.BIChange;
import ca.uwaterloo.ece.bicer.utils.Utils;

/*
 * Compare biChanged generated by BICER and sanitized one. Since the sanitized one has bugs in BISha1 as there is a bug (blamed in a wrong line num) in previous tool.
 * By the comparison, we can identify lines that require manual analysis whether a line is actually BI line.
 * by 'j22nam' at '27/05/16 5:45 PM' with Gradle 2.13
 *
 * @author j22nam, @date 27/05/16 5:45 PM
 */
public class BiChangeComparatorTest {
    @Test public void testSomeLibraryMethod() {
    	
    	String project ="jackrabbit";
    	String dir = System.getProperty("user.home") + "/Documents/ODP/projects/" + project +"/";
        
    	String pathForBIChangesBICER = dir + "biChangesBICER.txt";
    	String pathForBIChangesSanitized = dir + "biChangesBICER_partial_manual.txt"; // for reuse labels from oldtool "biChangesOldToolAllOldPathCorrectedSanitized.txt";
    	String pathForBIChangesManualSanitized = dir + "biChangesBICER_partial_manual_no_noise.txt"; // for labeling: real bi lines from biChangesOldToolAllSanitized.txt
    	String pathForBIChangesActualNoisesToolFound = dir + "biChangesBICERActualNoisesToolFound.txt"; // Actual Noises tool found

    	ArrayList<BIChange> biChangesBICER = loadBIChanges(pathForBIChangesBICER, false);
    	ArrayList<BIChange> biChangesSanitized = loadBIChanges(pathForBIChangesSanitized, false);
    	ArrayList<BIChange> biChangesManualSanitized = loadBIChanges(pathForBIChangesManualSanitized, false);
    	ArrayList<BIChange> biChangesActualNoisesToolFound = loadBIChanges(pathForBIChangesActualNoisesToolFound, false);
    	
    	ArrayList<BIChange> biChangesBICERNotExistInSenitized = new ArrayList<BIChange>();
    	ArrayList<BIChange> biChangesCoEixst = new ArrayList<BIChange>();
    	ArrayList<BIChange> biChangesRedundantInSanitized = new ArrayList<BIChange>();
    	
    	HashMap<String,ArrayList<BIChange>> mapBIChangesSanitized = new HashMap<String,ArrayList<BIChange>>(); // key fixSha1 + path + lineNum + line
    	    	
    	for(BIChange biChange:biChangesSanitized){
    		String key = biChange.getFixSha1() + biChange.getPath()+biChange.getLineNum() + ":" + biChange.getLineNumInPrevFixRev() + biChange.getIsAddedLine();// + biChange.getLine().trim();
    		if(!mapBIChangesSanitized.containsKey(key)){
    			ArrayList<BIChange> arrBIChange = new ArrayList<BIChange>();
    			arrBIChange.add(biChange);
    			mapBIChangesSanitized.put(key, arrBIChange);
    		} else{
    			mapBIChangesSanitized.get(key).add(biChange);
    		}
    	}
    	
    	HashMap<String,ArrayList<BIChange>> mapBIChangesManualSanitized = new HashMap<String,ArrayList<BIChange>>(); // key fixSha1 + path + lineNum + line
    	
    	for(BIChange biChange:biChangesManualSanitized){
    		String key = biChange.getFixSha1() + biChange.getPath()+biChange.getLineNum() + ":" + biChange.getLineNumInPrevFixRev() + biChange.getIsAddedLine();// + biChange.getLine().trim();
    		if(!mapBIChangesManualSanitized.containsKey(key)){
    			ArrayList<BIChange> arrBIChange = new ArrayList<BIChange>();
    			arrBIChange.add(biChange);
    			mapBIChangesManualSanitized.put(key, arrBIChange);
    		} else{
    			mapBIChangesManualSanitized.get(key).add(biChange);
    		}
    	}
    	
    	HashMap<String,ArrayList<BIChange>> mapBIChangesActualNoisesToolFound= new HashMap<String,ArrayList<BIChange>>(); // key fixSha1 + path + lineNum + line
    	
    	for(BIChange biChange:biChangesActualNoisesToolFound){
    		String key = biChange.getFixSha1() + biChange.getPath()+biChange.getLineNum() + ":" + biChange.getLineNumInPrevFixRev() + biChange.getIsAddedLine();// + biChange.getLine().trim();
    		if(!mapBIChangesActualNoisesToolFound.containsKey(key)){
    			ArrayList<BIChange> arrBIChange = new ArrayList<BIChange>();
    			arrBIChange.add(biChange);
    			mapBIChangesActualNoisesToolFound.put(key, arrBIChange);
    		} else{
    			mapBIChangesActualNoisesToolFound.get(key).add(biChange);
    		}
    	}
    	
    	// labeling
    	for(BIChange biChange:biChangesBICER){
    		
    		String key = biChange.getFixSha1() + biChange.getPath() +biChange.getLineNum() + ":" + biChange.getLineNumInPrevFixRev() + biChange.getIsAddedLine();// +  biChange.getLine().trim();
    		
    		if(mapBIChangesSanitized.containsKey(key)){
    			ArrayList<BIChange> arrBIChange = mapBIChangesSanitized.get(key);
    			
    			if(mapBIChangesManualSanitized.containsKey(key) &&!mapBIChangesActualNoisesToolFound.containsKey(key))
    				biChange.setIsNoise(false);
    			else
    				biChange.setIsNoise(true);
    			
    			
    			
    			biChangesCoEixst.add(biChange);
    			if(arrBIChange.size()>1)
    				biChangesRedundantInSanitized.add(arrBIChange.get(0));
    		}
    		else
    			biChangesBICERNotExistInSenitized.add(biChange);
    	}
    	
    	System.out.println("Num of co-existing BIChanges: " + biChangesCoEixst.size());
    	System.out.println("Num of not-existing BIChanges: " + biChangesBICERNotExistInSenitized.size());
    	System.out.println("Redundant BIChanges in Sanitized: " + biChangesRedundantInSanitized.size());
    	
    	
    	System.out.println("Num of co-existing BIChanges: " + biChangesCoEixst.size());
    	for(BIChange biChange:biChangesCoEixst){
    		System.out.println(biChange.getRecord() + "\t" + biChange.isNoise());
    	}
    	
    	System.out.println("Num of not-existing BIChanges: " + biChangesBICERNotExistInSenitized.size());
    	for(BIChange biChange:biChangesBICERNotExistInSenitized){
    		if(biChange.getLine().trim().equals(""))
    			continue;
    		if(biChange.getLine().trim().equals("{") || biChange.getLine().trim().equals("}"))
    			System.out.println(biChange.getRecord() + "\ttrue");
    		else
    			System.out.println(biChange.getRecord());
    	}
    	
    	System.out.println("Redundant BIChanges in Sanitized: " + biChangesRedundantInSanitized.size());
    	for(BIChange biChange:biChangesRedundantInSanitized){
    		System.out.println(biChange.getRecord());
    	}
	}
    
    private ArrayList<BIChange> loadBIChanges(String path,boolean forSanitizer) {
		ArrayList<String> BIChangeInfo = Utils.getLines(path, true); // Sanitizer file does not have header line, so remove it.
		ArrayList<BIChange> biChanges = new ArrayList<BIChange>();
		for(String info: BIChangeInfo){
			biChanges.add(new BIChange(info,forSanitizer));
		}
		
		return biChanges;
	}
}
