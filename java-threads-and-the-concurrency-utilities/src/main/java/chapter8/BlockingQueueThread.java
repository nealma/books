package chapter8;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 阻塞队列
 *
 * @author neal.ma
 * @date 2019/6/29
 * @blog nealma.com
 */
public class BlockingQueueThread {
    private volatile static boolean shared = false;
    public static void main(String[] args) {


        final BlockingQueue<Character> characters = new ArrayBlockingQueue(26);
        Runnable producer = () -> {
            for (char c='a'; c < 'z'; c++){
                try {
                    // 如果队列满了，put() 方被阻塞
                    characters.put(c);
                    System.out.println(Thread.currentThread().getName() + " [producer] at " + System.currentTimeMillis() + " create : " + c);
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(producer);

        Runnable consumer = () -> {
            try {
                char c;
                do {
                    // 如果队列空了，take() 方被阻塞
                    c = characters.take();
                    System.out.println(Thread.currentThread().getName() + " [consumer] at " + System.currentTimeMillis() + " create: " + c);
                    Thread.sleep(new Random().nextInt(1000));
                } while (c != 'z');
                executorService.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        executorService.execute(consumer);

        // 输出
        // pool-1-thread-1 [producer] at 1561775500363 create : a
        // pool-1-thread-2 [consumer] at 1561775500365 create: a
        // pool-1-thread-1 [producer] at 1561775501067 create : b
        // ......
        // pool-1-thread-2 [consumer] at 1561775513113 create: v
        // pool-1-thread-1 [producer] at 1561775513260 create : w
        // pool-1-thread-1 [producer] at 1561775513435 create : x
        // pool-1-thread-2 [consumer] at 1561775513857 create: w
        // pool-1-thread-1 [producer] at 1561775513969 create : y
        // pool-1-thread-2 [consumer] at 1561775514193 create: x
        // pool-1-thread-2 [consumer] at 1561775515071 create: y
    }
}
