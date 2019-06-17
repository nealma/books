package chapter2;

/**
 * 缓存变量
 *
 * @author neal.ma
 * @date 2019/6/17
 * @blog nealma.com
 */
public class VolatileThread {

    public static void main(String[] args) {

        StoppableThread t1 = new StoppableThread();
        t1.start();

        try {
            Thread.sleep(2000);
            t1.stopThread();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

class StoppableThread extends Thread {
    /**
     * 由于 stopped 已经标记为 volatile，每条线程都会访问主存中该变量的拷贝，而不会访问缓存中的拷贝。
     * 这样，即使在多处理器或者在多核的机器上，该程序也会停止。
     */
    private volatile boolean stopped;

    @Override
    public void run() {
        while (!stopped){
            System.out.println("running");
        }
    }

    void stopThread(){
        stopped = true;
    }
}