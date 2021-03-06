package cn.tedu.sortscore;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class SortScoreMapper extends Mapper<LongWritable, Text,Score, NullWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // Alex	211
        String[] arr = value.toString().split("\t");
        Score score = new Score();
        score.setName(arr[0]);
        score.setScore(Integer.parseInt(arr[1]));
        context.write(score,NullWritable.get());
    }
}
