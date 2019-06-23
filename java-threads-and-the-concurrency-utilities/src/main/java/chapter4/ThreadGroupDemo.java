package chapter4;

/**
 * 线程组
 *
 * @author neal.ma
 * @date 2019/6/23
 * @blog nealma.com
 */
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