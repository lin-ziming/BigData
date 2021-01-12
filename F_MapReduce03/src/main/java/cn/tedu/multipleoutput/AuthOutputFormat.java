package cn.tedu.multipleoutput;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;

public class AuthOutputFormat extends FileOutputFormat<Text, IntWritable> {
    @Override
    public RecordWriter<Text, IntWritable> getRecordWriter(TaskAttemptContext job) throws IOException {
        // 获取输出路径
        Path path = getDefaultWorkFile(job, "");
        return new AuthWriter(job, path);
    }
}

class AuthWriter extends RecordWriter<Text, IntWritable> {

    private final FSDataOutputStream out;

    public AuthWriter(TaskAttemptContext job, Path path) throws IOException {
        Configuration conf = job.getConfiguration();
        System.err.println(path);
        // 连接HDFS
        FileSystem fs = FileSystem.get(URI.create(path.toString()), conf);
        // 获取输出流
        out = fs.create(path);
    }

    @Override
    public void write(Text key, IntWritable value) throws IOException {
        out.write(key.getBytes());
        out.write(",".getBytes());
        out.write(value.toString().getBytes());
        out.write("\n".getBytes());
    }

    @Override
    public void close(TaskAttemptContext context) throws IOException {
        if (out != null) {
            out.close();
        }
    }
}
