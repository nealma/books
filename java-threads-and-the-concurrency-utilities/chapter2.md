2 同步

线程交互是通过共享变量完成的，开发程序很容易；一旦线程间产生了交互，就会诱发很多线程不安全。

2.1 线程中的问题
  * 竟态条件
  * 数据竞争
  * 缓存变量
  
2.1.1 竟态条件
  
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

2.1.2 数据竞争

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

2.1.3 缓存变量
 
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
 
 
