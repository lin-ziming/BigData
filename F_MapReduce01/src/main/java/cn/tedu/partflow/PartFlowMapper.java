package cn.tedu.partflow;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class PartFlowMapper extends Mapper<LongWritable, Text,Text,Flow> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 13877779999 bj zs 5678
        String[] arr = value.toString().split(" ");
        Flow flow = new Flow();
        flow.setPhone(arr[0]);
        flow.setCity(arr[1]);
        flow.setName(arr[2]);
        flow.setFlow(Integer.parseInt(arr[3]));
        context.write(new Text(flow.getName()),flow);
    }
}
