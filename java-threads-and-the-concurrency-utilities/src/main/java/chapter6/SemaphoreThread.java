package chapter6;

import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * 信号量
 *
 * @author neal.ma
 * @date 2019/6/26
 * @blog nealma.com
 */
public class SemaphoreThread {

    public static void main(String[] args) {

        final Semaphore semaphore = new Semaphore(10);

        Runnable r = () -> {
            try {
                semaphore.acquire();
                Thread.sleep(new Random().nextInt(1000));
                System.out.println(Thread.currentThread().getName() + " handle " + semaphore.getQueueLength());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                semaphore.release();
            }
        };

        for (int i=0; i<100; i++){
            new Thread(r).start();
        }

        // 输出
        // Thread-1 handle
        // Thread-2 handle
        // Thread-9 handle
        // Thread-11 handle
        // Thread-8 handle
        // Thread-7 handle
        // Thread-5 handle
        // Thread-12 handle
        // ......
    }
}
