/**
 * Created by yangmeng on 12/5/16.
 */
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.conf.*;

public class EmotionTool extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new EmotionTool(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {

        // When implementing tool
        Configuration conf = new Configuration();
        conf.set("EmotionDictPath", args[2]);
        conf.set("OrgsRankingPath", args[3]);


        // Create job
        //Job job = new Job(conf, "EmotionAnalysis");
        Job job = Job.getInstance();
        job.setJarByClass(EmotionTool.class);
        //job.setNumReduceTasks(1);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(EmotionMapper.class);
        job.setReducerClass(EmotionReducer.class);

        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(Text.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }
}