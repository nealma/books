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
    
#### 7.3 条件 Condition

    接口 Condition 把 Object 的 wait 和 notification 方法（wait()\notify()\notifyAll()）分解到不同的条件
    对象中，把这些条件和任意Lock实现的使用结合起来，起到让每个对象上具有多重等待集合的作用。
    这里Lock取代了同步方法、代码块，Condition 取代了 Object 的 wait、notification 方法。
    
    **一个 Condition 的实例原则上只绑定到一个锁上**
    
    * void await() 在接收到信号或者被中断之前，强制当前线程一直等待。
    * void await(long timeout, TimeUnit unit) 同上，仅仅增加超时时间。
    * long awaitNanos(long nanosTimeout) 同上，仅仅增加超时时间。
    * void awaitUninterruptibly() 在接收到信号之前，强制当前线程一直等待。
    * void signal() 唤醒一个等待中的线程。
    * void signalAll() 唤醒所有等待中的线程。
 ```

/**
 * 条件
 */
public class ConditionThread {
    private volatile static boolean shared = false;
    public static void main(String[] args) {

        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        Runnable producer = () -> {
            lock.lock();
            try {
                while (!shared){
                    condition.await();
                }
                System.out.println(Thread.currentThread().getName() + " [producer] at " + System.currentTimeMillis() + " hold: " + ((ReentrantLock) lock).isHeldByCurrentThread());
                Thread.sleep(new Random().nextInt(1000));
                shared = false;
                condition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        };
        Runnable consumer = () -> {
            lock.lock();
            try {
                while (shared){
                    condition.await();
                }
                System.out.println(Thread.currentThread().getName() + " [consumer] at " + System.currentTimeMillis() + " hold: " + ((ReentrantLock) lock).isHeldByCurrentThread());
                Thread.sleep(new Random().nextInt(1000));
                shared = true;
                condition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i=0; i<10; i++){
            executorService.execute(producer);
            executorService.execute(consumer);
        }

        executorService.shutdown();

        // 输出
        // pool-1-thread-2 [consumer] at 1561679314558 hold: true
        // pool-1-thread-3 [producer] at 1561679314697 hold: true
        // pool-1-thread-4 [consumer] at 1561679315204 hold: true
        // pool-1-thread-4 [producer] at 1561679315342 hold: true
        // pool-1-thread-4 [consumer] at 1561679315724 hold: true
        // pool-1-thread-2 [producer] at 1561679315910 hold: true
        // pool-1-thread-3 [consumer] at 1561679316567 hold: true
        // pool-1-thread-3 [producer] at 1561679316792 hold: true
        // pool-1-thread-2 [consumer] at 1561679317664 hold: true
        // pool-1-thread-1 [producer] at 1561679317769 hold: true
        // pool-1-thread-1 [consumer] at 1561679318178 hold: true
        // pool-1-thread-2 [producer] at 1561679318618 hold: true
        // pool-1-thread-3 [consumer] at 1561679318661 hold: true
        // pool-1-thread-1 [producer] at 1561679319316 hold: true
        // pool-1-thread-2 [consumer] at 1561679320291 hold: true
        // pool-1-thread-2 [producer] at 1561679320674 hold: true
        // pool-1-thread-2 [consumer] at 1561679321324 hold: true
        // pool-1-thread-4 [producer] at 1561679322291 hold: true
        // pool-1-thread-1 [consumer] at 1561679322872 hold: true
        // pool-1-thread-3 [producer] at 1561679323806 hold: true
    }
} 
 ```
 
#### 7.4 读写锁 ReadWriteLock
 
   读写锁适用于对数据结构频繁读而较少修改的场景。
   读写锁（ReadWriteLock）机制，在读取时有较高的并发性，而写入时保证安全的互斥访问。
   读写锁维护了一对锁：一个锁针对只读操作，一个锁针对写操作。在没有写操作的时候，读锁可能会被多条读线程同时持有；
   写入锁是互斥的：只有单个线程可以修改共享数据。

   部分方法
   * Lock readLock() 返回读锁
   * Lock writeLock() 返回写锁
   
#### 7.5 重入读写锁 ReentrantReadWriteLock

    类ReentrantReadWriteLock实现了接口ReadWriteLock，代表与ReentrantLock具有相同语义的重入读-写锁
    
    ReentrantReadWriteLock 构造函数
  * ReentrantReadWriteLock() 等价于 ReentrantReadWriteLock(false)
  * ReentrantReadWriteLock(boolean fair)  fair 控制是否要使用公平的排序策略；
  
  基于公平的顺序策略，若当前持有的锁被释放了，那要么是等待最久的单条写线程会被分配血锁，
  要么就是当一组读线程比所有等待中的写线程等待时间还长时，这组读线程会被分配读锁。
  
  ReentrantReadWriteLock 方法
  * ReentrantReadWriteLock.ReadLock readLock() 返回用于读锁
  * ReentrantReadWriteLock.writeLock writeLock() 返回用于写锁
  * int getReadHoldCount() 返回被调用线程在这个锁上持有读锁的数量
  * int getWriteHoldCount() 返回被调用线程在这个锁上持有写锁的数量
```
/**
 * 重入读写锁
 */
public class ReentrantReadWriteLockThread {

    public static void main(String[] args) {

        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        final Lock rLock = lock.readLock();
        final Lock wLock = lock.writeLock();


        Runnable reader = () -> {
            rLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " hold: " + lock.getReadHoldCount());
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                rLock.unlock();
            }
        };

        Runnable writer = () -> {
            wLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " hold: " + lock.getWriteHoldCount());
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                wLock.unlock();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i=0; i<10; i++){
            executorService.execute(reader);
            executorService.execute(writer);
        }

        executorService.shutdown();

        // 输出
        // pool-1-thread-1 at 1561684047226 hold: 1
        // pool-1-thread-2 at 1561684047961 hold: 1
        // pool-1-thread-1 at 1561684048452 hold: 1
        // pool-1-thread-2 at 1561684049256 hold: 1
        // pool-1-thread-1 at 1561684050030 hold: 1
        // pool-1-thread-2 at 1561684050444 hold: 1
        // pool-1-thread-1 at 1561684051120 hold: 1
        // pool-1-thread-2 at 1561684051584 hold: 1
        // pool-1-thread-1 at 1561684051676 hold: 1
        // pool-1-thread-2 at 1561684052545 hold: 1
        // pool-1-thread-1 at 1561684053545 hold: 1
        // pool-1-thread-2 at 1561684054036 hold: 1
        // pool-1-thread-1 at 1561684054309 hold: 1
        // pool-1-thread-2 at 1561684054920 hold: 1
        // pool-1-thread-1 at 1561684055627 hold: 1
        // pool-1-thread-2 at 1561684055920 hold: 1
        // pool-1-thread-1 at 1561684056228 hold: 1
        // pool-1-thread-2 at 1561684057170 hold: 1
        // pool-1-thread-1 at 1561684057395 hold: 1
        // pool-1-thread-2 at 1561684057530 hold: 1
    }
}
```

#### 7.6 邮戳锁 StampedLock