package chapter2;

/**
 * 同步
 *
 * @author neal.ma
 * @date 2019/6/17
 * @blog nealma.com
 */
public class SynchronizedThread {

    public static void main(String[] args) {

        Integer o = 1;

        // 1 当同步在实例方法上，锁会和调用该方法的实例对象关联
        // 锁和ID对象相关联，对象的引用存储在id变量中
        ID id = new ID();
        System.out.println(id.getId());

        // 2 当同步在类方法上时，锁会和调用该方法的类对应的Class对象相关联
        // 锁和ID类关联的CLass对象ID.class相关联，对象的引用存储在id变量中
        System.out.println(ID2.getId());

        // 使用同步快，锁和该对象相关联
        synchronized (o){
            System.out.println();
        }

    }
}

class ID {
    private int  counter;
    public synchronized int getId(){
        return counter++;
    }
}

class ID2 {
    private static int counter;
    public static synchronized int getId(){
        return counter++;
    }
}