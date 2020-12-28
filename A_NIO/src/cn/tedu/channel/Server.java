package cn.tedu.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {

    public static void main(String[] args) throws IOException {

        // 开启服务器端的通道
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 绑定监听的端口
        ssc.bind(new InetSocketAddress(8090));
        // 设置为非阻塞
        ssc.configureBlocking(false);
        // 接收连接
        SocketChannel sc = ssc.accept();
        // 判断是否接收到连接
        while (sc == null)
            sc = ssc.accept();
        // 准本一个缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 接收数据
        sc.read(buffer);
        // 解析数据
        System.out.println(new String(buffer.array(), 0, buffer.position()));
        // 关流
        ssc.close();

    }

}
