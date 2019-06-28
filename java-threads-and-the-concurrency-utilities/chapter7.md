## 7 锁框架

    Java提供了 java.util.concurrent.locks 包，包含多种接口和类，针对条件进行加锁和等待。
    不同于对象的内置加锁同步以及 java.lang.Object 的等待/通知机制，包含锁框架的并发工具类通过轮训锁、限时等待及其他方式改善了这种机制。
    **同步及低级别的锁**
    Java支持同步以便线程能够安全的更新共享变量并且保证一条线程的更新对其他线程的可见。
    你可以给方法或者代码块标记上synchronized关键字已达到同步的目的。这样的代码块被称为"临界区"。
    JVM 通过JVM 指令 monitors、monitorenter 以及 monitorexit 来支持同步
    
    每一个Java对象都和一个监听器关联，监听器是一个互斥（每次只允许一个线程进入临界区中执行）构造，
    他阻止多条线程同时在临界区中并发执行。在线程可以进入临界区之前，它需要锁住监听器。
    如果这个监听器已经上锁，在监听器释放之前这条线程会一直阻塞（因为其他线程正在使用临界区）。
    
    当线程在多核、多处理器的环境中锁住一个监听器，存储在主存中的共享变量的值会被读取到对应的拷贝中，
    然后存储在线程的工作内存（称为本地内存或者缓存）。这一动作能够保证该线程使用这些变量最近的值并且不会污染这些值，我们称为"可见性"。
    线程会持续使用这些共享变量的拷贝。当离开临界区，线程会释放监听器，这些共享变量的值就会被写回到主存，
    以确保下一条线程进入临界区也能访问这些共享变量最近的值（volatile 仅仅解决了可见性）
    
    Java 锁框架包含了经常使用的锁、重入锁、条件、读写锁以及重入读写锁等，还有Java 8引入的StampedLock类。
    
### 7.1 锁

  接口 Lock 提供了比监听器关联的锁更为弹性的锁操作。例如，当锁不可用时，可以立即退出对一个锁的请求。
  * void lock() 获取锁。当锁不可用时，调用线程会被强制一直等待直到锁可用。
  * void lockInterruptibly() 除非调用线程被中断，否则获得锁。当锁不可用时，调用线程被强制一直等待，直到锁可用或线程中断。
  * Condition newCondition() 返回一个新的绑定到当前锁实例之上的 Condition 实例。
  * boolean tryLock() 在该方法被调用时，锁可用则获得锁。当获得锁之后，返回 true；否则返回 false。
  * boolean tryLock(long timeout, TimeUnit unit) 在该方法被调用时，在指定等待时间内锁可用并且线程没有被中断则获得锁。当获得锁之后，返回 true；否则返回 false。
  * void unlock() 释放锁
  
  块状锁：隐式监听器，当获得多个锁，则以相反的顺序自动释放锁
  Lock锁：获取和释放锁按照下面约定俗成,以保障获取到的锁总会被释放
  
```
    Lock l = ...;
    l.lock();
    try{
        // to do something
    }catch(Exception e){
        // handle error
    }finally{
        l.unlock();
    }
```  

#### 7.2 重入锁
  
  类 ReentrantLock 实现了接口 Lock，描述了一个可重入的互斥锁。这个锁和一个持有量相关联。
  当一条线程持有这个锁并且调用lock()、lockUninteruptibly()或者任意一个tryLock()方法重新获得锁，这个持有量就递增 1 ，
  当线程调用unLock()方式，这个持有量就递减 1 。当持有量为 0 ，锁就会被释放掉。
  
  当很多线程尝试获取共享资源时，JVM 会花费更少的时间来**调度**这些线程，把更多时间投入到**执行**。
  
  ReentrantLock 构造函数
  * ReentrantLock() 等价于ReentrantLock(false)
  * ReentrantLock(boolean fair)  fair 控制是否要使用公平的排序策略；为 true 时，在争用的情况下，这个锁倾向于将访问权限分配给等待最久的线程。
  
  ReentrantLock 方法
  * boolean isFair() 返回 公平策略
  * boolean isHeldByCurrentThread() 锁是否被当前线程锁持有
```
    
/**
 * 重入锁
 */
public class ReentrantLockThread {

    public static void main(String[] args) {

        final Lock lock = new ReentrantLock();

        Runnable r = () -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " hold: " + ((ReentrantLock) lock).isHeldByCurrentThread());
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i=0; i<10; i++){
            executorService.execute(r);
        }

        executorService.shutdown();
        
        // 输出
        // pool-1-thread-1 at 1561676957938 hold: true
        // pool-1-thread-2 at 1561676958050 hold: true
        // pool-1-thread-1 at 1561676958122 hold: true
        // pool-1-thread-2 at 1561676959057 hold: true
        // pool-1-thread-1 at 1561676959511 hold: true
        // pool-1-thread-1 at 1561676960184 hold: true
        // pool-1-thread-2 at 1561676960933 hold: true
        // pool-1-thread-1 at 1561676961804 hold: true
        // pool-1-thread-2 at 1561676962200 hold: true
        // pool-1-thread-1 at 1561676962714 hold: true
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
 
#### 6.4 信号量
 
   信号量维护了一组许可证（permit），以约束访问被限制资源的线程数。
   当没有可用的许可证时，线程的获取尝试会一直阻塞，知道其他线程释放一个许可证。
   * 计数信号量 当前的值可以被递增 1
   * 二进制或互斥信号量 当前的值只能是 0 和 1

   构造函数
   * Semaphore(int permits) 指定许可证数量，默认设置成不公平策略
   * Semaphore(int permits, boolean fair) 指定许可证数量和公平策略
   
#### 6.5 信号量和公平策略

    当公平策略设置成false，信号量不会保证线程获取信号量的顺序（抢占式的）。
    即便线程已经在等待，调用了acquire()方法的新线程还是能先于这条线程被分配许可证。
    逻辑上，新线程把自己放到了等待线程队列的队首了。当公平策略设置为true，
    信号量就能保证调用acquire()方法的任意线程能按照方法被调用处理的顺序获取许可证（先进先出，FIFO）。
    不限时tryAcquire()方法不会遵循公平策略的设定。
    
    一般来讲，信号量通常用来控制资源访问，它应当初始化成公平的，从而保证不会有任何线程在访问资源时饿死。
    
    * void acquire() 从信号量中获取一个许可证，否则阻塞，直到有一个许可证可用或者调用线程被中断。
    * void acquire(int permits) 从信号量中获取permits个许可证，否则阻塞，直到有一个许可证可用或者调用线程被中断。
    * void acquireUninterruptibly() 从信号量中获取一个许可证，否则阻塞，直到有一个许可证可用。
    * void acquireUninterruptibly(int permits) 从信号量中获取permits个许可证，否则阻塞，直到有一个许可证可用。
    * int availablePermits() 返回当前可用许可证数量
    * int drainPermits() 获取并返回立即可用许可证数量
    * int getQueueLength() 返回等待获取许可证的大致线程数
    * boolean hasQueueThreads() 查询是否存在等待获取许可证的线程
    * boolean isFair() 返回公平性设置
    * void release() 释放一个许可证
    * void release(int permits) 释放permits个许可证
    * boolean tryAcquire() 仅当调用时有一个许可证可用的情况，才能从这个信号量中获取这个信号
    * boolean tryAcquire(int permits) 仅当调用时有permits个许可证可用的情况，才能从这个信号量中获取这些个信号
    * boolean tryAcquire(int permits, long timeout, TimeUnit unit) 仅增加超时，其他同上
    * boolean tryAcquire(long timeout, TimeUnit unit) 调用线程会一直等待直到有一个许可证可用。
```
/**
 * 信号量
 */
public class SemaphoreThread {

    public static void main(String[] args) {
        final Semaphore semaphore = new Semaphore(10);
        Runnable r = () -> {
            try {
                semaphore.acquire();
                Thread.sleep(new Random().nextInt(1000));
                System.out.println(Thread.currentThread().getName() + " handle " + semaphore.getQueueLength());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                semaphore.release();
            }
        };

        for (int i=0; i<100; i++){
            new Thread(r).start();
        }

        // 输出
        // Thread-1 handle
        // Thread-2 handle
        // Thread-9 handle
        // Thread-11 handle
        // Thread-8 handle
        // Thread-7 handle
        // Thread-5 handle
        // Thread-12 handle
        // ......
    }
}
```    
  
#### 6.6 Phaser

    Phaser 是一个更加弹性的同步屏障。
    一个 Phaser 是的一组线程在屏障上等待，在最后一条线程到达之后，这些线程得以继续执行。
    Phaser也提供Barrier Action的等价操作。一个Phaser可以协调不定数目的线程。这些线程可以在任何时候注册。
    
    parties 参与者
    phase 阶段
    arrive 抵达
    advance 进阶
        
    * int register() 往这个Phaser中添加一条尚未抵达的线程，同时返回phase值作抵达分类用，这个值称为抵达phase值。
    * int arriveAndAwaitAdvance() 记录到达并等待Phaser前进，返回抵达phase值。
    * int arriveAndDeregister() 抵达此Phaser，同时从中注销而不会等待其他线程到达，由此减少未来phase上需要前进的线程数量。
    
```

/**
 * Phaser
 */
public class PhaserThread {

    public static void main(String[] args) {

        final Phaser phaser = new Phaser();

        Runnable r = () -> {
            try {
                System.out.println(Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " phase: " + phaser.arriveAndAwaitAdvance());
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        for (int i=0; i<10; i++){
            System.out.println(phaser.register());

        }
        for (int i=0; i<10; i++){
            new Thread(r).start();
            System.out.println("parties: " + phaser.getRegisteredParties() + ", phase: " + phaser.getPhase());
        }

        // 等待所有参与者都执行完，注销该phaser
        phaser.arriveAndDeregister();

        // 输出
        // 0
        // 0
        // 0
        // 0
        // 0
        // 0
        // 0
        // 0
        // 0
        // 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // parties: 10, phase: 0
        // Thread-7 at 1561593950111 phase: 1
        // Thread-2 at 1561593950110 phase: 1
        // Thread-3 at 1561593950110 phase: 1
        // Thread-4 at 1561593950110 phase: 1
        // Thread-6 at 1561593950111 phase: 1
        // Thread-8 at 1561593950111 phase: 1
        // Thread-5 at 1561593950111 phase: 1
        // Thread-1 at 1561593950108 phase: 1
        // Thread-0 at 1561593950106 phase: 1
    }
}
```    

    
 