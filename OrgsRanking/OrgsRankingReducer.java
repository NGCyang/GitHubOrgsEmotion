/**
 * Created by yangmeng on 12/4/16.
 */
import java.io.IOException;
import java.util.*;
import java.util.PriorityQueue;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class OrgsRankingReducer extends  Reducer<Text, IntWritable, Text, IntWritable> {
    private PriorityQueue<Pair> Q = null;
    private int k = 20;

    private Comparator<Pair> pairComparator = new Comparator<Pair>() {
        public int compare(Pair left, Pair right) {
            if (left.value != right.value) {
                return left.value - right.value;
            }
            return right.key.compareTo(left.key);
        }
    };

    @Override
    public void setup(Context context) {
        // initialize your data structure here
        //this.k = k;
        Q = new PriorityQueue<Pair>(k, pairComparator);
    }

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        int indexsum = 0;
        for (IntWritable value : values) {
            //indexsum += value.get();
            indexsum++;
        }

        Pair pair = new Pair(key.toString(), indexsum);
        if (Q.size() < k) {
            Q.add(pair);
        } else {
            Pair peak = Q.peek();
            if (pairComparator.compare(pair, peak) > 0) {
                Q.poll();
                Q.add(pair);
            }
        }
    }

    public void cleanup(Context context) {
        // Output the top k pairs <word, times> into output buffer.

        List<Pair> pairs = new ArrayList<Pair>();
        while (!Q.isEmpty()) {
                pairs.add(Q.poll());
            }

        // reverse result
        int n = pairs.size();
        for (int i = n - 1; i >= 0; --i) {
            Pair pair = pairs.get(i);
            try {
                context.write(new Text(pair.key), new IntWritable(pair.value));
            } catch (IOException ioe) {
                System.out.println("IOException in Reducer write process!");
            } catch (InterruptedException ine) {
                System.out.println("InterruptedException in Reducer write process!");
            }
        }
        //context.write(key, new IntWritable(indexsum));
    }
}

class Pair {
    String key;
    int value;

    Pair(String key, int value) {
        this.key = key;
        this.value = value;
    }
}