package chapter6;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 同步屏障
 *
 * @author neal.ma
 * @date 2019/6/26
 * @blog nealma.com
 */
public class CyclicBarrierThread {

    public static void main(String[] args) {

        // 1
        CyclicBarrier barrier = new CyclicBarrier(2);
        Runnable r = () -> {
            try {
                System.out.println(Thread.currentThread().getName() + " waiting " + barrier.getNumberWaiting() + " / " + barrier.getParties());
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        };
        new Thread(r).start();
        new Thread(r).start();

        // 输出
        // waiting 0 / 2
        // waiting 1 / 2

        // 2
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, new Tomb());
        Runnable runnable = () -> {
            try {
                System.out.println(Thread.currentThread().getName() +" waiting " + cyclicBarrier.getNumberWaiting() + " / " + cyclicBarrier.getParties());
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        };
        new Thread(runnable).start();
        new Thread(runnable).start();

        // 输出
        // waiting 0 / 2
        // waiting 1 / 2
        // open the door.

//        cyclicBarrier.reset();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();

        // 重复使用
        // Thread-4 waiting 0 / 2
        // Thread-5 waiting 1 / 2
        // Thread-5 open the door.
        // Thread-6 waiting 0 / 2
        // Thread-7 waiting 1 / 2
        // Thread-7 open the door.
        // Thread-8 waiting 0 / 2
        // Thread-9 waiting 1 / 2
        // Thread-9 open the door.
    }
}

class Tomb implements Runnable {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " open the door.");
    }
}