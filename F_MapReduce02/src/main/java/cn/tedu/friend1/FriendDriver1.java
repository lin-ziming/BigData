package cn.tedu.friend1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FriendDriver1 {

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(FriendDriver1.class);
        job.setMapperClass(FriendMapper1.class);
        job.setReducerClass(FriendReducer1.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job,
                new Path("hdfs://hadoop01:9000/result/relation"));
        FileOutputFormat.setOutputPath(job,
                new Path("hdfs://hadoop01:9000/result/friend"));

        job.waitForCompletion(true);

    }


}
