package cn.tedu.multipleoutput;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class WordCountMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final IntWritable once = new IntWritable(1);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // value = hello rose hello joy
        // 拆分单词
        String[] arr = value.toString().split(" ");
        // 写出单词的次数
        for (String s : arr) {
            context.write(new Text(s), once);
        }
    }

}
