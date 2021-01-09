package cn.tedu.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

// 完成Map阶段
// 在MapReduce中，要求被传输的数据能够被序列化
// KEYIN - 输入的键的类型。如果不指定，则表示行的字节偏移量
// VALUEIN - 输入的值的类型。如果不指定，则表示读取的一行数据
// KEYOUT - 输出的键的类型。当前案例中，输出的键是字符
// VALUEOUT - 输出的值的类型。当前案例中，输出的值是次数
public class WordCountMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final IntWritable once = new IntWritable(1);

    // MapTask的处理逻辑就是覆盖在这个方法中
    // key：键。行的字节偏移量
    // value：值。读取的一行数据
    // context：环境参数。利用这个参数将结果写出到Reduce
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // value = hello rose hello joy
        // 拆分单词
        String[] arr = value.toString().split(" ");
        // 写出单词的次数
        for (String s : arr) {
            context.write(new Text(s), once);
        }
    }
}
