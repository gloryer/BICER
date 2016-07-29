package ca.uwaterloo.ece.bicer;
import org.junit.Test;

import ca.uwaterloo.ece.bicer.utils.Labeler;

import static org.junit.Assert.*;

/*
 * This Java source file was auto generated by running 'gradle init --type java-library'
 * by 'j22nam' at '27/05/16 5:45 PM' with Gradle 2.13",
 *
 * @author j22nam, @date 27/05/16 5:45 PM
 */
public class LabelerTest {
    @Test public void testSomeLibraryMethod() {
    	
    	String[] luceneTrainingStartDates = { // Timezone: GMT
    			"2010-09-17",
    			"2010-10-17",
    			"2010-11-16",
    			"2010-12-16",
    			"2011-01-15",
    			"2011-02-14",
    			"2011-03-16",
    			"2011-04-15"};
    	
    	String[] luceneTrainingEndDates = { // Timezone: GMT
    			"2010-10-23",
    			"2010-11-22",
    			"2010-12-22",
    			"2011-01-21",
    			"2011-02-20",
    			"2011-03-22",
    			"2011-04-21",
    			"2011-05-21"
    	};
    	
    	String[] luceneTestStartDates = { // Timezone: GMT
    			"2010-11-29",
				"2010-12-29",
				"2011-01-28",
				"2011-02-27",
				"2011-03-29",
				"2011-04-28",
				"2011-05-28",
				"2011-06-27"
    	};
    		
    	String[] luceneTestEndDates = {
    			"2010-12-28",
    			"2011-01-27",
    			"2011-02-26",
    			"2011-03-28",
    			"2011-04-27",
    			"2011-05-27",
    			"2011-06-26",
    			"2011-06-29"
    	};
    	 
    	String luceneEndDateForTestLabelCollection = "2013-01-17 00:00:00 -0000";  // this is not inclusive
    	String path = System.getProperty("user.home") + "/Documents/ODP/projects/lucene/";
    	String classAttributeName = "500_Buggy?";
    	String positiveLabel = "1";
    	String pathToChangeIDSha1Pair = path + "change_id_sha1_thin_lucene.txt";
    	String pathToChangePatchSize = path + "changesWithPatchSize.txt";
    	int patchSizeCutoffForDeletedLines = 5;
    	
    	// no filter
    	/*generateArffs(path,luceneTrainingStartDates,luceneTrainingEndDates,luceneTestStartDates,luceneEndDateForTestLabelCollection,"wTestCases", "train",
    			classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERWithTestCases.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	generateArffs(path,luceneTestStartDates,luceneTestEndDates,null,luceneEndDateForTestLabelCollection,"wTestCases", "test",
    			classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERWithTestCases.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	*/
    	/*generateArffs(path,luceneTrainingStartDates,luceneTrainingEndDates,luceneTestStartDates,luceneEndDateForTestLabelCollection,"", "train",
        		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICER.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	generateArffs(path,luceneTestStartDates,luceneTestEndDates,null,luceneEndDateForTestLabelCollection,"", "test",
        		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICER.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	
    	// BICER filter
    	generateArffs(path,luceneTrainingStartDates,luceneTrainingEndDates,luceneTestStartDates,luceneEndDateForTestLabelCollection,"filtered", "train",
        		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERNoiseFiltered.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	generateArffs(path,luceneTestStartDates,luceneTestEndDates,null,luceneEndDateForTestLabelCollection,"filtered", "test",
    			classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERNoiseFiltered.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	
    	// manually filtered
    	generateArffs(path,luceneTrainingStartDates,luceneTrainingEndDates,luceneTestStartDates,luceneEndDateForTestLabelCollection,"manual", "train",
        		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERManuallyFiltered.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	generateArffs(path,luceneTestStartDates,luceneTestEndDates,null,luceneEndDateForTestLabelCollection,"manual", "test",
        		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERManuallyFiltered.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	*/
    	/*for(int i=0; i<luceneTrainingStartDates.length;i++){
    		
    		pathToArff = path + "arffsOriginalWOTestCases/" + i +"/train.arff";
    		startDate = luceneTrainingStartDates[i];
    		endDate = luceneTrainingEndDates[i] + "23:59:59";
    		lastDateForFixCollection = luceneTestStartDates[i];
    		if(manual)	
    			pathToNewArff = path + "arffsManuallyCleaned/" + i + "/train.arff";
    		else if(filtered)
    			pathToNewArff = path + "arffsNoiseFilteredWOTestCases/" + i + "/train.arff";
    		else
    			pathToNewArff = path + "arffsCleanedWOTestCases/" + i + "/train.arff";
    		
    		System.out.println("\n\nData index: " + i);
    		
    		Labeler.relabelArff(pathToArff, classAttributeName, positiveLabel, pathToChangeIDSha1Pair,
    				pathToBIChangesForLabeling, pathToNewArff, startDate, endDate, lastDateForFixCollection);
    	}*/
    	
    	
    	// Lucene test sets
    	/*lastDateForFixCollection = luceneEndDateForTestLabelCollection;
    	for(int i=0; i<luceneTestStartDates.length;i++){
		
			pathToArff = path + "arffsOriginalWOTestCases/" + i +"/test.arff";
			startDate = luceneTestStartDates[i];
			endDate = luceneTestEndDates[i] + "23:59:59";
			if(manual)	
    			pathToNewArff = path + "arffsManuallyCleaned/" + i + "/test.arff";
			else if(filtered)
    			pathToNewArff = path + "arffsNoiseFilteredWOTestCases/" + i + "/test.arff";
			else
    			pathToNewArff = path + "arffsCleanedWOTestCases/" + i + "/test.arff";
			
			System.out.println("\n\nData index: " + i);
			
			Labeler.relabelArff(pathToArff, classAttributeName, positiveLabel, pathToChangeIDSha1Pair,
					pathToBIChangesForLabeling, pathToNewArff, startDate, endDate, lastDateForFixCollection);
    	}*/
    	
    	String[] jackrabbitTrainingStartDates = {
    			"2007-09-13",
    			"2007-11-12",
    			"2008-01-11",
    			"2008-03-11",
    			"2008-05-10",
    			"2008-07-09",
    			"2008-09-07",
    			"2008-11-06",
    			"2009-01-05",
    			"2009-03-06",
    			};
    	
    	String[] jackrabbitTrainingEndDates = {
    			"2007-11-24",
    			"2008-01-23",
    			"2008-03-23",
    			"2008-05-22",
    			"2008-07-21",
    			"2008-09-19",
    			"2008-11-18",
    			"2009-01-17",
    			"2009-03-18",
    			"2009-05-17",
    	};
    	
    	String[] jackrabbitTestStartDates = {
    			"2008-02-06",
    			"2008-04-06",
    			"2008-06-05",
    			"2008-08-04",
    			"2008-10-03",
    			"2008-12-02",
    			"2009-01-31",
    			"2009-04-01",
    			"2009-05-31",
    			"2009-07-30",
    	};
    	
    	String[] jackrabbitTestEndDates = {
    			"2008-04-05",
    			"2008-06-04",
    			"2008-08-03",
    			"2008-10-02",
    			"2008-12-01",
    			"2009-01-30",
    			"2009-03-31",
    			"2009-05-30",
    			"2009-07-29",
    			"2009-09-27",
    	};
    	
    	String jackrabbitEndDateForTestLabelCollection = "2013-01-15 00:00:00 -0000"; // this is not inclusive
    	
    	path = System.getProperty("user.home") + "/Documents/ODP/projects/jackrabbit/";
    	classAttributeName = "500_Buggy?";
    	positiveLabel = "1";
    	pathToChangeIDSha1Pair = path + "change_id_sha1_thin_jackrabbit.txt";
    	pathToChangePatchSize = path + "changesWithPatchSize.txt";
    	patchSizeCutoffForDeletedLines = 5;
    	
    	// no filter
    	/*generateArffs(path,jackrabbitTrainingStartDates,jackrabbitTrainingEndDates,jackrabbitTestStartDates,jackrabbitEndDateForTestLabelCollection,"wTestCases", "train",
		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERWithTestCases.txt");
    	generateArffs(path,jackrabbitTestStartDates,jackrabbitTestEndDates,null,jackrabbitEndDateForTestLabelCollection,"wTestCases", "test",
		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERWithTestCases.txt");
    	*/
    	generateArffs(path,jackrabbitTrainingStartDates,jackrabbitTrainingEndDates,jackrabbitTestStartDates,jackrabbitEndDateForTestLabelCollection,"", "train",
        		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICER.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	generateArffs(path,jackrabbitTestStartDates,jackrabbitTestEndDates,null,jackrabbitEndDateForTestLabelCollection,"", "test",
        		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICER.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	
    	// BICER filter
    	generateArffs(path,jackrabbitTrainingStartDates,jackrabbitTrainingEndDates,jackrabbitTestStartDates,jackrabbitEndDateForTestLabelCollection,"filtered", "train",
        		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERNoiseFiltered.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	generateArffs(path,jackrabbitTestStartDates,jackrabbitTestEndDates,null,jackrabbitEndDateForTestLabelCollection,"filtered", "test",
    			classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERNoiseFiltered.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	
    	// manually filtered
    	generateArffs(path,jackrabbitTrainingStartDates,jackrabbitTrainingEndDates,jackrabbitTestStartDates,jackrabbitEndDateForTestLabelCollection,"manual", "train",
        		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERManuallyFiltered.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	generateArffs(path,jackrabbitTestStartDates,jackrabbitTestEndDates,null,jackrabbitEndDateForTestLabelCollection,"manual", "test",
        		classAttributeName, positiveLabel, pathToChangeIDSha1Pair,path + "biChangesBICERManuallyFiltered.txt",pathToChangePatchSize,patchSizeCutoffForDeletedLines);
    	
    	
    }
    
    private void generateArffs(String path,String[] startDates,String[] endDates,String[] testStartDates,String lastDateForFixCollection,String type, String dataType,
    		String classAttributeName,String positiveLabel, String pathToChangeIDSha1Pair,String pathToBIChangesForLabeling,String pathToPatchSize,
    		int patchSizeCutoffForDeletedLines){
    	
    	String pathToArff;
    	String startDate;
    	String endDate;
    	String pathToNewArff;

    	for(int i=0; i< startDates.length;i++){
    		if(type.equals("wTestCases"))
    			pathToArff = path + "arffsOriginal/" + i +"/" + dataType+ ".arff";
    		else
    			pathToArff = path + "arffsOriginalWOTestCases/" + i +"/" + dataType+ ".arff";
    		startDate = startDates[i] + " 00:00:00 -0000";
    		endDate = endDates[i] + " 23:59:59 -0000";
    		if(dataType.equals("train")){
    			lastDateForFixCollection = testStartDates[i] + " 00:00:00 -0000";
    		}
    		
    		if(type.equals("manual"))	
    			pathToNewArff = path + "arffsManuallyFilteredWOTestCases_" + patchSizeCutoffForDeletedLines + "/" + i + "/" + dataType+ ".arff";
    		else if(type.equals("filtered"))
    			pathToNewArff = path + "arffsNoiseFilteredWOTestCases_" + patchSizeCutoffForDeletedLines + "/" + i + "/" + dataType+ ".arff";
    		else if(type.equals("wTestCases"))
    			pathToNewArff = path + "arffsCleanedWTestCases_" + patchSizeCutoffForDeletedLines + "/" + i + "/" + dataType+ ".arff";
    		else
    			pathToNewArff = path + "arffsCleanedWOTestCases_" + patchSizeCutoffForDeletedLines + "/" + i + "/" + dataType+ ".arff";
    		
    		System.out.println("\n\nData index: " + i);
    		
    		System.out.println("Type: " + type);
    		System.out.println("Type: " + dataType);
    		System.out.println("Start date: " + startDate);
    		System.out.println("End date: " + endDate);
    		System.out.println("End date for labeling: " + lastDateForFixCollection);
    		System.out.println("Labeling data source: " + pathToBIChangesForLabeling);
    		
    		Labeler.relabelArff(pathToArff, classAttributeName, positiveLabel, pathToChangeIDSha1Pair,
    				pathToBIChangesForLabeling, pathToNewArff, startDate, endDate, lastDateForFixCollection,pathToPatchSize,patchSizeCutoffForDeletedLines);
    	}
    }
}
