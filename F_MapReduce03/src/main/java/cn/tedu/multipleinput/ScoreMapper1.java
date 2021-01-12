package cn.tedu.multipleinput;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

// 处理score2目录中的数据
public class ScoreMapper1 extends Mapper<LongWritable, Text, Text, IntWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // Bob 50
        String[] arr = value.toString().split(" ");
        context.write(new Text(arr[0]), new IntWritable(Integer.parseInt(arr[1])));
    }
}
