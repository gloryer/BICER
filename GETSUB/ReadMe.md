#Collect characteristic vectors using deckard

##Background:
What is characteristic vectors?<br />
https://www.cse.ust.hk/~hunkim/papers/jiang-ase2013.pdf<br />
Deckard tool:<br />
http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=4222572&tag=1<br />
https://github.com/skyhover/Deckard<br />

##Task description:
For example:<br />
- Collect Deckard features from 2007-09-13 00:00:00 to 2007-11-14 23:59:59 for training set and 2008-02-06 to 2008-04-05 for test set.<br />
- Here are the dates for test and training:
```
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
```
##stage 1:Collecting source file code before change and after change

- Clone Java project BICER (https://github.com/lifove/BICER.git)
- Modify two java files:
   - For file src/test/java/ca/uwaterloo/ece/bicer/BICCOllecterTest.java.<br />
     Specify the startdate, enddate, and label date
   - For file src/main/java/ca/uwaterloo/ece/bicer/BICCOllector.java.<br />
     print the file source or prev-file source to BICER/build/test-results/TEST-ca.uwaterloo.ece.bicer.BICCollectorTest.xml
- Modify the charvector.sh 
   - you need to point out your path<br />
   - remember to change the date and point out whether you are collcet the prev-file source(beforechange)
   or the file source(afterchange)
- run the ./charvector.sh 
   - It will build the gradle<br />
   - Generating all the files in TEST-ca.uwaterloo.ece.bicer.BICCollectorTest.xml.<br />
   - And the bash script will extract the content from the xml file to save as source.txt.<br />
   - Next, the large source.txt file will be divided into individual java files
   - Finally, we can use Deckard the generate the vectors based on the java files we just get, you can see the vectors in directory VECTOR.
 
##stage 2: Get the subtraction of prev-vector and vector

- run the Sub.java in GETSUB project. This will atomatically make subtraction of two versions of vectors file.


