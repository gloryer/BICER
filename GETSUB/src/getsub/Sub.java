package getsub;

import java.io.*; 

public class Sub { 
	
  public static void main(String[] args) throws IOException{  
	
	String beforeCommit = "/home/shuaili/crazy/test/2009-07-30-2009-09-27/beforecommit/VECTOR/vdb_50_2";
	String afterCommit = "/home/shuaili/crazy/test/2009-07-30-2009-09-27/aftercommit/VECTOR/vdb_50_2";
	BufferedReader enBr = new BufferedReader(new FileReader(beforeCommit));
	BufferedReader frBr = new BufferedReader(new FileReader(afterCommit));
	while (true) { 
	    String read = enBr.readLine();
	    String partOne = enBr.readLine();
	    read = frBr.readLine();
	    String partTwo = frBr.readLine();
	    
	    if (partOne==null && partTwo == null)
	    	break;
	        
	    if (partOne == null){
        	String [] tokens2 = partTwo.split(" ");
            String [] tokens1 = new String[tokens2.length];
            for(int p=0; p<tokens1.length; p++) {
                tokens1[p] = String.valueOf(0);
               }

    	    int maxLength = Math.max(tokens1.length, tokens2.length);
    	    
    	    for (int i = 0; i < maxLength; i++){
    		    int [] before = new int[maxLength];
    		    int [] after = new int[maxLength];
    		    int [] subtraction = new int[maxLength];
    		    before[i]= Integer.parseInt(tokens1[i]);
    		    after[i]= Integer.parseInt(tokens2[i]);
    	        subtraction[i]= after[i]-before[i];
    	        System.out.print(subtraction[i] + " ");
    	        }   
    	    
    	     }
    
            else if (partTwo == null){
        	     String [] tokens1 = partOne.split(" ");
                 String [] tokens2 = new String[tokens1.length];
                 for(int q=0; q<tokens2.length; q++) {
                     tokens2[q] = String.valueOf(0);
                     }
                 int maxLength = Math.max(tokens1.length, tokens2.length);
         	    
         	     for (int i = 0; i < maxLength; i++){
         		    int [] before = new int[maxLength];
         		    int [] after = new int[maxLength];
         		    int [] subtraction = new int[maxLength];
         		    before[i]= Integer.parseInt(tokens1[i]);
         		    after[i]= Integer.parseInt(tokens2[i]);
         	        subtraction[i]= after[i]-before[i];
         	        System.out.print(subtraction[i] + " ");
         	        }   
        	     }
	    
                 else{
                	 String [] tokens1 = partOne.split(" ");
                     String [] tokens2 = partTwo.split(" ");
                  
                     int maxLength = Math.max(tokens1.length, tokens2.length);
         	    
         	         for (int i = 0; i < maxLength; i++){
         		          int [] before = new int[maxLength];
         		          int [] after = new int[maxLength];
         		          int [] subtraction = new int[maxLength];
         		          before[i]= Integer.parseInt(tokens1[i]);
         		          after[i]= Integer.parseInt(tokens2[i]);
         	              subtraction[i]= after[i]-before[i];
         	              System.out.print(subtraction[i] + " ");
         	              }   
                	 }
	    
	            System.out.print("\n");
                    	 
            	}
         }
            		
  }

   
      







