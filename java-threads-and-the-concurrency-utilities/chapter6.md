## 6 同步器

Java提供了synchronized关键字对临界区进行线程同步访问，我们也知道通过synchronized很难写出正确的同步代码，于是并发工具类提供了更高级的同步器。
倒计时门闩count down latch，同步屏障cyclic barrier，交换器exchanger，信号量semaphone 和 phaser同步器
### 6.1 倒计时门闩CountDownLatch

  倒计时门闩会导致一条或多条线程在"门口"一直等待，直到一条线程打开这扇门，线程得以继续运行。
  由一个计数变量和两个操作组成。
  这些应用程序比对应的单线程程序提供了更好的性能和响应能力。但依然存在问题
  * void await() 除非线程被中断，否则强制调用线程一直等到计数器递减至0.
  * void await(long timeout, TimeUnit unit) 除非线程被中断，否则强制调用线程一直等到计数器递减至0,或以unit为单位的timeout超时
  * void countDown() 递减计数，当降至0时，释放所有等待线程。
  * long getCount() 返回当前计数
  
```
public class CountDownLatchThread {

    private static final int THREAD_SIZE = 3;

    public static void main(String[] args) {

        CountDownLatch doneSignal = new CountDownLatch(THREAD_SIZE);

        Runnable runnable = () -> {
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " starting, count " + doneSignal.getCount());
            doneSignal.countDown();
            System.out.println(Thread.currentThread().getName() + " done, count " + doneSignal.getCount());
        };

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_SIZE);

        int count = 0;
        while(3 > count){
            executorService.execute(runnable);
            count++;
        }

        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        System.out.println("main thread done.");
    }
}
```  

#### 6.2 同步屏障 CyclicBarrier
  
  同步屏障允许一组线程彼此相互等待，直到抵达某个公共的屏障点。
  因为该屏障在等待线程被释放之前可以重用，所以称它为可循环使用的屏障。
  该同步器对于数量固定并且相互之间必须不时等待彼此的多线程应用。
  
  打个比方：有一个古墓，古墓的入口已经被封闭千年，需要集齐东南西北四大法器，同时拼在开门机关上，古墓才能被打开，同步屏障就是那道门。
  
  CyclicBarrier构造函数
  * CyclicBarrier(int parties) 拥有共同执行目标的线程数目
  * CyclicBarrier(int parties, Runnable barrierAction)  在parties条线程执行之前，执行BarrierAction中的run(),多用于更新共享变量。
  
  CyclicBarrier提供的方法
  * int await() 强制调用线程一直等待直到所有的parties都已经在同步屏障上调用了await()方法。
    当调用线程自己或其他等待线程被中断、有线程在等待中超时或者有线程在同步屏障之上调用reset()方法，该调用线程就会停止等待。
  * int await(long timeout, TimeUnit unit) 除了让你指定调用线程愿意等待的时长之外，该方法等同于上面的方法。  
  * int getNumberWaiting() 返回在当前同步屏障上等待的线程数目。
  * int getParties() 返回需要跨越同步屏障的线程数目。
  * boolean isBroken() 当一条或多条线程由于在同步屏障创建或在上次重置之后，中断或超时从而被打破同步屏障，或者因为一个异常导致barrier action失败时，返回true，否则返回false。
  * void reset() 把同步屏障重置到其原始状态
```
    /**
     * 同步屏障
     */
    public class CyclicBarrierThread {
    
        public static void main(String[] args) {
            // 1
            CyclicBarrier barrier = new CyclicBarrier(2);
            Runnable r = () -> {
                try {
                    System.out.println("waiting " + barrier.getNumberWaiting() + " / " + barrier.getParties());
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
                    System.out.println("waiting " + cyclicBarrier.getNumberWaiting() + " / " + cyclicBarrier.getParties());
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
        }
    }
    
    class Tomb implements Runnable {
        @Override
        public void run() {
            System.out.println("open the door.");
        }
    }
  ```
  CyclicBarrier与CountDownLatch区别是，CyclicBarrier可以重复使用；
  
  ```
    new Thread(runnable).start();
    new Thread(runnable).start();
    new Thread(runnable).start();
    new Thread(runnable).start();
    new Thread(runnable).start();
    new Thread(runnable).start();
    
    // 重复使用 输出
    // Thread-4 waiting 0 / 2
    // Thread-5 waiting 1 / 2
    // Thread-5 open the door.
    // Thread-6 waiting 0 / 2
    // Thread-7 waiting 1 / 2
    // Thread-7 open the door.
    // Thread-8 waiting 0 / 2
    // Thread-9 waiting 1 / 2
    // Thread-9 open the door.
  ```
#### 6.3 交换器 Exchanger

    交换器提供了一个线程彼此之间能够交换对象的同步点。 
    
    * V exchange(V x) 在这个交互点上，等待其他线程的到达，之后将所给对象传入其中，接收其他线程的对象作为返回。
    * V exchange(V x, long timeout, TimeUnit unit) 除指定线程愿意等待的时长之外，功能同上。
 ```
/**
 * 交换器
 */
public class ExchangerThread {

    public static void main(String[] args) {

        final Exchanger<String> exchanger = new Exchanger<>();
        final List<String> shared = new ArrayList<>();

        Runnable r = () -> {
            try {
                while (true){
                    String name = Thread.currentThread().getName();
//                    String exchangeData = exchanger.exchange(name);
                    String exchangeData = exchanger.exchange(name, 1, TimeUnit.SECONDS);
                    System.out.println(name + " " + exchangeData);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        };

        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        
        // 输出
        // Thread-1 Thread-0
        // Thread-1 Thread-2
        // Thread-0 Thread-2
        // Thread-0 Thread-1
        // Thread-2 Thread-1
        // Thread-2 Thread-0
        // Thread-1 Thread-0
        // Thread-1 Thread-2
    }
}   
 ```
 
 #### 6.4 信号量
 
 