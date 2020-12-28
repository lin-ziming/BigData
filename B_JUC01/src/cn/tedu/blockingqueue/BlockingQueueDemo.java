package cn.tedu.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {

        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(5);

        // 添加元素
        queue.add("a");
        queue.add("b");
        queue.add("c");
        queue.add("d");
        queue.add("e");

        // 队列已满
        // 抛出异常 - IllegalStateException
        // queue.add("f");
        // 返回false
        // boolean b = queue.offer("g");
        // System.out.println(b);
        // 永久阻塞
        // queue.put("h");
        // 定时阻塞
        boolean b = queue.offer("i", 5, TimeUnit.SECONDS);
        System.out.println(b);

        System.out.println(queue);

    }

}
