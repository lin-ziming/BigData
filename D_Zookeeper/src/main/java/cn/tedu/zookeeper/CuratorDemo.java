package cn.tedu.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

// Curator是Google提供的一套Zookeeper的客户端框架
// Curator提供了对Zookeeper的基本操作和高级操作，使得代码变得更加的简便
public class CuratorDemo {

    private CuratorFramework client;

    // 开启客户端
    @Before
    public void startClient() {
        // 指定重试策略
        // int baseSleepTimeMs - 间隔时间，单位是毫秒
        // int maxRetries - 最大次数
        // 试图连接客户端，每隔5s连接一次，最多连接3次，如果3次都连接失败，则会报错
        RetryPolicy rp = new ExponentialBackoffRetry(
                5000, 3);
        // 构建客户端
        client = CuratorFrameworkFactory.newClient(
                "10.9.162.133:2181", rp);
        // 开启客户端
        client.start();
    }

    // 创建节点
    @Test
    public void createNode() throws Exception {
        // 返回值表示节点名
        String name = client.create().creatingParentsIfNeeded() // 递归创建
                .withMode(CreateMode.PERSISTENT)
                .forPath("/video/movie/strategy", "test".getBytes());
        System.out.println(name);
    }

    // 获取数据
    @Test
    public void getData() throws Exception {
        byte[] data = client.getData().forPath("/log");
        System.out.println(new String(data));
    }

    // 修改数据
    @Test
    public void setData() throws Exception {
        // 如果不指定withVersion，则默认为-1
        Stat s = client.setData().withVersion(-1).forPath("/log", "test".getBytes());
        System.out.println(s);
    }

    // 获取子节点
    @Test
    public void getChildren() throws Exception {
        List<String> cs = client.getChildren().forPath("/");
        for (String c : cs) {
            System.out.println(c);
        }
    }

    // 删除节点
    @Test
    public void deleteNode() throws Exception {
        client.delete().deletingChildrenIfNeeded() // 递归删除
                .withVersion(-1).forPath("/video");
    }

    // 监控
    @Test
    public void watch() throws Exception {
        // 获取树缓存对象
        // 这个TreeCache在运行的时候，会将/log节点以及其子节点全部加载到TreeCache中监控
        TreeCache cache = new TreeCache(client, "/log");
        // 开始监听
        cache.getListenable().addListener(
                (c, e) -> {
                    // 获取事件类型
                    TreeCacheEvent.Type t = e.getType();
                    switch (t) {
                        case NODE_ADDED:
                            System.out.println("节点被创建");
                            break;
                        case NODE_UPDATED:
                            System.out.println("节点数据被修改");
                            break;
                        case NODE_REMOVED:
                            System.out.println("节点被删除");
                            break;
                        case CONNECTION_SUSPENDED:
                            System.out.println("连接中断");
                            break;
                        case CONNECTION_RECONNECTED:
                            System.out.println("重新连接");
                            break;
                    }
                });
        // 开始监听
        cache.start();
        while (true) ;
    }

    // 关闭客户端
    @After
    public void closeClient() {
        if (client != null)
            client.close();
    }
}
