package cn.tedu.atomic;

public class VolatileDemo {

    public static void main(String[] args) throws InterruptedException {

        Data d = new Data();
        d.i = 10;

        // 线程A
        new Thread(() -> {
            System.out.println("A线程启动~~~");
            while (d.i == 10) ;
            System.out.println("A线程结束~~~");
        }).start();

        // 延迟B的启动，给A线程启动和执行留下充足的时间
        Thread.sleep(3000);

        // 线程B
        new Thread(() -> {
            System.out.println("B线程启动~~~");
            d.i = 12;
            System.out.println("B线程结束~~~");
        }).start();

    }

}

class Data {
    volatile int i;
}