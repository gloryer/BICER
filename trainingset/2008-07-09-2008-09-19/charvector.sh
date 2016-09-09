#!/bin/bash

#creat the directories of 10 date's pair for testset
#for i in 2008-02-06-2008-04-05 2008-04-06-2008-06-04 2008-06-05-2008-08-03 2008-08-04-2008-10-02 2008-10-03-2008-12-01 2008-12-02-2009-01-30 2009-04-01-2009-05-30 2009-04-01-2009-05-30 2009-05-31-2009-07-29 2008-07-09-2008-09-19
#do 
  #mkdir $i
#done

#change path to the location of your jackrabbit repository    
cd /home/shuaili/BI/jackrabbit

# the [commit_id]:[path] within the corresponing time period, for example, here are all the[commit_id]:[path] from 2008-02-06 to 2008-04-05, you need to modify it if you change the time period. I have attached different charvector.py in each directories.
for i in d631e0725f4a170b463347b95995414d8cb8509f:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/query/lucene/IndexingConfigurationImpl.java ac55ea6cc8563ca3712c70afd1d2b3a4f5d6ab52:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/jndi/BindableRepositoryFactory.java 33487dce76eb896118c5ebac0095e6725c264aed:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/query/lucene/IndexingConfigurationImpl.java 6fde44d5a8dfb33db8e1128bd00c46977f9dc5a5:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/ItemImpl.java 7f5c4d5c0235135db27fd0da0f8f1041a1900c71:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/query/lucene/NodeIndexer.java 087adacbb3f4f8e88d0d8aa5c907ddc08290cfa1:jackrabbit-jcr-server/src/main/java/org/apache/jackrabbit/webdav/jcr/JcrValueType.java f648405831112b1dfe4b033a4d2fae983ad96bb5:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/security/user/UserManagerImpl.java eac01367b78a5bf4474f171a09205a16f35699d8:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/xml/BufferedStringValue.java 45ba507bdf942e12b0ab179b975d41682a27b9fc:jackrabbit-webapp/src/main/java/org/apache/jackrabbit/j2ee/RepositoryStartupServlet.java f3c0427a88c42dfa632349dd4c4ab293c8300b41:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/config/RepositoryConfigurationParser.java e40b44b29a5774dd50742732b5c02ae96dd49ec9:jackrabbit-core/src/main/java/org/apache/jackrabbit/core/query/lucene/IndexMigration.java 
do 
#remove everything except digits, extract the commit id from the string.
SUBSTRING=$(echo $i| tr -cd '[[:digit:]]') 
#you can change the path where you want to store the after change source java code files 
git show $i >/home/shuaili/charvector/trainingset/2008-07-09-2008-09-19/afterchange/$SUBSTRING.java
#store the latest version of file before changes 
PREVIOUS=$(echo $i| sed 's/:/~1:/')
#you can change the path where you want to store the beforechange source java code files
git show $PREVIOUS >/home/shuaili/charvector/trainingset/2008-07-09-2008-09-19/beforechange/$SUBSTRING~1.java 
done

#run Deckard, you need to change to the directory where the config file located
cd /home/shuaili/charvector/trainingset/2008-07-09-2008-09-19
#/path/to/Deckard/scripts/clonedetect/deckard.sh
/home/shuaili/Deckard/scripts/clonedetect/deckard.sh

