/**
 * Created by yangmeng on 12/5/16.
 */
/**
 * Created by Kaiwen on 12/4/16.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;


import org.json.*;

/**
 output:
 K          V
 orgID      list<Integer>(mood1%, mood2%， mood3%, mood4%, mood5%)

 **/

public class EmotionMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();

        HashSet<Long> orgSet = new HashSet<Long>();
        HashSet<String> stopWordSet = new HashSet<String>();

        HashMap<String, String> moodMap = new HashMap<>();

        /**
         text:
         orgName: orgID
         <String>   <Long>
         **/
        //1.
        for (String line : Files.readAllLines(Paths.get("orgsRanking.txt"))) {
            String[] orgInfo = line.split(":");
            orgSet.add(Long.parseLong(orgInfo[1]));
        }
        //2.
        for (String word : Files.readAllLines(Paths.get("stopWordSet.txt"))) {
            stopWordSet.add(word);
        }
        //3.
        for (String line : Files.readAllLines(Paths.get("emotionDict"))) {
            String[] eachLine = line.split(":");
            if (eachLine[0].equals("Angry")) {
                for (String word : eachLine[1].trim().split(" ")) {
                    moodMap.putIfAbsent(word.trim(), "angry");
                }
                continue;
            }
            if (eachLine[0].equals("Sad")) {
                for (String word : eachLine[1].trim().split(" ")) {
                    moodMap.putIfAbsent(word.trim(), "sad");
                }
                continue;
            }
            if (eachLine[0].equals("Happy")) {
                for (String word : eachLine[1].trim().split(" ")) {
                    moodMap.putIfAbsent(word.trim(), "happy");
                }
                continue;
            }
            if (eachLine[0].equals("Ecstatic")) {
                for (String word : eachLine[1].trim().split(" ")) {
                    moodMap.putIfAbsent(word.trim(), "ecstatic");
                }
                continue;
            }
            if (eachLine[0].equals("Anxious")) {
                for (String word : eachLine[1].trim().split(" ")) {
                    moodMap.putIfAbsent(word.trim(), "anxious");
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


