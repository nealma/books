#### 1.1 Thread 和 Runnable

每个Java应用程序都有一个执行main()函数的默认主线程。
封装了代码执行序列的线程对象被称为Runnable。
Java虚拟机给每条线程分配独立的JVM栈空间，栈空间为每条线程单独准备了一份方法参数、局部变量以及返回值的拷贝。

##### 1.1.1 创建Thread和Runnable对象
