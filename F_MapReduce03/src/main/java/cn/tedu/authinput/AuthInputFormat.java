package cn.tedu.authinput;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

import java.io.IOException;
import java.net.URI;

/**
 * 泛型表示读取出来的数据类型
 */
public class AuthInputFormat extends FileInputFormat<Text,Text> {
    @Override
    public RecordReader<Text, Text> createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        return new AuthReader();
    }
}

class AuthReader extends RecordReader<Text,Text>{

    private LineReader reader;
    private Text key;
    private Text value;
    private float len;
    private float pos = 0;
    private static final byte[] blank = new Text(" ").getBytes();

    /**
     * 在初始化的时候被调用一次
     * 在这个方法需要获取一个真正的输入流 用于读取数据
     * @param inputSplit
     * @param taskAttemptContext
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        // 获取切片
        FileSplit fileSplit = (FileSplit) inputSplit;
        // 获取切片大小
        len = fileSplit.getLength();
        // 从切片中获取到文件路径
        Path path = fileSplit.getPath();
        // 连接HDFS
        FileSystem fs =
                FileSystem.get(URI.create(path.toString()), taskAttemptContext.getConfiguration());
        // 获取输入流
        FSDataInputStream in = fs.open(path);
        // in是一个字节流，而要读取的文件是一个字符文件
        // 如果直接用字节流来读，那么需要自己判断一行数据什么时候读完
        // 还需要自己拆分数据/字符
        // 考虑：将字节流转化为字符流，最好能够按行读取
        reader = new LineReader(in);
    }

    /**
     * 判断是否有下一个键值对要传递给map方法
     * 要判断是否有数据传递给map方法，试着去读取数据
     * 如果读取到了，那么表示有数据，需要返回true
     * 如果没有读到，那么表是没有数据，需要返回false
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        // 准备变量
        key = new Text();
        value = new Text();
        Text tmp = new Text();
        // 读取数据
        // 会将读取到的一行数据放到tmp中
        // 返回值表示的是读取到的一行数据的字节个数
        if (reader.readLine(tmp) <= 0) return false;
        key.set(tmp.toString());
        pos += tmp.getLength();
        // 读取第二行数据
        if (reader.readLine(tmp) <= 0) return false;
        value.set(tmp.toString());
        pos += tmp.getLength();
        // 拼接间隔符
        value.append(blank, 0, blank.length);
        // 读取第三行数据
        if (reader.readLine(tmp) <= 0) return false;
        value.append(tmp.getBytes(), 0, tmp.getLength());
        pos += tmp.getLength();
        // key = tom
        // value = math 80 english 90
        return true;
    }

    /**
     * 获取键
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public Text getCurrentKey() throws IOException, InterruptedException {
        return key;
    }

    /**
     * 获取值
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return value;
    }

    /**
     * 获取执行进度
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public float getProgress() throws IOException, InterruptedException {
        return pos / len;
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
