package cn.tedu.channel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelDemo {

    public static void main(String[] args) throws Exception {
        read();
        write();
        randomAccess();
    }

    public static void read() throws IOException {
        // 获取FileChannel对象
        // 因为这个FileChannel是通过FileInputStream获取的，所以这个Channel只能读取数据
        FileChannel fc = new FileInputStream("D:\\a.txt").getChannel();
        // 准备缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 读取数据
        fc.read(buffer);
        // 解析数据
        System.out.println(new String(buffer.array(), 0, buffer.position()));
        // 关流
        fc.close();
    }

    public static void write() throws IOException {
        // 获取FileChannel对象
        FileChannel fc = new FileOutputStream("D:\\b.txt").getChannel();
        // 写出数据
        fc.write(ByteBuffer.wrap("testing~~~".getBytes()));
        // 关流
        fc.close();
    }

    public static void randomAccess() throws IOException {
        // "r" - 只读方式
        // "rw" - 读写模式。如果文件不存在，则会试图创建文件
        // "rwd" - 读写模式。允许以读写的形式将数据写出
        // 并且要求每一次的写出内容必须同步更新到底层的存储设备上
        // 在操作文件的时候，会将文件看作是一个大型的字节数组，因此可以通过下标来操作文件的任意位置
        RandomAccessFile raf = new RandomAccessFile("D:\\c.txt", "rw");
        // 获取FileChannel
        FileChannel fc = raf.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(3);
        fc.read(buffer);
        // 设置文件的下标
        raf.seek(15);
        fc.write(ByteBuffer.wrap("hi".getBytes()));
        System.out.println(new String(buffer.array()));
    }

}
