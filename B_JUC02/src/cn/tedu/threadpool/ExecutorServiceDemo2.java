package cn.tedu.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceDemo2 {

    public static void main(String[] args) {

        // 预定义线程池

        // 特点：
        // 1. 没有核心线程全部都是临时线程
        // 2. 临时线程的数量为Integer.MAX_VALUE
        //    在实际生产过程中，一台服务器的线程承载量远远小于这个值
        //    所以一般认为这个线程池能够处理无限多的请求
        // 3. 临时线程用完之后能够存活1min
        // 4. 工作队列是一个同步队列
        // 大池子小队列
        // 适合于高并发的短任务场景，例如即时通讯
        // ExecutorService es = Executors.newCachedThreadPool();

        // 特点：
        // 1. 没有临时线程全部都是核心线程
        // 2. 工作队列是一个阻塞式链式队列
        //    在没有指定容量的前提下，容量为Integer.MAX_VALUE
        //    此时认为这个队列能够存储无限多的请求
        // 小池子大队列
        // 适合于并发低的长任务场景，例如云盘下载
        ExecutorService es = Executors.newFixedThreadPool(5);

        es.submit(new ESThread());
        es.shutdown();

    }

}
