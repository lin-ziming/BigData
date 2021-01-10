package cn.tedu.serialscore;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class SerialScoreReducer
        extends Reducer<Text,Score,Text, DoubleWritable> {
    @Override
    protected void reduce(Text key, Iterable<Score> values, Context context) throws IOException, InterruptedException {
        for (Score value : values) {
            double sum = 0;
            int[] scores = value.getScores();
            for (int score : scores) {
                sum += score;
            }
            context.write(key,new DoubleWritable(sum / scores.length));
        }
    }
}
