package cn.tedu.sink;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;

// 模拟：File Roll Sink，将数据写出到磁盘上
public class AuthSink extends AbstractSink
        implements Sink, Configurable {

    private String path;
    private PrintStream ps;

    // 获取指定属性的值
    @Override
    public void configure(Context context) {
        // 获取用户的指定路径
        String path = context.getString("path");
        // 判断用户是否指定了路径
        if (path == null) throw new IllegalArgumentException("必须指定写出路径！！！");
        this.path = path;
    }

    // 启动Sink
    @Override
    public synchronized void start() {
        // 构建输出流
        try {
            ps = new PrintStream(path + "/" + System.currentTimeMillis());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Sink启动了");
    }

    // 处理数据
    @Override
    public Status process() {
        // 获取Channel
        Channel channel = this.getChannel();
        // 获取事务
        Transaction t = channel.getTransaction();
        // 开启事务
        t.begin();
        // 从Channel中来获取数据
        Event e;
        try {
            while ((e = channel.take()) != null) {
                // 获取headers
                Map<String, String> headers = e.getHeaders();
                ps.println("headers:");
                for (Map.Entry<String, String> h : headers.entrySet()) {
                    ps.println("\t" + h.getKey() + "=" + h.getValue());
                }
                // 获取body
                byte[] body = e.getBody();
                ps.println("body:");
                ps.println("\t" + new String(body));
            }
            // 整个循环结束，都没有出现异常，那么说明写成功了
            // 提交事务
            t.commit();
            return Status.READY;
        } catch (Exception ex) {
            // 打印异常栈轨迹
            ex.printStackTrace();
            // 回滚事务
            t.rollback();
            return Status.BACKOFF;
        } finally {
            // 关闭事务
            t.close();
        }
    }

    // 关闭Sink
    @Override
    public synchronized void stop() {
        if (ps != null) ps.close();
        System.out.println("Sink关闭了！！！");
    }

}
