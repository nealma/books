## 3 等待和通知

Java使用wait 和 notify 这两个API来支持线程间交互。
一个线程持续等待某个条件成立，后续会有另一条线程创造出这一条件，进而通知处于等待中的线程。

### 3.1 等待、通知
  * void wait() 当前线程处于等待状态，直到另外的线程调用这个对象的notify()或notifyAll()方法；或者等待其他线程终端当前等待的线程。
  * void wait(long timeout) 同wait(), 只是多一个条件，等待特定毫秒数后，也会打破等待状态
  * void wait(long timeout, int nanos) 同上，只是时间不同而已
  * notify() 唤醒正在等待该对象监听器的单条线程。如果同时有几条线程在等待该对象，随意选出一条线程被唤醒。除非当前线程释放了该对象上的锁，否则被唤醒的线程扔不能执行。
  * notifyAll() 唤醒正在等待该对象监听器的所有线程。除非当前线程释放了该对象上的锁，否则被唤醒的所有线程扔不能执行。
  
  改组API利用条件队列存储等待的线程。
  由于该条件队列和对象的锁紧密捆绑在一起，以上5个方法必须在同步的上下文中（当前的线程必须是该对象监听器的所有者）被调用。否则抛出java.lang.IllegalMonitorStateException。

```wait()
  synchronized(obj){
    while(condition does not hold){
        obj.wait();
    }
    // go to logic
  }
```
```notify()
  synchronized(obj){
    // set the condition
    obj.notify();
  }
```
**遵照以上模式，避免陷入麻烦**

#### 3.2 生产者和消费者
  
  生产者和消费者线程之间的关系是涉及条件的线程交互的一个经典例子。
  生产者生产数据，消费者消费数据，数据存储在共享变量中。
  
  假设线程以不同的速度前行，生产者可能在消费者读取数据之前就生产数据并存储在共享变量中；也可能生产者还没生产数据，消费者已经读取共享变量的值了。
  
  为了克服这些问题，生产者线程必须等待，直到被通知之前生产的数据已经被消费；消费者也必须一直等待，直到被通知新数据已经被生产。
  
```
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
```