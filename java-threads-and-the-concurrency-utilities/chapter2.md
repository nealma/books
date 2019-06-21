## 2 同步

线程交互是通过共享变量完成的，开发程序很容易；一旦线程间产生了交互，就会诱发很多线程不安全。

### 2.1 线程中的问题
  * 竟态条件
  * 数据竞争
  * 缓存变量
  
#### 2.1.1 竟态条件
  
  当计算的正确性取决于相对时间或者调度器所控制的多线程交叉时，竟态条件就会发生。

* check-then-act

a b 是实例变量或者类（static）变量

```
if(a == 10.0){//1
  b = a / 2.0;//2
}
```
“检查”是 if(a == 10.0); “动作”是 b = a / 2.0;

假设一条线程已经执行完 1 ，在即将执行 2 时，被调度器暂停了，与此同时，调度器恢复了另一条线程改变了 a 的值；
当前一条线程恢复执行，变量 b 却不会等于 5.0；

* read-modify-write

新状态继承自旧状态；旧状态被读取，然后更改，最后更新；这3个不可分割的组合并非不可分割。

```
    public int getId(){
        return counter++;
    }
```
尽管看上去是单一操作，但事实上表达式 counter++ 是三个单独操作：读取counter值，给值加 1 ，然后把更新之后的值存储在counter中。
当时读取的值就是整个表达式的返回值。

#### 2.1.2 数据竞争

  数据竞争指的是两条或者两条以上的线程（在单个应用中）并发的访问同一块内存区域，同时至少有一条是为了写，而且这些线程没有协调对那块内存区域的访问。
  当满足这些条件，访问顺序就是不确定的，每次运行产生不同的结果。
  
```
 private static Parser parser;
    
    public static Parser getInstance(){
        if( parser == null ){
            parser = new Parser();
        }
        return parser;
    }
```
假设线程 1 调用了getInstance()方法。由于检测属性parser是空值，线程1实例化Parser并将引用赋值给变量parser。
随后，当线程 2 调用getInstance()方法时，可能检测到parser非空，简单返回；还有可能检测到parser还是空值，于是又创建了一个新的Parser对象。
由于线程 1 和 线程 2 读 parser 变量之间没有 happpen-before ordering（一个动作优先于另一个动作）的保证（这里不存在对parser访问顺序的协同），
数据竞争便产生了。

#### 2.1.3 缓存变量
 
 为了提升性能，编译器Java虚拟机（JVM）以及操作系统会协调在寄存器中或者处理器缓存中缓存变量，而不是依赖主存。
 每条线程都会有其自己的变量拷贝。当线程写入这个变量的时候，其实是写入自己的拷贝；其他线程不太可能看到自己的变量拷贝发生更改。
 
 ```
 public class CacheVariableThread {

    public static BigDecimal result = null;

    public static BigDecimal compute(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) { e.printStackTrace(); }
        return new BigDecimal(10000);
    }
    public static void main(String[] args) {
        Runnable r1 = () -> {
            result = compute();
        };

        Thread t1 = new Thread(r1, "t1");
        t1.start();

        try {
            t1.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("result [" + result +"]");
    }
}
 ```
 类属性result示范了缓存变量的问题。该属性在lambda表达式上下文当中被一条工作线程访问并执行代码result = compute();然后默认主线程执行System.out.println("result [" + result +"]");
 
### 2.2 同步临界区的访问
 同步是JVM的一个特性，旨在保证两个或多个并发的线程不会同时执行同一块临界区；
 临界区就是以串行方式（一次一条线程）访问的一段代码块
 
 因为其他线程在临界区中的时候每条线程对该临界区的访问都会互斥的执行，这种同步属性就成为互斥。由于这个原因，线程获取到的锁经常称为互斥锁。
 
 同步也表现出可见性，该属性能够保证一条线程在临界区执行的时候总是能看到共享变量最近的修改。
 当进入临界区时，它从主存中读入这些变量；离开时，把这些变量的值写回到主存中。
 
 同步是通过监听器来实现的，监听器时针对临界区构建的并发访问控制，并发必须以不可分割的形式执行。
 每一个Java对象都和一个监听器相关联，这样线程就可以通过获取和释放监听器的锁（一个标识）来上锁和解锁。
 
 Tips：
   当调用Thread的任意sleep（）方法时，已经获取锁的线程不会释放锁。
 
 只有一个线程持有监听器的锁，任意尝试锁住该监听器的线程都会一直阻塞，知道能够获取琐为止。
 当线程离开临界区，它会通过释放锁来解锁监听器。
 
 为了防止发生死锁，锁设计成可重入的，当线程尝试获取它已经持有的锁，请求会成功。
 
 Tips：
     java.lang.Thread 类声明了一个静态 static boolean holdslock(Object o)方法，即当调用线程持有对象o的锁，请求返回true。
     
 #### 2.2.1 使用同步方法
   同步方法会在方法头部包含synchronized关键字。
 
  * 当同步在实例方法上，锁会和调用该方法的实例对象关联。
  ```
  class ID {
      private int  counter;
      public synchronized int getId(){
          return counter++;
      }
  }
  
  ID id = new ID();
  System.out.println(id.getId());
  ```
  锁和ID对象关联；
  
 * 当同步在类方法上，锁会和调用该类方法的类所对应的的java.lang.Class对象关联。
  ```
  class ID2 {
      private int  counter;
      public static synchronized int getId(){
          return counter++;
      }
  }
  
  System.out.println(ID2.getId());
  ```
  锁和ID2类关联的ID2.class相关联；
  
  #### 2.2.2 使用同步块
   一个同步快语句把这个待锁住的对象作为前缀头。
   ```
  synchronized(object){
   //...
  }
  ```
同步块标识了一个临界区。

### 2.3 活跃性问题
活跃性：某件正确的事情最终会发生。
活跃性失败发生在应用程序触及一种无法继续执行的状态。

单线程
* 无线循环

多线程
* 死锁 两个线程互斥持有资源，都无法继续执行。

* 活锁 线程总是执行一个失败的操作，导致无法继续执行。

* 饿死 线程一直被（调度器）延迟访问其赖以执行的资源。
