package cn.tedu.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static void main(String[] args) throws IOException {

        // 开启服务器端通道
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 绑定监听的端口
        ssc.bind(new InetSocketAddress(8070));
        // 设置为非阻塞
        ssc.configureBlocking(false);
        // 开启选择器 --- 实际过程中，应该是多个服务器共用一个选择器
        // 理论上而言，选择器最好是单例的
        Selector selector = SingleSelector.get();
        // 需要将服务器注册到选择器上
        // 第二个参数表示要注册的事件，即需要选择器进行选择的事件
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        // 实际开发中，服务器一旦开启就不会关闭
        while (true) {
            // 服务器在一直开着的情况下，服务器接收到的请求就会越来越多
            // 并不意味着服务器接收到的所有的请求都是有用请求
            // 这一步操作就会筛选掉无用请求，留下有用的请求
            selector.select();
            // 剩下的请求都是有用的请求，这些请求无非能够触发服务器的accept/read/write操作
            // 但是不意味着能够触发服务器的所有的三类操作
            // 需要确定到底能够触发服务器的哪些操作
            Set<SelectionKey> set = selector.selectedKeys();
            // 遍历集合，确定请求类型分别处理
            Iterator<SelectionKey> it = set.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();

                if (key.isAcceptable()) {
                    // 确定是哪一个Channel触发了服务器的accept操作
                    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                    // 接收连接
                    SocketChannel sc = serverChannel.accept();
                    sc.configureBlocking(false);
                    // 如果这个连接需要读取数据，那么注册一个可读事件
                    // sc.register(selector, SelectionKey.OP_READ);
                    // 如果这个连接需要写出数据，那么注册一个可写事件
                    // sc.register(selector, SelectionKey.OP_WRITE);
                    // 如果这个连接既需要读有需要写，那么需要同时注册读写事件
                    // 后一次注册的事件会覆盖前一次的注册
                    // sc.register(selector, SelectionKey.OP_READ + SelectionKey.OP_WRITE);
                    // sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    sc.register(selector, SelectionKey.OP_READ ^ SelectionKey.OP_WRITE);
                }

                if (key.isReadable()) {
                    // 先确定是哪一个Channel触发了服务器的读操作
                    SocketChannel sc = (SocketChannel) key.channel();
                    // 准备ByteBuffer
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    // 接收数据
                    sc.read(buffer);
                    // 解析数据
                    System.out.println(new String(buffer.array(), 0, buffer.position()));
                    // 将这个Channel上的READ需要移除掉，防止数据被二次读取
                    // sc.register(selector, key.interestOps() - SelectionKey.OP_READ);
                    sc.register(selector, key.interestOps() ^ SelectionKey.OP_READ);
                }

                if (key.isWritable()) {
                    // 先确定是哪一个Channel出发了服务器的写操作
                    SocketChannel sc = (SocketChannel) key.channel();
                    // 写出数据
                    sc.write(ByteBuffer.wrap("hello client".getBytes()));
                    // 将这个Channel上的WRITE需要移除掉，防止数据被二次写出
                    sc.register(selector, key.interestOps() ^ SelectionKey.OP_WRITE);
                }
                // 需要将这次的请求移除掉
                it.remove();
            }
        }
    }

}

class SingleSelector {

    private static Selector selector;

    private SingleSelector() {
    }

    public static Selector get() throws IOException {
        if (selector == null)
            selector = Selector.open();
        return selector;
    }

}
