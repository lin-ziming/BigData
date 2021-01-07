package cn.tedu.charcount;

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
public class CharCountMapper
        extends Mapper<LongWritable, Text, Text, LongWritable> {

    private final LongWritable once = new LongWritable(1);

    // MapTask的处理逻辑就是覆盖在这个方法中
    // key：键。行的字节偏移量
    // value：值。读取的一行数据
    // context：环境参数。利用这个参数将结果写出到Reduce
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // value表示一行数据，例如：hello
        // 拿到一行数据之后，将一行数据中的字符拆分出来
        char[] cs = value.toString().toCharArray();
        // cs = {'h', 'e', 'l', 'l', 'o'}
        // h:1 e:1 l:2 o:1
        // h:1 e:1 l:1 l:1 o:1
        for (char c : cs) {
            context.write(new Text(c + ""), once);
        }
    }
}
