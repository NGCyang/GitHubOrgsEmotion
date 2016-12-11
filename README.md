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
    `$ export LIBJARS=/path/json-20151123.jar`
    `$ export HADOOP_CLASSPATH=/path/json-20151123.jar`


## Runing Analyatics:

### Part 1: Orgnization Ranking 
    `$ hadoop jar OrgsRanking.jar OrgsRankingTool -libjars ${LIBJARS} /path/githubdata/* /path/rankoutput`

### Part 2: Text Extracting
    `$  hadoop jar TextExtract.jar TextExtractingTool -libjars ${LIBJARS} /path/githubdata/* /user/my1421/textoutput`

### Part 3: Emotion Analysis
    Version 1: using five dimision
    `$ hadoop jar Emotion.jar EmotionTool -libjars ${LIBJARS} /path/textoutput/* /path/emotionoutput /path/githubdata/EmotionDict.txt /path/githubdata/sortedCompanyList /path/githubdata/stopwords.txt 0`


    Version 2: using three dimison
    `$ hadoop jar Emotion.jar EmotionTool -libjars ${LIBJARS} /path/textoutput/* /path/emotionoutput /path/githubdata/emotionDict_V2 /path/githubdata/sortedCompanyList /path/githubdata/stopwords.txt 1`

