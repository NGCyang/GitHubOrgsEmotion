/**
 * Created by yangmeng on 12/7/16.
 */
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Mapper;

import org.json.*;

public class TextExtractingMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        try{
            JSONObject jsonobj = new JSONObject(value.toString());
            String type = jsonobj.getString("type");

            if (!jsonobj.has("org")) {
                return;
            }
            JSONObject org = jsonobj.getJSONObject("org");

            Long orgId = org.getLong("id");
            String orgName = org.getString("login");
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

            context.write(new Text(orgId + ":" + orgName + ":"), new Text(sentence.toLowerCase().replaceAll("[^a-zA-Z]+"," ")));
        } catch (JSONException e) {
            System.out.println("JSON Exception!");
        }

//        JSONObject jsonobj = new JSONObject(value.toString());
//        String type = jsonobj.getString("type");
//
//        if (jsonobj.has("org")) {
//            JSONObject org = jsonobj.getJSONObject("org");
//            Long orgId = org.getLong("id");
//            String orgName = org.getString("login");
//            context.write(new Text(orgName), new IntWritable(1));
//        }
    }


}
