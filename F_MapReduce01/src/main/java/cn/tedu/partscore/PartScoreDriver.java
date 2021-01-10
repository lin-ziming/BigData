package cn.tedu.partscore;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class PartScoreDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(PartScoreDriver.class);
        job.setMapperClass(PartScoreMapper.class);
        job.setReducerClass(PartScoreReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Score.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setPartitionerClass(MonthPartitioner.class);
        job.setNumReduceTasks(3);

        FileInputFormat.addInputPath(job,
                new Path("hdfs://hadoop01:9000/txt/score1/"));
        FileOutputFormat.setOutputPath(job,
                new Path("hdfs://hadoop01:9000/result/part_score"));
        job.waitForCompletion(true);
    }
}
