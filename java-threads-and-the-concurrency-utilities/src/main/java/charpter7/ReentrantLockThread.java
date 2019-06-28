package charpter7;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 重入锁
 *
 * @author neal.ma
 * @date 2019/6/28
 * @blog nealma.com
 */
public class ReentrantLockThread {

    public static void main(String[] args) {

        final Lock lock = new ReentrantLock();

        Runnable r = () -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " hold: " + ((ReentrantLock) lock).isHeldByCurrentThread());
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i=0; i<10; i++){
            executorService.execute(r);
        }

        executorService.shutdown();

        // 输出
        // pool-1-thread-1 at 1561676957938 hold: true
        // pool-1-thread-2 at 1561676958050 hold: true
        // pool-1-thread-1 at 1561676958122 hold: true
        // pool-1-thread-2 at 1561676959057 hold: true
        // pool-1-thread-1 at 1561676959511 hold: true
        // pool-1-thread-1 at 1561676960184 hold: true
        // pool-1-thread-2 at 1561676960933 hold: true
        // pool-1-thread-1 at 1561676961804 hold: true
        // pool-1-thread-2 at 1561676962200 hold: true
        // pool-1-thread-1 at 1561676962714 hold: true
    }
}
