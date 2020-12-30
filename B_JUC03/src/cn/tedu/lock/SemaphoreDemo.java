package cn.tedu.lock;

import java.util.concurrent.Semaphore;

/*
    案例：去餐馆吃饭
    餐馆中的桌子的数量是固定的
    如果桌子被全部占用，后来的人就需要等待
 */
public class SemaphoreDemo {

    public static void main(String[] args) {

        // 相当于有6张桌子
        Semaphore s = new Semaphore(6);
        for (int i = 0; i < 10; i++) {
            new Thread(new Eater(s)).start();
        }

    }

}

class Eater implements Runnable {

    private final Semaphore s;

    public Eater(Semaphore s) {
        this.s = s;
    }

    @Override
    public void run() {
        try {
            // 占了一张桌子 -> 使用了一个信号
            // 当信号归零的时候，后来的线程就会被阻塞
            s.acquire();
            System.out.println("来了一波客人，占用了一张桌子~~~");
            // 模拟：客人用餐的时间
            Thread.sleep((long) (Math.random() * 10000));
            System.out.println("客人买单离开，空出来一张桌子~~~");
            // 空出来一张桌子 -> 释放了一个信号
            // 有被释放的信号，被阻塞的线程就可以获取信号来执行
            s.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
