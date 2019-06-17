package chapter1;

/**
 * 中断
 *
 * @author neal.ma
 * @date 2019/6/17
 * @blog nealma.com
 */
public class InterruptThread {

    public static void main(String[] args) {
        // current
        Thread main = Thread.currentThread();
        Runnable r1 = () -> {

            Thread current = Thread.currentThread();
            int count = 0;
            while(!Thread.interrupted()){
                System.out.println(""+ current.getName() + ", count: " + (count++));
            }
            System.out.println("thread [" + current.getName() + "] state: " + current.getState() + ", interrupt: " + current.isInterrupted());
            System.out.println("thread [" + main.getName() + "] state: " + main.getState() + ", interrupt: " + main.isInterrupted());
        };


        Thread t1 = new Thread(r1, "t1");
        Thread t2 = new Thread(r1, "t2");
        t1.start();
        t2.start();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) { e.printStackTrace(); }

        t1.interrupt();
        t2.interrupt();
        System.out.println("thread [" + main.getName()+ "] state: " + main.getState() + ", interrupt " + main.isInterrupted());
    }
}
