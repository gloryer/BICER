package ca.uwaterloo.ece.bicer.utils;

import java.util.ArrayList;
import java.util.HashMap;

import ca.uwaterloo.ece.bicer.data.BIChange;
import weka.core.Instances;

public class ArffInstanceFilter {
	static public void filterByDeletedLineSize(String pathToArff,
												String classAttributeName,
												String positiveLabel,
												String pathToChangeIDSha1Pair,
												String pathToBIChangesforLabeling,
												String pathToNewArff,
												String startDate,
												String endDate,
												String lastDateForFixCollection){
		
		// load arff
		Instances instances = Utils.loadArff(pathToArff, classAttributeName);
		
		// load changd_id and sha1 pair
		HashMap<String,String> sha1sbyChangeIDs = Labeler.getSha1sByChangeIDs(pathToChangeIDSha1Pair);
		
		// load BIChanges for labeling
		ArrayList<BIChange> biChangesForLabeling = Utils.loadBIChanges(pathToBIChangesforLabeling,true);
		// load all BIChanges to get all info
		HashMap<String,ArrayList<BIChange>> biChangesByKey = Labeler.getHashMapForBIChangesByKey(biChangesForLabeling); // key: biSha1+biPath
		
	}
}
