#!/bin/bash

#creat the directories of 10 date's pair for testset
#for i in 2008-02-06-2008-04-05 2008-04-06-2008-06-04 2008-06-05-2008-08-03 2008-08-04-2008-10-02 2008-10-03-2008-12-01 2008-12-02-2009-01-30 2009-04-01-2009-05-30 2009-04-01-2009-05-30 2009-05-31-2009-07-29 2009-01-05-2009-03-18
#do 
  #mkdir $i
#done

#change path to the location of your jackrabbit repository    
cd /home/shuaili/BI/jackrabbit

# the [commit_id]:[path] within the corresponing time period, for example, here are all the[commit_id]:[path] from 2008-02-06 to 2008-04-05, you need to modify it if you change the time period. I have attached different charvector.py in each directories.
for i in 483ac4e0a61444a2afa336b0a7e04ddc414addc7:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/security/user/UserAccessControlProvider.java 8478f6ffd6da90bf8ce9ae2416ec37e6a39cdc55:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/lock/LockManagerImpl.java 097764079ba6be54bd531948f3847855765b2345:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/value/InternalValue.java 793b9dfec55d4388aefa45b6aebb336fa0435f93:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/retention/RetentionRegistryImpl.java 9f7e620591da2b67f985c4130854f9a5076ad8c1:jackrabbit-jcr-server/src/main/java/org/apache/jackrabbit/server/remoting/davex/JsonWriter.java 9b8d2cd4a9a227784d586e87bf01d511acae3490:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/SessionImpl.java 728ff3a44862e3a5e2a43061f8f6a14003f9d102:jackrabbit-jcr-commons/src/main/java/org/apache/jackrabbit/commons/AbstractSession.java e903da2ca8f8635de1526e82bb5e1156204620c6:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/query/lucene/SharedFieldCache.java 3b8acc4edac828ca25253d78e496124b95207500:jackrabbit-spi-commons/src/main/java/org/apache/jackrabbit/spi/commons/batch/ConsolidatingChangeLog.java 9c222c79136b1f668eebdbf2af30ebee8bd96499:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/query/lucene/MultiIndex.java 0a6d34e4fe72f17fd5043edf48e59dce49a7692e:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/security/authorization/principalbased/ACLEditor.java 3b6044e1c50d787a1f69cefd1c0171b0b9844ad2:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/version/XAVersionManager.java
do 
#remove everything except digits, extract the commit id from the string.
SUBSTRING=$(echo $i| tr -cd '[[:digit:]]') 
#you can change the path where you want to store the after change source java code files 
git show $i >/home/shuaili/charvector/trainingset/2009-01-05-2009-03-18/afterchange/$SUBSTRING.java
#store the latest version of file before changes 
PREVIOUS=$(echo $i| sed 's/:/~1:/')
#you can change the path where you want to store the beforechange source java code files
git show $PREVIOUS >/home/shuaili/charvector/trainingset/2009-01-05-2009-03-18/beforechange/$SUBSTRING~1.java 
done

#run Deckard, you need to change to the directory where the config file located
cd /home/shuaili/charvector/trainingset/2009-01-05-2009-03-18
#/path/to/Deckard/scripts/clonedetect/deckard.sh
/home/shuaili/Deckard/scripts/clonedetect/deckard.sh

