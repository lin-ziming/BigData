package cn.tedu.invert;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class InvertReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // 用这个Set来完成去重
        Set<String> set = new HashSet<>();
        for (Text value : values) {
            set.add(value.toString());
        }
        // 文件名拼接
        StringBuilder sb = new StringBuilder();
        for (String s : set) {
            sb.append(s).append("\t");
        }
        context.write(key, new Text(sb.toString()));
    }
}
