# GitHubOrgsEmotion

This is a hadoop mapreduce project on GitHub Timeline data set for NYU.

To run this code, you must have Hadoop Environment.

## Data Source Needed:
    1. GitHub timeline Data Set
    2. Emotion Dict : (Provided as )
    3. Stop words List

## Libraries required:
    1. org.json Library

## Set up Class Path and Environment:
    $ export LIBJARS=/path/json-20151123.jar
    $ export HADOOP_CLASSPATH=/path/json-20151123.jar


## Runing Analyatics:

### Part 1: Orgnization Ranking
    $ hadoop jar OrgsRanking.jar OrgsRankingTool -libjars ${LIBJARS} /path/githubdata/* /path/rankoutput

### Part 2: Text Extracting
    $  hadoop jar TextExtract.jar TextExtractingTool -libjars ${LIBJARS} /path/githubdata/* /user/my1421/textoutput

### Part 3: Emotion Analysis
    Version 1: using five dimision
    $ hadoop jar Emotion.jar EmotionTool -libjars ${LIBJARS} /path/textoutput/* /path/emotionoutput /path/githubdata/EmotionDict.txt /path/githubdata/sortedCompanyList /path/githubdata/stopwords.txt 0


    Version 2: using three dimison
    $ hadoop jar Emotion.jar EmotionTool -libjars ${LIBJARS} /path/textoutput/* /path/emotionoutput /path/githubdata/emotionDict_V2 /path/githubdata/sortedCompanyList /path/githubdata/stopwords.txt 1

### Part 4: Store Emotion Results into HIVE, analyze, display results in Tableau
    step1. Get results from several worker nodes and merge to 1 document
    $ hadoop fs -getmerge /user/my1421/emotionoutput5/ /home/my1421/Emotional/output.txt

    step2. Put onto HDFS
    $ hdfs dfs -put

    step3. Create table in HIVE
    hive>   CREATE EXTERNAL TABlE emotionTable0(orgid STRING, orgname STRING, angry STRING, sad STRING, happy STRING, anxious STRING, anxious STRING)
            row format delimited fields terminated by ','
            LOCATION '/user/my1421/emotion_result_merged/';

    step4. Have a general look of the Select * from emotionTable0;

    step5. Get top 10 of each mood
    hive>   SELECT * FROM emotionTable0 SORT BY angry DESC LIMIT 10;
            SELECT * FROM emotionTable0 SORT BY sad DESC LIMIT 10;
            SELECT * FROM emotionTable0 SORT BY happy DESC LIMIT 10;
            SELECT * FROM emotionTable0 SORT BY fear DESC LIMIT 10;
            SELECT * FROM emotionTable0 SORT BY anxious DESC LIMIT 10;

    step6. Save result to local text file
    $   hive -e "SELECT * FROM emotionTable0 SORT BY angry DESC LIMIT 10" >>/home/my1421/Emotional/angryTop10.txt
    $   hive -e "SELECT * FROM emotionTable0 SORT BY sad DESC LIMIT 10" >>/home/my1421/Emotional/sadTop10.txt
    $   hive -e "SELECT * FROM emotionTable0 SORT BY happy DESC LIMIT 10" >>/home/my1421/Emotional/happyTop10.txt
    $   hive -e "SELECT * FROM emotionTable0 SORT BY ecstatic DESC LIMIT 10" >>/home/my1421/Emotional/fearTop10.txt
    $   hive -e "SELECT * FROM emotionTable0 SORT BY anxious DESC LIMIT 10" >>/home/my1421/Emotional/anxiousTop10.txt

    step7. import 5 top10 .txt file, display in Tableau
