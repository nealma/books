package chapter1;

/**
 * 等待
 *
 * @author neal.ma
 * @date 2019/6/17
 * @blog nealma.com
 */
public class JoinThread {

    public static void main(String[] args) {

        // current
        Thread main = Thread.currentThread();
        Runnable r1 = () -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) { e.printStackTrace(); }
            Thread current = Thread.currentThread();
            System.out.println("thread [" + current.getName() + "] state: " + current.getState() + ", interrupt: " + current.isInterrupted());
            System.out.println("thread [" + main.getName() + "] state: " + main.getState() + ", interrupt: " + main.isInterrupted());
        };

        Thread t1 = new Thread(r1, "t1");
        t1.start();

        try {
            t1.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("thread [" + main.getName()+ "] state: " + main.getState() + ", interrupt " + main.isInterrupted());
    }
}
