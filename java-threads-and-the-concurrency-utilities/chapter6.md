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

#### 5.2 探索 Executor
  
  并发工具类使用高级的Executor替代了底层的线程操作执行任务。
  一个executor，它的类直接或间接实现了java.util.concurrent.Executor接口，解耦任务提交操作。
  Executor声明了一个单独的void execute(Runnable runnable)方法，该方法会在将来的某个时间点执行这个名为runnable的可运行任务。
```


```
    
    