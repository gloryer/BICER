#!/bin/bash

#creat the directories of 10 date's pair for testset
#for i in 2007-09-13-2007-11-24 2008-01-11-2008-03-23 2008-03-11-2008-05-22 2008-05-10-2008-07-21 2009-03-06-2009-05-17 2008-09-07-2008-11-18 2008-11-06-2009-01-17 2009-03-06-2009-05-17 2009-03-06-2009-05-17
#do 
 # mkdir -p $i/beforecommit
 # mkdir -p $i/aftercommit
#done
cd /home/shuaili/BICER/
gradle build

cd /home/shuaili/BICER/build/test-results/
#extract the contents in <system-out> tag to text
xml_grep 'system-out' TEST-ca.uwaterloo.ece.bicer.BICCollectorTest.xml --text_only > source.txt

cp source.txt /home/shuaili/crazy/training/2009-03-06-2009-05-17/aftercommit/
#cp source.txt /home/shuaili/crazy/training/2009-03-06-2009-05-17/beforecommit

cd /home/shuaili/crazy/training/2009-03-06-2009-05-17/aftercommit/
#cd /home/shuaili/crazy/training/2009-03-06-2009-05-17/beforecommit/
#seperate the large souce file into every single java files
awk '{print $0 > "file" NR ".java"}' RS='-\\|' source.txt

#use deckard to extract vector features
/home/shuaili/Deckard/scripts/clonedetect/vdbgen


