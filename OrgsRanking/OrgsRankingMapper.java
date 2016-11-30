/**
 * Created by yangmeng on 11/18/16.
 */

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.mapreduce.Mapper;

import org.json.*;


public class JsonProfileMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException, JSONException{

        JSONObject jsonobj = new JSONObject(value);
        String type = jsonobj.getString("type");

        if (jsonobj.has("org")
                && (type.equals("fork")
                    || type.equals("watch") )) {
            JSONObject org = jsonobj.getJSONObject("org");
            Long orgId = org.getLong("id");
            String orgName = org.getString("login");
            context.write(new Text(orgId + ":" + orgName), new IntWritable(1));
        }
    }
}
