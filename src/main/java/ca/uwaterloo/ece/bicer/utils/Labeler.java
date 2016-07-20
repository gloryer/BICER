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
import weka.core.Instance;
import weka.core.Instances;

public class Labeler {
	
	static public void relabelArff(String pathToArff,String classAttributeName,String positiveLabel,
									String pathToChangeIDSha1Pair,
									String pathToBIChangesforLabeling,String pathToNewArff,
									String startDate,String endDate,String lastDateForFixCollection){
		
		// load arff
		Instances instances = loadArff(pathToArff, classAttributeName);
		
		// load changd_id and sha1 pair
		HashMap<String,String> sha1sbyChangeIDs = getSha1sByChangeIDs(pathToChangeIDSha1Pair);
		
		// load BIChanges for labeling
		ArrayList<BIChange> biChangesForLabeling = Utils.loadBIChanges(pathToBIChangesforLabeling,true);
		// load all BIChanges to get all info
		HashMap<String,ArrayList<BIChange>> biChangesByKey = getHashMapForBIChangesByKey(biChangesForLabeling); // key: biSha1+biPath
		
		// relabel
		int count =0;
		ArrayList<String> changesLabeledAsBuggy = new ArrayList<String>();
		for(Instance instance:instances){
			String changeID = (int)instance.value(instances.attribute("change_id")) + "";
			String biPath = instance.stringValue(instances.attribute("412_full_path"));
			String biSha1 = sha1sbyChangeIDs.get(changeID);
			
			String key = biSha1 + biPath;
			
			String newLabel = getNewLabel(key,startDate,endDate,lastDateForFixCollection,biChangesByKey);
			
			if(newLabel.equals("1")){
				System.out.println("Labeled as buggy:" + key);
				changesLabeledAsBuggy.add(key);
				count++;
			}
			
			instance.setValue(instances.classAttribute(), newLabel);
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

	private static String getNewLabel(String key, String startDate, String endDate, String lastDateForFixCollection,
			HashMap<String, ArrayList<BIChange>> biChangesByKey) {
		
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

	private static HashMap<String, String> getSha1sByChangeIDs(String pathToChangeIDSha1Pair) {
		
		HashMap<String, String> sha1sByChangeIDs = new HashMap<String, String>();
		
		// load a file
		ArrayList<String> lines = Utils.getLines(pathToChangeIDSha1Pair, false);
		
		for(String line:lines){
			String[] splitLine = line.split(","); // 0: change_id 1: sha1
			sha1sByChangeIDs.put(splitLine[0], splitLine[1]);
		}
		
		return sha1sByChangeIDs;
	}

	private static HashMap<String,ArrayList<BIChange>> getHashMapForBIChangesByKey(ArrayList<BIChange> biChanges) {
		
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

	/**
	 * Load Instances from arff file. Last attribute will be set as class attribute
	 * @param path arff file path
	 * @return Instances
	 */
	public static Instances loadArff(String path,String classAttributeName){
		Instances instances=null;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(path));
			instances = new Instances(reader);
			reader.close();
			instances.setClassIndex(instances.attribute(classAttributeName).index());
		} catch (NullPointerException e) {
			System.err.println("Class label name, " + classAttributeName + ", does not exist! Please, check if the label name is correct.");
			instances = null;
		} catch (FileNotFoundException e) {
			System.err.println("Data file, " +path + ", does not exist. Please, check the path again!");
		} catch (IOException e) {
			System.err.println("I/O error! Please, try again!");
		}

		return instances;
	}

}
