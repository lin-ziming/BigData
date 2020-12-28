package cn.tedu.buffer;

import java.nio.ByteBuffer;

public class ByteBufferDemo2 {

    public static void main(String[] args) {

        // 数据已知
        // ByteBuffer buffer = ByteBuffer.wrap("hello big2008".getBytes());
        // 数据未知
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("hello big2008".getBytes());
        // position
        // System.out.println(buffer.position());
        // 获取缓冲区底层的字节数组
        // 修改这个字节数组中的值，会改变缓冲区的数据
        byte[] data = buffer.array();
        // data[0] = 97;
        // System.out.println(buffer.get(0));
        System.out.println(new String(data, 0, buffer.position()));
        buffer.flip();
        System.out.println(new String(data, 0, buffer.limit()));

    }

}
