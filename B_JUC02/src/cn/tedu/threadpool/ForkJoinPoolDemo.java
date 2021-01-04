package cn.tedu.threadpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class ForkJoinPoolDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 需求：求1-100000000000L的和
        long begin = System.currentTimeMillis();

        // 在Java中，main所在的类默认是一个线程类 - 主线程
        // 当前的ForkJoinPoolDemo实际上是一个线程
        // 在这个线程中执行了一个for循环
        // 线程在执行的时候，是分配到CPU核上来执行
        // 目前为止，几乎的计算机都是多核的
        // 一个线程只能分布到一个核上执行
        // 43945  家里电脑运行时间：24018
        // long sum = 0;
        // for (long i = 1; i <= 100000000000L; i++) {
        //     sum += i;
        // }
        // System.out.println(sum);

        // 45242 -> 22s  家里电脑运行时间：7940
        // 构建分叉合并池
        ForkJoinPool pool = new ForkJoinPool();
        // 执行任务
        Future<Long> f = pool.submit(new Sum(1, 100000000000L));
        // 关闭线程池
        pool.shutdown();
        // 懒加载 - 在分叉合并池中，如果不获取结果，那么分叉合并过程不执行
        System.out.println(f.get());

        long end = System.currentTimeMillis();
        System.out.println(end - begin);

    }

}

class Sum extends RecursiveTask<Long> {

    private final long start;
    private final long end;

    public Sum(long start, long end) {
        this.start = start;
        this.end = end;
    }

    // 分叉合并的逻辑就是覆盖在这个方法中
    @Override
    protected Long compute() {
        // 确定范围，如果范围比较大，就继续分叉
        // 如果范围比较小，那么就将这个范围内的所有的数字求和
        if (end - start > 10000) {
            // 考虑到拆成2份更加容易，所以获取中间值
            long mid = (start + end) / 2;
            Sum left = new Sum(start, mid);
            Sum right = new Sum(mid + 1, end);
            // 分叉 - 将fork出去的这一部分看作是一个单独的线程来执行
            left.fork();
            right.fork();
            // 合并
            return left.join() + right.join();
        } else {
            long sum = 0;
            for (long i = start; i <= end; i++) {
                sum += i;
            }
            return sum;
        }
    }

}