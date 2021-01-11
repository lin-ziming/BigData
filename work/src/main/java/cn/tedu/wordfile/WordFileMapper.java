package cn.tedu.wordfile;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class WordFileMapper extends Mapper<LongWritable, Text,Text,Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] arr = value.toString().split(" ");
        /**
         * 获取文件名
         */
        FileSplit fs = (FileSplit) context.getInputSplit();
        String name = fs.getPath().getName();
        for (String word : arr) {
            context.write(new Text(word),new Text(name));
        }
    }
}
