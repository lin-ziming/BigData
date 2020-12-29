package cn.tedu.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceDemo {

    public static void main(String[] args) {

        // 构建线程池
        // int corePoolSize - 核心线程数量
        // int maximumPoolSize - 最大线程数量 = 核心线程数量 + 临时线程数量
        // long keepAliveTime - 存活时间
        // TimeUnit unit - 时间单位
        // BlockingQueue<Runnable> workQueue - 工作队列
        // RejectedExecutionHandler handler - 拒绝执行处理器
        ExecutorService es = new ThreadPoolExecutor(
                5, // 5个核心线程
                10, // 5个临时线程
                5, TimeUnit.SECONDS, // 临时线程用完之后存活5s
                new ArrayBlockingQueue<>(10),
                // 实际开发中，如果有明确的拒绝流程，那么可以添加handler
                // 实际过程中，一般会记录日志：时间、IP、请求内容等
                // 拒绝之后，需要给客户一个提示
                (r, e) -> System.out.println("拒绝执行线程：" + r)
        );
        // new Thread(new ESThread()).start();
        // es.execute(new ESThread());
        // 5个核心，工作队列为10， 5个临时
        for (int i = 0; i < 22; i++) {
            es.submit(new ESThread());
        }
        // 关闭线程池
        es.shutdown();
    }

}

class ESThread implements Runnable {
    @Override
    public void run() {
        try {
            System.out.println("running~~~");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
