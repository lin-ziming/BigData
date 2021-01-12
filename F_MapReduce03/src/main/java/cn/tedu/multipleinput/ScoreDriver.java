package cn.tedu.multipleinput;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class ScoreDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(ScoreDriver.class);
        job.setReducerClass(ScoreReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 多源输入
        // 如果输入的多个文件之间使用的是同一个Mapper，那么这儿只需要传递三个参数即可
        // 在之前需要通过job.setMapperClass来设置
        // 如果输入的多个文件之间是公用的不是同一个Mapper
        // 在多源输入的时候，就需要为每一个文件来单独指定一个Mapper
        MultipleInputs.addInputPath(job,
                new Path("hdfs://hadoop01:9000/txt/score2/"),
                TextInputFormat.class, ScoreMapper1.class);
        MultipleInputs.addInputPath(job,
                new Path("D:/00-BigData/02-Hadoop/txt/score.txt"),
                TextInputFormat.class, ScoreMapper2.class);

        // FileOutputFormat.setCompressOutput(job, true);
        // FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);

        FileOutputFormat.setOutputPath(job,
                new Path("D:/multiple_input"));

        job.waitForCompletion(true);
    }

}
