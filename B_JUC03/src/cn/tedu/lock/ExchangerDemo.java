package cn.tedu.lock;

import java.util.concurrent.Exchanger;

/*
    案例：消费
    一手交钱一手交货
 */
public class ExchangerDemo {

    public static void main(String[] args) {

        Exchanger<String> ex = new Exchanger<>();
        new Thread(new Seller(ex)).start();
        new Thread(new Consumer(ex)).start();

    }

}

// 商家
class Seller implements Runnable {

    private final Exchanger<String> ex;

    public Seller(Exchanger<String> ex) {
        this.ex = ex;
    }

    @Override
    public void run() {
        try {
            // 商家应该准备商品
            String info = "商品";
            // 商家将商品交换给买家，同时收到买家的钱
            String msg = ex.exchange(info);
            System.out.println("商家收到买家的：" + msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// 买家
class Consumer implements Runnable {

    private final Exchanger<String> ex;

    public Consumer(Exchanger<String> ex) {
        this.ex = ex;
    }

    @Override
    public void run() {
        try {
            // 买家应该准备钱
            String info = "钱";
            // 买家将钱交换给商家，同时收到商家的商品
            String msg = ex.exchange(info);
            System.out.println("买家收到商家的：" + msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
