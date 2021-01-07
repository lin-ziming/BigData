package cn.tedu.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

public class HDFSDemo {

    // 下载
    @Test
    public void get() throws IOException {
        // 准备配置对象
        Configuration conf = new Configuration();
        // 连接HDFS
        FileSystem fs = FileSystem.get(URI.create("hdfs://10.9.162.133:9000"), conf);
        // 指定要下载的文件
        // 返回值是一个输入流
        FSDataInputStream in = fs.open(new Path("/a.txt"));
        // 准备输出流
        FileOutputStream out = new FileOutputStream("D:/a.txt");
        // 读取数据，将读取到的数据写出
        IOUtils.copyBytes(in, out, conf);
        // 关流
        in.close();
        out.close();
    }

    // 上传
    @Test
    public void put() throws IOException, InterruptedException {
        // 准备配置对象
        Configuration conf = new Configuration();
        // 可以在conf对象中来指定参数
        conf.set("dfs.replication", "4");
        conf.set("dfs.blocksize", "67108864");
        // 连接HDFS
        FileSystem fs = FileSystem.get(URI.create("hdfs://10.9.162.133:9000"),
                conf, "root");
        // 指定存储位置
        // 获取到一个输出流
        FSDataOutputStream out = fs.create(new Path("/a.xml"));
        // 准备输入流
        FileInputStream in = new FileInputStream("D:/web.xml");
        // 上传文件
        IOUtils.copyBytes(in, out, conf);
        // 关流
        in.close();
        out.close();
    }

    // 删除
    @Test
    public void delete() throws IOException, InterruptedException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create("hdfs://10.9.162.133:9000"),
                conf, "root");
        // 删除文件
        // 第二个参数表示是否递归
        fs.delete(new Path("/a.xml"), true);
    }

}
