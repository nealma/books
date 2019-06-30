package chapter8;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 原子性
 *
 * @author neal.ma
 * @date 2019/6/29
 * @blog nealma.com
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
