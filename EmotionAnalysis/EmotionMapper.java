/**
 * Created by yangmeng on 11/18/16.
 */

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;

import org.json.*;


public class JsonProfileMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();

        List<Long> orgsList = new Long[];

        HashSet<String> stopWords = new HashSet<String>();

        for (String word : Files.readAllLines(Paths.get("stopwords.txt"))) {
            stopWords.add(line);
        }

        emotionDict = 

    }

    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException, JSONException{

        JSONObject jsonobj = new JSONObject(value);
        Long eventId = jsonobj.getLong("id");
        String type = jsonobj.getString("type");

        JSONObject repo = jsonobj.getJSONObject("repo");
        JSONObject payload = jsonobj.getJSONObject("payload");

        Long repoId = repo.getLong("id");
        String repoName = repo.getString("name");

        if (jsonobj.has("org")
                && (type.equals("commit_comment")
                    || type.equals("issue_comment")
                    || type.equals("issues")
                    || type.equals("pull_request")
                    || type.equals("pull_request_review")
                    || type.equals("pull_request_review_comment")
                    || type.equals("push") )) {

            context.write(new LongWritable(eventId), value);
        }
    }
}
