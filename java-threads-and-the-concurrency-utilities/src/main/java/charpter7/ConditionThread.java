package charpter7;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 条件
 *
 * @author neal.ma
 * @date 2019/6/28
 * @blog nealma.com
 */
public class ConditionThread {
    private volatile static boolean shared = false;
    public static void main(String[] args) {

        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        Runnable producer = () -> {
            lock.lock();
            try {
                while (!shared){
                    condition.await();
                }
                System.out.println(Thread.currentThread().getName() + " [producer] at " + System.currentTimeMillis() + " hold: " + ((ReentrantLock) lock).isHeldByCurrentThread());
                Thread.sleep(new Random().nextInt(1000));
                shared = false;
                condition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        };
        Runnable consumer = () -> {
            lock.lock();
            try {
                while (shared){
                    condition.await();
                }
                System.out.println(Thread.currentThread().getName() + " [consumer] at " + System.currentTimeMillis() + " hold: " + ((ReentrantLock) lock).isHeldByCurrentThread());
                Thread.sleep(new Random().nextInt(1000));
                shared = true;
                condition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i=0; i<10; i++){
            executorService.execute(producer);
            executorService.execute(consumer);
        }

        executorService.shutdown();

        // 输出
        // pool-1-thread-2 [consumer] at 1561679314558 hold: true
        // pool-1-thread-3 [producer] at 1561679314697 hold: true
        // pool-1-thread-4 [consumer] at 1561679315204 hold: true
        // pool-1-thread-4 [producer] at 1561679315342 hold: true
        // pool-1-thread-4 [consumer] at 1561679315724 hold: true
        // pool-1-thread-2 [producer] at 1561679315910 hold: true
        // pool-1-thread-3 [consumer] at 1561679316567 hold: true
        // pool-1-thread-3 [producer] at 1561679316792 hold: true
        // pool-1-thread-2 [consumer] at 1561679317664 hold: true
        // pool-1-thread-1 [producer] at 1561679317769 hold: true
        // pool-1-thread-1 [consumer] at 1561679318178 hold: true
        // pool-1-thread-2 [producer] at 1561679318618 hold: true
        // pool-1-thread-3 [consumer] at 1561679318661 hold: true
        // pool-1-thread-1 [producer] at 1561679319316 hold: true
        // pool-1-thread-2 [consumer] at 1561679320291 hold: true
        // pool-1-thread-2 [producer] at 1561679320674 hold: true
        // pool-1-thread-2 [consumer] at 1561679321324 hold: true
        // pool-1-thread-4 [producer] at 1561679322291 hold: true
        // pool-1-thread-1 [consumer] at 1561679322872 hold: true
        // pool-1-thread-3 [producer] at 1561679323806 hold: true
    }
}
