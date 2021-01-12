package cn.tedu.authinput;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AuthMapper extends Mapper<Text, Text, Text, IntWritable> {
    @Override
    protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        System.err.println("key:" + key + ", value:" + value);
        // key = tom
        // value = math 80 english 90
        String[] arr = value.toString().split(" ");
        context.write(key, new IntWritable(Integer.parseInt(arr[1])));
        context.write(key, new IntWritable(Integer.parseInt(arr[3])));
    }
}
