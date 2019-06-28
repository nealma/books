package charpter7;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 重入读写锁
 *
 * @author neal.ma
 * @date 2019/6/28
 * @blog nealma.com
 */
public class ReentrantReadWriteLockThread {

    public static void main(String[] args) {

        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        final Lock rLock = lock.readLock();
        final Lock wLock = lock.writeLock();


        Runnable reader = () -> {
            rLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " hold: " + lock.getReadHoldCount());
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                rLock.unlock();
            }
        };

        Runnable writer = () -> {
            wLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " hold: " + lock.getWriteHoldCount());
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                wLock.unlock();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i=0; i<10; i++){
            executorService.execute(reader);
            executorService.execute(writer);
        }

        executorService.shutdown();

        // 输出
        // pool-1-thread-1 at 1561684047226 hold: 1
        // pool-1-thread-2 at 1561684047961 hold: 1
        // pool-1-thread-1 at 1561684048452 hold: 1
        // pool-1-thread-2 at 1561684049256 hold: 1
        // pool-1-thread-1 at 1561684050030 hold: 1
        // pool-1-thread-2 at 1561684050444 hold: 1
        // pool-1-thread-1 at 1561684051120 hold: 1
        // pool-1-thread-2 at 1561684051584 hold: 1
        // pool-1-thread-1 at 1561684051676 hold: 1
        // pool-1-thread-2 at 1561684052545 hold: 1
        // pool-1-thread-1 at 1561684053545 hold: 1
        // pool-1-thread-2 at 1561684054036 hold: 1
        // pool-1-thread-1 at 1561684054309 hold: 1
        // pool-1-thread-2 at 1561684054920 hold: 1
        // pool-1-thread-1 at 1561684055627 hold: 1
        // pool-1-thread-2 at 1561684055920 hold: 1
        // pool-1-thread-1 at 1561684056228 hold: 1
        // pool-1-thread-2 at 1561684057170 hold: 1
        // pool-1-thread-1 at 1561684057395 hold: 1
        // pool-1-thread-2 at 1561684057530 hold: 1
    }
}
