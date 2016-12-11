/**
 * Created by yangmeng on 12/5/16.
 */
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class EmotionReducer extends  Reducer<Text, Text, Text, Text> {
    private int model = 0;
    /*
    input:
    K                       V
    "ID:orgName"         "0.1,0.2,0.3,0.4,0.5"; "0.0,0.2,0.0,0.4,0.0"; ..

    output:
    K                       V
    "ID:orgName"         "0.1,0.4,0.3,0.8,0.5"
     */
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        model = Integer.parseInt(conf.get("model"));
    }

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        int dimension = 3;
        if (model == 0) {
            dimension = 5;
        } else if (model == 1) {
            dimension = 3;
        }

        double[] mood_sum_arr = new double[dimension];
        int cnt_comments = 0;
        for (Text value : values) {
            String[] vals = value.toString().split(",");
            for (int i = 0; i < dimension; ++i) {
                mood_sum_arr[i] += Double.parseDouble(vals[i].trim());
            }

            ++cnt_comments;
        }

        for (int i = 0; i < dimension; ++i) {
            mood_sum_arr[i] = mood_sum_arr[i] / cnt_comments * 100;
        }

        String str_result = new String();
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        for (double each_mood_val : mood_sum_arr) {
            str_result += df.format(each_mood_val) + ",";
        }
        context.write(key, new Text(str_result.substring(0, str_result.length() - 1)));
    }
}