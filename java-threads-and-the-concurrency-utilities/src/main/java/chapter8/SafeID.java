package chapter8;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程安全的ID
 * CAS(存在自旋问题)
 *
 * @author neal.ma
 * @date 2019/6/29
 * @blog nealma.com
 */
public class SafeID {

    private static AtomicLong nextID = new AtomicLong(1);

    public static void main(String[] args) {
        Runnable r = () -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + ": " + getNextID());
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(r);
        executorService.execute(r);

        executorService.shutdown();
    }

    static long getNextID(){
        long currentValue = nextID.longValue();
        if(nextID.compareAndSet(currentValue, currentValue + 1)){
        } else {
            //自旋
            System.out.println(Thread.currentThread().getName() + ": " + currentValue + ", continue......");
        }
        return nextID.longValue();
    }

    // 输出
    // pool-1-thread-2: 2
    // pool-1-thread-1: 1, continue......
    // pool-1-thread-1: 2
    // pool-1-thread-1: 3
    // pool-1-thread-2: 2, continue......
}
