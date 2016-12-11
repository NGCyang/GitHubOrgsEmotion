/**
 * Created by yangmeng on 12/5/16.
 */
/**
 * Created by Kaiwen on 12/4/16.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


/**
 *output:
 *K          V
 *orgID      list<Integer>(mood1%, mood2%， mood3%, mood4%, mood5%)
 *
 **/

public class EmotionMapper extends Mapper<LongWritable, Text, Text, Text> {
    private HashSet<Long> orgSet;
    private HashSet<String> stopWordSet;
    private HashMap<String, String> moodMap;
    private int model = 0;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();

        Path emtiondictPath = new Path(conf.get("EmotionDictPath"));
        Path orgsRankingPath = new Path(conf.get("OrgsRankingPath"));
        Path stopWordsPath = new Path(conf.get("StopWordsPath"));
        model = Integer.parseInt(conf.get("model"));
        FileSystem fs = FileSystem.get(conf);

        orgSet = new HashSet<Long>();
        stopWordSet = new HashSet<String>();
        moodMap = new HashMap<>();

        /**
         *text:
         *orgID:orgName:    \t comment String
         *<Long>  <String>      <String>
         *eg.
         *9900864:yCanta:
         *9919:github:    i think rescuing and reporting a failure is
         **/

        //1.
        BufferedReader br1 = new BufferedReader(new InputStreamReader(fs.open(orgsRankingPath)));
        String line1 = null;
        while ((line1 = br1.readLine()) != null) {
            String[] orgInfo = line1.trim().split(" +");
            orgSet.add(Long.parseLong(orgInfo[0].trim().split(":")[0].trim()));
        }
        br1.close();
        //2.
        BufferedReader br2 = new BufferedReader(new InputStreamReader(fs.open(stopWordsPath)));
        String line2 = null;
        while ((line2 = br2.readLine()) != null) {
            stopWordSet.add(line2.trim());
        }
        br2.close();
        //3.
        BufferedReader br3 = new BufferedReader((new InputStreamReader((fs.open(emtiondictPath)))));
        String line3 = null;
        if (model == 0) {
            //  Five Basic Emotion Model
            while ((line3 = br3.readLine()) != null) {
                String[] eachLine = line3.split(":");
                if (eachLine[0].trim().equals("Angry")) {
                    for (String word : eachLine[1].trim().split(",")) {
                        moodMap.put(word.trim(), "angry");
                    }
                    continue;
                }
                if (eachLine[0].trim().equals("Sad")) {
                    for (String word : eachLine[1].trim().split(",")) {
                        moodMap.put(word.trim(), "sad");
                    }
                    continue;
                }
                if (eachLine[0].trim().equals("Happy")) {
                    for (String word : eachLine[1].trim().split(",")) {
                        moodMap.put(word.trim(), "happy");
                    }
                    continue;
                }
                if (eachLine[0].trim().equals("Fear")) {
                    for (String word : eachLine[1].trim().split(",")) {
                        moodMap.put(word.trim(), "fear");
                    }
                    continue;
                }
                if (eachLine[0].trim().equals("Anxious")) {
                    for (String word : eachLine[1].trim().split(",")) {
                        moodMap.put(word.trim(), "anxious");
                    }
                    continue;
                }
            }
        } else if (model == 1) {
            // Subjective polarity model
            if ((line1 = br3.readLine())!= null) {
                for (String word : line1.trim().split(",")) {
                    moodMap.put(word.trim(), "positive");
                }
            }
            if ((line2 = br3.readLine())!= null) {
                for (String word : line2.trim().split(",")) {
                    moodMap.put(word.trim(), "negative");
                }
            }
            if ((line3 = br3.readLine())!= null) {
                for (String word : line3.trim().split(",")) {
                    moodMap.put(word.trim(), "neutural");
                }
            }
    }
        br3.close();
}


    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
        //1. get organ ID
        String line =value.toString();
        String[] splitline = line.split(":");

        Long orgId = Long.parseLong(splitline[0].trim());

        if (!orgSet.contains(orgId)) {
            return;
        }

        String orgName = splitline[1].trim();

        if (splitline.length < 3) {
            return;
        }
        String text = splitline[2].trim();

        //3. count length
        String[] cleaned_strArr = text.split(" +");
        int total = cleaned_strArr.length;

        /*4. filter & count emotinal words(update array)
        arr[0,1,2,3,4]
        angry, sad, happy, ecstatic, anxious
        */
        int dimension = 0;
        if (model == 0) {
            dimension = 5;
        } else if (model == 1) {
            dimension = 3;
        }

        int[] cntArr = new int[dimension];
        for (String word : cleaned_strArr) {
            String trimmed_word = word.trim();
            if (stopWordSet.contains(trimmed_word)) {
                --total;
                continue;
            }

            if (model == 0) {
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
                        case "fear":
                            ++cntArr[3];
                            break;
                        case "anxious":
                            ++cntArr[4];
                            break;
                        default:
                    }
                }
            } else if (model == 1) {
                if (moodMap.containsKey(trimmed_word)) {
                    String whichMood = moodMap.get(trimmed_word);
                    switch(whichMood) {
                        case "positive":
                            ++cntArr[0];
                            break;
                        case "negative":
                            ++cntArr[1];
                            break;
                        case "neutural":
                            ++cntArr[2];
                            break;
                        default:
                    }
                }
            }

        }

        String outputString = new String();
        for (int i = 0; i < dimension; ++i) {
            if (total != 0) {
                outputString += 1.0 * cntArr[i] / total;
            } else {
                outputString += "0.00";
            }
            outputString +=",";
        }
        outputString = outputString.substring(0, outputString.length() - 1);
        context.write(new Text(orgId + "," + orgName), new Text(outputString));

        return;
        /*
        K                       V
        "ID:orgName"         0.1,0.2,0.3,0.4,0.5
         */
    }
}
