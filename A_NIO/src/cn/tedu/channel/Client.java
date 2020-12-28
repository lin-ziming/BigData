package cn.tedu.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {

    public static void main(String[] args) throws IOException {

        // 开启客户端的通道
        SocketChannel sc = SocketChannel.open();
        // 设置非阻塞
        sc.configureBlocking(false);
        // 发起连接
        sc.connect(new InetSocketAddress("localhost", 8090));
        // 判断连接是否建立
        // 如果连接多次，都没有成功，那么说明这个连接无法建立
        while (!sc.isConnected()) {
            // 试图再次建立连接
            // 这个方法底层，会自动的根据当前的网络环境来计算次数
            // 只要超过这个次数没有建立连接，那么就会报错
            sc.finishConnect();
        }
        // 写出数据
        sc.write(ByteBuffer.wrap("hello server".getBytes()));
        // 关流
        sc.close();

    }

}
