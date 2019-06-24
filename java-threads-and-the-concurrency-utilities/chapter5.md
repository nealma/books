## 5 并发工具类和 Executor 框架

之前介绍的主要关注Java对底层线程操作的支持。接下来我们关注下Java对高级线程操作的支持，即并发工具类。

### 5.1 并发工具类介绍

  Java对底层线程操作的支持使得你可以创建多线程应用程序。
  这些应用程序比对应的单线程程序提供了更好的性能和响应能力。但依然存在问题
  * 低级的并发原语，比如synchronized和wait、notify等很难被正确使用，从而导致竞态条件、线程饿死、死锁和其他风险。
  * 太过依赖synchronized原语存在性能问题，也会影响程序的课扩展性，对于诸如Web服务器之类的高度线程化的程序而言，后果很严重。
  * 开发者需要高级的线程结构，如线程池和信号量。需要自己构建，不仅耗时而且容易出错。
  
  基于以上问题，Java 5 引入了并发工具类，由强大且容易扩展的高性能工具类组成，包含线程池和阻塞队列。
  * java.util.concurrent: 并发编程工具类，如Executors
  * java.util.concurrent.atomic: 支持单个变量无所且线程安全操作
  * java.util.concurrent.locks: 在某些条件下获取锁和执行等待的工具类。虽然Java可以通过监听器来实现同步、等待以及唤醒机制，但这些类型具有更好的性能和伸缩性。
  
  可以通过java.long.System.nanoTime()访问纳秒级别的时间资源。
  
  并发工具类可以分为executor、同步器（synchronizer）以及锁框架等。

#### 5.2 探索 Executor
  
  并发工具类使用高级的Executor替代了底层的线程操作执行任务。
  一个executor，它的类直接或间接实现了java.util.concurrent.Executor接口，解耦任务提交操作。
  Executor声明了一个单独的void execute(Runnable runnable)方法，该方法会在将来的某个时间点执行这个名为runnable的可运行任务。
```
public class ExecutorThread {

    public static void main(String[] args) {
        Executor executor = command -> System.out.println("command");
        executor.execute(new RunnableTask());
    }
}

class RunnableTask implements Runnable{

    @Override
    public void run() {
        System.out.println("task run");
    }
}
```

 Executor 存在的限制
    
 * 只关注Runnable接口。由于Runnable的run()方法没有返回值，所以无法把值返回给调用者。
 * 无法追踪可运行任务的运行过程。
 * 无法执行一组可运行的任务。
 * 无法正确的关闭executor。
 

 解决办法：使用扩展接口java.util.concurrent.ExecutorService，其实现是一个典型的线程池。
 
 * boolean awaitTermination(long timeout, TimeUnit unit) 在一条关闭请求之后，
    不论是任务全部已经完成、timeout（以unit的时间单位衡量）超时还是当前线程被中断的哪一条先发生，都会一直阻塞（等待），
    如果executor已经终止，返回true，否则返回false。
 * <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) 执行任务集合中每一个callable任务并当所有任务执行完成后（即任务要么正确执行，要么抛出异常），
    返回一个java.util.List的java.util.concurrent.Future的实例，这些实例会持有任务的状态和结果。这组Future和任务迭代器返回的任务序列具有相同的顺序。
    当任务处于等待状态却被终端时，该方法抛出InterruptedException，同时未被完成的任务也会被取消；
 * <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) 同上，只是增加超时时间
 * <T> Future<T> invokeAny(Collection<? extends Callable<T>> tasks) 执行给出的tasks，如果有任务完成执行，就会返回其结果。
 * <T> Future<T> invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) 同上，只是增加超时时间。
 * boolean isShutdown() 当其executor已经终止时，返回true；否则，返回false。
 * boolean isTerminated() 
 * void shutdown() 有序地开始关闭之前提交执行的任务，不在接受新的任务 
 * List<Runnable> shutdownNow() 尝试停止所有活跃的执行线程，挂起等待任务的进程，并且返回一组正在等待执行的任务。
 * <T> Future<T> submit(Callable<T> task) 提交一个callable任务来执行，同时返回一个代表任务等待结果的Future实例。
    这个Future实例的get()方法在成功执行完成后返回任务的结果。result=exec.submit(aCallable).get() 
 * Future<?> submit(Runnable task) 提交一个runnable任务来执行，同时返回一个代表此任务的Future实例。该Future的get方法在成功完成之后为null
 * T Future<T> submit(Runnable task, T result) 提交一个runnable任务来执行，同时返回一个代表此任务的Future实例。其get方法在成功完成的情况下返回结果的值。

 Executors 工厂类，所有的方法返回的都是ThreadPoolExecutor、ScheduledThreadPoolExecutor这两个类的实例。
 * newCachedThreadPool 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
 * newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
 * newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。
 * newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。

```
/**
 * ExecutorService
 */
public class ExecutorServiceThread {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // execute(Runnable)
        executorService.execute(new RunnableTask());

        // submit(Runnable)
        Future future = executorService.submit(new RunnableTask());
        try {
            System.out.println("result: " + future.get()); //result: null
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // submit(Callable)
        Future<String> futureResult = executorService.submit(new CallableTask());
        try {
            while (!futureResult.isDone()){
                System.out.println("waiting");
            }
            System.out.println("result: " + futureResult.get()); //result: async callable
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // invokeAny
        List<Callable<String>> tasks = new ArrayList<>();
        tasks.add(new CallableTask1());
        tasks.add(new CallableTask2());
        tasks.add(new CallableTask3());
        try {
            String result = executorService.invokeAny(tasks);
            System.out.println("result: " + result); // result: async callable 2

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // invokeAll
        try {
            List<Future<String>> futures = executorService.invokeAll(tasks);
            futures.forEach(item -> {
                try {
                    System.out.println("result: " + item.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 输出
        // java.lang.ArithmeticException: / by zero
        // result: async callable 2
        // result: async callable 3

        // shutdown
        executorService.shutdown();

//        executorService.shutdownNow();

    }
}

class RunnableTask1 implements Runnable{

    @Override
    public void run() {
        System.out.println("task run");
    }
}
class CallableTask implements Callable {

    @Override
    public Object call() throws Exception {
        Thread.sleep(3000);
        return "async callable";
    }
}

class CallableTask1 implements Callable {

    @Override
    public Object call() throws Exception {
        int i = 1 / 0;
        return "async callable 1";
    }
}
class CallableTask2 implements Callable {

    @Override
    public Object call() throws Exception {
        return "async callable 2";
    }
}
class CallableTask3 implements Callable {

    @Override
    public Object call() throws Exception {
        return "async callable 3";
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
    
    