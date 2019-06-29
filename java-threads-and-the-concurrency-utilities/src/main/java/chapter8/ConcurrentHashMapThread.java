package chapter8;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ConcurrentHashMap
 *
 * @author neal.ma
 * @date 2019/6/29
 * @blog nealma.com
 */
public class ConcurrentHashMapThread {
    private volatile static boolean shared = false;
    public static void main(String[] args) {


        final ConcurrentHashMap<String, String> map = new ConcurrentHashMap(1);
        Runnable producer = () -> {
            try {
                String value = map.putIfAbsent("key", "value");
                Thread.sleep(new Random().nextInt(1000));
                System.out.println(Thread.currentThread().getName() + " [producer] " + value );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(producer);

        Runnable consumer = () -> {
            String value = map.get("key");
            System.out.println(Thread.currentThread().getName() + " [consumer] " + value );
        };
        int i = 0;
        do {
            executorService.execute(consumer);
            i++;
        } while (i < 10);

        executorService.shutdown();

        // 输出
        // pool-1-thread-1 [producer] null
        // pool-1-thread-2 [consumer] value
    }
}
