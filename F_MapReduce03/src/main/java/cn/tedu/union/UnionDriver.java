package cn.tedu.union;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;

public class UnionDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(UnionDriver.class);
        job.setMapperClass(UnionMapper.class);

        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        recursive(new Path("hdfs://hadoop01:9000/txt/"), conf, job);

        FileOutputFormat.setOutputPath(job,
                new Path("hdfs://hadoop01:9000/result/union"));

        job.waitForCompletion(true);

    }

    public static void recursive(Path path, Configuration conf, Job job) throws IOException {
        // 连接HDFS
        FileSystem fs = FileSystem.get(URI.create(path.toString()), conf);
        // 判断是否是一个目录
        if (fs.isDirectory(path)) {
            // 获取指定路径下所有的子文件和子目录
            FileStatus[] files = fs.listStatus(path);
            for (FileStatus file : files) {
                recursive(file.getPath(), conf, job);
            }
        } else {
            FileInputFormat.addInputPath(job, path);
        }
    }

}
