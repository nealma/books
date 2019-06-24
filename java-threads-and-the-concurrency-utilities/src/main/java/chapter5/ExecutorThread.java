package chapter5;

/**
 * Executor 框架
 *
 * @author neal.ma
 * @date 2019/6/24
 * @blog nealma.com
 */
public class ExecutorThread {

    public static void main(String[] args) {
        // current
        Thread main = Thread.currentThread();
        // 场景1 t1为守护线程，运行时间 r1（3s） > r2（1s）
        Runnable r1 = () -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) { e.printStackTrace(); }

            Thread current = Thread.currentThread();
            System.out.println("thread [" + current.getName() + "] is " + current.getState());
        };
        Runnable r2 = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(main.getState());
            Thread current = Thread.currentThread();
            System.out.println("thread [" + current.getName() + "] is " + current.getState());
            System.out.println("thread [" + current.getName() + "] is " + current.getState());
        };

        Thread t1 = new Thread(r1, "t1");
        Thread t2 = new Thread(r2, "t2");

        // daemon
        t2.setDaemon(true);

        t1.start();
        t2.start();

        System.out.println("thread [" + main.getName() + "] is " + main.getState());
    }
}
