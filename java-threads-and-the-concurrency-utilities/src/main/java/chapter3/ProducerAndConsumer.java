package chapter3;

/**
 * 等待
 *
 * @author neal.ma
 * @date 2019/6/23
 * @blog nealma.com
 */
public class ProducerAndConsumer {

    public static void main(String[] args) {

        SharedData s = new SharedData();
        Producer p = new Producer(s);
        Consumer c = new Consumer(s);

        p.start();
        c.start();

        /** 没有1和2 输出  发现奇怪的输出顺序，其实是同步执行的，只不过out没有加入同步块
            v -> used by consumer.
            w -> created by producer.
            x -> created by producer.
            w -> used by consumer.
            y -> created by producer.
            x -> used by consumer.
            y -> used by consumer.
        */

        /** 有1和2 输出 看着舒服多了
         v -> created by producer.
         v -> used by consumer.
         w -> created by producer.
         w -> used by consumer.
         x -> created by producer.
         x -> used by consumer.
         y -> created by producer.
         y -> used by consumer.
        */
    }
}

/**
 * 共享变量
 */
class SharedData {
    private char c;
    private volatile boolean writeable = true;

    synchronized void setChar(char c){
        while(!writeable){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.c = c;
        this.writeable = false;
        notify();

    }

    synchronized char getChar(){
        while(writeable){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.writeable = true;
        notify();
        return c;
    }
}

/**
 * 生产者
 */
class Producer extends Thread{

    private final SharedData s;

    Producer(SharedData s) {
        this.s = s;
    }

    @Override
    public void run() {
        for (char c = 'a'; c < 'z'; c++){
            synchronized (s){ // 1
                s.setChar(c);
                System.out.println(c + " -> created by producer.");
            }
        }
    }
}

/**
 * 消费者
 */
class Consumer extends Thread{

    private final SharedData s;

    Consumer(SharedData s) {
        this.s = s;
    }

    @Override
    public void run() {
        char c;
        do {
            synchronized (s){ // 2
                c = s.getChar();
                System.out.println(c + " -> used by consumer.");
            }
        } while (c != 'z');
    }
}