package cn.tedu.friend;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FriendReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        // key:rose-tom
        // values = 0,1,0,1,1,1...
        // 遍历迭代器，如果值中出现了1，表示两个人真的认识
        // 那么就不是要找的隐藏好友
        int count = 0;
        for (IntWritable value : values) {
            if (value.get() != 0) return;
            count++;
        }
        // 如果整个循环结束都没有return
        context.write(key, new IntWritable(count));
    }
}
