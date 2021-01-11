package cn.tedu.wordfile;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class WordFileReducer extends Reducer<Text,Text,Text,Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        StringBuffer file = new StringBuffer();
        for (Text value : values) {
            file.append(value.toString()).append("\t");
        }
        context.write(key,new Text(file.toString()));
    }
}
