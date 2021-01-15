package cn.tedu.source;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 模拟：Sequence Generator Source
 */
public class AuthSource extends AbstractSource
        implements EventDrivenSource, Configurable {

    private int step;
    private long end;
    private ExecutorService es;

    // 获取指定属性值
    @Override
    public void configure(Context context) {
        // 如果用户指定了步长，就按照用户指定的步长递增
        // 但是如果用户没有指定步长，那么默认步长为1
        step = context.getInteger("step", 1);
        // 允许用户指定截至位置
        end = context.getLong("end", Long.MAX_VALUE);
    }

    // 启动Source
    @Override
    public synchronized void start() {
        // 开启线程池 - 线程池中线程的数量需要按照实际情况给定
        es = Executors.newFixedThreadPool(5);
        // 获取ChannelProcessor
        ChannelProcessor cp = this.getChannelProcessor();
        // 提交任务
        es.submit(new Add(step, end, cp));
    }

    // 关闭Source
    @Override
    public synchronized void stop() {
        if (es != null) es.shutdown();
    }
}

class Add implements Runnable {

    private final int step;
    private final long end;
    private final ChannelProcessor cp;

    public Add(int step, long end, ChannelProcessor cp) {
        this.step = step;
        this.end = end;
        this.cp = cp;
    }

    @Override
    public void run() {
        for (long i = 0; i < end; i += step) {
            // 如果需要headers，那么就封装headers
            Map<String, String> headers = new HashMap<>();
            headers.put("date", new Date().toString());
            // 封装body
            byte[] body = (i + "").getBytes();
            // 在Flume中，会将收集到的数据封装成一个Event
            Event e = EventBuilder.withBody(body, headers);
            // 需要将递增的数据传递给Channel
            cp.processEvent(e);
        }
    }
}
