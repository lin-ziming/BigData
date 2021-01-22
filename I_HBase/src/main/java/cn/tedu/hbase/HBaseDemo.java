package cn.tedu.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class HBaseDemo {

    private Configuration conf;

    @Before
    public void conf(){
        // 获取配置
        conf = HBaseConfiguration.create();
        // 连接Zookeeper
        conf.set("hbase.zookeeper.quorum","hadoop01:2181,hadoop:2181,hadoop03:2181");
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

}
