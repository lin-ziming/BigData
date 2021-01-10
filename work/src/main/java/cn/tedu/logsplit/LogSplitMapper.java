package cn.tedu.logsplit;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class LogSplitMapper extends Mapper<LongWritable, Text,Log, NullWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String s = value.toString();
        Log log = new Log();
        log.setIP(s.matches("[1-9]*[0-9]+"));

    }
}