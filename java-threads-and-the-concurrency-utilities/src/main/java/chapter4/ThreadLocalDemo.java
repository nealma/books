package chapter4;

/**
 * 线程局部变量
 *
 * @author neal.ma
 * @date 2019/6/23
 * @blog nealma.com
 */
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