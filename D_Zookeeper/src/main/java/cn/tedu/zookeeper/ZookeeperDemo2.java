package cn.tedu.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperDemo2 {

    // 在Zookeeper中，还可以利用代码来对Zookeeper实现监控效果

    private ZooKeeper zk;

    // 连接Zookeeper
    @Before
    public void connect() throws IOException, InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);
        zk = new ZooKeeper("10.9.162.133:2181", 5000,
                e -> {
                    if (e.getState() == Watcher.Event.KeeperState.SyncConnected)
                        System.out.println("connected~~~");
                    cdl.countDown();
                });
        cdl.await();
    }

    // 监控节点数据是否被修改
    @Test
    public void dataChanged() throws KeeperException, InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);
        zk.getData("/txt", e -> {
            if (e.getType() == Watcher.Event.EventType.NodeDataChanged)
                // 实际开发中，需要记录日志：时间、IP、原来的内容、新的内存等
                System.out.println("节点数据被修改");
            cdl.countDown();
        }, null);
        cdl.await();
    }

    // 监控子节点个数是否变化
    @Test
    public void childrenChanged() throws KeeperException, InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);
        zk.getChildren("/txt", e -> {
            if (e.getType() == Watcher.Event.EventType.NodeChildrenChanged)
                System.out.println("子节点个数发生变化");
            cdl.countDown();
        });
        cdl.await();
    }

    // 监控节点的创建或者删除
    @Test
    public void nodeChanged() throws KeeperException, InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);
        zk.exists("/log", e -> {
            if (e.getType() == Watcher.Event.EventType.NodeCreated)
                System.out.println("节点被创建");
            else if (e.getType() == Watcher.Event.EventType.NodeDeleted)
                System.out.println("节点被删除");
            cdl.countDown();
        });
        cdl.await();
    }

}
