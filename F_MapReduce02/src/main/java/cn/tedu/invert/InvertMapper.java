package cn.tedu.invert;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class InvertMapper extends Mapper<LongWritable, Text, Text, Text> {

    private Text name;


    // setup方法发生在map方法之前，会先执行一次
    @Override
    protected void setup(Context context) {
        FileSplit fs = (FileSplit) context.getInputSplit();
        this.name = new Text(fs.getPath().getName());
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        // 拆分单词
        String[] arr = value.toString().split(" ");
        for (String s : arr) {
            context.write(new Text(s), name);
        }
    }
}
