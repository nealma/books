#### 1.1 Thread 和 Runnable

每个Java应用程序都有一个执行main()函数的默认主线程。
封装了代码执行序列的线程对象被称为Runnable。
Java虚拟机给每条线程分配独立的JVM栈空间，栈空间为每条线程单独准备了一份方法参数、局部变量以及返回值的拷贝。
Java线程分为守护线程和非守护线程。
一条守护线程扮演非守护线程辅助者的角色，并且会再应用程序最后一条非守护线程消失之后自动死亡，这时应用程序才能终止。

##### 1.1.1 创建Thread和Runnable对象

##### 1.1.2 获取和设置线程状态
Java中两种线程：用户线程（User Thread）、守护线程（Daemon Thread）
GC就是典型的守护线程，主线程终止，所有守护线程全部终止
通过setDaemon设置，主线程不能设置
所有非守护线程终止，主线程才终止
执行规则：
  * 所有用户线程终止，主线程才会终止
  * 主线程终止，所有守护线程都会终止
##### 1.1.3 启动线程
```
start()
```
#### 1.2  操作更高级的线程任务
  * 中断
  * 睡眠
  * 加入到另一线程中join
##### 1.2.1 中断线程
  * interrupt()
  * isInterrupt()
  * interrupted()
  * isInterrupted()
  
##### 1.2.2 等待线程
  * join() 无限期的等待直至该线程死亡
  * join(long millis) 该线程死亡之前最多等待millis毫秒，millis=0，无限期等待，同join(); millis<0,IllegalArgumentException被抛出
  * interrupted(long millis, int nanos) nanos [0, 999999)
  
  
