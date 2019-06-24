## 4 额外的线程能力

除了前面提到的Thread类，Runnable接口，同步，等待，以及通知，
还有接下来的更实用的，也是经常见面的线程组和线程局部变量。

### 4.1 线程组（ThreadGroup）

  一个线程组代表了一组线程。初次之外，一个线程组也能饱含其他的线程组。
  这些线程组形成一棵树，其中除了初始线程，每个线程组都有一个父线程组。
  
  使用ThreadGroup对象，可以对其中的所有线程进行操作。
  假设一个线程组被tg引用，tg.suspend();会暂停线程组内的所有线程。
  
  使用线程组要当心，因为ThreadGroup是非线程安全的。
  
  检查时间到使用时间（time_of_check_to_time_of_use）,该类场景存在缺陷。因为检查文件和操作期间，文件也可能被删除或创建。
  
```
    // thread group
    Runnable r = () -> {
        System.out.println("active count: " + Thread.activeCount());
    };
    ThreadGroup threadGroup = new ThreadGroup("group1");
    Thread t1 = new Thread(threadGroup, r,"t1");
    Thread t2 = new Thread(threadGroup, r,"t2");
    Thread t3 = new Thread(threadGroup, r,"t3");

    t1.start();
    t2.start();
    t3.start();

    // 这些方法很有用，但是都被废弃，因为很容易诱发死锁等问题
    threadGroup.suspend();
    threadGroup.resume();
    threadGroup.stop();

    // 输出
    // active count: 1
    // active count: 3
    // active count: 1
```  
  
  线程异常
  
```
public class ThreadGroupDemo {

    public static void main(String[] args) {

        // 1
        try{
            UncaughtExceptionThread uncaughtExceptionThread = new UncaughtExceptionThread();
            uncaughtExceptionThread.start();
        }catch (Exception e){
            // 不会执行
            System.out.println("caught " + e);
        }

        // 输出
        // Exception in thread "Thread-0" java.lang.ArithmeticException: / by zero
        //	at chapter4.UncaughtExceptionThread.run(ThreadGroupDemo.java:23)

        // 2
        try{
            UncaughtExceptionThread uncaughtExceptionThread = new UncaughtExceptionThread();
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
                // 捕获
                System.out.println("Caught throwable " + e + " for thread " + t);
            };
            uncaughtExceptionThread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            uncaughtExceptionThread.start();
        }catch (Exception e){
            // 不会执行
            System.out.println("caught " + e);
        }

        // 输出
        // Caught throwable java.lang.ArithmeticException: / by zero for thread Thread[Thread-0,5,main]

        // 3
        try{
            UncaughtExceptionThread uncaughtExceptionThread = new UncaughtExceptionThread();
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
                // 捕获
                System.out.println("Caught throwable " + e + " for thread " + t);
            };
            // 3-1 
            uncaughtExceptionThread.setUncaughtExceptionHandler(uncaughtExceptionHandler);

            Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = (t, e) -> {
                // 捕获
                System.out.println("Default Caught throwable " + e + " for thread " + t);
            };
            // 3-2
            Thread.setDefaultUncaughtExceptionHandler(defaultUncaughtExceptionHandler);

            uncaughtExceptionThread.start();
        }catch (Exception e){
            // 不会执行
            System.out.println("caught " + e);
        }

        // 输出 如果设置3-1，则3-1执行；如果设置3-2，没有设置3-1，则3-2执行
        // Caught throwable java.lang.ArithmeticException: / by zero for thread Thread[Thread-0,5,main]
        // Default Caught throwable java.lang.ArithmeticException: / by zero for thread Thread[Thread-2,5,main]

    }
}

class UncaughtExceptionThread extends Thread{
    @Override
    public void run() {
        int x = 1 / 0;
    }
}
```
一旦run()方法抛出异常，线程就会终止并被下列活动取代
* Java虚拟机(JVM)寻找Thread.UncaughtExceptionHandler的实例，
  该实例由Thread类的void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh)方法设置
  当找到这个handler时，线程就会执行它的void uncaughtException(Thread t, Throwable e)方法，
  这里的t代表抛出异常线程所关联的Thread对象，而代表抛出的异常或者错误本身。
* 默认的未捕获异常处理Thread.setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler ueh)


#### 4.2 线程局部变量
  
  ThreadLocal实例代表了一个线程局部变量，它为每一条线程提供了单独的存储槽（storage slot）。
  可以形象的比喻为具有多个槽的变量，每个线程单独存取自己的变量，井水不犯河水（互不影响）。
  
  一般用泛型使用 ThreadLocal<T>，T为存储在该变量值中的类型
  
  * ThreadLocal()，创建一个新的线程局部变量
  * T get()，返回存储槽中的值，如果值不存在，则调用initialValue()方法
  * T initialValue()，默认值是null。可以重写该方法
  * void remove()，清空调用线程的存储槽。
  * void set(T value)，设置调用线程的存储槽上的值。
  
```
public class ThreadLocalDemo {

    public static void main(String[] args) {

        ThreadLocalThread threadLocalThread = new ThreadLocalThread();
        threadLocalThread.setName("A");
        threadLocalThread.start();;

        threadLocalThread = new ThreadLocalThread();
        threadLocalThread.setName("B");
        threadLocalThread.start();

        // 输出
        // user name A
        // user name Other
    }
}

class ThreadLocalThread extends Thread{
    // 注意该处的修饰符，也可以使用final
    private static volatile ThreadLocal<String> userName = new ThreadLocal<>();
    @Override
    public void run() {
        Thread current = Thread.currentThread();
        if("A".equals(current.getName())){
            userName.set("A");
        } else {
            userName.set("Other");
        }

        System.out.println("user name " + userName.get());
    }
}
```

那如果我们想要在父线程和子线程之间传递数据，该怎么处理呢？

```
/**
 * 父线程传递给子线程数据
 */
public class InheritableThreadLocalDemo {

    private static final InheritableThreadLocal<String> passValue = new InheritableThreadLocal();
    public static void main(String[] args) {

        Runnable runnableParent = () -> {
            passValue.set("Parent");

            Runnable runnableChild = () -> {
                Thread current = Thread.currentThread();
                System.out.println(current.getName() + "," +  passValue.get());
            };

            Thread child = new Thread(runnableChild);
            child.setName("Child");
            child.start();
            System.out.println("Parent -> " + passValue.get());

        };

        Thread parent = new Thread(runnableParent);
        parent.setName("Parent");
        parent.start();
        System.out.println("Main -> " + passValue.get());

        // 输出
        // Main -> null
        // Parent -> Parent
        // Child,Parent
    }
}
```

#### 4.3 定时器框架

    Timer、TimerTask
    Timer让你能够在一个后台线程中调度TimerTask用于后续执行（以顺序的方式），它也称为任务执行线程。
    定时器任务可能会因为单次的执行或者规律性的重复执行而被调度。
    
    * 单次执行
    * 周期重复执行
    * 将来某一时刻执行 （如果是过去时间，则立即执行）
    
    Timer在内部，使用了一个二进制的堆表示其定时任务队列，是的时间复杂度为O(log n),n为并发调度时定时任务的数量。
    
    * Timer() 新建定时器，执行线程为非守护线程
    * Timer(boolean isDaemon) 新建定时器，同时设置是否守护线程
    * Timer(String name) 新建定时器，同时设置定时器名称
    * Timer(String name，boolean isDaemon) 新建定时器，同时设置定时器名称和是否守护线程
    
    * void cancel(), 终止定时器。不会干涉当前正在执行的定时器任务，优雅的终止掉并不会再有任务被调度。
    * int purge(), 从该定时器队列中移除所有取消的定时任务并且返回被移除任务的数目。
    * void schedule(TimerTask task, Date time), 在某个时间点调度任务执行
    * void schedule(TimerTask task, Date firstTime, long period), 调度任务于firstTime开始，以period秒固定时间间隔重复执行。
    * void schedule(TimerTask task, long delay), 调度任务延迟delay秒执行
    * void schedule(TimerTask task, long delay, long period), 调度任务延迟delay秒执行，之后以period秒固定时间间隔重复执行。
    * void scheduleAtFixedRate(TimerTask task, Date firstTime, long period), 调度任务于firstTime开始，以period秒固定时间间隔重复执行。
    * void scheduleAtFixedRate(TimerTask task, long delay, long period), 调度任务延迟delay秒执行，之后以period秒固定时间间隔重复执行。

    TimerTask 
    定时任务应该很快完成。如果一个定时任务花费了很长的时间完成，它就会霸占定时器的任务执行线程，推迟后续定时任务的执行。
    这些后续的定时任务可能会集中在一个时间点，并且当这个侵入的定时任务最终完成，它们会接连快速的执行。
    
    * void cancel() 取消这个定时任务
    * long scheduledExecutionTime() 最近一次被调度执行的时间
```
/**
 * 定时器
 */
public class TimerAndTimeTask {

    public static void main(String[] args) {

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " run at " + System.currentTimeMillis());
                // 不设置，timer会一值执行
//                System.exit(0);
            }
        };
        // execute one-shot timer task after 2-second delay
        timer.schedule(task, 2000);

        TimerTask taskDelay = new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " run at " + System.currentTimeMillis());
            }
        };
        // delay 2-second, repeat every 1-second
        timer.schedule(taskDelay, 2000, 1000);

        TimerTask taskFixed = new TimerTask() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " run at " + System.currentTimeMillis());
                // 此定时任务最近被实际调度执行的时间
                System.out.println(Thread.currentThread().getName() + " execute time: " + this.scheduledExecutionTime());
            }
        };
        // delay 2-second, repeat every 1-second
        timer.scheduleAtFixedRate(taskFixed, 2000, 1000);


        TimerTask taskCancel = new TimerTask() {
            @Override
            public void run() {

                System.out.println(Thread.currentThread().getName() + " cancel at " + System.currentTimeMillis());
                // cancel
                timer.cancel();
            }
        };
        // 5 秒之后 取消定时任务
        timer.schedule(taskCancel, new Date(System.currentTimeMillis() + 5000));

        // 输出
        // Timer-0 run at 1561338802370
        // Timer-0 run at 1561338802371
        // Timer-0 run at 1561338802371
        // Timer-0 execute time: 1561338802365
        // Timer-0 run at 1561338803370
        // Timer-0 execute time: 1561338803365
        // Timer-0 run at 1561338803371
        // Timer-0 run at 1561338804370
        // Timer-0 execute time: 1561338804365
        // Timer-0 run at 1561338804372
        // Timer-0 run at 1561338805369
        // Timer-0 execute time: 1561338805365
        // Timer-0 cancel at 1561338805369
    }
}

```
    
    