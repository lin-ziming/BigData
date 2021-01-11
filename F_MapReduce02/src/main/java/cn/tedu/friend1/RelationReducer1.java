package cn.tedu.friend1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RelationReducer1 extends Reducer<Text, Text, Text, IntWritable> {

    private final IntWritable trueFriend = new IntWritable(1);
    private final IntWritable fakeFriend = new IntWritable(0);

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // tom
        // values = rose jim smith lucy
        // 表达真实的好友关系
        String name = key.toString();
        List<String> list = new ArrayList<>();
        for (Text value : values) {
            String f = value.toString();
            list.add(f);
            if (name.compareTo(f) < 0)
                context.write(new Text(name + "-" + f), trueFriend);
            else
                context.write(new Text(f + "-" + name), trueFriend);
        }
        // 猜测好友关系
        for (int i = 0; i < list.size() - 1; i++) {
            String f1 = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                String f2 = list.get(j);
                if (f1.compareTo(f2) < 0)
                    context.write(new Text(f1 + "-" + f2 + "\t" + key), fakeFriend);
                else
                    context.write(new Text(f2 + "-" + f1 + "\t" + key), fakeFriend);

            }
        }

    }
}
