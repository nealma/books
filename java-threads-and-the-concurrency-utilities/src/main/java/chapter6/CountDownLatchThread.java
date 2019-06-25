package chapter6;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 缓存变量
 *
 * @author neal.ma
 * @date 2019/6/25
 * @blog nealma.com
 */
public class CountDownLatchThread {

    private static final int THREAD_SIZE = 3;

    public static void main(String[] args) {
        CountDownLatch doneSignal = new CountDownLatch(THREAD_SIZE);

        Runnable runnable = () -> {
            try {
                Thread.sleep(new Random(10).nextInt());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " starting, count " + doneSignal.getCount());
//            doneSignal.countDown();
            System.out.println(Thread.currentThread().getName() + " done, count " + doneSignal.getCount());
        };

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_SIZE);

        int count = THREAD_SIZE;
        while(count < 3){
            executorService.execute(runnable);
            count++;
        }
//
//        try {
//            doneSignal.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        executorService.shutdown();

        System.out.println("main thread done.");
    }
}
