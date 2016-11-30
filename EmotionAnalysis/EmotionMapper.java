/**
 * Created by yangmeng on 11/28/16.
 */

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;

import org.json.*;


public class EmotionMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();

        HashSet<Long> orgsList = new HashSet<Long>();
        HashSet<String> stopWords = new HashSet<String>();

        for (String line : Files.readAllLines(Paths.get("orgsRanking.txt"))) {
            String[] orgInfo = line.split(":");
            orgsList.add(Long.parseLong(orgInfo[1]));
        }

        for (String word : Files.readAllLines(Paths.get("stopwords.txt"))) {
            stopWords.add(line);
        }

        """
        emotionDict: {
            keyword : 0  //Angry
                    : 1  //Sad
                    : 2  //Happy
                    : 3  //Ecstatic
                    : 4  //Anxious
        }
        """
        HashMap<String, Integer> emotionDict = ; 

    }

    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException, JSONException{

        JSONObject jsonobj = new JSONObject(value);
        String type = jsonobj.getString("type");

        if (!jsonobj.has("org")) {
            return;
        } 

        JSONObject org = jsonobj.getJSONObject("org");
        if (!orgList.contains(org.getLong("id"))) {
            return;
        }

        JSONObject payload = jsonobj.getJSONObject("payload");

        if (type.equals("commit_comment")) {
            JSONObject comment = payload.getJSONObject("comment");
            String body = comment.getString("body");
            emotionClassifation(body);

        } else if (type.equals("issue_comment")) {

        } else if(type.equals("issues")) {

        } else if (type.equals("pull_request")) {

        } else if (type.equals("pull_request_review")) {

        } else if (type.equals("pull_request_review_comment")) {

        } else if (type.equals("push") ) {
            
        } 

        context.write(new LongWritable(orgId), value);
    }

    private List<Double> emotionClassifation(Sting text) {
        ArrayList<Double> score = new ArrayList<>();
        String[] words = text.split(" ");
        for (String word : words) {
            if (emotionDict.contains(word)) {
                score.set(emotionDict.get(word), 1);
            }
        }
    }
}
