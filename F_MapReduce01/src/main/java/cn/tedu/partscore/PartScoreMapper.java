package cn.tedu.partscore;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class PartScoreMapper extends Mapper<LongWritable, Text,Text,Score> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 1 zhang 89
        String[] arr = value.toString().split(" ");
        Score score = new Score();
        score.setMonth(Integer.parseInt(arr[0]));
        score.setName(arr[1]);
        score.setScore(Integer.parseInt(arr[2]));
        context.write(new Text(score.getName()),score);
    }
}
