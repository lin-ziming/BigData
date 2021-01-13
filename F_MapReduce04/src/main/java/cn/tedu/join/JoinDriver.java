package cn.tedu.join;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;

public class JoinDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(JoinDriver.class);
        job.setMapperClass(JoinMapper.class);
        job.setReducerClass(JoinReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Order.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        // 设置输入路径
        // 因为这次处理的文件相互关联，所以需要在文件中来确定一个主处理文件
        // 需求：获取每一天卖了多少钱 - 最终输出的键是日期，值是钱
        // 因为键所在的文件是order.txt，所以将order.txt作为主处理文件
        FileInputFormat.addInputPath(job,
                new Path("hdfs://hadoop01:9000/txt/union/order.txt"));
        // 另外一个关联的文件放到缓存中
        URI[] files = {URI.create("hdfs://hadoop01:9000/txt/union/product.txt")};
        job.setCacheFiles(files);

        FileOutputFormat.setOutputPath(job,
                new Path("hdfs://hadoop01:9000/result/join"));

        job.waitForCompletion(true);

    }

}
