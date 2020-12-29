package cn.tedu.threadpool;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorServiceDemo {

    public static void main(String[] args) {

        ScheduledExecutorService ses =
                new ScheduledThreadPoolExecutor(5);
        // 推迟执行线程
        // ses.schedule(new ScheduleThread(), 5, TimeUnit.SECONDS);

        // 每隔5s执行一次
        // 从上一次执行开始来计算下一次的启动时间
        // 实际执行时间 = max(线程执行时间, 间隔时间)
        ses.scheduleAtFixedRate(new ScheduleThread(), 0,
                5, TimeUnit.SECONDS);
        // 从上一次执行结束来计算下一次的启动时间
        // 实际执行时间 = 线程执行时间 + 间隔时间
        // ses.scheduleWithFixedDelay(new ScheduleThread(), 0,
        //         5, TimeUnit.SECONDS);


    }

}

class ScheduleThread implements Runnable {
    @Override
    public void run() {
        try {
            System.out.println("hello");
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
