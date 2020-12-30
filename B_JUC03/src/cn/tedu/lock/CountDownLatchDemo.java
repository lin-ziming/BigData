package cn.tedu.lock;

import java.util.concurrent.CountDownLatch;

/*
    案例：考试
    考官和考生到达考场之后，开始考试
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch cdl = new CountDownLatch(7);
        new Thread(new Teacher(cdl)).start();
        new Thread(new Teacher(cdl)).start();
        new Thread(new Student(cdl)).start();
        new Thread(new Student(cdl)).start();
        new Thread(new Student(cdl)).start();
        new Thread(new Student(cdl)).start();
        new Thread(new Student(cdl)).start();

        // 需要等上面的7个线程先执行完，再执行主线程
        // 在计数归零之前，主线程即使抢到资源也需要阻塞
        cdl.await();
        System.out.println("开始考试！！！");

    }

}

// 考官
class Teacher implements Runnable {

    private final CountDownLatch cdl;

    public Teacher(CountDownLatch cdl) {
        this.cdl = cdl;
    }

    @Override
    public void run() {
        try {
            // 模拟：考官从办公室走到考场的时间
            Thread.sleep((long) (Math.random() * 10000));
            System.out.println("考官到达考场~~~");
            // 减少一个计数
            cdl.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// 考生
class Student implements Runnable {

    private final CountDownLatch cdl;

    public Student(CountDownLatch cdl) {
        this.cdl = cdl;
    }

    @Override
    public void run() {
        try {
            // 模拟：考生走到考场的时间
            Thread.sleep((long) (Math.random() * 10000));
            System.out.println("考生到达考场~~~");
            cdl.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}