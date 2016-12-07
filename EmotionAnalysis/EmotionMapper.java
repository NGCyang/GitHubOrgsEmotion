/**
 * Created by yangmeng on 12/5/16.
 */
/**
 * Created by Kaiwen on 12/4/16.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;


import org.json.*;

/**
 *output:
 *K          V
 *orgID      list<Integer>(mood1%, mood2%， mood3%, mood4%, mood5%)
 *
 **/

public class EmotionMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
    private HashSet<Long> orgSet;
    private HashSet<String> stopWordSet;
    private HashMap<String, String> moodMap;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();

        Path emtiondictPath = new Path(conf.get("EmotionDictPath"));
        Path orgsRankingPath = new Path(conf.get("OrgsRankingPath"));
        Path stopWordsPath = new Path(conf.get("StopWordsPath"));

        FileSystem fs = FileSystem.get(conf);


        orgSet = new HashSet<Long>();
        stopWordSet = new HashSet<String>();
        moodMap = new HashMap<>();

        /**
         *text:
         *orgName: orgID    \t 10000
         *<String>   <Long>
         **/

        //1.
        BufferedReader br1 = new BufferedReader(new InputStreamReader(fs.open(orgsRankingPath)));
        String line1 = null;
        while ((line1 = br1.readLine()) != null) {
            String[] orgInfo = line1.trim().split(" +");
            orgSet.add(Long.parseLong(orgInfo[0].trim().split(":")[1].trim()));
        }
        //2.
        BufferedReader br2 = new BufferedReader(new InputStreamReader(fs.open(stopWordsPath)));
        String line2 = null;
        while ((line2 = br2.readLine()) != null) {
            stopWordSet.add(line2.trim());
        }
        //3.
        BufferedReader br3 = new BufferedReader((new InputStreamReader((fs.open(emtiondictPath)))));
        String line3 = null;
        while ((line3 = br3.readLine()) != null) {
            String[] eachLine = line3.split(":");
            if (eachLine[0].equals("Angry")) {
                for (String word : eachLine[1].trim().split(" ")) {
                    moodMap.put(word.trim(), "angry");
                }
                continue;
            }
            if (eachLine[0].equals("Sad")) {
                for (String word : eachLine[1].trim().split(" ")) {
                    moodMap.put(word.trim(), "sad");
                }
                continue;
            }
            if (eachLine[0].equals("Happy")) {
                for (String word : eachLine[1].trim().split(" ")) {
                    moodMap.put(word.trim(), "happy");
                }
                continue;
            }
            if (eachLine[0].equals("Ecstatic")) {
                for (String word : eachLine[1].trim().split(" ")) {
                    moodMap.put(word.trim(), "ecstatic");
                }
                continue;
            }
            if (eachLine[0].equals("Anxious")) {
                for (String word : eachLine[1].trim().split(" ")) {
                    moodMap.put(word.trim(), "anxious");
                }
                continue;
            }
        }
    }


    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException, JSONException{
        //1. get organ ID
        JSONObject jsonobj = new JSONObject(value.toString());
        String type = jsonobj.getString("type");

        //exit directly
        if (!jsonobj.has("org")) {
            return;
        }
        JSONObject org = jsonobj.getJSONObject("org");
        if (!orgSet.contains(org.getLong("id"))) {
            return;
        }

        Long orgId = org.getLong("id");
        JSONObject payload = jsonobj.getJSONObject("payload");

        //2. get String body
        String sentence = "";
        if (type.equals("CommitCommentEvent")) {
            JSONObject comment = payload.getJSONObject("comment");
            sentence = comment.getString("body");
        } else if (type.equals("IssueCommentEvent")) {
            JSONObject comment = payload.getJSONObject("comment");
            sentence = comment.getString("body");
        } else if(type.equals("IssuesEvent")) {
            JSONObject issue = payload.getJSONObject("issue");
            sentence = issue.getString("body");
        } else if (type.equals("PullRequestEven")) {
            JSONObject pull = payload.getJSONObject("pull_request");
            sentence = pull.getString("body");
        } else if (type.equals("pull_request_review")) {
            JSONObject pull = payload.getJSONObject("pull_request");
            sentence = pull.getString("body");
        } else if (type.equals("PullRequestReviewCommentEvent")) {
            JSONObject comment = payload.getJSONObject("comment");
            sentence = comment.getString("body");
        }

        //3. count length
        String[] cleaned_strArr = sentence.split("[^a-zA-Z1-9']+");
        int total = cleaned_strArr.length;

        /*4. filter & count emotinal words(update array)
        arr[0,1,2,3,4]
        angry, sad, happy, ecstatic, anxious
        */
        int[] cntArr = new int[5];
        for (String word : cleaned_strArr) {
            String trimmed_word = word.trim();
            if (stopWordSet.contains(trimmed_word)) {
                --total;
                continue;
            }

            if (moodMap.containsKey(trimmed_word)) {
                String whichMood = moodMap.get(trimmed_word);
                switch(whichMood) {
                    case "angry":
                        ++cntArr[0];
                        break;
                    case "sad":
                        ++cntArr[1];
                        break;
                    case "happy":
                        ++cntArr[2];
                        break;
                    case "ecstatic":
                        ++cntArr[3];
                        break;
                    case "anxious":
                        ++cntArr[4];
                        break;
                    default:
                }
            }
        }

        String outputString = new String();
        for (int i = 0; i < 5; ++i) {
            outputString += 1.0 * cntArr[i] / total;
            outputString +=",";
        }
        outputString = outputString.substring(0, outputString.length() - 1);
        context.write(new LongWritable(orgId), new Text(outputString));
    }

}
