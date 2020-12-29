package cn.tedu.threadpool;

import java.util.concurrent.*;

public class CallableDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 方式一：通过Thread来启动
        // Callable -> FutureTask -> RunnableFuture -> Runnable
        // FutureTask<String> f = new FutureTask<>(new CThread());
        // new Thread(f).start();
        // System.out.println(f.get());

        // 方式二：通过线程池来启动
        ExecutorService es = new ThreadPoolExecutor(
                5, 10, 5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10));
        Future<String> f = es.submit(new CThread());
        System.out.println(f.get());
        es.shutdown();

    }

}

// 泛型用于定义返回值类型
class CThread implements Callable<String> {
    @Override
    public String call() {
        return "SUCCESS";
    }
}