package cn.tedu.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicDemo {

    // static int i = 0;
    static AtomicInteger ai = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch cdl = new CountDownLatch(2);
        new Thread(new Add(cdl)).start();
        new Thread(new Add(cdl)).start();
        cdl.await();
        System.out.println(ai);

    }

}

class Add implements Runnable {

    private final CountDownLatch cdl;

    public Add(CountDownLatch cdl) {
        this.cdl = cdl;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100000; i++) {
            // AtomicDemo.i++;
            AtomicDemo.ai.incrementAndGet(); // ++i
            // AtomicDemo.ai.getAndIncrement(); // i++
        }
        cdl.countDown();
    }
}
