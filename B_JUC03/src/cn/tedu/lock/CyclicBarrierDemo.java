package cn.tedu.lock;

import java.util.concurrent.CyclicBarrier;

/*
    案例：跑步比赛
    运动员先到起跑线，听到发号枪响，运动员跑出去
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) {

        CyclicBarrier cb = new CyclicBarrier(6);
        new Thread(new Runner(cb), "1号").start();
        new Thread(new Runner(cb), "2号").start();
        new Thread(new Runner(cb), "3号").start();
        new Thread(new Runner(cb), "4号").start();
        new Thread(new Runner(cb), "5号").start();
        new Thread(new Runner(cb), "6号").start();

    }

}

// 运动员
class Runner implements Runnable {

    private final CyclicBarrier cb;

    public Runner(CyclicBarrier cb) {
        this.cb = cb;
    }

    @Override
    public void run() {

        try {
            // 模拟：运动员走到起跑线的时间
            Thread.sleep((long) (Math.random() * 10000));
            String name = Thread.currentThread().getName();
            System.out.println(name + "运动员走到了起跑线~~~");
            // 先到这儿的线程需要阻塞
            // 同时需要减少计数个数
            cb.await();
            System.out.println(name + "运动员跑了出去~~~");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}