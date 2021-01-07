package cn.tedu.charcount;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

// 实现Reduce阶段
// KEYIN, VALUEIN - 输入的键值类型
// Reduce的数据从Map来，所以Mapper的输出就是Reducer的输入
// KEYOUT, VALUEOUT - 输出的键值类型
// 当前案例中，最后应该输出每一个字符对应的次数
public class CharCountReducer
        extends Reducer<Text, LongWritable, Text, LongWritable> {
    // key：键。输入的键
    // values：值。在Reduce开始的时候，会自动的将相同的键对应的值分到一组去
    // 分组完成之后，会为每一个键形成一个迭代器
    // context：环境参数。可以利用这个参数将结果写出
    @Override
    protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
        // 目的：获取每一个字符对应的次数
        // key = 'a'
        // values = {1, 1, 1, 1, 1...}
        // 记录总次数
        long sum = 0;
        // 遍历values来求和
        for (LongWritable val : values) {
            sum += val.get();
        }
        context.write(key, new LongWritable(sum));
    }
}
