package ca.uwaterloo.ece.bicer.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import ca.uwaterloo.ece.bicer.data.BIChange;
import ca.uwaterloo.ece.bicer.data.Change;
import weka.core.Instance;
import weka.core.Instances;

public class Labeler {
	
	static public void relabelArff(String pathToArff,String classAttributeName,String positiveLabel,
									String pathToChangeIDSha1Pair,
									String pathToBIChangesforLabeling,String pathToNewArff,
									String startDate,String endDate,String lastDateForFixCollection, String pathToPatchSize, int patchSizeCutoffForDeletedLines){
		
		// load arff
		Instances instances = Utils.loadArff(pathToArff, classAttributeName);
		
		// load changd_id and sha1 pair
		HashMap<String,String> sha1sbyChangeIDs = getSha1sByChangeIDs(pathToChangeIDSha1Pair);
		
		// load sha1 and path pair with patch size
		HashMap<String,Change> changesWithPatchSize = getChangesWithPatchSize(pathToPatchSize);
		
		// load BIChanges for labeling
		ArrayList<BIChange> biChangesForLabeling = Utils.loadBIChanges(pathToBIChangesforLabeling,true);
		// load all BIChanges to get all info
		HashMap<String,ArrayList<BIChange>> biChangesByKey = getHashMapForBIChangesByKey(biChangesForLabeling); // key: biSha1+biPath
		
		// relabel
		int count =0;
		String instancesToBeDeleted = "";
		ArrayList<String> changesLabeledAsBuggy = new ArrayList<String>();
		for(int i=0; i<instances.numInstances();i++){
			Instance instance = instances.get(i);
			String changeID = (int)instance.value(instances.attribute("change_id")) + "";
			String biPath = instance.stringValue(instances.attribute("412_full_path"));
			String biSha1 = sha1sbyChangeIDs.get(changeID);
			
			String key = biSha1 + biPath;
			
			String newLabel = getNewLabel(key,startDate,endDate,lastDateForFixCollection,biChangesByKey,changesWithPatchSize,patchSizeCutoffForDeletedLines);
			
			if(newLabel.equals("1")){
				System.out.println("Labeled as buggy:" + key);
				changesLabeledAsBuggy.add(key);
				count++;
			}
			
			instance.setValue(instances.classAttribute(), newLabel);
			
			// ignore bigger patches based on patchSizeCutoffForDeletedLines
			if(changesWithPatchSize.get(key)!= null && !(changesWithPatchSize.get(key).getNumDeletedLines()<=patchSizeCutoffForDeletedLines))
				instancesToBeDeleted += (i+1) + ",";
		}

		ArrayList<String> validBIChanges = getValidBIChanges(startDate,endDate,lastDateForFixCollection,biChangesByKey);
		
		System.out.println("# of valid BI changes for the given period (A): " + validBIChanges.size());
		System.out.println("# of buggy instances actually labeled (B): " + count);
		System.out.println("If the numbers are different (B<A), there is an missing change in the original data : " + count);
		
		if(validBIChanges.size()!=count){
			for(String change:validBIChanges)
				if(!changesLabeledAsBuggy.contains(change))
					System.out.println(change);
			
			//System.exit(0);
		}
		
		instances = Utils.getInstancesByRemovingSpecificInstances(instances, instancesToBeDeleted, false);
		
		ArrayList<String> lines = new ArrayList<String>();
		
		lines.add(Instances.ARFF_RELATION + " " + instances.relationName());
		
		for (int i = 0; i < instances.numAttributes(); i++) {
			lines.add(instances.attribute(i).toString());
		}
		
		lines.add("\n" + Instances.ARFF_DATA);
		
		for(int i=0; i < instances.numInstances();i++)
			lines.add(instances.get(i).toString());
		
		Utils.writeAFile(lines, pathToNewArff);
	}

	private static HashMap<String, Change> getChangesWithPatchSize(String pathToPatchSize) {
		
		HashMap<String, Change> changes = new HashMap<String, Change>();
		
		for(String line:Utils.getLines(pathToPatchSize, true)){
			String[] splitLine = line.split("\t");
			String path = splitLine[1].toLowerCase();
			changes.put(splitLine[0] + path ,new Change(splitLine[0],path,Integer.parseInt(splitLine[2]),Integer.parseInt(splitLine[3])));
		}
		
		return changes;
	}

	private static String getNewLabel(String key, String startDate, String endDate, String lastDateForFixCollection,
			HashMap<String, ArrayList<BIChange>> biChangesByKey, HashMap<String, Change> changesWithPatchSize, int patchSizeCutoffForDeletedLines) {
		
		String newLabel = "0"; // 0: clean 1: buggy
		try {
			Date sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(startDate);  // GMT
			Date eDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(endDate); // GMT
			Date lDateForFixCollection = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(lastDateForFixCollection); // GMT
			
			if(biChangesByKey.get(key)==null){
				return newLabel;
			}
			
			for(BIChange biChange:biChangesByKey.get(key)){
					Date biDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(biChange.getBIDate()); // load Date in local timezone
					Date fixDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(biChange.getFixDate()); // load Date in local timezone
				
					String fixSha1 = biChange.getFixSha1();
					String path = biChange.getPath().toLowerCase();
					
					// if the number of deleted lines in a fix commit is > patchSizeCutoffForDeletedLines, ignore
					if(!(changesWithPatchSize.get(fixSha1+path).getNumDeletedLines()<=patchSizeCutoffForDeletedLines)) continue;
					
					if(fixDate.compareTo(lDateForFixCollection)>=0) // if fixDate >= lastDateForFixCollection (no inclusive for labeling), continue
						continue;
					// continue when not startDate < biDate < endDate
					if(!(sDate.compareTo(biDate) <= 0 && biDate.compareTo(eDate)<=0))
						continue;
					
					// biChange is now valid for a buggy label
					newLabel = "1";
			}
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return newLabel;
	}
	
	private static ArrayList<String> getValidBIChanges(String startDate, String endDate, String lastDateForFixCollection,
			HashMap<String, ArrayList<BIChange>> biChangesByKey) {
		
		ArrayList<String> validChanges = new ArrayList<String>();
		try {
			Date sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(startDate);  // GMT
			Date eDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(endDate); // GMT
			Date lDateForFixCollection = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(lastDateForFixCollection); // GMT
			
			for(String key:biChangesByKey.keySet()){
				for(BIChange biChange:biChangesByKey.get(key)){
					
						Date biDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(biChange.getBIDate());
						Date fixDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(biChange.getFixDate()); // load Date in local timezone
						
						if(fixDate.compareTo(lDateForFixCollection)>=0) // if fixDate is earlier than lastDateForFixCollection
							continue;
						if(!(sDate.compareTo(biDate) <= 0 && 0 >= biDate.compareTo(eDate)))
							continue;
						
						if(!validChanges.contains(key))
							validChanges.add(key);
					
					
					
				}	
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // load Date in local timezone
		return validChanges;
	}

	public static HashMap<String, String> getSha1sByChangeIDs(String pathToChangeIDSha1Pair) {
		
		HashMap<String, String> sha1sByChangeIDs = new HashMap<String, String>();
		
		// load a file
		ArrayList<String> lines = Utils.getLines(pathToChangeIDSha1Pair, false);
		
		for(String line:lines){
			String[] splitLine = line.split(","); // 0: change_id 1: sha1
			sha1sByChangeIDs.put(splitLine[0], splitLine[1]);
		}
		
		return sha1sByChangeIDs;
	}

	public static HashMap<String,ArrayList<BIChange>> getHashMapForBIChangesByKey(ArrayList<BIChange> biChanges) {
		
		HashMap<String,ArrayList<BIChange>> biChangesByKey = new HashMap<String,ArrayList<BIChange>>(); // key: biSha1+biPath
		
		for(BIChange biChange: biChanges){
			String key = biChange.getBISha1() + biChange.getBIPath().toLowerCase();
			
			if(biChangesByKey.containsKey(key)){
				biChangesByKey.get(key).add(biChange);
			} else{
				biChangesByKey.put(key, new ArrayList<BIChange>());
				biChangesByKey.get(key).add(biChange);
			}
			
		}
		
		return biChangesByKey;
	}
}
