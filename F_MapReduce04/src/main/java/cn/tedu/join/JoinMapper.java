package cn.tedu.join;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class JoinMapper extends Mapper<LongWritable, Text, Text, Order> {

    private Map<String, Order> map;

    @Override
    protected void setup(Context context) throws IOException {
        // 需要用到关联文件，那么可以将关联文件从缓存中取出来
        // 处理关联文件，从关联文件中获取指定的字段
        // 拿到的是文件地址，所以要处理这个文件，就需要将文件读取出来
        URI file = context.getCacheFiles()[0];
        // 连接HDFS
        FileSystem fs = FileSystem.get(file, context.getConfiguration());
        // 指定要读取文件，获取到输入流
        FSDataInputStream in = fs.open(new Path(file.toString()));
        // 获取到输入流之后，就可以利用这个输入流来读取数据
        // 输入流是一个字节流，但是要处理的文件是字符文件
        // 考虑将字节流转化为字符流，最好能够按行读取
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        map = new HashMap<>();
        // 读取数据
        String line;
        while ((line = reader.readLine()) != null) {
            // 3 xiaomi 2999
            // 拆分这一行数据的字段
            String[] arr = line.split(" ");
            // 封装对象
            Order o = new Order();
            o.setProductId(arr[0]);
            o.setName(arr[1]);
            o.setPrice(Double.parseDouble(arr[2]));
            map.put(o.getProductId(), o);
        }
        // 关流
        reader.close();
    }

    // 主处理文件是order.txt
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 1001 20170710 4 2
        // 拆分字段
        String[] arr = value.toString().split(" ");
        // 封装Order对象
        Order o = new Order();
        o.setOrderId(arr[0]);
        o.setOrderDate(arr[1]);
        o.setProductId(arr[2]);
        o.setNum(Integer.parseInt(arr[3]));
        o.setName(map.get(o.getProductId()).getName());
        o.setPrice(map.get(o.getProductId()).getPrice());
        context.write(new Text(o.getOrderDate()), o);
    }

}
