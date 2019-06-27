package chapter6;

import java.util.Random;
import java.util.concurrent.Phaser;

/**
 * Phaser
 *
 * @author neal.ma
 * @date 2019/6/27
 * @blog nealma.com
 */
public class PhaserThread {

    public static void main(String[] args) {

        final Phaser phaser = new Phaser();

        Runnable r = () -> {
            try {
                System.out.println(Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " phase: " + phaser.arriveAndAwaitAdvance());
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        for (int i=0; i<10; i++){
            System.out.println(phaser.register());

        }
        for (int i=0; i<10; i++){
            new Thread(r).start();
            System.out.println("parties: " + phaser.getRegisteredParties() + ", phase: " + phaser.getPhase());
        }

        // 等待所有参与者都执行完，注销该phaser
        phaser.arriveAndDeregister();

        // 输出
        // 0
        // 0
        // 0
        // 0
        // 0
        // 0
        // 0
        // 0
        // 0
        // 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // Thread-7 at 1561593950111 phase: 1
        // Thread-2 at 1561593950110 phase: 1
        // Thread-3 at 1561593950110 phase: 1
        // Thread-4 at 1561593950110 phase: 1
        // Thread-6 at 1561593950111 phase: 1
        // Thread-8 at 1561593950111 phase: 1
        // Thread-5 at 1561593950111 phase: 1
        // Thread-1 at 1561593950108 phase: 1
        // Thread-0 at 1561593950106 phase: 1
    }
}
