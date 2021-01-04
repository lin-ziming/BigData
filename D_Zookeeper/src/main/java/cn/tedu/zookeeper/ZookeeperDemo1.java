package cn.tedu.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperDemo1 {

    private ZooKeeper zk;

    // 连接Zookeeper
    @Before
    public void connect() throws IOException, InterruptedException {
        // 构建Zookeeper对象
        // String connectString - 连接IP+端口号
        // int sessionTimeout - 会话超时时间，单位默认为是毫秒
        // Watcher watcher - 监控者，可以监控连接是否建立
        // Zookeeper底层是依靠Netty来完成连接
        // Netty是基于NIO来实现的异步非阻塞的框架
        // 存在两个线程：Zookeeper的连接线程和Test的测试线程
        // 其中Zookeeper的连接线程是异步非阻塞的
        // 非阻塞：无论Zookeeper连接成功与否，都会继续往下执行
        // 也就意味着，Zookeeper可能还在连接过程中，Test测试线程就可以抢占资源执行
        CountDownLatch cdl = new CountDownLatch(1);
        zk = new ZooKeeper("10.9.162.133:2181",
                5000, // 现阶段，要求这个值必须在4000~40000之间
                e -> {
                    if (e.getState() == Watcher.Event.KeeperState.SyncConnected)
                        System.out.println("连接成功~~~");
                    cdl.countDown();
                });
        // 在上述的连接线程结束之前，测试线程即使抢到资源也需要阻塞
        cdl.await();
        System.out.println("finish");
    }

    // 创建节点
    @Test
    public void createNode() throws KeeperException, InterruptedException {
        // String path - 节点路径
        // byte data[] - 节点数据
        // List<ACL> acl - 节点权限
        // CreateMode createMode - 创建模式 - 节点类型
        // 返回值表示节点名
        String name = zk.create("/txt", "文本管理".getBytes(StandardCharsets.UTF_8),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(name);
    }

    // 获取节点数据
    @Test
    public void getData() throws KeeperException, InterruptedException {
        // 需要节点信息
        // Stat s = new Stat();
        // byte[] data = zk.getData("/txt", null, s);
        // 不需要节点信息
        byte[] data = zk.getData("/txt", null, null);
        System.out.println(new String(data));
    }

    // 修改节点数据
    @Test
    public void setData() throws KeeperException, InterruptedException {
        // String path - 节点路径
        // byte data[] - 节点数据
        // int version - 数据本版，对应了dataVersion
        // 在执行的时候，会先校验指定的version和节点的dataVersion是否一致
        // 如果一致则修改数据；如果不一致，则报错
        // 版本校验主要是为了防止ABA问题
        // 如果version的值为-1，则会忽略版本校验
        Stat s = zk.setData("/txt", "txt servers".getBytes(), -1);
        System.out.println(s);
    }

    // 获取子节点
    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        // 返回List，将子节点的名放到这个List中
        List<String> list = zk.getChildren("/", null);
        for (String c : list) {
            System.out.println(c);
        }
    }

    // 删除节点
    @Test
    public void deleteNode() throws KeeperException, InterruptedException {
        zk.delete("/txt", -1);
    }

    // 判断节点是否存在
    @Test
    public void exist() throws KeeperException, InterruptedException {
        // 如果节点存在，则返回节点信息
        // 如果节点不存在，则返回null
        Stat s = zk.exists("/txt", null);
        System.out.println(s != null);
    }
}
