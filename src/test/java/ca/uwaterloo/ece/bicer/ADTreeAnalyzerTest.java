package ca.uwaterloo.ece.bicer;
import org.junit.Test;

import ca.uwaterloo.ece.bicer.utils.Labeler;
import ca.uwaterloo.ece.bicer.utils.Utils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/*
 * This Java source file was auto generated by running 'gradle init --type java-library'
 * by 'j22nam' at '27/05/16 5:45 PM' with Gradle 2.13",
 *
 * @author j22nam, @date 27/05/16 5:45 PM
 */
public class ADTreeAnalyzerTest {
    @Test public void testSomeLibraryMethod() {
    	String pathForTPBIs = System.getProperty("user.home") + "/Documents/ODP/projects/jackrabbit/arffsManuallyFilteredWOTestCases/0/TPBIs.txt";
    	ArrayList<String> TPBIs = Utils.getLines(pathForTPBIs, false);
    	
    	String pathForArff = System.getProperty("user.home") + "/Documents/ODP/projects/jackrabbit/arffsManuallyFilteredWOTestCases/0/test.arff";
    	String classAttributeName = "500_Buggy?";
    	Instances instances = Utils.loadArff(pathForArff, classAttributeName);
    	
    	HashMap<String,Instance> mapInstances = new HashMap<String,Instance>(); // key = change_id, value = instance
    	
    	for(Instance instance: instances){
    		String changeId = ((int)instance.value(0)) + "";
    		mapInstances.put(changeId, instance);
    	}
    	
    	for(String line:TPBIs){
    		String[] splitLine = line.split(",");
    		String changeId = splitLine[0];
    		
    		System.out.println("\n" + line);
    		
    		if(mapInstances.containsKey(changeId)){
    			Instance instance = mapInstances.get(changeId);
    			
    			System.out.println("path: " + instance.stringValue(instances.attribute("412_full_path")));
    			
    			printPositiveNode(instances,instance,"<",25,"405_file_lines","1.099");
    			printPositiveNode(instances,instance,"==",-1,"412_full_path","0.587");
    			printPositiveNode(instances,instance,">=",2.5,"10645_statemgr","0.16");
    			printPositiveNode(instances,instance,"<",0.5,"402_lines_deleted","0.1");
    			printPositiveNode(instances,instance,">=",11.5,"10175_requir","0.07");
    			printPositiveNode(instances,instance,"<",45,"415_lines_removed","0.628");
    			printPositiveNode(instances,instance,">=",5,"10067_return","1.445");
    			
    		}
    	}
    	
	}
    
    private void printPositiveNode(Instances instances,Instance instance,
    		String condition,double conditionValue,String attrName, String posScore){
    	
    	int attrIdx = instances.attribute(attrName).index();
    	
    	if(condition.equals("<")){
    	
			if(instance.value(instances.attribute(attrIdx)) < conditionValue){
				System.out.println(attrName +" = " + instance.value(attrIdx) + " " + condition + " " + conditionValue + " : " + posScore );
			}
    	}else if(condition.equals(">=")){
    		if(instance.value(instances.attribute(attrIdx)) >= conditionValue){
				System.out.println(attrName +" = " + instance.value(attrIdx) + " " + condition + " " + conditionValue + " : " + posScore );
			}
    	}else{
    		if(instance.stringValue(instances.attribute(attrName)).equals("jackrabbit-core/src/main/java/org/apache/jackrabbit/core/propertyimpl.java")){
				System.out.println(attrName +" = " + instance.value(attrIdx) + " " + condition + " " + "propertyimpl.java" + " : " + posScore );
			}
    	}
    }
}
