/**
 * Created by yangmeng on 11/19/16.
 */
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class OrgsRankingReducer extends  Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        int indexsum = 0;
        for (IntWritable value : values) {
            indexsum += value;
        }
        context.write(key, new Text(value));
    }
}
