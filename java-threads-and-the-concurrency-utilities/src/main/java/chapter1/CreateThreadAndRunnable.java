package chapter1;

/**
 * msg
 *
 * @author neal.ma
 * @date 2019/6/17
 * @blog nealma.com
 */
public class CreateThreadAndRunnable {

    public static void main(String[] args) {

        System.out.println("args； " + args + ", args.0" + args[0]);

        boolean isDaemon = (args.length != 0);

        // current
        Thread main = Thread.currentThread();

        // 1 normal
        Runnable r = new Runnable() {


            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Thread current = Thread.currentThread();
                // perform some work
                System.out.println("[normal] Hello from thread "
                        + current.getId() + ","
                        + current.getName() + ","
                        + current.isAlive() + ","
                        + current.getState());
            }
        };
        // 2 lambda jdk1.8+
        Runnable r1 = () -> System.out.println("[lambda] Hello from thread");

        Thread t = new Thread(r);
        Thread t1 = new Thread(r1, "thread-lambda");

        // set name
        t.setName("thread-normal");
        System.out.println(t.getId() + t.getName());

        // daemon
        if(isDaemon){
            t1.setDaemon(isDaemon);
        }
        t.start();
        t1.start();

        // 3
       MyThread myThread = new MyThread();
       //set name
       myThread.setName("myThread");

       // priority
       myThread.setPriority(Thread.MAX_PRIORITY);
       myThread.start();

       // get active
       System.out.println(myThread.isAlive());

       // get state, NEW,RUNNABLE,BLOCKED,WAITING,TIMED_WAITTING,TERMINATED
       System.out.println(myThread.getState());

       // get priority
       System.out.println(myThread.getPriority());

    }

    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("[thread] Hello from thread");
        }
    }

}
