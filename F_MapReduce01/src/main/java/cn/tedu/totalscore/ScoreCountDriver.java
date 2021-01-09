package cn.tedu.totalscore;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

// 驱动类/入口类
public class ScoreCountDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        // 准备配置对象
        Configuration conf = new Configuration();
        // 申请任务
        Job job = Job.getInstance(conf);

        // 设置入口类
        job.setJarByClass(ScoreCountDriver.class);
        // 设置Mapper类
        job.setMapperClass(ScoreCountMapper.class);
        // 设置Reducer类
        job.setReducerClass(ScoreCountReducer.class);

        // 设置Mapper的输出类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);


        // 设置输入路径
        FileInputFormat.addInputPath(job,
                new Path("hdfs://hadoop01:9000/txt/score2/"));
        // 设置输出路径
        // 要求输出路径必须不存在
        FileOutputFormat.setOutputPath(job,
                new Path("hdfs://hadoop01:9000/result/total_score"));

        // 提交任务
        job.waitForCompletion(true);

    }

}