package cn.tedu.friend1;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FriendReducer1 extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // key:rose-tom
        // values = 0,1,0,1,1,1...
        // 遍历迭代器，如果值中出现了1，表示两个人真的认识
        // 那么就不是要找的隐藏好友
        StringBuilder comFriend = new StringBuilder();
        int count = 0;
        for (Text value : values) {
            if (value.toString().equals("1")) return;
            count++;
            comFriend.append(value + "\t");
        }
        // 如果整个循环结束都没有return
        context.write(key, new Text(comFriend.toString() + count));
    }
}
