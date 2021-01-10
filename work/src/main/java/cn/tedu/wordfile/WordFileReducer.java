package cn.tedu.wordfile;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class WordFileReducer extends Reducer<Text,String,Text,Text> {
    @Override
    protected void reduce(Text key, Iterable<String> values, Context context) throws IOException, InterruptedException {
        StringBuffer file = new StringBuffer();
        for (String value : values) {
            file.append(value).append("\t");
        }
        context.write(key,new Text(file.toString()));
    }
}
