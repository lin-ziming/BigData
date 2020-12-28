package cn.tedu.buffer;

import java.nio.ByteBuffer;

public class BufferDemo {

    public static void main(String[] args) {

        // 构建ByteBuffer对象
        // 这个ByteBuffer构建好之后，它的容量不能改变
        // 这种缓冲区的定义方式，适合于数据未知的场景
        ByteBuffer buffer = ByteBuffer.allocate(10);
        // 获取容量位
        System.out.println(buffer.capacity());
        // 获取操作位
        System.out.println(buffer.position());
        // 获取限制位
        System.out.println(buffer.limit());

        // 添加数据
        buffer.put("abc".getBytes());
        buffer.put("def".getBytes());

        // 修改position的位置
        // buffer.position(0);
        // 获取数据
        // byte b = buffer.get();
        // System.out.println(b);

        // 记录position的当前位置
        // int pos = buffer.position();
        // 将position归零
        // buffer.position(0);
        // 遍历
        // while (buffer.position() < pos) {
        //     byte b = buffer.get();
        //     System.out.println((char) b);
        // }

        // 先将limit挪到当前的position的位置上
        // buffer.limit(buffer.position());
        // 将position归零
        // buffer.position(0);
        // 上述两步操作，合称为翻转缓冲区，等价于
        buffer.flip();
        // 遍历
        // while (buffer.position() < buffer.limit()) {
        // 等价于
        while (buffer.hasRemaining()) {
            System.out.println((char) buffer.get());
        }

    }

}
