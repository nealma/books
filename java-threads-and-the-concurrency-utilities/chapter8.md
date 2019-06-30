## 8 额外的并发工具类

    前面课程介绍了并发框架，Executor（Callable 和 Future），同步器，以及锁框架。
    下面我们介绍并发结合、原子变量、Fork/Join 框架，Completion Service。
    
### 8.1 并发集合

  Java的结合框架提供了位于java.util包下的诸多接口和类。其中接口包括了List、Set 和 Map。类包括 ArrayList、TreeMap 和 HashMap。
  ArrayList、TreeMap 和 HashMap 以及实现类这些接口的类都不是线程安全的。不过你可以使用类java.util.Collections中的同步包装方法
  让他们变得安全。举个栗子，可以向Collections.synchronizedList()中传入一个ArrayList实例，已获得一个线程安全的ArrayList。
  
  线程安全的集合存在的问题：
  * 在遍历一个集合之前，获取锁是很有必要的。集合很可能在遍历过程当中被其他线程修改。当集合在遍历过程中被修改时，快速失败迭代器会抛出ConcurrentModificationException异常
  * 同步的集合经常被多条线程频繁的访问，性能存在瓶颈，最终影响影应用程序的扩展能力。
  
  那怎么办呢？使用并发工具类
  
  并发工具类使用并发集合来应对这些问题，并发集合具有并发性能和高扩展行、面向集合的类型，他们存储在java.util.concurrent 包中，面向结合的类返回了弱一致性的迭代器
  
  弱一致性迭代器具有的属性
  * 迭代开始之后，被删除但还没有通过迭代器的 next() 方法被返回的元素，就不会再被返回了。
  * 迭代开始之后被添加的元素可能会返回也可能不会返回。
  * 在集合迭代的过程中，即便对集合做了改变，也没有任何元素会被返回超过一次。
  
  部分并发集合类
  * BlockingQueue 是接口 java.util.Queue 的子接口，它支持阻塞操作，即在获取一个元素之前，等待队列成为非空；
    在存储一个元素之前，等待队列中的空间变为可用。
    其实现类包括：ArrayBlockingQueue、DelayQueue、LinkedBlockingQueue、PriorityBlockingQueue 以及 SynchronousQueue。
                类 LinkedBlockingDeque 和 LinkedTransferQueue 通过实现 BlockingQueue 的子接口间接的实现这个接口。
  * ConcurrentMap 是 java.util.map 的子接口，声明了额外的原子的方法 putIfAbsent()、remove() 和 replace。你可以把它当做 java.util.HashMap 的并发版本。
    类 ConcurrentNavigableMap 和 ConcurrentSkipListMap 都实现了这个接口。
    
#### 8.1.1  BlockingQueue 和 ArrayBlockingQueue

    前面我们通过wait() 和 notify（）实现了生产者-消费者应用, 有了 BlockingQueue 其实代码可以更简单。
    
```
/**
 * 阻塞队列
 */
public class BlockingQueueThread {
    private volatile static boolean shared = false;
    public static void main(String[] args) {

        final BlockingQueue<Character> characters = new ArrayBlockingQueue(26);
        Runnable producer = () -> {
            for (char c='a'; c < 'z'; c++){
                try {
                    // 如果队列满了，put() 方被阻塞
                    characters.put(c);
                    System.out.println(Thread.currentThread().getName() + " [producer] at " + System.currentTimeMillis() + " create : " + c);
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(producer);

        Runnable consumer = () -> {
            try {
                char c;
                do {
                    // 如果队列空了，take() 方被阻塞
                    c = characters.take();
                    System.out.println(Thread.currentThread().getName() + " [consumer] at " + System.currentTimeMillis() + " create: " + c);
                    Thread.sleep(new Random().nextInt(1000));
                } while (c != 'z');
                executorService.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        executorService.execute(consumer);

        // 输出
        // pool-1-thread-1 [producer] at 1561775500363 create : a
        // pool-1-thread-2 [consumer] at 1561775500365 create: a
        // pool-1-thread-1 [producer] at 1561775501067 create : b
        // ......
        // pool-1-thread-2 [consumer] at 1561775513113 create: v
        // pool-1-thread-1 [producer] at 1561775513260 create : w
        // pool-1-thread-1 [producer] at 1561775513435 create : x
        // pool-1-thread-2 [consumer] at 1561775513857 create: w
        // pool-1-thread-1 [producer] at 1561775513969 create : y
        // pool-1-thread-2 [consumer] at 1561775514193 create: x
        // pool-1-thread-2 [consumer] at 1561775515071 create: y
    }
}
```
   如果发现打印顺序错乱，别忘了使用 synchronized 或者 lock/unlock 来同步代码块。

#### 8.1.2 ConcurrentHashMap
  
  类 ConcurrentHashMap 和 HashMap 在行为上没啥区别，但无需显示同步就可以工作于多条线程的上下文。
  
  看个熟悉的列子：你经常需要检查一个 map 中是否包含某个特定的值，当这个值不存在的时候，将它放进 map 中；
  
```
if ( !map.containsKey("key_name") ) {
    map.put("key_name", "value_content");
}
```
  乍一看没啥问题，但是放在多线程的环境中，他却不是线程安全的。
  map.containsKey(）和 map.put() 方法之间，其他线程可能插入了这个条目，很可能会被覆盖掉。
  为了消除这个竞态条件，你必须显示的同步这段代码
```
synchronized(map){
    if ( !map.containsKey("key_name") ) {
        map.put("key_name", "value_content");
    }
}
```
    好了，你觉得有了 synchronized 的庇护所向无敌了，但你没想到你在准备存储 value_content 的时候，
    其他读线程也无法继续工作，只能等待，严重影响了性能。别担心，我们还有更厉害的武器，类 ConcurrentHashMap 中
    的 putIfAbsent(), 相当于下面的代码但有更好的性能，
```
synchronized(map){
    if ( !map.containsKey("key_name") ) {
        return map.put("key_name", "value_content");
    } else {
        return map.get("key_name");
    }
}
```   
```
/**
 * ConcurrentHashMap
 *
 * @author neal.ma
 * @date 2019/6/29
 * @blog nealma.com
 */
public class ConcurrentHashMapThread {
    private volatile static boolean shared = false;
    public static void main(String[] args) {

        final ConcurrentHashMap<String, String> map = new ConcurrentHashMap(1);
        Runnable producer = () -> {
            try {
                String value = map.putIfAbsent("key", "value");
                Thread.sleep(new Random().nextInt(1000));
                System.out.println(Thread.currentThread().getName() + " [producer] " + value );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(producer);

        Runnable consumer = () -> {
            String value = map.get("key");
            System.out.println(Thread.currentThread().getName() + " [consumer] " + value );
        };
        int i = 0;
        do {
            executorService.execute(consumer);
            i++;
        } while (i < 10);

        executorService.shutdown();

        // 输出
        // pool-1-thread-2 [consumer] value
        // pool-1-thread-2 [consumer] value
        // pool-1-thread-2 [consumer] value
        // pool-1-thread-2 [consumer] value
        // pool-1-thread-2 [consumer] value
        // pool-1-thread-2 [consumer] value
        // pool-1-thread-2 [consumer] value
        // pool-1-thread-2 [consumer] value
        // pool-1-thread-2 [consumer] value
        // pool-1-thread-2 [consumer] value
        // pool-1-thread-1 [producer] null
    }
}
``` 
    
### 8.2 原子变量

    和对象监听器有关联的那些内置锁一直以来都存在性能问题。虽有提高，但他们依旧是创建 Web 服务器以及其他在高争用环境下
    要求更好的扩展及性能之类的应用的瓶颈。
    
    大量研究探寻在同步上下文中创建非阻塞的算法，这些算法可以从根本上提高性能。
    当多条线程争用相同的数据时，线程不会阻塞，所以这些算法提高了扩展性，当然，线程也不会遭遇死锁和活跃性问题。
    Java 5 引入了 java.util.concurrent.atomic 提供了创建高效非阻塞的算法。这些小型工具类，
    支持在单个变量上进行无锁及线程安全的操作。包 java.util.concurrent.atomic 中的类把 volatile 标注的数值、
    属性以及数组元素扩展成也能支持原子的、有条件的更新，这样就不需要外部的同步操作。
    
    * AtomicBoolean 可被原子更新的布尔值
    * AtomicInteger 可被原子更新的整型值
    * AtomicIntegerArray 一个整型数组，其元素可被原子更新
    * AtomicLong 可被原子更新的长整型值
    * AtomicLongArray 一个长整型数组，其元素可被原子更新
    * AtomicReference 可被原子更新的对象引用
    * AtomicReferenceArray 一个对象引用数组，其元素可被原子更新
    
    原子变量用于实现计数器、序列生成器（如java.util.concurrent.ThreadLocalRandom）等。
    在线程高争用的环境中，这些构造要求互斥而不影响性能。
    
 ```
public class ID {
    private volatile static long nextID = 1;

    public static void main(String[] args) {
        System.out.println(getNextID());
        System.out.println(nextID);
    }

    synchronized static long getNextID(){
        return nextID++;
    }
    
    // 输出
    // 1
    // 2
}

 ```
 上面synchronized这段代码保证了可见性，但是在线程高争用环境下影响性能，还可能发生死锁之类的活跃性问题。
 那如何用原子变量来代替synchronized关键字呢？看下面的例子
```
/**
 * 原子性
 */
public class AtomicID {
    private static volatile AtomicLong nextID = new AtomicLong(1);

    public static void main(String[] args) {
        System.out.println(getNextID());
        System.out.println(nextID);
    }

    static long getNextID(){
        // 原子性
        return nextID.getAndIncrement();
    }

    // 输出
    // 1
    // 2
}
```
#### 8.2.1 理解原子魔法
 
   Java低级的同步机制，强制使用互斥以及可见性，以如下的方式影响了硬件的使用和扩展能力。

   * 争用的同步代价非常昂贵，吞吐量很糟糕。这种代价主要是由频繁的上下文切换引起的（把中央处理单元从一条线程切换到另外一条）。每个上下文切换都会花费多个处理器周期来完成。
   * 当持有锁的线层被延迟了（如由于调度延迟），所有需要那个锁的线程都没法继续执行，硬件就得不到充分利用。
   
   你会认为 volatile 是同步的备胎，但 volatile 变量也只能解决可见性问题，无法应用于安全的实现原子的 读-改-写 的序列。这个原子的序列对于
   实现线程安全的计数器以及其他需要互斥访问的实体是需要的。并发工具集提供了另一个备胎，即 compare-and-swap。
   
   Compare-and-swap(CAS) 是针对非抢占式微处理器的一条指令的宽泛术语。这条指令读取内存的位置，
   比较读到的值和期望的值，当读到的值和期望的值匹配时，就将期望的值存储到该内存的位置；否则什么事也不会发生。
   现代微处理提供了多种 CAS 的变体，
   CAS 支持 读-改-写 的序列
   1，从地址 A 读出值 x；
   2，在 x 上进行一个多步计算，衍生出一个新值 y；
   3，使用 CAS 把 A 的值从 x 改成 y。当操作这些步骤时，如果 A 的值没有发生改变，CAS 就成功了。
   
   最终，JDK通过CPU的cmpxchgl指令的支持，实现AtomicInteger的CAS操作的原子性。
```
/**
 * 线程安全的ID
 * CAS(存在自旋问题)
 */
public class SafeID {

    private static AtomicLong nextID = new AtomicLong(1);

    public static void main(String[] args) {
        Runnable r = () -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + ": " + getNextID());
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(r);
        executorService.execute(r);

        executorService.shutdown();
    }

    static long getNextID(){
        long currentValue = nextID.longValue();
        if(nextID.compareAndSet(currentValue, currentValue + 1)){
        } else {
            //自旋
            System.out.println(Thread.currentThread().getName() + ": " + currentValue + ", continue......");
        }
        return nextID.longValue();
    }

    // 输出
    // pool-1-thread-2: 2
    // pool-1-thread-1: 1, continue......
    // pool-1-thread-1: 2
    // pool-1-thread-1: 3
    // pool-1-thread-2: 2, continue......
}
```
### 8.3 Fork/Join 框架

    假设一个任务，分成两个部分分别由两个线程同时去做，一个线程很可能在另一个线程完成前完成并一直等待，直到两个线程都完成之后合并结果。
    这个场景下，一个处理器的资源可能就被浪费了。这个问题可以通过递归地把任务拆分成子任务然后组合结果的方式解决。
    
    Fork/Join 使用了任务窃取来最小化线程的争用和开销。线程池中的每条线程都有自己的双端工作队列并且会将新任务放入到这个队列中。
    它从队列的头部读取任务。如果队列是空的，工作线程就尝试从另一个队列的末尾获取一个任务。窃取操作不会很频繁，因为工作线程会
    采用后进先出（LIFO）的顺序将任务放入到他们的队列中，同时工作项的规模会随着问题分割称子问题而变小。
    你一开始把任务分配给中心的工作线程，之后中心线程会持续将这个任务分解成更小的任务。最终所有的工作线程都只会涉及很少量的同步操作。
    
    Fork/Join 框架绝大部分由 java.util.concurrent 包中的 ForkJoinPool、ForkJoinTask、ForkJoinWorkerThread、RecursiveAction、
    RecursiveTask 以及 CountedCompleter 类组成。
    
    ForkJoinPool 构造函数
    public ForkJoinPool(int parallelism) 创建一个包含 parallelism 个并行线程的 ForkJoinPool
    public ForkJoinPool() 以 Runtime.getRuntime().availableProcessors()的返回值作为 parallelism 来创建 ForkJoinPool

   * ForkJoinPool 是针对运行中的 ForkJoinTask 的 ExecutorService 的实现。ForkJoinPool的实例提供了一个来自非ForkJoinTask客户端的入口，
     同时提供了管理和监听操作。
   * ForkJoinTask 是一个抽象的基类，专门针对那些运行在 ForkJoinPool 上下文中的任务。一个 ForkJoinTask 实例类似一个线程的实体，
     不过比正常的线程轻量的多。大量的任务以及子任务可能会被少量 ForkJoinPool 中的真实线程以某些使用限制作为代价进行托管。
     其有两个实现类 RecursiveAction、RecursiveTask 。
   * ForkJoinWorkerThread 描述了被 ForkJoinPool 管理的一个线程，它执行 ForkJoinTask 。
   * RecursiveAction 描述了一个递归地、无结果的 ForkJoinTask。
   * RecursiveTask 描述了一个递归地、有结果的 ForkJoinTask。
   * CountedCompleter 描述了一个带有完成动作（完成一个fork/join任务）的 ForkJoinTask，再被触发且没有其他滞留的动作时这个任务会被执行。

看代码更亲切

* 无返回结果
```
/**
 * Fork/Join
 * 从 1 数到 10，分两个任务
 */
public class ForkJoinAction {

    public static void main(String[] args) throws InterruptedException {

        // 创建 ForkJoinPool 实例，默认包含 Runtime.getRuntime().availableProcessors() 个工作线程
        final ForkJoinPool pool = new ForkJoinPool();
        // 无结果
        pool.invoke(new CounterAction(1,10));

        // 等待结果，超时结束
        pool.awaitTermination(10, TimeUnit.SECONDS);

        //关闭线程池
        pool.shutdown();
        // 输出
        // ForkJoinPool-1-worker-1 start: 1, end: 10
        // ForkJoinPool-1-worker-2 i: 1
        // ForkJoinPool-1-worker-3 i: 6
        // ForkJoinPool-1-worker-2 i: 2
        // ForkJoinPool-1-worker-2 i: 3
        // ForkJoinPool-1-worker-3 i: 7
        // ForkJoinPool-1-worker-2 i: 4
        // ForkJoinPool-1-worker-3 i: 8
        // ForkJoinPool-1-worker-2 i: 5
        // ForkJoinPool-1-worker-2 start: 1, end: 5
        // ForkJoinPool-1-worker-3 i: 9
        // ForkJoinPool-1-worker-3 i: 10
        // ForkJoinPool-1-worker-3 start: 6, end: 10
    }
}

/**
 * 从 1 数到 10 ， 分成连个任务。
 * 无返回结果
 */
class CounterAction extends RecursiveAction {

    /**
     * 开始
     */
    private int start;
    /**
     * 结束
     */
    private int end;

    public CounterAction(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {

        if (end - start < 5) {
            for (int i=start; i<=end; i++) {
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " i: " + i);
            }
        } else {
            CounterAction t1 = new CounterAction(1,5);
            CounterAction t2 = new CounterAction(6,10);
            // 并行执行两个小任务
            t1.fork();
            t2.fork();
        }
        System.out.println(Thread.currentThread().getName() + " start: " + start + ", end: " + end);
    }
}
```
* 有返回结果
```
/**
 * Fork/Join
 * 从 1 数到 10，分两个任务
 */
public class ForkJoinTask {

    public static void main(String[] args) throws InterruptedException {

        // 创建 ForkJoinPool 实例，默认包含 Runtime.getRuntime().availableProcessors() 个工作线程
        final ForkJoinPool pool = new ForkJoinPool();
        // 有结果
        pool.invoke(new SumTask(1,10));

        // 等待结果，超时结束
        pool.awaitTermination(10, TimeUnit.SECONDS);

        //关闭线程池
        pool.shutdown();

        // 输出
        // ForkJoinPool-1-worker-3 sum: 40
        // ForkJoinPool-1-worker-2 sum: 15
        // ForkJoinPool-1-worker-1 sum: 55
    }
}

/**
 * 从 1 加到 10 ， 分成两个任务。
 * 有返回结果
 */
class SumTask extends RecursiveTask<Integer> {

    /**
     * 开始
     */
    private int start;
    /**
     * 结束
     */
    private int end;

    public SumTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        if (end - start < 5) {
            for (int i=start; i<=end; i++) {
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sum += i;
            }
        } else {
            SumTask t1 = new SumTask(1,5);
            SumTask t2 = new SumTask(6,10);
            // 并行两个小任务
            t1.fork();
            t2.fork();
            // 把两个小任务的返回结果合并
            sum = t1.join() + t2.join();
        }
        System.out.println(Thread.currentThread().getName() + " sum: " + sum);
        return sum;
    }
}
```
#### 8.4 Completion Service

    一个 Completion Service 就是一个 java.util.concurrent.CompletionService<V> 接口的实现，
    整合了 executor 和 BlockingQueue 功能，用于从已完成任务（消费者）结果的消费中解耦新的异步任务（生产者）的生产，V 是这个任务返回结果的类型。
    
    FutureTask 也可以实现同样的功能，和 CompletionService 有啥区别呢？只不过 FutureTask 大部分场景下执行时间不确定，浪费资源。
    
    CompletionService接口提供五个方法：

   * Future<V> submit(Callable<V> task)  提交 Callable 类型的 task；
   * Future<V> submit(Runnable task, V result) 提交 Runnable 类型的 task；
   * Future<V> take() throws InterruptedException 获取并移除已完成状态的 task，如果目前不存在这样的 task，则等待；
   * Future<V> poll() 获取并移除已完成状态的task，如果目前不存在这样的task，返回null；
   * Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException 获取并移除已完成状态的 task，如果在指定等待时间内不存在这样的 task，返回 null。
```
/**
 * Completion Service
 */
public class CompletionThread {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // 创建一个 executorService , 以供后续"执行"任务
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        // 创建一个 completionService , 以供后续"完成"任务
        CompletionService completionService = new ExecutorCompletionService(executorService);

        // 使用 completionService.submit() 提交两个计算任务，每个任务都会异步执行
        completionService.submit(new CallableTask(100));
        completionService.submit(new CallableTask(200));

        // result 1, 使用 take() 方法返回任务的 feature 对象，阻塞等待
        Future<BigDecimal> future = completionService.take();
        // 使用 future.get() 获取任务的结果
        System.out.println("result1: " + future.get());

        // result 2, 使用 take() 方法返回任务的 feature 对象，阻塞等待
        future = completionService.take();
        // 使用 future.get() 获取任务的结果
        System.out.println("result2: " + future.get());

        // 输出
        // result1: 200
        // result2: 100
    }
}

/**
 * 模拟耗时任务
 */
class CallableTask implements Callable<BigDecimal> {
    private int max;

    public CallableTask(int max) {
        this.max = max;
    }

    @Override
    public BigDecimal call() throws Exception {
        // long-running
        Thread.sleep(new Random().nextInt(1000));
        return BigDecimal.valueOf(max);
    }
}
``` 