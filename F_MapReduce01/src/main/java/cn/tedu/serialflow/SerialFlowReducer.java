package cn.tedu.serialflow;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * 需求：求每一个人花费的总流量
 */
public class SerialFlowReducer
        extends Reducer<Text,Flow, Text, IntWritable> {
    @Override
    protected void reduce(Text key, Iterable<Flow> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        for (Flow val : values) {
            sum += val.getFlow();
        }
        context.write(key,new IntWritable(sum));
    }
}


/**
 * 需求：每一个人去过的地方
 */
class SerialFlowReducer2 extends Reducer<Text,Flow,Text,Text>{
    @Override
    protected void reduce(Text key, Iterable<Flow> values, Context context) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        for (Flow val : values) {
            sb.append(val.getCity()).append("\t");
        }
        context.write(key,new Text(sb.toString()));
    }
}
