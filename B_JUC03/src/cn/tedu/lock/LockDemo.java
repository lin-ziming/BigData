package cn.tedu.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
// import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {

    static int i = 0;

    public static void main(String[] args) throws InterruptedException {

        // Lock lock = new ReentrantLock();
        // 获取写锁
        ReadWriteLock rwl = new ReentrantReadWriteLock();
        Lock lock = rwl.writeLock();
        new Thread(new Add(lock)).start();
        new Thread(new Add(lock)).start();

        // main所在的类默认是是一个线程类 - 主线程
        // 主线程在执行过程中需要启动Add线程
        // 线程启动需要花费时间
        // 主线程会在Add线程启动期间先抢占执行权
        // 需要的结果：等Add线程执行完，主线程再打印
        // 也就意味着主线程即使抢到执行权，也需要阻塞
        Thread.sleep(3000);
        System.out.println(i);

    }

}

class Add implements Runnable {

    private final Lock lock;

    public Add(Lock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        // 加锁
        lock.lock();
        for (int i = 0; i < 100000; i++) {
            LockDemo.i++;
        }
        // 解锁
        lock.unlock();
    }
}
