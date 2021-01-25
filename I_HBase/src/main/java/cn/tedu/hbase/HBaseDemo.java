package cn.tedu.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

public class HBaseDemo {

    private Configuration conf;

    @Before
    public void conf(){
        // 获取配置
        conf = HBaseConfiguration.create();
        // 连接Zookeeper
        conf.set("hbase.zookeeper.quorum","hadoop01:2181,hadoop02:2181,hadoop03:2181");
    }

    /**
     * 建表
     */
    @Test
    public void createTable() throws IOException {
        // 获取HBase的管理权
        HBaseAdmin admin = new HBaseAdmin(conf);
        // 构建表描述器
        HTableDescriptor table = new HTableDescriptor(TableName.valueOf("users"));
        // 构建列描述器
        HColumnDescriptor cf1 = new HColumnDescriptor("basic");
        HColumnDescriptor cf2 = new HColumnDescriptor("info");
        // 添加列族
        table.addFamily(cf1);
        table.addFamily(cf2);
        // 建表
        admin.createTable(table);
        // 关闭管理权
        admin.close();
    }

    /**
     *  添加/修改数据
     * @throws IOException
     */
    @Test
    public void putData() throws IOException {
        // 指定表
        HTable table = new HTable(conf, "users");
        // 封装Put对象
        Put put = new Put("u1".getBytes());
        // byte [] family, byte [] qualifier, byte [] value
        put.addColumn("basic".getBytes(),"name".getBytes(),"Rose".getBytes());
        // 添加数据
        table.put(put);
        // 关流
        table.close();
    }
    /**
     * 测试：向HBase中添加100W条数据
     * 在家测试的时间：106645
     * 在HBase客户端打印出来的时间：1000000 row(s) in 109.1150 seconds
     */
    @Test
    public void putMillion() throws IOException {
        long begin = System.currentTimeMillis();
        HTable table = new HTable(conf, "users");
        byte[] f = "basic".getBytes();
        byte[] c = "password".getBytes();
        List<Put> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            Put put = new Put(("u" + i).getBytes());
            put.addColumn(f,c,getPassword());
            list.add(put);
            // 每1000条数据向HBase中添加一次
            if (list.size() >= 1000){
                table.put(list);
                list.clear();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }

    /**
     * 产生由6位大写字母构成的初始密码
     */
    private byte[] getPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            char c = (char) (Math.random() * 26 + 65);
            sb.append(c);
        }
        return sb.toString().getBytes();
    }
    /**
     * 通过行键获取所有列
     * get 'users', 'u1'
     */
    @Test
    public void getRow() throws IOException {
        HTable table = new HTable(conf, "users");
        // 封装Get对象
        Get get = new Get("u1".getBytes());
        // 获取数据
        Result r = table.get(get);
        // 获取数据
        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = r.getMap();
        // 遍历Map
        for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> e : map.entrySet()) {
            // 键表示列族名
            System.err.println("Column Family:" + new String(e.getKey()));
            // 值表示这个列族中所包含的列
            NavigableMap<byte[], NavigableMap<Long, byte[]>> cs = e.getValue();
            for (Map.Entry<byte[], NavigableMap<Long, byte[]>> c : cs.entrySet()) {
                // 键表示列名
                System.err.println("\t Column:" + new String(c.getKey()));
                // 值表示列的值
                NavigableMap<Long, byte[]> vs = c.getValue();
                for (Map.Entry<Long, byte[]> v : vs.entrySet()) {
                    // 键表示时间戳
                    System.err.println("\t\tTimeStamp:" + v.getKey());
                    // 值表示实际的值
                    System.err.println("\t\tValue:" + new String(v.getValue()));
                }
            }
        }
        table.close();
    }
    /**
     * 获取列族
     * get 'users', 'u1', 'basic'
     */
    @Test
    public void getFamily() throws IOException {
        HTable table = new HTable(conf,"users");
        // 封装Get
        Get get = new Get("u1".getBytes());
        // 指定列族
        get.addFamily("basic".getBytes());
        // 查询数据
        Result r = table.get(get);
        // 解析结果
        NavigableMap<byte[], byte[]> map = r.getFamilyMap("basic".getBytes());
        for (Map.Entry<byte[], byte[]> en : map.entrySet()) {
            // 键表示列名
            System.err.println("Column:" + new String(en.getKey()));
            // 值表示实际的值
            System.err.println("Value:" + new String(en.getValue()));
        }
        table.close();
    }
    /**
     * 获取具体列
     * get 'users', 'u1', 'basic:name'
     */
    @Test
    public void getValue() throws IOException {
        HTable table = new HTable(conf,"users");
        // 封装Get
        Get get = new Get("u1".getBytes());
        // 指定列
        get.addColumn("basic".getBytes(),"name".getBytes());
        // 查询数据
        Result r = table.get(get);
        // 解析数据
        System.err.println(new String(r.getValue("basic".getBytes(),"name".getBytes())));
        // 关流
        table.close();
    }

}
