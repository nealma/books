package chapter6;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 交换器
 *
 * @author neal.ma
 * @date 2019/6/26
 * @blog nealma.com
 */
public class ExchangerThread {

    public static void main(String[] args) {

        final Exchanger<String> exchanger = new Exchanger<>();
        final List<String> shared = new ArrayList<>();

        Runnable r = () -> {
            try {
                while (true){
                    String name = Thread.currentThread().getName();
//                    String exchangeData = exchanger.exchange(name);
                    String exchangeData = exchanger.exchange(name, 1, TimeUnit.SECONDS);
                    System.out.println(name + " " + exchangeData);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        };

        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();

        // 输出
        // Thread-1 Thread-0
        // Thread-1 Thread-2
        // Thread-0 Thread-2
        // Thread-0 Thread-1
        // Thread-2 Thread-1
        // Thread-2 Thread-0
        // Thread-1 Thread-0
        // Thread-1 Thread-2
    }
}
