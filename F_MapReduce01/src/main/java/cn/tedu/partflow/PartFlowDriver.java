package cn.tedu.partflow;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class PartFlowDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(PartFlowDriver.class);
        job.setMapperClass(PartFlowMapper.class);
        job.setReducerClass(PartFlowReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Flow.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        /**
         * 设置Partitioner
         * NumReduceTasks要大于等于 分区的数量
         */
        job.setPartitionerClass(CityPartitioner.class);
        job.setNumReduceTasks(3);

        FileInputFormat.addInputPath(job,
                new Path("hdfs://hadoop01:9000/txt/flow.txt"));
        FileOutputFormat.setOutputPath(job,
                new Path("hdfs://hadoop01:9000/result/part_flow"));
        job.waitForCompletion(true);
    }
}
